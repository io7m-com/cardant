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

import com.io7m.cardant.client.api.CAClientAsynchronousType;
import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientCredentials;
import com.io7m.cardant.client.api.CAClientEventType;
import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.client.api.CAClientSynchronousType;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.hibiscus.api.HBResultType;
import com.io7m.hibiscus.basic.HBClientAsynchronousAbstract;

import java.io.InputStream;
import java.net.http.HttpClient;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The asynchronous client.
 */

public final class CAClientAsynchronous
  extends HBClientAsynchronousAbstract<
  CAClientException,
  CAICommandType<?>,
  CAIResponseType,
  CAIResponseType,
  CAIResponseError,
  CAClientEventType,
  CAClientCredentials>
  implements CAClientAsynchronousType
{
  private final CAClientUserIdSubscriber subscriber;

  /**
   * The asynchronous client.
   *
   * @param inConfiguration The configuration
   * @param inHttpClient    The HTTP client
   * @param inStrings       The string resources
   */

  public CAClientAsynchronous(
    final CAClientConfiguration inConfiguration,
    final CAStrings inStrings,
    final HttpClient inHttpClient)
  {
    super(
      new CAClientSynchronous(inConfiguration, inStrings, inHttpClient),
      "com.io7m.cardant.client.basic"
    );

    this.subscriber =
      new CAClientUserIdSubscriber();
    this.state()
      .subscribe(this.subscriber);
  }

  @Override
  public Optional<UUID> userId()
  {
    return this.subscriber.getUserId();
  }

  @Override
  public CompletableFuture<HBResultType<InputStream, CAIResponseError>>
  fileDataAsync(
    final CAFileID fileID)
  {
    super.checkNotClosingOrClosed();

    final var future =
      new CompletableFuture<HBResultType<InputStream, CAIResponseError>>();
    this.commandExecutor().execute(() -> {
      try {
        final var client = (CAClientSynchronousType) this.delegate();
        future.complete(client.fileData(fileID));
      } catch (final Throwable e) {
        future.completeExceptionally(e);
      }
    });
    return future;
  }
}
