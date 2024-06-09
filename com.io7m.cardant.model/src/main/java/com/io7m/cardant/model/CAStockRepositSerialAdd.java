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

package com.io7m.cardant.model;

import java.util.Objects;

/**
 * An operation that adds a single item with a serial number to a storage location.
 *
 * @param item     The item
 * @param location The storage location
 * @param serial   The item serial number
 */

public record CAStockRepositSerialAdd(
  CAItemID item,
  CALocationID location,
  CAItemSerial serial)
  implements CAStockRepositType
{
  /**
   * An operation that adds a single item with a serial number to a storage location.
   *
   * @param item     The item
   * @param location The storage location
   * @param serial   The item serial number
   */

  public CAStockRepositSerialAdd
  {
    Objects.requireNonNull(item, "item");
    Objects.requireNonNull(location, "location");
    Objects.requireNonNull(serial, "serial");
  }

  @Override
  public long count()
  {
    return 1L;
  }
}
