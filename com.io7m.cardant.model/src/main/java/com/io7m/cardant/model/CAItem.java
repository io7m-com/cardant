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

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.SortedMap;
import java.util.SortedSet;

import static java.time.ZoneOffset.UTC;

/**
 * An item in the inventory.
 *
 * @param id          The item ID
 * @param timeCreated The time the item was created
 * @param timeUpdated The time the item was last updated
 * @param name        The item name
 * @param metadata    The item metadata
 * @param attachments The item attachments
 * @param types       The item types
 */

public record CAItem(
  CAItemID id,
  String name,
  OffsetDateTime timeCreated,
  OffsetDateTime timeUpdated,
  SortedMap<CATypeRecordFieldIdentifier, CAMetadataType> metadata,
  SortedMap<CAAttachmentKey, CAAttachment> attachments,
  SortedSet<CATypeRecordIdentifier> types)
  implements CAInventoryObjectType<CAItemSummary>
{
  /**
   * Construct an item.
   *
   * @param id          The item ID
   * @param timeCreated The time the item was created
   * @param timeUpdated The time the item was last updated
   * @param name        The item name
   * @param metadata    The item metadata
   * @param attachments The item attachments
   * @param types       The item types
   */

  public CAItem
  {
    Objects.requireNonNull(attachments, "attachments");
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(metadata, "metadata");
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(types, "types");

    timeCreated = timeCreated.withOffsetSameInstant(UTC);
    timeUpdated = timeUpdated.withOffsetSameInstant(UTC);
    name = name.trim();
  }

  /**
   * Create an empty item with a pseudorandom ID.
   *
   * @return The item
   */

  public static CAItem create()
  {
    return createWith(CAItemID.random());
  }

  /**
   * Create an empty item with the given ID.
   *
   * @param id The ID
   *
   * @return The item
   */

  public static CAItem createWith(
    final CAItemID id)
  {
    return new CAItem(
      id,
      "",
      OffsetDateTime.now(UTC),
      OffsetDateTime.now(UTC),
      Collections.emptySortedMap(),
      Collections.emptySortedMap(),
      Collections.emptySortedSet()
    );
  }

  @Override
  public CAItemSummary summary()
  {
    return new CAItemSummary(this.id, this.name);
  }
}
