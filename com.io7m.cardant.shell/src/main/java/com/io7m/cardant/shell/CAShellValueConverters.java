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

import com.io7m.cardant.shell.internal.converters.CADescriptionMatchConverter;
import com.io7m.cardant.shell.internal.converters.CAFileIdConverter;
import com.io7m.cardant.shell.internal.converters.CAItemIDMatchConverter;
import com.io7m.cardant.shell.internal.converters.CAItemIdConverter;
import com.io7m.cardant.shell.internal.converters.CALocationMatchConverter;
import com.io7m.cardant.shell.internal.converters.CAItemSerialMatchConverter;
import com.io7m.cardant.shell.internal.converters.CALocationIdConverter;
import com.io7m.cardant.shell.internal.converters.CAMediaTypeMatchConverter;
import com.io7m.cardant.shell.internal.converters.CAMetadataConverter;
import com.io7m.cardant.shell.internal.converters.CAMetadataMatchConverter;
import com.io7m.cardant.shell.internal.converters.CAMonetaryRangeConverter;
import com.io7m.cardant.shell.internal.converters.CANameMatchConverter;
import com.io7m.cardant.shell.internal.converters.CAPatternConverter;
import com.io7m.cardant.shell.internal.converters.CARDottedNameConverter;
import com.io7m.cardant.shell.internal.converters.CARangeInclusiveDConverter;
import com.io7m.cardant.shell.internal.converters.CARangeInclusiveLConverter;
import com.io7m.cardant.shell.internal.converters.CARoleNameConverter;
import com.io7m.cardant.shell.internal.converters.CATimeRangeConverter;
import com.io7m.cardant.shell.internal.converters.CATypeMatchConverter;
import com.io7m.cardant.shell.internal.converters.CATypePackageUninstallBehaviorConverter;
import com.io7m.cardant.shell.internal.converters.CATypeRecordFieldIdentifierConverter;
import com.io7m.cardant.shell.internal.converters.CATypeRecordIdentifierConverter;
import com.io7m.cardant.shell.internal.converters.CAUserIdConverter;
import com.io7m.cardant.shell.internal.converters.CAVersionConverter;
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
      .with(new CADescriptionMatchConverter(strings))
      .with(new CAFileIdConverter())
      .with(new CAItemIdConverter())
      .with(new CALocationMatchConverter(strings))
      .with(new CAItemSerialMatchConverter(strings))
      .with(new CAItemIDMatchConverter(strings))
      .with(new CALocationIdConverter())
      .with(new CAMediaTypeMatchConverter(strings))
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
      .with(new CATypePackageUninstallBehaviorConverter())
      .with(new CATypeRecordFieldIdentifierConverter())
      .with(new CATypeRecordIdentifierConverter())
      .with(new CAUserIdConverter())
      .with(new CAVersionConverter());
  }
}
