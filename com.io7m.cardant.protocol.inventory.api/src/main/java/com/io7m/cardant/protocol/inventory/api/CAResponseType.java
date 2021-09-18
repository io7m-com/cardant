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

public sealed interface CAResponseType extends CAMessageType
{
  record CAResponseError(
    int status,
    String message,
    List<String> details)
    implements CAResponseType
  {
    public CAResponseError
    {
      Objects.requireNonNull(message, "message");
      Objects.requireNonNull(details, "details");
    }
  }

  record CAResponseLoginUsernamePassword()
    implements CAResponseType
  {

  }

  record CAResponseItemCreate(
    CAItem item)
    implements CAResponseType
  {
    public CAResponseItemCreate
    {
      Objects.requireNonNull(item, "item");
    }
  }

  record CAResponseItemAttachmentRemove(
    CAItem item)
    implements CAResponseType
  {
    public CAResponseItemAttachmentRemove
    {
      Objects.requireNonNull(item, "item");
    }
  }

  record CAResponseItemAttachmentPut(
    CAItem item)
    implements CAResponseType
  {
    public CAResponseItemAttachmentPut
    {
      Objects.requireNonNull(item, "item");
    }
  }

  record CAResponseItemMetadataPut(
    CAItem item)
    implements CAResponseType
  {
    public CAResponseItemMetadataPut
    {
      Objects.requireNonNull(item, "item");
    }
  }

  record CAResponseItemMetadataRemove(
    CAItem item)
    implements CAResponseType
  {
    public CAResponseItemMetadataRemove
    {
      Objects.requireNonNull(item, "item");
    }
  }

  record CAResponseItemList(
    CAItems items)
    implements CAResponseType
  {
    public CAResponseItemList
    {
      Objects.requireNonNull(items, "items");
    }
  }

  record CAResponseItemUpdate(
    CAItem item)
    implements CAResponseType
  {
    public CAResponseItemUpdate
    {
      Objects.requireNonNull(item, "item");
    }
  }

  record CAResponseItemGet(
    CAItem item)
    implements CAResponseType
  {
    public CAResponseItemGet
    {
      Objects.requireNonNull(item, "item");
    }
  }

  record CAResponseItemRemove(
    CAItemID id)
    implements CAResponseType
  {
    public CAResponseItemRemove
    {
      Objects.requireNonNull(id, "id");
    }
  }

  record CAResponseItemReposit(
    CAItemID id)
    implements CAResponseType
  {
    public CAResponseItemReposit
    {
      Objects.requireNonNull(id, "id");
    }
  }

  record CAResponseLocationGet(
    CALocation location)
    implements CAResponseType
  {
    public CAResponseLocationGet
    {
      Objects.requireNonNull(location, "location");
    }
  }

  record CAResponseTagList(
    CATags tags)
    implements CAResponseType
  {
    public CAResponseTagList
    {
      Objects.requireNonNull(tags, "tags");
    }
  }

  record CAResponseTagsPut(
    CATags tags)
    implements CAResponseType
  {
    public CAResponseTagsPut
    {
      Objects.requireNonNull(tags, "tags");
    }
  }

  record CAResponseTagsDelete(
    CATags tags)
    implements CAResponseType
  {
    public CAResponseTagsDelete
    {
      Objects.requireNonNull(tags, "tags");
    }
  }

  record CAResponseTransaction()
    implements CAResponseType
  {

  }
}
