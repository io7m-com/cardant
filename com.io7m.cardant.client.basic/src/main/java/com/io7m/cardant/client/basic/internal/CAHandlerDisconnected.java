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
import com.io7m.cardant.client.api.CAClientCredentials;
import com.io7m.cardant.client.api.CAClientEventType;
import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.client.api.CAClientTransferStatistics;
import com.io7m.cardant.client.api.CAClientUnit;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseBlame;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.hibiscus.api.HBResultFailure;
import com.io7m.hibiscus.api.HBResultType;
import com.io7m.hibiscus.basic.HBClientNewHandler;

import java.net.http.HttpClient;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static com.io7m.cardant.strings.CAStringConstants.ERROR_NOT_LOGGED_IN;

/**
 * The initial "disconnected" protocol handler.
 */

public final class CAHandlerDisconnected
  extends CAHandlerAbstract
{
  CAHandlerDisconnected(
    final CAClientConfiguration inConfiguration,
    final CAStrings inStrings,
    final HttpClient inHttpClient)
  {
    super(inConfiguration, inStrings, inHttpClient);
  }

  private <A> HBResultFailure<A, CAIResponseError> notLoggedIn()
  {
    return new HBResultFailure<>(
      new CAIResponseError(
        UUID.randomUUID(),
        this.strings().format(ERROR_NOT_LOGGED_IN),
        CAStandardErrorCodes.errorNotLoggedIn(),
        Map.of(),
        Optional.empty(),
        Optional.empty(),
        CAIResponseBlame.BLAME_CLIENT
      )
    );
  }

  @Override
  public boolean onIsConnected()
  {
    return false;
  }

  @Override
  public List<CAClientEventType> onPollEvents()
  {
    return List.of();
  }

  @Override
  public HBResultType<
    HBClientNewHandler<
      CAClientException,
      CAICommandType<?>,
      CAIResponseType,
      CAIResponseType,
      CAIResponseError,
      CAClientEventType,
      CAClientCredentials>,
    CAIResponseError>
  onExecuteLogin(
    final CAClientCredentials credentials)
    throws InterruptedException
  {
    try {
      final var handler =
        CAProtocolNegotiation.negotiateProtocolHandler(
          this.configuration(),
          credentials,
          this.httpClient(),
          this.strings()
        );

      return handler.onExecuteLogin(credentials);
    } catch (final CAClientException e) {
      return new HBResultFailure<>(
        new CAIResponseError(
          UUID.randomUUID(),
          e.message(),
          e.errorCode(),
          e.attributes(),
          e.remediatingAction(),
          Optional.of(e),
          CAIResponseBlame.BLAME_CLIENT
        )
      );
    }
  }

  @Override
  public HBResultType<CAIResponseType, CAIResponseError> onExecuteCommand(
    final CAICommandType<?> command)
  {
    return this.notLoggedIn();
  }

  @Override
  public void onDisconnect()
  {

  }

  @Override
  public HBResultType<Path, CAIResponseError> onExecuteFileDownload(
    final CAFileID fileID,
    final Path file,
    final Path fileTmp,
    final long size,
    final String hashAlgorithm,
    final String hashValue,
    final Consumer<CAClientTransferStatistics> statistics)
  {
    return this.notLoggedIn();
  }

  @Override
  public HBResultType<CAFileID, CAIResponseError> onExecuteFileUpload(
    final CAFileID fileID,
    final Path file,
    final String contentType,
    final String description,
    final Consumer<CAClientTransferStatistics> statisticsConsumer)
  {
    return this.notLoggedIn();
  }


  @Override
  public HBResultType<CAClientUnit, CAIResponseError> onExecuteGarbage()
  {
    return this.notLoggedIn();
  }

  @Override
  public HBResultType<CAClientUnit, CAIResponseError> onExecuteInvalid()
  {
    return this.notLoggedIn();
  }
}
