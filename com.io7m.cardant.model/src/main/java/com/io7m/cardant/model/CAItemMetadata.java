/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
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
 * Item metadata.
 *
 * @param itemId The item ID
 * @param name   The metadata name
 * @param value  The metadata value
 */

public record CAItemMetadata(
  CAItemID itemId,
  String name,
  String value
) implements CAInventoryElementType
{
  /**
   * Construct metadata.
   *
   * @param itemId The item ID
   * @param name   The metadata name
   * @param value  The metadata value
   */

  public CAItemMetadata
  {
    Objects.requireNonNull(itemId, "itemId");
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(value, "value");

    if (name.length() >= 128) {
      throw new IllegalArgumentException(
        String.format("Metadata name too long: %s", name)
      );
    }

    if (value.length() >= 1024) {
      throw new IllegalArgumentException(
        String.format("Metadata value too long: %s", name)
      );
    }
  }
}
