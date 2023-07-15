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

import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.GetType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAAttachment;
import com.io7m.cardant.model.CAAttachmentKey;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType.CAFileWithoutData;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAMetadata;
import com.io7m.lanark.core.RDottedName;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;

import static com.io7m.cardant.database.postgres.internal.Tables.FILES;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEMS;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_ATTACHMENTS;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_LOCATIONS_SUMMED;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_METADATA;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_TYPES;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_DECLARATIONS;

/**
 * Retrieve the item with the given ID, if one exists.
 */

public final class CADBQItemGet
  extends CADBQAbstract<CAItemID, Optional<CAItem>>
  implements GetType
{
  private static final Service<CAItemID, Optional<CAItem>, GetType> SERVICE =
    new Service<>(GetType.class, CADBQItemGet::new);

  private static final Long ZERO =
    Long.valueOf(0L);

  private static final Name ITEM_COUNT_NAME =
    DSL.name("ITEM_COUNT");

  private static final Name ITEM_TYPES_NAME =
    DSL.name("ITEM_TYPES");

  private static final Field<String[]> ITEM_TYPES_FIELD =
    DSL.array(DSL.field(ITEM_TYPES_NAME, SQLDataType.VARCHAR));

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemGet(
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
  protected Optional<CAItem> onExecute(
    final DSLContext context,
    final CAItemID itemID)
  {
    final var query =
      context.select(
          ITEMS.ITEM_ID,
          ITEMS.ITEM_NAME,
          DSL.coalesce(ITEM_LOCATIONS_SUMMED.ITEM_COUNT, ZERO)
            .as(ITEM_COUNT_NAME),
          DSL.arrayAgg(METADATA_TYPE_DECLARATIONS.NAME)
            .as(ITEM_TYPES_NAME),
          ITEM_METADATA.METADATA_NAME,
          ITEM_METADATA.METADATA_VALUE,
          ITEM_ATTACHMENTS.RELATION,
          FILES.ID,
          FILES.DATA_USED,
          FILES.DESCRIPTION,
          FILES.MEDIA_TYPE,
          FILES.HASH_ALGORITHM,
          FILES.HASH_VALUE
        ).from(ITEMS)
        .leftJoin(ITEM_LOCATIONS_SUMMED)
        .on(ITEM_LOCATIONS_SUMMED.ITEM_ID.eq(ITEMS.ITEM_ID))
        .leftJoin(ITEM_TYPES)
        .on(ITEM_TYPES.ITEM.eq(ITEMS.ITEM_ID))
        .leftJoin(METADATA_TYPE_DECLARATIONS)
        .on(METADATA_TYPE_DECLARATIONS.ID.eq(ITEM_TYPES.TYPE_DECLARATION))
        .leftJoin(ITEM_METADATA)
        .on(ITEM_METADATA.METADATA_ITEM_ID.eq(ITEMS.ITEM_ID))
        .leftJoin(ITEM_ATTACHMENTS)
        .on(ITEM_ATTACHMENTS.ITEM_ID.eq(ITEMS.ITEM_ID))
        .leftJoin(FILES)
        .on(FILES.ID.eq(ITEM_ATTACHMENTS.FILE_ID))
        .where(ITEMS.ITEM_ID.eq(itemID.id()).and(ITEMS.ITEM_DELETED.isFalse()))
        .groupBy(
          ITEMS.ITEM_ID,
          ITEMS.ITEM_NAME,
          DSL.field(ITEM_COUNT_NAME),
          ITEM_METADATA.METADATA_NAME,
          ITEM_METADATA.METADATA_VALUE,
          ITEM_ATTACHMENTS.RELATION,
          FILES.ID,
          FILES.DATA_USED,
          FILES.DESCRIPTION,
          FILES.MEDIA_TYPE,
          FILES.HASH_ALGORITHM,
          FILES.HASH_VALUE
        );

    final var results = query.fetch();
    if (results.isEmpty()) {
      return Optional.empty();
    }

    final var meta =
      new TreeMap<RDottedName, CAMetadata>();
    final var types =
      new TreeSet<RDottedName>();
    final var attachments =
      new TreeMap<CAAttachmentKey, CAAttachment>();

    CAItemID itemId = null;
    String name = null;
    long count = ZERO;

    for (final var rec : results) {
      itemId = new CAItemID(rec.get(ITEMS.ITEM_ID));
      name = rec.get(ITEMS.ITEM_NAME);
      count = rec.get(ITEM_LOCATIONS_SUMMED.ITEM_COUNT).longValue();

      Optional.ofNullable(rec.get(ITEM_METADATA.METADATA_NAME))
        .ifPresent(s -> {
          final var metaName =
            new RDottedName(s);
          final var metaValue =
            rec.get(ITEM_METADATA.METADATA_VALUE);
          meta.put(metaName, new CAMetadata(metaName, metaValue));
        });

      Optional.ofNullable(rec.get(ITEM_ATTACHMENTS.RELATION))
        .ifPresent(f -> {
          final var attachment = new CAAttachment(
            new CAFileWithoutData(
              new CAFileID(rec.get(FILES.ID)),
              rec.get(FILES.DESCRIPTION),
              rec.get(FILES.MEDIA_TYPE),
              rec.<Long>get(FILES.DATA_USED).longValue(),
              rec.get(FILES.HASH_ALGORITHM),
              rec.get(FILES.HASH_VALUE)
            ),
            rec.get(ITEM_ATTACHMENTS.RELATION)
          );
          attachments.put(attachment.key(), attachment);
        });

      types.addAll(
        Stream.of(rec.get(ITEM_TYPES_NAME, String[].class))
          .filter(Objects::nonNull)
          .map(RDottedName::new)
          .toList()
      );
    }

    return Optional.of(
      new CAItem(
        itemId,
        name,
        count,
        ZERO,
        meta,
        attachments,
        types
      )
    );
  }
}
