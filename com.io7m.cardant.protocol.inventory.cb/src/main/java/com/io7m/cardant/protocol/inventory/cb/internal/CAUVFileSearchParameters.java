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

import com.io7m.cardant.model.CAFileSearchParameters;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1FileSearchParameters;
import com.io7m.cedarbridge.runtime.api.CBString;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVFileColumnOrdering.FILE_COLUMN_ORDERING;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVSizeRange.SIZE_RANGE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVStrings.STRINGS;
import static com.io7m.cedarbridge.runtime.api.CBCore.unsigned32;

/**
 * A validator.
 */

public enum CAUVFileSearchParameters
  implements CAProtocolMessageValidatorType<CAFileSearchParameters, CAI1FileSearchParameters>
{
  /**
   * A validator.
   */

  FILE_SEARCH_PARAMETERS;

  private static final CAUVComparisonsFuzzy<String, CBString> FUZZY_VALIDATOR =
    new CAUVComparisonsFuzzy<>(STRINGS);

  @Override
  public CAI1FileSearchParameters convertToWire(
    final CAFileSearchParameters parameters)
    throws CAProtocolException
  {
    return new CAI1FileSearchParameters(
      FUZZY_VALIDATOR.convertToWire(parameters.description()),
      FUZZY_VALIDATOR.convertToWire(parameters.mediaType()),
      SIZE_RANGE.convertToWire(parameters.sizeRange()),
      FILE_COLUMN_ORDERING.convertToWire(parameters.ordering()),
      unsigned32(parameters.pageSize())
    );
  }

  @Override
  public CAFileSearchParameters convertFromWire(
    final CAI1FileSearchParameters message)
    throws CAProtocolException
  {
    return new CAFileSearchParameters(
      FUZZY_VALIDATOR.convertFromWire(message.fieldSearch()),
      FUZZY_VALIDATOR.convertFromWire(message.fieldMediaType()),
      SIZE_RANGE.convertFromWire(message.fieldSizeRange()),
      FILE_COLUMN_ORDERING.convertFromWire(message.fieldOrder()),
      message.fieldLimit().value()
    );
  }
}
