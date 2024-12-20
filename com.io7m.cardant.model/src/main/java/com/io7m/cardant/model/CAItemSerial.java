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


package com.io7m.cardant.model;

import com.io7m.lanark.core.RDottedName;

import java.util.Comparator;
import java.util.Objects;

/**
 * A serial number.
 *
 * @param type  The type
 * @param value The value
 */

public record CAItemSerial(
  RDottedName type,
  String value)
  implements Comparable<CAItemSerial>
{
  /**
   * A serial number.
   *
   * @param type  The type
   * @param value The value
   */

  public CAItemSerial
  {
    Objects.requireNonNull(type, "type");
    Objects.requireNonNull(value, "value");

    if (value.contains(":")) {
      throw new IllegalArgumentException(
        "Serial number values cannot contain ':'"
      );
    }
  }

  @Override
  public String toString()
  {
    return String.format("%s:%s", this.type, this.value);
  }

  /**
   * Parse a serial number of the form "&lt;type>:&lt;value>".
   *
   * @param text The text
   *
   * @return The parsed serial number
   *
   * @throws IllegalArgumentException On parse errors
   */

  public static CAItemSerial parse(
    final String text)
    throws IllegalArgumentException
  {
    final var segments = text.split(":", 3);
    if (segments.length != 2) {
      throw new IllegalArgumentException(
        "Unparseable serial number: %s".formatted(text)
      );
    }

    return new CAItemSerial(
      new RDottedName(segments[0]),
      segments[1]
    );
  }

  @Override
  public int compareTo(
    final CAItemSerial other)
  {
    return Comparator.comparing(CAItemSerial::type)
      .thenComparing(CAItemSerial::value)
      .compare(this, other);
  }
}
