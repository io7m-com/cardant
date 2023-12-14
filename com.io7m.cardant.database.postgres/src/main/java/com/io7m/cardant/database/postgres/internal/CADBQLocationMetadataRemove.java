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
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.MetadataRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.MetadataRemoveType.Parameters;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import org.jooq.DSLContext;
import org.jooq.Query;

import java.util.ArrayList;

import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATION_METADATA;
import static com.io7m.cardant.strings.CAStringConstants.LOCATION_ID;

/**
 * Remove metadata from a location.
 */

public final class CADBQLocationMetadataRemove
  extends CADBQAbstract<Parameters, CADatabaseUnit>
  implements MetadataRemoveType
{
  private static final Service<Parameters, CADatabaseUnit, MetadataRemoveType> SERVICE =
    new Service<>(MetadataRemoveType.class, CADBQLocationMetadataRemove::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQLocationMetadataRemove(
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

    this.setAttribute(LOCATION_ID, location.displayId());

    final var queries = new ArrayList<Query>();
    for (final var name : parameters.names()) {
      final var matchesLocation =
        LOCATION_METADATA.LOCATION_META_LOCATION.eq(location.id());
      final var matchesName =
        LOCATION_METADATA.LOCATION_META_NAME.eq(name.value());
      final var matches =
        matchesLocation.and(matchesName);

      queries.add(context.deleteFrom(LOCATION_METADATA).where(matches));
    }

    context.batch(queries).execute();
    return UNIT;
  }
}
