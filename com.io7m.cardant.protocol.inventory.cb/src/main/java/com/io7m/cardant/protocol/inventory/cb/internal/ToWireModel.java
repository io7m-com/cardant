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
import com.io7m.cardant.model.CAAttachment;
import com.io7m.cardant.model.CAAttachmentKey;
import com.io7m.cardant.model.CAFileColumn;
import com.io7m.cardant.model.CAFileColumnOrdering;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileSearchParameters;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAIdType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemColumn;
import com.io7m.cardant.model.CAItemColumnOrdering;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemLocation;
import com.io7m.cardant.model.CAItemLocationMatchType;
import com.io7m.cardant.model.CAItemLocations;
import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CAItemRepositMove;
import com.io7m.cardant.model.CAItemRepositRemove;
import com.io7m.cardant.model.CAItemRepositType;
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationSummary;
import com.io7m.cardant.model.CAMetadataElementMatchType;
import com.io7m.cardant.model.CAMetadataNameMatchType;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CAMetadataValueMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.IntegralMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.MonetaryMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.RealMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.TextMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.TimeMatchType;
import com.io7m.cardant.model.CANameMatchType;
import com.io7m.cardant.model.CANameMatchType.Any;
import com.io7m.cardant.model.CAPage;
import com.io7m.cardant.model.CASizeRange;
import com.io7m.cardant.model.CATypeDeclaration;
import com.io7m.cardant.model.CATypeDeclarationSummary;
import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.model.CATypeMatchType;
import com.io7m.cardant.model.CATypeMatchType.CATypeMatchAllOf;
import com.io7m.cardant.model.CATypeMatchType.CATypeMatchAny;
import com.io7m.cardant.model.CATypeMatchType.CATypeMatchAnyOf;
import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.inventory.CAIResponseBlame;
import com.io7m.cardant.protocol.inventory.cb.CAI1Attachment;
import com.io7m.cardant.protocol.inventory.cb.CAI1AttachmentKey;
import com.io7m.cardant.protocol.inventory.cb.CAI1File;
import com.io7m.cardant.protocol.inventory.cb.CAI1FileColumn;
import com.io7m.cardant.protocol.inventory.cb.CAI1FileColumnOrdering;
import com.io7m.cardant.protocol.inventory.cb.CAI1FileSearchParameters;
import com.io7m.cardant.protocol.inventory.cb.CAI1Id;
import com.io7m.cardant.protocol.inventory.cb.CAI1Item;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemColumn;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemColumnOrdering;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemLocation;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemLocations;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemReposit;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemSearchParameters;
import com.io7m.cardant.protocol.inventory.cb.CAI1ItemSummary;
import com.io7m.cardant.protocol.inventory.cb.CAI1ListLocationBehaviour;
import com.io7m.cardant.protocol.inventory.cb.CAI1Location;
import com.io7m.cardant.protocol.inventory.cb.CAI1LocationSummary;
import com.io7m.cardant.protocol.inventory.cb.CAI1Metadata;
import com.io7m.cardant.protocol.inventory.cb.CAI1MetadataElementMatch;
import com.io7m.cardant.protocol.inventory.cb.CAI1MetadataNameMatch;
import com.io7m.cardant.protocol.inventory.cb.CAI1MetadataValueMatch;
import com.io7m.cardant.protocol.inventory.cb.CAI1NameMatch;
import com.io7m.cardant.protocol.inventory.cb.CAI1NameMatch.CAI1NameMatchAny;
import com.io7m.cardant.protocol.inventory.cb.CAI1NameMatch.CAI1NameMatchExact;
import com.io7m.cardant.protocol.inventory.cb.CAI1NameMatch.CAI1NameMatchSearch;
import com.io7m.cardant.protocol.inventory.cb.CAI1Page;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseBlame;
import com.io7m.cardant.protocol.inventory.cb.CAI1SizeRange;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeDeclaration;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeDeclarationSummary;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeField;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeMatch;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeMatch.CAI1TypeMatchAllOf;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeMatch.CAI1TypeMatchAny;
import com.io7m.cardant.protocol.inventory.cb.CAI1TypeMatch.CAI1TypeMatchAnyOf;
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
import com.io7m.cedarbridge.runtime.convenience.CBLists;
import com.io7m.cedarbridge.runtime.convenience.CBMaps;
import com.io7m.cedarbridge.runtime.time.CBOffsetDateTime;
import com.io7m.lanark.core.RDottedName;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.io7m.cardant.protocol.inventory.cb.internal.CAI1ValidationCommon.errorProtocol;
import static com.io7m.cedarbridge.runtime.api.CBCore.float64;
import static com.io7m.cedarbridge.runtime.api.CBCore.signed64;
import static com.io7m.cedarbridge.runtime.api.CBCore.string;
import static com.io7m.cedarbridge.runtime.api.CBCore.unsigned32;
import static com.io7m.cedarbridge.runtime.api.CBCore.unsigned64;
import static java.lang.Integer.toUnsignedLong;

// CHECKSTYLE:OFF

public final class ToWireModel
{
  private ToWireModel()
  {
 
  }

  public static CAI1Item item(
    final CAItem item)
  {
    final var attachments =
      new HashMap<CAI1AttachmentKey, CAI1Attachment>();

    for (final var entry : item.attachments().entrySet()) {
      final var key =
        entry.getKey();
      final var val =
        entry.getValue();
      final var rKey =
        attachmentKey(key);
      final var rVal =
        attachment(val);
      attachments.put(rKey, rVal);
    }

    final var metadata =
      new HashMap<CBString, CAI1Metadata>();

    for (final var entry : item.metadata().entrySet()) {
      final var key =
        entry.getKey();
      final var val =
        entry.getValue();
      final var rKey =
        new CBString(key.value());
      final var rVal =
        metadata(val);
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
        item.types()
          .stream()
          .map(RDottedName::value)
          .map(CBString::new)
          .toList()
      )
    );
  }

  private static CAI1AttachmentKey attachmentKey(
    final CAAttachmentKey key)
  {
    return new CAI1AttachmentKey(
      new CBUUID(key.fileID().id()),
      new CBString(key.relation())
    );
  }

  private static CAI1Attachment attachment(
    final CAAttachment m)
  {
    return new CAI1Attachment(
      file(m.file()),
      new CBString(m.relation())
    );
  }

  public static CAI1Id iD(
    final CAIdType id)
  {
    if (id instanceof final CAFileID xid) {
      return new CAI1Id.CAI1FileID(new CBUUID(xid.id()));
    } else if (id instanceof final CALocationID xid) {
      return new CAI1Id.CAI1LocationID(new CBUUID(xid.id()));
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

  public static CAI1ItemReposit itemReposit(
    final CAItemRepositType reposit)
  {
    if (reposit instanceof final CAItemRepositAdd a) {
      return itemRepositAdd(a);
    } else if (reposit instanceof final CAItemRepositRemove r) {
      return itemRepositRemove(r);
    } else if (reposit instanceof final CAItemRepositMove m) {
      return itemRepositMove(m);
    }

    throw new ProtocolUncheckedException(
      errorProtocol(reposit));
  }

  private static CAI1ItemReposit itemRepositMove(
    final CAItemRepositMove m)
  {
    return new CAI1ItemReposit.CAI1ItemRepositMove(
      new CBUUID(m.item().id()),
      new CBUUID(m.fromLocation().id()),
      new CBUUID(m.toLocation().id()),
      new CBIntegerUnsigned64(m.count())
    );
  }

  private static CAI1ItemReposit itemRepositRemove(
    final CAItemRepositRemove r)
  {
    return new CAI1ItemReposit.CAI1ItemRepositRemove(
      new CBUUID(r.item().id()),
      new CBUUID(r.location().id()),
      new CBIntegerUnsigned64(r.count())
    );
  }

  private static CAI1ItemReposit itemRepositAdd(
    final CAItemRepositAdd a)
  {
    return new CAI1ItemReposit.CAI1ItemRepositAdd(
      new CBUUID(a.item().id()),
      new CBUUID(a.location().id()),
      new CBIntegerUnsigned64(a.count())
    );
  }

  public static CAI1Location location(
    final CALocation location)
  {
    final var attachments =
      new HashMap<CAI1AttachmentKey, CAI1Attachment>();

    for (final var entry : location.attachments().entrySet()) {
      final var key =
        entry.getKey();
      final var val =
        entry.getValue();
      final var rKey =
        attachmentKey(key);
      final var rVal =
        attachment(val);
      attachments.put(rKey, rVal);
    }

    final var metadata =
      new HashMap<CBString, CAI1Metadata>();

    for (final var entry : location.metadata().entrySet()) {
      final var key =
        entry.getKey();
      final var val =
        entry.getValue();
      final var rKey =
        new CBString(key.value());
      final var rVal =
        metadata(val);
      metadata.put(rKey, rVal);
    }

    return new CAI1Location(
      new CBUUID(location.id().id()),
      CBOptionType.fromOptional(location.parent().map(x -> new CBUUID(x.id()))),
      new CBString(location.name()),
      new CBMap<>(metadata),
      new CBMap<>(attachments),
      new CBList<>(
        location.types()
          .stream()
          .map(RDottedName::value)
          .map(CBString::new)
          .toList()
      )
    );
  }

  public static CAI1LocationSummary locationSummary(
    final CALocationSummary summary)
  {
    return new CAI1LocationSummary(
      new CBUUID(summary.id().id()),
      CBOptionType.fromOptional(summary.parent().map(x -> new CBUUID(x.id()))),
      new CBString(summary.name())
    );
  }

  public static CAI1Metadata metadata(
    final CAMetadataType c)
  {
    if (c instanceof final CAMetadataType.Integral cc) {
      return new CAI1Metadata.Integral(
        string(cc.name().value()),
        signed64(cc.value())
      );
    }
    if (c instanceof final CAMetadataType.Monetary cc) {
      return new CAI1Metadata.Monetary(
        string(cc.name().value()),
        string(cc.value().toString()),
        string(cc.currency().getCode())
      );
    }
    if (c instanceof final CAMetadataType.Text cc) {
      return new CAI1Metadata.Text(
        string(cc.name().value()),
        string(cc.value())
      );
    }
    if (c instanceof final CAMetadataType.Time cc) {
      return new CAI1Metadata.Time(
        string(cc.name().value()),
        new CBOffsetDateTime(cc.value())
      );
    }
    if (c instanceof final CAMetadataType.Real cc) {
      return new CAI1Metadata.Real(
        string(cc.name().value()),
        float64(cc.value())
      );
    }
    throw new ProtocolUncheckedException(errorProtocol(c));
  }

  public static CAI1ListLocationBehaviour listLocationBehaviour(
    final CAItemLocationMatchType b)
  {
    if (b instanceof CAItemLocationMatchType.CAItemLocationsAll) {
      return new CAI1ListLocationBehaviour.CAI1ListLocationsAll();
    } else if (b instanceof final CAItemLocationMatchType.CAItemLocationExact e) {
      return new CAI1ListLocationBehaviour.CAI1ListLocationExact(
        new CBUUID(e.location().id())
      );
    } else if (b instanceof final CAItemLocationMatchType.CAItemLocationWithDescendants e) {
      return new CAI1ListLocationBehaviour.CAI1ListLocationWithDescendants(
        new CBUUID(e.location().id())
      );
    }

    throw new ProtocolUncheckedException(errorProtocol(b));
  }

  public static CAI1File file(
    final CAFileType data)
  {
    if (data instanceof final CAFileType.CAFileWithData withData) {
      return fileWithData(withData);
    } else if (data instanceof final CAFileType.CAFileWithoutData withoutData) {
      return fileWithoutData(withoutData);
    }

    throw new ProtocolUncheckedException(errorProtocol(data));
  }

  private static CAI1File fileWithoutData(
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

  private static CAI1File fileWithData(
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

  public static CAI1ItemLocations itemLocations(
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
          itemLocation(itemLocation);
        outLocations.put(outLocation.fieldItemId(), outLocation);
      }

      output.put(new CBUUID(locationId.id()), new CBMap<>(outLocations));
    }

    return new CAI1ItemLocations(new CBMap<>(output));
  }

  private static CAI1ItemLocation itemLocation(
    final CAItemLocation itemLocation)
  {
    return new CAI1ItemLocation(
      new CBUUID(itemLocation.item().id()),
      new CBUUID(itemLocation.location().id()),
      new CBIntegerUnsigned64(itemLocation.count())
    );
  }

  public static CAI1ItemSearchParameters itemSearchParameters(
    final CAItemSearchParameters parameters)
  {
    return new CAI1ItemSearchParameters(
      listLocationBehaviour(parameters.locationMatch()),
      nameMatch(parameters.nameMatch()),
      typeMatch(parameters.typeMatch()),
      metadataMatch(parameters.metadataMatch()),
      itemColumnOrdering(parameters.ordering()),
      unsigned32(parameters.pageSize())
    );
  }

  private static CAI1MetadataElementMatch metadataMatch(
    final CAMetadataElementMatchType match)
  {
    if (match instanceof final CAMetadataElementMatchType.Specific specific) {
      return new CAI1MetadataElementMatch.Specific(
        metadataNameMatch(specific.name()),
        metadataValueMatch(specific.value())
      );
    }
    if (match instanceof final CAMetadataElementMatchType.And and) {
      return new CAI1MetadataElementMatch.And(
        metadataMatch(and.e0()),
        metadataMatch(and.e1())
      );
    }
    if (match instanceof final CAMetadataElementMatchType.Or or) {
      return new CAI1MetadataElementMatch.Or(
        metadataMatch(or.e0()),
        metadataMatch(or.e1())
      );
    }
    throw new IllegalStateException();
  }

  private static CAI1MetadataNameMatch metadataNameMatch(
    final CAMetadataNameMatchType name)
  {
    if (name instanceof CAMetadataNameMatchType.AnyName) {
      return new CAI1MetadataNameMatch.Anything();
    }
    if (name instanceof final CAMetadataNameMatchType.ExactName exact) {
      return new CAI1MetadataNameMatch.Exact(string(exact.name().value()));
    }
    if (name instanceof final CAMetadataNameMatchType.SearchName search) {
      return new CAI1MetadataNameMatch.Search(string(search.query()));
    }
    throw new IllegalStateException();
  }

  private static CAI1MetadataValueMatch metadataValueMatch(
    final CAMetadataValueMatchType match)
  {
    if (match instanceof CAMetadataValueMatchType.AnyValue) {
      return new CAI1MetadataValueMatch.Anything();
    }
    if (match instanceof final IntegralMatchType.WithinRange w) {
      return new CAI1MetadataValueMatch.IntegralWithinRange(
        signed64(w.lower()),
        signed64(w.upper())
      );
    }
    if (match instanceof final RealMatchType.WithinRange w) {
      return new CAI1MetadataValueMatch.RealWithinRange(
        float64(w.lower()),
        float64(w.upper())
      );
    }
    if (match instanceof final TimeMatchType.WithinRange w) {
      return new CAI1MetadataValueMatch.TimeWithinRange(
        new CBOffsetDateTime(w.lower()),
        new CBOffsetDateTime(w.upper())
      );
    }
    if (match instanceof final MonetaryMatchType.WithinRange w) {
      return new CAI1MetadataValueMatch.MonetaryWithinRange(
        string(w.lower().toString()),
        string(w.upper().toString())
      );
    }
    if (match instanceof final MonetaryMatchType.WithCurrency w) {
      return new CAI1MetadataValueMatch.MonetaryWithCurrency(
        string(w.currency().getCode())
      );
    }
    if (match instanceof final TextMatchType.ExactTextValue w) {
      return new CAI1MetadataValueMatch.TextExact(string(w.text()));
    }
    if (match instanceof final TextMatchType.Search w) {
      return new CAI1MetadataValueMatch.TextSearch(string(w.query()));
    }

    throw new IllegalStateException();
  }

  private static CAI1NameMatch nameMatch(
    final CANameMatchType match)
  {
    if (match instanceof Any) {
      return new CAI1NameMatchAny();
    }
    if (match instanceof final CANameMatchType.Exact exact) {
      return new CAI1NameMatchExact(string(exact.text()));
    }
    if (match instanceof final CANameMatchType.Search search) {
      return new CAI1NameMatchSearch(string(search.query()));
    }
    throw new IllegalStateException();
  }

  private static CAI1TypeMatch typeMatch(
    final CATypeMatchType match)
  {
    if (match instanceof CATypeMatchAny) {
      return new CAI1TypeMatchAny();
    }
    if (match instanceof final CATypeMatchAllOf exact) {
      return new CAI1TypeMatchAllOf(
        CBLists.ofCollection(exact.types(), x -> string(x.value()))
      );
    }
    if (match instanceof final CATypeMatchAnyOf exact) {
      return new CAI1TypeMatchAnyOf(
        CBLists.ofCollection(exact.types(), x -> string(x.value()))
      );
    }
    throw new IllegalStateException();
  }

  public static CAI1FileSearchParameters fileSearchParameters(
    final CAFileSearchParameters parameters)
  {
    return new CAI1FileSearchParameters(
      CBOptionType.fromOptional(parameters.description().map(CBString::new)),
      CBOptionType.fromOptional(parameters.mediaType().map(CBString::new)),
      CBOptionType.fromOptional(
        parameters.sizeRange()
          .map(ToWireModel::sizeRange)
      ),
      fileColumnOrdering(parameters.ordering()),
      unsigned32(parameters.pageSize())
    );
  }

  private static CAI1SizeRange sizeRange(
    final CASizeRange range)
  {
    return new CAI1SizeRange(
      unsigned64(range.sizeMinimum()),
      unsigned64(range.sizeMaximum())
    );
  }

  private static CAI1FileColumnOrdering fileColumnOrdering(
    final CAFileColumnOrdering ordering)
  {
    return new CAI1FileColumnOrdering(
      fileColumn(ordering.column()),
      CBBooleanType.fromBoolean(ordering.ascending())
    );
  }

  private static CAI1FileColumn fileColumn(
    final CAFileColumn column)
  {
    return switch (column) {
      case BY_ID -> new CAI1FileColumn.CAI1ById();
      case BY_DESCRIPTION -> new CAI1FileColumn.CAI1ByDescription();
    };
  }

  private static CAI1ItemColumnOrdering itemColumnOrdering(
    final CAItemColumnOrdering ordering)
  {
    return new CAI1ItemColumnOrdering(
      itemColumn(ordering.column()),
      CBBooleanType.fromBoolean(ordering.ascending())
    );
  }

  private static CAI1ItemColumn itemColumn(
    final CAItemColumn column)
  {
    return switch (column) {
      case BY_ID -> new CAI1ItemColumn.CAI1ById();
      case BY_NAME -> new CAI1ItemColumn.CAI1ByName();
    };
  }

  public static CAI1TypeScalar typeScalar(
    final CATypeScalarType c)
  {
    if (c instanceof final CATypeScalarType.Integral cc) {
      return new CAI1TypeScalar.Integral(
        string(cc.name().value()),
        string(cc.description()),
        signed64(cc.rangeLower()),
        signed64(cc.rangeUpper())
      );
    }
    if (c instanceof final CATypeScalarType.Monetary cc) {
      return new CAI1TypeScalar.Monetary(
        string(cc.name().value()),
        string(cc.description()),
        string(cc.rangeLower().toString()),
        string(cc.rangeUpper().toString())
      );
    }
    if (c instanceof final CATypeScalarType.Text cc) {
      return new CAI1TypeScalar.Text(
        string(cc.name().value()),
        string(cc.description()),
        string(cc.pattern())
      );
    }
    if (c instanceof final CATypeScalarType.Time cc) {
      return new CAI1TypeScalar.Time(
        string(cc.name().value()),
        string(cc.description()),
        new CBOffsetDateTime(cc.rangeLower()),
        new CBOffsetDateTime(cc.rangeUpper())
      );
    }
    if (c instanceof final CATypeScalarType.Real cc) {
      return new CAI1TypeScalar.Real(
        string(cc.name().value()),
        string(cc.description()),
        float64(cc.rangeLower()),
        float64(cc.rangeUpper())
      );
    }
    throw new ProtocolUncheckedException(errorProtocol(c));
  }

  public static CAI1TypeField typeField(
    final CATypeField x)
  {
    return new CAI1TypeField(
      string(x.name().value()),
      string(x.description()),
      typeScalar(x.type()),
      CBBooleanType.fromBoolean(x.isRequired())
    );
  }

  public static CAI1TypeDeclaration typeDeclaration(
    final CATypeDeclaration x)
  {
    return new CAI1TypeDeclaration(
      string(x.name().value()),
      string(x.description()),
      CBMaps.ofMap(
        x.fields(),
        s -> string(s.value()),
        ToWireModel::typeField
      )
    );
  }

  public static CAI1TypeDeclarationSummary typeDeclarationSummary(
    final CATypeDeclarationSummary x)
  {
    return new CAI1TypeDeclarationSummary(
      string(x.name().value()),
      string(x.description())
    );
  }

  public static CAI1ItemSummary itemSummary(
    final CAItemSummary i)
  {
    return new CAI1ItemSummary(
      new CBUUID(i.id().id()),
      new CBString(i.name())
    );
  }

  public static <A, B extends CBSerializableType> CAI1Page<B> page(
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

  public static CAI1ResponseBlame blame(
    final CAIResponseBlame blame)
  {
    return switch (blame) {
      case BLAME_SERVER -> new CAI1ResponseBlame.BlameServer();
      case BLAME_CLIENT -> new CAI1ResponseBlame.BlameClient();
    };
  }
}
