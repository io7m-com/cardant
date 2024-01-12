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


package com.io7m.cardant.type_packages.resolver.api;

import com.io7m.cardant.model.CATypeRecord;
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.cardant.model.CATypeScalarIdentifier;
import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.cardant.model.type_package.CATypePackage;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.lanark.core.RDottedName;
import com.io7m.verona.core.Version;
import com.io7m.verona.core.VersionRange;

import java.util.Objects;
import java.util.Optional;

/**
 * A resolver for various package elements.
 */

public interface CATypePackageResolverType
{
  /**
   * Find the scalar type with the given fully qualified name.
   *
   * @param name The name
   *
   * @return The type, if any
   */

  default Optional<CATypeScalarType> findTypeScalar(
    final CATypeScalarIdentifier name)
  {
    Objects.requireNonNull(name, "name");

    final var versionRangeMax =
      new VersionRange(
        Version.of(0, 0, 0),
        true,
        Version.of(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE),
        true
      );

    return this.findTypePackageId(name.packageName(), versionRangeMax)
      .flatMap(this::findTypePackage)
      .flatMap(r -> Optional.ofNullable(r.scalarTypes().get(name)));
  }

  /**
   * Find the record type with the given fully qualified name.
   *
   * @param name The name
   *
   * @return The type, if any
   */

  default Optional<CATypeRecord> findTypeRecord(
    final CATypeRecordIdentifier name)
  {
    Objects.requireNonNull(name, "name");

    final var versionRangeMax =
      new VersionRange(
        Version.of(0, 0, 0),
        true,
        Version.of(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE),
        true
      );

    return this.findTypePackageId(name.packageName(), versionRangeMax)
      .flatMap(this::findTypePackage)
      .flatMap(r -> Optional.ofNullable(r.recordTypes().get(name)));
  }

  /**
   * Find a package with the given name that satisfies the given version range.
   *
   * @param name         The name
   * @param versionRange The version range
   *
   * @return The package identifier, if any
   */

  Optional<CATypePackageIdentifier> findTypePackageId(
    RDottedName name,
    VersionRange versionRange
  );

  /**
   * Find a package with the given identifier.
   *
   * @param identifier The identifier
   *
   * @return The package, if any
   */

  Optional<CATypePackage> findTypePackage(
    CATypePackageIdentifier identifier
  );
}
