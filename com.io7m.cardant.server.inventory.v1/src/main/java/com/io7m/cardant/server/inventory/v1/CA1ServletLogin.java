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
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.database.api.CADatabaseUserUpdates;
import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.protocol.inventory.CAICommandLogin;
import com.io7m.cardant.protocol.inventory.CAIResponseLogin;
import com.io7m.cardant.protocol.inventory.cb.CAI1Messages;
import com.io7m.cardant.server.controller.CAServerStrings;
import com.io7m.cardant.server.http.CAHTTPErrorStatusException;
import com.io7m.cardant.server.http.CAHTTPServletFunctional;
import com.io7m.cardant.server.http.CAHTTPServletFunctionalCoreType;
import com.io7m.cardant.server.http.CAHTTPServletRequestInformation;
import com.io7m.cardant.server.http.CAHTTPServletResponseFixedSize;
import com.io7m.cardant.server.http.CAHTTPServletResponseType;
import com.io7m.cardant.server.service.idstore.CAIdstoreClientsType;
import com.io7m.cardant.server.service.reqlimit.CARequestLimits;
import com.io7m.cardant.server.service.sessions.CASessionService;
import com.io7m.idstore.model.IdLoginMetadataStandard;
import com.io7m.idstore.protocol.user.IdUResponseLogin;
import com.io7m.idstore.user_client.api.IdUClientCredentials;
import com.io7m.idstore.user_client.api.IdUClientException;
import com.io7m.medrina.api.MSubject;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorProtocol;
import static com.io7m.cardant.protocol.inventory.CAIResponseBlame.BLAME_CLIENT;
import static com.io7m.cardant.protocol.inventory.CAIResponseBlame.BLAME_SERVER;
import static com.io7m.cardant.server.http.CAHTTPServletCoreInstrumented.withInstrumentation;
import static com.io7m.cardant.server.inventory.v1.CA1Errors.errorResponseOf;
import static com.io7m.cardant.server.inventory.v1.CA1ServletCoreTransactional.withTransaction;
import static java.util.Map.entry;
import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;

/**
 * The v1 login servlet.
 */

public final class CA1ServletLogin extends CAHTTPServletFunctional
{
  /**
   * The v1 login servlet.
   *
   * @param services The services
   */

  public CA1ServletLogin(
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
    final var strings =
      services.requireService(CAServerStrings.class);
    final var idClients =
      services.requireService(CAIdstoreClientsType.class);
    final var database =
      services.requireService(CADatabaseType.class);
    final var sessions =
      services.requireService(CASessionService.class);

    return (request, information) -> {
      return withInstrumentation(
        services,
        (req0, info0) -> {
          return withTransaction(
            services,
            (req1, info1, transaction) -> {
              return execute(
                strings,
                limits,
                messages,
                idClients,
                database,
                sessions,
                req1,
                info1,
                transaction
              );
            }).execute(req0, info0);
        }).execute(request, information);
    };
  }

  private static CAHTTPServletResponseType execute(
    final CAServerStrings strings,
    final CARequestLimits limits,
    final CAI1Messages messages,
    final CAIdstoreClientsType idClients,
    final CADatabaseType database,
    final CASessionService sessions,
    final HttpServletRequest request,
    final CAHTTPServletRequestInformation information,
    final CADatabaseTransactionType transaction)
  {
    final CAICommandLogin login;
    try {
      login = readLoginCommand(strings, limits, messages, request);
    } catch (final CAException e) {
      return errorResponseOf(messages, information, BLAME_CLIENT, e);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }

    /*
     * Authenticate with idstore.
     */

    final UUID userId;
    final var clientMetadata =
      Map.ofEntries(
        entry(
          IdLoginMetadataStandard.remoteHostProxied(),
          request.getRemoteAddr()
        )
      );

    final var clientCredentials =
      new IdUClientCredentials(
        login.userName().value(),
        login.password(),
        idClients.baseURI(),
        clientMetadata
      );

    try (var client = idClients.createClient()) {
      final var result =
        client.login(clientCredentials)
          .map(IdUResponseLogin.class::cast)
          .orElseThrow(IdUClientException::ofError);
      userId = result.user().id();
    } catch (final IdUClientException e) {
      return errorResponseOf(
        messages,
        information,
        BLAME_CLIENT,
        new CAException(
          e.getMessage(),
          e,
          CAStandardErrorCodes.errorAuthentication(),
          e.attributes(),
          e.remediatingAction()
        )
      );
    } catch (final InterruptedException e) {
      throw new IllegalStateException(e);
    }

    /*
     * Merge in the user's latest username and credentials.
     */

    var icUser =
      new CAUser(userId, login.userName(), new MSubject(Set.of()));

    try {
      icUser = CADatabaseUserUpdates.userMerge(database, icUser);
    } catch (final CADatabaseException e) {
      return errorResponseOf(messages, information, BLAME_SERVER, e);
    }

    /*
     * Create a new session.
     */

    final var session =
      sessions.createSession(
        icUser.userId(),
        login.userName(),
        icUser.subject()
      );

    try {
      transaction.commit();
    } catch (final CADatabaseException e) {
      return errorResponseOf(messages, information, BLAME_SERVER, e);
    }

    final var httpSession = request.getSession(true);
    httpSession.setAttribute("ID", session.id());

    return new CAHTTPServletResponseFixedSize(
      200,
      CAI1Messages.contentType(),
      messages.serialize(
        new CAIResponseLogin(
          information.requestId(),
          userId
        )
      )
    );
  }

  private static CAICommandLogin readLoginCommand(
    final CAServerStrings strings,
    final CARequestLimits limits,
    final CAI1Messages messages,
    final HttpServletRequest request)
    throws CAHTTPErrorStatusException, IOException
  {
    try (var input = limits.boundedMaximumInput(request, 1024)) {
      final var data = input.readAllBytes();
      final var message = messages.parse(data);
      if (message instanceof final CAICommandLogin login) {
        return login;
      }
    } catch (final CAException e) {
      throw new CAHTTPErrorStatusException(
        e.getMessage(),
        e,
        errorProtocol(),
        e.attributes(),
        e.remediatingAction(),
        BAD_REQUEST_400
      );
    }

    throw new CAHTTPErrorStatusException(
      strings.format("expectedCommand", "CommandLogin"),
      errorProtocol(),
      Map.of(),
      Optional.empty(),
      BAD_REQUEST_400
    );
  }
}
