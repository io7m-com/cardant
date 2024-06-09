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
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CATypeRecordFieldIdentifier;
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.cardant.parsers.CAMetadataExpressions;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.lanark.core.RDottedName;
import com.io7m.quarrel.core.QException;
import com.io7m.quarrel.core.QValueConverterType;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * A value converter for metadata.
 */

public final class CAMetadataConverter
  implements QValueConverterType<CAMetadataType>
{
  private final CAStrings strings;

  /**
   * Construct a converter.
   *
   * @param inStrings The string resources
   */

  public CAMetadataConverter(
    final CAStrings inStrings)
  {
    this.strings = Objects.requireNonNull(inStrings, "strings");
  }

  @Override
  public CAMetadataType convertFromString(
    final String text)
    throws QException
  {
    try {
      return new CAMetadataExpressions(this.strings).metadataParse(text);
    } catch (final CAException e) {
      throw QException.adapt(e, CAErrorCode::id);
    }
  }

  @Override
  public String convertToString(
    final CAMetadataType value)
    throws QException
  {
    try {
      return new CAMetadataExpressions(this.strings)
        .metadataSerializeToString(value);
    } catch (final CAException e) {
      throw QException.adapt(e, CAErrorCode::id);
    }
  }

  @Override
  public CAMetadataType exampleValue()
  {
    return new CAMetadataType.Monetary(
      new CATypeRecordFieldIdentifier(
        new CATypeRecordIdentifier(
          new RDottedName("com.io7m.metadata"),
          new RDottedName("t")
        ),
        new RDottedName("x")
      ),
      new BigDecimal("250.23"),
      CurrencyUnit.EUR
    );
  }

  @Override
  public String syntax()
  {
    return "[Text <type-field-identifier> <quoted>] "
      + "| [Integer <type-field-identifier> <integer>] "
      + "| [Real <type-field-identifier> <real>] "
      + "| [Time <type-field-identifier> <offset-date-time>] "
      + "| [Money <type-field-identifier> <money> <currency>]";
  }

  @Override
  public Class<CAMetadataType> convertedClass()
  {
    return CAMetadataType.class;
  }
}
