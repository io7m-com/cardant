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

package com.io7m.cardant.database.api;

import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;

import java.util.Optional;
import java.util.SortedSet;

/**
 * Model database queries (Tags).
 */

public sealed interface CADatabaseQueriesTagsType
  extends CADatabaseQueriesType
{
  /**
   * Retrieve the tag with the given ID, if one exists.
   */

  non-sealed interface GetType
    extends CADatabaseQueryType<CATagID, Optional<CATag>>,
    CADatabaseQueriesTagsType
  {

  }

  /**
   * Create or update the given tag.
   */

  non-sealed interface PutType
    extends CADatabaseQueryType<CATag, CADatabaseUnit>,
    CADatabaseQueriesTagsType
  {

  }

  /**
   * Delete the given tag. The tag will be removed from any items it is associated with.
   */

  non-sealed interface DeleteType
    extends CADatabaseQueryType<CATag, CADatabaseUnit>,
    CADatabaseQueriesTagsType
  {

  }

  /**
   * List tags.
   */

  non-sealed interface ListType
    extends CADatabaseQueryType<CADatabaseUnit, SortedSet<CATag>>,
    CADatabaseQueriesTagsType
  {

  }
}
