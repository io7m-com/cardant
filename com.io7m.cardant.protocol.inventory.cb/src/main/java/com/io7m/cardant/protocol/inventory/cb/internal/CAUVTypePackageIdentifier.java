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

import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypePackageIdentifier;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVDottedNames.DOTTED_NAMES;

/**
 * A validator.
 */

public enum CAUVTypePackageIdentifier
  implements CAProtocolMessageValidatorType<CATypePackageIdentifier, CAI1TypePackageIdentifier>
{
  /**
   * A validator.
   */

  TYPE_PACKAGE_IDENTIFIER;

  @Override
  public CAI1TypePackageIdentifier convertToWire(
    final CATypePackageIdentifier parameters)
  {
    return new CAI1TypePackageIdentifier(
      DOTTED_NAMES.convertToWire(parameters.name()),
      CAUVVersion.VERSION.convertToWire(parameters.version())
    );
  }

  @Override
  public CATypePackageIdentifier convertFromWire(
    final CAI1TypePackageIdentifier message)
  {
    return new CATypePackageIdentifier(
      DOTTED_NAMES.convertFromWire(message.fieldName()),
      CAUVVersion.VERSION.convertFromWire(message.fieldVersion())
    );
  }
}
