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

import com.io7m.cardant.model.CAItemColumn;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemColumn;

/**
 * A validator.
 */

public enum CAUVItemColumn
  implements CAProtocolMessageValidatorType<CAItemColumn, CAI1ItemColumn>
{
  /**
   * A validator.
   */

  ITEM_COLUMN;

  @Override
  public CAI1ItemColumn convertToWire(
    final CAItemColumn parameters)
  {
    return switch (parameters) {
      case BY_ID -> {
        yield new CAI1ItemColumn.CAI1ById();
      }
      case BY_NAME -> {
        yield new CAI1ItemColumn.CAI1ByName();
      }
    };
  }

  @Override
  public CAItemColumn convertFromWire(
    final CAI1ItemColumn message)
  {
    return switch (message) {
      case final CAI1ItemColumn.CAI1ByName c -> {
        yield CAItemColumn.BY_NAME;
      }
      case final CAI1ItemColumn.CAI1ById c -> {
        yield CAItemColumn.BY_ID;
      }
    };
  }
}
