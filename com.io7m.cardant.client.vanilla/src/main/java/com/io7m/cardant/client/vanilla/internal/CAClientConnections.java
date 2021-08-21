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

package com.io7m.cardant.client.vanilla.internal;

import com.io7m.anethum.common.ParseException;
import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientEventType;
import com.io7m.cardant.client.vanilla.CAClientStrings;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageParsers;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageSerializers;
import com.io7m.cardant.protocol.inventory.v1.CA1InventorySchemas;
import com.io7m.cardant.protocol.versioning.CAVersioningMessageParserFactoryType;
import com.io7m.cardant.protocol.versioning.messages.CAVersion;
import com.io7m.cardant.protocol.versioning.messages.CAVersioningAPIVersioning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.concurrent.SubmissionPublisher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.io7m.cardant.client.api.CAClientEventStatusChanged.CLIENT_NEGOTIATING_PROTOCOLS;

public final class CAClientConnections
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAClientConnections.class);

  private CAClientConnections()
  {

  }

  public static CAClientConnectionType open(
    final SubmissionPublisher<CAClientEventType> events,
    final CAVersioningMessageParserFactoryType parsers,
    final HttpClient httpClient,
    final CAClientConfiguration configuration,
    final CAClientStrings strings)
    throws IOException, ParseException, InterruptedException
  {
    LOG.debug("retrieving supported protocols");

    events.submit(CLIENT_NEGOTIATING_PROTOCOLS);

    final var versions =
      fetchSupportedVersions(parsers, httpClient, strings, configuration);
    final var version =
      findBestCardantProtocol(strings, versions);

    if (version.version() == 1L) {
      LOG.debug("selected protocol {}", version.name());
      return new CAClientConnectionV1(
        events,
        new CA1InventoryMessageSerializers(),
        new CA1InventoryMessageParsers(),
        strings,
        httpClient,
        configuration,
        version
      );
    }

    throw new IOException(noSupportedProtocols(strings, versions));
  }

  private static CAVersioningAPIVersioning fetchSupportedVersions(
    final CAVersioningMessageParserFactoryType parsers,
    final HttpClient httpClient,
    final CAClientStrings strings,
    final CAClientConfiguration configuration)
    throws IOException, InterruptedException, ParseException
  {
    final var targetURI =
      configuration.baseURI();

    final var versionRequest =
      HttpRequest.newBuilder(targetURI)
        .build();

    final var response =
      httpClient.send(
        versionRequest,
        HttpResponse.BodyHandlers.ofInputStream());

    if (response.statusCode() != 200) {
      throw new IOException();
    }

    final var versions =
      parsers.parse(targetURI, response.body());

    if (versions instanceof CAVersioningAPIVersioning versioning) {
      return versioning;
    }

    throw new IOException(
      strings.format(
        "errorExpectedVersioningData",
        CAVersioningAPIVersioning.class.getSimpleName(),
        versions.getClass().getSimpleName())
    );
  }

  private static String noSupportedProtocols(
    final CAClientStrings strings,
    final CAVersioningAPIVersioning versions)
  {
    final var clientSupported =
      Stream.of(CA1InventorySchemas.protocol1Namespace().toString())
        .collect(Collectors.joining("\n    "));

    final var serverSupported =
      versions.apis()
        .stream()
        .flatMap(api -> api.versions().stream())
        .map(CAVersion::name)
        .collect(Collectors.joining("\n    "));

    return strings.format(
      "errorNoSupportedVersions",
      clientSupported,
      serverSupported);
  }

  private static CAVersion findBestCardantProtocol(
    final CAClientStrings strings,
    final CAVersioningAPIVersioning versions)
    throws IOException
  {
    LOG.debug("selecting protocol");

    for (final var api : versions.apis()) {
      for (final var version : api.versions()) {
        if (Objects.equals(
          version.name(),
          CA1InventorySchemas.protocol1Namespace().toString())) {
          return version;
        }
      }
    }
    throw new IOException(noSupportedProtocols(strings, versions));
  }
}
