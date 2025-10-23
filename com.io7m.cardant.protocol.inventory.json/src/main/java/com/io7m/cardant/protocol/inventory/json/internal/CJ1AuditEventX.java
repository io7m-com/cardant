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

import com.io7m.cardant.model.CAAuditEvent;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolUncheckedException;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1UnsignedLongX.UNSIGNED_LONG;

public enum CJ1AuditEventX
  implements CJ1SerialBijectionType<CJ1AuditEvent, CAAuditEvent>
{
  AUDIT_EVENT;

  @Override
  public CAAuditEvent toCore(
    final CJ1AuditEvent m)
  {
    try {
      return new CAAuditEvent(
        UNSIGNED_LONG.toCore(m.id()),
        m.time(),
        CAUserID.of(m.owner()),
        m.type(),
        m.data()
      );
    } catch (final CAProtocolException e) {
      throw new CAProtocolUncheckedException(e);
    }
  }

  @Override
  public CJ1AuditEvent toCJ1(
    final CAAuditEvent m)
  {
    try {
      return new CJ1AuditEvent(
        UNSIGNED_LONG.toCJ1(m.id()),
        m.time(),
        m.owner().id(),
        m.type(),
        m.data()
      );
    } catch (final CAProtocolException e) {
      throw new CAProtocolUncheckedException(e);
    }
  }
}
