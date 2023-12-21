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

import com.io7m.cardant.model.CAFileColumn;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1FileColumn;

/**
 * A validator.
 */

public enum CAUVFileColumn
  implements CAProtocolMessageValidatorType<CAFileColumn, CAI1FileColumn>
{
  /**
   * A validator.
   */

  FILE_COLUMN;

  @Override
  public CAI1FileColumn convertToWire(
    final CAFileColumn parameters)
  {
    return switch (parameters) {
      case BY_ID -> {
        yield new CAI1FileColumn.CAI1ById();
      }
      case BY_DESCRIPTION -> {
        yield new CAI1FileColumn.CAI1ByDescription();
      }
    };
  }

  @Override
  public CAFileColumn convertFromWire(
    final CAI1FileColumn message)
  {
    return switch (message) {
      case final CAI1FileColumn.CAI1ByDescription c -> {
        yield CAFileColumn.BY_DESCRIPTION;
      }
      case final CAI1FileColumn.CAI1ById c -> {
        yield CAFileColumn.BY_ID;
      }
    };
  }
}
