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

import com.io7m.cardant.model.CAMetadataElementMatchType;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1MetadataElementMatch;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVMetadataNameMatch.METADATA_NAME_MATCH;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVMetadataValueMatch.METADATA_VALUE_MATCH;

/**
 * A validator.
 */

public enum CAUVMetadataElementMatch
  implements CAProtocolMessageValidatorType<CAMetadataElementMatchType, CAI1MetadataElementMatch>
{
  /**
   * A validator.
   */

  METADATA_MATCH;

  @Override
  public CAI1MetadataElementMatch convertToWire(
    final CAMetadataElementMatchType message)
  {
    return switch (message) {
      case final CAMetadataElementMatchType.Specific specific -> {
        yield new CAI1MetadataElementMatch.Specific(
          METADATA_NAME_MATCH.convertToWire(specific.name()),
          METADATA_VALUE_MATCH.convertToWire(specific.value())
        );
      }
      case final CAMetadataElementMatchType.And and -> {
        yield new CAI1MetadataElementMatch.And(
          METADATA_MATCH.convertToWire(and.e0()),
          METADATA_MATCH.convertToWire(and.e1())
        );
      }
      case final CAMetadataElementMatchType.Or or -> {
        yield new CAI1MetadataElementMatch.Or(
          METADATA_MATCH.convertToWire(or.e0()),
          METADATA_MATCH.convertToWire(or.e1())
        );
      }
    };
  }

  @Override
  public CAMetadataElementMatchType convertFromWire(
    final CAI1MetadataElementMatch message)
  {
    return switch (message) {
      case final CAI1MetadataElementMatch.And and -> {
        yield new CAMetadataElementMatchType.And(
          METADATA_MATCH.convertFromWire(and.fieldE0()),
          METADATA_MATCH.convertFromWire(and.fieldE1())
        );
      }
      case final CAI1MetadataElementMatch.Or or -> {
        yield new CAMetadataElementMatchType.Or(
          METADATA_MATCH.convertFromWire(or.fieldE0()),
          METADATA_MATCH.convertFromWire(or.fieldE1())
        );
      }
      case final CAI1MetadataElementMatch.Specific specific -> {
        yield new CAMetadataElementMatchType.Specific(
          METADATA_NAME_MATCH.convertFromWire(specific.fieldName()),
          METADATA_VALUE_MATCH.convertFromWire(specific.fieldValue())
        );
      }
    };
  }
}
