/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.database.derby.internal;

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemAttachmentKey;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemLocation;
import com.io7m.cardant.model.CAItemLocations;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CAItemRepositMove;
import com.io7m.cardant.model.CAItemRepositRemove;
import com.io7m.cardant.model.CAItemRepositType;
import com.io7m.cardant.model.CAListLocationBehaviourType;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAModelDatabaseQueriesItemsType;
import com.io7m.cardant.model.CATag;
import com.io7m.junreachable.UnimplementedCodeException;
import com.io7m.junreachable.UnreachableCodeException;
import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_DUPLICATE;
import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_NONEXISTENT;
import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_PARAMETERS_INVALID;
import static com.io7m.cardant.database.derby.internal.CADatabaseBytes.fileIdBytes;
import static com.io7m.cardant.database.derby.internal.CADatabaseBytes.fileIdFromBytes;
import static com.io7m.cardant.database.derby.internal.CADatabaseBytes.itemIdBytes;
import static com.io7m.cardant.database.derby.internal.CADatabaseBytes.locationIdBytes;
import static com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationExact;
import static com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationWithDescendants;
import static com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationsAll;

/**
 * Internal database calls for the inventory.
 */

public final class CADatabaseModelQueriesItems
  extends CADatabaseModelQueriesAbstract
  implements CAModelDatabaseQueriesItemsType
{
  private static final String ITEM_CREATE = """
    INSERT INTO cardant.items (item_id, item_name, item_count, item_deleted) 
      VALUES (?, ?, ?, FALSE)
    """;
  private static final String ITEM_COUNT_SET = """
    UPDATE cardant.items
      SET item_count = ?
      WHERE item_id = ?
    """;
  private static final String ITEM_NAME_SET = """
    UPDATE cardant.items
      SET item_name = ?
      WHERE item_id = ?
    """;
  private static final String ITEM_GET = """
    SELECT * FROM cardant.items 
      WHERE item_id = ?
        AND item_deleted = ?
    """;
  private static final String ITEM_LIST = """
    SELECT item_id, item_name FROM cardant.items
      WHERE item_deleted = ?
    """;
  private static final String ITEM_LIST_EXACT = """
    SELECT i.item_id
      FROM cardant.items AS i
        JOIN cardant.item_locations AS il
          ON il.item_id = i.item_id
          WHERE il.item_location = ?
            AND i.item_deleted = ?
    """;
  private static final String ITEM_DELETE = """
    DELETE FROM cardant.items
      WHERE item_id = ?
    """;
  private static final String ITEM_DELETE_MARK_ONLY = """
    UPDATE cardant.items
      SET item_deleted = TRUE
      WHERE item_id = ?
    """;
  private static final String ITEM_TAG_REMOVE = """
    DELETE FROM cardant.item_tags 
      WHERE tag_item_id = ? AND tag_id = ?
    """;
  private static final String ITEM_TAG_REMOVE_ALL_BY_ITEM = """
    DELETE FROM cardant.item_tags 
      WHERE tag_item_id = ?
    """;
  private static final String ITEM_TAG_ADD = """
    INSERT INTO cardant.item_tags (tag_item_id, tag_id)
      VALUES (?, ?)
    """;
  private static final String ITEM_TAG_LIST = """
    SELECT t.tag_id, t.tag_name
      FROM cardant.tags AS t
      JOIN cardant.item_tags AS it ON it.tag_id = t.tag_id
        WHERE it.tag_item_id = ?
        ORDER BY t.tag_name
    """;
  private static final String ITEM_METADATA_PUT_INSERT = """
    INSERT INTO cardant.item_metadata (metadata_item_id, metadata_name, metadata_value)
      VALUES (?, ?, ?)
    """;
  private static final String ITEM_METADATA_PUT_UPDATE = """
    UPDATE cardant.item_metadata
      SET metadata_value = ?
      WHERE metadata_item_id = ?
        AND metadata_name = ?
    """;
  private static final String ITEM_METADATA_GET = """
    SELECT metadata_name, metadata_value
      FROM cardant.item_metadata
      WHERE metadata_item_id = ?
    """;
  private static final String ITEM_METADATA_REMOVE = """
    DELETE FROM cardant.item_metadata
      WHERE metadata_item_id = ? AND metadata_name = ?
    """;
  private static final String ITEM_METADATA_REMOVE_ALL_BY_ITEM = """
    DELETE FROM cardant.item_metadata
      WHERE metadata_item_id = ?
    """;

  private static final String ITEM_ATTACHMENTS_REMOVE_ALL_BY_ITEM = """
    DELETE FROM cardant.item_attachments
      WHERE item_id = ?
    """;
  private static final String ITEM_ATTACHMENT_REMOVE = """
    DELETE FROM cardant.item_attachments
      WHERE item_id = ?
        AND file_id = ?
        AND relation = ?
    """;
  private static final String ITEM_COUNT_SUM_GET = """
    SELECT
      v.item_count
    FROM cardant.item_locations_summed v
      WHERE v.item_id = ?
    """;
  private static final String ITEM_REPOSIT_REMOVE_DELETE = """
    DELETE FROM cardant.item_locations
      WHERE item_id = ?
        AND item_location = ?
    """;
  private static final String ITEM_REPOSIT_CHECK = """
      SELECT
        il.count
      FROM cardant.item_locations il
        WHERE il.item_id = ?
          AND il.item_location = ?
    """;
  private static final String ITEM_REPOSIT_ADD_INSERT = """
      INSERT INTO cardant.item_locations (
        item_id,
        item_location,
        count
      ) VALUES (?, ?, ?)
    """;
  private static final String ITEM_REPOSIT_ADD_UPDATE = """
      UPDATE cardant.item_locations
        SET count = ?
        WHERE item_id = ?
          AND item_location = ?
    """;
  private static final String ITEM_LOCATIONS_LIST = """
    SELECT
      il.item_id,
      il.item_location,
      il.count
    FROM
      cardant.item_locations il
    WHERE
      il.item_id = ?
    """;
  private static final String ITEM_LOCATION_COUNT_GET = """
    SELECT
      il.item_id,
      il.item_location,
      il.count
    FROM
      cardant.item_locations il
    WHERE il.item_id = ?
      AND il.item_location = ?
    """;
  private static final String ITEM_ATTACHMENT_ADD_INNER = """
      INSERT INTO cardant.item_attachments (
        file_id,
        item_id,
        relation
      ) VALUES (?, ?, ?)
    """;
  private final CADatabaseMessages messages;
  private final CADatabaseModelQueriesLocations locations;
  private final CADatabaseModelQueriesFiles files;
  private final CADatabaseModelQueriesTags tags;

  CADatabaseModelQueriesItems(
    final CADatabaseModelQueriesLocations inLocations,
    final CADatabaseModelQueriesFiles inFiles,
    final CADatabaseModelQueriesTags inTags,
    final CADatabaseDerbyTransaction inTransaction)
  {
    super(inTransaction);

    this.locations =
      Objects.requireNonNull(inLocations, "locations");
    this.files =
      Objects.requireNonNull(inFiles, "inFiles");
    this.tags =
      Objects.requireNonNull(inTags, "tags");
    this.messages =
      inTransaction.messages();
  }

  private static void itemMetadataPutInsert(
    final Connection connection,
    final CAItemID itemID,
    final CAItemMetadata metadata)
    throws SQLException
  {
    try (var statement =
           connection.prepareStatement(ITEM_METADATA_PUT_INSERT)) {
      statement.setBytes(1, itemIdBytes(itemID));
      statement.setString(2, metadata.name());
      statement.setString(3, metadata.value());
      statement.executeUpdate();
    }
  }

  private static void itemMetadataPutUpdate(
    final Connection connection,
    final CAItemID itemID,
    final CAItemMetadata metadata)
    throws SQLException
  {
    try (var statement =
           connection.prepareStatement(ITEM_METADATA_PUT_UPDATE)) {
      statement.setString(1, metadata.value());
      statement.setBytes(2, itemIdBytes(itemID));
      statement.setString(3, metadata.name());
      statement.executeUpdate();
    }
  }

  private static TreeMap<String, CAItemMetadata> itemMetadataInner(
    final Connection connection,
    final CAItemID itemId)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(ITEM_METADATA_GET)) {
      statement.setBytes(1, itemIdBytes(itemId));

      try (var result = statement.executeQuery()) {
        final var metas = new TreeMap<String, CAItemMetadata>();
        while (result.next()) {
          final var name =
            result.getString("metadata_name");
          final var value =
            result.getString("metadata_value");
          final var itemMetadata =
            new CAItemMetadata(name, value);
          metas.put(name, itemMetadata);
        }
        return metas;
      }
    }
  }

  private static SortedSet<CATag> itemTagListInner(
    final Connection connection,
    final CAItemID item)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(ITEM_TAG_LIST)) {
      statement.setBytes(1, CADatabaseBytes.uuidBytes(item.id()));

      try (var result = statement.executeQuery()) {
        final var tags = new TreeSet<CATag>();
        while (result.next()) {
          tags.add(CADatabaseModelQueriesTags.tagParse(result));
        }
        return tags;
      }
    }
  }

  private static void itemRepositInnerRemoveDelete(
    final Connection connection,
    final CAItemRepositRemove remove)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(ITEM_REPOSIT_REMOVE_DELETE)) {
      statement.setBytes(1, itemIdBytes(remove.item()));
      statement.setBytes(2, locationIdBytes(remove.location()));
      statement.executeUpdate();
    }
  }

  private static CAItemLocations itemLocationsInner(
    final Connection connection,
    final CAItemID id)
    throws SQLException
  {
    final var locations =
      new TreeMap<CALocationID, SortedMap<CAItemID, CAItemLocation>>();

    try (var statement = connection.prepareStatement(ITEM_LOCATIONS_LIST)) {
      statement.setBytes(1, itemIdBytes(id));

      try (var results = statement.executeQuery()) {
        while (results.next()) {
          final var itemLocation = new CAItemLocation(
            CADatabaseBytes.itemIdFromBytes(
              results.getBytes("item_id")),
            CADatabaseBytes.locationIdFromBytes(
              results.getBytes("item_location")),
            results.getLong("count")
          );

          final var byId =
            locations.computeIfAbsent(
              itemLocation.location(), x -> new TreeMap<>());

          byId.put(itemLocation.item(), itemLocation);
        }
        return new CAItemLocations(locations);
      }
    }
  }

  private Optional<CAItem> itemGetInner(
    final Connection connection,
    final CAItemID id,
    final boolean deleted)
    throws SQLException, IOException, CADatabaseException
  {
    try (var statement =
           connection.prepareStatement(ITEM_GET)) {
      statement.setBytes(1, itemIdBytes(id));
      statement.setBoolean(2, deleted);

      try (var result = statement.executeQuery()) {
        if (result.next()) {
          final var itemId =
            CADatabaseBytes.itemIdFromBytes(result.getBytes("item_id"));
          final var itemName =
            result.getString("item_name");
          final var itemCount =
            result.getLong("item_count");

          final var itemAttachments =
            this.itemAttachmentsInner(connection, itemId, false);
          final var itemMetadatas =
            itemMetadataInner(connection, itemId);
          final var itemTags =
            itemTagListInner(connection, itemId);

          return Optional.of(new CAItem(
            itemId,
            itemName,
            itemCount,
            0L,
            itemMetadatas,
            itemAttachments,
            itemTags
          ));
        }
        return Optional.empty();
      }
    }
  }

  private static final String ITEM_ATTACHMENTS = """
    SELECT
      ia.file_id,
      ia.item_id,
      ia.relation
    FROM
      cardant.item_attachments ia
    WHERE
      ia.item_id = ?
    """;

  private SortedMap<CAItemAttachmentKey, CAItemAttachment> itemAttachmentsInner(
    final Connection connection,
    final CAItemID itemId,
    final boolean withData)
    throws SQLException, IOException, CADatabaseException
  {
    final var attachments =
      new TreeMap<CAItemAttachmentKey, CAItemAttachment>();

    try (var statement = connection.prepareStatement(ITEM_ATTACHMENTS)) {
      statement.setBytes(1, itemIdBytes(itemId));
      try (var results = statement.executeQuery()) {
        while (results.next()) {
          final var fileId =
            fileIdFromBytes(results.getBytes("file_id"));
          final var relation =
            results.getString("relation");
          final var file =
            this.files.fileGetInner(connection, fileId, withData)
              .orElseThrow(() -> this.files.noSuchFile(fileId.id()));

          final var attachment = new CAItemAttachment(file, relation);
          attachments.put(attachment.key(), attachment);
        }
      }
    }
    return attachments;
  }

  private Set<CAItem> itemListInnerExact(
    final Connection connection,
    final CALocationID location)
    throws SQLException, IOException, CADatabaseException
  {
    final var ids =
      itemListInnerExactIDsAll(connection, location);

    final var items = new HashSet<CAItem>(ids.size());
    for (final var id : ids) {
      final var count =
        itemLocationCountGetInner(connection, id, location);
      this.itemGetInner(connection, id, false)
        .map(item -> item.withCountHere(count))
        .ifPresent(items::add);
    }
    return items;
  }

  private static long itemLocationCountGetInner(
    final Connection connection,
    final CAItemID id,
    final CALocationID location)
    throws SQLException
  {
    try (var statement =
           connection.prepareStatement(ITEM_LOCATION_COUNT_GET)) {
      statement.setBytes(1, itemIdBytes(id));
      statement.setBytes(2, locationIdBytes(location));
      try (var result = statement.executeQuery()) {
        while (result.next()) {
          return result.getLong("count");
        }
        return 0L;
      }
    }
  }

  private static HashSet<CAItemID> itemListInnerExactIDsAll(
    final Connection connection,
    final CALocationID location)
    throws SQLException
  {
    final var ids = new HashSet<CAItemID>(32);
    try (var statement = connection.prepareStatement(ITEM_LIST_EXACT)) {
      statement.setBytes(1, locationIdBytes(location));
      statement.setBoolean(2, false);
      try (var result = statement.executeQuery()) {
        while (result.next()) {
          final var id =
            CADatabaseBytes.itemIdFromBytes(
              result.getBytes("item_id"));
          ids.add(id);
        }
        return ids;
      }
    }
  }

  private Set<CAItem> itemListInnerAll(
    final Connection connection)
    throws SQLException, IOException, CADatabaseException
  {
    final var ids = itemListInnerIDsAll(connection);
    final var items = new HashSet<CAItem>(ids.size());
    for (final var id : ids) {
      this.itemGetInner(connection, id, false).ifPresent(items::add);
    }
    return items;
  }

  private static HashSet<CAItemID> itemListInnerIDsAll(
    final Connection connection)
    throws SQLException
  {
    final var ids = new HashSet<CAItemID>(32);
    try (var statement = connection.prepareStatement(ITEM_LIST)) {
      statement.setBoolean(1, false);
      try (var result = statement.executeQuery()) {
        while (result.next()) {
          final var id =
            CADatabaseBytes.itemIdFromBytes(
              result.getBytes("item_id"));
          ids.add(id);
        }
        return ids;
      }
    }
  }

  private void itemAttachmentAddInner(
    final Connection connection,
    final CAItemID item,
    final CAFileID file,
    final String relation)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(ITEM_ATTACHMENT_ADD_INNER)) {
      statement.setBytes(1, fileIdBytes(file));
      statement.setBytes(2, itemIdBytes(item));
      statement.setString(3, relation);
      statement.executeUpdate();
    }

    this.publishUpdate(file);
    this.publishUpdate(item);
  }

  private Object itemTagRemoveInner(
    final Connection connection,
    final CAItemID item,
    final CATag tag)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(ITEM_TAG_REMOVE)) {
      statement.setBytes(1, CADatabaseBytes.uuidBytes(item.id()));
      statement.setBytes(2, CADatabaseBytes.tagIdBytes(tag.id()));
      statement.executeUpdate();
    }

    this.publishUpdate(item);
    return null;
  }

  private Object itemTagAddInner(
    final Connection connection,
    final CAItemID item,
    final CATag tag)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(ITEM_TAG_ADD)) {
      statement.setBytes(1, CADatabaseBytes.uuidBytes(item.id()));
      statement.setBytes(2, CADatabaseBytes.tagIdBytes(tag.id()));
      statement.executeUpdate();
    }

    this.publishUpdate(item);
    return null;
  }

  private Object itemMetadataRemoveInner(
    final Connection connection,
    final CAItemID itemID,
    final String metadata)
    throws SQLException
  {
    try (var statement =
           connection.prepareStatement(ITEM_METADATA_REMOVE)) {
      statement.setBytes(1, itemIdBytes(itemID));
      statement.setString(2, metadata);
      statement.executeUpdate();
    }

    this.publishUpdate(itemID);
    return null;
  }

  private void itemAttachmentRemoveInner(
    final Connection connection,
    final CAItemID itemId,
    final CAFileID fileID,
    final String relation)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(ITEM_ATTACHMENT_REMOVE)) {
      statement.setBytes(1, itemIdBytes(itemId));
      statement.setBytes(2, fileIdBytes(fileID));
      statement.setString(3, relation);
      statement.executeUpdate();
    }
    this.publishUpdate(itemId);
    this.publishUpdate(fileID);
  }

  private void itemRepositInnerRemoveUpdate(
    final Connection connection,
    final CAItemRepositRemove remove,
    final long currentCount)
    throws SQLException, CADatabaseException
  {
    final var newCount = currentCount - remove.count();

    try (var statement = connection.prepareStatement(ITEM_REPOSIT_ADD_UPDATE)) {
      statement.setLong(1, newCount);
      statement.setBytes(2, itemIdBytes(remove.item()));
      statement.setBytes(3, locationIdBytes(remove.location()));
      statement.executeUpdate();
    } catch (final DerbySQLIntegrityConstraintViolationException e) {
      if (Objects.equals(e.getConstraintName(), "CHECK_ITEM_LOCATION_COUNT")) {
        final var attributes = new HashMap<String, String>();
        attributes.put(
          this.messages.format("item"),
          remove.item().id().toString());
        attributes.put(
          this.messages.format("startingCount"),
          Long.toUnsignedString(currentCount));
        attributes.put(
          this.messages.format("attemptedRemoval"),
          Long.toUnsignedString(remove.count()));
        attributes.put(
          this.messages.format("problem"),
          this.messages.format("errorItemCountTooManyRemoved"));

        throw new CADatabaseException(
          ERROR_PARAMETERS_INVALID,
          attributes,
          this.messages.format("errorItemCountTooManyRemoved")
        );
      }
      throw e;
    }
  }

  private CADatabaseException duplicateItem(
    final CAItemID id)
  {
    final var attributes = new HashMap<String, String>();
    attributes.put(
      this.messages.format("object"),
      id.displayId());
    attributes.put(
      this.messages.format("type"),
      this.messages.format("item"));

    return new CADatabaseException(
      ERROR_DUPLICATE,
      attributes,
      this.messages.format("errorDuplicate", id.id(), "Item")
    );
  }

  private CADatabaseException duplicateItemDeleted(
    final CAItemID id)
  {
    final var attributes = new HashMap<String, String>();
    attributes.put(
      this.messages.format("object"),
      id.displayId());
    attributes.put(
      this.messages.format("type"),
      this.messages.format("item"));

    return new CADatabaseException(
      ERROR_DUPLICATE,
      attributes,
      this.messages.format("errorDuplicateDeleted")
    );
  }

  private CADatabaseException noSuchItem(
    final UUID item)
  {
    final var attributes = new HashMap<String, String>();
    attributes.put(
      this.messages.format("object"),
      item.toString());
    attributes.put(
      this.messages.format("type"),
      this.messages.format("item"));

    return new CADatabaseException(
      ERROR_NONEXISTENT,
      attributes,
      this.messages.format("errorNonexistent")
    );
  }

  @Override
  public void itemReposit(
    final CAItemRepositType reposit)
    throws CADatabaseException
  {
    Objects.requireNonNull(reposit, "reposit");

    this.withSQLConnection(connection -> {
      this.itemRepositInner(connection, reposit);
      return null;
    });
  }

  @Override
  public CAItemLocations itemLocations(
    final CAItemID id)
    throws CADatabaseException
  {
    return this.withSQLConnection(
      connection -> itemLocationsInner(connection, id));
  }

  private void itemRepositInner(
    final Connection connection,
    final CAItemRepositType reposit)
    throws CADatabaseException, SQLException
  {
    final var item =
      this.itemGet(reposit.item())
        .orElseThrow(() -> this.noSuchItem(reposit.item().id()));

    if (reposit instanceof CAItemRepositAdd add) {
      this.itemRepositInnerAdd(connection, item, add);
      this.publishUpdate(item.id());
      return;
    } else if (reposit instanceof CAItemRepositRemove remove) {
      this.itemRepositInnerRemove(connection, item, remove);
      this.publishUpdate(item.id());
      return;
    } else if (reposit instanceof CAItemRepositMove move) {
      this.itemRepositInnerMove(connection, move);
      this.publishUpdate(item.id());
      return;
    } else {
      throw new IllegalStateException("Unexpected reposit: " + reposit);
    }
  }

  private void itemRepositInnerMove(
    final Connection connection,
    final CAItemRepositMove move)
    throws CADatabaseException, SQLException
  {
    this.itemRepositInnerRemove(
      connection,
      this.itemGet(move.item()).orElseThrow(),
      new CAItemRepositRemove(move.item(), move.fromLocation(), move.count())
    );

    this.itemRepositInnerAdd(
      connection,
      this.itemGet(move.item()).orElseThrow(),
      new CAItemRepositAdd(move.item(), move.toLocation(), move.count())
    );
  }

  private void itemRepositInnerRemove(
    final Connection connection,
    final CAItem item,
    final CAItemRepositRemove remove)
    throws CADatabaseException, SQLException
  {
    this.locations.locationGet(remove.location())
      .orElseThrow(() -> this.noSuchLocation(remove.location()));

    try (var statement = connection.prepareStatement(ITEM_REPOSIT_CHECK)) {
      statement.setBytes(1, itemIdBytes(remove.item()));
      statement.setBytes(2, locationIdBytes(remove.location()));

      try (var results = statement.executeQuery()) {
        if (results.next()) {
          final var currentCount = results.getLong("count");
          if (currentCount == remove.count()) {
            itemRepositInnerRemoveDelete(connection, remove);
          } else {
            this.itemRepositInnerRemoveUpdate(connection, remove, currentCount);
          }
        }
      }
    }

    this.itemCountSetInner(
      connection,
      remove.item(),
      item.countTotal() - remove.count()
    );
  }

  private void itemRepositInnerAdd(
    final Connection connection,
    final CAItem item,
    final CAItemRepositAdd add)
    throws SQLException, CADatabaseException
  {
    this.locations.locationGet(add.location())
      .orElseThrow(() -> this.noSuchLocation(add.location()));

    try (var statement = connection.prepareStatement(ITEM_REPOSIT_CHECK)) {
      statement.setBytes(1, itemIdBytes(add.item()));
      statement.setBytes(2, locationIdBytes(add.location()));

      try (var results = statement.executeQuery()) {
        if (results.next()) {
          final var currentCount = results.getLong("count");
          this.itemRepositInnerAddUpdate(connection, item, add, currentCount);
        } else {
          this.itemRepositInnerAddInsert(connection, item, add);
        }
      }
    }
  }

  private void itemRepositInnerAddInsert(
    final Connection connection,
    final CAItem item,
    final CAItemRepositAdd add)
    throws SQLException, CADatabaseException
  {
    try (var statement = connection.prepareStatement(ITEM_REPOSIT_ADD_INSERT)) {
      statement.setBytes(1, itemIdBytes(add.item()));
      statement.setBytes(2, locationIdBytes(add.location()));
      statement.setLong(3, add.count());
      statement.executeUpdate();
    }

    final var newCount = item.countTotal() + add.count();
    this.itemCountSetInner(connection, add.item(), newCount);
  }

  private void itemCountSetInner(
    final Connection connection,
    final CAItemID item,
    final long count)
    throws SQLException, CADatabaseException
  {
    try (var statement = connection.prepareStatement(ITEM_COUNT_SET)) {
      statement.setLong(1, count);
      statement.setBytes(2, itemIdBytes(item));
      statement.executeUpdate();
    } catch (final DerbySQLIntegrityConstraintViolationException e) {
      if (Objects.equals(e.getConstraintName(), "CHECK_NATURAL_COUNT")) {
        final var attributes = new HashMap<String, String>();
        attributes.put(
          this.messages.format("item"),
          item.displayId());

        throw new CADatabaseException(
          ERROR_PARAMETERS_INVALID,
          attributes,
          e.getMessage());
      }
      throw e;
    }

    this.checkItemCountInvariant(connection, item, count);
    this.publishUpdate(item);
  }

  private void itemRepositInnerAddUpdate(
    final Connection connection,
    final CAItem item,
    final CAItemRepositAdd add,
    final long locationCount)
    throws SQLException, CADatabaseException
  {
    final var newCount = locationCount + add.count();

    try (var statement = connection.prepareStatement(ITEM_REPOSIT_ADD_UPDATE)) {
      statement.setLong(1, newCount);
      statement.setBytes(2, itemIdBytes(add.item()));
      statement.setBytes(3, locationIdBytes(add.location()));
      statement.executeUpdate();
    }

    this.itemCountSetInner(connection, item.id(), newCount);
  }

  @Override
  public Optional<CAItem> itemGet(
    final CAItemID id)
    throws CADatabaseException
  {
    Objects.requireNonNull(id, "id");

    return this.withSQLConnection(
      connection -> this.itemGetInner(connection, id, false));
  }

  @Override
  public void itemCreate(
    final CAItemID id)
    throws CADatabaseException
  {
    Objects.requireNonNull(id, "id");

    this.withSQLConnection(connection -> {

      {
        final var existing = this.itemGetInner(connection, id, true);
        if (existing.isPresent()) {
          throw this.duplicateItemDeleted(id);
        }
      }

      {
        final var existing = this.itemGetInner(connection, id, false);
        if (existing.isPresent()) {
          throw this.duplicateItem(id);
        }
      }

      try (var statement = connection.prepareStatement(ITEM_CREATE)) {
        statement.setBytes(1, itemIdBytes(id));
        statement.setString(2, "");
        statement.setLong(3, 0L);
        statement.executeUpdate();
      }

      this.publishUpdate(id);
      return null;
    });
  }

  private void checkItemCountInvariant(
    final Connection connection,
    final CAItemID id,
    final long expectedItemCount)
    throws SQLException, CADatabaseException
  {
    final long itemSum;
    try (var statement = connection.prepareStatement(ITEM_COUNT_SUM_GET)) {
      statement.setBytes(1, itemIdBytes(id));
      try (var results = statement.executeQuery()) {
        if (results.next()) {
          itemSum = results.getLong("item_count");
        } else {
          itemSum = 0L;
        }
      }
    }

    if (itemSum != expectedItemCount) {
      final var attributes = new HashMap<String, String>();
      attributes.put(
        this.messages.format("item"),
        id.displayId());
      attributes.put(
        this.messages.format("expectedCount"),
        Long.toUnsignedString(expectedItemCount));
      attributes.put(
        this.messages.format("receivedCount"),
        Long.toUnsignedString(itemSum));

      throw new CADatabaseException(
        ERROR_PARAMETERS_INVALID,
        attributes,
        this.messages.format("errorItemCountStoreInvariant")
      );
    }
  }

  @Override
  public void itemNameSet(
    final CAItemID id,
    final String name)
    throws CADatabaseException
  {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(name, "name");

    this.withSQLConnection(connection -> {
      this.itemCheck(connection, id, false);

      try (var statement = connection.prepareStatement(ITEM_NAME_SET)) {
        statement.setString(1, name);
        statement.setBytes(2, itemIdBytes(id));
        statement.executeUpdate();
      }
      return null;
    });
  }

  @Override
  public Set<CAItem> itemList(
    final CAListLocationBehaviourType locationBehaviour)
    throws CADatabaseException
  {
    Objects.requireNonNull(locationBehaviour, "locationBehaviour");

    return this.withSQLConnection(connection -> {
      if (locationBehaviour instanceof CAListLocationsAll) {
        return itemListInnerAll(connection);
      }
      if (locationBehaviour instanceof CAListLocationExact exact) {
        return this.itemListInnerExact(connection, exact.location());
      }
      if (locationBehaviour instanceof CAListLocationWithDescendants with) {
        return this.itemListInnerWithDescendants(connection, with.location());
      }
      throw new UnreachableCodeException();
    });
  }

  private Set<CAItem> itemListInnerWithDescendants(
    final Connection connection,
    final CALocationID location)
  {
    throw new UnimplementedCodeException();
  }

  @Override
  public Set<CAItemID> itemListDeleted()
    throws CADatabaseException
  {
    return this.withSQLConnection(connection -> {
      try (var statement =
             connection.prepareStatement(ITEM_LIST)) {
        statement.setBoolean(1, true);

        try (var result = statement.executeQuery()) {
          final var items = new HashSet<CAItemID>(32);
          while (result.next()) {
            items.add(CADatabaseBytes.itemIdFromBytes(result.getBytes("item_id")));
          }
          return items;
        }
      }
    });
  }

  @Override
  public void itemsDelete(
    final Collection<CAItemID> items)
    throws CADatabaseException
  {
    Objects.requireNonNull(items, "items");

    this.withSQLConnection(connection -> {
      for (final var item : items) {
        final var currentItemDeleted =
          this.itemGetInner(connection, item, true);
        final var currentItemNotDeleted =
          this.itemGetInner(connection, item, false);
        final var currentItem =
          currentItemDeleted.or(() -> currentItemNotDeleted)
            .orElseThrow(() -> this.noSuchItem(item.id()));

        final var itemIdBytes = itemIdBytes(item);
        try (var statement =
               connection.prepareStatement(ITEM_ATTACHMENTS_REMOVE_ALL_BY_ITEM)) {
          statement.setBytes(1, itemIdBytes);
          statement.executeUpdate();
        }
        try (var statement =
               connection.prepareStatement(ITEM_METADATA_REMOVE_ALL_BY_ITEM)) {
          statement.setBytes(1, itemIdBytes);
          statement.executeUpdate();
        }
        try (var statement =
               connection.prepareStatement(ITEM_TAG_REMOVE_ALL_BY_ITEM)) {
          statement.setBytes(1, itemIdBytes);
          statement.executeUpdate();
        }
        try (var statement =
               connection.prepareStatement(ITEM_DELETE)) {
          statement.setBytes(1, itemIdBytes);
          statement.executeUpdate();
        }
      }

      for (final var item : items) {
        this.publishRemove(item);
      }
      return null;
    });
  }

  @Override
  public void itemsDeleteMarkOnly(
    final Collection<CAItemID> items)
    throws CADatabaseException
  {
    Objects.requireNonNull(items, "items");

    this.withSQLConnection(connection -> {
      for (final var item : items) {
        final var currentItem =
          this.itemGetInner(connection, item, false)
            .orElseThrow(() -> this.noSuchItem(item.id()));

        final var itemIdBytes = itemIdBytes(item);
        try (var statement =
               connection.prepareStatement(ITEM_DELETE_MARK_ONLY)) {
          statement.setBytes(1, itemIdBytes);
          statement.executeUpdate();
        }

        this.publishRemove(item);
      }
      return null;
    });
  }

  @Override
  public void itemTagAdd(
    final CAItemID item,
    final CATag tag)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");
    Objects.requireNonNull(tag, "tag");

    this.withSQLConnection(connection -> {
      this.itemCheck(connection, item, false);
      this.tags.tagCheck(connection, tag.id());
      this.itemTagRemoveInner(connection, item, tag);
      return this.itemTagAddInner(connection, item, tag);
    });
  }

  @Override
  public void itemTagRemove(
    final CAItemID item,
    final CATag tag)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");
    Objects.requireNonNull(tag, "tag");

    this.withSQLConnection(connection -> {
      this.itemCheck(connection, item, false);
      this.tags.tagCheck(connection, tag.id());
      return this.itemTagRemoveInner(connection, item, tag);
    });
  }

  @Override
  public SortedSet<CATag> itemTagList(
    final CAItemID item)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");

    return this.withSQLConnection(connection -> {
      this.itemCheck(connection, item, false);
      return itemTagListInner(connection, item);
    });
  }

  @Override
  public void itemMetadataPut(
    final CAItemID itemID,
    final CAItemMetadata metadata)
    throws CADatabaseException
  {
    Objects.requireNonNull(itemID, "itemID");
    Objects.requireNonNull(metadata, "metadata");

    this.withSQLConnection(connection -> {
      this.itemCheck(connection, itemID, false);

      final var metas =
        itemMetadataInner(connection, itemID);

      final var existing = metas.get(metadata.name());
      if (existing != null) {
        itemMetadataPutUpdate(connection, itemID, metadata);
      } else {
        itemMetadataPutInsert(connection, itemID, metadata);
      }

      this.publishUpdate(itemID);
      return null;
    });
  }

  @Override
  public SortedMap<String, CAItemMetadata> itemMetadata(
    final CAItemID item)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");

    return this.withSQLConnection(connection -> {
      this.itemCheck(connection, item, false);
      return itemMetadataInner(connection, item);
    });
  }

  @Override
  public void itemMetadataRemove(
    final CAItemID itemID,
    final String metadata)
    throws CADatabaseException
  {
    Objects.requireNonNull(itemID, "itemID");
    Objects.requireNonNull(metadata, "metadata");

    this.withSQLConnection(connection -> {
      this.itemCheck(connection, itemID, false);
      return this.itemMetadataRemoveInner(connection, itemID, metadata);
    });
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

    this.withSQLConnection(connection -> {
      this.itemCheck(connection, item, false);
      this.files.fileCheck(connection, file);
      this.itemAttachmentAddInner(connection, item, file, relation);
      return null;
    });
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

    this.withSQLConnection(connection -> {
      this.itemCheck(connection, item, false);
      this.files.fileCheck(connection, file);
      this.itemAttachmentRemoveInner(connection, item, file, relation);
      return null;
    });
  }

  @Override
  public Set<CAItemAttachment> itemAttachments(
    final CAItemID item,
    final boolean withData)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");

    return this.withSQLConnection(connection -> {
      this.itemCheck(connection, item, false);
      throw new UnimplementedCodeException();
    });
  }

  private void itemCheck(
    final Connection connection,
    final CAItemID id,
    final boolean deleted)
    throws CADatabaseException, SQLException
  {
    try (var statement = connection.prepareStatement(ITEM_GET)) {
      statement.setBytes(1, itemIdBytes(id));
      statement.setBoolean(2, deleted);

      try (var result = statement.executeQuery()) {
        if (!result.next()) {
          throw this.noSuchItem(id.id());
        }
      }
    }
  }

  private CADatabaseException noSuchLocation(
    final CALocationID location)
  {
    final var attributes = new HashMap<String, String>();
    attributes.put(
      this.messages.format("object"),
      location.id().toString());
    attributes.put(
      this.messages.format("type"),
      this.messages.format("location"));

    return new CADatabaseException(
      ERROR_NONEXISTENT,
      attributes,
      this.messages.format("errorNonexistent")
    );
  }
}
