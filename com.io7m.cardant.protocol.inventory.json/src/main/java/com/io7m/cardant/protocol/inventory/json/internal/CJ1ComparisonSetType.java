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
  @JsonSubTypes.Type(value = CJ1ComparisonSetAnything.class, name = "Anything"),
  @JsonSubTypes.Type(value = CJ1ComparisonSetIsEqualTo.class, name = "IsEqualTo"),
  @JsonSubTypes.Type(value = CJ1ComparisonSetIsNotEqualTo.class, name = "IsNotEqualTo"),
  @JsonSubTypes.Type(value = CJ1ComparisonSetIsOverlapping.class, name = "IsOverlapping"),
  @JsonSubTypes.Type(value = CJ1ComparisonSetIsSubsetOf.class, name = "IsSubsetOf"),
  @JsonSubTypes.Type(value = CJ1ComparisonSetIsSupersetOf.class, name = "IsSupersetOf"),
})
public sealed interface CJ1ComparisonSetType<T>
  extends CJ1ValueType
  permits CJ1ComparisonSetAnything,
  CJ1ComparisonSetIsEqualTo,
  CJ1ComparisonSetIsNotEqualTo,
  CJ1ComparisonSetIsOverlapping,
  CJ1ComparisonSetIsSubsetOf,
  CJ1ComparisonSetIsSupersetOf
{

}
