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
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CATypeRecordFieldIdentifier;
import com.io7m.cardant.model.CATypeRecordIdentifier;

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

  non-sealed interface ItemGetType
    extends CADatabaseQueryType<CAItemID, Optional<CAItem>>,
    CADatabaseQueriesItemsType
  {

  }

  /**
   * Create an item with the given ID.
   */

  non-sealed interface ItemCreateType
    extends CADatabaseQueryType<CAItemID, CADatabaseUnit>,
    CADatabaseQueriesItemsType
  {

  }

  /**
   * Set the name for the given item.
   */

  non-sealed interface ItemSetNameType
    extends CADatabaseQueryType<ItemSetNameType.Parameters, CADatabaseUnit>,
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

  non-sealed interface ItemDeleteType
    extends CADatabaseQueryType<Collection<CAItemID>, CADatabaseUnit>,
    CADatabaseQueriesItemsType
  {

  }

  /**
   * Mark the given items as deleted/undeleted.
   */

  non-sealed interface ItemDeleteMarkOnlyType
    extends CADatabaseQueryType<ItemDeleteMarkOnlyType.Parameters, CADatabaseUnit>,
    CADatabaseQueriesItemsType
  {
    /**
     * Parameters for the operation.
     *
     * @param items   The item IDs
     * @param deleted Whether items are deleted
     */

    record Parameters(
      Collection<CAItemID> items,
      boolean deleted)
    {

    }
  }

  /**
   * Add or update metadata on an item.
   */

  non-sealed interface ItemMetadataPutType
    extends CADatabaseQueryType<ItemMetadataPutType.Parameters, CADatabaseUnit>,
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

  non-sealed interface ItemMetadataRemoveType
    extends CADatabaseQueryType<ItemMetadataRemoveType.Parameters, CADatabaseUnit>,
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
      Set<CATypeRecordFieldIdentifier> names)
    {

    }
  }

  /**
   * Add an attachment to the given item.
   */

  non-sealed interface ItemAttachmentAddType
    extends CADatabaseQueryType<ItemAttachmentAddType.Parameters, CADatabaseUnit>,
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

  non-sealed interface ItemAttachmentRemoveType
    extends CADatabaseQueryType<ItemAttachmentRemoveType.Parameters, CADatabaseUnit>,
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
   * Start searching for items.
   */

  non-sealed interface ItemSearchType
    extends CADatabaseQueryType<CAItemSearchParameters, CADatabaseItemSearchType>,
    CADatabaseQueriesItemsType
  {

  }

  /**
   * Assign types to the given item.
   */

  non-sealed interface ItemTypesAssignType
    extends CADatabaseQueryType<ItemTypesAssignType.Parameters, CADatabaseUnit>,
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
      Set<CATypeRecordIdentifier> types)
    {

    }
  }

  /**
   * Revoke types from the given item.
   */

  non-sealed interface ItemTypesRevokeType
    extends CADatabaseQueryType<ItemTypesRevokeType.Parameters, CADatabaseUnit>,
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
      Set<CATypeRecordIdentifier> types)
    {

    }
  }
}
