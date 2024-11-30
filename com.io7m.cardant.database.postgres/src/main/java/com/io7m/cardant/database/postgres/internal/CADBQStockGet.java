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
import com.io7m.cardant.database.api.CADatabaseQueriesStockType.StockGetType;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemSerial;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationPath;
import com.io7m.cardant.model.CALocationSummary;
import com.io7m.cardant.model.CAStockInstanceID;
import com.io7m.cardant.model.CAStockOccurrenceSerial;
import com.io7m.cardant.model.CAStockOccurrenceSet;
import com.io7m.cardant.model.CAStockOccurrenceType;
import com.io7m.junreachable.UnreachableCodeException;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.io7m.cardant.database.postgres.internal.CADBLocationPaths.LOCATION_PATH_NAME;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEMS;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATIONS;
import static com.io7m.cardant.database.postgres.internal.Tables.STOCK;

/**
 * Get stock.
 */

public final class CADBQStockGet
  extends CADBQAbstract<CAStockInstanceID, Optional<CAStockOccurrenceType>>
  implements StockGetType
{
  private static final CADBQueryProviderType.Service<
    CAStockInstanceID,
    Optional<CAStockOccurrenceType>,
    StockGetType
    > SERVICE =
    new CADBQueryProviderType.Service<>(
      StockGetType.class,
      CADBQStockGet::new
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

  public CADBQStockGet(
    final CADatabaseTransaction transaction)
  {
    super(transaction);
  }

  @Override
  protected Optional<CAStockOccurrenceType> onExecute(
    final DSLContext context,
    final CAStockInstanceID parameters)
    throws CADatabaseException
  {
    final var tableSource =
      STOCK
        .join(ITEMS)
        .on(STOCK.STOCK_ITEM.eq(ITEMS.ITEM_ID))
        .join(LOCATIONS)
        .on(STOCK.STOCK_LOCATION.eq(LOCATIONS.LOCATION_ID));

    final var r =
      context.select(
          ITEMS.ITEM_ID,
          ITEMS.ITEM_NAME,
          STOCK.STOCK_ITEM,
          STOCK.STOCK_INSTANCE,
          STOCK.STOCK_LOCATION,
          STOCK.STOCK_SERIALS,
          STOCK.STOCK_COUNT,
          LOCATIONS.LOCATION_ID,
          CADBLocationPaths.locationPathFromColumnNamed(
            context,
            LOCATIONS.LOCATION_ID
          ),
          LOCATIONS.LOCATION_PARENT
        ).from(tableSource)
        .where(STOCK.STOCK_INSTANCE.eq(parameters.id()))
        .fetchOptional();

    return r.map(CADBQStockGet::mapRecord);
  }

  private static CAStockOccurrenceType mapRecord(
    final Record r)
  {
    final var serialsJSON =
      r.get(STOCK.STOCK_SERIALS);

    final var location =
      new CALocationSummary(
        new CALocationID(r.get(LOCATIONS.LOCATION_ID)),
        Optional.ofNullable(r.get(LOCATIONS.LOCATION_PARENT))
          .map(CALocationID::new),
        CALocationPath.ofArray(r.get(LOCATION_PATH_NAME))
      );

    final var item =
      new CAItemSummary(
        new CAItemID(r.get(STOCK.STOCK_ITEM)),
        r.get(ITEMS.ITEM_NAME)
      );

    if (serialsJSON == null) {
      return new CAStockOccurrenceSet(
        new CAStockInstanceID(r.get(STOCK.STOCK_INSTANCE)),
        location,
        item,
        r.get(STOCK.STOCK_COUNT).longValue()
      );
    }

    final List<CAItemSerial> serials;
    try {
      serials = CADBSerialsJSON.serialsFromJSON(serialsJSON);
    } catch (final IOException e) {
      throw new UnreachableCodeException(e);
    }

    return new CAStockOccurrenceSerial(
      new CAStockInstanceID(r.get(STOCK.STOCK_INSTANCE)),
      location,
      item,
      serials
    );
  }
}
