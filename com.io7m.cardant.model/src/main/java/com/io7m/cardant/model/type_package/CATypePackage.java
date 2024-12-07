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


package com.io7m.cardant.model.type_package;

import com.io7m.cardant.model.CATypeRecord;
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.cardant.model.CATypeScalarIdentifier;
import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.jaffirm.core.Preconditions;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A type package.
 *
 * @param identifier  The package identifier
 * @param description The package description
 * @param imports     The packages this package imports
 * @param scalarTypes The scalar types
 * @param recordTypes The record types
 */

public record CATypePackage(
  CATypePackageIdentifier identifier,
  String description,
  Set<CATypePackageImport> imports,
  Map<CATypeScalarIdentifier, CATypeScalarType> scalarTypes,
  Map<CATypeRecordIdentifier, CATypeRecord> recordTypes)
{
  /**
   * A type package.
   *
   * @param identifier  The package identifier
   * @param description The package description
   * @param imports     The packages this package imports
   * @param scalarTypes The scalar types
   * @param recordTypes The record types
   */

  public CATypePackage
  {
    Objects.requireNonNull(identifier, "identifier");
    Objects.requireNonNull(description, "description");

    imports = Set.copyOf(imports);
    scalarTypes = Map.copyOf(scalarTypes);
    recordTypes = Map.copyOf(recordTypes);

    for (final var e : scalarTypes.entrySet()) {
      Preconditions.checkPreconditionV(
        Objects.equals(e.getKey(), e.getValue().name()),
        "Type names must match the names used in the type map."
      );
    }

    for (final var e : recordTypes.entrySet()) {
      Preconditions.checkPreconditionV(
        Objects.equals(e.getKey(), e.getValue().name()),
        "Type names must match the names used in the type map."
      );
    }
  }

  /**
   * @return A summary of this package
   */

  public CATypePackageSummary summary()
  {
    return new CATypePackageSummary(
      this.identifier,
      this.description
    );
  }
}
