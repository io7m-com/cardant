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


package com.io7m.cardant.tests.server.controller;

import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemGetType;
import com.io7m.cardant.database.api.CADatabaseQueriesStockType;
import com.io7m.cardant.database.api.CADatabaseQueriesStockType.StockGetType;
import com.io7m.cardant.database.api.CADatabaseQueriesStockType.StockRepositType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationPath;
import com.io7m.cardant.model.CAStockInstanceID;
import com.io7m.cardant.model.CAStockOccurrenceSet;
import com.io7m.cardant.model.CAStockRepositSetIntroduce;
import com.io7m.cardant.protocol.inventory.CAICommandStockReposit;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdStockReposit;
import com.io7m.medrina.api.MMatchActionType.MMatchActionWithName;
import com.io7m.medrina.api.MMatchObjectType.MMatchObjectWithType;
import com.io7m.medrina.api.MMatchSubjectType.MMatchSubjectWithRolesAny;
import com.io7m.medrina.api.MPolicy;
import com.io7m.medrina.api.MRule;
import com.io7m.medrina.api.MRuleName;
import org.junit.jupiter.api.Test;
import org.mockito.internal.verification.Times;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSecurityPolicyDenied;
import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_ITEMS;
import static com.io7m.cardant.security.CASecurityPolicy.ROLE_INVENTORY_ITEMS_WRITER;
import static com.io7m.cardant.security.CASecurityPolicy.WRITE;
import static com.io7m.medrina.api.MRuleConclusion.ALLOW;
import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @see CAICmdStockReposit
 */

public final class CAICmdStockRepositTest
  extends CACmdAbstractContract
{
  private static final CAStockInstanceID STOCK_INSTANCE_ID =
    CAStockInstanceID.random();

  private static final CAItemID ITEM_ID =
    CAItemID.random();

  private static final CALocation LOCATION_0 =
    new CALocation(
      CALocationID.random(),
      Optional.empty(),
      CALocationPath.singleton("A"),
      OffsetDateTime.now(UTC),
      OffsetDateTime.now(UTC),
      Collections.emptySortedMap(),
      Collections.emptySortedMap(),
      Collections.emptySortedSet()
    );

  /**
   * Updating an item requires the permission to WRITE to INVENTORY_ITEMS.
   *
   * @throws Exception On errors
   */

  @Test
  public void testNotAllowed0()
    throws Exception
  {
    /* Arrange. */

    final var context =
      this.createContext();

    /* Act. */

    final var handler =
      new CAICmdStockReposit();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandStockReposit(
            new CAStockRepositSetIntroduce(
              STOCK_INSTANCE_ID,
              ITEM_ID,
              LOCATION_0.id(),
              23L
            )
          )
        );
      });

    /* Assert. */

    assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
  }

  /**
   * Executing a reposit works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testUpdates()
    throws Exception
  {
    /* Arrange. */

    final var stockGet =
      mock(StockGetType.class);
    final var stockReposit =
      mock(StockRepositType.class);
    final var transaction =
      this.transaction();

    when(transaction.queries(StockGetType.class))
      .thenReturn(stockGet);
    when(transaction.queries(StockRepositType.class))
      .thenReturn(stockReposit);

    when(stockGet.execute(any()))
      .thenReturn(Optional.of(
        new CAStockOccurrenceSet(
          STOCK_INSTANCE_ID,
          LOCATION_0.summary(),
          new CAItemSummary(
            ITEM_ID,
            "Item"
          ),
          23L
        )
      ));

    CASecurity.setPolicy(new MPolicy(List.of(
      new MRule(
        MRuleName.of("rule0"),
        "",
        ALLOW,
        new MMatchSubjectWithRolesAny(Set.of(ROLE_INVENTORY_ITEMS_WRITER)),
        new MMatchObjectWithType(INVENTORY_ITEMS.type()),
        new MMatchActionWithName(WRITE)
      )
    )));

    this.setRoles(ROLE_INVENTORY_ITEMS_WRITER);

    final var context =
      this.createContext();

    /* Act. */

    final var handler = new CAICmdStockReposit();
    handler.execute(
      context,
      new CAICommandStockReposit(
        new CAStockRepositSetIntroduce(
          STOCK_INSTANCE_ID,
          ITEM_ID,
          LOCATION_0.id(),
          23L
        ))
    );

    /* Assert. */

    verify(transaction)
      .queries(StockRepositType.class);
    verify(transaction)
      .queries(StockGetType.class);
    verify(stockReposit)
      .execute(new CAStockRepositSetIntroduce(STOCK_INSTANCE_ID, ITEM_ID, LOCATION_0.id(), 23L));
    verify(stockGet, new Times(1))
      .execute(STOCK_INSTANCE_ID);

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(stockReposit);
    verifyNoMoreInteractions(stockGet);
  }
}
