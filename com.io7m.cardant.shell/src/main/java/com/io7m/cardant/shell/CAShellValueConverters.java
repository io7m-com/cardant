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


package com.io7m.cardant.shell;

import com.io7m.cardant.shell.internal.CAFileIdConverter;
import com.io7m.cardant.shell.internal.CAItemIdConverter;
import com.io7m.cardant.shell.internal.CAItemLocationMatchConverter;
import com.io7m.cardant.shell.internal.CALocationIdConverter;
import com.io7m.cardant.shell.internal.CAMetadataConverter;
import com.io7m.cardant.shell.internal.CAMetadataMatchConverter;
import com.io7m.cardant.shell.internal.CAMonetaryRangeConverter;
import com.io7m.cardant.shell.internal.CANameMatchConverter;
import com.io7m.cardant.shell.internal.CAPatternConverter;
import com.io7m.cardant.shell.internal.CARDottedNameConverter;
import com.io7m.cardant.shell.internal.CARangeInclusiveDConverter;
import com.io7m.cardant.shell.internal.CARangeInclusiveLConverter;
import com.io7m.cardant.shell.internal.CARoleNameConverter;
import com.io7m.cardant.shell.internal.CATimeRangeConverter;
import com.io7m.cardant.shell.internal.CATypeMatchConverter;
import com.io7m.cardant.shell.internal.CAUserIdConverter;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.quarrel.core.QValueConverterDirectory;
import com.io7m.quarrel.core.QValueConverterDirectoryType;

/**
 * Value converters for the shell commands.
 */

public final class CAShellValueConverters
{
  private CAShellValueConverters()
  {

  }

  /**
   * @param strings The string resources
   *
   * @return The value converters
   */

  public static QValueConverterDirectoryType create(
    final CAStrings strings)
  {
    return QValueConverterDirectory.core()
      .with(new CAFileIdConverter())
      .with(new CAItemIdConverter())
      .with(new CAItemLocationMatchConverter(strings))
      .with(new CALocationIdConverter())
      .with(new CAMetadataConverter(strings))
      .with(new CAMetadataMatchConverter(strings))
      .with(new CAMonetaryRangeConverter(strings))
      .with(new CANameMatchConverter(strings))
      .with(new CAPatternConverter())
      .with(new CARDottedNameConverter())
      .with(new CARangeInclusiveDConverter(strings))
      .with(new CARangeInclusiveLConverter(strings))
      .with(new CARoleNameConverter())
      .with(new CATimeRangeConverter(strings))
      .with(new CATypeMatchConverter(strings))
      .with(new CAUserIdConverter());
  }
}
