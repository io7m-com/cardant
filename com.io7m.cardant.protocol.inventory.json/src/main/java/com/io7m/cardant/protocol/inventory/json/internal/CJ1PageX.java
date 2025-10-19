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

import com.io7m.cardant.model.CAPage;
import com.io7m.cardant.protocol.api.CAProtocolException;

import java.util.function.Function;

public final class CJ1PageX<W, C>
  implements CJ1SerialBijectionType<CJ1Page<W>, CAPage<C>>
{
  private final Function<C, W> coreToWire;
  private final Function<W, C> wireToCore;

  @FunctionalInterface
  public interface PartialFunction<A, B> {
    B apply(A x) throws CAProtocolException;
  }

  public CJ1PageX(
    final Function<C, W> inCoreToWire,
    final Function<W, C> inWireToCore)
  {
    this.coreToWire = inCoreToWire;
    this.wireToCore = inWireToCore;
  }

  @Override
  public CAPage<C> toCore(
    final CJ1Page<W> m)
    throws CAProtocolException
  {
    return new CAPage<>(
      m.items()
        .stream()
        .map(this.wireToCore)
        .toList(),
      m.pageIndex(),
      m.pageCount(),
      m.pageFirstOffset()
    );
  }

  @Override
  public CJ1Page<W> toCJ1(
    final CAPage<C> m)
    throws CAProtocolException
  {
    return new CJ1Page<>(
      m.items()
        .stream()
        .map(this.coreToWire)
        .toList(),
      m.pageIndex(),
      m.pageCount(),
      m.pageFirstOffset()
    );
  }
}
