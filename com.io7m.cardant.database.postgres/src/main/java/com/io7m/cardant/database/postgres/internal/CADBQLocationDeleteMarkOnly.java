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
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationDeleteMarkOnlyType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationDeleteMarkOnlyType.Parameters;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import org.jooq.DSLContext;
import org.jooq.Query;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Map;

import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd.auditEvent;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATIONS;

/**
 * Mark the given locations as deleted.
 */

public final class CADBQLocationDeleteMarkOnly
  extends CADBQAbstract<Parameters, CADatabaseUnit>
  implements LocationDeleteMarkOnlyType
{
  private static final Service<
    Parameters,
    CADatabaseUnit,
    LocationDeleteMarkOnlyType> SERVICE =
    new Service<>(
      LocationDeleteMarkOnlyType.class,
      CADBQLocationDeleteMarkOnly::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQLocationDeleteMarkOnly(
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
    final var transaction = this.transaction();
    final var updates =
      new ArrayList<Query>(parameters.locations().size());

    final OffsetDateTime deleted;
    if (parameters.deleted()) {
      deleted = OffsetDateTime.now();
    } else {
      deleted = null;
    }

    for (final var location : parameters.locations()) {
      updates.add(
        context.update(LOCATIONS)
          .set(LOCATIONS.LOCATION_DELETED, deleted)
          .where(LOCATIONS.LOCATION_ID.eq(location.id()))
      );
      updates.add(
        auditEvent(
          context,
          OffsetDateTime.now(transaction.clock()),
          transaction.userId(),
          parameters.deleted() ? "LOCATION_MARKED_DELETED" : "LOCATION_UNMARKED_DELETED",
          Map.entry("Location", location.displayId())
        )
      );
    }
    context.batch(updates).execute();
    return CADatabaseUnit.UNIT;
  }
}
