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
 * An operation that moves stock from one storage location to another.
 *
 * @param instanceSource The existing stock instance
 * @param instanceTarget The target stock instance
 * @param toLocation     The target storage location
 * @param count          The item count
 */

public record CAStockRepositSetMove(
  CAStockInstanceID instanceSource,
  CAStockInstanceID instanceTarget,
  CALocationID toLocation,
  long count)
  implements CAStockRepositType
{
  /**
   * An operation that moves stock from one storage location to another.
   *
   * @param instanceSource The existing stock instance
   * @param instanceTarget The target stock instance
   * @param toLocation     The target storage location
   * @param count          The item count
   */

  public CAStockRepositSetMove
  {
    Objects.requireNonNull(instanceSource, "instanceSource");
    Objects.requireNonNull(instanceTarget, "instanceTarget");
    Objects.requireNonNull(toLocation, "toLocation");
  }

  @Override
  public CAStockInstanceID instance()
  {
    return this.instanceTarget;
  }
}
