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
import com.io7m.cardant.database.api.CADatabaseQueriesStockType.StockSearchType;
import com.io7m.cardant.database.api.CADatabaseStockSearchType;
import com.io7m.cardant.protocol.inventory.CAICommandStockSearchBegin;
import com.io7m.cardant.protocol.inventory.CAIResponseStockSearch;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.security.CASecurityException;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;

import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_STOCK;
import static com.io7m.cardant.security.CASecurityPolicy.READ;

/**
 * @see CAICommandStockSearchBegin
 */

public final class CAICmdStockSearchBegin 
  extends CAICmdAbstract<CAICommandStockSearchBegin>
{
  /**
   * @see CAICommandStockSearchBegin
   */

  public CAICmdStockSearchBegin()
  {

  }

  @Override
  protected CAIResponseType executeActual(
    final CAICommandContext context,
    final CAICommandStockSearchBegin command)
    throws CASecurityException, CADatabaseException, CACommandExecutionFailure
  {
    context.securityCheck(INVENTORY_STOCK, READ);

    final var transaction =
      context.transaction();
    final var searchQuery =
      transaction.queries(StockSearchType.class);

    final var search =
      searchQuery.execute(command.searchParameters());

    context.session()
      .setProperty(CADatabaseStockSearchType.class, search);

    return new CAIResponseStockSearch(
      context.requestId(),
      search.pageCurrent(transaction)
    );
  }
}
