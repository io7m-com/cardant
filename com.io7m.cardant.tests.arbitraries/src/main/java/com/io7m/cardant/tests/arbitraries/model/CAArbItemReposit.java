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

import com.io7m.cardant.model.CAItemRepositSerialAdd;
import com.io7m.cardant.model.CAItemRepositSerialMove;
import com.io7m.cardant.model.CAItemRepositSerialRemove;
import com.io7m.cardant.model.CAItemRepositSetAdd;
import com.io7m.cardant.model.CAItemRepositSetMove;
import com.io7m.cardant.model.CAItemRepositSetRemove;
import com.io7m.cardant.model.CAItemRepositType;
import com.io7m.cardant.tests.arbitraries.CAArbAbstract;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Combinators;

public final class CAArbItemReposit extends CAArbAbstract<CAItemRepositType>
{
  public CAArbItemReposit()
  {
    super(
      CAItemRepositType.class,

      () -> Combinators.combine(
        Arbitraries.defaultFor(CAItemRepositSetAdd.class),
        Arbitraries.defaultFor(CAItemRepositSetMove.class),
        Arbitraries.defaultFor(CAItemRepositSetRemove.class),
        Arbitraries.defaultFor(CAItemRepositSerialAdd.class),
        Arbitraries.defaultFor(CAItemRepositSerialMove.class),
        Arbitraries.defaultFor(CAItemRepositSerialRemove.class),
        Arbitraries.integers().between(0, 5)
      ).as((add0, move0, remove0, add1, move1, remove1, which) -> {
        return switch (which) {
          case 0 -> add0;
          case 1 -> move0;
          case 2 -> remove0;
          case 3 -> add1;
          case 4 -> move1;
          case 5 -> remove1;
          default -> throw new IllegalStateException();
        };
      })
    );
  }
}
