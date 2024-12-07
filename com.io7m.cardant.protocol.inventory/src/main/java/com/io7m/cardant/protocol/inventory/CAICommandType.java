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

package com.io7m.cardant.protocol.inventory;

/**
 * The type of commands in the User protocol.
 *
 * @param <R> The associated response type
 */

public sealed interface CAICommandType<R extends CAIResponseType>
  extends CAIMessageType
  permits CAICommandAuditSearchBegin,
  CAICommandAuditSearchNext,
  CAICommandAuditSearchPrevious,
  CAICommandDebugInvalid,
  CAICommandDebugRandom,
  CAICommandFileDelete,
  CAICommandFileGet,
  CAICommandFilePut,
  CAICommandFileSearchBegin,
  CAICommandFileSearchNext,
  CAICommandFileSearchPrevious,
  CAICommandItemAttachmentAdd,
  CAICommandItemAttachmentRemove,
  CAICommandItemCreate,
  CAICommandItemDelete,
  CAICommandItemGet,
  CAICommandItemMetadataPut,
  CAICommandItemMetadataRemove,
  CAICommandItemSearchBegin,
  CAICommandItemSearchNext,
  CAICommandItemSearchPrevious,
  CAICommandItemSetName,
  CAICommandItemTypesAssign,
  CAICommandItemTypesRevoke,
  CAICommandLocationAttachmentAdd,
  CAICommandLocationAttachmentRemove,
  CAICommandLocationDelete,
  CAICommandLocationGet,
  CAICommandLocationList,
  CAICommandLocationMetadataPut,
  CAICommandLocationMetadataRemove,
  CAICommandLocationPut,
  CAICommandLocationTypesAssign,
  CAICommandLocationTypesRevoke,
  CAICommandLogin,
  CAICommandRolesAssign,
  CAICommandRolesGet,
  CAICommandRolesRevoke,
  CAICommandStockCount,
  CAICommandStockReposit,
  CAICommandStockSearchBegin,
  CAICommandStockSearchNext,
  CAICommandStockSearchPrevious,
  CAICommandTypePackageGetText,
  CAICommandTypePackageInstall,
  CAICommandTypePackageSearchBegin,
  CAICommandTypePackageSearchNext,
  CAICommandTypePackageSearchPrevious,
  CAICommandTypePackageUninstall,
  CAICommandTypePackageUpgrade
{
  /**
   * @return The response class associated with this command (excluding the
   * error response)
   */

  Class<R> responseClass();
}
