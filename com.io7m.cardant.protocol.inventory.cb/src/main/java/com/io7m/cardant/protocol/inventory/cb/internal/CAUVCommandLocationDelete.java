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

import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.CAICommandLocationDelete;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandLocationDelete;
import com.io7m.cedarbridge.runtime.api.CBUUID;

/**
 * A validator.
 */

public enum CAUVCommandLocationDelete
  implements CAProtocolMessageValidatorType<CAICommandLocationDelete, CAI1CommandLocationDelete>
{
  /**
   * A validator.
   */

  COMMAND_LOCATION_DELETE;

  @Override
  public CAI1CommandLocationDelete convertToWire(
    final CAICommandLocationDelete c)
  {
    return new CAI1CommandLocationDelete(
      new CBUUID(c.data().id())
    );
  }

  @Override
  public CAICommandLocationDelete convertFromWire(
    final CAI1CommandLocationDelete m)
  {
    return new CAICommandLocationDelete(
      new CALocationID(m.fieldLocation().value())
    );
  }
}
