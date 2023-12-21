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

import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationSummary;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.lanark.core.RDottedName;

import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;

/**
 * Model database queries (Locations).
 */

public sealed interface CADatabaseQueriesLocationsType
  extends CADatabaseQueriesType
{
  /**
   * Create or update the given location.
   */

  non-sealed interface PutType
    extends CADatabaseQueryType<CALocation, CADatabaseUnit>,
    CADatabaseQueriesLocationsType
  {

  }

  /**
   * Retrieve a location.
   */

  non-sealed interface GetType
    extends CADatabaseQueryType<CALocationID, Optional<CALocation>>,
    CADatabaseQueriesLocationsType
  {

  }

  /**
   * List locations.
   */

  non-sealed interface ListType
    extends CADatabaseQueryType<CADatabaseUnit, SortedMap<CALocationID, CALocationSummary>>,
    CADatabaseQueriesLocationsType
  {

  }

  /**
   * Add or update metadata on a location.
   */

  non-sealed interface MetadataPutType
    extends CADatabaseQueryType<CADatabaseQueriesLocationsType.MetadataPutType.Parameters, CADatabaseUnit>,
    CADatabaseQueriesLocationsType
  {
    /**
     * Parameters for the operation.
     *
     * @param location The location ID
     * @param metadata The metadata
     */

    record Parameters(
      CALocationID location,
      Set<CAMetadataType> metadata)
    {

    }
  }

  /**
   * Remove metadata from a location.
   */

  non-sealed interface MetadataRemoveType
    extends CADatabaseQueryType<CADatabaseQueriesLocationsType.MetadataRemoveType.Parameters, CADatabaseUnit>,
    CADatabaseQueriesLocationsType
  {
    /**
     * Parameters for the operation.
     *
     * @param location The location ID
     * @param names    The metadata names
     */

    record Parameters(
      CALocationID location,
      Set<RDottedName> names)
    {

    }
  }

  /**
   * Add an attachment to the given location.
   */

  non-sealed interface AttachmentAddType
    extends CADatabaseQueryType<CADatabaseQueriesLocationsType.AttachmentAddType.Parameters, CADatabaseUnit>,
    CADatabaseQueriesLocationsType
  {
    /**
     * Parameters for the operation.
     *
     * @param location The location ID
     * @param file     The file
     * @param relation The relation
     */

    record Parameters(
      CALocationID location,
      CAFileID file,
      String relation)
    {

    }
  }

  /**
   * Remove an attachment from the given location.
   */

  non-sealed interface AttachmentRemoveType
    extends CADatabaseQueryType<CADatabaseQueriesLocationsType.AttachmentRemoveType.Parameters, CADatabaseUnit>,
    CADatabaseQueriesLocationsType
  {
    /**
     * Parameters for the operation.
     *
     * @param location The location ID
     * @param file     The file
     * @param relation The relation
     */

    record Parameters(
      CALocationID location,
      CAFileID file,
      String relation)
    {

    }
  }

  /**
   * Assign types to the given location.
   */

  non-sealed interface TypesAssignType
    extends CADatabaseQueryType<CADatabaseQueriesLocationsType.TypesAssignType.Parameters, CADatabaseUnit>,
    CADatabaseQueriesLocationsType
  {
    /**
     * Parameters for the operation.
     *
     * @param location The location ID
     * @param types    The types
     */

    record Parameters(
      CALocationID location,
      Set<RDottedName> types)
    {

    }
  }

  /**
   * Revoke types from the given location.
   */

  non-sealed interface TypesRevokeType
    extends CADatabaseQueryType<CADatabaseQueriesLocationsType.TypesRevokeType.Parameters, CADatabaseUnit>,
    CADatabaseQueriesLocationsType
  {
    /**
     * Parameters for the operation.
     *
     * @param location The location ID
     * @param types    The types
     */

    record Parameters(
      CALocationID location,
      Set<RDottedName> types)
    {

    }
  }
}
