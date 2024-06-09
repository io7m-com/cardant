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

import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.quarrel.core.QValueConverterType;

/**
 * A value converter for type match expressions.
 */

public final class CATypeRecordIdentifierConverter
  implements QValueConverterType<CATypeRecordIdentifier>
{
  /**
   * Construct a converter.
   */

  public CATypeRecordIdentifierConverter()
  {

  }

  @Override
  public CATypeRecordIdentifier convertFromString(
    final String text)
  {
    return CATypeRecordIdentifier.of(text);
  }

  @Override
  public String convertToString(
    final CATypeRecordIdentifier value)
  {
    return value.toString();
  }

  @Override
  public CATypeRecordIdentifier exampleValue()
  {
    return CATypeRecordIdentifier.of("com.io7m.example:t");
  }

  @Override
  public String syntax()
  {
    return "<package> ':' <type-name>";
  }

  @Override
  public Class<CATypeRecordIdentifier> convertedClass()
  {
    return CATypeRecordIdentifier.class;
  }
}
