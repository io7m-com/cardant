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

import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1Error;
import com.io7m.cedarbridge.runtime.api.CBOptionType;
import com.io7m.cedarbridge.runtime.api.CBString;
import com.io7m.cedarbridge.runtime.convenience.CBMaps;
import com.io7m.seltzer.api.SStructuredError;

import java.util.Optional;

/**
 * A validator.
 */

public enum CAUVError
  implements CAProtocolMessageValidatorType<
  SStructuredError<CAErrorCode>, CAI1Error>
{
  /**
   * A validator.
   */

  ERROR;

  @Override
  public CAI1Error convertToWire(
    final SStructuredError<CAErrorCode> c)
  {
    return new CAI1Error(
      new CBString(c.errorCode().id()),
      new CBString(c.message()),
      CBMaps.ofMapString(c.attributes()),
      CBOptionType.fromOptional(c.remediatingAction().map(CBString::new))
    );
  }

  @Override
  public SStructuredError<CAErrorCode> convertFromWire(
    final CAI1Error m)
  {
    return new SStructuredError<>(
      new CAErrorCode(m.fieldErrorCode().value()),
      m.fieldMessage().value(),
      CBMaps.toMapString(m.fieldAttributes()),
      m.fieldRemediatingAction()
        .asOptional()
        .map(CBString::value),
      Optional.empty()
    );
  }
}
