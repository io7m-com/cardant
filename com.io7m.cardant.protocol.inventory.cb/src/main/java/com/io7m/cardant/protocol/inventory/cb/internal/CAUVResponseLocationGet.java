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
import com.io7m.cardant.protocol.inventory.CAIResponseLocationGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLocationGet;
import com.io7m.cedarbridge.runtime.api.CBUUID;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVLocation.LOCATION;

/**
 * A validator.
 */

public enum CAUVResponseLocationGet
  implements CAProtocolMessageValidatorType<CAIResponseLocationGet, CAI1ResponseLocationGet>
{
  /**
   * A validator.
   */

  RESPONSE_LOCATION_GET;

  @Override
  public CAI1ResponseLocationGet convertToWire(
    final CAIResponseLocationGet c)
  {
    return new CAI1ResponseLocationGet(
      new CBUUID(c.requestId()), LOCATION.convertToWire(c.data())
    );
  }

  @Override
  public CAIResponseLocationGet convertFromWire(
    final CAI1ResponseLocationGet m)
  {
    return new CAIResponseLocationGet(
      m.fieldRequestId().value(),
      LOCATION.convertFromWire(m.fieldLocation())
    );
  }
}
