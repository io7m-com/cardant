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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.SetNameType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.SetNameType.Parameters;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import org.jooq.DSLContext;

import java.util.Optional;

import static com.io7m.cardant.database.postgres.internal.Tables.ITEMS;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_NONEXISTENT;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_ID;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_NAME;

/**
 * Set the name for the given item.
 */

public final class CADBQItemSetName
  extends CADBQAbstract<Parameters, CADatabaseUnit>
  implements SetNameType
{
  private static final Service<Parameters, CADatabaseUnit, SetNameType> SERVICE =
    new Service<>(SetNameType.class, CADBQItemSetName::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemSetName(
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
    this.setAttribute(ITEM_NAME, parameters.name());

    final var itemRec = context.fetchOne(ITEMS, ITEMS.ITEM_ID.eq(itemID.id()));
    if (itemRec == null) {
      throw new CADatabaseException(
        this.local(ERROR_NONEXISTENT),
        errorNonexistent(),
        this.attributes(),
        Optional.empty()
      );
    }
    itemRec.setItemName(parameters.name());
    itemRec.store();
    return CADatabaseUnit.UNIT;
  }
}
