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

import com.io7m.cardant.model.CAItemLocationMatchType;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1ListLocationBehaviour;
import com.io7m.cedarbridge.runtime.api.CBUUID;

/**
 * A validator.
 */

public enum CAUVItemLocationMatch
  implements CAProtocolMessageValidatorType<CAItemLocationMatchType, CAI1ListLocationBehaviour>
{
  /**
   * A validator.
   */

  ITEM_LOCATION_MATCH;

  @Override
  public CAI1ListLocationBehaviour convertToWire(
    final CAItemLocationMatchType message)
  {
    return switch (message) {
      case final CAItemLocationMatchType.CAItemLocationsAll a -> {
        yield new CAI1ListLocationBehaviour.CAI1ListLocationsAll();
      }
      case final CAItemLocationMatchType.CAItemLocationExact e -> {
        yield new CAI1ListLocationBehaviour.CAI1ListLocationExact(
          new CBUUID(e.location().id())
        );
      }
      case final CAItemLocationMatchType.CAItemLocationWithDescendants e -> {
        yield new CAI1ListLocationBehaviour.CAI1ListLocationWithDescendants(
          new CBUUID(e.location().id())
        );
      }
    };
  }

  @Override
  public CAItemLocationMatchType convertFromWire(
    final CAI1ListLocationBehaviour message)
  {
    return switch (message) {
      case final CAI1ListLocationBehaviour.CAI1ListLocationExact x -> {
        yield new CAItemLocationMatchType.CAItemLocationExact(
          new CALocationID(x.fieldLocationId().value())
        );
      }
      case final CAI1ListLocationBehaviour.CAI1ListLocationsAll a -> {
        yield new CAItemLocationMatchType.CAItemLocationsAll();
      }
      case final CAI1ListLocationBehaviour.CAI1ListLocationWithDescendants x -> {
        yield new CAItemLocationMatchType.CAItemLocationWithDescendants(
          new CALocationID(x.fieldLocationId().value())
        );
      }
    };
  }
}
