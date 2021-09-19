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
import com.io7m.cardant.client.api.CAClientCommandResultType;
import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientEventCommandFailed;
import com.io7m.cardant.client.api.CAClientEventDataChanged;
import com.io7m.cardant.client.api.CAClientEventDataReceived;
import com.io7m.cardant.client.api.CAClientEventType;
import com.io7m.cardant.client.vanilla.CAClientStrings;
import com.io7m.cardant.model.CAInventoryElementType;
import com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandLoginUsernamePassword;
import com.io7m.cardant.protocol.inventory.api.CAEventType;
import com.io7m.cardant.protocol.inventory.api.CAEventType.CAEventUpdated;
import com.io7m.cardant.protocol.inventory.api.CAMessageParserFactoryType;
import com.io7m.cardant.protocol.inventory.api.CAMessageSerializerFactoryType;
import com.io7m.cardant.protocol.inventory.api.CAMessageType;
import com.io7m.cardant.protocol.inventory.api.CAResponseType;
import com.io7m.cardant.protocol.versioning.messages.CAVersion;
import com.io7m.jaffirm.core.Preconditions;
import com.io7m.junreachable.UnreachableCodeException;
import net.jcip.annotations.GuardedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.io7m.cardant.client.api.CAClientEventStatusChanged.CLIENT_CONNECTED;
import static com.io7m.cardant.client.api.CAClientEventStatusChanged.CLIENT_DISCONNECTED;
import static com.io7m.cardant.client.api.CAClientEventStatusChanged.CLIENT_RECEIVING_DATA;
import static com.io7m.cardant.client.api.CAClientEventStatusChanged.CLIENT_SENDING_REQUEST;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseError;
import static com.io7m.cardant.protocol.inventory.api.CAResponseType.CAResponseWithElementType;

/**
 * A version 1 client connection.
 */

public final class CAClientConnectionV1 implements CAClientConnectionType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAClientConnectionV1.class);

  private final SubmissionPublisher<CAClientEventType> events;
  private final CAMessageSerializerFactoryType serializers;

  @GuardedBy("parsersLock")
  private final CAMessageParserFactoryType parsers;
  private final Object parsersLock;
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
  private final Random random;

  /**
   * A version 1 client connection.
   *
   * @param inSerializers   The serializer factory
   * @param inEvents        The event stream
   * @param inClient        The client
   * @param inConfiguration The client configuration
   * @param inParsers       The parser factory
   * @param inVersion       The version
   * @param inStrings       The client strings
   */

  public CAClientConnectionV1(
    final SubmissionPublisher<CAClientEventType> inEvents,
    final CAMessageSerializerFactoryType inSerializers,
    final CAMessageParserFactoryType inParsers,
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

    this.parsersLock = new Object();

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
    this.random =
      new Random(0L);
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
    if (command instanceof CAClientCommandHostileType<T> cmd) {
      return this.serializeHostile(cmd);
    }

    if (command instanceof CAClientCommandValid cmd) {
      try (var bytes = new ByteArrayOutputStream()) {
        this.serializers.serialize(this.commandURI, bytes, cmd.command());
        return bytes.toByteArray();
      }
    }

    if (command instanceof CAClientCommandTransactional cmd) {
      try (var bytes = new ByteArrayOutputStream()) {
        this.serializers.serialize(this.commandURI, bytes, cmd.transaction());
        return bytes.toByteArray();
      }
    }

    throw new UnreachableCodeException();
  }

  private <T> byte[] serializeHostile(
    final CAClientCommandHostileType<T> command)
  {
    if (command instanceof CAClientCommandGarbage) {
      final var bytes = new byte[256];
      this.random.nextBytes(bytes);
      return bytes;
    }

    if (command instanceof CAClientCommandInvalid) {
      return """
        <CommandInvalid xmlns="urn:com.io7m.cardant.inventory.protocol:1"
                        id="70258edd-db3f-4a3a-81e1-26492ca6a824"/>
                """.getBytes(StandardCharsets.UTF_8);
    }

    throw new IllegalStateException("Unrecognized command: " + command);
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
    final CAMessageType data =
      this.parseStream(source, inputStream);

    if (data instanceof CAEventType event) {
      this.parseEvent(event);
      return;
    }

    throw new IllegalStateException();
  }

  private CAMessageType parseStream(
    final URI source,
    final InputStream inputStream)
    throws ParseException
  {
    synchronized (this.parsersLock) {
      return this.parsers.parse(source, inputStream);
    }
  }

  private <T> void parseResponseStream(
    final URI source,
    final CAClientCommandType<T> command,
    final InputStream inputStream)
    throws ParseException
  {
    final CAMessageType data =
      this.parseStream(source, inputStream);

    if (data instanceof CAResponseType response) {
      this.parseResponse(command, response);
      return;
    }

    throw new IllegalStateException();
  }

  private <T> void parseResponse(
    final CAClientCommandType<T> command,
    final CAResponseType response)
  {
    final var expectedReturn =
      command.returnType();
    final var commandFuture =
      command.future();

    if (response instanceof CAResponseError error) {
      final CAClientCommandError<T> result = CAClientConnectionV1.responseError(error);
      this.events.submit(new CAClientEventCommandFailed<>(
        command.getClass().getSimpleName(),
        result
      ));
      commandFuture.complete(result);
      return;
    }

    if (response instanceof CAResponseWithElementType withData) {
      this.handleResult(
        expectedReturn,
        commandFuture,
        expectedReturn.cast(withData.data())
      );
      return;
    }

    throw new IllegalStateException(
      "Unexpected response: " + response.getClass());
  }

  private <T> void handleResult(
    final Class<T> expectedReturn,
    final CompletableFuture<CAClientCommandResultType<T>> commandFuture,
    final T data)
  {
    if (expectedReturn.isAssignableFrom(data.getClass())) {
      commandFuture.complete(
        new CAClientCommandOK<>(expectedReturn.cast(data)));

      if (data instanceof CAInventoryElementType inventory) {
        this.events.submit(new CAClientEventDataReceived(inventory));
      }
      return;
    }

    commandFuture.complete(
      this.responseTypeError(expectedReturn, data.getClass().getSimpleName())
    );
  }

  private static <T> CAClientCommandError<T> responseError(
    final CAResponseError error)
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
    final CAEventType event)
  {
    if (event instanceof CAEventUpdated updated) {
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
          new CACommandLoginUsernamePassword(
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
    final CAMessageType message)
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
