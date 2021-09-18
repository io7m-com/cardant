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

import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CATags;

import java.util.List;
import java.util.Objects;

/**
 * The base type of responses.
 */

public sealed interface CAResponseType extends CAMessageType
{
  /**
   * A command failed.
   *
   * @param status  The status code
   * @param details The error details
   * @param message The error message
   */

  record CAResponseError(
    int status,
    String message,
    List<String> details)
    implements CAResponseType
  {
    /**
     * A command failed.
     */

    public CAResponseError
    {
      Objects.requireNonNull(message, "message");
      Objects.requireNonNull(details, "details");
    }
  }

  /**
   * @see CACommandType.CACommandLoginUsernamePassword
   */

  record CAResponseLoginUsernamePassword()
    implements CAResponseType
  {

  }

  /**
   * @param item The returned item
   *
   * @see CACommandType.CACommandItemCreate
   */

  record CAResponseItemCreate(
    CAItem item)
    implements CAResponseType
  {
    /**
     * @see CACommandType.CACommandItemCreate
     */

    public CAResponseItemCreate
    {
      Objects.requireNonNull(item, "item");
    }
  }

  /**
   * @param item The returned item
   *
   * @see CACommandType.CACommandItemAttachmentRemove
   */

  record CAResponseItemAttachmentRemove(
    CAItem item)
    implements CAResponseType
  {
    /**
     * @see CACommandType.CACommandItemAttachmentRemove
     */

    public CAResponseItemAttachmentRemove
    {
      Objects.requireNonNull(item, "item");
    }
  }

  /**
   * @param item The returned item
   *
   * @see CACommandType.CACommandItemAttachmentPut
   */

  record CAResponseItemAttachmentPut(
    CAItem item)
    implements CAResponseType
  {
    /**
     * @see CACommandType.CACommandItemAttachmentPut
     */

    public CAResponseItemAttachmentPut
    {
      Objects.requireNonNull(item, "item");
    }
  }

  /**
   * @param item The returned item
   *
   * @see CACommandType.CACommandItemMetadataPut
   */

  record CAResponseItemMetadataPut(
    CAItem item)
    implements CAResponseType
  {
    /**
     * @see CACommandType.CACommandItemMetadataPut
     */

    public CAResponseItemMetadataPut
    {
      Objects.requireNonNull(item, "item");
    }
  }

  /**
   * @param item The returned item
   *
   * @see CACommandType.CACommandItemMetadataRemove
   */

  record CAResponseItemMetadataRemove(
    CAItem item)
    implements CAResponseType
  {
    /**
     * @see CACommandType.CACommandItemMetadataRemove
     */

    public CAResponseItemMetadataRemove
    {
      Objects.requireNonNull(item, "item");
    }
  }

  /**
   * @param items The returned items
   *
   * @see CACommandType.CACommandItemList
   */

  record CAResponseItemList(
    CAItems items)
    implements CAResponseType
  {
    /**
     * @see CACommandType.CACommandItemList
     */

    public CAResponseItemList
    {
      Objects.requireNonNull(items, "items");
    }
  }

  /**
   * @param item The returned item
   *
   * @see CACommandType.CACommandItemUpdate
   */

  record CAResponseItemUpdate(
    CAItem item)
    implements CAResponseType
  {
    /**
     * @see CACommandType.CACommandItemUpdate
     */

    public CAResponseItemUpdate
    {
      Objects.requireNonNull(item, "item");
    }
  }

  /**
   * @param item The returned item
   *
   * @see CACommandType.CACommandItemGet
   */

  record CAResponseItemGet(
    CAItem item)
    implements CAResponseType
  {
    /**
     * @see CACommandType.CACommandItemGet
     */

    public CAResponseItemGet
    {
      Objects.requireNonNull(item, "item");
    }
  }

  /**
   * @param id The returned item ID
   *
   * @see CACommandType.CACommandItemRemove
   */

  record CAResponseItemRemove(
    CAItemID id)
    implements CAResponseType
  {
    /**
     * @see CACommandType.CACommandItemRemove
     */

    public CAResponseItemRemove
    {
      Objects.requireNonNull(id, "id");
    }
  }

  /**
   * @param id The returned item ID
   *
   * @see CACommandType.CACommandItemReposit
   */

  record CAResponseItemReposit(
    CAItemID id)
    implements CAResponseType
  {
    /**
     * @see CACommandType.CACommandItemReposit
     */

    public CAResponseItemReposit
    {
      Objects.requireNonNull(id, "id");
    }
  }

  /**
   * @param location The returned location
   *
   * @see CACommandType.CACommandLocationGet
   */

  record CAResponseLocationGet(
    CALocation location)
    implements CAResponseType
  {
    /**
     * @see CACommandType.CACommandLocationGet
     */

    public CAResponseLocationGet
    {
      Objects.requireNonNull(location, "location");
    }
  }

  /**
   * @param tags The returned tags
   *
   * @see CACommandType.CACommandTagList
   */

  record CAResponseTagList(
    CATags tags)
    implements CAResponseType
  {
    /**
     * @see CACommandType.CACommandTagList
     */

    public CAResponseTagList
    {
      Objects.requireNonNull(tags, "tags");
    }
  }

  /**
   * @param tags The returned tags
   *
   * @see CACommandType.CACommandTagsPut
   */

  record CAResponseTagsPut(
    CATags tags)
    implements CAResponseType
  {
    public CAResponseTagsPut
    {
      Objects.requireNonNull(tags, "tags");
    }
  }

  /**
   * @param tags The returned tags
   *
   * @see CACommandType.CACommandTagsDelete
   */

  record CAResponseTagsDelete(
    CATags tags)
    implements CAResponseType
  {
    /**
     * @see CACommandType.CACommandTagsDelete
     */

    public CAResponseTagsDelete
    {
      Objects.requireNonNull(tags, "tags");
    }
  }

  /**
   * @see CATransaction
   */

  record CAResponseTransaction()
    implements CAResponseType
  {

  }
}
