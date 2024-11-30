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
import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.cardant.strings.CAStringConstants;
import org.jooq.Check;
import org.jooq.ForeignKey;
import org.jooq.Record;
import org.jooq.UniqueKey;
import org.jooq.exception.DataAccessException;
import org.postgresql.util.PSQLException;
import org.postgresql.util.ServerErrorMessage;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.io7m.cardant.database.postgres.internal.Keys.ITEM_TYPES_RECORD_TYPE_EXISTS;
import static com.io7m.cardant.database.postgres.internal.Keys.LOCATION_PARENT_EXISTS;
import static com.io7m.cardant.database.postgres.internal.Keys.STOCK_ITEM_EXISTS;
import static com.io7m.cardant.database.postgres.internal.Keys.STOCK_LOCATION_EXISTS;
import static com.io7m.cardant.database.postgres.internal.Tables.STOCK;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorDuplicate;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorItemStillInLocation;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorLocationNonDeletedChildren;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorLocationNotEmpty;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorOperationNotPermitted;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorProtocol;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorRemoveTooManyItems;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSql;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeFieldTypeNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeReferenced;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_DUPLICATE_FILE;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_DUPLICATE_ITEM;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_DUPLICATE_ITEM_ATTACHMENT;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_DUPLICATE_LOCATION;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_DUPLICATE_STOCK_INSTANCE;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_DUPLICATE_TYPE_PACKAGE;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_ITEM_COUNT_TOO_MANY_REMOVED;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_ITEM_STILL_IN_LOCATION;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_LOCATION_NON_DELETED_CHILDREN;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_LOCATION_NOT_EMPTY;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_NONEXISTENT;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_TYPE_DECLARATION_REFERS_TO_NONEXISTENT_TYPE;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_TYPE_STILL_REFERENCED;
import static java.util.Locale.ROOT;

/**
 * Functions to handle database exceptions.
 */

public final class CADatabaseExceptions
{
  private static final Map<String, CAForeignKeyViolation> FOREIGN_KEYS =
    Stream.of(
      new CAForeignKeyViolation(
        ITEM_TYPES_RECORD_TYPE_EXISTS,
        errorTypeReferenced(),
        ERROR_TYPE_STILL_REFERENCED
      ),
      new CAForeignKeyViolation(
        LOCATION_PARENT_EXISTS,
        errorLocationNonDeletedChildren(),
        ERROR_LOCATION_NON_DELETED_CHILDREN
      ),
      new CAForeignKeyViolation(
        STOCK_ITEM_EXISTS,
        errorItemStillInLocation(),
        ERROR_ITEM_STILL_IN_LOCATION
      ),
      new CAForeignKeyViolation(
        STOCK_LOCATION_EXISTS,
        errorLocationNotEmpty(),
        ERROR_LOCATION_NOT_EMPTY
      )
    ).collect(Collectors.toMap(e -> {
      return e.foreignKey.getName().toUpperCase(ROOT);
    }, e -> e));

  private static final Map<String, CAUniqueKeyViolation> UNIQUE_KEYS =
    Stream.of(
      new CAUniqueKeyViolation(
        Keys.METADATA_TYPE_PACKAGES_NAME_UNIQUE,
        errorDuplicate(),
        ERROR_DUPLICATE_TYPE_PACKAGE
      ),
      new CAUniqueKeyViolation(
        Keys.FILES_PRIMARY_KEY,
        errorDuplicate(),
        ERROR_DUPLICATE_FILE
      ),
      new CAUniqueKeyViolation(
        Keys.ITEM_ATTACHMENTS_PRIMARY_KEY,
        errorDuplicate(),
        ERROR_DUPLICATE_ITEM_ATTACHMENT
      ),
      new CAUniqueKeyViolation(
        Keys.ITEMS_PRIMARY_KEY,
        errorDuplicate(),
        ERROR_DUPLICATE_ITEM
      ),
      new CAUniqueKeyViolation(
        Keys.LOCATIONS_PRIMARY_KEY,
        errorDuplicate(),
        ERROR_DUPLICATE_LOCATION
      ),
      new CAUniqueKeyViolation(
        Keys.STOCK_PRIMARY_KEY,
        errorDuplicate(),
        ERROR_DUPLICATE_STOCK_INSTANCE
      )
    ).collect(Collectors.toMap(e -> {
      return e.uniqueKey.getName().toUpperCase(ROOT);
    }, e -> e));

  private static final Map<String, CACheckViolation> CHECKS =
    Stream.of(
      new CACheckViolation(
        STOCK.getChecks()
          .stream()
          .filter(c -> "STOCK_COUNT_NON_NEGATIVE".equals(c.getName()))
          .findFirst()
          .orElseThrow(),
        errorRemoveTooManyItems(),
        ERROR_ITEM_COUNT_TOO_MANY_REMOVED
      )
    ).collect(Collectors.toMap(e -> {
      return e.check.getName().toUpperCase(ROOT);
    }, e -> e));

  private record CACheckViolation(
    Check<Record> check,
    CAErrorCode errorCode,
    CAStringConstants errorMessage)
  {
    CACheckViolation
    {
      Objects.requireNonNull(check, "check");
      Objects.requireNonNull(errorCode, "errorCode");
      Objects.requireNonNull(errorMessage, "errorMessage");
    }
  }

  private record CAForeignKeyViolation(
    ForeignKey<org.jooq.Record, org.jooq.Record> foreignKey,
    CAErrorCode errorCode,
    CAStringConstants errorMessage)
  {
    CAForeignKeyViolation
    {
      Objects.requireNonNull(foreignKey, "foreignKey");
      Objects.requireNonNull(errorCode, "errorCode");
      Objects.requireNonNull(errorMessage, "errorMessage");
    }
  }

  private record CAUniqueKeyViolation(
    UniqueKey<Record> uniqueKey,
    CAErrorCode errorCode,
    CAStringConstants errorMessage)
  {
    CAUniqueKeyViolation
    {
      Objects.requireNonNull(uniqueKey, "uniqueKey");
      Objects.requireNonNull(errorCode, "errorCode");
      Objects.requireNonNull(errorMessage, "errorMessage");
    }
  }

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
      case "CA001" -> locationHasNonDeletedChildren(transaction, e, attributes);
      case "CA002" -> locationNotEmpty(transaction, e, attributes);

      /*
       * check_violation
       */

      case "23514" -> checkViolation(transaction, e, attributes, m);

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

      case "23505" -> handleUniqueViolation(transaction, e, attributes, m);

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

  private static CADatabaseException checkViolation(
    final CADatabaseTransaction transaction,
    final DataAccessException e,
    final SortedMap<String, String> attributes,
    final String m)
  {
    final String constraint =
      findPSQLException(e)
        .flatMap(z -> Optional.ofNullable(z.getServerErrorMessage()))
        .map(ServerErrorMessage::getConstraint)
        .map(x -> x.toUpperCase(ROOT))
        .orElse("");

    final var key = CHECKS.get(constraint);
    if (key != null) {
      return new CADatabaseException(
        transaction.localize(key.errorMessage),
        key.errorCode,
        attributes,
        Optional.empty()
      );
    }

    return new CADatabaseException(
      m,
      e,
      errorSql(),
      attributes,
      Optional.empty()
    );
  }

  private static CADatabaseException locationNotEmpty(
    final CADatabaseTransaction transaction,
    final DataAccessException e,
    final SortedMap<String, String> attributes)
  {
    return new CADatabaseException(
      transaction.localize(ERROR_LOCATION_NOT_EMPTY),
      e,
      errorLocationNotEmpty(),
      attributes,
      Optional.empty()
    );
  }

  private static CADatabaseException locationHasNonDeletedChildren(
    final CADatabaseTransaction transaction,
    final DataAccessException e,
    final SortedMap<String, String> attributes)
  {
    return new CADatabaseException(
      transaction.localize(ERROR_LOCATION_NON_DELETED_CHILDREN),
      e,
      errorLocationNonDeletedChildren(),
      attributes,
      Optional.empty()
    );
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
        .map(x -> x.toUpperCase(ROOT))
        .orElse("");

    return switch (column) {
      case "MTRF_DECLARATION", "IT_TYPE" -> {
        yield new CADatabaseException(
          transaction.localize(ERROR_NONEXISTENT),
          e,
          errorNonexistent(),
          attributes,
          Optional.empty()
        );
      }

      case "MTRF_SCALAR_TYPE" -> {
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
    final CADatabaseTransaction transaction,
    final DataAccessException e,
    final SortedMap<String, String> attributes,
    final String m)
  {
    final String constraint =
      findPSQLException(e)
        .flatMap(z -> Optional.ofNullable(z.getServerErrorMessage()))
        .map(ServerErrorMessage::getConstraint)
        .map(x -> x.toUpperCase(ROOT))
        .orElse("");

    final var key = UNIQUE_KEYS.get(constraint);
    if (key != null) {
      return new CADatabaseException(
        transaction.localize(key.errorMessage),
        key.errorCode,
        attributes,
        Optional.empty()
      );
    }

    return new CADatabaseException(
      m,
      e,
      errorSql(),
      attributes,
      Optional.empty()
    );
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
    var cause = e.getCause();
    while (cause != null) {
      if (cause instanceof final PSQLException actual) {
        final var constraint =
          Optional.ofNullable(actual.getServerErrorMessage())
            .flatMap(x -> Optional.ofNullable(x.getConstraint()))
            .map(x -> x.toUpperCase(ROOT))
            .orElse("");

        final var key = FOREIGN_KEYS.get(constraint);
        if (key != null) {
          return new CADatabaseException(
            transaction.localize(key.errorMessage),
            e,
            key.errorCode,
            attributes,
            Optional.empty()
          );
        }
      }
      cause = cause.getCause();
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
