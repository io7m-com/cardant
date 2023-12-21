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
import com.io7m.cardant.model.CALocationSummaries;
import com.io7m.cardant.model.CALocationSummary;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1LocationSummary;
import com.io7m.cedarbridge.runtime.api.CBMap;
import com.io7m.cedarbridge.runtime.api.CBUUID;

import java.util.HashMap;
import java.util.TreeMap;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVLocationSummary.LOCATION_SUMMARY;

/**
 * A validator.
 */

public enum CAUVLocationSummaries
  implements CAProtocolMessageValidatorType<
  CALocationSummaries, CBMap<CBUUID, CAI1LocationSummary>>
{
  /**
   * A validator.
   */

  LOCATION_SUMMARIES;

  @Override
  public CBMap<CBUUID, CAI1LocationSummary> convertToWire(
    final CALocationSummaries message)
  {
    final var r = new HashMap<CBUUID, CAI1LocationSummary>();
    for (final var e : message.locations().entrySet()) {
      r.put(
        new CBUUID(e.getKey().id()),
        LOCATION_SUMMARY.convertToWire(e.getValue())
      );
    }
    return new CBMap<>(r);
  }

  @Override
  public CALocationSummaries convertFromWire(
    final CBMap<CBUUID, CAI1LocationSummary> locations)
  {
    final var results = new TreeMap<CALocationID, CALocationSummary>();
    for (final var entry : locations.values().entrySet()) {
      final var location = LOCATION_SUMMARY.convertFromWire(entry.getValue());
      results.put(location.id(), location);
    }
    return new CALocationSummaries(results);
  }
}
