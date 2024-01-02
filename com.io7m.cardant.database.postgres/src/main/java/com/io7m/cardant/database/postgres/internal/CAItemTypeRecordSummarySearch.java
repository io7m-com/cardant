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
import com.io7m.cardant.database.api.CADatabaseTypeRecordSearchType;
import com.io7m.cardant.model.CAPage;
import com.io7m.cardant.model.CATypeRecordSummary;
import com.io7m.jqpage.core.JQKeysetRandomAccessPageDefinition;
import com.io7m.lanark.core.RDottedName;
import org.jooq.exception.DataAccessException;

import java.util.List;

import static com.io7m.cardant.database.postgres.internal.CADBQTypeScalarGet.packageIdentifier;
import static com.io7m.cardant.database.postgres.internal.CADatabaseExceptions.handleDatabaseException;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_RECORDS;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_PACKAGES;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DB_STATEMENT;

final class CAItemTypeRecordSummarySearch
  extends CAAbstractSearch<CATypeRecordSummary>
  implements CADatabaseTypeRecordSearchType
{
  CAItemTypeRecordSummarySearch(
    final List<JQKeysetRandomAccessPageDefinition> inPages)
  {
    super(inPages);
  }

  @Override
  protected CAPage<CATypeRecordSummary> page(
    final CADatabaseTransaction transaction,
    final JQKeysetRandomAccessPageDefinition page)
    throws CADatabaseException
  {
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CAItemTypeDeclarationSummarySearch.page");

    try {
      final var query =
        page.queryFields(
          context,
          List.of(
            METADATA_TYPE_PACKAGES.MTP_NAME,
            METADATA_TYPE_PACKAGES.MTP_VERSION_MAJOR,
            METADATA_TYPE_PACKAGES.MTP_VERSION_MINOR,
            METADATA_TYPE_PACKAGES.MTP_VERSION_PATCH,
            METADATA_TYPE_PACKAGES.MTP_VERSION_QUALIFIER,
            METADATA_TYPES_RECORDS.MTR_NAME,
            METADATA_TYPES_RECORDS.MTR_DESCRIPTION
          )
        );

      querySpan.setAttribute(DB_STATEMENT, query.toString());

      final var items =
        query.fetch().map(record -> {
          return new CATypeRecordSummary(
            packageIdentifier(record),
            new RDottedName(record.get(METADATA_TYPES_RECORDS.MTR_NAME)),
            record.get(METADATA_TYPES_RECORDS.MTR_DESCRIPTION)
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
