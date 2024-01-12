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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemMetadataPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemMetadataPutType.Parameters;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAMetadataType;
import org.jooq.DSLContext;
import org.jooq.Query;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Map;

import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd.auditEvent;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_METADATA;
import static com.io7m.cardant.database.postgres.internal.enums.MetadataScalarBaseTypeT.SCALAR_INTEGRAL;
import static com.io7m.cardant.database.postgres.internal.enums.MetadataScalarBaseTypeT.SCALAR_MONEY;
import static com.io7m.cardant.database.postgres.internal.enums.MetadataScalarBaseTypeT.SCALAR_REAL;
import static com.io7m.cardant.database.postgres.internal.enums.MetadataScalarBaseTypeT.SCALAR_TEXT;
import static com.io7m.cardant.database.postgres.internal.enums.MetadataScalarBaseTypeT.SCALAR_TIME;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_ID;

/**
 * Add or update metadata for an item.
 */

public final class CADBQItemMetadataPut
  extends CADBQAbstract<Parameters, CADatabaseUnit>
  implements ItemMetadataPutType
{
  private static final Service<Parameters, CADatabaseUnit, ItemMetadataPutType> SERVICE =
    new Service<>(ItemMetadataPutType.class, CADBQItemMetadataPut::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemMetadataPut(
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

  @Override
  protected CADatabaseUnit onExecute(
    final DSLContext context,
    final Parameters parameters)
    throws CADatabaseException
  {
    final var item =
      parameters.item();
    final var metadata =
      parameters.metadata();

    this.setAttribute(ITEM_ID, item.displayId());

    final var batches = new ArrayList<Query>();
    for (final var meta : metadata) {
      batches.add(setMetadataValue(context, item, meta));
    }

    final var transaction = this.transaction();
    batches.add(
      auditEvent(
        context,
        OffsetDateTime.now(transaction.clock()),
        transaction.userId(),
        "ITEM_METADATA_UPDATED",
        Map.entry("Item", item.displayId())
      )
    );

    context.batch(batches).execute();
    return UNIT;
  }

  private static Query setMetadataValue(
    final DSLContext context,
    final CAItemID itemID,
    final CAMetadataType meta)
  {
    return switch (meta) {
      case final CAMetadataType.Monetary m -> {
        yield setMetadataValueMonetary(context, itemID, m);
      }
      case final CAMetadataType.Real m -> {
        yield setMetadataValueReal(context, itemID, m);
      }
      case final CAMetadataType.Time m -> {
        yield setMetadataValueTime(context, itemID, m);
      }
      case final CAMetadataType.Text m -> {
        yield setMetadataValueText(context, itemID, m);
      }
      case final CAMetadataType.Integral m -> {
        yield setMetadataValueIntegral(context, itemID, m);
      }
    };
  }

  private static Query setMetadataValueIntegral(
    final DSLContext context,
    final CAItemID itemID,
    final CAMetadataType.Integral meta)
  {
    return context.insertInto(ITEM_METADATA)
      .set(ITEM_METADATA.ITEM_META_ITEM, itemID.id())
      .set(
        ITEM_METADATA.ITEM_META_TYPE_PACKAGE,
        meta.name().typeName().packageName().value())
      .set(
        ITEM_METADATA.ITEM_META_TYPE_RECORD,
        meta.name().typeName().typeName().value())
      .set(
        ITEM_METADATA.ITEM_META_TYPE_FIELD,
        meta.name().fieldName().value())
      .set(ITEM_METADATA.ITEM_META_VALUE_TYPE, SCALAR_INTEGRAL)
      .set(ITEM_METADATA.ITEM_META_VALUE_INTEGRAL, Long.valueOf(meta.value()))
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY, (BigDecimal) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY_CURRENCY, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_REAL, (Double) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TEXT, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TIME, (OffsetDateTime) null)
      .onConflict(
        ITEM_METADATA.ITEM_META_ITEM,
        ITEM_METADATA.ITEM_META_TYPE_PACKAGE,
        ITEM_METADATA.ITEM_META_TYPE_RECORD,
        ITEM_METADATA.ITEM_META_TYPE_FIELD)
      .doUpdate()
      .set(ITEM_METADATA.ITEM_META_VALUE_TYPE, SCALAR_INTEGRAL)
      .set(ITEM_METADATA.ITEM_META_VALUE_INTEGRAL, Long.valueOf(meta.value()))
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY, (BigDecimal) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY_CURRENCY, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_REAL, (Double) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TEXT, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TIME, (OffsetDateTime) null);
  }

  private static Query setMetadataValueText(
    final DSLContext context,
    final CAItemID itemID,
    final CAMetadataType.Text meta)
  {
    return context.insertInto(ITEM_METADATA)
      .set(ITEM_METADATA.ITEM_META_ITEM, itemID.id())
      .set(
        ITEM_METADATA.ITEM_META_TYPE_PACKAGE,
        meta.name().typeName().packageName().value())
      .set(
        ITEM_METADATA.ITEM_META_TYPE_RECORD,
        meta.name().typeName().typeName().value())
      .set(
        ITEM_METADATA.ITEM_META_TYPE_FIELD,
        meta.name().fieldName().value())
      .set(ITEM_METADATA.ITEM_META_VALUE_TYPE, SCALAR_TEXT)
      .set(ITEM_METADATA.ITEM_META_VALUE_INTEGRAL, (Long) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY, (BigDecimal) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY_CURRENCY, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_REAL, (Double) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TEXT, meta.value())
      .set(ITEM_METADATA.ITEM_META_VALUE_TIME, (OffsetDateTime) null)
      .onConflict(
        ITEM_METADATA.ITEM_META_ITEM,
        ITEM_METADATA.ITEM_META_TYPE_PACKAGE,
        ITEM_METADATA.ITEM_META_TYPE_RECORD,
        ITEM_METADATA.ITEM_META_TYPE_FIELD)
      .doUpdate()
      .set(ITEM_METADATA.ITEM_META_VALUE_TYPE, SCALAR_TEXT)
      .set(ITEM_METADATA.ITEM_META_VALUE_INTEGRAL, (Long) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY, (BigDecimal) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY_CURRENCY, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_REAL, (Double) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TEXT, meta.value())
      .set(ITEM_METADATA.ITEM_META_VALUE_TIME, (OffsetDateTime) null);
  }

  private static Query setMetadataValueTime(
    final DSLContext context,
    final CAItemID itemID,
    final CAMetadataType.Time meta)
  {
    return context.insertInto(ITEM_METADATA)
      .set(ITEM_METADATA.ITEM_META_ITEM, itemID.id())
      .set(
        ITEM_METADATA.ITEM_META_TYPE_PACKAGE,
        meta.name().typeName().packageName().value())
      .set(
        ITEM_METADATA.ITEM_META_TYPE_RECORD,
        meta.name().typeName().typeName().value())
      .set(
        ITEM_METADATA.ITEM_META_TYPE_FIELD,
        meta.name().fieldName().value())
      .set(ITEM_METADATA.ITEM_META_VALUE_TYPE, SCALAR_TIME)
      .set(ITEM_METADATA.ITEM_META_VALUE_INTEGRAL, (Long) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY, (BigDecimal) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY_CURRENCY, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_REAL, (Double) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TEXT, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TIME, meta.value())
      .onConflict(
        ITEM_METADATA.ITEM_META_ITEM,
        ITEM_METADATA.ITEM_META_TYPE_PACKAGE,
        ITEM_METADATA.ITEM_META_TYPE_RECORD,
        ITEM_METADATA.ITEM_META_TYPE_FIELD)
      .doUpdate()
      .set(ITEM_METADATA.ITEM_META_VALUE_TYPE, SCALAR_TIME)
      .set(ITEM_METADATA.ITEM_META_VALUE_INTEGRAL, (Long) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY, (BigDecimal) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY_CURRENCY, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_REAL, (Double) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TEXT, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TIME, meta.value());
  }

  private static Query setMetadataValueReal(
    final DSLContext context,
    final CAItemID itemID,
    final CAMetadataType.Real meta)
  {
    return context.insertInto(ITEM_METADATA)
      .set(ITEM_METADATA.ITEM_META_ITEM, itemID.id())
      .set(
        ITEM_METADATA.ITEM_META_TYPE_PACKAGE,
        meta.name().typeName().packageName().value())
      .set(
        ITEM_METADATA.ITEM_META_TYPE_RECORD,
        meta.name().typeName().typeName().value())
      .set(
        ITEM_METADATA.ITEM_META_TYPE_FIELD,
        meta.name().fieldName().value())
      .set(ITEM_METADATA.ITEM_META_VALUE_TYPE, SCALAR_REAL)
      .set(ITEM_METADATA.ITEM_META_VALUE_INTEGRAL, (Long) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY, (BigDecimal) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY_CURRENCY, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_REAL, Double.valueOf(meta.value()))
      .set(ITEM_METADATA.ITEM_META_VALUE_TEXT, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TIME, (OffsetDateTime) null)
      .onConflict(
        ITEM_METADATA.ITEM_META_ITEM,
        ITEM_METADATA.ITEM_META_TYPE_PACKAGE,
        ITEM_METADATA.ITEM_META_TYPE_RECORD,
        ITEM_METADATA.ITEM_META_TYPE_FIELD)
      .doUpdate()
      .set(ITEM_METADATA.ITEM_META_VALUE_TYPE, SCALAR_REAL)
      .set(ITEM_METADATA.ITEM_META_VALUE_INTEGRAL, (Long) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY, (BigDecimal) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY_CURRENCY, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_REAL, Double.valueOf(meta.value()))
      .set(ITEM_METADATA.ITEM_META_VALUE_TEXT, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TIME, (OffsetDateTime) null);
  }

  private static Query setMetadataValueMonetary(
    final DSLContext context,
    final CAItemID itemID,
    final CAMetadataType.Monetary meta)
  {
    return context.insertInto(ITEM_METADATA)
      .set(ITEM_METADATA.ITEM_META_ITEM, itemID.id())
      .set(
        ITEM_METADATA.ITEM_META_TYPE_PACKAGE,
        meta.name().typeName().packageName().value())
      .set(
        ITEM_METADATA.ITEM_META_TYPE_RECORD,
        meta.name().typeName().typeName().value())
      .set(
        ITEM_METADATA.ITEM_META_TYPE_FIELD,
        meta.name().fieldName().value())
      .set(ITEM_METADATA.ITEM_META_VALUE_TYPE, SCALAR_MONEY)
      .set(ITEM_METADATA.ITEM_META_VALUE_INTEGRAL, (Long) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY, meta.value())
      .set(
        ITEM_METADATA.ITEM_META_VALUE_MONEY_CURRENCY,
        meta.currency().getCode())
      .set(ITEM_METADATA.ITEM_META_VALUE_REAL, (Double) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TIME, (OffsetDateTime) null)
      .onConflict(
        ITEM_METADATA.ITEM_META_ITEM,
        ITEM_METADATA.ITEM_META_TYPE_PACKAGE,
        ITEM_METADATA.ITEM_META_TYPE_RECORD,
        ITEM_METADATA.ITEM_META_TYPE_FIELD)
      .doUpdate()
      .set(ITEM_METADATA.ITEM_META_VALUE_TYPE, SCALAR_MONEY)
      .set(ITEM_METADATA.ITEM_META_VALUE_INTEGRAL, (Long) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY, meta.value())
      .set(
        ITEM_METADATA.ITEM_META_VALUE_MONEY_CURRENCY,
        meta.currency().getCode())
      .set(ITEM_METADATA.ITEM_META_VALUE_REAL, (Double) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TIME, (OffsetDateTime) null);
  }
}
