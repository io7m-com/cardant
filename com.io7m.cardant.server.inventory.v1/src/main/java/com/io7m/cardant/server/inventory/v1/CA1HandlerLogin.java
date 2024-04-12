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
import com.io7m.cardant.database.api.CADatabaseQueriesAuditType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.database.api.CADatabaseUserUpdates;
import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAAuditEvent;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.protocol.inventory.CAICommandLogin;
import com.io7m.cardant.protocol.inventory.CAIResponseBlame;
import com.io7m.cardant.protocol.inventory.CAIResponseLogin;
import com.io7m.cardant.protocol.inventory.cb.CAI1Messages;
import com.io7m.cardant.server.http.CAHTTPCookieDeclaration;
import com.io7m.cardant.server.http.CAHTTPErrorStatusException;
import com.io7m.cardant.server.http.CAHTTPHandlerFunctional;
import com.io7m.cardant.server.http.CAHTTPHandlerFunctionalCoreType;
import com.io7m.cardant.server.http.CAHTTPRequestInformation;
import com.io7m.cardant.server.http.CAHTTPResponseFixedSize;
import com.io7m.cardant.server.http.CAHTTPResponseType;
import com.io7m.cardant.server.service.configuration.CAConfigurationServiceType;
import com.io7m.cardant.server.service.idstore.CAIdstoreClientsType;
import com.io7m.cardant.server.service.reqlimit.CARequestLimits;
import com.io7m.cardant.server.service.sessions.CASessionService;
import com.io7m.cardant.server.service.telemetry.api.CAServerTelemetryServiceType;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.idstore.model.IdLoginMetadataStandard;
import com.io7m.idstore.protocol.user.IdUResponseLogin;
import com.io7m.idstore.user_client.api.IdUClientConnectionParameters;
import com.io7m.idstore.user_client.api.IdUClientException;
import com.io7m.medrina.api.MSubject;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import io.helidon.webserver.http.ServerRequest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorApiMisuse;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorProtocol;
import static com.io7m.cardant.server.http.CAHTTPHandlerCoreInstrumented.withInstrumentation;
import static com.io7m.cardant.server.inventory.v1.CA1Errors.errorResponseOf;
import static com.io7m.cardant.server.inventory.v1.CA1HandlerCoreTransactional.withTransaction;
import static com.io7m.cardant.strings.CAStringConstants.COMMAND;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_EXPECTED_COMMAND;
import static io.helidon.http.Status.BAD_REQUEST_400;
import static java.util.Map.entry;

/**
 * The v1 login servlet.
 */

public final class CA1HandlerLogin extends CAHTTPHandlerFunctional
{
  private static final Duration DEFAULT_EXPIRATION =
    Duration.ofDays(365L);

  /**
   * The v1 login servlet.
   *
   * @param services The services
   */

  public CA1HandlerLogin(
    final RPServiceDirectoryType services)
  {
    super(createCore(services));
  }

  private static CAHTTPHandlerFunctionalCoreType createCore(
    final RPServiceDirectoryType services)
  {
    final var limits =
      services.requireService(CARequestLimits.class);
    final var messages =
      services.requireService(CAI1Messages.class);
    final var strings =
      services.requireService(CAStrings.class);
    final var database =
      services.requireService(CADatabaseType.class);
    final var sessions =
      services.requireService(CASessionService.class);
    final var telemetry =
      services.requireService(CAServerTelemetryServiceType.class);
    final var idClients =
      services.requireService(CAIdstoreClientsType.class);
    final var configuration =
      services.requireService(CAConfigurationServiceType.class);

    final var sessionDuration =
      configuration.configuration()
        .inventoryApiConfiguration()
        .sessionExpiration()
        .orElse(DEFAULT_EXPIRATION);

    final var transactional =
      withTransaction(services, (request, info, transaction) -> {
        return execute(
          database,
          telemetry,
          idClients,
          sessions,
          strings,
          limits,
          messages,
          request,
          info,
          transaction,
          sessionDuration
        );
      });

    return withInstrumentation(services, transactional);
  }

  private static CAHTTPResponseType execute(
    final CADatabaseType database,
    final CAServerTelemetryServiceType telemetry,
    final CAIdstoreClientsType idClients,
    final CASessionService sessions,
    final CAStrings strings,
    final CARequestLimits limits,
    final CAI1Messages messages,
    final ServerRequest request,
    final CAHTTPRequestInformation information,
    final CADatabaseTransactionType transaction,
    final Duration sessionDuration)
  {
    final CAICommandLogin login;
    try {
      login = readLoginCommand(strings, limits, messages, request);
    } catch (final CAException e) {
      return errorResponseOf(
        messages,
        information,
        CAIResponseBlame.BLAME_CLIENT,
        e);
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
          information.remoteAddress()
        )
      );

    final var clientCredentials =
      new IdUClientConnectionParameters(
        login.userName().value(),
        login.password(),
        idClients.baseURI(),
        clientMetadata,
        Duration.ofSeconds(30L),
        Duration.ofSeconds(30L)
      );

    final var span =
      telemetry.tracer()
        .spanBuilder("IdstoreLogin")
        .startSpan();

    try (var ignored = span.makeCurrent()) {
      try (var client = idClients.createClient()) {
        final var result =
          client.connectOrThrow(clientCredentials);
        final var resultM =
          (IdUResponseLogin) result;

        userId = resultM.user().id();
      } catch (final IdUClientException e) {
        span.setAttribute("idstore.errorCode", e.errorCode().id());
        return errorResponseOf(
          messages,
          information,
          CAIResponseBlame.BLAME_CLIENT,
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
    } finally {
      span.end();
    }

    /*
     * Merge in the user's latest username and credentials.
     */

    var icUser =
      new CAUser(
        new CAUserID(userId),
        login.userName(),
        new MSubject(Set.of()));

    try {
      icUser = CADatabaseUserUpdates.userMerge(database, icUser);
    } catch (final CADatabaseException e) {
      return errorResponseOf(
        messages,
        information,
        CAIResponseBlame.BLAME_SERVER,
        e);
    }

    return createNewSession(
      messages,
      sessions,
      information,
      transaction,
      login,
      icUser,
      sessionDuration
    );
  }


  /**
   * Create a new session.
   */

  private static CAHTTPResponseType createNewSession(
    final CAI1Messages messages,
    final CASessionService sessions,
    final CAHTTPRequestInformation information,
    final CADatabaseTransactionType transaction,
    final CAICommandLogin login,
    final CAUser icUser,
    final Duration sessionDuration)
  {
    final var session =
      sessions.createSession(
        icUser.userId(),
        login.userName(),
        icUser.subject()
      );

    try {
      transaction.queries(CADatabaseQueriesAuditType.EventAddType.class)
        .execute(new CAAuditEvent(
          0L,
          OffsetDateTime.now(),
          icUser.userId(),
          "USER_LOGGED_IN",
          Map.ofEntries(
            entry("Host", information.remoteAddress()),
            entry("UserAgent", information.userAgent())
          )
        ));

      transaction.commit();
    } catch (final CADatabaseException e) {
      return errorResponseOf(
        messages,
        information,
        CAIResponseBlame.BLAME_SERVER,
        e);
    }

    final var cookie =
      new CAHTTPCookieDeclaration(
        "CARDANT_INVENTORY_SESSION",
        session.id().value(),
        sessionDuration
      );

    return new CAHTTPResponseFixedSize(
      200,
      Set.of(cookie),
      CAI1Messages.contentType(),
      messages.serialize(
        new CAIResponseLogin(
          information.requestID(),
          icUser.userId()
        )
      )
    );
  }

  private static CAICommandLogin readLoginCommand(
    final CAStrings strings,
    final CARequestLimits limits,
    final CAI1Messages messages,
    final ServerRequest request)
    throws CAHTTPErrorStatusException, IOException
  {
    try (var input = limits.boundedMaximumInputForLoginCommand(request)) {
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
        BAD_REQUEST_400.code()
      );
    }

    throw new CAHTTPErrorStatusException(
      strings.format(ERROR_EXPECTED_COMMAND),
      errorApiMisuse(),
      Map.of(
        strings.format(COMMAND), "CommandLogin"
      ),
      Optional.empty(),
      BAD_REQUEST_400.code()
    );
  }
}
