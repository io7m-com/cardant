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
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationMetadataPutType.Parameters;
import com.io7m.cardant.database.api.CADatabaseTypePackageResolver;
import com.io7m.cardant.protocol.inventory.CAICommandLocationMetadataPut;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationMetadataPut;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.security.CASecurityException;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.type_packages.compiler.api.CATypePackageCompilerFactoryType;

import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_LOCATIONS;
import static com.io7m.cardant.security.CASecurityPolicy.WRITE;
import static com.io7m.cardant.strings.CAStringConstants.ITEM_ID;

/**
 * @see CAICommandLocationMetadataPut
 */

public final class CAICmdLocationMetadataPut
  extends CAICmdAbstract<CAICommandLocationMetadataPut>
{
  /**
   * @see CAICommandLocationMetadataPut
   */

  public CAICmdLocationMetadataPut()
  {

  }

  @Override
  protected CAIResponseType executeActual(
    final CAICommandContext context,
    final CAICommandLocationMetadataPut command)
    throws CASecurityException, CADatabaseException, CACommandExecutionFailure
  {
    context.securityCheck(INVENTORY_LOCATIONS, WRITE);

    final var services =
      context.services();
    final var compilers =
      services.requireService(CATypePackageCompilerFactoryType.class);

    final var transaction = context.transaction();
    transaction.setUserId(context.session().userId());

    final var metaPut =
      transaction.queries(CADatabaseQueriesLocationsType.LocationMetadataPutType.class);
    final var get =
      transaction.queries(CADatabaseQueriesLocationsType.LocationGetType.class);

    final var locationId = command.location();
    context.setAttribute(ITEM_ID, locationId.displayId());

    final var metadatas = command.metadatas();
    metaPut.execute(new Parameters(locationId, metadatas));

    final var resolver =
      CADatabaseTypePackageResolver.create(compilers, transaction);

    final var location = get.execute(locationId).orElseThrow();
    CAITypeChecking.checkTypes(context, resolver, location);
    return new CAIResponseLocationMetadataPut(context.requestId(), location);
  }
}
