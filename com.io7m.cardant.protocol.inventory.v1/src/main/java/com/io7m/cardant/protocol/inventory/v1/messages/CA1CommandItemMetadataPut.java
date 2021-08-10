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

package com.io7m.cardant.protocol.inventory.v1.messages;

import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemMetadatas;

import java.util.Objects;

/**
 * The "update item metadata" command.
 *
 * @param itemID    The item ID
 * @param metadatas The item metadata
 */

public record CA1CommandItemMetadataPut(
  CAItemID itemID,
  CAItemMetadatas metadatas
) implements CA1InventoryCommandType
{
  /**
   * The "update item metadata" command.
   *
   * @param itemID    The item ID
   * @param metadatas The item metadata
   */

  public CA1CommandItemMetadataPut
  {
    Objects.requireNonNull(itemID, "itemID");
    Objects.requireNonNull(metadatas, "metadatas");
  }
}
