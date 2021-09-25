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

import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.UNICODE_CHARACTER_CLASS;

/**
 * A tag.
 *
 * @param id   The unique tag ID
 * @param name The tag name
 */

public record CATag(
  CATagID id,
  String name
) implements CAInventoryElementType, Comparable<CATag>
{
  /**
   * The pattern of valid tag names.
   */

  public static final Pattern VALID_TAG_NAME =
    Pattern.compile("""
                        [\\p{IsAlphabetic}\\p{Digit}_\\-]{1,127}
                      """.trim(), UNICODE_CHARACTER_CLASS);

  /**
   * A tag.
   *
   * @param id   The unique tag ID
   * @param name The tag name
   */

  public CATag
  {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(name, "name");

    if (!VALID_TAG_NAME.matcher(name).matches()) {
      throw new IllegalArgumentException(
        String.format("Tag name %s does not match %s", name, VALID_TAG_NAME)
      );
    }
  }

  @Override
  public int compareTo(final CATag other)
  {
    return Comparator.comparing(CATag::id)
      .thenComparing(CATag::name)
      .compare(this, other);
  }

  public String displayId()
  {
    return this.id.displayId();
  }
}
