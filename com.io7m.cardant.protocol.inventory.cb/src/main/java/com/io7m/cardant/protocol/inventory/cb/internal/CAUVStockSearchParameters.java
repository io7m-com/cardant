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

import com.io7m.cardant.model.CAIncludeDeleted;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAStockSearchParameters;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1StockSearchParameters;
import com.io7m.cedarbridge.runtime.api.CBUUID;
import com.io7m.cedarbridge.runtime.convenience.CBLists;
import com.io7m.cedarbridge.runtime.convenience.CBSets;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVItemID.ITEM_ID;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVLocationMatch.LOCATION_MATCH;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVStockOccurrenceKind.STOCK_OCCURRENCE_KIND;
import static com.io7m.cedarbridge.runtime.api.CBCore.unsigned32;

/**
 * A validator.
 */

public enum CAUVStockSearchParameters
  implements CAProtocolMessageValidatorType<CAStockSearchParameters, CAI1StockSearchParameters>
{
  /**
   * A validator.
   */

  STOCK_SEARCH_PARAMETERS;

  private static final CAUVComparisonsExact<CAItemID, CBUUID> ITEM_ID_EXACT =
    new CAUVComparisonsExact<>(ITEM_ID);

  @Override
  public CAI1StockSearchParameters convertToWire(
    final CAStockSearchParameters parameters)
    throws CAProtocolException
  {
    return new CAI1StockSearchParameters(
      LOCATION_MATCH.convertToWire(parameters.locationMatch()),
      ITEM_ID_EXACT.convertToWire(parameters.itemMatch()),
      CBLists.ofCollection(
        parameters.includeOccurrences(),
        STOCK_OCCURRENCE_KIND::convertToWire
      ),
      unsigned32(parameters.pageSize())
    );
  }

  @Override
  public CAStockSearchParameters convertFromWire(
    final CAI1StockSearchParameters message)
    throws CAProtocolException
  {
    return new CAStockSearchParameters(
      LOCATION_MATCH.convertFromWire(message.fieldLocationMatch()),
      ITEM_ID_EXACT.convertFromWire(message.fieldItemMatch()),
      CBSets.toSet(
        message.fieldIncludeOccurrences(),
        STOCK_OCCURRENCE_KIND::convertFromWire
      ),
      CAIncludeDeleted.INCLUDE_ONLY_LIVE,
      message.fieldPageSize().value()
    );
  }
}
