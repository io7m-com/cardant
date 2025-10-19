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

import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAStockSearchParameters;

import java.util.UUID;
import java.util.stream.Collectors;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1IncludeDeletedX.INCLUDE_DELETED;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1LocationMatchX.LOCATION_MATCH;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1StockOccurenceKindX.STOCK_OCCURENCE_KIND;

public enum CJ1StockSearchParametersX
  implements CJ1SerialBijectionType<CJ1StockSearchParameters, CAStockSearchParameters>
{
  STOCK_SEARCH_PARAMETERS;

  @Override
  public CAStockSearchParameters toCore(
    final CJ1StockSearchParameters m)
  {
    return new CAStockSearchParameters(
      LOCATION_MATCH.toCore(m.locationMatch()),
      new CJ1ComparisonExactX<UUID>().toCore(m.itemMatch())
        .map(CAItemID::of),
      m.includeOccurrences()
        .stream().map(STOCK_OCCURENCE_KIND::toCore)
        .collect(Collectors.toSet()),
      INCLUDE_DELETED.toCore(m.includeDeleted()),
      m.pageSize()
    );
  }

  @Override
  public CJ1StockSearchParameters toCJ1(
    final CAStockSearchParameters m)
  {
    return new CJ1StockSearchParameters(
      LOCATION_MATCH.toCJ1(m.locationMatch()),
      new CJ1ComparisonExactX<UUID>().toCJ1(m.itemMatch().map(CAItemID::id)),
      m.includeOccurrences()
        .stream().map(STOCK_OCCURENCE_KIND::toCJ1)
        .collect(Collectors.toSet()),
      INCLUDE_DELETED.toCJ1(m.includeDeleted()),
      m.pageSize()
    );
  }
}
