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

import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemSearchParameters;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVItemColumnOrdering.ITEM_COLUMN_ORDERING;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVItemLocationMatch.ITEM_LOCATION_MATCH;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVItemNameMatch.ITEM_NAME_MATCH;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVItemTypeMatch.ITEM_TYPE_MATCH;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVMetadataElementMatch.METADATA_MATCH;
import static com.io7m.cedarbridge.runtime.api.CBCore.unsigned32;

/**
 * A validator.
 */

public enum CAUVItemSearchParameters
  implements CAProtocolMessageValidatorType<CAItemSearchParameters, CAI1ItemSearchParameters>
{
  /**
   * A validator.
   */

  ITEM_SEARCH_PARAMETERS;

  @Override
  public CAI1ItemSearchParameters convertToWire(
    final CAItemSearchParameters parameters)
    throws CAProtocolException
  {
    return new CAI1ItemSearchParameters(
      ITEM_LOCATION_MATCH.convertToWire(parameters.locationMatch()),
      ITEM_NAME_MATCH.convertToWire(parameters.nameMatch()),
      ITEM_TYPE_MATCH.convertToWire(parameters.typeMatch()),
      METADATA_MATCH.convertToWire(parameters.metadataMatch()),
      ITEM_COLUMN_ORDERING.convertToWire(parameters.ordering()),
      unsigned32(parameters.pageSize())
    );
  }

  @Override
  public CAItemSearchParameters convertFromWire(
    final CAI1ItemSearchParameters parameters)
    throws CAProtocolException
  {
    return new CAItemSearchParameters(
      ITEM_LOCATION_MATCH.convertFromWire(parameters.fieldLocation()),
      ITEM_NAME_MATCH.convertFromWire(parameters.fieldNameMatch()),
      ITEM_TYPE_MATCH.convertFromWire(parameters.fieldTypeMatch()),
      METADATA_MATCH.convertFromWire(parameters.fieldMetaMatch()),
      ITEM_COLUMN_ORDERING.convertFromWire(parameters.fieldOrder()),
      parameters.fieldLimit().value()
    );
  }
}
