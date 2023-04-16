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
import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.hibiscus.api.HBResultFailure;
import com.io7m.hibiscus.api.HBResultType;

import java.io.InputStream;
import java.net.http.HttpClient;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * The initial "disconnected" protocol handler.
 */

public final class CAHandlerDisconnected extends CAHandlerAbstract
{
  CAHandlerDisconnected(
    final CAClientConfiguration inConfiguration,
    final CAStrings inStrings,
    final HttpClient inHttpClient)
  {
    super(inConfiguration, inStrings, inHttpClient);
  }

  @Override
  public void pollEvents()
  {

  }

  @Override
  public <R extends CAIResponseType> HBResultType<R, CAIResponseError> executeCommand(
    final CAICommandType<R> command)
  {
    return this.notLoggedIn();
  }

  private <A> HBResultFailure<A, CAIResponseError> notLoggedIn()
  {
    return new HBResultFailure<>(
      new CAIResponseError(
        UUID.randomUUID(),
        this.strings().format("notLoggedIn"),
        CAStandardErrorCodes.errorNotLoggedIn(),
        Map.of(),
        Optional.empty()
      )
    );
  }

  @Override
  public boolean isConnected()
  {
    return false;
  }

  @Override
  public HBResultType<CANewHandler, CAIResponseError> login()
    throws InterruptedException
  {
    try {
      final var handler =
        CAProtocolNegotiation.negotiateProtocolHandler(
          this.configuration(),
          this.httpClient(),
          this.strings()
        );

      return handler.login();
    } catch (final CAClientException e) {
      return new HBResultFailure<>(
        new CAIResponseError(
          UUID.randomUUID(),
          e.summary(),
          e.errorCode(),
          e.attributes(),
          Optional.of(e)
        )
      );
    }
  }

  @Override
  public HBResultType<InputStream, CAIResponseError> fileData(
    final CAFileID id)
  {
    return this.notLoggedIn();
  }
}
