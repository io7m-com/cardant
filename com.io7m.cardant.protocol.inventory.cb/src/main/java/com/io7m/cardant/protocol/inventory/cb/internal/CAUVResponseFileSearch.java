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
import com.io7m.cardant.protocol.inventory.CAIResponseFileSearch;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseFileSearch;
import com.io7m.cedarbridge.runtime.api.CBUUID;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVFile.FILE;

/**
 * A validator.
 */

public enum CAUVResponseFileSearch
  implements CAProtocolMessageValidatorType<
  CAIResponseFileSearch, CAI1ResponseFileSearch>
{
  /**
   * A validator.
   */

  RESPONSE_FILE_SEARCH;

  @Override
  public CAI1ResponseFileSearch convertToWire(
    final CAIResponseFileSearch c)
  {
    return new CAI1ResponseFileSearch(
      new CBUUID(c.requestId()),
      CAUVPage.pageToWire(c.data(), FILE::convertToWire)
    );
  }

  @Override
  public CAIResponseFileSearch convertFromWire(
    final CAI1ResponseFileSearch c)
  {
    return new CAIResponseFileSearch(
      c.fieldRequestId().value(),
      CAUVPage.pageFromWire(
        c.fieldResults(),
        f -> FILE.convertFromWire(f).withoutData()
      )
    );
  }
}
