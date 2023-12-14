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
import com.io7m.cardant.database.api.CADatabaseQueriesUsersType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.model.CAValidityException;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.cb.CAI1Messages;
import com.io7m.cardant.server.http.CAHTTPHandlerFunctionalCoreAuthenticatedType;
import com.io7m.cardant.server.http.CAHTTPHandlerFunctionalCoreType;
import com.io7m.cardant.server.http.CAHTTPRequestInformation;
import com.io7m.cardant.server.http.CAHTTPResponseFixedSize;
import com.io7m.cardant.server.http.CAHTTPResponseType;
import com.io7m.cardant.server.service.sessions.CASession;
import com.io7m.cardant.server.service.sessions.CASessionSecretIdentifier;
import com.io7m.cardant.server.service.sessions.CASessionService;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import io.helidon.webserver.http.ServerRequest;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;
import static com.io7m.cardant.protocol.inventory.CAIResponseBlame.BLAME_CLIENT;
import static com.io7m.cardant.protocol.inventory.CAIResponseBlame.BLAME_SERVER;
import static com.io7m.cardant.server.inventory.v1.CA1Errors.errorResponseOf;
import static com.io7m.cardant.server.service.telemetry.api.CAServerTelemetryServiceType.setSpanErrorCode;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_UNAUTHORIZED;

/**
 * A core that executes the given core under authentication.
 */

public final class CA1HandlerCoreAuthenticated
  implements CAHTTPHandlerFunctionalCoreType
{
  private final CAHTTPHandlerFunctionalCoreAuthenticatedType<CASession, CAUser> core;
  private final CADatabaseType database;
  private final CASessionService userSessions;
  private final CAI1Messages messages;
  private final CAStrings strings;

  private CA1HandlerCoreAuthenticated(
    final RPServiceDirectoryType services,
    final CAHTTPHandlerFunctionalCoreAuthenticatedType<CASession, CAUser> inCore)
  {
    Objects.requireNonNull(services, "services");

    this.core =
      Objects.requireNonNull(inCore, "core");
    this.strings =
      services.requireService(CAStrings.class);
    this.database =
      services.requireService(CADatabaseType.class);
    this.userSessions =
      services.requireService(CASessionService.class);
    this.messages =
      services.requireService(CAI1Messages.class);
  }

  /**
   * @param services The services
   * @param inCore   The executed core
   *
   * @return A core that executes the given core under authentication
   */

  public static CAHTTPHandlerFunctionalCoreType withAuthentication(
    final RPServiceDirectoryType services,
    final CAHTTPHandlerFunctionalCoreAuthenticatedType<CASession, CAUser> inCore)
  {
    return new CA1HandlerCoreAuthenticated(services, inCore);
  }

  @Override
  public CAHTTPResponseType execute(
    final ServerRequest request,
    final CAHTTPRequestInformation information)
  {
    final var headers =
      request.headers();
    final var cookies =
      headers.cookies();

    final String cookie;
    try {
      cookie = cookies.get("CARDANT_INVENTORY_SESSION");
      Objects.requireNonNull(cookie, "cookie");
    } catch (final NoSuchElementException e) {
      return this.notAuthenticated(information);
    }

    final CASessionSecretIdentifier userSessionId;
    try {
      userSessionId = new CASessionSecretIdentifier(cookie);
    } catch (final CAValidityException e) {
      return this.notAuthenticated(information);
    }

    final var userSessionOpt =
      this.userSessions.findSession(userSessionId);

    if (userSessionOpt.isEmpty()) {
      return this.notAuthenticated(information);
    }

    final var userSession =
      userSessionOpt.get();

    final Optional<CAUser> userOpt;
    try {
      userOpt = this.userGet(userSession.userId());
    } catch (final CADatabaseException e) {
      setSpanErrorCode(e.errorCode());
      return errorResponseOf(this.messages, information, BLAME_SERVER, e);
    }

    if (userOpt.isEmpty()) {
      return this.notAuthenticated(information);
    }

    return this.core.executeAuthenticated(
      request,
      information,
      userSession,
      userOpt.get()
    );
  }

  private CAHTTPResponseType notAuthenticated(
    final CAHTTPRequestInformation information)
  {
    return new CAHTTPResponseFixedSize(
      401,
      Set.of(),
      CAI1Messages.contentType(),
      this.messages.serialize(
        new CAIResponseError(
          information.requestID(),
          this.strings.format(ERROR_UNAUTHORIZED),
          CAStandardErrorCodes.errorAuthentication(),
          Map.of(),
          Optional.empty(),
          Optional.empty(),
          BLAME_CLIENT
        )
      )
    );
  }

  private Optional<CAUser> userGet(
    final UUID id)
    throws CADatabaseException
  {
    try (var c = this.database.openConnection(CARDANT)) {
      try (var t = c.openTransaction()) {
        final var q =
          t.queries(CADatabaseQueriesUsersType.GetType.class);
        return q.execute(id);
      }
    }
  }
}
