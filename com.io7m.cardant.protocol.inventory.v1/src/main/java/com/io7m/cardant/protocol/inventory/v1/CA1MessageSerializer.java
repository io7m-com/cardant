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

package com.io7m.cardant.protocol.inventory.v1;

import com.io7m.anethum.common.SerializeException;
import com.io7m.cardant.model.CAIdType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemAttachmentID;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAListLocationBehaviourType;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.protocol.inventory.api.CACommandType;
import com.io7m.cardant.protocol.inventory.api.CAEventType;
import com.io7m.cardant.protocol.inventory.api.CAMessageSerializerType;
import com.io7m.cardant.protocol.inventory.api.CAMessageType;
import com.io7m.cardant.protocol.inventory.api.CAResponseType;
import com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemUpdate;
import com.io7m.cardant.protocol.inventory.api.CATransaction;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemCreateDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemUpdateDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.IDType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentIDType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemAttachmentsType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemIDType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemType;
import com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType;
import com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType;
import com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType;
import com.io7m.cardant.protocol.inventory.v1.beans.LocationIDType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentPutDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentRemoveDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagListDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.TagIDType;
import com.io7m.cardant.protocol.inventory.v1.beans.TagType;
import com.io7m.cardant.protocol.inventory.v1.beans.TagsType;
import com.io7m.cardant.protocol.inventory.v1.beans.TransactionDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.UserIDType;
import com.io7m.junreachable.UnreachableCodeException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlTokenSource;

import javax.xml.namespace.QName;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationExact;
import static com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationWithDescendants;
import static com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationsAll;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemAttachmentPut;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemAttachmentRemove;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemCreate;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemGet;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemList;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemMetadataPut;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemMetadataRemove;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemRemove;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemUpdate;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandLoginUsernamePassword;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandTagList;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandTagsDelete;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandTagsPut;
import static com.io7m.cardant.protocol.inventory.api.CAEventType.CAEventUpdated;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseError;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemAttachmentPut;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemAttachmentRemove;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemCreate;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemGet;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemList;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemMetadataPut;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemMetadataRemove;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemRemove;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseLoginUsernamePassword;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseTagList;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseTagsDelete;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseTagsPut;

public final class CA1MessageSerializer
  implements CAMessageSerializerType
{
  private static final Pattern REMOVE_URN_XMLNS =
    Pattern.compile(" xmlns:urn=\"urn:com.io7m.cardant.inventory:1\"");

  private final URI target;
  private final OutputStream stream;
  private final XmlOptions options;

  public CA1MessageSerializer(
    final URI inTarget,
    final OutputStream inStream)
  {
    this.target = Objects.requireNonNull(inTarget, "target");
    this.stream = Objects.requireNonNull(inStream, "stream");

    final var suggestedPrefixes =
      Map.ofEntries(
        Map.entry("urn:com.io7m.cardant.inventory:1", "i")
      );

    this.options = new XmlOptions();
    this.options.setSaveSuggestedPrefixes(suggestedPrefixes);
    this.options.setSaveAggressiveNamespaces(true);
    this.options.setSaveNamespacesFirst(true);
    this.options.setSavePrettyPrintIndent(2);
    this.options.setSavePrettyPrint(true);
  }

  @Override
  public void execute(
    final CAMessageType value)
    throws SerializeException
  {
    try {
      final var transformed = this.transform(value);

      try (var byteOutput = new ByteArrayOutputStream()) {
        transformed.save(byteOutput, this.options);
        byteOutput.flush();

        /*
         * Remove xmlns declarations erroneously inserted by XMLBeans.
         */

        final var text = byteOutput.toString(StandardCharsets.UTF_8);
        this.stream.write(
          REMOVE_URN_XMLNS.matcher(text)
            .replaceAll("")
            .getBytes(StandardCharsets.UTF_8)
        );
        this.stream.flush();
      }
    } catch (final IOException e) {
      throw new SerializeException(e.getMessage(), e);
    }
  }

  private XmlObject transform(
    final CAMessageType value)
    throws IOException
  {
    if (value instanceof CAResponseType response) {
      return this.transformResponse(response);
    }
    if (value instanceof CACommandType command) {
      return this.transformCommand(command);
    }
    if (value instanceof CAEventType event) {
      return this.transformEvent(event);
    }
    if (value instanceof CATransaction transaction) {
      return this.transformTransaction(transaction);
    }

    throw new UnreachableCodeException();
  }

  private XmlObject transformTransaction(
    final CATransaction transaction)
  {
    final var document =
      TransactionDocument.Factory.newInstance(this.options);
    final var transactionItem =
      document.addNewTransaction();
    final var commandList =
      transactionItem.getCommandList();

    for (final var command : transaction.commands()) {
      final var serialized =
        this.transformCommand(command).getCommand();
      commandList.add(serialized);
    }

    fixCorrectElementNames(commandList);
    fixCorrectPrefixesAndUnusedAttributes(document);
    return document;
  }

  private static <T extends XmlObject>
  void fixCorrectElementNames(
    final Iterable<T> commandList)
  {
    /*
     * Use explicit element names rather than the abstract element name
     * and an xsi:type attribute.
     */

    for (final var command : commandList) {
      final var type = command.schemaType();
      final var name = type.getName();
      final var nameElement = new QName(
        name.getNamespaceURI(),
        name.getLocalPart().replaceAll("Type$", ""),
        "i"
      );

      final var cursor = command.newCursor();
      try {
        cursor.setName(nameElement);
      } finally {
        cursor.dispose();
      }
    }
  }

  private static void fixCorrectPrefixesAndUnusedAttributes(
    final XmlTokenSource document)
  {
    /*
     * Use consistent prefixes everywhere, and remove all xsi:type
     * attributes.
     */

    final var cursorStart = document.newCursor();
    final var cursor = document.newCursor();

    try {
      while (true) {
        final var token = cursor.toNextToken();
        if (cursor.isEnddoc()) {
          break;
        }
        if (cursor.isStart()) {
          cursorStart.toCursor(cursor);
          final var name = cursor.getName();
          final var newName =
            new QName(name.getNamespaceURI(), name.getLocalPart(), "i");
          cursor.setName(newName);
        }
        if (cursor.isAttr()) {
          final var name = cursor.getName();
          if (Objects.equals(name.getPrefix(), "xsi")) {
            cursorStart.removeAttribute(name);
          }
        }
      }
    } finally {
      cursor.dispose();
      cursorStart.dispose();
    }
  }

  private XmlObject transformEvent(
    final CAEventType event)
  {
    if (event instanceof CAEventUpdated e) {
      return this.transformEventUpdated(e);
    }

    throw new UnreachableCodeException();
  }

  private XmlObject transformEventUpdated(
    final CAEventUpdated e)
  {
    final var document =
      EventUpdatedDocument.Factory.newInstance(this.options);
    final var event =
      document.addNewEventUpdated();

    final var updateIds =
      event.addNewUpdated().getIDList();

    for (final var id : e.updated()) {
      updateIds.add(this.transformId(id));
    }

    final var removeIds =
      event.addNewRemoved().getIDList();

    for (final var id : e.removed()) {
      removeIds.add(this.transformId(id));
    }

    fixCorrectElementNames(updateIds);
    fixCorrectElementNames(removeIds);
    fixCorrectPrefixesAndUnusedAttributes(document);
    return document;
  }

  private IDType transformId(
    final CAIdType id)
  {
    if (id instanceof CAItemID inId) {
      final var result =
        ItemIDType.Factory.newInstance(this.options);
      result.setValue(inId.id().toString());
      return result;
    }

    if (id instanceof CAItemAttachmentID inId) {
      final var result =
        ItemAttachmentIDType.Factory.newInstance(this.options);
      result.setValue(inId.id().toString());
      return result;
    }

    if (id instanceof CALocationID inId) {
      final var result =
        LocationIDType.Factory.newInstance(this.options);
      result.setValue(inId.id().toString());
      return result;
    }

    if (id instanceof CATagID inId) {
      final var result =
        TagIDType.Factory.newInstance(this.options);
      result.setValue(inId.id().toString());
      return result;
    }

    if (id instanceof CAUserID inId) {
      final var result =
        UserIDType.Factory.newInstance(this.options);
      result.setValue(inId.id().toString());
      return result;
    }

    throw new UnreachableCodeException();
  }

  private CommandDocument transformCommand(
    final CACommandType command)
  {
    if (command instanceof CACommandLoginUsernamePassword c) {
      return this.transformCommandLoginUsernamePassword(c);
    }
    if (command instanceof CACommandItemList c) {
      return this.transformCommandItemList(c);
    }
    if (command instanceof CACommandItemCreate c) {
      return this.transformCommandItemCreate(c);
    }
    if (command instanceof CACommandItemGet c) {
      return this.transformCommandItemGet(c);
    }
    if (command instanceof CACommandItemRemove c) {
      return this.transformCommandItemRemove(c);
    }
    if (command instanceof CACommandItemAttachmentPut c) {
      return this.transformCommandItemAttachmentPut(c);
    }
    if (command instanceof CACommandItemAttachmentRemove c) {
      return this.transformCommandItemAttachmentRemove(c);
    }
    if (command instanceof CACommandItemMetadataPut c) {
      return this.transformCommandItemMetadataPut(c);
    }
    if (command instanceof CACommandItemMetadataRemove c) {
      return this.transformCommandItemMetadataRemove(c);
    }
    if (command instanceof CACommandTagList c) {
      return this.transformCommandTagList(c);
    }
    if (command instanceof CACommandTagsDelete c) {
      return this.transformCommandTagsDelete(c);
    }
    if (command instanceof CACommandTagsPut c) {
      return this.transformCommandTagsPut(c);
    }
    if (command instanceof CACommandItemUpdate c) {
      return this.transformCommandItemUpdate(c);
    }

    throw new UnreachableCodeException();
  }

  private CommandDocument transformCommandTagsPut(
    final CACommandTagsPut c)
  {
    final var document =
      CommandTagsPutDocument.Factory.newInstance(this.options);
    final var command =
      document.addNewCommandTagsPut();

    command.setTags(this.transformTags(c.tags().tags()));
    return document;
  }

  private CommandDocument transformCommandTagsDelete(
    final CACommandTagsDelete c)
  {
    final var document =
      CommandTagsDeleteDocument.Factory.newInstance(this.options);
    final var command =
      document.addNewCommandTagsDelete();

    command.setTags(this.transformTags(c.tags().tags()));
    return document;
  }

  private CommandDocument transformCommandTagList(
    final CACommandTagList c)
  {
    final var document =
      CommandTagListDocument.Factory.newInstance(this.options);
    final var command =
      document.addNewCommandTagList();
    return document;
  }

  private CommandDocument transformCommandItemCreate(
    final CACommandItemCreate c)
  {
    final var document =
      CommandItemCreateDocument.Factory.newInstance(this.options);
    final var command =
      document.addNewCommandItemCreate();

    command.setId(c.id().id().toString());
    command.setName(c.name());
    return document;
  }

  private CommandDocument transformCommandItemUpdate(
    final CACommandItemUpdate c)
  {
    final var document =
      CommandItemUpdateDocument.Factory.newInstance(this.options);
    final var command =
      document.addNewCommandItemUpdate();

    command.setId(c.id().id().toString());
    command.setName(c.name());
    return document;
  }

  private CommandDocument transformCommandItemGet(
    final CACommandItemGet c)
  {
    final var document =
      CommandItemGetDocument.Factory.newInstance(this.options);
    final var command =
      document.addNewCommandItemGet();

    command.setId(c.id().id().toString());
    return document;
  }

  private CommandDocument transformCommandItemRemove(
    final CACommandItemRemove c)
  {
    final var document =
      CommandItemRemoveDocument.Factory.newInstance(this.options);
    final var command =
      document.addNewCommandItemRemove();

    command.setId(c.id().id().toString());
    return document;
  }

  private CommandDocument transformCommandItemAttachmentPut(
    final CACommandItemAttachmentPut c)
  {
    final var document =
      CommandItemAttachmentPutDocument.Factory.newInstance(this.options);
    final var command =
      document.addNewCommandItemAttachmentPut();

    command.setItem(c.item().id().toString());
    command.setItemAttachment(this.transformItemAttachment(c.attachment()));
    return document;
  }

  private CommandDocument transformCommandItemAttachmentRemove(
    final CACommandItemAttachmentRemove c)
  {
    final var document =
      CommandItemAttachmentRemoveDocument.Factory.newInstance(this.options);
    final var command =
      document.addNewCommandItemAttachmentRemove();

    command.setItem(c.item().id().toString());
    command.setAttachment(c.attachmentID().id().toString());
    return document;
  }

  private CommandDocument transformCommandItemMetadataPut(
    final CACommandItemMetadataPut c)
  {
    final var document =
      CommandItemMetadataPutDocument.Factory.newInstance(this.options);
    final var command =
      document.addNewCommandItemMetadataPut();

    command.setItem(c.item().id().toString());
    command.setItemMetadatas(this.transformItemMetadatasSet(c.metadatas()));
    return document;
  }

  private CommandDocument transformCommandItemMetadataRemove(
    final CACommandItemMetadataRemove c)
  {
    final var document =
      CommandItemMetadataRemoveDocument.Factory.newInstance(this.options);
    final var command =
      document.addNewCommandItemMetadataRemove();

    command.setItem(c.item().id().toString());
    command.getItemMetadataNameList().addAll(c.metadataNames());
    return document;
  }

  private CommandDocument transformCommandItemList(
    final CACommandItemList c)
  {
    final var document =
      CommandItemListDocument.Factory.newInstance(this.options);
    final var command =
      document.addNewCommandItemList();

    final CAListLocationBehaviourType locationBehaviour = c.locationBehaviour();
    if (locationBehaviour instanceof CAListLocationExact exact) {
      final var location =
        ListLocationExactType.Factory.newInstance(this.options);
      location.setLocation(exact.location().id().toString());
      command.setListLocationExact(location);
    } else if (locationBehaviour instanceof CAListLocationsAll all) {
      final var location =
        ListLocationsAllType.Factory.newInstance(this.options);
      command.setListLocationsAll(location);
    } else if (locationBehaviour instanceof CAListLocationWithDescendants desc) {
      final var location =
        ListLocationWithDescendantsType.Factory.newInstance(this.options);
      location.setLocation(desc.location().id().toString());
      command.setListLocationWithDescendants(location);
    } else {
      throw new UnreachableCodeException();
    }
    return document;
  }

  private CommandDocument transformCommandLoginUsernamePassword(
    final CACommandLoginUsernamePassword c)
  {
    final var document =
      CommandLoginUsernamePasswordDocument.Factory.newInstance(this.options);
    final var command =
      document.addNewCommandLoginUsernamePassword();

    command.setUser(c.user());
    command.setPassword(c.password());
    return document;
  }

  private XmlObject transformResponse(
    final CAResponseType response)
  {
    if (response instanceof CAResponseLoginUsernamePassword r) {
      return this.transformResponseLoginUsernamePassword();
    }
    if (response instanceof CAResponseItemList r) {
      return this.transformResponseItemList(r);
    }
    if (response instanceof CAResponseError r) {
      return this.transformResponseError(r);
    }
    if (response instanceof CAResponseItemAttachmentRemove r) {
      return this.transformItemAttachmentRemove(r);
    }
    if (response instanceof CAResponseItemAttachmentPut r) {
      return this.transformItemAttachmentPut(r);
    }
    if (response instanceof CAResponseItemMetadataRemove r) {
      return this.transformItemMetadataRemove(r);
    }
    if (response instanceof CAResponseItemMetadataPut r) {
      return this.transformItemMetadataPut(r);
    }
    if (response instanceof CAResponseItemCreate r) {
      return this.transformResponseItemCreate(r);
    }
    if (response instanceof CAResponseItemUpdate r) {
      return this.transformResponseItemUpdate(r);
    }
    if (response instanceof CAResponseItemRemove r) {
      return this.transformResponseItemRemove(r);
    }
    if (response instanceof CAResponseItemGet r) {
      return this.transformResponseItemGet(r);
    }
    if (response instanceof CAResponseTagsPut r) {
      return this.transformResponseTagsPut(r);
    }
    if (response instanceof CAResponseTagList r) {
      return this.transformResponseTagList(r);
    }
    if (response instanceof CAResponseTagsDelete r) {
      return this.transformResponseTagsDelete(r);
    }

    throw new UnreachableCodeException();
  }

  private XmlObject transformResponseTagsDelete(
    final CAResponseTagsDelete r)
  {
    final var document =
      ResponseTagsDeleteDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseTagsDelete();

    response.setTags(this.transformTags(r.tags().tags()));
    return document;
  }

  private XmlObject transformResponseTagList(
    final CAResponseTagList r)
  {
    final var document =
      ResponseTagListDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseTagList();

    response.setTags(this.transformTags(r.tags().tags()));
    return document;
  }

  private XmlObject transformResponseTagsPut(
    final CAResponseTagsPut r)
  {
    final var document =
      ResponseTagsPutDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseTagsPut();

    response.setTags(this.transformTags(r.tags().tags()));
    return document;
  }

  private XmlObject transformResponseItemRemove(
    final CAResponseItemRemove r)
  {
    final var document =
      ResponseItemRemoveDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemRemove();

    response.setId(r.id().id().toString());
    return document;
  }

  private XmlObject transformResponseItemCreate(
    final CAResponseItemCreate r)
  {
    final var document =
      ResponseItemCreateDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemCreate();

    response.setItem(this.transformItem(r.item()));
    return document;
  }

  private XmlObject transformResponseItemUpdate(
    final CAResponseItemUpdate r)
  {
    final var document =
      ResponseItemUpdateDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemUpdate();

    response.setItem(this.transformItem(r.item()));
    return document;
  }

  private XmlObject transformResponseItemGet(
    final CAResponseItemGet r)
  {
    final var document =
      ResponseItemGetDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemGet();

    response.setItem(this.transformItem(r.item()));
    return document;
  }

  private XmlObject transformItemAttachmentRemove(
    final CAResponseItemAttachmentRemove r)
  {
    final var document =
      ResponseItemAttachmentRemoveDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemAttachmentRemove();

    response.setItem(this.transformItem(r.item()));
    return document;
  }

  private XmlObject transformItemAttachmentPut(
    final CAResponseItemAttachmentPut r)
  {
    final var document =
      ResponseItemAttachmentPutDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemAttachmentPut();

    response.setItem(this.transformItem(r.item()));
    return document;
  }

  private XmlObject transformItemMetadataRemove(
    final CAResponseItemMetadataRemove r)
  {
    final var document =
      ResponseItemMetadataRemoveDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemMetadataRemove();

    response.setItem(this.transformItem(r.item()));
    return document;
  }

  private XmlObject transformItemMetadataPut(
    final CAResponseItemMetadataPut r)
  {
    final var document =
      ResponseItemMetadataPutDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemMetadataPut();

    response.setItem(this.transformItem(r.item()));
    return document;
  }

  private XmlObject transformResponseError(
    final CAResponseError r)
  {
    final var document =
      ResponseErrorDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseError();

    response.setStatus(BigInteger.valueOf((long) r.status()));
    response.setMessage(r.message());

    final var output =
      response.getResponseErrorDetailList();

    for (final var detail : r.details()) {
      output.add(this.transformErrorDetail(detail));
    }

    return document;
  }

  private ResponseErrorDetailType transformErrorDetail(
    final String detail)
  {
    final var response =
      ResponseErrorDetailType.Factory.newInstance(this.options);
    response.setMessage(detail);
    return response;
  }

  private XmlObject transformResponseItemList(
    final CAResponseItemList r)
  {
    final var document =
      ResponseItemListDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemList();

    final var output = response.getItemList();
    final var items = r.items().items();
    for (final var item : items) {
      output.add(this.transformItem(item));
    }

    return document;
  }

  private ItemType transformItem(
    final CAItem item)
  {
    final var result = ItemType.Factory.newInstance(this.options);
    result.setId(item.id().id().toString());
    result.setName(item.name());
    result.setCount(BigInteger.valueOf(item.count()));
    result.setItemAttachments(this.transformItemAttachments(item.attachments()));
    result.setItemMetadatas(this.transformItemMetadatas(item.metadata()));
    result.setTags(this.transformTags(item.tags()));
    return result;
  }

  private TagsType transformTags(
    final SortedSet<CATag> tags)
  {
    final var result =
      TagsType.Factory.newInstance(this.options);
    final var output =
      result.getTagList();

    for (final var tag : tags) {
      output.add(this.transformTag(tag));
    }
    return result;
  }

  private TagType transformTag(
    final CATag tag)
  {
    final var result = TagType.Factory.newInstance(this.options);
    result.setName(tag.name());
    result.setId(tag.id().id().toString());
    return result;
  }

  private ItemMetadatasType transformItemMetadatas(
    final Map<String, CAItemMetadata> metadatas)
  {
    final var result =
      ItemMetadatasType.Factory.newInstance(this.options);
    final var output =
      result.getItemMetadataList();

    for (final var metadata : metadatas.values()) {
      output.add(this.transformItemMetadata(metadata));
    }
    return result;
  }

  private ItemMetadatasType transformItemMetadatasSet(
    final Set<CAItemMetadata> metadatas)
  {
    return this.transformItemMetadatas(
      metadatas.stream()
        .collect(Collectors.toMap(CAItemMetadata::name, Function.identity()))
    );
  }

  private ItemMetadataType transformItemMetadata(
    final CAItemMetadata metadata)
  {
    final var result =
      ItemMetadataType.Factory.newInstance(this.options);

    result.setName(metadata.name());
    result.setValue(metadata.value());
    return result;
  }

  private ItemAttachmentsType transformItemAttachments(
    final Map<CAItemAttachmentID, CAItemAttachment> attachments)
  {
    final var result =
      ItemAttachmentsType.Factory.newInstance(this.options);
    final var output =
      result.getItemAttachmentList();

    for (final var attachment : attachments.values()) {
      output.add(this.transformItemAttachment(attachment));
    }
    return result;
  }

  private ItemAttachmentType transformItemAttachment(
    final CAItemAttachment attachment)
  {
    final var result =
      ItemAttachmentType.Factory.newInstance(this.options);

    result.setId(attachment.id().id().toString());
    result.setHashAlgorithm(attachment.hashAlgorithm());
    result.setHashValue(attachment.hashValue());
    result.setMediaType(attachment.mediaType());
    result.setRelation(attachment.relation());
    result.setDescription(attachment.description());
    result.setSize(BigInteger.valueOf(attachment.size()));

    attachment.data()
      .ifPresent(data -> result.setItemAttachmentData(data.data()));

    return result;
  }

  private XmlObject transformResponseLoginUsernamePassword()
  {
    final var document =
      ResponseLoginUsernamePasswordDocument.Factory.newInstance(this.options);

    document.addNewResponseLoginUsernamePassword();
    return document;
  }

  @Override
  public void close()
    throws IOException
  {
    this.stream.close();
  }
}
