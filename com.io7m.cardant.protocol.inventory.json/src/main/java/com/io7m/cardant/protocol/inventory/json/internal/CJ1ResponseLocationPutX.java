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
import com.io7m.cardant.protocol.inventory.CAIResponseLocationPut;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1LocationX.LOCATION;

public enum CJ1ResponseLocationPutX
  implements CJ1SerialBijectionType<CJ1ResponseLocationPut, CAIResponseLocationPut>
{
  RESPONSE_LOCATION_PUT;

  @Override
  public CAIResponseLocationPut toCore(
    final CJ1ResponseLocationPut m)
    throws CAProtocolException
  {
    return new CAIResponseLocationPut(
      m.requestId(),
      LOCATION.toCore(m.location())
    );
  }

  @Override
  public CJ1ResponseLocationPut toCJ1(
    final CAIResponseLocationPut m)
    throws CAProtocolException
  {
    return new CJ1ResponseLocationPut(
      m.requestId(),
      LOCATION.toCJ1(m.data())
    );
  }
}
