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

import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAFileColumn;
import com.io7m.cardant.model.CAFileColumnOrdering;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileSearchParameters;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAIdType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemAttachmentKey;
import com.io7m.cardant.model.CAItemColumn;
import com.io7m.cardant.model.CAItemColumnOrdering;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemLocation;
import com.io7m.cardant.model.CAItemLocations;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CAItemRepositMove;
import com.io7m.cardant.model.CAItemRepositRemove;
import com.io7m.cardant.model.CAItemRepositType;
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CAListLocationBehaviourType;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocations;
import com.io7m.cardant.model.CAPage;
import com.io7m.cardant.model.CASizeRange;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;
import com.io7m.cardant.model.CATags;
import com.io7m.cardant.model.CATypeDeclaration;
import com.io7m.cardant.model.CATypeDeclarationSummary;
import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.model.CATypeScalar;
import com.io7m.cardant.protocol.inventory.CAIResponseBlame;
import com.io7m.cardant.protocol.inventory.cb.CAI1File;
import com.io7m.cardant.protocol.inventory.cb.CAI1FileColumn;
import com.io7m.cardant.protocol.inventory.cb.CAI1FileColumnOrdering;
import com.io7m.cardant.protocol.inventory.cb.CAI1FileSearchParameters;
import com.io7m.cardant.protocol.inventory.cb.CAI1Id;
import com.io7m.cardant.protocol.inventory.cb.CAI1Item;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemAttachment;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemAttachmentKey;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemColumn;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemColumnOrdering;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemLocation;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemLocations;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemMetadata;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemReposit;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemSearchParameters;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemSummary;
import com.io7m.cardant.protocol.inventory.cb.CAI1ListLocationBehaviour;
import com.io7m.cardant.protocol.inventory.cb.CAI1Location;
import com.io7m.cardant.protocol.inventory.cb.CAI1Page;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseBlame;
import com.io7m.cardant.protocol.inventory.cb.CAI1SizeRange;
import com.io7m.cardant.protocol.inventory.cb.CAI1Tag;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeDeclaration;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeDeclarationSummary;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeField;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeScalar;
import com.io7m.cedarbridge.runtime.api.CBByteArray;
import com.io7m.cedarbridge.runtime.api.CBList;
import com.io7m.cedarbridge.runtime.api.CBMap;
import com.io7m.cedarbridge.runtime.api.CBSerializableType;
import com.io7m.cedarbridge.runtime.api.CBString;
import com.io7m.cedarbridge.runtime.api.CBUUID;
import com.io7m.cedarbridge.runtime.convenience.CBMaps;
import com.io7m.cedarbridge.runtime.convenience.CBSets;
import com.io7m.lanark.core.RDottedName;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon.errorProtocol;

// CHECKSTYLE:OFF

public final class FromWireModel
{
  private FromWireModel()
  {

  }

  public static CAItem item(
    final CAI1Item item)
  {
    final var attachments =
      new HashMap<CAItemAttachmentKey, CAItemAttachment>();

    for (final var entry : item.fieldAttachments().values().entrySet()) {
      final var key =
        entry.getKey();
      final var val =
        entry.getValue();
      final var rKey =
        itemAttachmentKey(key);
      final var rVal =
        itemAttachment(val);
      attachments.put(rKey, rVal);
    }

    final var metadata =
      new HashMap<RDottedName, CAItemMetadata>();

    for (final var entry : item.fieldMetadata().values().entrySet()) {
      final var key =
        entry.getKey();
      final var val =
        entry.getValue();
      final var rKey =
        new RDottedName(key.value());
      final var rVal =
        itemMetadata(val);
      metadata.put(rKey, rVal);
    }

    return new CAItem(
      new CAItemID(item.fieldId().value()),
      item.fieldName().value(),
      item.fieldCountTotal().value(),
      item.fieldCountHere().value(),
      new TreeMap<>(metadata),
      new TreeMap<>(attachments),
      new TreeSet<>(
        item.fieldTags()
          .values()
          .stream()
          .map(FromWireModel::tag)
          .toList()
      ),
      new TreeSet<>(
        item.fieldTypes()
          .values()
          .stream()
          .map(CBString::value)
          .map(RDottedName::new)
          .toList()
      )
    );
  }

  private static CAItemAttachmentKey itemAttachmentKey(
    final CAI1ItemAttachmentKey key)
  {
    return new CAItemAttachmentKey(
      new CAFileID(key.fieldId().value()),
      key.fieldRelation().value()
    );
  }

  private static CAItemAttachment itemAttachment(
    final CAI1ItemAttachment a)
  {
    return new CAItemAttachment(
      file(a.fieldFile()),
      a.fieldRelation().value()
    );
  }

  public static CAItemRepositType reposit(
    final CAI1ItemReposit i)
  {
    if (i instanceof final CAI1ItemReposit.CAI1ItemRepositMove m) {
      return repositMove(m);
    } else if (i instanceof final CAI1ItemReposit.CAI1ItemRepositRemove r) {
      return repositRemove(r);
    } else if (i instanceof final CAI1ItemReposit.CAI1ItemRepositAdd a) {
      return repositAdd(a);
    }

    throw new ProtocolUncheckedException(
      CAI1ValidationCommon.errorProtocol(i));
  }

  private static CAItemRepositType repositAdd(
    final CAI1ItemReposit.CAI1ItemRepositAdd m)
  {
    return new CAItemRepositAdd(
      new CAItemID(m.fieldItemId().value()),
      new CALocationID(m.fieldLocationId().value()),
      m.fieldCount().value()
    );
  }

  private static CAItemRepositType repositRemove(
    final CAI1ItemReposit.CAI1ItemRepositRemove m)
  {
    return new CAItemRepositRemove(
      new CAItemID(m.fieldItemId().value()),
      new CALocationID(m.fieldLocationId().value()),
      m.fieldCount().value()
    );
  }

  private static CAItemRepositType repositMove(
    final CAI1ItemReposit.CAI1ItemRepositMove m)
  {
    return new CAItemRepositMove(
      new CAItemID(m.fieldItemId().value()),
      new CALocationID(m.fieldLocationFrom().value()),
      new CALocationID(m.fieldLocationTo().value()),
      m.fieldCount().value()
    );
  }

  public static CATags tags(
    final CBList<CAI1Tag> c)
  {
    return new CATags(
      new TreeSet<>(CBSets.toSet(c, FromWireModel::tag))
    );
  }

  private static CATag tag(
    final CAI1Tag t)
  {
    return new CATag(new CATagID(t.fieldId().value()), t.fieldValue().value());
  }

  public static CAIdType id(final CAI1Id i)
  {
    if (i instanceof final CAI1Id.CAI1LocationID ii) {
      return new CALocationID(ii.fieldId().value());
    }
    if (i instanceof final CAI1Id.CAI1ItemID ii) {
      return new CAItemID(ii.fieldId().value());
    }
    if (i instanceof final CAI1Id.CAI1TagID ii) {
      return new CATagID(ii.fieldId().value());
    }
    if (i instanceof final CAI1Id.CAI1FileID ii) {
      return new CAFileID(ii.fieldId().value());
    }

    throw new ProtocolUncheckedException(
      CAI1ValidationCommon.errorProtocol(i));
  }

  public static CAItemMetadata itemMetadata(
    final CAI1ItemMetadata i)
  {
    return CAItemMetadata.of(
      i.fieldKey().value(),
      i.fieldValue().value()
    );
  }

  public static CAListLocationBehaviourType itemLocationBehaviour(
    final CAI1ListLocationBehaviour b)
  {
    if (b instanceof final CAI1ListLocationBehaviour.CAI1ListLocationExact x) {
      return new CAListLocationBehaviourType.CAListLocationExact(
        new CALocationID(x.fieldLocationId().value())
      );
    }
    if (b instanceof final CAI1ListLocationBehaviour.CAI1ListLocationsAll a) {
      return new CAListLocationBehaviourType.CAListLocationsAll();
    }
    if (b instanceof final CAI1ListLocationBehaviour.CAI1ListLocationWithDescendants x) {
      return new CAListLocationBehaviourType.CAListLocationWithDescendants(
        new CALocationID(x.fieldLocationId().value())
      );
    }

    throw new ProtocolUncheckedException(
      CAI1ValidationCommon.errorProtocol(b));
  }

  public static CAFileType file(
    final CAI1File f)
  {
    if (f instanceof final CAI1File.CAI1FileWithData with) {
      return new CAFileType.CAFileWithData(
        new CAFileID(with.fieldId().value()),
        with.fieldDescription().value(),
        with.fieldMediaType().value(),
        with.fieldSize().value(),
        with.fieldHashAlgorithm().value(),
        with.fieldHashValue().value(),
        byteArray(with.fieldData())
      );
    }

    if (f instanceof final CAI1File.CAI1FileWithoutData with) {
      return new CAFileType.CAFileWithoutData(
        new CAFileID(with.fieldId().value()),
        with.fieldDescription().value(),
        with.fieldMediaType().value(),
        with.fieldSize().value(),
        with.fieldHashAlgorithm().value(),
        with.fieldHashValue().value()
      );
    }

    throw new ProtocolUncheckedException(
      CAI1ValidationCommon.errorProtocol(f));
  }

  private static CAByteArray byteArray(
    final CBByteArray b)
  {
    final var buf = b.value();
    final var d = new byte[buf.capacity()];
    buf.get(d);
    return new CAByteArray(d);
  }

  public static CALocation location(
    final CAI1Location c)
  {
    return new CALocation(
      new CALocationID(c.fieldLocationId().value()),
      c.fieldParent()
        .asOptional()
        .map(x -> new CALocationID(x.value())),
      c.fieldName().value(),
      c.fieldDescription().value()
    );
  }

  public static CALocations locations(
    final CBMap<CBUUID, CAI1Location> locations)
  {
    final var results = new TreeMap<CALocationID, CALocation>();
    for (final var entry : locations.values().entrySet()) {
      final var location =
        location(entry.getValue());
      results.put(location.id(), location);
    }
    return new CALocations(results);
  }

  public static CAItemLocations itemLocations(
    final CAI1ItemLocations locations)
  {
    final var input =
      locations.fieldLocations();
    final var output =
      new TreeMap<CALocationID, SortedMap<CAItemID, CAItemLocation>>();

    for (final var locationId : input.values().keySet()) {
      final var locationMap =
        input.values().get(locationId);
      final var outLocations =
        new TreeMap<CAItemID, CAItemLocation>();

      for (final var itemId : locationMap.values().keySet()) {
        final var itemLocation =
          locationMap.values().get(itemId);
        final var outLocation =
          itemLocation(itemLocation);
        outLocations.put(outLocation.item(), outLocation);
      }

      output.put(
        new CALocationID(locationId.value()),
        outLocations
      );
    }

    return new CAItemLocations(output);
  }

  private static CAItemLocation itemLocation(
    final CAI1ItemLocation itemLocation)
  {
    return new CAItemLocation(
      new CAItemID(itemLocation.fieldItemId().value()),
      new CALocationID(itemLocation.fieldLocationId().value()),
      itemLocation.fieldCount().value()
    );
  }

  public static CAItemSearchParameters itemSearchParameters(
    final CAI1ItemSearchParameters p)
  {
    return new CAItemSearchParameters(
      itemLocationBehaviour(p.fieldLocation()),
      p.fieldSearch().asOptional().map(CBString::value),
      itemColumnOrdering(p.fieldOrder()),
      (int) p.fieldLimit().value()
    );
  }

  private static CAItemColumnOrdering itemColumnOrdering(
    final CAI1ItemColumnOrdering c)
  {
    return new CAItemColumnOrdering(
      itemColumn(c.fieldColumn()),
      c.fieldAscending().asBoolean()
    );
  }

  private static CAItemColumn itemColumn(
    final CAI1ItemColumn c)
  {
    if (c instanceof CAI1ItemColumn.CAI1ById) {
      return CAItemColumn.BY_ID;
    }
    if (c instanceof CAI1ItemColumn.CAI1ByName) {
      return CAItemColumn.BY_NAME;
    }
    throw new ProtocolUncheckedException(
      CAI1ValidationCommon.errorProtocol(c));
  }

  public static CAFileSearchParameters fileSearchParameters(
    final CAI1FileSearchParameters p)
  {
    return new CAFileSearchParameters(
      p.fieldSearch().asOptional().map(CBString::value),
      p.fieldMediaType()
        .asOptional().map(CBString::value),
      p.fieldSizeRange()
        .asOptional().map(FromWireModel::sizeRange),
      fileColumnOrdering(p.fieldOrder()),
      (int) p.fieldLimit().value()
    );
  }

  private static CASizeRange sizeRange(
    final CAI1SizeRange r)
  {
    return new CASizeRange(
      r.fieldSizeMinimum().value(),
      r.fieldSizeMaximum().value()
    );
  }

  private static CAFileColumnOrdering fileColumnOrdering(
    final CAI1FileColumnOrdering c)
  {
    return new CAFileColumnOrdering(
      fileColumn(c.fieldColumn()),
      c.fieldAscending().asBoolean()
    );
  }

  private static CAFileColumn fileColumn(
    final CAI1FileColumn c)
  {
    if (c instanceof CAI1FileColumn.CAI1ById) {
      return CAFileColumn.BY_ID;
    }
    if (c instanceof CAI1FileColumn.CAI1ByDescription) {
      return CAFileColumn.BY_DESCRIPTION;
    }
    throw new ProtocolUncheckedException(
      CAI1ValidationCommon.errorProtocol(c));
  }

  public static CAItemSummary itemSummary(
    final CAI1ItemSummary x)
  {
    return new CAItemSummary(
      new CAItemID(x.fieldId().value()),
      x.fieldName().value()
    );
  }

  public static <A extends CBSerializableType, B> CAPage<B> page(
    final CAI1Page<A> page,
    final Function<A, B> f)
  {
    return new CAPage<>(
      page.fieldItems().values().stream().map(f).toList(),
      (int) page.fieldPageIndex().value(),
      (int) page.fieldPageCount().value(),
      page.fieldPageFirstOffset().value()
    );
  }

  public static CATypeScalar typeScalar(
    final CAI1TypeScalar x)
  {
    return new CATypeScalar(
      new RDottedName(x.fieldName().value()),
      x.fieldDescription().value(),
      x.fieldPattern().value()
    );
  }

  public static CATypeField typeField(
    final CAI1TypeField x)
  {
    return new CATypeField(
      new RDottedName(x.fieldName().value()),
      x.fieldDescription().value(),
      typeScalar(x.fieldType()),
      x.fieldRequired().asBoolean()
    );
  }

  public static CATypeDeclaration typeDeclaration(
    final CAI1TypeDeclaration x)
  {
    return new CATypeDeclaration(
      new RDottedName(x.fieldName().value()),
      x.fieldDescription().value(),
      CBMaps.toMap(
        x.fieldFields(),
        s -> new RDottedName(s.value()),
        FromWireModel::typeField
      )
    );
  }

  public static CATypeDeclarationSummary typeDeclarationSummary(
    final CAI1TypeDeclarationSummary x)
  {
    return new CATypeDeclarationSummary(
      new RDottedName(x.fieldName().value()),
      x.fieldDescription().value()
    );
  }

  public static CAIResponseBlame blame(
    final CAI1ResponseBlame blame)
  {
    if (blame instanceof CAI1ResponseBlame.BlameClient) {
      return CAIResponseBlame.BLAME_CLIENT;
    }
    if (blame instanceof CAI1ResponseBlame.BlameServer) {
      return CAIResponseBlame.BLAME_SERVER;
    }
    throw new ProtocolUncheckedException(errorProtocol(blame));
  }
}
