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

import com.io7m.quarrel.core.QException;
import com.io7m.quarrel.core.QValueConverterType;
import com.io7m.verona.core.Version;
import com.io7m.verona.core.VersionException;
import com.io7m.verona.core.VersionParser;
import com.io7m.verona.core.VersionQualifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A value converter for type match expressions.
 */

public final class CAVersionConverter
  implements QValueConverterType<Version>
{
  /**
   * Construct a converter.
   *
   */

  public CAVersionConverter()
  {

  }

  @Override
  public Version convertFromString(
    final String text)
    throws QException
  {
    try {
      return VersionParser.parse(text);
    } catch (final VersionException e) {
      throw new QException(
        e.getMessage(),
        e,
        "parse-error",
        Map.of(),
        Optional.empty(),
        List.of()
      );
    }
  }

  @Override
  public String convertToString(
    final Version value)
  {
    return value.toString();
  }

  @Override
  public Version exampleValue()
  {
    return new Version(
      1,
      0,
      2,
      Optional.of(new VersionQualifier("SNAPSHOT"))
    );
  }

  @Override
  public String syntax()
  {
    return "<version>";
  }

  @Override
  public Class<Version> convertedClass()
  {
    return Version.class;
  }
}
