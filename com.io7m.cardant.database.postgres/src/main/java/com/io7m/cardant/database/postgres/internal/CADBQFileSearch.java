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
import com.io7m.cardant.database.api.CADatabaseFileSearchType;
import com.io7m.cardant.database.api.CADatabaseQueriesFilesType.SearchType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAFileColumnOrdering;
import com.io7m.cardant.model.CAFileSearchParameters;
import com.io7m.jqpage.core.JQField;
import com.io7m.jqpage.core.JQKeysetRandomAccessPagination;
import com.io7m.jqpage.core.JQKeysetRandomAccessPaginationParameters;
import com.io7m.jqpage.core.JQOrder;
import io.opentelemetry.api.trace.Span;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.impl.DSL;

import static com.io7m.cardant.database.postgres.internal.tables.Files.FILES;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DB_STATEMENT;

/**
 * Remove a file.
 */

public final class CADBQFileSearch
  extends CADBQAbstract<CAFileSearchParameters, CADatabaseFileSearchType>
  implements SearchType
{
  private static final Service<CAFileSearchParameters, CADatabaseFileSearchType, SearchType> SERVICE =
    new Service<>(SearchType.class, CADBQFileSearch::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQFileSearch(
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
  protected CADatabaseFileSearchType onExecute(
    final DSLContext context,
    final CAFileSearchParameters parameters)
    throws CADatabaseException
  {
    final Table<?> tableSource = FILES;

    final Condition descriptionCondition =
      CADBComparisons.createFuzzyMatchQuery(
        this.language(),
        parameters.description(),
        FILES.FILE_DESCRIPTION,
        "FILES.FILE_DESCRIPTION_SEARCH"
      );


    final Condition mediaCondition =
      CADBComparisons.createFuzzyMatchQuery(
        this.language(),
        parameters.mediaType(),
        FILES.FILE_MEDIA_TYPE,
        "FILES.FILE_MEDIA_TYPE_SEARCH"
      );

    final var range =
      parameters.sizeRange();
    final var sizeLowerCondition =
      DSL.condition(FILES.FILE_DATA_USED.ge(Long.valueOf(range.sizeMinimum())));
    final var sizeUpperCondition =
      DSL.condition(FILES.FILE_DATA_USED.le(Long.valueOf(range.sizeMaximum())));
    final Condition sizeCondition =
      DSL.and(sizeLowerCondition, sizeUpperCondition);

    final var allConditions =
      DSL.and(descriptionCondition, mediaCondition, sizeCondition);

    final var orderField =
      orderingToJQField(parameters.ordering());

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
        context, pageParameters);

    return new CAFileSearch(pages);
  }

  private static JQField orderingToJQField(
    final CAFileColumnOrdering ordering)
  {
    final var field =
      switch (ordering.column()) {
        case BY_ID -> FILES.FILE_ID;
        case BY_DESCRIPTION -> FILES.FILE_DESCRIPTION;
      };

    return new JQField(
      field,
      ordering.ascending() ? JQOrder.ASCENDING : JQOrder.DESCENDING
    );
  }
}
