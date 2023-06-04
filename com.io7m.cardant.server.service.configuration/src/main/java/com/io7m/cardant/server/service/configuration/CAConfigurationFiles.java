/*
 * Copyright © 2022 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.server.service.configuration;

import com.io7m.cardant.server.api.CAServerConfigurationFile;
import com.io7m.cardant.server.api.CAServerDatabaseConfiguration;
import com.io7m.cardant.server.api.CAServerDatabaseKind;
import com.io7m.cardant.server.api.CAServerHTTPConfiguration;
import com.io7m.cardant.server.api.CAServerHTTPServiceConfiguration;
import com.io7m.cardant.server.api.CAServerIdstoreConfiguration;
import com.io7m.cardant.server.api.CAServerOpenTelemetryConfiguration;
import com.io7m.cardant.server.api.CAServerOpenTelemetryConfiguration.CALogs;
import com.io7m.cardant.server.api.CAServerOpenTelemetryConfiguration.CAMetrics;
import com.io7m.cardant.server.api.CAServerOpenTelemetryConfiguration.CAOTLPProtocol;
import com.io7m.cardant.server.api.CAServerOpenTelemetryConfiguration.CATraces;
import com.io7m.cardant.server.service.configuration.xml.Database;
import com.io7m.cardant.server.service.configuration.xml.HTTPService;
import com.io7m.cardant.server.service.configuration.xml.IdStore;
import com.io7m.cardant.server.service.configuration.xml.Configuration;
import com.io7m.cardant.server.service.configuration.xml.DatabaseKind;
import com.io7m.cardant.server.service.configuration.xml.OpenTelemetry;
import com.io7m.cardant.server.service.configuration.xml.OpenTelemetryProtocol;
import com.io7m.repetoir.core.RPServiceType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.datatype.Duration;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * The configuration file parser.
 */

public final class CAConfigurationFiles
  implements RPServiceType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAConfigurationFiles.class);

  /**
   * The configuration file parser.
   */

  public CAConfigurationFiles()
  {

  }

  /**
   * Parse a configuration file.
   *
   * @param source The URI of the source file, for error messages
   * @param stream The input stream
   *
   * @return The file
   *
   * @throws IOException On errors
   */

  public CAServerConfigurationFile parse(
    final URI source,
    final InputStream stream)
    throws IOException
  {
    try {
      final var schemas =
        SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      final var schema =
        schemas.newSchema(
          CAConfigurationFiles.class.getResource(
            "/com/io7m/cardant/server/service/configuration/configuration-1.xsd")
        );

      final var context =
        JAXBContext.newInstance(
          "com.io7m.cardant.server.service.configuration.xml"
        );
      final var unmarshaller =
        context.createUnmarshaller();

      unmarshaller.setEventHandler(event -> {
        LOG.error(
          "{}:{}:{}: {}",
          event.getLocator().getURL(),
          Integer.valueOf(event.getLocator().getLineNumber()),
          Integer.valueOf(event.getLocator().getColumnNumber()),
          event.getMessage()
        );
        return true;
      });
      unmarshaller.setSchema(schema);

      final var streamSource =
        new StreamSource(stream, source.toString());
      final var configuration =
        (Configuration) unmarshaller.unmarshal(streamSource);

      return processConfiguration(configuration);
    } catch (final JAXBException | URISyntaxException | SAXException e) {
      throw new IOException(e);
    }
  }

  private static CAServerConfigurationFile processConfiguration(
    final Configuration configuration)
    throws URISyntaxException
  {
    return new CAServerConfigurationFile(
      new CAServerHTTPConfiguration(
        processInventory(configuration.getInventoryService())
      ),
      processDatabase(configuration.getDatabase()),
      processIdstore(configuration.getIdStore()),
      processOpenTelemetry(configuration.getOpenTelemetry())
    );
  }

  private static Optional<CAServerOpenTelemetryConfiguration> processOpenTelemetry(
    final OpenTelemetry openTelemetry)
  {
    if (openTelemetry == null) {
      return Optional.empty();
    }

    final var logs =
      Optional.ofNullable(openTelemetry.getLogs())
        .map(m -> new CALogs(
          URI.create(m.getEndpoint()),
          processProtocol(m.getProtocol())
        ));

    final var metrics =
      Optional.ofNullable(openTelemetry.getMetrics())
        .map(m -> new CAMetrics(
          URI.create(m.getEndpoint()),
          processProtocol(m.getProtocol())
        ));

    final var traces =
      Optional.ofNullable(openTelemetry.getTraces())
        .map(m -> new CATraces(
          URI.create(m.getEndpoint()),
          processProtocol(m.getProtocol())
        ));

    return Optional.of(
      new CAServerOpenTelemetryConfiguration(
        openTelemetry.getLogicalServiceName(),
        logs,
        metrics,
        traces
      )
    );
  }

  private static CAOTLPProtocol processProtocol(
    final OpenTelemetryProtocol protocol)
  {
    return switch (protocol) {
      case GRPC -> CAOTLPProtocol.GRPC;
      case HTTP -> CAOTLPProtocol.HTTP;
    };
  }

  private static CAServerIdstoreConfiguration processIdstore(
    final IdStore idStore)
    throws URISyntaxException
  {
    return new CAServerIdstoreConfiguration(
      new URI(idStore.getBaseURI()),
      new URI(idStore.getPasswordResetURI())
    );
  }

  private static CAServerDatabaseConfiguration processDatabase(
    final Database database)
  {
    return new CAServerDatabaseConfiguration(
      processDatabaseKind(database.getKind()),
      database.getUser(),
      database.getPassword(),
      database.getDatabaseAddress(),
      database.getDatabasePort().intValue(),
      database.getDatabaseName(),
      database.isCreate(),
      database.isUpgrade()
    );
  }

  private static CAServerDatabaseKind processDatabaseKind(
    final DatabaseKind kind)
  {
    return switch (kind) {
      case POSTGRESQL -> CAServerDatabaseKind.POSTGRESQL;
    };
  }

  private static CAServerHTTPServiceConfiguration processInventory(
    final HTTPService service)
    throws URISyntaxException
  {
    return new CAServerHTTPServiceConfiguration(
      service.getListenAddress(),
      service.getListenPort().intValue(),
      new URI(service.getExternalAddress()),
      processDuration(service.getSessionExpiration())
    );
  }

  private static Optional<java.time.Duration> processDuration(
    final Duration d)
  {
    if (d == null) {
      return Optional.empty();
    }

    var base = java.time.Duration.ofSeconds(0L);
    base = base.plusDays((long) d.getDays());
    base = base.plusHours((long) d.getHours());
    base = base.plusMinutes((long) d.getMinutes());
    base = base.plusSeconds((long) d.getSeconds());
    return Optional.of(base);
  }

  /**
   * Parse a configuration file.
   *
   * @param file The input file
   *
   * @return The file
   *
   * @throws IOException On errors
   */

  public CAServerConfigurationFile parse(
    final Path file)
    throws IOException
  {
    try (var stream = Files.newInputStream(file)) {
      return this.parse(file.toUri(), stream);
    }
  }

  @Override
  public String description()
  {
    return "Server configuration elements.";
  }

  @Override
  public String toString()
  {
    return "[CAConfigurationFiles 0x%s]"
      .formatted(Long.toUnsignedString((long) this.hashCode(), 16));
  }
}
