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
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemLocations;
import com.io7m.cardant.model.CAItemRepositType;
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.lanark.core.RDottedName;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Model database queries (Items).
 */

public sealed interface CADatabaseQueriesItemsType
  extends CADatabaseQueriesType
{
  /**
   * Retrieve the item with the given ID, if one exists.
   */

  non-sealed interface GetType
    extends CADatabaseQueryType<CAItemID, Optional<CAItem>>,
    CADatabaseQueriesItemsType
  {

  }

  /**
   * Create an item with the given ID.
   */

  non-sealed interface CreateType
    extends CADatabaseQueryType<CAItemID, CADatabaseUnit>,
    CADatabaseQueriesItemsType
  {

  }

  /**
   * Set the name for the given item.
   */

  non-sealed interface SetNameType
    extends CADatabaseQueryType<SetNameType.Parameters, CADatabaseUnit>,
    CADatabaseQueriesItemsType
  {
    /**
     * Parameters for the operation.
     *
     * @param item The item ID
     * @param name The new name
     */

    record Parameters(
      CAItemID item,
      String name)
    {

    }
  }

  /**
   * Delete the given items.
   */

  non-sealed interface DeleteType
    extends CADatabaseQueryType<Collection<CAItemID>, CADatabaseUnit>,
    CADatabaseQueriesItemsType
  {

  }

  /**
   * Mark the given items as deleted.
   */

  non-sealed interface DeleteMarkOnlyType
    extends CADatabaseQueryType<Collection<CAItemID>, CADatabaseUnit>,
    CADatabaseQueriesItemsType
  {

  }

  /**
   * Add or update metadata on an item.
   */

  non-sealed interface MetadataPutType
    extends CADatabaseQueryType<MetadataPutType.Parameters, CADatabaseUnit>,
    CADatabaseQueriesItemsType
  {
    /**
     * Parameters for the operation.
     *
     * @param item     The item ID
     * @param metadata The metadata
     */

    record Parameters(
      CAItemID item,
      Set<CAMetadataType> metadata)
    {

    }
  }

  /**
   * Remove metadata from an item.
   */

  non-sealed interface MetadataRemoveType
    extends CADatabaseQueryType<MetadataRemoveType.Parameters, CADatabaseUnit>,
    CADatabaseQueriesItemsType
  {
    /**
     * Parameters for the operation.
     *
     * @param item  The item ID
     * @param names The metadata names
     */

    record Parameters(
      CAItemID item,
      Set<RDottedName> names)
    {

    }
  }

  /**
   * Add an attachment to the given item.
   */

  non-sealed interface AttachmentAddType
    extends CADatabaseQueryType<AttachmentAddType.Parameters, CADatabaseUnit>,
    CADatabaseQueriesItemsType
  {
    /**
     * Parameters for the operation.
     *
     * @param item     The item ID
     * @param file     The file
     * @param relation The relation
     */

    record Parameters(
      CAItemID item,
      CAFileID file,
      String relation)
    {

    }
  }

  /**
   * Remove an attachment from the given item.
   */

  non-sealed interface AttachmentRemoveType
    extends CADatabaseQueryType<AttachmentRemoveType.Parameters, CADatabaseUnit>,
    CADatabaseQueriesItemsType
  {
    /**
     * Parameters for the operation.
     *
     * @param item     The item ID
     * @param file     The file
     * @param relation The relation
     */

    record Parameters(
      CAItemID item,
      CAFileID file,
      String relation)
    {

    }
  }

  /**
   * Reposit items.
   */

  non-sealed interface RepositType
    extends CADatabaseQueryType<CAItemRepositType, CADatabaseUnit>,
    CADatabaseQueriesItemsType
  {

  }

  /**
   * Retrieve the locations of the given item.
   */

  non-sealed interface LocationsType
    extends CADatabaseQueryType<CAItemID, CAItemLocations>,
    CADatabaseQueriesItemsType
  {

  }

  /**
   * Start searching for items.
   */

  non-sealed interface SearchType
    extends CADatabaseQueryType<CAItemSearchParameters, CADatabaseItemSearchType>,
    CADatabaseQueriesItemsType
  {

  }

  /**
   * Assign types to the given item.
   */

  non-sealed interface TypesAssignType
    extends CADatabaseQueryType<TypesAssignType.Parameters, CADatabaseUnit>,
    CADatabaseQueriesItemsType
  {
    /**
     * Parameters for the operation.
     *
     * @param item  The item ID
     * @param types The types
     */

    record Parameters(
      CAItemID item,
      Set<RDottedName> types)
    {

    }
  }

  /**
   * Revoke types from the given item.
   */

  non-sealed interface TypesRevokeType
    extends CADatabaseQueryType<TypesRevokeType.Parameters, CADatabaseUnit>,
    CADatabaseQueriesItemsType
  {
    /**
     * Parameters for the operation.
     *
     * @param item  The item ID
     * @param types The types
     */

    record Parameters(
      CAItemID item,
      Set<RDottedName> types)
    {

    }
  }
}
