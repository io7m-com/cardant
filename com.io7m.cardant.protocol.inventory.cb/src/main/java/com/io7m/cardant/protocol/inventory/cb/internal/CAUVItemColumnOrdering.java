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

import com.io7m.cardant.model.CAItemColumnOrdering;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemColumnOrdering;
import com.io7m.cedarbridge.runtime.api.CBBooleanType;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVItemColumn.ITEM_COLUMN;

/**
 * A validator.
 */

public enum CAUVItemColumnOrdering
  implements CAProtocolMessageValidatorType<CAItemColumnOrdering, CAI1ItemColumnOrdering>
{
  /**
   * A validator.
   */

  ITEM_COLUMN_ORDERING;

  @Override
  public CAI1ItemColumnOrdering convertToWire(
    final CAItemColumnOrdering parameters)
  {
    return new CAI1ItemColumnOrdering(
      ITEM_COLUMN.convertToWire(parameters.column()),
      CBBooleanType.fromBoolean(parameters.ascending())
    );
  }

  @Override
  public CAItemColumnOrdering convertFromWire(
    final CAI1ItemColumnOrdering message)
  {
    return new CAItemColumnOrdering(
      ITEM_COLUMN.convertFromWire(message.fieldColumn()),
      message.fieldAscending().asBoolean()
    );
  }
}
