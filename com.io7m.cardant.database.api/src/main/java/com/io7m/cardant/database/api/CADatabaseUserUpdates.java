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

package com.io7m.cardant.database.api;

import com.io7m.cardant.model.CAUser;
import com.io7m.medrina.api.MSubject;

import java.util.Set;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;

/**
 * Functions to update users.
 */

public final class CADatabaseUserUpdates
{
  private CADatabaseUserUpdates()
  {

  }

  /**
   * Merge the given user with the user in the database.
   *
   * @param database The database
   * @param user     The user
   *
   * @return The merged user
   *
   * @throws CADatabaseException On errors
   */

  public static CAUser userMerge(
    final CADatabaseType database,
    final CAUser user)
    throws CADatabaseException
  {
    try (var connection = database.openConnection(CARDANT)) {
      return userMerge(connection, user);
    }
  }

  /**
   * Merge the given user with the user in the database.
   *
   * @param connection The database connection
   * @param user       The user
   *
   * @return The merged user
   *
   * @throws CADatabaseException On errors
   */

  public static CAUser userMerge(
    final CADatabaseConnectionType connection,
    final CAUser user)
    throws CADatabaseException
  {
    try (var transaction = connection.openTransaction()) {
      transaction.setUserId(user.userId());
      final var r = userMerge(transaction, user);
      transaction.commit();
      return r;
    }
  }

  /**
   * Merge the given user with the user in the database.
   *
   * @param transaction The database transaction
   * @param user        The user
   *
   * @return The merged user
   *
   * @throws CADatabaseException On errors
   */

  public static CAUser userMerge(
    final CADatabaseTransactionType transaction,
    final CAUser user)
    throws CADatabaseException
  {
    final var get =
      transaction.queries(CADatabaseQueriesUsersType.GetType.class);
    final var put =
      transaction.queries(CADatabaseQueriesUsersType.PutType.class);

    final var existingOpt =
      get.execute(user.userId());

    if (existingOpt.isPresent()) {
      final var existing = existingOpt.get();
      final var merged =
        new CAUser(
          user.userId(),
          user.name(),
          existing.subject()
        );
      put.execute(merged);
      return merged;
    }

    final var merged =
      new CAUser(
        user.userId(),
        user.name(),
        new MSubject(Set.of())
      );
    put.execute(merged);
    return merged;
  }
}
