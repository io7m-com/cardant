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

package com.io7m.cardant.database.api;

import com.io7m.cardant.model.CAStockRepositType;
import com.io7m.cardant.model.CAStockSearchParameters;

/**
 * Model database queries (Stock).
 */

public sealed interface CADatabaseQueriesStockType
  extends CADatabaseQueriesType
{
  /**
   * Reposit items.
   */

  non-sealed interface StockRepositType
    extends CADatabaseQueryType<CAStockRepositType, CADatabaseUnit>,
    CADatabaseQueriesStockType
  {

  }

  /**
   * Search for items.
   */

  non-sealed interface StockSearchType
    extends CADatabaseQueryType<CAStockSearchParameters, CADatabaseStockSearchType>,
    CADatabaseQueriesStockType
  {

  }

  /**
   * Count items.
   */

  non-sealed interface StockCountType
    extends CADatabaseQueryType<CAStockSearchParameters, Long>,
    CADatabaseQueriesStockType
  {

  }
}
