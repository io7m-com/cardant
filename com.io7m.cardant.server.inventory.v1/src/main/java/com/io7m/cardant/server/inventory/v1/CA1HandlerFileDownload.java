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
import com.io7m.cardant.database.api.CADatabaseQueriesFilesType.GetType.Parameters;
import com.io7m.cardant.database.api.CADatabaseRole;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.protocol.inventory.cb.CAI1Messages;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.security.CASecurityException;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.http.CAHTTPHandlerFunctional;
import com.io7m.cardant.server.http.CAHTTPHandlerFunctionalCoreType;
import com.io7m.cardant.server.http.CAHTTPRequestInformation;
import com.io7m.cardant.server.http.CAHTTPResponseFixedSize;
import com.io7m.cardant.server.http.CAHTTPResponseType;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.jvindicator.core.Vindication;
import com.io7m.medrina.api.MActionName;
import com.io7m.medrina.api.MObject;
import com.io7m.medrina.api.MTypeName;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import io.helidon.webserver.http.ServerRequest;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorApiMisuse;
import static com.io7m.cardant.protocol.inventory.CAIResponseBlame.BLAME_CLIENT;
import static com.io7m.cardant.protocol.inventory.CAIResponseBlame.BLAME_SERVER;
import static com.io7m.cardant.server.http.CAHTTPHandlerCoreInstrumented.withInstrumentation;
import static com.io7m.cardant.server.inventory.v1.CA1Errors.errorResponseOf;
import static com.io7m.cardant.server.inventory.v1.CA1HandlerCoreAuthenticated.withAuthentication;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_NONEXISTENT;
import static com.io7m.cardant.strings.CAStringConstants.FILE;
import static com.io7m.jvindicator.core.Vindication.uuids;

/**
 * The v1 file download servlet.
 */

public final class CA1HandlerFileDownload
  extends CAHTTPHandlerFunctional
{
  /**
   * The v1 file download servlet.
   *
   * @param services The services
   */

  public CA1HandlerFileDownload(
    final RPServiceDirectoryType services)
  {
    super(createCore(services));
  }

  private static CAHTTPHandlerFunctionalCoreType createCore(
    final RPServiceDirectoryType services)
  {
    final var messages =
      services.requireService(CAI1Messages.class);
    final var database =
      services.requireService(CADatabaseType.class);
    final var strings =
      services.requireService(CAStrings.class);

    final CAHTTPHandlerFunctionalCoreType main =
      withAuthentication(services, (request, information, session, user) -> {
        return execute(
          request,
          information,
          user,
          strings,
          messages,
          database
        );
      });

    return withInstrumentation(services, main);
  }

  private static CAHTTPResponseType execute(
    final ServerRequest request,
    final CAHTTPRequestInformation information,
    final CAUser user,
    final CAStrings strings,
    final CAI1Messages messages,
    final CADatabaseType database)
  {
    try {
      CASecurity.check(
        user.userId().id(),
        user.subject(),
        new MObject(
          MTypeName.of("inventory.files"),
          Map.of()
        ),
        MActionName.of("read")
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
    final var vFileId =
      vindicator.addRequiredParameter("FileID", uuids());

    try {
      vindicator.check(request.query().toMap());
    } catch (final CAException e) {
      return errorResponseOf(messages, information, BLAME_CLIENT, e);
    }

    try (var connection = database.openConnection(CADatabaseRole.CARDANT)) {
      try (var transaction = connection.openTransaction()) {
        final var q =
          transaction.queries(CADatabaseQueriesFilesType.GetType.class);
        final var fileOpt =
          q.execute(new Parameters(new CAFileID(vFileId.get()), true));

        if (fileOpt.isEmpty()) {
          throw new CACommandExecutionFailure(
            strings.format(ERROR_NONEXISTENT),
            CAStandardErrorCodes.errorNonexistent(),
            Map.of(
              strings.format(FILE), vFileId.get().toString()
            ),
            Optional.empty(),
            information.requestID(),
            404
          );
        }

        final var file = fileOpt.get();
        return new CAHTTPResponseFixedSize(
          200,
          Set.of(),
          file.mediaType(),
          file.dataOptional().get().data()
        );
      }
    } catch (final CADatabaseException e) {
      return errorResponseOf(messages, information, BLAME_SERVER, e);
    } catch (final CACommandExecutionFailure e) {
      return errorResponseOf(messages, information, e);
    }
  }
}
