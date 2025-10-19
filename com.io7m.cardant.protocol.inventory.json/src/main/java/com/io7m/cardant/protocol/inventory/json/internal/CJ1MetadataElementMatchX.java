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

import com.io7m.cardant.model.CAMetadataElementMatchType;
import com.io7m.lanark.core.RDottedName;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1MetadataValueMatchX.METADATA_VALUE_MATCH;

public enum CJ1MetadataElementMatchX
  implements CJ1SerialBijectionType<CJ1MetadataElementMatchType, CAMetadataElementMatchType>
{
  METADATA_MATCH;

  @Override
  public CAMetadataElementMatchType toCore(
    final CJ1MetadataElementMatchType m)
  {
    return switch (m) {
      case final CJ1MetadataElementMatchType.And and -> {
        yield new CAMetadataElementMatchType.And(
          METADATA_MATCH.toCore(and.e0()),
          METADATA_MATCH.toCore(and.e1())
        );
      }
      case final CJ1MetadataElementMatchType.Or or -> {
        yield new CAMetadataElementMatchType.Or(
          METADATA_MATCH.toCore(or.e0()),
          METADATA_MATCH.toCore(or.e1())
        );
      }
      case final CJ1MetadataElementMatchType.Specific specific -> {
        yield new CAMetadataElementMatchType.Specific(
          new CJ1ComparisonExactX<RDottedName>().toCore(specific.packageName()),
          new CJ1ComparisonExactX<String>().toCore(specific.typeName()),
          new CJ1ComparisonExactX<String>().toCore(specific.fieldName()),
          METADATA_VALUE_MATCH.toCore(specific.value())
        );
      }
    };
  }

  @Override
  public CJ1MetadataElementMatchType toCJ1(
    final CAMetadataElementMatchType m)
  {
    return switch (m) {
      case final CAMetadataElementMatchType.And and -> {
        yield new CJ1MetadataElementMatchType.And(
          METADATA_MATCH.toCJ1(and.e0()),
          METADATA_MATCH.toCJ1(and.e1())
        );
      }
      case final CAMetadataElementMatchType.Or or -> {
        yield new CJ1MetadataElementMatchType.Or(
          METADATA_MATCH.toCJ1(or.e0()),
          METADATA_MATCH.toCJ1(or.e1())
        );
      }
      case final CAMetadataElementMatchType.Specific specific -> {
        yield new CJ1MetadataElementMatchType.Specific(
          new CJ1ComparisonExactX<RDottedName>().toCJ1(specific.packageName()),
          new CJ1ComparisonExactX<String>().toCJ1(specific.typeName()),
          new CJ1ComparisonExactX<String>().toCJ1(specific.fieldName()),
          METADATA_VALUE_MATCH.toCJ1(specific.value())
        );
      }
    };
  }
}
