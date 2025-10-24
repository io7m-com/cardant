/*
 * Copyright © 2025 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.protocol.inventory.json.internal;

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
import com.io7m.cardant.protocol.api.CAProtocolException;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1ItemSerialX.ITEM_SERIAL;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1UnsignedLongX.UNSIGNED_LONG;

public enum CJ1StockRepositTypeX
  implements CJ1SerialBijectionType<CJ1StockRepositType, CAStockRepositType>
{
  STOCK_REPOSIT_TYPE;

  @Override
  public CAStockRepositType toCore(
    final CJ1StockRepositType m)
    throws CAProtocolException
  {
    return switch (m) {
      case final CJ1StockRepositRemove mm -> {
        yield new CAStockRepositRemove(
          new CAStockInstanceID(mm.instance())
        );
      }
      case final CJ1StockRepositSerialIntroduce mm -> {
        yield new CAStockRepositSerialIntroduce(
          new CAStockInstanceID(mm.instance()),
          new CAItemID(mm.item()),
          new CALocationID(mm.location()),
          ITEM_SERIAL.toCore(mm.serial())
        );
      }
      case final CJ1StockRepositSerialMove mm -> {
        yield new CAStockRepositSerialMove(
          new CAStockInstanceID(mm.instance()),
          new CALocationID(mm.toLocation())
        );
      }
      case final CJ1StockRepositSerialNumberAdd mm -> {
        yield new CAStockRepositSerialNumberAdd(
          new CAStockInstanceID(mm.instance()),
          ITEM_SERIAL.toCore(mm.serial())
        );
      }
      case final CJ1StockRepositSerialNumberRemove mm -> {
        yield new CAStockRepositSerialNumberRemove(
          new CAStockInstanceID(mm.instance()),
          ITEM_SERIAL.toCore(mm.serial())
        );
      }
      case final CJ1StockRepositSetAdd mm -> {
        yield new CAStockRepositSetAdd(
          new CAStockInstanceID(mm.instance()),
          UNSIGNED_LONG.toCore(mm.count())
        );
      }
      case final CJ1StockRepositSetIntroduce mm -> {
        yield new CAStockRepositSetIntroduce(
          new CAStockInstanceID(mm.instance()),
          new CAItemID(mm.item()),
          new CALocationID(mm.location()),
          UNSIGNED_LONG.toCore(mm.count())
        );
      }
      case final CJ1StockRepositSetMove mm -> {
        yield new CAStockRepositSetMove(
          new CAStockInstanceID(mm.instanceSource()),
          new CAStockInstanceID(mm.instanceTarget()),
          new CALocationID(mm.toLocation()),
          UNSIGNED_LONG.toCore(mm.count())
        );
      }
      case final CJ1StockRepositSetRemove mm -> {
        yield new CAStockRepositSetRemove(
          new CAStockInstanceID(mm.instance()),
          UNSIGNED_LONG.toCore(mm.count())
        );
      }
    };
  }

  @Override
  public CJ1StockRepositType toCJ1(
    final CAStockRepositType m)
    throws CAProtocolException
  {
    return switch (m) {
      case final CAStockRepositRemove mm -> {
        yield new CJ1StockRepositRemove(
          m.instance().id()
        );
      }
      case final CAStockRepositSerialIntroduce mm -> {
        yield new CJ1StockRepositSerialIntroduce(
          mm.instance().id(),
          mm.item().id(),
          mm.location().id(),
          ITEM_SERIAL.toCJ1(mm.serial())
        );
      }
      case final CAStockRepositSerialMove mm -> {
        yield new CJ1StockRepositSerialMove(
          mm.instance().id(),
          mm.toLocation().id()
        );
      }
      case final CAStockRepositSerialNumberAdd mm -> {
        yield new CJ1StockRepositSerialNumberAdd(
          mm.instance().id(),
          ITEM_SERIAL.toCJ1(mm.serial())
        );
      }
      case final CAStockRepositSerialNumberRemove mm -> {
        yield new CJ1StockRepositSerialNumberRemove(
          mm.instance().id(),
          ITEM_SERIAL.toCJ1(mm.serial())
        );
      }
      case final CAStockRepositSetAdd mm -> {
        yield new CJ1StockRepositSetAdd(
          mm.instance().id(),
          UNSIGNED_LONG.toCJ1(mm.count())
        );
      }
      case final CAStockRepositSetIntroduce mm -> {
        yield new CJ1StockRepositSetIntroduce(
          mm.instance().id(),
          mm.item().id(),
          mm.location().id(),
          UNSIGNED_LONG.toCJ1(mm.count())
        );
      }
      case final CAStockRepositSetMove mm -> {
        yield new CJ1StockRepositSetMove(
          mm.instanceSource().id(),
          mm.instanceTarget().id(),
          mm.toLocation().id(),
          UNSIGNED_LONG.toCJ1(mm.count())
        );
      }
      case final CAStockRepositSetRemove mm -> {
        yield new CJ1StockRepositSetRemove(
          mm.instance().id(),
          UNSIGNED_LONG.toCJ1(mm.count())
        );
      }
    };
  }
}
