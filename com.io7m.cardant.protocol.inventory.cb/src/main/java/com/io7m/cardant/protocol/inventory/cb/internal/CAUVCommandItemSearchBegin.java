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
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchBegin;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemSearchBegin;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVItemSearchParameters.ITEM_SEARCH_PARAMETERS;

/**
 * A validator.
 */

public enum CAUVCommandItemSearchBegin
  implements CAProtocolMessageValidatorType<CAICommandItemSearchBegin, CAI1CommandItemSearchBegin>
{
  /**
   * A validator.
   */

  COMMAND_ITEM_SEARCH_BEGIN;

  @Override
  public CAI1CommandItemSearchBegin convertToWire(
    final CAICommandItemSearchBegin c)
    throws CAProtocolException
  {
    return new CAI1CommandItemSearchBegin(
      ITEM_SEARCH_PARAMETERS.convertToWire(c.parameters())
    );
  }

  @Override
  public CAICommandItemSearchBegin convertFromWire(
    final CAI1CommandItemSearchBegin m)
    throws CAProtocolException
  {
    return new CAICommandItemSearchBegin(
      ITEM_SEARCH_PARAMETERS.convertFromWire(m.fieldParameters())
    );
  }
}
