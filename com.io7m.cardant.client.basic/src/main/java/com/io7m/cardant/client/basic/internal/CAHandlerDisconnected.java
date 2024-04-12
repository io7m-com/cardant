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
import com.io7m.cardant.client.api.CAClientTransferStatistics;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.hibiscus.api.HBClientHandlerType;
import com.io7m.hibiscus.api.HBConnectionError;
import com.io7m.hibiscus.api.HBConnectionResultType;
import com.io7m.hibiscus.api.HBTransportClosed;
import com.io7m.hibiscus.api.HBTransportType;

import java.net.http.HttpClient;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The initial "disconnected" protocol handler.
 */

final class CAHandlerDisconnected extends CAHandlerAbstract
{
  private final HBTransportType<CAIMessageType, CAClientException> transport;
  private final Supplier<HttpClient> httpClients;

  /**
   * Construct a handler.
   *
   * @param inConfiguration The configuration
   * @param inStrings       The string resources
   * @param inHttpClients    The client supplier
   */

  CAHandlerDisconnected(
    final CAClientConfiguration inConfiguration,
    final CAStrings inStrings,
    final Supplier<HttpClient> inHttpClients)
  {
    super(inConfiguration, inStrings);

    this.transport =
      new HBTransportClosed<>(CAClientException::ofException);
    this.httpClients =
      Objects.requireNonNull(inHttpClients, "inHttpClients");
  }

  @Override
  public HBConnectionResultType<
      CAIMessageType,
      CAClientConnectionParameters,
      HBClientHandlerType<
        CAIMessageType,
        CAClientConnectionParameters,
        CAClientException>,
      CAClientException>
  doConnect(
    final CAClientConnectionParameters parameters)
    throws InterruptedException
  {
    try {
      final var client =
        this.httpClients.get();

      final var newTransport =
        CAProtocolNegotiation.negotiateTransport(
          this.configuration(),
          parameters,
          client,
          this.strings()
        );

      final var newHandler =
        new CAHandlerConnected(
          this.configuration(),
          this.strings(),
          newTransport
        );

      return newHandler.doConnect(parameters);
    } catch (final CAClientException e) {
      return new HBConnectionError<>(e);
    }
  }

  @Override
  public HBTransportType<CAIMessageType, CAClientException> transport()
  {
    return this.transport;
  }

  @Override
  public boolean isClosed()
  {
    return this.transport.isClosed();
  }

  @Override
  public void close()
    throws CAClientException
  {
    this.transport.close();
  }

  @Override
  public String toString()
  {
    return "[%s 0x%s]".formatted(
      this.getClass().getSimpleName(),
      Integer.toUnsignedString(this.hashCode(), 16)
    );
  }

  @Override
  public Optional<CAUserID> userId()
  {
    return Optional.empty();
  }

  @Override
  public Path onExecuteFileDownload(
    final CAFileID fileID,
    final Path file,
    final Path fileTmp,
    final long size,
    final String hashAlgorithm,
    final String hashValue,
    final Consumer<CAClientTransferStatistics> statistics)
    throws CAClientException
  {
    throw super.onNotConnected();
  }

  @Override
  public void onExecuteFileUpload(
    final CAFileID fileID,
    final Path file,
    final String contentType,
    final String description,
    final Consumer<CAClientTransferStatistics> statistics)
    throws CAClientException
  {
    throw super.onNotConnected();
  }

  @Override
  public List<CAIResponseType> transaction(
    final List<CAICommandType<?>> commands)
    throws CAClientException
  {
    throw super.onNotConnected();
  }
}
