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
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * A metadata value.
 */

public sealed interface CAMetadataType
{
  /**
   * @return The metadata name
   */

  RDottedName name();

  /**
   * @return The metadata value as a string
   */

  String valueString();

  /**
   * An integral value.
   *
   * @param name  The metadata name
   * @param value The value
   */

  record Integral(
    RDottedName name,
    long value)
    implements CAMetadataType
  {
    /**
     * An integral value.
     */

    public Integral
    {
      Objects.requireNonNull(name, "name");
    }

    @Override
    public String valueString()
    {
      return Long.toUnsignedString(this.value);
    }
  }

  /**
   * A text value.
   *
   * @param name  The metadata name
   * @param value The metadata value
   */

  record Text(
    RDottedName name,
    String value)
    implements CAMetadataType
  {
    /**
     * A text value.
     */

    public Text
    {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(value, "value");
    }

    @Override
    public String valueString()
    {
      return this.value;
    }
  }

  /**
   * A time value.
   *
   * @param name  The metadata name
   * @param value The metadata value
   */

  record Time(
    RDottedName name,
    OffsetDateTime value)
    implements CAMetadataType
  {
    /**
     * A time value.
     */

    public Time
    {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(value, "value");
    }

    @Override
    public String valueString()
    {
      return this.value.toString();
    }
  }

  /**
   * A monetary value.
   *
   * @param name     The metadata name
   * @param value    The value
   * @param currency The currency unit
   */

  record Monetary(
    RDottedName name,
    BigDecimal value,
    CurrencyUnit currency)
    implements CAMetadataType
  {
    /**
     * A monetary value.
     */

    public Monetary
    {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(value, "value");
      Objects.requireNonNull(currency, "currency");
    }

    @Override
    public String valueString()
    {
      return this.value.toString();
    }
  }

  /**
   * A real value.
   *
   * @param name  The metadata name
   * @param value The value
   */

  record Real(
    RDottedName name,
    double value)
    implements CAMetadataType
  {
    /**
     * An real value.
     */

    public Real
    {
      Objects.requireNonNull(name, "name");
    }

    @Override
    public String valueString()
    {
      return Double.toString(this.value);
    }
  }
}
