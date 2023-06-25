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
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.cb.CAI1Messages;
import com.io7m.cardant.server.http.CAHTTPServletFunctionalCoreAuthenticatedType;
import com.io7m.cardant.server.http.CAHTTPServletFunctionalCoreType;
import com.io7m.cardant.server.http.CAHTTPServletRequestInformation;
import com.io7m.cardant.server.http.CAHTTPServletResponseFixedSize;
import com.io7m.cardant.server.http.CAHTTPServletResponseType;
import com.io7m.cardant.server.service.sessions.CASession;
import com.io7m.cardant.server.service.sessions.CASessionSecretIdentifier;
import com.io7m.cardant.server.service.sessions.CASessionService;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;
import static com.io7m.cardant.protocol.inventory.CAIResponseBlame.BLAME_CLIENT;
import static com.io7m.cardant.protocol.inventory.CAIResponseBlame.BLAME_SERVER;
import static com.io7m.cardant.server.inventory.v1.CA1Errors.errorResponseOf;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_UNAUTHORIZED;

/**
 * A core that executes the given core under authentication.
 */

public final class CA1ServletCoreAuthenticated
  implements CAHTTPServletFunctionalCoreType
{
  private final CAHTTPServletFunctionalCoreAuthenticatedType<CASession, CAUser> core;
  private final CADatabaseType database;
  private final CASessionService sessions;
  private final CAI1Messages messages;
  private final CAStrings strings;

  private CA1ServletCoreAuthenticated(
    final RPServiceDirectoryType services,
    final CAHTTPServletFunctionalCoreAuthenticatedType<CASession, CAUser> inCore)
  {
    Objects.requireNonNull(services, "services");

    this.core =
      Objects.requireNonNull(inCore, "core");
    this.strings =
      services.requireService(CAStrings.class);
    this.database =
      services.requireService(CADatabaseType.class);
    this.sessions =
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

  public static CAHTTPServletFunctionalCoreType withAuthentication(
    final RPServiceDirectoryType services,
    final CAHTTPServletFunctionalCoreAuthenticatedType<CASession, CAUser> inCore)
  {
    return new CA1ServletCoreAuthenticated(services, inCore);
  }

  @Override
  public CAHTTPServletResponseType execute(
    final HttpServletRequest request,
    final CAHTTPServletRequestInformation information)
  {
    final var httpSession =
      request.getSession(true);
    final var sessionId =
      (CASessionSecretIdentifier) httpSession.getAttribute("ID");

    if (sessionId == null) {
      return this.notAuthenticated(information);
    }

    final var sessionOpt =
      this.sessions.findSession(sessionId);

    if (sessionOpt.isEmpty()) {
      return this.notAuthenticated(information);
    }

    final var session =
      sessionOpt.get();

    final Optional<CAUser> adminOpt;
    try {
      adminOpt = this.userGet(session.userId());
    } catch (final CADatabaseException e) {
      return errorResponseOf(this.messages, information, BLAME_SERVER, e);
    }

    if (adminOpt.isEmpty()) {
      return this.notAuthenticated(information);
    }

    return this.core.executeAuthenticated(
      request,
      information,
      session,
      adminOpt.get()
    );
  }

  private CAHTTPServletResponseType notAuthenticated(
    final CAHTTPServletRequestInformation information)
  {
    return new CAHTTPServletResponseFixedSize(
      401,
      CAI1Messages.contentType(),
      this.messages.serialize(
        new CAIResponseError(
          information.requestId(),
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
          t.queries(CADatabaseQueriesUsersType.class);
        return q.userGet(id);
      }
    }
  }
}
