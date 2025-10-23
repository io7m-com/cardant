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

@JsonClassDescription("Parameters to search for files.")
public record CJ1FileSearchParameters(
  @JsonPropertyDescription("Include files with descriptions matching the given expression.")
  @JsonProperty(value = "matchDescription", required = true)
  CJ1ComparisonFuzzyType<String> description,
  @JsonPropertyDescription("Include files with media types matching the given expression.")
  @JsonProperty(value = "matchMediaType", required = true)
  CJ1ComparisonFuzzyType<String> mediaType,
  @JsonPropertyDescription("Include files with sizes matching the given expression.")
  @JsonProperty(value = "matchSizeRange", required = true)
  CJ1SizeRange sizeRange,
  @JsonPropertyDescription("The result ordering.")
  @JsonProperty(value = "orderBy", required = true)
  CJ1FileColumnOrdering ordering,
  @JsonPropertyDescription("The maximum number of results per page.")
  @JsonProperty(value = "pageSize", required = true)
  CJ1UnsignedLong pageSize)
  implements CJ1ValueType
{

}
