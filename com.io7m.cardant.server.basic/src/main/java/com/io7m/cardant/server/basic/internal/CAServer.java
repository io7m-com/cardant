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

package com.io7m.cardant.server.basic.internal;

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.cardant.protocol.inventory.cb.CAI1Messages;
import com.io7m.cardant.server.api.CAServerConfiguration;
import com.io7m.cardant.server.api.CAServerException;
import com.io7m.cardant.server.api.CAServerType;
import com.io7m.cardant.server.controller.CAServerStrings;
import com.io7m.cardant.server.inventory.v1.CAI1Sends;
import com.io7m.cardant.server.inventory.v1.CAI1Server;
import com.io7m.cardant.server.service.clock.CAServerClock;
import com.io7m.cardant.server.service.configuration.CAConfigurationService;
import com.io7m.cardant.server.service.configuration.CAConfigurationServiceType;
import com.io7m.cardant.server.service.idstore.CAIdstoreClients;
import com.io7m.cardant.server.service.idstore.CAIdstoreClientsType;
import com.io7m.cardant.server.service.reqlimit.CARequestLimits;
import com.io7m.cardant.server.service.sessions.CASessionService;
import com.io7m.cardant.server.service.telemetry.api.CAServerTelemetryNoOp;
import com.io7m.cardant.server.service.telemetry.api.CAServerTelemetryServiceFactoryType;
import com.io7m.cardant.server.service.telemetry.api.CAServerTelemetryServiceType;
import com.io7m.cardant.server.service.verdant.CAVerdantMessages;
import com.io7m.cardant.server.service.verdant.CAVerdantMessagesType;
import com.io7m.jmulticlose.core.CloseableCollection;
import com.io7m.jmulticlose.core.CloseableCollectionType;
import com.io7m.repetoir.core.RPServiceDirectory;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.SpanKind;
import org.eclipse.jetty.server.Server;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The basic server frontend.
 */

public final class CAServer implements CAServerType
{
  private final CAServerConfiguration configuration;
  private CloseableCollectionType<CAServerException> resources;
  private final AtomicBoolean stopped;
  private CAServerTelemetryServiceType telemetry;
  private CADatabaseType database;

  /**
   * The basic server frontend.
   *
   * @param inConfiguration The server configuration
   */

  public CAServer(
    final CAServerConfiguration inConfiguration)
  {
    this.configuration =
      Objects.requireNonNull(inConfiguration, "configuration");
    this.resources =
      createResourceCollection();
    this.stopped =
      new AtomicBoolean(true);
  }

  @Override
  public void start()
    throws CAServerException
  {
    try {
      if (this.stopped.compareAndSet(true, false)) {
        this.resources = createResourceCollection();
        this.telemetry = this.createTelemetry();

        final var startupSpan =
          this.telemetry.tracer()
            .spanBuilder("CAServer.start")
            .setSpanKind(SpanKind.INTERNAL)
            .startSpan();

        try {
          this.database =
            this.resources.add(this.createDatabase(this.telemetry.openTelemetry()));
          final var services =
            this.resources.add(this.createServiceDirectory(this.database));

          final Server userView = CAI1Server.create(services);
          this.resources.add(userView::stop);

        } catch (final CADatabaseException e) {
          startupSpan.recordException(e);

          try {
            this.close();
          } catch (final CAServerException ex) {
            e.addSuppressed(ex);
          }
          throw new CAServerException(
            new CAErrorCode("database"),
            e.getMessage(),
            e,
            Collections.emptySortedMap()
          );
        } catch (final Exception e) {
          startupSpan.recordException(e);

          try {
            this.close();
          } catch (final CAServerException ex) {
            e.addSuppressed(ex);
          }
          throw new CAServerException(
            new CAErrorCode("startup"),
            e.getMessage(),
            e,
            Collections.emptySortedMap()
          );
        } finally {
          startupSpan.end();
        }
      }
    } catch (final Throwable e) {
      this.close();
      throw e;
    }
  }

  private RPServiceDirectoryType createServiceDirectory(
    final CADatabaseType newDatabase)
    throws IOException
  {
    final var services = new RPServiceDirectory();
    services.register(CAServerTelemetryServiceType.class, this.telemetry);
    services.register(CADatabaseType.class, newDatabase);

    final var strings = new CAServerStrings(this.configuration.locale());
    services.register(CAServerStrings.class, strings);

    final var sessionInventoryService =
      new CASessionService(
        this.telemetry.openTelemetry(),
        this.configuration.inventoryApiAddress()
          .sessionExpiration()
          .orElse(Duration.ofDays(3650L)),
        "inventory"
      );

    services.register(CASessionService.class, sessionInventoryService);

    final var idstore =
      CAIdstoreClients.create(
        this.configuration.locale(),
        this.configuration.idstoreConfiguration()
      );
    services.register(CAIdstoreClientsType.class, idstore);

    final var config = new CAConfigurationService(this.configuration);
    services.register(CAConfigurationServiceType.class, config);

    final var clock = new CAServerClock(this.configuration.clock());
    services.register(CAServerClock.class, clock);

    final var vMessages = new CAVerdantMessages();
    services.register(CAVerdantMessagesType.class, vMessages);

    final var idA1Messages = new CAI1Messages();
    services.register(CAI1Messages.class, idA1Messages);
    services.register(CAI1Sends.class, new CAI1Sends(idA1Messages));

    services.register(CARequestLimits.class, new CARequestLimits(size -> {
      return strings.format("requestTooLarge", size);
    }));
    return services;
  }

  private CADatabaseType createDatabase(
    final OpenTelemetry openTelemetry)
    throws CADatabaseException
  {
    return this.configuration.databases()
      .open(
        this.configuration.databaseConfiguration(),
        openTelemetry,
        event -> {

        });
  }

  private CAServerTelemetryServiceType createTelemetry()
  {
    return this.configuration.openTelemetry()
      .flatMap(config -> {
        final var loader =
          ServiceLoader.load(CAServerTelemetryServiceFactoryType.class);
        return loader.findFirst().map(f -> f.create(config));
      }).orElseGet(CAServerTelemetryNoOp::noop);
  }

  @Override
  public CADatabaseType database()
  {
    if (this.stopped.get()) {
      throw new IllegalStateException("Server is not started.");
    }

    return this.database;
  }

  @Override
  public boolean isClosed()
  {
    return this.stopped.get();
  }

  @Override
  public void close()
    throws CAServerException
  {
    if (this.stopped.compareAndSet(false, true)) {
      this.resources.close();
    }
  }

  private static CloseableCollectionType<CAServerException> createResourceCollection()
  {
    return CloseableCollection.create(
      () -> {
        return new CAServerException(
          new CAErrorCode("server-creation"),
          "Server creation failed."
        );
      }
    );
  }
}
