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

import com.io7m.cardant.model.CAMoney;
import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeScalar;
import com.io7m.cedarbridge.runtime.time.CBOffsetDateTime;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVTypeScalarIdentifier.TYPE_SCALAR_IDENTIFIER;
import static com.io7m.cedarbridge.runtime.api.CBCore.float64;
import static com.io7m.cedarbridge.runtime.api.CBCore.signed64;
import static com.io7m.cedarbridge.runtime.api.CBCore.string;

/**
 * A validator.
 */

public enum CAUVTypeScalar
  implements CAProtocolMessageValidatorType<CATypeScalarType, CAI1TypeScalar>
{
  /**
   * A validator.
   */

  TYPE_SCALAR;

  @Override
  public CAI1TypeScalar convertToWire(
    final CATypeScalarType message)
  {
    return switch (message) {
      case final CATypeScalarType.Integral cc -> {
        yield new CAI1TypeScalar.Integral(
          TYPE_SCALAR_IDENTIFIER.convertToWire(message.name()),
          string(cc.description()),
          signed64(cc.rangeLower()),
          signed64(cc.rangeUpper())
        );
      }
      case final CATypeScalarType.Monetary cc -> {
        yield new CAI1TypeScalar.Monetary(
          TYPE_SCALAR_IDENTIFIER.convertToWire(message.name()),
          string(cc.description()),
          string(cc.rangeLower().toString()),
          string(cc.rangeUpper().toString())
        );
      }
      case final CATypeScalarType.Text cc -> {
        yield new CAI1TypeScalar.Text(
          TYPE_SCALAR_IDENTIFIER.convertToWire(message.name()),
          string(cc.description()),
          string(cc.pattern())
        );
      }
      case final CATypeScalarType.Time cc -> {
        yield new CAI1TypeScalar.Time(
          TYPE_SCALAR_IDENTIFIER.convertToWire(message.name()),
          string(cc.description()),
          new CBOffsetDateTime(cc.rangeLower()),
          new CBOffsetDateTime(cc.rangeUpper())
        );
      }
      case final CATypeScalarType.Real cc -> {
        yield new CAI1TypeScalar.Real(
          TYPE_SCALAR_IDENTIFIER.convertToWire(message.name()),
          string(cc.description()),
          float64(cc.rangeLower()),
          float64(cc.rangeUpper())
        );
      }
    };
  }

  @Override
  public CATypeScalarType convertFromWire(
    final CAI1TypeScalar message)
  {
    return switch (message) {
      case final CAI1TypeScalar.Integral xt -> {
        yield new CATypeScalarType.Integral(
          TYPE_SCALAR_IDENTIFIER.convertFromWire(xt.fieldName()),
          xt.fieldDescription().value(),
          xt.fieldRangeLower().value(),
          xt.fieldRangeUpper().value()
        );
      }
      case final CAI1TypeScalar.Time xt -> {
        yield new CATypeScalarType.Time(
          TYPE_SCALAR_IDENTIFIER.convertFromWire(xt.fieldName()),
          xt.fieldDescription().value(),
          xt.fieldRangeLower().value(),
          xt.fieldRangeUpper().value()
        );
      }
      case final CAI1TypeScalar.Text xt -> {
        yield new CATypeScalarType.Text(
          TYPE_SCALAR_IDENTIFIER.convertFromWire(xt.fieldName()),
          xt.fieldDescription().value(),
          xt.fieldPattern().value()
        );
      }
      case final CAI1TypeScalar.Monetary xt -> {
        yield new CATypeScalarType.Monetary(
          TYPE_SCALAR_IDENTIFIER.convertFromWire(xt.fieldName()),
          xt.fieldDescription().value(),
          CAMoney.money(xt.fieldRangeLower().value()),
          CAMoney.money(xt.fieldRangeUpper().value())
        );
      }
      case final CAI1TypeScalar.Real xt -> {
        yield new CATypeScalarType.Real(
          TYPE_SCALAR_IDENTIFIER.convertFromWire(xt.fieldName()),
          xt.fieldDescription().value(),
          xt.fieldRangeLower().value(),
          xt.fieldRangeUpper().value()
        );
      }
    };
  }
}
