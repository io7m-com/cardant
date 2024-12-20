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

import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType;
import com.io7m.cardant.model.comparisons.CAComparisonSetType;

import java.util.Objects;

/**
 * The immutable parameters required to search items.
 *
 * @param nameMatch        The name match expression
 * @param descriptionMatch The description match expression
 * @param typeMatch        The type match expression
 * @param metadataMatch    The metadata match expression
 * @param ordering         The ordering specification
 * @param includeDeleted   Whether to include deleted data
 * @param pageSize         The page size
 */

public record CAItemSearchParameters(
  CAComparisonFuzzyType<String> nameMatch,
  CAComparisonFuzzyType<String> descriptionMatch,
  CAComparisonSetType<CATypeRecordIdentifier> typeMatch,
  CAMetadataElementMatchType metadataMatch,
  CAIncludeDeleted includeDeleted,
  CAItemColumnOrdering ordering,
  long pageSize)
  implements CASearchParametersType
{
  /**
   * The immutable parameters required to search items.
   *
   * @param nameMatch        The name match expression
   * @param descriptionMatch The description match expression
   * @param typeMatch        The type match expression
   * @param metadataMatch    The metadata match expression
   * @param includeDeleted   Whether to include deleted data
   * @param ordering         The ordering specification
   * @param pageSize         The page size
   */

  public CAItemSearchParameters
  {
    Objects.requireNonNull(typeMatch, "typeMatch");
    Objects.requireNonNull(nameMatch, "nameMatch");
    Objects.requireNonNull(descriptionMatch, "descriptionMatch");
    Objects.requireNonNull(metadataMatch, "metadataMatch");
    Objects.requireNonNull(includeDeleted, "includeDeleted");
    Objects.requireNonNull(ordering, "ordering");
    pageSize = CAPageSizes.clampPageSize(pageSize);
  }
}
