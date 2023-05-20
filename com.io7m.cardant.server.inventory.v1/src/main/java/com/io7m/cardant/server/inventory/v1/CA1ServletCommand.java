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
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.protocol.inventory.cb.CAI1Messages;
import com.io7m.cardant.server.controller.CAServerStrings;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICommandContext;
import com.io7m.cardant.server.controller.inventory.CAICommandExecutor;
import com.io7m.cardant.server.http.CAHTTPServletFunctional;
import com.io7m.cardant.server.http.CAHTTPServletFunctionalCoreType;
import com.io7m.cardant.server.http.CAHTTPServletRequestInformation;
import com.io7m.cardant.server.http.CAHTTPServletResponseFixedSize;
import com.io7m.cardant.server.http.CAHTTPServletResponseType;
import com.io7m.cardant.server.service.reqlimit.CARequestLimitExceeded;
import com.io7m.cardant.server.service.reqlimit.CARequestLimits;
import com.io7m.cardant.server.service.sessions.CASession;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import io.opentelemetry.api.trace.Span;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Optional;

import static com.io7m.cardant.protocol.inventory.CAIResponseBlame.BLAME_CLIENT;
import static com.io7m.cardant.protocol.inventory.CAIResponseBlame.BLAME_SERVER;
import static com.io7m.cardant.server.http.CAHTTPServletCoreInstrumented.withInstrumentation;
import static com.io7m.cardant.server.inventory.v1.CA1Errors.errorResponseOf;
import static com.io7m.cardant.server.inventory.v1.CA1ServletCoreAuthenticated.withAuthentication;
import static com.io7m.cardant.server.inventory.v1.CA1ServletCoreTransactional.withTransaction;

/**
 * The v1 command servlet.
 */

public final class CA1ServletCommand extends CAHTTPServletFunctional
{
  /**
   * The v1 command servlet.
   *
   * @param services The services
   */

  public CA1ServletCommand(
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

    return (request, information) -> {
      return withInstrumentation(
        services,
        (req0, info0) -> {
          return withAuthentication(
            services,
            (req1, info1, session, user) -> {
              return withTransaction(
                services,
                (req2, info2, transaction) -> {
                  return execute(
                    services,
                    req2,
                    info2,
                    messages,
                    limits,
                    strings,
                    session,
                    transaction
                  );
                }).execute(req1, info1);
            }).execute(req0, info0);
        }).execute(request, information);
    };
  }

  private static CAHTTPServletResponseType execute(
    final RPServiceDirectoryType services,
    final HttpServletRequest request,
    final CAHTTPServletRequestInformation information,
    final CAI1Messages messages,
    final CARequestLimits limits,
    final CAServerStrings strings,
    final CASession session,
    final CADatabaseTransactionType transaction)
  {
    try (var input =
           limits.boundedMaximumInput(request, 1048576)) {
      final var data =
        input.readAllBytes();
      final var message =
        messages.parse(data);
      if (message instanceof final CAICommandType<?> command) {
        return executeCommand(
          services,
          information,
          messages,
          session,
          command,
          transaction
        );
      }

      return errorResponseOf(
        messages,
        information,
        BLAME_CLIENT,
        new CAProtocolException(
          strings.format("commandNotHere"),
          CAStandardErrorCodes.errorProtocol(),
          Map.of(),
          Optional.empty()
        )
      );

    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    } catch (final CARequestLimitExceeded | CAProtocolException e) {
      return errorResponseOf(messages, information, BLAME_CLIENT, e);
    } catch (final CADatabaseException e) {
      return errorResponseOf(messages, information, BLAME_SERVER, e);
    }
  }

  private static CAHTTPServletResponseType executeCommand(
    final RPServiceDirectoryType services,
    final CAHTTPServletRequestInformation information,
    final CAI1Messages messages,
    final CASession session,
    final CAICommandType<?> command,
    final CADatabaseTransactionType transaction)
    throws CADatabaseException
  {
    final var executor =
      new CAICommandExecutor();

    final var context =
      new CAICommandContext(
        services,
        information.requestId(),
        transaction,
        session,
        information.remoteAddress(),
        information.userAgent()
      );

    final CAIResponseType result;
    try {
      result = executor.execute(context, command);
    } catch (final CACommandExecutionFailure e) {
      return errorResponseOf(messages, information, e);
    }

    if (result instanceof final CAIResponseError error) {
      Span.current().setAttribute("cardant.errorCode", error.errorCode().id());
      return new CAHTTPServletResponseFixedSize(
        switch (error.blame()) {
          case BLAME_SERVER -> 500;
          case BLAME_CLIENT -> 400;
        },
        CAI1Messages.contentType(),
        messages.serialize(error)
      );
    }

    transaction.commit();
    return new CAHTTPServletResponseFixedSize(
      200,
      CAI1Messages.contentType(),
      messages.serialize(result)
    );
  }
}
