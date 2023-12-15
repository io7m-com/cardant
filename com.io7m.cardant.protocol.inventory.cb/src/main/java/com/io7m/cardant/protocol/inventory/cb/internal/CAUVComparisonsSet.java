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

import com.io7m.cardant.model.CAValidityException;
import com.io7m.cardant.model.comparisons.CAComparisonSetType;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAU1ComparisonSet;
import com.io7m.cedarbridge.runtime.api.CBSerializableType;
import com.io7m.cedarbridge.runtime.convenience.CBLists;
import com.io7m.cedarbridge.runtime.convenience.CBSets;

import java.util.Objects;

/**
 * Set comparisons.
 *
 * @param <V> The type of model values
 * @param <W> The type of serialized values
 */

public final class CAUVComparisonsSet<V, W extends CBSerializableType>
  implements CAProtocolMessageValidatorType<
  CAComparisonSetType<V>,
  CAU1ComparisonSet<W>>
{
  private final CAProtocolMessageValidatorType<V, W> validator;

  /**
   * Set comparisons.
   *
   * @param inValidator A validator for values
   */

  public CAUVComparisonsSet(
    final CAProtocolMessageValidatorType<V, W> inValidator)
  {
    this.validator =
      Objects.requireNonNull(inValidator, "validator");
  }

  @Override
  public CAU1ComparisonSet<W> convertToWire(
    final CAComparisonSetType<V> message)
    throws CAProtocolException
  {
    if (message instanceof CAComparisonSetType.Anything<V>) {
      return new CAU1ComparisonSet.Anything<>();
    }

    if (message instanceof final CAComparisonSetType.IsEqualTo<V> e) {
      return new CAU1ComparisonSet.IsEqualTo<>(
        CBLists.ofCollection(e.value(), this::convertToWireQuiet)
      );
    }

    if (message instanceof final CAComparisonSetType.IsNotEqualTo<V> e) {
      return new CAU1ComparisonSet.IsNotEqualTo<>(
        CBLists.ofCollection(e.value(), this::convertToWireQuiet)
      );
    }

    if (message instanceof final CAComparisonSetType.IsSubsetOf<V> e) {
      return new CAU1ComparisonSet.IsSubsetOf<>(
        CBLists.ofCollection(e.value(), this::convertToWireQuiet)
      );
    }

    if (message instanceof final CAComparisonSetType.IsSupersetOf<V> e) {
      return new CAU1ComparisonSet.IsSupersetOf<>(
        CBLists.ofCollection(e.value(), this::convertToWireQuiet)
      );
    }

    if (message instanceof final CAComparisonSetType.IsOverlapping<V> e) {
      return new CAU1ComparisonSet.IsOverlapping<>(
        CBLists.ofCollection(e.value(), this::convertToWireQuiet)
      );
    }

    throw new IllegalStateException(
      "Unrecognized message: %s".formatted(message)
    );
  }

  private W convertToWireQuiet(
    final V m)
  {
    try {
      return this.validator.convertToWire(m);
    } catch (final CAProtocolException e) {
      throw new CAValidityException(e.getMessage(), e);
    }
  }

  private V convertFromWireQuiet(
    final W m)
  {
    try {
      return this.validator.convertFromWire(m);
    } catch (final CAProtocolException e) {
      throw new CAValidityException(e.getMessage(), e);
    }
  }

  @Override
  public CAComparisonSetType<V> convertFromWire(
    final CAU1ComparisonSet<W> message)
    throws CAProtocolException
  {
    if (message instanceof CAU1ComparisonSet.Anything<W>) {
      return new CAComparisonSetType.Anything<>();
    }

    if (message instanceof final CAU1ComparisonSet.IsEqualTo<W> e) {
      return new CAComparisonSetType.IsEqualTo<>(
        CBSets.toSet(e.fieldValue(), this::convertFromWireQuiet)
      );
    }

    if (message instanceof final CAU1ComparisonSet.IsNotEqualTo<W> e) {
      return new CAComparisonSetType.IsNotEqualTo<>(
        CBSets.toSet(e.fieldValue(), this::convertFromWireQuiet)
      );
    }

    if (message instanceof final CAU1ComparisonSet.IsSubsetOf<W> e) {
      return new CAComparisonSetType.IsSubsetOf<>(
        CBSets.toSet(e.fieldValue(), this::convertFromWireQuiet)
      );
    }

    if (message instanceof final CAU1ComparisonSet.IsSupersetOf<W> e) {
      return new CAComparisonSetType.IsSupersetOf<>(
        CBSets.toSet(e.fieldValue(), this::convertFromWireQuiet)
      );
    }

    if (message instanceof final CAU1ComparisonSet.IsOverlapping<W> e) {
      return new CAComparisonSetType.IsOverlapping<>(
        CBSets.toSet(e.fieldValue(), this::convertFromWireQuiet)
      );
    }

    throw new IllegalStateException(
      "Unrecognized message: %s".formatted(message)
    );
  }
}
