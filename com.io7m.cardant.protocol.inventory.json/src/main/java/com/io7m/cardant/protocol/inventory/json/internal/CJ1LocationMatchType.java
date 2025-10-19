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

import java.util.UUID;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1LocationMatchType.All;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1LocationMatchType.Exact;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1LocationMatchType.WithDescendants;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "@type"
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = Exact.class, name = "Exact"),
  @JsonSubTypes.Type(value = WithDescendants.class, name = "WithDescendants"),
  @JsonSubTypes.Type(value = All.class, name = "All"),
})
public sealed interface CJ1LocationMatchType
{
  @JsonTypeName("Exact")
  record Exact(
    @JsonProperty(value = "location", required = true)
    UUID location)
    implements CJ1LocationMatchType
  {

  }

  @JsonTypeName("WithDescendants")
  record WithDescendants(
    @JsonProperty(value = "location", required = true)
    UUID location)
    implements CJ1LocationMatchType
  {

  }

  @JsonTypeName("All")
  record All()
    implements CJ1LocationMatchType
  {

  }
}
