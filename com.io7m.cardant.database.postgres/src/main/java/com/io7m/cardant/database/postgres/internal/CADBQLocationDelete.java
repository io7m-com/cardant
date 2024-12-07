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
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationDeleteType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CALocationID;
import org.jooq.DSLContext;
import org.jooq.Query;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd.auditEvent;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATIONS;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATION_ATTACHMENTS;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATION_METADATA;

/**
 * Delete the given locations.
 */

public final class CADBQLocationDelete
  extends CADBQAbstract<Collection<CALocationID>, CADatabaseUnit>
  implements LocationDeleteType
{
  private static final Service<Collection<CALocationID>, CADatabaseUnit, LocationDeleteType> SERVICE =
    new Service<>(LocationDeleteType.class, CADBQLocationDelete::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQLocationDelete(
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
    final Collection<CALocationID> locations)
    throws CADatabaseException
  {
    final var transaction = this.transaction();
    final var deletes = new ArrayList<Query>(locations.size());
    for (final var location : locations) {
      deletes.add(
        context.deleteFrom(LOCATION_ATTACHMENTS)
          .where(LOCATION_ATTACHMENTS.LA_LOCATION_ID.eq(location.id()))
      );
      deletes.add(
        context.deleteFrom(LOCATION_METADATA)
          .where(LOCATION_METADATA.LOCATION_META_LOCATION.eq(location.id()))
      );
      deletes.add(
        context.deleteFrom(LOCATIONS)
          .where(LOCATIONS.LOCATION_ID.eq(location.id()))
      );
      deletes.add(
        auditEvent(
          context,
          OffsetDateTime.now(transaction.clock()),
          transaction.userId(),
          "LOCATION_DELETED",
          Map.entry("Location", location.displayId())
        )
      );
    }

    context.batch(deletes).execute();
    return CADatabaseUnit.UNIT;
  }
}
