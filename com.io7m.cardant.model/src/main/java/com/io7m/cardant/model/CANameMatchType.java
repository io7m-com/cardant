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

import java.util.Objects;

/**
 * The expression used to match against object names.
 */

public sealed interface CANameMatchType
{
  /**
   * Match any object name.
   */

  enum Any implements CANameMatchType
  {
    /**
     * Match any object name.
     */

    ANY_NAME
  }

  /**
   * Match objects with the exact given name.
   *
   * @param text The name
   */

  record Exact(
    String text)
    implements CANameMatchType
  {
    /**
     * Match objects with the exact given name.
     */

    public Exact
    {
      Objects.requireNonNull(text, "text");
      text = text.trim();
    }
  }

  /**
   * Match object names against the given search query.
   *
   * @param query The query
   */

  record Search(
    String query)
    implements CANameMatchType
  {
    /**
     * Match object names against the given search query.
     */

    public Search
    {
      Objects.requireNonNull(query, "query");
    }
  }
}
