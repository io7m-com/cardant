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
 * A fully qualified scalar type identifier.
 *
 * @param packageName The package name
 * @param typeName    The type name
 */

public record CATypeScalarIdentifier(
  RDottedName packageName,
  RDottedName typeName)
  implements Comparable<CATypeScalarIdentifier>
{
  /**
   * A fully qualified scalar type identifier.
   *
   * @param packageName The package name
   * @param typeName    The type name
   */

  public CATypeScalarIdentifier
  {
    Objects.requireNonNull(packageName, "packageName");
    Objects.requireNonNull(typeName, "typeName");

    Preconditions.checkPreconditionV(
      typeName.segments().size() == 1,
      "Type names must be unqualified."
    );
  }

  @Override
  public String toString()
  {
    return "%s:%s".formatted(this.packageName, this.typeName);
  }

  @Override
  public int compareTo(
    final CATypeScalarIdentifier other)
  {
    return Comparator.comparing(CATypeScalarIdentifier::packageName)
      .thenComparing(CATypeScalarIdentifier::typeName)
      .compare(this, other);
  }

  /**
   * Parse a type scalar identifier.
   *
   * @param text The text
   *
   * @return An identifier
   */

  public static CATypeScalarIdentifier of(
    final String text)
  {
    Objects.requireNonNull(text, "text");

    final var segments = List.of(text.split(":"));
    if (segments.size() == 2) {
      final var packageName =
        segments.get(0);
      final var typeName =
        segments.get(1);

      return new CATypeScalarIdentifier(
        new RDottedName(packageName),
        new RDottedName(typeName)
      );
    }

    throw new IllegalArgumentException(
      "Unparseable scalar type identifier: %s".formatted(text)
    );
  }
}
