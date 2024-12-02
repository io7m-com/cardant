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
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageSearchType;
import com.io7m.cardant.database.api.CADatabaseTypePackageSearchType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAPage;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.model.type_package.CATypePackageSearchParameters;
import com.io7m.cardant.model.type_package.CATypePackageSummary;
import com.io7m.jqpage.core.JQField;
import com.io7m.jqpage.core.JQKeysetRandomAccessPageDefinition;
import com.io7m.jqpage.core.JQKeysetRandomAccessPagination;
import com.io7m.jqpage.core.JQKeysetRandomAccessPaginationParameters;
import com.io7m.jqpage.core.JQOrder;
import com.io7m.lanark.core.RDottedName;
import com.io7m.verona.core.Version;
import com.io7m.verona.core.VersionQualifier;
import io.opentelemetry.api.trace.Span;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.Optional;

import static com.io7m.cardant.database.postgres.internal.CADatabaseExceptions.handleDatabaseException;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_PACKAGES;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DB_STATEMENT;

/**
 * Search for items.
 */

public final class CADBQTypePackageSearch
  extends CADBQAbstract<CATypePackageSearchParameters, CADatabaseTypePackageSearchType>
  implements TypePackageSearchType
{
  private static final Service<
    CATypePackageSearchParameters,
    CADatabaseTypePackageSearchType,
    TypePackageSearchType> SERVICE =
    new Service<>(TypePackageSearchType.class, CADBQTypePackageSearch::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQTypePackageSearch(
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
  protected CADatabaseTypePackageSearchType onExecute(
    final DSLContext context,
    final CATypePackageSearchParameters parameters)
    throws CADatabaseException
  {
    final Condition descriptionCondition =
      CADBComparisons.createFuzzyMatchQuery(
        this.language(),
        parameters.descriptionMatch(),
        METADATA_TYPE_PACKAGES.MTP_DESCRIPTION,
        "METADATA_TYPE_PACKAGES.MTP_DESCRIPTION_SEARCH"
      );

    final var allConditions =
      DSL.and(descriptionCondition);

    final var pageParameters =
      JQKeysetRandomAccessPaginationParameters.forTable(METADATA_TYPE_PACKAGES)
        .addSortField(new JQField(METADATA_TYPE_PACKAGES.MTP_NAME, JQOrder.ASCENDING))
        .addWhereCondition(allConditions)
        .setPageSize(parameters.pageSize())
        .setStatementListener(statement -> {
          Span.current().setAttribute(DB_STATEMENT, statement.toString());
        }).build();

    final var pages =
      JQKeysetRandomAccessPagination.createPageDefinitions(
        context, pageParameters);

    return new CATypePackageSearch(pages);
  }

  private static final class CATypePackageSearch
    extends CAAbstractSearch<CATypePackageSummary>
    implements CADatabaseTypePackageSearchType
  {
    CATypePackageSearch(
      final List<JQKeysetRandomAccessPageDefinition> inPages)
    {
      super(inPages);
    }

    @Override
    protected CAPage<CATypePackageSummary> page(
      final CADatabaseTransaction transaction,
      final JQKeysetRandomAccessPageDefinition page)
      throws CADatabaseException
    {
      final var context =
        transaction.createContext();
      final var querySpan =
        transaction.createQuerySpan(
          "CATypePackageSearch.page");

      try {
        final var query =
          page.queryFields(context, List.of(
            METADATA_TYPE_PACKAGES.MTP_NAME,
            METADATA_TYPE_PACKAGES.MTP_VERSION_MAJOR,
            METADATA_TYPE_PACKAGES.MTP_VERSION_MINOR,
            METADATA_TYPE_PACKAGES.MTP_VERSION_PATCH,
            METADATA_TYPE_PACKAGES.MTP_VERSION_QUALIFIER,
            METADATA_TYPE_PACKAGES.MTP_DESCRIPTION
          ));

        querySpan.setAttribute(DB_STATEMENT, query.toString());

        final var items =
          query.fetch().map(record -> {
            return new CATypePackageSummary(
              new CATypePackageIdentifier(
                new RDottedName(record.get(METADATA_TYPE_PACKAGES.MTP_NAME)),
                new Version(
                  record.get(METADATA_TYPE_PACKAGES.MTP_VERSION_MAJOR),
                  record.get(METADATA_TYPE_PACKAGES.MTP_VERSION_MINOR),
                  record.get(METADATA_TYPE_PACKAGES.MTP_VERSION_PATCH),
                  Optional.ofNullable(
                    record.get(METADATA_TYPE_PACKAGES.MTP_VERSION_QUALIFIER)
                  ).map(VersionQualifier::new)
                )
              ),
              record.get(METADATA_TYPE_PACKAGES.MTP_DESCRIPTION)
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
