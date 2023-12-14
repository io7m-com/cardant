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
 * An operation that moves a set of items from one storage location to another.
 *
 * @param item         The item
 * @param fromLocation The source storage location
 * @param toLocation   The target storage location
 * @param count        The item count
 */

public record CAItemRepositMove(
  CAItemID item,
  CALocationID fromLocation,
  CALocationID toLocation,
  long count)
  implements CAItemRepositType
{
  /**
   * An operation that moves a set of items from one storage location to another.
   *
   * @param item         The item
   * @param fromLocation The source storage location
   * @param toLocation   The target storage location
   * @param count        The item count
   */

  public CAItemRepositMove
  {
    Objects.requireNonNull(item, "item");
    Objects.requireNonNull(fromLocation, "fromLocation");
    Objects.requireNonNull(toLocation, "toLocation");
  }
}
