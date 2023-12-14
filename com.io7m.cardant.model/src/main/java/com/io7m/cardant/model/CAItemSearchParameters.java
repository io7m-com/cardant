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

import java.util.Objects;

/**
 * The immutable parameters required to search items.
 *
 * @param locationMatch The item location search behaviour
 * @param nameMatch     The name match expression
 * @param typeMatch     The type match expression
 * @param metadataMatch The metadata match expression
 * @param ordering      The ordering specification
 * @param limit         The limit on the number of returned users
 */

public record CAItemSearchParameters(
  CAItemLocationMatchType locationMatch,
  CANameMatchType nameMatch,
  CATypeMatchType typeMatch,
  CAMetadataElementMatchType metadataMatch,
  CAItemColumnOrdering ordering,
  int limit)
{
  /**
   * The immutable parameters required to search items.
   *
   * @param locationMatch The location match expression
   * @param nameMatch     The name match expression
   * @param typeMatch     The type match expression
   * @param metadataMatch The metadata match expression
   * @param ordering      The ordering specification
   * @param limit         The limit on the number of returned users
   */

  public CAItemSearchParameters
  {
    Objects.requireNonNull(locationMatch, "locationMatch");
    Objects.requireNonNull(typeMatch, "typeMatch");
    Objects.requireNonNull(nameMatch, "nameMatch");
    Objects.requireNonNull(metadataMatch, "metadataMatch");
    Objects.requireNonNull(ordering, "ordering");
  }

  /**
   * @return The limit on the number of returned items
   */

  @Override
  public int limit()
  {
    return Math.max(1, this.limit);
  }

}
