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


package com.io7m.cardant.database.postgres.internal;

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationTypesRevokeType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationTypesRevokeType.Parameters;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Map;

import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd.auditEvent;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATIONS;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATION_TYPES;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_PACKAGES;
import static com.io7m.cardant.strings.CAStringConstants.LOCATION_ID;

/**
 * Revoke types for the given location.
 */

public final class CADBQLocationTypesRevoke
  extends CADBQAbstract<Parameters, CADatabaseUnit>
  implements LocationTypesRevokeType
{
  private static final Service<Parameters, CADatabaseUnit, LocationTypesRevokeType> SERVICE =
    new Service<>(LocationTypesRevokeType.class, CADBQLocationTypesRevoke::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQLocationTypesRevoke(
    final CADatabaseTransaction transaction)
  {
    super(transaction);
  }

  /**
   * @return A query provider
   */

  public static CADBQueryProviderType provider()
  {
    return () -> SERVICE;
  }

  @Override
  protected CADatabaseUnit onExecute(
    final DSLContext context,
    final Parameters parameters)
    throws CADatabaseException
  {
    final var locationID = parameters.location();
    this.setAttribute(LOCATION_ID, locationID.displayId());

    final var batches =
      new ArrayList<Query>(parameters.types().size());

    for (final var type : parameters.types()) {
      final var matches =
        DSL.and(
          METADATA_TYPES.MT_NAME.eq(type.typeName().value()),
          METADATA_TYPE_PACKAGES.MTP_NAME.eq(type.packageName().value())
        );

      final var selectType =
        context.select(METADATA_TYPES.MT_ID)
          .from(METADATA_TYPES)
          .join(METADATA_TYPE_PACKAGES)
          .on(METADATA_TYPE_PACKAGES.MTP_ID.eq(METADATA_TYPES.MT_PACKAGE))
          .where(matches);

      final var query =
        context.deleteFrom(LOCATION_TYPES)
          .where(LOCATION_TYPES.LT_LOCATION.eq(locationID.id())
                   .and(LOCATION_TYPES.LT_TYPE.eq(selectType)));

      batches.add(query);
    }

    batches.add(
      context.update(LOCATIONS)
        .set(LOCATIONS.LOCATION_UPDATED, this.now())
        .where(LOCATIONS.LOCATION_ID.eq(locationID.id()))
    );

    final var transaction = this.transaction();
    batches.add(
      auditEvent(
        context,
        OffsetDateTime.now(transaction.clock()),
        transaction.userId(),
        "LOCATION_UPDATED",
        Map.entry("Location", locationID.toString())
      )
    );

    context.batch(batches).execute();
    return CADatabaseUnit.UNIT;
  }
}
