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
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAItemColumnOrdering;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAItemSearchParameters.CAMetadataMatchType;
import com.io7m.cardant.model.CAItemSearchParameters.CAMetadataMatchType.CAMetadataMatchAny;
import com.io7m.cardant.model.CAItemSearchParameters.CAMetadataMatchType.CAMetadataRequire;
import com.io7m.cardant.model.CAItemSearchParameters.CAMetadataValueMatchType;
import com.io7m.cardant.model.CAItemSearchParameters.CAMetadataValueMatchType.CAMetadataValueMatchAny;
import com.io7m.cardant.model.CAItemSearchParameters.CAMetadataValueMatchType.CAMetadataValueMatchExact;
import com.io7m.cardant.model.CAItemSearchParameters.CANameMatchType;
import com.io7m.cardant.model.CAItemSearchParameters.CANameMatchType.CANameMatchAny;
import com.io7m.cardant.model.CAItemSearchParameters.CANameMatchType.CANameMatchExact;
import com.io7m.cardant.model.CAItemSearchParameters.CANameMatchType.CANameMatchSearch;
import com.io7m.cardant.model.CAItemSearchParameters.CATypeMatchType;
import com.io7m.cardant.model.CAItemSearchParameters.CATypeMatchType.CATypeMatchAllOf;
import com.io7m.cardant.model.CAItemSearchParameters.CATypeMatchType.CATypeMatchAny;
import com.io7m.cardant.model.CAItemSearchParameters.CATypeMatchType.CATypeMatchAnyOf;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CALocationMatchType;
import com.io7m.cardant.model.CALocationMatchType.CALocationExact;
import com.io7m.cardant.model.CALocationMatchType.CALocationWithDescendants;
import com.io7m.cardant.model.CALocationMatchType.CALocationsAll;
import com.io7m.cardant.model.CAPage;
import com.io7m.jqpage.core.JQField;
import com.io7m.jqpage.core.JQKeysetRandomAccessPageDefinition;
import com.io7m.jqpage.core.JQKeysetRandomAccessPagination;
import com.io7m.jqpage.core.JQKeysetRandomAccessPaginationParameters;
import com.io7m.jqpage.core.JQOrder;
import com.io7m.lanark.core.RDottedName;
import io.opentelemetry.api.trace.Span;
import org.jooq.CommonTableExpression;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.io7m.cardant.database.postgres.internal.CADatabaseExceptions.handleDatabaseException;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEMS;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_LOCATIONS;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_METADATA;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_TYPES;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_DECLARATIONS;
import static com.io7m.jqpage.core.JQSelectDistinct.SELECT_DISTINCT;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DB_STATEMENT;
import static java.lang.Boolean.FALSE;
import static java.lang.Integer.toUnsignedLong;
import static org.jooq.impl.SQLDataType.BINARY;
import static org.jooq.impl.SQLDataType.VARCHAR;

/**
 * Search for items.
 */

public final class CADBQItemSearch
  extends CADBQAbstract<CAItemSearchParameters, CADatabaseItemSearchType>
  implements CADatabaseQueriesItemsType.SearchType
{
  private static final Service<CAItemSearchParameters, CADatabaseItemSearchType, SearchType> SERVICE =
    new Service<>(SearchType.class, CADBQItemSearch::new);

  /*
   * The "expanded_items" common table expression and fields.
   */

  private static final Name EXPANDED_CTE_NAME =
    DSL.name("EXPANDED_ITEMS");

  private static final Name EXPANDED_CTE_ITEM_ID_FIELD_NAME =
    DSL.name(EXPANDED_CTE_NAME, DSL.name("ITEM_ID"));

  private static final Name EXPANDED_CTE_ITEM_NAME_FIELD_NAME =
    DSL.name(EXPANDED_CTE_NAME, DSL.name("ITEM_NAME"));

  private static final Name EXPANDED_CTE_ITEM_NAME_SEARCH_FIELD_NAME =
    DSL.name(EXPANDED_CTE_NAME, DSL.name("ITEM_NAME_SEARCH"));

  private static final Name EXPANDED_CTE_TYPE_NAME_FIELD_NAME =
    DSL.name(EXPANDED_CTE_NAME, DSL.name("TYPE_NAME"));

  private static final Name EXPANDED_CTE_META_KEY_FIELD_NAME =
    DSL.name(EXPANDED_CTE_NAME, DSL.name("META_KEY"));

  private static final Name EXPANDED_CTE_META_VALUE_FIELD_NAME =
    DSL.name(EXPANDED_CTE_NAME, DSL.name("META_VALUE"));

  private static final Name EXPANDED_CTE_LOCATION_FIELD_NAME =
    DSL.name(EXPANDED_CTE_NAME, DSL.name("LOCATION"));

  private static final Field<UUID> EXPANDED_CTE_OUTPUT_ITEM_ID_FIELD =
    ITEMS.ITEM_ID.as(EXPANDED_CTE_ITEM_ID_FIELD_NAME);

  private static final Field<String> EXPANDED_CTE_OUTPUT_ITEM_NAME_FIELD =
    ITEMS.ITEM_NAME.as(EXPANDED_CTE_ITEM_NAME_FIELD_NAME);

  private static final Field<byte[]> EXPANDED_CTE_OUTPUT_ITEM_NAME_SEARCH_FIELD =
    DSL.field(
      DSL.name(ITEMS.getName(), "ITEM_NAME_SEARCH"),
      BINARY
    ).as(EXPANDED_CTE_ITEM_NAME_SEARCH_FIELD_NAME);

  private static final Field<String> EXPANDED_CTE_OUTPUT_TYPE_NAME_FIELD =
    METADATA_TYPE_DECLARATIONS.NAME.as(EXPANDED_CTE_TYPE_NAME_FIELD_NAME);

  private static final Field<String> EXPANDED_CTE_OUTPUT_META_KEY_FIELD =
    ITEM_METADATA.METADATA_NAME.as(EXPANDED_CTE_META_KEY_FIELD_NAME);

  private static final Field<String> EXPANDED_CTE_OUTPUT_META_VALUE_FIELD =
    ITEM_METADATA.METADATA_VALUE.as(EXPANDED_CTE_META_VALUE_FIELD_NAME);

  private static final Field<UUID> EXPANDED_CTE_OUTPUT_LOCATION_FIELD =
    ITEM_LOCATIONS.ITEM_LOCATION.as(EXPANDED_CTE_LOCATION_FIELD_NAME);

  /*
   * The "aggregated_items" common table expression and fields. The CTE
   * transforms the "expanded_items" CTE based on the following field
   * definitions.
   */

  private static final Name AGGREGATED_CTE_NAME =
    DSL.name("AGGREGATED_ITEMS");

  private static final Name AGGREGATED_CTE_ITEM_ID_FIELD_NAME =
    DSL.name(AGGREGATED_CTE_NAME, DSL.name("ITEM_ID"));

  private static final Name AGGREGATED_CTE_ITEM_NAME_FIELD_NAME =
    DSL.name(AGGREGATED_CTE_NAME, DSL.name("ITEM_NAME"));

  private static final Name AGGREGATED_CTE_ITEM_NAME_SEARCH_FIELD_NAME =
    DSL.name(AGGREGATED_CTE_NAME, DSL.name("ITEM_NAME_SEARCH"));

  private static final Name AGGREGATED_CTE_TYPE_FIELD_NAME =
    DSL.name(AGGREGATED_CTE_NAME, DSL.name("TYPE"));

  private static final Name AGGREGATED_CTE_META_KEYS_FIELD_NAME =
    DSL.name(AGGREGATED_CTE_NAME, DSL.name("META_KEYS"));

  private static final Name AGGREGATED_CTE_META_PAIRS_FIELD_NAME =
    DSL.name(AGGREGATED_CTE_NAME, DSL.name("META_PAIRS"));

  private static final Name AGGREGATED_CTE_LOCATION_FIELD_NAME =
    DSL.name(AGGREGATED_CTE_NAME, DSL.name("LOCATION"));

  private static final Field<UUID> AGGREGATED_CTE_OUTPUT_ITEM_ID_FIELD =
    DSL.field(EXPANDED_CTE_ITEM_ID_FIELD_NAME, SQLDataType.UUID)
      .as(AGGREGATED_CTE_ITEM_ID_FIELD_NAME);

  private static final Field<String[]> AGGREGATED_CTE_OUTPUT_META_KEYS_FIELD =
    DSL.arrayAgg(DSL.field(EXPANDED_CTE_META_KEY_FIELD_NAME, VARCHAR))
      .as(AGGREGATED_CTE_META_KEYS_FIELD_NAME);

  private static final Field<Record> AGGREGATED_CTE_OUTPUT_META_PAIRS_FIELD =
    DSL.field(
        "array_agg(row(?, ?)::metadata_element_t)",
        SQLDataType.RECORD,
        DSL.field(EXPANDED_CTE_META_KEY_FIELD_NAME, VARCHAR),
        DSL.field(EXPANDED_CTE_META_VALUE_FIELD_NAME, VARCHAR))
      .as(AGGREGATED_CTE_META_PAIRS_FIELD_NAME);

  private static final Field<UUID[]> AGGREGATED_CTE_OUTPUT_LOCATIONS_FIELD =
    DSL.arrayAgg(DSL.field(EXPANDED_CTE_LOCATION_FIELD_NAME, SQLDataType.UUID))
      .as(AGGREGATED_CTE_LOCATION_FIELD_NAME);

  private static final Field<String> AGGREGATED_CTE_OUTPUT_ITEM_NAME_FIELD =
    DSL.field(EXPANDED_CTE_ITEM_NAME_FIELD_NAME, VARCHAR)
      .as(AGGREGATED_CTE_ITEM_NAME_FIELD_NAME);

  private static final Field<byte[]> AGGREGATED_CTE_OUTPUT_ITEM_NAME_SEARCH_FIELD =
    DSL.field(EXPANDED_CTE_ITEM_NAME_SEARCH_FIELD_NAME, BINARY)
      .as(AGGREGATED_CTE_ITEM_NAME_SEARCH_FIELD_NAME);

  private static final Field<String[]> AGGREGATED_CTE_OUTPUT_TYPE_FIELD =
    DSL.arrayAgg(DSL.field(EXPANDED_CTE_TYPE_NAME_FIELD_NAME, VARCHAR))
      .as(AGGREGATED_CTE_TYPE_FIELD_NAME);

  /*
   * The innermost "filter" query takes fields from the "aggregated_items" CTE.
   */

  private static final Name FILTER_NAME =
    DSL.name("FILTER_ITEMS");

  private static final Name FILTER_ITEM_ID_FIELD_NAME =
    DSL.name(FILTER_NAME, DSL.name("ITEM_ID"));

  private static final Name FILTER_ITEM_NAME_FIELD_NAME =
    DSL.name(FILTER_NAME, DSL.name("ITEM_NAME"));

  private static final Name FILTER_ITEM_NAME_SEARCH_FIELD_NAME =
    DSL.name(FILTER_NAME, DSL.name("ITEM_NAME_SEARCH"));

  private static final Name FILTER_TYPE_FIELD_NAME =
    DSL.name(FILTER_NAME, DSL.name("TYPE"));

  private static final Name FILTER_META_KEYS_FIELD_NAME =
    DSL.name(FILTER_NAME, DSL.name("META_KEYS"));

  private static final Name FILTER_META_PAIRS_FIELD_NAME =
    DSL.name(FILTER_NAME, DSL.name("META_PAIRS"));

  private static final Name FILTER_LOCATION_FIELD_NAME =
    DSL.name(FILTER_NAME, DSL.name("LOCATION"));

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
    final var baseTableSource =
      createBaseTableSource(parameters);
    final var expandedItemsCTE =
      createExpandedItemsCTE(context, parameters, baseTableSource);
    final var aggregatedItemsCTE =
      createAggregatedItemsCTE(context, parameters, expandedItemsCTE);

    /*
     * Now create all the conditions that will be used to filter rows from
     * the aggregated table source.
     */

    final var locationCondition =
      createLocationCondition(parameters.locationMatch());
    final var nameCondition =
      createNameCondition(parameters.nameMatch());
    final var typeCondition =
      createTypeCondition(parameters.typeMatch());
    final var metaCondition =
      createMetaCondition(parameters.metadataMatch());

    final var allConditions =
      DSL.and(
        locationCondition,
        nameCondition,
        typeCondition,
        metaCondition
      );

    final var orderField =
      orderingToJQField(parameters.ordering());

    final var filteredQuery =
      createFilteredQuery(
        context,
        parameters,
        expandedItemsCTE,
        aggregatedItemsCTE
      );

    final var pageParameters =
      JQKeysetRandomAccessPaginationParameters.forTable(filteredQuery)
        .addSortField(orderField)
        .addWhereCondition(allConditions)
        .setPageSize(toUnsignedLong(parameters.limit()))
        .setDistinct(SELECT_DISTINCT)
        .setStatementListener(statement -> {
          Span.current().setAttribute(DB_STATEMENT, statement.toString());
        }).build();

    final var pages =
      JQKeysetRandomAccessPagination.createPageDefinitions(
        context, pageParameters);

    return new CAItemSearch(pages);
  }

  /**
   * Create the final filtered query.
   */

  private static Table<Record> createFilteredQuery(
    final DSLContext context,
    final CAItemSearchParameters parameters,
    final CommonTableExpression<Record> expandedItemsCTE,
    final CommonTableExpression<Record> aggregatedItemsCTE)
  {
    final var filterFields = new ArrayList<Field<?>>(6);
    filterFields.add(
      DSL.field(AGGREGATED_CTE_ITEM_ID_FIELD_NAME)
        .as(FILTER_ITEM_ID_FIELD_NAME));
    filterFields.add(
      DSL.field(AGGREGATED_CTE_ITEM_NAME_FIELD_NAME)
        .as(FILTER_ITEM_NAME_FIELD_NAME));
    filterFields.add(
      DSL.field(AGGREGATED_CTE_TYPE_FIELD_NAME)
        .as(FILTER_TYPE_FIELD_NAME));
    filterFields.add(
      DSL.field(AGGREGATED_CTE_ITEM_NAME_SEARCH_FIELD_NAME)
        .as(FILTER_ITEM_NAME_SEARCH_FIELD_NAME));
    filterFields.add(
      DSL.field(AGGREGATED_CTE_META_KEYS_FIELD_NAME)
        .as(FILTER_META_KEYS_FIELD_NAME));
    filterFields.add(
      DSL.field(AGGREGATED_CTE_META_PAIRS_FIELD_NAME)
        .as(FILTER_META_PAIRS_FIELD_NAME));

    if (!(parameters.locationMatch() instanceof CALocationsAll)) {
      filterFields.add(
        DSL.field(AGGREGATED_CTE_LOCATION_FIELD_NAME)
          .as(FILTER_LOCATION_FIELD_NAME));
    }

    return context.with(expandedItemsCTE, aggregatedItemsCTE)
      .select(filterFields)
      .from(aggregatedItemsCTE)
      .asTable(FILTER_NAME);
  }

  /**
   * Now, create an "aggregated" table source. This reduces the above
   * table down to one where the metadata keys and values are aggregated
   * into array columns and each item has exactly one row.
   */

  private static CommonTableExpression<Record> createAggregatedItemsCTE(
    final DSLContext context,
    final CAItemSearchParameters parameters,
    final CommonTableExpression<Record> expandedItemsCTE)
  {
    final var aggregateFields = new ArrayList<Field<?>>(6);
    aggregateFields.add(AGGREGATED_CTE_OUTPUT_ITEM_ID_FIELD);
    aggregateFields.add(AGGREGATED_CTE_OUTPUT_ITEM_NAME_FIELD);
    aggregateFields.add(AGGREGATED_CTE_OUTPUT_ITEM_NAME_SEARCH_FIELD);
    aggregateFields.add(AGGREGATED_CTE_OUTPUT_TYPE_FIELD);
    aggregateFields.add(AGGREGATED_CTE_OUTPUT_META_KEYS_FIELD);
    aggregateFields.add(AGGREGATED_CTE_OUTPUT_META_PAIRS_FIELD);

    if (!(parameters.locationMatch() instanceof CALocationsAll)) {
      aggregateFields.add(AGGREGATED_CTE_OUTPUT_LOCATIONS_FIELD);
    }

    return AGGREGATED_CTE_NAME.as(
      context.select(aggregateFields)
        .from(expandedItemsCTE)
        .groupBy(
          AGGREGATED_CTE_OUTPUT_ITEM_ID_FIELD,
          AGGREGATED_CTE_OUTPUT_ITEM_NAME_FIELD,
          AGGREGATED_CTE_OUTPUT_ITEM_NAME_SEARCH_FIELD
        )
    );
  }

  /**
   * Create a common table expression that selects the required columns
   * from the base table source.
   */

  private static CommonTableExpression<Record> createExpandedItemsCTE(
    final DSLContext context,
    final CAItemSearchParameters parameters,
    final Table<?> baseTableSource)
  {
    final var expandedFields = new ArrayList<Field<?>>(7);
    expandedFields.add(EXPANDED_CTE_OUTPUT_ITEM_ID_FIELD);
    expandedFields.add(EXPANDED_CTE_OUTPUT_ITEM_NAME_FIELD);
    expandedFields.add(EXPANDED_CTE_OUTPUT_ITEM_NAME_SEARCH_FIELD);
    expandedFields.add(EXPANDED_CTE_OUTPUT_META_KEY_FIELD);
    expandedFields.add(EXPANDED_CTE_OUTPUT_META_VALUE_FIELD);
    expandedFields.add(EXPANDED_CTE_OUTPUT_TYPE_NAME_FIELD);

    if (!(parameters.locationMatch() instanceof CALocationsAll)) {
      expandedFields.add(EXPANDED_CTE_OUTPUT_LOCATION_FIELD);
    }

    return EXPANDED_CTE_NAME.as(
      context.select(expandedFields)
        .from(baseTableSource)
        .where(ITEMS.ITEM_DELETED.eq(FALSE))
    );
  }

  /**
   * Create the base table source. This consists of a series of left
   * joined tables. This will, effectively, result in a table where
   * each item has 1 + M rows, where M is the number of metadata values.
   */

  private static Table<?> createBaseTableSource(
    final CAItemSearchParameters parameters)
  {
    Table<?> baseTableSource;

    final var baseWithTypeAssignments =
      ITEMS.leftJoin(ITEM_TYPES)
        .on(ITEM_TYPES.ITEM.eq(ITEMS.ITEM_ID));

    final var baseWithMetadata =
      baseWithTypeAssignments.leftJoin(ITEM_METADATA)
        .on(ITEM_METADATA.METADATA_ITEM_ID.eq(ITEMS.ITEM_ID));

    final var baseWithTypeDeclarations =
      baseWithMetadata.leftJoin(METADATA_TYPE_DECLARATIONS)
        .on(METADATA_TYPE_DECLARATIONS.ID.eq(ITEM_TYPES.TYPE_DECLARATION));

    baseTableSource = baseWithTypeDeclarations;

    /*
     * If the location query mentions locations, the locations table needs
     * to be joined.
     */

    if (!(parameters.locationMatch() instanceof CALocationsAll)) {
      baseTableSource = baseTableSource.join(ITEM_LOCATIONS)
        .on(ITEM_LOCATIONS.ITEM_ID.eq(ITEMS.ITEM_ID));
    }

    return baseTableSource;
  }

  private static Condition createMetaCondition(
    final CAMetadataMatchType match)
  {
    if (match instanceof CAMetadataMatchAny) {
      return DSL.trueCondition();
    }

    if (match instanceof final CAMetadataRequire require) {
      final var conditions =
        new ArrayList<Condition>(require.values().size());

      for (final var entry : require.values().entrySet()) {
        final var condition =
          createMetaValueCondition(entry.getKey(), entry.getValue());

        conditions.add(condition);
      }
      return DSL.and(conditions);
    }

    throw new IllegalStateException();
  }

  private static Condition createMetaValueCondition(
    final RDottedName name,
    final CAMetadataValueMatchType value)
  {
    /*
     * Check to see if a metadata value exists in the array-typed field.
     */

    if (value instanceof CAMetadataValueMatchAny) {
      return DSL.condition(
        "? = any(?)",
        name.value(),
        FILTER_META_KEYS_FIELD_NAME
      );
    }

    /*
     * Check to see if a metadata value exists in the array-typed field.
     */

    if (value instanceof final CAMetadataValueMatchExact exact) {
      return DSL.condition(
        "ROW(?, ?)::METADATA_ELEMENT_T = any(?)",
        name.value(),
        exact.text(),
        FILTER_META_PAIRS_FIELD_NAME
      );
    }

    throw new IllegalStateException();
  }

  private static Condition createTypeCondition(
    final CATypeMatchType match)
  {
    if (match instanceof CATypeMatchAny) {
      return DSL.trueCondition();
    }

    /*
     * This condition is true if the types in the item field have any
     * overlap with the given array.
     */

    if (match instanceof final CATypeMatchAnyOf any) {
      final var types =
        any.types()
          .stream()
          .map(RDottedName::value)
          .toArray(String[]::new);

      return DSL.condition(
        "? && ?",
        FILTER_TYPE_FIELD_NAME,
        DSL.array(types)
      );
    }

    /*
     * This condition is true the given array is completely contained by
     * the types in the item field.
     */

    if (match instanceof final CATypeMatchAllOf all) {
      final var types =
        all.types()
          .stream()
          .map(RDottedName::value)
          .toArray(String[]::new);

      return DSL.condition(
        "? <@ ?",
        DSL.array(types),
        FILTER_TYPE_FIELD_NAME
      );
    }

    throw new IllegalStateException();
  }

  private static Condition createNameCondition(
    final CANameMatchType match)
  {
    if (match instanceof CANameMatchAny) {
      return DSL.trueCondition();
    }

    if (match instanceof final CANameMatchExact exact) {
      return DSL.condition(
        DSL.field(FILTER_ITEM_NAME_FIELD_NAME).eq(exact.text())
      );
    }

    if (match instanceof final CANameMatchSearch query) {
      return DSL.condition(
        "? @@ websearch_to_tsquery(?)",
        DSL.field(FILTER_ITEM_NAME_SEARCH_FIELD_NAME, VARCHAR),
        DSL.inline(query.query())
      );
    }

    throw new IllegalStateException();
  }

  private static Condition createLocationCondition(
    final CALocationMatchType match)
  {
    if (match instanceof CALocationsAll) {
      return DSL.trueCondition();
    }

    /*
     * This condition is true if the given "exact location" appears anywhere
     * within the aggregated item location array. The item location array
     * should only contain one item.
     */

    if (match instanceof final CALocationExact exact) {
      return DSL.condition(
        "? = any(?)",
        exact.location().id(),
        FILTER_LOCATION_FIELD_NAME
      );
    }

    /*
     * This condition is true if the locations in the item field have any
     * overlap with the array returned by the location_descendants()
     * function.
     */

    if (match instanceof final CALocationWithDescendants descendants) {
      return DSL.condition(
        "? && (select array(select location_descendants(?)))",
        FILTER_LOCATION_FIELD_NAME,
        descendants.location().id()
      );
    }

    throw new IllegalStateException();
  }

  private static JQField orderingToJQField(
    final CAItemColumnOrdering ordering)
  {
    final var field =
      switch (ordering.column()) {
        case BY_ID -> DSL.field(FILTER_ITEM_ID_FIELD_NAME);
        case BY_NAME -> DSL.field(FILTER_ITEM_NAME_FIELD_NAME);
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
            DSL.field(FILTER_ITEM_ID_FIELD_NAME),
            DSL.field(FILTER_ITEM_NAME_FIELD_NAME)
          ));

        querySpan.setAttribute(DB_STATEMENT, query.toString());

        final var items =
          query.fetch().map(record -> {
            return new CAItemSummary(
              new CAItemID(
                record.get(
                  DSL.field(FILTER_ITEM_ID_FIELD_NAME, SQLDataType.UUID)
                )
              ),
              record.get(
                DSL.field(FILTER_ITEM_NAME_FIELD_NAME, VARCHAR)
              )
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
