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
import com.io7m.cardant.protocol.inventory.CAITransaction;
import com.io7m.cardant.protocol.inventory.CAITransactionResponse;
import com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands;
import com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon;
import com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationEvents;
import com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses;
import com.io7m.cedarbridge.runtime.api.CBBooleanType;
import com.io7m.cedarbridge.runtime.api.CBList;

import java.util.ArrayList;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandFileGet;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandFilePut;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandFileRemove;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandFileSearchBegin;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandFileSearchNext;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandFileSearchPrevious;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandItemAttachmentAdd;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandItemAttachmentRemove;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandItemCreate;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandItemGet;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandItemLocationsList;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandItemMetadataPut;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandItemMetadataRemove;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandItemReposit;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandItemSearchBegin;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandItemSearchNext;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandItemSearchPrevious;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandItemUpdate;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandItemsRemove;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandLocationGet;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandLocationList;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandLocationPut;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandLogin;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandRolesAssign;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandRolesGet;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandRolesRevoke;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandTagList;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandTagsDelete;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertFromWireCAI1CommandTagsPut;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommands.convertToWireCommand;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseError;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseFileGet;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseFilePut;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseFileRemove;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseFileSearch;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseItemAttachmentAdd;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseItemAttachmentRemove;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseItemCreate;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseItemGet;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseItemLocationsList;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseItemMetadataPut;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseItemMetadataRemove;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseItemReposit;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseItemSearch;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseItemUpdate;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseItemsRemove;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseLocationGet;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseLocationList;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseLocationPut;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseLogin;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseRolesAssign;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseRolesGet;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseRolesRevoke;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseTagList;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseTagsDelete;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertFromWireCAI1ResponseTagsPut;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationResponses.convertToWireResponse;

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

  private static ProtocolCAIv1Type convertToWireTransaction(
    final CAITransaction transaction)
  {
    return new CAI1Transaction(
      new CBList<>(
        transaction.commands()
          .stream()
          .map(CAI1ValidationCommands::convertToWireCommand)
          .map(CAI1ValidationCommands::convertToWireWrapTransactionCommand)
          .toList())
    );
  }

  private static ProtocolCAIv1Type convertToWireTransactionResponse(
    final CAITransactionResponse transactionResponse)
  {
    return new CAI1TransactionResponse(
      CBBooleanType.fromBoolean(transactionResponse.failed()),
      new CBList<>(
        transactionResponse.responses()
          .stream()
          .map(CAI1ValidationResponses::convertToWireResponse)
          .map(CAI1ValidationResponses::convertToWireWrapTransactionResponse)
          .toList())
    );
  }

  private static CAITransaction convertFromWireTransaction(
    final CAI1Transaction m)
  {
    final var sourceCommands =
      m.fieldCommands().values();
    final var out =
      new ArrayList<CAIMessageType>(sourceCommands.size());

    for (final var c : sourceCommands) {
      if (c instanceof final CAI1Command.C1CommandFileRemove k) {
        out.add(convertFromWireCAI1CommandFileRemove(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandFilePut k) {
        out.add(convertFromWireCAI1CommandFilePut(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandItemAttachmentAdd k) {
        out.add(convertFromWireCAI1CommandItemAttachmentAdd(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandItemAttachmentRemove k) {
        out.add(convertFromWireCAI1CommandItemAttachmentRemove(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandItemCreate k) {
        out.add(convertFromWireCAI1CommandItemCreate(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandItemGet k) {
        out.add(convertFromWireCAI1CommandItemGet(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandFileGet k) {
        out.add(convertFromWireCAI1CommandFileGet(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandItemLocationsList k) {
        out.add(convertFromWireCAI1CommandItemLocationsList(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandItemMetadataPut k) {
        out.add(convertFromWireCAI1CommandItemMetadataPut(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandItemMetadataRemove k) {
        out.add(convertFromWireCAI1CommandItemMetadataRemove(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandItemsRemove k) {
        out.add(convertFromWireCAI1CommandItemsRemove(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandItemUpdate k) {
        out.add(convertFromWireCAI1CommandItemUpdate(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandLocationGet k) {
        out.add(convertFromWireCAI1CommandLocationGet(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandLocationList k) {
        out.add(convertFromWireCAI1CommandLocationList(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandLocationPut k) {
        out.add(convertFromWireCAI1CommandLocationPut(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandLogin k) {
        out.add(convertFromWireCAI1CommandLogin(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandTagList k) {
        out.add(convertFromWireCAI1CommandTagList(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandTagsDelete k) {
        out.add(convertFromWireCAI1CommandTagsDelete(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandTagsPut k) {
        out.add(convertFromWireCAI1CommandTagsPut(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandItemReposit k) {
        out.add(convertFromWireCAI1CommandItemReposit(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandItemSearchBegin k) {
        out.add(convertFromWireCAI1CommandItemSearchBegin(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandItemSearchNext k) {
        out.add(convertFromWireCAI1CommandItemSearchNext(k.fieldCommand()));
        continue;
      }
      if (c instanceof final CAI1Command.C1CommandItemSearchPrevious k) {
        out.add(convertFromWireCAI1CommandItemSearchPrevious(k.fieldCommand()));
        continue;
      }

      throw new IllegalStateException("Unrecognized message: " + c.getClass());
    }

    return new CAITransaction(
      out.stream()
        .map(c -> (CAICommandType<?>) c)
        .toList()
    );
  }

  private static CAITransactionResponse convertFromWireTransactionResponse(
    final CAI1TransactionResponse m)
  {
    final var sourceResponses =
      m.fieldResponses().values();
    final var out =
      new ArrayList<CAIMessageType>(sourceResponses.size());

    for (final var c : sourceResponses) {
      if (c instanceof final CAI1Response.C1ResponseFileRemove k) {
        out.add(convertFromWireCAI1ResponseFileRemove(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseFilePut k) {
        out.add(convertFromWireCAI1ResponseFilePut(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseItemAttachmentAdd k) {
        out.add(convertFromWireCAI1ResponseItemAttachmentAdd(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseItemAttachmentRemove k) {
        out.add(convertFromWireCAI1ResponseItemAttachmentRemove(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseItemCreate k) {
        out.add(convertFromWireCAI1ResponseItemCreate(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseItemGet k) {
        out.add(convertFromWireCAI1ResponseItemGet(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseFileGet k) {
        out.add(convertFromWireCAI1ResponseFileGet(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseItemLocationsList k) {
        out.add(convertFromWireCAI1ResponseItemLocationsList(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseItemMetadataPut k) {
        out.add(convertFromWireCAI1ResponseItemMetadataPut(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseItemMetadataRemove k) {
        out.add(convertFromWireCAI1ResponseItemMetadataRemove(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseItemsRemove k) {
        out.add(convertFromWireCAI1ResponseItemsRemove(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseItemUpdate k) {
        out.add(convertFromWireCAI1ResponseItemUpdate(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseLocationGet k) {
        out.add(convertFromWireCAI1ResponseLocationGet(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseLocationList k) {
        out.add(convertFromWireCAI1ResponseLocationList(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseLocationPut k) {
        out.add(convertFromWireCAI1ResponseLocationPut(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseLogin k) {
        out.add(convertFromWireCAI1ResponseLogin(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseTagList k) {
        out.add(convertFromWireCAI1ResponseTagList(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseTagsDelete k) {
        out.add(convertFromWireCAI1ResponseTagsDelete(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseTagsPut k) {
        out.add(convertFromWireCAI1ResponseTagsPut(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseItemReposit k) {
        out.add(convertFromWireCAI1ResponseItemReposit(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseError k) {
        out.add(convertFromWireCAI1ResponseError(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseItemSearch k) {
        out.add(convertFromWireCAI1ResponseItemSearch(k.fieldResponse()));
        continue;
      }
      if (c instanceof final CAI1Response.C1ResponseFileSearch k) {
        out.add(convertFromWireCAI1ResponseFileSearch(k.fieldResponse()));
        continue;
      }

      throw new IllegalStateException("Unrecognized message: " + c.getClass());
    }

    return new CAITransactionResponse(
      m.fieldFailed().asBoolean(),
      out.stream()
        .map(c -> (CAIResponseType) c)
        .toList()
    );
  }

  @Override
  public ProtocolCAIv1Type convertToWire(
    final CAIMessageType message)
    throws CAProtocolException
  {
    try {
      if (message instanceof final CAICommandType<?> cmd) {
        return convertToWireCommand(cmd);
      } else if (message instanceof final CAIResponseType response) {
        return convertToWireResponse(response);
      } else if (message instanceof final CAIEventType event) {
        return CAI1ValidationEvents.convertToWireEvent(event);
      } else if (message instanceof final CAITransaction transaction) {
        return convertToWireTransaction(transaction);
      } else if (message instanceof final CAITransactionResponse transactionResponse) {
        return convertToWireTransactionResponse(transactionResponse);
      }
    } catch (final CAI1ValidationCommon.ProtocolUncheckedException e) {
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
        return convertFromWireCAI1CommandFilePut(m);
      }
      if (message instanceof final CAI1CommandFileRemove m) {
        return convertFromWireCAI1CommandFileRemove(m);
      }
      if (message instanceof final CAI1CommandItemAttachmentAdd m) {
        return convertFromWireCAI1CommandItemAttachmentAdd(m);
      }
      if (message instanceof final CAI1CommandItemAttachmentRemove m) {
        return convertFromWireCAI1CommandItemAttachmentRemove(m);
      }
      if (message instanceof final CAI1CommandItemCreate m) {
        return convertFromWireCAI1CommandItemCreate(m);
      }
      if (message instanceof final CAI1CommandItemGet m) {
        return convertFromWireCAI1CommandItemGet(m);
      }
      if (message instanceof final CAI1CommandItemLocationsList m) {
        return convertFromWireCAI1CommandItemLocationsList(m);
      }
      if (message instanceof final CAI1CommandItemMetadataPut m) {
        return convertFromWireCAI1CommandItemMetadataPut(m);
      }
      if (message instanceof final CAI1CommandItemMetadataRemove m) {
        return convertFromWireCAI1CommandItemMetadataRemove(m);
      }
      if (message instanceof final CAI1CommandItemReposit m) {
        return convertFromWireCAI1CommandItemReposit(m);
      }
      if (message instanceof final CAI1CommandItemsRemove m) {
        return convertFromWireCAI1CommandItemsRemove(m);
      }
      if (message instanceof final CAI1CommandItemUpdate m) {
        return convertFromWireCAI1CommandItemUpdate(m);
      }
      if (message instanceof final CAI1CommandLocationGet m) {
        return convertFromWireCAI1CommandLocationGet(m);
      }
      if (message instanceof final CAI1CommandLocationList m) {
        return convertFromWireCAI1CommandLocationList(m);
      }
      if (message instanceof final CAI1CommandLocationPut m) {
        return convertFromWireCAI1CommandLocationPut(m);
      }
      if (message instanceof final CAI1CommandLogin m) {
        return convertFromWireCAI1CommandLogin(m);
      }
      if (message instanceof final CAI1CommandTagList m) {
        return convertFromWireCAI1CommandTagList(m);
      }
      if (message instanceof final CAI1CommandTagsDelete m) {
        return convertFromWireCAI1CommandTagsDelete(m);
      }
      if (message instanceof final CAI1CommandTagsPut m) {
        return convertFromWireCAI1CommandTagsPut(m);
      }
      if (message instanceof final CAI1CommandItemSearchBegin m) {
        return convertFromWireCAI1CommandItemSearchBegin(m);
      }
      if (message instanceof final CAI1CommandItemSearchNext m) {
        return convertFromWireCAI1CommandItemSearchNext(m);
      }
      if (message instanceof final CAI1CommandItemSearchPrevious m) {
        return convertFromWireCAI1CommandItemSearchPrevious(m);
      }
      if (message instanceof final CAI1CommandFileSearchBegin m) {
        return convertFromWireCAI1CommandFileSearchBegin(m);
      }
      if (message instanceof final CAI1CommandFileSearchNext m) {
        return convertFromWireCAI1CommandFileSearchNext(m);
      }
      if (message instanceof final CAI1CommandFileSearchPrevious m) {
        return convertFromWireCAI1CommandFileSearchPrevious(m);
      }
      if (message instanceof final CAI1CommandRolesAssign m) {
        return convertFromWireCAI1CommandRolesAssign(m);
      }
      if (message instanceof final CAI1CommandRolesRevoke m) {
        return convertFromWireCAI1CommandRolesRevoke(m);
      }
      if (message instanceof final CAI1CommandRolesGet m) {
        return convertFromWireCAI1CommandRolesGet(m);
      }
      if (message instanceof final CAI1CommandFileGet m) {
        return convertFromWireCAI1CommandFileGet(m);
      }

      /*
       * Response.
       */

      if (message instanceof final CAI1ResponseFilePut m) {
        return convertFromWireCAI1ResponseFilePut(m);
      }
      if (message instanceof final CAI1ResponseFileRemove m) {
        return convertFromWireCAI1ResponseFileRemove(m);
      }
      if (message instanceof final CAI1ResponseItemAttachmentAdd m) {
        return convertFromWireCAI1ResponseItemAttachmentAdd(m);
      }
      if (message instanceof final CAI1ResponseItemAttachmentRemove m) {
        return convertFromWireCAI1ResponseItemAttachmentRemove(m);
      }
      if (message instanceof final CAI1ResponseItemCreate m) {
        return convertFromWireCAI1ResponseItemCreate(m);
      }
      if (message instanceof final CAI1ResponseItemGet m) {
        return convertFromWireCAI1ResponseItemGet(m);
      }
      if (message instanceof final CAI1ResponseItemSearch m) {
        return convertFromWireCAI1ResponseItemSearch(m);
      }
      if (message instanceof final CAI1ResponseFileSearch m) {
        return convertFromWireCAI1ResponseFileSearch(m);
      }
      if (message instanceof final CAI1ResponseItemLocationsList m) {
        return convertFromWireCAI1ResponseItemLocationsList(m);
      }
      if (message instanceof final CAI1ResponseItemMetadataPut m) {
        return convertFromWireCAI1ResponseItemMetadataPut(m);
      }
      if (message instanceof final CAI1ResponseItemMetadataRemove m) {
        return convertFromWireCAI1ResponseItemMetadataRemove(m);
      }
      if (message instanceof final CAI1ResponseItemReposit m) {
        return convertFromWireCAI1ResponseItemReposit(m);
      }
      if (message instanceof final CAI1ResponseItemsRemove m) {
        return convertFromWireCAI1ResponseItemsRemove(m);
      }
      if (message instanceof final CAI1ResponseItemUpdate m) {
        return convertFromWireCAI1ResponseItemUpdate(m);
      }
      if (message instanceof final CAI1ResponseLocationGet m) {
        return convertFromWireCAI1ResponseLocationGet(m);
      }
      if (message instanceof final CAI1ResponseLocationList m) {
        return convertFromWireCAI1ResponseLocationList(m);
      }
      if (message instanceof final CAI1ResponseLocationPut m) {
        return convertFromWireCAI1ResponseLocationPut(m);
      }
      if (message instanceof final CAI1ResponseLogin m) {
        return convertFromWireCAI1ResponseLogin(m);
      }
      if (message instanceof final CAI1ResponseTagList m) {
        return convertFromWireCAI1ResponseTagList(m);
      }
      if (message instanceof final CAI1ResponseTagsDelete m) {
        return convertFromWireCAI1ResponseTagsDelete(m);
      }
      if (message instanceof final CAI1ResponseTagsPut m) {
        return convertFromWireCAI1ResponseTagsPut(m);
      }
      if (message instanceof final CAI1ResponseError m) {
        return convertFromWireCAI1ResponseError(m);
      }
      if (message instanceof final CAI1ResponseRolesRevoke m) {
        return convertFromWireCAI1ResponseRolesRevoke(m);
      }
      if (message instanceof final CAI1ResponseRolesAssign m) {
        return convertFromWireCAI1ResponseRolesAssign(m);
      }
      if (message instanceof final CAI1ResponseRolesGet m) {
        return convertFromWireCAI1ResponseRolesGet(m);
      }
      if (message instanceof final CAI1ResponseFileGet m) {
        return convertFromWireCAI1ResponseFileGet(m);
      }

      /*
       * Transaction.
       */

      if (message instanceof final CAI1Transaction m) {
        return convertFromWireTransaction(m);
      }
      if (message instanceof final CAI1TransactionResponse m) {
        return convertFromWireTransactionResponse(m);
      }

    } catch (final CAI1ValidationCommon.ProtocolUncheckedException e) {
      throw e.getCause();
    }

    throw CAI1ValidationCommon.errorProtocol(message);
  }
}
