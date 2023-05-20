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
import com.io7m.cardant.client.api.CAClientSynchronousType;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseBlame;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.hibiscus.api.HBResultType;
import com.io7m.hibiscus.basic.HBClientSynchronousAbstract;

import java.io.InputStream;
import java.net.http.HttpClient;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorIo;

/**
 * The synchronous client.
 */

public final class CAClientSynchronous
  extends HBClientSynchronousAbstract<
  CAClientException,
  CAICommandType<?>,
  CAIResponseType,
  CAIResponseType,
  CAIResponseError,
  CAClientEventType,
  CAClientCredentials>
  implements CAClientSynchronousType
{
  private final CAClientUserIdSubscriber subscriber;

  /**
   * The synchronous client.
   *
   * @param inConfiguration The configuration
   * @param inHttpClient    The HTTP client
   * @param inStrings       The string resources
   */

  public CAClientSynchronous(
    final CAClientConfiguration inConfiguration,
    final CAStrings inStrings,
    final HttpClient inHttpClient)
  {
    super(
      new CAHandlerDisconnected(inConfiguration, inStrings, inHttpClient),
      CAClientSynchronous::ofException
    );

    this.subscriber =
      new CAClientUserIdSubscriber();
    this.state()
      .subscribe(this.subscriber);
  }

  private static CAIResponseError ofException(
    final Throwable ex)
  {
    if (ex instanceof final CAClientException ca) {
      return new CAIResponseError(
        ca.requestId().orElse(new UUID(0L, 0L)),
        ca.message(),
        ca.errorCode(),
        ca.attributes(),
        ca.remediatingAction(),
        Optional.of(ca),
        CAIResponseBlame.BLAME_CLIENT
      );
    }

    return new CAIResponseError(
      new UUID(0L, 0L),
      Objects.requireNonNullElse(
        ex.getMessage(),
        ex.getClass().getSimpleName()),
      errorIo(),
      Map.of(),
      Optional.empty(),
      Optional.of(ex),
      CAIResponseBlame.BLAME_CLIENT
    );
  }

  @Override
  public Optional<UUID> userId()
  {
    return this.subscriber.getUserId();
  }

  @Override
  public HBResultType<InputStream, CAIResponseError> fileData(
    final CAFileID fileID)
    throws InterruptedException
  {
    Objects.requireNonNull(fileID, "fileID");

    final var handler = (CAHandlerType) this.currentHandler();
    return handler.onExecuteFileData(fileID);
  }
}
