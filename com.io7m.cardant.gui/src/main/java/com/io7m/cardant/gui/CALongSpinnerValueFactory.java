/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.gui;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.SpinnerValueFactory;
import javafx.util.StringConverter;

import java.math.BigInteger;
import java.util.Objects;

public final class CALongSpinnerValueFactory extends SpinnerValueFactory<Long>
{
  private static final BigInteger BIG_LONG_MAX =
    new BigInteger("18446744073709551615");
  private static final long LONG_MAX =
    0xffffffffffffffffL;
  private final SimpleBooleanProperty invalid;

  public CALongSpinnerValueFactory()
  {
    this.converterProperty()
      .set(new UnsignedLongStringConverter(this));
    this.valueProperty()
      .set(Long.valueOf(0L));
    this.invalid =
      new SimpleBooleanProperty(false);
  }

  @Override
  public void decrement(
    final int steps)
  {
    try {
      final var bigCurrent =
        BigInteger.valueOf(this.getValue().longValue());
      final var bigNew =
        bigCurrent.subtract(BigInteger.valueOf(steps));

      if (bigNew.compareTo(BigInteger.ZERO) < 0) {
        this.setValue(Long.valueOf(0L));
        return;
      }

      this.setValue(Long.valueOf(bigNew.longValueExact()));
    } catch (final ArithmeticException e) {
      this.setValue(Long.valueOf(0L));
    }
  }

  @Override
  public void increment(final int steps)
  {
    try {
      final var bigCurrent =
        BigInteger.valueOf(this.getValue().longValue());
      final var bigNew =
        bigCurrent.add(BigInteger.valueOf(steps));

      if (bigNew.compareTo(BIG_LONG_MAX) > 0) {
        this.setValue(Long.valueOf(LONG_MAX));
        return;
      }

      this.setValue(Long.valueOf(bigNew.longValueExact()));
    } catch (final ArithmeticException e) {
      this.setValue(Long.valueOf(LONG_MAX));
    }
  }

  public ObservableValue<Boolean> invalidProperty()
  {
    return this.invalid;
  }

  private static final class UnsignedLongStringConverter
    extends StringConverter<Long>
  {
    private final CALongSpinnerValueFactory factory;

    UnsignedLongStringConverter(
      final CALongSpinnerValueFactory inFactory)
    {
      this.factory = Objects.requireNonNull(inFactory, "factory");
    }

    @Override
    public String toString(
      final Long object)
    {
      return Long.toUnsignedString(object.longValue());
    }

    @Override
    public Long fromString(
      final String string)
    {
      try {
        final var result = Long.valueOf(Long.parseUnsignedLong(string));
        this.factory.invalid.setValue(Boolean.FALSE);
        return result;
      } catch (final NumberFormatException e) {
        this.factory.invalid.setValue(Boolean.TRUE);
        return Long.valueOf(0L);
      }
    }
  }
}
