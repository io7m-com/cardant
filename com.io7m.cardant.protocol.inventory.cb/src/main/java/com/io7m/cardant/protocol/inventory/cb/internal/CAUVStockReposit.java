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
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAStockInstanceID;
import com.io7m.cardant.model.CAStockRepositRemove;
import com.io7m.cardant.model.CAStockRepositSerialIntroduce;
import com.io7m.cardant.model.CAStockRepositSerialMove;
import com.io7m.cardant.model.CAStockRepositSerialNumberAdd;
import com.io7m.cardant.model.CAStockRepositSerialNumberRemove;
import com.io7m.cardant.model.CAStockRepositSetAdd;
import com.io7m.cardant.model.CAStockRepositSetIntroduce;
import com.io7m.cardant.model.CAStockRepositSetMove;
import com.io7m.cardant.model.CAStockRepositSetRemove;
import com.io7m.cardant.model.CAStockRepositType;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1StockReposit;
import com.io7m.cardant.protocol.inventory.cb.CAI1StockReposit.CAI1StockRepositSerialIntroduce;
import com.io7m.cardant.protocol.inventory.cb.CAI1StockReposit.CAI1StockRepositSerialMove;
import com.io7m.cardant.protocol.inventory.cb.CAI1StockReposit.CAI1StockRepositSerialNumberAdd;
import com.io7m.cardant.protocol.inventory.cb.CAI1StockReposit.CAI1StockRepositSerialNumberRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1StockReposit.CAI1StockRepositSerialRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1StockReposit.CAI1StockRepositSetAdd;
import com.io7m.cardant.protocol.inventory.cb.CAI1StockReposit.CAI1StockRepositSetIntroduce;
import com.io7m.cardant.protocol.inventory.cb.CAI1StockReposit.CAI1StockRepositSetMove;
import com.io7m.cardant.protocol.inventory.cb.CAI1StockReposit.CAI1StockRepositSetRemove;
import com.io7m.cedarbridge.runtime.api.CBIntegerUnsigned64;
import com.io7m.cedarbridge.runtime.api.CBUUID;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVItemSerial.ITEM_SERIAL;

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
      case final CAStockRepositSetIntroduce a -> {
        yield new CAI1StockRepositSetIntroduce(
          new CBUUID(a.instance().id()),
          new CBUUID(a.item().id()),
          new CBUUID(a.location().id()),
          new CBIntegerUnsigned64(a.count())
        );
      }

      case final CAStockRepositSetRemove r -> {
        yield new CAI1StockRepositSetRemove(
          new CBUUID(r.instance().id()),
          new CBIntegerUnsigned64(r.count())
        );
      }

      case final CAStockRepositSetMove m -> {
        yield new CAI1StockRepositSetMove(
          new CBUUID(m.instanceSource().id()),
          new CBUUID(m.instanceTarget().id()),
          new CBUUID(m.toLocation().id()),
          new CBIntegerUnsigned64(m.count())
        );
      }

      case final CAStockRepositSerialNumberAdd a -> {
        yield new CAI1StockRepositSerialNumberAdd(
          new CBUUID(a.instance().id()),
          ITEM_SERIAL.convertToWire(a.serial())
        );
      }

      case final CAStockRepositRemove r -> {
        yield new CAI1StockRepositSerialRemove(
          new CBUUID(r.instance().id())
        );
      }

      case final CAStockRepositSerialMove m -> {
        yield new CAI1StockRepositSerialMove(
          new CBUUID(m.instance().id()),
          new CBUUID(m.toLocation().id())
        );
      }

      case final CAStockRepositSerialIntroduce r -> {
        yield new CAI1StockRepositSerialIntroduce(
          new CBUUID(r.instance().id()),
          new CBUUID(r.item().id()),
          new CBUUID(r.location().id()),
          ITEM_SERIAL.convertToWire(r.serial())
        );
      }

      case final CAStockRepositSerialNumberRemove r -> {
        yield new CAI1StockRepositSerialNumberRemove(
          new CBUUID(r.instance().id()),
          ITEM_SERIAL.convertToWire(r.serial())
        );
      }

      case final CAStockRepositSetAdd r -> {
        yield new CAI1StockRepositSetAdd(
          new CBUUID(r.instance().id()),
          new CBIntegerUnsigned64(r.count())
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
          new CAStockInstanceID(m.fieldInstanceSource().value()),
          new CAStockInstanceID(m.fieldInstanceTarget().value()),
          new CALocationID(m.fieldLocationTo().value()),
          m.fieldCount().value()
        );
      }

      case final CAI1StockRepositSetRemove r -> {
        yield new CAStockRepositSetRemove(
          new CAStockInstanceID(r.fieldInstanceId().value()),
          r.fieldCount().value()
        );
      }

      case final CAI1StockRepositSetIntroduce a -> {
        yield new CAStockRepositSetIntroduce(
          new CAStockInstanceID(a.fieldInstanceId().value()),
          new CAItemID(a.fieldItemId().value()),
          new CALocationID(a.fieldLocationId().value()),
          a.fieldCount().value()
        );
      }

      case final CAI1StockRepositSerialIntroduce r -> {
        yield new CAStockRepositSerialIntroduce(
          new CAStockInstanceID(r.fieldInstanceId().value()),
          new CAItemID(r.fieldItemId().value()),
          new CALocationID(r.fieldLocationId().value()),
          ITEM_SERIAL.convertFromWire(r.fieldSerial())
        );
      }

      case final CAI1StockRepositSerialMove r -> {
        yield new CAStockRepositSerialMove(
          new CAStockInstanceID(r.fieldInstanceId().value()),
          new CALocationID(r.fieldLocationTo().value())
        );
      }

      case final CAI1StockRepositSerialNumberAdd r -> {
        yield new CAStockRepositSerialNumberAdd(
          new CAStockInstanceID(r.fieldInstanceId().value()),
          ITEM_SERIAL.convertFromWire(r.fieldSerial())
        );
      }

      case final CAI1StockRepositSerialNumberRemove r -> {
        yield new CAStockRepositSerialNumberRemove(
          new CAStockInstanceID(r.fieldInstanceId().value()),
          ITEM_SERIAL.convertFromWire(r.fieldSerial())
        );
      }

      case final CAI1StockRepositSerialRemove r -> {
        yield new CAStockRepositRemove(
          new CAStockInstanceID(r.fieldInstanceId().value())
        );
      }

      case final CAI1StockRepositSetAdd r -> {
        yield new CAStockRepositSetAdd(
          new CAStockInstanceID(r.fieldInstanceId().value()),
          r.fieldCount().value()
        );
      }
    };
  }
}
