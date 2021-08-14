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

import com.io7m.cardant.database.api.CADatabaseEventTransactionCommitted;
import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseQueriesType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.model.CAModelDatabaseQueriesType;
import com.io7m.junreachable.UnimplementedCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * A database transaction.
 */

public final class CADatabaseDerbyTransaction
  implements CADatabaseTransactionType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CADatabaseDerbyTransaction.class);

  private final CADatabaseMessages messages;
  private final CADatabaseDerbyConnection connection;
  private final HashMap<Class<?>, CADatabaseTransactionListenerType> listeners;

  CADatabaseDerbyTransaction(
    final CADatabaseMessages inMessages,
    final CADatabaseDerbyConnection inConnection)
  {
    this.messages =
      Objects.requireNonNull(inMessages, "messages");
    this.connection =
      Objects.requireNonNull(inConnection, "connection");
    this.listeners =
      new HashMap<>(1);
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
      final var database = this.connection.database();
      database.publishEvent(new CADatabaseEventTransactionCommitted());
      this.listeners.values().forEach(listener -> listener.onCommit(this));
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
      this.listeners.values().forEach(listener -> listener.onRollback(this));
    } catch (final SQLException e) {
      throw this.messages.ofSQLException("errorConnectionRollback", e);
    }
  }

  /**
   * Register a listener, or return an existing one.
   *
   * @param clazz        The listener class
   * @param newListeners The listener constructor
   * @param <T>          The type of listener
   *
   * @return A new or existing listener
   */

  <T extends CADatabaseTransactionListenerType>
  T registerListener(
    final Class<T> clazz,
    final Supplier<T> newListeners)
  {
    Objects.requireNonNull(clazz, "clazz");
    Objects.requireNonNull(newListeners, "listeners");

    final var listener = this.listeners.get(clazz);
    if (listener != null) {
      return (T) listener;
    }
    final var newListener = newListeners.get();
    this.listeners.put(clazz, newListener);
    return newListener;
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
  {
    if (Objects.equals(queriesClass, CAModelDatabaseQueriesType.class)) {
      return (P) new CADatabaseModelQueries(this);
    }

    throw new UnimplementedCodeException();
  }
}
