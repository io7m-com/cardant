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

import com.io7m.cardant.tests.arbitraries.CAArbCommand;
import com.io7m.cardant.tests.arbitraries.CAArbCommandDebugInvalid;
import com.io7m.cardant.tests.arbitraries.CAArbCommandDebugRandom;
import com.io7m.cardant.tests.arbitraries.CAArbCommandFileGet;
import com.io7m.cardant.tests.arbitraries.CAArbCommandFilePut;
import com.io7m.cardant.tests.arbitraries.CAArbCommandFileRemove;
import com.io7m.cardant.tests.arbitraries.CAArbCommandFileSearchBegin;
import com.io7m.cardant.tests.arbitraries.CAArbCommandFileSearchNext;
import com.io7m.cardant.tests.arbitraries.CAArbCommandFileSearchPrevious;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemAttachmentAdd;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemAttachmentRemove;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemCreate;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemGet;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemLocationsList;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemMetadataPut;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemMetadataRemove;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemReposit;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemSearchBegin;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemSearchNext;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemSearchPrevious;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemSetName;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemTypesAssign;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemTypesRevoke;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemsRemove;
import com.io7m.cardant.tests.arbitraries.CAArbCommandLocationAttachmentAdd;
import com.io7m.cardant.tests.arbitraries.CAArbCommandLocationAttachmentRemove;
import com.io7m.cardant.tests.arbitraries.CAArbCommandLocationGet;
import com.io7m.cardant.tests.arbitraries.CAArbCommandLocationList;
import com.io7m.cardant.tests.arbitraries.CAArbCommandLocationMetadataPut;
import com.io7m.cardant.tests.arbitraries.CAArbCommandLocationMetadataRemove;
import com.io7m.cardant.tests.arbitraries.CAArbCommandLocationPut;
import com.io7m.cardant.tests.arbitraries.CAArbCommandLocationTypesAssign;
import com.io7m.cardant.tests.arbitraries.CAArbCommandLocationTypesRevoke;
import com.io7m.cardant.tests.arbitraries.CAArbCommandLogin;
import com.io7m.cardant.tests.arbitraries.CAArbCommandRolesAssign;
import com.io7m.cardant.tests.arbitraries.CAArbCommandRolesGet;
import com.io7m.cardant.tests.arbitraries.CAArbCommandRolesRevoke;
import com.io7m.cardant.tests.arbitraries.CAArbCommandTypeDeclarationGet;
import com.io7m.cardant.tests.arbitraries.CAArbCommandTypeDeclarationPut;
import com.io7m.cardant.tests.arbitraries.CAArbCommandTypeDeclarationRemove;
import com.io7m.cardant.tests.arbitraries.CAArbCommandTypeDeclarationSearchBegin;
import com.io7m.cardant.tests.arbitraries.CAArbCommandTypeDeclarationSearchNext;
import com.io7m.cardant.tests.arbitraries.CAArbCommandTypeDeclarationSearchPrevious;
import com.io7m.cardant.tests.arbitraries.CAArbCommandTypeScalarGet;
import com.io7m.cardant.tests.arbitraries.CAArbCommandTypeScalarPut;
import com.io7m.cardant.tests.arbitraries.CAArbCommandTypeScalarRemove;
import com.io7m.cardant.tests.arbitraries.CAArbCommandTypeScalarSearchBegin;
import com.io7m.cardant.tests.arbitraries.CAArbCommandTypeScalarSearchNext;
import com.io7m.cardant.tests.arbitraries.CAArbCommandTypeScalarSearchPrevious;
import com.io7m.cardant.tests.arbitraries.CAArbResponse;
import com.io7m.cardant.tests.arbitraries.CAArbResponseError;
import com.io7m.cardant.tests.arbitraries.CAArbResponseFileGet;
import com.io7m.cardant.tests.arbitraries.CAArbResponseFilePut;
import com.io7m.cardant.tests.arbitraries.CAArbResponseFileRemove;
import com.io7m.cardant.tests.arbitraries.CAArbResponseFileSearch;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemAttachmentAdd;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemAttachmentRemove;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemCreate;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemGet;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemLocationsList;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemMetadataPut;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemMetadataRemove;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemReposit;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemSearch;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemSetName;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemTypesAssign;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemTypesRevoke;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemsRemove;
import com.io7m.cardant.tests.arbitraries.CAArbResponseLocationAttachmentAdd;
import com.io7m.cardant.tests.arbitraries.CAArbResponseLocationAttachmentRemove;
import com.io7m.cardant.tests.arbitraries.CAArbResponseLocationGet;
import com.io7m.cardant.tests.arbitraries.CAArbResponseLocationList;
import com.io7m.cardant.tests.arbitraries.CAArbResponseLocationMetadataPut;
import com.io7m.cardant.tests.arbitraries.CAArbResponseLocationMetadataRemove;
import com.io7m.cardant.tests.arbitraries.CAArbResponseLocationPut;
import com.io7m.cardant.tests.arbitraries.CAArbResponseLocationTypesAssign;
import com.io7m.cardant.tests.arbitraries.CAArbResponseLocationTypesRevoke;
import com.io7m.cardant.tests.arbitraries.CAArbResponseLogin;
import com.io7m.cardant.tests.arbitraries.CAArbResponseRolesAssign;
import com.io7m.cardant.tests.arbitraries.CAArbResponseRolesGet;
import com.io7m.cardant.tests.arbitraries.CAArbResponseRolesRevoke;
import com.io7m.cardant.tests.arbitraries.CAArbResponseTypeDeclarationGet;
import com.io7m.cardant.tests.arbitraries.CAArbResponseTypeDeclarationPut;
import com.io7m.cardant.tests.arbitraries.CAArbResponseTypeDeclarationRemove;
import com.io7m.cardant.tests.arbitraries.CAArbResponseTypeDeclarationSearch;
import com.io7m.cardant.tests.arbitraries.CAArbResponseTypeScalarGet;
import com.io7m.cardant.tests.arbitraries.CAArbResponseTypeScalarPut;
import com.io7m.cardant.tests.arbitraries.CAArbResponseTypeScalarRemove;
import com.io7m.cardant.tests.arbitraries.CAArbResponseTypeScalarSearch;
import com.io7m.cardant.tests.arbitraries.model.CAArbAttachment;
import com.io7m.cardant.tests.arbitraries.model.CAArbAttachmentKey;
import com.io7m.cardant.tests.arbitraries.model.CAArbDottedName;
import com.io7m.cardant.tests.arbitraries.model.CAArbErrorCode;
import com.io7m.cardant.tests.arbitraries.model.CAArbFile;
import com.io7m.cardant.tests.arbitraries.model.CAArbFileColumnOrdering;
import com.io7m.cardant.tests.arbitraries.model.CAArbFileID;
import com.io7m.cardant.tests.arbitraries.model.CAArbFileSearchParameters;
import com.io7m.cardant.tests.arbitraries.model.CAArbFileWithoutData;
import com.io7m.cardant.tests.arbitraries.model.CAArbIdName;
import com.io7m.cardant.tests.arbitraries.model.CAArbIds;
import com.io7m.cardant.tests.arbitraries.model.CAArbItem;
import com.io7m.cardant.tests.arbitraries.model.CAArbItemColumnOrdering;
import com.io7m.cardant.tests.arbitraries.model.CAArbItemID;
import com.io7m.cardant.tests.arbitraries.model.CAArbItemLocation;
import com.io7m.cardant.tests.arbitraries.model.CAArbItemLocations;
import com.io7m.cardant.tests.arbitraries.model.CAArbItemMetadata;
import com.io7m.cardant.tests.arbitraries.model.CAArbItemReposit;
import com.io7m.cardant.tests.arbitraries.model.CAArbItemRepositAdd;
import com.io7m.cardant.tests.arbitraries.model.CAArbItemRepositMove;
import com.io7m.cardant.tests.arbitraries.model.CAArbItemRepositRemove;
import com.io7m.cardant.tests.arbitraries.model.CAArbItemSearchParameters;
import com.io7m.cardant.tests.arbitraries.model.CAArbItemSummary;
import com.io7m.cardant.tests.arbitraries.model.CAArbListLocationAll;
import com.io7m.cardant.tests.arbitraries.model.CAArbListLocationBehaviour;
import com.io7m.cardant.tests.arbitraries.model.CAArbListLocationExact;
import com.io7m.cardant.tests.arbitraries.model.CAArbListLocationWithDescendants;
import com.io7m.cardant.tests.arbitraries.model.CAArbLocation;
import com.io7m.cardant.tests.arbitraries.model.CAArbLocationID;
import com.io7m.cardant.tests.arbitraries.model.CAArbLocationSummaries;
import com.io7m.cardant.tests.arbitraries.model.CAArbLocationSummary;
import com.io7m.cardant.tests.arbitraries.model.CAArbMRoleName;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataMatch;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataMatchAny;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataMatchRequire;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataValueMatch;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataValueMatchAny;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataValueMatchExact;
import com.io7m.cardant.tests.arbitraries.model.CAArbNameMatch;
import com.io7m.cardant.tests.arbitraries.model.CAArbNameMatchAny;
import com.io7m.cardant.tests.arbitraries.model.CAArbNameMatchExact;
import com.io7m.cardant.tests.arbitraries.model.CAArbNameMatchSearch;
import com.io7m.cardant.tests.arbitraries.model.CAArbSizeRange;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeDeclaration;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeDeclarationSearchParameters;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeDeclarationSummary;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeField;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeMatch;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeMatchAllOf;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeMatchAny;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeMatchAnyOf;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeScalar;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeScalarSearchParameters;
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
      CAArbAttachment,
      CAArbAttachmentKey,
      CAArbCommand,
      CAArbCommandDebugInvalid,
      CAArbCommandDebugRandom,
      CAArbCommandFileGet,
      CAArbCommandFilePut,
      CAArbCommandFileRemove,
      CAArbCommandFileSearchBegin,
      CAArbCommandFileSearchNext,
      CAArbCommandFileSearchPrevious,
      CAArbCommandItemAttachmentAdd,
      CAArbCommandItemAttachmentRemove,
      CAArbCommandItemCreate,
      CAArbCommandItemGet,
      CAArbCommandItemLocationsList,
      CAArbCommandItemMetadataPut,
      CAArbCommandItemMetadataRemove,
      CAArbCommandItemReposit,
      CAArbCommandItemSearchBegin,
      CAArbCommandItemSearchNext,
      CAArbCommandItemSearchPrevious,
      CAArbCommandItemSetName,
      CAArbCommandItemTypesAssign,
      CAArbCommandItemTypesRevoke,
      CAArbCommandItemsRemove,
      CAArbCommandLocationAttachmentAdd,
      CAArbCommandLocationAttachmentRemove,
      CAArbCommandLocationGet,
      CAArbCommandLocationList,
      CAArbCommandLocationMetadataPut,
      CAArbCommandLocationMetadataRemove,
      CAArbCommandLocationPut,
      CAArbCommandLocationTypesAssign,
      CAArbCommandLocationTypesRevoke,
      CAArbCommandLogin,
      CAArbCommandRolesAssign,
      CAArbCommandRolesGet,
      CAArbCommandRolesRevoke,
      CAArbCommandTypeDeclarationGet,
      CAArbCommandTypeDeclarationPut,
      CAArbCommandTypeDeclarationRemove,
      CAArbCommandTypeDeclarationSearchBegin,
      CAArbCommandTypeDeclarationSearchNext,
      CAArbCommandTypeDeclarationSearchPrevious,
      CAArbCommandTypeScalarGet,
      CAArbCommandTypeScalarPut,
      CAArbCommandTypeScalarRemove,
      CAArbCommandTypeScalarSearchBegin,
      CAArbCommandTypeScalarSearchNext,
      CAArbCommandTypeScalarSearchPrevious,
      CAArbDottedName,
      CAArbErrorCode,
      CAArbFile,
      CAArbFileColumnOrdering,
      CAArbFileID,
      CAArbFileSearchParameters,
      CAArbFileWithoutData,
      CAArbIdName,
      CAArbIds,
      CAArbItem,
      CAArbItemColumnOrdering,
      CAArbItemID,
      CAArbItemLocation,
      CAArbItemLocations,
      CAArbItemMetadata,
      CAArbItemReposit,
      CAArbItemRepositAdd,
      CAArbItemRepositMove,
      CAArbItemRepositRemove,
      CAArbItemSearchParameters,
      CAArbItemSummary,
      CAArbListLocationAll,
      CAArbListLocationBehaviour,
      CAArbListLocationExact,
      CAArbListLocationWithDescendants,
      CAArbLocation,
      CAArbLocationID,
      CAArbLocationSummaries,
      CAArbLocationSummary,
      CAArbMRoleName,
      CAArbMetadataMatch,
      CAArbMetadataMatchAny,
      CAArbMetadataMatchRequire,
      CAArbMetadataValueMatch,
      CAArbMetadataValueMatchAny,
      CAArbMetadataValueMatchExact,
      CAArbNameMatch,
      CAArbNameMatchAny,
      CAArbNameMatchExact,
      CAArbNameMatchSearch,
      CAArbResponse,
      CAArbResponseError,
      CAArbResponseFileGet,
      CAArbResponseFilePut,
      CAArbResponseFileRemove,
      CAArbResponseFileSearch,
      CAArbResponseItemAttachmentAdd,
      CAArbResponseItemAttachmentRemove,
      CAArbResponseItemCreate,
      CAArbResponseItemGet,
      CAArbResponseItemLocationsList,
      CAArbResponseItemMetadataPut,
      CAArbResponseItemMetadataRemove,
      CAArbResponseItemReposit,
      CAArbResponseItemSearch,
      CAArbResponseItemSetName,
      CAArbResponseItemTypesAssign,
      CAArbResponseItemTypesRevoke,
      CAArbResponseItemsRemove,
      CAArbResponseLocationAttachmentAdd,
      CAArbResponseLocationAttachmentRemove,
      CAArbResponseLocationGet,
      CAArbResponseLocationList,
      CAArbResponseLocationMetadataPut,
      CAArbResponseLocationMetadataRemove,
      CAArbResponseLocationPut,
      CAArbResponseLocationTypesAssign,
      CAArbResponseLocationTypesRevoke,
      CAArbResponseLogin,
      CAArbResponseRolesAssign,
      CAArbResponseRolesGet,
      CAArbResponseRolesRevoke,
      CAArbResponseTypeDeclarationGet,
      CAArbResponseTypeDeclarationPut,
      CAArbResponseTypeDeclarationRemove,
      CAArbResponseTypeDeclarationSearch,
      CAArbResponseTypeScalarGet,
      CAArbResponseTypeScalarPut,
      CAArbResponseTypeScalarRemove,
      CAArbResponseTypeScalarSearch,
      CAArbSizeRange,
      CAArbTypeDeclaration,
      CAArbTypeDeclarationSearchParameters,
      CAArbTypeDeclarationSummary,
      CAArbTypeField,
      CAArbTypeMatch,
      CAArbTypeMatchAllOf,
      CAArbTypeMatchAny,
      CAArbTypeMatchAnyOf,
      CAArbTypeScalar,
      CAArbTypeScalarSearchParameters
    ;
}
