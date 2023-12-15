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
import com.io7m.cardant.protocol.inventory.cb.CAU1ComparisonFuzzy;
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
  CAU1ComparisonFuzzy<W>>
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
  public CAU1ComparisonFuzzy<W> convertToWire(
    final CAComparisonFuzzyType<V> message)
    throws CAProtocolException
  {
    if (message instanceof CAComparisonFuzzyType.Anything<V>) {
      return new CAU1ComparisonFuzzy.Anything<>();
    }

    if (message instanceof final CAComparisonFuzzyType.IsEqualTo<V> e) {
      return new CAU1ComparisonFuzzy.IsEqualTo<>(
        this.validator.convertToWire(e.value())
      );
    }

    if (message instanceof final CAComparisonFuzzyType.IsNotEqualTo<V> e) {
      return new CAU1ComparisonFuzzy.IsNotEqualTo<>(
        this.validator.convertToWire(e.value())
      );
    }

    if (message instanceof final CAComparisonFuzzyType.IsSimilarTo<V> e) {
      return new CAU1ComparisonFuzzy.IsSimilarTo<>(
        this.validator.convertToWire(e.value())
      );
    }

    if (message instanceof final CAComparisonFuzzyType.IsNotSimilarTo<V> e) {
      return new CAU1ComparisonFuzzy.IsNotSimilarTo<>(
        this.validator.convertToWire(e.value())
      );
    }

    throw new IllegalStateException(
      "Unrecognized message: %s".formatted(message)
    );
  }

  @Override
  public CAComparisonFuzzyType<V> convertFromWire(
    final CAU1ComparisonFuzzy<W> message)
    throws CAProtocolException
  {
    if (message instanceof CAU1ComparisonFuzzy.Anything<W>) {
      return new CAComparisonFuzzyType.Anything<>();
    }

    if (message instanceof final CAU1ComparisonFuzzy.IsEqualTo<W> e) {
      return new CAComparisonFuzzyType.IsEqualTo<>(
        this.validator.convertFromWire(e.fieldValue())
      );
    }

    if (message instanceof final CAU1ComparisonFuzzy.IsNotEqualTo<W> e) {
      return new CAComparisonFuzzyType.IsNotEqualTo<>(
        this.validator.convertFromWire(e.fieldValue())
      );
    }

    if (message instanceof final CAU1ComparisonFuzzy.IsSimilarTo<W> e) {
      return new CAComparisonFuzzyType.IsSimilarTo<>(
        this.validator.convertFromWire(e.fieldValue())
      );
    }

    if (message instanceof final CAU1ComparisonFuzzy.IsNotSimilarTo<W> e) {
      return new CAComparisonFuzzyType.IsNotSimilarTo<>(
        this.validator.convertFromWire(e.fieldValue())
      );
    }

    throw new IllegalStateException(
      "Unrecognized message: %s".formatted(message)
    );
  }
}
