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

import com.io7m.cardant.model.CAIds;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.CAIResponseItemsRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseItemsRemove;
import com.io7m.cedarbridge.runtime.api.CBUUID;
import com.io7m.cedarbridge.runtime.convenience.CBLists;
import com.io7m.cedarbridge.runtime.convenience.CBSets;

/**
 * A validator.
 */

public enum CAUVResponseItemsRemove
  implements CAProtocolMessageValidatorType<CAIResponseItemsRemove, CAI1ResponseItemsRemove>
{
  /**
   * A validator.
   */

  RESPONSE_ITEMS_REMOVE;

  @Override
  public CAI1ResponseItemsRemove convertToWire(
    final CAIResponseItemsRemove c)
  {
    return new CAI1ResponseItemsRemove(
      new CBUUID(c.requestId()),
      CBLists.ofCollection(c.data().ids(), i -> new CBUUID(i.id()))
    );
  }

  @Override
  public CAIResponseItemsRemove convertFromWire(
    final CAI1ResponseItemsRemove m)
  {
    return new CAIResponseItemsRemove(
      m.fieldRequestId().value(),
      new CAIds(CBSets.toSet(m.fieldItems(), x -> new CAItemID(x.value())))
    );
  }
}
