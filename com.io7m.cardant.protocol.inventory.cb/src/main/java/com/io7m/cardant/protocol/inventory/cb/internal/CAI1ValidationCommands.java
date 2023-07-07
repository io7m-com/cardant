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

import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.protocol.inventory.CAICommandFileGet;
import com.io7m.cardant.protocol.inventory.CAICommandFilePut;
import com.io7m.cardant.protocol.inventory.CAICommandFileRemove;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchPrevious;
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
import com.io7m.cardant.protocol.inventory.CAICommandItemSetName;
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
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarPut;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.cb.CAI1Command;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandFileGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandFilePut;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandFileRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandFileSearchBegin;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandFileSearchNext;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandFileSearchPrevious;
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
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemSetName;
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
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeScalarPut;
import com.io7m.cardant.protocol.inventory.cb.ProtocolCAIv1Type;
import com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon.ProtocolUncheckedException;
import com.io7m.cedarbridge.runtime.api.CBString;
import com.io7m.cedarbridge.runtime.api.CBUUID;
import com.io7m.cedarbridge.runtime.convenience.CBLists;
import com.io7m.cedarbridge.runtime.convenience.CBMaps;
import com.io7m.cedarbridge.runtime.convenience.CBSets;
import com.io7m.idstore.model.IdName;
import com.io7m.lanark.core.RDottedName;
import com.io7m.medrina.api.MRoleName;

import java.util.stream.Collectors;

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
    if (cmd instanceof final CAICommandItemSetName c) {
      return convertToWireCommandCAICommandItemSetName(c);
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
    if (cmd instanceof final CAICommandFileSearchBegin c) {
      return convertToWireCommandCAICommandFileSearchBegin(c);
    }
    if (cmd instanceof final CAICommandFileSearchNext c) {
      return convertToWireCommandCAICommandFileSearchNext(c);
    }
    if (cmd instanceof final CAICommandFileSearchPrevious c) {
      return convertToWireCommandCAICommandFileSearchPrevious(c);
    }
    if (cmd instanceof final CAICommandFileGet c) {
      return convertToWireCommandCAICommandFileGet(c);
    }
    if (cmd instanceof final CAICommandTypeScalarPut c) {
      return convertToWireCommandCAICommandTypeScalarPut(c);
    }

    throw new ProtocolUncheckedException(CAI1ValidationCommon.errorProtocol(cmd));
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandTypeScalarPut(
    final CAICommandTypeScalarPut c)
  {
    return new CAI1CommandTypeScalarPut(
      CBLists.ofCollection(
        c.types(), CAI1ValidationCommon::convertToWireTypeScalar)
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandRolesGet(
    final CAICommandRolesGet c)
  {
    return new CAI1CommandRolesGet(
      new CBUUID(c.user())
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandRolesAssign(
    final CAICommandRolesAssign c)
  {
    return new CAI1CommandRolesAssign(
      new CBUUID(c.user()),
      CBLists.ofCollection(c.roles(), r -> new CBString(r.value().value()))
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandRolesRevoke(
    final CAICommandRolesRevoke c)
  {
    return new CAI1CommandRolesRevoke(
      new CBUUID(c.user()),
      CBLists.ofCollection(c.roles(), r -> new CBString(r.value().value()))
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandFileSearchPrevious(
    final CAICommandFileSearchPrevious c)
  {
    return new CAI1CommandFileSearchPrevious();
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandFileSearchNext(
    final CAICommandFileSearchNext c)
  {
    return new CAI1CommandFileSearchNext();
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandFileSearchBegin(
    final CAICommandFileSearchBegin c)
  {
    return new CAI1CommandFileSearchBegin(
      CAI1ValidationCommon.convertToWireFileSearchParameters(c.parameters())
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
      CBLists.ofCollection(c.ids(), x -> new CBUUID(x.id()))
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemSetName(
    final CAICommandItemSetName c)
  {
    return new CAI1CommandItemSetName(
      new CBUUID(c.id().id()),
      new CBString(c.name())
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandLogin(
    final CAICommandLogin c)
  {
    return new CAI1CommandLogin(
      new CBString(c.userName().value()),
      new CBString(c.password()),
      CBMaps.ofMapString(c.metadata())
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandTagsPut(
    final CAICommandTagsPut c)
  {
    return new CAI1CommandTagsPut(
      CBLists.ofCollection(
        c.tags().tags(),
        CAI1ValidationCommon::convertToWireTag
      )
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandTagsDelete(
    final CAICommandTagsDelete c)
  {
    return new CAI1CommandTagsDelete(
      CBLists.ofCollection(
        c.tags().tags(),
        CAI1ValidationCommon::convertToWireTag
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
    return new CAI1CommandLocationGet(new CBUUID(c.id().id()));
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
      new CBUUID(c.item().id()),
      CBLists.ofCollectionString(
        c.metadataNames()
          .stream()
          .map(RDottedName::value)
          .toList()
      )
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemMetadataPut(
    final CAICommandItemMetadataPut c)
  {
    return new CAI1CommandItemMetadataPut(
      new CBUUID(c.item().id()),
      CBLists.ofCollection(
        c.metadatas(),
        CAI1ValidationCommon::convertToWireItemMetadata
      )
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemLocationsList(
    final CAICommandItemLocationsList c)
  {
    return new CAI1CommandItemLocationsList(
      new CBUUID(c.item().id())
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemGet(
    final CAICommandItemGet c)
  {
    return new CAI1CommandItemGet(new CBUUID(c.id().id()));
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandFileGet(
    final CAICommandFileGet c)
  {
    return new CAI1CommandFileGet(new CBUUID(c.id().id()));
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemCreate(
    final CAICommandItemCreate c)
  {
    return new CAI1CommandItemCreate(
      new CBUUID(c.id().id()),
      new CBString(c.name())
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemAttachmentRemove(
    final CAICommandItemAttachmentRemove c)
  {
    return new CAI1CommandItemAttachmentRemove(
      new CBUUID(c.item().id()),
      new CBUUID(c.file().id()),
      new CBString(c.relation())
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandItemAttachmentAdd(
    final CAICommandItemAttachmentAdd c)
  {
    return new CAI1CommandItemAttachmentAdd(
      new CBUUID(c.item().id()),
      new CBUUID(c.file().id()),
      new CBString(c.relation())
    );
  }

  private static ProtocolCAIv1Type convertToWireCommandCAICommandFileRemove(
    final CAICommandFileRemove c)
  {
    return new CAI1CommandFileRemove(new CBUUID(c.data().id()));
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

  public static CAIMessageType convertFromWireCAI1CommandItemSetName(
    final CAI1CommandItemSetName m)
  {
    return new CAICommandItemSetName(
      new CAItemID(m.fieldItem().value()),
      m.fieldName().value()
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandItemsRemove(
    final CAI1CommandItemsRemove m)
  {
    return new CAICommandItemsRemove(
      CBSets.toSet(m.fieldItems(), x -> new CAItemID(x.value()))
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandLocationGet(
    final CAI1CommandLocationGet m)
  {
    return new CAICommandLocationGet(
      new CALocationID(m.fieldLocation().value())
    );
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
      CBMaps.toMapString(m.fieldMetadata())
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandItemMetadataRemove(
    final CAI1CommandItemMetadataRemove m)
  {
    return new CAICommandItemMetadataRemove(
      new CAItemID(m.fieldItemId().value()),
      CBSets.toSetString(m.fieldMetadatas())
        .stream()
        .map(RDottedName::new)
        .collect(Collectors.toUnmodifiableSet())
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandItemMetadataPut(
    final CAI1CommandItemMetadataPut m)
  {
    return new CAICommandItemMetadataPut(
      new CAItemID(m.fieldItemId().value()),
      CBSets.toSet(
        m.fieldMetadatas(),
        CAI1ValidationCommon::convertFromWireItemMetadata
      )
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandItemLocationsList(
    final CAI1CommandItemLocationsList m)
  {
    return new CAICommandItemLocationsList(
      new CAItemID(m.fieldItemId().value())
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandItemGet(
    final CAI1CommandItemGet m)
  {
    return new CAICommandItemGet(
      new CAItemID(m.fieldItemId().value())
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandFileGet(
    final CAI1CommandFileGet m)
  {
    return new CAICommandFileGet(
      new CAFileID(m.fieldFileId().value())
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandItemCreate(
    final CAI1CommandItemCreate m)
  {
    return new CAICommandItemCreate(
      new CAItemID(m.fieldItemId().value()),
      m.fieldName().value()
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandItemAttachmentRemove(
    final CAI1CommandItemAttachmentRemove m)
  {
    return new CAICommandItemAttachmentRemove(
      new CAItemID(m.fieldItemId().value()),
      new CAFileID(m.fieldFileId().value()),
      m.fieldRelation().value()
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandItemAttachmentAdd(
    final CAI1CommandItemAttachmentAdd m)
  {
    return new CAICommandItemAttachmentAdd(
      new CAItemID(m.fieldItemId().value()),
      new CAFileID(m.fieldFileId().value()),
      m.fieldRelation().value()
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandFileRemove(
    final CAI1CommandFileRemove m)
  {
    return new CAICommandFileRemove(
      new CAFileID(m.fieldId().value())
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
    if (msg instanceof final CAI1CommandItemSetName c) {
      return new CAI1Command.C1CommandItemSetName(c);
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
    if (msg instanceof final CAI1CommandFileGet c) {
      return new CAI1Command.C1CommandFileGet(c);
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

  public static CAIMessageType convertFromWireCAI1CommandFileSearchBegin(
    final CAI1CommandFileSearchBegin m)
  {
    return new CAICommandFileSearchBegin(
      CAI1ValidationCommon.convertFromWireFileSearchParameters(m.fieldParameters())
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandFileSearchNext(
    final CAI1CommandFileSearchNext m)
  {
    return new CAICommandFileSearchNext();
  }

  public static CAIMessageType convertFromWireCAI1CommandFileSearchPrevious(
    final CAI1CommandFileSearchPrevious m)
  {
    return new CAICommandFileSearchPrevious();
  }

  public static CAIMessageType convertFromWireCAI1CommandRolesAssign(
    final CAI1CommandRolesAssign m)
  {
    return new CAICommandRolesAssign(
      m.fieldUser().value(),
      CBSets.toSet(m.fieldRoles(), x -> MRoleName.of(x.value()))
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandRolesRevoke(
    final CAI1CommandRolesRevoke m)
  {
    return new CAICommandRolesRevoke(
      m.fieldUser().value(),
      CBSets.toSet(m.fieldRoles(), x -> MRoleName.of(x.value()))
    );
  }

  public static CAIMessageType convertFromWireCAI1CommandRolesGet(
    final CAI1CommandRolesGet m)
  {
    return new CAICommandRolesGet(m.fieldUser().value());
  }

  public static CAIMessageType convertFromWireCommandTypeScalarPut(
    final CAI1CommandTypeScalarPut c)
  {
    return new CAICommandTypeScalarPut(
      CBSets.toSet(c.fieldTypes(),
                   CAI1ValidationCommon::convertFromWireTypeScalar)
    );
  }
}
