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


package com.io7m.cardant.database.api;

import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageInstallType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageSetVersionType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeRecordFieldRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeRecordFieldUpdateType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeRecordPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeRecordRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeScalarPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeScalarRemoveType;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpFail;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpPackageInstall;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpPackageSetVersion;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpType;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpTypeRecordCreate;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpTypeRecordFieldRemove;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpTypeRecordFieldUpdate;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpTypeRecordRemove;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpTypeScalarRemove;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpTypeScalarUpdate;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUpgradeExecutorType;

import java.util.List;
import java.util.Objects;

/**
 * An executor for type package upgrade plans.
 */

public final class CADatabaseTypePackageUpgradeExecutor
  implements CATypePackageUpgradeExecutorType
{
  private final CADatabaseTransactionType transaction;

  private CADatabaseTypePackageUpgradeExecutor(
    final CADatabaseTransactionType inTransaction)
  {

    this.transaction =
      Objects.requireNonNull(inTransaction, "transaction");
  }

  /**
   * An executor for type package upgrade plans.
   *
   * @param transaction The database transaction
   *
   * @return An executor
   */

  public static CATypePackageUpgradeExecutorType create(
    final CADatabaseTransactionType transaction)
  {
    return new CADatabaseTypePackageUpgradeExecutor(transaction);
  }

  @Override
  public void execute(
    final List<CATypePackageUOpType> plan)
    throws CADatabaseException
  {
    for (final var operation : plan) {
      this.executeOperation(operation);
    }
  }

  private CADatabaseUnit executeOperation(
    final CATypePackageUOpType operation)
    throws CADatabaseException
  {
    return switch (operation) {
      case final CATypePackageUOpFail o -> {
        final var error = o.error();
        throw new CADatabaseException(
          error.message(),
          error.errorCode(),
          error.attributes(),
          error.remediatingAction()
        );
      }

      case final CATypePackageUOpPackageInstall o -> {
        this.transaction.queries(TypePackageInstallType.class)
          .execute(o.typePackage());
        yield CADatabaseUnit.UNIT;
      }

      case final CATypePackageUOpPackageSetVersion o -> {
        this.transaction.queries(TypePackageSetVersionType.class)
          .execute(o.typePackage());
        yield CADatabaseUnit.UNIT;
      }

      case final CATypePackageUOpTypeRecordCreate o -> {
        yield this.transaction.queries(TypeRecordPutType.class)
          .execute(o.typeRecord());
      }

      case final CATypePackageUOpTypeRecordFieldRemove o -> {
        yield this.transaction.queries(TypeRecordFieldRemoveType.class)
          .execute(o.field().name());
      }

      case final CATypePackageUOpTypeRecordFieldUpdate o -> {
        yield this.transaction.queries(TypeRecordFieldUpdateType.class)
          .execute(o.update());
      }

      case final CATypePackageUOpTypeRecordRemove o -> {
        yield this.transaction.queries(TypeRecordRemoveType.class)
          .execute(o.removal());
      }

      case final CATypePackageUOpTypeScalarRemove o -> {
        yield this.transaction.queries(TypeScalarRemoveType.class)
          .execute(o.removal());
      }

      case final CATypePackageUOpTypeScalarUpdate o -> {
        yield this.transaction.queries(TypeScalarPutType.class)
          .execute(o.typeScalar());
      }
    };
  }

  @Override
  public String toString()
  {
    return "[CADatabaseTypePackageUpgradeExecutor 0x%s]"
      .formatted(Integer.toUnsignedString(this.hashCode(), 16));
  }
}
