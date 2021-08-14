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
import com.io7m.cardant.model.CAIdType;
import com.io7m.cardant.model.CAIds;
import com.io7m.cardant.model.CAInventoryElementType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemAttachmentID;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItemMetadatas;
import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CAItemRepositMove;
import com.io7m.cardant.model.CAItemRepositRemove;
import com.io7m.cardant.model.CAItemRepositType;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocations;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;
import com.io7m.cardant.model.CATags;
import com.io7m.cardant.model.CAUserID;
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
    writer.writeAttribute("id", tag.id().id().toString());
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

  private void serializeLocations(
    final XMLStreamWriter writer,
    final CALocations locations)
    throws XMLStreamException
  {
    writer.writeStartElement(NAMESPACE, "Locations");
    this.writeNamespaceIfRequired(writer, NAMESPACE);

    for (final var location : locations.locations().values()) {
      this.serializeLocation(writer, location);
    }
    writer.writeEndElement();
  }

  private void serializeLocation(
    final XMLStreamWriter writer,
    final CALocation location)
    throws XMLStreamException
  {
    writer.writeEmptyElement(NAMESPACE, "Location");
    this.writeNamespaceIfRequired(writer, NAMESPACE);
    writer.writeAttribute("id", location.id().id().toString());
    writer.writeAttribute("name", location.name());
    writer.writeAttribute("description", location.description());
  }

  private void serializeItemReposit(
    final XMLStreamWriter writer,
    final CAItemRepositType reposit)
    throws XMLStreamException
  {
    if (reposit instanceof CAItemRepositAdd add) {
      this.serializeItemRepositAdd(writer, add);
    } else if (reposit instanceof CAItemRepositRemove remove) {
      this.serializeItemRepositRemove(writer, remove);
    } else if (reposit instanceof CAItemRepositMove move) {
      this.serializeItemRepositMove(writer, move);
    } else {
      throw new IllegalStateException("Unexpected message: " + reposit);
    }
  }

  private void serializeItemRepositMove(
    final XMLStreamWriter writer,
    final CAItemRepositMove move)
    throws XMLStreamException
  {
    writer.writeEmptyElement(NAMESPACE, "ItemRepositMove");
    this.writeNamespaceIfRequired(writer, NAMESPACE);
    writer.writeAttribute("item", move.item().id().toString());
    writer.writeAttribute("fromLocation", move.fromLocation().id().toString());
    writer.writeAttribute("toLocation", move.toLocation().id().toString());
    writer.writeAttribute("count", Long.toUnsignedString(move.count()));
  }

  private void serializeItemRepositRemove(
    final XMLStreamWriter writer,
    final CAItemRepositRemove remove)
    throws XMLStreamException
  {
    writer.writeEmptyElement(NAMESPACE, "ItemRepositRemove");
    this.writeNamespaceIfRequired(writer, NAMESPACE);
    writer.writeAttribute("item", remove.item().id().toString());
    writer.writeAttribute("location", remove.location().id().toString());
    writer.writeAttribute("count", Long.toUnsignedString(remove.count()));
  }

  private void serializeItemRepositAdd(
    final XMLStreamWriter writer,
    final CAItemRepositAdd add)
    throws XMLStreamException
  {
    writer.writeEmptyElement(NAMESPACE, "ItemRepositAdd");
    this.writeNamespaceIfRequired(writer, NAMESPACE);
    writer.writeAttribute("item", add.item().id().toString());
    writer.writeAttribute("location", add.location().id().toString());
    writer.writeAttribute("count", Long.toUnsignedString(add.count()));
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
      } else if (value instanceof CALocations locations) {
        this.serializeLocations(writer, locations);
      } else if (value instanceof CALocation location) {
        this.serializeLocation(writer, location);
      } else if (value instanceof CAItemRepositType reposit) {
        this.serializeItemReposit(writer, reposit);
      } else if (value instanceof CAIdType id) {
        this.serializeId(writer, id);
      } else if (value instanceof CAIds ids) {
        this.serializeIds(writer, ids);
      } else {
        throw new IllegalStateException("Unrecognized message: " + value);
      }

      this.finish(writer);
    } catch (final XMLStreamException e) {
      throw new SerializeException(e.getMessage(), e);
    }
  }

  private void serializeIds(
    final XMLStreamWriter writer,
    final CAIds ids)
    throws XMLStreamException
  {
    writer.writeStartElement(NAMESPACE, "IDs");
    this.writeNamespaceIfRequired(writer, NAMESPACE);
    for (final var id : ids.ids()) {
      this.serializeId(writer, id);
    }
    writer.writeEndElement();
  }

  private void serializeId(
    final XMLStreamWriter writer,
    final CAIdType id)
    throws XMLStreamException
  {
    if (id instanceof CAItemID itemID) {
      this.serializeItemId(writer, itemID);
    } else if (id instanceof CAItemAttachmentID itemAttachmentID) {
      this.serializeItemAttachmentId(writer, itemAttachmentID);
    } else if (id instanceof CALocationID locationID) {
      this.serializeLocationId(writer, locationID);
    } else if (id instanceof CATagID tagID) {
      this.serializeTagId(writer, tagID);
    } else if (id instanceof CAUserID userID) {
      this.serializeUserId(writer, userID);
    } else {
      throw new IllegalStateException("Unrecognized message: " + id);
    }
  }

  private void serializeUserId(
    final XMLStreamWriter writer,
    final CAUserID id)
    throws XMLStreamException
  {
    writer.writeEmptyElement(NAMESPACE, "UserID");
    this.writeNamespaceIfRequired(writer, NAMESPACE);
    writer.writeAttribute("value", id.id().toString());
  }

  private void serializeTagId(
    final XMLStreamWriter writer,
    final CATagID id)
    throws XMLStreamException
  {
    writer.writeEmptyElement(NAMESPACE, "TagID");
    this.writeNamespaceIfRequired(writer, NAMESPACE);
    writer.writeAttribute("value", id.id().toString());
  }

  private void serializeLocationId(
    final XMLStreamWriter writer,
    final CALocationID id)
    throws XMLStreamException
  {
    writer.writeEmptyElement(NAMESPACE, "LocationID");
    this.writeNamespaceIfRequired(writer, NAMESPACE);
    writer.writeAttribute("value", id.id().toString());
  }

  private void serializeItemAttachmentId(
    final XMLStreamWriter writer,
    final CAItemAttachmentID id)
    throws XMLStreamException
  {
    writer.writeEmptyElement(NAMESPACE, "ItemAttachmentID");
    this.writeNamespaceIfRequired(writer, NAMESPACE);
    writer.writeAttribute("value", id.id().toString());
  }

  private void serializeItemId(
    final XMLStreamWriter writer,
    final CAItemID id)
    throws XMLStreamException
  {
    writer.writeEmptyElement(NAMESPACE, "ItemID");
    this.writeNamespaceIfRequired(writer, NAMESPACE);
    writer.writeAttribute("value", id.id().toString());
  }

  @Override
  public void close()
    throws IOException
  {
    this.stream.close();
  }
}
