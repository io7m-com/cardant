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

import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemAttachmentID;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItemRepositType;
import com.io7m.cardant.model.CAListLocationBehaviourType;
import com.io7m.cardant.model.CATags;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public sealed interface CACommandType
  extends CAMessageType
{
  record CACommandLoginUsernamePassword(
    String user,
    String password)
    implements CACommandType
  {
    public CACommandLoginUsernamePassword
    {
      Objects.requireNonNull(user, "user");
      Objects.requireNonNull(password, "password");
    }
  }

  record CACommandItemCreate(
    CAItemID id,
    String name)
    implements CACommandType
  {
    public CACommandItemCreate
    {
      Objects.requireNonNull(id, "id");
      Objects.requireNonNull(name, "name");
    }
  }

  record CACommandItemUpdate(
    CAItemID id,
    String name)
    implements CACommandType
  {
    public CACommandItemUpdate
    {
      Objects.requireNonNull(id, "id");
      Objects.requireNonNull(name, "name");
    }
  }

  record CACommandItemAttachmentRemove(
    CAItemID item,
    CAItemAttachmentID attachmentID)
    implements CACommandType
  {
    public CACommandItemAttachmentRemove
    {
      Objects.requireNonNull(item, "item");
      Objects.requireNonNull(attachmentID, "attachmentID");
    }
  }

  record CACommandItemAttachmentPut(
    CAItemID item,
    CAItemAttachment attachment)
    implements CACommandType
  {
    public CACommandItemAttachmentPut
    {
      Objects.requireNonNull(item, "item");
      Objects.requireNonNull(attachment, "attachment");
    }
  }

  record CACommandItemMetadataRemove(
    CAItemID item,
    Set<String> metadataNames)
    implements CACommandType
  {
    public CACommandItemMetadataRemove
    {
      Objects.requireNonNull(item, "item");
      Objects.requireNonNull(metadataNames, "metadataNames");
    }
  }

  record CACommandItemMetadataPut(
    CAItemID item,
    Set<CAItemMetadata> metadatas)
    implements CACommandType
  {
    public CACommandItemMetadataPut
    {
      Objects.requireNonNull(item, "item");
      Objects.requireNonNull(metadatas, "metadatas");
    }
  }

  record CACommandLocationPut()
    implements CACommandType
  {

  }

  record CACommandLocationList()
    implements CACommandType
  {

  }

  record CACommandLocationGet()
    implements CACommandType
  {

  }

  record CACommandItemReposit(
    CAItemRepositType reposit)
    implements CACommandType
  {
    public CACommandItemReposit
    {
      Objects.requireNonNull(reposit, "reposit");
    }
  }

  record CACommandItemLocationList()
    implements CACommandType
  {

  }

  record CACommandItemGet(
    CAItemID id)
    implements CACommandType
  {
    public CACommandItemGet
    {
      Objects.requireNonNull(id, "id");
    }
  }

  record CACommandItemRemove(
    CAItemID id)
    implements CACommandType
  {
    public CACommandItemRemove
    {
      Objects.requireNonNull(id, "id");
    }
  }

  record CACommandItemList(
    CAListLocationBehaviourType locationBehaviour)
    implements CACommandType
  {
    public CACommandItemList
    {
      Objects.requireNonNull(locationBehaviour, "locationBehaviour");
    }
  }

  record CACommandTagList()
    implements CACommandType
  {

  }

  record CACommandTagsPut(
    CATags tags)
    implements CACommandType
  {
    public CACommandTagsPut
    {
      Objects.requireNonNull(tags, "tags");
    }
  }

  record CACommandTagsDelete(
    CATags tags)
    implements CACommandType
  {
    public CACommandTagsDelete
    {
      Objects.requireNonNull(tags, "tags");
    }
  }
}
