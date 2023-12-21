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

import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CAItemRepositMove;
import com.io7m.cardant.model.CAItemRepositRemove;
import com.io7m.cardant.model.CAItemRepositType;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemReposit;
import com.io7m.cedarbridge.runtime.api.CBIntegerUnsigned64;
import com.io7m.cedarbridge.runtime.api.CBUUID;

/**
 * A validator.
 */

public enum CAUVItemReposit
  implements CAProtocolMessageValidatorType<CAItemRepositType, CAI1ItemReposit>
{
  /**
   * A validator.
   */

  ITEM_REPOSIT;

  @Override
  public CAI1ItemReposit convertToWire(
    final CAItemRepositType message)
  {
    return switch (message) {
      case final CAItemRepositAdd a -> {
        yield new CAI1ItemReposit.CAI1ItemRepositAdd(
          new CBUUID(a.item().id()),
          new CBUUID(a.location().id()),
          new CBIntegerUnsigned64(a.count())
        );
      }
      case final CAItemRepositRemove r -> {
        yield new CAI1ItemReposit.CAI1ItemRepositRemove(
          new CBUUID(r.item().id()),
          new CBUUID(r.location().id()),
          new CBIntegerUnsigned64(r.count())
        );
      }
      case final CAItemRepositMove m -> {
        yield new CAI1ItemReposit.CAI1ItemRepositMove(
          new CBUUID(m.item().id()),
          new CBUUID(m.fromLocation().id()),
          new CBUUID(m.toLocation().id()),
          new CBIntegerUnsigned64(m.count())
        );
      }
    };
  }

  @Override
  public CAItemRepositType convertFromWire(
    final CAI1ItemReposit message)
  {
    return switch (message) {
      case final CAI1ItemReposit.CAI1ItemRepositMove m -> {
        yield new CAItemRepositMove(
          new CAItemID(m.fieldItemId().value()),
          new CALocationID(m.fieldLocationFrom().value()),
          new CALocationID(m.fieldLocationTo().value()),
          m.fieldCount().value()
        );
      }
      case final CAI1ItemReposit.CAI1ItemRepositRemove r -> {
        yield new CAItemRepositRemove(
          new CAItemID(r.fieldItemId().value()),
          new CALocationID(r.fieldLocationId().value()),
          r.fieldCount().value()
        );
      }
      case final CAI1ItemReposit.CAI1ItemRepositAdd a -> {
        yield new CAItemRepositAdd(
          new CAItemID(a.fieldItemId().value()),
          new CALocationID(a.fieldLocationId().value()),
          a.fieldCount().value()
        );
      }
    };
  }
}
