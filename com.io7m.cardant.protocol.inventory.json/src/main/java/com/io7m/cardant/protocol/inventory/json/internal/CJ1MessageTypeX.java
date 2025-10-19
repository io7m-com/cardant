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

import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.inventory.CAICommandAuditSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandAuditSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandAuditSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAICommandDebugInvalid;
import com.io7m.cardant.protocol.inventory.CAICommandDebugRandom;
import com.io7m.cardant.protocol.inventory.CAICommandFileDelete;
import com.io7m.cardant.protocol.inventory.CAICommandFileGet;
import com.io7m.cardant.protocol.inventory.CAICommandFilePut;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentRemove;
import com.io7m.cardant.protocol.inventory.CAICommandItemCreate;
import com.io7m.cardant.protocol.inventory.CAICommandItemDelete;
import com.io7m.cardant.protocol.inventory.CAICommandItemGet;
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataPut;
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataRemove;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAICommandItemSetName;
import com.io7m.cardant.protocol.inventory.CAICommandItemTypesAssign;
import com.io7m.cardant.protocol.inventory.CAICommandItemTypesRevoke;
import com.io7m.cardant.protocol.inventory.CAICommandLocationAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAICommandLocationAttachmentRemove;
import com.io7m.cardant.protocol.inventory.CAICommandLocationDelete;
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
import com.io7m.cardant.protocol.inventory.CAICommandStockCount;
import com.io7m.cardant.protocol.inventory.CAICommandStockReposit;
import com.io7m.cardant.protocol.inventory.CAICommandStockSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandStockSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandStockSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageGetText;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageInstall;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageUninstall;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageUpgrade;
import com.io7m.cardant.protocol.inventory.CAIEventType;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.protocol.inventory.CAITransactionResponse;
import com.io7m.junreachable.UnimplementedCodeException;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandAuditSearchBeginX.AUDIT_SEARCH_BEGIN;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandAuditSearchNextX.AUDIT_SEARCH_NEXT;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandAuditSearchPreviousX.AUDIT_SEARCH_PREVIOUS;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandFileDeleteX.FILE_DELETE;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandFileGetX.FILE_GET;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandFilePutX.FILE_PUT;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandFileSearchBeginX.FILE_SEARCH_BEGIN;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandFileSearchNextX.FILE_SEARCH_NEXT;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandFileSearchPreviousX.FILE_SEARCH_PREVIOUS;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemAttachmentAddX.ITEM_ATTACHMENT_ADD;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemAttachmentRemoveX.ITEM_ATTACHMENT_REMOVE;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemCreateX.ITEM_CREATE;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemDeleteX.ITEM_DELETE;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemGetX.ITEM_GET;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemMetadataPutX.ITEM_METADATA_PUT;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemMetadataRemoveX.ITEM_METADATA_REMOVE;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemSearchBeginX.ITEM_SEARCH_BEGIN;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemSearchNextX.ITEM_SEARCH_NEXT;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemSearchPreviousX.ITEM_SEARCH_PREVIOUS;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemSetNameX.ITEM_SET_NAME;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemTypesAssignX.ITEM_TYPES_ASSIGN;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandItemTypesRevokeX.ITEM_TYPES_REVOKE;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandLocationAttachmentAddX.LOCATION_ATTACHMENT_ADD;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandLocationAttachmentRemoveX.LOCATION_ATTACHMENT_REMOVE;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandLocationDeleteX.LOCATION_DELETE;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandLocationGetX.LOCATION_GET;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandLocationListX.LOCATION_LIST;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandLocationMetadataPutX.LOCATION_METADATA_PUT;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandLocationMetadataRemoveX.LOCATION_METADATA_REMOVE;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandLocationPutX.LOCATION_PUT;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandLocationTypesAssignX.LOCATION_TYPES_ASSIGN;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandLocationTypesRevokeX.LOCATION_TYPES_REVOKE;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandLoginX.LOGIN;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandRolesAssignX.ROLES_ASSIGN;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandRolesGetX.ROLES_GET;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandRolesRevokeX.ROLES_REVOKE;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandStockCountX.STOCK_COUNT;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandStockRepositX.STOCK_REPOSIT;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandStockSearchBeginX.STOCK_SEARCH_BEGIN;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandStockSearchNextX.STOCK_SEARCH_NEXT;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandStockSearchPreviousX.STOCK_SEARCH_PREVIOUS;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandTypePackageGetTextX.TYPE_PACKAGE_GET_TEXT;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandTypePackageInstallX.TYPE_PACKAGE_INSTALL;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandTypePackageSearchBeginX.TYPE_PACKAGE_SEARCH_BEGIN;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandTypePackageSearchNextX.TYPE_PACKAGE_SEARCH_NEXT;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandTypePackageSearchPreviousX.TYPE_PACKAGE_SEARCH_PREVIOUS;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandTypePackageUninstallX.TYPE_PACKAGE_UNINSTALL;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1CommandTypePackageUpgradeX.TYPE_PACKAGE_UPGRADE;
import static com.io7m.cardant.protocol.inventory.json.internal.CJ1TypePackageUninstallX.TYPE_PACKAGE_UNINSTALL_TYPE;

public enum CJ1MessageTypeX
  implements CJ1SerialBijectionType<CJ1MessageType, CAIMessageType>
{
  MESSAGE;

  @Override
  public CAIMessageType toCore(
    final CJ1MessageType m)
    throws CAProtocolException
  {
    return switch (m) {
      case final CJ1CommandAuditSearchBegin mm -> {
        yield AUDIT_SEARCH_BEGIN.toCore(mm);
      }
      case final CJ1CommandAuditSearchNext mm -> {
        yield AUDIT_SEARCH_NEXT.toCore(mm);
      }
      case final CJ1CommandAuditSearchPrevious mm -> {
        yield AUDIT_SEARCH_PREVIOUS.toCore(mm);
      }
      case final CJ1CommandFileDelete mm -> {
        yield FILE_DELETE.toCore(mm);
      }
      case final CJ1CommandFileGet mm -> {
        yield FILE_GET.toCore(mm);
      }
      case final CJ1CommandFilePut mm -> {
        yield FILE_PUT.toCore(mm);
      }
      case final CJ1CommandFileSearchBegin mm -> {
        yield FILE_SEARCH_BEGIN.toCore(mm);
      }
      case final CJ1CommandFileSearchNext mm -> {
        yield FILE_SEARCH_NEXT.toCore(mm);
      }
      case final CJ1CommandFileSearchPrevious mm -> {
        yield FILE_SEARCH_PREVIOUS.toCore(mm);
      }
      case final CJ1CommandItemAttachmentAdd mm -> {
        yield ITEM_ATTACHMENT_ADD.toCore(mm);
      }
      case final CJ1CommandItemAttachmentRemove mm -> {
        yield ITEM_ATTACHMENT_REMOVE.toCore(mm);
      }
      case final CJ1CommandItemCreate mm -> {
        yield ITEM_CREATE.toCore(mm);
      }
      case final CJ1CommandItemDelete mm -> {
        yield ITEM_DELETE.toCore(mm);
      }
      case final CJ1CommandItemGet mm -> {
        yield ITEM_GET.toCore(mm);
      }
      case final CJ1CommandItemMetadataPut mm -> {
        yield ITEM_METADATA_PUT.toCore(mm);
      }
      case final CJ1CommandItemMetadataRemove mm -> {
        yield ITEM_METADATA_REMOVE.toCore(mm);
      }
      case final CJ1CommandItemSearchBegin mm -> {
        yield ITEM_SEARCH_BEGIN.toCore(mm);
      }
      case final CJ1CommandItemSearchNext mm -> {
        yield ITEM_SEARCH_NEXT.toCore(mm);
      }
      case final CJ1CommandItemSearchPrevious mm -> {
        yield ITEM_SEARCH_PREVIOUS.toCore(mm);
      }
      case final CJ1CommandItemSetName mm -> {
        yield ITEM_SET_NAME.toCore(mm);
      }
      case final CJ1CommandLocationDelete mm -> {
        yield LOCATION_DELETE.toCore(mm);
      }
      case final CJ1CommandLocationGet mm -> {
        yield LOCATION_GET.toCore(mm);
      }
      case final CJ1CommandLocationMetadataPut mm -> {
        yield LOCATION_METADATA_PUT.toCore(mm);
      }
      case final CJ1CommandLocationMetadataRemove mm -> {
        yield LOCATION_METADATA_REMOVE.toCore(mm);
      }
      case final CJ1CommandLogin mm -> {
        yield LOGIN.toCore(mm);
      }
      case final CJ1CommandLocationAttachmentAdd mm -> {
        yield LOCATION_ATTACHMENT_ADD.toCore(mm);
      }
      case final CJ1CommandLocationAttachmentRemove mm -> {
        yield LOCATION_ATTACHMENT_REMOVE.toCore(mm);
      }
      case final CJ1CommandLocationPut mm -> {
        yield LOCATION_PUT.toCore(mm);
      }
      case final CJ1CommandItemTypesAssign mm -> {
        yield ITEM_TYPES_ASSIGN.toCore(mm);
      }
      case final CJ1CommandItemTypesRevoke mm -> {
        yield ITEM_TYPES_REVOKE.toCore(mm);
      }
      case final CJ1CommandLocationTypesAssign mm -> {
        yield LOCATION_TYPES_ASSIGN.toCore(mm);
      }
      case final CJ1CommandLocationTypesRevoke mm -> {
        yield LOCATION_TYPES_REVOKE.toCore(mm);
      }
      case final CJ1CommandLocationList mm -> {
        yield LOCATION_LIST.toCore(mm);
      }
      case final CJ1CommandRolesAssign mm -> {
        yield ROLES_ASSIGN.toCore(mm);
      }
      case final CJ1CommandRolesRevoke mm -> {
        yield ROLES_REVOKE.toCore(mm);
      }
      case final CJ1CommandRolesGet mm -> {
        yield ROLES_GET.toCore(mm);
      }
      case final CJ1CommandStockCount mm -> {
        yield STOCK_COUNT.toCore(mm);
      }
      case final CJ1CommandStockSearchBegin mm -> {
        yield STOCK_SEARCH_BEGIN.toCore(mm);
      }
      case final CJ1CommandStockSearchNext mm -> {
        yield STOCK_SEARCH_NEXT.toCore(mm);
      }
      case final CJ1CommandStockSearchPrevious mm -> {
        yield STOCK_SEARCH_PREVIOUS.toCore(mm);
      }
      case final CJ1CommandStockReposit mm -> {
        yield STOCK_REPOSIT.toCore(mm);
      }
      case final CJ1CommandTypePackageGetText mm -> {
        yield TYPE_PACKAGE_GET_TEXT.toCore(mm);
      }
      case final CJ1CommandTypePackageInstall mm -> {
        yield TYPE_PACKAGE_INSTALL.toCore(mm);
      }
      case final CJ1CommandTypePackageSearchBegin mm -> {
        yield TYPE_PACKAGE_SEARCH_BEGIN.toCore(mm);
      }
      case final CJ1CommandTypePackageSearchNext mm -> {
        yield TYPE_PACKAGE_SEARCH_NEXT.toCore(mm);
      }
      case final CJ1CommandTypePackageSearchPrevious mm -> {
        yield TYPE_PACKAGE_SEARCH_PREVIOUS.toCore(mm);
      }
      case final CJ1CommandTypePackageUninstall mm -> {
        yield TYPE_PACKAGE_UNINSTALL.toCore(mm);
      }
      case final CJ1CommandTypePackageUpgrade mm -> {
        yield TYPE_PACKAGE_UPGRADE.toCore(mm);
      }
    };
  }

  @Override
  public CJ1MessageType toCJ1(
    final CAIMessageType m)
    throws CAProtocolException
  {
    return switch (m) {
      case final CAICommandType<?> mm -> {
        yield this.toCJ1Command(mm);
      }
      case final CAIEventType mm -> {
        throw new UnimplementedCodeException();
      }
      case final CAIResponseType mm -> {
        throw new UnimplementedCodeException();
      }
      case final CAITransactionResponse mm -> {
        throw new UnimplementedCodeException();
      }
    };
  }

  private CJ1MessageType toCJ1Command(
    final CAICommandType<?> m)
    throws CAProtocolException
  {
    return switch (m) {
      case final CAICommandAuditSearchBegin mm -> {
        yield AUDIT_SEARCH_BEGIN.toCJ1(mm);
      }
      case final CAICommandAuditSearchNext mm -> {
        yield AUDIT_SEARCH_NEXT.toCJ1(mm);
      }
      case final CAICommandAuditSearchPrevious mm -> {
        yield AUDIT_SEARCH_PREVIOUS.toCJ1(mm);
      }
      case final CAICommandDebugInvalid mm -> {
        throw new UnimplementedCodeException();
      }
      case final CAICommandDebugRandom mm -> {
        throw new UnimplementedCodeException();
      }
      case final CAICommandFileDelete mm -> {
        yield FILE_DELETE.toCJ1(mm);
      }
      case final CAICommandFileGet mm -> {
        yield FILE_GET.toCJ1(mm);
      }
      case final CAICommandFilePut mm -> {
        yield FILE_PUT.toCJ1(mm);
      }
      case final CAICommandFileSearchBegin mm -> {
        yield FILE_SEARCH_BEGIN.toCJ1(mm);
      }
      case final CAICommandFileSearchNext mm -> {
        yield FILE_SEARCH_NEXT.toCJ1(mm);
      }
      case final CAICommandFileSearchPrevious mm -> {
        yield FILE_SEARCH_PREVIOUS.toCJ1(mm);
      }
      case final CAICommandItemAttachmentAdd mm -> {
        yield ITEM_ATTACHMENT_ADD.toCJ1(mm);
      }
      case final CAICommandItemAttachmentRemove mm -> {
        yield ITEM_ATTACHMENT_REMOVE.toCJ1(mm);
      }
      case final CAICommandItemCreate mm -> {
        yield ITEM_CREATE.toCJ1(mm);
      }
      case final CAICommandItemDelete mm -> {
        yield ITEM_DELETE.toCJ1(mm);
      }
      case final CAICommandItemGet mm -> {
        yield ITEM_GET.toCJ1(mm);
      }
      case final CAICommandItemMetadataPut mm -> {
        yield ITEM_METADATA_PUT.toCJ1(mm);
      }
      case final CAICommandItemMetadataRemove mm -> {
        yield ITEM_METADATA_REMOVE.toCJ1(mm);
      }
      case final CAICommandItemSearchBegin mm -> {
        yield ITEM_SEARCH_BEGIN.toCJ1(mm);
      }
      case final CAICommandItemSearchNext mm -> {
        yield ITEM_SEARCH_NEXT.toCJ1(mm);
      }
      case final CAICommandItemSearchPrevious mm -> {
        yield ITEM_SEARCH_PREVIOUS.toCJ1(mm);
      }
      case final CAICommandItemSetName mm -> {
        yield ITEM_SET_NAME.toCJ1(mm);
      }
      case final CAICommandItemTypesAssign mm -> {
        yield ITEM_TYPES_ASSIGN.toCJ1(mm);
      }
      case final CAICommandItemTypesRevoke mm -> {
        yield ITEM_TYPES_REVOKE.toCJ1(mm);
      }
      case final CAICommandLocationAttachmentAdd mm -> {
        yield LOCATION_ATTACHMENT_ADD.toCJ1(mm);
      }
      case final CAICommandLocationAttachmentRemove mm -> {
        yield LOCATION_ATTACHMENT_REMOVE.toCJ1(mm);
      }
      case final CAICommandLocationDelete mm -> {
        yield LOCATION_DELETE.toCJ1(mm);
      }
      case final CAICommandLocationGet mm -> {
        yield LOCATION_GET.toCJ1(mm);
      }
      case final CAICommandLocationList mm -> {
        yield LOCATION_LIST.toCJ1(mm);
      }
      case final CAICommandLocationMetadataPut mm -> {
        yield LOCATION_METADATA_PUT.toCJ1(mm);
      }
      case final CAICommandLocationMetadataRemove mm -> {
        yield LOCATION_METADATA_REMOVE.toCJ1(mm);
      }
      case final CAICommandLocationPut mm -> {
        yield LOCATION_PUT.toCJ1(mm);
      }
      case final CAICommandLocationTypesAssign mm -> {
        yield LOCATION_TYPES_ASSIGN.toCJ1(mm);
      }
      case final CAICommandLocationTypesRevoke mm -> {
        yield LOCATION_TYPES_REVOKE.toCJ1(mm);
      }
      case final CAICommandLogin mm -> {
        yield LOGIN.toCJ1(mm);
      }
      case final CAICommandRolesAssign mm -> {
        yield ROLES_ASSIGN.toCJ1(mm);
      }
      case final CAICommandRolesGet mm -> {
        yield ROLES_GET.toCJ1(mm);
      }
      case final CAICommandRolesRevoke mm -> {
        yield ROLES_REVOKE.toCJ1(mm);
      }
      case final CAICommandStockCount mm -> {
        yield STOCK_COUNT.toCJ1(mm);
      }
      case final CAICommandStockReposit mm -> {
        yield STOCK_REPOSIT.toCJ1(mm);
      }
      case final CAICommandStockSearchBegin mm -> {
        yield STOCK_SEARCH_BEGIN.toCJ1(mm);
      }
      case final CAICommandStockSearchNext mm -> {
        yield STOCK_SEARCH_NEXT.toCJ1(mm);
      }
      case final CAICommandStockSearchPrevious mm -> {
        yield STOCK_SEARCH_PREVIOUS.toCJ1(mm);
      }
      case final CAICommandTypePackageGetText mm -> {
        yield TYPE_PACKAGE_GET_TEXT.toCJ1(mm);
      }
      case final CAICommandTypePackageInstall mm -> {
        yield TYPE_PACKAGE_INSTALL.toCJ1(mm);
      }
      case final CAICommandTypePackageSearchBegin mm -> {
        yield TYPE_PACKAGE_SEARCH_BEGIN.toCJ1(mm);
      }
      case final CAICommandTypePackageSearchNext mm -> {
        yield TYPE_PACKAGE_SEARCH_NEXT.toCJ1(mm);
      }
      case final CAICommandTypePackageSearchPrevious mm -> {
        yield TYPE_PACKAGE_SEARCH_PREVIOUS.toCJ1(mm);
      }
      case final CAICommandTypePackageUninstall mm -> {
        yield TYPE_PACKAGE_UNINSTALL.toCJ1(mm);
      }
      case final CAICommandTypePackageUpgrade mm -> {
        yield TYPE_PACKAGE_UPGRADE.toCJ1(mm);
      }
    };
  }
}
