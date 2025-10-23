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

import com.io7m.cardant.model.comparisons.CAComparisonExactType;

public final class CJ1ComparisonExactX<T>
  implements CJ1SerialBijectionType<CJ1ComparisonExactType<T>, CAComparisonExactType<T>>
{
  public CJ1ComparisonExactX()
  {

  }

  @Override
  public CAComparisonExactType<T> toCore(
    final CJ1ComparisonExactType<T> m)
  {
    return switch (m) {
      case final CJ1ComparisonExactAnything<T> v -> {
        yield new CAComparisonExactType.Anything<>();
      }
      case final CJ1ComparisonExactIsEqualTo<T> v -> {
        yield new CAComparisonExactType.IsEqualTo<>(v.value());
      }
      case final CJ1ComparisonExactIsNotEqualTo<T> v -> {
        yield new CAComparisonExactType.IsNotEqualTo<>(v.value());
      }
    };
  }

  @Override
  public CJ1ComparisonExactType<T> toCJ1(
    final CAComparisonExactType<T> m)
  {
    return switch (m) {
      case final CAComparisonExactType.Anything<T> v -> {
        yield new CJ1ComparisonExactAnything<>();
      }
      case final CAComparisonExactType.IsEqualTo<T> v -> {
        yield new CJ1ComparisonExactIsEqualTo<>(v.value());
      }
      case final CAComparisonExactType.IsNotEqualTo<T> v -> {
        yield new CJ1ComparisonExactIsNotEqualTo<>(v.value());
      }
    };
  }
}
