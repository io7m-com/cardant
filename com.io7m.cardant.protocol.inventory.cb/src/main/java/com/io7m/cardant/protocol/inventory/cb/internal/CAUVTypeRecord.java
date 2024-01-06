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

import com.io7m.cardant.model.CATypeRecord;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeRecord;
import com.io7m.cedarbridge.runtime.convenience.CBMaps;
import com.io7m.lanark.core.RDottedName;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVTypeField.TYPE_FIELD;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVTypePackageIdentifier.TYPE_PACKAGE_IDENTIFIER;
import static com.io7m.cedarbridge.runtime.api.CBCore.string;

/**
 * A validator.
 */

public enum CAUVTypeRecord
  implements CAProtocolMessageValidatorType<CATypeRecord, CAI1TypeRecord>
{
  /**
   * A validator.
   */

  TYPE_RECORD;

  @Override
  public CAI1TypeRecord convertToWire(
    final CATypeRecord message)
  {
    return new CAI1TypeRecord(
      TYPE_PACKAGE_IDENTIFIER.convertToWire(message.packageIdentifier()),
      string(message.name().value()),
      string(message.description()),
      CBMaps.ofMap(
        message.fields(),
        s -> string(s.value()),
        TYPE_FIELD::convertToWire
      )
    );
  }

  @Override
  public CATypeRecord convertFromWire(
    final CAI1TypeRecord message)
  {
    return new CATypeRecord(
      TYPE_PACKAGE_IDENTIFIER.convertFromWire(message.fieldPackageIdentifier()),
      new RDottedName(message.fieldName().value()),
      message.fieldDescription().value(),
      CBMaps.toMap(
        message.fieldFields(),
        s -> new RDottedName(s.value()),
        TYPE_FIELD::convertFromWire
      )
    );
  }
}
