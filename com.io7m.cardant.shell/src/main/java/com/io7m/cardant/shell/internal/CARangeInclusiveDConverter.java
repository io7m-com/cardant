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


package com.io7m.cardant.shell.internal;

import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.parsers.CAConstraintExpressions;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.jranges.RangeInclusiveD;
import com.io7m.quarrel.core.QException;
import com.io7m.quarrel.core.QValueConverterType;

import java.util.Objects;

/**
 * A value converter for ranges.
 */

public final class CARangeInclusiveDConverter
  implements QValueConverterType<RangeInclusiveD>
{
  private final CAStrings strings;

  /**
   * Construct a converter.
   *
   * @param inStrings The string resources
   */

  public CARangeInclusiveDConverter(
    final CAStrings inStrings)
  {
    this.strings = Objects.requireNonNull(inStrings, "strings");
  }

  @Override
  public RangeInclusiveD convertFromString(
    final String text)
    throws QException
  {
    try {
      return new CAConstraintExpressions(this.strings).realRange(text);
    } catch (final CAException e) {
      throw QException.adapt(e, CAErrorCode::id);
    }
  }

  @Override
  public String convertToString(
    final RangeInclusiveD value)
  {
    return "[%f %f]".formatted(
      Double.valueOf(value.lower()),
      Double.valueOf(value.upper())
    );
  }

  @Override
  public RangeInclusiveD exampleValue()
  {
    return RangeInclusiveD.of(
      -100.0,
      2000.0
    );
  }

  @Override
  public String syntax()
  {
    return "[ <real> <real> ]";
  }

  @Override
  public Class<RangeInclusiveD> convertedClass()
  {
    return RangeInclusiveD.class;
  }
}
