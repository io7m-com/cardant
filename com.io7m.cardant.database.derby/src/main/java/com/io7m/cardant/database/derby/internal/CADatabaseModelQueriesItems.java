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
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemAttachmentID;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemLocation;
import com.io7m.cardant.model.CAItemLocations;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CAItemRepositMove;
import com.io7m.cardant.model.CAItemRepositRemove;
import com.io7m.cardant.model.CAItemRepositType;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAModelDatabaseQueriesItemsType;
import com.io7m.cardant.model.CATag;
import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_DUPLICATE;
import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_GENERAL;
import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_NONEXISTENT;
import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_PARAMETERS_INVALID;
import static com.io7m.cardant.database.derby.internal.CADatabaseBytes.locationIdBytes;

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
  private static final String ITEM_ATTACHMENT_GET = """
    SELECT
      cia.attachment_id,
      cia.attachment_item_id,
      cia.attachment_description,
      cia.attachment_media_type,
      cia.attachment_relation,
      cia.attachment_hash_algorithm,
      cia.attachment_hash_value,
      cia.attachment_data,
      cia.attachment_data_used
    FROM cardant.item_attachments cia
      WHERE attachment_id = ?
    """;
  private static final String ITEM_ATTACHMENT_PUT_INSERT = """
    INSERT INTO cardant.item_attachments (
      attachment_id,
      attachment_item_id,
      attachment_description,
      attachment_media_type,
      attachment_relation,
      attachment_hash_algorithm,
      attachment_hash_value,
      attachment_data,
      attachment_data_used
    ) VALUES (
      ?, ?, ?, ?, ?, ?, ?, ?, ?
    )
    """;
  private static final String ITEM_ATTACHMENT_PUT_UPDATE = """
    UPDATE cardant.item_attachments
      SET attachment_description = ?,
          attachment_media_type = ?,
          attachment_relation = ?,
          attachment_hash_algorithm = ?,
          attachment_hash_value = ?,
          attachment_data = ?,
          attachment_data_used = ?
      WHERE attachment_id = ? AND attachment_item_id = ?
    """;
  private static final String ITEM_ATTACHMENTS_GET = """
    SELECT
      cia.attachment_id,
      cia.attachment_item_id,
      cia.attachment_description,
      cia.attachment_media_type,
      cia.attachment_relation,
      cia.attachment_hash_algorithm,
      cia.attachment_hash_value,
      cia.attachment_data,
      cia.attachment_data_used
    FROM cardant.item_attachments cia
      WHERE attachment_item_id = ?
      ORDER BY attachment_id
    """;
  private static final String ITEM_FOR_ATTACHMENT = """
    SELECT
      cia.attachment_item_id
    FROM cardant.item_attachments cia
      WHERE attachment_id = ?
    """;

  private static final String ITEM_ATTACHMENTS_REMOVE_ALL_BY_ITEM = """
    DELETE FROM cardant.item_attachments
      WHERE attachment_item_id = ?
    """;
  private static final String ITEM_ATTACHMENT_REMOVE = """
    DELETE FROM cardant.item_attachments
      WHERE attachment_id = ?
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
    """;

  private final CADatabaseMessages messages;
  private final CADatabaseModelQueriesLocations locations;
  private final CADatabaseModelQueriesTags tags;

  CADatabaseModelQueriesItems(
    final CADatabaseModelQueriesLocations inLocations,
    final CADatabaseModelQueriesTags inTags,
    final CADatabaseDerbyTransaction inTransaction)
  {
    super(inTransaction);

    this.locations =
      Objects.requireNonNull(inLocations, "locations");
    this.tags =
      Objects.requireNonNull(inTags, "tags");
    this.messages =
      inTransaction.messages();
  }

  private static void itemMetadataPutInsert(
    final Connection connection,
    final CAItemMetadata metadata)
    throws SQLException
  {
    try (var statement =
           connection.prepareStatement(ITEM_METADATA_PUT_INSERT)) {
      statement.setBytes(1, CADatabaseBytes.itemIdBytes(metadata.itemId()));
      statement.setString(2, metadata.name());
      statement.setString(3, metadata.value());
      statement.executeUpdate();
    }
  }

  private static void itemMetadataPutUpdate(
    final Connection connection,
    final CAItemMetadata metadata)
    throws SQLException
  {
    try (var statement =
           connection.prepareStatement(ITEM_METADATA_PUT_UPDATE)) {
      statement.setString(1, metadata.value());
      statement.setBytes(2, CADatabaseBytes.itemIdBytes(metadata.itemId()));
      statement.setString(3, metadata.name());
      statement.executeUpdate();
    }
  }

  private static void itemAttachmentPutInsert(
    final Connection connection,
    final CAItemAttachment attachment,
    final CAByteArray data)
    throws SQLException, IOException
  {
    final var dataLength = data.data().length;
    final var blob = connection.createBlob();
    try (var output = blob.setBinaryStream(1L)) {
      output.write(data.data());
    }

    try (var statement =
           connection.prepareStatement(ITEM_ATTACHMENT_PUT_INSERT)) {
      statement.setBytes(1, attachmentIdBytes(attachment.id()));
      statement.setBytes(2, CADatabaseBytes.itemIdBytes(attachment.itemId()));
      statement.setString(3, attachment.description());
      statement.setString(4, attachment.mediaType());
      statement.setString(5, attachment.relation());
      statement.setString(6, attachment.hashAlgorithm());
      statement.setString(7, attachment.hashValue());
      statement.setBlob(8, blob);
      statement.setLong(9, Integer.toUnsignedLong(dataLength));
      statement.executeUpdate();
    }
  }

  private static CAItemAttachmentID attachmentIdFromBytes(
    final byte[] bytes)
  {
    return new CAItemAttachmentID(CADatabaseBytes.uuidFromBytes(bytes));
  }

  private static byte[] attachmentIdBytes(
    final CAItemAttachmentID id)
  {
    return CADatabaseBytes.uuidBytes(id.id());
  }

  private static void itemAttachmentPutUpdate(
    final Connection connection,
    final CAItemAttachment attachment,
    final CAByteArray data)
    throws SQLException, IOException
  {
    final var dataLength = data.data().length;
    final var blob = connection.createBlob();
    try (var output = blob.setBinaryStream(1L)) {
      output.write(data.data());
    }

    try (var statement =
           connection.prepareStatement(ITEM_ATTACHMENT_PUT_UPDATE)) {
      statement.setString(1, attachment.description());
      statement.setString(2, attachment.mediaType());
      statement.setString(3, attachment.relation());
      statement.setString(4, attachment.hashAlgorithm());
      statement.setString(5, attachment.hashValue());
      statement.setBlob(6, blob);
      statement.setLong(7, Integer.toUnsignedLong(dataLength));
      statement.setBytes(8, attachmentIdBytes(attachment.id()));
      statement.setBytes(9, CADatabaseBytes.itemIdBytes(attachment.itemId()));
      statement.executeUpdate();
    }
  }

  private static CAItemAttachment itemAttachmentFromResultWithData(
    final ResultSet result)
    throws SQLException, IOException
  {
    final var blob =
      result.getBlob("attachment_data");
    final var size =
      result.getLong("attachment_data_used");

    final var byteArrayStream = new ByteArrayOutputStream();
    try (var blobInput = blob.getBinaryStream()) {
      for (long index = 0L; Long.compareUnsigned(index, size) < 0; ++index) {
        byteArrayStream.write(blobInput.read());
      }
    }

    final var bytes =
      byteArrayStream.toByteArray();

    return new CAItemAttachment(
      attachmentIdFromBytes(result.getBytes("attachment_id")),
      CADatabaseBytes.itemIdFromBytes(result.getBytes("attachment_item_id")),
      result.getString("attachment_description"),
      result.getString("attachment_media_type"),
      result.getString("attachment_relation"),
      size,
      result.getString("attachment_hash_algorithm"),
      result.getString("attachment_hash_value"),
      Optional.of(new CAByteArray(bytes))
    );
  }

  private static CAItemAttachment itemAttachmentFromResultWithoutData(
    final ResultSet result)
    throws SQLException
  {
    return new CAItemAttachment(
      attachmentIdFromBytes(result.getBytes("attachment_id")),
      CADatabaseBytes.itemIdFromBytes(result.getBytes("attachment_item_id")),
      result.getString("attachment_description"),
      result.getString("attachment_media_type"),
      result.getString("attachment_relation"),
      result.getLong("attachment_data_used"),
      result.getString("attachment_hash_algorithm"),
      result.getString("attachment_hash_value"),
      Optional.empty()
    );
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

  private static TreeMap<String, CAItemMetadata> itemMetadataInner(
    final Connection connection,
    final CAItemID itemId)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(ITEM_METADATA_GET)) {
      statement.setBytes(1, CADatabaseBytes.itemIdBytes(itemId));

      try (var result = statement.executeQuery()) {
        final var metas = new TreeMap<String, CAItemMetadata>();
        while (result.next()) {
          final var name =
            result.getString("metadata_name");
          final var value =
            result.getString("metadata_value");
          final var itemMetadata =
            new CAItemMetadata(itemId, name, value);
          metas.put(name, itemMetadata);
        }
        return metas;
      }
    }
  }

  private static TreeMap<CAItemAttachmentID, CAItemAttachment> itemAttachmentsInner(
    final Connection connection,
    final CAItemID item,
    final boolean withData)
    throws SQLException, IOException
  {
    final var attachments =
      new TreeMap<CAItemAttachmentID, CAItemAttachment>();

    if (withData) {
      try (var statement =
             connection.prepareStatement(ITEM_ATTACHMENTS_GET)) {
        statement.setBytes(1, CADatabaseBytes.itemIdBytes(item));

        try (var results = statement.executeQuery()) {
          while (results.next()) {
            final var attachment =
              itemAttachmentFromResultWithData(results);
            attachments.put(attachment.id(), attachment);
          }
          return attachments;
        }
      }
    }

    try (var statement =
           connection.prepareStatement(ITEM_ATTACHMENTS_GET)) {
      statement.setBytes(1, CADatabaseBytes.itemIdBytes(item));

      try (var results = statement.executeQuery()) {
        while (results.next()) {
          final var attachment =
            itemAttachmentFromResultWithoutData(results);
          attachments.put(attachment.id(), attachment);
        }
        return attachments;
      }
    }
  }

  private static Optional<CAItemAttachment> itemAttachmentGetInner(
    final Connection connection,
    final CAItemAttachmentID id,
    final boolean withData)
    throws SQLException, IOException
  {
    if (withData) {
      try (var statement =
             connection.prepareStatement(ITEM_ATTACHMENT_GET)) {
        statement.setBytes(1, attachmentIdBytes(id));
        try (var result = statement.executeQuery()) {
          if (result.next()) {
            return Optional.of(itemAttachmentFromResultWithData(result));
          }
          return Optional.empty();
        }
      }
    }

    try (var statement =
           connection.prepareStatement(ITEM_ATTACHMENT_GET)) {
      statement.setBytes(1, attachmentIdBytes(id));
      try (var result = statement.executeQuery()) {
        if (result.next()) {
          return Optional.of(itemAttachmentFromResultWithoutData(result));
        }
        return Optional.empty();
      }
    }
  }

  private Object itemMetadataRemoveInner(
    final Connection connection,
    final CAItemMetadata metadata)
    throws SQLException
  {
    try (var statement =
           connection.prepareStatement(ITEM_METADATA_REMOVE)) {
      statement.setBytes(1, CADatabaseBytes.itemIdBytes(metadata.itemId()));
      statement.setString(2, metadata.name());
      statement.executeUpdate();
    }

    this.publishUpdate(metadata.itemId());
    return null;
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

  private static CAItemID itemForAttachment(
    final Connection connection,
    final CAItemAttachmentID id)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(ITEM_FOR_ATTACHMENT)) {
      statement.setBytes(1, CADatabaseBytes.uuidBytes(id.id()));
      try (var results = statement.executeQuery()) {
        results.next();
        return CADatabaseBytes.itemIdFromBytes(results.getBytes(1));
      }
    }
  }

  private void itemAttachmentRemoveInner(
    final Connection connection,
    final CAItemAttachmentID id)
    throws SQLException
  {
    final var itemId = itemForAttachment(connection, id);
    try (var statement = connection.prepareStatement(ITEM_ATTACHMENT_REMOVE)) {
      statement.setBytes(1, CADatabaseBytes.uuidBytes(id.id()));
      statement.executeUpdate();
    }
    this.publishRemove(id);
    this.publishUpdate(itemId);
  }

  private static void itemRepositInnerRemoveDelete(
    final Connection connection,
    final CAItemRepositRemove remove)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(ITEM_REPOSIT_REMOVE_DELETE)) {
      statement.setBytes(1, CADatabaseBytes.itemIdBytes(remove.item()));
      statement.setBytes(2, locationIdBytes(remove.location()));
      statement.executeUpdate();
    }
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
      statement.setBytes(2, CADatabaseBytes.itemIdBytes(remove.item()));
      statement.setBytes(3, locationIdBytes(remove.location()));
      statement.executeUpdate();
    } catch (final DerbySQLIntegrityConstraintViolationException e) {
      if (Objects.equals(e.getConstraintName(), "CHECK_ITEM_LOCATION_COUNT")) {
        throw new CADatabaseException(
          ERROR_PARAMETERS_INVALID,
          this.messages.format(
            "errorItemCountTooManyRemoved",
            remove.item().id(),
            Long.valueOf(currentCount),
            Long.valueOf(remove.count())
          )
        );
      }
      throw e;
    }
  }

  private CADatabaseException duplicateItem(
    final CAItemID id)
  {
    return new CADatabaseException(
      ERROR_DUPLICATE,
      this.messages.format("errorDuplicate", id.id(), "Item"),
      new NoSuchElementException()
    );
  }

  private CADatabaseException duplicateItemDeleted(
    final CAItemID id)
  {
    return new CADatabaseException(
      ERROR_DUPLICATE,
      this.messages.format("errorDuplicateDeleted", id.id(), "Item"),
      new NoSuchElementException()
    );
  }

  private CADatabaseException noSuchItem(
    final UUID item)
  {
    return new CADatabaseException(
      ERROR_NONEXISTENT,
      this.messages.format("errorNonexistent", item, "Item"),
      new NoSuchElementException()
    );
  }

  @Override
  public Map<CAItemAttachmentID, CAItemAttachment> itemAttachments(
    final CAItemID item,
    final boolean withData)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");

    return this.withSQLConnection(connection -> {
      this.itemCheck(connection, item, false);
      return itemAttachmentsInner(connection, item, withData);
    });
  }

  @Override
  public Optional<CAItemAttachment> itemAttachmentGet(
    final CAItemAttachmentID id,
    final boolean withData)
    throws CADatabaseException
  {
    Objects.requireNonNull(id, "id");

    return this.withSQLConnection(
      connection -> itemAttachmentGetInner(connection, id, withData));
  }

  @Override
  public void itemAttachmentRemove(
    final CAItemAttachmentID id)
    throws CADatabaseException
  {
    Objects.requireNonNull(id, "id");

    this.withSQLConnection(connection -> {
      this.itemAttachmentRemoveInner(connection, id);
      return null;
    });
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
  public CAItemLocations itemLocations()
    throws CADatabaseException
  {
    return this.withSQLConnection(
      CADatabaseModelQueriesItems::itemLocationsInner);
  }

  private static CAItemLocations itemLocationsInner(
    final Connection connection)
    throws SQLException
  {
    final var locations =
      new TreeMap<CALocationID, SortedMap<CAItemID, CAItemLocation>>();

    try (var statement = connection.prepareStatement(ITEM_LOCATIONS_LIST)) {
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
      statement.setBytes(1, CADatabaseBytes.itemIdBytes(remove.item()));
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
      item.count() - remove.count()
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
      statement.setBytes(1, CADatabaseBytes.itemIdBytes(add.item()));
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
      statement.setBytes(1, CADatabaseBytes.itemIdBytes(add.item()));
      statement.setBytes(2, locationIdBytes(add.location()));
      statement.setLong(3, add.count());
      statement.executeUpdate();
    }

    final var newCount = item.count() + add.count();
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
      statement.setBytes(2, CADatabaseBytes.itemIdBytes(item));
      statement.executeUpdate();
    } catch (final DerbySQLIntegrityConstraintViolationException e) {
      if (Objects.equals(e.getConstraintName(), "CHECK_NATURAL_COUNT")) {
        throw new CADatabaseException(
          ERROR_PARAMETERS_INVALID,
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
      statement.setBytes(2, CADatabaseBytes.itemIdBytes(add.item()));
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
      connection -> itemGetInner(connection, id, false));
  }

  private static Optional<CAItem> itemGetInner(
    final Connection connection,
    final CAItemID id,
    final boolean deleted)
    throws SQLException, IOException
  {
    try (var statement =
           connection.prepareStatement(ITEM_GET)) {
      statement.setBytes(1, CADatabaseBytes.itemIdBytes(id));
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
            itemAttachmentsInner(connection, itemId, false);
          final var itemMetadatas =
            itemMetadataInner(connection, itemId);
          final var itemTags =
            itemTagListInner(connection, itemId);

          return Optional.of(new CAItem(
            itemId,
            itemName,
            itemCount,
            itemMetadatas,
            itemAttachments,
            itemTags
          ));
        }
        return Optional.empty();
      }
    }
  }

  @Override
  public void itemCreate(
    final CAItemID id)
    throws CADatabaseException
  {
    Objects.requireNonNull(id, "id");

    this.withSQLConnection(connection -> {

      {
        final var existing = itemGetInner(connection, id, true);
        if (existing.isPresent()) {
          throw this.duplicateItemDeleted(id);
        }
      }

      {
        final var existing = itemGetInner(connection, id, false);
        if (existing.isPresent()) {
          throw this.duplicateItem(id);
        }
      }

      try (var statement = connection.prepareStatement(ITEM_CREATE)) {
        statement.setBytes(1, CADatabaseBytes.itemIdBytes(id));
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
      statement.setBytes(1, CADatabaseBytes.itemIdBytes(id));
      try (var results = statement.executeQuery()) {
        if (results.next()) {
          itemSum = results.getLong("item_count");
        } else {
          itemSum = 0L;
        }
      }
    }

    if (itemSum != expectedItemCount) {
      throw new CADatabaseException(
        ERROR_PARAMETERS_INVALID,
        this.messages.format(
          "errorItemCountStoreInvariant",
          id.id(),
          Long.valueOf(expectedItemCount),
          Long.valueOf(itemSum))
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
        statement.setBytes(2, CADatabaseBytes.itemIdBytes(id));
        statement.executeUpdate();
      }
      return null;
    });
  }

  @Override
  public Set<CAItemID> itemList()
    throws CADatabaseException
  {
    return this.withSQLConnection(connection -> {
      try (var statement =
             connection.prepareStatement(ITEM_LIST)) {
        statement.setBoolean(1, false);

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
  public void itemDelete(
    final CAItemID item)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");

    this.withSQLConnection(connection -> {
      final var currentItemDeleted =
        itemGetInner(connection, item, true);
      final var currentItemNotDeleted =
        itemGetInner(connection, item, false);
      final var currentItem =
        currentItemDeleted.or(() -> currentItemNotDeleted)
          .orElseThrow(() -> this.noSuchItem(item.id()));

      final var itemIdBytes = CADatabaseBytes.itemIdBytes(item);
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

      currentItem.attachments()
        .keySet()
        .forEach(this::publishRemove);

      this.publishRemove(item);
      return null;
    });
  }

  @Override
  public void itemDeleteMarkOnly(final CAItemID item)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");

    final var currentItem =
      this.itemGet(item)
        .orElseThrow(() -> this.noSuchItem(item.id()));

    this.withSQLConnection(connection -> {
      final var itemIdBytes = CADatabaseBytes.itemIdBytes(item);

      try (var statement =
             connection.prepareStatement(ITEM_DELETE_MARK_ONLY)) {
        statement.setBytes(1, itemIdBytes);
        statement.executeUpdate();
      }

      currentItem.attachments()
        .keySet()
        .forEach(this::publishRemove);

      this.publishRemove(item);
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
    final CAItemMetadata metadata)
    throws CADatabaseException
  {
    Objects.requireNonNull(metadata, "metadata");

    this.withSQLConnection(connection -> {
      this.itemCheck(connection, metadata.itemId(), false);

      final var metas =
        itemMetadataInner(connection, metadata.itemId());

      final var existing = metas.get(metadata.name());
      if (existing != null) {
        itemMetadataPutUpdate(connection, metadata);
      } else {
        itemMetadataPutInsert(connection, metadata);
      }

      this.publishUpdate(metadata.itemId());
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
    final CAItemMetadata metadata)
    throws CADatabaseException
  {
    Objects.requireNonNull(metadata, "metadata");

    this.withSQLConnection(connection -> {
      this.itemCheck(connection, metadata.itemId(), false);
      return this.itemMetadataRemoveInner(connection, metadata);
    });
  }

  @Override
  public void itemAttachmentPut(
    final CAItemAttachment attachment)
    throws CADatabaseException
  {
    Objects.requireNonNull(attachment, "attachment");

    final var data =
      attachment.data()
        .orElseThrow(() -> new CADatabaseException(
          ERROR_GENERAL,
          this.messages.format("errorAttachmentMissingData"))
        );

    this.withSQLConnection(connection -> {
      this.itemCheck(connection, attachment.itemId(), false);

      final var existing =
        itemAttachmentGetInner(connection, attachment.id(), false);

      if (existing.isPresent()) {
        itemAttachmentPutUpdate(connection, attachment, data);
      } else {
        itemAttachmentPutInsert(connection, attachment, data);
      }

      this.publishUpdate(attachment.itemId());
      this.publishUpdate(attachment.id());
      return null;
    });
  }

  private void itemCheck(
    final Connection connection,
    final CAItemID id,
    final boolean deleted)
    throws CADatabaseException, SQLException
  {
    try (var statement = connection.prepareStatement(ITEM_GET)) {
      statement.setBytes(1, CADatabaseBytes.itemIdBytes(id));
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
    return new CADatabaseException(
      ERROR_NONEXISTENT,
      this.messages.format("errorNonexistent", location.id(), "Location"),
      new NoSuchElementException()
    );
  }
}
