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


package com.io7m.cardant.server.service.maintenance;

import com.io7m.cardant.database.api.CADatabaseQueriesMaintenanceType;
import com.io7m.cardant.database.api.CADatabaseRole;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.server.service.clock.CAServerClock;
import com.io7m.cardant.server.service.configuration.CAConfigurationService;
import com.io7m.cardant.server.service.telemetry.api.CAServerTelemetryServiceType;
import com.io7m.cardant.server.service.tls.CATLSContextServiceType;
import com.io7m.jmulticlose.core.CloseableCollection;
import com.io7m.jmulticlose.core.CloseableCollectionType;
import com.io7m.jmulticlose.core.ClosingResourceFailedException;
import com.io7m.repetoir.core.RPServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A service that performs nightly database maintenance.
 */

public final class CAMaintenanceService
  implements RPServiceType, AutoCloseable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAMaintenanceService.class);

  private final CAServerTelemetryServiceType telemetry;
  private final CADatabaseType database;
  private final CATLSContextServiceType tlsContexts;
  private final AtomicBoolean closed;
  private final ScheduledExecutorService tlsExecutor;
  private final ScheduledExecutorService maintenanceExecutor;
  private final CloseableCollectionType<ClosingResourceFailedException> resources;

  private CAMaintenanceService(
    final CAServerTelemetryServiceType inTelemetry,
    final CADatabaseType inDatabase,
    final CATLSContextServiceType inTlsContexts,
    final ScheduledExecutorService inTlsExecutor,
    final ScheduledExecutorService inMaintenanceExecutor)
  {
    this.tlsExecutor =
      Objects.requireNonNull(inTlsExecutor, "executor");
    this.maintenanceExecutor =
      Objects.requireNonNull(inMaintenanceExecutor, "executor");
    this.telemetry =
      Objects.requireNonNull(inTelemetry, "telemetry");
    this.database =
      Objects.requireNonNull(inDatabase, "database");
    this.tlsContexts =
      Objects.requireNonNull(inTlsContexts, "tlsContexts");

    this.resources = CloseableCollection.create();
    this.resources.add(this.tlsExecutor);
    this.resources.add(this.maintenanceExecutor);

    this.closed =
      new AtomicBoolean(false);
  }

  /**
   * A service that performs nightly maintenance.
   *
   * @param clock         The clock
   * @param telemetry     The telemetry service
   * @param database      The database
   * @param configuration The configuration service
   * @param tlsContexts   The TLS contexts
   *
   * @return The service
   */

  public static CAMaintenanceService create(
    final CAServerClock clock,
    final CAServerTelemetryServiceType telemetry,
    final CAConfigurationService configuration,
    final CATLSContextServiceType tlsContexts,
    final CADatabaseType database)
  {
    Objects.requireNonNull(clock, "clock");
    Objects.requireNonNull(configuration, "configuration");
    Objects.requireNonNull(database, "database");
    Objects.requireNonNull(telemetry, "telemetry");
    Objects.requireNonNull(tlsContexts, "tlsContexts");

    final var tlsExecutor =
      Executors.newSingleThreadScheduledExecutor(r -> {
        final var thread = new Thread(r);
        thread.setName("com.io7m.cardant.maintenance.tls");
        thread.setDaemon(true);
        return thread;
      });

    final var maintenanceExecutor =
      Executors.newSingleThreadScheduledExecutor(r -> {
        final var thread = new Thread(r);
        thread.setName("com.io7m.cardant.maintenance.db");
        thread.setDaemon(true);
        return thread;
      });

    final var maintenanceService =
      new CAMaintenanceService(
        telemetry,
        database,
        tlsContexts,
        tlsExecutor,
        maintenanceExecutor
      );

    final var reloadIntervalOpt =
      configuration.configuration()
        .maintenanceConfiguration()
        .tlsReloadInterval();

    reloadIntervalOpt.ifPresent(duration -> {
      tlsExecutor.scheduleWithFixedDelay(
        maintenanceService::runTLSReload,
        0L,
        duration.toMillis(),
        TimeUnit.MILLISECONDS
      );
    });

    maintenanceExecutor.scheduleWithFixedDelay(
      maintenanceService::runMaintenance,
      0L,
      24L,
      TimeUnit.HOURS
    );

    return maintenanceService;
  }

  private void runTLSReload()
  {
    LOG.info("Reloading TLS contexts");
    this.tlsContexts.reload();
  }

  private void runMaintenance()
  {
    LOG.info("Maintenance task starting");

    final var span =
      this.telemetry.tracer()
        .spanBuilder("Maintenance")
        .startSpan();

    try (var ignored = span.makeCurrent()) {
      try (var connection =
             this.database.openConnection(CADatabaseRole.CARDANT)) {
        try (var transaction = connection.openTransaction()) {
          transaction.queries(CADatabaseQueriesMaintenanceType.ExecuteType.class)
            .execute(CADatabaseUnit.UNIT);

          transaction.commit();
          LOG.info("Maintenance task completed.");
        }
      }
    } catch (final Exception e) {
      LOG.error("Maintenance task failed: ", e);
      span.recordException(e);
    } finally {
      span.end();
    }
  }

  @Override
  public String description()
  {
    return "Server maintenance service.";
  }

  @Override
  public void close()
  {
    if (this.closed.compareAndSet(false, true)) {
      try {
        this.resources.close();
      } catch (final ClosingResourceFailedException e) {
        // Nothing we can do about this.
      }
    }
  }

  @Override
  public String toString()
  {
    return "[CAMaintenanceService 0x%s]"
      .formatted(Long.toUnsignedString(this.hashCode(), 16));
  }
}
