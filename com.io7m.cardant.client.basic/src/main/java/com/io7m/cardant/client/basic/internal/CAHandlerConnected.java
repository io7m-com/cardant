/*
 * Copyright © 2024 Mark Raynsford <code@io7m.com> https://www.io7m.com
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
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.protocol.inventory.CAICommandLogin;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseLogin;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.hibiscus.api.HBClientHandlerType;
import com.io7m.hibiscus.api.HBConnectionError;
import com.io7m.hibiscus.api.HBConnectionFailed;
import com.io7m.hibiscus.api.HBConnectionResultType;
import com.io7m.hibiscus.api.HBConnectionSucceeded;
import com.io7m.hibiscus.api.HBTransportType;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

final class CAHandlerConnected
  extends CAHandlerAbstract
{
  private final CATransportType transport;
  private CAICommandLogin mostRecentLogin;
  private Duration loginTimeout;
  private CAUserID userId;

  CAHandlerConnected(
    final CAClientConfiguration inConfiguration,
    final CAStrings inStrings,
    final CATransportType inTransport)
  {
    super(inConfiguration, inStrings);

    this.transport =
      Objects.requireNonNull(inTransport, "transport");
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
    final var commandLogin =
      new CAICommandLogin(
        parameters.username(),
        parameters.password(),
        parameters.metadata()
      );

    this.loginTimeout = parameters.loginTimeout();
    try {
      return this.doLogin(commandLogin);
    } catch (final CAClientException | TimeoutException e) {
      return new HBConnectionError<>(e);
    }
  }

  @Override
  public CAIMessageType sendAndWait(
    final CAIMessageType message,
    final Duration timeout)
    throws CAClientException, InterruptedException, TimeoutException
  {
    var attempt = 0;

    while (true) {
      ++attempt;

      final var response =
        this.transport.sendAndWait(message, timeout);

      if (response instanceof final CAIResponseError error) {
        if (!isAuthenticationError(error)) {
          return error;
        }

        if (attempt == 3) {
          return error;
        }

        this.doLogin(this.mostRecentLogin);
        continue;
      }

      return response;
    }
  }

  @Override
  public HBTransportType<CAIMessageType, CAClientException> transport()
  {
    return this.transport;
  }

  private HBConnectionResultType<
    CAIMessageType,
    CAClientConnectionParameters,
    HBClientHandlerType<
      CAIMessageType,
      CAClientConnectionParameters,
      CAClientException>,
    CAClientException>
  doLogin(
    final CAICommandLogin commandLogin)
    throws CAClientException,
    InterruptedException,
    TimeoutException
  {
    final var response =
      this.transport.sendAndWait(commandLogin, this.loginTimeout);

    return switch (response) {
      case final CAIResponseLogin login -> {
        this.mostRecentLogin = commandLogin;
        this.userId = login.userId();
        yield new HBConnectionSucceeded<>(login, this);
      }

      case final CAIResponseError error -> {
        yield new HBConnectionFailed<>(error);
      }

      default -> {
        yield new HBConnectionFailed<>(response);
      }
    };
  }

  private static boolean isAuthenticationError(
    final CAIResponseError error)
  {
    return Objects.equals(
      error.errorCode(),
      CAStandardErrorCodes.errorAuthentication()
    );
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
    return Optional.ofNullable(this.userId);
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
    throws InterruptedException, CAClientException
  {
    Objects.requireNonNull(fileID, "fileID");
    Objects.requireNonNull(file, "file");
    Objects.requireNonNull(fileTmp, "fileTmp");
    Objects.requireNonNull(hashAlgorithm, "hashAlgorithm");
    Objects.requireNonNull(hashValue, "hashValue");
    Objects.requireNonNull(statistics, "statistics");

    return this.transport.fileDownload(
      fileID,
      file,
      fileTmp,
      size,
      hashAlgorithm,
      hashValue,
      statistics
    );
  }

  @Override
  public void onExecuteFileUpload(
    final CAFileID fileID,
    final Path file,
    final String contentType,
    final String description,
    final Consumer<CAClientTransferStatistics> statistics)
    throws InterruptedException, CAClientException
  {
    Objects.requireNonNull(fileID, "fileID");
    Objects.requireNonNull(file, "file");
    Objects.requireNonNull(contentType, "fileTmp");
    Objects.requireNonNull(description, "hashAlgorithm");
    Objects.requireNonNull(statistics, "statistics");

    this.transport.fileUpload(
      fileID,
      file,
      contentType,
      description,
      statistics
    );
  }

  @Override
  public List<CAIResponseType> transaction(
    final List<CAICommandType<?>> commands)
    throws CAClientException, InterruptedException
  {
    return this.transport.transaction(commands);
  }
}
