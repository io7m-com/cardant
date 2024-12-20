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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemAttachmentAddType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemAttachmentAddType.Parameters;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import org.jooq.DSLContext;

import java.time.OffsetDateTime;
import java.util.Map;

import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd.auditEvent;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEMS;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_ATTACHMENTS;
import static com.io7m.cardant.strings.CAStringConstants.FILE_ID;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_ID;
import static com.io7m.cardant.strings.CAStringConstants.RELATION;

/**
 * Add an attachment to an item.
 */

public final class CADBQItemAttachmentAdd
  extends CADBQAbstract<Parameters, CADatabaseUnit>
  implements ItemAttachmentAddType
{
  private static final Service<Parameters, CADatabaseUnit, ItemAttachmentAddType> SERVICE =
    new Service<>(ItemAttachmentAddType.class, CADBQItemAttachmentAdd::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemAttachmentAdd(
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

    context.insertInto(ITEM_ATTACHMENTS)
      .set(ITEM_ATTACHMENTS.IA_ITEM_ID, item.id())
      .set(ITEM_ATTACHMENTS.IA_FILE_ID, file.id())
      .set(ITEM_ATTACHMENTS.IA_RELATION, relation)
      .onDuplicateKeyUpdate()
      .set(ITEM_ATTACHMENTS.IA_FILE_ID, file.id())
      .set(ITEM_ATTACHMENTS.IA_RELATION, relation)
      .execute();

    context.update(ITEMS)
      .set(ITEMS.ITEM_UPDATED, this.now())
      .where(ITEMS.ITEM_ID.eq(item.id()))
      .execute();

    final var transaction = this.transaction();
    auditEvent(
      context,
      OffsetDateTime.now(transaction.clock()),
      transaction.userId(),
      "ITEM_ATTACHMENT_ADDED",
      Map.entry("Item", item.displayId()),
      Map.entry("File", file.displayId())
    ).execute();

    return UNIT;
  }
}
