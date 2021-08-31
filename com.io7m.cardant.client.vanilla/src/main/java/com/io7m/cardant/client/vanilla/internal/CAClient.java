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
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachmentID;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemLocations;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocations;
import com.io7m.cardant.protocol.versioning.CAVersioningMessageParserFactoryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.http.HttpClient;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.io7m.cardant.client.api.CAClientEventStatusChanged.CLIENT_DISCONNECTED;
import static com.io7m.cardant.client.api.CAClientEventStatusChanged.CLIENT_NEGOTIATING_PROTOCOLS_FAILED;

public final class CAClient implements CAClientHostileType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAClient.class);

  private final AtomicBoolean closed;
  private final CAClientConfiguration configuration;
  private final CAClientStrings strings;
  private final CAVersioningMessageParserFactoryType parsers;
  private final boolean hostile;
  private final ExecutorService commandExecutor;
  private final ExecutorService httpExecutor;
  private final ExecutorService pollExecutor;
  private final HttpClient httpClient;
  private final LinkedBlockingDeque<CAClientCommandType<?>> requests;
  private final SubmissionPublisher<CAClientEventType> events;
  private volatile CAClientConnectionType connection;

  public CAClient(
    final ExecutorService inPollExecutor,
    final ExecutorService inHTTPExecutor,
    final ExecutorService inCommandExecutor,
    final CAClientConfiguration inConfiguration,
    final CAClientStrings inStrings,
    final CAVersioningMessageParserFactoryType inParsers,
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
  public CompletableFuture<CAClientCommandResultType<CAItems>> itemsList()
  {
    final var future =
      new CompletableFuture<CAClientCommandResultType<CAItems>>();
    this.requests.add(new CAClientCommandItemsList(future));
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<CAItem>> itemGet(
    final CAItemID id)
  {
    Objects.requireNonNull(id, "id");

    final var future =
      new CompletableFuture<CAClientCommandResultType<CAItem>>();
    this.requests.add(new CAClientCommandItemGet(future, id));
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<CAItem>> itemCreate(
    final CAItem item)
  {
    Objects.requireNonNull(item, "item");

    final var future =
      new CompletableFuture<CAClientCommandResultType<CAItem>>();
    this.requests.add(new CAClientCommandItemCreate(future, item));
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<Void>> itemsDelete(
    final List<CAItemID> items)
  {
    Objects.requireNonNull(items, "items");

    final var future = new CompletableFuture<CAClientCommandResultType<Void>>();
    this.requests.add(new CAClientCommandItemsDelete(future, items));
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<Void>> itemMetadataDelete(
    final Collection<CAItemMetadata> metadata)
  {
    Objects.requireNonNull(metadata, "metadata");

    final var future = new CompletableFuture<CAClientCommandResultType<Void>>();
    this.requests.add(new CAClientCommandItemMetadataDelete(future, metadata));
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<Void>> itemMetadataUpdate(
    final CAItemMetadata itemMetadata)
  {
    Objects.requireNonNull(itemMetadata, "itemMetadata");

    final var future = new CompletableFuture<CAClientCommandResultType<Void>>();
    this.requests.add(new CAClientCommandItemMetadataUpdate(
      future,
      itemMetadata));
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<Void>> itemAttachmentDelete(
    final CAItemAttachmentID itemAttachment)
  {
    Objects.requireNonNull(itemAttachment, "itemAttachment");

    final var future = new CompletableFuture<CAClientCommandResultType<Void>>();
    this.requests.add(new CAClientCommandItemAttachmentDelete(
      future,
      itemAttachment));
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<CALocations>> locationsList()
  {
    final var future =
      new CompletableFuture<CAClientCommandResultType<CALocations>>();
    this.requests.add(new CAClientCommandLocationsList(future));
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<Void>> garbageCommand()
  {
    this.checkHostile();
    final var future = new CompletableFuture<CAClientCommandResultType<Void>>();
    this.requests.add(new CAClientCommandGarbage(future));
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<Void>> invalidCommand()
  {
    this.checkHostile();
    final var future = new CompletableFuture<CAClientCommandResultType<Void>>();
    this.requests.add(new CAClientCommandInvalid(future));
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<CALocation>> locationGet(
    final CALocationID id)
  {
    Objects.requireNonNull(id, "id");

    final var future =
      new CompletableFuture<CAClientCommandResultType<CALocation>>();
    this.requests.add(new CAClientCommandLocationGet(future, id));
    return future;
  }

  @Override
  public CompletableFuture<CAClientCommandResultType<CAItemLocations>> itemLocationsList()
  {
    final var future =
      new CompletableFuture<CAClientCommandResultType<CAItemLocations>>();
    this.requests.add(new CAClientCommandItemLocationsList(future));
    return future;
  }
}
