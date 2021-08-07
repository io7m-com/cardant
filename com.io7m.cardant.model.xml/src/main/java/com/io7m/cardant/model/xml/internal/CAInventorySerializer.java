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

package com.io7m.cardant.model.xml.internal;

import com.io7m.anethum.common.SerializeException;
import com.io7m.cardant.model.CAInventoryElementType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemAttachmentID;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATags;
import com.io7m.cardant.model.xml.CAInventorySchemas;
import com.io7m.cardant.model.xml.CAInventorySerializerType;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.SortedMap;

/**
 * A serializer.
 */

public final class CAInventorySerializer implements CAInventorySerializerType
{
  private final XMLOutputFactory writers;
  private final OutputStream stream;
  private boolean namespace;

  /**
   * Construct a serializer.
   *
   * @param inWriters The XML writers
   * @param inStream  The output stream
   */

  public CAInventorySerializer(
    final XMLOutputFactory inWriters,
    final OutputStream inStream)
  {
    this.writers =
      Objects.requireNonNull(inWriters, "writers");
    this.stream =
      Objects.requireNonNull(inStream, "stream");
  }

  private void serializeItem(
    final XMLStreamWriter writer,
    final CAItem item)
    throws XMLStreamException
  {
    final var namespaceURI = CAInventorySchemas.inventory1Namespace().toString();
    writer.writeStartElement(namespaceURI, "Item");
    this.writeNamespaceIfRequired(writer, namespaceURI);
    writer.writeAttribute("id", item.id().id().toString());
    writer.writeAttribute("name", item.name());
    writer.writeAttribute("count", String.valueOf(item.count()));

    this.serializeItemMetadatas(writer, item.metadata());
    this.serializeTags(writer, new CATags(item.tags()));
    this.serializeItemAttachments(writer, item.attachments());

    writer.writeEndElement();
  }

  private void serializeItemAttachments(
    final XMLStreamWriter writer,
    final SortedMap<CAItemAttachmentID, CAItemAttachment> attachments)
    throws XMLStreamException
  {
    final var namespaceURI = CAInventorySchemas.inventory1Namespace().toString();
    writer.writeStartElement(namespaceURI, "ItemAttachments");
    this.writeNamespaceIfRequired(writer, namespaceURI);
    writer.writeEndElement();
  }

  private void serializeItemMetadatas(
    final XMLStreamWriter writer,
    final SortedMap<String, CAItemMetadata> metadata)
    throws XMLStreamException
  {
    final var namespaceURI = CAInventorySchemas.inventory1Namespace().toString();
    writer.writeStartElement(namespaceURI, "ItemMetadatas");
    this.writeNamespaceIfRequired(writer, namespaceURI);
    writer.writeEndElement();
  }

  private void writeNamespaceIfRequired(
    final XMLStreamWriter writer,
    final String namespaceURI)
    throws XMLStreamException
  {
    if (!this.namespace) {
      writer.writeNamespace("ci", namespaceURI);
      this.namespace = true;
    }
  }

  private void serializeItems(
    final XMLStreamWriter writer,
    final CAItems items)
    throws XMLStreamException
  {
    final var namespaceURI = CAInventorySchemas.inventory1Namespace().toString();
    writer.writeStartElement(namespaceURI, "Items");
    this.writeNamespaceIfRequired(writer, namespaceURI);

    for (final var item : items.items()) {
      this.serializeItem(writer, item);
    }
    writer.writeEndElement();
  }

  private void serializeTag(
    final XMLStreamWriter writer,
    final CATag tag)
    throws XMLStreamException
  {
    final var namespaceURI = CAInventorySchemas.inventory1Namespace().toString();
    writer.writeEmptyElement(namespaceURI, "Tag");
    this.writeNamespaceIfRequired(writer, namespaceURI);
    writer.writeAttribute("id", tag.id().toString());
    writer.writeAttribute("name", tag.name());
  }

  private void serializeTags(
    final XMLStreamWriter writer,
    final CATags tags)
    throws XMLStreamException
  {
    final var namespaceURI = CAInventorySchemas.inventory1Namespace().toString();
    writer.writeStartElement(namespaceURI, "Tags");
    this.writeNamespaceIfRequired(writer, namespaceURI);

    for (final var tag : tags.tags()) {
      this.serializeTag(writer, tag);
    }
    writer.writeEndElement();
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
    final var namespaceURI = CAInventorySchemas.inventory1Namespace().toString();
    writer.setPrefix("ci", namespaceURI);
  }

  @Override
  public void execute(
    final CAInventoryElementType value)
    throws SerializeException
  {
    try {
      final var writer =
        this.writers.createXMLStreamWriter(this.stream);

      this.start(writer);

      if (value instanceof CATags tags) {
        this.serializeTags(writer, tags);
      } else if (value instanceof CATag tag) {
        this.serializeTag(writer, tag);
      } else if (value instanceof CAItems items) {
        this.serializeItems(writer, items);
      } else if (value instanceof CAItem item) {
        this.serializeItem(writer, item);
      } else {
        throw new IllegalStateException();
      }

      this.finish(writer);
    } catch (final XMLStreamException e) {
      throw new SerializeException(e.getMessage(), e);
    }
  }

  @Override
  public void close()
    throws IOException
  {
    this.stream.close();
  }
}
