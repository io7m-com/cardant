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
import com.io7m.cardant.database.api.CADatabaseItemSearchType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType;
import com.io7m.cardant.database.postgres.internal.tables.records.ItemsRecord;
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemAttachmentKey;
import com.io7m.cardant.model.CAItemColumnOrdering;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemLocations;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CAItemRepositMove;
import com.io7m.cardant.model.CAItemRepositRemove;
import com.io7m.cardant.model.CAItemRepositType;
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CAListLocationBehaviourType;
import com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationExact;
import com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationWithDescendants;
import com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationsAll;
import com.io7m.cardant.model.CAPage;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;
import com.io7m.jqpage.core.JQField;
import com.io7m.jqpage.core.JQKeysetRandomAccessPageDefinition;
import com.io7m.jqpage.core.JQKeysetRandomAccessPagination;
import com.io7m.jqpage.core.JQOrder;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Table;
import org.jooq.UpdateConditionStep;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.SQLStateClass;
import org.jooq.impl.DSL;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
import static com.io7m.cardant.database.postgres.internal.Tables.FILES;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEMS;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_ATTACHMENTS;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_LOCATIONS;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_LOCATIONS_SUMMED;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_METADATA;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_TAGS;
import static com.io7m.cardant.database.postgres.internal.Tables.TAGS;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorDuplicate;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorRemoveTooManyItems;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSql;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DB_STATEMENT;
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
        final var c = itemRepositAdd(context, add);
        updateItemCount(context, add.item(), c);
        this.checkItemCountInvariant(context, add.item(), c);
        return;
      }
      if (reposit instanceof final CAItemRepositMove move) {
        final var c = this.itemRepositMove(context, move);
        updateItemCount(context, move.item(), c);
        this.checkItemCountInvariant(context, move.item(), c);
        return;
      }
      if (reposit instanceof final CAItemRepositRemove remove) {
        final var c = this.itemRepositRemove(context, remove);
        updateItemCount(context, remove.item(), c);
        this.checkItemCountInvariant(context, remove.item(), c);
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

  private static void updateItemCount(
    final DSLContext context,
    final CAItemID itemID,
    final long newCount)
  {
    context.update(ITEMS)
      .set(ITEMS.ITEM_COUNT, Long.valueOf(newCount))
      .where(ITEMS.ITEM_ID.eq(itemID.id()))
      .execute();
  }

  private long itemRepositRemove(
    final DSLContext context,
    final CAItemRepositRemove remove)
    throws CADatabaseException
  {
    try {
      final var matchItem =
        ITEM_LOCATIONS.ITEM_ID.eq(remove.item().id());
      final var matchLocation =
        ITEM_LOCATIONS.ITEM_LOCATION.eq(remove.location().id());
      final var matches =
        matchItem.and(matchLocation);

      final var newCount =
        context.update(ITEM_LOCATIONS)
          .set(
            ITEM_LOCATIONS.COUNT,
            ITEM_LOCATIONS.COUNT.sub(Long.valueOf(remove.count())))
          .where(matches)
          .returning(ITEM_LOCATIONS.COUNT)
          .fetchOne(ITEM_LOCATIONS.COUNT)
          .longValue();

      if (newCount == 0L) {
        context.deleteFrom(ITEM_LOCATIONS)
          .where(matches)
          .execute();
      }

      return newCount;
    } catch (final DataAccessException e) {
      if (e.sqlStateClass() == SQLStateClass.C23_INTEGRITY_CONSTRAINT_VIOLATION) {
        if (e.getMessage().contains("check_item_location_count")) {
          throw new CADatabaseException(
            errorRemoveTooManyItems(),
            this.messages().format("errorItemCountTooManyRemoved"),
            Map.ofEntries(
              Map.entry("Item ID", remove.item().displayId()),
              Map.entry("Item Location", remove.location().displayId()),
              Map.entry("Count", Long.toUnsignedString(remove.count()))
            )
          );
        }
      }
      throw e;
    }
  }

  private void checkItemCountInvariant(
    final DSLContext context,
    final CAItemID item,
    final long newCount)
    throws CADatabaseException
  {
    final var count =
      context.select(ITEM_LOCATIONS_SUMMED.ITEM_COUNT)
        .from(ITEM_LOCATIONS_SUMMED)
        .where(ITEM_LOCATIONS_SUMMED.ITEM_ID.eq(item.id()))
        .fetchOptional(ITEM_LOCATIONS_SUMMED.ITEM_COUNT)
        .orElse(BigInteger.ZERO);

    if (!Objects.equals(BigInteger.valueOf(newCount), count)) {
      throw new CADatabaseException(
        errorSql(),
        this.messages().format("errorItemCountStoreInvariant"),
        Map.ofEntries(
          Map.entry("Item ID", item.displayId()),
          Map.entry("Count Expected", Long.toUnsignedString(newCount)),
          Map.entry("Count Received", count.toString())
        )
      );
    }
  }

  private long itemRepositMove(
    final DSLContext context,
    final CAItemRepositMove move)
    throws CADatabaseException
  {
    final var newCount0 =
      this.itemRepositRemove(context, new CAItemRepositRemove(
        move.item(),
        move.fromLocation(),
        move.count()
      ));

    final var newCount1 =
      itemRepositAdd(context, new CAItemRepositAdd(
        move.item(),
        move.toLocation(),
        move.count()
      ));

    return newCount0 + newCount1;
  }

  private static long itemRepositAdd(
    final DSLContext context,
    final CAItemRepositAdd add)
  {
    final var newCount =
      context.insertInto(
          ITEM_LOCATIONS,
          ITEM_LOCATIONS.ITEM_ID,
          ITEM_LOCATIONS.ITEM_LOCATION,
          ITEM_LOCATIONS.COUNT)
        .values(
          add.item().id(),
          add.location().id(),
          Long.valueOf(add.count())
        )
        .onDuplicateKeyUpdate()
        .set(
          ITEM_LOCATIONS.COUNT,
          ITEM_LOCATIONS.COUNT.add(Long.valueOf(add.count())))
        .returning(ITEM_LOCATIONS.COUNT)
        .fetchOne(ITEM_LOCATIONS.COUNT);
    return newCount;
  }

  @Override
  public CAItemLocations itemLocations(
    final CAItemID item)
    throws CADatabaseException
  {
    return null;
  }

  @Override
  public CADatabaseItemSearchType itemSearch(
    final CAItemSearchParameters parameters)
    throws CADatabaseException
  {
    Objects.requireNonNull(parameters, "parameters");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesItems.itemSearch");

    try {
      final Table<?> tableSource;

      /*
       * The location query.
       *
       * Note that we only join the ITEM_LOCATIONS table if a search query
       * actually requires it.
       */

      final Condition locationCondition;
      final var behaviour = parameters.locationBehaviour();
      if (behaviour instanceof final CAListLocationExact exact) {
        tableSource =
          ITEMS.join(ITEM_LOCATIONS).on(ITEM_LOCATIONS.ITEM_ID.eq(ITEMS.ITEM_ID));
        locationCondition =
          ITEM_LOCATIONS.ITEM_LOCATION.eq(exact.location().id());

      } else if (behaviour instanceof final CAListLocationWithDescendants descendants) {
        tableSource =
          ITEMS.join(ITEM_LOCATIONS).on(ITEM_LOCATIONS.ITEM_ID.eq(ITEMS.ITEM_ID));

        final var funcCall =
          DSL.select(DSL.field("location_descendants.location"))
            .from("location_descendants(?)", descendants.location().id())
            .asField();

        locationCondition =
          ITEM_LOCATIONS.ITEM_LOCATION.in(funcCall);
      } else if (behaviour instanceof final CAListLocationsAll all) {
        tableSource = ITEMS;
        locationCondition = DSL.trueCondition();
      } else {
        tableSource = ITEMS;
        locationCondition = DSL.trueCondition();
      }

      /*
       * A search query might be present.
       */

      final Condition searchCondition;
      final var search = parameters.search();
      if (search.isPresent()) {
        final var searchText = "%%%s%%".formatted(search.get());
        searchCondition =
          DSL.condition(ITEMS.ITEM_NAME.likeIgnoreCase(searchText));
      } else {
        searchCondition = DSL.trueCondition();
      }

      /*
       * Items might be deleted.
       */

      final Condition deletedCondition =
        DSL.condition(ITEMS.ITEM_DELETED.eq(FALSE));

      final var allConditions =
        searchCondition
          .and(locationCondition)
          .and(deletedCondition);

      final var orderField =
        orderingToJQField(parameters.ordering());

      final var pages =
        JQKeysetRandomAccessPagination.createPageDefinitions(
          context,
          tableSource,
          List.of(orderField),
          List.of(allConditions),
          List.of(),
          Integer.toUnsignedLong(parameters.limit()),
          statement -> {
            querySpan.setAttribute(DB_STATEMENT, statement.toString());
          }
        );

      return new ItemSearch(pages);
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(transaction, e);
    } finally {
      querySpan.end();
    }
  }

  private static final class ItemSearch
    extends CAAbstractSearch<CADatabaseQueriesItems, CADatabaseQueriesItemsType, CAItemSummary>
    implements CADatabaseItemSearchType
  {
    ItemSearch(
      final List<JQKeysetRandomAccessPageDefinition> inPages)
    {
      super(inPages);
    }

    @Override
    protected CAPage<CAItemSummary> page(
      final CADatabaseQueriesItems queries,
      final JQKeysetRandomAccessPageDefinition page)
      throws CADatabaseException
    {
      final var transaction =
        queries.transaction();
      final var context =
        transaction.createContext();

      final var querySpan =
        transaction.createQuerySpan(
          "CADatabaseQueriesItems.itemSearch.page");

      try {
        final var query =
          page.queryFields(context, List.of(
            ITEMS.ITEM_ID,
            ITEMS.ITEM_NAME
          ));

        querySpan.setAttribute(DB_STATEMENT, query.toString());

        final var items =
          query.fetch().map(record -> {
            return new CAItemSummary(
              new CAItemID(record.get(ITEMS.ITEM_ID)),
              record.get(ITEMS.ITEM_NAME)
            );
          });

        return new CAPage<>(
          items,
          (int) page.index(),
          this.pageCount(),
          page.firstOffset()
        );
      } catch (final DataAccessException e) {
        querySpan.recordException(e);
        throw handleDatabaseException(transaction, e);
      } finally {
        querySpan.end();
      }
    }
  }

  private static JQField orderingToJQField(
    final CAItemColumnOrdering ordering)
  {
    final var field =
      switch (ordering.column()) {
        case BY_ID -> ITEMS.ITEM_ID;
        case BY_NAME -> ITEMS.ITEM_NAME;
      };

    return new JQField(
      field,
      ordering.ascending() ? JQOrder.ASCENDING : JQOrder.DESCENDING
    );
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
