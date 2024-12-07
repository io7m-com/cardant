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
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.util.Map;

import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd.auditEvent;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES;
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

    final CADatabaseTransaction transaction = this.transaction();
    final var packageId1 =
      typePackage.identifier();
    final var version1 =
      packageId1.version();
    final var text =
      CADBTypePackages.serialize(
        transaction.connection()
          .database()
          .typePackageSerializers(),
        typePackage,
        this.attributes()
      );

    final var packageDbID =
      context.insertInto(METADATA_TYPE_PACKAGES)
        .set(
          METADATA_TYPE_PACKAGES.MTP_NAME,
          packageId1.name().value())
        .set(
          METADATA_TYPE_PACKAGES.MTP_DESCRIPTION,
          typePackage.description())
        .set(
          METADATA_TYPE_PACKAGES.MTP_VERSION_MAJOR,
          Integer.valueOf(version1.major()))
        .set(
          METADATA_TYPE_PACKAGES.MTP_VERSION_MINOR,
          Integer.valueOf(version1.minor()))
        .set(
          METADATA_TYPE_PACKAGES.MTP_VERSION_PATCH,
          Integer.valueOf(version1.patch()))
        .set(
          METADATA_TYPE_PACKAGES.MTP_VERSION_QUALIFIER,
          version1.qualifier()
            .map(VersionQualifier::text)
            .orElse(null))
        .set(METADATA_TYPE_PACKAGES.MTP_TEXT, text)
        .onConflictOnConstraint(DSL.constraint(
          "metadata_type_packages_name_unique"))
        .doUpdate()
        .set(
          METADATA_TYPE_PACKAGES.MTP_VERSION_MAJOR,
          Integer.valueOf(version1.major()))
        .set(
          METADATA_TYPE_PACKAGES.MTP_VERSION_MINOR,
          Integer.valueOf(version1.minor()))
        .set(
          METADATA_TYPE_PACKAGES.MTP_VERSION_PATCH,
          Integer.valueOf(version1.patch()))
        .set(
          METADATA_TYPE_PACKAGES.MTP_VERSION_QUALIFIER,
          version1.qualifier()
            .map(VersionQualifier::text)
            .orElse(null))
        .set(METADATA_TYPE_PACKAGES.MTP_TEXT, text)
        .returning(METADATA_TYPE_PACKAGES.MTP_ID)
        .fetchOptional(METADATA_TYPE_PACKAGES.MTP_ID)
        .orElseThrow()
        .intValue();

    for (final var e : typePackage.recordTypes().entrySet()) {
      final var type =
        e.getValue();
      final var query =
        context.insertInto(METADATA_TYPES)
          .set(METADATA_TYPES.MT_NAME, type.name().typeName().value())
          .set(METADATA_TYPES.MT_PACKAGE, Integer.valueOf(packageDbID))
          .onConflictDoNothing();

      query.execute();
    }

    auditEvent(
      context,
      OffsetDateTime.now(transaction.clock()),
      transaction.userId(),
      "TYPE_PACKAGE_INSTALLED",
      Map.entry("Package", packageId1.name().value()),
      Map.entry("PackageVersion", version1.toString())
    ).execute();

    return CADatabaseUnit.UNIT;
  }
}
