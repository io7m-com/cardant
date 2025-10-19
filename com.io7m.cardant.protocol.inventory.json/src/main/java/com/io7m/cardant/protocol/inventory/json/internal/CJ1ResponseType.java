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

import java.util.UUID;

/**
 * The type of responses in the Inventory protocol.
 */

public sealed interface CJ1ResponseType
  extends CJ1MessageType
  permits CJ1ResponseAuditSearch,
  CJ1ResponseError,
  CJ1ResponseFileDelete,
  CJ1ResponseFileGet,
  CJ1ResponseFilePut,
  CJ1ResponseFileSearch,
  CJ1ResponseItemAttachmentAdd,
  CJ1ResponseItemAttachmentRemove,
  CJ1ResponseItemCreate,
  CJ1ResponseItemDelete,
  CJ1ResponseItemGet,
  CJ1ResponseItemMetadataPut,
  CJ1ResponseItemMetadataRemove,
  CJ1ResponseItemSearch,
  CJ1ResponseItemSetName,
  CJ1ResponseItemTypesAssign,
  CJ1ResponseItemTypesRevoke,
  CJ1ResponseLocationAttachmentAdd,
  CJ1ResponseLocationAttachmentRemove,
  CJ1ResponseLocationDelete,
  CJ1ResponseLocationGet,
  CJ1ResponseLocationList,
  CJ1ResponseLocationMetadataPut,
  CJ1ResponseLocationMetadataRemove,
  CJ1ResponseLocationPut,
  CJ1ResponseLocationTypesAssign,
  CJ1ResponseLocationTypesRevoke,
  CJ1ResponseLogin,
  CJ1ResponseRolesAssign,
  CJ1ResponseRolesGet,
  CJ1ResponseRolesRevoke,
  CJ1ResponseStockCount,
  CJ1ResponseStockReposit,
  CJ1ResponseStockSearch,
  CJ1ResponseTypePackageGetText,
  CJ1ResponseTypePackageInstall,
  CJ1ResponseTypePackageSearch,
  CJ1ResponseTypePackageUninstall,
  CJ1ResponseTypePackageUpgrade
{
  /**
   * @return The ID of the request that yielded this response
   */

  UUID requestId();
}
