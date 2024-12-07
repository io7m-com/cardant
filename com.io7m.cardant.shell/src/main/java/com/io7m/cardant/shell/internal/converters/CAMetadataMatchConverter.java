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
import com.io7m.cardant.model.CAMetadataElementMatchType;
import com.io7m.cardant.model.CAMetadataElementMatchType.And;
import com.io7m.cardant.model.CAMetadataElementMatchType.Specific;
import com.io7m.cardant.model.CAMetadataValueMatchType.MonetaryMatchType.WithCurrency;
import com.io7m.cardant.model.CAMetadataValueMatchType.RealMatchType.WithinRange;
import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.cardant.parsers.CAMetadataMatchExpressions;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.lanark.core.RDottedName;
import com.io7m.quarrel.core.QException;
import com.io7m.quarrel.core.QValueConverterType;

import java.util.Objects;

import static org.joda.money.CurrencyUnit.EUR;

/**
 * A value converter for metadata match expressions.
 */

public final class CAMetadataMatchConverter
  implements QValueConverterType<CAMetadataElementMatchType>
{
  private final CAStrings strings;

  /**
   * Construct a converter.
   *
   * @param inStrings The string resources
   */

  public CAMetadataMatchConverter(
    final CAStrings inStrings)
  {
    this.strings = Objects.requireNonNull(inStrings, "strings");
  }

  @Override
  public CAMetadataElementMatchType convertFromString(
    final String text)
    throws QException
  {
    try {
      return new CAMetadataMatchExpressions(this.strings).metadataMatch(text);
    } catch (final CAException e) {
      throw QException.adapt(e, CAErrorCode::id);
    }
  }

  @Override
  public String convertToString(
    final CAMetadataElementMatchType value)
    throws QException
  {
    try {
      return new CAMetadataMatchExpressions(this.strings)
        .metadataMatchSerializeToString(value);
    } catch (final CAException e) {
      throw QException.adapt(e, CAErrorCode::id);
    }
  }

  @Override
  public CAMetadataElementMatchType exampleValue()
  {
    return new And(
      new Specific(
        new CAComparisonExactType.IsEqualTo<>(new RDottedName("x.y")),
        new CAComparisonExactType.Anything<>(),
        new CAComparisonExactType.IsNotEqualTo<>("z"),
        new WithinRange(23.0, 200.0)
      ),
      new Specific(
        new CAComparisonExactType.IsEqualTo<>(new RDottedName("a.b")),
        new CAComparisonExactType.Anything<>(),
        new CAComparisonExactType.IsNotEqualTo<>("q"),
        new WithCurrency(EUR)
      )
    );
  }

  @Override
  public String syntax()
  {
    return "<metadata-match>";
  }

  @Override
  public Class<CAMetadataElementMatchType> convertedClass()
  {
    return CAMetadataElementMatchType.class;
  }
}
