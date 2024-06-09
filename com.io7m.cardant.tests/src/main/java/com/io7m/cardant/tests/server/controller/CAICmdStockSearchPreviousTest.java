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

import com.io7m.cardant.database.api.CADatabaseQueriesStockType.StockSearchType;
import com.io7m.cardant.database.api.CADatabaseStockSearchType;
import com.io7m.cardant.model.CAPage;
import com.io7m.cardant.model.CAStockOccurrenceSet;
import com.io7m.cardant.model.CAStockOccurrenceType;
import com.io7m.cardant.protocol.inventory.CAICommandStockSearchPrevious;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdStockSearchPrevious;
import com.io7m.medrina.api.MMatchActionType.MMatchActionWithName;
import com.io7m.medrina.api.MMatchObjectType.MMatchObjectWithType;
import com.io7m.medrina.api.MMatchSubjectType.MMatchSubjectWithRolesAny;
import com.io7m.medrina.api.MPolicy;
import com.io7m.medrina.api.MRule;
import com.io7m.medrina.api.MRuleName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorApiMisuse;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSecurityPolicyDenied;
import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_STOCK;
import static com.io7m.cardant.security.CASecurityPolicy.READ;
import static com.io7m.cardant.security.CASecurityPolicy.ROLE_INVENTORY_STOCK_READER;
import static com.io7m.cardant.tests.server.controller.CAICmdStockSearchBeginTest.I0;
import static com.io7m.cardant.tests.server.controller.CAICmdStockSearchBeginTest.I1;
import static com.io7m.cardant.tests.server.controller.CAICmdStockSearchBeginTest.L0S;
import static com.io7m.cardant.tests.server.controller.CAICmdStockSearchBeginTest.L1S;
import static com.io7m.medrina.api.MRuleConclusion.ALLOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @see CAICmdStockSearchPrevious
 */

public final class CAICmdStockSearchPreviousTest
  extends CACmdAbstractContract
{
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
      new CAICmdStockSearchPrevious();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandStockSearchPrevious());
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
      mock(StockSearchType.class);
    final var itemSearch =
      mock(CADatabaseStockSearchType.class);

    final var transaction =
      this.transaction();

    final var page =
      new CAPage<CAStockOccurrenceType>(
        List.of(
          new CAStockOccurrenceSet(L0S, I0, 23L),
          new CAStockOccurrenceSet(L0S, I1, 20L),
          new CAStockOccurrenceSet(L1S, I1, 10L)
        ),
        1,
        1,
        0L
      );

    when(transaction.queries(StockSearchType.class))
      .thenReturn(items);
    when(items.execute(any()))
      .thenReturn(itemSearch);
    when(itemSearch.pagePrevious(any()))
      .thenReturn(page);

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
    final var session =
      context.session();

    session.setProperty(CADatabaseStockSearchType.class, itemSearch);

    /* Act. */

    final var handler = new CAICmdStockSearchPrevious();
    handler.execute(context, new CAICommandStockSearchPrevious());

    /* Assert. */

    verify(itemSearch)
      .pagePrevious(transaction);

    verifyNoMoreInteractions(itemSearch);
    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(items);
  }

  /**
   * Searching for items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testSearchMustBegin()
    throws Exception
  {
    /* Arrange. */

    final var items =
      mock(StockSearchType.class);
    final var itemSearch =
      mock(CADatabaseStockSearchType.class);

    final var transaction =
      this.transaction();

    when(transaction.queries(StockSearchType.class))
      .thenReturn(items);
    when(items.execute(any()))
      .thenReturn(itemSearch);

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

    final var handler = new CAICmdStockSearchPrevious();

    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(context, new CAICommandStockSearchPrevious());
      });

    /* Assert. */

    assertEquals(errorApiMisuse(), ex.errorCode());
  }
}
