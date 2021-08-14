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
import com.io7m.cardant.model.CAModelDatabaseQueriesTagsType;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_DUPLICATE;
import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_NONEXISTENT;
import static com.io7m.cardant.database.derby.internal.CADerbyConstants.LANG_DUPLICATE_KEY_CONSTRAINT;

/**
 * Internal database calls for the inventory.
 */

public final class CADatabaseModelQueriesTags extends
  CADatabaseModelQueriesAbstract
  implements CAModelDatabaseQueriesTagsType
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

  private static final String TAG_LIST = """
    SELECT * FROM cardant.tags ORDER BY tag_name
    """;

  private static final String ITEM_TAG_REMOVE_ALL_BY_TAG = """
    DELETE FROM cardant.item_tags 
      WHERE tag_id = ?
    """;

  private final CADatabaseMessages messages;

  CADatabaseModelQueriesTags(
    final CADatabaseDerbyTransaction inTransaction)
  {
    super(inTransaction);
    this.messages = inTransaction.messages();
  }

  private static void tagCreate(
    final Connection connection,
    final CATag tag)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(TAG_INSERT)) {
      statement.setBytes(1, CADatabaseBytes.tagIdBytes(tag.id()));
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
      statement.setBytes(2, CADatabaseBytes.tagIdBytes(tag.id()));
      statement.executeUpdate();
    }
  }

  static CATag tagParse(
    final ResultSet result)
    throws SQLException
  {
    return new CATag(
      CADatabaseBytes.tagIdFromBytes(result.getBytes("tag_id")),
      result.getString("tag_name")
    );
  }

  private static Optional<CATag> tagGetInner(
    final Connection connection,
    final CATagID id)
    throws SQLException
  {
    try (var statement = connection.prepareStatement(TAG_GET)) {
      statement.setBytes(1, CADatabaseBytes.tagIdBytes(id));

      try (var result = statement.executeQuery()) {
        if (!result.next()) {
          return Optional.empty();
        }
        return Optional.of(tagParse(result));
      }
    }
  }

  void tagCheck(
    final Connection connection,
    final CATagID id)
    throws SQLException, CADatabaseException
  {
    try (var statement = connection.prepareStatement(TAG_GET)) {
      statement.setBytes(1, CADatabaseBytes.tagIdBytes(id));

      try (var result = statement.executeQuery()) {
        if (!result.next()) {
          throw this.noSuchTag(id);
        }
      }
    }
  }

  @Override
  public Optional<CATag> tagGet(
    final CATagID id)
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

        this.publishUpdate(tag.id());
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
      final var idBytes = CADatabaseBytes.tagIdBytes(tag.id());
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

      this.publishRemove(tag.id());
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
    final CATagID id)
  {
    return new CADatabaseException(
      ERROR_NONEXISTENT,
      this.messages.format("errorNonexistent", id.id(), "Tag"),
      new NoSuchElementException()
    );
  }
}
