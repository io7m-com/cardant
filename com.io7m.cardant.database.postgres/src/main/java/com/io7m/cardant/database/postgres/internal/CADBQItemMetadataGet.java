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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.MetadataGetType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.lanark.core.RDottedName;
import org.jooq.DSLContext;

import java.util.SortedMap;
import java.util.TreeMap;

import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_METADATA;

/**
 * Retrieve metadata for an item.
 */

public final class CADBQItemMetadataGet
  extends CADBQAbstract<CAItemID, SortedMap<RDottedName, CAItemMetadata>>
  implements MetadataGetType
{
  private static final Service<CAItemID, SortedMap<RDottedName, CAItemMetadata>, MetadataGetType> SERVICE =
    new Service<>(MetadataGetType.class, CADBQItemMetadataGet::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemMetadataGet(
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
  protected SortedMap<RDottedName, CAItemMetadata> onExecute(
    final DSLContext context,
    final CAItemID itemID)
    throws CADatabaseException
  {
    final var results = new TreeMap<RDottedName, CAItemMetadata>();
    context.select(ITEM_METADATA.METADATA_NAME, ITEM_METADATA.METADATA_VALUE)
      .from(ITEM_METADATA)
      .where(ITEM_METADATA.METADATA_ITEM_ID.eq(itemID.id()))
      .stream()
      .forEach(r -> {
        final var meta = CAItemMetadata.of(
          r.get(ITEM_METADATA.METADATA_NAME),
          r.get(ITEM_METADATA.METADATA_VALUE)
        );
        results.put(meta.name(), meta);
      });
    return results;
  }
}
