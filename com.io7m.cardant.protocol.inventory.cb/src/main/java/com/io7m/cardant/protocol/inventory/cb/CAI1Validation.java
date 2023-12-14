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

package com.io7m.cardant.protocol.inventory.cb;

import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIEventType;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon;
import com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationEvents;
import com.io7m.cardant.protocol.inventory.cb.internal.FromWireCommands;
import com.io7m.cardant.protocol.inventory.cb.internal.FromWireResponses;
import com.io7m.cardant.protocol.inventory.cb.internal.ProtocolUncheckedException;
import com.io7m.cardant.protocol.inventory.cb.internal.ToWireCommands;
import com.io7m.cardant.protocol.inventory.cb.internal.ToWireResponses;

/**
 * Functions to translate between the core command set and the Inventory v1
 * Cedarbridge encoding command set.
 */

public final class CAI1Validation
  implements CAProtocolMessageValidatorType<CAIMessageType, ProtocolCAIv1Type>
{
  /**
   * Functions to translate between the core command set and the Inventory v1
   * Cedarbridge encoding command set.
   */

  public CAI1Validation()
  {

  }

  @Override
  public ProtocolCAIv1Type convertToWire(
    final CAIMessageType message)
    throws CAProtocolException
  {
    try {
      if (message instanceof final CAICommandType<?> cmd) {
        return ToWireCommands.command(cmd);
      } else if (message instanceof final CAIResponseType response) {
        return ToWireResponses.response(response);
      } else if (message instanceof final CAIEventType event) {
        return CAI1ValidationEvents.convertToWireEvent(event);
      }
    } catch (final ProtocolUncheckedException e) {
      throw e.getCause();
    }

    throw CAI1ValidationCommon.errorProtocol(message);
  }

  @Override
  public CAIMessageType convertFromWire(
    final ProtocolCAIv1Type message)
    throws CAProtocolException
  {
    try {

      /*
       * Events.
       */

      if (message instanceof final CAI1EventUpdated m) {
        return CAI1ValidationEvents.convertFromWireCAI1EventUpdated(m);
      }

      /*
       * Commands.
       */

      if (message instanceof final CAI1CommandFilePut m) {
        return FromWireCommands.filePut(m);
      }
      if (message instanceof final CAI1CommandFileRemove m) {
        return FromWireCommands.fileRemove(m);
      }
      if (message instanceof final CAI1CommandItemAttachmentAdd m) {
        return FromWireCommands.itemAttachmentAdd(m);
      }
      if (message instanceof final CAI1CommandItemAttachmentRemove m) {
        return FromWireCommands.itemAttachmentRemove(m);
      }
      if (message instanceof final CAI1CommandItemCreate m) {
        return FromWireCommands.itemCreate(m);
      }
      if (message instanceof final CAI1CommandItemGet m) {
        return FromWireCommands.itemGet(m);
      }
      if (message instanceof final CAI1CommandItemLocationsList m) {
        return FromWireCommands.itemLocationsList(m);
      }
      if (message instanceof final CAI1CommandItemMetadataPut m) {
        return FromWireCommands.itemMetadataPut(m);
      }
      if (message instanceof final CAI1CommandItemMetadataRemove m) {
        return FromWireCommands.itemMetadataRemove(m);
      }
      if (message instanceof final CAI1CommandItemReposit m) {
        return FromWireCommands.itemReposit(m);
      }
      if (message instanceof final CAI1CommandItemsRemove m) {
        return FromWireCommands.itemsRemove(m);
      }
      if (message instanceof final CAI1CommandItemSetName m) {
        return FromWireCommands.itemSetName(m);
      }
      if (message instanceof final CAI1CommandLocationGet m) {
        return FromWireCommands.locationGet(m);
      }
      if (message instanceof final CAI1CommandLocationList m) {
        return FromWireCommands.locationList(m);
      }
      if (message instanceof final CAI1CommandLocationPut m) {
        return FromWireCommands.locationPut(m);
      }
      if (message instanceof final CAI1CommandLogin m) {
        return FromWireCommands.login(m);
      }
      if (message instanceof final CAI1CommandItemSearchBegin m) {
        return FromWireCommands.itemSearchBegin(m);
      }
      if (message instanceof final CAI1CommandItemSearchNext m) {
        return FromWireCommands.itemSearchNext(m);
      }
      if (message instanceof final CAI1CommandItemSearchPrevious m) {
        return FromWireCommands.itemSearchPrevious(m);
      }
      if (message instanceof final CAI1CommandFileSearchBegin m) {
        return FromWireCommands.fileSearchBegin(m);
      }
      if (message instanceof final CAI1CommandFileSearchNext m) {
        return FromWireCommands.fileSearchNext(m);
      }
      if (message instanceof final CAI1CommandFileSearchPrevious m) {
        return FromWireCommands.fileSearchPrevious(m);
      }
      if (message instanceof final CAI1CommandRolesAssign m) {
        return FromWireCommands.rolesAssign(m);
      }
      if (message instanceof final CAI1CommandRolesRevoke m) {
        return FromWireCommands.rolesRevoke(m);
      }
      if (message instanceof final CAI1CommandRolesGet m) {
        return FromWireCommands.rolesGet(m);
      }
      if (message instanceof final CAI1CommandFileGet m) {
        return FromWireCommands.fileGet(m);
      }
      if (message instanceof final CAI1CommandTypeScalarPut c) {
        return FromWireCommands.typeScalarPut(c);
      }
      if (message instanceof final CAI1CommandTypeScalarGet c) {
        return FromWireCommands.typeScalarGet(c);
      }
      if (message instanceof final CAI1CommandTypeScalarSearchBegin c) {
        return FromWireCommands.typeScalarSearchBegin(c);
      }
      if (message instanceof final CAI1CommandTypeScalarSearchNext c) {
        return FromWireCommands.typeScalarSearchNext(c);
      }
      if (message instanceof final CAI1CommandTypeScalarSearchPrevious c) {
        return FromWireCommands.typeScalarSearchPrevious(c);
      }
      if (message instanceof final CAI1CommandTypeScalarRemove c) {
        return FromWireCommands.typeScalarRemove(c);
      }
      if (message instanceof final CAI1CommandTypeDeclarationPut c) {
        return FromWireCommands.typeDeclarationPut(c);
      }
      if (message instanceof final CAI1CommandTypeDeclarationGet c) {
        return FromWireCommands.typeDeclarationGet(c);
      }
      if (message instanceof final CAI1CommandTypeDeclarationSearchBegin c) {
        return FromWireCommands.typeDeclarationSearchBegin(c);
      }
      if (message instanceof final CAI1CommandTypeDeclarationSearchNext c) {
        return FromWireCommands.typeDeclarationSearchNext(c);
      }
      if (message instanceof final CAI1CommandTypeDeclarationSearchPrevious c) {
        return FromWireCommands.typeDeclarationSearchPrevious(c);
      }
      if (message instanceof final CAI1CommandTypeDeclarationRemove c) {
        return FromWireCommands.typeDeclarationRemove(c);
      }
      if (message instanceof final CAI1CommandItemTypesAssign c) {
        return FromWireCommands.itemTypesAssign(c);
      }
      if (message instanceof final CAI1CommandItemTypesRevoke c) {
        return FromWireCommands.itemTypesRevoke(c);
      }
      if (message instanceof final CAI1CommandLocationTypesAssign c) {
        return FromWireCommands.locationTypesAssign(c);
      }
      if (message instanceof final CAI1CommandLocationTypesRevoke c) {
        return FromWireCommands.locationTypesRevoke(c);
      }
      if (message instanceof final CAI1CommandLocationMetadataPut m) {
        return FromWireCommands.locationMetadataPut(m);
      }
      if (message instanceof final CAI1CommandLocationMetadataRemove m) {
        return FromWireCommands.locationMetadataRemove(m);
      }
      if (message instanceof final CAI1CommandLocationAttachmentAdd m) {
        return FromWireCommands.locationAttachmentAdd(m);
      }
      if (message instanceof final CAI1CommandLocationAttachmentRemove m) {
        return FromWireCommands.locationAttachmentRemove(m);
      }

      /*
       * Response.
       */

      if (message instanceof final CAI1ResponseFilePut m) {
        return FromWireResponses.filePut(m);
      }
      if (message instanceof final CAI1ResponseFileRemove m) {
        return FromWireResponses.fileRemove(m);
      }
      if (message instanceof final CAI1ResponseItemAttachmentAdd m) {
        return FromWireResponses.itemAttachmentAdd(m);
      }
      if (message instanceof final CAI1ResponseItemAttachmentRemove m) {
        return FromWireResponses.itemAttachmentRemove(m);
      }
      if (message instanceof final CAI1ResponseItemCreate m) {
        return FromWireResponses.itemCreate(m);
      }
      if (message instanceof final CAI1ResponseItemGet m) {
        return FromWireResponses.itemGet(m);
      }
      if (message instanceof final CAI1ResponseItemSearch m) {
        return FromWireResponses.itemSearch(m);
      }
      if (message instanceof final CAI1ResponseFileSearch m) {
        return FromWireResponses.fileSearch(m);
      }
      if (message instanceof final CAI1ResponseItemLocationsList m) {
        return FromWireResponses.itemLocationsList(m);
      }
      if (message instanceof final CAI1ResponseItemMetadataPut m) {
        return FromWireResponses.itemMetadataPut(m);
      }
      if (message instanceof final CAI1ResponseItemMetadataRemove m) {
        return FromWireResponses.itemMetadataRemove(m);
      }
      if (message instanceof final CAI1ResponseItemReposit m) {
        return FromWireResponses.itemReposit(m);
      }
      if (message instanceof final CAI1ResponseItemsRemove m) {
        return FromWireResponses.itemsRemove(m);
      }
      if (message instanceof final CAI1ResponseItemSetName m) {
        return FromWireResponses.itemSetName(m);
      }
      if (message instanceof final CAI1ResponseLocationGet m) {
        return FromWireResponses.locationGet(m);
      }
      if (message instanceof final CAI1ResponseLocationList m) {
        return FromWireResponses.locationList(m);
      }
      if (message instanceof final CAI1ResponseLocationPut m) {
        return FromWireResponses.locationPut(m);
      }
      if (message instanceof final CAI1ResponseLogin m) {
        return FromWireResponses.login(m);
      }
      if (message instanceof final CAI1ResponseError m) {
        return FromWireResponses.error(m);
      }
      if (message instanceof final CAI1ResponseRolesRevoke m) {
        return FromWireResponses.rolesRevoke(m);
      }
      if (message instanceof final CAI1ResponseRolesAssign m) {
        return FromWireResponses.rolesAssign(m);
      }
      if (message instanceof final CAI1ResponseRolesGet m) {
        return FromWireResponses.rolesGet(m);
      }
      if (message instanceof final CAI1ResponseFileGet m) {
        return FromWireResponses.fileGet(m);
      }
      if (message instanceof final CAI1ResponseTypeScalarPut m) {
        return FromWireResponses.typeScalarPut(m);
      }
      if (message instanceof final CAI1ResponseTypeScalarGet m) {
        return FromWireResponses.typeScalarGet(m);
      }
      if (message instanceof final CAI1ResponseTypeScalarSearch m) {
        return FromWireResponses.typeScalarSearch(m);
      }
      if (message instanceof final CAI1ResponseTypeScalarRemove m) {
        return FromWireResponses.typeScalarRemove(m);
      }
      if (message instanceof final CAI1ResponseTypeDeclarationPut m) {
        return FromWireResponses.typeDeclarationPut(m);
      }
      if (message instanceof final CAI1ResponseTypeDeclarationGet m) {
        return FromWireResponses.typeDeclarationGet(m);
      }
      if (message instanceof final CAI1ResponseTypeDeclarationSearch m) {
        return FromWireResponses.typeDeclarationSearch(m);
      }
      if (message instanceof final CAI1ResponseTypeDeclarationRemove m) {
        return FromWireResponses.typeDeclarationRemove(m);
      }
      if (message instanceof final CAI1ResponseItemTypesAssign c) {
        return FromWireResponses.itemTypesAssign(c);
      }
      if (message instanceof final CAI1ResponseItemTypesRevoke c) {
        return FromWireResponses.itemTypesRevoke(c);
      }
      if (message instanceof final CAI1ResponseLocationTypesAssign c) {
        return FromWireResponses.locationTypesAssign(c);
      }
      if (message instanceof final CAI1ResponseLocationTypesRevoke c) {
        return FromWireResponses.locationTypesRevoke(c);
      }
      if (message instanceof final CAI1ResponseLocationMetadataPut m) {
        return FromWireResponses.locationMetadataPut(m);
      }
      if (message instanceof final CAI1ResponseLocationMetadataRemove m) {
        return FromWireResponses.locationMetadataRemove(m);
      }
      if (message instanceof final CAI1ResponseLocationAttachmentAdd m) {
        return FromWireResponses.locationAttachmentAdd(m);
      }
      if (message instanceof final CAI1ResponseLocationAttachmentRemove m) {
        return FromWireResponses.locationAttachmentRemove(m);
      }

    } catch (final ProtocolUncheckedException e) {
      throw e.getCause();
    }

    throw CAI1ValidationCommon.errorProtocol(message);
  }
}
