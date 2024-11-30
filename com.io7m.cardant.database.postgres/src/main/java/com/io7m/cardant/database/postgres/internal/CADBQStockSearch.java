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
import com.io7m.cardant.database.api.CADatabaseQueriesStockType.StockSearchType;
import com.io7m.cardant.database.api.CADatabaseStockSearchType;
import com.io7m.cardant.model.CAIncludeDeleted;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemSerial;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationMatchType;
import com.io7m.cardant.model.CALocationMatchType.CALocationExact;
import com.io7m.cardant.model.CALocationMatchType.CALocationWithDescendants;
import com.io7m.cardant.model.CALocationMatchType.CALocationsAll;
import com.io7m.cardant.model.CALocationPath;
import com.io7m.cardant.model.CALocationSummary;
import com.io7m.cardant.model.CAPage;
import com.io7m.cardant.model.CAStockInstanceID;
import com.io7m.cardant.model.CAStockOccurrenceKind;
import com.io7m.cardant.model.CAStockOccurrenceSerial;
import com.io7m.cardant.model.CAStockOccurrenceSet;
import com.io7m.cardant.model.CAStockOccurrenceType;
import com.io7m.cardant.model.CAStockSearchParameters;
import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.jqpage.core.JQField;
import com.io7m.jqpage.core.JQKeysetRandomAccessPageDefinition;
import com.io7m.jqpage.core.JQKeysetRandomAccessPagination;
import com.io7m.jqpage.core.JQKeysetRandomAccessPaginationParameters;
import com.io7m.jqpage.core.JQOrder;
import io.opentelemetry.api.trace.Span;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.database.postgres.internal.CADBLocationPaths.LOCATION_PATH_NAME;
import static com.io7m.cardant.database.postgres.internal.CADatabaseExceptions.handleDatabaseException;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEMS;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATIONS;
import static com.io7m.cardant.database.postgres.internal.Tables.STOCK;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DB_STATEMENT;

/**
 * Search for stock.
 */

public final class CADBQStockSearch
  extends CADBQAbstract<CAStockSearchParameters, CADatabaseStockSearchType>
  implements StockSearchType
{
  private static final CADBQueryProviderType.Service<
    CAStockSearchParameters,
    CADatabaseStockSearchType,
    StockSearchType
    > SERVICE =
    new CADBQueryProviderType.Service<>(
      StockSearchType.class,
      CADBQStockSearch::new
    );

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQStockSearch(
    final CADatabaseTransaction transaction)
  {
    super(transaction);
  }

  /**
   * @return A query provider
   */

  public static CADBQueryProviderType provider()
  {
    return () -> SERVICE;
  }

  static Condition createKindCondition(
    final Set<CAStockOccurrenceKind> kinds)
  {
    if (Objects.equals(kinds, CAStockOccurrenceKind.all())) {
      return DSL.trueCondition();
    }

    Condition cond = DSL.falseCondition();
    for (final var kind : kinds) {
      cond = switch (kind) {
        case SERIAL -> {
          yield cond.or(STOCK.STOCK_SERIALS.isNotNull());
        }
        case SET -> {
          yield cond.or(STOCK.STOCK_SERIALS.isNull());
        }
      };
    }
    return cond;
  }

  static Condition createDeletedCondition(
    final CAIncludeDeleted deleted)
  {
    return switch (deleted) {
      case INCLUDE_ONLY_LIVE -> {
        yield ITEMS.ITEM_DELETED.isNull()
          .and(LOCATIONS.LOCATION_DELETED.isNull());
      }
      case INCLUDE_ONLY_DELETED -> {
        yield ITEMS.ITEM_DELETED.isNotNull()
          .or(LOCATIONS.LOCATION_DELETED.isNotNull());
      }
      case INCLUDE_BOTH_LIVE_AND_DELETED -> {
        yield DSL.trueCondition();
      }
    };
  }

  static Condition createItemMatchCondition(
    final CAComparisonExactType<CAItemID> match)
  {
    return switch (match) {
      case final CAComparisonExactType.Anything<CAItemID> ignored -> {
        yield DSL.trueCondition();
      }
      case final CAComparisonExactType.IsEqualTo<CAItemID> isEqualTo -> {
        yield ITEMS.ITEM_ID.eq(isEqualTo.value().id());
      }
      case final CAComparisonExactType.IsNotEqualTo<CAItemID> isNotEqualTo -> {
        yield ITEMS.ITEM_ID.notEqual(isNotEqualTo.value().id());
      }
    };
  }

  @Override
  protected CADatabaseStockSearchType onExecute(
    final DSLContext context,
    final CAStockSearchParameters parameters)
    throws CADatabaseException
  {
    final var locationCondition =
      createLocationMatchCondition(parameters.locationMatch());
    final var itemCondition =
      createItemMatchCondition(parameters.itemMatch());
    final var kindCondition =
      createKindCondition(parameters.includeOccurrences());
    final var deletedCondition =
      createDeletedCondition(parameters.includeDeleted());

    final var tableSource =
      STOCK
        .join(ITEMS)
        .on(STOCK.STOCK_ITEM.eq(ITEMS.ITEM_ID))
        .join(LOCATIONS)
        .on(STOCK.STOCK_LOCATION.eq(LOCATIONS.LOCATION_ID));

    final var allConditions =
      DSL.and(
        locationCondition,
        itemCondition,
        deletedCondition,
        kindCondition
      );

    final var orderField =
      new JQField(
        STOCK.STOCK_COUNT,
        JQOrder.DESCENDING
      );

    final var pageParameters =
      JQKeysetRandomAccessPaginationParameters.forTable(tableSource)
        .addSortField(orderField)
        .addWhereCondition(allConditions)
        .setPageSize(parameters.pageSize())
        .setStatementListener(statement -> {
          Span.current().setAttribute(DB_STATEMENT, statement.toString());
        }).build();

    final var pages =
      JQKeysetRandomAccessPagination.createPageDefinitions(
        context,
        pageParameters
      );

    return new CAStockSearch(pages);
  }

  static Condition createLocationMatchCondition(
    final CALocationMatchType match)
  {
    return switch (match) {
      case final CALocationExact exact -> {
        yield STOCK.STOCK_LOCATION.eq(exact.location().id());
      }
      case final CALocationWithDescendants withDescendants -> {
        yield DSL.condition(
          "ARRAY[?] && (SELECT ARRAY(SELECT location_descendants(?)))",
          STOCK.STOCK_LOCATION,
          withDescendants.location().id()
        );
      }
      case final CALocationsAll all -> {
        yield DSL.trueCondition();
      }
    };
  }

  private static final class CAStockSearch
    extends CAAbstractSearch<CAStockOccurrenceType>
    implements CADatabaseStockSearchType
  {
    CAStockSearch(
      final List<JQKeysetRandomAccessPageDefinition> pages)
    {
      super(pages);
    }

    private static CAStockOccurrenceType mapRecord(
      final org.jooq.Record r)
    {
      final var isSerial =
        r.get(STOCK.STOCK_SERIALS) != null;

      final var instance =
        new CAStockInstanceID(r.get(STOCK.STOCK_INSTANCE));

      final var itemSummary =
        new CAItemSummary(
          new CAItemID(r.get(ITEMS.ITEM_ID)),
          r.get(ITEMS.ITEM_NAME)
        );

      final var locationSummary =
        new CALocationSummary(
          new CALocationID(r.get(STOCK.STOCK_LOCATION)),
          Optional.ofNullable(r.get(LOCATIONS.LOCATION_PARENT))
            .map(CALocationID::new),
          CALocationPath.ofArray(r.get(LOCATION_PATH_NAME))
        );

      if (isSerial) {
        final var serialsJSON =
          r.get(STOCK.STOCK_SERIALS);

        List<CAItemSerial> serials;
        try {
          serials = CADBSerialsJSON.serialsFromJSON(serialsJSON)
            .stream()
            .sorted(CAItemSerial::compareTo)
            .toList();
        } catch (final IOException e) {
          serials = List.of();
        }

        return new CAStockOccurrenceSerial(
          instance,
          locationSummary,
          itemSummary,
          serials
        );
      }

      return new CAStockOccurrenceSet(
        instance,
        locationSummary,
        itemSummary,
        r.<Long>get(STOCK.STOCK_COUNT).longValue()
      );
    }

    @Override
    protected CAPage<CAStockOccurrenceType> page(
      final CADatabaseTransaction transaction,
      final JQKeysetRandomAccessPageDefinition page)
      throws CADatabaseException
    {
      final var context =
        transaction.createContext();
      final var querySpan =
        transaction.createQuerySpan("CAStockSearch.page");

      try {
        final var query =
          page.queryFields(context, List.of(
            ITEMS.ITEM_ID,
            ITEMS.ITEM_NAME,
            STOCK.STOCK_INSTANCE,
            STOCK.STOCK_LOCATION,
            STOCK.STOCK_SERIALS,
            STOCK.STOCK_COUNT,
            CADBLocationPaths.locationPathFromColumnNamed(
              context,
              LOCATIONS.LOCATION_ID
            ),
            LOCATIONS.LOCATION_PARENT
          ));

        querySpan.setAttribute(DB_STATEMENT, query.toString());

        final var items =
          query.fetch().map(CAStockSearch::mapRecord);

        return new CAPage<>(
          items,
          (int) page.index(),
          this.pageCount(),
          page.firstOffset()
        );
      } catch (final DataAccessException e) {
        querySpan.recordException(e);
        throw handleDatabaseException(transaction, e);
      } finally {
        querySpan.end();
      }
    }
  }
}
