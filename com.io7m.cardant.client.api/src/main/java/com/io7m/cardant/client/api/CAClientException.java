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

import java.util.Map;

/**
 * The type of exceptions raised by the client.
 */

public final class CAClientException extends CAException
{
  /**
   * Create an exception.
   *
   * @param inMessage    The message
   * @param inCause      The cause
   * @param inErrorCode  The error code
   * @param inAttributes The error attributes
   */

  public CAClientException(
    final CAErrorCode inErrorCode,
    final String inMessage,
    final Throwable inCause,
    final Map<String, String> inAttributes)
  {
    super(inErrorCode, inMessage, inCause, inAttributes);
  }

  /**
   * Create an exception.
   *
   * @param inMessage    The message
   * @param inErrorCode  The error code
   * @param inAttributes The error attributes
   */

  public CAClientException(
    final CAErrorCode inErrorCode,
    final String inMessage,
    final Map<String, String> inAttributes)
  {
    super(inErrorCode, inMessage, inAttributes);
  }
}
