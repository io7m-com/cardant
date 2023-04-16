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
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.server.controller.CAServerStrings;
import com.io7m.cardant.server.http.CACommonInstrumentedServlet;
import com.io7m.cardant.server.http.CAHTTPErrorStatusException;
import com.io7m.cardant.server.http.CARequestUniqueIDs;
import com.io7m.cardant.server.service.sessions.CASession;
import com.io7m.cardant.server.service.sessions.CASessionSecretIdentifier;
import com.io7m.cardant.server.service.sessions.CASessionService;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorAuthentication;
import static org.eclipse.jetty.http.HttpStatus.INTERNAL_SERVER_ERROR_500;
import static org.eclipse.jetty.http.HttpStatus.UNAUTHORIZED_401;

/**
 * A servlet that checks that a user is authenticated before delegating
 * execution to a subclass.
 */

public abstract class CA1AuthenticatedServlet
  extends CACommonInstrumentedServlet
{
  private final CAI1Sends sends;
  private final CAServerStrings strings;
  private final CASessionService sessions;

  /**
   * A servlet that checks that a user is authenticated before delegating
   * execution to a subclass.
   *
   * @param services The service directory
   */

  protected CA1AuthenticatedServlet(
    final RPServiceDirectoryType services)
  {
    super(Objects.requireNonNull(services, "services"));

    this.strings =
      services.requireService(CAServerStrings.class);
    this.sends =
      services.requireService(CAI1Sends.class);
    this.sessions =
      services.requireService(CASessionService.class);
  }

  protected final CAI1Sends sends()
  {
    return this.sends;
  }

  protected final CAServerStrings strings()
  {
    return this.strings;
  }

  protected abstract void serviceAuthenticated(
    HttpServletRequest request,
    HttpServletResponse servletResponse,
    CASession session)
    throws Exception;

  @Override
  protected final void service(
    final HttpServletRequest request,
    final HttpServletResponse servletResponse)
    throws IOException
  {
    final var requestId =
      CARequestUniqueIDs.requestIdFor(request);

    try {
      final var session = request.getSession(false);
      if (session != null) {
        final var sessionId =
          (CASessionSecretIdentifier) session.getAttribute("ID");

        if (sessionId != null) {
          final var userSessionOpt =
            this.sessions.findSession(sessionId);
          if (userSessionOpt.isPresent()) {
            this.serviceAuthenticated(
              request, servletResponse, userSessionOpt.get());
            return;
          }
        }
      }

      this.sends.send(
        servletResponse,
        UNAUTHORIZED_401,
        new CAIResponseError(
          requestId,
          this.strings.format("unauthorized"),
          errorAuthentication(),
          Map.of(),
          Optional.empty()
        )
      );
    } catch (final CAHTTPErrorStatusException e) {
      this.sends.send(
        servletResponse,
        e.statusCode(),
        new CAIResponseError(
          requestId,
          e.getMessage(),
          e.errorCode(),
          Map.of(),
          Optional.of(e)
        )
      );
    } catch (final CADatabaseException e) {
      this.sends.send(
        servletResponse,
        INTERNAL_SERVER_ERROR_500,
        new CAIResponseError(
          requestId,
          e.getMessage(),
          e.errorCode(),
          e.attributes(),
          Optional.of(e)
        )
      );
    } catch (final Exception e) {
      throw new IOException(e);
    }
  }
}
