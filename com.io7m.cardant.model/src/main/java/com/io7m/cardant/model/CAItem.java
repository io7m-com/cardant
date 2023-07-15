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
import java.util.Optional;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.stream.Stream;

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
  SortedMap<RDottedName, CAItemMetadata> metadata,
  SortedMap<CAItemAttachmentKey, CAItemAttachment> attachments,
  SortedSet<RDottedName> types)
  implements CAInventoryElementType
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
   * @param relation The relation
   *
   * @return The attachments with the given relation
   */

  public Stream<CAItemAttachment> attachmentsWithRelation(
    final String relation)
  {
    Objects.requireNonNull(relation, "relation");
    return this.attachments.values()
      .stream()
      .filter(attachment -> Objects.equals(attachment.relation(), relation));
  }

  /**
   * @param relation The relation
   *
   * @return The first attachment with the given relation
   */

  public Optional<CAItemAttachment> firstAttachmentWithRelation(
    final String relation)
  {
    Objects.requireNonNull(relation, "relation");
    return this.attachmentsWithRelation(relation)
      .findFirst();
  }

  /**
   * @param relation The relation
   *
   * @return {@code true} if any attachment exists with the given relation
   */

  public boolean hasAttachmentWithRelation(
    final String relation)
  {
    Objects.requireNonNull(relation, "relation");
    return this.firstAttachmentWithRelation(relation).isPresent();
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
}
