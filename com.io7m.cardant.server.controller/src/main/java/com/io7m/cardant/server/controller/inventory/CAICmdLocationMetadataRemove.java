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
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationGetType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationMetadataRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationMetadataRemoveType.Parameters;
import com.io7m.cardant.database.api.CADatabaseTypePackageResolver;
import com.io7m.cardant.protocol.inventory.CAICommandLocationMetadataRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationMetadataRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.security.CASecurityException;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.type_packages.compiler.api.CATypePackageCompilerFactoryType;

import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_LOCATIONS;
import static com.io7m.cardant.security.CASecurityPolicy.WRITE;
import static com.io7m.cardant.strings.CAStringConstants.LOCATION_ID;

/**
 * @see CAICommandLocationMetadataRemove
 */

public final class CAICmdLocationMetadataRemove
  extends CAICmdAbstract<CAICommandLocationMetadataRemove>
{
  /**
   * @see CAICommandLocationMetadataRemove
   */

  public CAICmdLocationMetadataRemove()
  {

  }

  @Override
  protected CAIResponseType executeActual(
    final CAICommandContext context,
    final CAICommandLocationMetadataRemove command)
    throws CASecurityException, CADatabaseException, CACommandExecutionFailure
  {
    context.securityCheck(INVENTORY_LOCATIONS, WRITE);

    final var services =
      context.services();
    final var compilers =
      services.requireService(CATypePackageCompilerFactoryType.class);

    final var transaction =
      context.transaction();
    final var metaRemove =
      transaction.queries(LocationMetadataRemoveType.class);
    final var get =
      transaction.queries(LocationGetType.class);

    final var metadatas = command.metadataNames();

    final var locationID = command.location();
    context.setAttribute(LOCATION_ID, locationID.displayId());
    CAIChecks.checkLocationExists(context, get, locationID);

    metaRemove.execute(new Parameters(locationID, metadatas));

    final var resolver =
      CADatabaseTypePackageResolver.create(compilers, transaction);

    final var location = get.execute(locationID).orElseThrow();
    CAITypeChecking.checkTypes(context, resolver, location);
    return new CAIResponseLocationMetadataRemove(context.requestId(), location);
  }
}
