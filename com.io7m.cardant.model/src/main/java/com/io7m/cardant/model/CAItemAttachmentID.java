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
 * The unique ID of an attachment.
 *
 * @param id The ID value
 */

public record CAItemAttachmentID(UUID id)
  implements Comparable<CAItemAttachmentID>
{
  /**
   * The unique ID of an attachment.
   *
   * @param id The ID value
   */

  public CAItemAttachmentID
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

  public static CAItemAttachmentID of(
    final String id)
  {
    return new CAItemAttachmentID(UUID.fromString(id));
  }

  /**
   * Construct an ID.
   *
   * @param id The ID value
   *
   * @return An ID
   */

  public static CAItemAttachmentID of(
    final UUID id)
  {
    return new CAItemAttachmentID(id);
  }

  /**
   * Construct an ID using a pseudorandom value.
   *
   * @return An ID
   */

  public static CAItemAttachmentID random()
  {
    return new CAItemAttachmentID(UUID.randomUUID());
  }

  @Override
  public int compareTo(
    final CAItemAttachmentID other)
  {
    return this.id.compareTo(other.id);
  }
}
