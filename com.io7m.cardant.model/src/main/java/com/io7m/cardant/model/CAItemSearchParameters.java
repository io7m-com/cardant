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

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The immutable parameters required to search items.
 *
 * @param locationMatch The item location search behaviour
 * @param nameMatch     The name match expression
 * @param typeMatch     The type match expression
 * @param metadataMatch The metadata match expression
 * @param ordering      The ordering specification
 * @param limit         The limit on the number of returned users
 */

public record CAItemSearchParameters(
  CALocationMatchType locationMatch,
  CANameMatchType nameMatch,
  CATypeMatchType typeMatch,
  CAMetadataMatchType metadataMatch,
  CAItemColumnOrdering ordering,
  int limit)
{
  /**
   * The immutable parameters required to search items.
   *
   * @param locationMatch The location match expression
   * @param nameMatch     The name match expression
   * @param typeMatch     The type match expression
   * @param metadataMatch The metadata match expression
   * @param ordering      The ordering specification
   * @param limit         The limit on the number of returned users
   */

  public CAItemSearchParameters
  {
    Objects.requireNonNull(locationMatch, "locationMatch");
    Objects.requireNonNull(typeMatch, "typeMatch");
    Objects.requireNonNull(nameMatch, "nameMatch");
    Objects.requireNonNull(metadataMatch, "metadataMatch");
    Objects.requireNonNull(ordering, "ordering");
  }

  /**
   * @return The limit on the number of returned items
   */

  @Override
  public int limit()
  {
    return Math.max(1, this.limit);
  }

  /**
   * The expression used to match against item names.
   */

  public sealed interface CANameMatchType
  {
    /**
     * Match any item name.
     */

    enum CANameMatchAny implements CANameMatchType
    {
      /**
       * Match any item name.
       */

      ANY
    }

    /**
     * Match items with the exact given name.
     *
     * @param text The name
     */

    record CANameMatchExact(
      String text)
      implements CANameMatchType
    {
      /**
       * Match items with the exact given name.
       */

      public CANameMatchExact
      {
        Objects.requireNonNull(text, "text");
        text = text.trim();
      }
    }

    /**
     * Match item names against the given search query.
     *
     * @param query The query
     */

    record CANameMatchSearch(
      String query)
      implements CANameMatchType
    {
      /**
       * Match item names against the given search query.
       */

      public CANameMatchSearch
      {
        Objects.requireNonNull(query, "query");
      }
    }
  }

  /**
   * The expression used to match against item types.
   */

  public sealed interface CATypeMatchType
  {
    /**
     * Match items with any type.
     */

    enum CATypeMatchAny
      implements CATypeMatchType
    {
      /**
       * Match items with any type.
       */

      ANY
    }

    /**
     * Match items that have any of the given types.
     *
     * @param types The types
     */

    record CATypeMatchAnyOf(
      Set<RDottedName> types)
      implements CATypeMatchType
    {
      /**
       * Match items that have any of the given types.
       */

      public CATypeMatchAnyOf
      {
        Objects.requireNonNull(types, "types");
      }
    }

    /**
     * Match items that have all the given types.
     *
     * @param types The types
     */

    record CATypeMatchAllOf(
      Set<RDottedName> types)
      implements CATypeMatchType
    {
      /**
       * Match items that have all the given types.
       */

      public CATypeMatchAllOf
      {
        Objects.requireNonNull(types, "types");
      }
    }
  }

  /**
   * The expression used to match against item metadata.
   */

  public sealed interface CAMetadataMatchType
  {
    /**
     * Match items with any metadata.
     */

    enum CAMetadataMatchAny
      implements CAMetadataMatchType
    {
      /**
       * Match items with any metadata.
       */

      ANY
    }

    /**
     * Match items that have metadata that matches the given expressions.
     *
     * @param values The expressions
     */

    record CAMetadataRequire(
      Map<RDottedName, CAMetadataValueMatchType> values)
      implements CAMetadataMatchType
    {

    }
  }

  /**
   * The expression used to match against item metadata values.
   */

  public sealed interface CAMetadataValueMatchType
  {
    /**
     * Match any metadata value.
     */

    enum CAMetadataValueMatchAny
      implements CAMetadataValueMatchType
    {
      /**
       * Match any metadata value.
       */

      ANY
    }

    /**
     * Match metadata values that have exactly the given value.
     *
     * @param text The value
     */

    record CAMetadataValueMatchExact(
      String text)
      implements CAMetadataValueMatchType
    {
      /**
       * Match metadata values that have exactly the given value.
       */

      public CAMetadataValueMatchExact
      {
        Objects.requireNonNull(text, "text");
        text = text.trim();
      }
    }
  }
}
