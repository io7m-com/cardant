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
import org.jooq.exception.DataAccessException;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorDuplicate;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorOperationNotPermitted;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorProtocol;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSql;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeFieldTypeNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeScalarReferenced;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_TYPE_DECLARATION_REFERS_TO_NONEXISTENT_TYPE;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_TYPE_SCALAR_STILL_REFERENCED;

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
    final CADatabaseTransaction transaction,
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
    final CADatabaseTransaction transaction,
    final DataAccessException e,
    final SortedMap<String, String> attributes)
  {
    final var m = e.getMessage();

    /*
     * See https://www.postgresql.org/docs/current/errcodes-appendix.html
     * for all of these numeric codes.
     */

    final CADatabaseException result = switch (e.sqlState()) {

      /*
       * foreign_key_violation
       */

      case "23502" -> integrityViolation(transaction, e, attributes, m);

      /*
       * foreign_key_violation
       */

      case "23503" -> handleForeignKeyViolation(transaction, e, attributes, m);

      /*
       * unique_violation
       */

      case "23505" -> handleUniqueViolation(e, attributes, m);

      /*
       * PostgreSQL: character_not_in_repertoire
       */

      case "22021" -> handleCharacterEncoding(e, attributes);

      /*
       * insufficient_privilege
       */

      case "42501" -> {
        yield new CADatabaseException(
          m,
          e,
          errorOperationNotPermitted(),
          attributes,
          Optional.empty()
        );
      }

      default -> {
        yield new CADatabaseException(
          m,
          e,
          errorSql(),
          attributes,
          Optional.empty()
        );
      }
    };

    try {
      transaction.rollback();
    } catch (final CADatabaseException ex) {
      result.addSuppressed(ex);
    }
    return result;
  }

  private static CADatabaseException integrityViolation(
    final CADatabaseTransaction transaction,
    final DataAccessException e,
    final SortedMap<String, String> attributes,
    final String m)
  {
    final String column =
      findPSQLException(e)
        .flatMap(z -> Optional.ofNullable(z.getServerErrorMessage()))
        .map(ServerErrorMessage::getColumn)
        .orElse("");

    return switch (column) {
      case "field_scalar_type" -> {
        yield new CADatabaseException(
          transaction.localize(ERROR_TYPE_DECLARATION_REFERS_TO_NONEXISTENT_TYPE),
          e,
          errorTypeFieldTypeNonexistent(),
          attributes,
          Optional.empty()
        );
      }

      default -> {
        yield new CADatabaseException(
          m,
          e,
          errorSql(),
          attributes,
          Optional.empty()
        );
      }
    };
  }

  private static CADatabaseException handleCharacterEncoding(
    final DataAccessException e,
    final SortedMap<String, String> attributes)
  {
    final String message =
      findPSQLException(e)
        .flatMap(z -> Optional.ofNullable(z.getServerErrorMessage()))
        .map(ServerErrorMessage::getMessage)
        .orElseGet(e::getMessage);

    return new CADatabaseException(
      message,
      e,
      errorProtocol(),
      attributes,
      Optional.empty()
    );
  }

  private static CADatabaseException handleUniqueViolation(
    final DataAccessException e,
    final SortedMap<String, String> attributes,
    final String m)
  {
    final String constraint =
      findPSQLException(e)
        .flatMap(z -> Optional.ofNullable(z.getServerErrorMessage()))
        .map(ServerErrorMessage::getConstraint)
        .orElse("");

    return switch (constraint) {
      case "files_pkey" -> {
        yield new CADatabaseException(
          "File already exists.",
          errorDuplicate(),
          attributes,
          Optional.empty()
        );
      }

      case "item_attachments_pkey" -> {
        yield new CADatabaseException(
          "Item attachment already exists.",
          errorDuplicate(),
          attributes,
          Optional.empty()
        );
      }

      case "item_locations_pkey" -> {
        yield new CADatabaseException(
          "Item location already exists.",
          errorDuplicate(),
          attributes,
          Optional.empty()
        );
      }

      case "items_pkey" -> {
        yield new CADatabaseException(
          "Item already exists.",
          errorDuplicate(),
          attributes,
          Optional.empty()
        );
      }

      case "locations_pkey" -> {
        yield new CADatabaseException(
          "Location already exists.",
          errorDuplicate(),
          attributes,
          Optional.empty()
        );
      }

      case "tags_pkey" -> {
        yield new CADatabaseException(
          "Tag already exists.",
          errorDuplicate(),
          attributes,
          Optional.empty()
        );
      }

      case "users_pkey" -> {
        yield new CADatabaseException(
          "User already exists.",
          errorDuplicate(),
          attributes,
          Optional.empty()
        );
      }

      default -> {
        yield new CADatabaseException(
          m,
          e,
          errorSql(),
          attributes,
          Optional.empty()
        );
      }
    };
  }

  private static Optional<PSQLException> findPSQLException(
    final DataAccessException e)
  {
    var x = e.getCause();
    while (x != null) {
      if (x instanceof final PSQLException xx) {
        return Optional.of(xx);
      }
      x = x.getCause();
    }
    return Optional.empty();
  }

  private static CADatabaseException handleForeignKeyViolation(
    final CADatabaseTransaction transaction,
    final DataAccessException e,
    final SortedMap<String, String> attributes,
    final String m)
  {
    if (e.getCause() instanceof final PSQLException actual) {
      final var constraint =
        Optional.ofNullable(actual.getServerErrorMessage())
          .flatMap(x -> Optional.ofNullable(x.getConstraint()))
          .orElse("");

      if (Objects.equals(constraint, "field_scalar_type_exists")) {
        return new CADatabaseException(
          transaction.localize(ERROR_TYPE_SCALAR_STILL_REFERENCED),
          e,
          errorTypeScalarReferenced(),
          attributes,
          Optional.empty()
        );
      }
    }

    return new CADatabaseException(
      m,
      e,
      errorSql(),
      attributes,
      Optional.empty()
    );
  }
}
