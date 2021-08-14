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

package com.io7m.cardant.protocol.inventory.v1.internal;

import com.io7m.anethum.common.SerializeException;
import com.io7m.cardant.model.xml.CAInventorySerializerFactoryType;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageSerializerType;
import com.io7m.cardant.protocol.inventory.v1.CA1InventorySchemas;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemAttachmentPut;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemAttachmentRemove;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemCreate;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemList;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemMetadataPut;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemMetadataRemove;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemRemove;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemReposit;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemUpdate;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandLocationList;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandLocationPut;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandLoginUsernamePassword;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandTagList;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandTagsDelete;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandTagsPut;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryCommandType;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryMessageType;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryTransaction;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1ResponseError;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1ResponseErrorDetail;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1ResponseOK;
import org.apache.commons.io.output.CloseShieldOutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Objects;

/**
 * A serializer.
 */

public final class CA1InventoryMessageSerializer
  implements CA1InventoryMessageSerializerType
{
  private static final String PROTO_NAMESPACE =
    CA1InventorySchemas.protocol1Namespace().toString();

  private final CAInventorySerializerFactoryType serializers;
  private final URI target;
  private final OutputStream stream;
  private boolean namespace;

  /**
   * Construct a serializer.
   *
   * @param inSerializers The inventory serializers
   * @param inTarget      The target
   * @param inStream      The output stream
   */

  public CA1InventoryMessageSerializer(
    final CAInventorySerializerFactoryType inSerializers,
    final URI inTarget,
    final OutputStream inStream)
  {
    this.serializers =
      Objects.requireNonNull(inSerializers, "inSerializers");
    this.target =
      Objects.requireNonNull(inTarget, "target");
    this.stream =
      Objects.requireNonNull(inStream, "stream");
    this.namespace = false;
  }

  private void writeResponseOK(
    final XMLStreamWriter writer,
    final CA1ResponseOK ok)
    throws XMLStreamException, SerializeException, IOException
  {
    writer.writeStartElement(PROTO_NAMESPACE, "ResponseOK");
    this.writeNamespaceIfNecessary(writer, PROTO_NAMESPACE);
    writer.writeCharacters("\n");
    writer.flush();
    this.stream.flush();

    final var dataOpt = ok.data();
    if (dataOpt.isPresent()) {
      final var data = dataOpt.get();
      try (var subOutput = CloseShieldOutputStream.wrap(this.stream)) {
        this.serializers.serialize(this.target, subOutput, data);
      }
    }

    writer.writeEndElement();
  }

  private void writeNamespaceIfNecessary(
    final XMLStreamWriter writer,
    final String namespaceURI)
    throws XMLStreamException
  {
    if (!this.namespace) {
      writer.writeNamespace("ca", namespaceURI);
      this.namespace = true;
    }
  }

  private void writeResponseError(
    final XMLStreamWriter writer,
    final CA1ResponseError error)
    throws XMLStreamException
  {
    writer.writeStartElement(PROTO_NAMESPACE, "ResponseError");
    this.writeNamespaceIfNecessary(writer, PROTO_NAMESPACE);
    writer.writeAttribute("status", String.valueOf(error.status()));
    writer.writeAttribute("message", error.message());
    for (final var detail : error.details()) {
      this.writeResponseErrorDetail(writer, detail);
    }
    writer.writeEndElement();
  }

  private void writeResponseErrorDetail(
    final XMLStreamWriter writer,
    final CA1ResponseErrorDetail detail)
    throws XMLStreamException
  {
    writer.writeEmptyElement(PROTO_NAMESPACE, "ResponseErrorDetail");
    this.writeNamespaceIfNecessary(writer, PROTO_NAMESPACE);
    writer.writeAttribute("message", detail.message());
  }

  private void writeCommandLoginUsernamePassword(
    final XMLStreamWriter writer,
    final CA1CommandLoginUsernamePassword creds)
    throws XMLStreamException
  {
    writer.writeEmptyElement(PROTO_NAMESPACE, "CommandLoginUsernamePassword");
    this.writeNamespaceIfNecessary(writer, PROTO_NAMESPACE);
    writer.writeAttribute("user", creds.user());
    writer.writeAttribute("password", creds.password());
  }

  private void writeCommandTagList(
    final XMLStreamWriter writer,
    final CA1CommandTagList tagList)
    throws XMLStreamException
  {
    writer.writeEmptyElement(PROTO_NAMESPACE, "CommandTagList");
    this.writeNamespaceIfNecessary(writer, PROTO_NAMESPACE);
  }

  private void writeCommandTagsPut(
    final XMLStreamWriter writer,
    final CA1CommandTagsPut tagsPut)
    throws XMLStreamException, IOException, SerializeException
  {
    writer.writeStartElement(PROTO_NAMESPACE, "CommandTagsPut");
    this.writeNamespaceIfNecessary(writer, PROTO_NAMESPACE);
    writer.writeCharacters("\n");
    writer.flush();
    this.stream.flush();

    try (var subOutput = CloseShieldOutputStream.wrap(this.stream)) {
      this.serializers.serialize(this.target, subOutput, tagsPut.tags());
    }

    writer.writeEndElement();
  }

  private void writeCommandTagsDelete(
    final XMLStreamWriter writer,
    final CA1CommandTagsDelete tagsDelete)
    throws XMLStreamException, IOException, SerializeException
  {
    writer.writeStartElement(PROTO_NAMESPACE, "CommandTagsDelete");
    this.writeNamespaceIfNecessary(writer, PROTO_NAMESPACE);
    writer.writeCharacters("\n");
    writer.flush();
    this.stream.flush();

    try (var subOutput = CloseShieldOutputStream.wrap(this.stream)) {
      this.serializers.serialize(this.target, subOutput, tagsDelete.tags());
    }

    writer.writeEndElement();
  }

  private void writeCommandItemList(
    final XMLStreamWriter writer,
    final CA1CommandItemList itemList)
    throws XMLStreamException
  {
    writer.writeEmptyElement(PROTO_NAMESPACE, "CommandItemList");
    this.writeNamespaceIfNecessary(writer, PROTO_NAMESPACE);
  }

  private void writeCommandItemCreate(
    final XMLStreamWriter writer,
    final CA1CommandItemCreate itemCreate)
    throws XMLStreamException
  {
    writer.writeEmptyElement(PROTO_NAMESPACE, "CommandItemCreate");
    this.writeNamespaceIfNecessary(writer, PROTO_NAMESPACE);
    writer.writeAttribute("id", itemCreate.id().id().toString());
    writer.writeAttribute("name", itemCreate.name());
  }

  private void writeCommandItemUpdate(
    final XMLStreamWriter writer,
    final CA1CommandItemUpdate itemUpdate)
    throws XMLStreamException
  {
    writer.writeEmptyElement(PROTO_NAMESPACE, "CommandItemUpdate");
    this.writeNamespaceIfNecessary(writer, PROTO_NAMESPACE);
    writer.writeAttribute("id", itemUpdate.id().id().toString());
    writer.writeAttribute("name", itemUpdate.name());
  }

  private void writeCommandItemRemove(
    final XMLStreamWriter writer,
    final CA1CommandItemRemove itemRemove)
    throws XMLStreamException
  {
    writer.writeEmptyElement(PROTO_NAMESPACE, "CommandItemRemove");
    this.writeNamespaceIfNecessary(writer, PROTO_NAMESPACE);
    writer.writeAttribute("id", itemRemove.id().id().toString());
  }

  private void writeCommandLocationPut(
    final XMLStreamWriter writer,
    final CA1CommandLocationPut cmd)
    throws XMLStreamException, IOException, SerializeException
  {
    writer.writeStartElement(PROTO_NAMESPACE, "CommandLocationPut");
    this.writeNamespaceIfNecessary(writer, PROTO_NAMESPACE);

    writer.writeCharacters("\n");
    writer.flush();
    this.stream.flush();

    try (var subOutput = CloseShieldOutputStream.wrap(this.stream)) {
      this.serializers.serialize(this.target, subOutput, cmd.location());
    }

    writer.writeEndElement();
  }

  private void writeCommandLocationList(
    final XMLStreamWriter writer,
    final CA1CommandLocationList locationList)
    throws XMLStreamException
  {
    writer.writeEmptyElement(PROTO_NAMESPACE, "CommandLocationList");
    this.writeNamespaceIfNecessary(writer, PROTO_NAMESPACE);
  }

  private void writeCommandItemAttachmentPut(
    final XMLStreamWriter writer,
    final CA1CommandItemAttachmentPut itemAttachmentPut)
    throws XMLStreamException, IOException, SerializeException
  {
    writer.writeStartElement(PROTO_NAMESPACE, "CommandItemAttachmentPut");
    this.writeNamespaceIfNecessary(writer, PROTO_NAMESPACE);
    writer.writeAttribute(
      "item",
      itemAttachmentPut.attachment().itemId().id().toString());
    writer.writeCharacters("\n");
    writer.flush();
    this.stream.flush();

    try (var subOutput = CloseShieldOutputStream.wrap(this.stream)) {
      this.serializers.serialize(
        this.target,
        subOutput,
        itemAttachmentPut.attachment());
    }

    writer.writeEndElement();
  }

  private void writeCommandItemMetadataPut(
    final XMLStreamWriter writer,
    final CA1CommandItemMetadataPut itemMetadataPut)
    throws XMLStreamException, IOException, SerializeException
  {
    writer.writeStartElement(PROTO_NAMESPACE, "CommandItemMetadataPut");
    this.writeNamespaceIfNecessary(writer, PROTO_NAMESPACE);
    writer.writeAttribute(
      "item",
      itemMetadataPut.itemID().id().toString());
    writer.writeCharacters("\n");
    writer.flush();
    this.stream.flush();

    try (var subOutput = CloseShieldOutputStream.wrap(this.stream)) {
      this.serializers.serialize(
        this.target,
        subOutput,
        itemMetadataPut.metadatas());
    }

    writer.writeEndElement();
  }

  private void writeCommandItemMetadataRemove(
    final XMLStreamWriter writer,
    final CA1CommandItemMetadataRemove itemMetadataRemove)
    throws XMLStreamException, IOException, SerializeException
  {
    writer.writeStartElement(PROTO_NAMESPACE, "CommandItemMetadataRemove");
    this.writeNamespaceIfNecessary(writer, PROTO_NAMESPACE);
    writer.writeAttribute(
      "item",
      itemMetadataRemove.itemID().id().toString());
    writer.writeCharacters("\n");
    writer.flush();
    this.stream.flush();

    try (var subOutput = CloseShieldOutputStream.wrap(this.stream)) {
      this.serializers.serialize(
        this.target,
        subOutput,
        itemMetadataRemove.metadatas());
    }

    writer.writeEndElement();
  }

  private void writeCommandItemAttachmentRemove(
    final XMLStreamWriter writer,
    final CA1CommandItemAttachmentRemove itemAttachmentRemove)
    throws XMLStreamException
  {
    writer.writeEmptyElement(PROTO_NAMESPACE, "CommandItemAttachmentRemove");
    this.writeNamespaceIfNecessary(writer, PROTO_NAMESPACE);
    writer.writeAttribute(
      "attachment",
      itemAttachmentRemove.attachment().id().toString());
  }

  private void writeCommandItemReposit(
    final XMLStreamWriter writer,
    final CA1CommandItemReposit itemReposit)
    throws SerializeException, XMLStreamException, IOException
  {
    writer.writeStartElement(PROTO_NAMESPACE, "CommandItemReposit");
    this.writeNamespaceIfNecessary(writer, PROTO_NAMESPACE);
    writer.writeCharacters("\n");
    writer.flush();
    this.stream.flush();

    try (var subOutput = CloseShieldOutputStream.wrap(this.stream)) {
      this.serializers.serialize(this.target, subOutput, itemReposit.reposit());
    }

    writer.writeEndElement();
  }

  private void writeTransaction(
    final XMLStreamWriter writer,
    final CA1InventoryTransaction transaction)
    throws XMLStreamException, SerializeException, IOException
  {
    writer.writeStartElement(PROTO_NAMESPACE, "Transaction");
    this.writeNamespaceIfNecessary(writer, PROTO_NAMESPACE);
    for (final var command : transaction.commands()) {
      this.writeCommand(writer, command);
    }
    writer.writeEndElement();
  }

  private void writeCommand(
    final XMLStreamWriter writer,
    final CA1InventoryCommandType command)
    throws XMLStreamException, IOException, SerializeException
  {
    if (command instanceof CA1CommandLoginUsernamePassword creds) {
      this.writeCommandLoginUsernamePassword(writer, creds);
    } else if (command instanceof CA1CommandTagList tagList) {
      this.writeCommandTagList(writer, tagList);
    } else if (command instanceof CA1CommandTagsPut tagsPut) {
      this.writeCommandTagsPut(writer, tagsPut);
    } else if (command instanceof CA1CommandTagsDelete tagsDelete) {
      this.writeCommandTagsDelete(writer, tagsDelete);
    } else if (command instanceof CA1CommandItemList itemList) {
      this.writeCommandItemList(writer, itemList);
    } else if (command instanceof CA1CommandItemCreate itemCreate) {
      this.writeCommandItemCreate(writer, itemCreate);
    } else if (command instanceof CA1CommandItemUpdate itemUpdate) {
      this.writeCommandItemUpdate(writer, itemUpdate);
    } else if (command instanceof CA1CommandItemRemove itemRemove) {
      this.writeCommandItemRemove(writer, itemRemove);
    } else if (command instanceof CA1CommandItemAttachmentPut itemAttachmentPut) {
      this.writeCommandItemAttachmentPut(writer, itemAttachmentPut);
    } else if (command instanceof CA1CommandItemMetadataPut itemMetadataPut) {
      this.writeCommandItemMetadataPut(writer, itemMetadataPut);
    } else if (command instanceof CA1CommandItemMetadataRemove itemMetadataRemove) {
      this.writeCommandItemMetadataRemove(writer, itemMetadataRemove);
    } else if (command instanceof CA1CommandItemAttachmentRemove itemAttachmentRemove) {
      this.writeCommandItemAttachmentRemove(writer, itemAttachmentRemove);
    } else if (command instanceof CA1CommandLocationPut locationPut) {
      this.writeCommandLocationPut(writer, locationPut);
    } else if (command instanceof CA1CommandLocationList locationList) {
      this.writeCommandLocationList(writer, locationList);
    } else if (command instanceof CA1CommandItemReposit itemReposit) {
      this.writeCommandItemReposit(writer, itemReposit);
    } else {
      throw new IllegalStateException("Unexpected command: " + command);
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
    writer.setPrefix("ca", PROTO_NAMESPACE);
  }

  @Override
  public void execute(final CA1InventoryMessageType message)
    throws SerializeException
  {
    Objects.requireNonNull(message, "message");

    final var writers =
      XMLOutputFactory.newFactory();

    try {
      final var writer =
        writers.createXMLStreamWriter(this.stream);

      this.start(writer);

      if (message instanceof CA1InventoryCommandType command) {
        this.writeCommand(writer, command);
      } else if (message instanceof CA1ResponseError error) {
        this.writeResponseError(writer, error);
      } else if (message instanceof CA1ResponseOK ok) {
        this.writeResponseOK(writer, ok);
      } else if (message instanceof CA1InventoryTransaction transaction) {
        this.writeTransaction(writer, transaction);
      } else {
        throw new IllegalStateException("Unexpected message: " + message);
      }

      this.finish(writer);
    } catch (final XMLStreamException | IOException e) {
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
