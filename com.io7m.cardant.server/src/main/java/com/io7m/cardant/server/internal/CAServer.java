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

package com.io7m.cardant.server.internal;

import com.io7m.cardant.database.api.CADatabaseParameters;
import com.io7m.cardant.database.api.CADatabaseProviderType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.server.api.CAServerConfiguration;
import com.io7m.cardant.server.api.CAServerDatabaseConfigurationType;
import com.io7m.cardant.server.api.CAServerDatabaseLocalConfiguration;
import com.io7m.cardant.server.api.CAServerDatabaseRemoteConfiguration;
import com.io7m.cardant.server.api.CAServerType;
import com.io7m.cardant.server.internal.metrics.CAServerMetrics;
import com.io7m.cardant.server.internal.metrics.CAServerMetricsBean;
import com.io7m.cardant.server.internal.rest.CAServerEventType;
import com.io7m.jmulticlose.core.CloseableCollection;
import com.io7m.jmulticlose.core.CloseableCollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Objects;
import java.util.concurrent.SubmissionPublisher;

/**
 * A server.
 */

public final class CAServer implements CAServerType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAServer.class);

  private final CAServerConfiguration configuration;
  private final CloseableCollectionType<?> resources;
  private final CADatabaseType database;
  private final CAJettyServer jetty;
  private final CAServerMetrics metrics;
  private final CAServerMetricsBean metricsBean;

  private CAServer(
    final CAServerConfiguration inConfiguration,
    final CloseableCollectionType<?> inResources,
    final CADatabaseType inDatabase,
    final CAJettyServer inJetty,
    final CAServerMetrics inMetrics,
    final CAServerMetricsBean inMetricsBean)
  {
    this.configuration =
      Objects.requireNonNull(inConfiguration, "configuration");
    this.resources =
      Objects.requireNonNull(inResources, "inResources");
    this.database =
      Objects.requireNonNull(inDatabase, "database");
    this.jetty =
      Objects.requireNonNull(inJetty, "jetty");
    this.metrics =
      Objects.requireNonNull(inMetrics, "metrics");
    this.metricsBean =
      Objects.requireNonNull(inMetricsBean, "metricsBean");
  }

  /**
   * Create a server.
   *
   * @param configuration The server configuration
   * @param databases     A provider of databases
   *
   * @return The new server
   *
   * @throws Exception On errors
   */

  public static CAServerType create(
    final CAServerConfiguration configuration,
    final CADatabaseProviderType databases)
    throws Exception
  {
    final var resources =
      CloseableCollection.create();
    final var databaseParameters =
      createDatabaseParameters(configuration.database());

    final var database =
      databases.open(databaseParameters, databaseEvent -> {
        LOG.debug("database: {}", databaseEvent.message());
      });
    resources.add(database);

    final var metricsBean =
      new CAServerMetricsBean();

    final var commandEvents =
      new SubmissionPublisher<CAServerEventType>();
    resources.add(commandEvents);

    final var metrics =
      new CAServerMetrics(
        database.events(),
        commandEvents,
        metricsBean
      );

    final var jetty =
      CAJettyServer.create(configuration, commandEvents, database);
    resources.add(jetty);

    final var server =
      new CAServer(
        configuration,
        resources,
        database,
        jetty,
        metrics,
        metricsBean
      );

    server.start();
    return server;
  }

  private static CADatabaseParameters createDatabaseParameters(
    final CAServerDatabaseConfigurationType database)
  {
    if (database instanceof CAServerDatabaseLocalConfiguration local) {
      return new CADatabaseParameters(
        local.file().toString(),
        local.create()
      );
    }
    if (database instanceof CAServerDatabaseRemoteConfiguration remote) {
      throw new IllegalStateException("Unimplemented code!");
    }
    throw new IllegalStateException();
  }

  private void setupMetrics()
  {
    try {
      final var server =
        ManagementFactory.getPlatformMBeanServer();
      final var objectName =
        new ObjectName("com.io7m.cardant:name=Metrics");

      server.registerMBean(this.metricsBean, objectName);
    } catch (final MalformedObjectNameException
      | InstanceAlreadyExistsException
      | MBeanRegistrationException
      | NotCompliantMBeanException e) {
      LOG.error("unable to register metrics bean: ", e);
    }
  }

  private void start()
    throws Exception
  {
    this.setupMetrics();
    this.jetty.start();
  }

  @Override
  public void close()
    throws IOException
  {
    try {
      LOG.info("shutting down");
      this.resources.close();
    } catch (final Exception e) {
      throw new IOException(e);
    }
  }

  @Override
  public CADatabaseType database()
  {
    return this.database;
  }
}
