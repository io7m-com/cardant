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

import com.io7m.cardant.model.CAIds;
import com.io7m.cardant.model.CAInventoryElementType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemLocations;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocations;
import com.io7m.cardant.model.CATags;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The base type of responses.
 */

public sealed interface CAResponseType extends CAMessageType
{
  /**
   * The type of responses that return data.
   */

  sealed interface CAResponseWithElementType extends CAResponseType
  {

    /**
     * @return The returned data
     */

    CAInventoryElementType data();
  }

  /**
   * A command failed.
   *
   * @param summary    The error summary
   * @param statusCode The status code
   * @param details    The error details
   * @param attributes The error attributes
   */

  record CAResponseError(
    String summary,
    int statusCode,
    Map<String, String> attributes,
    List<String> details)
    implements CAResponseType
  {
    /**
     * A command failed.
     *
     * @param summary    The error summary
     * @param statusCode The status code
     * @param details    The error details
     * @param attributes The error attributes
     */

    public CAResponseError
    {
      Objects.requireNonNull(summary, "summary");
      Objects.requireNonNull(attributes, "attributes");
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
   * @param data The returned item
   *
   * @see CACommandType.CACommandItemCreate
   */

  record CAResponseItemCreate(
    CAItem data)
    implements CAResponseWithElementType
  {
    /**
     * @see CACommandType.CACommandItemCreate
     */

    public CAResponseItemCreate
    {
      Objects.requireNonNull(data, "item");
    }
  }

  /**
   * @param data The returned item
   *
   * @see CACommandType.CACommandItemAttachmentRemove
   */

  record CAResponseItemAttachmentRemove(
    CAItem data)
    implements CAResponseWithElementType
  {
    /**
     * @see CACommandType.CACommandItemAttachmentRemove
     */

    public CAResponseItemAttachmentRemove
    {
      Objects.requireNonNull(data, "item");
    }
  }

  /**
   * @param data The returned item
   *
   * @see CACommandType.CACommandItemAttachmentPut
   */

  record CAResponseItemAttachmentPut(
    CAItem data)
    implements CAResponseWithElementType
  {
    /**
     * @see CACommandType.CACommandItemAttachmentPut
     */

    public CAResponseItemAttachmentPut
    {
      Objects.requireNonNull(data, "item");
    }
  }

  /**
   * @param data The returned item
   *
   * @see CACommandType.CACommandItemMetadataPut
   */

  record CAResponseItemMetadataPut(
    CAItem data)
    implements CAResponseWithElementType
  {
    /**
     * @see CACommandType.CACommandItemMetadataPut
     */

    public CAResponseItemMetadataPut
    {
      Objects.requireNonNull(data, "item");
    }
  }

  /**
   * @param data The returned item
   *
   * @see CACommandType.CACommandItemMetadataRemove
   */

  record CAResponseItemMetadataRemove(
    CAItem data)
    implements CAResponseWithElementType
  {
    /**
     * @see CACommandType.CACommandItemMetadataRemove
     */

    public CAResponseItemMetadataRemove
    {
      Objects.requireNonNull(data, "item");
    }
  }

  /**
   * @param data The returned items
   *
   * @see CACommandType.CACommandItemList
   */

  record CAResponseItemList(
    CAItems data)
    implements CAResponseWithElementType
  {
    /**
     * @see CACommandType.CACommandItemList
     */

    public CAResponseItemList
    {
      Objects.requireNonNull(data, "items");
    }
  }

  /**
   * @param data The returned item
   *
   * @see CACommandType.CACommandItemUpdate
   */

  record CAResponseItemUpdate(
    CAItem data)
    implements CAResponseWithElementType
  {
    /**
     * @see CACommandType.CACommandItemUpdate
     */

    public CAResponseItemUpdate
    {
      Objects.requireNonNull(data, "item");
    }
  }

  /**
   * @param data The returned locations
   *
   * @see CACommandType.CACommandItemLocationsList
   */

  record CAResponseItemLocationsList(
    CAItemLocations data)
    implements CAResponseWithElementType
  {
    /**
     * @see CACommandType.CACommandItemLocationsList
     */

    public CAResponseItemLocationsList
    {
      Objects.requireNonNull(data, "data");
    }
  }

  /**
   * @param data The returned item
   *
   * @see CACommandType.CACommandItemGet
   */

  record CAResponseItemGet(
    CAItem data)
    implements CAResponseWithElementType
  {
    /**
     * @see CACommandType.CACommandItemGet
     */

    public CAResponseItemGet
    {
      Objects.requireNonNull(data, "item");
    }
  }

  /**
   * @param data The returned item IDs
   *
   * @see CACommandType.CACommandItemsRemove
   */

  record CAResponseItemsRemove(
    CAIds data)
    implements CAResponseWithElementType
  {
    /**
     * @param data The returned item IDs
     *
     * @see CACommandType.CACommandItemsRemove
     */

    public CAResponseItemsRemove
    {
      Objects.requireNonNull(data, "id");
    }
  }

  /**
   * @param data The returned item ID
   *
   * @see CACommandType.CACommandItemReposit
   */

  record CAResponseItemReposit(
    CAItemID data)
    implements CAResponseWithElementType
  {
    /**
     * @see CACommandType.CACommandItemReposit
     */

    public CAResponseItemReposit
    {
      Objects.requireNonNull(data, "id");
    }
  }

  /**
   * @param data The returned tags
   *
   * @see CACommandType.CACommandTagList
   */

  record CAResponseTagList(
    CATags data)
    implements CAResponseWithElementType
  {
    /**
     * @see CACommandType.CACommandTagList
     */

    public CAResponseTagList
    {
      Objects.requireNonNull(data, "tags");
    }
  }

  /**
   * @param data The returned tags
   *
   * @see CACommandType.CACommandTagsPut
   */

  record CAResponseTagsPut(
    CATags data)
    implements CAResponseWithElementType
  {
    public CAResponseTagsPut
    {
      Objects.requireNonNull(data, "tags");
    }
  }

  /**
   * @param data The returned tags
   *
   * @see CACommandType.CACommandTagsDelete
   */

  record CAResponseTagsDelete(
    CATags data)
    implements CAResponseWithElementType
  {
    /**
     * @see CACommandType.CACommandTagsDelete
     */

    public CAResponseTagsDelete
    {
      Objects.requireNonNull(data, "tags");
    }
  }

  /**
   * @param data The returned location
   *
   * @see CACommandType.CACommandLocationGet
   */

  record CAResponseLocationGet(
    CALocation data)
    implements CAResponseWithElementType
  {
    /**
     * @see CACommandType.CACommandLocationGet
     */

    public CAResponseLocationGet
    {
      Objects.requireNonNull(data, "location");
    }
  }

  /**
   * @param data The returned location
   *
   * @see CACommandType.CACommandLocationPut
   */

  record CAResponseLocationPut(
    CALocation data)
    implements CAResponseWithElementType
  {
    /**
     * @see CACommandType.CACommandLocationPut
     */

    public CAResponseLocationPut
    {
      Objects.requireNonNull(data, "location");
    }
  }

  /**
   * @param data The returned locations
   *
   * @see CACommandType.CACommandLocationList
   */

  record CAResponseLocationList(
    CALocations data)
    implements CAResponseWithElementType
  {
    /**
     * @see CACommandType.CACommandLocationList
     */

    public CAResponseLocationList
    {
      Objects.requireNonNull(data, "locations");
    }
  }
}
