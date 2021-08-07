/*
 * Copyright © 2019 Mark Raynsford <code@io7m.com> http://io7m.com
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

package com.io7m.cardant.database.derby.internal;

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.jxtrand.vanilla.JXTAbstractStrings;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;

import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_GENERAL;

/**
 * Database strings.
 */

public final class CADatabaseMessages extends JXTAbstractStrings
{
  /**
   * Database strings.
   *
   * @param locale The locale
   *
   * @throws IOException On I/O errors
   */

  public CADatabaseMessages(
    final Locale locale)
    throws IOException
  {
    super(
      locale,
      CADatabaseMessages.class,
      "/com/io7m/cardant/database/derby/internal",
      "Messages"
    );
  }

  /**
   * @param resourceId The given resource ID
   * @param e          The SQL exception
   *
   * @return A database exception
   */

  public CADatabaseException ofSQLException(
    final String resourceId,
    final SQLException e)
  {
    return new CADatabaseException(
      ERROR_GENERAL,
      this.format(resourceId, e.getLocalizedMessage()),
      e
    );
  }
}
