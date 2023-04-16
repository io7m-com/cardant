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


package com.io7m.cardant.protocol.api;

import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.cardant.error_codes.CAException;

import java.util.SortedMap;

/**
 * An exception encountered whilst handling a protocol.
 */

public final class CAProtocolException extends CAException
{
  /**
   * Construct an exception.
   *
   * @param inErrorCode The error code
   * @param inMessage   The message
   */

  public CAProtocolException(
    final CAErrorCode inErrorCode,
    final String inMessage)
  {
    super(inErrorCode, inMessage);
  }

  /**
   * Construct an exception.
   *
   * @param inErrorCode  The error code
   * @param inMessage    The message
   * @param inCause      The cause
   * @param inAttributes The error attributes
   */

  public CAProtocolException(
    final CAErrorCode inErrorCode,
    final String inMessage,
    final Throwable inCause,
    final SortedMap<String, String> inAttributes)
  {
    super(inErrorCode, inMessage, inCause, inAttributes);
  }

  /**
   * Construct an exception.
   *
   * @param inErrorCode The error code
   * @param inCause     The cause
   */

  public CAProtocolException(
    final CAErrorCode inErrorCode,
    final Throwable inCause)
  {
    super(inErrorCode, inCause);
  }
}
