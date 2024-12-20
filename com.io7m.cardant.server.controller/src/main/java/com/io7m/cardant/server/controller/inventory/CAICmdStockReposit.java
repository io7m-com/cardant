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

package com.io7m.cardant.server.controller.inventory;

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseQueriesStockType;
import com.io7m.cardant.database.api.CADatabaseQueriesStockType.StockRepositType;
import com.io7m.cardant.protocol.inventory.CAICommandStockReposit;
import com.io7m.cardant.protocol.inventory.CAIResponseStockReposit;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.security.CASecurityException;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;

import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_ITEMS;
import static com.io7m.cardant.security.CASecurityPolicy.WRITE;

/**
 * @see CAICommandStockReposit
 */

public final class CAICmdStockReposit
  extends CAICmdAbstract<CAICommandStockReposit>
{
  /**
   * @see CAICommandStockReposit
   */

  public CAICmdStockReposit()
  {

  }

  @Override
  protected CAIResponseType executeActual(
    final CAICommandContext context,
    final CAICommandStockReposit command)
    throws CASecurityException, CADatabaseException, CACommandExecutionFailure
  {
    context.securityCheck(INVENTORY_ITEMS, WRITE);

    final var transaction =
      context.transaction();
    final var repositQuery =
      transaction.queries(StockRepositType.class);
    final var get =
      transaction.queries(CADatabaseQueriesStockType.StockGetType.class);

    final var reposit =
      command.reposit();

    repositQuery.execute(reposit);

    final var item =
      get.execute(reposit.instance())
        .orElseThrow();

    return new CAIResponseStockReposit(context.requestId(), item);
  }
}
