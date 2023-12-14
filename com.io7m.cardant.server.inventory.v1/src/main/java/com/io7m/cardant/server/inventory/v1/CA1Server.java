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

import com.io7m.cardant.server.http.CAHTTPRequestTimeFilter;
import com.io7m.cardant.server.service.clock.CAServerClock;
import com.io7m.cardant.server.service.configuration.CAConfigurationServiceType;
import com.io7m.cardant.server.service.telemetry.api.CAMetricsServiceType;
import com.io7m.cardant.server.service.tls.CATLSContextServiceType;
import com.io7m.cardant.tls.CATLSEnabled;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import io.helidon.common.tls.TlsConfig;
import io.helidon.webserver.WebServer;
import io.helidon.webserver.WebServerConfig;
import io.helidon.webserver.http.HttpRouting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;

import static java.net.StandardSocketOptions.SO_REUSEADDR;
import static java.net.StandardSocketOptions.SO_REUSEPORT;

/**
 * An inventory API v1 server.
 */

public final class CA1Server
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CA1Server.class);

  private CA1Server()
  {

  }

  /**
   * Create an inventory API v1 server.
   *
   * @param services The service directory
   *
   * @return A server
   *
   * @throws Exception On errors
   */

  public static WebServer create(
    final RPServiceDirectoryType services)
    throws Exception
  {
    final var configurationService =
      services.requireService(CAConfigurationServiceType.class);
    final var tlsService =
      services.requireService(CATLSContextServiceType.class);
    final var configuration =
      configurationService.configuration();
    final var httpConfig =
      configuration.inventoryApiConfiguration();
    final var address =
      InetSocketAddress.createUnresolved(
        httpConfig.listenAddress(),
        httpConfig.listenPort()
      );

    final var routing =
      HttpRouting.builder()
        .addFilter(new CAHTTPRequestTimeFilter(
          services.requireService(CAMetricsServiceType.class),
          services.requireService(CAServerClock.class)
        ))
        .get("/", new CA1HandlerVersions(services))
        .post(
          "/inventory/1/0/login",
          new CA1HandlerLogin(services))
        .post(
          "/inventory/1/0/command",
          new CA1HandlerCommand(services))
        .post(
          "/inventory/1/0/file-upload",
          new CA1HandlerFileUpload(services))
        .post(
          "/inventory/1/0/file-download",
          new CA1HandlerFileDownload(services))
        .get("/version", new CA1HandlerVersion(services))
        .get("/health", new CA1HandlerHealth(services));

    final var webServerBuilder =
      WebServerConfig.builder();

    if (httpConfig.tlsConfiguration() instanceof final CATLSEnabled enabled) {
      final var tlsContext =
        tlsService.create(
          "UserAPI",
          enabled.keyStore(),
          enabled.trustStore()
        );

      webServerBuilder.tls(
        TlsConfig.builder()
          .enabled(true)
          .sslContext(tlsContext.context())
          .build()
      );
    }

    final var webServer =
      webServerBuilder
        .port(httpConfig.listenPort())
        .address(InetAddress.getByName(httpConfig.listenAddress()))
        .listenerSocketOptions(Map.ofEntries(
          Map.entry(SO_REUSEADDR, Boolean.TRUE),
          Map.entry(SO_REUSEPORT, Boolean.TRUE)
        ))
        .routing(routing)
        .build();

    webServer.start();
    LOG.info("[{}] Admin API server started", address);
    return webServer;
  }
}
