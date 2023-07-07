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
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarGet;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarPut;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarRemove;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
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
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeScalarGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeScalarPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeScalarRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeScalarSearchBegin;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeScalarSearchNext;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeScalarSearchPrevious;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeScalarSearchParameters;
import com.io7m.cedarbridge.runtime.api.CBString;
import com.io7m.cedarbridge.runtime.convenience.CBMaps;
import com.io7m.cedarbridge.runtime.convenience.CBSets;
import com.io7m.idstore.model.IdName;
import com.io7m.lanark.core.RDottedName;
import com.io7m.medrina.api.MRoleName;

import java.util.stream.Collectors;

// CHECKSTYLE:OFF

public final class FromWireCommands
{
  private FromWireCommands()
  {
    
  }

  public static CAIMessageType itemReposit(
    final CAI1CommandItemReposit m)
  {
    return new CAICommandItemReposit(
      FromWireModel.reposit(m.fieldReposit())
    );
  }

  public static CAIMessageType itemSetName(
    final CAI1CommandItemSetName m)
  {
    return new CAICommandItemSetName(
      new CAItemID(m.fieldItem().value()),
      m.fieldName().value()
    );
  }

  public static CAIMessageType itemsRemove(
    final CAI1CommandItemsRemove m)
  {
    return new CAICommandItemsRemove(
      CBSets.toSet(m.fieldItems(), x -> new CAItemID(x.value()))
    );
  }

  public static CAIMessageType locationGet(
    final CAI1CommandLocationGet m)
  {
    return new CAICommandLocationGet(
      new CALocationID(m.fieldLocation().value())
    );
  }

  public static CAIMessageType locationPut(
    final CAI1CommandLocationPut m)
  {
    return new CAICommandLocationPut(
      FromWireModel.location(m.fieldLocation())
    );
  }

  public static CAIMessageType locationList(
    final CAI1CommandLocationList m)
  {
    return new CAICommandLocationList();
  }

  public static CAIMessageType tagsPut(
    final CAI1CommandTagsPut m)
  {
    return new CAICommandTagsPut(FromWireModel.tags(m.fieldTags())
    );
  }

  public static CAIMessageType tagsDelete(
    final CAI1CommandTagsDelete m)
  {
    return new CAICommandTagsDelete(FromWireModel.tags(m.fieldTags()));
  }

  public static CAIMessageType tagList(
    final CAI1CommandTagList m)
  {
    return new CAICommandTagList();
  }

  public static CAIMessageType login(
    final CAI1CommandLogin m)
  {
    return new CAICommandLogin(
      new IdName(m.fieldUserName().value()),
      m.fieldPassword().value(),
      CBMaps.toMapString(m.fieldMetadata())
    );
  }

  public static CAIMessageType itemMetadataRemove(
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

  public static CAIMessageType itemMetadataPut(
    final CAI1CommandItemMetadataPut m)
  {
    return new CAICommandItemMetadataPut(
      new CAItemID(m.fieldItemId().value()),
      CBSets.toSet(
        m.fieldMetadatas(),
        FromWireModel::itemMetadata
      )
    );
  }

  public static CAIMessageType itemLocationsList(
    final CAI1CommandItemLocationsList m)
  {
    return new CAICommandItemLocationsList(
      new CAItemID(m.fieldItemId().value())
    );
  }

  public static CAIMessageType itemGet(
    final CAI1CommandItemGet m)
  {
    return new CAICommandItemGet(
      new CAItemID(m.fieldItemId().value())
    );
  }

  public static CAIMessageType fileGet(
    final CAI1CommandFileGet m)
  {
    return new CAICommandFileGet(
      new CAFileID(m.fieldFileId().value())
    );
  }

  public static CAIMessageType itemCreate(
    final CAI1CommandItemCreate m)
  {
    return new CAICommandItemCreate(
      new CAItemID(m.fieldItemId().value()),
      m.fieldName().value()
    );
  }

  public static CAIMessageType itemAttachmentRemove(
    final CAI1CommandItemAttachmentRemove m)
  {
    return new CAICommandItemAttachmentRemove(
      new CAItemID(m.fieldItemId().value()),
      new CAFileID(m.fieldFileId().value()),
      m.fieldRelation().value()
    );
  }

  public static CAIMessageType itemAttachmentAdd(
    final CAI1CommandItemAttachmentAdd m)
  {
    return new CAICommandItemAttachmentAdd(
      new CAItemID(m.fieldItemId().value()),
      new CAFileID(m.fieldFileId().value()),
      m.fieldRelation().value()
    );
  }

  public static CAIMessageType fileRemove(
    final CAI1CommandFileRemove m)
  {
    return new CAICommandFileRemove(
      new CAFileID(m.fieldId().value())
    );
  }

  public static CAIMessageType filePut(
    final CAI1CommandFilePut m)
  {
    return new CAICommandFilePut(
      FromWireModel.file(m.fieldFile())
    );
  }

  public static CAIMessageType itemSearchBegin(
    final CAI1CommandItemSearchBegin m)
  {
    return new CAICommandItemSearchBegin(
      FromWireModel.itemSearchParameters(m.fieldParameters())
    );
  }

  public static CAIMessageType itemSearchNext(
    final CAI1CommandItemSearchNext m)
  {
    return new CAICommandItemSearchNext();
  }

  public static CAIMessageType itemSearchPrevious(
    final CAI1CommandItemSearchPrevious m)
  {
    return new CAICommandItemSearchPrevious();
  }

  public static CAIMessageType fileSearchBegin(
    final CAI1CommandFileSearchBegin m)
  {
    return new CAICommandFileSearchBegin(
      FromWireModel.fileSearchParameters(m.fieldParameters())
    );
  }

  public static CAIMessageType fileSearchNext(
    final CAI1CommandFileSearchNext m)
  {
    return new CAICommandFileSearchNext();
  }

  public static CAIMessageType fileSearchPrevious(
    final CAI1CommandFileSearchPrevious m)
  {
    return new CAICommandFileSearchPrevious();
  }

  public static CAIMessageType rolesAssign(
    final CAI1CommandRolesAssign m)
  {
    return new CAICommandRolesAssign(
      m.fieldUser().value(),
      CBSets.toSet(m.fieldRoles(), x -> MRoleName.of(x.value()))
    );
  }

  public static CAIMessageType rolesRevoke(
    final CAI1CommandRolesRevoke m)
  {
    return new CAICommandRolesRevoke(
      m.fieldUser().value(),
      CBSets.toSet(m.fieldRoles(), x -> MRoleName.of(x.value()))
    );
  }

  public static CAIMessageType rolesGet(
    final CAI1CommandRolesGet m)
  {
    return new CAICommandRolesGet(m.fieldUser().value());
  }

  public static CAIMessageType typeScalarPut(
    final CAI1CommandTypeScalarPut c)
  {
    return new CAICommandTypeScalarPut(
      CBSets.toSet(c.fieldTypes(), FromWireModel::typeScalar)
    );
  }

  public static CAIMessageType typeScalarGet(
    final CAI1CommandTypeScalarGet c)
  {
    return new CAICommandTypeScalarGet(
      new RDottedName(c.fieldName().value())
    );
  }

  public static CAIMessageType typeScalarSearchBegin(
    final CAI1CommandTypeScalarSearchBegin c)
  {
    return new CAICommandTypeScalarSearchBegin(
      typeScalarSearchParameters(c.fieldParameters())
    );
  }

  private static CATypeScalarSearchParameters typeScalarSearchParameters(
    final CAI1TypeScalarSearchParameters p)
  {
    return new CATypeScalarSearchParameters(
      p.fieldSearch().asOptional().map(CBString::value),
      Math.toIntExact(p.fieldLimit().value())
    );
  }

  public static CAIMessageType typeScalarSearchNext(
    final CAI1CommandTypeScalarSearchNext c)
  {
    return new CAICommandTypeScalarSearchNext();
  }

  public static CAIMessageType typeScalarSearchPrevious(
    final CAI1CommandTypeScalarSearchPrevious c)
  {
    return new CAICommandTypeScalarSearchPrevious();
  }

  public static CAIMessageType typeScalarRemove(
    final CAI1CommandTypeScalarRemove c)
  {
    return new CAICommandTypeScalarRemove(
      CBSets.toSet(c.fieldTypes(), x -> new RDottedName(x.value()))
    );
  }
}
