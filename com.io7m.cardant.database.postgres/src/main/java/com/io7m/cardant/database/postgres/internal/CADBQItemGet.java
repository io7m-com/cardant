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
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemAttachmentKey;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.lanark.core.RDottedName;
import org.jooq.DSLContext;

import java.util.Collections;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.io7m.cardant.database.postgres.internal.CADBQItemReposit.itemCount;
import static com.io7m.cardant.database.postgres.internal.Tables.FILES;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEMS;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_ATTACHMENTS;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_METADATA;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_TYPES;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_DECLARATIONS;
import static java.lang.Boolean.FALSE;

/**
 * Retrieve the item with the given ID, if one exists.
 */

public final class CADBQItemGet
  extends CADBQAbstract<CAItemID, Optional<CAItem>>
  implements GetType
{
  private static final Service<CAItemID, Optional<CAItem>, GetType> SERVICE =
    new Service<>(GetType.class, CADBQItemGet::new);

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
    return itemGetInner(
      itemID,
      context,
      IncludeDeleted.DELETED_NOT_INCLUDED,
      IncludeAttachments.ATTACHMENTS_INCLUDED,
      IncludeMetadata.METADATA_INCLUDED
    );
  }

  private static Optional<CAItem> itemGetInner(
    final CAItemID id,
    final DSLContext context,
    final IncludeDeleted includeDeleted,
    final IncludeAttachments includeAttachments,
    final IncludeMetadata includeMetadata)
  {
    final var itemRec =
      switch (includeDeleted) {
        case DELETED_INCLUDED -> context.fetchOne(
          ITEMS,
          ITEMS.ITEM_ID.eq(id.id())
        );
        case DELETED_NOT_INCLUDED -> context.fetchOne(
          ITEMS,
          ITEMS.ITEM_ID.eq(id.id()).and(ITEMS.ITEM_DELETED.eq(FALSE))
        );
      };

    if (itemRec == null) {
      return Optional.empty();
    }

    final var itemAttachments =
      itemAttachmentsInner(context, id, includeAttachments);

    final SortedMap<RDottedName, CAItemMetadata> itemMetadatas =
      switch (includeMetadata) {
        case METADATA_INCLUDED -> itemMetadataInner(context, id);
        case METADATA_NOT_INCLUDED -> Collections.emptySortedMap();
      };

    final var itemTypes =
      itemTypeListInner(context, id);

    final var count =
      itemCount(context, id);

    return Optional.of(new CAItem(
      id,
      itemRec.getItemName(),
      count,
      0L,
      itemMetadatas,
      itemAttachments,
      itemTypes
    ));
  }

  private static TreeSet<RDottedName> itemTypeListInner(
    final DSLContext context,
    final CAItemID id)
  {
    final var results =
      context.select(METADATA_TYPE_DECLARATIONS.NAME)
        .from(METADATA_TYPE_DECLARATIONS)
        .join(ITEM_TYPES)
        .on(ITEM_TYPES.ITEM.eq(id.id()))
        .fetch();

    final var names = new TreeSet<RDottedName>();
    for (final var result : results) {
      names.add(new RDottedName(result.get(METADATA_TYPE_DECLARATIONS.NAME)));
    }
    return names;
  }

  private static SortedMap<RDottedName, CAItemMetadata> itemMetadataInner(
    final DSLContext context,
    final CAItemID id)
  {
    final var tableSource =
      ITEM_METADATA.join(ITEMS)
        .on(ITEM_METADATA.METADATA_ITEM_ID.eq(ITEMS.ITEM_ID));

    final var metadata =
      context.select(
          ITEM_METADATA.METADATA_NAME,
          ITEM_METADATA.METADATA_VALUE)
        .from(tableSource)
        .where(ITEM_METADATA.METADATA_ITEM_ID.eq(id.id()))
        .fetch();

    final var results = new TreeMap<RDottedName, CAItemMetadata>();
    for (final var metaRec : metadata) {
      final var meta =
        CAItemMetadata.of(metaRec.component1(), metaRec.component2());
      results.put(meta.name(), meta);
    }
    return results;
  }

  static SortedMap<CAItemAttachmentKey, CAItemAttachment>
  itemAttachmentsInner(
    final DSLContext context,
    final CAItemID id,
    final IncludeAttachments includeAttachments)
  {
    return switch (includeAttachments) {
      case ATTACHMENTS_INCLUDED -> {
        yield itemAttachmentsInnerWithoutData(context, id);
      }
      case ATTACHMENTS_AND_DATA_INCLUDED -> {
        yield itemAttachmentsInnerWithData(context, id);
      }
      case ATTACHMENTS_NOT_INCLUDED -> {
        yield Collections.emptySortedMap();
      }
    };
  }

  static SortedMap<CAItemAttachmentKey, CAItemAttachment>
  itemAttachmentsInnerWithData(
    final DSLContext context,
    final CAItemID id)
  {
    final var tableSource =
      ITEM_ATTACHMENTS
        .join(ITEMS)
        .on(ITEM_ATTACHMENTS.ITEM_ID.eq(ITEMS.ITEM_ID))
        .join(FILES)
        .on(FILES.ID.eq(ITEM_ATTACHMENTS.FILE_ID));

    final var attachments =
      context.select(
          ITEM_ATTACHMENTS.FILE_ID,
          ITEM_ATTACHMENTS.RELATION,
          FILES.ID,
          FILES.DESCRIPTION,
          FILES.MEDIA_TYPE,
          FILES.HASH_ALGORITHM,
          FILES.HASH_VALUE,
          FILES.DATA,
          FILES.DATA_USED
        )
        .from(tableSource)
        .where(ITEM_ATTACHMENTS.ITEM_ID.eq(id.id()))
        .fetch();

    final var results =
      new TreeMap<CAItemAttachmentKey, CAItemAttachment>();

    for (final var attRec : attachments) {
      final var fileId =
        new CAFileID(attRec.get(FILES.ID));
      final var relation =
        attRec.get(ITEM_ATTACHMENTS.RELATION);

      final var itemAttachment =
        new CAItemAttachment(
          new CAFileType.CAFileWithData(
            fileId,
            attRec.get(FILES.DESCRIPTION),
            attRec.get(FILES.MEDIA_TYPE),
            attRec.<Long>get(FILES.DATA_USED).longValue(),
            attRec.get(FILES.HASH_ALGORITHM),
            attRec.get(FILES.HASH_VALUE),
            new CAByteArray(attRec.get(FILES.DATA))
          ),
          relation
        );

      results.put(
        new CAItemAttachmentKey(fileId, relation),
        itemAttachment
      );
    }

    return results;
  }

  private static SortedMap<CAItemAttachmentKey, CAItemAttachment>
  itemAttachmentsInnerWithoutData(
    final DSLContext context,
    final CAItemID id)
  {
    final var tableSource =
      ITEM_ATTACHMENTS
        .join(ITEMS)
        .on(ITEM_ATTACHMENTS.ITEM_ID.eq(ITEMS.ITEM_ID))
        .join(FILES)
        .on(FILES.ID.eq(ITEM_ATTACHMENTS.FILE_ID));

    final var attachments =
      context.select(
          ITEM_ATTACHMENTS.FILE_ID,
          ITEM_ATTACHMENTS.RELATION,
          FILES.ID,
          FILES.DESCRIPTION,
          FILES.MEDIA_TYPE,
          FILES.HASH_ALGORITHM,
          FILES.HASH_VALUE,
          FILES.DATA_USED
        )
        .from(tableSource)
        .where(ITEM_ATTACHMENTS.ITEM_ID.eq(id.id()))
        .fetch();

    final var results =
      new TreeMap<CAItemAttachmentKey, CAItemAttachment>();

    for (final var attRec : attachments) {
      final var fileId =
        new CAFileID(attRec.get(FILES.ID));
      final var relation =
        attRec.get(ITEM_ATTACHMENTS.RELATION);

      final var itemAttachment =
        new CAItemAttachment(
          new CAFileType.CAFileWithoutData(
            fileId,
            attRec.get(FILES.DESCRIPTION),
            attRec.get(FILES.MEDIA_TYPE),
            attRec.<Long>get(FILES.DATA_USED).longValue(),
            attRec.get(FILES.HASH_ALGORITHM),
            attRec.get(FILES.HASH_VALUE)
          ),
          relation
        );

      results.put(
        new CAItemAttachmentKey(fileId, relation),
        itemAttachment
      );
    }

    return results;
  }


  enum IncludeTags
  {
    TAGS_INCLUDED,
    TAGS_NOT_INCLUDED
  }

  enum IncludeDeleted
  {
    DELETED_INCLUDED,
    DELETED_NOT_INCLUDED
  }

  enum IncludeAttachments
  {
    ATTACHMENTS_INCLUDED,
    ATTACHMENTS_AND_DATA_INCLUDED,
    ATTACHMENTS_NOT_INCLUDED
  }

  enum IncludeMetadata
  {
    METADATA_INCLUDED,
    METADATA_NOT_INCLUDED
  }
}
