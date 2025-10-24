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
import com.io7m.cardant.protocol.inventory.CAIResponseRolesRevoke;

public enum CJ1ResponseRolesRevokeX
  implements CJ1SerialBijectionType<CJ1ResponseRolesRevoke, CAIResponseRolesRevoke>
{
  RESPONSE_ROLES_REVOKE;

  @Override
  public CAIResponseRolesRevoke toCore(
    final CJ1ResponseRolesRevoke m)
    throws CAProtocolException
  {
    return new CAIResponseRolesRevoke(
      m.requestId()
    );
  }

  @Override
  public CJ1ResponseRolesRevoke toCJ1(
    final CAIResponseRolesRevoke m)
    throws CAProtocolException
  {
    return new CJ1ResponseRolesRevoke(
      m.requestId()
    );
  }
}
