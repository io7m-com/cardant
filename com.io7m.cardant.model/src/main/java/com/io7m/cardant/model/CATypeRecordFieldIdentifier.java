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

import com.io7m.jaffirm.core.Preconditions;
import com.io7m.lanark.core.RDottedName;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * A fully qualified record field identifier.
 *
 * @param typeName  The type name
 * @param fieldName The field name
 */

public record CATypeRecordFieldIdentifier(
  CATypeRecordIdentifier typeName,
  RDottedName fieldName)
  implements Comparable<CATypeRecordFieldIdentifier>
{
  /**
   * A fully qualified record field identifier.
   *
   * @param typeName  The type name
   * @param fieldName The field name
   */

  public CATypeRecordFieldIdentifier
  {
    Objects.requireNonNull(fieldName, "fieldName");
    Objects.requireNonNull(typeName, "typeName");

    Preconditions.checkPreconditionV(
      fieldName.segments().size() == 1,
      "Field names must be unqualified."
    );
  }

  @Override
  public String toString()
  {
    return "%s.%s".formatted(this.typeName, this.fieldName);
  }

  @Override
  public int compareTo(
    final CATypeRecordFieldIdentifier other)
  {
    return Comparator.comparing(CATypeRecordFieldIdentifier::typeName)
      .thenComparing(CATypeRecordFieldIdentifier::fieldName)
      .compare(this, other);
  }

  /**
   * Parse a type record field identifier.
   *
   * @param text The text
   *
   * @return An identifier
   */

  public static CATypeRecordFieldIdentifier of(
    final String text)
  {
    Objects.requireNonNull(text, "text");

    final var segments = List.of(text.split(":"));
    if (segments.size() == 2) {
      final var packageName =
        segments.get(0);
      final var expression =
        segments.get(1);
      final var expressionSegments =
        List.of(expression.split("\\."));

      if (expressionSegments.size() == 2) {
        return new CATypeRecordFieldIdentifier(
          new CATypeRecordIdentifier(
            new RDottedName(packageName),
            new RDottedName(expressionSegments.get(0))
          ),
          new RDottedName(expressionSegments.get(1))
        );
      }
    }

    throw new IllegalArgumentException(
      "Unparseable record field identifier: %s".formatted(text)
    );
  }
}
