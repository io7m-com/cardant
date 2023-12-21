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
import com.io7m.cardant.protocol.inventory.CAIResponseItemSetName;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseItemSetName;
import com.io7m.cedarbridge.runtime.api.CBUUID;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVItem.ITEM;

/**
 * A validator.
 */

public enum CAUVResponseItemSetName
  implements CAProtocolMessageValidatorType<CAIResponseItemSetName, CAI1ResponseItemSetName>
{
  /**
   * A validator.
   */

  RESPONSE_ITEM_SET_NAME;

  @Override
  public CAI1ResponseItemSetName convertToWire(
    final CAIResponseItemSetName c)
  {
    return new CAI1ResponseItemSetName(
      new CBUUID(c.requestId()), ITEM.convertToWire(c.data())
    );
  }

  @Override
  public CAIResponseItemSetName convertFromWire(
    final CAI1ResponseItemSetName m)
  {
    return new CAIResponseItemSetName(
      m.fieldRequestId().value(),
      ITEM.convertFromWire(m.fieldItem())
    );
  }
}
