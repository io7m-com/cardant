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

import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.inventory.CAIResponseAuditSearch;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1AuditEventX.AUDIT_EVENT;

public enum CJ1ResponseAuditSearchX
  implements CJ1SerialBijectionType<CJ1ResponseAuditSearch, CAIResponseAuditSearch>
{
  RESPONSE_AUDIT_SEARCH;

  @Override
  public CAIResponseAuditSearch toCore(
    final CJ1ResponseAuditSearch m)
    throws CAProtocolException
  {
    return new CAIResponseAuditSearch(
      m.requestId(),
      new CJ1PageX<>(
        AUDIT_EVENT::toCJ1,
        AUDIT_EVENT::toCore
      ).toCore(m.results())
    );
  }

  @Override
  public CJ1ResponseAuditSearch toCJ1(
    final CAIResponseAuditSearch m)
    throws CAProtocolException
  {
    return new CJ1ResponseAuditSearch(
      m.requestId(),
      new CJ1PageX<>(
        AUDIT_EVENT::toCJ1,
        AUDIT_EVENT::toCore
      ).toCJ1(m.results())
    );
  }
}
