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

import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageGetTextType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageSatisfyingType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageSatisfyingType.Parameters;
import com.io7m.cardant.model.type_package.CATypePackage;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.type_packages.compiler.api.CATypePackageCompileCheckingFailed;
import com.io7m.cardant.type_packages.compiler.api.CATypePackageCompileOK;
import com.io7m.cardant.type_packages.compiler.api.CATypePackageCompileParsingFailed;
import com.io7m.cardant.type_packages.compiler.api.CATypePackageCompilerFactoryType;
import com.io7m.cardant.type_packages.resolver.api.CATypePackageResolverType;
import com.io7m.lanark.core.RDottedName;
import com.io7m.verona.core.VersionRange;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * A database type package resolver.
 */

public final class CADatabaseTypePackageResolver
  implements CATypePackageResolverType
{
  private final CATypePackageCompilerFactoryType compilers;
  private final CADatabaseTransactionType transaction;

  private CADatabaseTypePackageResolver(
    final CATypePackageCompilerFactoryType inCompilers,
    final CADatabaseTransactionType inTransaction)
  {
    this.compilers =
      Objects.requireNonNull(inCompilers, "inCompilers");
    this.transaction =
      Objects.requireNonNull(inTransaction, "transaction");
  }

  /**
   * A database type package resolver.
   *
   * @param inCompilers   The compilers
   * @param inTransaction The transaction
   *
   * @return A resolver
   */

  public static CATypePackageResolverType create(
    final CATypePackageCompilerFactoryType inCompilers,
    final CADatabaseTransactionType inTransaction)
  {
    return new CADatabaseTypePackageResolver(
      inCompilers,
      inTransaction
    );
  }

  @Override
  public Optional<CATypePackageIdentifier> findTypePackageId(
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
  public Optional<CATypePackage> findTypePackage(
    final CATypePackageIdentifier identifier)
  {
    try {
      final var textOpt =
        this.transaction.queries(TypePackageGetTextType.class)
          .execute(identifier);

      if (textOpt.isEmpty()) {
        return Optional.empty();
      }

      final var compiler =
        this.compilers.createCompiler(this);

      return switch (compiler.execute(textOpt.get())) {
        case final CATypePackageCompileCheckingFailed f -> {
          yield Optional.empty();
        }
        case final CATypePackageCompileOK ok -> {
          yield Optional.of(ok.typePackage());
        }
        case final CATypePackageCompileParsingFailed f -> {
          yield Optional.empty();
        }
      };
    } catch (final CADatabaseException | IOException e) {
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
