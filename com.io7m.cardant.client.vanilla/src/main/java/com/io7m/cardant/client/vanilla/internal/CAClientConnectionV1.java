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

package com.io7m.cardant.client.vanilla.internal;

import com.io7m.anethum.common.ParseException;
import com.io7m.anethum.common.SerializeException;
import com.io7m.cardant.client.api.CAClientCommandError;
import com.io7m.cardant.client.api.CAClientCommandOK;
import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientEventDataChanged;
import com.io7m.cardant.client.api.CAClientEventDataReceived;
import com.io7m.cardant.client.api.CAClientEventType;
import com.io7m.cardant.client.api.CAClientUnit;
import com.io7m.cardant.client.vanilla.CAClientStrings;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItemMetadatas;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageParserFactoryType;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageSerializerFactoryType;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemAttachmentRemove;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemCreate;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemGet;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemList;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemMetadataPut;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemMetadataRemove;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemRemove;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandLoginUsernamePassword;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryCommandType;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryEventType;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryEventUpdated;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryMessageType;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryResponseType;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryTransaction;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1ResponseError;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1ResponseOK;
import com.io7m.cardant.protocol.versioning.messages.CAVersion;
import com.io7m.jaffirm.core.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.io7m.cardant.client.api.CAClientEventStatusChanged.CLIENT_CONNECTED;
import static com.io7m.cardant.client.api.CAClientEventStatusChanged.CLIENT_DISCONNECTED;
import static com.io7m.cardant.client.api.CAClientEventStatusChanged.CLIENT_RECEIVING_DATA;
import static com.io7m.cardant.client.api.CAClientEventStatusChanged.CLIENT_SENDING_REQUEST;
import static com.io7m.cardant.client.api.CAClientUnit.UNIT;

public final class CAClientConnectionV1 implements CAClientConnectionType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAClientConnectionV1.class);

  private final SubmissionPublisher<CAClientEventType> events;
  private final CA1InventoryMessageSerializerFactoryType serializers;
  private final CA1InventoryMessageParserFactoryType parsers;
  private final CAClientStrings strings;
  private final HttpClient httpClient;
  private final CAClientConfiguration configuration;
  private final CAVersion version;
  private final AtomicBoolean connected;
  private final URI baseURI;
  private final URI loginURI;
  private final URI commandURI;
  private final URI attachmentURI;
  private final URI eventsURI;

  public CAClientConnectionV1(
    final SubmissionPublisher<CAClientEventType> inEvents,
    final CA1InventoryMessageSerializerFactoryType inSerializers,
    final CA1InventoryMessageParserFactoryType inParsers,
    final CAClientStrings inStrings,
    final HttpClient inClient,
    final CAClientConfiguration inConfiguration,
    final CAVersion inVersion)
  {
    this.events =
      Objects.requireNonNull(inEvents, "events");
    this.serializers =
      Objects.requireNonNull(inSerializers, "serializers");
    this.parsers =
      Objects.requireNonNull(inParsers, "parsers");
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.httpClient =
      Objects.requireNonNull(inClient, "client");
    this.configuration =
      Objects.requireNonNull(inConfiguration, "configuration");
    this.version =
      Objects.requireNonNull(inVersion, "version");

    this.baseURI =
      URI.create(inConfiguration.baseURI() + "/" + this.version.baseURI())
        .normalize();

    Preconditions.checkPrecondition(
      this.baseURI.isAbsolute(),
      "Base URI must be absolute"
    );

    this.loginURI =
      URI.create(this.baseURI + "/login")
        .normalize();
    this.commandURI =
      URI.create(this.baseURI + "/command")
        .normalize();
    this.attachmentURI =
      URI.create(this.baseURI + "/attachment")
        .normalize();
    this.eventsURI =
      URI.create(this.baseURI + "/events")
        .normalize();

    this.connected =
      new AtomicBoolean(false);
  }

  private static <T> CA1InventoryMessageType transformCommand(
    final CAClientCommandType<T> command)
  {
    if (command instanceof CAClientCommandItemsList itemList) {
      return new CA1CommandItemList();
    }

    if (command instanceof CAClientCommandItemGet itemGet) {
      return new CA1CommandItemGet(itemGet.id());
    }

    if (command instanceof CAClientCommandItemMetadataDelete itemMetadataDelete) {
      return transformCommandItemMetadataDelete(itemMetadataDelete);
    }

    if (command instanceof CAClientCommandItemMetadataUpdate itemMetadataUpdate) {
      return transformCommandItemMetadataUpdate(itemMetadataUpdate);
    }

    if (command instanceof CAClientCommandItemCreate itemCreate) {
      return transformCommandItemCreate(itemCreate);
    }

    if (command instanceof CAClientCommandItemsDelete itemsDelete) {
      return transformCommandItemsDelete(itemsDelete);
    }

    if (command instanceof CAClientCommandItemAttachmentDelete delete) {
      return transformCommandItemAttachmentDelete(delete);
    }

    throw new IllegalStateException("Unrecognized command: " + command);
  }

  private static CA1InventoryMessageType transformCommandItemAttachmentDelete(
    final CAClientCommandItemAttachmentDelete delete)
  {
    return new CA1CommandItemAttachmentRemove(delete.itemAttachment());
  }

  private static CA1InventoryTransaction transformCommandItemsDelete(
    final CAClientCommandItemsDelete itemsDelete)
  {
    return new CA1InventoryTransaction(
      itemsDelete.items()
        .stream()
        .map(CA1CommandItemRemove::new)
        .collect(Collectors.toList())
    );
  }

  private static CA1InventoryTransaction transformCommandItemCreate(
    final CAClientCommandItemCreate itemCreate)
  {
    final var item = itemCreate.item();
    final var commands = new ArrayList<CA1InventoryCommandType>(2);
    commands.add(
      new CA1CommandItemCreate(item.id(), item.name()));
    commands.add(
      new CA1CommandItemMetadataPut(
        item.id(),
        new CAItemMetadatas(item.metadata())
      ));
    return new CA1InventoryTransaction(commands);
  }

  private static CA1CommandItemMetadataPut transformCommandItemMetadataUpdate(
    final CAClientCommandItemMetadataUpdate itemMetadataUpdate)
  {
    final var entries = new TreeMap<String, CAItemMetadata>();
    final var itemMetadata = itemMetadataUpdate.itemMetadata();
    entries.put(itemMetadata.name(), itemMetadata);
    return new CA1CommandItemMetadataPut(
      itemMetadata.itemId(), new CAItemMetadatas(entries));
  }

  private static CA1CommandItemMetadataRemove transformCommandItemMetadataDelete(
    final CAClientCommandItemMetadataDelete itemMetadataDelete)
  {
    final var entries = new TreeMap<String, CAItemMetadata>();
    for (final var metadata : itemMetadataDelete.metadata()) {
      entries.put(metadata.name(), metadata);
    }
    return new CA1CommandItemMetadataRemove(
      itemMetadataDelete.itemID(),
      new CAItemMetadatas(entries)
    );
  }

  @Override
  public void poll()
    throws IOException
  {
    if (this.isConnected()) {
      this.fetchEvents();
    } else {
      this.login();
      this.fetchEvents();
    }
  }

  @Override
  public <T> void sendCommand(
    final CAClientCommandType<T> command)
  {
    Objects.requireNonNull(command, "command");

    LOG.debug("command send {}", command.getClass().getSimpleName());

    try {
      this.events.submit(CLIENT_SENDING_REQUEST);

      final var commandBytes =
        this.serializeCommand(command);

      final var request =
        HttpRequest.newBuilder(this.commandURI)
          .POST(HttpRequest.BodyPublishers.ofByteArray(commandBytes))
          .build();

      final var response =
        this.httpClient.send(
          request,
          HttpResponse.BodyHandlers.ofInputStream());

      this.events.submit(CLIENT_RECEIVING_DATA);

      LOG.debug("command status {}", Integer.valueOf(response.statusCode()));
      try (var inputStream = response.body()) {
        this.parseResponseStream(this.commandURI, command, inputStream);
      }

    } catch (final Exception e) {
      LOG.debug("exception raised: ", e);
      command.future().completeExceptionally(e);
    }
  }

  private <T> byte[] serializeCommand(
    final CAClientCommandType<T> command)
    throws SerializeException, IOException
  {
    try (var bytes = new ByteArrayOutputStream()) {
      this.serializers.serialize(
        this.commandURI,
        bytes,
        transformCommand(command)
      );
      return bytes.toByteArray();
    }
  }

  private void fetchEvents()
    throws IOException
  {
    LOG.debug("events fetch {}", this.eventsURI);

    try {
      final var request =
        HttpRequest.newBuilder(this.eventsURI)
          .build();

      final var response =
        this.httpClient.send(
          request,
          HttpResponse.BodyHandlers.ofInputStream()
        );

      final var statusCode = response.statusCode();
      switch (statusCode) {
        case 200 -> {
          LOG.debug("events ok {}", this.eventsURI);
          try (var inputStream = response.body()) {
            this.parseMessageStream(this.eventsURI, inputStream);
          }
        }
        case 204 -> LOG.debug("events empty {}", this.eventsURI);
        default -> {
          LOG.error("events error {}", Integer.valueOf(statusCode));
          this.onDisconnected();
        }
      }
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (final ParseException e) {
      LOG.error("received corrupted data: ", e);
    } catch (final IOException e) {
      LOG.debug("i/o error: ", e);
      this.onDisconnected();
      throw e;
    }
  }

  private void parseMessageStream(
    final URI source,
    final InputStream inputStream)
    throws ParseException
  {
    final var data =
      this.parsers.parse(source, inputStream);

    if (data instanceof CA1InventoryEventType event) {
      this.parseEvent(event);
      return;
    }

    throw new IllegalStateException();
  }

  private <T> void parseResponseStream(
    final URI source,
    final CAClientCommandType<T> command,
    final InputStream inputStream)
    throws ParseException
  {
    final var data =
      this.parsers.parse(source, inputStream);

    if (data instanceof CA1InventoryResponseType response) {
      this.parseResponse(command, response);
      return;
    }

    throw new IllegalStateException();
  }

  private <T> void parseResponse(
    final CAClientCommandType<T> command,
    final CA1InventoryResponseType response)
  {
    final var expectedReturn = command.returnType();
    final var commandFuture = command.future();

    if (response instanceof CA1ResponseOK ok) {
      final var dataOpt = ok.data();
      if (dataOpt.isEmpty()) {
        if (Objects.equals(expectedReturn, CAClientUnit.class)) {
          commandFuture.complete(new CAClientCommandOK<>((T) UNIT));
          return;
        }
        commandFuture.complete(
          this.responseTypeError(expectedReturn, "Nothing")
        );
        return;
      }

      final var data = dataOpt.get();
      if (expectedReturn.isAssignableFrom(data.getClass())) {
        commandFuture.complete(
          new CAClientCommandOK<>(expectedReturn.cast(data)));

        this.events.submit(new CAClientEventDataReceived(data));
        return;
      }

      commandFuture.complete(
        this.responseTypeError(expectedReturn, data.getClass().getSimpleName())
      );
      return;
    }

    if (response instanceof CA1ResponseError error) {
      commandFuture.complete(this.responseError(error));
      return;
    }

    throw new IllegalStateException(
      "Unexpected response: " + response.getClass());
  }

  private <T> CAClientCommandError<T> responseError(
    final CA1ResponseError error)
  {
    return new CAClientCommandError<>(error.message());
  }

  private <T> CAClientCommandError<T> responseTypeError(
    final Class<?> expectedReturn,
    final String receivedReturn)
  {
    return new CAClientCommandError<>(
      this.strings.format(
        "errorResponseType",
        expectedReturn.getSimpleName(),
        receivedReturn
      )
    );
  }

  private void parseEvent(
    final CA1InventoryEventType event)
  {
    if (event instanceof CA1InventoryEventUpdated updated) {
      this.events.submit(new CAClientEventDataChanged(
        updated.updated(),
        updated.removed()
      ));
    }
  }

  private void login()
    throws IOException
  {
    LOG.debug("login {}", this.loginURI);

    try {
      final var data =
        this.serializeMessage(
          this.loginURI,
          new CA1CommandLoginUsernamePassword(
            this.configuration.username(),
            this.configuration.password()
          ));

      final var request =
        HttpRequest.newBuilder(this.loginURI)
          .POST(HttpRequest.BodyPublishers.ofByteArray(data))
          .build();

      final var response =
        this.httpClient.send(
          request,
          HttpResponse.BodyHandlers.ofInputStream()
        );

      if (response.statusCode() < 300) {
        LOG.debug("login ok {}", Integer.valueOf(response.statusCode()));
        this.onConnected();
      } else {
        LOG.debug("login failed {}", Integer.valueOf(response.statusCode()));
      }
    } catch (final SerializeException e) {
      throw new IOException(e);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  private void onConnected()
  {
    this.connected.set(true);
    this.events.submit(CLIENT_CONNECTED);
  }

  private void onDisconnected()
  {
    this.connected.set(false);
    this.events.submit(CLIENT_DISCONNECTED);
  }

  private byte[] serializeMessage(
    final URI uri,
    final CA1InventoryMessageType message)
    throws IOException, SerializeException
  {
    try (var bytes = new ByteArrayOutputStream()) {
      this.serializers.serialize(uri, bytes, message);
      return bytes.toByteArray();
    }
  }

  @Override
  public boolean isConnected()
  {
    return this.connected.get();
  }
}
