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
import com.io7m.cardant.model.CAItemSerial;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAStockRepositSerialAdd;
import com.io7m.cardant.model.CAStockRepositSerialMove;
import com.io7m.cardant.model.CAStockRepositSerialRemove;
import com.io7m.cardant.model.CAStockRepositSetAdd;
import com.io7m.cardant.model.CAStockRepositSetMove;
import com.io7m.cardant.model.CAStockRepositSetRemove;
import com.io7m.cardant.model.CAStockRepositType;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1StockReposit;
import com.io7m.cardant.protocol.inventory.cb.CAI1StockReposit.CAI1StockRepositSerialAdd;
import com.io7m.cardant.protocol.inventory.cb.CAI1StockReposit.CAI1StockRepositSerialMove;
import com.io7m.cardant.protocol.inventory.cb.CAI1StockReposit.CAI1StockRepositSerialRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1StockReposit.CAI1StockRepositSetAdd;
import com.io7m.cardant.protocol.inventory.cb.CAI1StockReposit.CAI1StockRepositSetMove;
import com.io7m.cardant.protocol.inventory.cb.CAI1StockReposit.CAI1StockRepositSetRemove;
import com.io7m.cedarbridge.runtime.api.CBIntegerUnsigned64;
import com.io7m.cedarbridge.runtime.api.CBUUID;

import static com.io7m.cedarbridge.runtime.api.CBCore.string;

/**
 * A validator.
 */

public enum CAUVStockReposit
  implements CAProtocolMessageValidatorType<CAStockRepositType, CAI1StockReposit>
{
  /**
   * A validator.
   */

  STOCK_REPOSIT;

  @Override
  public CAI1StockReposit convertToWire(
    final CAStockRepositType message)
  {
    return switch (message) {
      case final CAStockRepositSetAdd a -> {
        yield new CAI1StockRepositSetAdd(
          new CBUUID(a.item().id()),
          new CBUUID(a.location().id()),
          new CBIntegerUnsigned64(a.count())
        );
      }

      case final CAStockRepositSetRemove r -> {
        yield new CAI1StockRepositSetRemove(
          new CBUUID(r.item().id()),
          new CBUUID(r.location().id()),
          new CBIntegerUnsigned64(r.count())
        );
      }

      case final CAStockRepositSetMove m -> {
        yield new CAI1StockRepositSetMove(
          new CBUUID(m.item().id()),
          new CBUUID(m.fromLocation().id()),
          new CBUUID(m.toLocation().id()),
          new CBIntegerUnsigned64(m.count())
        );
      }

      case final CAStockRepositSerialAdd a -> {
        yield new CAI1StockRepositSerialAdd(
          new CBUUID(a.item().id()),
          new CBUUID(a.location().id()),
          string(a.serial().value())
        );
      }

      case final CAStockRepositSerialRemove r -> {
        yield new CAI1StockRepositSerialRemove(
          new CBUUID(r.item().id()),
          new CBUUID(r.location().id()),
          string(r.serial().value())
        );
      }

      case final CAStockRepositSerialMove m -> {
        yield new CAI1StockRepositSerialMove(
          new CBUUID(m.item().id()),
          new CBUUID(m.fromLocation().id()),
          new CBUUID(m.toLocation().id()),
          string(m.serial().value())
        );
      }
    };
  }

  @Override
  public CAStockRepositType convertFromWire(
    final CAI1StockReposit message)
  {
    return switch (message) {
      case final CAI1StockRepositSetMove m -> {
        yield new CAStockRepositSetMove(
          new CAItemID(m.fieldItemId().value()),
          new CALocationID(m.fieldLocationFrom().value()),
          new CALocationID(m.fieldLocationTo().value()),
          m.fieldCount().value()
        );
      }

      case final CAI1StockRepositSetRemove r -> {
        yield new CAStockRepositSetRemove(
          new CAItemID(r.fieldItemId().value()),
          new CALocationID(r.fieldLocationId().value()),
          r.fieldCount().value()
        );
      }

      case final CAI1StockRepositSetAdd a -> {
        yield new CAStockRepositSetAdd(
          new CAItemID(a.fieldItemId().value()),
          new CALocationID(a.fieldLocationId().value()),
          a.fieldCount().value()
        );
      }

      case final CAI1StockRepositSerialAdd a -> {
        yield new CAStockRepositSerialAdd(
          new CAItemID(a.fieldItemId().value()),
          new CALocationID(a.fieldLocationId().value()),
          new CAItemSerial(a.fieldSerial().value())
        );
      }

      case final CAI1StockReposit.CAI1StockRepositSerialMove m -> {
        yield new CAStockRepositSerialMove(
          new CAItemID(m.fieldItemId().value()),
          new CALocationID(m.fieldLocationFrom().value()),
          new CALocationID(m.fieldLocationTo().value()),
          new CAItemSerial(m.fieldSerial().value())
        );
      }

      case final CAI1StockRepositSerialRemove r -> {
        yield new CAStockRepositSerialRemove(
          new CAItemID(r.fieldItemId().value()),
          new CALocationID(r.fieldLocationId().value()),
          new CAItemSerial(r.fieldSerial().value())
        );
      }
    };
  }
}
