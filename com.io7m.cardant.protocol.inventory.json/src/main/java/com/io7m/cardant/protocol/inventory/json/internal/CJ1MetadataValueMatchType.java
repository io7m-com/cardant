/*
 * Copyright © 2025 Mark Raynsford <code@io7m.com> https://www.io7m.com
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


package com.io7m.cardant.protocol.inventory.json.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataValueMatchType.AnyValue;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataValueMatchType.IntegralMatchType;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataValueMatchType.MonetaryMatchType;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataValueMatchType.RealMatchType;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataValueMatchType.TextMatchType;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataValueMatchType.TimeMatchType;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "@type"
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = AnyValue.class, name = "AnyValue"),
  @JsonSubTypes.Type(value = IntegralMatchType.WithinRange.class, name = "IntegralWithinRange"),
  @JsonSubTypes.Type(value = RealMatchType.WithinRange.class, name = "RealWithinRange"),
  @JsonSubTypes.Type(value = TimeMatchType.WithinRange.class, name = "TimeWithinRange"),
  @JsonSubTypes.Type(value = MonetaryMatchType.WithCurrency.class, name = "MonetaryWithCurrency"),
  @JsonSubTypes.Type(value = MonetaryMatchType.WithinRange.class, name = "MonetaryWithinRange"),
  @JsonSubTypes.Type(value = TextMatchType.ExactTextValue.class, name = "TextExact"),
  @JsonSubTypes.Type(value = TextMatchType.Search.class, name = "TextSearch"),
})
public sealed interface CJ1MetadataValueMatchType
  extends CJ1ValueType
{
  @JsonTypeName("AnyValue")
  enum AnyValue implements CJ1MetadataValueMatchType
  {
    ANY_VALUE
  }

  sealed interface IntegralMatchType
    extends CJ1MetadataValueMatchType
  {
    @JsonTypeName("IntegralWithinRange")
    record WithinRange(
      @JsonProperty(value = "lower", required = true)
      long lower,
      @JsonProperty(value = "upper", required = true)
      long upper)
      implements IntegralMatchType
    {

    }
  }

  sealed interface RealMatchType
    extends CJ1MetadataValueMatchType
  {
    @JsonTypeName("RealWithinRange")
    record WithinRange(
      @JsonProperty(value = "lower", required = true)
      double lower,
      @JsonProperty(value = "upper", required = true)
      double upper)
      implements RealMatchType
    {

    }
  }

  sealed interface TimeMatchType
    extends CJ1MetadataValueMatchType
  {
    @JsonTypeName("TimeWithinRange")
    record WithinRange(
      @JsonProperty(value = "lower", required = true)
      OffsetDateTime lower,
      @JsonProperty(value = "upper", required = true)
      OffsetDateTime upper)
      implements TimeMatchType
    {

    }
  }

  sealed interface MonetaryMatchType
    extends CJ1MetadataValueMatchType
  {
    @JsonTypeName("MonetaryWithCurrency")
    record WithCurrency(
      @JsonProperty(value = "currency", required = true)
      CurrencyUnit currency)
      implements MonetaryMatchType
    {

    }

    @JsonTypeName("MonetaryWithinRange")
    record WithinRange(
      @JsonProperty(value = "lower", required = true)
      BigDecimal lower,
      @JsonProperty(value = "upper", required = true)
      BigDecimal upper)
      implements MonetaryMatchType
    {

    }
  }

  sealed interface TextMatchType
    extends CJ1MetadataValueMatchType
  {
    @JsonTypeName("TextExact")
    record ExactTextValue(
      @JsonProperty(value = "text", required = true)
      String text)
      implements TextMatchType
    {

    }

    @JsonTypeName("TextSearch")
    record Search(
      @JsonProperty(value = "query", required = true)
      String query)
      implements TextMatchType
    {

    }
  }
}
