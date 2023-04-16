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

import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.cardant.error_codes.CAException;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * A failure to execute a command.
 */

public final class CACommandExecutionFailure extends CAException
{
  private final UUID requestId;
  private final int httpStatusCode;

  /**
   * @return The request ID
   */

  public UUID requestId()
  {
    return this.requestId;
  }

  /**
   * @return The HTTP status code
   */

  public int httpStatusCode()
  {
    return this.httpStatusCode;
  }

  /**
   * A failure to execute a command.
   *
   * @param inErrorCode      The error code
   * @param inMessage        The message
   * @param inCause          The cause
   * @param inAttributes     The error attributes
   * @param inRequestId      The request ID
   * @param inHttpStatusCode The HTTP status code
   */

  public CACommandExecutionFailure(
    final CAErrorCode inErrorCode,
    final String inMessage,
    final Throwable inCause,
    final Map<String, String> inAttributes,
    final UUID inRequestId,
    final int inHttpStatusCode)
  {
    super(inErrorCode, inMessage, inCause, inAttributes);

    this.requestId =
      Objects.requireNonNull(inRequestId, "requestId");
    this.httpStatusCode =
      inHttpStatusCode;
  }

  /**
   * A failure to execute a command.
   *
   * @param inErrorCode      The error code
   * @param inMessage        The message
   * @param inAttributes     The error attributes
   * @param inRequestId      The request ID
   * @param inHttpStatusCode The HTTP status code
   */

  public CACommandExecutionFailure(
    final CAErrorCode inErrorCode,
    final String inMessage,
    final Map<String, String> inAttributes,
    final UUID inRequestId,
    final int inHttpStatusCode)
  {
    super(inErrorCode, inMessage, inAttributes);

    this.requestId =
      Objects.requireNonNull(inRequestId, "requestId");
    this.httpStatusCode =
      inHttpStatusCode;
  }
}
