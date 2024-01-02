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
import com.io7m.cardant.protocol.inventory.CAIResponseTypePackageUpgrade;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypePackageUpgrade;
import com.io7m.cedarbridge.runtime.api.CBUUID;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVTypePackageIdentifier.TYPE_PACKAGE_IDENTIFIER;

/**
 * A validator.
 */

public enum CAUVResponseTypePackageUpgrade
  implements CAProtocolMessageValidatorType<
  CAIResponseTypePackageUpgrade, CAI1ResponseTypePackageUpgrade>
{
  /**
   * A validator.
   */

  RESPONSE_TYPE_PACKAGE_UPGRADE;

  @Override
  public CAI1ResponseTypePackageUpgrade convertToWire(
    final CAIResponseTypePackageUpgrade c)
  {
    return new CAI1ResponseTypePackageUpgrade(
      new CBUUID(c.requestId()),
      TYPE_PACKAGE_IDENTIFIER.convertToWire(c.data())
    );
  }

  @Override
  public CAIResponseTypePackageUpgrade convertFromWire(
    final CAI1ResponseTypePackageUpgrade c)
  {
    return new CAIResponseTypePackageUpgrade(
      c.fieldRequestId().value(),
      TYPE_PACKAGE_IDENTIFIER.convertFromWire(c.fieldIdentifier())
    );
  }
}
