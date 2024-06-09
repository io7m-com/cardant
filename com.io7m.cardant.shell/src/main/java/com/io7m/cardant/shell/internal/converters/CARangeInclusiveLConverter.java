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


package com.io7m.cardant.shell.internal.converters;

import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.parsers.CAMetadataConstraintExpressions;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.jranges.RangeInclusiveL;
import com.io7m.quarrel.core.QException;
import com.io7m.quarrel.core.QValueConverterType;

import java.util.Objects;

/**
 * A value converter for ranges.
 */

public final class CARangeInclusiveLConverter
  implements QValueConverterType<RangeInclusiveL>
{
  private final CAStrings strings;

  /**
   * Construct a converter.
   *
   * @param inStrings The string resources
   */

  public CARangeInclusiveLConverter(
    final CAStrings inStrings)
  {
    this.strings = Objects.requireNonNull(inStrings, "strings");
  }

  @Override
  public RangeInclusiveL convertFromString(
    final String text)
    throws QException
  {
    try {
      return new CAMetadataConstraintExpressions(this.strings).integerRange(text);
    } catch (final CAException e) {
      throw QException.adapt(e, CAErrorCode::id);
    }
  }

  @Override
  public String convertToString(
    final RangeInclusiveL value)
  {
    return "[%d %d]".formatted(
      Long.valueOf(value.lower()),
      Long.valueOf(value.upper())
    );
  }

  @Override
  public RangeInclusiveL exampleValue()
  {
    return RangeInclusiveL.of(
      (long) Integer.MIN_VALUE,
      (long) Integer.MAX_VALUE
    );
  }

  @Override
  public String syntax()
  {
    return "[ <integer> <integer> ]";
  }

  @Override
  public Class<RangeInclusiveL> convertedClass()
  {
    return RangeInclusiveL.class;
  }
}
