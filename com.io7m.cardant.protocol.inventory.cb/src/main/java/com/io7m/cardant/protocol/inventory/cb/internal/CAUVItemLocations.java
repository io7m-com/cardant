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

import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemLocation;
import com.io7m.cardant.model.CAItemLocations;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemLocation;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemLocations;
import com.io7m.cedarbridge.runtime.api.CBMap;
import com.io7m.cedarbridge.runtime.api.CBUUID;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVItemLocation.ITEM_LOCATION;

/**
 * A validator.
 */

public enum CAUVItemLocations
  implements CAProtocolMessageValidatorType<CAItemLocations, CAI1ItemLocations>
{
  /**
   * A validator.
   */

  ITEM_LOCATIONS;

  @Override
  public CAI1ItemLocations convertToWire(
    final CAItemLocations data)
  {
    final var input =
      data.itemLocations();
    final var output =
      new HashMap<CBUUID, CBMap<CBUUID, CAI1ItemLocation>>();

    for (final var e0 : input.entrySet()) {
      final var locationId = e0.getKey();
      final var locationMap =
        input.get(locationId);
      final var outLocations =
        new HashMap<CBUUID, CAI1ItemLocation>();

      for (final var e1 : locationMap.entrySet()) {
        final var itemId = e1.getKey();
        final var itemLocation =
          locationMap.get(itemId);
        final var outLocation =
          ITEM_LOCATION.convertToWire(itemLocation);
        outLocations.put(outLocation.fieldItemId(), outLocation);
      }

      output.put(new CBUUID(locationId.id()), new CBMap<>(outLocations));
    }

    return new CAI1ItemLocations(new CBMap<>(output));
  }

  @Override
  public CAItemLocations convertFromWire(
    final CAI1ItemLocations message)
  {
    final var input =
      message.fieldLocations();
    final var output =
      new TreeMap<CALocationID, SortedMap<CAItemID, CAItemLocation>>();

    for (final var locationId : input.values().keySet()) {
      final var locationMap =
        input.values().get(locationId);
      final var outLocations =
        new TreeMap<CAItemID, CAItemLocation>();

      for (final var itemId : locationMap.values().keySet()) {
        final var itemLocation =
          locationMap.values().get(itemId);
        final var outLocation =
          ITEM_LOCATION.convertFromWire(itemLocation);
        outLocations.put(outLocation.item(), outLocation);
      }

      output.put(
        new CALocationID(locationId.value()),
        outLocations
      );
    }

    return new CAItemLocations(output);
  }
}
