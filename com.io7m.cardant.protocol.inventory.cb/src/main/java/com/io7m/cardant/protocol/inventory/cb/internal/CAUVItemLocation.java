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

import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemLocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemLocation;
import com.io7m.cedarbridge.runtime.api.CBIntegerUnsigned64;
import com.io7m.cedarbridge.runtime.api.CBUUID;

/**
 * A validator.
 */

public enum CAUVItemLocation
  implements CAProtocolMessageValidatorType<CAItemLocation, CAI1ItemLocation>
{
  /**
   * A validator.
   */

  ITEM_LOCATION;

  @Override
  public CAI1ItemLocation convertToWire(
    final CAItemLocation itemLocation)
  {
    return new CAI1ItemLocation(
      new CBUUID(itemLocation.item().id()),
      new CBUUID(itemLocation.location().id()),
      new CBIntegerUnsigned64(itemLocation.count())
    );
  }

  @Override
  public CAItemLocation convertFromWire(
    final CAI1ItemLocation itemLocation)
  {
    return new CAItemLocation(
      new CAItemID(itemLocation.fieldItemId().value()),
      new CALocationID(itemLocation.fieldLocationId().value()),
      itemLocation.fieldCount().value()
    );
  }
}
