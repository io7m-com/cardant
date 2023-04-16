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


package com.io7m.cardant.server.controller.command_exec;

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAValidityException;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolMessageType;
import com.io7m.cardant.server.controller.CAServerStrings;
import com.io7m.cardant.server.controller.security.CASecurityException;
import com.io7m.cardant.server.service.clock.CAServerClock;
import com.io7m.cardant.server.service.sessions.CASession;
import com.io7m.cardant.server.service.telemetry.api.CAServerTelemetryServiceType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import io.opentelemetry.api.trace.Tracer;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * The context for execution of a command (or set of commands in a
 * transaction).
 *
 * @param <E> The type of error messages
 */

public abstract class CACommandContext<E extends CAProtocolMessageType>
{
  private final RPServiceDirectoryType services;
  private final UUID requestId;
  private final CADatabaseTransactionType transaction;
  private final CAServerClock clock;
  private final CAServerStrings strings;
  private final CASession session;
  private final String remoteHost;
  private final String remoteUserAgent;
  private final Tracer tracer;

  /**
   * The context for execution of a command (or set of commands in a
   * transaction).
   *
   * @param inServices        The service directory
   * @param inRequestId       The request ID
   * @param inTransaction     The transaction
   * @param inSession         The user session
   * @param inRemoteHost      The remote host
   * @param inRemoteUserAgent The remote user agent
   */

  public CACommandContext(
    final RPServiceDirectoryType inServices,
    final UUID inRequestId,
    final CADatabaseTransactionType inTransaction,
    final CASession inSession,
    final String inRemoteHost,
    final String inRemoteUserAgent)
  {
    this.services =
      Objects.requireNonNull(inServices, "services");
    this.requestId =
      Objects.requireNonNull(inRequestId, "requestId");
    this.transaction =
      Objects.requireNonNull(inTransaction, "transaction");
    this.session =
      Objects.requireNonNull(inSession, "inSession");
    this.remoteHost =
      Objects.requireNonNull(inRemoteHost, "remoteHost");
    this.remoteUserAgent =
      Objects.requireNonNull(inRemoteUserAgent, "remoteUserAgent");

    this.clock =
      inServices.requireService(CAServerClock.class);
    this.strings =
      inServices.requireService(CAServerStrings.class);
    this.tracer =
      inServices.requireService(CAServerTelemetryServiceType.class)
        .tracer();
  }

  /**
   * @return The user session
   */

  public final CASession session()
  {
    return this.session;
  }

  /**
   * @return The remote host
   */

  public final String remoteHost()
  {
    return this.remoteHost;
  }

  /**
   * @return The remote user agent
   */

  public final String remoteUserAgent()
  {
    return this.remoteUserAgent;
  }

  /**
   * @return The service directory used during execution
   */

  public final RPServiceDirectoryType services()
  {
    return this.services;
  }

  /**
   * @return The ID of the incoming request
   */

  public final UUID requestId()
  {
    return this.requestId;
  }

  /**
   * @return The database transaction
   */

  public final CADatabaseTransactionType transaction()
  {
    return this.transaction;
  }

  /**
   * @return The OpenTelemetry tracer
   */

  public final Tracer tracer()
  {
    return this.tracer;
  }

  /**
   * @return The current time
   */

  public final OffsetDateTime now()
  {
    return this.clock.now();
  }

  /**
   * Produce an exception indicating an error, with a formatted error message.
   *
   * @param statusCode The HTTP status code
   * @param errorCode  The error code
   * @param attributes The error attributes
   * @param messageId  The string resource message ID
   * @param args       The string resource format arguments
   *
   * @return An execution failure
   */

  public final CACommandExecutionFailure failFormatted(
    final int statusCode,
    final CAErrorCode errorCode,
    final Map<String, String> attributes,
    final String messageId,
    final Object... args)
  {
    return this.fail(
      statusCode,
      errorCode,
      this.strings.format(messageId, args),
      attributes
    );
  }

  /**
   * Produce an exception indicating an error, with a string constant message.
   *
   * @param statusCode The HTTP status code
   * @param errorCode  The error code
   * @param attributes The error attributes
   * @param message    The string message
   *
   * @return An execution failure
   */

  public final CACommandExecutionFailure fail(
    final int statusCode,
    final CAErrorCode errorCode,
    final String message,
    final Map<String, String> attributes)
  {
    return new CACommandExecutionFailure(
      errorCode,
      message,
      attributes,
      this.requestId,
      statusCode
    );
  }

  /**
   * Produce an exception indicating an error, with a string constant message.
   *
   * @param statusCode The HTTP status code
   * @param errorCode  The error code
   * @param attributes The error attributes
   * @param message    The string message
   * @param cause      The cause
   *
   * @return An execution failure
   */

  public final CACommandExecutionFailure failWithCause(
    final Exception cause,
    final int statusCode,
    final CAErrorCode errorCode,
    final String message,
    final Map<String, String> attributes)
  {
    return new CACommandExecutionFailure(
      errorCode,
      message,
      cause,
      attributes,
      this.requestId,
      statusCode
    );
  }

  /**
   * Produce an exception indicating a database error.
   *
   * @param e The database exception
   *
   * @return An execution failure
   */

  public final CACommandExecutionFailure failDatabase(
    final CADatabaseException e)
  {
    return new CACommandExecutionFailure(
      e.errorCode(),
      e.getMessage(),
      e,
      e.attributes(),
      this.requestId,
      500
    );
  }

  /**
   * Produce an exception indicating a security policy error.
   *
   * @param e The security exception
   *
   * @return An execution failure
   */

  public CACommandExecutionFailure failSecurity(
    final CASecurityException e)
  {
    return new CACommandExecutionFailure(
      CAStandardErrorCodes.errorSecurityPolicyDenied(),
      e.getMessage(),
      e,
      e.attributes(),
      this.requestId,
      400
    );
  }

  /**
   * Produce an exception indicating a protocol error.
   *
   * @param e The exception
   *
   * @return An execution failure
   */

  public CACommandExecutionFailure failProtocol(
    final CAProtocolException e)
  {
    return new CACommandExecutionFailure(
      CAStandardErrorCodes.errorProtocol(),
      e.getMessage(),
      e,
      e.attributes(),
      this.requestId,
      400
    );
  }

  /**
   * Produce an exception indicating a validation error.
   *
   * @param e The exception
   *
   * @return An execution failure
   */

  public CACommandExecutionFailure failValidity(
    final CAValidityException e)
  {
    return new CACommandExecutionFailure(
      CAStandardErrorCodes.errorProtocol(),
      e.getMessage(),
      e,
      Map.of(),
      this.requestId,
      400
    );
  }
}
