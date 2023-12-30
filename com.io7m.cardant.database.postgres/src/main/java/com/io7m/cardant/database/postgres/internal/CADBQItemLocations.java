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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemLocationsType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemLocations;
import org.jooq.DSLContext;

import java.util.Map;
import java.util.Optional;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorIo;

/**
 * Retrieve the locations of the given item.
 */

public final class CADBQItemLocations
  extends CADBQAbstract<CAItemID, CAItemLocations>
  implements ItemLocationsType
{
  private static final Service<CAItemID, CAItemLocations, ItemLocationsType> SERVICE =
    new Service<>(ItemLocationsType.class, CADBQItemLocations::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemLocations(
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
  protected CAItemLocations onExecute(
    final DSLContext context,
    final CAItemID itemID)
    throws CADatabaseException
  {
    throw new CADatabaseException(
      "Unimplemented kind.",
      errorIo(),
      Map.of(),
      Optional.empty()
    );
  }
}
