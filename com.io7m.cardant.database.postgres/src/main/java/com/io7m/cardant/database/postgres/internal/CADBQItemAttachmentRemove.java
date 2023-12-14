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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.AttachmentRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.AttachmentRemoveType.Parameters;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import org.jooq.DSLContext;

import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_ATTACHMENTS;
import static com.io7m.cardant.strings.CAStringConstants.FILE_ID;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_ID;
import static com.io7m.cardant.strings.CAStringConstants.RELATION;

/**
 * Remove an attachment from an item.
 */

public final class CADBQItemAttachmentRemove
  extends CADBQAbstract<Parameters, CADatabaseUnit>
  implements AttachmentRemoveType
{
  private static final Service<Parameters, CADatabaseUnit, AttachmentRemoveType> SERVICE =
    new Service<>(AttachmentRemoveType.class, CADBQItemAttachmentRemove::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemAttachmentRemove(
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
    final var file = parameters.file();
    final var relation = parameters.relation();

    this.setAttribute(ITEM_ID, item.displayId());
    this.setAttribute(FILE_ID, file.displayId());
    this.setAttribute(RELATION, relation);

    final var matchesItem =
      ITEM_ATTACHMENTS.IA_ITEM_ID.eq(item.id());
    final var matchesFile =
      ITEM_ATTACHMENTS.IA_FILE_ID.eq(file.id());
    final var matchesRelation =
      ITEM_ATTACHMENTS.IA_RELATION.eq(relation);
    final var matches =
      matchesItem.and(matchesFile).and(matchesRelation);

    context.deleteFrom(ITEM_ATTACHMENTS)
      .where(matches)
      .execute();

    return UNIT;
  }
}
