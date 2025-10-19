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

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1ComparisonFuzzyType.Anything;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1ComparisonFuzzyType.IsEqualTo;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1ComparisonFuzzyType.IsNotEqualTo;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1ComparisonFuzzyType.IsNotSimilarTo;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1ComparisonFuzzyType.IsSimilarTo;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "@type"
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = Anything.class, name = "Anything"),
  @JsonSubTypes.Type(value = IsEqualTo.class, name = "IsEqualTo"),
  @JsonSubTypes.Type(value = IsNotEqualTo.class, name = "IsNotEqualTo"),
  @JsonSubTypes.Type(value = IsSimilarTo.class, name = "IsSimilarTo"),
  @JsonSubTypes.Type(value = IsNotSimilarTo.class, name = "IsNotSimilarTo"),
})
public sealed interface CJ1ComparisonFuzzyType<T>
  extends CJ1ValueType
{
  @JsonTypeName("Anything")
  record Anything<T>()
    implements CJ1ComparisonFuzzyType<T>
  {

  }

  @JsonTypeName("IsEqualTo")
  record IsEqualTo<T>(
    @JsonProperty(value = "value", required = true) T value)
    implements CJ1ComparisonFuzzyType<T>
  {

  }

  @JsonTypeName("IsNotEqualTo")
  record IsNotEqualTo<T>(
    @JsonProperty(value = "value", required = true) T value)
    implements CJ1ComparisonFuzzyType<T>
  {

  }

  @JsonTypeName("IsSimilarTo")
  record IsSimilarTo<T>(
    @JsonProperty(value = "value", required = true) T value)
    implements CJ1ComparisonFuzzyType<T>
  {

  }

  @JsonTypeName("IsNotSimilarTo")
  record IsNotSimilarTo<T>(
    @JsonProperty(value = "value", required = true) T value)
    implements CJ1ComparisonFuzzyType<T>
  {

  }
}
