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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.TagListType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;
import org.jooq.DSLContext;

import java.util.Set;
import java.util.stream.Collectors;

import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_TAGS;
import static com.io7m.cardant.database.postgres.internal.Tables.TAGS;

/**
 * List tags on an item.
 */

public final class CADBQItemTagList
  extends CADBQAbstract<CAItemID, Set<CATag>>
  implements TagListType
{
  private static final Service<CAItemID, Set<CATag>, TagListType> SERVICE =
    new Service<>(TagListType.class, CADBQItemTagList::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemTagList(
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
  protected Set<CATag> onExecute(
    final DSLContext context,
    final CAItemID item)
    throws CADatabaseException
  {
    final var tableSource =
      TAGS.join(ITEM_TAGS).on(TAGS.TAG_ID.eq(ITEM_TAGS.TAG_ID));
    final var tagMatchesItem =
      ITEM_TAGS.TAG_ITEM_ID.eq(item.id());

    return context.select(TAGS.TAG_ID, TAGS.TAG_NAME)
      .from(tableSource)
      .where(tagMatchesItem)
      .stream()
      .map(r -> {
        return new CATag(
          new CATagID(r.get(TAGS.TAG_ID)),
          r.get(TAGS.TAG_NAME)
        );
      })
      .collect(Collectors.toUnmodifiableSet());
  }
}
