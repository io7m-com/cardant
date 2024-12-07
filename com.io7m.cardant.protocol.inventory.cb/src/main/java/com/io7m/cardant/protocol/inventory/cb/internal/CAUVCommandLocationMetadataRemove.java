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
import com.io7m.cardant.protocol.inventory.CAICommandLocationMetadataRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandLocationMetadataRemove;
import com.io7m.cedarbridge.runtime.api.CBUUID;
import com.io7m.cedarbridge.runtime.convenience.CBLists;
import com.io7m.cedarbridge.runtime.convenience.CBSets;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVTypeRecordFieldIdentifier.TYPE_RECORD_FIELD_IDENTIFIER;

/**
 * A validator.
 */

public enum CAUVCommandLocationMetadataRemove
  implements CAProtocolMessageValidatorType<CAICommandLocationMetadataRemove, CAI1CommandLocationMetadataRemove>
{
  /**
   * A validator.
   */

  COMMAND_LOCATION_METADATA_REMOVE;

  @Override
  public CAI1CommandLocationMetadataRemove convertToWire(
    final CAICommandLocationMetadataRemove c)
  {
    return new CAI1CommandLocationMetadataRemove(
      new CBUUID(c.location().id()),
      CBLists.ofCollection(
        c.metadataNames(),
        TYPE_RECORD_FIELD_IDENTIFIER::convertToWire
      )
    );
  }

  @Override
  public CAICommandLocationMetadataRemove convertFromWire(
    final CAI1CommandLocationMetadataRemove m)
  {
    return new CAICommandLocationMetadataRemove(
      new CALocationID(m.fieldLocationId().value()),
      CBSets.toSet(
        m.fieldMetadatas(),
        TYPE_RECORD_FIELD_IDENTIFIER::convertFromWire
      )
    );
  }
}
