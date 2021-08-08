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
import com.io7m.cardant.database.api.CADatabaseQueriesType;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.UUID;

/**
 * Model database queries.
 */

public interface CAModelCADatabaseQueriesType extends CADatabaseQueriesType
{
  /**
   * Retrieve the tag with the given ID, if one exists.
   *
   * @param id The ID
   *
   * @return The tag, if any
   *
   * @throws CADatabaseException On database errors
   */

  Optional<CATag> tagGet(UUID id)
    throws CADatabaseException;

  /**
   * Create or update the given tag.
   *
   * @param tag The tag
   *
   * @throws CADatabaseException On database errors
   */

  void tagPut(CATag tag)
    throws CADatabaseException;

  /**
   * Delete the given tag. The tag will be removed from any items it is associated with.
   *
   * @param tag The tag
   *
   * @throws CADatabaseException On database errors
   */

  void tagDelete(CATag tag)
    throws CADatabaseException;

  /**
   * @return The available tags
   *
   * @throws CADatabaseException On database errors
   */

  SortedSet<CATag> tagList()
    throws CADatabaseException;

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
   * Set the count for the given item.
   *
   * @param id    The item
   * @param count The count
   *
   * @throws CADatabaseException On database errors
   */

  void itemCountSet(
    CAItemID id,
    long count)
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
   * Delete the given item.
   *
   * @param item The item
   *
   * @throws CADatabaseException On database errors
   */

  void itemDelete(CAItemID item)
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
   * Create or update the given user.
   *
   * @param user The user
   *
   * @throws CADatabaseException On database errors
   */

  void userPut(CAUser user)
    throws CADatabaseException;

  /**
   * @param id The user id
   *
   * @return The user with the given ID
   *
   * @throws CADatabaseException On database errors
   */

  Optional<CAUser> userGet(CAUserID id)
    throws CADatabaseException;

  /**
   * @param name The user name
   *
   * @return The user with the given name
   *
   * @throws CADatabaseException On database errors
   */

  Optional<CAUser> userGetByName(String name)
    throws CADatabaseException;

  /**
   * @return The list of users in the database
   *
   * @throws CADatabaseException On database errors
   */

  Map<CAUserID, CAUser> userList()
    throws CADatabaseException;
}
