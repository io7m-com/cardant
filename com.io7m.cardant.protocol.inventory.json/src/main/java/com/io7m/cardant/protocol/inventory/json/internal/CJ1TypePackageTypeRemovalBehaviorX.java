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

import com.io7m.cardant.model.type_package.CATypePackageTypeRemovalBehavior;
import com.io7m.cardant.protocol.api.CAProtocolException;

public enum CJ1TypePackageTypeRemovalBehaviorX
  implements CJ1SerialBijectionType<CJ1TypePackageTypeRemovalBehavior, CATypePackageTypeRemovalBehavior>
{
  TYPE_PACKAGE_TYPE_REMOVAL_BEHAVIOR;

  @Override
  public CATypePackageTypeRemovalBehavior toCore(
    final CJ1TypePackageTypeRemovalBehavior m)
    throws CAProtocolException
  {
    return switch (m) {
      case TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED ->
        CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED;
      case TYPE_REMOVAL_REVOKE_TYPES ->
        CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_REVOKE_TYPES;
    };
  }

  @Override
  public CJ1TypePackageTypeRemovalBehavior toCJ1(
    final CATypePackageTypeRemovalBehavior m)
    throws CAProtocolException
  {
    return switch (m) {
      case TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED ->
        CJ1TypePackageTypeRemovalBehavior.TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED;
      case TYPE_REMOVAL_REVOKE_TYPES ->
        CJ1TypePackageTypeRemovalBehavior.TYPE_REMOVAL_REVOKE_TYPES;
    };
  }
}
