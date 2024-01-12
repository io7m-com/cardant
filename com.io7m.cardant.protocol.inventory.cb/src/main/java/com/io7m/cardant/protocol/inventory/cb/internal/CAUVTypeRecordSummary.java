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

import com.io7m.cardant.model.CATypeRecordSummary;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeRecordSummary;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVTypeRecordIdentifier.TYPE_RECORD_IDENTIFIER;
import static com.io7m.cedarbridge.runtime.api.CBCore.string;

/**
 * A validator.
 */

public enum CAUVTypeRecordSummary
  implements CAProtocolMessageValidatorType<CATypeRecordSummary, CAI1TypeRecordSummary>
{
  /**
   * A validator.
   */

  TYPE_RECORD_SUMMARY;

  @Override
  public CAI1TypeRecordSummary convertToWire(
    final CATypeRecordSummary message)
  {
    return new CAI1TypeRecordSummary(
      TYPE_RECORD_IDENTIFIER.convertToWire(message.name()),
      string(message.description())
    );
  }

  @Override
  public CATypeRecordSummary convertFromWire(
    final CAI1TypeRecordSummary message)
  {
    return new CATypeRecordSummary(
      TYPE_RECORD_IDENTIFIER.convertFromWire(message.fieldName()),
      message.fieldDescription().value()
    );
  }
}
