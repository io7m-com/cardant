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
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.protocol.inventory.cb.CAI1Messages;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICommandContext;
import com.io7m.cardant.server.controller.inventory.CAICommandExecutor;
import com.io7m.cardant.server.http.CAHTTPErrorStatusException;
import com.io7m.cardant.server.http.CARequestUniqueIDs;
import com.io7m.cardant.server.service.reqlimit.CARequestLimits;
import com.io7m.cardant.server.service.sessions.CASession;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import io.opentelemetry.api.trace.Span;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorIo;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorProtocol;
import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;

/**
 * A servlet for executing a single command.
 */

public final class CA1CommandServlet extends CA1AuthenticatedServlet
{
  private final CADatabaseType database;
  private final CARequestLimits limits;
  private final CAI1Messages messages;
  private final CAICommandExecutor executor;
  private final RPServiceDirectoryType services;

  /**
   * A servlet for executing a single command.
   *
   * @param inServices The service directory
   */

  public CA1CommandServlet(
    final RPServiceDirectoryType inServices)
  {
    super(inServices);

    this.services =
      Objects.requireNonNull(inServices, "inServices");
    this.database =
      inServices.requireService(CADatabaseType.class);
    this.limits =
      inServices.requireService(CARequestLimits.class);
    this.messages =
      inServices.requireService(CAI1Messages.class);
    this.executor =
      new CAICommandExecutor();
  }

  @Override
  protected void serviceAuthenticated(
    final HttpServletRequest request,
    final HttpServletResponse servletResponse,
    final CASession session)
    throws Exception
  {
    try (var input = this.limits.boundedMaximumInput(request, 1048576)) {
      final var data = input.readAllBytes();
      final var message = this.messages.parse(data);
      if (message instanceof CAICommandType<?> command) {
        this.executeCommand(request, servletResponse, session, command);
        return;
      }
    } catch (final CAProtocolException e) {
      throw new CAHTTPErrorStatusException(
        BAD_REQUEST_400,
        errorProtocol(),
        e.getMessage(),
        e
      );
    }

    throw new CAHTTPErrorStatusException(
      BAD_REQUEST_400,
      errorProtocol(),
      this.strings().format("expectedCommand", "EIPCommandType")
    );
  }

  private void executeCommand(
    final HttpServletRequest request,
    final HttpServletResponse servletResponse,
    final CASession session,
    final CAICommandType<?> command)
    throws CADatabaseException, IOException
  {
    try (var connection = this.database.openConnection(CARDANT)) {
      try (var transaction = connection.openTransaction()) {
        this.executeCommandInTransaction(
          request,
          servletResponse,
          session,
          command,
          transaction
        );
      }
    }
  }

  private void executeCommandInTransaction(
    final HttpServletRequest request,
    final HttpServletResponse servletResponse,
    final CASession session,
    final CAICommandType<?> command,
    final CADatabaseTransactionType transaction)
    throws IOException
  {
    final var requestId =
      CARequestUniqueIDs.requestIdFor(request);

    final var context =
      new CAICommandContext(
        this.services,
        requestId,
        transaction,
        session,
        request.getRemoteHost(),
        Optional.ofNullable(request.getHeader("User-Agent"))
          .orElse("<unavailable>")
      );

    final var sends = this.sends();

    try {
      final CAIResponseType result = this.executor.execute(context, command);
      sends.send(servletResponse, 200, result);
      if (result instanceof CAIResponseError error) {
        Span.current()
          .setAttribute("cardant.errorCode", error.errorCode().id());
      } else {
        transaction.commit();
      }
    } catch (final CACommandExecutionFailure e) {
      sends.send(
        servletResponse,
        e.httpStatusCode(),
        new CAIResponseError(
          e.requestId(),
          e.getMessage(),
          e.errorCode(),
          e.attributes(),
          Optional.of(e)
        )
      );
    } catch (final Exception e) {
      sends.send(
        servletResponse,
        500,
        new CAIResponseError(
          requestId,
          e.getMessage(),
          errorIo(),
          Collections.emptySortedMap(),
          Optional.of(e)
        )
      );
    }
  }
}
