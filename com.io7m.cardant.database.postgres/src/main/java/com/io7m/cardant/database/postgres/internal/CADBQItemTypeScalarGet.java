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
import com.io7m.cardant.database.api.CADatabaseQueriesItemTypesType.TypeScalarGetType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CATypeScalar;
import com.io7m.lanark.core.RDottedName;
import org.jooq.DSLContext;

import java.util.Optional;

import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_SCALAR_TYPES;
import static com.io7m.cardant.strings.CAStringConstants.TYPE;

/**
 * Retrieve a scalar type declaration.
 */

public final class CADBQItemTypeScalarGet
  extends CADBQAbstract<RDottedName, Optional<CATypeScalar>>
  implements TypeScalarGetType
{
  private static final Service<RDottedName, Optional<CATypeScalar>, TypeScalarGetType> SERVICE =
    new Service<>(TypeScalarGetType.class, CADBQItemTypeScalarGet::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemTypeScalarGet(
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
  protected Optional<CATypeScalar> onExecute(
    final DSLContext context,
    final RDottedName name)
    throws CADatabaseException
  {
    this.setAttribute(TYPE, name.value());

    return context.select(
        METADATA_SCALAR_TYPES.NAME,
        METADATA_SCALAR_TYPES.PATTERN,
        METADATA_SCALAR_TYPES.DESCRIPTION)
      .from(METADATA_SCALAR_TYPES)
      .where(METADATA_SCALAR_TYPES.NAME.eq(name.value()))
      .fetchOptional()
      .map(record -> {
        return new CATypeScalar(
          new RDottedName(record.get(METADATA_SCALAR_TYPES.NAME)),
          record.get(METADATA_SCALAR_TYPES.DESCRIPTION),
          record.get(METADATA_SCALAR_TYPES.PATTERN)
        );
      });
  }
}
