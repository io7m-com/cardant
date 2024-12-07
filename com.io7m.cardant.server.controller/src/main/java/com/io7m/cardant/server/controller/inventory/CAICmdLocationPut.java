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
import com.io7m.cardant.protocol.inventory.CAICommandLocationPut;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationPut;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.security.CASecurityException;

import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_LOCATIONS;
import static com.io7m.cardant.security.CASecurityPolicy.WRITE;

/**
 * @see CAICommandLocationPut
 */

public final class CAICmdLocationPut extends CAICmdAbstract<CAICommandLocationPut>
{
  /**
   * @see CAICommandLocationPut
   */

  public CAICmdLocationPut()
  {

  }

  @Override
  protected CAIResponseType executeActual(
    final CAICommandContext context,
    final CAICommandLocationPut command)
    throws CASecurityException, CADatabaseException
  {
    context.securityCheck(INVENTORY_LOCATIONS, WRITE);

    final var transaction =
      context.transaction();
    final var put =
      transaction.queries(CADatabaseQueriesLocationsType.LocationPutType.class);

    put.execute(command.location());
    return new CAIResponseLocationPut(context.requestId(), command.location());
  }
}
