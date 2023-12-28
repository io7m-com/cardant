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
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageSatisfyingType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageSatisfyingType.Parameters;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.lanark.core.RDottedName;
import com.io7m.verona.core.Version;
import com.io7m.verona.core.VersionQualifier;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.util.Comparator;
import java.util.Optional;

import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_PACKAGES;

/**
 * Uninstall a type package.
 */

public final class CADBQTypePackageSatisfying
  extends CADBQAbstract<Parameters, Optional<CATypePackageIdentifier>>
  implements TypePackageSatisfyingType
{
  private static final Service<
    Parameters,
    Optional<CATypePackageIdentifier>,
    TypePackageSatisfyingType> SERVICE =
    new Service<>(
      TypePackageSatisfyingType.class,
      CADBQTypePackageSatisfying::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQTypePackageSatisfying(
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

  private static Condition conditionLower(
    final Field<Integer> column,
    final boolean inclusive,
    final int value)
  {
    return inclusive
      ? column.ge(Integer.valueOf(value))
      : column.gt(Integer.valueOf(value));
  }

  private static Condition conditionQualifierLower(
    final Field<String> column,
    final boolean inclusive,
    final Optional<VersionQualifier> valueOpt)
  {
    if (valueOpt.isEmpty()) {
      return DSL.trueCondition();
    }

    final var value = valueOpt.get();
    return inclusive
      ? column.ge(value.text())
      : column.gt(value.text());
  }

  @Override
  protected Optional<CATypePackageIdentifier> onExecute(
    final DSLContext context,
    final Parameters parameters)
    throws CADatabaseException
  {
    final var versionRange =
      parameters.versionRange();
    final var rangeLower =
      versionRange.lower();
    final var rangeLowerInclusive =
      versionRange.lowerInclusive();

    final var nameCondition =
      METADATA_TYPE_PACKAGES.MTP_NAME.eq(parameters.name().value());

    final var majorConditionLower =
      conditionLower(
        METADATA_TYPE_PACKAGES.MTP_VERSION_MAJOR,
        rangeLowerInclusive,
        rangeLower.major()
      );
    final var minorConditionLower =
      conditionLower(
        METADATA_TYPE_PACKAGES.MTP_VERSION_MINOR,
        rangeLowerInclusive,
        rangeLower.minor()
      );
    final var patchConditionLower =
      conditionLower(
        METADATA_TYPE_PACKAGES.MTP_VERSION_PATCH,
        rangeLowerInclusive,
        rangeLower.patch()
      );
    final var qualConditionLower =
      conditionQualifierLower(
        METADATA_TYPE_PACKAGES.MTP_VERSION_QUALIFIER,
        rangeLowerInclusive,
        rangeLower.qualifier()
      );

    final var matchCondition =
      DSL.and(
        nameCondition,
        majorConditionLower,
        minorConditionLower,
        patchConditionLower,
        qualConditionLower
      );

    final var query =
      context.select(
          METADATA_TYPE_PACKAGES.MTP_NAME,
          METADATA_TYPE_PACKAGES.MTP_VERSION_MAJOR,
          METADATA_TYPE_PACKAGES.MTP_VERSION_MINOR,
          METADATA_TYPE_PACKAGES.MTP_VERSION_PATCH,
          METADATA_TYPE_PACKAGES.MTP_VERSION_QUALIFIER
        ).from(METADATA_TYPE_PACKAGES)
        .where(matchCondition);

    return query
      .stream()
      .map(CADBQTypePackageSatisfying::mapRecord)
      .filter(i -> versionRange.contains(i.version()))
      .max(Comparator.comparing(CATypePackageIdentifier::version));
  }

  private static CATypePackageIdentifier mapRecord(
    final org.jooq.Record r)
  {
    return new CATypePackageIdentifier(
      new RDottedName(r.get(METADATA_TYPE_PACKAGES.MTP_NAME)),
      new Version(
        r.<Integer>get(METADATA_TYPE_PACKAGES.MTP_VERSION_MAJOR)
          .intValue(),
        r.<Integer>get(METADATA_TYPE_PACKAGES.MTP_VERSION_MINOR)
          .intValue(),
        r.<Integer>get(METADATA_TYPE_PACKAGES.MTP_VERSION_PATCH)
          .intValue(),
        Optional.ofNullable(
          r.get(METADATA_TYPE_PACKAGES.MTP_VERSION_QUALIFIER)
        ).map(VersionQualifier::new)
      )
    );
  }
}
