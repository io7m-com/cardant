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
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationDeleteMarkOnlyType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationDeleteMarkOnlyType.Parameters;
import com.io7m.cardant.protocol.inventory.CAICommandLocationDelete;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationDelete;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.security.CASecurityException;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;

import java.util.Set;

import static com.io7m.cardant.security.CASecurityPolicy.DELETE;
import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_LOCATIONS;

/**
 * @see CAICommandLocationDelete
 */

public final class CAICmdLocationDelete extends CAICmdAbstract<CAICommandLocationDelete>
{
  /**
   * @see CAICommandLocationDelete
   */

  public CAICmdLocationDelete()
  {

  }

  @Override
  protected CAIResponseType executeActual(
    final CAICommandContext context,
    final CAICommandLocationDelete command)
    throws CASecurityException, CADatabaseException, CACommandExecutionFailure
  {
    context.securityCheck(INVENTORY_LOCATIONS, DELETE);

    final var remove =
      context.transaction().queries(LocationDeleteMarkOnlyType.class);

    remove.execute(new Parameters(Set.of(command.data()), true));
    return new CAIResponseLocationDelete(context.requestId(), command.data());
  }
}
