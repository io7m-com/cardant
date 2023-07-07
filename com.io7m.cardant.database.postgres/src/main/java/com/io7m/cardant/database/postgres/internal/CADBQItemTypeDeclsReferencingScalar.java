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
import com.io7m.cardant.database.api.CADatabasePagedQueryType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemTypesType.TypeDeclarationsReferencingScalarType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CATypeDeclarationSummary;
import com.io7m.jqpage.core.JQField;
import com.io7m.jqpage.core.JQKeysetRandomAccessPagination;
import com.io7m.jqpage.core.JQOrder;
import com.io7m.lanark.core.RDottedName;
import io.opentelemetry.api.trace.Span;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.impl.DSL;

import java.util.List;

import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_SCALAR_TYPES;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_DECLARATIONS;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_FIELDS;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DB_STATEMENT;

/**
 * Search for type declarations that reference the scalar type with the
 * given name.
 */

public final class CADBQItemTypeDeclsReferencingScalar
  extends CADBQAbstract<RDottedName,
  CADatabasePagedQueryType<CATypeDeclarationSummary>>
  implements TypeDeclarationsReferencingScalarType
{
  private static final Service<
    RDottedName,
    CADatabasePagedQueryType<CATypeDeclarationSummary>,
    TypeDeclarationsReferencingScalarType> SERVICE =
    new Service<>(
      TypeDeclarationsReferencingScalarType.class,
      CADBQItemTypeDeclsReferencingScalar::new
    );

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemTypeDeclsReferencingScalar(
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
  protected CADatabasePagedQueryType<CATypeDeclarationSummary> onExecute(
    final DSLContext context,
    final RDottedName name)
    throws CADatabaseException
  {
    final Table<?> tableSource =
      METADATA_TYPE_DECLARATIONS
        .join(METADATA_TYPE_FIELDS)
        .on(METADATA_TYPE_FIELDS.FIELD_DECLARATION.eq(
          METADATA_TYPE_DECLARATIONS.ID))
        .join(METADATA_SCALAR_TYPES)
        .on(METADATA_SCALAR_TYPES.ID.eq(METADATA_TYPE_FIELDS.FIELD_TYPE));

    final var searchCondition =
      DSL.condition(METADATA_SCALAR_TYPES.NAME.eq(name.value()));

    final var orderField =
      new JQField(METADATA_TYPE_DECLARATIONS.NAME, JQOrder.ASCENDING);

    final var pages =
      JQKeysetRandomAccessPagination.createPageDefinitions(
        context,
        tableSource,
        List.of(orderField),
        List.of(searchCondition),
        List.of(),
        Integer.toUnsignedLong(10),
        st -> Span.current().setAttribute(DB_STATEMENT, st.toString())
      );

    return new CAItemTypeDeclarationSummarySearch(pages);
  }

}
