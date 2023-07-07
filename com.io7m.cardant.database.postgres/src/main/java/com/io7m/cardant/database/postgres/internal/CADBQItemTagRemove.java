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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.TagRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.TagRemoveType.Parameters;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import org.jooq.DSLContext;

import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_TAGS;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_ID;
import static com.io7m.cardant.strings.CAStringConstants.TAG_ID;
import static com.io7m.cardant.strings.CAStringConstants.TAG_NAME;

/**
 * Remove a tag from an item.
 */

public final class CADBQItemTagRemove
  extends CADBQAbstract<Parameters, CADatabaseUnit>
  implements TagRemoveType
{
  private static final Service<Parameters, CADatabaseUnit, TagRemoveType> SERVICE =
    new Service<>(TagRemoveType.class, CADBQItemTagRemove::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemTagRemove(
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

    final var matchesItem =
      ITEM_TAGS.TAG_ITEM_ID.eq(item.id());
    final var matchesTag =
      ITEM_TAGS.TAG_ID.eq(tag.id().id());

    context.deleteFrom(ITEM_TAGS)
      .where(matchesItem.and(matchesTag))
      .execute();

    return UNIT;
  }
}
