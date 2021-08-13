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

package com.io7m.cardant.database.derby.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DriverManager;
import java.sql.SQLException;

public final class CADerbyItemCounts
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CADerbyItemCounts.class);

  private CADerbyItemCounts()
  {

  }

  private static final String ITEMS_GET = """
    SELECT
      item_location_item_id,
      item_location_count
    FROM new_item_locations
    """;

  public static void checkUpdatedItemCounts()
    throws SQLException
  {
    LOG.debug("enforcing item counts");

    try (var conn = DriverManager.getConnection("jdbc:default:connection")) {
      conn.setAutoCommit(false);

      try (var idStatement = conn.prepareStatement(ITEMS_GET)) {
        try (var results = idStatement.executeQuery()) {
          while (results.next()) {

          }
        }
      }
    }
  }
}
