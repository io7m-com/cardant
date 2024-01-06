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
import com.io7m.cardant.model.CAItemRepositSerialAdd;
import com.io7m.cardant.model.CAItemRepositSerialMove;
import com.io7m.cardant.model.CAItemRepositSerialRemove;
import com.io7m.cardant.model.CAItemRepositSetAdd;
import com.io7m.cardant.model.CAItemRepositSetMove;
import com.io7m.cardant.model.CAItemRepositSetRemove;
import com.io7m.cardant.model.CAItemRepositType;
import com.io7m.cardant.model.CAItemSerial;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemReposit;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemReposit.CAI1ItemRepositSerialAdd;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemReposit.CAI1ItemRepositSerialMove;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemReposit.CAI1ItemRepositSerialRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemReposit.CAI1ItemRepositSetAdd;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemReposit.CAI1ItemRepositSetMove;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemReposit.CAI1ItemRepositSetRemove;
import com.io7m.cedarbridge.runtime.api.CBIntegerUnsigned64;
import com.io7m.cedarbridge.runtime.api.CBUUID;

import static com.io7m.cedarbridge.runtime.api.CBCore.string;

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
      case final CAItemRepositSetAdd a -> {
        yield new CAI1ItemRepositSetAdd(
          new CBUUID(a.item().id()),
          new CBUUID(a.location().id()),
          new CBIntegerUnsigned64(a.count())
        );
      }

      case final CAItemRepositSetRemove r -> {
        yield new CAI1ItemRepositSetRemove(
          new CBUUID(r.item().id()),
          new CBUUID(r.location().id()),
          new CBIntegerUnsigned64(r.count())
        );
      }

      case final CAItemRepositSetMove m -> {
        yield new CAI1ItemRepositSetMove(
          new CBUUID(m.item().id()),
          new CBUUID(m.fromLocation().id()),
          new CBUUID(m.toLocation().id()),
          new CBIntegerUnsigned64(m.count())
        );
      }

      case final CAItemRepositSerialAdd a -> {
        yield new CAI1ItemRepositSerialAdd(
          new CBUUID(a.item().id()),
          new CBUUID(a.location().id()),
          string(a.serial().value())
        );
      }

      case final CAItemRepositSerialRemove r -> {
        yield new CAI1ItemRepositSerialRemove(
          new CBUUID(r.item().id()),
          new CBUUID(r.location().id()),
          string(r.serial().value())
        );
      }

      case final CAItemRepositSerialMove m -> {
        yield new CAI1ItemRepositSerialMove(
          new CBUUID(m.item().id()),
          new CBUUID(m.fromLocation().id()),
          new CBUUID(m.toLocation().id()),
          string(m.serial().value())
        );
      }
    };
  }

  @Override
  public CAItemRepositType convertFromWire(
    final CAI1ItemReposit message)
  {
    return switch (message) {
      case final CAI1ItemRepositSetMove m -> {
        yield new CAItemRepositSetMove(
          new CAItemID(m.fieldItemId().value()),
          new CALocationID(m.fieldLocationFrom().value()),
          new CALocationID(m.fieldLocationTo().value()),
          m.fieldCount().value()
        );
      }

      case final CAI1ItemRepositSetRemove r -> {
        yield new CAItemRepositSetRemove(
          new CAItemID(r.fieldItemId().value()),
          new CALocationID(r.fieldLocationId().value()),
          r.fieldCount().value()
        );
      }

      case final CAI1ItemRepositSetAdd a -> {
        yield new CAItemRepositSetAdd(
          new CAItemID(a.fieldItemId().value()),
          new CALocationID(a.fieldLocationId().value()),
          a.fieldCount().value()
        );
      }

      case final CAI1ItemRepositSerialAdd a -> {
        yield new CAItemRepositSerialAdd(
          new CAItemID(a.fieldItemId().value()),
          new CALocationID(a.fieldLocationId().value()),
          new CAItemSerial(a.fieldSerial().value())
        );
      }

      case final CAI1ItemReposit.CAI1ItemRepositSerialMove m -> {
        yield new CAItemRepositSerialMove(
          new CAItemID(m.fieldItemId().value()),
          new CALocationID(m.fieldLocationFrom().value()),
          new CALocationID(m.fieldLocationTo().value()),
          new CAItemSerial(m.fieldSerial().value())
        );
      }

      case final CAI1ItemRepositSerialRemove r -> {
        yield new CAItemRepositSerialRemove(
          new CAItemID(r.fieldItemId().value()),
          new CALocationID(r.fieldLocationId().value()),
          new CAItemSerial(r.fieldSerial().value())
        );
      }
    };
  }
}
