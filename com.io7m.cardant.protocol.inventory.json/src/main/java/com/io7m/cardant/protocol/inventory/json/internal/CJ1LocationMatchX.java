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
import com.io7m.cardant.model.CALocationMatchType;

public enum CJ1LocationMatchX
  implements CJ1SerialBijectionType<CJ1LocationMatchType, CALocationMatchType>
{
  LOCATION_MATCH;

  @Override
  public CALocationMatchType toCore(
    final CJ1LocationMatchType m)
  {
    return switch (m) {
      case final CJ1LocationMatchType.Exact mm -> {
        yield new CALocationMatchType.CALocationExact(
          CALocationID.of(mm.location())
        );
      }
      case final CJ1LocationMatchType.WithDescendants mm -> {
        yield new CALocationMatchType.CALocationWithDescendants(
          CALocationID.of(mm.location())
        );
      }
      case final CJ1LocationMatchType.All mm -> {
        yield new CALocationMatchType.CALocationsAll();
      }
    };
  }

  @Override
  public CJ1LocationMatchType toCJ1(
    final CALocationMatchType m)
  {
    return switch (m) {
      case final CALocationMatchType.CALocationExact mm -> {
        yield new CJ1LocationMatchType.Exact(mm.location().id());
      }
      case final CALocationMatchType.CALocationWithDescendants mm -> {
        yield new CJ1LocationMatchType.WithDescendants(mm.location().id());
      }
      case final CALocationMatchType.CALocationsAll mm -> {
        yield new CJ1LocationMatchType.All();
      }
    };
  }
}
