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

package com.io7m.cardant.model;

import com.io7m.cardant.database.api.CADatabaseException;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;

/**
 * Model database queries (Items).
 */

public interface CAModelDatabaseQueriesItemsType
{
  /**
   * Retrieve the item with the given ID, if one exists.
   *
   * @param id The ID
   *
   * @return The item, if any
   *
   * @throws CADatabaseException On database errors
   */

  Optional<CAItem> itemGet(CAItemID id)
    throws CADatabaseException;

  /**
   * Create an item with the given ID.
   *
   * @param id The ID
   *
   * @throws CADatabaseException On database errors
   */

  void itemCreate(CAItemID id)
    throws CADatabaseException;

  /**
   * Set the name for the given item.
   *
   * @param id   The item
   * @param name The count
   *
   * @throws CADatabaseException On database errors
   */

  void itemNameSet(
    CAItemID id,
    String name)
    throws CADatabaseException;

  /**
   * @return The available items
   *
   * @throws CADatabaseException On database errors
   */

  Set<CAItemID> itemList()
    throws CADatabaseException;

  /**
   * @return The deleted items
   *
   * @throws CADatabaseException On database errors
   */

  Set<CAItemID> itemListDeleted()
    throws CADatabaseException;

  /**
   * Delete the given item.
   *
   * @param item The item
   *
   * @throws CADatabaseException On database errors
   */

  void itemDelete(CAItemID item)
    throws CADatabaseException;

  /**
   * Mark the given item as deleted.
   *
   * @param item The item
   *
   * @throws CADatabaseException On database errors
   */

  void itemDeleteMarkOnly(CAItemID item)
    throws CADatabaseException;

  /**
   * Add the given tag to the given item.
   *
   * @param item The item
   * @param tag  The tag
   *
   * @throws CADatabaseException On database errors
   */

  void itemTagAdd(
    CAItemID item,
    CATag tag)
    throws CADatabaseException;

  /**
   * Remove the given tag from the given item.
   *
   * @param item The item
   * @param tag  The tag
   *
   * @throws CADatabaseException On database errors
   */

  void itemTagRemove(
    CAItemID item,
    CATag tag)
    throws CADatabaseException;

  /**
   * List the tags associated with the given item.
   *
   * @param item The item
   *
   * @return The list of tags
   *
   * @throws CADatabaseException On database errors
   */

  Set<CATag> itemTagList(
    CAItemID item)
    throws CADatabaseException;

  /**
   * Create or update the given item metadata.
   *
   * @param metadata The metadata
   *
   * @throws CADatabaseException On database errors
   */

  void itemMetadataPut(
    CAItemMetadata metadata)
    throws CADatabaseException;

  /**
   * @param item The item
   *
   * @return All metadata associated with the item
   *
   * @throws CADatabaseException On database errors
   */

  SortedMap<String, CAItemMetadata> itemMetadata(
    CAItemID item)
    throws CADatabaseException;

  /**
   * Remove the given metadata from the associated item.
   *
   * @param metadata The metadata
   *
   * @throws CADatabaseException On database errors
   */

  void itemMetadataRemove(
    CAItemMetadata metadata)
    throws CADatabaseException;

  /**
   * Create or update the given attachment.
   *
   * @param attachment The attachment
   *
   * @throws CADatabaseException On database errors
   */

  void itemAttachmentPut(
    CAItemAttachment attachment)
    throws CADatabaseException;

  /**
   * Retrieve all the attachments associated with an item.
   *
   * @param item     The item
   * @param withData {@code true} if the attachment data should be retrieved
   *
   * @return The attachments
   *
   * @throws CADatabaseException On database errors
   */

  Map<CAItemAttachmentID, CAItemAttachment> itemAttachments(
    CAItemID item,
    boolean withData)
    throws CADatabaseException;

  /**
   * Retrieve the attachment with the given ID.
   *
   * @param id       The attachment ID
   * @param withData {@code true} if the attachment data should be retrieved
   *
   * @return The attachment
   *
   * @throws CADatabaseException On database errors
   */

  Optional<CAItemAttachment> itemAttachmentGet(
    CAItemAttachmentID id,
    boolean withData)
    throws CADatabaseException;

  /**
   * Remove the attachment with the given ID.
   *
   * @param id The attachment ID
   *
   * @throws CADatabaseException On database errors
   */

  void itemAttachmentRemove(
    CAItemAttachmentID id)
    throws CADatabaseException;

  /**
   * Reposit items.
   *
   * @param reposit The reposit operation
   *
   * @throws CADatabaseException On database errors
   */

  void itemReposit(CAItemRepositType reposit)
    throws CADatabaseException;
}
