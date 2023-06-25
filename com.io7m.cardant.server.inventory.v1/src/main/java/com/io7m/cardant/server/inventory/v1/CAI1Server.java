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

package com.io7m.cardant.server.inventory.v1;

import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.security.CASecurityPolicy;
import com.io7m.cardant.server.http.CAHTTPRequestTimeFilter;
import com.io7m.cardant.server.http.CAPlainErrorHandler;
import com.io7m.cardant.server.http.CAServletHolders;
import com.io7m.cardant.server.service.clock.CAServerClock;
import com.io7m.cardant.server.service.configuration.CAConfigurationServiceType;
import com.io7m.cardant.server.service.telemetry.api.CAMetricsServiceType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.NullSessionDataStore;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.EnumSet;
import java.util.Objects;

import static jakarta.servlet.DispatcherType.REQUEST;

/**
 * Inventory servers.
 */

public final class CAI1Server
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAI1Server.class);

  private CAI1Server()
  {

  }

  /**
   * Create an Inventory server.
   *
   * @param services The service directory
   *
   * @return A server
   *
   * @throws Exception On errors
   */

  public static Server create(
    final RPServiceDirectoryType services)
    throws Exception
  {
    Objects.requireNonNull(services, "services");

    final var configurations =
      services.requireService(CAConfigurationServiceType.class);
    final var configuration =
      configurations.configuration();
    final var httpConfig =
      configuration.inventoryApiAddress();
    final var address =
      InetSocketAddress.createUnresolved(
        httpConfig.listenAddress(),
        httpConfig.listenPort()
      );

    CASecurity.setPolicy(CASecurityPolicy.open());

    final var server =
      new Server(address);

    /*
     * Add a request customizer that properly handles headers such as
     * X-Forwarded-For and so on.
     */

    for (final var connector : server.getConnectors()) {
      for (final var factory : connector.getConnectionFactories()) {
        if (factory instanceof final HttpConfiguration.ConnectionFactory http) {
          http.getHttpConfiguration()
            .addCustomizer(new ForwardedRequestCustomizer());
        }
      }
    }

    /*
     * Configure all the servlets.
     */

    final var servletHolders =
      new CAServletHolders(services);

    final var servlets =
      new ServletContextHandler();

    servlets.addServlet(
      servletHolders.create(CA1ServletVersions.class, CA1ServletVersions::new),
      "/"
    );
    servlets.addServlet(
      servletHolders.create(CA1ServletLogin.class, CA1ServletLogin::new),
      "/inventory/1/0/login"
    );
    servlets.addServlet(
      servletHolders.create(CA1ServletCommand.class, CA1ServletCommand::new),
      "/inventory/1/0/command"
    );
    servlets.addServlet(
      servletHolders.create(
        CA1ServletFileDownload.class,
        CA1ServletFileDownload::new),
      "/inventory/1/0/file-download"
    );
    servlets.addServlet(
      servletHolders.create(
        CA1ServletFileUpload.class,
        CA1ServletFileUpload::new),
      "/inventory/1/0/file-upload"
    );

    servlets.addServlet(
      servletHolders.create(
        CA1ServletHealth.class,
        CA1ServletHealth::new),
      "/health"
    );
    servlets.addServlet(
      servletHolders.create(
        CA1ServletVersion.class,
        CA1ServletVersion::new),
      "/version"
    );

    /*
     * Add a handler that tracks request/response time.
     */

    final var filterHolder =
      new FilterHolder(
        new CAHTTPRequestTimeFilter(
          services.requireService(CAMetricsServiceType.class),
          services.requireService(CAServerClock.class)
        )
      );

    servlets.addFilter(filterHolder, "*", EnumSet.of(REQUEST));

    /*
     * Set up a session handler.
     */

    final var sessionIds = new DefaultSessionIdManager(server);
    server.setSessionIdManager(sessionIds);

    final var sessionHandler = new SessionHandler();
    sessionHandler.setSessionCookie("CARDANT_INVENTORY_SESSION");

    final var sessionStore = new NullSessionDataStore();
    final var sessionCache = new DefaultSessionCache(sessionHandler);
    sessionCache.setSessionDataStore(sessionStore);

    sessionHandler.setSessionCache(sessionCache);
    sessionHandler.setSessionIdManager(sessionIds);
    sessionHandler.setHandler(servlets);

    /*
     * Enable gzip.
     */

    final var gzip = new GzipHandler();
    gzip.setHandler(sessionHandler);

    server.setErrorHandler(new CAPlainErrorHandler());
    server.setRequestLog((request, response) -> {

    });
    server.setHandler(gzip);
    server.start();
    LOG.info("[{}] inventory server started", address);
    return server;
  }
}
