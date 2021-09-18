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
import java.util.Optional;

/**
 * A location.
 *
 * @param id          The location ID
 * @param parent      The location parent, if any
 * @param name        The location name
 * @param description The location description
 */

public record CALocation(
  CALocationID id,
  Optional<CALocationID> parent,
  String name,
  String description
) implements CAInventoryElementType
{
  /**
   * Construct a location.
   *
   * @param id          The location ID
   * @param parent      The location parent, if any
   * @param name        The location name
   * @param description The location description
   */

  public CALocation
  {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(parent, "parent");
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(description, "description");

    parent.ifPresent(parentId -> {
      if (id.equals(parentId)) {
        throw new IllegalArgumentException(
          "A location's parent cannot equal itself");
      }
    });
  }
}
