/*
 * Copyright © 2022 Mark Raynsford <code@io7m.com> https://www.io7m.com
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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType;
import com.io7m.cardant.database.postgres.internal.cardant.tables.records.ItemsRecord;
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemAttachmentKey;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemLocations;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CAItemRepositMove;
import com.io7m.cardant.model.CAItemRepositRemove;
import com.io7m.cardant.model.CAItemRepositType;
import com.io7m.cardant.model.CAListLocationBehaviourType;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.UpdateConditionStep;
import org.jooq.exception.DataAccessException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.io7m.cardant.database.postgres.internal.CADatabaseExceptions.handleDatabaseException;
import static com.io7m.cardant.database.postgres.internal.cardant.Tables.FILES;
import static com.io7m.cardant.database.postgres.internal.cardant.Tables.ITEMS;
import static com.io7m.cardant.database.postgres.internal.cardant.Tables.ITEM_ATTACHMENTS;
import static com.io7m.cardant.database.postgres.internal.cardant.Tables.ITEM_METADATA;
import static com.io7m.cardant.database.postgres.internal.cardant.Tables.ITEM_TAGS;
import static com.io7m.cardant.database.postgres.internal.cardant.Tables.TAGS;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorDuplicate;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

final class CADatabaseQueriesItems
  extends CABaseQueries
  implements CADatabaseQueriesItemsType
{
  CADatabaseQueriesItems(
    final CADatabaseTransaction inTransaction)
  {
    super(inTransaction);
  }

  private static Optional<CAItem> itemGetInner(
    final CAItemID id,
    final DSLContext context,
    final IncludeDeleted includeDeleted,
    final IncludeTags includeTags,
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

    final var itemMetadatas =
      switch (includeMetadata) {
        case METADATA_INCLUDED -> {
          yield itemMetadataInner(context, id);
        }
        case METADATA_NOT_INCLUDED -> {
          yield Collections.<String, CAItemMetadata>emptySortedMap();
        }
      };

    final var itemTags =
      switch (includeTags) {
        case TAGS_INCLUDED -> {
          yield itemTagListInner(context, id);
        }
        case TAGS_NOT_INCLUDED -> {
          yield Collections.<CATag>emptySortedSet();
        }
      };

    return Optional.of(new CAItem(
      id,
      itemRec.getItemName(),
      itemRec.getItemCount().longValue(),
      0L,
      itemMetadatas,
      itemAttachments,
      itemTags
    ));
  }

  private static SortedSet<CATag> itemTagListInner(
    final DSLContext context,
    final CAItemID id)
  {
    final var tableSource =
      TAGS.join(ITEM_TAGS).on(TAGS.TAG_ID.eq(ITEM_TAGS.TAG_ITEM_ID));

    final var tags =
      context.select(TAGS.TAG_ID, TAGS.TAG_NAME)
        .from(tableSource)
        .where(ITEM_TAGS.TAG_ITEM_ID.eq(id.id()))
        .fetch();

    final var results = new TreeSet<CATag>();
    for (final var tagRec : tags) {
      results.add(
        new CATag(
          new CATagID(tagRec.get(TAGS.TAG_ID)),
          tagRec.get(TAGS.TAG_NAME)
        )
      );
    }
    return results;
  }

  private static SortedMap<String, CAItemMetadata> itemMetadataInner(
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

    final var results = new TreeMap<String, CAItemMetadata>();
    for (final var metaRec : metadata) {
      final var meta =
        new CAItemMetadata(metaRec.component1(), metaRec.component2());
      results.put(meta.name(), meta);
    }
    return results;
  }

  private static SortedMap<CAItemAttachmentKey, CAItemAttachment>
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

  private static SortedMap<CAItemAttachmentKey, CAItemAttachment>
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

  @Override
  public Optional<CAItem> itemGet(
    final CAItemID id)
    throws CADatabaseException
  {
    Objects.requireNonNull(id, "id");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesItems.itemCreate");

    try {
      return itemGetInner(
        id,
        context,
        IncludeDeleted.DELETED_NOT_INCLUDED,
        IncludeTags.TAGS_INCLUDED,
        IncludeAttachments.ATTACHMENTS_INCLUDED,
        IncludeMetadata.METADATA_INCLUDED
      );
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(transaction, e);
    } finally {
      querySpan.end();
    }
  }

  @Override
  public void itemCreate(
    final CAItemID id)
    throws CADatabaseException
  {
    Objects.requireNonNull(id, "id");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesItems.itemCreate");

    final var errorAttributes = new TreeMap<String, String>();
    errorAttributes.put("Item ID", id.displayId());

    try {
      var itemRec = context.fetchOne(ITEMS, ITEMS.ITEM_ID.eq(id.id()));
      if (itemRec != null) {
        throw new CADatabaseException(
          errorDuplicate(),
          this.messages().format("errorDuplicate"),
          errorAttributes
        );
      }

      itemRec = context.newRecord(ITEMS);
      itemRec.setItemId(id.id());
      itemRec.setItemCount(Long.valueOf(0L));
      itemRec.setItemDeleted(FALSE);
      itemRec.setItemName("");
      itemRec.store();
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(transaction, e, errorAttributes);
    } finally {
      querySpan.end();
    }
  }

  @Override
  public void itemNameSet(
    final CAItemID id,
    final String name)
    throws CADatabaseException
  {
    Objects.requireNonNull(id, "id");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesItems.itemNameSet");

    final var errorAttributes = new TreeMap<String, String>();
    errorAttributes.put("Item ID", id.displayId());
    errorAttributes.put("Item Name", name);

    try {
      final var itemRec = context.fetchOne(ITEMS, ITEMS.ITEM_ID.eq(id.id()));
      if (itemRec == null) {
        throw new CADatabaseException(
          errorNonexistent(),
          this.messages().format("errorNonexistent"),
          errorAttributes
        );
      }
      itemRec.setItemName(name);
      itemRec.store();
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(transaction, e, errorAttributes);
    } finally {
      querySpan.end();
    }
  }

  @Override
  public Set<CAItem> itemList(
    final CAListLocationBehaviourType locationBehaviour)
    throws CADatabaseException
  {
    return null;
  }

  @Override
  public Set<CAItemID> itemListDeleted()
    throws CADatabaseException
  {
    return null;
  }

  @Override
  public void itemsDelete(
    final Collection<CAItemID> items)
    throws CADatabaseException
  {
    Objects.requireNonNull(items, "items");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesItems.itemsDelete");

    try {
      final var deletes = new ArrayList<Query>(items.size());
      for (final var item : items) {
        deletes.add(
          context.deleteFrom(ITEM_ATTACHMENTS)
            .where(ITEM_ATTACHMENTS.ITEM_ID.eq(item.id()))
        );
        deletes.add(
          context.deleteFrom(ITEM_METADATA)
            .where(ITEM_METADATA.METADATA_ITEM_ID.eq(item.id()))
        );
        deletes.add(
          context.deleteFrom(ITEM_TAGS)
            .where(ITEM_TAGS.TAG_ITEM_ID.eq(item.id()))
        );
        deletes.add(
          context.deleteFrom(ITEMS)
            .where(ITEMS.ITEM_ID.eq(item.id()))
        );
      }
      context.batch(deletes).execute();
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(transaction, e);
    } finally {
      querySpan.end();
    }
  }

  @Override
  public void itemsDeleteMarkOnly(
    final Collection<CAItemID> items)
    throws CADatabaseException
  {
    Objects.requireNonNull(items, "items");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesItems.itemsDeleteMarkOnly");

    try {
      final var updates =
        new ArrayList<UpdateConditionStep<ItemsRecord>>(items.size());
      for (final var item : items) {
        updates.add(
          context.update(ITEMS)
            .set(ITEMS.ITEM_DELETED, TRUE)
            .where(ITEMS.ITEM_ID.eq(item.id()))
        );
      }
      context.batch(updates).execute();
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(transaction, e);
    } finally {
      querySpan.end();
    }
  }

  @Override
  public void itemTagAdd(
    final CAItemID item,
    final CATag tag)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");
    Objects.requireNonNull(tag, "tag");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesItems.itemTagAdd");

    try {
      context.insertInto(ITEM_TAGS)
        .set(ITEM_TAGS.TAG_ITEM_ID, item.id())
        .set(ITEM_TAGS.TAG_ID, tag.id().id())
        .onConflictDoNothing()
        .execute();
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(
        transaction,
        e,
        Map.entry("Item ID", item.displayId()),
        Map.entry("Tag ID", tag.displayId()),
        Map.entry("Tag Name", tag.name())
      );
    } finally {
      querySpan.end();
    }
  }

  @Override
  public void itemTagRemove(
    final CAItemID item,
    final CATag tag)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");
    Objects.requireNonNull(tag, "tag");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesItems.itemTagRemove");

    try {
      final var matchesItem =
        ITEM_TAGS.TAG_ITEM_ID.eq(item.id());
      final var matchesTag =
        ITEM_TAGS.TAG_ID.eq(tag.id().id());

      context.deleteFrom(ITEM_TAGS)
        .where(matchesItem.and(matchesTag))
        .execute();
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(
        transaction,
        e,
        Map.entry("Item ID", item.displayId()),
        Map.entry("Tag ID", tag.displayId()),
        Map.entry("Tag Name", tag.name())
      );
    } finally {
      querySpan.end();
    }
  }

  @Override
  public Set<CATag> itemTagList(
    final CAItemID item)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesItems.itemTagList");

    try {
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
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(
        transaction,
        e,
        Map.entry("Item ID", item.displayId())
      );
    } finally {
      querySpan.end();
    }
  }

  @Override
  public void itemMetadataPut(
    final CAItemID item,
    final CAItemMetadata metadata)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");
    Objects.requireNonNull(metadata, "metadata");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesItems.itemMetadataPut");

    try {
      context.insertInto(ITEM_METADATA)
        .set(ITEM_METADATA.METADATA_ITEM_ID, item.id())
        .set(ITEM_METADATA.METADATA_NAME, metadata.name())
        .set(ITEM_METADATA.METADATA_VALUE, metadata.value())
        .onDuplicateKeyUpdate()
        .set(ITEM_METADATA.METADATA_VALUE, metadata.value())
        .execute();
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(
        transaction,
        e,
        Map.entry("Item ID", item.displayId()),
        Map.entry("Metadata Name", metadata.name()),
        Map.entry("Metadata Value", metadata.value())
      );
    } finally {
      querySpan.end();
    }
  }

  @Override
  public SortedMap<String, CAItemMetadata> itemMetadata(
    final CAItemID item)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesItems.itemMetadata");

    try {
      final var results = new TreeMap<String, CAItemMetadata>();
      context.select(ITEM_METADATA.METADATA_NAME, ITEM_METADATA.METADATA_VALUE)
        .from(ITEM_METADATA)
        .where(ITEM_METADATA.METADATA_ITEM_ID.eq(item.id()))
        .stream()
        .forEach(r -> {
          final var meta = new CAItemMetadata(
            r.get(ITEM_METADATA.METADATA_NAME),
            r.get(ITEM_METADATA.METADATA_VALUE)
          );
          results.put(meta.name(), meta);
        });
      return results;
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(
        transaction,
        e,
        Map.entry("Item ID", item.displayId())
      );
    } finally {
      querySpan.end();
    }
  }

  @Override
  public void itemMetadataRemove(
    final CAItemID item,
    final String name)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesItems.itemMetadataRemove");

    try {
      final var matchesItem =
        ITEM_METADATA.METADATA_ITEM_ID.eq(item.id());
      final var matchesName =
        ITEM_METADATA.METADATA_NAME.eq(name);
      final var matches =
        matchesItem.and(matchesName);

      context.deleteFrom(ITEM_METADATA)
        .where(matches)
        .execute();
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(
        transaction,
        e,
        Map.entry("Item ID", item.displayId()),
        Map.entry("Metadata Name", name)
      );
    } finally {
      querySpan.end();
    }
  }

  @Override
  public void itemAttachmentAdd(
    final CAItemID item,
    final CAFileID file,
    final String relation)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");
    Objects.requireNonNull(file, "file");
    Objects.requireNonNull(relation, "relation");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesItems.itemAttachmentAdd");

    try {
      context.insertInto(ITEM_ATTACHMENTS)
        .set(ITEM_ATTACHMENTS.ITEM_ID, item.id())
        .set(ITEM_ATTACHMENTS.FILE_ID, file.id())
        .set(ITEM_ATTACHMENTS.RELATION, relation)
        .onDuplicateKeyUpdate()
        .set(ITEM_ATTACHMENTS.FILE_ID, file.id())
        .set(ITEM_ATTACHMENTS.RELATION, relation)
        .execute();
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(
        transaction,
        e,
        Map.entry("Item ID", item.displayId()),
        Map.entry("File ID", file.displayId()),
        Map.entry("Relation", relation)
      );
    } finally {
      querySpan.end();
    }
  }

  @Override
  public void itemAttachmentRemove(
    final CAItemID item,
    final CAFileID file,
    final String relation)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");
    Objects.requireNonNull(file, "file");
    Objects.requireNonNull(relation, "relation");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesItems.itemAttachmentRemove");

    try {
      final var matchesItem =
        ITEM_ATTACHMENTS.ITEM_ID.eq(item.id());
      final var matchesFile =
        ITEM_ATTACHMENTS.FILE_ID.eq(file.id());
      final var matchesRelation =
        ITEM_ATTACHMENTS.RELATION.eq(relation);
      final var matches =
        matchesItem.and(matchesFile).and(matchesRelation);

      context.deleteFrom(ITEM_ATTACHMENTS)
        .where(matches)
        .execute();
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(
        transaction,
        e,
        Map.entry("Item ID", item.displayId()),
        Map.entry("File ID", file.displayId()),
        Map.entry("Relation", relation)
      );
    } finally {
      querySpan.end();
    }
  }

  @Override
  public Set<CAItemAttachment> itemAttachments(
    final CAItemID item,
    final boolean withData)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesItems.itemAttachments");

    try {
      final var includeData =
        withData
          ? IncludeAttachments.ATTACHMENTS_AND_DATA_INCLUDED
          : IncludeAttachments.ATTACHMENTS_INCLUDED;

      return Set.copyOf(
        itemAttachmentsInner(context, item, includeData)
          .values()
      );
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(
        transaction,
        e,
        Map.entry("Item ID", item.displayId())
      );
    } finally {
      querySpan.end();
    }
  }

  @Override
  public void itemReposit(
    final CAItemRepositType reposit)
    throws CADatabaseException
  {
    Objects.requireNonNull(reposit, "reposit");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesItems.itemReposit");

    try {
      if (reposit instanceof final CAItemRepositAdd add) {
        this.itemRepositAdd(context, add);
        return;
      }
      if (reposit instanceof final CAItemRepositMove move) {
        this.itemRepositMove(context, move);
        return;
      }
      if (reposit instanceof final CAItemRepositRemove remove) {
        this.itemRepositRemove(context, remove);
        return;
      }
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(
        transaction,
        e,
        Map.entry("Item ID", reposit.item().displayId())
      );
    } finally {
      querySpan.end();
    }
  }

  private void itemRepositRemove(
    final DSLContext context,
    final CAItemRepositRemove remove)
  {

  }

  private void itemRepositMove(
    final DSLContext context,
    final CAItemRepositMove move)
  {

  }

  private void itemRepositAdd(
    final DSLContext context,
    final CAItemRepositAdd add)
  {

  }

  @Override
  public CAItemLocations itemLocations(
    final CAItemID item)
    throws CADatabaseException
  {
    return null;
  }

  private enum IncludeTags
  {
    TAGS_INCLUDED,
    TAGS_NOT_INCLUDED
  }

  private enum IncludeDeleted
  {
    DELETED_INCLUDED,
    DELETED_NOT_INCLUDED
  }

  private enum IncludeAttachments
  {
    ATTACHMENTS_INCLUDED,
    ATTACHMENTS_AND_DATA_INCLUDED,
    ATTACHMENTS_NOT_INCLUDED
  }

  private enum IncludeMetadata
  {
    METADATA_INCLUDED,
    METADATA_NOT_INCLUDED
  }
}
