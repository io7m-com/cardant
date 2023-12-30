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

import com.io7m.cardant.model.type_package.CATypePackageUninstall;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypePackageUninstall;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVTypePackageIdentifier.TYPE_PACKAGE_IDENTIFIER;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVTypePackageUninstallBehavior.TYPE_PACKAGE_UNINSTALL_BEHAVIOR;

/**
 * A validator.
 */

public enum CAUVTypePackageUninstall
  implements CAProtocolMessageValidatorType<
  CATypePackageUninstall, CAI1TypePackageUninstall>
{
  /**
   * A validator.
   */

  TYPE_PACKAGE_UNINSTALL;

  @Override
  public CAI1TypePackageUninstall convertToWire(
    final CATypePackageUninstall c)
    throws CAProtocolException
  {
    return new CAI1TypePackageUninstall(
      TYPE_PACKAGE_UNINSTALL_BEHAVIOR.convertToWire(c.behavior()),
      TYPE_PACKAGE_IDENTIFIER.convertToWire(c.packageIdentifier())
    );
  }

  @Override
  public CATypePackageUninstall convertFromWire(
    final CAI1TypePackageUninstall m)
    throws CAProtocolException
  {
    return new CATypePackageUninstall(
      TYPE_PACKAGE_UNINSTALL_BEHAVIOR.convertFromWire(m.fieldBehavior()),
      TYPE_PACKAGE_IDENTIFIER.convertFromWire(m.fieldIdentifier())
    );
  }
}
