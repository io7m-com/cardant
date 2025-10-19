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

import com.io7m.cardant.model.CAFileColumn;

public enum CJ1FileColumnX
  implements CJ1SerialBijectionType<CJ1FileColumn, CAFileColumn>
{
  FILE_COLUMN;

  @Override
  public CAFileColumn toCore(
    final CJ1FileColumn m)
  {
    return switch (m) {
      case BY_ID -> {
        yield CAFileColumn.BY_ID;
      }
      case BY_DESCRIPTION -> {
        yield CAFileColumn.BY_DESCRIPTION;
      }
    };
  }

  @Override
  public CJ1FileColumn toCJ1(
    final CAFileColumn m)
  {
    return switch (m) {
      case BY_ID -> {
        yield CJ1FileColumn.BY_ID;
      }
      case BY_DESCRIPTION -> {
        yield CJ1FileColumn.BY_DESCRIPTION;
      }
    };
  }
}
