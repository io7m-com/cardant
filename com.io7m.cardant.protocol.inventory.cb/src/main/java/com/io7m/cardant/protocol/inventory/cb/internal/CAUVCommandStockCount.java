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


package com.io7m.cardant.protocol.inventory.cb.internal;

import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.CAICommandStockCount;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandStockCount;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVStockSearchParameters.STOCK_SEARCH_PARAMETERS;

/**
 * A validator.
 */

public enum CAUVCommandStockCount
  implements CAProtocolMessageValidatorType<CAICommandStockCount, CAI1CommandStockCount>
{
  /**
   * A validator.
   */

  COMMAND_STOCK_COUNT;

  @Override
  public CAI1CommandStockCount convertToWire(
    final CAICommandStockCount message)
    throws CAProtocolException
  {
    return new CAI1CommandStockCount(
      STOCK_SEARCH_PARAMETERS.convertToWire(message.searchParameters())
    );
  }

  @Override
  public CAICommandStockCount convertFromWire(
    final CAI1CommandStockCount message)
    throws CAProtocolException
  {
    return new CAICommandStockCount(
      STOCK_SEARCH_PARAMETERS.convertFromWire(message.fieldParameters())
    );
  }
}
