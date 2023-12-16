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

import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationSummary;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1LocationSummary;
import com.io7m.cedarbridge.runtime.api.CBOptionType;
import com.io7m.cedarbridge.runtime.api.CBString;
import com.io7m.cedarbridge.runtime.api.CBUUID;

/**
 * A validator.
 */

public enum CAUVLocationSummary
  implements CAProtocolMessageValidatorType<CALocationSummary, CAI1LocationSummary>
{
  /**
   * A validator.
   */

  LOCATION_SUMMARY;

  @Override
  public CAI1LocationSummary convertToWire(
    final CALocationSummary message)
  {
    return new CAI1LocationSummary(
      new CBUUID(message.id().id()),
      CBOptionType.fromOptional(message.parent().map(x -> new CBUUID(x.id()))),
      new CBString(message.name())
    );
  }

  @Override
  public CALocationSummary convertFromWire(
    final CAI1LocationSummary message)
  {
    return new CALocationSummary(
      new CALocationID(message.fieldId().value()),
      message.fieldParent()
        .asOptional()
        .map(x -> new CALocationID(x.value())),
      message.fieldName().value()
    );
  }
}
