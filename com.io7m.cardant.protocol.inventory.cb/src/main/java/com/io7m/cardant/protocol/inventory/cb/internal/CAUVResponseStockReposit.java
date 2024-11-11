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

import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.CAIResponseStockReposit;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseStockReposit;
import com.io7m.cedarbridge.runtime.api.CBUUID;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVStockOccurrence.STOCK_OCCURRENCE;

/**
 * A validator.
 */

public enum CAUVResponseStockReposit
  implements CAProtocolMessageValidatorType<CAIResponseStockReposit, CAI1ResponseStockReposit>
{
  /**
   * A validator.
   */

  RESPONSE_STOCK_REPOSIT;

  @Override
  public CAI1ResponseStockReposit convertToWire(
    final CAIResponseStockReposit c)
  {
    return new CAI1ResponseStockReposit(
      new CBUUID(c.requestId()),
      STOCK_OCCURRENCE.convertToWire(c.data())
    );
  }

  @Override
  public CAIResponseStockReposit convertFromWire(
    final CAI1ResponseStockReposit m)
  {
    return new CAIResponseStockReposit(
      m.fieldRequestId().value(),
      STOCK_OCCURRENCE.convertFromWire(m.fieldStock())
    );
  }
}
