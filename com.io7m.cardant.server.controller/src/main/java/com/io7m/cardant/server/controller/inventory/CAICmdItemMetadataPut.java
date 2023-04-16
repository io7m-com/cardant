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
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataPut;
import com.io7m.cardant.protocol.inventory.CAIResponseItemMetadataPut;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.security.CASecurityException;

import java.util.Map;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.server.controller.inventory.CAISecurityObjects.INVENTORY_ITEMS;
import static com.io7m.cardant.server.controller.inventory.CAISecurityObjects.WRITE;

/**
 * @see CAICommandItemMetadataPut
 */

public final class CAICmdItemMetadataPut
  extends CAICmdAbstract<CAICommandItemMetadataPut>
{
  /**
   * @see CAICommandItemMetadataPut
   */

  public CAICmdItemMetadataPut()
  {

  }

  @Override
  protected CAIResponseType executeActual(
    final CAICommandContext context,
    final CAICommandItemMetadataPut command)
    throws CASecurityException, CADatabaseException, CACommandExecutionFailure
  {
    context.securityCheck(INVENTORY_ITEMS, WRITE);

    final var queries =
      context.transaction()
        .queries(CADatabaseQueriesItemsType.class);

    final var metadatas = command.metadatas();
    final var itemId = command.item();
    for (final var metadata : metadatas) {
      queries.itemMetadataPut(itemId, metadata);
    }

    final var itemOpt = queries.itemGet(itemId);
    if (itemOpt.isEmpty()) {
      throw context.failFormatted(
        400,
        errorNonexistent(),
        Map.of("Item ID", itemId.displayId()),
        "notFound"
      );
    }

    return new CAIResponseItemMetadataPut(
      context.requestId(),
      itemOpt.get()
    );
  }
}
