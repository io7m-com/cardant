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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.TypesRevokeType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.TypesRevokeType.Parameters;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import org.jooq.DSLContext;
import org.jooq.Query;

import java.util.ArrayList;

import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_TYPES;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_DECLARATIONS;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_ID;

/**
 * Revoke types for the given item.
 */

public final class CADBQItemTypesRevoke
  extends CADBQAbstract<Parameters, CADatabaseUnit>
  implements TypesRevokeType
{
  private static final Service<Parameters, CADatabaseUnit, TypesRevokeType> SERVICE =
    new Service<>(TypesRevokeType.class, CADBQItemTypesRevoke::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemTypesRevoke(
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
        context.select(METADATA_TYPE_DECLARATIONS.ID)
          .from(METADATA_TYPE_DECLARATIONS)
          .where(METADATA_TYPE_DECLARATIONS.NAME.eq(type.value()));

      final var query =
        context.deleteFrom(ITEM_TYPES)
          .where(ITEM_TYPES.ITEM.eq(itemID.id())
                   .and(ITEM_TYPES.TYPE_DECLARATION.eq(selectType)));

      batches.add(query);
    }

    context.batch(batches).execute();
    return CADatabaseUnit.UNIT;
  }
}
