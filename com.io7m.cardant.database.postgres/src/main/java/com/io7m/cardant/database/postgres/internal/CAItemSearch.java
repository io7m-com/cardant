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
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CAPage;
import com.io7m.jqpage.core.JQKeysetRandomAccessPageDefinition;
import org.jooq.exception.DataAccessException;

import java.util.List;

import static com.io7m.cardant.database.postgres.internal.CADatabaseExceptions.handleDatabaseException;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEMS;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DB_STATEMENT;

final class CAItemSearch
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
          ITEMS.ITEM_ID,
          ITEMS.ITEM_NAME
        ));

      querySpan.setAttribute(DB_STATEMENT, query.toString());

      final var items =
        query.fetch().map(record -> {
          return new CAItemSummary(
            new CAItemID(record.get(ITEMS.ITEM_ID)),
            record.get(ITEMS.ITEM_NAME)
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
