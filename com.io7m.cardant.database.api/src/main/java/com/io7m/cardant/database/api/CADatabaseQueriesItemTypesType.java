/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

import com.io7m.cardant.model.CATypeDeclaration;
import com.io7m.cardant.model.CATypeDeclarationSummary;
import com.io7m.cardant.model.CATypeScalar;
import com.io7m.lanark.core.RDottedName;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Model database queries (Items types).
 */

public sealed interface CADatabaseQueriesItemTypesType
  extends CADatabaseQueriesType
{
  /**
   * Remove a type declaration.
   */

  non-sealed interface TypeDeclarationRemoveType
    extends CADatabaseQueryType<RDottedName, CADatabaseUnit>,
    CADatabaseQueriesItemTypesType
  {

  }

  /**
   * Create or update a type declaration.
   */

  non-sealed interface TypeDeclarationPutType
    extends CADatabaseQueryType<CATypeDeclaration, CADatabaseUnit>,
    CADatabaseQueriesItemTypesType
  {

  }

  /**
   * Get a type declaration.
   */

  non-sealed interface TypeDeclarationGetType
    extends CADatabaseQueryType<RDottedName, Optional<CATypeDeclaration>>,
    CADatabaseQueriesItemTypesType
  {

  }

  /**
   * Get multiple type declarations.
   */

  non-sealed interface TypeDeclarationGetMultipleType
    extends CADatabaseQueryType<Set<RDottedName>, List<CATypeDeclaration>>,
    CADatabaseQueriesItemTypesType
  {

  }

  /**
   * Search for type declarations that reference the scalar type with the
   * given name.
   */

  non-sealed interface TypeDeclarationsReferencingScalarType
    extends CADatabaseQueryType<
    RDottedName,
    CADatabasePagedQueryType<CATypeDeclarationSummary>>,
    CADatabaseQueriesItemTypesType
  {

  }

  /**
   * Search for type declarations.
   */

  non-sealed interface TypeDeclarationsSearchType
    extends CADatabaseQueryType<
    String,
    CADatabasePagedQueryType<CATypeDeclarationSummary>>,
    CADatabaseQueriesItemTypesType
  {

  }

  /**
   * Get a scalar type declaration.
   */

  non-sealed interface TypeScalarGetType
    extends CADatabaseQueryType<RDottedName, Optional<CATypeScalar>>,
    CADatabaseQueriesItemTypesType
  {

  }

  /**
   * Search for scalar type declarations.
   */

  non-sealed interface TypeScalarSearchType
    extends CADatabaseQueryType<String, CADatabasePagedQueryType<CATypeScalar>>,
    CADatabaseQueriesItemTypesType
  {

  }

  /**
   * Remove a scalar type declaration. Fails if any type declarations still
   * refer to this type.
   */

  non-sealed interface TypeScalarRemoveType
    extends CADatabaseQueryType<RDottedName, CADatabaseUnit>,
    CADatabaseQueriesItemTypesType
  {

  }

  /**
   * Create or update a scalar type declaration.
   */

  non-sealed interface TypeScalarPutType
    extends CADatabaseQueryType<CATypeScalar, CADatabaseUnit>,
    CADatabaseQueriesItemTypesType
  {

  }
}
