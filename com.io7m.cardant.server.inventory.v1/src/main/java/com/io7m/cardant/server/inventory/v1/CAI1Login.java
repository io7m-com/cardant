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

import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.database.api.CADatabaseUserUpdates;
import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.inventory.CAICommandLogin;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseLogin;
import com.io7m.cardant.protocol.inventory.cb.CAI1Messages;
import com.io7m.cardant.server.controller.CAServerStrings;
import com.io7m.cardant.server.http.CACommonInstrumentedServlet;
import com.io7m.cardant.server.http.CAHTTPErrorStatusException;
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
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorHttpMethod;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorProtocol;
import static com.io7m.cardant.server.http.CARequestUniqueIDs.requestIdFor;
import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;
import static org.eclipse.jetty.http.HttpStatus.METHOD_NOT_ALLOWED_405;

/**
 * A servlet that handles user logins.
 */

public final class CAI1Login extends CACommonInstrumentedServlet
{
  private final CAI1Messages messages;
  private final CAServerStrings strings;
  private final CAI1Sends errors;
  private final CARequestLimits limits;
  private final CAIdstoreClientsType idClients;
  private final CASessionService sessions;
  private final CADatabaseType database;

  /**
   * A servlet that handles user logins.
   *
   * @param inServices The service directory
   */

  public CAI1Login(
    final RPServiceDirectoryType inServices)
  {
    super(Objects.requireNonNull(inServices, "services"));

    this.messages =
      inServices.requireService(CAI1Messages.class);
    this.idClients =
      inServices.requireService(CAIdstoreClientsType.class);
    this.strings =
      inServices.requireService(CAServerStrings.class);
    this.errors =
      inServices.requireService(CAI1Sends.class);
    this.limits =
      inServices.requireService(CARequestLimits.class);
    this.sessions =
      inServices.requireService(CASessionService.class);
    this.database =
      inServices.requireService(CADatabaseType.class);
  }

  @Override
  protected void service(
    final HttpServletRequest request,
    final HttpServletResponse response)
    throws IOException
  {
    try {
      if (!Objects.equals(request.getMethod(), "POST")) {
        throw new CAHTTPErrorStatusException(
          this.strings.format("methodNotAllowed"),
          errorHttpMethod(),
          Map.of(),
          Optional.empty(),
          METHOD_NOT_ALLOWED_405
        );
      }

      final var login =
        this.readLoginCommand(request);

      final UUID userId;
      try (var client = this.idClients.createClient()) {
        final var result =
          client.login(new IdUClientCredentials(
              login.userName().value(),
              login.password(),
              this.idClients.baseURI(),
              Map.ofEntries(
                Map.entry(
                  IdLoginMetadataStandard.remoteHostProxied(),
                  request.getRemoteAddr()
                )
              )
            )).map(IdUResponseLogin.class::cast)
            .orElseThrow(IdUClientException::ofError);
        userId = result.user().id();
      }

      var icUser = new CAUser(userId, new MSubject(Set.of()));
      icUser = CADatabaseUserUpdates.userMerge(this.database, icUser);
      final var session =
        this.sessions.createSession(icUser.userId(), icUser.subject());
      final var httpSession =
        request.getSession(true);

      httpSession.setAttribute("ID", session.id());
      this.sendLoginResponse(request, response, userId);

    } catch (final CAHTTPErrorStatusException e) {
      this.errors.send(
        response,
        e.httpStatusCode(),
        new CAIResponseError(
          requestIdFor(request),
          e.getMessage(),
          e.errorCode(),
          e.attributes(),
          e.remediatingAction(),
          Optional.of(e)
        )
      );
    } catch (final IdUClientException e) {
      this.errors.send(
        response,
        401,
        new CAIResponseError(
          requestIdFor(request),
          e.message(),
          new CAErrorCode(e.errorCode().id()),
          e.attributes(),
          e.remediatingAction(),
          Optional.of(e)
        )
      );
    } catch (final CAException e) {
      this.errors.send(
        response,
        500,
        new CAIResponseError(
          requestIdFor(request),
          e.getMessage(),
          e.errorCode(),
          e.attributes(),
          e.remediatingAction(),
          Optional.of(e)
        )
      );
    } catch (final Exception e) {
      this.errors.send(
        response,
        500,
        new CAIResponseError(
          requestIdFor(request),
          e.getMessage(),
          CAStandardErrorCodes.errorIo(),
          Map.of(),
          Optional.empty(),
          Optional.of(e)
        )
      );
    }
  }

  private void sendLoginResponse(
    final HttpServletRequest request,
    final HttpServletResponse response,
    final UUID userId)
    throws IOException
  {
    response.setStatus(200);
    response.setContentType(CAI1Messages.contentType());

    try {
      final var data =
        this.messages.serialize(
          new CAIResponseLogin(
            requestIdFor(request),
            userId
          )
        );
      response.setContentLength(data.length);
      try (var output = response.getOutputStream()) {
        output.write(data);
      }
    } catch (final CAProtocolException e) {
      throw new IOException(e);
    }
  }

  private CAICommandLogin readLoginCommand(
    final HttpServletRequest request)
    throws CAHTTPErrorStatusException, IOException
  {
    try (var input = this.limits.boundedMaximumInput(request, 1024)) {
      final var data = input.readAllBytes();
      final var message = this.messages.parse(data);
      if (message instanceof CAICommandLogin login) {
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
      this.strings.format("expectedCommand", "CommandLogin"),
      errorProtocol(),
      Map.of(),
      Optional.empty(),
      BAD_REQUEST_400
    );
  }
}
