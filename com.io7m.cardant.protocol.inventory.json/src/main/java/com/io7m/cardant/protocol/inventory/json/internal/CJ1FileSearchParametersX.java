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

import com.io7m.cardant.model.CAFileSearchParameters;
import com.io7m.cardant.protocol.api.CAProtocolException;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1FileColumnOrderingX.FILE_COLUMN_ORDERING;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1SizeRangeX.SIZE_RANGE;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1UnsignedLongX.UNSIGNED_LONG;

public enum CJ1FileSearchParametersX
  implements CJ1SerialBijectionType<CJ1FileSearchParameters, CAFileSearchParameters>
{
  FILE_SEARCH_PARAMETERS;

  @Override
  public CAFileSearchParameters toCore(
    final CJ1FileSearchParameters m)
    throws CAProtocolException
  {
    return new CAFileSearchParameters(
      new CJ1ComparisonFuzzyX<String>().toCore(m.description()),
      new CJ1ComparisonFuzzyX<String>().toCore(m.mediaType()),
      SIZE_RANGE.toCore(m.sizeRange()),
      FILE_COLUMN_ORDERING.toCore(m.ordering()),
      UNSIGNED_LONG.toCore(m.pageSize())
    );
  }

  @Override
  public CJ1FileSearchParameters toCJ1(
    final CAFileSearchParameters m)
    throws CAProtocolException
  {
    return new CJ1FileSearchParameters(
      new CJ1ComparisonFuzzyX<String>().toCJ1(m.description()),
      new CJ1ComparisonFuzzyX<String>().toCJ1(m.mediaType()),
      SIZE_RANGE.toCJ1(m.sizeRange()),
      FILE_COLUMN_ORDERING.toCJ1(m.ordering()),
      UNSIGNED_LONG.toCJ1(m.pageSize())
    );
  }
}
