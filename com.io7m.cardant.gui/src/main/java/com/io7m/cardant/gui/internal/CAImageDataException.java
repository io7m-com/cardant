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

package com.io7m.cardant.gui.internal;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class CAImageDataException extends Exception
{
  private final Map<String, String> attributes;
  private final List<String> details;

  public CAImageDataException(
    final String message,
    final Map<String, String> inAttributes,
    final List<String> inDetails)
  {
    super(Objects.requireNonNull(message, "message"));

    this.attributes =
      Objects.requireNonNull(inAttributes, "attributes");
    this.details =
      Objects.requireNonNull(inDetails, "details");
  }

  public CAImageDataException(
    final String message,
    final Map<String, String> inAttributes,
    final List<String> inDetails,
    final Throwable cause)
  {
    super(
      Objects.requireNonNull(message, "message"),
      Objects.requireNonNull(cause, "cause"));

    this.attributes =
      Objects.requireNonNull(inAttributes, "attributes");
    this.details =
      Objects.requireNonNull(inDetails, "details");
  }

  public Map<String, String> attributes()
  {
    return this.attributes;
  }

  public List<String> details()
  {
    return this.details;
  }
}
