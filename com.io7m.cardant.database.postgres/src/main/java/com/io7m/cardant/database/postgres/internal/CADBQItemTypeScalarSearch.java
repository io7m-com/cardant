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

import com.io7m.cardant.database.api.CADatabasePagedQueryType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemTypesType.TypeScalarSearchType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CATypeScalar;
import com.io7m.jqpage.core.JQField;
import com.io7m.jqpage.core.JQKeysetRandomAccessPagination;
import com.io7m.jqpage.core.JQOrder;
import io.opentelemetry.api.trace.Span;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.List;

import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_SCALAR_TYPES;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DB_STATEMENT;

/**
 * Search for scalar type declarations.
 */

public final class CADBQItemTypeScalarSearch
  extends CADBQAbstract<String, CADatabasePagedQueryType<CATypeScalar>>
  implements TypeScalarSearchType
{
  private static final Service<String, CADatabasePagedQueryType<CATypeScalar>, TypeScalarSearchType> SERVICE =
    new Service<>(TypeScalarSearchType.class, CADBQItemTypeScalarSearch::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemTypeScalarSearch(
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
  protected CADatabasePagedQueryType<CATypeScalar> onExecute(
    final DSLContext context,
    final String query)
  {
    final var searchCondition =
      DSL.condition(
        "METADATA_SCALAR_TYPES.description_search @@ websearch_to_tsquery(?)",
        DSL.inline(query)
      );

    final var orderField =
      new JQField(METADATA_SCALAR_TYPES.NAME, JQOrder.ASCENDING);

    final var pages =
      JQKeysetRandomAccessPagination.createPageDefinitions(
        context,
        METADATA_SCALAR_TYPES,
        List.of(orderField),
        List.of(searchCondition),
        List.of(),
        Integer.toUnsignedLong(10),
        st -> Span.current().setAttribute(DB_STATEMENT, st.toString())
      );

    return new CAItemTypeScalarSearch(pages);
  }
}
