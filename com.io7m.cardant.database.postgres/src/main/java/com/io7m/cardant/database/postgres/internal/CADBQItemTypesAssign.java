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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemTypesAssignType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemTypesAssignType.Parameters;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import org.jooq.DSLContext;
import org.jooq.Query;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Map;

import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd.auditEvent;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_TYPES;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_RECORDS;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_ID;

/**
 * Assign types for the given item.
 */

public final class CADBQItemTypesAssign
  extends CADBQAbstract<Parameters, CADatabaseUnit>
  implements ItemTypesAssignType
{
  private static final Service<Parameters, CADatabaseUnit, ItemTypesAssignType> SERVICE =
    new Service<>(ItemTypesAssignType.class, CADBQItemTypesAssign::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemTypesAssign(
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
    final Parameters parameters)
    throws CADatabaseException
  {
    final var itemID = parameters.item();
    this.setAttribute(ITEM_ID, itemID.displayId());

    final var batches =
      new ArrayList<Query>(parameters.types().size());

    for (final var type : parameters.types()) {
      final var selectType =
        context.select(METADATA_TYPES_RECORDS.MTR_ID)
          .from(METADATA_TYPES_RECORDS)
          .where(METADATA_TYPES_RECORDS.MTR_NAME.eq(type.value()));

      final var query =
        context.insertInto(ITEM_TYPES)
          .set(ITEM_TYPES.IT_ITEM, itemID.id())
          .set(ITEM_TYPES.IT_TYPE, selectType)
          .onConflictDoNothing();

      batches.add(query);
    }

    final var transaction = this.transaction();
    batches.add(
      auditEvent(
        context,
        OffsetDateTime.now(transaction.clock()),
        transaction.userId(),
        "ITEM_TYPES_UPDATED",
        Map.entry("Item", itemID.displayId())
      )
    );

    context.batch(batches).execute();
    return CADatabaseUnit.UNIT;
  }
}
