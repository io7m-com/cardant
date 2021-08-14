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

import com.io7m.cardant.database.api.CADatabaseException;

import java.util.Optional;
import java.util.SortedSet;
import java.util.UUID;

/**
 * Model database queries (Tags).
 */

public interface CAModelCADatabaseQueriesTagsType
{
  /**
   * Retrieve the tag with the given ID, if one exists.
   *
   * @param id The ID
   *
   * @return The tag, if any
   *
   * @throws CADatabaseException On database errors
   */

  Optional<CATag> tagGet(UUID id)
    throws CADatabaseException;

  /**
   * Create or update the given tag.
   *
   * @param tag The tag
   *
   * @throws CADatabaseException On database errors
   */

  void tagPut(CATag tag)
    throws CADatabaseException;

  /**
   * Delete the given tag. The tag will be removed from any items it is associated with.
   *
   * @param tag The tag
   *
   * @throws CADatabaseException On database errors
   */

  void tagDelete(CATag tag)
    throws CADatabaseException;

  /**
   * @return The available tags
   *
   * @throws CADatabaseException On database errors
   */

  SortedSet<CATag> tagList()
    throws CADatabaseException;
}
