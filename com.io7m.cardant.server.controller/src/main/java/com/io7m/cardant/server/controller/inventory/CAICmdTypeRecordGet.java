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
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeRecordGetType;
import com.io7m.cardant.protocol.inventory.CAICommandTypeRecordGet;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeRecordGet;
import com.io7m.cardant.security.CASecurityException;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;

import java.util.Map;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_ITEMS;
import static com.io7m.cardant.security.CASecurityPolicy.READ;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_NONEXISTENT;
import static com.io7m.cardant.strings.CAStringConstants.TYPE;

/**
 * @see CAICommandTypeRecordGet
 */

public final class CAICmdTypeRecordGet
  extends CAICmdAbstract<CAICommandTypeRecordGet>
{
  /**
   * @see CAICommandTypeRecordGet
   */

  public CAICmdTypeRecordGet()
  {

  }

  @Override
  protected CAIResponseType executeActual(
    final CAICommandContext context,
    final CAICommandTypeRecordGet command)
    throws CASecurityException, CACommandExecutionFailure, CADatabaseException
  {
    context.securityCheck(INVENTORY_ITEMS, READ);

    final var get =
      context.transaction()
        .queries(TypeRecordGetType.class);

    final var result =
      get.execute(command.name());

    if (result.isEmpty()) {
      throw context.failFormatted(
        400,
        errorNonexistent(),
        Map.of(TYPE, command.name().value()),
        ERROR_NONEXISTENT
      );
    }

    return new CAIResponseTypeRecordGet(context.requestId(), result.get());
  }
}
