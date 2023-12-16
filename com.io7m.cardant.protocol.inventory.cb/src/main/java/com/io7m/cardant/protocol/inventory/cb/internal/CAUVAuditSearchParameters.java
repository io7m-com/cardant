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

import com.io7m.cardant.model.CAAuditSearchParameters;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1AuditSearchParameters;
import com.io7m.cedarbridge.runtime.api.CBOptionType;
import com.io7m.cedarbridge.runtime.api.CBString;
import com.io7m.cedarbridge.runtime.api.CBUUID;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVStrings.STRINGS;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVTimeRange.TIME_RANGE;
import static com.io7m.cedarbridge.runtime.api.CBCore.unsigned32;

/**
 * A validator.
 */

public enum CAUVAuditSearchParameters
  implements CAProtocolMessageValidatorType<CAAuditSearchParameters, CAI1AuditSearchParameters>
{
  /**
   * A validator.
   */

  AUDIT_SEARCH_PARAMETERS;

  private static final CAUVComparisonsExact<String, CBString> EXACT =
    new CAUVComparisonsExact<>(STRINGS);

  @Override
  public CAI1AuditSearchParameters convertToWire(
    final CAAuditSearchParameters parameters)
    throws CAProtocolException
  {
    return new CAI1AuditSearchParameters(
      CBOptionType.fromOptional(
        parameters.owner().map(CAUserID::id).map(CBUUID::new)),
      EXACT.convertToWire(parameters.type()),
      TIME_RANGE.convertToWire(parameters.timeRange()),
      unsigned32(parameters.pageSize())
    );
  }

  @Override
  public CAAuditSearchParameters convertFromWire(
    final CAI1AuditSearchParameters message)
    throws CAProtocolException
  {
    return new CAAuditSearchParameters(
      message.fieldOwner().asOptional()
        .map(CBUUID::value)
        .map(CAUserID::new),
      EXACT.convertFromWire(message.fieldType()),
      TIME_RANGE.convertFromWire(message.fieldTimeRange()),
      message.fieldPageSize().value()
    );
  }
}
