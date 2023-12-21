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


package com.io7m.cardant.protocol.inventory.cb.internal;

import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1ComparisonFuzzy;
import com.io7m.cedarbridge.runtime.api.CBSerializableType;

import java.util.Objects;

/**
 * Fuzzy comparisons.
 *
 * @param <V> The type of model values
 * @param <W> The type of serialized values
 */

public final class CAUVComparisonsFuzzy<V, W extends CBSerializableType>
  implements CAProtocolMessageValidatorType<
  CAComparisonFuzzyType<V>,
  CAI1ComparisonFuzzy<W>>
{
  private final CAProtocolMessageValidatorType<V, W> validator;

  /**
   * Fuzzy comparisons.
   *
   * @param inValidator A validator for values
   */

  public CAUVComparisonsFuzzy(
    final CAProtocolMessageValidatorType<V, W> inValidator)
  {
    this.validator =
      Objects.requireNonNull(inValidator, "validator");
  }

  @Override
  public CAI1ComparisonFuzzy<W> convertToWire(
    final CAComparisonFuzzyType<V> message)
    throws CAProtocolException
  {
    return switch (message) {
      case final CAComparisonFuzzyType.Anything<V> e -> {
        yield new CAI1ComparisonFuzzy.Anything<>();
      }
      case final CAComparisonFuzzyType.IsEqualTo<V> e -> {
        yield new CAI1ComparisonFuzzy.IsEqualTo<>(
          this.validator.convertToWire(e.value())
        );
      }
      case final CAComparisonFuzzyType.IsNotEqualTo<V> e -> {
        yield new CAI1ComparisonFuzzy.IsNotEqualTo<>(
          this.validator.convertToWire(e.value())
        );
      }
      case final CAComparisonFuzzyType.IsSimilarTo<V> e -> {
        yield new CAI1ComparisonFuzzy.IsSimilarTo<>(
          this.validator.convertToWire(e.value())
        );
      }
      case final CAComparisonFuzzyType.IsNotSimilarTo<V> e -> {
        yield new CAI1ComparisonFuzzy.IsNotSimilarTo<>(
          this.validator.convertToWire(e.value())
        );
      }
    };
  }

  @Override
  public CAComparisonFuzzyType<V> convertFromWire(
    final CAI1ComparisonFuzzy<W> message)
    throws CAProtocolException
  {
    return switch (message) {
      case CAI1ComparisonFuzzy.Anything<W> e -> {
        yield new CAComparisonFuzzyType.Anything<>();
      }
      case final CAI1ComparisonFuzzy.IsEqualTo<W> e -> {
        yield new CAComparisonFuzzyType.IsEqualTo<>(
          this.validator.convertFromWire(e.fieldValue())
        );
      }
      case final CAI1ComparisonFuzzy.IsNotEqualTo<W> e -> {
        yield new CAComparisonFuzzyType.IsNotEqualTo<>(
          this.validator.convertFromWire(e.fieldValue())
        );
      }
      case final CAI1ComparisonFuzzy.IsSimilarTo<W> e -> {
        yield new CAComparisonFuzzyType.IsSimilarTo<>(
          this.validator.convertFromWire(e.fieldValue())
        );
      }
      case final CAI1ComparisonFuzzy.IsNotSimilarTo<W> e -> {
        yield new CAComparisonFuzzyType.IsNotSimilarTo<>(
          this.validator.convertFromWire(e.fieldValue())
        );
      }
    };
  }
}
