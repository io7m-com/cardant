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

package com.io7m.cardant.error_codes;

import java.util.Collections;
import java.util.Objects;
import java.util.Map;
import java.util.Optional;

/**
 * The base type of exceptions.
 */

public class CAException extends Exception
  implements CAErrorStructuredType
{
  private final CAErrorCode errorCode;
  private final Map<String, String> attributes;

  /**
   * Construct an exception.
   *
   * @param inErrorCode The error code
   * @param message     The message
   */

  public CAException(
    final CAErrorCode inErrorCode,
    final String message)
  {
    super(message);
    this.errorCode =
      Objects.requireNonNull(inErrorCode, "errorCode");
    this.attributes =
      Collections.emptyMap();
  }

  /**
   * Construct an exception.
   *
   * @param inErrorCode  The error code
   * @param inMessage    The message
   * @param inCause      The cause
   * @param inAttributes The error attributes
   */

  public CAException(
    final CAErrorCode inErrorCode,
    final String inMessage,
    final Throwable inCause,
    final Map<String, String> inAttributes)
  {
    super(
      Objects.requireNonNull(inMessage, "inMessage"),
      Objects.requireNonNull(inCause, "inCause")
    );

    this.errorCode =
      Objects.requireNonNull(inErrorCode, "errorCode");
    this.attributes =
      Objects.requireNonNull(inAttributes, "attributes");
  }

  /**
   * Construct an exception.
   *
   * @param inErrorCode  The error code
   * @param inMessage    The message
   * @param inAttributes The error attributes
   */

  public CAException(
    final CAErrorCode inErrorCode,
    final String inMessage,
    final Map<String, String> inAttributes)
  {
    super(
      Objects.requireNonNull(inMessage, "inMessage")
    );

    this.errorCode =
      Objects.requireNonNull(inErrorCode, "errorCode");
    this.attributes =
      Objects.requireNonNull(inAttributes, "attributes");
  }

  /**
   * Construct an exception.
   *
   * @param inErrorCode The error code
   * @param cause       The cause
   */

  public CAException(
    final CAErrorCode inErrorCode,
    final Throwable cause)
  {
    super(cause);
    this.errorCode =
      Objects.requireNonNull(inErrorCode, "errorCode");
    this.attributes =
      Collections.emptyMap();
  }

  @Override
  public final String summary()
  {
    return this.getMessage();
  }

  /**
   * @return The error code
   */

  @Override
  public final CAErrorCode errorCode()
  {
    return this.errorCode;
  }

  @Override
  public final Map<String, String> attributes()
  {
    return this.attributes;
  }

  @Override
  public final Optional<Throwable> exception()
  {
    return Optional.of(this);
  }
}
