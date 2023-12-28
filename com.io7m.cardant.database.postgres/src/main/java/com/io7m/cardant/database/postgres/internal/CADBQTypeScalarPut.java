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

import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeScalarPutType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CATypeScalarType;
import org.jooq.DSLContext;
import org.jooq.Query;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd.auditEvent;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_SCALAR;
import static com.io7m.cardant.database.postgres.internal.enums.MetadataScalarBaseTypeT.SCALAR_INTEGRAL;
import static com.io7m.cardant.database.postgres.internal.enums.MetadataScalarBaseTypeT.SCALAR_MONEY;
import static com.io7m.cardant.database.postgres.internal.enums.MetadataScalarBaseTypeT.SCALAR_REAL;
import static com.io7m.cardant.database.postgres.internal.enums.MetadataScalarBaseTypeT.SCALAR_TEXT;
import static com.io7m.cardant.database.postgres.internal.enums.MetadataScalarBaseTypeT.SCALAR_TIME;

/**
 * Create or update a scalar type declaration.
 */

public final class CADBQTypeScalarPut
  extends CADBQAbstract<CATypeScalarType, CADatabaseUnit>
  implements TypeScalarPutType
{
  private static final Service<CATypeScalarType, CADatabaseUnit, TypeScalarPutType> SERVICE =
    new Service<>(TypeScalarPutType.class, CADBQTypeScalarPut::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQTypeScalarPut(
    final CADatabaseTransaction transaction)
  {
    super(transaction);
  }

  /**
   * @return A query provider
   */

  public static CADBQueryProviderType provider()
  {
    return () -> SERVICE;
  }

  private static Query onExecuteTime(
    final DSLContext context,
    final OptionalInt packageDbID,
    final CATypeScalarType.Time t)
  {
    return context.insertInto(METADATA_TYPES_SCALAR)
      .set(METADATA_TYPES_SCALAR.MTS_PACKAGE, box(packageDbID))
      .set(METADATA_TYPES_SCALAR.MTS_DESCRIPTION, t.description())
      .set(METADATA_TYPES_SCALAR.MTS_NAME, t.name().value())
      .set(METADATA_TYPES_SCALAR.MTS_BASE_TYPE, SCALAR_TIME)
      .set(METADATA_TYPES_SCALAR.MTS_INTEGRAL_LOWER, (Long) null)
      .set(METADATA_TYPES_SCALAR.MTS_INTEGRAL_UPPER, (Long) null)
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_LOWER, (BigDecimal) null)
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_UPPER, (BigDecimal) null)
      .set(METADATA_TYPES_SCALAR.MTS_REAL_LOWER, (Double) null)
      .set(METADATA_TYPES_SCALAR.MTS_REAL_UPPER, (Double) null)
      .set(METADATA_TYPES_SCALAR.MTS_TEXT_PATTERN, (String) null)
      .set(METADATA_TYPES_SCALAR.MTS_TIME_LOWER, t.rangeLower())
      .set(METADATA_TYPES_SCALAR.MTS_TIME_UPPER, t.rangeUpper())
      .onConflict(METADATA_TYPES_SCALAR.MTS_NAME)
      .doUpdate()
      .set(METADATA_TYPES_SCALAR.MTS_PACKAGE, box(packageDbID))
      .set(METADATA_TYPES_SCALAR.MTS_DESCRIPTION, t.description())
      .set(METADATA_TYPES_SCALAR.MTS_NAME, t.name().value())
      .set(METADATA_TYPES_SCALAR.MTS_BASE_TYPE, SCALAR_TIME)
      .set(METADATA_TYPES_SCALAR.MTS_INTEGRAL_LOWER, (Long) null)
      .set(METADATA_TYPES_SCALAR.MTS_INTEGRAL_UPPER, (Long) null)
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_LOWER, (BigDecimal) null)
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_UPPER, (BigDecimal) null)
      .set(METADATA_TYPES_SCALAR.MTS_REAL_LOWER, (Double) null)
      .set(METADATA_TYPES_SCALAR.MTS_REAL_UPPER, (Double) null)
      .set(METADATA_TYPES_SCALAR.MTS_TEXT_PATTERN, (String) null)
      .set(METADATA_TYPES_SCALAR.MTS_TIME_LOWER, t.rangeLower())
      .set(METADATA_TYPES_SCALAR.MTS_TIME_UPPER, t.rangeUpper());
  }

  private static Integer box(
    final OptionalInt id)
  {
    if (id.isEmpty()) {
      return null;
    }
    return Integer.valueOf(id.getAsInt());
  }

  private static Query onExecuteReal(
    final DSLContext context,
    final OptionalInt packageDbID,
    final CATypeScalarType.Real t)
  {
    return context.insertInto(METADATA_TYPES_SCALAR)
      .set(METADATA_TYPES_SCALAR.MTS_PACKAGE, box(packageDbID))
      .set(METADATA_TYPES_SCALAR.MTS_DESCRIPTION, t.description())
      .set(METADATA_TYPES_SCALAR.MTS_NAME, t.name().value())
      .set(METADATA_TYPES_SCALAR.MTS_BASE_TYPE, SCALAR_REAL)
      .set(METADATA_TYPES_SCALAR.MTS_INTEGRAL_LOWER, (Long) null)
      .set(METADATA_TYPES_SCALAR.MTS_INTEGRAL_UPPER, (Long) null)
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_LOWER, (BigDecimal) null)
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_UPPER, (BigDecimal) null)
      .set(METADATA_TYPES_SCALAR.MTS_REAL_LOWER, Double.valueOf(t.rangeLower()))
      .set(METADATA_TYPES_SCALAR.MTS_REAL_UPPER, Double.valueOf(t.rangeUpper()))
      .set(METADATA_TYPES_SCALAR.MTS_TEXT_PATTERN, (String) null)
      .set(METADATA_TYPES_SCALAR.MTS_TIME_LOWER, (OffsetDateTime) null)
      .set(METADATA_TYPES_SCALAR.MTS_TIME_UPPER, (OffsetDateTime) null)
      .onConflict(METADATA_TYPES_SCALAR.MTS_NAME)
      .doUpdate()
      .set(METADATA_TYPES_SCALAR.MTS_PACKAGE, box(packageDbID))
      .set(METADATA_TYPES_SCALAR.MTS_DESCRIPTION, t.description())
      .set(METADATA_TYPES_SCALAR.MTS_NAME, t.name().value())
      .set(METADATA_TYPES_SCALAR.MTS_BASE_TYPE, SCALAR_REAL)
      .set(METADATA_TYPES_SCALAR.MTS_INTEGRAL_LOWER, (Long) null)
      .set(METADATA_TYPES_SCALAR.MTS_INTEGRAL_UPPER, (Long) null)
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_LOWER, (BigDecimal) null)
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_UPPER, (BigDecimal) null)
      .set(METADATA_TYPES_SCALAR.MTS_REAL_LOWER, Double.valueOf(t.rangeLower()))
      .set(METADATA_TYPES_SCALAR.MTS_REAL_UPPER, Double.valueOf(t.rangeUpper()))
      .set(METADATA_TYPES_SCALAR.MTS_TEXT_PATTERN, (String) null)
      .set(METADATA_TYPES_SCALAR.MTS_TIME_LOWER, (OffsetDateTime) null)
      .set(METADATA_TYPES_SCALAR.MTS_TIME_UPPER, (OffsetDateTime) null);
  }

  private static Query onExecuteIntegral(
    final DSLContext context,
    final OptionalInt packageDbID,
    final CATypeScalarType.Integral t)
  {
    return context.insertInto(METADATA_TYPES_SCALAR)
      .set(METADATA_TYPES_SCALAR.MTS_PACKAGE, box(packageDbID))
      .set(METADATA_TYPES_SCALAR.MTS_DESCRIPTION, t.description())
      .set(METADATA_TYPES_SCALAR.MTS_NAME, t.name().value())
      .set(METADATA_TYPES_SCALAR.MTS_BASE_TYPE, SCALAR_INTEGRAL)
      .set(
        METADATA_TYPES_SCALAR.MTS_INTEGRAL_LOWER,
        Long.valueOf(t.rangeLower()))
      .set(
        METADATA_TYPES_SCALAR.MTS_INTEGRAL_UPPER,
        Long.valueOf(t.rangeUpper()))
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_LOWER, (BigDecimal) null)
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_UPPER, (BigDecimal) null)
      .set(METADATA_TYPES_SCALAR.MTS_REAL_LOWER, (Double) null)
      .set(METADATA_TYPES_SCALAR.MTS_REAL_UPPER, (Double) null)
      .set(METADATA_TYPES_SCALAR.MTS_TEXT_PATTERN, (String) null)
      .set(METADATA_TYPES_SCALAR.MTS_TIME_LOWER, (OffsetDateTime) null)
      .set(METADATA_TYPES_SCALAR.MTS_TIME_UPPER, (OffsetDateTime) null)
      .onConflict(METADATA_TYPES_SCALAR.MTS_NAME)
      .doUpdate()
      .set(METADATA_TYPES_SCALAR.MTS_PACKAGE, box(packageDbID))
      .set(METADATA_TYPES_SCALAR.MTS_DESCRIPTION, t.description())
      .set(METADATA_TYPES_SCALAR.MTS_NAME, t.name().value())
      .set(METADATA_TYPES_SCALAR.MTS_BASE_TYPE, SCALAR_INTEGRAL)
      .set(
        METADATA_TYPES_SCALAR.MTS_INTEGRAL_LOWER,
        Long.valueOf(t.rangeLower()))
      .set(
        METADATA_TYPES_SCALAR.MTS_INTEGRAL_UPPER,
        Long.valueOf(t.rangeUpper()))
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_LOWER, (BigDecimal) null)
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_UPPER, (BigDecimal) null)
      .set(METADATA_TYPES_SCALAR.MTS_REAL_LOWER, (Double) null)
      .set(METADATA_TYPES_SCALAR.MTS_REAL_UPPER, (Double) null)
      .set(METADATA_TYPES_SCALAR.MTS_TEXT_PATTERN, (String) null)
      .set(METADATA_TYPES_SCALAR.MTS_TIME_LOWER, (OffsetDateTime) null)
      .set(METADATA_TYPES_SCALAR.MTS_TIME_UPPER, (OffsetDateTime) null);
  }

  private static Query onExecuteText(
    final DSLContext context,
    final OptionalInt packageDbID,
    final CATypeScalarType.Text t)
  {
    return context.insertInto(METADATA_TYPES_SCALAR)
      .set(METADATA_TYPES_SCALAR.MTS_PACKAGE, box(packageDbID))
      .set(METADATA_TYPES_SCALAR.MTS_DESCRIPTION, t.description())
      .set(METADATA_TYPES_SCALAR.MTS_NAME, t.name().value())
      .set(METADATA_TYPES_SCALAR.MTS_BASE_TYPE, SCALAR_TEXT)
      .set(METADATA_TYPES_SCALAR.MTS_INTEGRAL_LOWER, (Long) null)
      .set(METADATA_TYPES_SCALAR.MTS_INTEGRAL_UPPER, (Long) null)
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_LOWER, (BigDecimal) null)
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_UPPER, (BigDecimal) null)
      .set(METADATA_TYPES_SCALAR.MTS_REAL_LOWER, (Double) null)
      .set(METADATA_TYPES_SCALAR.MTS_REAL_UPPER, (Double) null)
      .set(METADATA_TYPES_SCALAR.MTS_TEXT_PATTERN, t.pattern())
      .set(METADATA_TYPES_SCALAR.MTS_TIME_LOWER, (OffsetDateTime) null)
      .set(METADATA_TYPES_SCALAR.MTS_TIME_UPPER, (OffsetDateTime) null)
      .onConflict(METADATA_TYPES_SCALAR.MTS_NAME)
      .doUpdate()
      .set(METADATA_TYPES_SCALAR.MTS_PACKAGE, box(packageDbID))
      .set(METADATA_TYPES_SCALAR.MTS_DESCRIPTION, t.description())
      .set(METADATA_TYPES_SCALAR.MTS_NAME, t.name().value())
      .set(METADATA_TYPES_SCALAR.MTS_BASE_TYPE, SCALAR_TEXT)
      .set(METADATA_TYPES_SCALAR.MTS_INTEGRAL_LOWER, (Long) null)
      .set(METADATA_TYPES_SCALAR.MTS_INTEGRAL_UPPER, (Long) null)
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_LOWER, (BigDecimal) null)
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_UPPER, (BigDecimal) null)
      .set(METADATA_TYPES_SCALAR.MTS_REAL_LOWER, (Double) null)
      .set(METADATA_TYPES_SCALAR.MTS_REAL_UPPER, (Double) null)
      .set(METADATA_TYPES_SCALAR.MTS_TEXT_PATTERN, t.pattern())
      .set(METADATA_TYPES_SCALAR.MTS_TIME_LOWER, (OffsetDateTime) null)
      .set(METADATA_TYPES_SCALAR.MTS_TIME_UPPER, (OffsetDateTime) null);
  }

  private static Query onExecuteMonetary(
    final DSLContext context,
    final OptionalInt packageDbID,
    final CATypeScalarType.Monetary t)
  {
    return context.insertInto(METADATA_TYPES_SCALAR)
      .set(METADATA_TYPES_SCALAR.MTS_PACKAGE, box(packageDbID))
      .set(METADATA_TYPES_SCALAR.MTS_DESCRIPTION, t.description())
      .set(METADATA_TYPES_SCALAR.MTS_NAME, t.name().value())
      .set(METADATA_TYPES_SCALAR.MTS_BASE_TYPE, SCALAR_MONEY)
      .set(METADATA_TYPES_SCALAR.MTS_INTEGRAL_LOWER, (Long) null)
      .set(METADATA_TYPES_SCALAR.MTS_INTEGRAL_UPPER, (Long) null)
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_LOWER, t.rangeLower())
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_UPPER, t.rangeUpper())
      .set(METADATA_TYPES_SCALAR.MTS_REAL_LOWER, (Double) null)
      .set(METADATA_TYPES_SCALAR.MTS_REAL_UPPER, (Double) null)
      .set(METADATA_TYPES_SCALAR.MTS_TEXT_PATTERN, (String) null)
      .set(METADATA_TYPES_SCALAR.MTS_TIME_LOWER, (OffsetDateTime) null)
      .set(METADATA_TYPES_SCALAR.MTS_TIME_UPPER, (OffsetDateTime) null)
      .onConflict(METADATA_TYPES_SCALAR.MTS_NAME)
      .doUpdate()
      .set(METADATA_TYPES_SCALAR.MTS_PACKAGE, box(packageDbID))
      .set(METADATA_TYPES_SCALAR.MTS_DESCRIPTION, t.description())
      .set(METADATA_TYPES_SCALAR.MTS_NAME, t.name().value())
      .set(METADATA_TYPES_SCALAR.MTS_BASE_TYPE, SCALAR_MONEY)
      .set(METADATA_TYPES_SCALAR.MTS_INTEGRAL_LOWER, (Long) null)
      .set(METADATA_TYPES_SCALAR.MTS_INTEGRAL_UPPER, (Long) null)
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_LOWER, t.rangeLower())
      .set(METADATA_TYPES_SCALAR.MTS_MONEY_UPPER, t.rangeUpper())
      .set(METADATA_TYPES_SCALAR.MTS_REAL_LOWER, (Double) null)
      .set(METADATA_TYPES_SCALAR.MTS_REAL_UPPER, (Double) null)
      .set(METADATA_TYPES_SCALAR.MTS_TEXT_PATTERN, (String) null)
      .set(METADATA_TYPES_SCALAR.MTS_TIME_LOWER, (OffsetDateTime) null)
      .set(METADATA_TYPES_SCALAR.MTS_TIME_UPPER, (OffsetDateTime) null);
  }

  @Override
  protected CADatabaseUnit onExecute(
    final DSLContext context,
    final CATypeScalarType scalar)
  {
    context.batch(
      insertType(this.transaction(), context, OptionalInt.empty(), scalar)
    ).execute();
    return CADatabaseUnit.UNIT;
  }

  static List<Query> insertType(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final OptionalInt packageDbID,
    final CATypeScalarType scalar)
  {
    final var query =
      switch (scalar) {
        case final CATypeScalarType.Monetary t -> {
          yield onExecuteMonetary(context, packageDbID, t);
        }
        case final CATypeScalarType.Text t -> {
          yield onExecuteText(context, packageDbID, t);
        }
        case final CATypeScalarType.Integral t -> {
          yield onExecuteIntegral(context, packageDbID, t);
        }
        case final CATypeScalarType.Real t -> {
          yield onExecuteReal(context, packageDbID, t);
        }
        case final CATypeScalarType.Time t -> {
          yield onExecuteTime(context, packageDbID, t);
        }
      };

    final var auditQuery =
      auditEvent(
        context,
        OffsetDateTime.now(transaction.clock()),
        transaction.userId(),
        "TYPE_SCALAR_UPDATED",
        Map.entry("Type", scalar.name().value())
      );

    return List.of(query, auditQuery);
  }
}
