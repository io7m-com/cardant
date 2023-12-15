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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.RepositType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CAItemRepositMove;
import com.io7m.cardant.model.CAItemRepositRemove;
import com.io7m.cardant.model.CAItemRepositType;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.SQLStateClass;

import java.math.BigInteger;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd.auditEvent;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_LOCATIONS;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_LOCATIONS_SUMMED;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorRemoveTooManyItems;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSql;
import static com.io7m.cardant.strings.CAStringConstants.COUNT;
import static com.io7m.cardant.strings.CAStringConstants.COUNT_EXPECTED;
import static com.io7m.cardant.strings.CAStringConstants.COUNT_RECEIVED;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_ITEM_COUNT_STORE_INVARIANT;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_ITEM_COUNT_TOO_MANY_REMOVED;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_ID;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_LOCATION;
import static java.lang.Long.toUnsignedString;

/**
 * Reposit items.
 */

public final class CADBQItemReposit
  extends CADBQAbstract<CAItemRepositType, CADatabaseUnit>
  implements RepositType
{
  private static final Service<CAItemRepositType, CADatabaseUnit, RepositType> SERVICE =
    new Service<>(RepositType.class, CADBQItemReposit::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemReposit(
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
    final CAItemRepositType reposit)
    throws CADatabaseException
  {
    final var countInitial =
      itemCount(context, reposit.item());

    switch (reposit) {
      case final CAItemRepositAdd add -> {
        final var countAdded =
          itemRepositAdd(this.transaction(), context, add);
        this.checkItemCountInvariant(
          context,
          add.item(),
          countInitial + countAdded
        );
        return UNIT;
      }

      case final CAItemRepositMove move -> {
        this.itemRepositMove(this.transaction(), context, move);
        this.checkItemCountInvariant(context, move.item(), countInitial);
        return UNIT;
      }

      case final CAItemRepositRemove remove -> {
        final var countRemoved =
          this.itemRepositRemove(this.transaction(), context, remove);
        this.checkItemCountInvariant(
          context,
          remove.item(),
          countInitial - countRemoved
        );
        return UNIT;
      }
    }
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

  private long itemRepositRemove(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final CAItemRepositRemove remove)
    throws CADatabaseException
  {
    try {
      final var matchItem =
        ITEM_LOCATIONS.ITEM_LOCATION_ITEM.eq(remove.item().id());
      final var matchLocation =
        ITEM_LOCATIONS.ITEM_LOCATION.eq(remove.location().id());
      final var matches =
        matchItem.and(matchLocation);

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

      if (newCount == 0L) {
        context.deleteFrom(ITEM_LOCATIONS)
          .where(matches)
          .execute();
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
            Map.ofEntries(
              Map.entry(this.local(ITEM_ID), remove.item().displayId()),
              Map.entry(
                this.local(ITEM_LOCATION),
                remove.location().displayId()),
              Map.entry(
                this.local(COUNT),
                toUnsignedString(remove.count()))
            ),
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

    if (countExpected != countReceived) {
      throw new CADatabaseException(
        this.local(ERROR_ITEM_COUNT_STORE_INVARIANT),
        errorSql(),
        Map.ofEntries(
          Map.entry(this.local(ITEM_ID), item.displayId()),
          Map.entry(
            this.local(COUNT_EXPECTED),
            toUnsignedString(countExpected)),
          Map.entry(
            this.local(COUNT_RECEIVED),
            toUnsignedString(countReceived))
        ),
        Optional.empty()
      );
    }
  }

  private long itemRepositMove(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final CAItemRepositMove move)
    throws CADatabaseException
  {
    final var newCount0 =
      this.itemRepositRemove(
        transaction,
        context,
        new CAItemRepositRemove(
          move.item(),
          move.fromLocation(),
          move.count()
        ));

    final var newCount1 =
      itemRepositAdd(
        transaction,
        context,
        new CAItemRepositAdd(
          move.item(),
          move.toLocation(),
          move.count()
        ));

    return newCount0 + newCount1;
  }


  private static long itemRepositAdd(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final CAItemRepositAdd add)
  {
    context.insertInto(
        ITEM_LOCATIONS,
        ITEM_LOCATIONS.ITEM_LOCATION_ITEM,
        ITEM_LOCATIONS.ITEM_LOCATION,
        ITEM_LOCATIONS.ITEM_LOCATION_COUNT)
      .values(
        add.item().id(),
        add.location().id(),
        Long.valueOf(add.count())
      )
      .onDuplicateKeyUpdate()
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
}
