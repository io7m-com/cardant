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
import com.io7m.cardant.protocol.inventory.cb.CAU1ComparisonExact;
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
  CAU1ComparisonExact<W>>
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
  public CAU1ComparisonExact<W> convertToWire(
    final CAComparisonExactType<V> message)
    throws CAProtocolException
  {
    if (message instanceof CAComparisonExactType.Anything<V>) {
      return new CAU1ComparisonExact.Anything<>();
    }

    if (message instanceof final CAComparisonExactType.IsEqualTo<V> e) {
      return new CAU1ComparisonExact.IsEqualTo<>(
        this.validator.convertToWire(e.value())
      );
    }

    if (message instanceof final CAComparisonExactType.IsNotEqualTo<V> e) {
      return new CAU1ComparisonExact.IsNotEqualTo<>(
        this.validator.convertToWire(e.value())
      );
    }

    throw new IllegalStateException(
      "Unrecognized message: %s".formatted(message)
    );
  }

  @Override
  public CAComparisonExactType<V> convertFromWire(
    final CAU1ComparisonExact<W> message)
    throws CAProtocolException
  {
    if (message instanceof CAU1ComparisonExact.Anything<W>) {
      return new CAComparisonExactType.Anything<>();
    }

    if (message instanceof final CAU1ComparisonExact.IsEqualTo<W> e) {
      return new CAComparisonExactType.IsEqualTo<>(
        this.validator.convertFromWire(e.fieldValue())
      );
    }

    if (message instanceof final CAU1ComparisonExact.IsNotEqualTo<W> e) {
      return new CAComparisonExactType.IsNotEqualTo<>(
        this.validator.convertFromWire(e.fieldValue())
      );
    }

    throw new IllegalStateException(
      "Unrecognized message: %s".formatted(message)
    );
  }
}
