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
import com.io7m.cardant.protocol.inventory.CAIResponseItemTypesRevoke;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1ItemX.ITEM;

public enum CJ1ResponseItemTypesRevokeX
  implements CJ1SerialBijectionType<CJ1ResponseItemTypesRevoke, CAIResponseItemTypesRevoke>
{
  RESPONSE_ITEM_TYPES_REVOKE;

  @Override
  public CAIResponseItemTypesRevoke toCore(
    final CJ1ResponseItemTypesRevoke m)
    throws CAProtocolException
  {
    return new CAIResponseItemTypesRevoke(
      m.requestId(),
      ITEM.toCore(m.item())
    );
  }

  @Override
  public CJ1ResponseItemTypesRevoke toCJ1(
    final CAIResponseItemTypesRevoke m)
    throws CAProtocolException
  {
    return new CJ1ResponseItemTypesRevoke(
      m.requestId(),
      ITEM.toCJ1(m.data())
    );
  }
}
