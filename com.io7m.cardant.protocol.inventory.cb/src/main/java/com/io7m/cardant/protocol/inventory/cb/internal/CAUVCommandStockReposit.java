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
import com.io7m.cardant.protocol.inventory.CAICommandStockReposit;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandStockReposit;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVStockReposit.STOCK_REPOSIT;

/**
 * A validator.
 */

public enum CAUVCommandStockReposit
  implements CAProtocolMessageValidatorType<CAICommandStockReposit, CAI1CommandStockReposit>
{
  /**
   * A validator.
   */

  COMMAND_STOCK_REPOSIT;

  @Override
  public CAI1CommandStockReposit convertToWire(
    final CAICommandStockReposit message)
  {
    return new CAI1CommandStockReposit(
      STOCK_REPOSIT.convertToWire(message.reposit())
    );
  }

  @Override
  public CAICommandStockReposit convertFromWire(
    final CAI1CommandStockReposit message)
  {
    return new CAICommandStockReposit(
      STOCK_REPOSIT.convertFromWire(message.fieldReposit())
    );
  }
}
