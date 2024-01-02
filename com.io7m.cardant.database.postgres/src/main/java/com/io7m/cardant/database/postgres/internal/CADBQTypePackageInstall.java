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
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageInstallType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.type_package.CATypePackage;
import com.io7m.verona.core.VersionQualifier;
import org.jooq.DSLContext;
import org.jooq.Query;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Map;

import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd.auditEvent;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_PACKAGES;
import static com.io7m.cardant.strings.CAStringConstants.PACKAGE;
import static com.io7m.cardant.strings.CAStringConstants.PACKAGE_VERSION;

/**
 * Install a type package.
 */

public final class CADBQTypePackageInstall
  extends CADBQAbstract<CATypePackage, CADatabaseUnit>
  implements TypePackageInstallType
{
  private static final Service<
    CATypePackage,
    CADatabaseUnit,
    TypePackageInstallType> SERVICE =
    new Service<>(TypePackageInstallType.class, CADBQTypePackageInstall::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQTypePackageInstall(
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
    final CATypePackage typePackage)
    throws CADatabaseException
  {
    final var packageId =
      typePackage.identifier();
    final var version =
      packageId.version();

    this.setAttribute(PACKAGE, packageId.name().value());
    this.setAttribute(PACKAGE_VERSION, version.toString());

    final var batch =
      installQueries(
        this.transaction(),
        context,
        typePackage,
        this.attributes()
      );

    context.batch(batch).execute();
    return CADatabaseUnit.UNIT;
  }

  static ArrayList<Query> installQueries(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final CATypePackage typePackage,
    final Map<String, String> attributes)
    throws CADatabaseException
  {
    final var packageId =
      typePackage.identifier();
    final var version =
      packageId.version();
    final var text =
      CADBTypePackages.serialize(
        transaction.connection()
          .database()
          .typePackageSerializers(),
        typePackage,
        attributes
      );

    final var packageDbID =
      context.insertInto(METADATA_TYPE_PACKAGES)
        .set(
          METADATA_TYPE_PACKAGES.MTP_NAME,
          packageId.name().value())
        .set(
          METADATA_TYPE_PACKAGES.MTP_DESCRIPTION,
          typePackage.description())
        .set(
          METADATA_TYPE_PACKAGES.MTP_VERSION_MAJOR,
          Integer.valueOf(version.major()))
        .set(
          METADATA_TYPE_PACKAGES.MTP_VERSION_MINOR,
          Integer.valueOf(version.minor()))
        .set(
          METADATA_TYPE_PACKAGES.MTP_VERSION_PATCH,
          Integer.valueOf(version.patch()))
        .set(
          METADATA_TYPE_PACKAGES.MTP_VERSION_QUALIFIER,
          version.qualifier()
            .map(VersionQualifier::text)
            .orElse(null))
        .set(METADATA_TYPE_PACKAGES.MTP_TEXT, text)
        .returning(METADATA_TYPE_PACKAGES.MTP_ID)
        .fetchOptional(METADATA_TYPE_PACKAGES.MTP_ID)
        .orElseThrow()
        .intValue();

    final var batch = new ArrayList<Query>();
    for (final var e : typePackage.scalarTypes().entrySet()) {
      batch.addAll(
        CADBQTypeScalarPut.insertType(
          transaction,
          context,
          packageDbID,
          e.getValue()
        )
      );
    }
    for (final var e : typePackage.recordTypes().entrySet()) {
      batch.addAll(
        CADBQTypeRecordPut.putTypeRecord(
          transaction,
          context,
          packageDbID,
          e.getValue()
        )
      );
    }

    batch.add(
      auditEvent(
        context,
        OffsetDateTime.now(transaction.clock()),
        transaction.userId(),
        "TYPE_PACKAGE_INSTALLED",
        Map.entry("Package", packageId.name().value()),
        Map.entry("PackageVersion", version.toString())
      )
    );
    return batch;
  }
}
