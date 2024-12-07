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


package com.io7m.cardant.type_packages.standard;

import com.io7m.cardant.model.CAVersion;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.model.type_package.CATypePackageProviderType;
import com.io7m.lanark.core.RDottedName;
import com.io7m.verona.core.VersionException;
import com.io7m.verona.core.VersionParser;

import java.io.InputStream;

/**
 * A package provider.
 */

public final class CATPComputer
  implements CATypePackageProviderType
{
  /**
   * A package provider.
   */

  public CATPComputer()
  {

  }

  @Override
  public CATypePackageIdentifier identifier()
  {
    try {
      return new CATypePackageIdentifier(
        new RDottedName("cardant.computer"),
        VersionParser.parse(CAVersion.MAIN_VERSION)
      );
    } catch (final VersionException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public InputStream packageText()
  {
    return CATPComputer.class.getResourceAsStream(
      "/com/io7m/cardant/type_packages/standard/Computer.xml"
    );
  }
}
