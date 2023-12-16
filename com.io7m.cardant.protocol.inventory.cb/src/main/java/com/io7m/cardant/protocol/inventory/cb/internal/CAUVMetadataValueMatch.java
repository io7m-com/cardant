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

import com.io7m.cardant.model.CAMetadataValueMatchType;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1MetadataValueMatch;
import com.io7m.cedarbridge.runtime.time.CBOffsetDateTime;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;

import static com.io7m.cedarbridge.runtime.api.CBCore.float64;
import static com.io7m.cedarbridge.runtime.api.CBCore.signed64;
import static com.io7m.cedarbridge.runtime.api.CBCore.string;

/**
 * A validator.
 */

public enum CAUVMetadataValueMatch
  implements CAProtocolMessageValidatorType<CAMetadataValueMatchType, CAI1MetadataValueMatch>
{
  /**
   * A validator.
   */

  METADATA_VALUE_MATCH;

  @Override
  public CAI1MetadataValueMatch convertToWire(
    final CAMetadataValueMatchType message)
  {
    return switch (message) {
      case final CAMetadataValueMatchType.AnyValue anyValue -> {
        yield new CAI1MetadataValueMatch.Anything();
      }
      case final CAMetadataValueMatchType.IntegralMatchType.WithinRange w -> {
        yield new CAI1MetadataValueMatch.IntegralWithinRange(
          signed64(w.lower()),
          signed64(w.upper())
        );
      }
      case final CAMetadataValueMatchType.RealMatchType.WithinRange w -> {
        yield new CAI1MetadataValueMatch.RealWithinRange(
          float64(w.lower()),
          float64(w.upper())
        );
      }
      case final CAMetadataValueMatchType.TimeMatchType.WithinRange w -> {
        yield new CAI1MetadataValueMatch.TimeWithinRange(
          new CBOffsetDateTime(w.lower()),
          new CBOffsetDateTime(w.upper())
        );
      }
      case final CAMetadataValueMatchType.MonetaryMatchType.WithinRange w -> {
        yield new CAI1MetadataValueMatch.MonetaryWithinRange(
          string(w.lower().toString()),
          string(w.upper().toString())
        );
      }
      case final CAMetadataValueMatchType.MonetaryMatchType.WithCurrency w -> {
        yield new CAI1MetadataValueMatch.MonetaryWithCurrency(
          string(w.currency().getCode())
        );
      }
      case final CAMetadataValueMatchType.TextMatchType.ExactTextValue w -> {
        yield new CAI1MetadataValueMatch.TextExact(string(w.text()));
      }
      case final CAMetadataValueMatchType.TextMatchType.Search w -> {
        yield new CAI1MetadataValueMatch.TextSearch(string(w.query()));
      }
    };
  }

  @Override
  public CAMetadataValueMatchType convertFromWire(
    final CAI1MetadataValueMatch message)
  {
    return switch (message) {
      case final CAI1MetadataValueMatch.Anything anything -> {
        yield CAMetadataValueMatchType.AnyValue.ANY_VALUE;
      }
      case final CAI1MetadataValueMatch.IntegralWithinRange w -> {
        yield new CAMetadataValueMatchType.IntegralMatchType.WithinRange(
          w.fieldLower().value(),
          w.fieldUpper().value()
        );
      }
      case final CAI1MetadataValueMatch.RealWithinRange w -> {
        yield new CAMetadataValueMatchType.RealMatchType.WithinRange(
          w.fieldLower().value(),
          w.fieldUpper().value()
        );
      }
      case final CAI1MetadataValueMatch.TimeWithinRange w -> {
        yield new CAMetadataValueMatchType.TimeMatchType.WithinRange(
          w.fieldLower().value(),
          w.fieldUpper().value()
        );
      }
      case final CAI1MetadataValueMatch.MonetaryWithinRange w -> {
        yield new CAMetadataValueMatchType.MonetaryMatchType.WithinRange(
          new BigDecimal(w.fieldLower().value()),
          new BigDecimal(w.fieldUpper().value())
        );
      }
      case final CAI1MetadataValueMatch.MonetaryWithCurrency w -> {
        yield new CAMetadataValueMatchType.MonetaryMatchType.WithCurrency(
          CurrencyUnit.of(w.fieldCurrency().value())
        );
      }
      case final CAI1MetadataValueMatch.TextExact w -> {
        yield new CAMetadataValueMatchType.TextMatchType.ExactTextValue(w.fieldExact().value());
      }
      case final CAI1MetadataValueMatch.TextSearch w -> {
        yield new CAMetadataValueMatchType.TextMatchType.Search(w.fieldSearch().value());
      }
    };
  }
}
