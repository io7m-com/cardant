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

import com.io7m.cardant.model.type_package.CATypePackageVersionBehavior;
import com.io7m.cardant.protocol.api.CAProtocolException;

public enum CJ1TypePackageVersionBehaviorX
  implements CJ1SerialBijectionType<CJ1TypePackageVersionBehavior, CATypePackageVersionBehavior>
{
  TYPE_PACKAGE_VERSION_BEHAVIOR;

  @Override
  public CATypePackageVersionBehavior toCore(
    final CJ1TypePackageVersionBehavior m)
    throws CAProtocolException
  {
    return switch (m) {
      case VERSION_ALLOW_DOWNGRADES ->
        CATypePackageVersionBehavior.VERSION_ALLOW_DOWNGRADES;
      case VERSION_DISALLOW_DOWNGRADES ->
        CATypePackageVersionBehavior.VERSION_DISALLOW_DOWNGRADES;
    };
  }

  @Override
  public CJ1TypePackageVersionBehavior toCJ1(
    final CATypePackageVersionBehavior m)
    throws CAProtocolException
  {
    return switch (m) {
      case VERSION_ALLOW_DOWNGRADES ->
        CJ1TypePackageVersionBehavior.VERSION_ALLOW_DOWNGRADES;
      case VERSION_DISALLOW_DOWNGRADES ->
        CJ1TypePackageVersionBehavior.VERSION_DISALLOW_DOWNGRADES;
    };
  }
}
