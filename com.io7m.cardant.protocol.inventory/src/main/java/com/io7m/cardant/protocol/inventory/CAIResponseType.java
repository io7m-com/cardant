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

import java.util.UUID;

/**
 * The type of responses in the Inventory protocol.
 */

public sealed interface CAIResponseType
  extends CAIMessageType
  permits CAIResponseAuditSearch,
  CAIResponseError,
  CAIResponseFileDelete,
  CAIResponseFileGet,
  CAIResponseFilePut,
  CAIResponseFileSearch,
  CAIResponseItemAttachmentAdd,
  CAIResponseItemAttachmentRemove,
  CAIResponseItemCreate,
  CAIResponseItemDelete,
  CAIResponseItemGet,
  CAIResponseItemMetadataPut,
  CAIResponseItemMetadataRemove,
  CAIResponseItemSearch,
  CAIResponseItemSetName,
  CAIResponseItemTypesAssign,
  CAIResponseItemTypesRevoke,
  CAIResponseLocationAttachmentAdd,
  CAIResponseLocationAttachmentRemove,
  CAIResponseLocationDelete,
  CAIResponseLocationGet,
  CAIResponseLocationList,
  CAIResponseLocationMetadataPut,
  CAIResponseLocationMetadataRemove,
  CAIResponseLocationPut,
  CAIResponseLocationTypesAssign,
  CAIResponseLocationTypesRevoke,
  CAIResponseLogin,
  CAIResponseRolesAssign,
  CAIResponseRolesGet,
  CAIResponseRolesRevoke,
  CAIResponseStockCount,
  CAIResponseStockReposit,
  CAIResponseStockSearch,
  CAIResponseTypePackageGetText,
  CAIResponseTypePackageInstall,
  CAIResponseTypePackageSearch,
  CAIResponseTypePackageUninstall,
  CAIResponseTypePackageUpgrade
{
  /**
   * @return The ID of the request that yielded this response
   */

  UUID requestId();
}
