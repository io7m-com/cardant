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
import com.io7m.cardant.model.CALocationName;
import com.io7m.cardant.model.CALocationPath;
import com.io7m.cardant.model.CALocationSummary;

import java.util.List;

public enum CJ1LocationSummaryX
  implements CJ1SerialBijectionType<CJ1LocationSummary, CALocationSummary>
{
  LOCATION_SUMMARY;

  @Override
  public CALocationSummary toCore(
    final CJ1LocationSummary m)
  {
    return new CALocationSummary(
      CALocationID.of(m.id()),
      m.parent().map(CALocationID::of),
      toCoreLocationPath(m.path()),
      m.timeCreated(),
      m.timeUpdated()
    );
  }

  private static CALocationPath toCoreLocationPath(
    final List<String> path)
  {
    return new CALocationPath(
      path.stream()
        .map(CALocationName::new)
        .toList()
    );
  }

  @Override
  public CJ1LocationSummary toCJ1(
    final CALocationSummary m)
  {
    return new CJ1LocationSummary(
      m.id().id(),
      m.parent().map(CALocationID::id),
      toCJ1LocationPath(m.path()),
      m.timeCreated(),
      m.timeUpdated()
    );
  }

  private static List<String> toCJ1LocationPath(
    final CALocationPath path)
  {
    return path.path()
      .stream()
      .map(CALocationName::value)
      .toList();
  }
}
