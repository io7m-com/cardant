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

import com.io7m.hibiscus.api.HBCommandType;

/**
 * The type of commands in the User protocol.
 *
 * @param <R> The associated response type
 */

public sealed interface CAICommandType<R extends CAIResponseType>
  extends CAIMessageType, HBCommandType
  permits CAICommandDebugInvalid,
  CAICommandDebugRandom,
  CAICommandFileGet,
  CAICommandFilePut,
  CAICommandFileRemove,
  CAICommandFileSearchBegin,
  CAICommandFileSearchNext,
  CAICommandFileSearchPrevious,
  CAICommandItemAttachmentAdd,
  CAICommandItemAttachmentRemove,
  CAICommandItemCreate,
  CAICommandItemGet,
  CAICommandItemLocationsList,
  CAICommandItemMetadataPut,
  CAICommandItemMetadataRemove,
  CAICommandItemReposit,
  CAICommandItemSearchBegin,
  CAICommandItemSearchNext,
  CAICommandItemSearchPrevious,
  CAICommandItemSetName,
  CAICommandItemsRemove,
  CAICommandLocationGet,
  CAICommandLocationList,
  CAICommandLocationPut,
  CAICommandLogin,
  CAICommandRolesAssign,
  CAICommandRolesGet,
  CAICommandRolesRevoke,
  CAICommandTagList,
  CAICommandTagsDelete,
  CAICommandTagsPut,
  CAICommandTypeScalarGet,
  CAICommandTypeScalarPut,
  CAICommandTypeScalarRemove,
  CAICommandTypeScalarSearchBegin,
  CAICommandTypeScalarSearchNext,
  CAICommandTypeScalarSearchPrevious
{
  /**
   * @return The response class associated with this command (excluding the
   * error response)
   */

  Class<R> responseClass();
}
