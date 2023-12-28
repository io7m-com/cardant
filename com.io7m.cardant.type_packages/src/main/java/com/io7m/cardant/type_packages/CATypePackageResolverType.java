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


package com.io7m.cardant.type_packages;

import com.io7m.cardant.model.CATypeRecord;
import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.lanark.core.RDottedName;
import com.io7m.verona.core.VersionRange;

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

  Optional<CATypeScalarType> findTypeScalar(
    RDottedName name
  );

  /**
   * Find the record type with the given fully qualified name.
   *
   * @param name The name
   *
   * @return The type, if any
   */

  Optional<CATypeRecord> findTypeRecord(
    RDottedName name
  );

  /**
   * Find a package with the given name that satisfies the given version range.
   *
   * @param name         The name
   * @param versionRange The version range
   *
   * @return The package identifier, if any
   */

  Optional<CATypePackageIdentifier> findTypePackage(
    RDottedName name,
    VersionRange versionRange
  );
}
