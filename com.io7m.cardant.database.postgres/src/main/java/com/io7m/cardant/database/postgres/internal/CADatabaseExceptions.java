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

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import org.jooq.exception.DataAccessException;
import org.postgresql.util.PSQLState;

import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorOperationNotPermitted;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSql;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSqlForeignKey;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSqlUnique;

/**
 * Functions to handle database exceptions.
 */

public final class CADatabaseExceptions
{
  private CADatabaseExceptions()
  {

  }

  /**
   * Handle a data access exception.
   *
   * @param transaction The transaction
   * @param e           The exception
   * @param attributes  The attributes
   *
   * @return The resulting exception
   */

  @SafeVarargs
  public static CADatabaseException handleDatabaseException(
    final CADatabaseTransactionType transaction,
    final DataAccessException e,
    final Map.Entry<String, String>... attributes)
  {
    final var tm = new TreeMap<String, String>();
    for (final var entry : attributes) {
      tm.put(entry.getKey(), entry.getValue());
    }
    return handleDatabaseException(transaction, e, tm);
  }

  /**
   * Handle a data access exception.
   *
   * @param transaction The transaction
   * @param e           The exception
   * @param attributes  The attributes
   *
   * @return The resulting exception
   */

  public static CADatabaseException handleDatabaseException(
    final CADatabaseTransactionType transaction,
    final DataAccessException e,
    final SortedMap<String, String> attributes)
  {
    final var m = e.getMessage();

    final CADatabaseException result = switch (e.sqlState()) {
      case "42501" -> {
        yield new CADatabaseException(
          errorOperationNotPermitted(),
          m,
          e,
          attributes);
      }

      default -> {
        PSQLState actual = null;
        for (final var possible : PSQLState.values()) {
          if (Objects.equals(possible.getState(), e.sqlState())) {
            actual = possible;
            break;
          }
        }

        if (actual != null) {
          yield switch (actual) {
            case FOREIGN_KEY_VIOLATION -> {
              yield new CADatabaseException(
                errorSqlForeignKey(),
                m,
                e,
                attributes);
            }
            case UNIQUE_VIOLATION -> {
              yield new CADatabaseException(errorSqlUnique(), m, e, attributes);
            }
            default -> new CADatabaseException(errorSql(), m, e, attributes);
          };
        }

        yield new CADatabaseException(errorSql(), m, e, attributes);
      }
    };

    try {
      transaction.rollback();
    } catch (final CADatabaseException ex) {
      result.addSuppressed(ex);
    }
    return result;
  }
}
