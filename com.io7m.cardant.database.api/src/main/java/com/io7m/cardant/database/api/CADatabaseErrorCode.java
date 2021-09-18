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

/**
 * A database error code.
 */

public enum CADatabaseErrorCode
{
  /**
   * An attempt was made to perform an operation with invalid parameters. That
   * is, the fault lies on behalf of the caller, not the database.
   */

  ERROR_PARAMETERS_INVALID,

  /**
   * An attempt was made to introduce an object that would violate a uniqueness
   * constraint.
   */

  ERROR_DUPLICATE,

  /**
   * An attempt was made to remove a required object (and perhaps violate
   * a foreign key constraint).
   */

  ERROR_NONEXISTENT,

  /**
   * An attempt was made to add or modify an object in a manner that
   * would introduce a graph cycle.
   */

  ERROR_CYCLIC,

  /**
   * A general database error occurred.
   */

  ERROR_GENERAL
}
