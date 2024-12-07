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
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageUninstallType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.type_package.CATypePackageUninstall;
import com.io7m.cardant.strings.CAStringConstants;
import com.io7m.verona.core.VersionQualifier;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.TableField;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd.auditEvent;
import static com.io7m.cardant.database.postgres.internal.Tables.ITEM_TYPES;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_PACKAGES;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;

/**
 * Uninstall a type package.
 */

public final class CADBQTypePackageUninstall
  extends CADBQAbstract<CATypePackageUninstall, CADatabaseUnit>
  implements TypePackageUninstallType
{
  private static final Service<
    CATypePackageUninstall,
    CADatabaseUnit,
    TypePackageUninstallType> SERVICE =
    new Service<>(
      TypePackageUninstallType.class,
      CADBQTypePackageUninstall::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQTypePackageUninstall(
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
    final CATypePackageUninstall parameters)
    throws CADatabaseException
  {
    final var packageId =
      parameters.packageIdentifier();
    final var version =
      packageId.version();

    final var transaction =
      this.transaction();
    final var batch =
      new ArrayList<Query>();

    final var matchCondition =
      DSL.and(
        METADATA_TYPE_PACKAGES.MTP_NAME
          .eq(packageId.name().value()),
        METADATA_TYPE_PACKAGES.MTP_VERSION_MAJOR
          .eq(Integer.valueOf(version.major())),
        METADATA_TYPE_PACKAGES.MTP_VERSION_MINOR
          .eq(Integer.valueOf(version.minor())),
        METADATA_TYPE_PACKAGES.MTP_VERSION_PATCH
          .eq(Integer.valueOf(version.patch())),
        eqQualifier(version.qualifier(), METADATA_TYPE_PACKAGES.MTP_VERSION_QUALIFIER)
      );

    final var packageQuery =
      context.select(METADATA_TYPE_PACKAGES.MTP_ID)
        .from(METADATA_TYPE_PACKAGES)
        .where(matchCondition);

    final var packageDbID =
      packageQuery.fetchOptional(METADATA_TYPE_PACKAGES.MTP_ID)
        .orElseThrow(() -> {
          return new CADatabaseException(
            this.local(CAStringConstants.ERROR_NONEXISTENT),
            errorNonexistent(),
            this.attributes(),
            Optional.empty()
          );
        });

    /*
     * The user can request that referenced types simply be revoked rather
     * than preventing the uninstallation of the package.
     */

    switch (parameters.typeRemovalBehavior()) {
      case TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED -> {
        break;
      }
      case TYPE_REMOVAL_REVOKE_TYPES -> {
        batch.add(
          context.deleteFrom(ITEM_TYPES)
            .where(ITEM_TYPES.IT_TYPE.in(
              context.select(METADATA_TYPES.MT_ID)
                .from(METADATA_TYPES)
                .where(METADATA_TYPES.MT_PACKAGE.eq(packageDbID))
            ))
        );
      }
    }

    batch.add(
      context.deleteFrom(METADATA_TYPES)
        .where(METADATA_TYPES.MT_PACKAGE.eq(packageDbID))
    );

    batch.add(
      context.deleteFrom(METADATA_TYPE_PACKAGES)
        .where(METADATA_TYPE_PACKAGES.MTP_ID.eq(packageDbID))
    );

    batch.add(
      auditEvent(
        context,
        OffsetDateTime.now(transaction.clock()),
        transaction.userId(),
        "TYPE_PACKAGE_UNINSTALLED",
        Map.entry("Package", packageId.name().value()),
        Map.entry("PackageVersion", version.toString())
      )
    );

    context.batch(batch).execute();
    return CADatabaseUnit.UNIT;
  }

  private static Condition eqQualifier(
    final Optional<VersionQualifier> qualifier,
    final TableField<Record, String> column)
  {
    if (qualifier.isEmpty()) {
      return column.isNull();
    }
    final var text = qualifier.get();
    return column.eq(text.text());
  }
}
