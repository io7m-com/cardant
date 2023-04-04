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
import com.io7m.cardant.client.api.CAClientCommandResultType;
import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientEventType;
import com.io7m.cardant.client.api.CAClientHostileType;
import com.io7m.cardant.client.vanilla.CAClientStrings;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAFileType.CAFileWithData;
import com.io7m.cardant.model.CAIds;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemLocations;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItemRepositType;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.model.CAListLocationBehaviourType;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocations;
import com.io7m.cardant.protocol.inventory.api.CACommandType;
import com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandFilePut;
import com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandFileRemove;
import com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemAttachmentAdd;
import com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemList;
import com.io7m.cardant.protocol.inventory.api.CAMessageServicesType;
import com.io7m.cardant.protocol.inventory.api.CATransaction;
import com.io7m.cardant.protocol.versioning.CAVersioningMessageParserFactoryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.io7m.cardant.client.api.CAClientEventStatusChanged.CLIENT_DISCONNECTED;
import static com.io7m.cardant.client.api.CAClientEventStatusChanged.CLIENT_NEGOTIATING_PROTOCOLS_FAILED;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemCreate;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemGet;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemLocationsList;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemMetadataPut;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemMetadataRemove;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemReposit;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandItemsRemove;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandLocationList;
import static com.io7m.cardant.protocol.inventory.api.CACommandType.CACommandLocationPut;

/**
 * The default client implementation.
 */

public final class CAClient implements CAClientHostileType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAClient.class);

  private final AtomicBoolean closed;
  private final CAClientConfiguration configuration;
  private final CAClientStrings strings;
  private final CAVersioningMessageParserFactoryType parsers;
  private final CAMessageServicesType messages;
  private final boolean hostile;
  private final ExecutorService commandExecutor;
  private final ExecutorService httpExecutor;
  private final ExecutorService pollExecutor;
  private final HttpClient httpClient;
  private final LinkedBlockingDeque<CAClientCommandType<?>> requests;
  private final SubmissionPublisher<CAClientEventType> events;
  private volatile CAClientConnectionType connection;

  /**
   * The default client implementation.
   *
   * @param inConfiguration   The configuration
   * @param inCommandExecutor The executor used for handling commands
   * @param inHostile         {@code true} if the client is hostile
   * @param inHTTPExecutor    The executor used for HTTP I/O operations
   * @param inMessages        The message service
   * @param inParsers         The parser factory
   * @param inPollExecutor    The executor used to poll for events
   * @param inStrings         The client strings
   */

  public CAClient(
    final ExecutorService inPollExecutor,
    final ExecutorService inHTTPExecutor,
    final ExecutorService inCommandExecutor,
    final CAClientConfiguration inConfiguration,
    final CAClientStrings inStrings,
    final CAVersioningMessageParserFactoryType inParsers,
    final CAMessageServicesType inMessages,
    final boolean inHostile)
  {
    this.pollExecutor =
      Objects.requireNonNull(inPollExecutor, "mainExecutor");
    this.httpExecutor =
      Objects.requireNonNull(inHTTPExecutor, "httpExecutor");
    this.commandExecutor =
      Objects.requireNonNull(inCommandExecutor, "commandExecutor");
    this.configuration =
      Objects.requireNonNull(inConfiguration, "configuration");
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.parsers =
      Objects.requireNonNull(inParsers, "parsers");
    this.messages =
      Objects.requireNonNull(inMessages, "messages");
    this.hostile =
      inHostile;

    this.events =
      new SubmissionPublisher<>();
    this.closed =
      new AtomicBoolean(false);
    this.httpClient =
      CAClientsHTTP.create(this.httpExecutor);
    this.connection =
      new CAClientConnectionUnconnected();
    this.requests =
      new LinkedBlockingDeque<>();
  }

  private static void pause()
  {
    try {
      Thread.sleep(1_000L);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  /**
   * Poll for events until the client is closed.
   */

  public void executeEventPolling()
  {
    while (true) {
      if (this.closed.get()) {
        return;
      }

      try {
        if (!this.isConnected()) {
          this.connect();
        }

        this.connection.poll();
      } catch (final IOException e) {
        LOG.error("error receiving data: ", e);
        pause();
      } catch (final InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (final ParseException e) {
        LOG.error("error parsing data: ", e);
        pause();
      }
    }
  }

  /**
   * Execute commands until the client is closed.
   */

  public void executeCommandProcessing()
  {
    while (true) {
      if (this.closed.get()) {
        return;
      }

      if (this.isConnected()) {
        try {
          final var request =
            this.requests.poll(1L, TimeUnit.SECONDS);
          if (request != null) {
            this.connection.sendCommand(request);
          }
        } catch (final InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      } else {
        pause();
      }
    }
  }

  private void checkHostile()
  {
    if (!this.hostile) {
      throw new IllegalStateException("Not a hostile client!");
    }
  }

  private void connect()
    throws IOException, InterruptedException, ParseException
  {
    LOG.info("open {}", this.configuration.baseURI());

    try {
      this.connection =
        CAClientConnections.open(
          this.events,
          this.parsers,
          this.messages,
          this.httpClient,
          this.configuration,
          this.strings
        );
    } catch (final Exception e) {
      this.events.submit(CLIENT_NEGOTIATING_PROTOCOLS_FAILED);
      throw e;
    }

    LOG.info("opened {}", this.configuration.baseURI());
  }

  @Override
  public void close()
  {
    if (this.closed.compareAndSet(false, true)) {
      LOG.info("close");
      this.pollExecutor.shutdown();
      this.httpExecutor.shutdown();
      this.commandExecutor.shutdown();
      this.connection = new CAClientConnectionUnconnected();
      this.events.submit(CLIENT_DISCONNECTED);
      this.events.close();
    }
  }

  @Override
  public boolean isConnected()
  {
    return this.connection.isConnected();
  }

  @Override
  public Flow.Publisher<CAClientEventType> events()
  {
    return this.events;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<CAItems>> itemsList(
    final CAListLocationBehaviourType locationBehaviour)
  {
    Objects.requireNonNull(locationBehaviour, "locationBehaviour");

    final var future =
      new CompletableFuture<CAClientCommandResultType<CAItems>>();
    final var command =
      new CACommandItemList(locationBehaviour);

    this.requests.add(
      new CAClientCommandValid<>(future, command, CAItems.class)
    );
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<CAItem>>
  itemGet(
    final CAItemID id)
  {
    Objects.requireNonNull(id, "id");

    final var future =
      new CompletableFuture<CAClientCommandResultType<CAItem>>();
    final var command =
      new CACommandItemGet(id);

    this.requests.add(
      new CAClientCommandValid<>(future, command, CAItem.class)
    );
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<CAItem>>
  itemCreate(
    final CAItemID id,
    final String name)
  {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(name, "name");

    final var future =
      new CompletableFuture<CAClientCommandResultType<CAItem>>();
    final var command =
      new CACommandItemCreate(id, name);

    this.requests.add(
      new CAClientCommandValid<>(future, command, CAItem.class)
    );
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<CAIds>>
  itemsDelete(
    final Set<CAItemID> items)
  {
    Objects.requireNonNull(items, "items");

    final var future =
      new CompletableFuture<CAClientCommandResultType<CAIds>>();

    this.requests.add(
      new CAClientCommandValid<>(
        future,
        new CACommandItemsRemove(items),
        CAIds.class
      )
    );
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<CAItem>>
  itemMetadataDelete(
    final CAItemID id,
    final Set<String> names)
  {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(names, "names");

    final var future =
      new CompletableFuture<CAClientCommandResultType<CAItem>>();
    final var command =
      new CACommandItemMetadataRemove(id, names);

    this.requests.add(
      new CAClientCommandValid<>(future, command, CAItem.class)
    );
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<CAItem>>
  itemMetadataUpdate(
    final CAItemID id,
    final Set<CAItemMetadata> itemMetadatas)
  {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(itemMetadatas, "itemMetadatas");

    final var future =
      new CompletableFuture<CAClientCommandResultType<CAItem>>();
    final var command =
      new CACommandItemMetadataPut(id, itemMetadatas);

    this.requests.add(
      new CAClientCommandValid<>(future, command, CAItem.class)
    );
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<CAItem>> itemAttachmentAdd(
    final CAItemID id,
    final CAFileWithData file,
    final String relation)
  {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(file, "file");
    Objects.requireNonNull(relation, "relation");

    final var future =
      new CompletableFuture<CAClientCommandResultType<CAItem>>();

    final var transaction =
      new CATransaction(
        List.of(
          new CACommandFilePut(file),
          new CACommandItemAttachmentAdd(id, file.id(), relation),
          new CACommandItemGet(id)
        )
      );

    final var transactional =
      new CAClientCommandTransactional<>(
        future,
        transaction,
        CAItem.class
      );

    this.requests.add(transactional);
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<CAItem>> itemAttachmentDelete(
    final CAItemID item,
    final CAFileID file,
    final String relation)
  {
    Objects.requireNonNull(item, "item");
    Objects.requireNonNull(file, "file");
    Objects.requireNonNull(relation, "relation");

    final var future =
      new CompletableFuture<CAClientCommandResultType<CAItem>>();
    final var command =
      new CACommandType.CACommandItemAttachmentRemove(item, file, relation);

    this.requests.add(
      new CAClientCommandValid<>(future, command, CAItem.class)
    );
    return future;
  }

  @Override
  public CompletableFuture<InputStream> fileData(
    final CAFileID file)
  {
    Objects.requireNonNull(file, "file");
    return this.connection.fileData(file);
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<CAFileType>> filePut(
    final CAFileWithData file)
  {
    Objects.requireNonNull(file, "file");

    final var future =
      new CompletableFuture<CAClientCommandResultType<CAFileType>>();
    final var command =
      new CACommandFilePut(file);

    this.requests.add(
      new CAClientCommandValid<>(future, command, CAFileType.class)
    );
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<CAFileID>> fileDelete(
    final CAFileID file)
  {
    Objects.requireNonNull(file, "file");

    final var future =
      new CompletableFuture<CAClientCommandResultType<CAFileID>>();
    final var command =
      new CACommandFileRemove(file);

    this.requests.add(
      new CAClientCommandValid<>(future, command, CAFileID.class)
    );
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<CALocation>> locationPut(
    final CALocation location)
  {
    Objects.requireNonNull(location, "location");

    final var future =
      new CompletableFuture<CAClientCommandResultType<CALocation>>();
    final var command =
      new CACommandLocationPut(location);

    this.requests.add(
      new CAClientCommandValid<>(future, command, CALocation.class)
    );
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<CALocations>> locationList()
  {
    final var future =
      new CompletableFuture<CAClientCommandResultType<CALocations>>();
    final var command =
      new CACommandLocationList();

    this.requests.add(
      new CAClientCommandValid<>(future, command, CALocations.class)
    );
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<CAItemID>> itemReposit(
    final CAItemRepositType reposit)
  {
    Objects.requireNonNull(reposit, "reposit");

    final var future =
      new CompletableFuture<CAClientCommandResultType<CAItemID>>();
    final var command =
      new CACommandItemReposit(reposit);

    this.requests.add(
      new CAClientCommandValid<>(future, command, CAItemID.class)
    );
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<CAItemLocations>> itemLocationsList(
    final CAItemID item)
  {
    Objects.requireNonNull(item, "item");

    final var future =
      new CompletableFuture<CAClientCommandResultType<CAItemLocations>>();
    final var command =
      new CACommandItemLocationsList(item);

    this.requests.add(
      new CAClientCommandValid<>(future, command, CAItemLocations.class)
    );
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<Void>> garbageCommand()
  {
    this.checkHostile();

    final var future =
      new CompletableFuture<CAClientCommandResultType<Void>>();

    this.requests.add(new CAClientCommandGarbage(future));
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<Void>> invalidCommand()
  {
    this.checkHostile();

    final var future =
      new CompletableFuture<CAClientCommandResultType<Void>>();

    this.requests.add(new CAClientCommandInvalid(future));
    return future;
  }
}
