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

import com.io7m.anethum.common.ParseException;
import com.io7m.anethum.common.ParseStatus;
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAIdType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemAttachmentID;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.model.CAListLocationBehaviourType;
import com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationExact;
import com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationWithDescendants;
import com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationsAll;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocations;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;
import com.io7m.cardant.model.CATags;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.protocol.inventory.api.CACommandType;
import com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemList;
import com.io7m.cardant.protocol.inventory.api.CAEventType;
import com.io7m.cardant.protocol.inventory.api.CAMessageParserType;
import com.io7m.cardant.protocol.inventory.api.CAMessageType;
import com.io7m.cardant.protocol.inventory.api.CAResponseType;
import com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemUpdate;
import com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseLoginUsernamePassword;
import com.io7m.cardant.protocol.inventory.api.CATransaction;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentPutType;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemAttachmentRemoveType;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemCreateType;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemGetType;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemListType;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataPutType;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemMetadataRemoveType;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemRemoveType;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandItemUpdateType;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationListType;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandLocationPutType;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandLoginUsernamePasswordType;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandTagListType;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsDeleteType;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandTagsPutType;
import com.io7m.cardant.protocol.inventory.v1.beans.CommandType;
import com.io7m.cardant.protocol.inventory.v1.beans.EventType;
import com.io7m.cardant.protocol.inventory.v1.beans.EventUpdatedType;
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
import com.io7m.cardant.protocol.inventory.v1.beans.ListLocationsBehaviourType;
import com.io7m.cardant.protocol.inventory.v1.beans.LocationIDType;
import com.io7m.cardant.protocol.inventory.v1.beans.LocationType;
import com.io7m.cardant.protocol.inventory.v1.beans.MessageDocument;
import com.io7m.cardant.protocol.inventory.v1.beans.MessageType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorDetailType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseErrorType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentPutType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemAttachmentRemoveType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemCreateType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemGetType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemListType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataPutType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemMetadataRemoveType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemRemoveType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseItemUpdateType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationListType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseLocationPutType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseLoginUsernamePasswordType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagListType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsDeleteType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseTagsPutType;
import com.io7m.cardant.protocol.inventory.v1.beans.ResponseType;
import com.io7m.cardant.protocol.inventory.v1.beans.TagIDType;
import com.io7m.cardant.protocol.inventory.v1.beans.TagType;
import com.io7m.cardant.protocol.inventory.v1.beans.TagsType;
import com.io7m.cardant.protocol.inventory.v1.beans.TransactionType;
import com.io7m.cardant.protocol.inventory.v1.beans.UserIDType;
import com.io7m.jlexing.core.LexicalPosition;
import com.io7m.junreachable.UnreachableCodeException;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.io7m.anethum.common.ParseSeverity.PARSE_ERROR;
import static com.io7m.anethum.common.ParseSeverity.PARSE_WARNING;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemAttachmentPut;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemAttachmentRemove;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemCreate;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemGet;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemMetadataPut;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemMetadataRemove;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemRemove;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemUpdate;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandLocationList;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandLocationPut;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandLoginUsernamePassword;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandTagList;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandTagsDelete;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandTagsPut;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseError;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemAttachmentPut;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemAttachmentRemove;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemCreate;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemGet;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemList;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemMetadataPut;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemMetadataRemove;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseItemRemove;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseLocationList;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseLocationPut;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseTagList;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseTagsDelete;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseTagsPut;
import static org.apache.xmlbeans.XmlError.SEVERITY_ERROR;
import static org.apache.xmlbeans.XmlError.SEVERITY_INFO;
import static org.apache.xmlbeans.XmlError.SEVERITY_WARNING;

public final class CA1MessageParser implements CAMessageParserType
{
  private final SchemaFactory schemas;
  private final URI source;
  private final InputStream stream;
  private final Consumer<ParseStatus> statusConsumer;
  private final ArrayList<ParseStatus> errors;
  private boolean validationFailed;

  public CA1MessageParser(
    final SchemaFactory inSchemas,
    final URI inSource,
    final InputStream inStream,
    final Consumer<ParseStatus> inStatusConsumer)
  {
    this.schemas =
      Objects.requireNonNull(inSchemas, "schemas");
    this.source =
      Objects.requireNonNull(inSource, "source");
    this.stream =
      Objects.requireNonNull(inStream, "stream");
    this.statusConsumer =
      Objects.requireNonNull(inStatusConsumer, "statusConsumer");
    this.errors =
      new ArrayList<>();
    this.validationFailed = false;
  }

  @Override
  public CAMessageType execute()
    throws ParseException
  {
    return this.executeTransformation(this.executeValidation());
  }

  private CAMessageType executeTransformation(
    final byte[] data)
    throws ParseException
  {
    try (var streamCopy = new ByteArrayInputStream(data)) {
      final var options = new XmlOptions();
      options.setBaseURI(this.source);
      options.setLoadLineNumbers(true);
      options.setLoadDTDGrammar(false);
      options.setLoadStripComments(true);
      options.setLoadStripProcinsts(true);
      options.setLoadStripWhitespace(true);
      options.setEntityExpansionLimit(1);

      final var messageDocument =
        MessageDocument.Factory.parse(streamCopy, options);

      return parseMessage(messageDocument.getMessage());
    } catch (final IOException e) {
      this.logException(e);
      throw new ParseException(e.getMessage(), this.errors);
    } catch (final XmlException e) {
      final var errors = e.getErrors();
      if (errors != null) {
        for (final var errorObject : errors) {
          this.logXmlError((XmlError) errorObject);
        }
      }
      throw new ParseException(e.getMessage(), this.errors);
    }
  }

  private static CAMessageType parseMessage(
    final MessageType message)
  {
    if (message instanceof CommandType command) {
      return parseCommand(command);
    }
    if (message instanceof ResponseType response) {
      return parseResponse(response);
    }
    if (message instanceof EventType event) {
      return parseEvent(event);
    }
    if (message instanceof TransactionType transaction) {
      return parseTransaction(transaction);
    }

    throw new IllegalStateException("Unexpected value: " + message);
  }

  private static CAMessageType parseTransaction(
    final TransactionType transaction)
  {
    return new CATransaction(
      transaction.getCommandList()
        .stream()
        .map(CA1MessageParser::parseCommand)
        .collect(Collectors.toList())
    );
  }

  private static CAResponseType parseResponse(
    final ResponseType response)
  {
    if (response instanceof ResponseLoginUsernamePasswordType usernamePassword) {
      return parseResponseLoginUsernamePassword(usernamePassword);
    }
    if (response instanceof ResponseTagsPutType tagsPut) {
      return parseResponseTagsPut(tagsPut);
    }
    if (response instanceof ResponseTagsDeleteType tagsDelete) {
      return parseResponseTagsDelete(tagsDelete);
    }
    if (response instanceof ResponseTagListType tagList) {
      return parseResponseTagList(tagList);
    }
    if (response instanceof ResponseItemListType itemList) {
      return parseResponseItemList(itemList);
    }
    if (response instanceof ResponseItemCreateType itemCreate) {
      return parseResponseItemCreate(itemCreate);
    }
    if (response instanceof ResponseItemUpdateType itemUpdate) {
      return parseResponseItemUpdate(itemUpdate);
    }
    if (response instanceof ResponseItemGetType itemGet) {
      return parseResponseItemGet(itemGet);
    }
    if (response instanceof ResponseItemRemoveType itemRemove) {
      return parseResponseItemRemove(itemRemove);
    }
    if (response instanceof ResponseItemAttachmentPutType itemAttachmentPut) {
      return parseResponseItemAttachmentPut(itemAttachmentPut);
    }
    if (response instanceof ResponseItemAttachmentRemoveType itemAttachmentRemove) {
      return parseResponseItemAttachmentRemove(itemAttachmentRemove);
    }
    if (response instanceof ResponseItemMetadataPutType itemMetadataPut) {
      return parseResponseItemMetadataPut(itemMetadataPut);
    }
    if (response instanceof ResponseItemMetadataRemoveType itemMetadataRemove) {
      return parseResponseItemMetadataRemove(itemMetadataRemove);
    }
    if (response instanceof ResponseErrorType error) {
      return parseResponseError(error);
    }
    if (response instanceof ResponseLocationPutType locationPut) {
      return parseResponseLocationPut(locationPut);
    }
    if (response instanceof ResponseLocationListType locationList) {
      return parseResponseLocationList(locationList);
    }

    throw new IllegalStateException("Unexpected message: " + response);
  }

  private static CAResponseType parseResponseLocationPut(
    final ResponseLocationPutType locationPut)
  {
    return new CAResponseLocationPut(
      parseLocation(locationPut.getLocation())
    );
  }

  private static CAResponseType parseResponseLocationList(
    final ResponseLocationListType locationList)
  {
    return new CAResponseLocationList(
      parseLocations(locationList.getLocationList())
    );
  }

  private static CALocation parseLocation(
    final LocationType location)
  {
    return new CALocation(
      CALocationID.of(location.getId()),
      Optional.ofNullable(location.getParent()).map(CALocationID::of),
      location.getName(),
      location.getDescription()
    );
  }

  private static CAResponseType parseResponseError(
    final ResponseErrorType error)
  {
    return new CAResponseError(
      error.getStatus().intValue(),
      error.getMessage(),
      error.getResponseErrorDetailList()
        .stream()
        .map(ResponseErrorDetailType::getMessage)
        .collect(Collectors.toList())
    );
  }

  private static CAResponseType parseResponseTagList(
    final ResponseTagListType tagList)
  {
    return new CAResponseTagList(
      new CATags(parseTags(tagList.getTags()))
    );
  }

  private static CAResponseType parseResponseTagsDelete(
    final ResponseTagsDeleteType tagsDelete)
  {
    return new CAResponseTagsDelete(
      new CATags(parseTags(tagsDelete.getTags()))
    );
  }

  private static CAResponseType parseResponseTagsPut(
    final ResponseTagsPutType tagsPut)
  {
    return new CAResponseTagsPut(
      new CATags(parseTags(tagsPut.getTags()))
    );
  }

  private static CAResponseType parseResponseItemAttachmentPut(
    final ResponseItemAttachmentPutType itemAttachmentPut)
  {
    return new CAResponseItemAttachmentPut(
      parseItem(itemAttachmentPut.getItem())
    );
  }

  private static CAResponseType parseResponseItemAttachmentRemove(
    final ResponseItemAttachmentRemoveType itemAttachmentRemove)
  {
    return new CAResponseItemAttachmentRemove(
      parseItem(itemAttachmentRemove.getItem())
    );
  }

  private static CAResponseType parseResponseItemMetadataPut(
    final ResponseItemMetadataPutType itemMetadataPut)
  {
    return new CAResponseItemMetadataPut(
      parseItem(itemMetadataPut.getItem())
    );
  }

  private static CAResponseType parseResponseItemMetadataRemove(
    final ResponseItemMetadataRemoveType itemMetadataRemove)
  {
    return new CAResponseItemMetadataRemove(
      parseItem(itemMetadataRemove.getItem())
    );
  }

  private static CAResponseType parseResponseItemCreate(
    final ResponseItemCreateType itemCreate)
  {
    return new CAResponseItemCreate(
      parseItem(itemCreate.getItem())
    );
  }

  private static CAResponseType parseResponseItemUpdate(
    final ResponseItemUpdateType itemUpdate)
  {
    return new CAResponseItemUpdate(
      parseItem(itemUpdate.getItem())
    );
  }

  private static CAResponseType parseResponseItemGet(
    final ResponseItemGetType itemGet)
  {
    return new CAResponseItemGet(
      parseItem(itemGet.getItem())
    );
  }

  private static CAResponseType parseResponseItemRemove(
    final ResponseItemRemoveType itemRemove)
  {
    return new CAResponseItemRemove(
      CAItemID.of(itemRemove.getId())
    );
  }

  private static CAResponseType parseResponseItemList(
    final ResponseItemListType itemList)
  {
    return new CAResponseItemList(
      new CAItems(
        itemList.getItemList()
          .stream()
          .map(CA1MessageParser::parseItem)
          .collect(Collectors.toSet()))
    );
  }

  private static CAItem parseItem(
    final ItemType item)
  {
    return new CAItem(
      CAItemID.of(item.getId()),
      item.getName(),
      item.getCount().longValue(),
      parseItemMetadatas(item.getItemMetadatas()),
      parseAttachments(item.getItemAttachments()),
      parseTags(item.getTags())
    );
  }

  private static SortedSet<CATag> parseTags(
    final TagsType tags)
  {
    return tags.getTagList()
      .stream()
      .map(CA1MessageParser::parseTag)
      .collect(Collectors.toCollection(TreeSet::new));
  }

  private static CALocations parseLocations(
    final List<LocationType> locationList)
  {
    return new CALocations(
      new TreeMap<>(
        locationList
          .stream()
          .map(CA1MessageParser::parseLocation)
          .collect(Collectors.toMap(CALocation::id, Function.identity()))
      )
    );
  }

  private static CATag parseTag(
    final TagType tag)
  {
    return new CATag(
      CATagID.of(tag.getId()),
      tag.getName()
    );
  }

  private static SortedMap<CAItemAttachmentID, CAItemAttachment> parseAttachments(
    final ItemAttachmentsType itemAttachments)
  {
    return new TreeMap<>(
      itemAttachments.getItemAttachmentList()
        .stream()
        .map(CA1MessageParser::parseItemAttachment)
        .collect(Collectors.toMap(CAItemAttachment::id, Function.identity()))
    );
  }

  private static CAItemAttachment parseItemAttachment(
    final ItemAttachmentType itemAttachment)
  {
    return new CAItemAttachment(
      CAItemAttachmentID.of(itemAttachment.getId()),
      itemAttachment.getDescription(),
      itemAttachment.getMediaType(),
      itemAttachment.getRelation(),
      itemAttachment.getSize().longValue(),
      itemAttachment.getHashAlgorithm(),
      itemAttachment.getHashValue(),
      Optional.ofNullable(itemAttachment.getItemAttachmentData())
        .map(CAByteArray::new)
    );
  }

  private static SortedMap<String, CAItemMetadata> parseItemMetadatas(
    final ItemMetadatasType itemMetadatas)
  {
    return new TreeMap<>(
      itemMetadatas.getItemMetadataList()
        .stream()
        .map(CA1MessageParser::parseItemMetadata)
        .collect(Collectors.toMap(CAItemMetadata::name, Function.identity()))
    );
  }

  private static CAItemMetadata parseItemMetadata(
    final ItemMetadataType i)
  {
    return new CAItemMetadata(i.getName(), i.getValue());
  }

  private static CAResponseType parseResponseLoginUsernamePassword(
    final ResponseLoginUsernamePasswordType usernamePassword)
  {
    return new CAResponseLoginUsernamePassword();
  }

  private static CAEventType parseEvent(
    final EventType event)
  {
    if (event instanceof EventUpdatedType u) {
      return parseEventUpdated(u);
    }

    throw new IllegalStateException("Unexpected message: " + event);
  }

  private static CAEventType parseEventUpdated(
    final EventUpdatedType u)
  {
    final var updatedIds =
      u.getUpdated().getIDList();
    final var removedIds =
      u.getRemoved().getIDList();

    return new CAEventType.CAEventUpdated(
      transformIds(updatedIds),
      transformIds(removedIds)
    );
  }

  private static Set<CAIdType> transformIds(
    final List<IDType> ids)
  {
    final var result = new HashSet<CAIdType>(ids.size());
    for (final var id : ids) {
      result.add(transformId(id));
    }
    return result;
  }

  private static CAIdType transformId(
    final IDType id)
  {
    if (id instanceof ItemIDType) {
      return CAItemID.of(id.getValue());
    } else if (id instanceof ItemAttachmentIDType) {
      return CAItemAttachmentID.of(id.getValue());
    } else if (id instanceof UserIDType) {
      return CAUserID.of(id.getValue());
    } else if (id instanceof LocationIDType) {
      return CALocationID.of(id.getValue());
    } else if (id instanceof TagIDType) {
      return CATagID.of(id.getValue());
    } else {
      throw new UnreachableCodeException();
    }
  }

  private static CACommandType parseCommand(
    final CommandType command)
  {
    if (command instanceof CommandLoginUsernamePasswordType login) {
      return parseCommandLoginUsernamePassword(login);
    }
    if (command instanceof CommandItemListType itemList) {
      return parseCommandItemList(itemList);
    }
    if (command instanceof CommandItemCreateType itemCreate) {
      return parseCommandItemCreate(itemCreate);
    }
    if (command instanceof CommandItemGetType itemGet) {
      return parseCommandItemGet(itemGet);
    }
    if (command instanceof CommandItemRemoveType itemRemove) {
      return parseCommandItemRemove(itemRemove);
    }
    if (command instanceof CommandItemUpdateType itemUpdate) {
      return parseCommandItemUpdate(itemUpdate);
    }
    if (command instanceof CommandItemAttachmentPutType itemAttachmentPut) {
      return parseCommandItemAttachmentPut(itemAttachmentPut);
    }
    if (command instanceof CommandItemAttachmentRemoveType itemAttachmentRemove) {
      return parseCommandItemAttachmentRemove(itemAttachmentRemove);
    }
    if (command instanceof CommandItemMetadataPutType itemMetadataPut) {
      return parseCommandItemMetadataPut(itemMetadataPut);
    }
    if (command instanceof CommandItemMetadataRemoveType itemMetadataRemove) {
      return parseCommandItemMetadataRemove(itemMetadataRemove);
    }
    if (command instanceof CommandTagsPutType tagsPut) {
      return parseCommandTagsPut(tagsPut);
    }
    if (command instanceof CommandTagListType tagsList) {
      return parseCommandTagList(tagsList);
    }
    if (command instanceof CommandTagsDeleteType tagsDelete) {
      return parseCommandTagsDelete(tagsDelete);
    }
    if (command instanceof CommandLocationPutType locationPut) {
      return parseCommandLocationPut(locationPut);
    }
    if (command instanceof CommandLocationListType locationList) {
      return parseCommandLocationList(locationList);
    }

    throw new IllegalStateException("Unexpected message: " + command);
  }

  private static CACommandType parseCommandLocationList(
    final CommandLocationListType locationList)
  {
    return new CACommandLocationList();
  }

  private static CACommandType parseCommandLocationPut(
    final CommandLocationPutType locationPut)
  {
    return new CACommandLocationPut(
      parseLocation(locationPut.getLocation())
    );
  }

  private static CACommandType parseCommandTagsDelete(
    final CommandTagsDeleteType tagsDelete)
  {
    return new CACommandTagsDelete(
      new CATags(parseTags(tagsDelete.getTags()))
    );
  }

  private static CACommandType parseCommandTagList(
    final CommandTagListType tagsList)
  {
    return new CACommandTagList();
  }

  private static CACommandType parseCommandTagsPut(
    final CommandTagsPutType tagsPut)
  {
    return new CACommandTagsPut(
      new CATags(parseTags(tagsPut.getTags()))
    );
  }

  private static CACommandType parseCommandItemCreate(
    final CommandItemCreateType itemCreate)
  {
    return new CACommandItemCreate(
      CAItemID.of(itemCreate.getId()),
      itemCreate.getName()
    );
  }

  private static CACommandType parseCommandItemGet(
    final CommandItemGetType itemGet)
  {
    return new CACommandItemGet(
      CAItemID.of(itemGet.getId())
    );
  }

  private static CACommandType parseCommandItemRemove(
    final CommandItemRemoveType itemRemove)
  {
    return new CACommandItemRemove(
      CAItemID.of(itemRemove.getId())
    );
  }

  private static CACommandType parseCommandItemUpdate(
    final CommandItemUpdateType itemUpdate)
  {
    return new CACommandItemUpdate(
      CAItemID.of(itemUpdate.getId()),
      itemUpdate.getName()
    );
  }

  private static CACommandType parseCommandItemAttachmentPut(
    final CommandItemAttachmentPutType itemAttachmentPut)
  {
    return new CACommandItemAttachmentPut(
      CAItemID.of(itemAttachmentPut.getItem()),
      parseItemAttachment(itemAttachmentPut.getItemAttachment())
    );
  }

  private static CACommandType parseCommandItemAttachmentRemove(
    final CommandItemAttachmentRemoveType itemAttachmentRemove)
  {
    return new CACommandItemAttachmentRemove(
      CAItemID.of(itemAttachmentRemove.getItem()),
      CAItemAttachmentID.of(itemAttachmentRemove.getAttachment())
    );
  }

  private static CACommandType parseCommandItemMetadataPut(
    final CommandItemMetadataPutType itemMetadataPut)
  {
    return new CACommandItemMetadataPut(
      CAItemID.of(itemMetadataPut.getItem()),
      Set.copyOf(parseItemMetadatas(itemMetadataPut.getItemMetadatas()).values())
    );
  }

  private static CACommandType parseCommandItemMetadataRemove(
    final CommandItemMetadataRemoveType itemMetadataRemove)
  {
    return new CACommandItemMetadataRemove(
      CAItemID.of(itemMetadataRemove.getItem()),
      Set.copyOf(itemMetadataRemove.getItemMetadataNameList())
    );
  }

  private static CACommandType parseCommandLoginUsernamePassword(
    final CommandLoginUsernamePasswordType login)
  {
    return new CACommandLoginUsernamePassword(
      login.getUser(),
      login.getPassword()
    );
  }

  private static CACommandType parseCommandItemList(
    final CommandItemListType itemList)
  {
    if (itemList.isSetListLocationExact()) {
      return new CACommandItemList(
        mapListLocation(itemList.getListLocationExact())
      );
    }
    if (itemList.isSetListLocationWithDescendants()) {
      return new CACommandItemList(
        mapListLocation(itemList.getListLocationWithDescendants())
      );
    }
    if (itemList.isSetListLocationsAll()) {
      return new CACommandItemList(
        mapListLocation(itemList.getListLocationsAll())
      );
    }
    throw new UnreachableCodeException();
  }

  private static CAListLocationBehaviourType mapListLocation(
    final ListLocationsBehaviourType behaviour)
  {
    if (behaviour instanceof ListLocationExactType exact) {
      return new CAListLocationExact(
        CALocationID.of(exact.getLocation()));
    }
    if (behaviour instanceof ListLocationsAllType) {
      return new CAListLocationsAll();
    }
    if (behaviour instanceof ListLocationWithDescendantsType with) {
      return new CAListLocationWithDescendants(
        CALocationID.of(with.getLocation()));
    }

    throw new IllegalStateException("Unexpected message: " + behaviour);
  }

  private void logXmlError(
    final XmlError error)
  {
    final var severity =
      switch (error.getSeverity()) {
        case SEVERITY_ERROR -> PARSE_ERROR;
        case SEVERITY_WARNING, SEVERITY_INFO -> PARSE_WARNING;
        default -> PARSE_ERROR;
      };

    final var status =
      ParseStatus.builder()
        .setSeverity(severity)
        .setMessage(error.getMessage())
        .setErrorCode("parse")
        .setLexical(LexicalPosition.of(
          error.getLine(),
          error.getColumn(),
          Optional.of(this.source)))
        .build();

    this.errors.add(status);
    this.statusConsumer.accept(status);
  }

  private byte[] executeValidation()
    throws ParseException
  {
    try {
      final var data =
        this.stream.readAllBytes();

      final var schema =
        this.schemas.newSchema(
          CA1MessageParser.class.getResource(
            "/com/io7m/cardant/protocol/inventory/v1/inventory-1.xsd"));

      final var validator =
        schema.newValidator();

      validator.setErrorHandler(new ErrorHandler()
      {
        @Override
        public void warning(
          final SAXParseException exception)
        {
          CA1MessageParser.this.onSAXWarning(exception);
        }

        @Override
        public void error(
          final SAXParseException exception)
        {
          CA1MessageParser.this.onSAXError(exception);
        }

        @Override
        public void fatalError(
          final SAXParseException exception)
          throws SAXException
        {
          CA1MessageParser.this.onSAXFatalError(exception);
          throw exception;
        }
      });

      final var source = new StreamSource(new ByteArrayInputStream(data));
      source.setSystemId(this.source.toString());
      validator.validate(source);

      if (this.validationFailed) {
        throw new ParseException("Validation failed.", this.errors);
      }

      return data;
    } catch (final IOException | SAXException e) {
      this.logException(e);
      throw new ParseException(e.getMessage(), this.errors);
    }
  }

  private void onSAXFatalError(
    final SAXParseException exception)
  {
    this.onSAXError(exception);
  }

  private void onSAXError(
    final SAXParseException exception)
  {
    final var status =
      ParseStatus.builder()
        .setSeverity(PARSE_ERROR)
        .setMessage(exception.getMessage())
        .setErrorCode("validation")
        .setLexical(LexicalPosition.of(
          exception.getLineNumber(),
          exception.getColumnNumber(),
          Optional.of(this.source)))
        .build();

    this.errors.add(status);
    this.statusConsumer.accept(status);
    this.validationFailed = true;
  }

  private void onSAXWarning(
    final SAXParseException exception)
  {
    final var status =
      ParseStatus.builder()
        .setSeverity(PARSE_WARNING)
        .setMessage(exception.getMessage())
        .setErrorCode("validation")
        .setLexical(LexicalPosition.of(
          exception.getLineNumber(),
          exception.getColumnNumber(),
          Optional.of(this.source)))
        .build();

    this.errors.add(status);
    this.statusConsumer.accept(status);
  }

  private void logException(
    final Exception e)
  {
    final var status =
      ParseStatus.builder()
        .setSeverity(PARSE_ERROR)
        .setMessage(e.getMessage())
        .setErrorCode("io")
        .setLexical(LexicalPosition.of(0, 0, Optional.of(this.source)))
        .build();

    this.statusConsumer.accept(status);
    this.errors.add(status);
    this.validationFailed = true;
  }

  @Override
  public void close()
    throws IOException
  {
    this.stream.close();
  }
}
