/*
 * Copyright © 2025 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.protocol.inventory.json.internal;


import com.io7m.cardant.model.type_package.CATypePackageSummary;
import com.io7m.cardant.protocol.api.CAProtocolException;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1TypePackageIdentifierX.TYPE_PACKAGE_IDENTIFIER;

public enum CJ1TypePackageSummaryX
  implements CJ1SerialBijectionType<CJ1TypePackageSummary, CATypePackageSummary>
{
  TYPE_PACKAGE_SUMMARY;

  @Override
  public CATypePackageSummary toCore(
    final CJ1TypePackageSummary m)
    throws CAProtocolException
  {
    return new CATypePackageSummary(
      TYPE_PACKAGE_IDENTIFIER.toCore(m.identifier()),
      m.description()
    );
  }

  @Override
  public CJ1TypePackageSummary toCJ1(
    final CATypePackageSummary m)
  {
    return new CJ1TypePackageSummary(
      TYPE_PACKAGE_IDENTIFIER.toCJ1(m.identifier()),
      m.description()
    );
  }
}
