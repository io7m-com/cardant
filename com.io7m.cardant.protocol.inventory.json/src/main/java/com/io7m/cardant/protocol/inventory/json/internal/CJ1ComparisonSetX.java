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

import com.io7m.cardant.model.comparisons.CAComparisonSetType;

import java.util.Set;
import java.util.function.Function;

public final class CJ1ComparisonSetX<W, C>
  implements CJ1SerialBijectionType<CJ1ComparisonSetType<W>, CAComparisonSetType<C>>
{
  private final Function<Set<C>, Set<W>> coreToWire;
  private final Function<Set<W>, Set<C>> wireToCore;

  public CJ1ComparisonSetX(
    final Function<Set<C>, Set<W>> inCoreToWire,
    final Function<Set<W>, Set<C>> inWireToCore)
  {
    this.coreToWire = inCoreToWire;
    this.wireToCore = inWireToCore;
  }

  @Override
  public CAComparisonSetType<C> toCore(
    final CJ1ComparisonSetType<W> m)
  {
    return switch (m) {
      case final CJ1ComparisonSetAnything<W> v -> {
        yield new CAComparisonSetType.Anything<>();
      }
      case final CJ1ComparisonSetIsEqualTo<W> v -> {
        yield new CAComparisonSetType.IsEqualTo<>(
          this.wireToCore.apply(v.value()));
      }
      case final CJ1ComparisonSetIsNotEqualTo<W> v -> {
        yield new CAComparisonSetType.IsNotEqualTo<>(
          this.wireToCore.apply(v.value()));
      }
      case final CJ1ComparisonSetIsOverlapping<W> v -> {
        yield new CAComparisonSetType.IsOverlapping<>(
          this.wireToCore.apply(v.value()));
      }
      case final CJ1ComparisonSetIsSubsetOf<W> v -> {
        yield new CAComparisonSetType.IsSubsetOf<>(
          this.wireToCore.apply(v.value()));
      }
      case final CJ1ComparisonSetIsSupersetOf<W> v -> {
        yield new CAComparisonSetType.IsSupersetOf<>(
          this.wireToCore.apply(v.value()));
      }
    };
  }

  @Override
  public CJ1ComparisonSetType<W> toCJ1(
    final CAComparisonSetType<C> m)
  {
    return switch (m) {
      case final CAComparisonSetType.Anything<C> v -> {
        yield new CJ1ComparisonSetAnything<>();
      }
      case final CAComparisonSetType.IsEqualTo<C> v -> {
        yield new CJ1ComparisonSetIsEqualTo<>(
          this.coreToWire.apply(v.value()));
      }
      case final CAComparisonSetType.IsNotEqualTo<C> v -> {
        yield new CJ1ComparisonSetIsNotEqualTo<>(
          this.coreToWire.apply(v.value()));
      }
      case final CAComparisonSetType.IsOverlapping<C> v -> {
        yield new CJ1ComparisonSetIsOverlapping<>(
          this.coreToWire.apply(v.value()));
      }
      case final CAComparisonSetType.IsSubsetOf<C> v -> {
        yield new CJ1ComparisonSetIsSubsetOf<>(
          this.coreToWire.apply(v.value()));
      }
      case final CAComparisonSetType.IsSupersetOf<C> v -> {
        yield new CJ1ComparisonSetIsSupersetOf<>(
          this.coreToWire.apply(v.value()));
      }
    };
  }
}
