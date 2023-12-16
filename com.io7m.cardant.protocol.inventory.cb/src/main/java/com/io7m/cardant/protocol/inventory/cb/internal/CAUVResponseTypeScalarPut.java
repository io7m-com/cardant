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
import com.io7m.cardant.protocol.inventory.CAIResponseTypeScalarPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeScalarPut;
import com.io7m.cedarbridge.runtime.api.CBUUID;
import com.io7m.cedarbridge.runtime.convenience.CBLists;
import com.io7m.cedarbridge.runtime.convenience.CBSets;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVTypeScalar.TYPE_SCALAR;

/**
 * A validator.
 */

public enum CAUVResponseTypeScalarPut
  implements CAProtocolMessageValidatorType<
  CAIResponseTypeScalarPut, CAI1ResponseTypeScalarPut>
{
  /**
   * A validator.
   */

  RESPONSE_TYPE_SCALAR_PUT;

  @Override
  public CAI1ResponseTypeScalarPut convertToWire(
    final CAIResponseTypeScalarPut c)
  {
    return new CAI1ResponseTypeScalarPut(
      new CBUUID(c.requestId()),
      CBLists.ofCollection(c.types(), TYPE_SCALAR::convertToWire)
    );
  }

  @Override
  public CAIResponseTypeScalarPut convertFromWire(
    final CAI1ResponseTypeScalarPut m)
  {
    return new CAIResponseTypeScalarPut(
      m.fieldRequestId().value(),
      CBSets.toSet(m.fieldTypes(), TYPE_SCALAR::convertFromWire)
    );
  }
}
