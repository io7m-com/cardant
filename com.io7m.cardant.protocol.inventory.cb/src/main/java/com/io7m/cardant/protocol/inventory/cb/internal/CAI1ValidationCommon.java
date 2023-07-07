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

import com.io7m.cardant.error_codes.CAStandardErrorCodes;
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
import com.io7m.cardant.model.CATypeScalar;
import com.io7m.cardant.protocol.api.CAProtocolException;
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
import com.io7m.cardant.protocol.inventory.cb.CAI1SizeRange;
import com.io7m.cardant.protocol.inventory.cb.CAI1Tag;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeScalar;
import com.io7m.cedarbridge.runtime.api.CBBooleanType;
import com.io7m.cedarbridge.runtime.api.CBByteArray;
import com.io7m.cedarbridge.runtime.api.CBIntegerUnsigned32;
import com.io7m.cedarbridge.runtime.api.CBIntegerUnsigned64;
import com.io7m.cedarbridge.runtime.api.CBList;
import com.io7m.cedarbridge.runtime.api.CBMap;
import com.io7m.cedarbridge.runtime.api.CBOptionType;
import com.io7m.cedarbridge.runtime.api.CBSerializableType;
import com.io7m.cedarbridge.runtime.api.CBString;
import com.io7m.cedarbridge.runtime.api.CBUUID;
import com.io7m.cedarbridge.runtime.convenience.CBSets;
import com.io7m.lanark.core.RDottedName;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;

import static com.io7m.cedarbridge.runtime.api.CBCore.string;
import static com.io7m.cedarbridge.runtime.api.CBCore.unsigned64;
import static java.lang.Integer.toUnsignedLong;

public final class CAI1ValidationCommon
{
  private CAI1ValidationCommon()
  {

  }

  public static CAI1Item convertToWireItem(
    final CAItem item)
  {
    final var attachments =
      new HashMap<CAI1ItemAttachmentKey, CAI1ItemAttachment>();

    for (final var entry : item.attachments().entrySet()) {
      final var key =
        entry.getKey();
      final var val =
        entry.getValue();
      final var rKey =
        convertToWireItemAttachmentKey(key);
      final var rVal =
        convertToWireItemAttachment(val);
      attachments.put(rKey, rVal);
    }

    final var metadata =
      new HashMap<CBString, CAI1ItemMetadata>();

    for (final var entry : item.metadata().entrySet()) {
      final var key =
        entry.getKey();
      final var val =
        entry.getValue();
      final var rKey =
        new CBString(key.value());
      final var rVal =
        convertToWireItemMetadata(val);
      metadata.put(rKey, rVal);
    }

    return new CAI1Item(
      new CBUUID(item.id().id()),
      new CBString(item.name()),
      new CBIntegerUnsigned64(item.countTotal()),
      new CBIntegerUnsigned64(item.countHere()),
      new CBMap<>(metadata),
      new CBMap<>(attachments),
      new CBList<>(
        item.tags()
          .stream()
          .map(CAI1ValidationCommon::convertToWireTag)
          .toList()
      ),
      new CBList<>(
        item.types()
          .stream()
          .map(RDottedName::value)
          .map(CBString::new)
          .toList()
      )
    );
  }

  private static CAI1ItemAttachmentKey convertToWireItemAttachmentKey(
    final CAItemAttachmentKey key)
  {
    return new CAI1ItemAttachmentKey(
      new CBUUID(key.fileID().id()),
      new CBString(key.relation())
    );
  }

  private static CAI1ItemAttachment convertToWireItemAttachment(
    final CAItemAttachment m)
  {
    return new CAI1ItemAttachment(
      convertToWireFile(m.file()),
      new CBString(m.relation())
    );
  }

  public static CAItem convertFromWireItem(
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
        convertFromWireItemAttachmentKey(key);
      final var rVal =
        convertFromWireItemAttachment(val);
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
        convertFromWireItemMetadata(val);
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
          .map(CAI1ValidationCommon::convertFromWireTag)
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

  private static CAItemAttachmentKey convertFromWireItemAttachmentKey(
    final CAI1ItemAttachmentKey key)
  {
    return new CAItemAttachmentKey(
      new CAFileID(key.fieldId().value()),
      key.fieldRelation().value()
    );
  }

  private static CAItemAttachment convertFromWireItemAttachment(
    final CAI1ItemAttachment a)
  {
    return new CAItemAttachment(
      convertFromWireFile(a.fieldFile()),
      a.fieldRelation().value()
    );
  }

  public static CAI1Id convertToWireID(
    final CAIdType id)
  {
    if (id instanceof final CAFileID xid) {
      return new CAI1Id.CAI1FileID(new CBUUID(xid.id()));
    } else if (id instanceof final CALocationID xid) {
      return new CAI1Id.CAI1LocationID(new CBUUID(xid.id()));
    } else if (id instanceof final CATagID xid) {
      return new CAI1Id.CAI1TagID(new CBUUID(xid.id()));
    } else if (id instanceof final CAItemID xid) {
      return new CAI1Id.CAI1ItemID(new CBUUID(xid.id()));
    }

    throw new ProtocolUncheckedException(
      new CAProtocolException(
        "Unrecognized ID type: %s".formatted(id),
        CAStandardErrorCodes.errorProtocol(),
        Map.of(),
        Optional.empty()
      )
    );
  }

  public static CAI1ItemReposit convertToWireItemReposit(
    final CAItemRepositType reposit)
  {
    if (reposit instanceof final CAItemRepositAdd a) {
      return convertToWireItemRepositAdd(a);
    } else if (reposit instanceof final CAItemRepositRemove r) {
      return convertToWireItemRepositRemove(r);
    } else if (reposit instanceof final CAItemRepositMove m) {
      return convertToWireItemRepositMove(m);
    }

    throw new ProtocolUncheckedException(errorProtocol(reposit));
  }

  private static CAI1ItemReposit convertToWireItemRepositMove(
    final CAItemRepositMove m)
  {
    return new CAI1ItemReposit.CAI1ItemRepositMove(
      new CBUUID(m.item().id()),
      new CBUUID(m.fromLocation().id()),
      new CBUUID(m.toLocation().id()),
      new CBIntegerUnsigned64(m.count())
    );
  }

  private static CAI1ItemReposit convertToWireItemRepositRemove(
    final CAItemRepositRemove r)
  {
    return new CAI1ItemReposit.CAI1ItemRepositRemove(
      new CBUUID(r.item().id()),
      new CBUUID(r.location().id()),
      new CBIntegerUnsigned64(r.count())
    );
  }

  private static CAI1ItemReposit convertToWireItemRepositAdd(
    final CAItemRepositAdd a)
  {
    return new CAI1ItemReposit.CAI1ItemRepositAdd(
      new CBUUID(a.item().id()),
      new CBUUID(a.location().id()),
      new CBIntegerUnsigned64(a.count())
    );
  }

  public static CAI1Tag convertToWireTag(
    final CATag t)
  {
    return new CAI1Tag(
      new CBUUID(t.id().id()),
      new CBString(t.name())
    );
  }

  public static CAI1Location convertToWireLocation(
    final CALocation location)
  {
    return new CAI1Location(
      new CBUUID(location.id().id()),
      CBOptionType.fromOptional(location.parent().map(x -> new CBUUID(x.id()))),
      new CBString(location.name()),
      new CBString(location.description())
    );
  }

  public static CAI1ItemMetadata convertToWireItemMetadata(
    final CAItemMetadata c)
  {
    return new CAI1ItemMetadata(
      new CBString(c.name().value()),
      new CBString(c.value())
    );
  }

  public static CAI1ListLocationBehaviour convertToWireListLocationBehaviour(
    final CAListLocationBehaviourType b)
  {
    if (b instanceof CAListLocationBehaviourType.CAListLocationsAll) {
      return new CAI1ListLocationBehaviour.CAI1ListLocationsAll();
    } else if (b instanceof final CAListLocationBehaviourType.CAListLocationExact e) {
      return new CAI1ListLocationBehaviour.CAI1ListLocationExact(
        new CBUUID(e.location().id())
      );
    } else if (b instanceof final CAListLocationBehaviourType.CAListLocationWithDescendants e) {
      return new CAI1ListLocationBehaviour.CAI1ListLocationWithDescendants(
        new CBUUID(e.location().id())
      );
    }

    throw new ProtocolUncheckedException(errorProtocol(b));
  }

  public static CAI1File convertToWireFile(
    final CAFileType data)
  {
    if (data instanceof final CAFileType.CAFileWithData withData) {
      return convertToWireFileWithData(withData);
    } else if (data instanceof final CAFileType.CAFileWithoutData withoutData) {
      return convertToWireFileWithoutData(withoutData);
    }

    throw new ProtocolUncheckedException(errorProtocol(data));
  }

  private static CAI1File convertToWireFileWithoutData(
    final CAFileType.CAFileWithoutData withoutData)
  {
    return new CAI1File.CAI1FileWithoutData(
      new CBUUID(withoutData.id().id()),
      new CBString(withoutData.description()),
      new CBString(withoutData.mediaType()),
      new CBIntegerUnsigned64(withoutData.size()),
      new CBString(withoutData.hashAlgorithm()),
      new CBString(withoutData.hashValue())
    );
  }

  private static CAI1File convertToWireFileWithData(
    final CAFileType.CAFileWithData withData)
  {
    return new CAI1File.CAI1FileWithData(
      new CBUUID(withData.id().id()),
      new CBString(withData.description()),
      new CBString(withData.mediaType()),
      new CBIntegerUnsigned64(withData.size()),
      new CBString(withData.hashAlgorithm()),
      new CBString(withData.hashValue()),
      new CBByteArray(ByteBuffer.wrap(withData.data().data()))
    );
  }

  public static CAProtocolException errorProtocol(
    final Object msg)
  {
    return new CAProtocolException(
      "Unrecognized message: %s".formatted(msg),
      CAStandardErrorCodes.errorProtocol(),
      Map.of(),
      Optional.empty()
    );
  }

  public static CAItemRepositType convertFromWireReposit(
    final CAI1ItemReposit i)
  {
    if (i instanceof final CAI1ItemReposit.CAI1ItemRepositMove m) {
      return convertFromWireRepositMove(m);
    } else if (i instanceof final CAI1ItemReposit.CAI1ItemRepositRemove r) {
      return convertFromWireRepositRemove(r);
    } else if (i instanceof final CAI1ItemReposit.CAI1ItemRepositAdd a) {
      return convertFromWireRepositAdd(a);
    }

    throw new ProtocolUncheckedException(errorProtocol(i));
  }

  private static CAItemRepositType convertFromWireRepositAdd(
    final CAI1ItemReposit.CAI1ItemRepositAdd m)
  {
    return new CAItemRepositAdd(
      new CAItemID(m.fieldItemId().value()),
      new CALocationID(m.fieldLocationId().value()),
      m.fieldCount().value()
    );
  }

  private static CAItemRepositType convertFromWireRepositRemove(
    final CAI1ItemReposit.CAI1ItemRepositRemove m)
  {
    return new CAItemRepositRemove(
      new CAItemID(m.fieldItemId().value()),
      new CALocationID(m.fieldLocationId().value()),
      m.fieldCount().value()
    );
  }

  private static CAItemRepositType convertFromWireRepositMove(
    final CAI1ItemReposit.CAI1ItemRepositMove m)
  {
    return new CAItemRepositMove(
      new CAItemID(m.fieldItemId().value()),
      new CALocationID(m.fieldLocationFrom().value()),
      new CALocationID(m.fieldLocationTo().value()),
      m.fieldCount().value()
    );
  }

  public static CATags convertFromWireTags(
    final CBList<CAI1Tag> c)
  {
    return new CATags(
      new TreeSet<>(CBSets.toSet(c, CAI1ValidationCommon::convertFromWireTag))
    );
  }

  private static CATag convertFromWireTag(
    final CAI1Tag t)
  {
    return new CATag(new CATagID(t.fieldId().value()), t.fieldValue().value());
  }

  public static CAIdType convertFromWireId(final CAI1Id i)
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

    throw new ProtocolUncheckedException(errorProtocol(i));
  }

  public static CAItemMetadata convertFromWireItemMetadata(
    final CAI1ItemMetadata i)
  {
    return CAItemMetadata.of(
      i.fieldKey().value(),
      i.fieldValue().value()
    );
  }

  public static CAListLocationBehaviourType convertFromWireItemLocationBehaviour(
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

    throw new ProtocolUncheckedException(errorProtocol(b));
  }

  public static CAFileType convertFromWireFile(
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
        convertFromWireByteArray(with.fieldData())
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

    throw new ProtocolUncheckedException(errorProtocol(f));
  }

  private static CAByteArray convertFromWireByteArray(
    final CBByteArray b)
  {
    final var buf = b.value();
    final var d = new byte[buf.capacity()];
    buf.get(d);
    return new CAByteArray(d);
  }

  public static CALocation convertFromWireLocation(
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

  public static CAI1ItemLocations convertToWireItemLocations(
    final CAItemLocations data)
  {
    final var input =
      data.itemLocations();
    final var output =
      new HashMap<CBUUID, CBMap<CBUUID, CAI1ItemLocation>>();

    for (final var e0 : input.entrySet()) {
      final var locationId = e0.getKey();
      final var locationMap =
        input.get(locationId);
      final var outLocations =
        new HashMap<CBUUID, CAI1ItemLocation>();

      for (final var e1 : locationMap.entrySet()) {
        final var itemId = e1.getKey();
        final var itemLocation =
          locationMap.get(itemId);
        final var outLocation =
          convertToWireItemLocation(itemLocation);
        outLocations.put(outLocation.fieldItemId(), outLocation);
      }

      output.put(new CBUUID(locationId.id()), new CBMap<>(outLocations));
    }

    return new CAI1ItemLocations(new CBMap<>(output));
  }

  private static CAI1ItemLocation convertToWireItemLocation(
    final CAItemLocation itemLocation)
  {
    return new CAI1ItemLocation(
      new CBUUID(itemLocation.item().id()),
      new CBUUID(itemLocation.location().id()),
      new CBIntegerUnsigned64(itemLocation.count())
    );
  }

  public static CALocations convertFromWireLocations(
    final CBMap<CBUUID, CAI1Location> locations)
  {
    final var results = new TreeMap<CALocationID, CALocation>();
    for (final var entry : locations.values().entrySet()) {
      final var location =
        convertFromWireLocation(entry.getValue());
      results.put(location.id(), location);
    }
    return new CALocations(results);
  }

  public static CAItemLocations convertFromWireItemLocations(
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
          convertFromWireItemLocation(itemLocation);
        outLocations.put(outLocation.item(), outLocation);
      }

      output.put(
        new CALocationID(locationId.value()),
        outLocations
      );
    }

    return new CAItemLocations(output);
  }

  private static CAItemLocation convertFromWireItemLocation(
    final CAI1ItemLocation itemLocation)
  {
    return new CAItemLocation(
      new CAItemID(itemLocation.fieldItemId().value()),
      new CALocationID(itemLocation.fieldLocationId().value()),
      itemLocation.fieldCount().value()
    );
  }

  public static CAI1ItemSearchParameters convertToWireItemSearchParameters(
    final CAItemSearchParameters parameters)
  {
    return new CAI1ItemSearchParameters(
      convertToWireListLocationBehaviour(parameters.locationBehaviour()),
      CBOptionType.fromOptional(parameters.search().map(CBString::new)),
      convertToWireItemColumnOrdering(parameters.ordering()),
      new CBIntegerUnsigned32(toUnsignedLong(parameters.limit()))
    );
  }

  public static CAI1FileSearchParameters convertToWireFileSearchParameters(
    final CAFileSearchParameters parameters)
  {
    return new CAI1FileSearchParameters(
      CBOptionType.fromOptional(parameters.description().map(CBString::new)),
      CBOptionType.fromOptional(parameters.mediaType().map(CBString::new)),
      CBOptionType.fromOptional(
        parameters.sizeRange()
          .map(CAI1ValidationCommon::convertToWireSizeRange)
      ),
      convertToWireFileColumnOrdering(parameters.ordering()),
      new CBIntegerUnsigned32(toUnsignedLong(parameters.limit()))
    );
  }

  private static CAI1SizeRange convertToWireSizeRange(
    final CASizeRange range)
  {
    return new CAI1SizeRange(
      unsigned64(range.sizeMinimum()),
      unsigned64(range.sizeMaximum())
    );
  }


  private static CAI1FileColumnOrdering convertToWireFileColumnOrdering(
    final CAFileColumnOrdering ordering)
  {
    return new CAI1FileColumnOrdering(
      convertToWireFileColumn(ordering.column()),
      CBBooleanType.fromBoolean(ordering.ascending())
    );
  }

  private static CAI1FileColumn convertToWireFileColumn(
    final CAFileColumn column)
  {
    return switch (column) {
      case BY_ID -> new CAI1FileColumn.CAI1ById();
      case BY_DESCRIPTION -> new CAI1FileColumn.CAI1ByDescription();
    };
  }


  private static CAI1ItemColumnOrdering convertToWireItemColumnOrdering(
    final CAItemColumnOrdering ordering)
  {
    return new CAI1ItemColumnOrdering(
      convertToWireItemColumn(ordering.column()),
      CBBooleanType.fromBoolean(ordering.ascending())
    );
  }

  private static CAI1ItemColumn convertToWireItemColumn(
    final CAItemColumn column)
  {
    return switch (column) {
      case BY_ID -> new CAI1ItemColumn.CAI1ById();
      case BY_NAME -> new CAI1ItemColumn.CAI1ByName();
    };
  }

  public static CAItemSearchParameters convertFromWireItemSearchParameters(
    final CAI1ItemSearchParameters p)
  {
    return new CAItemSearchParameters(
      convertFromWireItemLocationBehaviour(p.fieldLocation()),
      p.fieldSearch().asOptional().map(CBString::value),
      convertFromWireItemColumnOrdering(p.fieldOrder()),
      (int) p.fieldLimit().value()
    );
  }

  private static CAItemColumnOrdering convertFromWireItemColumnOrdering(
    final CAI1ItemColumnOrdering c)
  {
    return new CAItemColumnOrdering(
      convertFromWireItemColumn(c.fieldColumn()),
      c.fieldAscending().asBoolean()
    );
  }

  private static CAItemColumn convertFromWireItemColumn(
    final CAI1ItemColumn c)
  {
    if (c instanceof CAI1ItemColumn.CAI1ById) {
      return CAItemColumn.BY_ID;
    }
    if (c instanceof CAI1ItemColumn.CAI1ByName) {
      return CAItemColumn.BY_NAME;
    }
    throw new ProtocolUncheckedException(errorProtocol(c));
  }

  public static CAFileSearchParameters convertFromWireFileSearchParameters(
    final CAI1FileSearchParameters p)
  {
    return new CAFileSearchParameters(
      p.fieldSearch().asOptional().map(CBString::value),
      p.fieldMediaType()
        .asOptional().map(CBString::value),
      p.fieldSizeRange()
        .asOptional().map(CAI1ValidationCommon::convertFromWireSizeRange),
      convertFromWireFileColumnOrdering(p.fieldOrder()),
      (int) p.fieldLimit().value()
    );
  }

  private static CASizeRange convertFromWireSizeRange(
    final CAI1SizeRange r)
  {
    return new CASizeRange(
      r.fieldSizeMinimum().value(),
      r.fieldSizeMaximum().value()
    );
  }

  private static CAFileColumnOrdering convertFromWireFileColumnOrdering(
    final CAI1FileColumnOrdering c)
  {
    return new CAFileColumnOrdering(
      convertFromWireFileColumn(c.fieldColumn()),
      c.fieldAscending().asBoolean()
    );
  }

  private static CAFileColumn convertFromWireFileColumn(
    final CAI1FileColumn c)
  {
    if (c instanceof CAI1FileColumn.CAI1ById) {
      return CAFileColumn.BY_ID;
    }
    if (c instanceof CAI1FileColumn.CAI1ByDescription) {
      return CAFileColumn.BY_DESCRIPTION;
    }
    throw new ProtocolUncheckedException(errorProtocol(c));
  }






  public static CAItemSummary convertFromWireItemSummary(
    final CAI1ItemSummary x)
  {
    return new CAItemSummary(
      new CAItemID(x.fieldId().value()),
      x.fieldName().value()
    );
  }

  public static <A extends CBSerializableType, B> CAPage<B> convertFromWirePage(
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

  public static CAI1TypeScalar convertToWireTypeScalar(
    final CATypeScalar x)
  {
    return new CAI1TypeScalar(
      string(x.name().value()),
      string(x.description()),
      string(x.pattern())
    );
  }

  static CATypeScalar convertFromWireTypeScalar(
    final CAI1TypeScalar x)
  {
    return new CATypeScalar(
      new RDottedName(x.fieldName().value()),
      x.fieldDescription().value(),
      x.fieldPattern().value()
    );
  }

  public static final class ProtocolUncheckedException
    extends RuntimeException
  {
    public ProtocolUncheckedException(
      final CAProtocolException cause)
    {
      super(cause);
    }

    @Override
    public CAProtocolException getCause()
    {
      return (CAProtocolException) super.getCause();
    }
  }
}
