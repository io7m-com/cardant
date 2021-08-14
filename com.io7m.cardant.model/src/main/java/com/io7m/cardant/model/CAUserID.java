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
import java.util.UUID;

/**
 * The unique ID of a user.
 *
 * @param id The ID value
 */

public record CAUserID(UUID id)
  implements Comparable<CAUserID>, CAIdType
{
  /**
   * The unique ID of a user.
   *
   * @param id The ID value
   */

  public CAUserID
  {
    Objects.requireNonNull(id, "id");
  }

  /**
   * Construct an ID.
   *
   * @param id The ID value
   *
   * @return An ID
   */

  public static CAUserID of(
    final String id)
  {
    return new CAUserID(UUID.fromString(id));
  }

  /**
   * Construct an ID.
   *
   * @param id The ID value
   *
   * @return An ID
   */

  public static CAUserID of(
    final UUID id)
  {
    return new CAUserID(id);
  }

  /**
   * Construct an ID using a pseudorandom value.
   *
   * @return An ID
   */

  public static CAUserID random()
  {
    return new CAUserID(UUID.randomUUID());
  }

  @Override
  public int compareTo(
    final CAUserID other)
  {
    return this.id.compareTo(other.id);
  }
}
