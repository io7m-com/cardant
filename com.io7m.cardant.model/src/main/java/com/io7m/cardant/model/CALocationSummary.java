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

import static java.time.ZoneOffset.UTC;

/**
 * A summary of a location.
 *
 * @param id     The id
 * @param parent The parent
 * @param path   The location path
 * @param timeCreated The time the item was created
 * @param timeUpdated The time the item was last updated
 */

public record CALocationSummary(
  CALocationID id,
  Optional<CALocationID> parent,
  CALocationPath path,
  OffsetDateTime timeCreated,
  OffsetDateTime timeUpdated)
{
  /**
   * A summary of a location.
   *
   * @param id     The id
   * @param parent The parent
   * @param path   The location path
   * @param timeCreated The time the item was created
   * @param timeUpdated The time the item was last updated
   */

  public CALocationSummary
  {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(parent, "parent");
    Objects.requireNonNull(path, "path");

    timeCreated = timeCreated.withOffsetSameInstant(UTC);
    timeUpdated = timeUpdated.withOffsetSameInstant(UTC);
  }

  /**
   * @return The location name (the last path component)
   */

  public CALocationName name()
  {
    return this.path.last();
  }

  /**
   * Adjust this location summary to have a new path.
   *
   * @param newPath The new path
   *
   * @return This location summary with a new path
   */

  public CALocationSummary withPath(
    final CALocationPath newPath)
  {
    return new CALocationSummary(
      this.id,
      this.parent,
      newPath,
      this.timeCreated,
      this.timeUpdated
    );
  }
}
