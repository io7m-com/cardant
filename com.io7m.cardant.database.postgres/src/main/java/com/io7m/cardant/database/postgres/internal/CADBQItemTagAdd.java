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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.TagAddType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.TagAddType.Parameters;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import org.jooq.DSLContext;

import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_TAGS;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_ID;
import static com.io7m.cardant.strings.CAStringConstants.TAG_ID;
import static com.io7m.cardant.strings.CAStringConstants.TAG_NAME;

/**
 * Add a tag to an item.
 */

public final class CADBQItemTagAdd
  extends CADBQAbstract<Parameters, CADatabaseUnit>
  implements TagAddType
{
  private static final Service<Parameters, CADatabaseUnit, TagAddType> SERVICE =
    new Service<>(TagAddType.class, CADBQItemTagAdd::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemTagAdd(
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
    final var item = parameters.item();
    final var tag = parameters.tag();

    this.setAttribute(TAG_ID, tag.id().displayId());
    this.setAttribute(TAG_NAME, tag.name());
    this.setAttribute(ITEM_ID, item.displayId());

    context.insertInto(ITEM_TAGS)
      .set(ITEM_TAGS.TAG_ITEM_ID, item.id())
      .set(ITEM_TAGS.TAG_ID, tag.id().id())
      .onConflictDoNothing()
      .execute();

    return UNIT;
  }
}
