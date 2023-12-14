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


package com.io7m.cardant.tests.server;

import com.io7m.anethum.slf4j.ParseStatusLogging;
import com.io7m.cardant.database.api.CADatabaseConfiguration;
import com.io7m.cardant.database.api.CADatabaseCreate;
import com.io7m.cardant.database.api.CADatabaseUpgrade;
import com.io7m.cardant.server.api.CAServerConfigurations;
import com.io7m.cardant.server.api.CAServerHTTPServiceConfiguration;
import com.io7m.cardant.server.api.CAServerIdstoreConfiguration;
import com.io7m.cardant.server.api.CAServerLimitsConfiguration;
import com.io7m.cardant.server.api.CAServerOpenTelemetryConfiguration;
import com.io7m.cardant.server.api.CAServerOpenTelemetryConfiguration.CALogs;
import com.io7m.cardant.server.api.CAServerOpenTelemetryConfiguration.CAMetrics;
import com.io7m.cardant.server.api.CAServerOpenTelemetryConfiguration.CATraces;
import com.io7m.cardant.server.service.configuration.CAServerConfigurationParsers;
import com.io7m.cardant.tests.CATestDirectories;
import com.io7m.cardant.tls.CATLSDisabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CAServerConfigurationTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAServerConfigurationTest.class);

  @Test
  public void testParse(
    final @TempDir Path directory)
    throws Exception
  {
    final var file =
      CATestDirectories.resourceOf(
        CAServerConfigurationTest.class,
        directory,
        "config0.xml"
      );

    final var parsers =
      new CAServerConfigurationParsers();

    final var configFile =
      parsers.parseFile(
        file,
        status -> ParseStatusLogging.logWithAll(LOG, status)
      );

    final var configuration =
      CAServerConfigurations.ofFile(
        Locale.getDefault(),
        Clock.systemUTC(),
        configFile
      );

    assertEquals(
      new CADatabaseConfiguration(
        "cardant_install",
        "892a2b68-2ddf-478a-a8ab-37172f6ac2fe",
        "e61135dc-1d3f-4ab2-85ef-95ef49d66285",
        Optional.of("c2026069-97e7-45b1-85c4-a2349bbb847b"),
        "db.example.com",
        5432,
        "cardant",
        CADatabaseCreate.CREATE_DATABASE,
        CADatabaseUpgrade.UPGRADE_DATABASE,
        "english",
        configuration.databaseConfiguration().clock(),
        configuration.databaseConfiguration().strings()
      ),
      configuration.databaseConfiguration()
    );

    assertEquals(
      new CAServerHTTPServiceConfiguration(
        "[::]",
        30000,
        URI.create("http://cardant.example.com:30000"),
        Optional.of(Duration.ofMinutes(30L)),
        CATLSDisabled.TLS_DISABLED
      ),
      configuration.inventoryApiConfiguration()
    );

    assertEquals(
      new CAServerLimitsConfiguration(
        10000000L,
        100000L
      ),
      configuration.limitsConfiguration()
    );

    assertEquals(
      new CAServerIdstoreConfiguration(
        URI.create("http://idstore.example.com:50000"),
        URI.create("http://idstore.example.com:50001/reset")
      ),
      configuration.idstoreConfiguration()
    );

    assertEquals(
      Optional.of(
        new CAServerOpenTelemetryConfiguration(
          "cardant01",
          Optional.of(
            new CALogs(
              URI.create("http://logs.example.com:4317"),
              CAServerOpenTelemetryConfiguration.CAOTLPProtocol.GRPC
            )
          ),
          Optional.of(
            new CAMetrics(
              URI.create("http://metrics.example.com:4317"),
              CAServerOpenTelemetryConfiguration.CAOTLPProtocol.GRPC
            )
          ),
          Optional.of(
            new CATraces(
              URI.create("http://traces.example.com:4317"),
              CAServerOpenTelemetryConfiguration.CAOTLPProtocol.GRPC
            )
          )
        )
      ),
      configuration.openTelemetry()
    );
  }
}
