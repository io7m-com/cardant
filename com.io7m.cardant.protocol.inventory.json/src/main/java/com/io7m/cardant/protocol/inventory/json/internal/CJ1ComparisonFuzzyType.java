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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "@type"
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = CJ1ComparisonFuzzyAnything.class, name = "Anything"),
  @JsonSubTypes.Type(value = CJ1ComparisonFuzzyIsEqualTo.class, name = "IsEqualTo"),
  @JsonSubTypes.Type(value = CJ1ComparisonFuzzyIsNotEqualTo.class, name = "IsNotEqualTo"),
  @JsonSubTypes.Type(value = CJ1ComparisonFuzzyIsSimilarTo.class, name = "IsSimilarTo"),
  @JsonSubTypes.Type(value = CJ1ComparisonFuzzyIsNotSimilarTo.class, name = "IsNotSimilarTo"),
})
public sealed interface CJ1ComparisonFuzzyType<T>
  extends CJ1ValueType
  permits CJ1ComparisonFuzzyAnything,
  CJ1ComparisonFuzzyIsEqualTo,
  CJ1ComparisonFuzzyIsNotEqualTo,
  CJ1ComparisonFuzzyIsNotSimilarTo,
  CJ1ComparisonFuzzyIsSimilarTo
{

}
