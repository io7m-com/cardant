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

package com.io7m.cardant.protocol.inventory.api;

import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItemRepositType;
import com.io7m.cardant.model.CAListLocationBehaviourType;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CATags;

import java.util.Objects;
import java.util.Set;

/**
 * The base type of commands.
 */

public sealed interface CACommandType
  extends CAMessageType
{
  /**
   * Attempt to log in using the given username and password.
   *
   * @param user     The user
   * @param password The password
   */

  record CACommandLoginUsernamePassword(
    String user,
    String password)
    implements CACommandType
  {
    /**
     * Attempt to log in using the given username and password.
     */

    public CACommandLoginUsernamePassword
    {
      Objects.requireNonNull(user, "user");
      Objects.requireNonNull(password, "password");
    }
  }

  /**
   * Create a new item.
   *
   * @param id   The item ID
   * @param name The item name
   */

  record CACommandItemCreate(
    CAItemID id,
    String name)
    implements CACommandType
  {
    /**
     * Create a new item.
     */

    public CACommandItemCreate
    {
      Objects.requireNonNull(id, "id");
      Objects.requireNonNull(name, "name");
    }
  }

  /**
   * Update an existing item.
   *
   * @param id   The item ID
   * @param name The item name
   */

  record CACommandItemUpdate(
    CAItemID id,
    String name)
    implements CACommandType
  {
    /**
     * Update an existing item.
     */

    public CACommandItemUpdate
    {
      Objects.requireNonNull(id, "id");
      Objects.requireNonNull(name, "name");
    }
  }

  /**
   * Remove an attachment from an item.
   *
   * @param item     The item ID
   * @param file     The file ID
   * @param relation The relation
   */

  record CACommandItemAttachmentRemove(
    CAItemID item,
    CAFileID file,
    String relation)
    implements CACommandType
  {
    /**
     * Remove an attachment from an item.
     */

    public CACommandItemAttachmentRemove
    {
      Objects.requireNonNull(item, "item");
      Objects.requireNonNull(file, "fileID");
      Objects.requireNonNull(relation, "relation");
    }
  }

  /**
   * Add an attachment to an item.
   *
   * @param item     The item ID
   * @param file     The file
   * @param relation The relation
   */

  record CACommandItemAttachmentAdd(
    CAItemID item,
    CAFileID file,
    String relation)
    implements CACommandType
  {
    /**
     * Add an attachment to an item.
     */

    public CACommandItemAttachmentAdd
    {
      Objects.requireNonNull(item, "item");
      Objects.requireNonNull(file, "file");
      Objects.requireNonNull(relation, "relation");
    }
  }

  /**
   * Remove metadata values from an item.
   *
   * @param item          The item ID
   * @param metadataNames The metadata names
   */

  record CACommandItemMetadataRemove(
    CAItemID item,
    Set<String> metadataNames)
    implements CACommandType
  {
    /**
     * Remove metadata values from an item.
     */

    public CACommandItemMetadataRemove
    {
      Objects.requireNonNull(item, "item");
      Objects.requireNonNull(metadataNames, "metadataNames");
    }
  }

  /**
   * Add or update metadata values in an item.
   *
   * @param item      The item ID
   * @param metadatas The metadata values
   */

  record CACommandItemMetadataPut(
    CAItemID item,
    Set<CAItemMetadata> metadatas)
    implements CACommandType
  {
    /**
     * Add or update metadata values in an item.
     */

    public CACommandItemMetadataPut
    {
      Objects.requireNonNull(item, "item");
      Objects.requireNonNull(metadatas, "metadatas");
    }
  }

  /**
   * Create or update a location.
   *
   * @param location The location
   */

  record CACommandLocationPut(
    CALocation location)
    implements CACommandType
  {
    /**
     * Create or update a location.
     */

    public CACommandLocationPut
    {
      Objects.requireNonNull(location, "location");
    }
  }

  /**
   * List locations.
   */

  record CACommandLocationList()
    implements CACommandType
  {

  }

  /**
   * Retrieve a location.
   *
   * @param id The location ID
   */

  record CACommandLocationGet(
    CALocationID id)
    implements CACommandType
  {

  }

  /**
   * Reposit an item.
   *
   * @param reposit The item reposition
   */

  record CACommandItemReposit(
    CAItemRepositType reposit)
    implements CACommandType
  {
    /**
     * Reposit an item.
     */

    public CACommandItemReposit
    {
      Objects.requireNonNull(reposit, "reposit");
    }
  }

  /**
   * List all the locations an item is in, and how many times the item
   * is in them.
   *
   * @param item The item
   */

  record CACommandItemLocationsList(
    CAItemID item)
    implements CACommandType
  {
    /**
     * List all the locations an item is in, and how many times the item
     * is in them.
     */

    public CACommandItemLocationsList
    {
      Objects.requireNonNull(item, "item");
    }
  }

  /**
   * Retrieve an item.
   *
   * @param id The item ID
   */

  record CACommandItemGet(
    CAItemID id)
    implements CACommandType
  {
    /**
     * Retrieve an item.
     */

    public CACommandItemGet
    {
      Objects.requireNonNull(id, "id");
    }
  }

  /**
   * Delete items.
   *
   * @param ids The item IDs
   */

  record CACommandItemsRemove(
    Set<CAItemID> ids)
    implements CACommandType
  {
    /**
     * Delete items.
     */

    public CACommandItemsRemove
    {
      Objects.requireNonNull(ids, "ids");
    }
  }

  /**
   * List items.
   *
   * @param locationBehaviour The location behaviour
   */

  record CACommandItemList(
    CAListLocationBehaviourType locationBehaviour)
    implements CACommandType
  {
    /**
     * List items.
     */

    public CACommandItemList
    {
      Objects.requireNonNull(locationBehaviour, "locationBehaviour");
    }
  }

  /**
   * List tags.
   */

  record CACommandTagList()
    implements CACommandType
  {

  }

  /**
   * Create or update tags.
   *
   * @param tags The tags
   */

  record CACommandTagsPut(
    CATags tags)
    implements CACommandType
  {
    /**
     * Create or update tags.
     */

    public CACommandTagsPut
    {
      Objects.requireNonNull(tags, "tags");
    }
  }

  /**
   * Delete tags.
   *
   * @param tags The tags
   */

  record CACommandTagsDelete(
    CATags tags)
    implements CACommandType
  {
    /**
     * Delete tags.
     */

    public CACommandTagsDelete
    {
      Objects.requireNonNull(tags, "tags");
    }
  }

  /**
   * Add or update files.
   *
   * @param data The file
   */

  record CACommandFilePut(CAFileType data)
    implements CACommandType
  {
    /**
     * Add or update files.
     */

    public CACommandFilePut
    {
      Objects.requireNonNull(data, "data");
    }
  }

  /**
   * Delete files.
   *
   * @param data The file
   */

  record CACommandFileRemove(CAFileID data)
    implements CACommandType
  {
    /**
     * Delete files.
     */

    public CACommandFileRemove
    {
      Objects.requireNonNull(data, "data");
    }
  }
}
