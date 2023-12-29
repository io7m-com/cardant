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


package com.io7m.cardant.protocol.inventory.cb.internal;

import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.CAIResponseTypePackageSearch;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypePackageSearch;
import com.io7m.cedarbridge.runtime.api.CBUUID;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVTypePackageSummary.TYPE_PACKAGE_SUMMARY;

/**
 * A validator.
 */

public enum CAUVResponseTypePackageSearch
  implements CAProtocolMessageValidatorType<
  CAIResponseTypePackageSearch, CAI1ResponseTypePackageSearch>
{
  /**
   * A validator.
   */

  RESPONSE_TYPE_PACKAGE_SEARCH;

  @Override
  public CAI1ResponseTypePackageSearch convertToWire(
    final CAIResponseTypePackageSearch c)
  {
    return new CAI1ResponseTypePackageSearch(
      new CBUUID(c.requestId()),
      CAUVPage.pageToWire(c.data(), TYPE_PACKAGE_SUMMARY::convertToWire)
    );
  }

  @Override
  public CAIResponseTypePackageSearch convertFromWire(
    final CAI1ResponseTypePackageSearch c)
  {
    return new CAIResponseTypePackageSearch(
      c.fieldRequestId().value(),
      CAUVPage.pageFromWire(c.fieldResults(), TYPE_PACKAGE_SUMMARY::convertFromWire)
    );
  }
}
