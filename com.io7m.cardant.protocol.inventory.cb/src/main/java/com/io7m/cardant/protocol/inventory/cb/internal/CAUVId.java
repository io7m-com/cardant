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

import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAIdType;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAStockInstanceID;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1Id;
import com.io7m.cedarbridge.runtime.api.CBUUID;

/**
 * A validator.
 */

public enum CAUVId
  implements CAProtocolMessageValidatorType<CAIdType, CAI1Id>
{
  /**
   * A validator.
   */

  ID;

  @Override
  public CAI1Id convertToWire(
    final CAIdType message)
  {
    return switch (message) {
      case final CAFileID ii -> {
        yield new CAI1Id.CAI1FileID(new CBUUID(ii.id()));
      }
      case final CAItemID ii -> {
        yield new CAI1Id.CAI1ItemID(new CBUUID(ii.id()));
      }
      case final CALocationID ii -> {
        yield new CAI1Id.CAI1LocationID(new CBUUID(ii.id()));
      }
      case final CAUserID ii -> {
        yield new CAI1Id.CAI1UserID(new CBUUID(ii.id()));
      }
      case final CAStockInstanceID ii -> {
        yield new CAI1Id.CAI1StockInstanceID(new CBUUID(ii.id()));
      }
    };
  }

  @Override
  public CAIdType convertFromWire(
    final CAI1Id message)
  {
    return switch (message) {
      case final CAI1Id.CAI1LocationID ii -> {
        yield new CALocationID(ii.fieldId().value());
      }
      case final CAI1Id.CAI1ItemID ii -> {
        yield new CAItemID(ii.fieldId().value());
      }
      case final CAI1Id.CAI1FileID ii -> {
        yield new CAFileID(ii.fieldId().value());
      }
      case final CAI1Id.CAI1UserID ii -> {
        yield new CAUserID(ii.fieldId().value());
      }
      case final CAI1Id.CAI1StockInstanceID ii -> {
        yield new CAStockInstanceID(ii.fieldId().value());
      }
    };
  }
}
