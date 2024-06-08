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
import com.io7m.cardant.tests.arbitraries.CAArbCommandAuditSearchBegin;
import com.io7m.cardant.tests.arbitraries.CAArbCommandAuditSearchNext;
import com.io7m.cardant.tests.arbitraries.CAArbCommandAuditSearchPrevious;
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
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemDelete;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemGet;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemMetadataPut;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemMetadataRemove;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemSearchBegin;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemSearchNext;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemSearchPrevious;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemSetName;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemTypesAssign;
import com.io7m.cardant.tests.arbitraries.CAArbCommandItemTypesRevoke;
import com.io7m.cardant.tests.arbitraries.CAArbCommandLocationAttachmentAdd;
import com.io7m.cardant.tests.arbitraries.CAArbCommandLocationAttachmentRemove;
import com.io7m.cardant.tests.arbitraries.CAArbCommandLocationDelete;
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
import com.io7m.cardant.tests.arbitraries.CAArbCommandStockReposit;
import com.io7m.cardant.tests.arbitraries.CAArbCommandTypePackageGetText;
import com.io7m.cardant.tests.arbitraries.CAArbCommandTypePackageInstall;
import com.io7m.cardant.tests.arbitraries.CAArbCommandTypePackageSearchBegin;
import com.io7m.cardant.tests.arbitraries.CAArbCommandTypePackageSearchNext;
import com.io7m.cardant.tests.arbitraries.CAArbCommandTypePackageSearchPrevious;
import com.io7m.cardant.tests.arbitraries.CAArbCommandTypePackageUninstall;
import com.io7m.cardant.tests.arbitraries.CAArbCommandTypePackageUpgrade;
import com.io7m.cardant.tests.arbitraries.CAArbResponse;
import com.io7m.cardant.tests.arbitraries.CAArbResponseAuditSearch;
import com.io7m.cardant.tests.arbitraries.CAArbResponseError;
import com.io7m.cardant.tests.arbitraries.CAArbResponseFileGet;
import com.io7m.cardant.tests.arbitraries.CAArbResponseFilePut;
import com.io7m.cardant.tests.arbitraries.CAArbResponseFileRemove;
import com.io7m.cardant.tests.arbitraries.CAArbResponseFileSearch;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemAttachmentAdd;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemAttachmentRemove;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemCreate;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemDelete;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemGet;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemMetadataPut;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemMetadataRemove;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemSearch;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemSetName;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemTypesAssign;
import com.io7m.cardant.tests.arbitraries.CAArbResponseItemTypesRevoke;
import com.io7m.cardant.tests.arbitraries.CAArbResponseLocationAttachmentAdd;
import com.io7m.cardant.tests.arbitraries.CAArbResponseLocationAttachmentRemove;
import com.io7m.cardant.tests.arbitraries.CAArbResponseLocationDelete;
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
import com.io7m.cardant.tests.arbitraries.CAArbResponseStockReposit;
import com.io7m.cardant.tests.arbitraries.CAArbResponseTypePackageGetText;
import com.io7m.cardant.tests.arbitraries.CAArbResponseTypePackageInstall;
import com.io7m.cardant.tests.arbitraries.CAArbResponseTypePackageSearch;
import com.io7m.cardant.tests.arbitraries.CAArbResponseTypePackageUninstall;
import com.io7m.cardant.tests.arbitraries.CAArbResponseTypePackageUpgrade;
import com.io7m.cardant.tests.arbitraries.CAArbTypePackageUninstall;
import com.io7m.cardant.tests.arbitraries.model.CAArbAttachment;
import com.io7m.cardant.tests.arbitraries.model.CAArbAttachmentKey;
import com.io7m.cardant.tests.arbitraries.model.CAArbAuditEvent;
import com.io7m.cardant.tests.arbitraries.model.CAArbAuditSearchParameters;
import com.io7m.cardant.tests.arbitraries.model.CAArbDescriptionMatch;
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
import com.io7m.cardant.tests.arbitraries.model.CAArbItemSearchParameters;
import com.io7m.cardant.tests.arbitraries.model.CAArbItemSerial;
import com.io7m.cardant.tests.arbitraries.model.CAArbItemSummary;
import com.io7m.cardant.tests.arbitraries.model.CAArbLocation;
import com.io7m.cardant.tests.arbitraries.model.CAArbLocationID;
import com.io7m.cardant.tests.arbitraries.model.CAArbLocationMatch;
import com.io7m.cardant.tests.arbitraries.model.CAArbLocationMatchAny;
import com.io7m.cardant.tests.arbitraries.model.CAArbLocationMatchExact;
import com.io7m.cardant.tests.arbitraries.model.CAArbLocationMatchWithDescendants;
import com.io7m.cardant.tests.arbitraries.model.CAArbLocationSummaries;
import com.io7m.cardant.tests.arbitraries.model.CAArbLocationSummary;
import com.io7m.cardant.tests.arbitraries.model.CAArbMRoleName;
import com.io7m.cardant.tests.arbitraries.model.CAArbMediaTypeMatch;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadata;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataElementMatch;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataElementMatchAnd;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataElementMatchOr;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataElementMatchSpecific;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataIntegral;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataMonetary;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataReal;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataText;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataTime;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataValueMatch;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataValueMatchAnything;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataValueMatchIntegral;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataValueMatchIntegralWithinRange;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataValueMatchMonetary;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataValueMatchMonetaryWithCurrency;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataValueMatchMonetaryWithinRange;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataValueMatchReal;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataValueMatchRealWithinRange;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataValueMatchText;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataValueMatchTextExact;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataValueMatchTextSearch;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataValueMatchTime;
import com.io7m.cardant.tests.arbitraries.model.CAArbMetadataValueMatchTimeWithinRange;
import com.io7m.cardant.tests.arbitraries.model.CAArbNameMatch;
import com.io7m.cardant.tests.arbitraries.model.CAArbOffsetDateTime;
import com.io7m.cardant.tests.arbitraries.model.CAArbSizeRange;
import com.io7m.cardant.tests.arbitraries.model.CAArbStockReposit;
import com.io7m.cardant.tests.arbitraries.model.CAArbStockRepositSerialAdd;
import com.io7m.cardant.tests.arbitraries.model.CAArbStockRepositSerialMove;
import com.io7m.cardant.tests.arbitraries.model.CAArbStockRepositSerialRemove;
import com.io7m.cardant.tests.arbitraries.model.CAArbStockRepositSetAdd;
import com.io7m.cardant.tests.arbitraries.model.CAArbStockRepositSetMove;
import com.io7m.cardant.tests.arbitraries.model.CAArbStockRepositSetRemove;
import com.io7m.cardant.tests.arbitraries.model.CAArbTimeRange;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeDeclaration;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeDeclarationSearchParameters;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeDeclarationSummary;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeField;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeMatch;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypePackageIdentifier;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypePackageSearchParameters;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypePackageSummary;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeRecordFieldIdentifier;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeRecordIdentifier;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeScalar;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeScalarIntegral;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeScalarMonetary;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeScalarReal;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeScalarSearchParameters;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeScalarText;
import com.io7m.cardant.tests.arbitraries.model.CAArbTypeScalarTime;
import com.io7m.cardant.tests.arbitraries.model.CAArbUserID;
import com.io7m.cardant.tests.arbitraries.model.CAArbVersion;
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

  provides ArbitraryProvider with
CAArbAttachment,
CAArbAttachmentKey,
CAArbAuditEvent,
CAArbAuditSearchParameters,
CAArbCommand,
CAArbCommandAuditSearchBegin,
CAArbCommandAuditSearchNext,
CAArbCommandAuditSearchPrevious,
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
CAArbCommandItemDelete,
CAArbCommandItemGet,
CAArbCommandItemMetadataPut,
CAArbCommandItemMetadataRemove,
CAArbCommandItemSearchBegin,
CAArbCommandItemSearchNext,
CAArbCommandItemSearchPrevious,
CAArbCommandItemSetName,
CAArbCommandItemTypesAssign,
CAArbCommandItemTypesRevoke,
CAArbCommandLocationAttachmentAdd,
CAArbCommandLocationAttachmentRemove,
CAArbCommandLocationDelete,
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
CAArbCommandStockReposit,
CAArbCommandTypePackageGetText,
CAArbCommandTypePackageInstall,
CAArbCommandTypePackageSearchBegin,
CAArbCommandTypePackageSearchNext,
CAArbCommandTypePackageSearchPrevious,
CAArbCommandTypePackageUninstall,
CAArbCommandTypePackageUpgrade,
CAArbDescriptionMatch,
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
CAArbItemSearchParameters,
CAArbItemSerial,
CAArbItemSummary,
CAArbLocation,
CAArbLocationID,
CAArbLocationMatch,
CAArbLocationMatchAny,
CAArbLocationMatchExact,
CAArbLocationMatchWithDescendants,
CAArbLocationSummaries,
CAArbLocationSummary,
CAArbMRoleName,
CAArbMediaTypeMatch,
CAArbMetadata,
CAArbMetadataElementMatch,
CAArbMetadataElementMatchAnd,
CAArbMetadataElementMatchOr,
CAArbMetadataElementMatchSpecific,
CAArbMetadataIntegral,
CAArbMetadataMonetary,
CAArbMetadataReal,
CAArbMetadataText,
CAArbMetadataTime,
CAArbMetadataValueMatch,
CAArbMetadataValueMatchAnything,
CAArbMetadataValueMatchIntegral,
CAArbMetadataValueMatchIntegralWithinRange,
CAArbMetadataValueMatchMonetary,
CAArbMetadataValueMatchMonetaryWithCurrency,
CAArbMetadataValueMatchMonetaryWithinRange,
CAArbMetadataValueMatchReal,
CAArbMetadataValueMatchRealWithinRange,
CAArbMetadataValueMatchText,
CAArbMetadataValueMatchTextExact,
CAArbMetadataValueMatchTextSearch,
CAArbMetadataValueMatchTime,
CAArbMetadataValueMatchTimeWithinRange,
CAArbNameMatch,
CAArbOffsetDateTime,
CAArbResponse,
CAArbResponseAuditSearch,
CAArbResponseError,
CAArbResponseFileGet,
CAArbResponseFilePut,
CAArbResponseFileRemove,
CAArbResponseFileSearch,
CAArbResponseItemAttachmentAdd,
CAArbResponseItemAttachmentRemove,
CAArbResponseItemCreate,
CAArbResponseItemDelete,
CAArbResponseItemGet,
CAArbResponseItemMetadataPut,
CAArbResponseItemMetadataRemove,
CAArbResponseItemSearch,
CAArbResponseItemSetName,
CAArbResponseItemTypesAssign,
CAArbResponseItemTypesRevoke,
CAArbResponseLocationAttachmentAdd,
CAArbResponseLocationAttachmentRemove,
CAArbResponseLocationDelete,
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
CAArbResponseStockReposit,
CAArbResponseTypePackageGetText,
CAArbResponseTypePackageInstall,
CAArbResponseTypePackageSearch,
CAArbResponseTypePackageUninstall,
CAArbResponseTypePackageUpgrade,
CAArbSizeRange,
CAArbStockReposit,
CAArbStockRepositSerialAdd,
CAArbStockRepositSerialMove,
CAArbStockRepositSerialRemove,
CAArbStockRepositSetAdd,
CAArbStockRepositSetMove,
CAArbStockRepositSetRemove,
CAArbTimeRange,
CAArbTypeDeclaration,
CAArbTypeDeclarationSearchParameters,
CAArbTypeDeclarationSummary,
CAArbTypeField,
CAArbTypeMatch,
CAArbTypePackageIdentifier,
CAArbTypePackageSearchParameters,
CAArbTypePackageSummary,
CAArbTypePackageUninstall,
CAArbTypeRecordFieldIdentifier,
CAArbTypeRecordIdentifier,
CAArbTypeScalar,
CAArbTypeScalarIntegral,
CAArbTypeScalarMonetary,
CAArbTypeScalarReal,
CAArbTypeScalarSearchParameters,
CAArbTypeScalarText,
CAArbTypeScalarTime,
CAArbUserID,
CAArbVersion
    ;
}
