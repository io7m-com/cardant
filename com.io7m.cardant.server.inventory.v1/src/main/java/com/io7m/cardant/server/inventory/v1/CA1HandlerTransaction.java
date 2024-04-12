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
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.protocol.inventory.CAITransactionResponse;
import com.io7m.cardant.protocol.inventory.cb.CAI1Messages;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICommandContext;
import com.io7m.cardant.server.controller.inventory.CAICommandExecutor;
import com.io7m.cardant.server.http.CAHTTPHandlerFunctional;
import com.io7m.cardant.server.http.CAHTTPHandlerFunctionalCoreType;
import com.io7m.cardant.server.http.CAHTTPRequestInformation;
import com.io7m.cardant.server.http.CAHTTPResponseType;
import com.io7m.cardant.server.service.reqlimit.CARequestLimitExceeded;
import com.io7m.cardant.server.service.reqlimit.CARequestLimits;
import com.io7m.cardant.server.service.sessions.CASession;
import com.io7m.cardant.server.service.telemetry.api.CAServerTelemetryServiceType;
import com.io7m.cardant.strings.CAStringConstants;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import io.helidon.webserver.http.ServerRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.io7m.cardant.protocol.inventory.CAIResponseBlame.BLAME_CLIENT;
import static com.io7m.cardant.protocol.inventory.CAIResponseBlame.BLAME_SERVER;
import static com.io7m.cardant.server.http.CAHTTPHandlerCoreInstrumented.withInstrumentation;
import static com.io7m.cardant.server.inventory.v1.CA1Errors.errorOf;
import static com.io7m.cardant.server.inventory.v1.CA1Errors.transactionResponseOf;
import static com.io7m.cardant.server.inventory.v1.CA1HandlerCoreAuthenticated.withAuthentication;
import static com.io7m.cardant.server.inventory.v1.CA1HandlerCoreTransactional.withTransaction;
import static com.io7m.cardant.server.service.telemetry.api.CAServerTelemetryServiceType.setSpanErrorCode;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_COMMAND_NOT_HERE;

/**
 * The v1 transaction servlet.
 */

public final class CA1HandlerTransaction extends CAHTTPHandlerFunctional
{
  /**
   * The v1 transaction servlet.
   *
   * @param services The services
   */

  public CA1HandlerTransaction(
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
    final var telemetry =
      services.requireService(CAServerTelemetryServiceType.class);

    final var authenticated =
      withAuthentication(services, (req1, info1, session, user) -> {
        return withTransaction(services, (req2, info2, transaction) -> {
          return execute(
            services,
            req2,
            info2,
            messages,
            telemetry,
            limits,
            strings,
            session,
            transaction
          );
        }).execute(req1, info1);
      });

    return withInstrumentation(services, authenticated);
  }

  private static CAHTTPResponseType execute(
    final RPServiceDirectoryType services,
    final ServerRequest request,
    final CAHTTPRequestInformation information,
    final CAI1Messages messages,
    final CAServerTelemetryServiceType telemetry,
    final CARequestLimits limits,
    final CAStrings strings,
    final CASession session,
    final CADatabaseTransactionType transaction)
  {
    final var results = new ArrayList<CAIResponseType>(16);
    try (var input = limits.boundedMaximumInput(request, 1048576L)) {
      final var messagesParsed =
        parseMessages(telemetry, strings, messages, input);

      for (final var message : messagesParsed) {
        if (message instanceof final CAICommandType<?> command) {
          final var r =
            executeCommand(
              services,
              information,
              session,
              command,
              transaction
            );

          results.add(r);
          if (r instanceof CAIResponseError) {
            return respond(messages, results);
          }
          continue;
        }

        results.add(errorOf(
          information,
          BLAME_CLIENT,
          new CAProtocolException(
            strings.format(ERROR_COMMAND_NOT_HERE),
            CAStandardErrorCodes.errorApiMisuse(),
            Map.of(),
            Optional.empty()
          )));

        return respond(messages, results);
      }

      commit(telemetry, transaction);
      return respond(messages, results);
    } catch (final IOException e) {
      setSpanErrorCode(CAStandardErrorCodes.errorIo());
      results.add(errorOf(information, BLAME_SERVER, e));
      return respond(messages, results);
    } catch (final CARequestLimitExceeded | CAProtocolException e) {
      setSpanErrorCode(e.errorCode());
      results.add(errorOf(information, BLAME_CLIENT, e));
      return respond(messages, results);
    } catch (final CADatabaseException e) {
      setSpanErrorCode(e.errorCode());
      results.add(errorOf(information, BLAME_SERVER, e));
      return respond(messages, results);
    }
  }

  private static CAHTTPResponseType respond(
    final CAI1Messages messages,
    final ArrayList<CAIResponseType> results)
  {
    return transactionResponseOf(
      messages,
      new CAITransactionResponse(results)
    );
  }

  private static List<CAIMessageType> parseMessages(
    final CAServerTelemetryServiceType telemetry,
    final CAStrings strings,
    final CAI1Messages messages,
    final InputStream input)
    throws IOException, CAProtocolException
  {
    final var parseSpan =
      telemetry.tracer()
        .spanBuilder("ParseMessages")
        .startSpan();

    try (var ignored = parseSpan.makeCurrent()) {
      final var data = parseMessageReadData(telemetry, strings, input);
      return parseMessagesDeserialize(telemetry, messages, data);
    } finally {
      parseSpan.end();
    }
  }

  private static List<CAIMessageType> parseMessagesDeserialize(
    final CAServerTelemetryServiceType telemetry,
    final CAI1Messages messages,
    final List<byte[]> data)
    throws CAProtocolException
  {
    final var readSpan =
      telemetry.tracer()
        .spanBuilder("Deserialize")
        .startSpan();

    final var results = new ArrayList<CAIMessageType>(data.size());
    try (var ignored = readSpan.makeCurrent()) {
      for (final var item : data) {
        results.add(messages.parse(item));
      }
      return List.copyOf(results);
    } finally {
      readSpan.end();
    }
  }

  private static List<byte[]> parseMessageReadData(
    final CAServerTelemetryServiceType telemetry,
    final CAStrings strings,
    final InputStream input)
    throws IOException, CAProtocolException
  {
    final var readSpan =
      telemetry.tracer()
        .spanBuilder("Read")
        .startSpan();

    final var results = new ArrayList<byte[]>();
    try (var ignored = readSpan.makeCurrent()) {
      while (true) {
        final var size =
          input.readNBytes(4);

        if (size.length != 4) {
          throw new CAProtocolException(
            strings.format(CAStringConstants.ERROR_IO),
            CAStandardErrorCodes.errorApiMisuse(),
            Map.of(),
            Optional.empty()
          );
        }

        final var sizeBuffer = ByteBuffer.wrap(size);
        sizeBuffer.order(ByteOrder.BIG_ENDIAN);

        final var messageSize = sizeBuffer.getInt(0);
        if (messageSize == 0) {
          return List.copyOf(results);
        }

        results.add(input.readNBytes(messageSize));
      }
    } finally {
      readSpan.end();
    }
  }

  private static CAIResponseType executeCommand(
    final RPServiceDirectoryType services,
    final CAHTTPRequestInformation information,
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
        information.requestID(),
        transaction,
        session,
        information.remoteAddress(),
        information.userAgent()
      );

    try {
      return executor.execute(context, command);
    } catch (final CACommandExecutionFailure e) {
      setSpanErrorCode(e.errorCode());
      return errorOf(information, e.blame(), e);
    }
  }

  private static void commit(
    final CAServerTelemetryServiceType telemetry,
    final CADatabaseTransactionType transaction)
    throws CADatabaseException
  {
    final var commitSpan =
      telemetry.tracer()
        .spanBuilder("Commit")
        .startSpan();

    try (var ignored = commitSpan.makeCurrent()) {
      transaction.commit();
    } finally {
      commitSpan.end();
    }
  }
}
