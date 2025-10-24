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

import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.Set;
import java.util.UUID;

@JsonClassDescription("Parameters to search for stock.")
public record CJ1StockSearchParameters(
  @JsonPropertyDescription("Include stock with locations matching the given expression.")
  @JsonProperty(value = "matchLocation", required = true)
  CJ1LocationMatchType locationMatch,
  @JsonPropertyDescription("Include stock with items matching the given expression.")
  @JsonProperty(value = "matchItem", required = true)
  CJ1ComparisonExactType<UUID> itemMatch,
  @JsonPropertyDescription("Include stock of the given occurrence kinds.")
  @JsonProperty(value = "includeOccurrences", required = true)
  Set<CJ1StockOccurrenceKind> includeOccurrences,
  @JsonPropertyDescription("Include deleted stock.")
  @JsonProperty(value = "includeDeleted", required = true)
  CJ1IncludeDeleted includeDeleted,
  @JsonPropertyDescription("The maximum number of results per page.")
  @JsonProperty(value = "pageSize", required = true)
  CJ1UnsignedLong pageSize)
  implements CJ1ValueType
{

}
