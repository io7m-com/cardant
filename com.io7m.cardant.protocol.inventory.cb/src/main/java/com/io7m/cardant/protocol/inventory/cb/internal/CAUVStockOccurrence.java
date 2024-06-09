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

import com.io7m.cardant.model.CAItemSerial;
import com.io7m.cardant.model.CAStockOccurrenceSerial;
import com.io7m.cardant.model.CAStockOccurrenceSet;
import com.io7m.cardant.model.CAStockOccurrenceType;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1StockOccurrence;
import com.io7m.cedarbridge.runtime.api.CBIntegerUnsigned64;
import com.io7m.cedarbridge.runtime.api.CBString;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVItemSummary.ITEM_SUMMARY;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVLocationSummary.LOCATION_SUMMARY;

/**
 * A validator.
 */

public enum CAUVStockOccurrence
  implements CAProtocolMessageValidatorType<CAStockOccurrenceType, CAI1StockOccurrence>
{
  /**
   * A validator.
   */

  STOCK_OCCURRENCE;

  @Override
  public CAI1StockOccurrence convertToWire(
    final CAStockOccurrenceType message)
  {
    return switch (message) {
      case final CAStockOccurrenceSerial serial -> {
        yield convertToWireSerial(serial);
      }
      case final CAStockOccurrenceSet set -> {
        yield convertToWireSet(set);
      }
    };
  }

  private static CAI1StockOccurrence convertToWireSet(
    final CAStockOccurrenceSet set)
  {
    return new CAI1StockOccurrence.Set(
      LOCATION_SUMMARY.convertToWire(set.location()),
      ITEM_SUMMARY.convertToWire(set.item()),
      new CBIntegerUnsigned64(set.count())
    );
  }

  private static CAI1StockOccurrence convertToWireSerial(
    final CAStockOccurrenceSerial serial)
  {
    return new CAI1StockOccurrence.Serial(
      LOCATION_SUMMARY.convertToWire(serial.location()),
      ITEM_SUMMARY.convertToWire(serial.item()),
      new CBString(serial.serial().value())
    );
  }

  @Override
  public CAStockOccurrenceType convertFromWire(
    final CAI1StockOccurrence message)
  {
    return switch (message) {
      case final CAI1StockOccurrence.Serial serial -> {
        yield convertFromWireSerial(serial);
      }
      case final CAI1StockOccurrence.Set set -> {
        yield convertFromWireSet(set);
      }
    };
  }

  private static CAStockOccurrenceType convertFromWireSet(
    final CAI1StockOccurrence.Set set)
  {
    return new CAStockOccurrenceSet(
      LOCATION_SUMMARY.convertFromWire(set.fieldLocation()),
      ITEM_SUMMARY.convertFromWire(set.fieldItem()),
      set.fieldCount().value()
    );
  }

  private static CAStockOccurrenceType convertFromWireSerial(
    final CAI1StockOccurrence.Serial serial)
  {
    return new CAStockOccurrenceSerial(
      LOCATION_SUMMARY.convertFromWire(serial.fieldLocation()),
      ITEM_SUMMARY.convertFromWire(serial.fieldItem()),
      new CAItemSerial(serial.fieldSerial().value())
    );
  }
}
