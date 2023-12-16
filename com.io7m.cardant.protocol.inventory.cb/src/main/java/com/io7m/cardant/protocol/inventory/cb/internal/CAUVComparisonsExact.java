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

import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1ComparisonExact;
import com.io7m.cedarbridge.runtime.api.CBSerializableType;

import java.util.Objects;

/**
 * Exact comparisons.
 *
 * @param <V> The type of model values
 * @param <W> The type of serialized values
 */

public final class CAUVComparisonsExact<V, W extends CBSerializableType>
  implements CAProtocolMessageValidatorType<
  CAComparisonExactType<V>,
  CAI1ComparisonExact<W>>
{
  private final CAProtocolMessageValidatorType<V, W> validator;

  /**
   * Exact comparisons.
   *
   * @param inValidator A validator for values
   */

  public CAUVComparisonsExact(
    final CAProtocolMessageValidatorType<V, W> inValidator)
  {
    this.validator =
      Objects.requireNonNull(inValidator, "validator");
  }

  @Override
  public CAI1ComparisonExact<W> convertToWire(
    final CAComparisonExactType<V> message)
    throws CAProtocolException
  {
    return switch (message) {
      case final CAComparisonExactType.Anything<V> e -> {
        yield new CAI1ComparisonExact.Anything<>();
      }
      case final CAComparisonExactType.IsEqualTo<V> e -> {
        yield new CAI1ComparisonExact.IsEqualTo<>(
          this.validator.convertToWire(e.value())
        );
      }
      case final CAComparisonExactType.IsNotEqualTo<V> e -> {
        yield new CAI1ComparisonExact.IsNotEqualTo<>(
          this.validator.convertToWire(e.value())
        );
      }
    };
  }

  @Override
  public CAComparisonExactType<V> convertFromWire(
    final CAI1ComparisonExact<W> message)
    throws CAProtocolException
  {
    return switch (message) {
      case final CAI1ComparisonExact.Anything<W> e -> {
        yield new CAComparisonExactType.Anything<>();
      }
      case final CAI1ComparisonExact.IsEqualTo<W> e -> {
        yield new CAComparisonExactType.IsEqualTo<>(
          this.validator.convertFromWire(e.fieldValue())
        );
      }
      case final CAI1ComparisonExact.IsNotEqualTo<W> e -> {
        yield new CAComparisonExactType.IsNotEqualTo<>(
          this.validator.convertFromWire(e.fieldValue())
        );
      }
    };
  }
}
