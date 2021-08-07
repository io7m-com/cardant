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
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAModelCADatabaseQueriesType;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.model.CAUserID;
import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
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
import static com.io7m.cardant.database.derby.internal.CADerbyConstants.LANG_DUPLICATE_KEY_CONSTRAINT;
import static java.nio.ByteOrder.BIG_ENDIAN;

/**
 * Internal database calls for the inventory.
 */

public final class CADatabaseModelQueries
  implements CAModelCADatabaseQueriesType
{
  private static final String TAG_INSERT = """
    INSERT INTO cardant.tags (tag_id, tag_name) 
      VALUES (?, ?)
    """;

  private static final String TAG_GET = """
    SELECT * FROM cardant.tags 
      WHERE tag_id = ?
    """;

  private static final String TAG_UPDATE = """
    UPDATE cardant.tags 
      SET tag_name = ? 
      WHERE tag_id = ?
    """;

  private static final String TAG_DELETE = """
    DELETE FROM cardant.tags
      WHERE tag_id = ?
    """;

  private static final String ITEM_CREATE = """
    INSERT INTO cardant.items (item_id, item_name, item_count) 
      VALUES (?, ?, ?)
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

  private static final String TAG_LIST = """
    SELECT * FROM cardant.tags ORDER BY tag_name
    """;

  private static final String ITEM_GET = """
    SELECT * FROM cardant.items 
      WHERE item_id = ?
    """;

  private static final String ITEM_LIST = """
    SELECT item_id, item_name FROM cardant.items
    """;

  private static final String ITEM_DELETE = """
    DELETE FROM cardant.items
      WHERE item_id = ?
    """;

  private static final String ITEM_TAG_REMOVE = """
    DELETE FROM cardant.item_tags 
      WHERE tag_item_id = ? AND tag_id = ?
    """;

  private static final String ITEM_TAG_REMOVE_ALL_BY_TAG = """
    DELETE FROM cardant.item_tags 
      WHERE tag_id = ?
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
      cia.attachment_media_type,
      cia.attachment_relation,
      cia.attachment_hash_algorithm,
      cia.attachment_hash_value,
      cia.attachment_data,
      cia.attachment_data_used
    FROM cardant.item_attachments cia
      WHERE attachment_id = ? AND attachment_item_id = ?
    """;

  private static final String ITEM_ATTACHMENT_PUT_INSERT = """
    INSERT INTO cardant.item_attachments (
      attachment_id,
      attachment_item_id,
      attachment_media_type,
      attachment_relation,
      attachment_hash_algorithm,
      attachment_hash_value,
      attachment_data,
      attachment_data_used
    ) VALUES (
      ?, ?, ?, ?, ?, ?, ?, ?
    )
    """;

  private static final String ITEM_ATTACHMENT_PUT_UPDATE = """
    UPDATE cardant.item_attachments
      SET attachment_media_type = ?,
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

  private static final String ITEM_ATTACHMENTS_REMOVE_ALL_BY_ITEM = """
    DELETE FROM cardant.item_attachments
      WHERE attachment_item_id = ?
    """;

  private static final String USER_GET = """
    SELECT
      u.user_id,
      u.user_name,
      u.user_pass_hash,
      u.user_pass_salt,
      u.user_pass_algo
    FROM cardant.users u
      WHERE user_id = ?
    """;

  private static final String USER_PUT_INSERT = """
    INSERT INTO cardant.users (
      user_id,
      user_name,
      user_pass_hash,
      user_pass_salt,
      user_pass_algo
    ) VALUES (?, ?, ?, ?, ?)
    """;

  private static final String USER_PUT_UPDATE = """
    UPDATE cardant.users
      SET user_name = ?,
          user_pass_hash = ?,
          user_pass_salt = ?,
          user_pass_algo = ?
      WHERE user_id = ?
    """;

  private static final String USER_LIST = """
    SELECT
      u.user_id,
      u.user_name,
      u.user_pass_hash,
      u.user_pass_salt,
      u.user_pass_algo
    FROM cardant.users u
    """;

  private static final String USER_GET_BY_NAME = """
    SELECT
      u.user_id,
      u.user_name,
      u.user_pass_hash,
      u.user_pass_salt,
      u.user_pass_algo
    FROM cardant.users u
      WHERE user_name = ?
    """;

  private final CADatabaseDerbyTransaction transaction;
  private final CADatabaseMessages messages;

  CADatabaseModelQueries(
    final CADatabaseDerbyTransaction inTransaction)
  {
    this.transaction =
      Objects.requireNonNull(inTransaction, "transaction");
    this.messages =
      this.transaction.messages();
  }

  private static byte[] uuidBytes(
    final UUID id)
  {
    final var buffer = ByteBuffer.allocate(16).order(BIG_ENDIAN);
    buffer.putLong(0, id.getMostSignificantBits());
    buffer.putLong(8, id.getLeastSignificantBits());
    return buffer.array();
  }

  private static UUID uuidFromBytes(
    final byte[] bytes)
  {
    final var buffer = ByteBuffer.wrap(bytes).order(BIG_ENDIAN);
    final var msb = buffer.getLong(0);
    final var lsb = buffer.getLong(8);
    return new UUID(msb, lsb);
  }

  private static CAItemID itemIdFromBytes(
    final byte[] bytes)
  {
    return new CAItemID(uuidFromBytes(bytes));
  }

  private static byte[] itemIdBytes(
    final CAItemID id)
  {
    return uuidBytes(id.id());
  }

  private static CAItemAttachmentID attachmentIdFromBytes(
    final byte[] bytes)
  {
    return new CAItemAttachmentID(uuidFromBytes(bytes));
  }

  private static byte[] attachmentIdBytes(
    final CAItemAttachmentID id)
  {
    return uuidBytes(id.id());
  }

  private static CAUserID userIdFromBytes(
    final byte[] bytes)
  {
    return new CAUserID(uuidFromBytes(bytes));
  }

  private static byte[] userIdBytes(
    final CAUserID id)
  {
    return uuidBytes(id.id());
  }

  private static void tagCreate(
    final Connection connection,
    final CATag tag)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(TAG_INSERT)) {
      statement.setBytes(1, uuidBytes(tag.id()));
      statement.setString(2, tag.name());
      statement.executeUpdate();
    }
  }

  private static void tagUpdate(
    final Connection connection,
    final CATag tag)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(TAG_UPDATE)) {
      statement.setString(1, tag.name());
      statement.setBytes(2, uuidBytes(tag.id()));
      statement.executeUpdate();
    }
  }

  private static CATag tagParse(
    final ResultSet result)
    throws SQLException
  {
    return new CATag(
      uuidFromBytes(result.getBytes("tag_id")),
      result.getString("tag_name")
    );
  }

  private static void itemMetadataPutInsert(
    final Connection connection,
    final CAItemMetadata metadata)
    throws SQLException
  {
    try (var statement =
           connection.prepareStatement(ITEM_METADATA_PUT_INSERT)) {
      statement.setBytes(1, itemIdBytes(metadata.itemId()));
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
      statement.setBytes(2, itemIdBytes(metadata.itemId()));
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
      statement.setBytes(2, itemIdBytes(attachment.itemId()));
      statement.setString(3, attachment.mediaType());
      statement.setString(4, attachment.relation());
      statement.setString(5, attachment.hashAlgorithm());
      statement.setString(6, attachment.hashValue());
      statement.setBlob(7, blob);
      statement.setLong(8, Integer.toUnsignedLong(dataLength));
      statement.executeUpdate();
    }
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
      statement.setString(1, attachment.mediaType());
      statement.setString(2, attachment.relation());
      statement.setString(3, attachment.hashAlgorithm());
      statement.setString(4, attachment.hashValue());
      statement.setBlob(5, blob);
      statement.setLong(6, Integer.toUnsignedLong(dataLength));
      statement.setBytes(7, attachmentIdBytes(attachment.id()));
      statement.setBytes(8, itemIdBytes(attachment.itemId()));
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
      itemIdFromBytes(result.getBytes("attachment_item_id")),
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
      itemIdFromBytes(result.getBytes("attachment_item_id")),
      result.getString("attachment_media_type"),
      result.getString("attachment_relation"),
      result.getLong("attachment_data_used"),
      result.getString("attachment_hash_algorithm"),
      result.getString("attachment_hash_value"),
      Optional.empty()
    );
  }

  private static Object itemTagRemoveInner(
    final Connection connection,
    final CAItemID item,
    final CATag tag)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(ITEM_TAG_REMOVE)) {
      statement.setBytes(1, uuidBytes(item.id()));
      statement.setBytes(2, uuidBytes(tag.id()));
      statement.executeUpdate();
    }
    return null;
  }

  private static Object itemTagAddInner(
    final Connection connection,
    final CAItemID item,
    final CATag tag)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(ITEM_TAG_ADD)) {
      statement.setBytes(1, uuidBytes(item.id()));
      statement.setBytes(2, uuidBytes(tag.id()));
      statement.executeUpdate();
    }
    return null;
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
        statement.setBytes(1, itemIdBytes(item));

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
      statement.setBytes(1, itemIdBytes(item));

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
    final CAItemID itemId,
    final boolean withData)
    throws SQLException, IOException
  {
    if (withData) {
      try (var statement =
             connection.prepareStatement(ITEM_ATTACHMENT_GET)) {
        statement.setBytes(1, attachmentIdBytes(id));
        statement.setBytes(2, itemIdBytes(itemId));
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
      statement.setBytes(2, itemIdBytes(itemId));
      try (var result = statement.executeQuery()) {
        if (result.next()) {
          return Optional.of(itemAttachmentFromResultWithoutData(result));
        }
        return Optional.empty();
      }
    }
  }

  private static Object itemMetadataRemoveInner(
    final Connection connection,
    final CAItemMetadata metadata)
    throws SQLException
  {
    try (var statement =
           connection.prepareStatement(ITEM_METADATA_REMOVE)) {
      statement.setBytes(1, itemIdBytes(metadata.itemId()));
      statement.setString(2, metadata.name());
      statement.executeUpdate();
    }
    return null;
  }

  private static Optional<CATag> tagGetInner(
    final Connection connection,
    final UUID id)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(TAG_GET)) {
      statement.setBytes(1, uuidBytes(id));

      try (var result = statement.executeQuery()) {
        if (!result.next()) {
          return Optional.empty();
        }
        return Optional.of(tagParse(result));
      }
    }
  }

  private static SortedSet<CATag> itemTagListInner(
    final Connection connection,
    final CAItemID item)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(ITEM_TAG_LIST)) {
      statement.setBytes(1, uuidBytes(item.id()));

      try (var result = statement.executeQuery()) {
        final var tags = new TreeSet<CATag>();
        while (result.next()) {
          tags.add(tagParse(result));
        }
        return tags;
      }
    }
  }

  private static Optional<CAUser> userGetInner(
    final Connection connection,
    final CAUserID id)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(USER_GET)) {
      statement.setBytes(1, userIdBytes(id));
      try (var result = statement.executeQuery()) {
        if (!result.next()) {
          return Optional.empty();
        }
        return Optional.of(userParse(result));
      }
    }
  }

  private static CAUser userParse(
    final ResultSet result)
    throws SQLException
  {
    return new CAUser(
      userIdFromBytes(result.getBytes("user_id")),
      result.getString("user_name"),
      result.getString("user_pass_hash"),
      result.getString("user_pass_salt"),
      result.getString("user_pass_algo")
    );
  }

  private static void userPutInsert(
    final Connection connection,
    final CAUser user)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(USER_PUT_INSERT)) {
      statement.setBytes(1, userIdBytes(user.id()));
      statement.setString(2, user.name());
      statement.setString(3, user.passwordHash());
      statement.setString(4, user.passwordSalt());
      statement.setString(5, user.passwordAlgorithm());
      statement.executeUpdate();
    }
  }

  private static void userPutUpdate(
    final Connection connection,
    final CAUser user)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(USER_PUT_UPDATE)) {
      statement.setString(1, user.name());
      statement.setString(2, user.passwordHash());
      statement.setString(3, user.passwordSalt());
      statement.setString(4, user.passwordAlgorithm());
      statement.setBytes(5, userIdBytes(user.id()));
      statement.executeUpdate();
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

  private CADatabaseException duplicateTag(
    final CATag tag)
  {
    return new CADatabaseException(
      ERROR_DUPLICATE,
      this.messages.format("errorDuplicate", tag.name(), "Tag"),
      new NoSuchElementException()
    );
  }

  private CADatabaseException noSuchTag(
    final UUID id)
  {
    return new CADatabaseException(
      ERROR_NONEXISTENT,
      this.messages.format("errorNonexistent", id, "Tag"),
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

  private void itemCheck(
    final Connection connection,
    final CAItemID id)
    throws CADatabaseException, SQLException
  {
    try (var statement = connection.prepareStatement(ITEM_GET)) {
      statement.setBytes(1, itemIdBytes(id));

      try (var result = statement.executeQuery()) {
        if (!result.next()) {
          throw this.noSuchItem(id.id());
        }
      }
    }
  }

  private <T> T withSQLConnection(
    final WithSQLConnectionType<T> with)
    throws CADatabaseException
  {
    try {
      final var connection =
        this.transaction.connection()
          .sqlConnection();

      return with.call(connection);
    } catch (final SQLException | IOException e) {
      throw new CADatabaseException(ERROR_GENERAL, e.getMessage(), e);
    }
  }

  private void tagCheck(
    final Connection connection,
    final UUID id)
    throws SQLException, CADatabaseException
  {
    try (var statement = connection.prepareStatement(TAG_GET)) {
      statement.setBytes(1, uuidBytes(id));

      try (var result = statement.executeQuery()) {
        if (!result.next()) {
          throw this.noSuchTag(id);
        }
      }
    }
  }

  @Override
  public Optional<CATag> tagGet(
    final UUID id)
    throws CADatabaseException
  {
    Objects.requireNonNull(id, "id");

    return this.withSQLConnection(connection -> tagGetInner(connection, id));
  }

  @Override
  public void tagPut(
    final CATag tag)
    throws CADatabaseException
  {
    Objects.requireNonNull(tag, "tag");

    this.withSQLConnection(connection -> {
      final var existing = this.tagGet(tag.id());
      try {
        if (existing.isPresent()) {
          tagUpdate(connection, tag);
        } else {
          tagCreate(connection, tag);
        }
        return null;
      } catch (final SQLException e) {
        switch (e.getSQLState()) {
          case LANG_DUPLICATE_KEY_CONSTRAINT -> throw this.duplicateTag(tag);
          default -> throw e;
        }
      }
    });
  }

  @Override
  public void tagDelete(
    final CATag tag)
    throws CADatabaseException
  {
    Objects.requireNonNull(tag, "tag");

    this.withSQLConnection(connection -> {
      final var idBytes = uuidBytes(tag.id());
      try (var statement =
             connection.prepareStatement(ITEM_TAG_REMOVE_ALL_BY_TAG)) {
        statement.setBytes(1, idBytes);
        statement.executeUpdate();
      }
      try (var statement =
             connection.prepareStatement(TAG_DELETE)) {
        statement.setBytes(1, idBytes);
        statement.executeUpdate();
      }
      return null;
    });
  }

  @Override
  public SortedSet<CATag> tagList()
    throws CADatabaseException
  {
    return this.withSQLConnection(connection -> {
      try (var statement =
             connection.prepareStatement(TAG_LIST)) {
        try (var result = statement.executeQuery()) {
          final var tags = new TreeSet<CATag>();
          while (result.next()) {
            tags.add(tagParse(result));
          }
          return tags;
        }
      }
    });
  }

  @Override
  public Optional<CAItem> itemGet(
    final CAItemID id)
    throws CADatabaseException
  {
    Objects.requireNonNull(id, "id");

    return this.withSQLConnection(connection -> {
      try (var statement =
             connection.prepareStatement(ITEM_GET)) {
        statement.setBytes(1, itemIdBytes(id));

        try (var result = statement.executeQuery()) {
          if (result.next()) {
            final var itemId =
              itemIdFromBytes(result.getBytes("item_id"));
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
    });
  }

  @Override
  public void itemCreate(
    final CAItemID id)
    throws CADatabaseException
  {
    Objects.requireNonNull(id, "id");

    this.withSQLConnection(connection -> {
      final var existing = this.itemGet(id);
      if (existing.isPresent()) {
        throw this.duplicateItem(id);
      }

      try (var statement = connection.prepareStatement(ITEM_CREATE)) {
        statement.setBytes(1, itemIdBytes(id));
        statement.setString(2, "");
        statement.setLong(3, 0L);
        statement.executeUpdate();
      }
      return null;
    });
  }

  @Override
  public void itemCountSet(
    final CAItemID id,
    final long count)
    throws CADatabaseException
  {
    Objects.requireNonNull(id, "id");

    this.withSQLConnection(connection -> {
      this.itemCheck(connection, id);

      try (var statement = connection.prepareStatement(ITEM_COUNT_SET)) {
        statement.setLong(1, count);
        statement.setBytes(2, itemIdBytes(id));
        statement.executeUpdate();
      } catch (final DerbySQLIntegrityConstraintViolationException e) {
        if (Objects.equals(e.getConstraintName(), "CHECK_NATURAL_COUNT")) {
          throw new CADatabaseException(
            ERROR_PARAMETERS_INVALID,
            e.getMessage());
        }
        throw e;
      }
      return null;
    });
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
      this.itemCheck(connection, id);

      try (var statement = connection.prepareStatement(ITEM_NAME_SET)) {
        statement.setString(1, name);
        statement.setBytes(2, itemIdBytes(id));
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
        try (var result = statement.executeQuery()) {
          final var items = new HashSet<CAItemID>(32);
          while (result.next()) {
            items.add(itemIdFromBytes(result.getBytes("item_id")));
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
      this.itemCheck(connection, item);
      this.tagCheck(connection, tag.id());
      itemTagRemoveInner(connection, item, tag);
      return itemTagAddInner(connection, item, tag);
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
      this.itemCheck(connection, item);
      this.tagCheck(connection, tag.id());
      return itemTagRemoveInner(connection, item, tag);
    });
  }

  @Override
  public SortedSet<CATag> itemTagList(
    final CAItemID item)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");

    return this.withSQLConnection(connection -> {
      this.itemCheck(connection, item);

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
      this.itemCheck(connection, metadata.itemId());

      final var metas =
        itemMetadataInner(connection, metadata.itemId());

      final var existing = metas.get(metadata.name());
      if (existing != null) {
        itemMetadataPutUpdate(connection, metadata);
      } else {
        itemMetadataPutInsert(connection, metadata);
      }
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
      this.itemCheck(connection, item);
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
      this.itemCheck(connection, metadata.itemId());
      return itemMetadataRemoveInner(connection, metadata);
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
      this.itemCheck(connection, attachment.itemId());

      final var existing =
        itemAttachmentGetInner(
          connection,
          attachment.id(),
          attachment.itemId(),
          false);

      if (existing.isPresent()) {
        itemAttachmentPutUpdate(connection, attachment, data);
      } else {
        itemAttachmentPutInsert(connection, attachment, data);
      }
      return null;
    });
  }

  @Override
  public Map<CAItemAttachmentID, CAItemAttachment> itemAttachments(
    final CAItemID item,
    final boolean withData)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");

    return this.withSQLConnection(connection -> {
      this.itemCheck(connection, item);
      return itemAttachmentsInner(connection, item, withData);
    });
  }

  @Override
  public Optional<CAItemAttachment> itemAttachmentGet(
    final CAItemID item,
    final CAItemAttachmentID id,
    final boolean withData)
    throws CADatabaseException
  {
    Objects.requireNonNull(item, "item");
    Objects.requireNonNull(id, "id");

    return this.withSQLConnection(
      connection -> {
        this.itemCheck(connection, item);
        return itemAttachmentGetInner(connection, id, item, withData);
      });
  }

  @Override
  public void userPut(
    final CAUser user)
    throws CADatabaseException
  {
    Objects.requireNonNull(user, "user");

    this.withSQLConnection(connection -> {
      final var existing = userGetInner(connection, user.id());
      if (existing.isPresent()) {
        userPutUpdate(connection, user);
      } else {
        userPutInsert(connection, user);
      }
      return null;
    });
  }

  @Override
  public Optional<CAUser> userGet(
    final CAUserID id)
    throws CADatabaseException
  {
    Objects.requireNonNull(id, "id");

    return this.withSQLConnection(connection -> userGetInner(connection, id));
  }

  @Override
  public Optional<CAUser> userGetByName(
    final String name)
    throws CADatabaseException
  {
    Objects.requireNonNull(name, "name");

    return this.withSQLConnection(connection -> {
      try (var statement = connection.prepareStatement(USER_GET_BY_NAME)) {
        statement.setString(1, name);
        try (var result = statement.executeQuery()) {
          if (!result.next()) {
            return Optional.empty();
          }
          return Optional.of(userParse(result));
        }
      }
    });
  }

  @Override
  public Map<CAUserID, CAUser> userList()
    throws CADatabaseException
  {
    return this.withSQLConnection(connection -> {
      try (var statement = connection.prepareStatement(USER_LIST)) {
        try (var result = statement.executeQuery()) {
          final var users = new HashMap<CAUserID, CAUser>();
          while (result.next()) {
            final var user = userParse(result);
            users.put(user.id(), user);
          }
          return users;
        }
      }
    });
  }

  private interface WithSQLConnectionType<T>
  {
    T call(Connection connection)
      throws SQLException, CADatabaseException, IOException;
  }
}
