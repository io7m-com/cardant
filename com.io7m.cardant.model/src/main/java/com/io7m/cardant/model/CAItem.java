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
 * @param count       The item count
 * @param metadata    The item metadata
 * @param attachments The item attachments
 * @param tags        The item tags
 */

public record CAItem(
  CAItemID id,
  String name,
  long count,
  SortedMap<String, CAItemMetadata> metadata,
  SortedMap<CAItemAttachmentID, CAItemAttachment> attachments,
  SortedSet<CATag> tags
) implements CAInventoryElementType
{
  /**
   * Construct an item.
   *
   * @param id          The item ID
   * @param name        The item name
   * @param count       The item count
   * @param metadata    The item metadata
   * @param attachments The item attachments
   * @param tags        The item tags
   */

  public CAItem
  {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(metadata, "metadata");
    Objects.requireNonNull(attachments, "attachments");
    Objects.requireNonNull(tags, "tags");
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
      Collections.emptySortedMap(),
      Collections.emptySortedMap(),
      Collections.emptySortedSet()
    );
  }

  public Optional<String> description()
  {
    return Optional.ofNullable(this.metadata.get("Description"))
      .map(CAItemMetadata::value);
  }

  public String descriptionOrEmpty()
  {
    return this.description()
      .orElse("");
  }

  public Stream<CAItemAttachment> attachmentsWithRelation(
    final String relation)
  {
    Objects.requireNonNull(relation, "relation");
    return this.attachments.values()
      .stream()
      .filter(attachment -> Objects.equals(attachment.relation(), relation));
  }

  public Optional<CAItemAttachment> firstAttachmentWithRelation(
    final String relation)
  {
    Objects.requireNonNull(relation, "relation");
    return this.attachmentsWithRelation(relation)
      .findFirst();
  }

  public boolean hasAttachmentWithRelation(
    final String relation)
  {
    Objects.requireNonNull(relation, "relation");
    return this.firstAttachmentWithRelation(relation).isPresent();
  }
}
