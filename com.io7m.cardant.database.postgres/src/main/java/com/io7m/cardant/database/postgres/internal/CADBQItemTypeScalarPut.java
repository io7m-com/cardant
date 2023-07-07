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
import com.io7m.cardant.database.api.CADatabaseQueriesItemTypesType.TypeScalarPutType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CATypeScalar;
import org.jooq.DSLContext;

import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_SCALAR_TYPES;

/**
 * Create or update a scalar type declaration.
 */

public final class CADBQItemTypeScalarPut
  extends CADBQAbstract<CATypeScalar, CADatabaseUnit>
  implements TypeScalarPutType
{
  private static final Service<CATypeScalar, CADatabaseUnit, TypeScalarPutType> SERVICE =
    new Service<>(TypeScalarPutType.class, CADBQItemTypeScalarPut::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemTypeScalarPut(
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
    final CATypeScalar scalar)
    throws CADatabaseException
  {
    context.insertInto(METADATA_SCALAR_TYPES)
      .set(METADATA_SCALAR_TYPES.NAME, scalar.name().value())
      .set(METADATA_SCALAR_TYPES.DESCRIPTION, scalar.description())
      .set(METADATA_SCALAR_TYPES.PATTERN, scalar.pattern())
      .onConflict(METADATA_SCALAR_TYPES.NAME)
      .doUpdate()
      .set(METADATA_SCALAR_TYPES.NAME, scalar.name().value())
      .set(METADATA_SCALAR_TYPES.DESCRIPTION, scalar.description())
      .set(METADATA_SCALAR_TYPES.PATTERN, scalar.pattern())
      .execute();

    return CADatabaseUnit.UNIT;
  }
}
