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
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAListLocationBehaviourType;
import com.io7m.jqpage.core.JQField;
import com.io7m.jqpage.core.JQKeysetRandomAccessPagination;
import com.io7m.jqpage.core.JQOrder;
import io.opentelemetry.api.trace.Span;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.util.List;

import static com.io7m.cardant.database.postgres.internal.Tables.ITEMS;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_LOCATIONS;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DB_STATEMENT;
import static java.lang.Boolean.FALSE;

/**
 * Search for items.
 */

public final class CADBQItemSearch
  extends CADBQAbstract<CAItemSearchParameters, CADatabaseItemSearchType>
  implements CADatabaseQueriesItemsType.SearchType
{
  private static final Service<CAItemSearchParameters, CADatabaseItemSearchType, SearchType> SERVICE =
    new Service<>(SearchType.class, CADBQItemSearch::new);

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
    final Table<?> tableSource;

    /*
     * The location query.
     *
     * Note that we only join the ITEM_LOCATIONS table if a search query
     * actually requires it.
     */

    final Condition locationCondition;
    final var behaviour = parameters.locationBehaviour();
    if (behaviour instanceof final CAListLocationBehaviourType.CAListLocationExact exact) {
      tableSource =
        ITEMS.join(ITEM_LOCATIONS).on(ITEM_LOCATIONS.ITEM_ID.eq(ITEMS.ITEM_ID));
      locationCondition =
        ITEM_LOCATIONS.ITEM_LOCATION.eq(exact.location().id());

    } else if (behaviour instanceof final CAListLocationBehaviourType.CAListLocationWithDescendants descendants) {
      tableSource =
        ITEMS.join(ITEM_LOCATIONS).on(ITEM_LOCATIONS.ITEM_ID.eq(ITEMS.ITEM_ID));

      final var funcCall =
        DSL.select(DSL.field("location_descendants.location"))
          .from("location_descendants(?)", descendants.location().id())
          .asField();

      locationCondition = ITEM_LOCATIONS.ITEM_LOCATION.in(funcCall);
    } else if (behaviour instanceof CAListLocationBehaviourType.CAListLocationsAll) {
      tableSource = ITEMS;
      locationCondition = DSL.trueCondition();
    } else {
      throw new IllegalStateException();
    }

    /*
     * A search query might be present.
     */

    final Condition searchCondition;
    final var search = parameters.search();
    if (search.isPresent()) {
      final var searchText = "%%%s%%".formatted(search.get());
      searchCondition =
        DSL.condition(ITEMS.ITEM_NAME.likeIgnoreCase(searchText));
    } else {
      searchCondition = DSL.trueCondition();
    }

    /*
     * Items might be deleted.
     */

    final Condition deletedCondition =
      DSL.condition(ITEMS.ITEM_DELETED.eq(FALSE));

    final var allConditions =
      searchCondition
        .and(locationCondition)
        .and(deletedCondition);

    final var orderField =
      orderingToJQField(parameters.ordering());

    final var pages =
      JQKeysetRandomAccessPagination.createPageDefinitions(
        context,
        tableSource,
        List.of(orderField),
        List.of(allConditions),
        List.of(),
        Integer.toUnsignedLong(parameters.limit()),
        statement -> {
          Span.current().setAttribute(DB_STATEMENT, statement.toString());
        }
      );

    return new CAItemSearch(pages);
  }


  private static JQField orderingToJQField(
    final CAItemColumnOrdering ordering)
  {
    final var field =
      switch (ordering.column()) {
        case BY_ID -> ITEMS.ITEM_ID;
        case BY_NAME -> ITEMS.ITEM_NAME;
      };

    return new JQField(
      field,
      ordering.ascending() ? JQOrder.ASCENDING : JQOrder.DESCENDING
    );
  }
}
