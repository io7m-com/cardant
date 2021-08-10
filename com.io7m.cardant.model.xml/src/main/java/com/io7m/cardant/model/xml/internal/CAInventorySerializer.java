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
import com.io7m.cardant.model.CAItemMetadatas;
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
import java.util.Base64;
import java.util.Objects;
import java.util.SortedMap;

/**
 * A serializer.
 */

public final class CAInventorySerializer implements CAInventorySerializerType
{
  private static final String NAMESPACE =
    CAInventorySchemas.inventory1Namespace().toString();

  private final XMLOutputFactory writers;
  private final OutputStream stream;
  private final Base64.Encoder base64;
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
    this.base64 =
      Base64.getMimeEncoder();
  }

  private void serializeItem(
    final XMLStreamWriter writer,
    final CAItem item)
    throws XMLStreamException
  {
    writer.writeStartElement(NAMESPACE, "Item");
    this.writeNamespaceIfRequired(writer, NAMESPACE);
    writer.writeAttribute("id", item.id().id().toString());
    writer.writeAttribute("name", item.name());
    writer.writeAttribute("count", String.valueOf(item.count()));

    this.serializeItemMetadatas(writer, item.metadata());
    this.serializeTags(writer, new CATags(item.tags()));
    this.serializeItemAttachments(writer, item.attachments());

    writer.writeEndElement();
  }

  private void serializeItemAttachment(
    final XMLStreamWriter writer,
    final CAItemAttachment itemAttachment)
    throws XMLStreamException
  {
    writer.writeStartElement(NAMESPACE, "ItemAttachment");
    this.writeNamespaceIfRequired(writer, NAMESPACE);

    writer.writeAttribute("id", itemAttachment.id().id().toString());
    writer.writeAttribute("description", itemAttachment.description());
    writer.writeAttribute("mediaType", itemAttachment.mediaType());
    writer.writeAttribute("relation", itemAttachment.relation());
    writer.writeAttribute("size", String.valueOf(itemAttachment.size()));
    writer.writeAttribute("hashAlgorithm", itemAttachment.hashAlgorithm());
    writer.writeAttribute("hashValue", itemAttachment.hashValue());

    final var data = itemAttachment.data();
    if (data.isPresent()) {
      writer.writeStartElement(NAMESPACE, "ItemAttachmentData");
      this.writeNamespaceIfRequired(writer, NAMESPACE);
      writer.writeCharacters(this.base64.encodeToString(data.get().data()));
      writer.writeEndElement();
    }

    writer.writeEndElement();
  }

  private void serializeItemMetadata(
    final XMLStreamWriter writer,
    final CAItemMetadata value)
    throws XMLStreamException
  {
    writer.writeStartElement(NAMESPACE, "ItemMetadata");
    this.writeNamespaceIfRequired(writer, NAMESPACE);
    writer.writeAttribute("name", value.name());
    writer.writeCharacters(value.value());
    writer.writeEndElement();
  }

  private void serializeItemAttachments(
    final XMLStreamWriter writer,
    final SortedMap<CAItemAttachmentID, CAItemAttachment> attachments)
    throws XMLStreamException
  {
    writer.writeStartElement(NAMESPACE, "ItemAttachments");
    this.writeNamespaceIfRequired(writer, NAMESPACE);
    for (final var entry : attachments.entrySet()) {
      this.serializeItemAttachment(writer, entry.getValue());
    }
    writer.writeEndElement();
  }

  private void serializeItemMetadatas(
    final XMLStreamWriter writer,
    final SortedMap<String, CAItemMetadata> metadata)
    throws XMLStreamException
  {
    writer.writeStartElement(NAMESPACE, "ItemMetadatas");
    this.writeNamespaceIfRequired(writer, NAMESPACE);

    for (final var entry : metadata.entrySet()) {
      this.serializeItemMetadata(writer, entry.getValue());
    }

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
    writer.writeStartElement(NAMESPACE, "Items");
    this.writeNamespaceIfRequired(writer, NAMESPACE);

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
    writer.writeEmptyElement(NAMESPACE, "Tag");
    this.writeNamespaceIfRequired(writer, NAMESPACE);
    writer.writeAttribute("id", tag.id().toString());
    writer.writeAttribute("name", tag.name());
  }

  private void serializeTags(
    final XMLStreamWriter writer,
    final CATags tags)
    throws XMLStreamException
  {
    writer.writeStartElement(NAMESPACE, "Tags");
    this.writeNamespaceIfRequired(writer, NAMESPACE);

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
    writer.setPrefix("ci", NAMESPACE);
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
      } else if (value instanceof CAItemAttachment itemAttachment) {
        this.serializeItemAttachment(writer, itemAttachment);
      } else if (value instanceof CAItemMetadata itemMetadata) {
        this.serializeItemMetadata(writer, itemMetadata);
      } else if (value instanceof CAItemMetadatas itemMetadatas) {
        this.serializeItemMetadatas(writer, itemMetadatas.metadatas());
      } else {
        throw new IllegalStateException("Unrecognized message: " + value);
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
