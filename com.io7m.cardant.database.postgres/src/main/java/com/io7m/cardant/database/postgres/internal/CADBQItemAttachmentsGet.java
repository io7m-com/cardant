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

import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.AttachmentsGetType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.AttachmentsGetType.Parameters;
import com.io7m.cardant.database.postgres.internal.CADBQItemGet.IncludeAttachments;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAItemAttachment;
import org.jooq.DSLContext;

import java.util.Set;

import static com.io7m.cardant.database.postgres.internal.CADBQItemGet.itemAttachmentsInner;

/**
 * Retrieve attachments for an item.
 */

public final class CADBQItemAttachmentsGet
  extends CADBQAbstract<Parameters, Set<CAItemAttachment>>
  implements AttachmentsGetType
{
  private static final Service<Parameters, Set<CAItemAttachment>, AttachmentsGetType> SERVICE =
    new Service<>(AttachmentsGetType.class, CADBQItemAttachmentsGet::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemAttachmentsGet(
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
  protected Set<CAItemAttachment> onExecute(
    final DSLContext context,
    final Parameters parameters)
  {
    final var withData =
      parameters.withData();
    final var item =
      parameters.item();

    final var includeData =
      withData
        ? IncludeAttachments.ATTACHMENTS_AND_DATA_INCLUDED
        : IncludeAttachments.ATTACHMENTS_INCLUDED;

    return Set.copyOf(
      itemAttachmentsInner(context, item, includeData)
        .values()
    );
  }
}
