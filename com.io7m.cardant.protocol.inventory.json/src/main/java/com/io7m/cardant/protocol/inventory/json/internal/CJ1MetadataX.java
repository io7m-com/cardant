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

import com.io7m.cardant.model.CAMetadataType;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1TypeRecordFieldIdentifierX.TYPE_RECORD_FIELD_IDENTIFIER;

public enum CJ1MetadataX
  implements CJ1SerialBijectionType<CJ1MetadataType, CAMetadataType>
{
  METADATA;


  @Override
  public CAMetadataType toCore(
    final CJ1MetadataType m)
  {
    return switch (m) {
      case final CJ1MetadataIntegral integral -> {
        yield new CAMetadataType.Integral(
          TYPE_RECORD_FIELD_IDENTIFIER.toCore(integral.name()), integral.value()
        );
      }
      case final CJ1MetadataMonetary monetary -> {
        yield new CAMetadataType.Monetary(
          TYPE_RECORD_FIELD_IDENTIFIER.toCore(monetary.name()),
          monetary.value(),
          monetary.currency()
        );
      }
      case final CJ1MetadataReal real -> {
        yield new CAMetadataType.Real(
          TYPE_RECORD_FIELD_IDENTIFIER.toCore(real.name()), real.value()
        );
      }
      case final CJ1MetadataText text -> {
        yield new CAMetadataType.Text(
          TYPE_RECORD_FIELD_IDENTIFIER.toCore(text.name()), text.value()
        );
      }
      case final CJ1MetadataTime time -> {
        yield new CAMetadataType.Time(
          TYPE_RECORD_FIELD_IDENTIFIER.toCore(time.name()), time.value()
        );
      }
    };
  }

  @Override
  public CJ1MetadataType toCJ1(
    final CAMetadataType m)
  {
    return switch (m) {
      case final CAMetadataType.Integral integral -> {
        yield new CJ1MetadataIntegral(
          TYPE_RECORD_FIELD_IDENTIFIER.toCJ1(integral.name()),
          integral.value()
        );
      }
      case final CAMetadataType.Monetary monetary -> {
        yield new CJ1MetadataMonetary(
          TYPE_RECORD_FIELD_IDENTIFIER.toCJ1(monetary.name()),
          monetary.value(),
          monetary.currency()
        );
      }
      case final CAMetadataType.Real real -> {
        yield new CJ1MetadataReal(
          TYPE_RECORD_FIELD_IDENTIFIER.toCJ1(real.name()),
          real.value()
        );
      }
      case final CAMetadataType.Text text -> {
        yield new CJ1MetadataText(
          TYPE_RECORD_FIELD_IDENTIFIER.toCJ1(text.name()),
          text.value()
        );
      }
      case final CAMetadataType.Time time -> {
        yield new CJ1MetadataTime(
          TYPE_RECORD_FIELD_IDENTIFIER.toCJ1(time.name()),
          time.value()
        );
      }
    };
  }
}
