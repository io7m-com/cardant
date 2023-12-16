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

import com.io7m.cardant.model.CANameMatchType;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1NameMatch;

import static com.io7m.cedarbridge.runtime.api.CBCore.string;

/**
 * A validator.
 */

public enum CAUVItemNameMatch
  implements CAProtocolMessageValidatorType<CANameMatchType, CAI1NameMatch>
{
  /**
   * A validator.
   */

  ITEM_NAME_MATCH;

  @Override
  public CAI1NameMatch convertToWire(
    final CANameMatchType message)
    throws CAProtocolException
  {
    return switch (message) {
      case final CANameMatchType.Any any -> {
        yield new CAI1NameMatch.CAI1NameMatchAny();
      }
      case final CANameMatchType.Exact exact -> {
        yield new CAI1NameMatch.CAI1NameMatchExact(string(exact.text()));
      }
      case final CANameMatchType.Search search -> {
        yield new CAI1NameMatch.CAI1NameMatchSearch(string(search.query()));
      }
    };
  }

  @Override
  public CANameMatchType convertFromWire(
    final CAI1NameMatch message)
    throws CAProtocolException
  {
    return switch (message) {
      case final CAI1NameMatch.CAI1NameMatchAny a -> {
        yield CANameMatchType.Any.ANY_NAME;
      }
      case final CAI1NameMatch.CAI1NameMatchExact exact -> {
        yield new CANameMatchType.Exact(exact.fieldText().value());
      }
      case final CAI1NameMatch.CAI1NameMatchSearch search -> {
        yield new CANameMatchType.Search(search.fieldQuery().value());
      }
    };
  }
}
