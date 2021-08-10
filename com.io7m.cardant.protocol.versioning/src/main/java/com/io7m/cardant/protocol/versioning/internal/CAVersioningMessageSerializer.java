/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.protocol.versioning.internal;

import com.io7m.anethum.common.SerializeException;
import com.io7m.cardant.protocol.versioning.CAVersioningMessageSerializerType;
import com.io7m.cardant.protocol.versioning.CAVersioningSchemas;
import com.io7m.cardant.protocol.versioning.messages.CAAPI;
import com.io7m.cardant.protocol.versioning.messages.CAVersion;
import com.io7m.cardant.protocol.versioning.messages.CAVersioningAPIVersioning;
import com.io7m.cardant.protocol.versioning.messages.CAVersioningMessageType;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

/**
 * A serializer.
 */

public final class CAVersioningMessageSerializer
  implements CAVersioningMessageSerializerType
{
  private static final String NAMESPACE =
    CAVersioningSchemas.namespace().toString();

  private final OutputStream stream;
  private boolean namespace;

  /**
   * Construct a serializer.
   *
   * @param inStream The target stream
   */

  public CAVersioningMessageSerializer(
    final OutputStream inStream)
  {
    this.stream = Objects.requireNonNull(inStream, "stream");
    this.namespace = false;
  }

  @Override
  public void execute(
    final CAVersioningMessageType message)
    throws SerializeException
  {
    Objects.requireNonNull(message, "message");

    final var writers =
      XMLOutputFactory.newFactory();

    try {
      final var writer =
        writers.createXMLStreamWriter(this.stream);

      this.start(writer);

      if (message instanceof CAVersioningAPIVersioning versioning) {
        this.serializeVersioning(writer, versioning);
      }

      this.finish(writer);
    } catch (final XMLStreamException e) {
      throw new SerializeException(e.getMessage(), e);
    }
  }

  private void serializeVersioning(
    final XMLStreamWriter writer,
    final CAVersioningAPIVersioning versioning)
    throws XMLStreamException
  {
    writer.writeStartElement(NAMESPACE, "APIVersioning");
    this.writeNamespaceIfNecessary(writer);
    for (final var api : versioning.apis()) {
      this.serializeAPI(writer, api);
    }
    writer.writeEndElement();
  }

  private void serializeAPI(
    final XMLStreamWriter writer,
    final CAAPI api)
    throws XMLStreamException
  {
    writer.writeStartElement(NAMESPACE, "API");
    this.writeNamespaceIfNecessary(writer);
    for (final var version : api.versions()) {
      this.serializeVersion(writer, version);
    }
    writer.writeEndElement();
  }

  private void serializeVersion(
    final XMLStreamWriter writer,
    final CAVersion version)
    throws XMLStreamException
  {
    writer.writeEmptyElement(NAMESPACE, "Version");
    this.writeNamespaceIfNecessary(writer);
    writer.writeAttribute("name", version.name());
    writer.writeAttribute("baseURI", version.baseURI());
    writer.writeAttribute("version", Long.toUnsignedString(version.version()));
  }

  private void writeNamespaceIfNecessary(
    final XMLStreamWriter writer)
    throws XMLStreamException
  {
    if (!this.namespace) {
      writer.writeNamespace("cv", NAMESPACE);
      this.namespace = true;
    }
  }

  private void finish(
    final XMLStreamWriter writer)
    throws XMLStreamException
  {
    writer.writeEndDocument();
    writer.writeCharacters(System.lineSeparator());
    writer.flush();
  }

  private void start(
    final XMLStreamWriter writer)
    throws XMLStreamException
  {
    writer.writeStartDocument("UTF-8", "1.0");
    writer.writeCharacters("\n");
    writer.setPrefix("cv", NAMESPACE);
  }

  @Override
  public void close()
    throws IOException
  {
    this.stream.close();
  }
}
