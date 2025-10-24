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
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageUninstall;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1TypePackageUninstallX.TYPE_PACKAGE_UNINSTALL_TYPE;

public enum CJ1CommandTypePackageUninstallX
  implements CJ1SerialBijectionType<CJ1CommandTypePackageUninstall, CAICommandTypePackageUninstall>
{
  TYPE_PACKAGE_UNINSTALL;

  @Override
  public CAICommandTypePackageUninstall toCore(
    final CJ1CommandTypePackageUninstall m)
    throws CAProtocolException
  {
    return new CAICommandTypePackageUninstall(
      TYPE_PACKAGE_UNINSTALL_TYPE.toCore(m.uninstall())
    );
  }

  @Override
  public CJ1CommandTypePackageUninstall toCJ1(
    final CAICommandTypePackageUninstall m)
    throws CAProtocolException
  {
    return new CJ1CommandTypePackageUninstall(
      TYPE_PACKAGE_UNINSTALL_TYPE.toCJ1(m.uninstall())
    );
  }
}
