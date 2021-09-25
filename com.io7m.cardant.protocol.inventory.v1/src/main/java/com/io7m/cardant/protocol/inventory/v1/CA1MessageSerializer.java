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
import com.io7m.cardant.model.CAItemLocation;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CAItemRepositMove;
import com.io7m.cardant.model.CAItemRepositRemove;
import com.io7m.cardant.model.CAItemRepositType;
import com.io7m.cardant.model.CAListLocationBehaviourType;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.protocol.inventory.api.CACommandType;
import com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemLocationsList;
import com.io7m.cardant.protocol.inventory.api.CAEventType;
import com.io7m.cardant.protocol.inventory.api.CAMessageSerializerType;
import com.io7m.cardant.protocol.inventory.api.CAMessageType;
import com.io7m.cardant.protocol.inventory.api.CAResponseType;
import com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemUpdate;
import com.io7m.cardant.protocol.inventory.api.CATransaction;
import com.io7m.cardant.protocol.inventory.api.CATransactionResponse;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemCreateDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemLocationsListDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRepositDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemUpdateDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationListDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutDocument;
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
import com.io7m.cardant.protocol.inventory.v1.beans.ItemLocationType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadataType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemMetadatasType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositAddType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositMoveType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositRemoveType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemRepositType;
import com.io7m.cardant.protocol.inventory.v1.beans.ItemType;
import com.io7m.cardant.protocol.inventory.v1.beans.ListLocationExactType;
import com.io7m.cardant.protocol.inventory.v1.beans.ListLocationWithDescendantsType;
import com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsAllType;
import com.io7m.cardant.protocol.inventory.v1.beans.LocationIDType;
import com.io7m.cardant.protocol.inventory.v1.beans.LocationType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorAttributeType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentPutDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentRemoveDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemLocationsListDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRepositDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagListDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.TagIDType;
import com.io7m.cardant.protocol.inventory.v1.beans.TagType;
import com.io7m.cardant.protocol.inventory.v1.beans.TagsType;
import com.io7m.cardant.protocol.inventory.v1.beans.TransactionDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.TransactionResponseDocument;
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
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemReposit;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemUpdate;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandLocationList;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandLocationPut;
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
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemLocationsList;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemMetadataPut;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemMetadataRemove;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemRemove;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemReposit;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseLocationList;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseLocationPut;
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
    if (value instanceof CATransactionResponse transactionResponse) {
      return this.transformTransactionResponse(transactionResponse);
    }

    throw new UnreachableCodeException();
  }

  private XmlObject transformTransactionResponse(
    final CATransactionResponse transactionResponse)
  {
    final var document =
      TransactionResponseDocument.Factory.newInstance(this.options);

    final var transactionItem =
      document.addNewTransactionResponse();
    final var responseList =
      transactionItem.getResponseList();

    transactionItem.setFailed(transactionResponse.failed());
    for (final var response : transactionResponse.responses()) {
      final var serialized =
        this.transformResponse(response);
      responseList.add(serialized.getResponse());
    }

    fixCorrectElementNames(responseList);
    fixCorrectPrefixesAndUnusedAttributes(document);
    return document;
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
    final Iterable<T> elements)
  {
    /*
     * Use explicit element names rather than the abstract element name
     * and an xsi:type attribute.
     */

    for (final var element : elements) {
      fixCorrectElementName(element);
    }
  }

  private static <T extends XmlObject>
  void fixCorrectElementName(
    final T element)
  {
    final var type = element.schemaType();
    final var name = type.getName();
    final var nameElement = new QName(
      name.getNamespaceURI(),
      name.getLocalPart().replaceAll("Type$", ""),
      "i"
    );

    final var cursor = element.newCursor();
    try {
      cursor.setName(nameElement);
    } finally {
      cursor.dispose();
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
    if (command instanceof CACommandLocationPut c) {
      return this.transformCommandLocationPut(c);
    }
    if (command instanceof CACommandLocationList c) {
      return this.transformCommandLocationList(c);
    }
    if (command instanceof CACommandItemReposit c) {
      return this.transformCommandItemReposit(c);
    }
    if (command instanceof CACommandItemLocationsList c) {
      return this.transformCommandItemLocationsList(c);
    }

    throw new UnreachableCodeException();
  }

  private CommandDocument transformCommandItemLocationsList(
    final CACommandItemLocationsList c)
  {
    final var document =
      CommandItemLocationsListDocument.Factory.newInstance(this.options);
    final var command =
      document.addNewCommandItemLocationsList();

    command.setItem(c.item().displayId());
    return document;
  }

  private CommandDocument transformCommandItemReposit(
    final CACommandItemReposit c)
  {
    final var document =
      CommandItemRepositDocument.Factory.newInstance(this.options);
    final var command =
      document.addNewCommandItemReposit();

    command.setItemReposit(this.transformItemReposit(c.reposit()));

    fixCorrectElementName(command.getItemReposit());
    fixCorrectPrefixesAndUnusedAttributes(document);
    return document;
  }

  private ItemRepositType transformItemReposit(
    final CAItemRepositType reposit)
  {
    if (reposit instanceof CAItemRepositAdd add) {
      final var result =
        ItemRepositAddType.Factory.newInstance(this.options);
      result.setItem(add.item().id().toString());
      result.setLocation(add.location().id().toString());
      result.setCount(new BigInteger(Long.toUnsignedString(add.count())));
      return result;
    }

    if (reposit instanceof CAItemRepositRemove remove) {
      final var result =
        ItemRepositRemoveType.Factory.newInstance(this.options);
      result.setItem(remove.item().id().toString());
      result.setLocation(remove.location().id().toString());
      result.setCount(new BigInteger(Long.toUnsignedString(reposit.count())));
      return result;
    }

    if (reposit instanceof CAItemRepositMove move) {
      final var result =
        ItemRepositMoveType.Factory.newInstance(this.options);
      result.setItem(move.item().id().toString());
      result.setFromLocation(move.fromLocation().id().toString());
      result.setToLocation(move.toLocation().id().toString());
      result.setCount(new BigInteger(Long.toUnsignedString(reposit.count())));
      return result;
    }

    throw new UnreachableCodeException();
  }

  private CommandDocument transformCommandLocationList(
    final CACommandLocationList c)
  {
    final var document =
      CommandLocationListDocument.Factory.newInstance(this.options);
    final var command =
      document.addNewCommandLocationList();

    return document;
  }

  private CommandDocument transformCommandLocationPut(
    final CACommandLocationPut c)
  {
    final var document =
      CommandLocationPutDocument.Factory.newInstance(this.options);
    final var command =
      document.addNewCommandLocationPut();

    command.setLocation(this.transformLocation(c.location()));
    return document;
  }

  private LocationType transformLocation(
    final CALocation location)
  {
    final var result =
      LocationType.Factory.newInstance(this.options);
    result.setId(location.id().id().toString());
    result.setDescription(location.description());
    result.setName(location.name());
    location.parent().ifPresent(locationID -> {
      result.setParent(locationID.id().toString());
    });
    return result;
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

  private ResponseDocument transformResponse(
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
    if (response instanceof CAResponseLocationPut r) {
      return this.transformResponseLocationPut(r);
    }
    if (response instanceof CAResponseLocationList r) {
      return this.transformResponseLocationList(r);
    }
    if (response instanceof CAResponseItemLocationsList r) {
      return this.transformResponseItemLocationsList(r);
    }
    if (response instanceof CAResponseItemReposit r) {
      return this.transformResponseItemReposit(r);
    }

    throw new UnreachableCodeException();
  }

  private ResponseDocument transformResponseItemReposit(
    final CAResponseItemReposit r)
  {
    final var document =
      ResponseItemRepositDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemReposit();

    response.setId(r.data().displayId());
    return document;
  }

  private ResponseDocument transformResponseItemLocationsList(
    final CAResponseItemLocationsList r)
  {
    final var document =
      ResponseItemLocationsListDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemLocationsList();
    final var itemLocations =
      response.getItemLocationList();

    for (final var locationEntry : r.data().itemLocations().entrySet()) {
      final var byItem = locationEntry.getValue();
      for (final var itemEntry : byItem.entrySet()) {
        itemLocations.add(this.transformItemLocation(itemEntry.getValue()));
      }
    }

    return document;
  }

  private ItemLocationType transformItemLocation(
    final CAItemLocation value)
  {
    final var itemLocation =
      ItemLocationType.Factory.newInstance(this.options);

    itemLocation.setItem(value.item().displayId());
    itemLocation.setLocation(value.location().displayId());
    itemLocation.setCount(new BigInteger(Long.toUnsignedString(value.count())));
    return itemLocation;
  }

  private ResponseDocument transformResponseLocationList(
    final CAResponseLocationList r)
  {
    final var document =
      ResponseLocationListDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseLocationList();

    final var locations = response.getLocationList();
    r.data()
      .locations()
      .values()
      .stream()
      .map(this::transformLocation)
      .forEach(locations::add);

    return document;
  }

  private ResponseDocument transformResponseTagsDelete(
    final CAResponseTagsDelete r)
  {
    final var document =
      ResponseTagsDeleteDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseTagsDelete();

    response.setTags(this.transformTags(r.data().tags()));
    return document;
  }

  private ResponseDocument transformResponseTagList(
    final CAResponseTagList r)
  {
    final var document =
      ResponseTagListDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseTagList();

    response.setTags(this.transformTags(r.data().tags()));
    return document;
  }

  private ResponseDocument transformResponseTagsPut(
    final CAResponseTagsPut r)
  {
    final var document =
      ResponseTagsPutDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseTagsPut();

    response.setTags(this.transformTags(r.data().tags()));
    return document;
  }

  private ResponseDocument transformResponseItemRemove(
    final CAResponseItemRemove r)
  {
    final var document =
      ResponseItemRemoveDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemRemove();

    response.setId(r.data().id().toString());
    return document;
  }

  private ResponseDocument transformResponseItemCreate(
    final CAResponseItemCreate r)
  {
    final var document =
      ResponseItemCreateDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemCreate();

    response.setItem(this.transformItem(r.data()));
    return document;
  }

  private ResponseDocument transformResponseItemUpdate(
    final CAResponseItemUpdate r)
  {
    final var document =
      ResponseItemUpdateDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemUpdate();

    response.setItem(this.transformItem(r.data()));
    return document;
  }

  private ResponseDocument transformResponseItemGet(
    final CAResponseItemGet r)
  {
    final var document =
      ResponseItemGetDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemGet();

    response.setItem(this.transformItem(r.data()));
    return document;
  }

  private ResponseDocument transformItemAttachmentRemove(
    final CAResponseItemAttachmentRemove r)
  {
    final var document =
      ResponseItemAttachmentRemoveDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemAttachmentRemove();

    response.setItem(this.transformItem(r.data()));
    return document;
  }

  private ResponseDocument transformItemAttachmentPut(
    final CAResponseItemAttachmentPut r)
  {
    final var document =
      ResponseItemAttachmentPutDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemAttachmentPut();

    response.setItem(this.transformItem(r.data()));
    return document;
  }

  private ResponseDocument transformItemMetadataRemove(
    final CAResponseItemMetadataRemove r)
  {
    final var document =
      ResponseItemMetadataRemoveDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemMetadataRemove();

    response.setItem(this.transformItem(r.data()));
    return document;
  }

  private ResponseDocument transformItemMetadataPut(
    final CAResponseItemMetadataPut r)
  {
    final var document =
      ResponseItemMetadataPutDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemMetadataPut();

    response.setItem(this.transformItem(r.data()));
    return document;
  }

  private ResponseDocument transformResponseError(
    final CAResponseError r)
  {
    final var document =
      ResponseErrorDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseError();

    response.setStatus(BigInteger.valueOf((long) r.statusCode()));
    response.setSummary(r.summary());

    final var errorAttributes =
      response.addNewResponseErrorAttributes();
    final var errorAttributeList =
      errorAttributes.getResponseErrorAttributeList();

    for (final var entry : r.attributes().entrySet()) {
      errorAttributeList.add(
        this.transformErrorAttribute(entry.getKey(), entry.getValue()));
    }

    final var errorDetails =
      response.addNewResponseErrorDetails();
    final var errorDetailList =
      errorDetails.getResponseErrorDetailList();

    for (final var detail : r.details()) {
      errorDetailList.add(this.transformErrorDetail(detail));
    }

    return document;
  }

  private ResponseErrorAttributeType transformErrorAttribute(
    final String key,
    final String value)
  {
    final var attribute =
      ResponseErrorAttributeType.Factory.newInstance(this.options);

    attribute.setName(key);
    attribute.setValue(value);
    return attribute;
  }

  private ResponseErrorDetailType transformErrorDetail(
    final String detail)
  {
    final var response =
      ResponseErrorDetailType.Factory.newInstance(this.options);
    response.setMessage(detail);
    return response;
  }

  private ResponseDocument transformResponseItemList(
    final CAResponseItemList r)
  {
    final var document =
      ResponseItemListDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseItemList();

    final var output = response.getItemList();
    final var items = r.data().items();
    for (final var item : items) {
      output.add(this.transformItem(item));
    }

    return document;
  }

  private ResponseDocument transformResponseLocationPut(
    final CAResponseLocationPut r)
  {
    final var document =
      ResponseLocationPutDocument.Factory.newInstance(this.options);
    final var response =
      document.addNewResponseLocationPut();

    response.setLocation(this.transformLocation(r.data()));
    return document;
  }

  private ItemType transformItem(
    final CAItem item)
  {
    final var result = ItemType.Factory.newInstance(this.options);
    result.setId(item.id().id().toString());
    result.setName(item.name());
    result.setCountTotal(
      new BigInteger(Long.toUnsignedString(item.countTotal())));
    result.setCountHere(
      new BigInteger(Long.toUnsignedString(item.countHere())));
    result.setItemAttachments(
      this.transformItemAttachments(item.attachments()));
    result.setItemMetadatas(
      this.transformItemMetadatas(item.metadata()));
    result.setTags(
      this.transformTags(item.tags()));
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
    result.setId(tag.displayId());
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

  private ResponseDocument transformResponseLoginUsernamePassword()
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
