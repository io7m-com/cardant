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
 * The behaviour requested for listing items in locations.
 */

public sealed interface CAListLocationBehaviourType
{
  /**
   * Only list items within the exact given location.
   *
   * @param location The location
   */

  record CAListLocationExact(
    CALocationID location)
    implements CAListLocationBehaviourType
  {
    /**
     * Only list items within the exact given location.
     */

    public CAListLocationExact
    {
      Objects.requireNonNull(location, "location");
    }
  }

  /**
   * List items within the given location and all descendants of the given
   * location.
   *
   * @param location The location
   */

  record CAListLocationWithDescendants(
    CALocationID location)
    implements CAListLocationBehaviourType
  {
    /**
     * List items within the given location and all descendants of the given
     * location.
     */

    public CAListLocationWithDescendants
    {
      Objects.requireNonNull(location, "location");
    }
  }

  /**
   * List items within all locations.
   */

  record CAListLocationsAll()
    implements CAListLocationBehaviourType
  {
  }
}
