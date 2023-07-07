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
import com.io7m.cardant.database.api.CADatabaseFileSearchType;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchNext;
import com.io7m.cardant.protocol.inventory.CAIResponseFileSearch;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.security.CASecurityException;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;

import java.util.Map;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorApiMisuse;
import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_FILES;
import static com.io7m.cardant.security.CASecurityPolicy.READ;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_SEARCH_NOT_ACTIVE;

/**
 * @see CAICommandFileSearchNext
 */

public final class CAICmdFileSearchNext
  extends CAICmdAbstract<CAICommandFileSearchNext>
{
  /**
   * @see CAICommandFileSearchNext
   */

  public CAICmdFileSearchNext()
  {

  }

  @Override
  protected CAIResponseType executeActual(
    final CAICommandContext context,
    final CAICommandFileSearchNext command)
    throws CASecurityException, CADatabaseException, CACommandExecutionFailure
  {
    context.securityCheck(INVENTORY_FILES, READ);

    final var search =
      context.session()
        .property(CADatabaseFileSearchType.class)
        .orElseThrow(() -> {
          return context.failFormatted(
            400,
            errorApiMisuse(),
            Map.of(),
            ERROR_SEARCH_NOT_ACTIVE
          );
        });

    return new CAIResponseFileSearch(
      context.requestId(),
      search.pageNext(context.transaction())
    );
  }
}
