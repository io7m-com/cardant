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

import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolUncheckedException;
import com.io7m.cardant.protocol.inventory.CAIResponseTypePackageSearch;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1StockOccurrenceX.STOCK_OCCURRENCE_TYPE;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1TypePackageIdentifierX.TYPE_PACKAGE_IDENTIFIER;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1TypePackageSummaryX.TYPE_PACKAGE_SUMMARY;

public enum CJ1ResponseTypePackageSearchX
  implements CJ1SerialBijectionType<CJ1ResponseTypePackageSearch, CAIResponseTypePackageSearch>
{
  RESPONSE_TYPE_PACKAGE_SEARCH;

  @Override
  public CAIResponseTypePackageSearch toCore(
    final CJ1ResponseTypePackageSearch m)
    throws CAProtocolException
  {
    try {
      return new CAIResponseTypePackageSearch(
        m.requestId(),
        new CJ1PageX<>(
          TYPE_PACKAGE_SUMMARY::toCJ1,
          m1 -> {
            try {
              return TYPE_PACKAGE_SUMMARY.toCore(m1);
            } catch (final CAProtocolException e) {
              throw new CAProtocolUncheckedException(e);
            }
          }
        ).toCore(m.data())
      );
    } catch (final CAProtocolUncheckedException e) {
      throw e.getCause();
    }
  }

  @Override
  public CJ1ResponseTypePackageSearch toCJ1(
    final CAIResponseTypePackageSearch m)
    throws CAProtocolException
  {
    try {
      return new CJ1ResponseTypePackageSearch(
        m.requestId(),
        new CJ1PageX<>(
          TYPE_PACKAGE_SUMMARY::toCJ1,
          m1 -> {
            try {
              return TYPE_PACKAGE_SUMMARY.toCore(m1);
            } catch (final CAProtocolException e) {
              throw new CAProtocolUncheckedException(e);
            }
          }
        ).toCJ1(m.data())
      );
    } catch (final CAProtocolUncheckedException e) {
      throw e.getCause();
    }
  }
}
