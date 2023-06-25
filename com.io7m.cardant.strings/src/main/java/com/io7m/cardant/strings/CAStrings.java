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

package com.io7m.cardant.strings;

import com.io7m.jxtrand.vanilla.JXTAbstractGenericStrings;
import com.io7m.repetoir.core.RPServiceType;

import java.util.Locale;

/**
 * The string resources.
 */

public final class CAStrings
  extends JXTAbstractGenericStrings<CAStringConstantType>
  implements RPServiceType
{
  /**
   * The string resources.
   *
   * @param locale The application locale
   */

  private CAStrings(
    final Locale locale)
  {
    super(
      locale,
      CAStrings.class,
      "/com/io7m/cardant/strings",
      "Messages"
    );
  }

  /**
   * Create string resources.
   *
   * @param locale The locale
   *
   * @return The string resources
   */

  public static CAStrings create(
    final Locale locale)
  {
    return new CAStrings(locale);
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CAStrings 0x%08x]",
      Integer.valueOf(this.hashCode())
    );
  }

  @Override
  public String description()
  {
    return "String resource service.";
  }
}
