/*
 * Copyright © 2022 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.client.api;

import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.protocol.inventory.CAIResponseError;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * The type of exceptions raised by the client.
 */

public final class CAClientException extends CAException
{
  private final Optional<UUID> requestId;

  /**
   * Construct an exception.
   *
   * @param message             The message
   * @param inErrorCode         The error code
   * @param inAttributes        The error attributes
   * @param inRemediatingAction The remediating action, if any
   * @param inRequestId         The request ID
   */

  public CAClientException(
    final String message,
    final CAErrorCode inErrorCode,
    final Map<String, String> inAttributes,
    final Optional<String> inRemediatingAction,
    final Optional<UUID> inRequestId)
  {
    super(message, inErrorCode, inAttributes, inRemediatingAction);
    this.requestId = Objects.requireNonNull(inRequestId, "requestId");
  }

  /**
   * Construct an exception.
   *
   * @param message             The message
   * @param cause               The cause
   * @param inErrorCode         The error code
   * @param inAttributes        The error attributes
   * @param inRemediatingAction The remediating action, if any
   * @param inRequestId         The request ID
   */

  public CAClientException(
    final String message,
    final Throwable cause,
    final CAErrorCode inErrorCode,
    final Map<String, String> inAttributes,
    final Optional<String> inRemediatingAction,
    final Optional<UUID> inRequestId)
  {
    super(message, cause, inErrorCode, inAttributes, inRemediatingAction);
    this.requestId = Objects.requireNonNull(inRequestId, "requestId");
  }

  /**
   * @return The ID associated with the request, if the server returned one
   */

  public Optional<UUID> requestId()
  {
    return this.requestId;
  }

  /**
   * Transform an error response to an exception.
   *
   * @param error The error
   *
   * @return The exception
   */

  public static CAClientException ofError(
    final CAIResponseError error)
  {
    return new CAClientException(
      error.message(),
      error.errorCode(),
      error.attributes(),
      error.remediatingAction(),
      Optional.of(error.requestId())
    );
  }
}
