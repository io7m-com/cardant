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

import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageSatisfyingType.Parameters;
import com.io7m.cardant.model.type_package.CATypePackage;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.model.type_package.CATypePackageSearchParameters;
import com.io7m.lanark.core.RDottedName;
import com.io7m.verona.core.VersionRange;

import java.util.Objects;
import java.util.Optional;

/**
 * Model database queries (Type packages).
 */

public sealed interface CADatabaseQueriesTypePackagesType
  extends CADatabaseQueriesType
{
  /**
   * Install a type package.
   */

  non-sealed interface TypePackageInstallType
    extends CADatabaseQueryType<CATypePackage, CADatabaseUnit>,
    CADatabaseQueriesTypePackagesType
  {

  }

  /**
   * Uninstall a type package.
   */

  non-sealed interface TypePackageUninstallType
    extends CADatabaseQueryType<CATypePackageIdentifier, CADatabaseUnit>,
    CADatabaseQueriesTypePackagesType
  {

  }

  /**
   * Find a package satisfying the given name and version range.
   */

  non-sealed interface TypePackageSatisfyingType
    extends CADatabaseQueryType<Parameters, Optional<CATypePackageIdentifier>>,
    CADatabaseQueriesTypePackagesType
  {
    /**
     * The parameters.
     *
     * @param name         The package name
     * @param versionRange The package version range
     */

    record Parameters(
      RDottedName name,
      VersionRange versionRange)
    {
      /**
       * The parameters.
       */

      public Parameters
      {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(versionRange, "versionRange");
      }
    }
  }

  /**
   * Get the text of a type package.
   */

  non-sealed interface TypePackageGetTextType
    extends CADatabaseQueryType<CATypePackageIdentifier, Optional<String>>,
    CADatabaseQueriesTypePackagesType
  {

  }

  /**
   * Start searching for type packages.
   */

  non-sealed interface TypePackageSearchType
    extends CADatabaseQueryType<CATypePackageSearchParameters, CADatabaseTypePackageSearchType>,
    CADatabaseQueriesTypePackagesType
  {

  }
}
