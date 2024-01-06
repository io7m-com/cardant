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

package com.io7m.cardant.server.controller.inventory;

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationAttachmentAddType.Parameters;
import com.io7m.cardant.protocol.inventory.CAICommandLocationAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.security.CASecurityException;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;

import java.util.Map;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_LOCATIONS;
import static com.io7m.cardant.security.CASecurityPolicy.WRITE;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_NONEXISTENT;
import static com.io7m.cardant.strings.CAStringConstants.LOCATION_ID;

/**
 * @see CAICommandLocationAttachmentAdd
 */

public final class CAICmdLocationAttachmentAdd
  extends CAICmdAbstract<CAICommandLocationAttachmentAdd>
{
  /**
   * @see CAICommandLocationAttachmentAdd
   */

  public CAICmdLocationAttachmentAdd()
  {

  }

  @Override
  protected CAIResponseType executeActual(
    final CAICommandContext context,
    final CAICommandLocationAttachmentAdd command)
    throws CASecurityException, CADatabaseException, CACommandExecutionFailure
  {
    context.securityCheck(INVENTORY_LOCATIONS, WRITE);

    final var transaction = context.transaction();
    transaction.setUserId(context.session().userId());

    final var attachmentAdd =
      transaction.queries(CADatabaseQueriesLocationsType.LocationAttachmentAddType.class);
    final var get =
      transaction.queries(CADatabaseQueriesLocationsType.LocationGetType.class);

    final var locationID = command.location();
    attachmentAdd.execute(
      new Parameters(locationID, command.file(), command.relation())
    );

    final var locationOpt = get.execute(locationID);
    if (locationOpt.isEmpty()) {
      throw context.failFormatted(
        400,
        errorNonexistent(),
        Map.of(LOCATION_ID, locationID.displayId()),
        ERROR_NONEXISTENT
      );
    }

    return new CAIResponseLocationAttachmentAdd(
      context.requestId(),
      locationOpt.get()
    );
  }
}
