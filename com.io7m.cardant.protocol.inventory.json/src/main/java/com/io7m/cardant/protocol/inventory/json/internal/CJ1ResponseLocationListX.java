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

import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationSummaries;
import com.io7m.cardant.model.CALocationSummary;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationList;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1LocationSummaryX.LOCATION_SUMMARY;

public enum CJ1ResponseLocationListX
  implements CJ1SerialBijectionType<CJ1ResponseLocationList, CAIResponseLocationList>
{
  RESPONSE_LOCATION_LIST;

  @Override
  public CAIResponseLocationList toCore(
    final CJ1ResponseLocationList m)
    throws CAProtocolException
  {
    return new CAIResponseLocationList(
      m.requestId(),
      toCoreLocationSummaries(m.locations())
    );
  }

  private static CALocationSummaries toCoreLocationSummaries(
    final Map<UUID, CJ1LocationSummary> locations)
  {
    final var r = new TreeMap<CALocationID, CALocationSummary>();
    for (final var e : locations.entrySet()) {
      r.put(
        new CALocationID(e.getKey()),
        LOCATION_SUMMARY.toCore(e.getValue())
      );
    }
    return new CALocationSummaries(r);
  }

  @Override
  public CJ1ResponseLocationList toCJ1(
    final CAIResponseLocationList m)
    throws CAProtocolException
  {
    return new CJ1ResponseLocationList(
      m.requestId(),
      toCJ1LocationSummaries(m.data())
    );
  }

  private static Map<UUID, CJ1LocationSummary> toCJ1LocationSummaries(
    final CALocationSummaries data)
  {
    final var r =
      new HashMap<UUID, CJ1LocationSummary>(data.locations().size());
    for (final var e : data.locations().entrySet()) {
      r.put(
        e.getKey().id(),
        LOCATION_SUMMARY.toCJ1(e.getValue())
      );
    }
    return r;
  }
}
