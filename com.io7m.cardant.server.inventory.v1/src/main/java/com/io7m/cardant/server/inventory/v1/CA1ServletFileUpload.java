/*
 * Copyright © 2023 Mark Raynsford <code@io7m.com> https://www.io7m.com
 *
 * Permission to use, copy, modify, and/or distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
 * SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
 * IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */


package com.io7m.cardant.server.inventory.v1;

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseQueriesFilesType;
import com.io7m.cardant.database.api.CADatabaseRole;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.protocol.inventory.CAIResponseFilePut;
import com.io7m.cardant.protocol.inventory.cb.CAI1Messages;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.security.CASecurityException;
import com.io7m.cardant.server.http.CAHTTPServletFunctional;
import com.io7m.cardant.server.http.CAHTTPServletFunctionalCoreType;
import com.io7m.cardant.server.http.CAHTTPServletRequestInformation;
import com.io7m.cardant.server.http.CAHTTPServletResponseFixedSize;
import com.io7m.cardant.server.http.CAHTTPServletResponseType;
import com.io7m.cardant.server.service.reqlimit.CARequestLimitExceeded;
import com.io7m.cardant.server.service.reqlimit.CARequestLimits;
import com.io7m.jvindicator.core.VParameterCheckType;
import com.io7m.jvindicator.core.Vindication;
import com.io7m.medrina.api.MActionName;
import com.io7m.medrina.api.MObject;
import com.io7m.medrina.api.MTypeName;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Optional;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorApiMisuse;
import static com.io7m.cardant.protocol.inventory.CAIResponseBlame.BLAME_CLIENT;
import static com.io7m.cardant.protocol.inventory.CAIResponseBlame.BLAME_SERVER;
import static com.io7m.cardant.server.http.CAHTTPServletCoreInstrumented.withInstrumentation;
import static com.io7m.cardant.server.inventory.v1.CA1Errors.errorResponseOf;
import static com.io7m.cardant.server.inventory.v1.CA1ServletCoreAuthenticated.withAuthentication;
import static com.io7m.jvindicator.core.Vindication.strings;
import static com.io7m.jvindicator.core.Vindication.uuids;

/**
 * The v1 file upload servlet.
 */

public final class CA1ServletFileUpload
  extends CAHTTPServletFunctional
{
  /**
   * The v1 file upload servlet.
   *
   * @param services The services
   */

  public CA1ServletFileUpload(
    final RPServiceDirectoryType services)
  {
    super(createCore(services));
  }

  private static CAHTTPServletFunctionalCoreType createCore(
    final RPServiceDirectoryType services)
  {
    final var limits =
      services.requireService(CARequestLimits.class);
    final var messages =
      services.requireService(CAI1Messages.class);
    final var database =
      services.requireService(CADatabaseType.class);

    final var main =
      withAuthentication(services, (request, information, session, user) -> {
        return execute(
          request,
          information,
          user,
          messages,
          database,
          limits
        );
      });

    return withInstrumentation(services, main);
  }

  private static CAHTTPServletResponseType execute(
    final HttpServletRequest request,
    final CAHTTPServletRequestInformation information,
    final CAUser user,
    final CAI1Messages messages,
    final CADatabaseType database,
    final CARequestLimits limits)
  {
    try {
      CASecurity.check(
        user.userId(),
        user.subject(),
        new MObject(
          MTypeName.of("inventory.files"),
          Map.of()
        ),
        MActionName.of("write")
      );
    } catch (final CASecurityException e) {
      return errorResponseOf(messages, information, BLAME_CLIENT, e);
    }

    final var vindicator =
      Vindication.startWithExceptions(message -> {
        return new CAException(
          message, errorApiMisuse(), Map.of(), Optional.empty()
        );
      });

    final var vContentType =
      vindicator.addRequiredParameter("FileType", strings());
    final var vFileId =
      vindicator.addRequiredParameter("FileID", uuids());
    final var vDescription =
      vindicator.addRequiredParameter("FileDescription", strings());
    final var vHashAlgorithm =
      vindicator.addRequiredParameter("HashAlgorithm", digests());
    final var vHashValue =
      vindicator.addRequiredParameter("HashValue", strings());

    try {
      vindicator.check(request.getParameterMap());
    } catch (final CAException e) {
      return errorResponseOf(messages, information, BLAME_CLIENT, e);
    }

    try (var input = limits.boundedMaximumInputForFileUpload(request)) {
      final var digest = vHashAlgorithm.get();

      try (var digestStream = new DigestInputStream(input, digest)) {
        final var data = digestStream.readAllBytes();

        try (var connection = database.openConnection(CADatabaseRole.CARDANT)) {
          try (var transaction = connection.openTransaction()) {
            final var q = transaction.queries(CADatabaseQueriesFilesType.class);
            final var file = new CAFileType.CAFileWithData(
              new CAFileID(vFileId.get()),
              vDescription.get(),
              vContentType.get(),
              Integer.toUnsignedLong(data.length),
              digest.getAlgorithm(),
              vHashValue.get(),
              new CAByteArray(data)
            );

            q.filePut(file);
            transaction.commit();

            return new CAHTTPServletResponseFixedSize(
              200,
              CAI1Messages.contentType(),
              messages.serialize(new CAIResponseFilePut(
                information.requestId(),
                file.withoutData()
              ))
            );
          }
        }
      }
    } catch (final CARequestLimitExceeded e) {
      return errorResponseOf(messages, information, BLAME_CLIENT, e);
    } catch (final CADatabaseException e) {
      return errorResponseOf(messages, information, BLAME_SERVER, e);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static VParameterCheckType<MessageDigest> digests()
  {
    return MessageDigest::getInstance;
  }
}
