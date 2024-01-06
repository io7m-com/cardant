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

import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.CAICommandTypeRecordSearchBegin;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeRecordSearchBegin;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVTypeRecordSearchParameters.TYPE_DECLARATION_SEARCH_PARAMETERS;

/**
 * A validator.
 */

public enum CAUVCommandTypeRecordSearchBegin
  implements CAProtocolMessageValidatorType<
  CAICommandTypeRecordSearchBegin, CAI1CommandTypeRecordSearchBegin>
{
  /**
   * A validator.
   */

  COMMAND_TYPE_RECORD_SEARCH_BEGIN;

  @Override
  public CAI1CommandTypeRecordSearchBegin convertToWire(
    final CAICommandTypeRecordSearchBegin c)
    throws CAProtocolException
  {
    return new CAI1CommandTypeRecordSearchBegin(
      TYPE_DECLARATION_SEARCH_PARAMETERS.convertToWire(c.parameters())
    );
  }

  @Override
  public CAICommandTypeRecordSearchBegin convertFromWire(
    final CAI1CommandTypeRecordSearchBegin m)
    throws CAProtocolException
  {
    return new CAICommandTypeRecordSearchBegin(
      TYPE_DECLARATION_SEARCH_PARAMETERS.convertFromWire(m.fieldParameters())
    );
  }
}
