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
import com.io7m.cardant.protocol.inventory.CAIResponseLocationTypesAssign;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLocationTypesAssign;
import com.io7m.cedarbridge.runtime.api.CBUUID;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVLocation.LOCATION;

/**
 * A validator.
 */

public enum CAUVResponseLocationTypesAssign
  implements CAProtocolMessageValidatorType<
  CAIResponseLocationTypesAssign, CAI1ResponseLocationTypesAssign>
{
  /**
   * A validator.
   */

  RESPONSE_LOCATION_TYPES_ASSIGN;

  @Override
  public CAI1ResponseLocationTypesAssign convertToWire(
    final CAIResponseLocationTypesAssign c)
  {
    return new CAI1ResponseLocationTypesAssign(
      new CBUUID(c.requestId()),
      LOCATION.convertToWire(c.data())
    );
  }

  @Override
  public CAIResponseLocationTypesAssign convertFromWire(
    final CAI1ResponseLocationTypesAssign m)
  {
    return new CAIResponseLocationTypesAssign(
      m.fieldRequestId().value(),
      LOCATION.convertFromWire(m.fieldLocation())
    );
  }
}
