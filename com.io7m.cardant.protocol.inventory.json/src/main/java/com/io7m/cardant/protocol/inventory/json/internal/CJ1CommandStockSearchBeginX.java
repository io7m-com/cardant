/*
 * Copyright © 2025 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.protocol.inventory.json.internal;

import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.inventory.CAICommandStockSearchBegin;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1StockSearchParametersX.STOCK_SEARCH_PARAMETERS;

public enum CJ1CommandStockSearchBeginX
  implements CJ1SerialBijectionType<CJ1CommandStockSearchBegin, CAICommandStockSearchBegin>
{
  STOCK_SEARCH_BEGIN;

  @Override
  public CAICommandStockSearchBegin toCore(
    final CJ1CommandStockSearchBegin m)
    throws CAProtocolException
  {
    return new CAICommandStockSearchBegin(
      STOCK_SEARCH_PARAMETERS.toCore(m.searchParameters())
    );
  }

  @Override
  public CJ1CommandStockSearchBegin toCJ1(
    final CAICommandStockSearchBegin m)
    throws CAProtocolException
  {
    return new CJ1CommandStockSearchBegin(
      STOCK_SEARCH_PARAMETERS.toCJ1(m.searchParameters())
    );
  }
}
