/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
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


package com.io7m.cardant.server.controller.inventory;

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;

import java.util.Map;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_NONEXISTENT;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_ID;
import static com.io7m.cardant.strings.CAStringConstants.LOCATION_ID;

final class CAIChecks
{
  private CAIChecks()
  {

  }

  static void checkItemExists(
    final CAICommandContext context,
    final CADatabaseQueriesItemsType.ItemGetType get,
    final CAItemID itemId)
    throws CACommandExecutionFailure, CADatabaseException
  {
    get.execute(itemId)
      .orElseThrow(() -> {
        return context.failFormatted(
          400,
          errorNonexistent(),
          Map.of(ITEM_ID, itemId.displayId()),
          ERROR_NONEXISTENT
        );
      });
  }

  static void checkLocationExists(
    final CAICommandContext context,
    final CADatabaseQueriesLocationsType.LocationGetType get,
    final CALocationID locationID)
    throws CACommandExecutionFailure, CADatabaseException
  {
    get.execute(locationID)
      .orElseThrow(() -> {
        return context.failFormatted(
          400,
          errorNonexistent(),
          Map.of(LOCATION_ID, locationID.displayId()),
          ERROR_NONEXISTENT
        );
      });
  }
}
