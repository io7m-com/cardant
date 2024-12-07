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
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.SortedSet;

import static java.time.ZoneOffset.UTC;

/**
 * A location.
 *
 * @param id          The location ID
 * @param timeCreated The time the location was created
 * @param timeUpdated The time the location was last updated
 * @param parent      The location parent, if any
 * @param path        The location path
 * @param metadata    The metadata associated with the location
 * @param types       The types associated with the location
 * @param attachments The attachments associated with the location
 */

public record CALocation(
  CALocationID id,
  Optional<CALocationID> parent,
  CALocationPath path,
  OffsetDateTime timeCreated,
  OffsetDateTime timeUpdated,
  SortedMap<CATypeRecordFieldIdentifier, CAMetadataType> metadata,
  SortedMap<CAAttachmentKey, CAAttachment> attachments,
  SortedSet<CATypeRecordIdentifier> types)
  implements CAInventoryObjectType<CALocationSummary>
{
  /**
   * Construct a location.
   *
   * @param id          The location ID
   * @param timeCreated The time the location was created
   * @param timeUpdated The time the location was last updated
   * @param parent      The location parent, if any
   * @param path        The location path
   * @param metadata    The metadata associated with the location
   * @param types       The types associated with the location
   * @param attachments The attachments associated with the location
   */

  public CALocation
  {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(parent, "parent");
    Objects.requireNonNull(path, "path");
    Objects.requireNonNull(metadata, "metadata");
    Objects.requireNonNull(attachments, "attachments");
    Objects.requireNonNull(types, "types");

    timeCreated = timeCreated.withOffsetSameInstant(UTC);
    timeUpdated = timeUpdated.withOffsetSameInstant(UTC);

    parent.ifPresent(parentId -> {
      if (id.equals(parentId)) {
        throw new CAValidityException(
          "A location's parent cannot equal itself");
      }
    });
  }

  /**
   * @return The location display ID
   */

  public String displayId()
  {
    return this.id.displayId();
  }

  @Override
  public CALocationSummary summary()
  {
    return new CALocationSummary(this.id, this.parent, this.path);
  }

  /**
   * @return The location name (the last path component)
   */

  public CALocationName name()
  {
    return this.path.last();
  }
}
