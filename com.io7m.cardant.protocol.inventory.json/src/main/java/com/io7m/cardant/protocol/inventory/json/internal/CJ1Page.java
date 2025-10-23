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

import java.util.List;

@JsonClassDescription("A page of results.")
public record CJ1Page<T>(
  @JsonPropertyDescription("The results.")
  @JsonProperty(value = "items", required = true)
  List<T> items,
  @JsonPropertyDescription("The page number.")
  @JsonProperty(value = "pageIndex", required = true)
  CJ1UnsignedInt pageIndex,
  @JsonPropertyDescription("The total number of pages.")
  @JsonProperty(value = "pageCount", required = true)
  CJ1UnsignedInt pageCount,
  @JsonPropertyDescription("The offset of the first result in the page.")
  @JsonProperty(value = "pageFirstOffset", required = true)
  CJ1UnsignedLong pageFirstOffset)
  implements CJ1ValueType
{

}
