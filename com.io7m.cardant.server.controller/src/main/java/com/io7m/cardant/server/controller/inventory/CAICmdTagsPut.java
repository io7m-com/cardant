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
import com.io7m.cardant.database.api.CADatabaseQueriesTagsType;
import com.io7m.cardant.protocol.inventory.CAICommandTagsPut;
import com.io7m.cardant.protocol.inventory.CAIResponseTagsPut;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.security.CASecurityException;

import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_TAGS;
import static com.io7m.cardant.security.CASecurityPolicy.WRITE;

/**
 * @see CAICommandTagsPut
 */

public final class CAICmdTagsPut extends CAICmdAbstract<CAICommandTagsPut>
{
  /**
   * @see CAICommandTagsPut
   */

  public CAICmdTagsPut()
  {

  }

  @Override
  protected CAIResponseType executeActual(
    final CAICommandContext context,
    final CAICommandTagsPut command)
    throws CASecurityException, CADatabaseException
  {
    context.securityCheck(INVENTORY_TAGS, WRITE);

    final var queries =
      context.transaction()
        .queries(CADatabaseQueriesTagsType.class);

    final var tags = command.tags();
    for (final var tag : tags.tags()) {
      queries.tagPut(tag);
    }

    return new CAIResponseTagsPut(context.requestId(), tags);
  }
}
