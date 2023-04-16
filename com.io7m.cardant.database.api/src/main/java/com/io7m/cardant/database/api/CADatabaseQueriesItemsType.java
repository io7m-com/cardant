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

import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemLocations;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItemRepositType;
import com.io7m.cardant.model.CAListLocationBehaviourType;
import com.io7m.cardant.model.CATag;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;

/**
 * Model database queries (Items).
 */

public non-sealed interface CADatabaseQueriesItemsType
  extends CADatabaseQueriesType
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
   * @param locationBehaviour The location behaviour
   *
   * @return The available items
   *
   * @throws CADatabaseException On database errors
   */

  Set<CAItem> itemList(
    CAListLocationBehaviourType locationBehaviour)
    throws CADatabaseException;

  /**
   * @return The deleted items
   *
   * @throws CADatabaseException On database errors
   */

  Set<CAItemID> itemListDeleted()
    throws CADatabaseException;

  /**
   * Delete the given items.
   *
   * @param item The items
   *
   * @throws CADatabaseException On database errors
   */

  void itemsDelete(Collection<CAItemID> item)
    throws CADatabaseException;

  /**
   * Mark the given item as deleted.
   *
   * @param item The item
   *
   * @throws CADatabaseException On database errors
   */

  void itemsDeleteMarkOnly(Collection<CAItemID> item)
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
   * @param item     The item
   * @param metadata The metadata
   *
   * @throws CADatabaseException On database errors
   */

  void itemMetadataPut(
    CAItemID item,
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
   * @param item The item
   * @param name The metadata name
   *
   * @throws CADatabaseException On database errors
   */

  void itemMetadataRemove(
    CAItemID item,
    String name)
    throws CADatabaseException;

  /**
   * Add an attachment to the given item.
   *
   * @param item     The item
   * @param file     The file
   * @param relation The attachment relation
   *
   * @throws CADatabaseException On database errors
   */

  void itemAttachmentAdd(
    CAItemID item,
    CAFileID file,
    String relation)
    throws CADatabaseException;

  /**
   * Remove an attachment from the given item.
   *
   * @param item     The item
   * @param file     The file
   * @param relation The attachment relation
   *
   * @throws CADatabaseException On database errors
   */

  void itemAttachmentRemove(
    CAItemID item,
    CAFileID file,
    String relation)
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

  Set<CAItemAttachment> itemAttachments(
    CAItemID item,
    boolean withData)
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

  /**
   * @param item The item
   *
   * @return The locations of the given item
   *
   * @throws CADatabaseException On database errors
   */

  CAItemLocations itemLocations(CAItemID item)
    throws CADatabaseException;
}
