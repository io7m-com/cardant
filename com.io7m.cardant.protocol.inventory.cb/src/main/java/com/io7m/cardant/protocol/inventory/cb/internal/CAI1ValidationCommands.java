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

package com.io7m.cardant.protocol.inventory.cb.internal;

import com.io7m.cardant.protocol.inventory.CAICommandFilePut;
import com.io7m.cardant.protocol.inventory.CAICommandFileRemove;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentRemove;
import com.io7m.cardant.protocol.inventory.CAICommandItemCreate;
import com.io7m.cardant.protocol.inventory.CAICommandItemGet;
import com.io7m.cardant.protocol.inventory.CAICommandItemLocationsList;
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataPut;
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataRemove;
import com.io7m.cardant.protocol.inventory.CAICommandItemReposit;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAICommandItemUpdate;
import com.io7m.cardant.protocol.inventory.CAICommandItemsRemove;
import com.io7m.cardant.protocol.inventory.CAICommandLocationGet;
import com.io7m.cardant.protocol.inventory.CAICommandLocationList;
import com.io7m.cardant.protocol.inventory.CAICommandLocationPut;
import com.io7m.cardant.protocol.inventory.CAICommandLogin;
import com.io7m.cardant.protocol.inventory.CAICommandRolesAssign;
import com.io7m.cardant.protocol.inventory.CAICommandRolesGet;
import com.io7m.cardant.protocol.inventory.CAICommandRolesRevoke;
import com.io7m.cardant.protocol.inventory.CAICommandTagList;
import com.io7m.cardant.protocol.inventory.CAICommandTagsDelete;
import com.io7m.cardant.protocol.inventory.CAICommandTagsPut;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.cb.CAI1Command;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandFilePut;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandFileRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemAttachmentAdd;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemAttachmentRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemCreate;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemLocationsList;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemMetadataPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemMetadataRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemReposit;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemSearchBegin;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemSearchNext;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemSearchPrevious;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemUpdate;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemsRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandLocationGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandLocationList;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandLocationPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandLogin;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandRolesAssign;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandRolesGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandRolesRevoke;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTagList;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTagsDelete;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTagsPut;
import com.io7m.cardant.protocol.inventory.cb.ProtocolCAIv1Type;
import com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon.ProtocolUncheckedException;
import com.io7m.cedarbridge.runtime.api.CBList;
import com.io7m.cedarbridge.runtime.api.CBString;
import com.io7m.idstore.model.IdName;
import com.io7m.medrina.api.MRoleName;

import java.util.stream.Collectors;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon.convertFromWireUUID;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon.convertToWireUUID;

public final class CAI1ValidationCommands
{
  private CAI1ValidationCommands()
  {

  }

  public static ProtocolCAIv1Type convertToWireCommand(
    final CAICommandType<?> cmd)
  {
    if (cmd instanceof final CAICommandFilePut c) {
      return convertToWireCommandCAICommandFilePut(c);
    }
    if (cmd instanceof final CAICommandFileRemove c) {
      return convertToWireCommandCAICommandFileRemove(c);
    }
    if (cmd instanceof final CAICommandItemAttachmentAdd c) {
      return convertToWireCommandCAICommandItemAttachmentAdd(c);
    }
    if (cmd instanceof final CAICommandItemAttachmentRemove c) {
      return convertToWireCommandCAICommandItemAttachmentRemove(c);
    }
    if (cmd instanceof final CAICommandItemCreate c) {
      return convertToWireCommandCAICommandItemCreate(c);
    }
    if (cmd instanceof final CAICommandItemGet c) {
      return convertToWireCommandCAICommandItemGet(c);
    }
    if (cmd instanceof final CAICommandItemLocationsList c) {
      return convertToWireCommandCAICommandItemLocationsList(c);
    }
    if (cmd instanceof final CAICommandItemMetadataPut c) {
      return convertToWireCommandCAICommandItemMetadataPut(c);
    }
    if (cmd instanceof final CAICommandItemMetadataRemove c) {
      return convertToWireCommandCAICommandItemMetadataRemove(c);
    }
    if (cmd instanceof final CAICommandItemReposit c) {
      return convertToWireCommandCAICommandItemReposit(c);
    }
    if (cmd instanceof final CAICommandItemsRemove c) {
      return convertToWireCommandCAICommandItemsRemove(c);
    }
    if (cmd instanceof final CAICommandItemUpdate c) {
      return convertToWireCommandCAICommandItemUpdate(c);
    }
    if (cmd instanceof final CAICommandLocationGet c) {
      return convertToWireCommandCAICommandLocationGet(c);
    }
    if (cmd instanceof final CAICommandLocationList c) {
      return convertToWireCommandCAICommandLocationList(c);
    }
    if (cmd instanceof final CAICommandLocationPut c) {
      return convertToWireCommandCAICommandLocationPut(c);
    }
    if (cmd instanceof final CAICommandLogin c) {
      return convertToWireCommandCAICommandLogin(c);
    }
    if (cmd instanceof final CAICommandTagList c) {
      return convertToWireCommandCAICommandTagList(c);
    }
    if (cmd instanceof final CAICommandTagsDelete c) {
      return convertToWireCommandCAICommandTagsDelete(c);
    }
    if (cmd instanceof final CAICommandTagsPut c) {
      return convertToWireCommandCAICommandTagsPut(c);
    }
    if (cmd instanceof final CAICommandItemSearchBegin c) {
      return convertToWireCommandCAICommandItemSearchBegin(c);
    }
    if (cmd instanceof final CAICommandItemSearchNext c) {
      return convertToWireCommandCAICommandItemSearchNext(c);
    }
    if (cmd instanceof final CAICommandItemSearchPrevious c) {
      return convertToWireCommandCAICommandItemSearchPrevious(c);
    }
    if (cmd instanceof final CAICommandRolesRevoke c) {
      return convertToWireCommandCAICommandRolesRevoke(c);
    }
    if (cmd instanceof final CAICommandRolesAssign c) {
      return convertToWireCommandCAICommandRolesAssign(c);
    }
    if (cmd instanceof final CAICommandRolesGet c) {
      return convertToWireCommandCAICommandRolesGet(c);
    }

    throw new ProtocolUncheckedException(CAI1ValidationCommon.errorProtocol(cmd));
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandRolesGet(
    final CAICommandRolesGet c)
  {
    return new CAI1CommandRolesGet(
      convertToWireUUID(c.user())
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandRolesAssign(
    final CAICommandRolesAssign c)
  {
    return new CAI1CommandRolesAssign(
      convertToWireUUID(c.user()),
      new CBList<>(
      c.roles()
        .stream()
        .map(r -> new CBString(r.value()))
        .toList()
      )
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandRolesRevoke(
    final CAICommandRolesRevoke c)
  {
    return new CAI1CommandRolesRevoke(
      convertToWireUUID(c.user()),
      new CBList<>(
        c.roles()
          .stream()
          .map(r -> new CBString(r.value()))
          .toList()
      )
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemSearchPrevious(
    final CAICommandItemSearchPrevious c)
  {
    return new CAI1CommandItemSearchPrevious();
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemSearchNext(
    final CAICommandItemSearchNext c)
  {
    return new CAI1CommandItemSearchNext();
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemSearchBegin(
    final CAICommandItemSearchBegin c)
  {
    return new CAI1CommandItemSearchBegin(
      CAI1ValidationCommon.convertToWireItemSearchParameters(c.parameters())
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemReposit(
    final CAICommandItemReposit c)
  {
    return new CAI1CommandItemReposit(
      CAI1ValidationCommon.convertToWireItemReposit(c.reposit())
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemsRemove(
    final CAICommandItemsRemove c)
  {
    return new CAI1CommandItemsRemove(
      new CBList<>(
        c.ids()
          .stream()
          .map(i -> convertToWireUUID(i.id()))
          .toList())
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemUpdate(
    final CAICommandItemUpdate c)
  {
    return new CAI1CommandItemUpdate(
      convertToWireUUID(c.id().id()),
      new CBString(c.name())
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandLogin(
    final CAICommandLogin c)
  {
    return new CAI1CommandLogin(
      new CBString(c.userName().value()),
      new CBString(c.password()),
      CAI1ValidationCommon.convertToWireStringMap(c.metadata())
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandTagsPut(
    final CAICommandTagsPut c)
  {
    return new CAI1CommandTagsPut(
      new CBList<>(
        c.tags()
          .tags()
          .stream()
          .map(CAI1ValidationCommon::convertToWireTag)
          .toList()
      )
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandTagsDelete(
    final CAICommandTagsDelete c)
  {
    return new CAI1CommandTagsDelete(
      new CBList<>(
        c.tags()
          .tags()
          .stream()
          .map(CAI1ValidationCommon::convertToWireTag)
          .toList()
      )
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandTagList(
    final CAICommandTagList c)
  {
    return new CAI1CommandTagList();
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandLocationPut(
    final CAICommandLocationPut c)
  {
    return new CAI1CommandLocationPut(CAI1ValidationCommon.convertToWireLocation(
      c.location()));
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandLocationGet(
    final CAICommandLocationGet c)
  {
    return new CAI1CommandLocationGet(convertToWireUUID(c.id().id()));
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandLocationList(
    final CAICommandLocationList c)
  {
    return new CAI1CommandLocationList();
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemMetadataRemove(
    final CAICommandItemMetadataRemove c)
  {
    return new CAI1CommandItemMetadataRemove(
      convertToWireUUID(c.item().id()),
      new CBList<>(
        c.metadataNames()
          .stream()
          .map(CBString::new)
          .toList()
      )
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemMetadataPut(
    final CAICommandItemMetadataPut c)
  {
    return new CAI1CommandItemMetadataPut(
      convertToWireUUID(c.item().id()),
      new CBList<>(
        c.metadatas()
          .stream()
          .map(CAI1ValidationCommon::convertToWireItemMetadata)
          .toList()
      )
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemLocationsList(
    final CAICommandItemLocationsList c)
  {
    return new CAI1CommandItemLocationsList(
      convertToWireUUID(c.item().id())
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemGet(
    final CAICommandItemGet c)
  {
    return new CAI1CommandItemGet(convertToWireUUID(c.id().id()));
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemCreate(
    final CAICommandItemCreate c)
  {
    return new CAI1CommandItemCreate(
      convertToWireUUID(c.id().id()),
      new CBString(c.name())
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemAttachmentRemove(
    final CAICommandItemAttachmentRemove c)
  {
    return new CAI1CommandItemAttachmentRemove(
      convertToWireUUID(c.item().id()),
      convertToWireUUID(c.file().id()),
      new CBString(c.relation())
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemAttachmentAdd(
    final CAICommandItemAttachmentAdd c)
  {
    return new CAI1CommandItemAttachmentAdd(
      convertToWireUUID(c.item().id()),
      convertToWireUUID(c.file().id()),
      new CBString(c.relation())
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandFileRemove(
    final CAICommandFileRemove c)
  {
    return new CAI1CommandFileRemove(convertToWireUUID(c.data().id()));
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandFilePut(
    final CAICommandFilePut c)
  {
    return new CAI1CommandFilePut(CAI1ValidationCommon.convertToWireFile(c.data()));
  }

  public static CAIMessageType convertFromWireCAI1CommandItemReposit(
    final CAI1CommandItemReposit m)
  {
    return new CAICommandItemReposit(
      CAI1ValidationCommon.convertFromWireReposit(m.fieldReposit())
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandItemUpdate(
    final CAI1CommandItemUpdate m)
  {
    return new CAICommandItemUpdate(
      CAI1ValidationCommon.convertFromWireItemID(m.fieldItem()),
      m.fieldName().value()
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandItemsRemove(
    final CAI1CommandItemsRemove m)
  {
    return new CAICommandItemsRemove(
      m.fieldItems()
        .values()
        .stream()
        .map(CAI1ValidationCommon::convertFromWireItemID)
        .collect(Collectors.toUnmodifiableSet())
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandLocationGet(
    final CAI1CommandLocationGet m)
  {
    return new CAICommandLocationGet(CAI1ValidationCommon.convertFromWireLocationId(
      m.fieldLocation()));
  }

  public static CAIMessageType convertFromWireCAI1CommandLocationPut(
    final CAI1CommandLocationPut m)
  {
    return new CAICommandLocationPut(
      CAI1ValidationCommon.convertFromWireLocation(m.fieldLocation())
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandLocationList(
    final CAI1CommandLocationList m)
  {
    return new CAICommandLocationList();
  }

  public static CAIMessageType convertFromWireCAI1CommandTagsPut(
    final CAI1CommandTagsPut m)
  {
    return new CAICommandTagsPut(CAI1ValidationCommon.convertFromWireTags(m.fieldTags())
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandTagsDelete(
    final CAI1CommandTagsDelete m)
  {
    return new CAICommandTagsDelete(CAI1ValidationCommon.convertFromWireTags(m.fieldTags()));
  }

  public static CAIMessageType convertFromWireCAI1CommandTagList(
    final CAI1CommandTagList m)
  {
    return new CAICommandTagList();
  }

  public static CAIMessageType convertFromWireCAI1CommandLogin(
    final CAI1CommandLogin m)
  {
    return new CAICommandLogin(
      new IdName(m.fieldUserName().value()),
      m.fieldPassword().value(),
      CAI1ValidationCommon.convertFromWireStringMap(m.fieldMetadata())
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandItemMetadataRemove(
    final CAI1CommandItemMetadataRemove m)
  {
    return new CAICommandItemMetadataRemove(
      CAI1ValidationCommon.convertFromWireItemID(m.fieldItemId()),
      m.fieldMetadatas()
        .values()
        .stream()
        .map(CBString::value)
        .collect(Collectors.toUnmodifiableSet())
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandItemMetadataPut(
    final CAI1CommandItemMetadataPut m)
  {
    return new CAICommandItemMetadataPut(
      CAI1ValidationCommon.convertFromWireItemID(m.fieldItemId()),
      m.fieldMetadatas()
        .values()
        .stream()
        .map(CAI1ValidationCommon::convertFromWireItemMetadata)
        .collect(Collectors.toUnmodifiableSet())
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandItemLocationsList(
    final CAI1CommandItemLocationsList m)
  {
    return new CAICommandItemLocationsList(
      CAI1ValidationCommon.convertFromWireItemID(m.fieldItemId())
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandItemGet(
    final CAI1CommandItemGet m)
  {
    return new CAICommandItemGet(
      CAI1ValidationCommon.convertFromWireItemID(m.fieldItemId())
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandItemCreate(
    final CAI1CommandItemCreate m)
  {
    return new CAICommandItemCreate(
      CAI1ValidationCommon.convertFromWireItemID(m.fieldItemId()),
      m.fieldName().value()
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandItemAttachmentRemove(
    final CAI1CommandItemAttachmentRemove m)
  {
    return new CAICommandItemAttachmentRemove(
      CAI1ValidationCommon.convertFromWireItemID(m.fieldItemId()),
      CAI1ValidationCommon.convertFromWireFileId(m.fieldFileId()),
      m.fieldRelation().value()
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandItemAttachmentAdd(
    final CAI1CommandItemAttachmentAdd m)
  {
    return new CAICommandItemAttachmentAdd(
      CAI1ValidationCommon.convertFromWireItemID(m.fieldItemId()),
      CAI1ValidationCommon.convertFromWireFileId(m.fieldFileId()),
      m.fieldRelation().value()
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandFileRemove(
    final CAI1CommandFileRemove m)
  {
    return new CAICommandFileRemove(
      CAI1ValidationCommon.convertFromWireFileId(m.fieldId())
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandFilePut(
    final CAI1CommandFilePut m)
  {
    return new CAICommandFilePut(
      CAI1ValidationCommon.convertFromWireFile(m.fieldFile())
    );
  }

  public static CAI1Command convertToWireWrapTransactionCommand(
    final ProtocolCAIv1Type msg)
  {
    if (msg instanceof final CAI1CommandFilePut c) {
      return new CAI1Command.C1CommandFilePut(c);
    }
    if (msg instanceof final CAI1CommandFileRemove c) {
      return new CAI1Command.C1CommandFileRemove(c);
    }
    if (msg instanceof final CAI1CommandItemAttachmentAdd c) {
      return new CAI1Command.C1CommandItemAttachmentAdd(c);
    }
    if (msg instanceof final CAI1CommandItemAttachmentRemove c) {
      return new CAI1Command.C1CommandItemAttachmentRemove(c);
    }
    if (msg instanceof final CAI1CommandItemCreate c) {
      return new CAI1Command.C1CommandItemCreate(c);
    }
    if (msg instanceof final CAI1CommandItemGet c) {
      return new CAI1Command.C1CommandItemGet(c);
    }
    if (msg instanceof final CAI1CommandItemLocationsList c) {
      return new CAI1Command.C1CommandItemLocationsList(c);
    }
    if (msg instanceof final CAI1CommandItemMetadataPut c) {
      return new CAI1Command.C1CommandItemMetadataPut(c);
    }
    if (msg instanceof final CAI1CommandItemMetadataRemove c) {
      return new CAI1Command.C1CommandItemMetadataRemove(c);
    }
    if (msg instanceof final CAI1CommandItemReposit c) {
      return new CAI1Command.C1CommandItemReposit(c);
    }
    if (msg instanceof final CAI1CommandItemUpdate c) {
      return new CAI1Command.C1CommandItemUpdate(c);
    }
    if (msg instanceof final CAI1CommandItemsRemove c) {
      return new CAI1Command.C1CommandItemsRemove(c);
    }
    if (msg instanceof final CAI1CommandLocationGet c) {
      return new CAI1Command.C1CommandLocationGet(c);
    }
    if (msg instanceof final CAI1CommandLocationList c) {
      return new CAI1Command.C1CommandLocationList(c);
    }
    if (msg instanceof final CAI1CommandLocationPut c) {
      return new CAI1Command.C1CommandLocationPut(c);
    }
    if (msg instanceof final CAI1CommandLogin c) {
      return new CAI1Command.C1CommandLogin(c);
    }
    if (msg instanceof final CAI1CommandTagList c) {
      return new CAI1Command.C1CommandTagList(c);
    }
    if (msg instanceof final CAI1CommandTagsDelete c) {
      return new CAI1Command.C1CommandTagsDelete(c);
    }
    if (msg instanceof final CAI1CommandTagsPut c) {
      return new CAI1Command.C1CommandTagsPut(c);
    }
    if (msg instanceof final CAI1CommandItemSearchBegin c) {
      return new CAI1Command.C1CommandItemSearchBegin(c);
    }
    if (msg instanceof final CAI1CommandItemSearchNext c) {
      return new CAI1Command.C1CommandItemSearchNext(c);
    }
    if (msg instanceof final CAI1CommandItemSearchPrevious c) {
      return new CAI1Command.C1CommandItemSearchPrevious(c);
    }
    if (msg instanceof final CAI1CommandRolesAssign c) {
      return new CAI1Command.C1CommandRolesAssign(c);
    }
    if (msg instanceof final CAI1CommandRolesRevoke c) {
      return new CAI1Command.C1CommandRolesRevoke(c);
    }

    throw new IllegalStateException();
  }

  public static CAIMessageType convertFromWireCAI1CommandItemSearchBegin(
    final CAI1CommandItemSearchBegin m)
  {
    return new CAICommandItemSearchBegin(
      CAI1ValidationCommon.convertFromWireItemSearchParameters(m.fieldParameters())
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandItemSearchNext(
    final CAI1CommandItemSearchNext m)
  {
    return new CAICommandItemSearchNext();
  }

  public static CAIMessageType convertFromWireCAI1CommandItemSearchPrevious(
    final CAI1CommandItemSearchPrevious m)
  {
    return new CAICommandItemSearchPrevious();
  }

  public static CAIMessageType convertFromWireCAI1CommandRolesAssign(
    final CAI1CommandRolesAssign m)
  {
    return new CAICommandRolesAssign(
      convertFromWireUUID(m.fieldUser()),
      m.fieldRoles()
        .values()
        .stream().map(CBString::value)
        .map(MRoleName::new)
        .collect(Collectors.toUnmodifiableSet())
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandRolesRevoke(
    final CAI1CommandRolesRevoke m)
  {
    return new CAICommandRolesRevoke(
      convertFromWireUUID(m.fieldUser()),
      m.fieldRoles()
        .values()
        .stream().map(CBString::value)
        .map(MRoleName::new)
        .collect(Collectors.toUnmodifiableSet())
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandRolesGet(
    final CAI1CommandRolesGet m)
  {
    return new CAICommandRolesGet(
      convertFromWireUUID(m.fieldUser())
    );
  }
}
