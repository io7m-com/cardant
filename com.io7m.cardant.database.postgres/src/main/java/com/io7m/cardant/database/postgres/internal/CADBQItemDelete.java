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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.DeleteType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAItemID;
import org.jooq.DSLContext;
import org.jooq.Query;

import java.util.ArrayList;
import java.util.Collection;

import static com.io7m.cardant.database.postgres.internal.Tables.ITEMS;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_ATTACHMENTS;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_METADATA;

/**
 * Delete the given items.
 */

public final class CADBQItemDelete
  extends CADBQAbstract<Collection<CAItemID>, CADatabaseUnit>
  implements DeleteType
{
  private static final Service<Collection<CAItemID>, CADatabaseUnit, DeleteType> SERVICE =
    new Service<>(DeleteType.class, CADBQItemDelete::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemDelete(
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
  protected CADatabaseUnit onExecute(
    final DSLContext context,
    final Collection<CAItemID> items)
    throws CADatabaseException
  {
    final var deletes = new ArrayList<Query>(items.size());
    for (final var item : items) {
      deletes.add(
        context.deleteFrom(ITEM_ATTACHMENTS)
          .where(ITEM_ATTACHMENTS.IA_ITEM_ID.eq(item.id()))
      );
      deletes.add(
        context.deleteFrom(ITEM_METADATA)
          .where(ITEM_METADATA.ITEM_META_ITEM.eq(item.id()))
      );
      deletes.add(
        context.deleteFrom(ITEMS)
          .where(ITEMS.ITEM_ID.eq(item.id()))
      );
    }
    context.batch(deletes).execute();
    return CADatabaseUnit.UNIT;
  }
}
