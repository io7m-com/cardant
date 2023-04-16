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

package com.io7m.cardant.database.postgres.internal;

import com.io7m.cardant.database.api.CADatabaseConnectionType;
import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseRole;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSql;

record CADatabaseConnection(
  CADatabase database,
  Connection connection,
  CADatabaseRole role,
  Span connectionSpan)
  implements CADatabaseConnectionType
{
  @Override
  public CADatabaseTransactionType openTransaction()
    throws CADatabaseException
  {
    final var transactionSpan =
      this.database.tracer()
        .spanBuilder("CADatabaseTransaction")
        .setParent(Context.current().with(this.connectionSpan))
        .startSpan();

    try {
      final var t =
        new CADatabaseTransaction(
          this,
          transactionSpan
        );

      this.database.counterTransactions().add(1L);
      t.setRole(this.role);
      t.commit();
      return t;
    } catch (final SQLException e) {
      transactionSpan.recordException(e);
      transactionSpan.end();
      throw new CADatabaseException(
        errorSql(),
        e.getMessage(),
        e,
        Collections.emptySortedMap()
      );
    }
  }

  @Override
  public void close()
    throws CADatabaseException
  {
    try {
      if (!this.connection.isClosed()) {
        this.connection.close();
      }
    } catch (final SQLException e) {
      this.connectionSpan.recordException(e);
      throw new CADatabaseException(
        errorSql(),
        e.getMessage(),
        e,
        Collections.emptySortedMap()
      );
    } finally {
      this.connectionSpan.end();
    }
  }
}
