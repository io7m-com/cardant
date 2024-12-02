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
import java.util.UUID;

/**
 * The unique ID of a serial stock instance. This is effectively a unique
 * identifier for a specific item.
 *
 * @param id The ID value
 */

public record CAStockInstanceID(UUID id)
  implements Comparable<CAStockInstanceID>, CAIdType
{
  /**
   * The unique ID of an file.
   *
   * @param id The ID value
   */

  public CAStockInstanceID
  {
    Objects.requireNonNull(id, "id");
  }

  @Override
  public String toString()
  {
    return this.id.toString();
  }

  /**
   * Construct an ID.
   *
   * @param id The ID value
   *
   * @return An ID
   */

  public static CAStockInstanceID of(
    final String id)
  {
    return new CAStockInstanceID(UUID.fromString(id));
  }

  /**
   * Construct an ID.
   *
   * @param id The ID value
   *
   * @return An ID
   */

  public static CAStockInstanceID of(
    final UUID id)
  {
    return new CAStockInstanceID(id);
  }

  /**
   * Construct an ID using a pseudorandom value.
   *
   * @return An ID
   */

  public static CAStockInstanceID random()
  {
    return new CAStockInstanceID(UUID.randomUUID());
  }

  @Override
  public int compareTo(
    final CAStockInstanceID other)
  {
    return this.id.compareTo(other.id);
  }
}