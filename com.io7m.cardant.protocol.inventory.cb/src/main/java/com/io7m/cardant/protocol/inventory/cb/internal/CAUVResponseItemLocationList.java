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
import com.io7m.cardant.protocol.inventory.CAIResponseItemLocationsList;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseItemLocationsList;
import com.io7m.cedarbridge.runtime.api.CBUUID;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVItemLocations.ITEM_LOCATIONS;

/**
 * A validator.
 */

public enum CAUVResponseItemLocationList
  implements CAProtocolMessageValidatorType<
  CAIResponseItemLocationsList, CAI1ResponseItemLocationsList>
{
  /**
   * A validator.
   */

  RESPONSE_ITEM_LOCATION_LIST;

  @Override
  public CAI1ResponseItemLocationsList convertToWire(
    final CAIResponseItemLocationsList c)
  {
    return new CAI1ResponseItemLocationsList(
      new CBUUID(c.requestId()),
      ITEM_LOCATIONS.convertToWire(c.data())
    );
  }

  @Override
  public CAIResponseItemLocationsList convertFromWire(
    final CAI1ResponseItemLocationsList m)
  {
    return new CAIResponseItemLocationsList(
      m.fieldRequestId().value(),
      ITEM_LOCATIONS.convertFromWire(m.fieldItemLocations())
    );
  }
}
