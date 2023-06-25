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
import java.util.Optional;

/**
 * The immutable parameters required to search for files.
 *
 * @param description The description query
 * @param mediaType   The media type query
 * @param ordering    The ordering specification
 * @param sizeRange   The range of file sizes to consider
 * @param limit       The limit on the number of returned users
 */

public record CAFileSearchParameters(
  Optional<String> description,
  Optional<String> mediaType,
  Optional<CASizeRange> sizeRange,
  CAFileColumnOrdering ordering,
  int limit)
{
  /**
   * The immutable parameters required to search for files.
   *
   * @param description The description query
   * @param mediaType   The media type query
   * @param ordering    The ordering specification
   * @param sizeRange   The range of file sizes to consider
   * @param limit       The limit on the number of returned users
   */

  public CAFileSearchParameters
  {
    Objects.requireNonNull(description, "description");
    Objects.requireNonNull(mediaType, "mediaType");
    Objects.requireNonNull(sizeRange, "sizeRange");
    Objects.requireNonNull(ordering, "ordering");
  }

  /**
   * @return The limit on the number of returned items
   */

  @Override
  public int limit()
  {
    return Math.max(1, this.limit);
  }
}
