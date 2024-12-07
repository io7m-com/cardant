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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemCreateType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.model.CAItemID;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.util.Map;

import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd.auditEvent;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEMS;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_ID;

/**
 * Create an item.
 */

public final class CADBQItemCreate
  extends CADBQAbstract<CAItemID, CADatabaseUnit>
  implements ItemCreateType
{
  private static final CADBQueryProviderType.Service<CAItemID, CADatabaseUnit, ItemCreateType> SERVICE =
    new CADBQueryProviderType.Service<>(ItemCreateType.class, CADBQItemCreate::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemCreate(
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
    final CAItemID itemID)
    throws CADatabaseException
  {
    this.setAttribute(ITEM_ID, itemID.displayId());

    context.insertInto(ITEMS)
      .set(ITEMS.ITEM_ID, itemID.id())
      .set(ITEMS.ITEM_NAME, "")
      .set(ITEMS.ITEM_CREATED, this.now())
      .set(ITEMS.ITEM_UPDATED, this.now())
      .execute();

    final var transaction = this.transaction();
    auditEvent(
      context,
      OffsetDateTime.now(transaction.clock()),
      transaction.userId(),
      "ITEM_CREATED",
      Map.entry("Item", itemID.displayId())
    ).execute();

    return UNIT;
  }
}
