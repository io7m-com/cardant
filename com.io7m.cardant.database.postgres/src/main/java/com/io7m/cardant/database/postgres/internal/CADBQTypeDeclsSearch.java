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

import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeDeclarationsSearchType;
import com.io7m.cardant.database.api.CADatabaseTypeDeclarationSearchType;
import com.io7m.jqpage.core.JQField;
import com.io7m.jqpage.core.JQKeysetRandomAccessPagination;
import com.io7m.jqpage.core.JQKeysetRandomAccessPaginationParameters;
import com.io7m.jqpage.core.JQOrder;
import io.opentelemetry.api.trace.Span;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_RECORDS;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DB_STATEMENT;

/**
 * Search for type declarations.
 */

public final class CADBQTypeDeclsSearch
  extends CADBQAbstract<String, CADatabaseTypeDeclarationSearchType>
  implements TypeDeclarationsSearchType
{
  private static final CADBQueryProviderType.Service<
    String,
    CADatabaseTypeDeclarationSearchType,
    TypeDeclarationsSearchType> SERVICE =
    new CADBQueryProviderType.Service<>(
      TypeDeclarationsSearchType.class,
      CADBQTypeDeclsSearch::new
    );

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQTypeDeclsSearch(
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
  protected CADatabaseTypeDeclarationSearchType
  onExecute(
    final DSLContext context,
    final String query)
  {
    final var tableSource =
      METADATA_TYPES_RECORDS;

    final var searchCondition =
      DSL.condition(
        "METADATA_TYPES_RECORDS.mtr_description_search @@ websearch_to_tsquery(?)",
        DSL.inline(query)
      );

    final var orderField =
      new JQField(METADATA_TYPES_RECORDS.MTR_NAME, JQOrder.ASCENDING);

    final var pageParameters =
      JQKeysetRandomAccessPaginationParameters.forTable(tableSource)
        .addSortField(orderField)
        .addWhereCondition(searchCondition)
        .setPageSize(10L)
        .setStatementListener(statement -> {
          Span.current().setAttribute(DB_STATEMENT, statement.toString());
        }).build();

    final var pages =
      JQKeysetRandomAccessPagination.createPageDefinitions(
        context, pageParameters);

    return new CAItemTypeDeclarationSummarySearch(pages);
  }
}
