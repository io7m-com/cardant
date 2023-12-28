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


package com.io7m.cardant.type_packages.internal;

import com.io7m.anethum.api.SerializationException;
import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.model.CATypeRecord;
import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.cardant.model.type_package.CATypePackage;
import com.io7m.cardant.model.type_package.CATypePackageImport;
import com.io7m.cardant.type_packages.CATypePackageSchemas;
import com.io7m.cardant.type_packages.CATypePackageSerializerType;
import com.io7m.lanark.core.RDottedName;
import com.io7m.verona.core.VersionRange;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A type package serializer.
 */

public final class CATypePackageSerializer
  implements CATypePackageSerializerType
{
  private final OutputStream stream;
  private final XMLStreamWriter output;

  /**
   * A type package serializer.
   *
   * @param inStream The output stream
   */

  public CATypePackageSerializer(
    final OutputStream inStream)
  {
    this.stream =
      Objects.requireNonNull(inStream, "stream");

    try {
      this.output =
        XMLOutputFactory.newFactory()
          .createXMLStreamWriter(this.stream, "UTF-8");
    } catch (final XMLStreamException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void execute(
    final CATypePackage value)
    throws SerializationException
  {
    try {
      this.output.writeStartDocument("UTF-8", "1.0");
      this.serializePackage(value);
      this.output.writeEndDocument();
    } catch (final XMLStreamException e) {
      throw new SerializationException(e.getMessage(), e);
    }
  }

  private void serializePackage(
    final CATypePackage value)
    throws XMLStreamException
  {
    this.output.writeStartElement("Package");
    this.output.writeDefaultNamespace(findNS());

    this.serializePackageInfo(value);
    this.serializeImports(value.imports());
    this.serializeTypeScalars(value.scalarTypes());
    this.serializeRecords(value, value.recordTypes());

    this.output.writeEndElement();
  }

  private void serializeRecords(
    final CATypePackage typePackage,
    final Map<RDottedName, CATypeRecord> records)
    throws XMLStreamException
  {
    for (final var entry : records.entrySet()) {
      this.serializeTypeRecord(typePackage, entry.getValue());
    }
  }

  private void serializeTypeRecord(
    final CATypePackage typePackage,
    final CATypeRecord value)
    throws XMLStreamException
  {
    this.output.writeStartElement("TypeRecord");
    this.output.writeAttribute("Name", unqualify(value.name()));
    this.output.writeAttribute("Description", value.description());

    for (final var e : value.fields().entrySet()) {
      this.serializeField(typePackage, e.getValue());
    }

    this.output.writeEndElement();
  }

  private void serializeField(
    final CATypePackage typePackage,
    final CATypeField field)
    throws XMLStreamException
  {
    final var type = field.type();

    if (isInThisPackage(typePackage, type)) {
      this.output.writeStartElement("Field");
      this.output.writeAttribute(
        "Name", unqualify(field.name()));
      this.output.writeAttribute(
        "Description", field.description());
      this.output.writeAttribute(
        "Type", unqualify(type.name()));
      this.output.writeAttribute(
        "Required",
        String.valueOf(field.isRequired()));
      this.output.writeEndElement();
    } else {
      this.output.writeStartElement("FieldWithExternalType");
      this.output.writeAttribute(
        "Name", unqualify(field.name()));
      this.output.writeAttribute(
        "Description", field.description());
      this.output.writeAttribute(
        "Type", type.name().value());
      this.output.writeAttribute(
        "Required",
        String.valueOf(field.isRequired()));
      this.output.writeEndElement();
    }
  }

  private static boolean isInThisPackage(
    final CATypePackage typePackage,
    final CATypeScalarType type)
  {
    final var pName =
      typePackage.identifier().name().segments();
    final var tName =
      new LinkedList<>(type.name().segments());

    tName.removeLast();
    return Objects.equals(pName, tName);
  }

  private void serializeTypeScalars(
    final Map<RDottedName, CATypeScalarType> scalars)
    throws XMLStreamException
  {
    for (final var entry : scalars.entrySet()) {
      this.serializeTypeScalar(entry.getValue());
    }
  }

  private void serializeTypeScalar(
    final CATypeScalarType value)
    throws XMLStreamException
  {
    switch (value) {
      case final CATypeScalarType.Integral integral -> {
        this.output.writeStartElement("TypeScalarIntegral");
        this.output.writeAttribute(
          "Name", unqualify(value.name()));
        this.output.writeAttribute(
          "Description", integral.description());
        this.output.writeAttribute(
          "RangeLower", Long.toString(integral.rangeLower()));
        this.output.writeAttribute(
          "RangeUpper", Long.toString(integral.rangeUpper()));
        this.output.writeEndElement();
      }

      case final CATypeScalarType.Monetary monetary -> {
        this.output.writeStartElement("TypeScalarMonetary");
        this.output.writeAttribute(
          "Name", unqualify(value.name()));
        this.output.writeAttribute(
          "Description", monetary.description());
        this.output.writeAttribute(
          "RangeLower", monetary.rangeLower().toString());
        this.output.writeAttribute(
          "RangeUpper", monetary.rangeUpper().toString());
        this.output.writeEndElement();
      }

      case final CATypeScalarType.Real real -> {
        this.output.writeStartElement("TypeScalarReal");
        this.output.writeAttribute(
          "Name", unqualify(value.name()));
        this.output.writeAttribute(
          "Description", real.description());
        this.output.writeAttribute(
          "RangeLower", Double.toString(real.rangeLower()));
        this.output.writeAttribute(
          "RangeUpper", Double.toString(real.rangeUpper()));
        this.output.writeEndElement();
      }

      case final CATypeScalarType.Text text -> {
        this.output.writeStartElement("TypeScalarText");
        this.output.writeAttribute(
          "Name", unqualify(value.name()));
        this.output.writeAttribute(
          "Description", text.description());
        this.output.writeAttribute(
          "Pattern", text.pattern());
        this.output.writeEndElement();
      }

      case final CATypeScalarType.Time time -> {
        this.output.writeStartElement("TypeScalarTime");
        this.output.writeAttribute(
          "Name", unqualify(value.name()));
        this.output.writeAttribute(
          "Description", time.description());
        this.output.writeAttribute(
          "RangeLower", toXMLDateTime(time.rangeLower()));
        this.output.writeAttribute(
          "RangeUpper", toXMLDateTime(time.rangeUpper()));
        this.output.writeEndElement();
      }
    }
  }

  private static String toXMLDateTime(
    final OffsetDateTime time)
  {
    final var b = new StringBuilder();
    b.append(String.format("%04d", Integer.valueOf(time.getYear())));
    b.append('-');
    b.append(String.format("%02d", Integer.valueOf(time.getMonthValue())));
    b.append('-');
    b.append(String.format("%02d", Integer.valueOf(time.getDayOfMonth())));
    b.append('T');
    b.append(String.format("%02d", Integer.valueOf(time.getHour())));
    b.append(':');
    b.append(String.format("%02d", Integer.valueOf(time.getMinute())));
    b.append(':');
    b.append(String.format("%02d", Integer.valueOf(time.getSecond())));

    final var offset = time.getOffset();
    final var seconds = offset.getTotalSeconds();
    if (seconds >= 0) {
      b.append('+');
    } else {
      b.append('-');
    }

    final var offsetDuration = Duration.ofSeconds(seconds);
    b.append(
      String.format("%02d", Integer.valueOf(offsetDuration.toMinutesPart())));
    b.append(':');
    b.append(
      String.format("%02d", Integer.valueOf(offsetDuration.toSecondsPart())));
    return b.toString();
  }

  private static String unqualify(
    final RDottedName name)
  {
    return name.segments().get(name.segments().size() - 1);
  }

  private void serializeImports(
    final Set<CATypePackageImport> imports)
    throws XMLStreamException
  {
    for (final var importV : imports) {
      this.serializeImport(importV);
    }
  }

  private void serializeImport(
    final CATypePackageImport importV)
    throws XMLStreamException
  {
    this.output.writeStartElement("Import");
    this.output.writeAttribute("Package", importV.packageName().value());
    this.serializeVersionRange(importV.versionRange());
    this.output.writeEndElement();
  }

  private void serializeVersionRange(
    final VersionRange versionRange)
    throws XMLStreamException
  {
    this.output.writeStartElement("VersionRange");
    this.output.writeAttribute(
      "VersionLower",
      versionRange.lower().toString());
    this.output.writeAttribute(
      "VersionLowerInclusive",
      String.valueOf(versionRange.lowerInclusive()));
    this.output.writeAttribute(
      "VersionUpper",
      versionRange.upper().toString());
    this.output.writeAttribute(
      "VersionUpperInclusive",
      String.valueOf(versionRange.upperInclusive()));
    this.output.writeEndElement();
  }

  private void serializePackageInfo(
    final CATypePackage value)
    throws XMLStreamException
  {
    this.output.writeStartElement("PackageInfo");
    this.output.writeAttribute("Name", value.identifier().name().value());
    this.output.writeAttribute(
      "Version",
      value.identifier().version().toString());
    this.output.writeAttribute("Description", value.description());
    this.output.writeEndElement();
  }

  @Override
  public void close()
    throws IOException
  {
    this.stream.close();
  }

  private static String findNS()
  {
    return CATypePackageSchemas.schema1().namespace().toString();
  }
}
