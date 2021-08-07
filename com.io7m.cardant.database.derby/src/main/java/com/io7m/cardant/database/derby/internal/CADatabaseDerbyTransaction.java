/*
 * Copyright © 2019 Mark Raynsford <code@io7m.com> http://io7m.com
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
import com.io7m.cardant.database.api.CADatabaseQueriesType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.model.CAModelCADatabaseQueriesType;
import com.io7m.junreachable.UnimplementedCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Objects;

/**
 * A database transaction.
 */

public final class CADatabaseDerbyTransaction implements
  CADatabaseTransactionType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CADatabaseDerbyTransaction.class);

  private final CADatabaseMessages messages;
  private final CADatabaseDerbyConnection connection;

  CADatabaseDerbyTransaction(
    final CADatabaseMessages inMessages,
    final CADatabaseDerbyConnection inConnection)
  {
    this.messages =
      Objects.requireNonNull(inMessages, "messages");
    this.connection =
      Objects.requireNonNull(inConnection, "connection");
  }

  @Override
  public void close()
    throws CADatabaseException
  {
    this.rollback();
  }

  @Override
  public void commit()
    throws CADatabaseException
  {
    try {
      LOG.trace("commit");
      this.connection.sqlConnection().commit();
    } catch (final SQLException e) {
      throw this.messages.ofSQLException("errorConnectionCommit", e);
    }
  }

  @Override
  public void rollback()
    throws CADatabaseException
  {
    try {
      LOG.trace("rollback");
      this.connection.sqlConnection().rollback();
    } catch (final SQLException e) {
      throw this.messages.ofSQLException("errorConnectionRollback", e);
    }
  }

  /**
   * @return The underlying database connection
   */

  CADatabaseDerbyConnection connection()
  {
    return this.connection;
  }

  /**
   * @return The database messages
   */

  CADatabaseMessages messages()
  {
    return this.messages;
  }

  @Override
  public <P extends CADatabaseQueriesType> P queries(
    final Class<P> queriesClass)
    throws CADatabaseException
  {
    if (Objects.equals(queriesClass, CAModelCADatabaseQueriesType.class)) {
      return (P) new CADatabaseModelQueries(this);
    }

    throw new UnimplementedCodeException();
  }
}
