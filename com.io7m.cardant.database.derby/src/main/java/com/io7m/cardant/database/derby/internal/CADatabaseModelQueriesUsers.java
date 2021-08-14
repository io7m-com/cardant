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
import com.io7m.cardant.model.CAModelCADatabaseQueriesUsersType;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.model.CAUserID;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Internal database calls for the inventory.
 */

public final class CADatabaseModelQueriesUsers
  extends CADatabaseModelQueriesAbstract
  implements CAModelCADatabaseQueriesUsersType
{

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

  private final CADatabaseMessages messages;

  CADatabaseModelQueriesUsers(
    final CADatabaseDerbyTransaction inTransaction)
  {
    super(inTransaction);
    this.messages = inTransaction.messages();
  }

  private static CAUserID userIdFromBytes(
    final byte[] bytes)
  {
    return new CAUserID(CADatabaseBytes.uuidFromBytes(bytes));
  }

  private static byte[] userIdBytes(
    final CAUserID id)
  {
    return CADatabaseBytes.uuidBytes(id.id());
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
}
