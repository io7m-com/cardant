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
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAPage;
import com.io7m.jqpage.core.JQKeysetRandomAccessPageDefinition;
import org.jooq.exception.DataAccessException;

import java.util.List;

import static com.io7m.cardant.database.postgres.internal.CADatabaseExceptions.handleDatabaseException;
import static com.io7m.cardant.database.postgres.internal.tables.Files.FILES;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DB_STATEMENT;

final class CAFileSearch
  extends CAAbstractSearch<CAFileType.CAFileWithoutData>
  implements CADatabaseFileSearchType
{
  CAFileSearch(
    final List<JQKeysetRandomAccessPageDefinition> inPages)
  {
    super(inPages);
  }

  @Override
  protected CAPage<CAFileType.CAFileWithoutData> page(
    final CADatabaseTransaction transaction,
    final JQKeysetRandomAccessPageDefinition page)
    throws CADatabaseException
  {
    final var context =
      transaction.createContext();

    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesFiles.fileSearch.page");

    try {
      final var query =
        page.queryFields(context, List.of(
          FILES.FILE_DATA_USED,
          FILES.FILE_DESCRIPTION,
          FILES.FILE_HASH_ALGORITHM,
          FILES.FILE_HASH_VALUE,
          FILES.FILE_ID,
          FILES.FILE_MEDIA_TYPE
        ));

      querySpan.setAttribute(DB_STATEMENT, query.toString());

      final var items =
        query.fetch().map(record -> {
          return new CAFileType.CAFileWithoutData(
            new CAFileID(record.get(FILES.FILE_ID)),
            record.get(FILES.FILE_DESCRIPTION),
            record.get(FILES.FILE_MEDIA_TYPE),
            record.<Long>get(FILES.FILE_DATA_USED).longValue(),
            record.get(FILES.FILE_HASH_ALGORITHM),
            record.get(FILES.FILE_HASH_VALUE)
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
