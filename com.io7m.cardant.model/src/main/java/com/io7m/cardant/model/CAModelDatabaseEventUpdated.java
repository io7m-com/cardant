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
import java.util.Set;

/**
 * Data in the model database was updated.
 *
 * @param updated The objects that were updated
 * @param removed The items that were removed
 */

public record CAModelDatabaseEventUpdated(
  Set<CAIdType> updated,
  Set<CAIdType> removed)
  implements CAModelDatabaseEventType
{
  /**
   * Data in the model database was updated.
   *
   * @param updated The objects that were updated
   * @param removed The items that were removed
   */

  public CAModelDatabaseEventUpdated
  {
    Objects.requireNonNull(updated, "updated");
    Objects.requireNonNull(removed, "removed");
  }
}
