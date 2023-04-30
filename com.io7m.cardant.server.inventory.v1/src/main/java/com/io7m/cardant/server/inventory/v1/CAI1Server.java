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
import com.io7m.cardant.server.http.CAPlainErrorHandler;
import com.io7m.cardant.server.http.CARequestUniqueIDs;
import com.io7m.cardant.server.http.CAServletHolders;
import com.io7m.cardant.server.service.configuration.CAConfigurationServiceType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.session.DefaultSessionCache;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.NullSessionDataStore;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Objects;

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
     * Configure all the servlets.
     */

    final var servletHolders =
      new CAServletHolders(services);
    final var servlets =
      new ServletContextHandler();

    servlets.addServlet(
      servletHolders.create(CAI1Versions.class, CAI1Versions::new),
      "/"
    );
    servlets.addServlet(
      servletHolders.create(CA1VersionServlet.class, CA1VersionServlet::new),
      "/version"
    );
    servlets.addServlet(
      servletHolders.create(CAI1Login.class, CAI1Login::new),
      "/inventory/1/0/login"
    );
    servlets.addServlet(
      servletHolders.create(
        CA1CommandServlet.class,
        CA1CommandServlet::new),
      "/inventory/1/0/command"
    );

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

    /*
     * Add a connector listener that adds unique identifiers to all requests.
     */

    Arrays.stream(server.getConnectors()).forEach(
      connector -> connector.addBean(new CARequestUniqueIDs(services))
    );

    server.setErrorHandler(new CAPlainErrorHandler());
    server.setHandler(gzip);
    server.start();
    LOG.info("[{}] inventory server started", address);
    return server;
  }
}
