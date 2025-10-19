/*
 * Copyright © 2025 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.protocol.inventory.json.internal;

import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageUpgrade;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1TypePackageTypeRemovalBehaviorX.TYPE_PACKAGE_TYPE_REMOVAL_BEHAVIOR;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1TypePackageVersionBehaviorX.TYPE_PACKAGE_VERSION_BEHAVIOR;

public enum CJ1CommandTypePackageUpgradeX
  implements CJ1SerialBijectionType<CJ1CommandTypePackageUpgrade, CAICommandTypePackageUpgrade>
{
  TYPE_PACKAGE_UPGRADE;

  @Override
  public CAICommandTypePackageUpgrade toCore(
    final CJ1CommandTypePackageUpgrade m)
    throws CAProtocolException
  {
    return new CAICommandTypePackageUpgrade(
      TYPE_PACKAGE_TYPE_REMOVAL_BEHAVIOR.toCore(m.typeRemovalBehavior()),
      TYPE_PACKAGE_VERSION_BEHAVIOR.toCore(m.versionBehavior()),
      m.text()
    );
  }

  @Override
  public CJ1CommandTypePackageUpgrade toCJ1(
    final CAICommandTypePackageUpgrade m)
    throws CAProtocolException
  {
    return new CJ1CommandTypePackageUpgrade(
      TYPE_PACKAGE_TYPE_REMOVAL_BEHAVIOR.toCJ1(m.typeRemovalBehavior()),
      TYPE_PACKAGE_VERSION_BEHAVIOR.toCJ1(m.versionBehavior()),
      m.text()
    );
  }
}
