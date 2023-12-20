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

import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.CAICommandLocationTypesRevoke;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandLocationTypesRevoke;
import com.io7m.cedarbridge.runtime.api.CBCore;
import com.io7m.cedarbridge.runtime.api.CBUUID;
import com.io7m.cedarbridge.runtime.convenience.CBLists;
import com.io7m.cedarbridge.runtime.convenience.CBSets;
import com.io7m.lanark.core.RDottedName;

/**
 * A validator.
 */

public enum CAUVCommandLocationTypesRevoke
  implements CAProtocolMessageValidatorType<CAICommandLocationTypesRevoke, CAI1CommandLocationTypesRevoke>
{
  /**
   * A validator.
   */

  COMMAND_LOCATION_TYPES_REVOKE;

  @Override
  public CAI1CommandLocationTypesRevoke convertToWire(
    final CAICommandLocationTypesRevoke c)
  {
    return new CAI1CommandLocationTypesRevoke(
      new CBUUID(c.location().id()),
      CBLists.ofCollection(c.types(), r -> CBCore.string(r.value()))
    );
  }

  @Override
  public CAICommandLocationTypesRevoke convertFromWire(
    final CAI1CommandLocationTypesRevoke c)
  {
    return new CAICommandLocationTypesRevoke(
      new CALocationID(c.fieldLocation().value()),
      CBSets.toSet(c.fieldTypes(), x -> new RDottedName(x.value()))
    );
  }
}