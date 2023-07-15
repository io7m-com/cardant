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

import com.io7m.lanark.core.RDottedName;

import java.util.Collections;
import java.util.Objects;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * An item in the inventory.
 *
 * @param id          The item ID
 * @param name        The item name
 * @param countTotal  The item count across all storage locations in the inventory
 * @param countHere   The item count in the context of a storage location
 * @param metadata    The item metadata
 * @param attachments The item attachments
 * @param types       The item types
 */

public record CAItem(
  CAItemID id,
  String name,
  long countTotal,
  long countHere,
  SortedMap<RDottedName, CAMetadata> metadata,
  SortedMap<CAAttachmentKey, CAAttachment> attachments,
  SortedSet<RDottedName> types)
  implements CAInventoryObjectType<CAItemSummary>
{
  /**
   * Construct an item.
   *
   * @param id          The item ID
   * @param name        The item name
   * @param countTotal  The item count
   * @param countHere   The item count in the context of a storage location
   * @param metadata    The item metadata
   * @param attachments The item attachments
   * @param types       The types assigned to the item
   */

  public CAItem
  {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(metadata, "metadata");
    Objects.requireNonNull(attachments, "attachments");
    Objects.requireNonNull(types, "types");
    name = name.trim();
  }

  /**
   * Create an empty item with a pseudorandom ID.
   *
   * @return The item
   */

  public static CAItem create()
  {
    return new CAItem(
      CAItemID.random(),
      "",
      0L,
      0L,
      Collections.emptySortedMap(),
      Collections.emptySortedMap(),
      Collections.emptySortedSet()
    );
  }

  /**
   * Set the count in the current storage location context for this item.
   *
   * @param newCountHere The item count
   *
   * @return This item with the given count
   */

  public CAItem withCountHere(
    final long newCountHere)
  {
    return new CAItem(
      this.id,
      this.name,
      this.countTotal,
      newCountHere,
      this.metadata,
      this.attachments,
      this.types
    );
  }

  @Override
  public CAItemSummary summary()
  {
    return new CAItemSummary(this.id, this.name);
  }
}
