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

package com.io7m.cardant.client.basic.internal;

import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientConnectionParameters;
import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.protocol.inventory.cb.CAI1Messages;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.genevan.core.GenProtocolException;
import com.io7m.genevan.core.GenProtocolIdentifier;
import com.io7m.genevan.core.GenProtocolServerEndpointType;
import com.io7m.genevan.core.GenProtocolSolved;
import com.io7m.genevan.core.GenProtocolSolver;
import com.io7m.genevan.core.GenProtocolVersion;
import com.io7m.verdant.core.VProtocolException;
import com.io7m.verdant.core.VProtocols;
import com.io7m.verdant.core.cb.VProtocolMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.cardant.client.basic.internal.CACompression.decompressResponse;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorHttpMethod;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorIo;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNoSupportedProtocols;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorProtocol;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_HTTP;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_SERVER_CONNECT;
import static com.io7m.cardant.strings.CAStringConstants.URI;
import static java.net.http.HttpResponse.BodyHandlers.ofByteArray;

/**
 * Functions to negotiate protocols.
 */

public final class CAProtocolNegotiation
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAProtocolNegotiation.class);

  private CAProtocolNegotiation()
  {

  }

  private static List<CAServerEndpoint> fetchSupportedVersions(
    final URI base,
    final HttpClient httpClient,
    final CAStrings strings)
    throws InterruptedException, CAClientException
  {
    LOG.debug("Retrieving supported server protocols");

    final var request =
      HttpRequest.newBuilder(base)
        .GET()
        .build();

    final HttpResponse<byte[]> response;
    try {
      response = httpClient.send(request, ofByteArray());
    } catch (final IOException e) {
      throw new CAClientException(
        Objects.requireNonNullElse(
          e.getMessage(),
          strings.format(ERROR_SERVER_CONNECT)
        ),
        e,
        errorIo(),
        Map.ofEntries(
          Map.entry(strings.format(URI), base.toString())
        ),
        Optional.empty(),
        Optional.empty()
      );
    }

    LOG.debug("Server: Status {}", response.statusCode());

    if (response.statusCode() >= 400) {
      throw new CAClientException(
        strings.format(ERROR_HTTP, Integer.valueOf(response.statusCode())),
        errorHttpMethod(),
        Map.ofEntries(
          Map.entry(strings.format(URI), base.toString())
        ),
        Optional.empty(),
        Optional.empty()
      );
    }

    final var protocols =
      VProtocolMessages.create();

    final VProtocols message;
    try {
      final var body =
        decompressResponse(response, response.headers());

      message = protocols.parse(base, body);
    } catch (final VProtocolException e) {
      throw new CAClientException(
        e.getMessage(),
        e,
        errorProtocol(),
        Map.ofEntries(
          Map.entry(strings.format(URI), base.toString())
        ),
        Optional.empty(),
        Optional.empty()
      );
    } catch (final IOException e) {
      throw new CAClientException(
        Objects.requireNonNullElse(
          e.getMessage(),
          e.getClass().getSimpleName()
        ),
        e,
        errorIo(),
        Map.ofEntries(
          Map.entry(strings.format(URI), base.toString())
        ),
        Optional.empty(),
        Optional.empty()
      );
    }

    return message.protocols()
      .stream()
      .map(v -> {
        return new CAServerEndpoint(
          new GenProtocolIdentifier(
            v.id().toString(),
            new GenProtocolVersion(
              new BigInteger(Long.toUnsignedString(v.versionMajor())),
              new BigInteger(Long.toUnsignedString(v.versionMinor()))
            )
          ),
          v.endpointPath()
        );
      }).toList();
  }

  private record CAServerEndpoint(
    GenProtocolIdentifier supported,
    String endpoint)
    implements GenProtocolServerEndpointType
  {
    CAServerEndpoint
    {
      Objects.requireNonNull(supported, "supported");
      Objects.requireNonNull(endpoint, "endpoint");
    }
  }

  /**
   * Negotiate a transport.
   *
   * @param configuration The configuration
   * @param credentials   The credentials
   * @param httpClient    The HTTP client
   * @param strings       The string resources
   *
   * @return The transport
   *
   * @throws CAClientException    On errors
   * @throws InterruptedException On interruption
   */

  public static CATransportType negotiateTransport(
    final CAClientConfiguration configuration,
    final CAClientConnectionParameters credentials,
    final HttpClient httpClient,
    final CAStrings strings)
    throws CAClientException, InterruptedException
  {
    Objects.requireNonNull(configuration, "configuration");
    Objects.requireNonNull(credentials, "credentials");
    Objects.requireNonNull(httpClient, "httpClient");
    Objects.requireNonNull(strings, "strings");

    final var clientSupports =
      List.of(
        new CATransports1()
      );

    final var serverProtocols =
      fetchSupportedVersions(credentials.baseURI(), httpClient, strings);

    LOG.debug("Server supports {} protocols", serverProtocols.size());

    final var solver =
      GenProtocolSolver.<CATransportFactoryType, CAServerEndpoint>
        create(configuration.locale());

    final GenProtocolSolved<CATransportFactoryType, CAServerEndpoint> solved;
    try {
      solved = solver.solve(
        serverProtocols,
        clientSupports,
        List.of(CAI1Messages.protocolId().toString())
      );
    } catch (final GenProtocolException e) {
      throw new CAClientException(
        e.getMessage(),
        e,
        errorNoSupportedProtocols(),
        Map.of(),
        Optional.empty(),
        Optional.empty()
      );
    }

    final var serverEndpoint =
      solved.serverEndpoint();
    final var target =
      credentials.baseURI()
        .resolve(serverEndpoint.endpoint())
        .normalize();

    final var protocol = serverEndpoint.supported();
    LOG.debug(
      "Using protocol {} {}.{} at endpoint {}",
      protocol.identifier(),
      protocol.version().versionMajor(),
      protocol.version().versionMinor(),
      target
    );

    return solved.clientHandler().createTransport(
      configuration,
      httpClient,
      strings,
      target
    );
  }
}
