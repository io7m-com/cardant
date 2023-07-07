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

import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CAItemRepositMove;
import com.io7m.cardant.model.CAItemRepositRemove;
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
        Arbitraries.defaultFor(CAItemRepositAdd.class),
        Arbitraries.defaultFor(CAItemRepositMove.class),
        Arbitraries.defaultFor(CAItemRepositRemove.class),
        Arbitraries.integers().between(0, 2)
      ).as((add, move, remove, which) -> {
        return switch (which) {
          case 0 -> add;
          case 1 -> move;
          case 2 -> remove;
          default -> throw new IllegalStateException();
        };
      })
    );
  }
}
