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
import com.io7m.cardant.model.CATypeMatch;
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.cardant.model.comparisons.CAComparisonSetType;
import com.io7m.cardant.parsers.CATypeMatchExpressions;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.lanark.core.RDottedName;
import com.io7m.quarrel.core.QException;
import com.io7m.quarrel.core.QValueConverterType;

import java.util.Objects;
import java.util.Set;

/**
 * A value converter for type match expressions.
 */

public final class CATypeMatchConverter
  implements QValueConverterType<CATypeMatch>
{
  private final CAStrings strings;

  /**
   * Construct a converter.
   *
   * @param inStrings The string resources
   */

  public CATypeMatchConverter(
    final CAStrings inStrings)
  {
    this.strings = Objects.requireNonNull(inStrings, "strings");
  }

  @Override
  public CATypeMatch convertFromString(
    final String text)
    throws QException
  {
    try {
      return new CATypeMatchExpressions(this.strings).typeMatch(text);
    } catch (final CAException e) {
      throw QException.adapt(e, CAErrorCode::id);
    }
  }

  @Override
  public String convertToString(
    final CATypeMatch value)
    throws QException
  {
    try {
      return new CATypeMatchExpressions(this.strings)
        .typeMatchSerializeToString(value);
    } catch (final CAException e) {
      throw QException.adapt(e, CAErrorCode::id);
    }
  }

  @Override
  public CATypeMatch exampleValue()
  {
    return new CATypeMatch(
      new CAComparisonSetType.IsOverlapping<>(
        Set.of(
          new CATypeRecordIdentifier(
            new RDottedName("x.y"),
            new RDottedName("t")
          ),
          new CATypeRecordIdentifier(
            new RDottedName("com.io7m.example"),
            new RDottedName("item")
          )
        )
      )
    );
  }

  @Override
  public String syntax()
  {
    return "<type-match>";
  }

  @Override
  public Class<CATypeMatch> convertedClass()
  {
    return CATypeMatch.class;
  }
}
