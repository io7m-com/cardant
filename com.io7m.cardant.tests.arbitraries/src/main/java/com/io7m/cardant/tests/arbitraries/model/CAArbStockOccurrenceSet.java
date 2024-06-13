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


package com.io7m.cardant.tests.arbitraries.model;

import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CALocationSummary;
import com.io7m.cardant.model.CAStockOccurrenceSet;
import com.io7m.cardant.tests.arbitraries.CAArbAbstract;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Combinators;

public final class CAArbStockOccurrenceSet
  extends CAArbAbstract<CAStockOccurrenceSet>
{
  public CAArbStockOccurrenceSet()
  {
    super(
      CAStockOccurrenceSet.class,
      () -> Combinators.combine(
        Arbitraries.defaultFor(CALocationSummary.class),
        Arbitraries.defaultFor(CAItemSummary.class),
        Arbitraries.longs().greaterOrEqual(1L)
      ).as(CAStockOccurrenceSet::new)
    );
  }
}