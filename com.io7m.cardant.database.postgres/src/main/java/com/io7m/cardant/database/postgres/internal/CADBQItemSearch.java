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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemSearchType;
import com.io7m.cardant.database.postgres.internal.CADBMatch.MetaFields;
import com.io7m.cardant.database.postgres.internal.CADBMatch.QuerySetType;
import com.io7m.cardant.database.postgres.internal.CADBMatch.QuerySetType.QuerySetCondition;
import com.io7m.cardant.database.postgres.internal.CADBMatch.QuerySetType.QuerySetIntersection;
import com.io7m.cardant.database.postgres.internal.CADBMatch.QuerySetType.QuerySetUnion;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAItemColumnOrdering;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CAPage;
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.cardant.model.comparisons.CAComparisonSetType;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.Anything;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.IsEqualTo;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.IsNotEqualTo;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.IsOverlapping;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.IsSubsetOf;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.IsSupersetOf;
import com.io7m.jqpage.core.JQField;
import com.io7m.jqpage.core.JQKeysetRandomAccessPageDefinition;
import com.io7m.jqpage.core.JQKeysetRandomAccessPagination;
import com.io7m.jqpage.core.JQKeysetRandomAccessPaginationParameters;
import com.io7m.jqpage.core.JQOrder;
import io.opentelemetry.api.trace.Span;
import org.jooq.DSLContext;
import org.jooq.EnumType;
import org.jooq.Field;
import org.jooq.Record7;
import org.jooq.Select;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.io7m.cardant.database.postgres.internal.CADatabaseExceptions.handleDatabaseException;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_SEARCH_VIEW;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_PACKAGES;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DB_STATEMENT;

/**
 * Search for items.
 */

public final class CADBQItemSearch
  extends CADBQAbstract<CAItemSearchParameters, CADatabaseItemSearchType>
  implements ItemSearchType
{
  private static final Service<CAItemSearchParameters, CADatabaseItemSearchType, ItemSearchType> SERVICE =
    new Service<>(ItemSearchType.class, CADBQItemSearch::new);

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
      CADBComparisons.createSetMatchQueryInteger(
        mapTypeMatch(context, parameters.typeMatch()),
        ITEM_SEARCH_VIEW.ISV_METADATA_TYPE_IDS
      );

    final var deletedCondition =
      switch (parameters.includeDeleted()) {
        case INCLUDE_ONLY_LIVE -> ITEM_SEARCH_VIEW.ITEM_DELETED.isNull();
        case INCLUDE_ONLY_DELETED -> ITEM_SEARCH_VIEW.ITEM_DELETED.isNotNull();
        case INCLUDE_BOTH_LIVE_AND_DELETED -> DSL.trueCondition();
      };

    final var simpleConditions =
      DSL.and(
        deletedCondition,
        nameCondition,
        typeCondition
      );

    /*
     * The set of conditions that require set operations over multiple rows.
     */

    final var metaQuerySet =
      CADBMatch.ofMetaElementMatch(
        new MetaFields(
          ITEM_SEARCH_VIEW.ITEM_META_TYPE_PACKAGE,
          ITEM_SEARCH_VIEW.ITEM_META_TYPE_RECORD,
          ITEM_SEARCH_VIEW.ITEM_META_TYPE_FIELD,
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

  private static CAComparisonSetType<Integer> mapTypeMatch(
    final DSLContext context,
    final CAComparisonSetType<CATypeRecordIdentifier> c)
  {
    return switch (c) {
      case final Anything<CATypeRecordIdentifier> a -> {
        yield new Anything<>();
      }
      case final IsEqualTo<CATypeRecordIdentifier> e -> {
        yield new IsEqualTo<>(resolveTypes(context, e.value()));
      }
      case final IsNotEqualTo<CATypeRecordIdentifier> e -> {
        yield new IsNotEqualTo<>(resolveTypes(context, e.value()));
      }
      case final IsOverlapping<CATypeRecordIdentifier> e -> {
        yield new IsOverlapping<>(resolveTypes(context, e.value()));
      }
      case final IsSubsetOf<CATypeRecordIdentifier> e -> {
        yield new IsSubsetOf<>(resolveTypes(context, e.value()));
      }
      case final IsSupersetOf<CATypeRecordIdentifier> e -> {
        yield new IsSupersetOf<>(resolveTypes(context, e.value()));
      }
    };
  }

  private static Set<Integer> resolveTypes(
    final DSLContext context,
    final Set<CATypeRecordIdentifier> identifiers)
  {
    final var results = new HashSet<Integer>(identifiers.size());
    for (final var identifier : identifiers) {
      final var matches =
        DSL.and(
          METADATA_TYPES.MT_NAME
            .eq(identifier.typeName().value()),
          METADATA_TYPE_PACKAGES.MTP_NAME
            .eq(identifier.packageName().value())
        );
      context.select(METADATA_TYPES.MT_ID)
        .from(METADATA_TYPES)
        .join(METADATA_TYPE_PACKAGES)
        .on(METADATA_TYPE_PACKAGES.MTP_ID.eq(METADATA_TYPES.MT_PACKAGE))
        .where(matches)
        .fetchOptional(METADATA_TYPES.MT_ID)
        .ifPresent(results::add);
    }
    return Set.copyOf(results);
  }

  private static Select<Record7<UUID, String, Object, OffsetDateTime, UUID[], Integer[], String[]>> generateQuerySetFor(
    final DSLContext context,
    final QuerySetType metaQuerySet)
  {
    return switch (metaQuerySet) {
      case final QuerySetCondition c -> {
        yield context.select(
            ITEM_SEARCH_VIEW.ITEM_ID,
            ITEM_SEARCH_VIEW.ITEM_NAME,
            ITEM_NAME_SEARCH,
            ITEM_SEARCH_VIEW.ITEM_DELETED,
            ITEM_SEARCH_VIEW.ISV_ITEM_LOCATIONS,
            ITEM_SEARCH_VIEW.ISV_METADATA_TYPE_IDS,
            ITEM_SEARCH_VIEW.ISV_ITEM_SERIALS)
          .from(ITEM_SEARCH_VIEW)
          .where(c.condition());
      }
      case final QuerySetUnion u -> {
        final var rq0 = generateQuerySetFor(context, u.q0());
        final var rq1 = generateQuerySetFor(context, u.q1());
        yield rq0.union(rq1);
      }
      case final QuerySetIntersection u -> {
        final var rq0 = generateQuerySetFor(context, u.q0());
        final var rq1 = generateQuerySetFor(context, u.q1());
        yield rq0.intersect(rq1);
      }
    };
  }

  private static JQField orderingToJQField(
    final CAItemColumnOrdering ordering)
  {
    final var field =
      switch (ordering.column()) {
        case BY_ID -> {
          yield ITEM_SEARCH_VIEW.ITEM_ID;
        }
        case BY_NAME -> {
          yield ITEM_SEARCH_VIEW.ITEM_NAME;
        }
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
