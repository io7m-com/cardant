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

import com.io7m.cardant.model.CATypeDeclarationSearchParameters;
import com.io7m.cardant.model.CATypeScalarSearchParameters;
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
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationGet;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationPut;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationRemove;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarGet;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarPut;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarRemove;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarSearchPrevious;
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
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeDeclarationGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeDeclarationPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeDeclarationRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeDeclarationSearchBegin;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeDeclarationSearchNext;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeDeclarationSearchPrevious;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeScalarGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeScalarPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeScalarRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeScalarSearchBegin;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeScalarSearchNext;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeScalarSearchPrevious;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeDeclarationSearchParameters;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeScalarSearchParameters;
import com.io7m.cardant.protocol.inventory.cb.ProtocolCAIv1Type;
import com.io7m.cedarbridge.runtime.api.CBCore;
import com.io7m.cedarbridge.runtime.api.CBOptionType;
import com.io7m.cedarbridge.runtime.api.CBString;
import com.io7m.cedarbridge.runtime.api.CBUUID;
import com.io7m.cedarbridge.runtime.convenience.CBLists;
import com.io7m.cedarbridge.runtime.convenience.CBMaps;
import com.io7m.lanark.core.RDottedName;

// CHECKSTYLE:OFF

public final class ToWireCommands
{
  private ToWireCommands()
  {

  }

  public static ProtocolCAIv1Type command(
    final CAICommandType<?> cmd)
  {
    if (cmd instanceof final CAICommandFilePut c) {
      return filePut(c);
    }
    if (cmd instanceof final CAICommandFileRemove c) {
      return fileRemove(c);
    }
    if (cmd instanceof final CAICommandItemAttachmentAdd c) {
      return itemAttachmentAdd(c);
    }
    if (cmd instanceof final CAICommandItemAttachmentRemove c) {
      return itemAttachmentRemove(c);
    }
    if (cmd instanceof final CAICommandItemCreate c) {
      return itemCreate(c);
    }
    if (cmd instanceof final CAICommandItemGet c) {
      return itemGet(c);
    }
    if (cmd instanceof final CAICommandItemLocationsList c) {
      return itemLocationsList(c);
    }
    if (cmd instanceof final CAICommandItemMetadataPut c) {
      return itemMetadataPut(c);
    }
    if (cmd instanceof final CAICommandItemMetadataRemove c) {
      return itemMetadataRemove(c);
    }
    if (cmd instanceof final CAICommandItemReposit c) {
      return itemReposit(c);
    }
    if (cmd instanceof final CAICommandItemsRemove c) {
      return itemsRemove(c);
    }
    if (cmd instanceof final CAICommandItemSetName c) {
      return itemSetName(c);
    }
    if (cmd instanceof final CAICommandLocationGet c) {
      return locationGet(c);
    }
    if (cmd instanceof final CAICommandLocationList c) {
      return locationList(c);
    }
    if (cmd instanceof final CAICommandLocationPut c) {
      return locationPut(c);
    }
    if (cmd instanceof final CAICommandLogin c) {
      return login(c);
    }
    if (cmd instanceof final CAICommandTagList c) {
      return tagList(c);
    }
    if (cmd instanceof final CAICommandTagsDelete c) {
      return tagsDelete(c);
    }
    if (cmd instanceof final CAICommandTagsPut c) {
      return tagsPut(c);
    }
    if (cmd instanceof final CAICommandItemSearchBegin c) {
      return itemSearchBegin(c);
    }
    if (cmd instanceof final CAICommandItemSearchNext c) {
      return itemSearchNext(c);
    }
    if (cmd instanceof final CAICommandItemSearchPrevious c) {
      return itemSearchPrevious(c);
    }
    if (cmd instanceof final CAICommandRolesRevoke c) {
      return rolesRevoke(c);
    }
    if (cmd instanceof final CAICommandRolesAssign c) {
      return rolesAssign(c);
    }
    if (cmd instanceof final CAICommandRolesGet c) {
      return rolesGet(c);
    }
    if (cmd instanceof final CAICommandFileSearchBegin c) {
      return fileSearchBegin(c);
    }
    if (cmd instanceof final CAICommandFileSearchNext c) {
      return fileSearchNext(c);
    }
    if (cmd instanceof final CAICommandFileSearchPrevious c) {
      return fileSearchPrevious(c);
    }
    if (cmd instanceof final CAICommandFileGet c) {
      return fileGet(c);
    }
    if (cmd instanceof final CAICommandTypeScalarPut c) {
      return typeScalarPut(c);
    }
    if (cmd instanceof final CAICommandTypeScalarGet c) {
      return typeScalarGet(c);
    }
    if (cmd instanceof final CAICommandTypeScalarSearchBegin c) {
      return typeScalarSearchBegin(c);
    }
    if (cmd instanceof final CAICommandTypeScalarSearchNext c) {
      return typeScalarSearchNext(c);
    }
    if (cmd instanceof final CAICommandTypeScalarSearchPrevious c) {
      return typeScalarSearchPrevious(c);
    }
    if (cmd instanceof final CAICommandTypeScalarRemove c) {
      return typeScalarRemove(c);
    }


    if (cmd instanceof final CAICommandTypeDeclarationPut c) {
      return typeDeclarationPut(c);
    }
    if (cmd instanceof final CAICommandTypeDeclarationGet c) {
      return typeDeclarationGet(c);
    }
    if (cmd instanceof final CAICommandTypeDeclarationSearchBegin c) {
      return typeDeclarationSearchBegin(c);
    }
    if (cmd instanceof final CAICommandTypeDeclarationSearchNext c) {
      return typeDeclarationSearchNext(c);
    }
    if (cmd instanceof final CAICommandTypeDeclarationSearchPrevious c) {
      return typeDeclarationSearchPrevious(c);
    }
    if (cmd instanceof final CAICommandTypeDeclarationRemove c) {
      return typeDeclarationRemove(c);
    }


    throw new ProtocolUncheckedException(CAI1ValidationCommon.errorProtocol(cmd));
  }

  private static ProtocolCAIv1Type typeScalarRemove(
    final CAICommandTypeScalarRemove c)
  {
    return new CAI1CommandTypeScalarRemove(
      CBLists.ofCollection(c.types(), r -> CBCore.string(r.value()))
    );
  }

  private static ProtocolCAIv1Type typeScalarSearchPrevious(
    final CAICommandTypeScalarSearchPrevious c)
  {
    return new CAI1CommandTypeScalarSearchPrevious();
  }

  private static ProtocolCAIv1Type typeScalarSearchNext(
    final CAICommandTypeScalarSearchNext c)
  {
    return new CAI1CommandTypeScalarSearchNext();
  }

  private static ProtocolCAIv1Type typeScalarSearchBegin(
    final CAICommandTypeScalarSearchBegin c)
  {
    return new CAI1CommandTypeScalarSearchBegin(
      typeScalarSearchParameters(c.parameters())
    );
  }

  private static CAI1TypeScalarSearchParameters typeScalarSearchParameters(
    final CATypeScalarSearchParameters parameters)
  {
    return new CAI1TypeScalarSearchParameters(
      CBOptionType.fromOptional(parameters.search().map(CBString::new)),
      CBCore.unsigned32(parameters.limit())
    );
  }

  private static ProtocolCAIv1Type typeScalarGet(
    final CAICommandTypeScalarGet c)
  {
    return new CAI1CommandTypeScalarGet(
      new CBString(c.name().value())
    );
  }

  private static ProtocolCAIv1Type typeScalarPut(
    final CAICommandTypeScalarPut c)
  {
    return new CAI1CommandTypeScalarPut(
      CBLists.ofCollection(
        c.types(), ToWireModel::typeScalar)
    );
  }




  private static ProtocolCAIv1Type typeDeclarationRemove(
    final CAICommandTypeDeclarationRemove c)
  {
    return new CAI1CommandTypeDeclarationRemove(
      CBLists.ofCollection(c.types(), r -> CBCore.string(r.value()))
    );
  }

  private static ProtocolCAIv1Type typeDeclarationSearchPrevious(
    final CAICommandTypeDeclarationSearchPrevious c)
  {
    return new CAI1CommandTypeDeclarationSearchPrevious();
  }

  private static ProtocolCAIv1Type typeDeclarationSearchNext(
    final CAICommandTypeDeclarationSearchNext c)
  {
    return new CAI1CommandTypeDeclarationSearchNext();
  }

  private static ProtocolCAIv1Type typeDeclarationSearchBegin(
    final CAICommandTypeDeclarationSearchBegin c)
  {
    return new CAI1CommandTypeDeclarationSearchBegin(
      typeDeclarationSearchParameters(c.parameters())
    );
  }

  private static CAI1TypeDeclarationSearchParameters typeDeclarationSearchParameters(
    final CATypeDeclarationSearchParameters parameters)
  {
    return new CAI1TypeDeclarationSearchParameters(
      CBOptionType.fromOptional(parameters.search().map(CBString::new)),
      CBCore.unsigned32(parameters.limit())
    );
  }

  private static ProtocolCAIv1Type typeDeclarationGet(
    final CAICommandTypeDeclarationGet c)
  {
    return new CAI1CommandTypeDeclarationGet(
      new CBString(c.name().value())
    );
  }

  private static ProtocolCAIv1Type typeDeclarationPut(
    final CAICommandTypeDeclarationPut c)
  {
    return new CAI1CommandTypeDeclarationPut(
      CBLists.ofCollection(
        c.types(), ToWireModel::typeDeclaration)
    );
  }






  private static ProtocolCAIv1Type rolesGet(
    final CAICommandRolesGet c)
  {
    return new CAI1CommandRolesGet(
      new CBUUID(c.user())
    );
  }

  private static ProtocolCAIv1Type rolesAssign(
    final CAICommandRolesAssign c)
  {
    return new CAI1CommandRolesAssign(
      new CBUUID(c.user()),
      CBLists.ofCollection(c.roles(), r -> new CBString(r.value().value()))
    );
  }

  private static ProtocolCAIv1Type rolesRevoke(
    final CAICommandRolesRevoke c)
  {
    return new CAI1CommandRolesRevoke(
      new CBUUID(c.user()),
      CBLists.ofCollection(c.roles(), r -> new CBString(r.value().value()))
    );
  }

  private static ProtocolCAIv1Type fileSearchPrevious(
    final CAICommandFileSearchPrevious c)
  {
    return new CAI1CommandFileSearchPrevious();
  }

  private static ProtocolCAIv1Type fileSearchNext(
    final CAICommandFileSearchNext c)
  {
    return new CAI1CommandFileSearchNext();
  }

  private static ProtocolCAIv1Type fileSearchBegin(
    final CAICommandFileSearchBegin c)
  {
    return new CAI1CommandFileSearchBegin(
      ToWireModel.fileSearchParameters(c.parameters())
    );
  }

  private static ProtocolCAIv1Type itemSearchPrevious(
    final CAICommandItemSearchPrevious c)
  {
    return new CAI1CommandItemSearchPrevious();
  }

  private static ProtocolCAIv1Type itemSearchNext(
    final CAICommandItemSearchNext c)
  {
    return new CAI1CommandItemSearchNext();
  }

  private static ProtocolCAIv1Type itemSearchBegin(
    final CAICommandItemSearchBegin c)
  {
    return new CAI1CommandItemSearchBegin(
      ToWireModel.itemSearchParameters(c.parameters())
    );
  }

  private static ProtocolCAIv1Type itemReposit(
    final CAICommandItemReposit c)
  {
    return new CAI1CommandItemReposit(
      ToWireModel.itemReposit(c.reposit())
    );
  }

  private static ProtocolCAIv1Type itemsRemove(
    final CAICommandItemsRemove c)
  {
    return new CAI1CommandItemsRemove(
      CBLists.ofCollection(c.ids(), x -> new CBUUID(x.id()))
    );
  }

  private static ProtocolCAIv1Type itemSetName(
    final CAICommandItemSetName c)
  {
    return new CAI1CommandItemSetName(
      new CBUUID(c.id().id()),
      new CBString(c.name())
    );
  }

  private static ProtocolCAIv1Type login(
    final CAICommandLogin c)
  {
    return new CAI1CommandLogin(
      new CBString(c.userName().value()),
      new CBString(c.password()),
      CBMaps.ofMapString(c.metadata())
    );
  }

  private static ProtocolCAIv1Type tagsPut(
    final CAICommandTagsPut c)
  {
    return new CAI1CommandTagsPut(
      CBLists.ofCollection(c.tags().tags(), ToWireModel::tag)
    );
  }

  private static ProtocolCAIv1Type tagsDelete(
    final CAICommandTagsDelete c)
  {
    return new CAI1CommandTagsDelete(
      CBLists.ofCollection(c.tags().tags(), ToWireModel::tag)
    );
  }

  private static ProtocolCAIv1Type tagList(
    final CAICommandTagList c)
  {
    return new CAI1CommandTagList();
  }

  private static ProtocolCAIv1Type locationPut(
    final CAICommandLocationPut c)
  {
    return new CAI1CommandLocationPut(ToWireModel.location(c.location()));
  }

  private static ProtocolCAIv1Type locationGet(
    final CAICommandLocationGet c)
  {
    return new CAI1CommandLocationGet(new CBUUID(c.id().id()));
  }

  private static ProtocolCAIv1Type locationList(
    final CAICommandLocationList c)
  {
    return new CAI1CommandLocationList();
  }

  private static ProtocolCAIv1Type itemMetadataRemove(
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

  private static ProtocolCAIv1Type itemMetadataPut(
    final CAICommandItemMetadataPut c)
  {
    return new CAI1CommandItemMetadataPut(
      new CBUUID(c.item().id()),
      CBLists.ofCollection(c.metadatas(), ToWireModel::itemMetadata)
    );
  }

  private static ProtocolCAIv1Type itemLocationsList(
    final CAICommandItemLocationsList c)
  {
    return new CAI1CommandItemLocationsList(
      new CBUUID(c.item().id())
    );
  }

  private static ProtocolCAIv1Type itemGet(
    final CAICommandItemGet c)
  {
    return new CAI1CommandItemGet(new CBUUID(c.id().id()));
  }

  private static ProtocolCAIv1Type fileGet(
    final CAICommandFileGet c)
  {
    return new CAI1CommandFileGet(new CBUUID(c.id().id()));
  }

  private static ProtocolCAIv1Type itemCreate(
    final CAICommandItemCreate c)
  {
    return new CAI1CommandItemCreate(
      new CBUUID(c.id().id()),
      new CBString(c.name())
    );
  }

  private static ProtocolCAIv1Type itemAttachmentRemove(
    final CAICommandItemAttachmentRemove c)
  {
    return new CAI1CommandItemAttachmentRemove(
      new CBUUID(c.item().id()),
      new CBUUID(c.file().id()),
      new CBString(c.relation())
    );
  }

  private static ProtocolCAIv1Type itemAttachmentAdd(
    final CAICommandItemAttachmentAdd c)
  {
    return new CAI1CommandItemAttachmentAdd(
      new CBUUID(c.item().id()),
      new CBUUID(c.file().id()),
      new CBString(c.relation())
    );
  }

  private static ProtocolCAIv1Type fileRemove(
    final CAICommandFileRemove c)
  {
    return new CAI1CommandFileRemove(new CBUUID(c.data().id()));
  }

  private static ProtocolCAIv1Type filePut(
    final CAICommandFilePut c)
  {
    return new CAI1CommandFilePut(ToWireModel.file(c.data()));
  }
}
