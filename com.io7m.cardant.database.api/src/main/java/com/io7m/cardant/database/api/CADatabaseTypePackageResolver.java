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

import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageSatisfyingType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageSatisfyingType.Parameters;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeDeclarationGetType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeScalarGetType;
import com.io7m.cardant.model.CATypeRecord;
import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.type_packages.CATypePackageResolverType;
import com.io7m.lanark.core.RDottedName;
import com.io7m.verona.core.VersionRange;

import java.util.Objects;
import java.util.Optional;

/**
 * A database type package resolver.
 */

public final class CADatabaseTypePackageResolver
  implements CATypePackageResolverType
{
  private final CADatabaseTransactionType transaction;

  private CADatabaseTypePackageResolver(
    final CADatabaseTransactionType inTransaction)
  {
    this.transaction =
      Objects.requireNonNull(inTransaction, "transaction");
  }

  /**
   * A database type package resolver.
   *
   * @param inTransaction The transaction
   *
   * @return A resolver
   */

  public static CATypePackageResolverType create(
    final CADatabaseTransactionType inTransaction)
  {
    return new CADatabaseTypePackageResolver(inTransaction);
  }

  @Override
  public Optional<CATypeScalarType> findTypeScalar(
    final RDottedName name)
  {
    try {
      return this.transaction.queries(TypeScalarGetType.class)
        .execute(name);
    } catch (final CADatabaseException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<CATypeRecord> findTypeRecord(
    final RDottedName name)
  {
    try {
      return this.transaction.queries(TypeDeclarationGetType.class)
        .execute(name);
    } catch (final CADatabaseException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<CATypePackageIdentifier> findTypePackage(
    final RDottedName name,
    final VersionRange versionRange)
  {
    try {
      return this.transaction.queries(TypePackageSatisfyingType.class)
        .execute(new Parameters(name, versionRange));
    } catch (final CADatabaseException e) {
      return Optional.empty();
    }
  }

  @Override
  public String toString()
  {
    return "[CADatabaseTypePackageResolver 0x%s]"
      .formatted(Integer.toUnsignedString(this.hashCode(), 16));
  }
}
