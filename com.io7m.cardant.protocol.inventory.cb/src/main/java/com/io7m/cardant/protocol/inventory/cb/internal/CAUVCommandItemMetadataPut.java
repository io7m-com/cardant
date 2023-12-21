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

import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemMetadataPut;
import com.io7m.cedarbridge.runtime.api.CBUUID;
import com.io7m.cedarbridge.runtime.convenience.CBLists;
import com.io7m.cedarbridge.runtime.convenience.CBSets;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVMetadata.METADATA;

/**
 * A validator.
 */

public enum CAUVCommandItemMetadataPut
  implements CAProtocolMessageValidatorType<CAICommandItemMetadataPut, CAI1CommandItemMetadataPut>
{
  /**
   * A validator.
   */

  COMMAND_ITEM_METADATA_PUT;

  @Override
  public CAI1CommandItemMetadataPut convertToWire(
    final CAICommandItemMetadataPut c)
  {
    return new CAI1CommandItemMetadataPut(
      new CBUUID(c.item().id()),
      CBLists.ofCollection(c.metadatas(), METADATA::convertToWire)
    );
  }

  @Override
  public CAICommandItemMetadataPut convertFromWire(
    final CAI1CommandItemMetadataPut m)
  {
    return new CAICommandItemMetadataPut(
      new CAItemID(m.fieldItemId().value()),
      CBSets.toSet(
        m.fieldMetadatas(),
        METADATA::convertFromWire
      )
    );
  }
}
