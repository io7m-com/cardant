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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.MetadataPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.MetadataPutType.Parameters;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAMetadataType;
import org.jooq.DSLContext;
import org.jooq.Query;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;

import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
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
  implements MetadataPutType
{
  private static final Service<Parameters, CADatabaseUnit, MetadataPutType> SERVICE =
    new Service<>(MetadataPutType.class, CADBQItemMetadataPut::new);

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

    context.batch(batches).execute();
    return UNIT;
  }

  private static Query setMetadataValue(
    final DSLContext context,
    final CAItemID itemID,
    final CAMetadataType meta)
  {
    if (meta instanceof final CAMetadataType.Monetary m) {
      return setMetadataValueMonetary(context, itemID, m);
    }
    if (meta instanceof final CAMetadataType.Real m) {
      return setMetadataValueReal(context, itemID, m);
    }
    if (meta instanceof final CAMetadataType.Time m) {
      return setMetadataValueTime(context, itemID, m);
    }
    if (meta instanceof final CAMetadataType.Text m) {
      return setMetadataValueText(context, itemID, m);
    }
    if (meta instanceof final CAMetadataType.Integral m) {
      return setMetadataValueIntegral(context, itemID, m);
    }
    throw new IllegalStateException();
  }

  private static Query setMetadataValueIntegral(
    final DSLContext context,
    final CAItemID itemID,
    final CAMetadataType.Integral meta)
  {
    return context.insertInto(ITEM_METADATA)
      .set(ITEM_METADATA.ITEM_META_ITEM, itemID.id())
      .set(ITEM_METADATA.ITEM_META_NAME, meta.name().value())
      .set(ITEM_METADATA.ITEM_META_VALUE_TYPE, SCALAR_INTEGRAL)
      .set(ITEM_METADATA.ITEM_META_VALUE_INTEGRAL, Long.valueOf(meta.value()))
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY, (BigDecimal) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY_CURRENCY, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_REAL, (Double) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TEXT, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TIME, (OffsetDateTime) null)
      .onConflict(ITEM_METADATA.ITEM_META_ITEM, ITEM_METADATA.ITEM_META_NAME)
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
      .set(ITEM_METADATA.ITEM_META_NAME, meta.name().value())
      .set(ITEM_METADATA.ITEM_META_VALUE_TYPE, SCALAR_TEXT)
      .set(ITEM_METADATA.ITEM_META_VALUE_INTEGRAL, (Long) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY, (BigDecimal) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY_CURRENCY, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_REAL, (Double) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TEXT, meta.value())
      .set(ITEM_METADATA.ITEM_META_VALUE_TIME, (OffsetDateTime) null)
      .onConflict(ITEM_METADATA.ITEM_META_ITEM, ITEM_METADATA.ITEM_META_NAME)
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
      .set(ITEM_METADATA.ITEM_META_NAME, meta.name().value())
      .set(ITEM_METADATA.ITEM_META_VALUE_TYPE, SCALAR_TIME)
      .set(ITEM_METADATA.ITEM_META_VALUE_INTEGRAL, (Long) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY, (BigDecimal) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY_CURRENCY, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_REAL, (Double) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TEXT, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TIME, meta.value())
      .onConflict(ITEM_METADATA.ITEM_META_ITEM, ITEM_METADATA.ITEM_META_NAME)
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
      .set(ITEM_METADATA.ITEM_META_NAME, meta.name().value())
      .set(ITEM_METADATA.ITEM_META_VALUE_TYPE, SCALAR_REAL)
      .set(ITEM_METADATA.ITEM_META_VALUE_INTEGRAL, (Long) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY, (BigDecimal) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY_CURRENCY, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_REAL, Double.valueOf(meta.value()))
      .set(ITEM_METADATA.ITEM_META_VALUE_TEXT, (String) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TIME, (OffsetDateTime) null)
      .onConflict(ITEM_METADATA.ITEM_META_ITEM, ITEM_METADATA.ITEM_META_NAME)
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
      .set(ITEM_METADATA.ITEM_META_NAME, meta.name().value())
      .set(ITEM_METADATA.ITEM_META_VALUE_TYPE, SCALAR_MONEY)
      .set(ITEM_METADATA.ITEM_META_VALUE_INTEGRAL, (Long) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_MONEY, meta.value())
      .set(
        ITEM_METADATA.ITEM_META_VALUE_MONEY_CURRENCY,
        meta.currency().getCode())
      .set(ITEM_METADATA.ITEM_META_VALUE_REAL, (Double) null)
      .set(ITEM_METADATA.ITEM_META_VALUE_TIME, (OffsetDateTime) null)
      .onConflict(ITEM_METADATA.ITEM_META_ITEM, ITEM_METADATA.ITEM_META_NAME)
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
