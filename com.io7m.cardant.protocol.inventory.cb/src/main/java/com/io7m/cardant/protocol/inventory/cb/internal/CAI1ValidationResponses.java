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

import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType.CAFileWithoutData;
import com.io7m.cardant.model.CAIds;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CAPage;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.CAIResponseBlame;
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
import com.io7m.cardant.protocol.inventory.CAIResponseItemUpdate;
import com.io7m.cardant.protocol.inventory.CAIResponseItemsRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationGet;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationList;
import com.io7m.cardant.protocol.inventory.CAIResponseLocationPut;
import com.io7m.cardant.protocol.inventory.CAIResponseLogin;
import com.io7m.cardant.protocol.inventory.CAIResponseRolesAssign;
import com.io7m.cardant.protocol.inventory.CAIResponseRolesGet;
import com.io7m.cardant.protocol.inventory.CAIResponseRolesRevoke;
import com.io7m.cardant.protocol.inventory.CAIResponseTagList;
import com.io7m.cardant.protocol.inventory.CAIResponseTagsDelete;
import com.io7m.cardant.protocol.inventory.CAIResponseTagsPut;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemSummary;
import com.io7m.cardant.protocol.inventory.cb.CAI1Location;
import com.io7m.cardant.protocol.inventory.cb.CAI1Page;
import com.io7m.cardant.protocol.inventory.cb.CAI1Response;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseBlame;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseBlame.BlameClient;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseBlame.BlameServer;
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
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseItemUpdate;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseItemsRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLocationGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLocationList;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLocationPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseLogin;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseRolesAssign;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseRolesGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseRolesRevoke;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTagList;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTagsDelete;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTagsPut;
import com.io7m.cardant.protocol.inventory.cb.ProtocolCAIv1Type;
import com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon.ProtocolUncheckedException;
import com.io7m.cedarbridge.runtime.api.CBIntegerUnsigned32;
import com.io7m.cedarbridge.runtime.api.CBIntegerUnsigned64;
import com.io7m.cedarbridge.runtime.api.CBMap;
import com.io7m.cedarbridge.runtime.api.CBOptionType;
import com.io7m.cedarbridge.runtime.api.CBSerializableType;
import com.io7m.cedarbridge.runtime.api.CBString;
import com.io7m.cedarbridge.runtime.api.CBUUID;
import com.io7m.cedarbridge.runtime.convenience.CBLists;
import com.io7m.cedarbridge.runtime.convenience.CBMaps;
import com.io7m.cedarbridge.runtime.convenience.CBSets;
import com.io7m.medrina.api.MRoleName;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon.convertFromWireFile;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon.convertFromWireItem;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon.convertFromWireLocation;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon.convertFromWireLocations;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon.convertFromWireTags;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon.convertToWireFile;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon.convertToWireItem;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon.convertToWireItemLocations;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon.convertToWireLocation;
import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon.errorProtocol;
import static java.lang.Integer.toUnsignedLong;

public final class CAI1ValidationResponses
{
  private CAI1ValidationResponses()
  {

  }

  public static ProtocolCAIv1Type convertToWireResponse(
    final CAIResponseType cmd)
  {
    if (cmd instanceof final CAIResponseFilePut c) {
      return convertToWireResponseCAIResponseFilePut(c);
    }
    if (cmd instanceof final CAIResponseFileRemove c) {
      return convertToWireResponseCAIResponseFileRemove(c);
    }
    if (cmd instanceof final CAIResponseItemAttachmentAdd c) {
      return convertToWireResponseCAIResponseItemAttachmentAdd(c);
    }
    if (cmd instanceof final CAIResponseItemAttachmentRemove c) {
      return convertToWireResponseCAIResponseItemAttachmentRemove(c);
    }
    if (cmd instanceof final CAIResponseItemCreate c) {
      return convertToWireResponseCAIResponseItemCreate(c);
    }
    if (cmd instanceof final CAIResponseItemGet c) {
      return convertToWireResponseCAIResponseItemGet(c);
    }
    if (cmd instanceof final CAIResponseItemLocationsList c) {
      return convertToWireResponseCAIResponseItemLocationsList(c);
    }
    if (cmd instanceof final CAIResponseItemMetadataPut c) {
      return convertToWireResponseCAIResponseItemMetadataPut(c);
    }
    if (cmd instanceof final CAIResponseItemMetadataRemove c) {
      return convertToWireResponseCAIResponseItemMetadataRemove(c);
    }
    if (cmd instanceof final CAIResponseItemReposit c) {
      return convertToWireResponseCAIResponseItemReposit(c);
    }
    if (cmd instanceof final CAIResponseItemsRemove c) {
      return convertToWireResponseCAIResponseItemsRemove(c);
    }
    if (cmd instanceof final CAIResponseItemUpdate c) {
      return convertToWireResponseCAIResponseItemUpdate(c);
    }
    if (cmd instanceof final CAIResponseLocationGet c) {
      return convertToWireResponseCAIResponseLocationGet(c);
    }
    if (cmd instanceof final CAIResponseLocationList c) {
      return convertToWireResponseCAIResponseLocationList(c);
    }
    if (cmd instanceof final CAIResponseLocationPut c) {
      return convertToWireResponseCAIResponseLocationPut(c);
    }
    if (cmd instanceof final CAIResponseLogin c) {
      return convertToWireResponseCAIResponseLogin(c);
    }
    if (cmd instanceof final CAIResponseTagList c) {
      return convertToWireResponseCAIResponseTagList(c);
    }
    if (cmd instanceof final CAIResponseTagsDelete c) {
      return convertToWireResponseCAIResponseTagsDelete(c);
    }
    if (cmd instanceof final CAIResponseTagsPut c) {
      return convertToWireResponseCAIResponseTagsPut(c);
    }
    if (cmd instanceof final CAIResponseError c) {
      return convertToWireResponseCAIResponseError(c);
    }
    if (cmd instanceof final CAIResponseItemSearch c) {
      return convertToWireResponseCAIResponseItemSearch(c);
    }
    if (cmd instanceof final CAIResponseRolesAssign c) {
      return convertToWireResponseCAIResponseRolesAssign(c);
    }
    if (cmd instanceof final CAIResponseRolesRevoke c) {
      return convertToWireResponseCAIResponseRolesRevoke(c);
    }
    if (cmd instanceof final CAIResponseRolesGet c) {
      return convertToWireResponseCAIResponseRolesGet(c);
    }
    if (cmd instanceof final CAIResponseFileSearch c) {
      return convertToWireResponseCAIResponseFileSearch(c);
    }
    if (cmd instanceof final CAIResponseFileGet c) {
      return convertToWireResponseCAIResponseFileGet(c);
    }

    throw new ProtocolUncheckedException(errorProtocol(cmd));
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseRolesGet(
    final CAIResponseRolesGet c)
  {
    return new CAI1ResponseRolesGet(
      new CBUUID(c.requestId()),
      CBLists.ofCollection(c.roles(), x -> new CBString(x.value().value()))
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseRolesRevoke(
    final CAIResponseRolesRevoke c)
  {
    return new CAI1ResponseRolesRevoke(
      new CBUUID(c.requestId())
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseRolesAssign(
    final CAIResponseRolesAssign c)
  {
    return new CAI1ResponseRolesAssign(
      new CBUUID(c.requestId())
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseItemSearch(
    final CAIResponseItemSearch c)
  {
    return new CAI1ResponseItemSearch(
      new CBUUID(c.requestId()),
      convertToWirePage(c.data(), CAI1ValidationResponses::convertToWireItemSummary)
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseFileSearch(
    final CAIResponseFileSearch c)
  {
    return new CAI1ResponseFileSearch(
      new CBUUID(c.requestId()),
      convertToWirePage(c.data(), CAI1ValidationCommon::convertToWireFile)
    );
  }

  private static CAI1ItemSummary convertToWireItemSummary(
    final CAItemSummary i)
  {
    return new CAI1ItemSummary(
      new CBUUID(i.id().id()),
      new CBString(i.name())
    );
  }

  private static <A, B extends CBSerializableType> CAI1Page<B> convertToWirePage(
    final CAPage<A> data,
    final Function<A, B> f)
  {
    return new CAI1Page<>(
      CBLists.ofCollection(data.items(), f),
      new CBIntegerUnsigned32(toUnsignedLong(data.pageIndex())),
      new CBIntegerUnsigned32(toUnsignedLong(data.pageCount())),
      new CBIntegerUnsigned64(data.pageFirstOffset())
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseError(
    final CAIResponseError c)
  {
    return new CAI1ResponseError(
      new CBUUID(c.requestId()),
      new CBString(c.errorCode().id()),
      new CBString(c.message()),
      CBMaps.ofMapString(c.attributes()),
      CBOptionType.fromOptional(c.remediatingAction().map(CBString::new)),
      convertToWireBlame(c.blame())
    );
  }

  private static CAI1ResponseBlame convertToWireBlame(
    final CAIResponseBlame blame)
  {
    return switch (blame) {
      case BLAME_SERVER -> new BlameServer();
      case BLAME_CLIENT -> new BlameClient();
    };
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseItemReposit(
    final CAIResponseItemReposit c)
  {
    return new CAI1ResponseItemReposit(
      new CBUUID(c.requestId()),
      convertToWireItem(c.data())
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseItemsRemove(
    final CAIResponseItemsRemove c)
  {
    return new CAI1ResponseItemsRemove(
      new CBUUID(c.requestId()),
      CBLists.ofCollection(c.data().ids(), i -> new CBUUID(i.id()))
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseItemUpdate(
    final CAIResponseItemUpdate c)
  {
    return new CAI1ResponseItemUpdate(
      new CBUUID(c.requestId()),
      convertToWireItem(c.data())
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseLogin(
    final CAIResponseLogin c)
  {
    return new CAI1ResponseLogin(
      new CBUUID(c.requestId()),
      new CBUUID(c.userId())
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseTagsPut(
    final CAIResponseTagsPut c)
  {
    return new CAI1ResponseTagsPut(
      new CBUUID(c.requestId()),
      CBLists.ofCollection(
        c.data().tags(),
        CAI1ValidationCommon::convertToWireTag
      )
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseTagsDelete(
    final CAIResponseTagsDelete c)
  {
    return new CAI1ResponseTagsDelete(
      new CBUUID(c.requestId()),
      CBLists.ofCollection(
        c.data().tags(),
        CAI1ValidationCommon::convertToWireTag
      )
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseTagList(
    final CAIResponseTagList c)
  {
    return new CAI1ResponseTagList(
      new CBUUID(c.requestId()),
      CBLists.ofCollection(
        c.data().tags(),
        CAI1ValidationCommon::convertToWireTag
      )
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseLocationPut(
    final CAIResponseLocationPut c)
  {
    return new CAI1ResponseLocationPut(
      new CBUUID(c.requestId()),
      convertToWireLocation(c.data())
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseLocationGet(
    final CAIResponseLocationGet c)
  {
    return new CAI1ResponseLocationGet(
      new CBUUID(c.requestId()),
      convertToWireLocation(c.data())
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseLocationList(
    final CAIResponseLocationList c)
  {
    return new CAI1ResponseLocationList(
      new CBUUID(c.requestId()),
      new CBMap<>(
        c.data()
          .locations()
          .values()
          .stream()
          .map(CAI1ValidationCommon::convertToWireLocation)
          .collect(Collectors.toMap(
            CAI1Location::fieldLocationId,
            Function.identity())
          )
      )
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseItemMetadataRemove(
    final CAIResponseItemMetadataRemove c)
  {
    return new CAI1ResponseItemMetadataRemove(
      new CBUUID(c.requestId()),
      convertToWireItem(c.data())
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseItemMetadataPut(
    final CAIResponseItemMetadataPut c)
  {
    return new CAI1ResponseItemMetadataPut(
      new CBUUID(c.requestId()),
      convertToWireItem(c.data())
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseItemLocationsList(
    final CAIResponseItemLocationsList c)
  {
    return new CAI1ResponseItemLocationsList(
      new CBUUID(c.requestId()),
      convertToWireItemLocations(c.data())
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseItemGet(
    final CAIResponseItemGet c)
  {
    return new CAI1ResponseItemGet(
      new CBUUID(c.requestId()),
      convertToWireItem(c.data())
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseFileGet(
    final CAIResponseFileGet c)
  {
    return new CAI1ResponseFileGet(
      new CBUUID(c.requestId()),
      convertToWireFile(c.data())
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseItemCreate(
    final CAIResponseItemCreate c)
  {
    return new CAI1ResponseItemCreate(
      new CBUUID(c.requestId()),
      convertToWireItem(c.data())
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseItemAttachmentRemove(
    final CAIResponseItemAttachmentRemove c)
  {
    return new CAI1ResponseItemAttachmentRemove(
      new CBUUID(c.requestId()),
      convertToWireItem(c.data())
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseItemAttachmentAdd(
    final CAIResponseItemAttachmentAdd c)
  {
    return new CAI1ResponseItemAttachmentAdd(
      new CBUUID(c.requestId()),
      convertToWireItem(c.data())
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseFileRemove(
    final CAIResponseFileRemove c)
  {
    return new CAI1ResponseFileRemove(
      new CBUUID(c.requestId()),
      new CBUUID(c.data().id())
    );
  }

  private static ProtocolCAIv1Type convertToWireResponseCAIResponseFilePut(
    final CAIResponseFilePut c)
  {
    return new CAI1ResponseFilePut(
      new CBUUID(c.requestId()),
      convertToWireFile(c.data())
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseItemReposit(
    final CAI1ResponseItemReposit m)
  {
    return new CAIResponseItemReposit(
      m.fieldRequestId().value(),
      convertFromWireItem(m.fieldItem())
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseItemUpdate(
    final CAI1ResponseItemUpdate m)
  {
    return new CAIResponseItemUpdate(
      m.fieldRequestId().value(),
      convertFromWireItem(m.fieldItem())
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseItemsRemove(
    final CAI1ResponseItemsRemove m)
  {
    return new CAIResponseItemsRemove(
      m.fieldRequestId().value(),
      new CAIds(CBSets.toSet(m.fieldItems(), x -> new CAItemID(x.value())))
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseLocationGet(
    final CAI1ResponseLocationGet m)
  {
    return new CAIResponseLocationGet(
      m.fieldRequestId().value(),
      convertFromWireLocation(m.fieldLocation())
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseLocationPut(
    final CAI1ResponseLocationPut m)
  {
    return new CAIResponseLocationPut(
      m.fieldRequestId().value(),
      convertFromWireLocation(m.fieldLocation())
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseLocationList(
    final CAI1ResponseLocationList m)
  {
    return new CAIResponseLocationList(
      m.fieldRequestId().value(),
      convertFromWireLocations(m.fieldLocations())
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseTagsPut(
    final CAI1ResponseTagsPut m)
  {
    return new CAIResponseTagsPut(
      m.fieldRequestId().value(),
      convertFromWireTags(m.fieldTags())
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseTagsDelete(
    final CAI1ResponseTagsDelete m)
  {
    return new CAIResponseTagsDelete(
      m.fieldRequestId().value(),
      convertFromWireTags(m.fieldTags())
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseTagList(
    final CAI1ResponseTagList m)
  {
    return new CAIResponseTagList(
      m.fieldRequestId().value(),
      convertFromWireTags(m.fieldTags())
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseLogin(
    final CAI1ResponseLogin m)
  {
    return new CAIResponseLogin(
      m.fieldRequestId().value(),
      m.fieldUserId().value()
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseItemMetadataRemove(
    final CAI1ResponseItemMetadataRemove m)
  {
    return new CAIResponseItemMetadataRemove(
      m.fieldRequestId().value(),
      convertFromWireItem(m.fieldItem())
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseItemMetadataPut(
    final CAI1ResponseItemMetadataPut m)
  {
    return new CAIResponseItemMetadataPut(
      m.fieldRequestId().value(),
      convertFromWireItem(m.fieldItem())
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseItemLocationsList(
    final CAI1ResponseItemLocationsList m)
  {
    return new CAIResponseItemLocationsList(
      m.fieldRequestId().value(),
      CAI1ValidationCommon.convertFromWireItemLocations(m.fieldItemLocations())
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseItemGet(
    final CAI1ResponseItemGet m)
  {
    return new CAIResponseItemGet(
      m.fieldRequestId().value(),
      convertFromWireItem(m.fieldItem())
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseFileGet(
    final CAI1ResponseFileGet m)
  {
    return new CAIResponseFileGet(
      m.fieldRequestId().value(),
      convertFromWireFile(m.fieldFile()).withoutData()
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseItemCreate(
    final CAI1ResponseItemCreate m)
  {
    return new CAIResponseItemCreate(
      m.fieldRequestId().value(),
      convertFromWireItem(m.fieldItem())
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseItemAttachmentRemove(
    final CAI1ResponseItemAttachmentRemove m)
  {
    return new CAIResponseItemAttachmentRemove(
      m.fieldRequestId().value(),
      convertFromWireItem(m.fieldItem())
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseItemAttachmentAdd(
    final CAI1ResponseItemAttachmentAdd m)
  {
    return new CAIResponseItemAttachmentAdd(
      m.fieldRequestId().value(),
      convertFromWireItem(m.fieldItem())
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseFileRemove(
    final CAI1ResponseFileRemove m)
  {
    return new CAIResponseFileRemove(
      m.fieldRequestId().value(),
      new CAFileID(m.fieldId().value())
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseFilePut(
    final CAI1ResponseFilePut m)
  {
    return new CAIResponseFilePut(
      m.fieldRequestId().value(),
      convertFromWireFile(m.fieldFile())
    );
  }

  public static CAI1Response convertToWireWrapTransactionResponse(
    final ProtocolCAIv1Type msg)
  {
    if (msg instanceof final CAI1ResponseError c) {
      return new CAI1Response.C1ResponseError(c);
    }
    if (msg instanceof final CAI1ResponseFilePut c) {
      return new CAI1Response.C1ResponseFilePut(c);
    }
    if (msg instanceof final CAI1ResponseFileRemove c) {
      return new CAI1Response.C1ResponseFileRemove(c);
    }
    if (msg instanceof final CAI1ResponseItemAttachmentAdd c) {
      return new CAI1Response.C1ResponseItemAttachmentAdd(c);
    }
    if (msg instanceof final CAI1ResponseItemAttachmentRemove c) {
      return new CAI1Response.C1ResponseItemAttachmentRemove(c);
    }
    if (msg instanceof final CAI1ResponseItemCreate c) {
      return new CAI1Response.C1ResponseItemCreate(c);
    }
    if (msg instanceof final CAI1ResponseItemGet c) {
      return new CAI1Response.C1ResponseItemGet(c);
    }
    if (msg instanceof final CAI1ResponseItemLocationsList c) {
      return new CAI1Response.C1ResponseItemLocationsList(c);
    }
    if (msg instanceof final CAI1ResponseItemMetadataPut c) {
      return new CAI1Response.C1ResponseItemMetadataPut(c);
    }
    if (msg instanceof final CAI1ResponseItemMetadataRemove c) {
      return new CAI1Response.C1ResponseItemMetadataRemove(c);
    }
    if (msg instanceof final CAI1ResponseItemReposit c) {
      return new CAI1Response.C1ResponseItemReposit(c);
    }
    if (msg instanceof final CAI1ResponseItemUpdate c) {
      return new CAI1Response.C1ResponseItemUpdate(c);
    }
    if (msg instanceof final CAI1ResponseItemsRemove c) {
      return new CAI1Response.C1ResponseItemsRemove(c);
    }
    if (msg instanceof final CAI1ResponseItemSearch c) {
      return new CAI1Response.C1ResponseItemSearch(c);
    }
    if (msg instanceof final CAI1ResponseLocationGet c) {
      return new CAI1Response.C1ResponseLocationGet(c);
    }
    if (msg instanceof final CAI1ResponseLocationList c) {
      return new CAI1Response.C1ResponseLocationList(c);
    }
    if (msg instanceof final CAI1ResponseLocationPut c) {
      return new CAI1Response.C1ResponseLocationPut(c);
    }
    if (msg instanceof final CAI1ResponseLogin c) {
      return new CAI1Response.C1ResponseLogin(c);
    }
    if (msg instanceof final CAI1ResponseTagList c) {
      return new CAI1Response.C1ResponseTagList(c);
    }
    if (msg instanceof final CAI1ResponseTagsDelete c) {
      return new CAI1Response.C1ResponseTagsDelete(c);
    }
    if (msg instanceof final CAI1ResponseTagsPut c) {
      return new CAI1Response.C1ResponseTagsPut(c);
    }
    if (msg instanceof final CAI1ResponseRolesAssign c) {
      return new CAI1Response.C1ResponseRolesAssign(c);
    }
    if (msg instanceof final CAI1ResponseRolesRevoke c) {
      return new CAI1Response.C1ResponseRolesRevoke(c);
    }
    if (msg instanceof final CAI1ResponseFileSearch c) {
      return new CAI1Response.C1ResponseFileSearch(c);
    }

    throw new IllegalStateException();
  }

  public static CAIMessageType convertFromWireCAI1ResponseItemSearch(
    final CAI1ResponseItemSearch c)
  {
    return new CAIResponseItemSearch(
      c.fieldRequestId().value(),
      CAI1ValidationCommon.convertFromWirePage(
        c.fieldResults(),
        CAI1ValidationCommon::convertFromWireItemSummary)
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseFileSearch(
    final CAI1ResponseFileSearch c)
  {
    return new CAIResponseFileSearch(
      c.fieldRequestId().value(),
      CAI1ValidationCommon.convertFromWirePage(
        c.fieldResults(),
        f -> {
          return (CAFileWithoutData) convertFromWireFile(f).withoutData();
        })
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseError(
    final CAI1ResponseError m)
  {
    return new CAIResponseError(
      m.fieldRequestId().value(),
      m.fieldMessage().value(),
      new CAErrorCode(m.fieldErrorCode().value()),
      CBMaps.toMapString(m.fieldAttributes()),
      m.fieldRemediatingAction()
        .asOptional()
        .map(CBString::value),
      Optional.empty(),
      convertFromWireBlame(m.fieldBlame())
    );
  }

  private static CAIResponseBlame convertFromWireBlame(
    final CAI1ResponseBlame blame)
  {
    if (blame instanceof BlameClient) {
      return CAIResponseBlame.BLAME_CLIENT;
    }
    if (blame instanceof BlameServer) {
      return CAIResponseBlame.BLAME_SERVER;
    }
    throw new ProtocolUncheckedException(errorProtocol(blame));
  }

  public static CAIMessageType convertFromWireCAI1ResponseRolesRevoke(
    final CAI1ResponseRolesRevoke m)
  {
    return new CAIResponseRolesRevoke(
      m.fieldRequestId().value()
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseRolesAssign(
    final CAI1ResponseRolesAssign m)
  {
    return new CAIResponseRolesAssign(
      m.fieldRequestId().value()
    );
  }

  public static CAIMessageType convertFromWireCAI1ResponseRolesGet(
    final CAI1ResponseRolesGet m)
  {
    return new CAIResponseRolesGet(
      m.fieldRequestId().value(),
      CBSets.toSet(m.fieldRoles(), x -> MRoleName.of(x.value()))
    );
  }
}
