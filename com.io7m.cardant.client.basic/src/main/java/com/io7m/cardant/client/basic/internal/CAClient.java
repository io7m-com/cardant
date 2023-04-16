/*
 * Copyright © 2023 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.client.basic.internal;

import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientEventCommandFailed;
import com.io7m.cardant.client.api.CAClientEventType;
import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.client.api.CAClientType;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.protocol.inventory.CAICommandLogin;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.hibiscus.api.HBResultFailure;
import com.io7m.hibiscus.api.HBResultSuccess;
import com.io7m.hibiscus.api.HBResultType;
import com.io7m.hibiscus.api.HBState;
import com.io7m.jmulticlose.core.CloseableCollection;
import com.io7m.jmulticlose.core.CloseableCollectionType;
import com.io7m.junreachable.UnreachableCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.http.HttpClient;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorIo;
import static com.io7m.hibiscus.api.HBState.CLIENT_AUTHENTICATING;
import static com.io7m.hibiscus.api.HBState.CLIENT_AUTHENTICATION_FAILED;
import static com.io7m.hibiscus.api.HBState.CLIENT_CONNECTED;
import static com.io7m.hibiscus.api.HBState.CLIENT_IDLE;
import static com.io7m.hibiscus.api.HBState.CLIENT_RECEIVING_DATA;
import static com.io7m.hibiscus.api.HBState.CLIENT_SENDING_COMMAND;

/**
 * The basic client.
 */

public final class CAClient
  implements CAClientType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAClient.class);

  private final AtomicReference<HBState> stateNow;
  private final SubmissionPublisher<HBState> state;
  private final SubmissionPublisher<CAClientEventType> events;
  private final CAClientConfiguration configuration;
  private final ExecutorService commandExecutor;
  private final ExecutorService pollExecutor;
  private final CloseableCollectionType<CAClientException> resources;
  private final CAStrings strings;
  private final HttpClient httpClient;
  private final AtomicBoolean closed;
  private volatile CAHandlerType handler;

  private CAClient(
    final CAClientConfiguration inConfiguration,
    final ExecutorService inCommandExecutor,
    final ExecutorService inPollExecutor,
    final CloseableCollectionType<CAClientException> inResources,
    final CAStrings inStrings,
    final HttpClient inHttpClient)
  {
    this.configuration =
      Objects.requireNonNull(inConfiguration, "configuration");
    this.commandExecutor =
      Objects.requireNonNull(inCommandExecutor, "commandExecutor");
    this.pollExecutor =
      Objects.requireNonNull(inPollExecutor, "pollExecutor");
    this.resources =
      Objects.requireNonNull(inResources, "resources");
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.httpClient =
      Objects.requireNonNull(inHttpClient, "httpClient");

    this.events =
      new SubmissionPublisher<>();
    this.state =
      new SubmissionPublisher<>();
    this.stateNow =
      new AtomicReference<>(HBState.CLIENT_DISCONNECTED);
    this.handler =
      new CAHandlerDisconnected(inConfiguration, inStrings, inHttpClient);
    this.closed =
      new AtomicBoolean(false);
  }

  /**
   * Open the basic client.
   *
   * @param configuration The configuration
   * @param strings       String resources
   * @param httpClient    The HTTP client used for requests
   *
   * @return The basic client
   */

  public static CAClientType open(
    final CAClientConfiguration configuration,
    final CAStrings strings,
    final HttpClient httpClient)
  {
    final var resources =
      CloseableCollection.create(() -> {
        return new CAClientException(
          CAStandardErrorCodes.errorResourceCloseFailed(),
          strings.format("resourceCloseFailed"),
          Map.of()
        );
      });

    final var commandExecutor =
      Executors.newSingleThreadExecutor(r -> {
        final var th = new Thread(r);
        th.setName(
          "com.io7m.cardant.client.basic.command[%s]"
            .formatted(Long.toUnsignedString(th.getId()))
        );
        return th;
      });

    resources.add(commandExecutor::shutdown);

    final var pollExecutor =
      Executors.newSingleThreadExecutor(r -> {
        final var th = new Thread(r);
        th.setName(
          "com.io7m.cardant.client.basic.poll[%s]"
            .formatted(Long.toUnsignedString(th.getId()))
        );
        return th;
      });

    resources.add(pollExecutor::shutdown);

    final var client =
      new CAClient(
        configuration,
        commandExecutor,
        pollExecutor,
        resources,
        strings,
        httpClient
      );

    pollExecutor.execute(client::executeEventPolling);
    return client;
  }

  private static void pause()
  {
    try {
      Thread.sleep(5_000L);
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
      try {
        if (this.closed.get()) {
          return;
        }

        if (!this.isConnected()) {
          this.loginAsync()
            .get(60L, TimeUnit.SECONDS);
        }

        this.handler.pollEvents();
      } catch (final Throwable e) {
        pause();
      }
    }
  }

  private void publishState(
    final HBState newState)
  {
    this.stateNow.set(newState);
    this.state.submit(newState);
  }

  private void publishEvent(
    final CAClientEventType event)
  {
    this.events.submit(event);
  }

  @Override
  public boolean isConnected()
  {
    return this.handler.isConnected();
  }

  @Override
  public Flow.Publisher<CAClientEventType> events()
  {
    return this.events;
  }

  @Override
  public Flow.Publisher<HBState> state()
  {
    return this.state;
  }

  @Override
  public HBState stateNow()
  {
    return this.stateNow.get();
  }

  @Override
  public <RS1 extends CAIResponseType> HBResultType<RS1, CAIResponseError> login()
  {
    this.checkNotClosed();
    this.publishState(CLIENT_AUTHENTICATING);

    HBResultType<CANewHandler, CAIResponseError> result;
    try {
      result = this.handler.login();
    } catch (final InterruptedException e) {
      result = new HBResultFailure<>(new CAIResponseError(
        CAUUIDs.nullUUID(),
        e.getMessage(),
        errorIo(),
        Map.of(),
        Optional.of(e)
      ));
    }

    if (result instanceof HBResultSuccess<CANewHandler, CAIResponseError> success) {
      this.handler = success.result().handler();
      this.publishState(CLIENT_CONNECTED);
      return success.map(h -> (RS1) h.response());
    }
    if (result instanceof HBResultFailure<CANewHandler, CAIResponseError> failure) {
      this.publishState(CLIENT_AUTHENTICATION_FAILED);
      return failure.cast();
    }
    throw new UnreachableCodeException();
  }

  @Override
  public <C1 extends CAICommandType<?>, RS1 extends CAIResponseType>
  HBResultType<RS1, CAIResponseError>
  execute(
    final C1 command)
  {
    this.checkNotClosed();

    try {
      final HBResultType<?, CAIResponseError> response;
      if (command instanceof CAICommandLogin) {
        response = this.login();
      } else {
        this.publishState(CLIENT_SENDING_COMMAND);
        response = this.handler.executeCommand(
          (CAICommandType<? extends CAIResponseType>) command
        );
        this.publishState(CLIENT_IDLE);

        if (response instanceof HBResultFailure<?, CAIResponseError> error) {
          this.publishEvent(
            new CAClientEventCommandFailed(
              command.getClass().getSimpleName(),
              error.result()
            )
          );
        }
      }

      return (HBResultType<RS1, CAIResponseError>) response;
    } catch (final Throwable e) {
      return new HBResultFailure<>(
        new CAIResponseError(
          CAUUIDs.nullUUID(),
          e.getMessage(),
          errorIo(),
          new HashMap<>(),
          Optional.of(e)
        )
      );
    } finally {
      this.publishState(CLIENT_IDLE);
    }
  }

  @Override
  public <T> CompletableFuture<T> runAsync(
    final Supplier<T> f)
  {
    this.checkNotClosed();

    final var future = new CompletableFuture<T>();
    this.commandExecutor.execute(() -> {
      try {
        future.complete(f.get());
      } catch (final Throwable e) {
        future.completeExceptionally(e);
      }
    });
    return future;
  }

  private void checkNotClosed()
  {
    if (this.closed.get()) {
      throw new IllegalStateException("Client is closed!");
    }
  }

  @Override
  public void close()
    throws CAClientException
  {
    if (this.closed.compareAndSet(false, true)) {
      this.resources.close();
    }
  }

  @Override
  public HBResultType<InputStream, CAIResponseError> fileData(
    final CAFileID fileID)
  {
    this.checkNotClosed();

    try {
      this.publishState(CLIENT_RECEIVING_DATA);
      final var result =
        this.handler.fileData(fileID);
      this.publishState(CLIENT_IDLE);
      return result;
    } catch (final Throwable e) {
      return new HBResultFailure<>(
        new CAIResponseError(
          CAUUIDs.nullUUID(),
          e.getMessage(),
          errorIo(),
          new HashMap<>(),
          Optional.of(e)
        )
      );
    }
  }
}
