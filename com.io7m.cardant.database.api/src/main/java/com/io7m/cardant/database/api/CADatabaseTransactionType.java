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

package com.io7m.cardant.database.api;

/**
 * A database transaction.
 */

public interface CADatabaseTransactionType
  extends AutoCloseable
{
  /**
   * Close this transaction. The transaction is rolled back if {@link #commit()} has not been
   * called, or if changes have been made since the last call to {@link #commit()}.
   *
   * @throws CADatabaseException On errors
   */

  @Override
  void close()
    throws CADatabaseException;

  /**
   * Commit any changes made.
   *
   * @throws CADatabaseException On errors
   */

  void commit()
    throws CADatabaseException;

  /**
   * Roll back any changes made since the last call to {@link #commit()} (or everything, if {@link
   * #commit()} has never been called).
   *
   * @throws CADatabaseException On errors
   */

  void rollback()
    throws CADatabaseException;

  /**
   * @param queriesClass The precise type of queries to execute in the transaction
   * @param <P>          The precise type of queries to execute
   *
   * @return The transaction queries
   *
   * @throws CADatabaseException On errors
   */

  <P extends CADatabaseQueriesType>
  P queries(Class<P> queriesClass)
    throws CADatabaseException;
}
