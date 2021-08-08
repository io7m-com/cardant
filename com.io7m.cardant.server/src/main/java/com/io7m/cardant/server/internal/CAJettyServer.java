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

import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageParsers;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageSerializers;
import com.io7m.cardant.server.api.CAServerHTTPConfiguration;
import com.io7m.cardant.server.internal.rest.CAServerEventType;
import com.io7m.cardant.server.internal.rest.v1.CA1CommandServlet;
import com.io7m.cardant.server.internal.rest.v1.CA1LoginServlet;
import com.io7m.cardant.server.internal.rest.v1.CA1ServletHolder;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.FileSessionDataStore;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.io.Closeable;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.SubmissionPublisher;

/**
 * An internal Jetty server.
 */

public final class CAJettyServer implements Closeable
{
  private final Server server;

  private CAJettyServer(
    final Server inServer)
  {
    this.server = Objects.requireNonNull(inServer, "server");
  }

  /**
   * Create a Jetty server.
   *
   * @param configuration The configuration
   * @param serverEvents  The server event sink
   * @param database      The database
   *
   * @return A new server
   *
   * @throws IOException On I/O errors
   */

  public static CAJettyServer create(
    final CAServerHTTPConfiguration configuration,
    final SubmissionPublisher<CAServerEventType> serverEvents,
    final CADatabaseType database)
    throws IOException
  {
    Objects.requireNonNull(configuration, "configuration");
    Objects.requireNonNull(serverEvents, "commandEvents");
    Objects.requireNonNull(database, "database");

    final var inventorySerializers =
      new CA1InventoryMessageSerializers();
    final var inventoryParsers =
      new CA1InventoryMessageParsers();
    final var messages =
      new CAServerMessages(Locale.getDefault());

    final var server = new Server();
    final var serverConnector = new ServerConnector(server);
    serverConnector.setPort(configuration.port());
    server.addConnector(serverConnector);

    /*
     * Set up a servlet container.
     */

    final var servlets = new ServletContextHandler();
    servlets.addServlet(
      new CA1ServletHolder<>(
        CA1LoginServlet.class, () -> {
        return new CA1LoginServlet(
          serverEvents,
          inventoryParsers,
          inventorySerializers,
          database
        );
      }),
      "/v1/login"
    );
    servlets.addServlet(
      new CA1ServletHolder<>(
        CA1CommandServlet.class, () -> {
        return new CA1CommandServlet(
          serverEvents,
          inventoryParsers,
          inventorySerializers,
          messages,
          database
        );
      }),
      "/v1/command"
    );

    /*
     * Set up a session handler that allows for Servlets to have sessions
     * that can survive server restarts.
     */

    final var sessionIds = new DefaultSessionIdManager(server);
    final var sessionHandler = new SessionHandler();

    final var sessionStore = new FileSessionDataStore();
    sessionStore.setStoreDir(configuration.sessionDirectory().toFile());

    final var sessionCache = new DefaultSessionCache(sessionHandler);
    sessionCache.setSessionDataStore(sessionStore);

    sessionHandler.setSessionCache(sessionCache);
    sessionHandler.setSessionIdManager(sessionIds);
    sessionHandler.setHandler(servlets);

    /*
     * Set up an MBean container so that the statistics handler can export
     * statistics to JMX.
     */

    final var mbeanContainer =
      new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
    server.addBean(mbeanContainer);

    /*
     * Set up a statistics handler that wraps everything.
     */

    final var statsHandler = new StatisticsHandler();
    statsHandler.setHandler(sessionHandler);

    server.setHandler(statsHandler);
    return new CAJettyServer(server);
  }

  /**
   * Start the server, asynchronously.
   *
   * @throws Exception On errors
   */

  public void start()
    throws Exception
  {
    this.server.start();
  }

  @Override
  public void close()
    throws IOException
  {
    try {
      this.server.stop();
    } catch (final Exception e) {
      throw new IOException(e);
    }
  }
}
