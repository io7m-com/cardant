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

import com.io7m.cardant.model.CATypeRecordFieldIdentifier;
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeRecordFieldIdentifier;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVDottedNames.DOTTED_NAMES;

/**
 * A validator.
 */

public enum CAUVTypeRecordFieldIdentifier
  implements CAProtocolMessageValidatorType<CATypeRecordFieldIdentifier, CAI1TypeRecordFieldIdentifier>
{
  /**
   * A validator.
   */

  TYPE_RECORD_FIELD_IDENTIFIER;

  @Override
  public CAI1TypeRecordFieldIdentifier convertToWire(
    final CATypeRecordFieldIdentifier parameters)
  {
    return new CAI1TypeRecordFieldIdentifier(
      DOTTED_NAMES.convertToWire(parameters.typeName().packageName()),
      DOTTED_NAMES.convertToWire(parameters.typeName().typeName()),
      DOTTED_NAMES.convertToWire(parameters.fieldName())
    );
  }

  @Override
  public CATypeRecordFieldIdentifier convertFromWire(
    final CAI1TypeRecordFieldIdentifier message)
  {
    return new CATypeRecordFieldIdentifier(
      new CATypeRecordIdentifier(
        DOTTED_NAMES.convertFromWire(message.fieldPackageName()),
        DOTTED_NAMES.convertFromWire(message.fieldTypeName())
      ),
      DOTTED_NAMES.convertFromWire(message.fieldFieldName())
    );
  }
}
