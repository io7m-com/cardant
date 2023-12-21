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

import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.CAICommandAuditSearchNext;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandAuditSearchNext;

/**
 * A validator.
 */

public enum CAUVCommandAuditSearchNext
  implements CAProtocolMessageValidatorType<
  CAICommandAuditSearchNext, CAI1CommandAuditSearchNext>
{
  /**
   * A validator.
   */

  COMMAND_AUDIT_SEARCH_NEXT;

  @Override
  public CAI1CommandAuditSearchNext convertToWire(
    final CAICommandAuditSearchNext c)
    throws CAProtocolException
  {
    return new CAI1CommandAuditSearchNext(

    );
  }

  @Override
  public CAICommandAuditSearchNext convertFromWire(
    final CAI1CommandAuditSearchNext m)
    throws CAProtocolException
  {
    return new CAICommandAuditSearchNext(

    );
  }
}
