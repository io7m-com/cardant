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

import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationMatchType;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1LocationMatch;
import com.io7m.cedarbridge.runtime.api.CBUUID;

/**
 * A validator.
 */

public enum CAUVLocationMatch
  implements CAProtocolMessageValidatorType<CALocationMatchType, CAI1LocationMatch>
{
  /**
   * A validator.
   */

  LOCATION_MATCH;

  @Override
  public CAI1LocationMatch convertToWire(
    final CALocationMatchType message)
  {
    return switch (message) {
      case final CALocationMatchType.CALocationsAll a -> {
        yield new CAI1LocationMatch.CAI1LocationAny();
      }
      case final CALocationMatchType.CALocationExact e -> {
        yield new CAI1LocationMatch.CAI1LocationExact(
          new CBUUID(e.location().id())
        );
      }
      case final CALocationMatchType.CALocationWithDescendants e -> {
        yield new CAI1LocationMatch.CAI1LocationWithDescendants(
          new CBUUID(e.location().id())
        );
      }
    };
  }

  @Override
  public CALocationMatchType convertFromWire(
    final CAI1LocationMatch message)
  {
    return switch (message) {
      case final CAI1LocationMatch.CAI1LocationExact x -> {
        yield new CALocationMatchType.CALocationExact(
          new CALocationID(x.fieldLocationId().value())
        );
      }
      case final CAI1LocationMatch.CAI1LocationAny a -> {
        yield new CALocationMatchType.CALocationsAll();
      }
      case final CAI1LocationMatch.CAI1LocationWithDescendants x -> {
        yield new CALocationMatchType.CALocationWithDescendants(
          new CALocationID(x.fieldLocationId().value())
        );
      }
    };
  }
}
