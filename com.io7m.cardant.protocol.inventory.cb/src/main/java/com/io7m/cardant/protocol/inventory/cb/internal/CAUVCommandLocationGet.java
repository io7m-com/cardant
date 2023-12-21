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
import com.io7m.cardant.protocol.inventory.CAICommandLocationGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandLocationGet;
import com.io7m.cedarbridge.runtime.api.CBUUID;

/**
 * A validator.
 */

public enum CAUVCommandLocationGet
  implements CAProtocolMessageValidatorType<CAICommandLocationGet, CAI1CommandLocationGet>
{
  /**
   * A validator.
   */

  COMMAND_LOCATION_GET;

  @Override
  public CAI1CommandLocationGet convertToWire(
    final CAICommandLocationGet message)
  {
    return new CAI1CommandLocationGet(new CBUUID(message.id().id()));
  }

  @Override
  public CAICommandLocationGet convertFromWire(
    final CAI1CommandLocationGet message)
  {
    return new CAICommandLocationGet(new CALocationID(message.fieldLocation().value()));
  }
}
