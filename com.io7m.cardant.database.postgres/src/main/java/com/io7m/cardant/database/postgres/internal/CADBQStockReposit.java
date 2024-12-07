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
import com.io7m.cardant.database.api.CADatabaseQueriesStockType.StockRepositType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAItemSerial;
import com.io7m.cardant.model.CAStockRepositRemove;
import com.io7m.cardant.model.CAStockRepositSerialIntroduce;
import com.io7m.cardant.model.CAStockRepositSerialMove;
import com.io7m.cardant.model.CAStockRepositSerialNumberAdd;
import com.io7m.cardant.model.CAStockRepositSerialNumberRemove;
import com.io7m.cardant.model.CAStockRepositSetAdd;
import com.io7m.cardant.model.CAStockRepositSetIntroduce;
import com.io7m.cardant.model.CAStockRepositSetMove;
import com.io7m.cardant.model.CAStockRepositSetRemove;
import com.io7m.cardant.model.CAStockRepositType;
import org.jooq.DSLContext;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd.auditEvent;
import static com.io7m.cardant.database.postgres.internal.Tables.STOCK;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorStockIsNotSerial;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorStockIsNotSet;
import static com.io7m.cardant.strings.CAStringConstants.COUNT;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_NONEXISTENT;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_STOCK_INSTANCE_IS_NOT_SERIAL;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_STOCK_INSTANCE_IS_NOT_SET;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_ID;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_SERIAL;
import static com.io7m.cardant.strings.CAStringConstants.LOCATION_ID;
import static com.io7m.cardant.strings.CAStringConstants.STOCK_INSTANCE;
import static java.lang.Long.toUnsignedString;

/**
 * Reposit items.
 */

public final class CADBQStockReposit
  extends CADBQAbstract<CAStockRepositType, CADatabaseUnit>
  implements StockRepositType
{
  private static final Service<CAStockRepositType, CADatabaseUnit, StockRepositType> SERVICE =
    new Service<>(StockRepositType.class, CADBQStockReposit::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQStockReposit(
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
    final CAStockRepositType reposit)
    throws CADatabaseException
  {
    this.setAttribute(STOCK_INSTANCE, reposit.instance().displayId());

    return switch (reposit) {
      case final CAStockRepositSetIntroduce r -> {
        yield this.itemRepositSetIntroduce(
          this.transaction(),
          context,
          r
        );
      }

      case final CAStockRepositSetAdd r -> {
        yield this.itemRepositSetAdd(
          this.transaction(),
          context,
          r
        );
      }

      case final CAStockRepositSetMove r -> {
        yield this.itemRepositSetMove(
          this.transaction(),
          context,
          r
        );
      }

      case final CAStockRepositSetRemove r -> {
        yield this.itemRepositSetRemove(
          this.transaction(),
          context,
          r
        );
      }

      case final CAStockRepositSerialNumberAdd r -> {
        this.setAttribute(ITEM_SERIAL, r.serial().value());

        yield this.itemRepositSerialNumberAdd(
          this.transaction(),
          context,
          r
        );
      }

      case final CAStockRepositRemove r -> {
        yield this.itemRepositRemove(
          this.transaction(),
          context,
          r
        );
      }

      case final CAStockRepositSerialMove r -> {
        yield this.itemRepositSerialMove(
          this.transaction(),
          context,
          r
        );
      }

      case final CAStockRepositSerialIntroduce r -> {
        this.setAttribute(ITEM_SERIAL, r.serial().value());

        yield this.itemRepositSerialIntroduce(
          this.transaction(),
          context,
          r
        );
      }

      case final CAStockRepositSerialNumberRemove r -> {
        this.setAttribute(ITEM_SERIAL, r.serial().value());

        yield this.itemRepositSerialNumberRemove(
          this.transaction(),
          context,
          r
        );
      }
    };
  }

  private CADatabaseUnit itemRepositSetIntroduce(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final CAStockRepositSetIntroduce r)
  {
    final var instance =
      r.instance();
    final var item =
      r.item();
    final var location =
      r.location();
    final var count =
      r.count();

    this.setAttribute(COUNT, toUnsignedString(count));
    this.setAttribute(ITEM_ID, item.displayId());
    this.setAttribute(LOCATION_ID, location.displayId());

    context.insertInto(STOCK)
      .set(STOCK.STOCK_INSTANCE, instance.id())
      .set(STOCK.STOCK_ITEM, item.id())
      .set(STOCK.STOCK_LOCATION, location.id())
      .set(STOCK.STOCK_COUNT, Long.valueOf(count))
      .execute();

    auditEvent(
      context,
      OffsetDateTime.now(transaction.clock()),
      transaction.userId(),
      "ITEM_REPOSIT_SET_INTRODUCED",
      Map.entry("Count", toUnsignedString(count)),
      Map.entry("Instance", instance.displayId()),
      Map.entry("Item", item.displayId()),
      Map.entry("Location", location.displayId())
    ).execute();

    return UNIT;
  }

  private CADatabaseUnit itemRepositSetAdd(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final CAStockRepositSetAdd r)
    throws CADatabaseException
  {
    final var count =
      r.count();

    this.setAttribute(COUNT, toUnsignedString(count));

    final var updated =
      context.update(STOCK)
        .set(
          STOCK.STOCK_COUNT,
          STOCK.STOCK_COUNT.add(Long.valueOf(count)))
        .where(STOCK.STOCK_INSTANCE.eq(r.instance().id()))
        .execute();

    if (updated != 1) {
      throw this.exceptionErrorNonexistent();
    }

    auditEvent(
      context,
      OffsetDateTime.now(transaction.clock()),
      transaction.userId(),
      "ITEM_REPOSIT_SET_ADDED",
      Map.entry("Count", toUnsignedString(count)),
      Map.entry("Instance", r.instance().displayId())
    ).execute();

    return UNIT;
  }

  private CADatabaseUnit itemRepositSetRemove(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final CAStockRepositSetRemove r)
    throws CADatabaseException
  {
    final var instance =
      r.instance();
    final var count =
      r.count();

    this.setAttribute(COUNT, toUnsignedString(count));

    final var updated =
      context.update(STOCK)
        .set(
          STOCK.STOCK_COUNT,
          STOCK.STOCK_COUNT.minus(Long.valueOf(count)))
        .where(STOCK.STOCK_INSTANCE.eq(instance.id()))
        .execute();

    if (updated != 1) {
      throw this.exceptionErrorNonexistent();
    }

    auditEvent(
      context,
      OffsetDateTime.now(transaction.clock()),
      transaction.userId(),
      "ITEM_REPOSIT_SET_REMOVED",
      Map.entry("Count", toUnsignedString(count)),
      Map.entry("Instance", instance.displayId())
    ).execute();

    final var countNow =
      context.select(STOCK.STOCK_COUNT)
        .from(STOCK)
        .where(STOCK.STOCK_INSTANCE.eq(instance.id()))
        .fetchOne(STOCK.STOCK_COUNT);

    if (countNow.longValue() == 0L) {
      context.deleteFrom(STOCK)
        .where(STOCK.STOCK_INSTANCE.eq(instance.id()))
        .execute();
    }

    return UNIT;
  }

  private CADatabaseUnit itemRepositSetMove(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final CAStockRepositSetMove r)
    throws CADatabaseException
  {
    final var instanceSource =
      r.instanceSource();
    final var instanceTarget =
      r.instanceTarget();
    final var location =
      r.toLocation();
    final var count =
      r.count();

    this.setAttribute(COUNT, toUnsignedString(count));

    /*
     * Determine if the source instance exists at all. Trying to move stock
     * from a nonexistent source instance is an error.
     */

    final var existingItem =
      context.select(STOCK.STOCK_ITEM)
        .from(STOCK)
        .where(STOCK.STOCK_INSTANCE.eq(instanceSource.id()))
        .fetchOne(STOCK.STOCK_ITEM);

    if (existingItem == null) {
      throw this.exceptionErrorNonexistent();
    }

    /*
     * Check that the source instance isn't a serial instance.
     */

    final var sourceIsSerial =
      context.select(STOCK.STOCK_INSTANCE)
        .from(STOCK)
        .where(
          STOCK.STOCK_INSTANCE.eq(instanceSource.id())
            .and(STOCK.STOCK_SERIALS.isNotNull())
        )
        .fetchOne(STOCK.STOCK_INSTANCE);

    if (sourceIsSerial != null) {
      throw new CADatabaseException(
        this.local(ERROR_STOCK_INSTANCE_IS_NOT_SET),
        errorStockIsNotSet(),
        this.attributes(),
        Optional.empty()
      );
    }

    /*
     * Check that the target instance isn't a serial instance.
     */

    final var targetIsSerial =
      context.select(STOCK.STOCK_INSTANCE)
        .from(STOCK)
        .where(
          STOCK.STOCK_INSTANCE.eq(instanceTarget.id())
            .and(STOCK.STOCK_SERIALS.isNotNull())
        )
        .fetchOne(STOCK.STOCK_INSTANCE);

    if (targetIsSerial != null) {
      throw new CADatabaseException(
        this.local(ERROR_STOCK_INSTANCE_IS_NOT_SET),
        errorStockIsNotSet(),
        this.attributes(),
        Optional.empty()
      );
    }

    /*
     * Determine the new count for the source instance. If the returned count
     * is null, then this means that the instance is not a "set" instance; this
     * is an error. If the new count is zero, then remove the source instance.
     */

    final var newSourceCount =
      context.update(STOCK)
        .set(
          STOCK.STOCK_COUNT,
          STOCK.STOCK_COUNT.minus(Long.valueOf(count)))
        .where(STOCK.STOCK_INSTANCE.eq(instanceSource.id()))
        .returning(STOCK.STOCK_COUNT)
        .fetchOne(STOCK.STOCK_COUNT);

    if (newSourceCount.longValue() == 0L) {
      context.deleteFrom(STOCK)
        .where(STOCK.STOCK_INSTANCE.eq(instanceSource.id()))
        .execute();
    }

    /*
     * Create or update the target instance. The target instance might already
     * exist.
     */

    context.insertInto(STOCK)
      .set(STOCK.STOCK_INSTANCE, instanceTarget.id())
      .set(STOCK.STOCK_ITEM, existingItem)
      .set(STOCK.STOCK_LOCATION, location.id())
      .set(STOCK.STOCK_COUNT, Long.valueOf(count))
      .onDuplicateKeyUpdate()
      .set(
        STOCK.STOCK_COUNT,
        STOCK.STOCK_COUNT.plus(Long.valueOf(count)))
      .execute();

    auditEvent(
      context,
      OffsetDateTime.now(transaction.clock()),
      transaction.userId(),
      "ITEM_REPOSIT_SET_MOVED",
      Map.entry("Count", toUnsignedString(count)),
      Map.entry("Instance (From)", instanceSource.displayId()),
      Map.entry("Instance (To)", instanceTarget.displayId())
    ).execute();

    return UNIT;
  }

  private CADatabaseUnit itemRepositSerialIntroduce(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final CAStockRepositSerialIntroduce r)
  {
    final var instance =
      r.instance();
    final var item =
      r.item();
    final var serial =
      r.serial();
    final var location =
      r.location();

    this.setAttribute(ITEM_ID, item.toString());
    this.setAttribute(ITEM_SERIAL, serial.toString());
    this.setAttribute(LOCATION_ID, location.toString());

    final var serialData =
      CADBSerialsJSON.serialsToJSON(List.of(r.serial()));

    context.insertInto(STOCK)
      .set(STOCK.STOCK_INSTANCE, instance.id())
      .set(STOCK.STOCK_ITEM, item.id())
      .set(STOCK.STOCK_LOCATION, location.id())
      .set(STOCK.STOCK_COUNT, Long.valueOf(1L))
      .set(STOCK.STOCK_SERIALS, serialData)
      .execute();

    auditEvent(
      context,
      OffsetDateTime.now(transaction.clock()),
      transaction.userId(),
      "ITEM_REPOSIT_SERIAL_INTRODUCED",
      Map.entry("Instance", instance.displayId()),
      Map.entry("Item", item.displayId()),
      Map.entry("Location", location.displayId()),
      Map.entry("Serial", serial.toString())
    ).execute();

    return UNIT;
  }

  private CADatabaseUnit itemRepositSerialMove(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final CAStockRepositSerialMove r)
    throws CADatabaseException
  {
    final var instance =
      r.instance();
    final var location =
      r.toLocation();

    /*
     * Determine if the source instance exists at all. Trying to move stock
     * from a nonexistent source instance is an error.
     */

    final var existingLocation =
      context.select(STOCK.STOCK_LOCATION)
        .from(STOCK)
        .where(STOCK.STOCK_INSTANCE.eq(instance.id()))
        .fetchOne(STOCK.STOCK_LOCATION);

    if (existingLocation == null) {
      throw this.exceptionErrorNonexistent();
    }

    /*
     * Check that the instance isn't a set instance.
     */

    final var setInstance =
      context.select(STOCK.STOCK_INSTANCE)
        .from(STOCK)
        .where(
          STOCK.STOCK_INSTANCE.eq(instance.id())
            .and(STOCK.STOCK_SERIALS.isNull())
        )
        .fetchOne(STOCK.STOCK_INSTANCE);

    if (setInstance != null) {
      throw new CADatabaseException(
        this.local(ERROR_STOCK_INSTANCE_IS_NOT_SERIAL),
        errorStockIsNotSerial(),
        this.attributes(),
        Optional.empty()
      );
    }

    context.update(STOCK)
      .set(STOCK.STOCK_LOCATION, location.id())
      .where(STOCK.STOCK_INSTANCE.eq(instance.id()))
      .execute();

    auditEvent(
      context,
      OffsetDateTime.now(transaction.clock()),
      transaction.userId(),
      "ITEM_REPOSIT_SERIAL_MOVED",
      Map.entry("Instance", instance.displayId()),
      Map.entry("Location (From)", existingLocation.toString()),
      Map.entry("Location (To)", location.displayId())
    ).execute();

    return UNIT;
  }

  private CADatabaseUnit itemRepositRemove(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final CAStockRepositRemove r)
    throws CADatabaseException
  {
    final var instance =
      r.instance();

    final var deleted =
      context.deleteFrom(STOCK)
        .where(STOCK.STOCK_INSTANCE.eq(instance.id()))
        .execute();

    if (deleted != 1) {
      throw this.exceptionErrorNonexistent();
    }

    auditEvent(
      context,
      OffsetDateTime.now(transaction.clock()),
      transaction.userId(),
      "ITEM_REPOSIT_REMOVED",
      Map.entry("Instance", instance.displayId())
    ).execute();

    return UNIT;
  }

  private CADatabaseUnit itemRepositSerialNumberRemove(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final CAStockRepositSerialNumberRemove r)
    throws CADatabaseException
  {
    final var instance =
      r.instance();
    final var serial =
      r.serial();

    this.setAttribute(ITEM_SERIAL, serial.toString());

    final var serialsJSON =
      context.select(STOCK.STOCK_SERIALS)
        .from(STOCK)
        .where(STOCK.STOCK_INSTANCE.eq(instance.id()))
        .fetchOne(STOCK.STOCK_SERIALS);

    if (serialsJSON == null) {
      throw this.exceptionErrorNonexistent();
    }

    final List<CAItemSerial> parsedSerials;
    try {
      parsedSerials = CADBSerialsJSON.serialsFromJSON(serialsJSON);
    } catch (final IOException e) {
      throw new CADatabaseException(
        e.getMessage(),
        e,
        CAStandardErrorCodes.errorIo(),
        this.attributes(),
        Optional.empty()
      );
    }

    final var filteredSerials =
      parsedSerials.stream()
        .filter(s -> !Objects.equals(s, r.serial()))
        .toList();

    if (filteredSerials.equals(parsedSerials)) {
      throw this.exceptionErrorNonexistent();
    }

    context.update(STOCK)
      .set(STOCK.STOCK_SERIALS, CADBSerialsJSON.serialsToJSON(filteredSerials))
      .where(STOCK.STOCK_INSTANCE.eq(instance.id()))
      .execute();

    auditEvent(
      context,
      OffsetDateTime.now(transaction.clock()),
      transaction.userId(),
      "ITEM_REPOSIT_SERIAL_NUMBER_REMOVED",
      Map.entry("Instance", instance.toString()),
      Map.entry("Serial", serial.toString())
    ).execute();

    return UNIT;
  }

  private CADatabaseException exceptionErrorNonexistent()
  {
    return new CADatabaseException(
      this.local(ERROR_NONEXISTENT),
      errorNonexistent(),
      this.attributes(),
      Optional.empty()
    );
  }

  private CADatabaseUnit itemRepositSerialNumberAdd(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final CAStockRepositSerialNumberAdd r)
    throws CADatabaseException
  {
    final var instance =
      r.instance();
    final var serial =
      r.serial();

    this.setAttribute(ITEM_SERIAL, serial.toString());

    final var serialsJSON =
      context.select(STOCK.STOCK_SERIALS)
        .from(STOCK)
        .where(STOCK.STOCK_INSTANCE.eq(instance.id()))
        .fetchOne(STOCK.STOCK_SERIALS);

    if (serialsJSON == null) {
      throw this.exceptionErrorNonexistent();
    }

    final List<CAItemSerial> serialList;
    try {
      serialList = new ArrayList<>(CADBSerialsJSON.serialsFromJSON(serialsJSON));
    } catch (final IOException e) {
      throw new CADatabaseException(
        e.getMessage(),
        e,
        CAStandardErrorCodes.errorIo(),
        this.attributes(),
        Optional.empty()
      );
    }

    serialList.add(serial);

    context.update(STOCK)
      .set(STOCK.STOCK_SERIALS, CADBSerialsJSON.serialsToJSON(serialList))
      .where(STOCK.STOCK_INSTANCE.eq(instance.id()))
      .execute();

    auditEvent(
      context,
      OffsetDateTime.now(transaction.clock()),
      transaction.userId(),
      "ITEM_REPOSIT_SERIAL_NUMBER_ADDED",
      Map.entry("Instance", instance.toString()),
      Map.entry("Serial", serial.toString())
    ).execute();

    return UNIT;
  }
}
