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
package com.io7m.cardant.tests.arbitraries;

import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.protocol.inventory.CAITransaction;
import com.io7m.cardant.protocol.inventory.CAITransactionResponse;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Combinators;

import java.util.List;
import java.util.UUID;

public final class CAArbTransaction extends CAArbAbstract<CAITransaction>
{
  public CAArbTransaction()
  {
    super(
      CAITransaction.class,
      () ->
        Arbitraries.defaultFor(CAICommandType.class)
          .list()
          .ofMaxSize(10)
          .ofMinSize(0)
          .map(CAArbTransaction::create)
    );
  }

  private static CAITransaction create(
    final List<CAICommandType> c)
  {
    return new CAITransaction((List<CAICommandType<?>>) (Object) c);
  }
}
