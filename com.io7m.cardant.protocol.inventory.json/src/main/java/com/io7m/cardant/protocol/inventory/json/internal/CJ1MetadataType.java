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

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "@type"
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = CJ1MetadataType.Integral.class, name = "Integral"),
  @JsonSubTypes.Type(value = CJ1MetadataType.Text.class, name = "Text"),
  @JsonSubTypes.Type(value = CJ1MetadataType.Time.class, name = "Time"),
  @JsonSubTypes.Type(value = CJ1MetadataType.Monetary.class, name = "Monetary"),
  @JsonSubTypes.Type(value = CJ1MetadataType.Real.class, name = "Real"),
})
public sealed interface CJ1MetadataType
  extends CJ1ValueType
{
  @JsonTypeName("Integral")
  record Integral(
    @JsonProperty(value = "name", required = true)
    CJ1TypeRecordFieldIdentifier name,
    @JsonProperty(value = "value", required = true)
    long value)
    implements CJ1MetadataType
  {

  }

  @JsonTypeName("Text")
  record Text(
    @JsonProperty(value = "name", required = true)
    CJ1TypeRecordFieldIdentifier name,
    @JsonProperty(value = "value", required = true)
    String value)
    implements CJ1MetadataType
  {

  }

  @JsonTypeName("Time")
  record Time(
    @JsonProperty(value = "name", required = true)
    CJ1TypeRecordFieldIdentifier name,
    @JsonProperty(value = "value", required = true)
    OffsetDateTime value)
    implements CJ1MetadataType
  {

  }

  @JsonTypeName("Monetary")
  record Monetary(
    @JsonProperty(value = "name", required = true)
    CJ1TypeRecordFieldIdentifier name,
    @JsonProperty(value = "value", required = true)
    BigDecimal value,
    @JsonProperty(value = "currency", required = true)
    CurrencyUnit currency)
    implements CJ1MetadataType
  {

  }

  @JsonTypeName("Real")
  record Real(
    @JsonProperty(value = "name", required = true)
    CJ1TypeRecordFieldIdentifier name,
    @JsonProperty(value = "value", required = true)
    double value)
    implements CJ1MetadataType
  {

  }
}
