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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType;
import com.io7m.cardant.protocol.inventory.CAICommandItemLocationsList;
import com.io7m.cardant.protocol.inventory.CAIResponseItemLocationsList;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.security.CASecurityException;

import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_ITEMS;
import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_LOCATIONS;
import static com.io7m.cardant.security.CASecurityPolicy.READ;

/**
 * @see CAICommandItemLocationsList
 */

public final class CAICmdItemLocationsList
  extends CAICmdAbstract<CAICommandItemLocationsList>
{
  /**
   * @see CAICommandItemLocationsList
   */

  public CAICmdItemLocationsList()
  {

  }

  @Override
  protected CAIResponseType executeActual(
    final CAICommandContext context,
    final CAICommandItemLocationsList command)
    throws CASecurityException, CADatabaseException
  {
    context.securityCheck(INVENTORY_ITEMS, READ);
    context.securityCheck(INVENTORY_LOCATIONS, READ);

    final var queries =
      context.transaction()
        .queries(CADatabaseQueriesItemsType.class);

    final var locations = queries.itemLocations(command.item());
    return new CAIResponseItemLocationsList(
      context.requestId(), locations
    );
  }
}