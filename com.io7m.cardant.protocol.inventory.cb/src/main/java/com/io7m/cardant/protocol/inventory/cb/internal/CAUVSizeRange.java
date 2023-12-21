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

import com.io7m.cardant.model.CASizeRange;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1SizeRange;

import static com.io7m.cedarbridge.runtime.api.CBCore.unsigned64;

/**
 * A validator.
 */

public enum CAUVSizeRange
  implements CAProtocolMessageValidatorType<CASizeRange, CAI1SizeRange>
{
  /**
   * A validator.
   */

  SIZE_RANGE;

  @Override
  public CAI1SizeRange convertToWire(
    final CASizeRange parameters)
  {
    return new CAI1SizeRange(
      unsigned64(parameters.sizeMinimum()),
      unsigned64(parameters.sizeMaximum())
    );
  }

  @Override
  public CASizeRange convertFromWire(
    final CAI1SizeRange message)
  {
    return new CASizeRange(
      message.fieldSizeMinimum().value(),
      message.fieldSizeMaximum().value()
    );
  }
}
