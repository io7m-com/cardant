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

import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.ListType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import org.jooq.DSLContext;

import java.util.SortedMap;

import static com.io7m.cardant.database.postgres.internal.CADBQLocationPut.locationListInner;

/**
 * List locations.
 */

public final class CADBQLocationList
  extends CADBQAbstract<CADatabaseUnit, SortedMap<CALocationID, CALocation>>
  implements ListType
{
  private static final Service<CADatabaseUnit, SortedMap<CALocationID, CALocation>, ListType> SERVICE =
    new Service<>(ListType.class, CADBQLocationList::new);

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
  protected SortedMap<CALocationID, CALocation> onExecute(
    final DSLContext context,
    final CADatabaseUnit parameters)
  {
    return locationListInner(context);
  }
}
