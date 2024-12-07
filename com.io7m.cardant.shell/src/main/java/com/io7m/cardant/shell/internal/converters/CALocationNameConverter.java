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

import com.io7m.cardant.model.CALocationName;
import com.io7m.quarrel.core.QValueConverterType;

/**
 * A value converter for location names.
 */

public final class CALocationNameConverter
  implements QValueConverterType<CALocationName>
{
  /**
   * Construct a converter.
   */

  public CALocationNameConverter()
  {

  }

  @Override
  public CALocationName convertFromString(
    final String text)
  {
    return new CALocationName(text);
  }

  @Override
  public String convertToString(
    final CALocationName value)
  {
    return value.value();
  }

  @Override
  public CALocationName exampleValue()
  {
    return new CALocationName("Location 0 (East)");
  }

  @Override
  public String syntax()
  {
    return CALocationName.VALID_NAME.pattern();
  }

  @Override
  public Class<CALocationName> convertedClass()
  {
    return CALocationName.class;
  }
}
