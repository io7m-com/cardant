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

import com.io7m.cardant.database.api.CADatabaseQueriesTagsType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CATag;
import org.jooq.DSLContext;

import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
import static com.io7m.cardant.database.postgres.internal.tables.Tags.TAGS;
import static com.io7m.cardant.strings.CAStringConstants.TAG;

/**
 * Create tags.
 */

public final class CADBQTagPut
  extends CADBQAbstract<CATag, CADatabaseUnit>
  implements CADatabaseQueriesTagsType.PutType
{
  private static final Service<CATag, CADatabaseUnit, PutType> SERVICE =
    new Service<>(PutType.class, CADBQTagPut::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQTagPut(
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
    final CATag tag)
  {
    this.setAttribute(TAG, tag.displayId());

    final var tagId = tag.id().id();
    final var tagName = tag.name();
    context.insertInto(TAGS)
      .columns(TAGS.TAG_ID, TAGS.TAG_NAME)
      .values(tagId, tagName)
      .onDuplicateKeyUpdate()
      .set(TAGS.TAG_ID, tagId)
      .set(TAGS.TAG_NAME, tagName)
      .execute();

    return UNIT;
  }
}
