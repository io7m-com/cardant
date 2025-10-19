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

import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType;

public final class CJ1ComparisonFuzzyX<T>
  implements CJ1SerialBijectionType<CJ1ComparisonFuzzyType<T>, CAComparisonFuzzyType<T>>
{
  public CJ1ComparisonFuzzyX()
  {

  }

  @Override
  public CAComparisonFuzzyType<T> toCore(
    final CJ1ComparisonFuzzyType<T> m)
  {
    return switch (m) {
      case final CJ1ComparisonFuzzyType.Anything<T> v -> {
        yield new CAComparisonFuzzyType.Anything<>();
      }
      case final CJ1ComparisonFuzzyType.IsEqualTo<T> v -> {
        yield new CAComparisonFuzzyType.IsEqualTo<>(v.value());
      }
      case final CJ1ComparisonFuzzyType.IsNotEqualTo<T> v -> {
        yield new CAComparisonFuzzyType.IsNotEqualTo<>(v.value());
      }
      case final CJ1ComparisonFuzzyType.IsNotSimilarTo<T> v -> {
        yield new CAComparisonFuzzyType.IsNotSimilarTo<>(v.value());
      }
      case final CJ1ComparisonFuzzyType.IsSimilarTo<T> v -> {
        yield new CAComparisonFuzzyType.IsSimilarTo<>(v.value());
      }
    };
  }

  @Override
  public CJ1ComparisonFuzzyType<T> toCJ1(
    final CAComparisonFuzzyType<T> m)
  {
    return switch (m) {
      case final CAComparisonFuzzyType.Anything<T> v -> {
        yield new CJ1ComparisonFuzzyType.Anything<>();
      }
      case final CAComparisonFuzzyType.IsEqualTo<T> v -> {
        yield new CJ1ComparisonFuzzyType.IsEqualTo<>(v.value());
      }
      case final CAComparisonFuzzyType.IsNotEqualTo<T> v -> {
        yield new CJ1ComparisonFuzzyType.IsNotEqualTo<>(v.value());
      }
      case final CAComparisonFuzzyType.IsNotSimilarTo<T> v -> {
        yield new CJ1ComparisonFuzzyType.IsNotSimilarTo<>(v.value());
      }
      case final CAComparisonFuzzyType.IsSimilarTo<T> v -> {
        yield new CJ1ComparisonFuzzyType.IsSimilarTo<>(v.value());
      }
    };
  }
}
