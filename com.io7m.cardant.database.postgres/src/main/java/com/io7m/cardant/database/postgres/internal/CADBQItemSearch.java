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


package com.io7m.cardant.database.postgres.internal;

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseItemSearchType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType;
import com.io7m.cardant.database.postgres.internal.CADBMatch.QuerySetType.QuerySetCondition;
import com.io7m.cardant.database.postgres.internal.CADBMatch.QuerySetType.QuerySetIntersection;
import com.io7m.cardant.database.postgres.internal.CADBMatch.QuerySetType.QuerySetUnion;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAItemColumnOrdering;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CAPage;
import com.io7m.jqpage.core.JQField;
import com.io7m.jqpage.core.JQKeysetRandomAccessPageDefinition;
import com.io7m.jqpage.core.JQKeysetRandomAccessPagination;
import com.io7m.jqpage.core.JQKeysetRandomAccessPaginationParameters;
import com.io7m.jqpage.core.JQOrder;
import com.io7m.lanark.core.RDottedName;
import io.opentelemetry.api.trace.Span;
import org.jooq.DSLContext;
import org.jooq.EnumType;
import org.jooq.Field;
import org.jooq.Record5;
import org.jooq.Select;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.UUID;

import static com.io7m.cardant.database.postgres.internal.CADatabaseExceptions.handleDatabaseException;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_SEARCH_VIEW;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DB_STATEMENT;

/**
 * Search for items.
 */

public final class CADBQItemSearch
  extends CADBQAbstract<CAItemSearchParameters, CADatabaseItemSearchType>
  implements CADatabaseQueriesItemsType.SearchType
{
  private static final Service<CAItemSearchParameters, CADatabaseItemSearchType, SearchType> SERVICE =
    new Service<>(SearchType.class, CADBQItemSearch::new);

  private static final Field<Object> ITEM_NAME_SEARCH =
    DSL.field(DSL.name(ITEM_SEARCH_VIEW.getName(), "ITEM_NAME_SEARCH"));

  private static final Field<Object> ITEM_META_NAME_SEARCH =
    DSL.field(DSL.name(ITEM_SEARCH_VIEW.getName(), "ITEM_META_NAME_SEARCH"));

  private static final Field<Object> ITEM_META_VALUE_TEXT_SEARCH =
    DSL.field(DSL.name(ITEM_SEARCH_VIEW.getName(), "ITEM_META_VALUE_TEXT"));

  private static final Field<EnumType> ITEM_META_VALUE_TYPE =
    (Field<EnumType>) (Object) ITEM_SEARCH_VIEW.ITEM_META_VALUE_TYPE;

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemSearch(
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

  @Override
  protected CADatabaseItemSearchType onExecute(
    final DSLContext context,
    final CAItemSearchParameters parameters)
    throws CADatabaseException
  {
    /*
     * The "simple" conditions; these are the conditions that are true
     * for all expanded rows of a given item, or false for all rows. They
     * can be handled using a single WHERE clause on the outermost query.
     */

    final var nameCondition =
      CADBComparisons.createFuzzyMatchQuery(
        parameters.nameMatch(),
        ITEM_SEARCH_VIEW.ITEM_NAME,
        ITEM_NAME_SEARCH.getName()
      );

    final var typeCondition =
      CADBComparisons.createSetMatchQueryString(
        parameters.typeMatch().map(RDottedName::value),
        ITEM_SEARCH_VIEW.ISV_MTR_NAMES
      );

    final var locationCondition =
      CADBMatch.ofLocationMatch(
        new CADBMatch.LocationFields(
          ITEM_SEARCH_VIEW.ISV_ITEM_LOCATIONS
        ),
        parameters.locationMatch()
      );

    final var simpleConditions =
      DSL.and(
        nameCondition,
        typeCondition,
        locationCondition
      );

    /*
     * The set of conditions that require set operations over multiple rows.
     */

    final var metaQuerySet =
      CADBMatch.ofMetaElementMatch(
        new CADBMatch.MetaFields(
          new CADBMatch.NameFields(
            ITEM_SEARCH_VIEW.ITEM_META_NAME,
            ITEM_META_NAME_SEARCH
          ),
          ITEM_META_VALUE_TYPE,
          ITEM_SEARCH_VIEW.ITEM_META_VALUE_INTEGRAL,
          ITEM_SEARCH_VIEW.ITEM_META_VALUE_REAL,
          ITEM_SEARCH_VIEW.ITEM_META_VALUE_TIME,
          ITEM_SEARCH_VIEW.ITEM_META_VALUE_MONEY_CURRENCY,
          ITEM_SEARCH_VIEW.ITEM_META_VALUE_MONEY,
          ITEM_SEARCH_VIEW.ITEM_META_VALUE_TEXT,
          ITEM_META_VALUE_TEXT_SEARCH
        ),
        parameters.metadataMatch()
      );

    final var query =
      generateQuerySetFor(context, metaQuerySet)
        .asTable(ITEM_SEARCH_VIEW);

    final var orderField =
      orderingToJQField(parameters.ordering());

    final var pageParameters =
      JQKeysetRandomAccessPaginationParameters.forTable(query)
        .addSortField(orderField)
        .addWhereCondition(simpleConditions)
        .addGroupByField(ITEM_SEARCH_VIEW.ITEM_ID)
        .addGroupByField(ITEM_SEARCH_VIEW.ITEM_NAME)
        .setPageSize(parameters.pageSize())
        .setStatementListener(statement -> {
          Span.current().setAttribute(DB_STATEMENT, statement.toString());
        }).build();

    final var pages =
      JQKeysetRandomAccessPagination.createPageDefinitions(
        context, pageParameters);

    return new CAItemSearch(pages);
  }

  private static Select<Record5<UUID, String, Object, UUID[], String[]>> generateQuerySetFor(
    final DSLContext context,
    final CADBMatch.QuerySetType metaQuerySet)
  {
    if (metaQuerySet instanceof final QuerySetCondition c) {
      return context.select(
          ITEM_SEARCH_VIEW.ITEM_ID,
          ITEM_SEARCH_VIEW.ITEM_NAME,
          ITEM_NAME_SEARCH,
          ITEM_SEARCH_VIEW.ISV_ITEM_LOCATIONS,
          ITEM_SEARCH_VIEW.ISV_MTR_NAMES)
        .from(ITEM_SEARCH_VIEW)
        .where(c.condition());
    }

    if (metaQuerySet instanceof final QuerySetUnion u) {
      final var rq0 = generateQuerySetFor(context, u.q0());
      final var rq1 = generateQuerySetFor(context, u.q1());
      return rq0.union(rq1);
    }

    if (metaQuerySet instanceof final QuerySetIntersection u) {
      final var rq0 = generateQuerySetFor(context, u.q0());
      final var rq1 = generateQuerySetFor(context, u.q1());
      return rq0.intersect(rq1);
    }

    throw new IllegalStateException();
  }

  private static JQField orderingToJQField(
    final CAItemColumnOrdering ordering)
  {
    final var field =
      switch (ordering.column()) {
        case BY_ID -> ITEM_SEARCH_VIEW.ITEM_ID;
        case BY_NAME -> ITEM_SEARCH_VIEW.ITEM_NAME;
      };

    return new JQField(
      field,
      ordering.ascending() ? JQOrder.ASCENDING : JQOrder.DESCENDING
    );
  }

  private static final class CAItemSearch
    extends CAAbstractSearch<CAItemSummary>
    implements CADatabaseItemSearchType
  {
    CAItemSearch(
      final List<JQKeysetRandomAccessPageDefinition> inPages)
    {
      super(inPages);
    }

    @Override
    protected CAPage<CAItemSummary> page(
      final CADatabaseTransaction transaction,
      final JQKeysetRandomAccessPageDefinition page)
      throws CADatabaseException
    {
      final var context =
        transaction.createContext();
      final var querySpan =
        transaction.createQuerySpan(
          "CAItemSearch.page");

      try {
        final var query =
          page.queryFields(context, List.of(
            ITEM_SEARCH_VIEW.ITEM_ID,
            ITEM_SEARCH_VIEW.ITEM_NAME
          ));

        querySpan.setAttribute(DB_STATEMENT, query.toString());

        final var items =
          query.fetch().map(record -> {
            return new CAItemSummary(
              new CAItemID(record.get(ITEM_SEARCH_VIEW.ITEM_ID)),
              record.get(ITEM_SEARCH_VIEW.ITEM_NAME)
            );
          });

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
