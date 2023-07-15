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
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeScalarRemoveType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.lanark.core.RDottedName;
import org.jooq.DSLContext;

import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_SCALAR_TYPES;

/**
 * Remove a scalar type declaration. Fails if any type declarations still
 * refer to this type.
 */

public final class CADBQTypeScalarRemove
  extends CADBQAbstract<RDottedName, CADatabaseUnit>
  implements TypeScalarRemoveType
{
  private static final Service<RDottedName, CADatabaseUnit, TypeScalarRemoveType> SERVICE =
    new Service<>(TypeScalarRemoveType.class, CADBQTypeScalarRemove::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQTypeScalarRemove(
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
    final RDottedName name)
    throws CADatabaseException
  {
    final var typeName = name.value();

    context.deleteFrom(METADATA_SCALAR_TYPES)
      .where(METADATA_SCALAR_TYPES.NAME.eq(typeName))
      .execute();

    return CADatabaseUnit.UNIT;
  }
}
