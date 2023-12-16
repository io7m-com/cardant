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

import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.CAICommandDebugInvalid;
import com.io7m.cardant.protocol.inventory.CAICommandDebugRandom;
import com.io7m.cardant.protocol.inventory.CAICommandFileGet;
import com.io7m.cardant.protocol.inventory.CAICommandFilePut;
import com.io7m.cardant.protocol.inventory.CAICommandFileRemove;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentRemove;
import com.io7m.cardant.protocol.inventory.CAICommandItemCreate;
import com.io7m.cardant.protocol.inventory.CAICommandItemGet;
import com.io7m.cardant.protocol.inventory.CAICommandItemLocationsList;
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataPut;
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataRemove;
import com.io7m.cardant.protocol.inventory.CAICommandItemReposit;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAICommandItemSetName;
import com.io7m.cardant.protocol.inventory.CAICommandItemTypesAssign;
import com.io7m.cardant.protocol.inventory.CAICommandItemTypesRevoke;
import com.io7m.cardant.protocol.inventory.CAICommandItemsRemove;
import com.io7m.cardant.protocol.inventory.CAICommandLocationAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAICommandLocationAttachmentRemove;
import com.io7m.cardant.protocol.inventory.CAICommandLocationGet;
import com.io7m.cardant.protocol.inventory.CAICommandLocationList;
import com.io7m.cardant.protocol.inventory.CAICommandLocationMetadataPut;
import com.io7m.cardant.protocol.inventory.CAICommandLocationMetadataRemove;
import com.io7m.cardant.protocol.inventory.CAICommandLocationPut;
import com.io7m.cardant.protocol.inventory.CAICommandLocationTypesAssign;
import com.io7m.cardant.protocol.inventory.CAICommandLocationTypesRevoke;
import com.io7m.cardant.protocol.inventory.CAICommandLogin;
import com.io7m.cardant.protocol.inventory.CAICommandRolesAssign;
import com.io7m.cardant.protocol.inventory.CAICommandRolesGet;
import com.io7m.cardant.protocol.inventory.CAICommandRolesRevoke;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationGet;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationPut;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationRemove;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarGet;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarPut;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarRemove;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAIEventType;
import com.io7m.cardant.protocol.inventory.CAIEventUpdated;
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
import com.io7m.cardant.protocol.inventory.CAIResponseItemTypesAssign;
import com.io7m.cardant.protocol.inventory.CAIResponseItemTypesRevoke;
import com.io7m.cardant.protocol.inventory.CAIResponseItemsRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationAttachmentRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationGet;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationList;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationMetadataPut;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationMetadataRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationPut;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationTypesAssign;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationTypesRevoke;
import com.io7m.cardant.protocol.inventory.CAIResponseLogin;
import com.io7m.cardant.protocol.inventory.CAIResponseRolesAssign;
import com.io7m.cardant.protocol.inventory.CAIResponseRolesGet;
import com.io7m.cardant.protocol.inventory.CAIResponseRolesRevoke;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeDeclarationGet;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeDeclarationPut;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeDeclarationRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeDeclarationSearch;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeScalarGet;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeScalarPut;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeScalarRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeScalarSearch;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandFileGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandFilePut;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandFileRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandFileSearchBegin;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandFileSearchNext;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandFileSearchPrevious;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemAttachmentAdd;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemAttachmentRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemCreate;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemLocationsList;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemMetadataPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemMetadataRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemReposit;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemSearchBegin;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemSearchNext;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemSearchPrevious;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemSetName;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemTypesAssign;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemTypesRevoke;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandItemsRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandLocationAttachmentAdd;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandLocationAttachmentRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandLocationGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandLocationList;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandLocationMetadataPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandLocationMetadataRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandLocationPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandLocationTypesAssign;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandLocationTypesRevoke;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandLogin;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandRolesAssign;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandRolesGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandRolesRevoke;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeDeclarationGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeDeclarationPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeDeclarationRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeDeclarationSearchBegin;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeDeclarationSearchNext;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeDeclarationSearchPrevious;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeScalarGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeScalarPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeScalarRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeScalarSearchBegin;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeScalarSearchNext;
import com.io7m.cardant.protocol.inventory.cb.CAI1CommandTypeScalarSearchPrevious;
import com.io7m.cardant.protocol.inventory.cb.CAI1EventUpdated;
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
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseItemTypesAssign;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseItemTypesRevoke;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseItemsRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLocationAttachmentAdd;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLocationAttachmentRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLocationGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLocationList;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLocationMetadataPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLocationMetadataRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLocationPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLocationTypesAssign;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLocationTypesRevoke;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLogin;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseRolesAssign;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseRolesGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseRolesRevoke;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeDeclarationGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeDeclarationPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeDeclarationRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeDeclarationSearch;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeScalarGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeScalarPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeScalarRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeScalarSearch;
import com.io7m.cardant.protocol.inventory.cb.ProtocolCAIv1Type;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAIUVEventUpdated.EVENT_UPDATED;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandFileGet.COMMAND_FILE_GET;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandFilePut.COMMAND_FILE_PUT;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandFileRemove.COMMAND_FILE_REMOVE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandFileSearchBegin.COMMAND_FILE_SEARCH_BEGIN;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandFileSearchNext.COMMAND_FILE_SEARCH_NEXT;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandFileSearchPrevious.COMMAND_FILE_SEARCH_PREVIOUS;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandItemAttachmentAdd.COMMAND_ITEM_ATTACHMENT_ADD;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandItemAttachmentRemove.COMMAND_ITEM_ATTACHMENT_REMOVE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandItemCreate.COMMAND_ITEM_CREATE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandItemGet.COMMAND_ITEM_GET;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandItemLocationsList.COMMAND_ITEM_LOCATIONS_LIST;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandItemMetadataPut.COMMAND_ITEM_METADATA_PUT;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandItemMetadataRemove.COMMAND_ITEM_METADATA_REMOVE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandItemReposit.COMMAND_ITEM_REPOSIT;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandItemSearchBegin.COMMAND_ITEM_SEARCH_BEGIN;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandItemSearchNext.COMMAND_ITEM_SEARCH_NEXT;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandItemSearchPrevious.COMMAND_ITEM_SEARCH_PREVIOUS;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandItemSetName.COMMAND_ITEM_SET_NAME;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandItemTypesAssign.COMMAND_ITEM_TYPES_ASSIGN;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandItemTypesRevoke.COMMAND_ITEM_TYPES_REVOKE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandItemsRemove.COMMAND_ITEMS_REMOVE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandLocationAttachmentAdd.COMMAND_LOCATION_ATTACHMENT_ADD;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandLocationAttachmentRemove.COMMAND_LOCATION_ATTACHMENT_REMOVE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandLocationGet.COMMAND_LOCATION_GET;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandLocationList.COMMAND_LOCATION_LIST;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandLocationMetadataPut.COMMAND_LOCATION_METADATA_PUT;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandLocationMetadataRemove.COMMAND_LOCATION_METADATA_REMOVE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandLocationPut.COMMAND_LOCATION_PUT;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandLocationTypesAssign.COMMAND_LOCATION_TYPES_ASSIGN;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandLocationTypesRevoke.COMMAND_LOCATION_TYPES_REVOKE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandLogin.COMMAND_LOGIN;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandRolesAssign.COMMAND_ROLES_ASSIGN;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandRolesGet.COMMAND_ROLES_GET;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandRolesRevoke.COMMAND_ROLES_REVOKE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandTypeDeclarationGet.COMMAND_TYPE_DECLARATION_GET;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandTypeDeclarationPut.COMMAND_TYPE_DECLARATION_PUT;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandTypeDeclarationRemove.COMMAND_TYPE_DECLARATION_REMOVE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandTypeDeclarationSearchBegin.COMMAND_TYPE_DECLARATION_SEARCH_BEGIN;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandTypeDeclarationSearchNext.COMMAND_TYPE_DECLARATION_SEARCH_NEXT;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandTypeDeclarationSearchPrevious.COMMAND_TYPE_DECLARATION_SEARCH_PREVIOUS;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandTypeScalarGet.COMMAND_TYPE_SCALAR_GET;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandTypeScalarPut.COMMAND_TYPE_SCALAR_PUT;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandTypeScalarRemove.COMMAND_TYPE_SCALAR_REMOVE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandTypeScalarSearchBegin.COMMAND_TYPE_SCALAR_SEARCH_BEGIN;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandTypeScalarSearchNext.COMMAND_TYPE_SCALAR_SEARCH_NEXT;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVCommandTypeScalarSearchPrevious.COMMAND_TYPE_SCALAR_SEARCH_PREVIOUS;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseError.RESPONSE_ERROR;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseFileGet.RESPONSE_FILE_GET;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseFilePut.RESPONSE_FILE_PUT;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseFileRemove.RESPONSE_FILE_REMOVE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseFileSearch.RESPONSE_FILE_SEARCH;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseItemAttachmentAdd.RESPONSE_ITEM_ATTACHMENT_ADD;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseItemAttachmentRemove.RESPONSE_ITEM_ATTACHMENT_REMOVE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseItemCreate.RESPONSE_ITEM_CREATE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseItemGet.RESPONSE_ITEM_GET;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseItemLocationList.RESPONSE_ITEM_LOCATION_LIST;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseItemMetadataPut.RESPONSE_ITEM_METADATA_PUT;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseItemMetadataRemove.RESPONSE_ITEM_METADATA_REMOVE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseItemReposit.RESPONSE_ITEM_REPOSIT;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseItemSearch.RESPONSE_ITEM_SEARCH;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseItemSetName.RESPONSE_ITEM_SET_NAME;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseItemTypesAssign.RESPONSE_ITEM_TYPES_ASSIGN;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseItemTypesRevoke.RESPONSE_ITEM_TYPES_REVOKE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseItemsRemove.RESPONSE_ITEMS_REMOVE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseLocationAttachmentAdd.RESPONSE_LOCATION_ATTACHMENT_ADD;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseLocationAttachmentRemove.RESPONSE_LOCATION_ATTACHMENT_REMOVE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseLocationGet.RESPONSE_LOCATION_GET;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseLocationList.RESPONSE_LOCATION_LIST;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseLocationMetadataPut.RESPONSE_LOCATION_METADATA_PUT;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseLocationMetadataRemove.RESPONSE_LOCATION_METADATA_REMOVE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseLocationPut.RESPONSE_LOCATION_PUT;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseLocationTypesAssign.RESPONSE_LOCATION_TYPES_ASSIGN;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseLocationTypesRevoke.RESPONSE_LOCATION_TYPES_REVOKE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseLogin.RESPONSE_LOGIN;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseRolesAssign.RESPONSE_ROLES_ASSIGN;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseRolesGet.RESPONSE_ROLES_GET;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseRolesRevoke.RESPONSE_ROLES_REVOKE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseTypeDeclarationGet.RESPONSE_TYPE_DECLARATION_GET;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseTypeDeclarationPut.RESPONSE_TYPE_DECLARATION_PUT;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseTypeDeclarationRemove.RESPONSE_TYPE_DECLARATION_REMOVE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseTypeDeclarationSearch.RESPONSE_TYPE_DECLARATION_SEARCH;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseTypeScalarGet.RESPONSE_TYPE_SCALAR_GET;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseTypeScalarPut.RESPONSE_TYPE_SCALAR_PUT;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseTypeScalarRemove.RESPONSE_TYPE_SCALAR_REMOVE;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAUVResponseTypeScalarSearch.RESPONSE_TYPE_SCALAR_SEARCH;

/**
 * A validator.
 */

public enum CAUVMessage
  implements CAProtocolMessageValidatorType<CAIMessageType, ProtocolCAIv1Type>
{
  /**
   * A validator.
   */
  
  MESSAGE;

  private static ProtocolCAIv1Type convertToWireCommand(
    final CAICommandType<?> message)
    throws CAProtocolException
  {
    return switch (message) {
      case final CAICommandDebugInvalid c -> {
        throw new IllegalStateException(
          "Cannot serialize messages of type " + c.getClass()
        );
      }
      case final CAICommandDebugRandom c -> {
        throw new IllegalStateException(
          "Cannot serialize messages of type " + c.getClass()
        );
      }
      case final CAICommandFileGet c -> {
        yield COMMAND_FILE_GET.convertToWire(c);
      }
      case final CAICommandFilePut c -> {
        yield COMMAND_FILE_PUT.convertToWire(c);
      }
      case final CAICommandFileRemove c -> {
        yield COMMAND_FILE_REMOVE.convertToWire(c);
      }
      case final CAICommandFileSearchBegin c -> {
        yield COMMAND_FILE_SEARCH_BEGIN.convertToWire(c);
      }
      case final CAICommandFileSearchNext c -> {
        yield COMMAND_FILE_SEARCH_NEXT.convertToWire(c);
      }
      case final CAICommandFileSearchPrevious c -> {
        yield COMMAND_FILE_SEARCH_PREVIOUS.convertToWire(c);
      }
      case final CAICommandItemAttachmentAdd c -> {
        yield COMMAND_ITEM_ATTACHMENT_ADD.convertToWire(c);
      }
      case final CAICommandItemAttachmentRemove c -> {
        yield COMMAND_ITEM_ATTACHMENT_REMOVE.convertToWire(c);
      }
      case final CAICommandItemCreate c -> {
        yield COMMAND_ITEM_CREATE.convertToWire(c);
      }
      case final CAICommandItemGet c -> {
        yield COMMAND_ITEM_GET.convertToWire(c);
      }
      case final CAICommandItemLocationsList c -> {
        yield COMMAND_ITEM_LOCATIONS_LIST.convertToWire(c);
      }
      case final CAICommandItemMetadataPut c -> {
        yield COMMAND_ITEM_METADATA_PUT.convertToWire(c);
      }
      case final CAICommandItemMetadataRemove c -> {
        yield COMMAND_ITEM_METADATA_REMOVE.convertToWire(c);
      }
      case final CAICommandItemReposit c -> {
        yield COMMAND_ITEM_REPOSIT.convertToWire(c);
      }
      case final CAICommandItemSearchBegin c -> {
        yield COMMAND_ITEM_SEARCH_BEGIN.convertToWire(c);
      }
      case final CAICommandItemSearchNext c -> {
        yield COMMAND_ITEM_SEARCH_NEXT.convertToWire(c);
      }
      case final CAICommandItemSearchPrevious c -> {
        yield COMMAND_ITEM_SEARCH_PREVIOUS.convertToWire(c);
      }
      case final CAICommandItemSetName c -> {
        yield COMMAND_ITEM_SET_NAME.convertToWire(c);
      }
      case final CAICommandItemTypesAssign c -> {
        yield COMMAND_ITEM_TYPES_ASSIGN.convertToWire(c);
      }
      case final CAICommandItemTypesRevoke c -> {
        yield COMMAND_ITEM_TYPES_REVOKE.convertToWire(c);
      }
      case final CAICommandItemsRemove c -> {
        yield COMMAND_ITEMS_REMOVE.convertToWire(c);
      }
      case final CAICommandLocationAttachmentAdd c -> {
        yield COMMAND_LOCATION_ATTACHMENT_ADD.convertToWire(c);
      }
      case final CAICommandLocationAttachmentRemove c -> {
        yield COMMAND_LOCATION_ATTACHMENT_REMOVE.convertToWire(c);
      }
      case final CAICommandLocationGet c -> {
        yield COMMAND_LOCATION_GET.convertToWire(c);
      }
      case final CAICommandLocationList c -> {
        yield COMMAND_LOCATION_LIST.convertToWire(c);
      }
      case final CAICommandLocationMetadataPut c -> {
        yield COMMAND_LOCATION_METADATA_PUT.convertToWire(c);
      }
      case final CAICommandLocationMetadataRemove c -> {
        yield COMMAND_LOCATION_METADATA_REMOVE.convertToWire(c);
      }
      case final CAICommandLocationPut c -> {
        yield COMMAND_LOCATION_PUT.convertToWire(c);
      }
      case final CAICommandLocationTypesAssign c -> {
        yield COMMAND_LOCATION_TYPES_ASSIGN.convertToWire(c);
      }
      case final CAICommandLocationTypesRevoke c -> {
        yield COMMAND_LOCATION_TYPES_REVOKE.convertToWire(c);
      }
      case final CAICommandLogin c -> {
        yield COMMAND_LOGIN.convertToWire(c);
      }
      case final CAICommandRolesAssign c -> {
        yield COMMAND_ROLES_ASSIGN.convertToWire(c);
      }
      case final CAICommandRolesGet c -> {
        yield COMMAND_ROLES_GET.convertToWire(c);
      }
      case final CAICommandRolesRevoke c -> {
        yield COMMAND_ROLES_REVOKE.convertToWire(c);
      }
      case final CAICommandTypeDeclarationGet c -> {
        yield COMMAND_TYPE_DECLARATION_GET.convertToWire(c);
      }
      case final CAICommandTypeDeclarationPut c -> {
        yield COMMAND_TYPE_DECLARATION_PUT.convertToWire(c);
      }
      case final CAICommandTypeDeclarationRemove c -> {
        yield COMMAND_TYPE_DECLARATION_REMOVE.convertToWire(c);
      }
      case final CAICommandTypeDeclarationSearchBegin c -> {
        yield COMMAND_TYPE_DECLARATION_SEARCH_BEGIN.convertToWire(c);
      }
      case final CAICommandTypeDeclarationSearchNext c -> {
        yield COMMAND_TYPE_DECLARATION_SEARCH_NEXT.convertToWire(c);
      }
      case final CAICommandTypeDeclarationSearchPrevious c -> {
        yield COMMAND_TYPE_DECLARATION_SEARCH_PREVIOUS.convertToWire(c);
      }
      case final CAICommandTypeScalarGet c -> {
        yield COMMAND_TYPE_SCALAR_GET.convertToWire(c);
      }
      case final CAICommandTypeScalarPut c -> {
        yield COMMAND_TYPE_SCALAR_PUT.convertToWire(c);
      }
      case final CAICommandTypeScalarRemove c -> {
        yield COMMAND_TYPE_SCALAR_REMOVE.convertToWire(c);
      }
      case final CAICommandTypeScalarSearchBegin c -> {
        yield COMMAND_TYPE_SCALAR_SEARCH_BEGIN.convertToWire(c);
      }
      case final CAICommandTypeScalarSearchNext c -> {
        yield COMMAND_TYPE_SCALAR_SEARCH_NEXT.convertToWire(c);
      }
      case final CAICommandTypeScalarSearchPrevious c -> {
        yield COMMAND_TYPE_SCALAR_SEARCH_PREVIOUS.convertToWire(c);
      }
    };
  }

  @Override
  public ProtocolCAIv1Type convertToWire(
    final CAIMessageType message)
    throws CAProtocolException
  {
    return switch (message) {
      case final CAICommandType<?> c -> {
        yield CAUVMessage.convertToWireCommand(c);
      }
      case final CAIEventType e -> {
        yield convertToWireEvent(e);
      }
      case final CAIResponseType r -> {
        yield convertToWireResponse(r);
      }
    };
  }

  private static ProtocolCAIv1Type convertToWireEvent(
    final CAIEventType message)
  {
    return switch (message) {
      case final CAIEventUpdated e -> EVENT_UPDATED.convertToWire(e);
    };
  }

  private static ProtocolCAIv1Type convertToWireResponse(
    final CAIResponseType response)
  {
    return switch (response) {
      case final CAIResponseError r -> {
        yield RESPONSE_ERROR.convertToWire(r);
      }
      case final CAIResponseFileGet r -> {
        yield RESPONSE_FILE_GET.convertToWire(r);
      }
      case final CAIResponseFilePut r -> {
        yield RESPONSE_FILE_PUT.convertToWire(r);
      }
      case final CAIResponseFileRemove r -> {
        yield RESPONSE_FILE_REMOVE.convertToWire(r);
      }
      case final CAIResponseFileSearch r -> {
        yield RESPONSE_FILE_SEARCH.convertToWire(r);
      }
      case final CAIResponseItemAttachmentAdd r -> {
        yield RESPONSE_ITEM_ATTACHMENT_ADD.convertToWire(r);
      }
      case final CAIResponseItemAttachmentRemove r -> {
        yield RESPONSE_ITEM_ATTACHMENT_REMOVE.convertToWire(r);
      }
      case final CAIResponseItemCreate r -> {
        yield RESPONSE_ITEM_CREATE.convertToWire(r);
      }
      case final CAIResponseItemGet r -> {
        yield RESPONSE_ITEM_GET.convertToWire(r);
      }
      case final CAIResponseItemLocationsList r -> {
        yield RESPONSE_ITEM_LOCATION_LIST.convertToWire(r);
      }
      case final CAIResponseItemMetadataPut r -> {
        yield RESPONSE_ITEM_METADATA_PUT.convertToWire(r);
      }
      case final CAIResponseItemMetadataRemove r -> {
        yield RESPONSE_ITEM_METADATA_REMOVE.convertToWire(r);
      }
      case final CAIResponseItemReposit r -> {
        yield RESPONSE_ITEM_REPOSIT.convertToWire(r);
      }
      case final CAIResponseItemSearch r -> {
        yield RESPONSE_ITEM_SEARCH.convertToWire(r);
      }
      case final CAIResponseItemSetName r -> {
        yield RESPONSE_ITEM_SET_NAME.convertToWire(r);
      }
      case final CAIResponseItemTypesAssign r -> {
        yield RESPONSE_ITEM_TYPES_ASSIGN.convertToWire(r);
      }
      case final CAIResponseItemTypesRevoke r -> {
        yield RESPONSE_ITEM_TYPES_REVOKE.convertToWire(r);
      }
      case final CAIResponseItemsRemove r -> {
        yield RESPONSE_ITEMS_REMOVE.convertToWire(r);
      }
      case final CAIResponseLocationAttachmentAdd r -> {
        yield RESPONSE_LOCATION_ATTACHMENT_ADD.convertToWire(r);
      }
      case final CAIResponseLocationAttachmentRemove r -> {
        yield RESPONSE_LOCATION_ATTACHMENT_REMOVE.convertToWire(r);
      }
      case final CAIResponseLocationGet r -> {
        yield RESPONSE_LOCATION_GET.convertToWire(r);
      }
      case final CAIResponseLocationList r -> {
        yield RESPONSE_LOCATION_LIST.convertToWire(r);
      }
      case final CAIResponseLocationMetadataPut r -> {
        yield RESPONSE_LOCATION_METADATA_PUT.convertToWire(r);
      }
      case final CAIResponseLocationMetadataRemove r -> {
        yield RESPONSE_LOCATION_METADATA_REMOVE.convertToWire(r);
      }
      case final CAIResponseLocationPut r -> {
        yield RESPONSE_LOCATION_PUT.convertToWire(r);
      }
      case final CAIResponseLocationTypesAssign r -> {
        yield RESPONSE_LOCATION_TYPES_ASSIGN.convertToWire(r);
      }
      case final CAIResponseLocationTypesRevoke r -> {
        yield RESPONSE_LOCATION_TYPES_REVOKE.convertToWire(r);
      }
      case final CAIResponseLogin r -> {
        yield RESPONSE_LOGIN.convertToWire(r);
      }
      case final CAIResponseRolesAssign r -> {
        yield RESPONSE_ROLES_ASSIGN.convertToWire(r);
      }
      case final CAIResponseRolesGet r -> {
        yield RESPONSE_ROLES_GET.convertToWire(r);
      }
      case final CAIResponseRolesRevoke r -> {
        yield RESPONSE_ROLES_REVOKE.convertToWire(r);
      }
      case final CAIResponseTypeDeclarationGet r -> {
        yield RESPONSE_TYPE_DECLARATION_GET.convertToWire(r);
      }
      case final CAIResponseTypeDeclarationPut r -> {
        yield RESPONSE_TYPE_DECLARATION_PUT.convertToWire(r);
      }
      case final CAIResponseTypeDeclarationRemove r -> {
        yield RESPONSE_TYPE_DECLARATION_REMOVE.convertToWire(r);
      }
      case final CAIResponseTypeDeclarationSearch r -> {
        yield RESPONSE_TYPE_DECLARATION_SEARCH.convertToWire(r);
      }
      case final CAIResponseTypeScalarGet r -> {
        yield RESPONSE_TYPE_SCALAR_GET.convertToWire(r);
      }
      case final CAIResponseTypeScalarPut r -> {
        yield RESPONSE_TYPE_SCALAR_PUT.convertToWire(r);
      }
      case final CAIResponseTypeScalarRemove r -> {
        yield RESPONSE_TYPE_SCALAR_REMOVE.convertToWire(r);
      }
      case final CAIResponseTypeScalarSearch r -> {
        yield RESPONSE_TYPE_SCALAR_SEARCH.convertToWire(r);
      }
    };
  }

  @Override
  public CAIMessageType convertFromWire(
    final ProtocolCAIv1Type message)
    throws CAProtocolException
  {
    return switch (message) {
      case final CAI1CommandFileGet c -> {
        yield COMMAND_FILE_GET.convertFromWire(c);
      }
      case final CAI1CommandFilePut c -> {
        yield COMMAND_FILE_PUT.convertFromWire(c);
      }
      case final CAI1CommandFileRemove c -> {
        yield COMMAND_FILE_REMOVE.convertFromWire(c);
      }
      case final CAI1CommandFileSearchBegin c -> {
        yield COMMAND_FILE_SEARCH_BEGIN.convertFromWire(c);
      }
      case final CAI1CommandFileSearchNext c -> {
        yield COMMAND_FILE_SEARCH_NEXT.convertFromWire(c);
      }
      case final CAI1CommandFileSearchPrevious c -> {
        yield COMMAND_FILE_SEARCH_PREVIOUS.convertFromWire(c);
      }
      case final CAI1CommandItemAttachmentAdd c -> {
        yield COMMAND_ITEM_ATTACHMENT_ADD.convertFromWire(c);
      }
      case final CAI1CommandItemAttachmentRemove c -> {
        yield COMMAND_ITEM_ATTACHMENT_REMOVE.convertFromWire(c);
      }
      case final CAI1CommandItemCreate c -> {
        yield COMMAND_ITEM_CREATE.convertFromWire(c);
      }
      case final CAI1CommandItemGet c -> {
        yield COMMAND_ITEM_GET.convertFromWire(c);
      }
      case final CAI1CommandItemLocationsList c -> {
        yield COMMAND_ITEM_LOCATIONS_LIST.convertFromWire(c);
      }
      case final CAI1CommandItemMetadataPut c -> {
        yield COMMAND_ITEM_METADATA_PUT.convertFromWire(c);
      }
      case final CAI1CommandItemMetadataRemove c -> {
        yield COMMAND_ITEM_METADATA_REMOVE.convertFromWire(c);
      }
      case final CAI1CommandItemReposit c -> {
        yield COMMAND_ITEM_REPOSIT.convertFromWire(c);
      }
      case final CAI1CommandItemSearchBegin c -> {
        yield COMMAND_ITEM_SEARCH_BEGIN.convertFromWire(c);
      }
      case final CAI1CommandItemSearchNext c -> {
        yield COMMAND_ITEM_SEARCH_NEXT.convertFromWire(c);
      }
      case final CAI1CommandItemSearchPrevious c -> {
        yield COMMAND_ITEM_SEARCH_PREVIOUS.convertFromWire(c);
      }
      case final CAI1CommandItemSetName c -> {
        yield COMMAND_ITEM_SET_NAME.convertFromWire(c);
      }
      case final CAI1CommandItemTypesAssign c -> {
        yield COMMAND_ITEM_TYPES_ASSIGN.convertFromWire(c);
      }
      case final CAI1CommandItemTypesRevoke c -> {
        yield COMMAND_ITEM_TYPES_REVOKE.convertFromWire(c);
      }
      case final CAI1CommandItemsRemove c -> {
        yield COMMAND_ITEMS_REMOVE.convertFromWire(c);
      }
      case final CAI1CommandLocationAttachmentAdd c -> {
        yield COMMAND_LOCATION_ATTACHMENT_ADD.convertFromWire(c);
      }
      case final CAI1CommandLocationAttachmentRemove c -> {
        yield COMMAND_LOCATION_ATTACHMENT_REMOVE.convertFromWire(c);
      }
      case final CAI1CommandLocationGet c -> {
        yield COMMAND_LOCATION_GET.convertFromWire(c);
      }
      case final CAI1CommandLocationList c -> {
        yield COMMAND_LOCATION_LIST.convertFromWire(c);
      }
      case final CAI1CommandLocationMetadataPut c -> {
        yield COMMAND_LOCATION_METADATA_PUT.convertFromWire(c);
      }
      case final CAI1CommandLocationMetadataRemove c -> {
        yield COMMAND_LOCATION_METADATA_REMOVE.convertFromWire(c);
      }
      case final CAI1CommandLocationPut c -> {
        yield COMMAND_LOCATION_PUT.convertFromWire(c);
      }
      case final CAI1CommandLocationTypesAssign c -> {
        yield COMMAND_LOCATION_TYPES_ASSIGN.convertFromWire(c);
      }
      case final CAI1CommandLocationTypesRevoke c -> {
        yield COMMAND_LOCATION_TYPES_REVOKE.convertFromWire(c);
      }
      case final CAI1CommandLogin c -> {
        yield COMMAND_LOGIN.convertFromWire(c);
      }
      case final CAI1CommandRolesAssign c -> {
        yield COMMAND_ROLES_ASSIGN.convertFromWire(c);
      }
      case final CAI1CommandRolesGet c -> {
        yield COMMAND_ROLES_GET.convertFromWire(c);
      }
      case final CAI1CommandRolesRevoke c -> {
        yield COMMAND_ROLES_REVOKE.convertFromWire(c);
      }
      case final CAI1CommandTypeDeclarationGet c -> {
        yield COMMAND_TYPE_DECLARATION_GET.convertFromWire(c);
      }
      case final CAI1CommandTypeDeclarationPut c -> {
        yield COMMAND_TYPE_DECLARATION_PUT.convertFromWire(c);
      }
      case final CAI1CommandTypeDeclarationRemove c -> {
        yield COMMAND_TYPE_DECLARATION_REMOVE.convertFromWire(c);
      }
      case final CAI1CommandTypeDeclarationSearchBegin c -> {
        yield COMMAND_TYPE_DECLARATION_SEARCH_BEGIN.convertFromWire(c);
      }
      case final CAI1CommandTypeDeclarationSearchNext c -> {
        yield COMMAND_TYPE_DECLARATION_SEARCH_NEXT.convertFromWire(c);
      }
      case final CAI1CommandTypeDeclarationSearchPrevious c -> {
        yield COMMAND_TYPE_DECLARATION_SEARCH_PREVIOUS.convertFromWire(c);
      }
      case final CAI1CommandTypeScalarGet c -> {
        yield COMMAND_TYPE_SCALAR_GET.convertFromWire(c);
      }
      case final CAI1CommandTypeScalarPut c -> {
        yield COMMAND_TYPE_SCALAR_PUT.convertFromWire(c);
      }
      case final CAI1CommandTypeScalarRemove c -> {
        yield COMMAND_TYPE_SCALAR_REMOVE.convertFromWire(c);
      }
      case final CAI1CommandTypeScalarSearchBegin c -> {
        yield COMMAND_TYPE_SCALAR_SEARCH_BEGIN.convertFromWire(c);
      }
      case final CAI1CommandTypeScalarSearchNext c -> {
        yield COMMAND_TYPE_SCALAR_SEARCH_NEXT.convertFromWire(c);
      }
      case final CAI1CommandTypeScalarSearchPrevious c -> {
        yield COMMAND_TYPE_SCALAR_SEARCH_PREVIOUS.convertFromWire(c);
      }

      case final CAI1ResponseError r -> {
        yield RESPONSE_ERROR.convertFromWire(r);
      }
      case final CAI1ResponseFileGet r -> {
        yield RESPONSE_FILE_GET.convertFromWire(r);
      }
      case final CAI1ResponseFilePut r -> {
        yield RESPONSE_FILE_PUT.convertFromWire(r);
      }
      case final CAI1ResponseFileRemove r -> {
        yield RESPONSE_FILE_REMOVE.convertFromWire(r);
      }
      case final CAI1ResponseFileSearch r -> {
        yield RESPONSE_FILE_SEARCH.convertFromWire(r);
      }
      case final CAI1ResponseItemAttachmentAdd r -> {
        yield RESPONSE_ITEM_ATTACHMENT_ADD.convertFromWire(r);
      }
      case final CAI1ResponseItemAttachmentRemove r -> {
        yield RESPONSE_ITEM_ATTACHMENT_REMOVE.convertFromWire(r);
      }
      case final CAI1ResponseItemCreate r -> {
        yield RESPONSE_ITEM_CREATE.convertFromWire(r);
      }
      case final CAI1ResponseItemGet r -> {
        yield RESPONSE_ITEM_GET.convertFromWire(r);
      }
      case final CAI1ResponseItemLocationsList r -> {
        yield RESPONSE_ITEM_LOCATION_LIST.convertFromWire(r);
      }
      case final CAI1ResponseItemMetadataPut r -> {
        yield RESPONSE_ITEM_METADATA_PUT.convertFromWire(r);
      }
      case final CAI1ResponseItemMetadataRemove r -> {
        yield RESPONSE_ITEM_METADATA_REMOVE.convertFromWire(r);
      }
      case final CAI1ResponseItemReposit r -> {
        yield RESPONSE_ITEM_REPOSIT.convertFromWire(r);
      }
      case final CAI1ResponseItemSearch r -> {
        yield RESPONSE_ITEM_SEARCH.convertFromWire(r);
      }
      case final CAI1ResponseItemSetName r -> {
        yield RESPONSE_ITEM_SET_NAME.convertFromWire(r);
      }
      case final CAI1ResponseItemTypesAssign r -> {
        yield RESPONSE_ITEM_TYPES_ASSIGN.convertFromWire(r);
      }
      case final CAI1ResponseItemTypesRevoke r -> {
        yield RESPONSE_ITEM_TYPES_REVOKE.convertFromWire(r);
      }
      case final CAI1ResponseItemsRemove r -> {
        yield RESPONSE_ITEMS_REMOVE.convertFromWire(r);
      }
      case final CAI1ResponseLocationAttachmentAdd r -> {
        yield RESPONSE_LOCATION_ATTACHMENT_ADD.convertFromWire(r);
      }
      case final CAI1ResponseLocationAttachmentRemove r -> {
        yield RESPONSE_LOCATION_ATTACHMENT_REMOVE.convertFromWire(r);
      }
      case final CAI1ResponseLocationGet r -> {
        yield RESPONSE_LOCATION_GET.convertFromWire(r);
      }
      case final CAI1ResponseLocationList r -> {
        yield RESPONSE_LOCATION_LIST.convertFromWire(r);
      }
      case final CAI1ResponseLocationMetadataPut r -> {
        yield RESPONSE_LOCATION_METADATA_PUT.convertFromWire(r);
      }
      case final CAI1ResponseLocationMetadataRemove r -> {
        yield RESPONSE_LOCATION_METADATA_REMOVE.convertFromWire(r);
      }
      case final CAI1ResponseLocationPut r -> {
        yield RESPONSE_LOCATION_PUT.convertFromWire(r);
      }
      case final CAI1ResponseLocationTypesAssign r -> {
        yield RESPONSE_LOCATION_TYPES_ASSIGN.convertFromWire(r);
      }
      case final CAI1ResponseLocationTypesRevoke r -> {
        yield RESPONSE_LOCATION_TYPES_REVOKE.convertFromWire(r);
      }
      case final CAI1ResponseLogin r -> {
        yield RESPONSE_LOGIN.convertFromWire(r);
      }
      case final CAI1ResponseRolesAssign r -> {
        yield RESPONSE_ROLES_ASSIGN.convertFromWire(r);
      }
      case final CAI1ResponseRolesGet r -> {
        yield RESPONSE_ROLES_GET.convertFromWire(r);
      }
      case final CAI1ResponseRolesRevoke r -> {
        yield RESPONSE_ROLES_REVOKE.convertFromWire(r);
      }
      case final CAI1ResponseTypeDeclarationGet r -> {
        yield RESPONSE_TYPE_DECLARATION_GET.convertFromWire(r);
      }
      case final CAI1ResponseTypeDeclarationPut r -> {
        yield RESPONSE_TYPE_DECLARATION_PUT.convertFromWire(r);
      }
      case final CAI1ResponseTypeDeclarationRemove r -> {
        yield RESPONSE_TYPE_DECLARATION_REMOVE.convertFromWire(r);
      }
      case final CAI1ResponseTypeDeclarationSearch r -> {
        yield RESPONSE_TYPE_DECLARATION_SEARCH.convertFromWire(r);
      }
      case final CAI1ResponseTypeScalarGet r -> {
        yield RESPONSE_TYPE_SCALAR_GET.convertFromWire(r);
      }
      case final CAI1ResponseTypeScalarPut r -> {
        yield RESPONSE_TYPE_SCALAR_PUT.convertFromWire(r);
      }
      case final CAI1ResponseTypeScalarRemove r -> {
        yield RESPONSE_TYPE_SCALAR_REMOVE.convertFromWire(r);
      }
      case final CAI1ResponseTypeScalarSearch r -> {
        yield RESPONSE_TYPE_SCALAR_SEARCH.convertFromWire(r);
      }

      case final CAI1EventUpdated e -> {
        yield EVENT_UPDATED.convertFromWire(e);
      }
    };
  }
}
