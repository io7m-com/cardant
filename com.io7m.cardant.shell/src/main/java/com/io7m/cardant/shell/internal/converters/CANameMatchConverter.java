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
import com.io7m.cardant.model.CANameMatchFuzzy;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType;
import com.io7m.cardant.parsers.CANameMatchFuzzyExpressions;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.quarrel.core.QException;
import com.io7m.quarrel.core.QValueConverterType;

import java.util.Objects;

/**
 * A value converter for name match expressions.
 */

public final class CANameMatchConverter
  implements QValueConverterType<CANameMatchFuzzy>
{
  private final CAStrings strings;

  /**
   * Construct a converter.
   *
   * @param inStrings The string resources
   */

  public CANameMatchConverter(
    final CAStrings inStrings)
  {
    this.strings = Objects.requireNonNull(inStrings, "strings");
  }

  @Override
  public CANameMatchFuzzy convertFromString(
    final String text)
    throws QException
  {
    try {
      return new CANameMatchFuzzyExpressions(this.strings).nameMatch(text);
    } catch (final CAException e) {
      throw QException.adapt(e, CAErrorCode::id);
    }
  }

  @Override
  public String convertToString(
    final CANameMatchFuzzy value)
    throws QException
  {
    try {
      return new CANameMatchFuzzyExpressions(this.strings)
        .nameMatchSerializeToString(value);
    } catch (final CAException e) {
      throw QException.adapt(e, CAErrorCode::id);
    }
  }

  @Override
  public CANameMatchFuzzy exampleValue()
  {
    return new CANameMatchFuzzy(
      new CAComparisonFuzzyType.IsEqualTo<>("x")
    );
  }

  @Override
  public String syntax()
  {
    return "<name-match>";
  }

  @Override
  public Class<CANameMatchFuzzy> convertedClass()
  {
    return CANameMatchFuzzy.class;
  }
}
