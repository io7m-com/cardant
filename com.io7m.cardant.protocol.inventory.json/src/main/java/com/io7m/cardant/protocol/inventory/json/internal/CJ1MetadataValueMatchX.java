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

import com.io7m.cardant.model.CAMetadataValueMatchType;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataValueMatchType.MonetaryMatchType.WithCurrency;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataValueMatchType.TextMatchType.ExactTextValue;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataValueMatchType.TextMatchType.Search;
import com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataValueMatchType.TimeMatchType.WithinRange;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataValueMatchType.AnyValue;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataValueMatchType.IntegralMatchType;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataValueMatchType.MonetaryMatchType;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataValueMatchType.RealMatchType;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataValueMatchType.TextMatchType;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataValueMatchType.TimeMatchType;

public enum CJ1MetadataValueMatchX
  implements CJ1SerialBijectionType<CJ1MetadataValueMatchType, CAMetadataValueMatchType>
{
  METADATA_VALUE_MATCH;

  @Override
  public CAMetadataValueMatchType toCore(
    final CJ1MetadataValueMatchType m)
  {
    return validateMetadataValueMatch(m);
  }

  @Override
  public CJ1MetadataValueMatchType toCJ1(
    final CAMetadataValueMatchType m)
  {
    return serializeMetadataValueMatch(m);
  }

  private static CAMetadataValueMatchType validateMetadataValueMatch(
    final CJ1MetadataValueMatchType m)
  {
    return switch (m) {
      case final AnyValue ignored -> {
        yield CAMetadataValueMatchType.AnyValue.ANY_VALUE;
      }
      case final IntegralMatchType mm -> {
        yield validateMetadataValueMatchIntegral(mm);
      }
      case final MonetaryMatchType mm -> {
        yield validateMetadataValueMatchMonetary(mm);
      }
      case final RealMatchType mm -> {
        yield validateMetadataValueMatchReal(mm);
      }
      case final TextMatchType mm -> {
        yield validateMetadataValueMatchText(mm);
      }
      case final TimeMatchType mm -> {
        yield validateMetadataValueMatchTime(mm);
      }
    };
  }

  private static CAMetadataValueMatchType validateMetadataValueMatchTime(
    final TimeMatchType mm)
  {
    return switch (mm) {
      case final WithinRange withinRange -> {
        yield new CAMetadataValueMatchType.TimeMatchType.WithinRange(
          withinRange.lower(),
          withinRange.upper()
        );
      }
    };
  }

  private static CAMetadataValueMatchType validateMetadataValueMatchText(
    final TextMatchType mm)
  {
    return switch (mm) {
      case final ExactTextValue exactTextValue -> {
        yield new CAMetadataValueMatchType.TextMatchType.ExactTextValue(
          exactTextValue.text()
        );
      }
      case final Search search -> {
        yield new CAMetadataValueMatchType.TextMatchType.Search(search.query());
      }
    };
  }

  private static CAMetadataValueMatchType validateMetadataValueMatchReal(
    final RealMatchType mm)
  {
    return switch (mm) {
      case final RealMatchType.WithinRange withinRange -> {
        yield new CAMetadataValueMatchType.RealMatchType.WithinRange(
          withinRange.lower(),
          withinRange.upper()
        );
      }
    };
  }

  private static CAMetadataValueMatchType validateMetadataValueMatchMonetary(
    final MonetaryMatchType mm)
  {
    return switch (mm) {
      case final WithCurrency withCurrency -> {
        yield new CAMetadataValueMatchType.MonetaryMatchType.WithCurrency(
          withCurrency.currency());
      }
      case final MonetaryMatchType.WithinRange withinRange -> {
        yield new CAMetadataValueMatchType.MonetaryMatchType.WithinRange(
          withinRange.lower(),
          withinRange.upper()
        );
      }
    };
  }

  private static CAMetadataValueMatchType validateMetadataValueMatchIntegral(
    final IntegralMatchType mm)
  {
    return switch (mm) {
      case final IntegralMatchType.WithinRange withinRange -> {
        yield new CAMetadataValueMatchType.IntegralMatchType.WithinRange(
          withinRange.lower(),
          withinRange.upper());
      }
    };
  }

  private static CJ1MetadataValueMatchType serializeMetadataValueMatch(
    final CAMetadataValueMatchType value)
  {
    return switch (value) {
      case final CAMetadataValueMatchType.AnyValue m -> {
        yield serializeMetadataValueMatchAny(m);
      }
      case final CAMetadataValueMatchType.IntegralMatchType m -> {
        yield serializeMetadataValueMatchIntegral(m);
      }
      case final CAMetadataValueMatchType.MonetaryMatchType m -> {
        yield serializeMetadataValueMatchMonetary(m);
      }
      case final CAMetadataValueMatchType.RealMatchType m -> {
        yield serializeMetadataValueMatchReal(m);
      }
      case final CAMetadataValueMatchType.TextMatchType m -> {
        yield serializeMetadataValueMatchText(m);
      }
      case final CAMetadataValueMatchType.TimeMatchType m -> {
        yield serializeMetadataValueMatchTime(m);
      }
    };
  }

  private static CJ1MetadataValueMatchType serializeMetadataValueMatchText(
    final CAMetadataValueMatchType.TextMatchType m)
  {
    return switch (m) {
      case final CAMetadataValueMatchType.TextMatchType.ExactTextValue exactTextValue -> {
        yield new ExactTextValue(
          exactTextValue.text()
        );
      }
      case final CAMetadataValueMatchType.TextMatchType.Search search -> {
        yield new Search(
          search.query()
        );
      }
    };
  }

  private static CJ1MetadataValueMatchType serializeMetadataValueMatchMonetary(
    final CAMetadataValueMatchType.MonetaryMatchType m)
  {
    return switch (m) {
      case final CAMetadataValueMatchType.MonetaryMatchType.WithCurrency withCurrency -> {
        yield new WithCurrency(
          withCurrency.currency()
        );
      }
      case final CAMetadataValueMatchType.MonetaryMatchType.WithinRange withinRange -> {
        yield new MonetaryMatchType.WithinRange(
          withinRange.lower(),
          withinRange.upper()
        );
      }
    };
  }

  private static CJ1MetadataValueMatchType serializeMetadataValueMatchIntegral(
    final CAMetadataValueMatchType.IntegralMatchType m)
  {
    return switch (m) {
      case final CAMetadataValueMatchType.IntegralMatchType.WithinRange withinRange -> {
        yield new IntegralMatchType.WithinRange(
          withinRange.lower(),
          withinRange.upper());
      }
    };
  }

  private static CJ1MetadataValueMatchType serializeMetadataValueMatchReal(
    final CAMetadataValueMatchType.RealMatchType m)
  {
    return switch (m) {
      case final CAMetadataValueMatchType.RealMatchType.WithinRange withinRange -> {
        yield new RealMatchType.WithinRange(
          withinRange.lower(),
          withinRange.upper());
      }
    };
  }

  private static CJ1MetadataValueMatchType serializeMetadataValueMatchTime(
    final CAMetadataValueMatchType.TimeMatchType m)
  {
    return switch (m) {
      case final CAMetadataValueMatchType.TimeMatchType.WithinRange withinRange -> {
        yield new WithinRange(
          withinRange.lower(),
          withinRange.upper());
      }
    };
  }

  private static CJ1MetadataValueMatchType serializeMetadataValueMatchAny(
    final CAMetadataValueMatchType.AnyValue m)
  {
    return switch (m) {
      case ANY_VALUE -> {
        yield AnyValue.ANY_VALUE;
      }
    };
  }
}
