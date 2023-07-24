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

import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * Expressions that match metadata values.
 */

public sealed interface CAMetadataValueMatchType
{
  /**
   * Match anything.
   */

  enum AnyValue implements CAMetadataValueMatchType
  {
    /**
     * Match anything.
     */

    ANY_VALUE
  }

  /**
   * Match metadata of an integral type.
   */

  sealed interface IntegralMatchType
    extends CAMetadataValueMatchType
  {
    /**
     * Match metadata of an integral type with values within the given
     * inclusive range.
     *
     * @param lower The lower bound
     * @param upper The upper bound
     */

    record WithinRange(
      long lower,
      long upper)
      implements IntegralMatchType
    {

    }
  }

  /**
   * Match metadata of a real type.
   */

  sealed interface RealMatchType
    extends CAMetadataValueMatchType
  {
    /**
     * Match metadata of a real type with values within the given
     * inclusive range.
     *
     * @param lower The lower bound
     * @param upper The upper bound
     */

    record WithinRange(
      double lower,
      double upper)
      implements RealMatchType
    {

    }
  }

  /**
   * Match metadata of a time type.
   */

  sealed interface TimeMatchType
    extends CAMetadataValueMatchType
  {
    /**
     * Match metadata of a time type with values within the given
     * inclusive range.
     *
     * @param lower The lower bound
     * @param upper The upper bound
     */

    record WithinRange(
      OffsetDateTime lower,
      OffsetDateTime upper)
      implements TimeMatchType
    {
      /**
       * Match metadata of a time type with values within the given
       * inclusive range.
       */

      public WithinRange
      {
        Objects.requireNonNull(lower, "lower");
        Objects.requireNonNull(upper, "upper");
      }
    }
  }

  /**
   * Match metadata of a monetary type.
   */

  sealed interface MonetaryMatchType
    extends CAMetadataValueMatchType
  {
    /**
     * Match metadata of a monetary type with values with the given
     * currency.
     *
     * @param currency The currency value
     */

    record WithCurrency(
      CurrencyUnit currency)
      implements MonetaryMatchType
    {
      /**
       * Match metadata of a monetary type with values with the given
       * currency.
       */

      public WithCurrency
      {
        Objects.requireNonNull(currency, "currency");
      }
    }

    /**
     * Match metadata of a monetary type with values within the given
     * inclusive range.
     *
     * @param lower The lower bound
     * @param upper The upper bound
     */

    record WithinRange(
      BigDecimal lower,
      BigDecimal upper)
      implements MonetaryMatchType
    {
      /**
       * Match metadata of a monetary type with values within the given
       * inclusive range.
       */

      public WithinRange
      {
        Objects.requireNonNull(lower, "lower");
        Objects.requireNonNull(upper, "upper");
      }
    }
  }

  /**
   * Match metadata of a text type.
   */

  sealed interface TextMatchType
    extends CAMetadataValueMatchType
  {
    /**
     * Match metadata of a text type with the exact value.
     *
     * @param text The text value
     */

    record ExactTextValue(String text)
      implements TextMatchType
    {
      /**
       * Match metadata of a text type with the exact value.
       */

      public ExactTextValue
      {
        Objects.requireNonNull(text, "text");
      }
    }

    /**
     * Match metadata of a text type that matches the given search query.
     *
     * @param query The search query
     */

    record Search(String query)
      implements TextMatchType
    {
      /**
       * Match metadata of a text type that matches the given search query.
       */

      public Search
      {
        Objects.requireNonNull(query, "query");
      }
    }
  }
}
