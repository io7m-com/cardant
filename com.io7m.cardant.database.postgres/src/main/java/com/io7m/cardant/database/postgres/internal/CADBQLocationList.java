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

import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationListType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationPath;
import com.io7m.cardant.model.CALocationSummary;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.impl.DSL;

import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.io7m.cardant.database.postgres.internal.CADBLocationPaths.LOCATION_PATH_NAME;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATIONS;

/**
 * List locations.
 */

public final class CADBQLocationList
  extends CADBQAbstract<LocationListType.Parameters, SortedMap<CALocationID, CALocationSummary>>
  implements LocationListType
{
  private static final Service<Parameters, SortedMap<CALocationID, CALocationSummary>, LocationListType> SERVICE =
    new Service<>(LocationListType.class, CADBQLocationList::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQLocationList(
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
  protected SortedMap<CALocationID, CALocationSummary> onExecute(
    final DSLContext context,
    final Parameters parameters)
  {
    return list(context, parameters);
  }

  static TreeMap<CALocationID, CALocationSummary> list(
    final DSLContext context,
    final Parameters parameters)
  {
    final var conditions =
      switch (parameters.includeDeleted()) {
        case INCLUDE_ONLY_LIVE -> LOCATIONS.LOCATION_DELETED.isNull();
        case INCLUDE_ONLY_DELETED -> LOCATIONS.LOCATION_DELETED.isNotNull();
        case INCLUDE_BOTH_LIVE_AND_DELETED -> DSL.trueCondition();
      };

    return new TreeMap<>(
      context.select(
          LOCATIONS.LOCATION_ID,
          LOCATIONS.LOCATION_PARENT,
          CADBLocationPaths.locationPathFromColumnNamed(context, LOCATIONS.LOCATION_ID)
        ).from(LOCATIONS)
        .where(conditions)
        .orderBy(LOCATIONS.LOCATION_NAME)
        .stream()
        .map(CADBQLocationList::mapRecord)
        .collect(Collectors.toMap(CALocationSummary::id, s -> s))
    );
  }

  private static CALocationSummary mapRecord(
    final Record3<UUID, UUID, String[]> rec)
  {
    return new CALocationSummary(
      new CALocationID(rec.get(LOCATIONS.LOCATION_ID)),
      Optional.ofNullable(rec.get(LOCATIONS.LOCATION_PARENT))
        .map(CALocationID::new),
      CALocationPath.ofArray(rec.get(LOCATION_PATH_NAME))
    );
  }
}
