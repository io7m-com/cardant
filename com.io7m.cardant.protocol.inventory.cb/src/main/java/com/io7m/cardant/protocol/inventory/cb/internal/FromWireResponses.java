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

import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAIds;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseFileGet;
import com.io7m.cardant.protocol.inventory.CAIResponseFilePut;
import com.io7m.cardant.protocol.inventory.CAIResponseFileRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseFileSearch;
import com.io7m.cardant.protocol.inventory.CAIResponseItemAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAIResponseItemAttachmentRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseItemCreate;
import com.io7m.cardant.protocol.inventory.CAIResponseItemGet;
import com.io7m.cardant.protocol.inventory.CAIResponseItemLocationsList;
import com.io7m.cardant.protocol.inventory.CAIResponseItemMetadataPut;
import com.io7m.cardant.protocol.inventory.CAIResponseItemMetadataRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseItemReposit;
import com.io7m.cardant.protocol.inventory.CAIResponseItemSearch;
import com.io7m.cardant.protocol.inventory.CAIResponseItemSetName;
import com.io7m.cardant.protocol.inventory.CAIResponseItemsRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationGet;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationList;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationPut;
import com.io7m.cardant.protocol.inventory.CAIResponseLogin;
import com.io7m.cardant.protocol.inventory.CAIResponseRolesAssign;
import com.io7m.cardant.protocol.inventory.CAIResponseRolesGet;
import com.io7m.cardant.protocol.inventory.CAIResponseRolesRevoke;
import com.io7m.cardant.protocol.inventory.CAIResponseTagList;
import com.io7m.cardant.protocol.inventory.CAIResponseTagsDelete;
import com.io7m.cardant.protocol.inventory.CAIResponseTagsPut;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeDeclarationGet;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeDeclarationPut;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeDeclarationRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeDeclarationSearch;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeScalarGet;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeScalarPut;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeScalarRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeScalarSearch;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseError;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseFileGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseFilePut;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseFileRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseFileSearch;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseItemAttachmentAdd;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseItemAttachmentRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseItemCreate;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseItemGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseItemLocationsList;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseItemMetadataPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseItemMetadataRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseItemReposit;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseItemSearch;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseItemSetName;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseItemsRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLocationGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLocationList;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLocationPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLogin;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseRolesAssign;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseRolesGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseRolesRevoke;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTagList;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTagsDelete;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTagsPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeDeclarationGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeDeclarationPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeDeclarationRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeDeclarationSearch;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeScalarGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeScalarPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeScalarRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeScalarSearch;
import com.io7m.cedarbridge.runtime.api.CBString;
import com.io7m.cedarbridge.runtime.convenience.CBMaps;
import com.io7m.cedarbridge.runtime.convenience.CBSets;
import com.io7m.medrina.api.MRoleName;

import java.util.Optional;

// CHECKSTYLE:OFF

public final class FromWireResponses
{
  private FromWireResponses()
  {

  }

  public static CAIMessageType itemReposit(
    final CAI1ResponseItemReposit m)
  {
    return new CAIResponseItemReposit(
      m.fieldRequestId().value(),
      FromWireModel.item(m.fieldItem())
    );
  }

  public static CAIMessageType itemSetName(
    final CAI1ResponseItemSetName m)
  {
    return new CAIResponseItemSetName(
      m.fieldRequestId().value(),
      FromWireModel.item(m.fieldItem())
    );
  }

  public static CAIMessageType itemsRemove(
    final CAI1ResponseItemsRemove m)
  {
    return new CAIResponseItemsRemove(
      m.fieldRequestId().value(),
      new CAIds(CBSets.toSet(m.fieldItems(), x -> new CAItemID(x.value())))
    );
  }

  public static CAIMessageType locationGet(
    final CAI1ResponseLocationGet m)
  {
    return new CAIResponseLocationGet(
      m.fieldRequestId().value(),
      FromWireModel.location(m.fieldLocation())
    );
  }

  public static CAIMessageType locationPut(
    final CAI1ResponseLocationPut m)
  {
    return new CAIResponseLocationPut(
      m.fieldRequestId().value(),
      FromWireModel.location(m.fieldLocation())
    );
  }

  public static CAIMessageType locationList(
    final CAI1ResponseLocationList m)
  {
    return new CAIResponseLocationList(
      m.fieldRequestId().value(),
      FromWireModel.locations(m.fieldLocations())
    );
  }

  public static CAIMessageType tagsPut(
    final CAI1ResponseTagsPut m)
  {
    return new CAIResponseTagsPut(
      m.fieldRequestId().value(),
      FromWireModel.tags(m.fieldTags())
    );
  }

  public static CAIMessageType tagsDelete(
    final CAI1ResponseTagsDelete m)
  {
    return new CAIResponseTagsDelete(
      m.fieldRequestId().value(),
      FromWireModel.tags(m.fieldTags())
    );
  }

  public static CAIMessageType tagList(
    final CAI1ResponseTagList m)
  {
    return new CAIResponseTagList(
      m.fieldRequestId().value(),
      FromWireModel.tags(m.fieldTags())
    );
  }

  public static CAIMessageType login(
    final CAI1ResponseLogin m)
  {
    return new CAIResponseLogin(
      m.fieldRequestId().value(),
      m.fieldUserId().value()
    );
  }

  public static CAIMessageType itemMetadataRemove(
    final CAI1ResponseItemMetadataRemove m)
  {
    return new CAIResponseItemMetadataRemove(
      m.fieldRequestId().value(),
      FromWireModel.item(m.fieldItem())
    );
  }

  public static CAIMessageType itemMetadataPut(
    final CAI1ResponseItemMetadataPut m)
  {
    return new CAIResponseItemMetadataPut(
      m.fieldRequestId().value(),
      FromWireModel.item(m.fieldItem())
    );
  }

  public static CAIMessageType itemLocationsList(
    final CAI1ResponseItemLocationsList m)
  {
    return new CAIResponseItemLocationsList(
      m.fieldRequestId().value(),
      FromWireModel.itemLocations(m.fieldItemLocations())
    );
  }

  public static CAIMessageType itemGet(
    final CAI1ResponseItemGet m)
  {
    return new CAIResponseItemGet(
      m.fieldRequestId().value(),
      FromWireModel.item(m.fieldItem())
    );
  }

  public static CAIMessageType fileGet(
    final CAI1ResponseFileGet m)
  {
    return new CAIResponseFileGet(
      m.fieldRequestId().value(),
      FromWireModel.file(m.fieldFile()).withoutData()
    );
  }

  public static CAIMessageType typeScalarPut(
    final CAI1ResponseTypeScalarPut m)
  {
    return new CAIResponseTypeScalarPut(
      m.fieldRequestId().value(),
      CBSets.toSet(m.fieldTypes(), FromWireModel::typeScalar)
    );
  }

  public static CAIMessageType typeScalarGet(
    final CAI1ResponseTypeScalarGet m)
  {
    return new CAIResponseTypeScalarGet(
      m.fieldRequestId().value(),
      FromWireModel.typeScalar(m.fieldType())
    );
  }

  public static CAIMessageType itemCreate(
    final CAI1ResponseItemCreate m)
  {
    return new CAIResponseItemCreate(
      m.fieldRequestId().value(),
      FromWireModel.item(m.fieldItem())
    );
  }

  public static CAIMessageType itemAttachmentRemove(
    final CAI1ResponseItemAttachmentRemove m)
  {
    return new CAIResponseItemAttachmentRemove(
      m.fieldRequestId().value(),
      FromWireModel.item(m.fieldItem())
    );
  }

  public static CAIMessageType itemAttachmentAdd(
    final CAI1ResponseItemAttachmentAdd m)
  {
    return new CAIResponseItemAttachmentAdd(
      m.fieldRequestId().value(),
      FromWireModel.item(m.fieldItem())
    );
  }

  public static CAIMessageType fileRemove(
    final CAI1ResponseFileRemove m)
  {
    return new CAIResponseFileRemove(
      m.fieldRequestId().value(),
      new CAFileID(m.fieldId().value())
    );
  }

  public static CAIMessageType filePut(
    final CAI1ResponseFilePut m)
  {
    return new CAIResponseFilePut(
      m.fieldRequestId().value(),
      FromWireModel.file(m.fieldFile())
    );
  }

  public static CAIMessageType itemSearch(
    final CAI1ResponseItemSearch c)
  {
    return new CAIResponseItemSearch(
      c.fieldRequestId().value(),
      FromWireModel.page(
        c.fieldResults(),
        FromWireModel::itemSummary)
    );
  }

  public static CAIMessageType fileSearch(
    final CAI1ResponseFileSearch c)
  {
    return new CAIResponseFileSearch(
      c.fieldRequestId().value(),
      FromWireModel.page(
        c.fieldResults(),
        f -> {
          return FromWireModel.file(f).withoutData();
        })
    );
  }

  public static CAIMessageType error(
    final CAI1ResponseError m)
  {
    return new CAIResponseError(
      m.fieldRequestId().value(),
      m.fieldMessage().value(),
      new CAErrorCode(m.fieldErrorCode().value()),
      CBMaps.toMapString(m.fieldAttributes()),
      m.fieldRemediatingAction()
        .asOptional()
        .map(CBString::value),
      Optional.empty(),
      FromWireModel.blame(m.fieldBlame())
    );
  }

  public static CAIMessageType rolesRevoke(
    final CAI1ResponseRolesRevoke m)
  {
    return new CAIResponseRolesRevoke(
      m.fieldRequestId().value()
    );
  }

  public static CAIMessageType rolesAssign(
    final CAI1ResponseRolesAssign m)
  {
    return new CAIResponseRolesAssign(
      m.fieldRequestId().value()
    );
  }

  public static CAIMessageType rolesGet(
    final CAI1ResponseRolesGet m)
  {
    return new CAIResponseRolesGet(
      m.fieldRequestId().value(),
      CBSets.toSet(m.fieldRoles(), x -> MRoleName.of(x.value()))
    );
  }

  public static CAIMessageType typeScalarSearch(
    final CAI1ResponseTypeScalarSearch c)
  {
    return new CAIResponseTypeScalarSearch(
      c.fieldRequestId().value(),
      FromWireModel.page(
        c.fieldResults(),
        FromWireModel::typeScalar)
    );
  }

  public static CAIMessageType typeScalarRemove(
    final CAI1ResponseTypeScalarRemove c)
  {
    return new CAIResponseTypeScalarRemove(
      c.fieldRequestId().value()
    );
  }

  public static CAIMessageType typeDeclarationSearch(
    final CAI1ResponseTypeDeclarationSearch c)
  {
    return new CAIResponseTypeDeclarationSearch(
      c.fieldRequestId().value(),
      FromWireModel.page(
        c.fieldResults(),
        FromWireModel::typeDeclarationSummary)
    );
  }

  public static CAIMessageType typeDeclarationRemove(
    final CAI1ResponseTypeDeclarationRemove c)
  {
    return new CAIResponseTypeDeclarationRemove(
      c.fieldRequestId().value()
    );
  }

  public static CAIMessageType typeDeclarationPut(
    final CAI1ResponseTypeDeclarationPut m)
  {
    return new CAIResponseTypeDeclarationPut(
      m.fieldRequestId().value(),
      CBSets.toSet(m.fieldTypes(), FromWireModel::typeDeclaration)
    );
  }

  public static CAIMessageType typeDeclarationGet(
    final CAI1ResponseTypeDeclarationGet m)
  {
    return new CAIResponseTypeDeclarationGet(
      m.fieldRequestId().value(),
      FromWireModel.typeDeclaration(m.fieldType())
    );
  }
}
