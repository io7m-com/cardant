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

import com.io7m.cardant.model.type_package.CATypePackageTypeRemovalBehavior;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypePackageTypeRemovalBehavior;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypePackageTypeRemovalBehavior.FailIfTypesReferenced;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypePackageTypeRemovalBehavior.RevokeTypes;

/**
 * A validator.
 */

public enum CAUVTypePackageTypeRemovalBehavior
  implements CAProtocolMessageValidatorType<
  CATypePackageTypeRemovalBehavior, CAI1TypePackageTypeRemovalBehavior>
{
  /**
   * A validator.
   */

  TYPE_PACKAGE_TYPE_REMOVAL_BEHAVIOR;

  @Override
  public CAI1TypePackageTypeRemovalBehavior convertToWire(
    final CATypePackageTypeRemovalBehavior c)
    throws CAProtocolException
  {
    return switch (c) {
      case TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED -> {
        yield new FailIfTypesReferenced();
      }
      case TYPE_REMOVAL_REVOKE_TYPES -> {
        yield new RevokeTypes();
      }
    };
  }

  @Override
  public CATypePackageTypeRemovalBehavior convertFromWire(
    final CAI1TypePackageTypeRemovalBehavior m)
    throws CAProtocolException
  {
    return switch (m) {
      case final FailIfTypesReferenced u -> {
        yield CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED;
      }
      case final RevokeTypes u -> {
        yield CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_REVOKE_TYPES;
      }
    };
  }
}
