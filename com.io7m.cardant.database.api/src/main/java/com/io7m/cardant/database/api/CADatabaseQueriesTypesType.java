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

import com.io7m.cardant.model.CATypeRecord;
import com.io7m.cardant.model.CATypeRecordFieldUpdate;
import com.io7m.cardant.model.CATypeRecordRemoval;
import com.io7m.cardant.model.CATypeRecordSearchParameters;
import com.io7m.cardant.model.CATypeScalarRemoval;
import com.io7m.cardant.model.CATypeScalarSearchParameters;
import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.lanark.core.RDottedName;

import java.util.Optional;

/**
 * Model database queries (Types).
 */

public sealed interface CADatabaseQueriesTypesType
  extends CADatabaseQueriesType
{
  /**
   * Remove a type declaration.
   */

  non-sealed interface TypeRecordRemoveType
    extends CADatabaseQueryType<CATypeRecordRemoval, CADatabaseUnit>,
    CADatabaseQueriesTypesType
  {

  }

  /**
   * Create or update a type declaration.
   */

  non-sealed interface TypeRecordPutType
    extends CADatabaseQueryType<CATypeRecord, CADatabaseUnit>,
    CADatabaseQueriesTypesType
  {

  }

  /**
   * Remove a record type field.
   */

  non-sealed interface TypeRecordFieldRemoveType
    extends CADatabaseQueryType<RDottedName, CADatabaseUnit>,
    CADatabaseQueriesTypesType
  {

  }

  /**
   * Update a record type field.
   */

  non-sealed interface TypeRecordFieldUpdateType
    extends CADatabaseQueryType<CATypeRecordFieldUpdate, CADatabaseUnit>,
    CADatabaseQueriesTypesType
  {

  }

  /**
   * Get a type declaration.
   */

  non-sealed interface TypeRecordGetType
    extends CADatabaseQueryType<RDottedName, Optional<CATypeRecord>>,
    CADatabaseQueriesTypesType
  {

  }

  /**
   * Search for type declarations that reference the scalar type with the
   * given name.
   */

  non-sealed interface TypeRecordsReferencingScalarType
    extends CADatabaseQueryType<RDottedName, CADatabaseTypeRecordSearchType>,
    CADatabaseQueriesTypesType
  {

  }

  /**
   * Search for type declarations.
   */

  non-sealed interface TypeRecordsSearchType
    extends CADatabaseQueryType<CATypeRecordSearchParameters, CADatabaseTypeRecordSearchType>,
    CADatabaseQueriesTypesType
  {

  }

  /**
   * Get a scalar type declaration.
   */

  non-sealed interface TypeScalarGetType
    extends CADatabaseQueryType<RDottedName, Optional<CATypeScalarType>>,
    CADatabaseQueriesTypesType
  {

  }

  /**
   * Search for scalar type declarations.
   */

  non-sealed interface TypeScalarSearchType
    extends CADatabaseQueryType<CATypeScalarSearchParameters, CADatabaseTypeScalarSearchType>,
    CADatabaseQueriesTypesType
  {

  }

  /**
   * Remove a scalar type declaration.
   */

  non-sealed interface TypeScalarRemoveType
    extends CADatabaseQueryType<CATypeScalarRemoval, CADatabaseUnit>,
    CADatabaseQueriesTypesType
  {

  }

  /**
   * Create or update a scalar type declaration.
   */

  non-sealed interface TypeScalarPutType
    extends CADatabaseQueryType<CATypeScalarType, CADatabaseUnit>,
    CADatabaseQueriesTypesType
  {

  }
}
