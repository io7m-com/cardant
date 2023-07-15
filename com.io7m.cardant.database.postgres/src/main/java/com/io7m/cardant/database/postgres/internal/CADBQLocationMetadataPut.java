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
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.MetadataPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.MetadataPutType.Parameters;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import org.jooq.DSLContext;
import org.jooq.Query;

import java.util.ArrayList;

import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATION_METADATA;
import static com.io7m.cardant.strings.CAStringConstants.LOCATION_ID;

/**
 * Add or update metadata for a location.
 */

public final class CADBQLocationMetadataPut
  extends CADBQAbstract<Parameters, CADatabaseUnit>
  implements MetadataPutType
{
  private static final Service<Parameters, CADatabaseUnit, MetadataPutType> SERVICE =
    new Service<>(MetadataPutType.class, CADBQLocationMetadataPut::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQLocationMetadataPut(
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
    final var location =
      parameters.location();
    final var metadata =
      parameters.metadata();

    this.setAttribute(LOCATION_ID, location.displayId());

    final var batches = new ArrayList<Query>();
    for (final var meta : metadata) {
      batches.add(
        context.insertInto(LOCATION_METADATA)
          .set(LOCATION_METADATA.METADATA_LOCATION_ID, location.id())
          .set(LOCATION_METADATA.METADATA_NAME, meta.name().value())
          .set(LOCATION_METADATA.METADATA_VALUE, meta.value())
          .onConflict(LOCATION_METADATA.METADATA_LOCATION_ID, LOCATION_METADATA.METADATA_NAME)
          .doUpdate()
          .set(LOCATION_METADATA.METADATA_VALUE, meta.value())
      );
    }

    context.batch(batches).execute();
    return UNIT;
  }
}
