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

import com.io7m.cardant.database.api.CADatabaseQueriesStockType.StockCountType;
import com.io7m.cardant.model.CAIncludeDeleted;
import com.io7m.cardant.model.CALocationMatchType;
import com.io7m.cardant.model.CAStockOccurrenceKind;
import com.io7m.cardant.model.CAStockSearchParameters;
import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.cardant.protocol.inventory.CAICommandStockCount;
import com.io7m.cardant.protocol.inventory.CAIResponseStockCount;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdStockCount;
import com.io7m.medrina.api.MMatchActionType.MMatchActionWithName;
import com.io7m.medrina.api.MMatchObjectType.MMatchObjectWithType;
import com.io7m.medrina.api.MMatchSubjectType.MMatchSubjectWithRolesAny;
import com.io7m.medrina.api.MPolicy;
import com.io7m.medrina.api.MRule;
import com.io7m.medrina.api.MRuleName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSecurityPolicyDenied;
import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_STOCK;
import static com.io7m.cardant.security.CASecurityPolicy.READ;
import static com.io7m.cardant.security.CASecurityPolicy.ROLE_INVENTORY_STOCK_READER;
import static com.io7m.medrina.api.MRuleConclusion.ALLOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @see CAICmdStockCount
 */

public final class CAICmdStockCountTest
  extends CACmdAbstractContract
{
  private static final CAStockSearchParameters PARAMETERS =
    new CAStockSearchParameters(
      new CALocationMatchType.CALocationsAll(),
      new CAComparisonExactType.Anything<>(),
      CAStockOccurrenceKind.all(),
      CAIncludeDeleted.INCLUDE_ONLY_LIVE,
      100L
    );

  /**
   * Searching for items requires the permission to READ to INVENTORY_STOCK.
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
      new CAICmdStockCount();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandStockCount(PARAMETERS));
      });

    /* Assert. */

    assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
  }

  /**
   * Searching for items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testSearch()
    throws Exception
  {
    /* Arrange. */

    final var items =
      mock(StockCountType.class);

    final var transaction =
      this.transaction();

    when(transaction.queries(StockCountType.class))
      .thenReturn(items);
    when(items.execute(any()))
      .thenReturn(23L);

    CASecurity.setPolicy(new MPolicy(List.of(
      new MRule(
        MRuleName.of("rule0"),
        "",
        ALLOW,
        new MMatchSubjectWithRolesAny(Set.of(ROLE_INVENTORY_STOCK_READER)),
        new MMatchObjectWithType(INVENTORY_STOCK.type()),
        new MMatchActionWithName(READ)
      )
    )));

    this.setRoles(ROLE_INVENTORY_STOCK_READER);

    final var context =
      this.createContext();

    /* Act. */

    final var handler =
      new CAICmdStockCount();
    final var r =
      handler.execute(context, new CAICommandStockCount(PARAMETERS));
    final var rc =
      assertInstanceOf(CAIResponseStockCount.class, r);

    assertEquals(23L, rc.count());

    /* Assert. */

    verify(transaction)
      .queries(StockCountType.class);
    verify(items)
      .execute(PARAMETERS);

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(items);
  }
}
