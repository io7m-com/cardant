/*
 * Copyright © 2025 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.protocol.inventory.json.internal;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "@type"
)
@JsonSubTypes({
  @JsonSubTypes.Type(value = CJ1CommandAuditSearchBegin.class, name = "AuditSearchBegin"),
  @JsonSubTypes.Type(value = CJ1CommandAuditSearchNext.class, name = "AuditSearchNext"),
  @JsonSubTypes.Type(value = CJ1CommandAuditSearchPrevious.class, name = "AuditSearchPrevious"),
  @JsonSubTypes.Type(value = CJ1CommandFileDelete.class, name = "FileDelete"),
  @JsonSubTypes.Type(value = CJ1CommandFileGet.class, name = "FileGet"),
  @JsonSubTypes.Type(value = CJ1CommandFilePut.class, name = "FilePut"),
  @JsonSubTypes.Type(value = CJ1CommandFileSearchBegin.class, name = "FileSearchBegin"),
  @JsonSubTypes.Type(value = CJ1CommandFileSearchNext.class, name = "FileSearchNext"),
  @JsonSubTypes.Type(value = CJ1CommandFileSearchPrevious.class, name = "FileSearchPrevious"),
  @JsonSubTypes.Type(value = CJ1CommandItemAttachmentAdd.class, name = "ItemAttachmentAdd"),
  @JsonSubTypes.Type(value = CJ1CommandItemAttachmentRemove.class, name = "ItemAttachmentRemove"),
  @JsonSubTypes.Type(value = CJ1CommandItemCreate.class, name = "ItemCreate"),
  @JsonSubTypes.Type(value = CJ1CommandItemDelete.class, name = "ItemDelete"),
  @JsonSubTypes.Type(value = CJ1CommandItemGet.class, name = "ItemGet"),
  @JsonSubTypes.Type(value = CJ1CommandItemMetadataPut.class, name = "ItemMetadataPut"),
  @JsonSubTypes.Type(value = CJ1CommandItemMetadataRemove.class, name = "ItemMetadataRemove"),
  @JsonSubTypes.Type(value = CJ1CommandItemSearchBegin.class, name = "ItemSearchBegin"),
  @JsonSubTypes.Type(value = CJ1CommandItemSearchNext.class, name = "ItemSearchNext"),
  @JsonSubTypes.Type(value = CJ1CommandItemSearchPrevious.class, name = "ItemSearchPrevious"),
  @JsonSubTypes.Type(value = CJ1CommandItemSetName.class, name = "ItemSetName"),
  @JsonSubTypes.Type(value = CJ1CommandItemTypesAssign.class, name = "ItemTypesAssign"),
  @JsonSubTypes.Type(value = CJ1CommandItemTypesRevoke.class, name = "ItemTypesRevoke"),
  @JsonSubTypes.Type(value = CJ1CommandLocationAttachmentAdd.class, name = "LocationAttachmentAdd"),
  @JsonSubTypes.Type(value = CJ1CommandLocationAttachmentRemove.class, name = "LocationAttachmentRemove"),
  @JsonSubTypes.Type(value = CJ1CommandLocationDelete.class, name = "LocationDelete"),
  @JsonSubTypes.Type(value = CJ1CommandLocationGet.class, name = "LocationGet"),
  @JsonSubTypes.Type(value = CJ1CommandLocationList.class, name = "LocationList"),
  @JsonSubTypes.Type(value = CJ1CommandLocationMetadataPut.class, name = "LocationMetadataPut"),
  @JsonSubTypes.Type(value = CJ1CommandLocationMetadataRemove.class, name = "LocationMetadataRemove"),
  @JsonSubTypes.Type(value = CJ1CommandLocationPut.class, name = "LocationPut"),
  @JsonSubTypes.Type(value = CJ1CommandLocationTypesAssign.class, name = "LocationTypesAssign"),
  @JsonSubTypes.Type(value = CJ1CommandLocationTypesRevoke.class, name = "LocationTypesRevoke"),
  @JsonSubTypes.Type(value = CJ1CommandLogin.class, name = "Login"),
  @JsonSubTypes.Type(value = CJ1CommandRolesAssign.class, name = "RolesAssign"),
  @JsonSubTypes.Type(value = CJ1CommandRolesGet.class, name = "RolesGet"),
  @JsonSubTypes.Type(value = CJ1CommandRolesRevoke.class, name = "RolesRevoke"),
  @JsonSubTypes.Type(value = CJ1CommandStockCount.class, name = "StockCount"),
  @JsonSubTypes.Type(value = CJ1CommandStockReposit.class, name = "StockReposit"),
  @JsonSubTypes.Type(value = CJ1CommandStockSearchBegin.class, name = "StockSearchBegin"),
  @JsonSubTypes.Type(value = CJ1CommandStockSearchNext.class, name = "StockSearchNext"),
  @JsonSubTypes.Type(value = CJ1CommandStockSearchPrevious.class, name = "StockSearchPrevious"),
  @JsonSubTypes.Type(value = CJ1CommandTypePackageGetText.class, name = "TypePackageGetText"),
  @JsonSubTypes.Type(value = CJ1CommandTypePackageInstall.class, name = "TypePackageInstall"),
  @JsonSubTypes.Type(value = CJ1CommandTypePackageSearchBegin.class, name = "TypePackageSearchBegin"),
  @JsonSubTypes.Type(value = CJ1CommandTypePackageSearchNext.class, name = "TypePackageSearchNext"),
  @JsonSubTypes.Type(value = CJ1CommandTypePackageSearchPrevious.class, name = "TypePackageSearchPrevious"),
  @JsonSubTypes.Type(value = CJ1CommandTypePackageUninstall.class, name = "TypePackageUninstall"),
  @JsonSubTypes.Type(value = CJ1CommandTypePackageUpgrade.class, name = "TypePackageUpgrade"),
})
public sealed interface CJ1MessageType
  extends CJ1ValueType
  permits CJ1CommandAuditSearchBegin,
  CJ1CommandAuditSearchNext,
  CJ1CommandAuditSearchPrevious,
  CJ1CommandFileDelete,
  CJ1CommandFileGet,
  CJ1CommandFilePut,
  CJ1CommandFileSearchBegin,
  CJ1CommandFileSearchNext,
  CJ1CommandFileSearchPrevious,
  CJ1CommandItemAttachmentAdd,
  CJ1CommandItemAttachmentRemove,
  CJ1CommandItemCreate,
  CJ1CommandItemDelete,
  CJ1CommandItemGet,
  CJ1CommandItemMetadataPut,
  CJ1CommandItemMetadataRemove,
  CJ1CommandItemSearchBegin,
  CJ1CommandItemSearchNext,
  CJ1CommandItemSearchPrevious,
  CJ1CommandItemSetName,
  CJ1CommandItemTypesAssign,
  CJ1CommandItemTypesRevoke,
  CJ1CommandLocationAttachmentAdd,
  CJ1CommandLocationAttachmentRemove,
  CJ1CommandLocationDelete,
  CJ1CommandLocationGet,
  CJ1CommandLocationList,
  CJ1CommandLocationMetadataPut,
  CJ1CommandLocationMetadataRemove,
  CJ1CommandLocationPut,
  CJ1CommandLocationTypesAssign,
  CJ1CommandLocationTypesRevoke,
  CJ1CommandLogin,
  CJ1CommandRolesAssign,
  CJ1CommandRolesGet,
  CJ1CommandRolesRevoke,
  CJ1CommandStockCount,
  CJ1CommandStockReposit,
  CJ1CommandStockSearchBegin,
  CJ1CommandStockSearchNext,
  CJ1CommandStockSearchPrevious,
  CJ1CommandTypePackageGetText,
  CJ1CommandTypePackageInstall,
  CJ1CommandTypePackageSearchBegin,
  CJ1CommandTypePackageSearchNext,
  CJ1CommandTypePackageSearchPrevious,
  CJ1CommandTypePackageUninstall,
  CJ1CommandTypePackageUpgrade
{

}
