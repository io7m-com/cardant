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
import com.io7m.cardant.model.CATypeRecordFieldIdentifier;
import com.io7m.cardant.model.CATypeRecordIdentifier;

import java.util.Collection;
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

  non-sealed interface LocationPutType
    extends CADatabaseQueryType<CALocation, CADatabaseUnit>,
    CADatabaseQueriesLocationsType
  {

  }

  /**
   * Retrieve a location.
   */

  non-sealed interface LocationGetType
    extends CADatabaseQueryType<CALocationID, Optional<CALocation>>,
    CADatabaseQueriesLocationsType
  {

  }

  /**
   * Delete the given locations.
   */

  non-sealed interface LocationDeleteType
    extends CADatabaseQueryType<Collection<CALocationID>, CADatabaseUnit>,
    CADatabaseQueriesLocationsType
  {

  }

  /**
   * Mark the given locations as deleted.
   */

  non-sealed interface LocationDeleteMarkOnlyType
    extends CADatabaseQueryType<Collection<CALocationID>, CADatabaseUnit>,
    CADatabaseQueriesLocationsType
  {

  }

  /**
   * List locations.
   */

  non-sealed interface LocationListType
    extends CADatabaseQueryType<CADatabaseUnit, SortedMap<CALocationID, CALocationSummary>>,
    CADatabaseQueriesLocationsType
  {

  }

  /**
   * Add or update metadata on a location.
   */

  non-sealed interface LocationMetadataPutType
    extends CADatabaseQueryType<LocationMetadataPutType.Parameters, CADatabaseUnit>,
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

  non-sealed interface LocationMetadataRemoveType
    extends CADatabaseQueryType<LocationMetadataRemoveType.Parameters, CADatabaseUnit>,
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
      Set<CATypeRecordFieldIdentifier> names)
    {

    }
  }

  /**
   * Add an attachment to the given location.
   */

  non-sealed interface LocationAttachmentAddType
    extends CADatabaseQueryType<LocationAttachmentAddType.Parameters, CADatabaseUnit>,
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

  non-sealed interface LocationAttachmentRemoveType
    extends CADatabaseQueryType<LocationAttachmentRemoveType.Parameters, CADatabaseUnit>,
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

  non-sealed interface LocationTypesAssignType
    extends CADatabaseQueryType<LocationTypesAssignType.Parameters, CADatabaseUnit>,
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
      Set<CATypeRecordIdentifier> types)
    {

    }
  }

  /**
   * Revoke types from the given location.
   */

  non-sealed interface LocationTypesRevokeType
    extends CADatabaseQueryType<LocationTypesRevokeType.Parameters, CADatabaseUnit>,
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
      Set<CATypeRecordIdentifier> types)
    {

    }
  }
}
