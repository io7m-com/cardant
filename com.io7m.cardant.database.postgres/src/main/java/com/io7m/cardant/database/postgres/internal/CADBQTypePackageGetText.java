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
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.Optional;

import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_PACKAGES;

/**
 * Get the text of a type package.
 */

public final class CADBQTypePackageGetText
  extends CADBQAbstract<CATypePackageIdentifier, Optional<String>>
  implements CADatabaseQueriesTypePackagesType.TypePackageGetTextType
{
  private static final Service<
    CATypePackageIdentifier,
    Optional<String>,
    TypePackageGetTextType> SERVICE =
    new Service<>(
      TypePackageGetTextType.class,
      CADBQTypePackageGetText::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQTypePackageGetText(
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
  protected Optional<String> onExecute(
    final DSLContext context,
    final CATypePackageIdentifier parameters)
    throws CADatabaseException
  {
    final var version =
      parameters.version();

    final var nameCondition =
      METADATA_TYPE_PACKAGES.MTP_NAME
        .eq(parameters.name().value());
    final var majorCondition =
      METADATA_TYPE_PACKAGES.MTP_VERSION_MAJOR
        .eq(Integer.valueOf(version.major()));
    final var minorCondition =
      METADATA_TYPE_PACKAGES.MTP_VERSION_MINOR
        .eq(Integer.valueOf(version.minor()));
    final var patchCondition =
      METADATA_TYPE_PACKAGES.MTP_VERSION_PATCH
        .eq(Integer.valueOf(version.patch()));
    final var qualCondition =
      version.qualifier()
        .map(x -> METADATA_TYPE_PACKAGES.MTP_VERSION_QUALIFIER.eq(x.text()))
        .orElseGet(METADATA_TYPE_PACKAGES.MTP_VERSION_QUALIFIER::isNull);

    final var matchCondition =
      DSL.and(
        nameCondition,
        majorCondition,
        minorCondition,
        patchCondition,
        qualCondition
      );

    final var query =
      context.select(METADATA_TYPE_PACKAGES.MTP_TEXT)
        .from(METADATA_TYPE_PACKAGES)
        .where(matchCondition);

    return query.fetchOptional(METADATA_TYPE_PACKAGES.MTP_TEXT);
  }

}
