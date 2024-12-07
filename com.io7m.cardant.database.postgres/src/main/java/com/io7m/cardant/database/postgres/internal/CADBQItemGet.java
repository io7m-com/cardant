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

import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemGetType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAAttachment;
import com.io7m.cardant.model.CAAttachmentKey;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType.CAFileWithoutData;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CATypeRecordFieldIdentifier;
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.lanark.core.RDottedName;
import org.joda.money.CurrencyUnit;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.io7m.cardant.database.postgres.internal.Tables.FILES;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEMS;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_ATTACHMENTS;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_METADATA;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_TYPES;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_PACKAGES;

/**
 * Retrieve the item with the given ID, if one exists.
 */

public final class CADBQItemGet
  extends CADBQAbstract<CAItemID, Optional<CAItem>>
  implements ItemGetType
{
  private static final Service<CAItemID, Optional<CAItem>, ItemGetType> SERVICE =
    new Service<>(ItemGetType.class, CADBQItemGet::new);

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
          ITEMS.ITEM_CREATED,
          ITEMS.ITEM_UPDATED,
          DSL.multisetAgg(
            METADATA_TYPE_PACKAGES.MTP_NAME,
            METADATA_TYPES.MT_NAME
          ),
          ITEM_METADATA.ITEM_META_TYPE_PACKAGE,
          ITEM_METADATA.ITEM_META_TYPE_RECORD,
          ITEM_METADATA.ITEM_META_TYPE_FIELD,
          ITEM_METADATA.ITEM_META_VALUE_INTEGRAL,
          ITEM_METADATA.ITEM_META_VALUE_MONEY,
          ITEM_METADATA.ITEM_META_VALUE_MONEY_CURRENCY,
          ITEM_METADATA.ITEM_META_VALUE_REAL,
          ITEM_METADATA.ITEM_META_VALUE_TEXT,
          ITEM_METADATA.ITEM_META_VALUE_TIME,
          ITEM_METADATA.ITEM_META_VALUE_TYPE,
          ITEM_ATTACHMENTS.IA_RELATION,
          FILES.FILE_ID,
          FILES.FILE_DATA_USED,
          FILES.FILE_DESCRIPTION,
          FILES.FILE_MEDIA_TYPE,
          FILES.FILE_HASH_ALGORITHM,
          FILES.FILE_HASH_VALUE
        ).from(ITEMS)
        .leftJoin(ITEM_TYPES)
        .on(ITEM_TYPES.IT_ITEM.eq(ITEMS.ITEM_ID))
        .leftJoin(METADATA_TYPES)
        .on(METADATA_TYPES.MT_ID.eq(ITEM_TYPES.IT_TYPE))
        .leftJoin(METADATA_TYPE_PACKAGES)
        .on(METADATA_TYPE_PACKAGES.MTP_ID.eq(METADATA_TYPES.MT_PACKAGE))
        .leftJoin(ITEM_METADATA)
        .on(ITEM_METADATA.ITEM_META_ITEM.eq(ITEMS.ITEM_ID))
        .leftJoin(ITEM_ATTACHMENTS)
        .on(ITEM_ATTACHMENTS.IA_ITEM_ID.eq(ITEMS.ITEM_ID))
        .leftJoin(FILES)
        .on(FILES.FILE_ID.eq(ITEM_ATTACHMENTS.IA_FILE_ID))
        .where(ITEMS.ITEM_ID.eq(itemID.id()).and(ITEMS.ITEM_DELETED.isNull()))
        .groupBy(
          ITEMS.ITEM_ID,
          ITEMS.ITEM_NAME,
          ITEMS.ITEM_CREATED,
          ITEMS.ITEM_UPDATED,
          ITEM_METADATA.ITEM_META_TYPE_PACKAGE,
          ITEM_METADATA.ITEM_META_TYPE_RECORD,
          ITEM_METADATA.ITEM_META_TYPE_FIELD,
          ITEM_METADATA.ITEM_META_VALUE_INTEGRAL,
          ITEM_METADATA.ITEM_META_VALUE_MONEY,
          ITEM_METADATA.ITEM_META_VALUE_MONEY_CURRENCY,
          ITEM_METADATA.ITEM_META_VALUE_REAL,
          ITEM_METADATA.ITEM_META_VALUE_TEXT,
          ITEM_METADATA.ITEM_META_VALUE_TIME,
          ITEM_METADATA.ITEM_META_VALUE_TYPE,
          ITEM_ATTACHMENTS.IA_RELATION,
          FILES.FILE_ID,
          FILES.FILE_DATA_USED,
          FILES.FILE_DESCRIPTION,
          FILES.FILE_MEDIA_TYPE,
          FILES.FILE_HASH_ALGORITHM,
          FILES.FILE_HASH_VALUE
        );

    final var results = query.fetch();
    if (results.isEmpty()) {
      return Optional.empty();
    }

    final var meta =
      new TreeMap<CATypeRecordFieldIdentifier, CAMetadataType>();
    final var attachments =
      new TreeMap<CAAttachmentKey, CAAttachment>();

    CAItemID itemId = null;
    String name = null;
    OffsetDateTime created = null;
    OffsetDateTime updated = null;

    for (final var rec : results) {
      itemId = new CAItemID(rec.get(ITEMS.ITEM_ID));
      name = rec.get(ITEMS.ITEM_NAME);
      created = rec.get(ITEMS.ITEM_CREATED);
      updated = rec.get(ITEMS.ITEM_UPDATED);

      final var typePack =
        rec.get(ITEM_METADATA.ITEM_META_TYPE_PACKAGE);

      if (typePack != null) {
        final var typeRec =
          rec.get(ITEM_METADATA.ITEM_META_TYPE_RECORD);
        final var typeField =
          rec.get(ITEM_METADATA.ITEM_META_TYPE_FIELD);

        final var metaName =
          new CATypeRecordFieldIdentifier(
            new CATypeRecordIdentifier(
              new RDottedName(typePack),
              new RDottedName(typeRec)
            ),
            new RDottedName(typeField)
          );

        meta.put(metaName, mapItemMetadataRecord(metaName, rec));
      }

      Optional.ofNullable(rec.get(ITEM_ATTACHMENTS.IA_RELATION))
        .ifPresent(f -> {
          final var attachment = new CAAttachment(
            new CAFileWithoutData(
              new CAFileID(rec.get(FILES.FILE_ID)),
              rec.get(FILES.FILE_DESCRIPTION),
              rec.get(FILES.FILE_MEDIA_TYPE),
              rec.<Long>get(FILES.FILE_DATA_USED).longValue(),
              rec.get(FILES.FILE_HASH_ALGORITHM),
              rec.get(FILES.FILE_HASH_VALUE)
            ),
            rec.get(ITEM_ATTACHMENTS.IA_RELATION)
          );
          attachments.put(attachment.key(), attachment);
        });
    }

    final var types =
      new TreeSet<CATypeRecordIdentifier>();

    final var typeRecords =
      context.select(
          METADATA_TYPES.MT_NAME,
          METADATA_TYPE_PACKAGES.MTP_NAME
        ).from(ITEM_TYPES)
        .join(METADATA_TYPES)
        .on(METADATA_TYPES.MT_ID.eq(ITEM_TYPES.IT_TYPE))
        .join(METADATA_TYPE_PACKAGES)
        .on(METADATA_TYPE_PACKAGES.MTP_ID.eq(METADATA_TYPES.MT_PACKAGE))
        .where(ITEM_TYPES.IT_ITEM.eq(itemID.id()))
        .fetch();

    for (final var typeRecord : typeRecords) {
      types.add(
        new CATypeRecordIdentifier(
          new RDottedName(typeRecord.get(METADATA_TYPE_PACKAGES.MTP_NAME)),
          new RDottedName(typeRecord.get(METADATA_TYPES.MT_NAME))
        )
      );
    }

    return Optional.of(
      new CAItem(
        itemId,
        name,
        created,
        updated,
        meta,
        attachments,
        types
      )
    );
  }

  static CAMetadataType mapItemMetadataRecord(
    final CATypeRecordFieldIdentifier name,
    final Record rec)
  {
    final var typeCode =
      rec.get(ITEM_METADATA.ITEM_META_VALUE_TYPE);

    return switch (typeCode) {
      case SCALAR_INTEGRAL -> {
        yield new CAMetadataType.Integral(
          name,
          rec.get(ITEM_METADATA.ITEM_META_VALUE_INTEGRAL).longValue()
        );
      }
      case SCALAR_REAL -> {
        yield new CAMetadataType.Real(
          name,
          rec.get(ITEM_METADATA.ITEM_META_VALUE_REAL).doubleValue()
        );
      }
      case SCALAR_MONEY -> {
        yield new CAMetadataType.Monetary(
          name,
          rec.get(ITEM_METADATA.ITEM_META_VALUE_MONEY),
          CurrencyUnit.of(rec.get(ITEM_METADATA.ITEM_META_VALUE_MONEY_CURRENCY))
        );
      }
      case SCALAR_TEXT -> {
        yield new CAMetadataType.Text(
          name,
          rec.get(ITEM_METADATA.ITEM_META_VALUE_TEXT)
        );
      }
      case SCALAR_TIME -> {
        yield new CAMetadataType.Time(
          name,
          rec.get(ITEM_METADATA.ITEM_META_VALUE_TIME)
        );
      }
    };
  }
}
