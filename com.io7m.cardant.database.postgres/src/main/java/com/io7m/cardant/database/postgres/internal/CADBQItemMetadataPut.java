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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.MetadataPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.MetadataPutType.Parameters;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import org.jooq.DSLContext;

import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_METADATA;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_ID;
import static com.io7m.cardant.strings.CAStringConstants.METADATA_NAME;
import static com.io7m.cardant.strings.CAStringConstants.METADATA_VALUE;

/**
 * Add or update metadata for an item.
 */

public final class CADBQItemMetadataPut
  extends CADBQAbstract<Parameters, CADatabaseUnit>
  implements MetadataPutType
{
  private static final Service<Parameters, CADatabaseUnit, MetadataPutType> SERVICE =
    new Service<>(MetadataPutType.class, CADBQItemMetadataPut::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemMetadataPut(
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
    final var item =
      parameters.item();
    final var metadata =
      parameters.metadata();

    this.setAttribute(ITEM_ID, item.displayId());
    this.setAttribute(METADATA_NAME, metadata.name().value());
    this.setAttribute(METADATA_VALUE, metadata.value());

    context.insertInto(ITEM_METADATA)
      .set(ITEM_METADATA.METADATA_ITEM_ID, item.id())
      .set(ITEM_METADATA.METADATA_NAME, metadata.name().value())
      .set(ITEM_METADATA.METADATA_VALUE, metadata.value())
      .onConflict(ITEM_METADATA.METADATA_ITEM_ID, ITEM_METADATA.METADATA_NAME)
      .doUpdate()
      .set(ITEM_METADATA.METADATA_VALUE, metadata.value())
      .execute();

    return UNIT;
  }
}
