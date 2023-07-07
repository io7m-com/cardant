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

import net.jqwik.api.providers.ArbitraryProvider;

/**
 * Inventory server (Arbitrary instances)
 */

module com.io7m.cardant.tests.arbitraries
{
  requires static org.osgi.annotation.bundle;
  requires static org.osgi.annotation.versioning;

  requires com.io7m.cardant.protocol.inventory;

  requires net.jqwik.api;

  exports com.io7m.cardant.tests.arbitraries;
  exports com.io7m.cardant.tests.arbitraries.model;

  uses ArbitraryProvider;

  provides ArbitraryProvider
    with
com.io7m.cardant.tests.arbitraries.model.CAArbLocations,
com.io7m.cardant.tests.arbitraries.model.CAArbIds,
com.io7m.cardant.tests.arbitraries.model.CAArbItemLocation,
com.io7m.cardant.tests.arbitraries.model.CAArbItemLocations,
com.io7m.cardant.tests.arbitraries.model.CAArbItemAttachment,
com.io7m.cardant.tests.arbitraries.model.CAArbItem,
com.io7m.cardant.tests.arbitraries.model.CAArbItemAttachmentKey,
com.io7m.cardant.tests.arbitraries.model.CAArbErrorCode,
com.io7m.cardant.tests.arbitraries.model.CAArbDottedName,
com.io7m.cardant.tests.arbitraries.model.CAArbFile,
com.io7m.cardant.tests.arbitraries.model.CAArbFileColumnOrdering,
com.io7m.cardant.tests.arbitraries.model.CAArbFileID,
com.io7m.cardant.tests.arbitraries.model.CAArbFileSearchParameters,
com.io7m.cardant.tests.arbitraries.model.CAArbFileWithoutData,
com.io7m.cardant.tests.arbitraries.model.CAArbIdName,
com.io7m.cardant.tests.arbitraries.model.CAArbItemColumnOrdering,
com.io7m.cardant.tests.arbitraries.model.CAArbItemID,
com.io7m.cardant.tests.arbitraries.model.CAArbItemMetadata,
com.io7m.cardant.tests.arbitraries.model.CAArbItemReposit,
com.io7m.cardant.tests.arbitraries.model.CAArbItemRepositAdd,
com.io7m.cardant.tests.arbitraries.model.CAArbItemRepositMove,
com.io7m.cardant.tests.arbitraries.model.CAArbItemRepositRemove,
com.io7m.cardant.tests.arbitraries.model.CAArbItemSearchParameters,
com.io7m.cardant.tests.arbitraries.model.CAArbItemSummary,
com.io7m.cardant.tests.arbitraries.model.CAArbListLocationAll,
com.io7m.cardant.tests.arbitraries.model.CAArbListLocationBehaviour,
com.io7m.cardant.tests.arbitraries.model.CAArbListLocationExact,
com.io7m.cardant.tests.arbitraries.model.CAArbListLocationWithDescendants,
com.io7m.cardant.tests.arbitraries.model.CAArbLocation,
com.io7m.cardant.tests.arbitraries.model.CAArbLocationID,
com.io7m.cardant.tests.arbitraries.model.CAArbMRoleName,
com.io7m.cardant.tests.arbitraries.model.CAArbSizeRange,
com.io7m.cardant.tests.arbitraries.model.CAArbTag,
com.io7m.cardant.tests.arbitraries.model.CAArbTagID,
com.io7m.cardant.tests.arbitraries.model.CAArbTags,
com.io7m.cardant.tests.arbitraries.model.CAArbTypeScalar,

com.io7m.cardant.tests.arbitraries.CAArbCommand,
com.io7m.cardant.tests.arbitraries.CAArbResponse,

com.io7m.cardant.tests.arbitraries.CAArbCommandDebugInvalid,
com.io7m.cardant.tests.arbitraries.CAArbCommandDebugRandom,
com.io7m.cardant.tests.arbitraries.CAArbCommandFileGet,
com.io7m.cardant.tests.arbitraries.CAArbCommandFilePut,
com.io7m.cardant.tests.arbitraries.CAArbCommandFileRemove,
com.io7m.cardant.tests.arbitraries.CAArbCommandFileSearchBegin,
com.io7m.cardant.tests.arbitraries.CAArbCommandFileSearchNext,
com.io7m.cardant.tests.arbitraries.CAArbCommandFileSearchPrevious,
com.io7m.cardant.tests.arbitraries.CAArbCommandItemAttachmentAdd,
com.io7m.cardant.tests.arbitraries.CAArbCommandItemAttachmentRemove,
com.io7m.cardant.tests.arbitraries.CAArbCommandItemCreate,
com.io7m.cardant.tests.arbitraries.CAArbCommandItemGet,
com.io7m.cardant.tests.arbitraries.CAArbCommandItemLocationsList,
com.io7m.cardant.tests.arbitraries.CAArbCommandItemMetadataPut,
com.io7m.cardant.tests.arbitraries.CAArbCommandItemMetadataRemove,
com.io7m.cardant.tests.arbitraries.CAArbCommandItemReposit,
com.io7m.cardant.tests.arbitraries.CAArbCommandItemSearchBegin,
com.io7m.cardant.tests.arbitraries.CAArbCommandItemSearchNext,
com.io7m.cardant.tests.arbitraries.CAArbCommandItemSearchPrevious,
com.io7m.cardant.tests.arbitraries.CAArbCommandItemSetName,
com.io7m.cardant.tests.arbitraries.CAArbCommandItemsRemove,
com.io7m.cardant.tests.arbitraries.CAArbCommandLocationGet,
com.io7m.cardant.tests.arbitraries.CAArbCommandLocationList,
com.io7m.cardant.tests.arbitraries.CAArbCommandLocationPut,
com.io7m.cardant.tests.arbitraries.CAArbCommandLogin,
com.io7m.cardant.tests.arbitraries.CAArbCommandRolesAssign,
com.io7m.cardant.tests.arbitraries.CAArbCommandRolesGet,
com.io7m.cardant.tests.arbitraries.CAArbCommandRolesRevoke,
com.io7m.cardant.tests.arbitraries.CAArbCommandTagList,
com.io7m.cardant.tests.arbitraries.CAArbCommandTagsDelete,
com.io7m.cardant.tests.arbitraries.CAArbCommandTagsPut,
com.io7m.cardant.tests.arbitraries.CAArbCommandTypeScalarPut,
com.io7m.cardant.tests.arbitraries.CAArbResponseError,
com.io7m.cardant.tests.arbitraries.CAArbResponseFileGet,
com.io7m.cardant.tests.arbitraries.CAArbResponseFilePut,
com.io7m.cardant.tests.arbitraries.CAArbResponseFileRemove,
com.io7m.cardant.tests.arbitraries.CAArbResponseFileSearch,
com.io7m.cardant.tests.arbitraries.CAArbResponseItemAttachmentAdd,
com.io7m.cardant.tests.arbitraries.CAArbResponseItemAttachmentRemove,
com.io7m.cardant.tests.arbitraries.CAArbResponseItemCreate,
com.io7m.cardant.tests.arbitraries.CAArbResponseItemGet,
com.io7m.cardant.tests.arbitraries.CAArbResponseItemLocationsList,
com.io7m.cardant.tests.arbitraries.CAArbResponseItemMetadataPut,
com.io7m.cardant.tests.arbitraries.CAArbResponseItemMetadataRemove,
com.io7m.cardant.tests.arbitraries.CAArbResponseItemReposit,
com.io7m.cardant.tests.arbitraries.CAArbResponseItemSearch,
com.io7m.cardant.tests.arbitraries.CAArbResponseItemSetName,
com.io7m.cardant.tests.arbitraries.CAArbResponseItemsRemove,
com.io7m.cardant.tests.arbitraries.CAArbResponseLocationGet,
com.io7m.cardant.tests.arbitraries.CAArbResponseLocationList,
com.io7m.cardant.tests.arbitraries.CAArbResponseLocationPut,
com.io7m.cardant.tests.arbitraries.CAArbResponseLogin,
com.io7m.cardant.tests.arbitraries.CAArbResponseRolesAssign,
com.io7m.cardant.tests.arbitraries.CAArbResponseRolesGet,
com.io7m.cardant.tests.arbitraries.CAArbResponseRolesRevoke,
com.io7m.cardant.tests.arbitraries.CAArbResponseTagList,
com.io7m.cardant.tests.arbitraries.CAArbResponseTagsDelete,
com.io7m.cardant.tests.arbitraries.CAArbResponseTagsPut,
com.io7m.cardant.tests.arbitraries.CAArbResponseTypeScalarPut
  ;
}
