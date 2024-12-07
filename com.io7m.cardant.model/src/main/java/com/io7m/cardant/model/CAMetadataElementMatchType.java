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

import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.lanark.core.RDottedName;

import java.util.Objects;

import static com.io7m.cardant.model.CAMetadataValueMatchType.AnyValue.ANY_VALUE;

/**
 * The expression used to match against the set of metadata on an object.
 */

public sealed interface CAMetadataElementMatchType
{
  /**
   * An expression that matches anything.
   */

  CAMetadataElementMatchType ANYTHING =
    new Specific(
      new CAComparisonExactType.Anything<>(),
      new CAComparisonExactType.Anything<>(),
      new CAComparisonExactType.Anything<>(),
      ANY_VALUE
    );

  /**
   * The conjunction of {@code e0} and {@code e1}. The resulting set of
   * metadata elements is the intersection of those matched by both
   * {@code e0} and {@code e1}.
   *
   * @param e0 The left match expression
   * @param e1 The right match expression
   */

  record And(
    CAMetadataElementMatchType e0,
    CAMetadataElementMatchType e1)
    implements CAMetadataElementMatchType
  {
    /**
     * The conjunction of {@code e0} and {@code e1}. The resulting set of
     * metadata elements is the intersection of those matched by both
     * {@code e0} and {@code e1}.
     */

    public And
    {
      Objects.requireNonNull(e0, "e0");
      Objects.requireNonNull(e1, "e1");
    }
  }

  /**
   * The disjunction of {@code e0} and {@code e1}. The resulting set of
   * metadata elements is the union of those matched by {@code e0} or {@code e1}.
   *
   * @param e0 The left match expression
   * @param e1 The right match expression
   */

  record Or(
    CAMetadataElementMatchType e0,
    CAMetadataElementMatchType e1)
    implements CAMetadataElementMatchType
  {
    /**
     * The disjunction of {@code e0} and {@code e1}. The resulting set of
     * metadata elements is the union of those matched by {@code e0} or {@code e1}.
     */

    public Or
    {
      Objects.requireNonNull(e0, "e0");
      Objects.requireNonNull(e1, "e1");
    }
  }

  /**
   * Match a specific metadata field.
   *
   * @param packageName The metadata type package name
   * @param typeName    The metadata type record name
   * @param fieldName   The metadata type record field name
   * @param value       The value
   */

  record Specific(
    CAComparisonExactType<RDottedName> packageName,
    CAComparisonExactType<String> typeName,
    CAComparisonExactType<String> fieldName,
    CAMetadataValueMatchType value)
    implements CAMetadataElementMatchType
  {
    /**
     * Match a specific metadata element.
     */

    public Specific
    {
      Objects.requireNonNull(packageName, "packageName");
      Objects.requireNonNull(typeName, "typeName");
      Objects.requireNonNull(fieldName, "fieldName");
      Objects.requireNonNull(value, "value");
    }
  }
}
