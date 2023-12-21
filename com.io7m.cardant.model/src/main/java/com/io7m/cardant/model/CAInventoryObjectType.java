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

import com.io7m.lanark.core.RDottedName;

import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.stream.Stream;

/**
 * The type of major inventory objects.
 *
 * @param <S> The type of object summaries
 */

public sealed interface CAInventoryObjectType<S>
  permits CAItem,
  CALocation
{
  /**
   * @return A summary of the object
   */

  S summary();

  /**
   * Get the set of attachments on this object with the given relation.
   *
   * @param relation The relation
   *
   * @return The attachments
   */

  default Stream<CAAttachment> attachmentsWithRelation(
    final String relation)
  {
    Objects.requireNonNull(relation, "relation");
    return this.attachments().values()
      .stream()
      .filter(attachment -> Objects.equals(attachment.relation(), relation));
  }

  /**
   * Get the first attachment on this object with the given relation.
   *
   * @param relation The relation
   *
   * @return The attachment
   */

  default Optional<CAAttachment> firstAttachmentWithRelation(
    final String relation)
  {
    Objects.requireNonNull(relation, "relation");
    return this.attachmentsWithRelation(relation).findFirst();
  }

  /**
   * Check if this object has an attachment with the given relation.
   *
   * @param relation The relation
   *
   * @return {@code true} if this object has an attachment with the given relation
   */

  default boolean hasAttachmentWithRelation(
    final String relation)
  {
    Objects.requireNonNull(relation, "relation");
    return this.firstAttachmentWithRelation(relation).isPresent();
  }

  /**
   * @return The metadata on this object
   */

  SortedMap<RDottedName, CAMetadataType> metadata();

  /**
   * @return The attachments on this object
   */

  SortedMap<CAAttachmentKey, CAAttachment> attachments();

  /**
   * @return The types assigned to this object
   */

  SortedSet<RDottedName> types();
}
