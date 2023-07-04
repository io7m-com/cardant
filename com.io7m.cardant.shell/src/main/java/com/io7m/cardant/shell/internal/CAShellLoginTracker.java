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


package com.io7m.cardant.shell.internal;

import com.io7m.repetoir.core.RPServiceType;

import java.util.Optional;
import java.util.UUID;

/**
 * A tracker for login details.
 */

public final class CAShellLoginTracker
  implements RPServiceType
{
  private Optional<UUID> userId;

  /**
   * A tracker for login details.
   */

  public CAShellLoginTracker()
  {
    this.userId = Optional.empty();
  }

  /**
   * Set the current user ID.
   *
   * @param newUserId The new user ID
   */

  public void setUserId(
    final UUID newUserId)
  {
    this.userId = Optional.of(newUserId);
  }

  /**
   * Clear the current user ID.
   */

  public void clearUserId()
  {
    this.userId = Optional.empty();
  }

  /**
   * @return The current user ID
   */

  public Optional<UUID> userId()
  {
    return this.userId;
  }

  @Override
  public String description()
  {
    return "The user ID login tracker service.";
  }
}
