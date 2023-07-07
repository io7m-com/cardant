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
import com.io7m.cardant.protocol.inventory.CAIResponseTypeScalarGet;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeScalarPut;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeScalarRemove;
import com.io7m.cardant.protocol.inventory.CAIResponseTypeScalarSearch;
import com.io7m.cardant.protocol.inventory.cb.CAI1Location;
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
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeScalarGet;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeScalarPut;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeScalarRemove;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseTypeScalarSearch;
import com.io7m.cardant.protocol.inventory.cb.ProtocolCAIv1Type;
import com.io7m.cedarbridge.runtime.api.CBMap;
import com.io7m.cedarbridge.runtime.api.CBOptionType;
import com.io7m.cedarbridge.runtime.api.CBString;
import com.io7m.cedarbridge.runtime.api.CBUUID;
import com.io7m.cedarbridge.runtime.convenience.CBLists;
import com.io7m.cedarbridge.runtime.convenience.CBMaps;

import java.util.function.Function;
import java.util.stream.Collectors;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon.errorProtocol;

// CHECKSTYLE:OFF

public final class ToWireResponses
{
  private ToWireResponses()
  {

  }

  public static ProtocolCAIv1Type response(
    final CAIResponseType cmd)
  {
    if (cmd instanceof final CAIResponseFilePut c) {
      return filePut(c);
    }
    if (cmd instanceof final CAIResponseFileRemove c) {
      return fileRemove(c);
    }
    if (cmd instanceof final CAIResponseItemAttachmentAdd c) {
      return itemAttachmentAdd(c);
    }
    if (cmd instanceof final CAIResponseItemAttachmentRemove c) {
      return itemAttachmentRemove(c);
    }
    if (cmd instanceof final CAIResponseItemCreate c) {
      return itemCreate(c);
    }
    if (cmd instanceof final CAIResponseItemGet c) {
      return itemGet(c);
    }
    if (cmd instanceof final CAIResponseItemLocationsList c) {
      return itemLocationsList(c);
    }
    if (cmd instanceof final CAIResponseItemMetadataPut c) {
      return itemMetadataPut(c);
    }
    if (cmd instanceof final CAIResponseItemMetadataRemove c) {
      return itemMetadataRemove(c);
    }
    if (cmd instanceof final CAIResponseItemReposit c) {
      return itemReposit(c);
    }
    if (cmd instanceof final CAIResponseItemsRemove c) {
      return itemsRemove(c);
    }
    if (cmd instanceof final CAIResponseItemSetName c) {
      return itemSetName(c);
    }
    if (cmd instanceof final CAIResponseLocationGet c) {
      return locationGet(c);
    }
    if (cmd instanceof final CAIResponseLocationList c) {
      return locationList(c);
    }
    if (cmd instanceof final CAIResponseLocationPut c) {
      return locationPut(c);
    }
    if (cmd instanceof final CAIResponseLogin c) {
      return login(c);
    }
    if (cmd instanceof final CAIResponseTagList c) {
      return tagList(c);
    }
    if (cmd instanceof final CAIResponseTagsDelete c) {
      return tagsDelete(c);
    }
    if (cmd instanceof final CAIResponseTagsPut c) {
      return tagsPut(c);
    }
    if (cmd instanceof final CAIResponseError c) {
      return error(c);
    }
    if (cmd instanceof final CAIResponseItemSearch c) {
      return itemSearch(c);
    }
    if (cmd instanceof final CAIResponseRolesAssign c) {
      return rolesAssign(c);
    }
    if (cmd instanceof final CAIResponseRolesRevoke c) {
      return rolesRevoke(c);
    }
    if (cmd instanceof final CAIResponseRolesGet c) {
      return rolesGet(c);
    }
    if (cmd instanceof final CAIResponseFileSearch c) {
      return fileSearch(c);
    }
    if (cmd instanceof final CAIResponseFileGet c) {
      return fileGet(c);
    }
    if (cmd instanceof final CAIResponseTypeScalarPut c) {
      return typeScalarPut(c);
    }
    if (cmd instanceof final CAIResponseTypeScalarGet c) {
      return typeScalarGet(c);
    }
    if (cmd instanceof final CAIResponseTypeScalarSearch c) {
      return typeScalarSearch(c);
    }
    if (cmd instanceof final CAIResponseTypeScalarRemove c) {
      return typeScalarRemove(c);
    }

    throw new ProtocolUncheckedException(errorProtocol(cmd));
  }

  private static ProtocolCAIv1Type typeScalarRemove(
    final CAIResponseTypeScalarRemove c)
  {
    return new CAI1ResponseTypeScalarRemove(
      new CBUUID(c.requestId())
    );
  }

  private static ProtocolCAIv1Type typeScalarSearch(
    final CAIResponseTypeScalarSearch c)
  {
    return new CAI1ResponseTypeScalarSearch(
      new CBUUID(c.requestId()),
      ToWireModel.page(c.data(), ToWireModel::typeScalar)
    );
  }

  private static ProtocolCAIv1Type typeScalarGet(
    final CAIResponseTypeScalarGet c)
  {
    return new CAI1ResponseTypeScalarGet(
      new CBUUID(c.requestId()),
      ToWireModel.typeScalar(c.type())
    );
  }

  private static ProtocolCAIv1Type typeScalarPut(
    final CAIResponseTypeScalarPut c)
  {
    return new CAI1ResponseTypeScalarPut(
      new CBUUID(c.requestId()),
      CBLists.ofCollection(c.types(), ToWireModel::typeScalar)
    );
  }

  private static ProtocolCAIv1Type rolesGet(
    final CAIResponseRolesGet c)
  {
    return new CAI1ResponseRolesGet(
      new CBUUID(c.requestId()),
      CBLists.ofCollection(c.roles(), x -> new CBString(x.value().value()))
    );
  }

  private static ProtocolCAIv1Type rolesRevoke(
    final CAIResponseRolesRevoke c)
  {
    return new CAI1ResponseRolesRevoke(
      new CBUUID(c.requestId())
    );
  }

  private static ProtocolCAIv1Type rolesAssign(
    final CAIResponseRolesAssign c)
  {
    return new CAI1ResponseRolesAssign(
      new CBUUID(c.requestId())
    );
  }

  private static ProtocolCAIv1Type itemSearch(
    final CAIResponseItemSearch c)
  {
    return new CAI1ResponseItemSearch(
      new CBUUID(c.requestId()),
      ToWireModel.page(c.data(), ToWireModel::itemSummary)
    );
  }

  private static ProtocolCAIv1Type fileSearch(
    final CAIResponseFileSearch c)
  {
    return new CAI1ResponseFileSearch(
      new CBUUID(c.requestId()),
      ToWireModel.page(c.data(), ToWireModel::file)
    );
  }

  private static ProtocolCAIv1Type error(
    final CAIResponseError c)
  {
    return new CAI1ResponseError(
      new CBUUID(c.requestId()),
      new CBString(c.errorCode().id()),
      new CBString(c.message()),
      CBMaps.ofMapString(c.attributes()),
      CBOptionType.fromOptional(c.remediatingAction().map(CBString::new)),
      ToWireModel.blame(c.blame())
    );
  }

  private static ProtocolCAIv1Type itemReposit(
    final CAIResponseItemReposit c)
  {
    return new CAI1ResponseItemReposit(
      new CBUUID(c.requestId()), ToWireModel.item(c.data())
    );
  }

  private static ProtocolCAIv1Type itemsRemove(
    final CAIResponseItemsRemove c)
  {
    return new CAI1ResponseItemsRemove(
      new CBUUID(c.requestId()),
      CBLists.ofCollection(c.data().ids(), i -> new CBUUID(i.id()))
    );
  }

  private static ProtocolCAIv1Type itemSetName(
    final CAIResponseItemSetName c)
  {
    return new CAI1ResponseItemSetName(
      new CBUUID(c.requestId()), ToWireModel.item(c.data())
    );
  }

  private static ProtocolCAIv1Type login(
    final CAIResponseLogin c)
  {
    return new CAI1ResponseLogin(
      new CBUUID(c.requestId()),
      new CBUUID(c.userId())
    );
  }

  private static ProtocolCAIv1Type tagsPut(
    final CAIResponseTagsPut c)
  {
    return new CAI1ResponseTagsPut(
      new CBUUID(c.requestId()),
      CBLists.ofCollection(c.data().tags(), ToWireModel::tag)
    );
  }

  private static ProtocolCAIv1Type tagsDelete(
    final CAIResponseTagsDelete c)
  {
    return new CAI1ResponseTagsDelete(
      new CBUUID(c.requestId()),
      CBLists.ofCollection(c.data().tags(), ToWireModel::tag)
    );
  }

  private static ProtocolCAIv1Type tagList(
    final CAIResponseTagList c)
  {
    return new CAI1ResponseTagList(
      new CBUUID(c.requestId()),
      CBLists.ofCollection(c.data().tags(), ToWireModel::tag)
    );
  }

  private static ProtocolCAIv1Type locationPut(
    final CAIResponseLocationPut c)
  {
    return new CAI1ResponseLocationPut(
      new CBUUID(c.requestId()),
      ToWireModel.location(c.data())
    );
  }

  private static ProtocolCAIv1Type locationGet(
    final CAIResponseLocationGet c)
  {
    return new CAI1ResponseLocationGet(
      new CBUUID(c.requestId()),
      ToWireModel.location(c.data())
    );
  }

  private static ProtocolCAIv1Type locationList(
    final CAIResponseLocationList c)
  {
    return new CAI1ResponseLocationList(
      new CBUUID(c.requestId()),
      new CBMap<>(
        c.data()
          .locations()
          .values()
          .stream()
          .map(ToWireModel::location)
          .collect(Collectors.toMap(
            CAI1Location::fieldLocationId,
            Function.identity())
          )
      )
    );
  }

  private static ProtocolCAIv1Type itemMetadataRemove(
    final CAIResponseItemMetadataRemove c)
  {
    return new CAI1ResponseItemMetadataRemove(
      new CBUUID(c.requestId()),
      ToWireModel.item(c.data())
    );
  }

  private static ProtocolCAIv1Type itemMetadataPut(
    final CAIResponseItemMetadataPut c)
  {
    return new CAI1ResponseItemMetadataPut(
      new CBUUID(c.requestId()),
      ToWireModel.item(c.data())
    );
  }

  private static ProtocolCAIv1Type itemLocationsList(
    final CAIResponseItemLocationsList c)
  {
    return new CAI1ResponseItemLocationsList(
      new CBUUID(c.requestId()),
      ToWireModel.itemLocations(c.data())
    );
  }

  private static ProtocolCAIv1Type itemGet(
    final CAIResponseItemGet c)
  {
    return new CAI1ResponseItemGet(
      new CBUUID(c.requestId()),
      ToWireModel.item(c.data())
    );
  }

  private static ProtocolCAIv1Type fileGet(
    final CAIResponseFileGet c)
  {
    return new CAI1ResponseFileGet(
      new CBUUID(c.requestId()),
      ToWireModel.file(c.data())
    );
  }

  private static ProtocolCAIv1Type itemCreate(
    final CAIResponseItemCreate c)
  {
    return new CAI1ResponseItemCreate(
      new CBUUID(c.requestId()),
      ToWireModel.item(c.data())
    );
  }

  private static ProtocolCAIv1Type itemAttachmentRemove(
    final CAIResponseItemAttachmentRemove c)
  {
    return new CAI1ResponseItemAttachmentRemove(
      new CBUUID(c.requestId()), ToWireModel.item(c.data())
    );
  }

  private static ProtocolCAIv1Type itemAttachmentAdd(
    final CAIResponseItemAttachmentAdd c)
  {
    return new CAI1ResponseItemAttachmentAdd(
      new CBUUID(c.requestId()), ToWireModel.item(c.data())
    );
  }

  private static ProtocolCAIv1Type fileRemove(
    final CAIResponseFileRemove c)
  {
    return new CAI1ResponseFileRemove(
      new CBUUID(c.requestId()),
      new CBUUID(c.data().id())
    );
  }

  private static ProtocolCAIv1Type filePut(
    final CAIResponseFilePut c)
  {
    return new CAI1ResponseFilePut(
      new CBUUID(c.requestId()), ToWireModel.file(c.data())
    );
  }
}
