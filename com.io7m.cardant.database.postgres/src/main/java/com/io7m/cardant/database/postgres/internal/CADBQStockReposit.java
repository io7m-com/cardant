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
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAStockRepositSerialAdd;
import com.io7m.cardant.model.CAStockRepositSerialMove;
import com.io7m.cardant.model.CAStockRepositSerialRemove;
import com.io7m.cardant.model.CAStockRepositSetAdd;
import com.io7m.cardant.model.CAStockRepositSetMove;
import com.io7m.cardant.model.CAStockRepositSetRemove;
import com.io7m.cardant.model.CAStockRepositType;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.SQLStateClass;
import org.jooq.impl.DSL;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd.auditEvent;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_LOCATIONS;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_LOCATIONS_SUMMED;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorRemoveIdentifiedItems;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorRemoveTooManyItems;
import static com.io7m.cardant.strings.CAStringConstants.COUNT;
import static com.io7m.cardant.strings.CAStringConstants.COUNT_EXPECTED;
import static com.io7m.cardant.strings.CAStringConstants.COUNT_RECEIVED;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_ITEM_COUNT_STORE_INVARIANT;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_ITEM_COUNT_TOO_MANY_REMOVED;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_ITEM_REPOSIT_REMOVE_IDENTIFIED;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_NONEXISTENT;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_ID;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_LOCATION;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_SERIAL;
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
    final var countInitial =
      itemCount(context, reposit.item());

    return switch (reposit) {
      case final CAStockRepositSetAdd add -> {
        this.setAttribute(COUNT, toUnsignedString(add.count()));
        this.setAttribute(ITEM_ID, add.item().displayId());
        this.setAttribute(ITEM_LOCATION, add.location().displayId());

        final var countAdded =
          itemRepositSetAdd(this.transaction(), context, add);

        this.checkItemCountInvariant(
          context,
          add.item(),
          countInitial + countAdded
        );
        yield UNIT;
      }

      case final CAStockRepositSetMove move -> {
        this.setAttribute(COUNT, toUnsignedString(move.count()));
        this.setAttribute(ITEM_ID, move.item().displayId());

        this.itemRepositSetMove(
          this.transaction(),
          context,
          move,
          countInitial
        );

        this.checkItemCountInvariant(context, move.item(), countInitial);
        yield UNIT;
      }

      case final CAStockRepositSetRemove remove -> {
        this.setAttribute(COUNT, toUnsignedString(remove.count()));
        this.setAttribute(ITEM_ID, remove.item().displayId());
        this.setAttribute(ITEM_LOCATION, remove.location().displayId());

        final var countRemoved =
          this.itemRepositSetRemove(
            this.transaction(),
            context,
            remove,
            countInitial
          );

        this.checkItemCountInvariant(
          context,
          remove.item(),
          countInitial - countRemoved
        );
        yield UNIT;
      }

      case final CAStockRepositSerialAdd add -> {
        this.setAttribute(COUNT, toUnsignedString(add.count()));
        this.setAttribute(ITEM_ID, add.item().displayId());
        this.setAttribute(ITEM_LOCATION, add.location().displayId());
        this.setAttribute(ITEM_SERIAL, add.serial().value());

        final var countAdded =
          CADBQStockReposit.itemRepositSerialAdd(
            this.transaction(),
            context,
            add
          );

        this.checkItemCountInvariant(
          context,
          add.item(),
          countInitial + countAdded
        );
        yield UNIT;
      }

      case final CAStockRepositSerialRemove remove -> {
        this.setAttribute(COUNT, toUnsignedString(remove.count()));
        this.setAttribute(ITEM_ID, remove.item().displayId());
        this.setAttribute(ITEM_LOCATION, remove.location().displayId());
        this.setAttribute(ITEM_SERIAL, remove.serial().value());

        final var countRemoved =
          this.itemRepositSerialRemove(
            this.transaction(),
            context,
            remove
          );

        this.checkItemCountInvariant(
          context,
          remove.item(),
          countInitial - countRemoved
        );
        yield UNIT;
      }

      case final CAStockRepositSerialMove move -> {
        this.setAttribute(COUNT, toUnsignedString(move.count()));
        this.setAttribute(ITEM_ID, move.item().displayId());

        this.itemRepositSerialMove(
          this.transaction(),
          context,
          move
        );

        this.checkItemCountInvariant(context, move.item(), countInitial);
        yield UNIT;
      }
    };
  }

  static long itemCount(
    final DSLContext context,
    final CAItemID id)
  {
    return context.select(ITEM_LOCATIONS_SUMMED.ITEM_COUNT)
      .from(ITEM_LOCATIONS_SUMMED)
      .where(ITEM_LOCATIONS_SUMMED.ITEM_ID.eq(id.id()))
      .fetchOptional(ITEM_LOCATIONS_SUMMED.ITEM_COUNT)
      .map(i -> Long.valueOf(i.longValue()))
      .orElse(Long.valueOf(0L))
      .longValue();
  }

  private long itemRepositSetRemove(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final CAStockRepositSetRemove remove,
    final long countInitial)
    throws CADatabaseException
  {
    try {
      final var matchItem =
        ITEM_LOCATIONS.ITEM_LOCATION_ITEM.eq(remove.item().id());
      final var matchLocation =
        ITEM_LOCATIONS.ITEM_LOCATION.eq(remove.location().id());
      final var matchSerial =
        ITEM_LOCATIONS.ITEM_LOCATION_SERIAL.isNull();
      final var matches =
        DSL.and(matchItem, matchLocation, matchSerial);

      final var newCount =
        context.update(ITEM_LOCATIONS)
          .set(
            ITEM_LOCATIONS.ITEM_LOCATION_COUNT,
            ITEM_LOCATIONS.ITEM_LOCATION_COUNT.sub(Long.valueOf(remove.count())))
          .where(matches)
          .returning(ITEM_LOCATIONS.ITEM_LOCATION_COUNT)
          .fetchOptional(ITEM_LOCATIONS.ITEM_LOCATION_COUNT)
          .orElse(Long.valueOf(0L))
          .longValue();

      /*
       * If the resulting count is zero, then try to remove the row containing
       * the count.
       */

      if (newCount == 0L) {
        final var deleted =
          context.deleteFrom(ITEM_LOCATIONS)
            .where(matches)
            .execute();

        /*
         * If no rows were deleted, and the initial count was non-zero, then
         * this means that there are items remaining that have serial numbers.
         * Those can't be deleted as a set and have to be deleted manually.
         */

        if (deleted == 0 && countInitial > 0L) {
          throw new CADatabaseException(
            this.local(ERROR_ITEM_REPOSIT_REMOVE_IDENTIFIED),
            errorRemoveIdentifiedItems(),
            this.attributes(),
            Optional.empty()
          );
        }
      }

      auditEvent(
        context,
        OffsetDateTime.now(transaction.clock()),
        transaction.userId(),
        "ITEM_REPOSIT_REMOVED",
        Map.entry("Item", remove.item().displayId()),
        Map.entry("Count", toUnsignedString(remove.count()))
      ).execute();

      return remove.count();
    } catch (final DataAccessException e) {
      if (e.sqlStateClass() == SQLStateClass.C23_INTEGRITY_CONSTRAINT_VIOLATION) {
        if (e.getMessage().contains("check_item_location_count")) {
          throw new CADatabaseException(
            this.local(ERROR_ITEM_COUNT_TOO_MANY_REMOVED),
            e,
            errorRemoveTooManyItems(),
            this.attributes(),
            Optional.empty()
          );
        }
      }
      throw e;
    }
  }

  private void checkItemCountInvariant(
    final DSLContext context,
    final CAItemID item,
    final long countExpected)
    throws CADatabaseException
  {
    final var countReceived =
      context.select(ITEM_LOCATIONS_SUMMED.ITEM_COUNT)
        .from(ITEM_LOCATIONS_SUMMED)
        .where(ITEM_LOCATIONS_SUMMED.ITEM_ID.eq(item.id()))
        .fetchOptional(ITEM_LOCATIONS_SUMMED.ITEM_COUNT)
        .orElse(BigInteger.ZERO)
        .longValueExact();

    this.setAttribute(COUNT_EXPECTED, toUnsignedString(countExpected));
    this.setAttribute(COUNT_RECEIVED, toUnsignedString(countReceived));

    if (countExpected != countReceived) {
      throw new CADatabaseException(
        this.local(ERROR_ITEM_COUNT_STORE_INVARIANT),
        errorRemoveTooManyItems(),
        this.attributes(),
        Optional.empty()
      );
    }
  }

  private long itemRepositSetMove(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final CAStockRepositSetMove move,
    final long countInitial)
    throws CADatabaseException
  {
    final var newCount0 =
      this.itemRepositSetRemove(
        transaction,
        context,
        new CAStockRepositSetRemove(
          move.item(),
          move.fromLocation(),
          move.count()
        ),
        countInitial
      );

    final var newCount1 =
      itemRepositSetAdd(
        transaction,
        context,
        new CAStockRepositSetAdd(
          move.item(),
          move.toLocation(),
          move.count()
        ));

    return newCount0 + newCount1;
  }

  private long itemRepositSerialMove(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final CAStockRepositSerialMove move)
    throws CADatabaseException
  {
    final var newCount0 =
      this.itemRepositSerialRemove(
        transaction,
        context,
        new CAStockRepositSerialRemove(
          move.item(),
          move.fromLocation(),
          move.serial()
        )
      );

    final var newCount1 =
      itemRepositSerialAdd(
        transaction,
        context,
        new CAStockRepositSerialAdd(
          move.item(),
          move.toLocation(),
          move.serial()
        ));

    return newCount0 + newCount1;
  }


  private static long itemRepositSetAdd(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final CAStockRepositSetAdd add)
  {
    context.insertInto(
        ITEM_LOCATIONS,
        ITEM_LOCATIONS.ITEM_LOCATION_ITEM,
        ITEM_LOCATIONS.ITEM_LOCATION,
        ITEM_LOCATIONS.ITEM_LOCATION_COUNT,
        ITEM_LOCATIONS.ITEM_LOCATION_SERIAL)
      .values(
        add.item().id(),
        add.location().id(),
        Long.valueOf(add.count()),
        null
      )
      .onConflictOnConstraint(DSL.name("item_locations_unique"))
      .doUpdate()
      .set(
        ITEM_LOCATIONS.ITEM_LOCATION_COUNT,
        ITEM_LOCATIONS.ITEM_LOCATION_COUNT.add(Long.valueOf(add.count())))
      .execute();

    auditEvent(
      context,
      OffsetDateTime.now(transaction.clock()),
      transaction.userId(),
      "ITEM_REPOSIT_ADDED",
      Map.entry("Item", add.item().displayId()),
      Map.entry("Count", toUnsignedString(add.count()))
    ).execute();

    return add.count();
  }

  private static long itemRepositSerialAdd(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final CAStockRepositSerialAdd add)
  {
    context.insertInto(
        ITEM_LOCATIONS,
        ITEM_LOCATIONS.ITEM_LOCATION_ITEM,
        ITEM_LOCATIONS.ITEM_LOCATION,
        ITEM_LOCATIONS.ITEM_LOCATION_COUNT,
        ITEM_LOCATIONS.ITEM_LOCATION_SERIAL)
      .values(
        add.item().id(),
        add.location().id(),
        Long.valueOf(add.count()),
        add.serial().value()
      )
      .onConflictOnConstraint(DSL.name("item_locations_unique"))
      .doUpdate()
      .set(
        ITEM_LOCATIONS.ITEM_LOCATION_COUNT,
        ITEM_LOCATIONS.ITEM_LOCATION_COUNT.add(Long.valueOf(add.count())))
      .execute();

    auditEvent(
      context,
      OffsetDateTime.now(transaction.clock()),
      transaction.userId(),
      "ITEM_REPOSIT_ADDED",
      Map.entry("Item", add.item().displayId()),
      Map.entry("Count", toUnsignedString(add.count())),
      Map.entry("Serial", add.serial().value())
    ).execute();

    return add.count();
  }

  private long itemRepositSerialRemove(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final CAStockRepositSerialRemove remove)
    throws CADatabaseException
  {
    final var matchItem =
      ITEM_LOCATIONS.ITEM_LOCATION_ITEM.eq(remove.item().id());
    final var matchLocation =
      ITEM_LOCATIONS.ITEM_LOCATION.eq(remove.location().id());
    final var matchSerial =
      ITEM_LOCATIONS.ITEM_LOCATION_SERIAL.eq(remove.serial().value());

    final var matches =
      DSL.and(matchItem, matchLocation, matchSerial);

    final var deleted =
      context.deleteFrom(ITEM_LOCATIONS)
        .where(matches)
        .execute();

    if (deleted != 1) {
      throw new CADatabaseException(
        this.local(ERROR_NONEXISTENT),
        errorNonexistent(),
        this.attributes(),
        Optional.empty()
      );
    }

    auditEvent(
      context,
      OffsetDateTime.now(transaction.clock()),
      transaction.userId(),
      "ITEM_REPOSIT_REMOVED",
      Map.entry("Item", remove.item().displayId()),
      Map.entry("Count", toUnsignedString(remove.count())),
      Map.entry("Serial", remove.serial().value())
    ).execute();

    return remove.count();
  }
}
