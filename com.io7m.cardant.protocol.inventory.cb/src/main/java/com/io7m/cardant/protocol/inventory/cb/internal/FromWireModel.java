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

import com.io7m.cardant.model.CAAttachment;
import com.io7m.cardant.model.CAAttachmentKey;
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAFileColumn;
import com.io7m.cardant.model.CAFileColumnOrdering;
import com.io7m.cardant.model.CAFileID;
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
import com.io7m.cardant.model.CALocationSummaries;
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
import com.io7m.cardant.model.CAMoney;
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
import com.io7m.cardant.protocol.inventory.CAIResponseBlame;
import com.io7m.cardant.protocol.inventory.cb.CAI1Attachment;
import com.io7m.cardant.protocol.inventory.cb.CAI1AttachmentKey;
import com.io7m.cardant.protocol.inventory.cb.CAI1File;
import com.io7m.cardant.protocol.inventory.cb.CAI1FileColumn;
import com.io7m.cardant.protocol.inventory.cb.CAI1FileColumnOrdering;
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
import com.io7m.cardant.protocol.inventory.cb.CAI1MetadataValueMatch.IntegralWithinRange;
import com.io7m.cardant.protocol.inventory.cb.CAI1MetadataValueMatch.MonetaryWithCurrency;
import com.io7m.cardant.protocol.inventory.cb.CAI1MetadataValueMatch.MonetaryWithinRange;
import com.io7m.cardant.protocol.inventory.cb.CAI1MetadataValueMatch.RealWithinRange;
import com.io7m.cardant.protocol.inventory.cb.CAI1MetadataValueMatch.TextExact;
import com.io7m.cardant.protocol.inventory.cb.CAI1MetadataValueMatch.TextSearch;
import com.io7m.cardant.protocol.inventory.cb.CAI1MetadataValueMatch.TimeWithinRange;
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
import com.io7m.cedarbridge.runtime.api.CBByteArray;
import com.io7m.cedarbridge.runtime.api.CBMap;
import com.io7m.cedarbridge.runtime.api.CBSerializableType;
import com.io7m.cedarbridge.runtime.api.CBString;
import com.io7m.cedarbridge.runtime.api.CBUUID;
import com.io7m.cedarbridge.runtime.convenience.CBMaps;
import com.io7m.cedarbridge.runtime.convenience.CBSets;
import com.io7m.lanark.core.RDottedName;
import org.joda.money.CurrencyUnit;

import java.math.BigDecimal;
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
      new HashMap<CAAttachmentKey, CAAttachment>();

    for (final var entry : item.fieldAttachments().values().entrySet()) {
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
      new HashMap<RDottedName, CAMetadataType>();

    for (final var entry : item.fieldMetadata().values().entrySet()) {
      final var key =
        entry.getKey();
      final var val =
        entry.getValue();
      final var rKey =
        new RDottedName(key.value());
      final var rVal =
        metadata(val);
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
        item.fieldTypes()
          .values()
          .stream()
          .map(CBString::value)
          .map(RDottedName::new)
          .toList()
      )
    );
  }

  private static CAAttachmentKey attachmentKey(
    final CAI1AttachmentKey key)
  {
    return new CAAttachmentKey(
      new CAFileID(key.fieldId().value()),
      key.fieldRelation().value()
    );
  }

  private static CAAttachment attachment(
    final CAI1Attachment a)
  {
    return new CAAttachment(
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

    throw new ProtocolUncheckedException(errorProtocol(i));
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

  public static CAIdType id(final CAI1Id i)
  {
    if (i instanceof final CAI1Id.CAI1LocationID ii) {
      return new CALocationID(ii.fieldId().value());
    }
    if (i instanceof final CAI1Id.CAI1ItemID ii) {
      return new CAItemID(ii.fieldId().value());
    }
    if (i instanceof final CAI1Id.CAI1FileID ii) {
      return new CAFileID(ii.fieldId().value());
    }

    throw new ProtocolUncheckedException(errorProtocol(i));
  }

  public static CAMetadataType metadata(
    final CAI1Metadata i)
  {
    if (i instanceof final CAI1Metadata.Integral ti) {
      return new CAMetadataType.Integral(
        new RDottedName(ti.fieldKey().value()),
        ti.fieldValue().value()
      );
    }
    if (i instanceof final CAI1Metadata.Real ti) {
      return new CAMetadataType.Real(
        new RDottedName(ti.fieldKey().value()),
        ti.fieldValue().value()
      );
    }
    if (i instanceof final CAI1Metadata.Text ti) {
      return new CAMetadataType.Text(
        new RDottedName(ti.fieldKey().value()),
        ti.fieldValue().value()
      );
    }
    if (i instanceof final CAI1Metadata.Time ti) {
      return new CAMetadataType.Time(
        new RDottedName(ti.fieldKey().value()),
        ti.fieldValue().value()
      );
    }
    if (i instanceof final CAI1Metadata.Monetary ti) {
      return new CAMetadataType.Monetary(
        new RDottedName(ti.fieldKey().value()),
        CAMoney.money(ti.fieldValue().value()),
        CurrencyUnit.of(ti.fieldCurrency().value())
      );
    }

    throw new ProtocolUncheckedException(errorProtocol(i));
  }

  public static CAItemLocationMatchType itemLocationMatch(
    final CAI1ListLocationBehaviour b)
  {
    if (b instanceof final CAI1ListLocationBehaviour.CAI1ListLocationExact x) {
      return new CAItemLocationMatchType.CAItemLocationExact(
        new CALocationID(x.fieldLocationId().value())
      );
    }
    if (b instanceof final CAI1ListLocationBehaviour.CAI1ListLocationsAll a) {
      return new CAItemLocationMatchType.CAItemLocationsAll();
    }
    if (b instanceof final CAI1ListLocationBehaviour.CAI1ListLocationWithDescendants x) {
      return new CAItemLocationMatchType.CAItemLocationWithDescendants(
        new CALocationID(x.fieldLocationId().value())
      );
    }

    throw new ProtocolUncheckedException(
      errorProtocol(b));
  }

  public static CAFileType file(
    final CAI1File f)
  {
    if (f instanceof final CAI1File.CAI1FileWithData with) {
      return new CAFileType.CAFileWithData(
        new CAFileID(with.fieldId().value()),
        with.fieldDescription().value(),
        with.fieldMediaType().value(),
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

    throw new ProtocolUncheckedException(errorProtocol(f));
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
    final var attachments =
      new HashMap<CAAttachmentKey, CAAttachment>();

    for (final var entry : c.fieldAttachments().values().entrySet()) {
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
      new HashMap<RDottedName, CAMetadataType>();

    for (final var entry : c.fieldMetadata().values().entrySet()) {
      final var key =
        entry.getKey();
      final var val =
        entry.getValue();
      final var rKey =
        new RDottedName(key.value());
      final var rVal =
        metadata(val);
      metadata.put(rKey, rVal);
    }

    return new CALocation(
      new CALocationID(c.fieldLocationId().value()),
      c.fieldParent()
        .asOptional()
        .map(x -> new CALocationID(x.value())),
      c.fieldName().value(),
      new TreeMap<>(metadata),
      new TreeMap<>(attachments),
      new TreeSet<>(
        c.fieldTypes()
          .values()
          .stream()
          .map(CBString::value)
          .map(RDottedName::new)
          .toList()
      )
    );
  }

  public static CALocationSummary locationSummary(
    final CAI1LocationSummary c)
  {
    return new CALocationSummary(
      new CALocationID(c.fieldId().value()),
      c.fieldParent()
        .asOptional()
        .map(x -> new CALocationID(x.value())),
      c.fieldName().value()
    );
  }

  public static CALocationSummaries locations(
    final CBMap<CBUUID, CAI1LocationSummary> locations)
  {
    final var results = new TreeMap<CALocationID, CALocationSummary>();
    for (final var entry : locations.values().entrySet()) {
      final var location =
        locationSummary(entry.getValue());
      results.put(location.id(), location);
    }
    return new CALocationSummaries(results);
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
      itemLocationMatch(p.fieldLocation()),
      itemNameMatch(p.fieldNameMatch()),
      itemTypeMatch(p.fieldTypeMatch()),
      metadataMatch(p.fieldMetaMatch()),
      itemColumnOrdering(p.fieldOrder()),
      (int) p.fieldLimit().value()
    );
  }

  private static CAMetadataElementMatchType metadataMatch(
    final CAI1MetadataElementMatch match)
  {
    if (match instanceof final CAI1MetadataElementMatch.And and) {
      return new CAMetadataElementMatchType.And(
        metadataMatch(and.fieldE0()),
        metadataMatch(and.fieldE1())
      );
    }
    if (match instanceof final CAI1MetadataElementMatch.Or or) {
      return new CAMetadataElementMatchType.Or(
        metadataMatch(or.fieldE0()),
        metadataMatch(or.fieldE1())
      );
    }
    if (match instanceof final CAI1MetadataElementMatch.Specific specific) {
      return new CAMetadataElementMatchType.Specific(
        metadataNameMatch(specific.fieldName()),
        metadataValueMatch(specific.fieldValue())
      );
    }

    throw new IllegalStateException();
  }

  private static CAMetadataNameMatchType metadataNameMatch(
    final CAI1MetadataNameMatch m)
  {
    if (m instanceof CAI1MetadataNameMatch.Anything) {
      return CAMetadataNameMatchType.AnyName.ANY_NAME;
    }
    if (m instanceof final CAI1MetadataNameMatch.Exact exact) {
      return new CAMetadataNameMatchType.ExactName(
        new RDottedName(exact.fieldName().value())
      );
    }
    if (m instanceof final CAI1MetadataNameMatch.Search search) {
      return new CAMetadataNameMatchType.SearchName(
        search.fieldSearch().value()
      );
    }

    throw new IllegalStateException();
  }

  private static CAMetadataValueMatchType metadataValueMatch(
    final CAI1MetadataValueMatch m)
  {
    if (m instanceof CAI1MetadataValueMatch.Anything) {
      return CAMetadataValueMatchType.AnyValue.ANY_VALUE;
    }
    if (m instanceof final IntegralWithinRange w) {
      return new IntegralMatchType.WithinRange(
        w.fieldLower().value(),
        w.fieldUpper().value()
      );
    }
    if (m instanceof final RealWithinRange w) {
      return new RealMatchType.WithinRange(
        w.fieldLower().value(),
        w.fieldUpper().value()
      );
    }
    if (m instanceof final TimeWithinRange w) {
      return new TimeMatchType.WithinRange(
        w.fieldLower().value(),
        w.fieldUpper().value()
      );
    }
    if (m instanceof final MonetaryWithinRange w) {
      return new MonetaryMatchType.WithinRange(
        new BigDecimal(w.fieldLower().value()),
        new BigDecimal(w.fieldUpper().value())
      );
    }
    if (m instanceof final MonetaryWithCurrency w) {
      return new MonetaryMatchType.WithCurrency(
        CurrencyUnit.of(w.fieldCurrency().value())
      );
    }
    if (m instanceof final TextExact w) {
      return new TextMatchType.ExactTextValue(w.fieldExact().value());
    }
    if (m instanceof final TextSearch w) {
      return new TextMatchType.Search(w.fieldSearch().value());
    }

    throw new IllegalStateException();
  }

  private static CATypeMatchType itemTypeMatch(
    final CAI1TypeMatch match)
  {
    if (match instanceof CAI1TypeMatchAny) {
      return CATypeMatchAny.ANY;
    }
    if (match instanceof final CAI1TypeMatchAllOf types) {
      return new CATypeMatchAllOf(
        CBSets.toSet(types.fieldTypes(), x -> new RDottedName(x.value()))
      );
    }
    if (match instanceof final CAI1TypeMatchAnyOf types) {
      return new CATypeMatchAnyOf(
        CBSets.toSet(types.fieldTypes(), x -> new RDottedName(x.value()))
      );
    }
    throw new IllegalStateException();
  }

  private static CANameMatchType itemNameMatch(
    final CAI1NameMatch match)
  {
    if (match instanceof CAI1NameMatchAny) {
      return Any.ANY_NAME;
    }
    if (match instanceof final CAI1NameMatchExact exact) {
      return new CANameMatchType.Exact(exact.fieldText().value());
    }
    if (match instanceof final CAI1NameMatchSearch search) {
      return new CANameMatchType.Search(search.fieldQuery().value());
    }
    throw new IllegalStateException();
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
      errorProtocol(c));
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
    throw new ProtocolUncheckedException(errorProtocol(c));
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

  public static CATypeScalarType typeScalar(
    final CAI1TypeScalar x)
  {
    if (x instanceof final CAI1TypeScalar.Integral xt) {
      return new CATypeScalarType.Integral(
        new RDottedName(xt.fieldName().value()),
        xt.fieldDescription().value(),
        xt.fieldRangeLower().value(),
        xt.fieldRangeUpper().value()
      );
    }
    if (x instanceof final CAI1TypeScalar.Time xt) {
      return new CATypeScalarType.Time(
        new RDottedName(xt.fieldName().value()),
        xt.fieldDescription().value(),
        xt.fieldRangeLower().value(),
        xt.fieldRangeUpper().value()
      );
    }
    if (x instanceof final CAI1TypeScalar.Text xt) {
      return new CATypeScalarType.Text(
        new RDottedName(xt.fieldName().value()),
        xt.fieldDescription().value(),
        xt.fieldPattern().value()
      );
    }
    if (x instanceof final CAI1TypeScalar.Monetary xt) {
      return new CATypeScalarType.Monetary(
        new RDottedName(xt.fieldName().value()),
        xt.fieldDescription().value(),
        CAMoney.money(xt.fieldRangeLower().value()),
        CAMoney.money(xt.fieldRangeUpper().value())
      );
    }
    if (x instanceof final CAI1TypeScalar.Real xt) {
      return new CATypeScalarType.Real(
        new RDottedName(xt.fieldName().value()),
        xt.fieldDescription().value(),
        xt.fieldRangeLower().value(),
        xt.fieldRangeUpper().value()
      );
    }
    throw new ProtocolUncheckedException(errorProtocol(x));
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
