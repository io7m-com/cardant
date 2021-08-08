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

import com.io7m.cardant.database.api.CADatabaseConnectionType;
import com.io7m.cardant.database.api.CADatabaseEventType;
import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseType;
import org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * A Derby database.
 */

public final class CADatabaseDerby implements CADatabaseType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CADatabaseDerby.class);

  private final CADatabaseMessages messages;
  private final EmbeddedConnectionPoolDataSource dataSource;
  private final SubmissionPublisher<CADatabaseEventType> events;

  /**
   * A Derby database.
   *
   * @param inEvents     An event receiver
   * @param inDataSource A data source
   * @param inMessages   A set of messages
   */

  public CADatabaseDerby(
    final SubmissionPublisher<CADatabaseEventType> inEvents,
    final CADatabaseMessages inMessages,
    final EmbeddedConnectionPoolDataSource inDataSource)
  {
    this.events =
      Objects.requireNonNull(inEvents, "inEvents");
    this.messages =
      Objects.requireNonNull(inMessages, "inMessages");
    this.dataSource =
      Objects.requireNonNull(inDataSource, "dataSource");
  }

  @Override
  public CADatabaseConnectionType openConnection()
    throws CADatabaseException
  {
    try {
      final var pooledConnection =
        this.dataSource.getPooledConnection();
      final var connection =
        pooledConnection.getConnection();

      connection.setAutoCommit(false);
      return new CADatabaseDerbyConnection(this, this.messages, connection);
    } catch (final SQLException e) {
      throw this.messages.ofSQLException("errorOpenConnection", e);
    }
  }

  @Override
  public Flow.Publisher<CADatabaseEventType> events()
  {
    return this.events;
  }

  @Override
  public void close()
  {
    LOG.debug("close");
    this.events.close();
  }

  void publishEvent(
    final CADatabaseEventType event)
  {
    this.events.submit(event);
  }
}
