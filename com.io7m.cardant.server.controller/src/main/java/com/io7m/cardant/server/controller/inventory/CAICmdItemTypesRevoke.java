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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemTypesRevokeType.Parameters;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeDeclarationGetMultipleType;
import com.io7m.cardant.protocol.inventory.CAICommandItemTypesRevoke;
import com.io7m.cardant.protocol.inventory.CAIResponseItemTypesRevoke;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.security.CASecurityException;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;

import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_ITEMS;
import static com.io7m.cardant.security.CASecurityPolicy.WRITE;

/**
 * @see CAICommandItemTypesRevoke
 */

public final class CAICmdItemTypesRevoke extends CAICmdAbstract<CAICommandItemTypesRevoke>
{
  /**
   * @see CAICommandItemTypesRevoke
   */

  public CAICmdItemTypesRevoke()
  {

  }

  @Override
  protected CAIResponseType executeActual(
    final CAICommandContext context,
    final CAICommandItemTypesRevoke command)
    throws CASecurityException, CADatabaseException, CACommandExecutionFailure
  {
    context.securityCheck(INVENTORY_ITEMS, WRITE);

    final var transaction =
      context.transaction();
    final var get =
      transaction.queries(CADatabaseQueriesItemsType.ItemGetType.class);
    final var revoke =
      transaction.queries(CADatabaseQueriesItemsType.ItemTypesRevokeType.class);
    final var typeMultiGet =
      transaction.queries(TypeDeclarationGetMultipleType.class);

    revoke.execute(new Parameters(command.item(), command.types()));

    final var item = get.execute(command.item()).orElseThrow();
    CAITypeChecking.checkTypes(context, typeMultiGet, item);
    return new CAIResponseItemTypesRevoke(context.requestId(), item);
  }
}
