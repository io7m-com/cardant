/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
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


package com.io7m.cardant.database.postgres.internal;

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseQueriesStockType.StockCountType;
import com.io7m.cardant.model.CAStockSearchParameters;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.Objects;

import static com.io7m.cardant.database.postgres.internal.CADBQStockSearch.createDeletedCondition;
import static com.io7m.cardant.database.postgres.internal.CADBQStockSearch.createItemMatchCondition;
import static com.io7m.cardant.database.postgres.internal.CADBQStockSearch.createKindCondition;
import static com.io7m.cardant.database.postgres.internal.CADBQStockSearch.createLocationMatchCondition;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEMS;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATIONS;
import static com.io7m.cardant.database.postgres.internal.Tables.STOCK;

/**
 * Count stock.
 */

public final class CADBQStockCount
  extends CADBQAbstract<CAStockSearchParameters, Long>
  implements StockCountType
{
  private static final CADBQueryProviderType.Service<
    CAStockSearchParameters,
    Long,
    StockCountType
    > SERVICE =
    new CADBQueryProviderType.Service<>(
      StockCountType.class,
      CADBQStockCount::new
    );

  private static final Long ZERO =
    Long.valueOf(0L);

  /**
   * @return A query provider
   */

  public static CADBQueryProviderType provider()
  {
    return () -> SERVICE;
  }

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQStockCount(
    final CADatabaseTransaction transaction)
  {
    super(transaction);
  }

  @Override
  protected Long onExecute(
    final DSLContext context,
    final CAStockSearchParameters parameters)
    throws CADatabaseException
  {
    final var tableSource =
      STOCK
        .join(ITEMS)
        .on(STOCK.STOCK_ITEM.eq(ITEMS.ITEM_ID))
        .join(LOCATIONS)
        .on(STOCK.STOCK_LOCATION.eq(LOCATIONS.LOCATION_ID));

    final var locationCondition =
      createLocationMatchCondition(parameters.locationMatch());
    final var itemCondition =
      createItemMatchCondition(parameters.itemMatch());
    final var kindCondition =
      createKindCondition(parameters.includeOccurrences());
    final var deletedCondition =
      createDeletedCondition(parameters.includeDeleted());
    final var allConditions =
      DSL.and(
        locationCondition,
        itemCondition,
        deletedCondition,
        kindCondition
      );

    final var query =
      context.select(DSL.sum(STOCK.STOCK_COUNT))
        .from(tableSource)
        .where(allConditions);

    final var sum = query.fetchOneInto(Long.class);
    return Objects.requireNonNullElse(sum, ZERO);
  }
}
