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
import com.io7m.cardant.model.CAItemSerial;
import com.io7m.cardant.model.CAItemSerialMatch;
import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.cardant.parsers.CAItemSerialMatchExpressions;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.lanark.core.RDottedName;
import com.io7m.quarrel.core.QException;
import com.io7m.quarrel.core.QValueConverterType;

import java.util.Objects;

/**
 * A value converter for serial number match expressions.
 */

public final class CAItemSerialMatchConverter
  implements QValueConverterType<CAItemSerialMatch>
{
  private final CAStrings strings;

  /**
   * Construct a converter.
   *
   * @param inStrings The string resources
   */

  public CAItemSerialMatchConverter(
    final CAStrings inStrings)
  {
    this.strings = Objects.requireNonNull(inStrings, "strings");
  }

  @Override
  public CAItemSerialMatch convertFromString(
    final String text)
    throws QException
  {
    try {
      return new CAItemSerialMatchExpressions(this.strings).serialMatch(text);
    } catch (final CAException e) {
      throw QException.adapt(e, CAErrorCode::id);
    }
  }

  @Override
  public String convertToString(
    final CAItemSerialMatch value)
    throws QException
  {
    try {
      return new CAItemSerialMatchExpressions(this.strings)
        .serialMatchSerializeToString(value);
    } catch (final CAException e) {
      throw QException.adapt(e, CAErrorCode::id);
    }
  }

  @Override
  public CAItemSerialMatch exampleValue()
  {
    return new CAItemSerialMatch(
      new CAComparisonExactType.IsEqualTo<>(
        new CAItemSerial(
          new RDottedName("com.io7m.example"),
          "Z512345"
        )
      )
    );
  }

  @Override
  public String syntax()
  {
    return "<serial-match>";
  }

  @Override
  public Class<CAItemSerialMatch> convertedClass()
  {
    return CAItemSerialMatch.class;
  }
}
