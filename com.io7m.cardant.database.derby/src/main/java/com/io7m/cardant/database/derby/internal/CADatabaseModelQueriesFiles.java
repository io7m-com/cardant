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
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAFileType.CAFileWithData;
import com.io7m.cardant.model.CAFileType.CAFileWithoutData;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAModelDatabaseQueriesFilesType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_NONEXISTENT;
import static com.io7m.cardant.database.derby.internal.CADatabaseBytes.fileIdBytes;
import static com.io7m.cardant.database.derby.internal.CADatabaseBytes.fileIdFromBytes;
import static com.io7m.cardant.database.derby.internal.CADatabaseBytes.itemIdFromBytes;

/**
 * Internal database calls for the inventory.
 */

public final class CADatabaseModelQueriesFiles extends
  CADatabaseModelQueriesAbstract
  implements CAModelDatabaseQueriesFilesType
{
  private static final String FILE_PUT_INSERT = """
    INSERT INTO cardant.files (
      id,
      description,
      media_type,
      hash_algorithm,
      hash_value,
      data,
      data_used
    ) VALUES (
      ?, ?, ?, ?, ?, ?, ?
    )
    """;
  private static final String FILE_PUT_UPDATE = """
    UPDATE cardant.files
      SET description = ?,
          media_type = ?,
          hash_algorithm = ?,
          hash_value = ?,
          data = ?,
          data_used = ?
      WHERE id = ?
    """;
  private static final String FILE_GET = """
    SELECT
      f.id,
      f.description,
      f.media_type,
      f.hash_algorithm,
      f.hash_value,
      f.data,
      f.data_used
    FROM cardant.files f
      WHERE f.id = ?
    """;
  private static final String FILE_DELETE = """
    DELETE FROM cardant.files f
      WHERE f.id = ?
    """;
  private static final String FILE_DELETE_ATTACHMENTS_GET = """
    SELECT
      ia.file_id,
      ia.item_id,
      ia.relation
    FROM
      cardant.item_attachments ia
    WHERE
      ia.file_id = ?
    """;
  private static final String FILE_DELETE_ATTACHMENTS = """
    DELETE FROM cardant.item_attachments ia
      WHERE ia.file_id = ?
    """;

  private final CADatabaseMessages messages;

  CADatabaseModelQueriesFiles(
    final CADatabaseDerbyTransaction inTransaction)
  {
    super(inTransaction);
    this.messages = inTransaction.messages();
  }

  private static void filePutUpdate(
    final Connection connection,
    final CAFileType file)
    throws SQLException, IOException
  {
    if (file instanceof CAFileWithData withData) {
      final var dataLength = withData.data().data().length;
      final var blob = connection.createBlob();
      try (var output = blob.setBinaryStream(1L)) {
        output.write(withData.data().data());
      }

      try (var statement =
             connection.prepareStatement(FILE_PUT_UPDATE)) {
        statement.setString(1, file.description());
        statement.setString(2, file.mediaType());
        statement.setString(3, file.hashAlgorithm());
        statement.setString(4, file.hashValue());
        statement.setBlob(5, blob);
        statement.setLong(6, Integer.toUnsignedLong(dataLength));
        statement.setBytes(7, fileIdBytes(file.id()));
        statement.executeUpdate();
      }
    }
  }

  private static void filePutInsert(
    final Connection connection,
    final CAFileType file)
    throws SQLException, IOException
  {
    if (file instanceof CAFileWithoutData withoutData) {
      throw new IllegalStateException();
    }

    if (file instanceof CAFileWithData withData) {
      final var dataLength = withData.data().data().length;
      final var blob = connection.createBlob();
      try (var output = blob.setBinaryStream(1L)) {
        output.write(withData.data().data());
      }

      try (var statement =
             connection.prepareStatement(FILE_PUT_INSERT)) {
        statement.setBytes(1, fileIdBytes(file.id()));
        statement.setString(2, file.description());
        statement.setString(3, file.mediaType());
        statement.setString(4, file.hashAlgorithm());
        statement.setString(5, file.hashValue());
        statement.setBlob(6, blob);
        statement.setLong(7, Integer.toUnsignedLong(dataLength));
        statement.executeUpdate();
      }
    }
  }

  private static CAFileWithData fileFromResultWithData(
    final ResultSet result)
    throws SQLException, IOException
  {
    final var blob =
      result.getBlob("data");
    final var size =
      result.getLong("data_used");

    final var byteArrayStream = new ByteArrayOutputStream();
    try (var blobInput = blob.getBinaryStream()) {
      for (long index = 0L; Long.compareUnsigned(index, size) < 0; ++index) {
        byteArrayStream.write(blobInput.read());
      }
    }

    final var bytes =
      byteArrayStream.toByteArray();

    return new CAFileWithData(
      fileIdFromBytes(result.getBytes("id")),
      result.getString("description"),
      result.getString("media_type"),
      size,
      result.getString("hash_algorithm"),
      result.getString("hash_value"),
      new CAByteArray(bytes)
    );
  }

  private static CAFileWithoutData fileFromResultWithoutData(
    final ResultSet result)
    throws SQLException
  {
    return new CAFileWithoutData(
      fileIdFromBytes(result.getBytes("id")),
      result.getString("description"),
      result.getString("media_type"),
      result.getLong("data_used"),
      result.getString("hash_algorithm"),
      result.getString("hash_value")
    );
  }

  @Override
  public void filePut(
    final CAFileType file)
    throws CADatabaseException
  {
    this.withSQLConnection(connection -> {
      this.filePutInner(connection, file);
      return null;
    });
  }

  @Override
  public Optional<CAFileType> fileGet(
    final CAFileID file,
    final boolean withData)
    throws CADatabaseException
  {
    return this.withSQLConnection(
      connection -> fileGetInner(connection, file, withData));
  }

  @Override
  public void fileRemove(
    final CAFileID file)
    throws CADatabaseException
  {
    this.withSQLConnection(connection -> {
      this.fileRemoveInner(connection, file);
      return null;
    });
  }

  private void filePutInner(
    final Connection connection,
    final CAFileType file)
    throws SQLException, IOException
  {
    final var existing =
      fileGetInner(connection, file.id(), false);

    if (existing.isPresent()) {
      filePutUpdate(connection, file);
    } else {
      filePutInsert(connection, file);
    }

    this.publishUpdate(file.id());
  }

  private void fileRemoveInner(
    final Connection connection,
    final CAFileID file)
    throws SQLException
  {
    final var deletions = new ArrayList<FileAttachmentDeletion>();
    try (var statement = connection.prepareStatement(FILE_DELETE_ATTACHMENTS_GET)) {
      statement.setBytes(1, fileIdBytes(file));
      try (var results = statement.executeQuery()) {
        while (results.next()) {
          deletions.add(
            new FileAttachmentDeletion(
              itemIdFromBytes(results.getBytes("item_id")),
              fileIdFromBytes(results.getBytes("file_id")),
              results.getString("relation")
            )
          );
        }
      }
    }

    try (var statement = connection.prepareStatement(FILE_DELETE_ATTACHMENTS)) {
      statement.setBytes(1, fileIdBytes(file));
      statement.executeUpdate();
    }

    try (var statement = connection.prepareStatement(FILE_DELETE)) {
      statement.setBytes(1, fileIdBytes(file));
      statement.executeUpdate();
    }

    this.publishRemove(file);
    for (final var deletion : deletions) {
      this.publishUpdate(deletion.item);
    }
  }

  /**
   * Get a file.
   *
   * @param connection The connection
   * @param file       The file ID
   * @param withData   {@code true} if the file data should be read
   *
   * @return A file
   *
   * @throws SQLException On errors
   * @throws IOException  On errors
   */

  public static Optional<CAFileType> fileGetInner(
    final Connection connection,
    final CAFileID file,
    final boolean withData)
    throws SQLException, IOException
  {
    if (withData) {
      try (var statement =
             connection.prepareStatement(FILE_GET)) {
        statement.setBytes(1, fileIdBytes(file));
        try (var result = statement.executeQuery()) {
          if (result.next()) {
            return Optional.of(fileFromResultWithData(result));
          }
          return Optional.empty();
        }
      }
    }

    try (var statement =
           connection.prepareStatement(FILE_GET)) {
      statement.setBytes(1, fileIdBytes(file));
      try (var result = statement.executeQuery()) {
        if (result.next()) {
          return Optional.of(fileFromResultWithoutData(result));
        }
        return Optional.empty();
      }
    }
  }

  /**
   * Check a file exists.
   *
   * @param connection The connection
   * @param file       The file
   *
   * @throws SQLException        On errors
   * @throws IOException         On errors
   * @throws CADatabaseException On errors
   */

  public void fileCheck(
    final Connection connection,
    final CAFileID file)
    throws SQLException, IOException, CADatabaseException
  {
    fileGetInner(connection, file, false)
      .orElseThrow(() -> this.noSuchFile(file.id()));
  }

  /**
   * No such file exists.
   *
   * @param file The file
   *
   * @return An exception
   */

  public CADatabaseException noSuchFile(
    final UUID file)
  {
    final var attributes = new HashMap<String, String>();
    attributes.put(
      this.messages.format("object"),
      file.toString());
    attributes.put(
      this.messages.format("type"),
      this.messages.format("file"));

    return new CADatabaseException(
      ERROR_NONEXISTENT,
      attributes,
      this.messages.format("errorNonexistent")
    );
  }

  private record FileAttachmentDeletion(
    CAItemID item,
    CAFileID file,
    String relation)
  {

  }
}
