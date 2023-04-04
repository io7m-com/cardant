/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.client.transfer.vanilla.internal;

import java.util.Map;
import java.util.Objects;

/**
 * A transfer exception.
 */

public final class CATransferException extends Exception
{
  private final Map<String, String> attributes;

  /**
   * @return The error attributes
   */

  public Map<String, String> attributes()
  {
    return this.attributes;
  }

  /**
   * A transfer exception.
   *
   * @param inMessage    The message
   * @param inAttributes The attributes
   */

  public CATransferException(
    final String inMessage,
    final Map<String, String> inAttributes)
  {
    super(Objects.requireNonNull(inMessage, "message"));
    this.attributes =
      Objects.requireNonNull(inAttributes, "attributes");
  }

  /**
   * A transfer exception.
   *
   * @param message      The message
   * @param cause        The cause
   * @param inAttributes The attributes
   */

  public CATransferException(
    final String message,
    final Throwable cause,
    final Map<String, String> inAttributes)
  {
    super(
      Objects.requireNonNull(message, "message"),
      Objects.requireNonNull(cause, "cause"));
    this.attributes =
      Objects.requireNonNull(inAttributes, "attributes");
  }
}
