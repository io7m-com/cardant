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
import com.io7m.cardant.model.CAIdType;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_GENERAL;

/**
 * The abstract base for database queries.
 */

public abstract class CADatabaseModelQueriesAbstract
{
  private final CADatabaseDerbyTransaction transaction;
  private final CADatabaseModelTransactionListener listener;

  protected CADatabaseModelQueriesAbstract(
    final CADatabaseDerbyTransaction inTransaction)
  {
    this.transaction =
      Objects.requireNonNull(inTransaction, "transaction");

    this.listener =
      this.transaction.registerListener(
        CADatabaseModelTransactionListener.class,
        CADatabaseModelTransactionListener::new
      );
  }

  protected final void publishUpdate(
    final CAIdType id)
  {
    this.listener.publishUpdate(id);
  }

  protected final void publishRemove(
    final CAIdType id)
  {
    this.listener.publishRemove(id);
  }

  protected final <T> T withSQLConnection(
    final CADatabaseWithSQLConnectionType<T> with)
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
}
