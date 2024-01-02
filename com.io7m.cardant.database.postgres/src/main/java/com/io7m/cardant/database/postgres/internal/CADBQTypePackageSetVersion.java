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
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageSetVersionType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.verona.core.VersionQualifier;
import org.jooq.DSLContext;
import org.jooq.Query;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Map;

import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd.auditEvent;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_PACKAGES;

/**
 * Set the version of a type package.
 */

public final class CADBQTypePackageSetVersion
  extends CADBQAbstract<CATypePackageIdentifier, CADatabaseUnit>
  implements TypePackageSetVersionType
{
  private static final Service<
    CATypePackageIdentifier,
    CADatabaseUnit,
    TypePackageSetVersionType> SERVICE =
    new Service<>(TypePackageSetVersionType.class, CADBQTypePackageSetVersion::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQTypePackageSetVersion(
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
    final CATypePackageIdentifier typePackage)
    throws CADatabaseException
  {
    final var name =
      typePackage.name().value();
    final var version =
      typePackage.version();

    final var transaction =
      this.transaction();
    final var batch =
      new ArrayList<Query>(2);

    batch.add(
      context.update(METADATA_TYPE_PACKAGES)
        .set(METADATA_TYPE_PACKAGES.MTP_VERSION_MAJOR,
             Integer.valueOf(version.major()))
        .set(METADATA_TYPE_PACKAGES.MTP_VERSION_MINOR,
             Integer.valueOf(version.minor()))
        .set(METADATA_TYPE_PACKAGES.MTP_VERSION_PATCH,
             Integer.valueOf(version.patch()))
        .set(METADATA_TYPE_PACKAGES.MTP_VERSION_QUALIFIER,
             version.qualifier()
               .map(VersionQualifier::text)
               .orElse(null))
        .where(METADATA_TYPE_PACKAGES.MTP_NAME.eq(name))
    );

    batch.add(
      auditEvent(
        context,
        OffsetDateTime.now(transaction.clock()),
        transaction.userId(),
        "TYPE_PACKAGE_UPDATED",
        Map.entry("Package", name),
        Map.entry("PackageVersion", version.toString())
      )
    );

    context.batch(batch).execute();
    return CADatabaseUnit.UNIT;
  }
}
