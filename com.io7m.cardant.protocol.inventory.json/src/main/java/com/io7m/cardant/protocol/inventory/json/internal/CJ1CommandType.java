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

public sealed interface CJ1CommandType
  extends CJ1MessageType
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
