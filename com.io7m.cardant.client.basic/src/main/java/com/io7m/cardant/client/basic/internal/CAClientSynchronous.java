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
import com.io7m.cardant.client.api.CAClientTransferStatistics;
import com.io7m.cardant.client.api.CAClientUnit;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseBlame;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.hibiscus.api.HBResultType;
import com.io7m.hibiscus.basic.HBClientSynchronousAbstract;

import java.net.http.HttpClient;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorIo;
import static java.lang.Integer.toUnsignedString;

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
        ca.requestId().orElseGet(CAUUIDs::nullUUID),
        ca.message(),
        ca.errorCode(),
        ca.attributes(),
        ca.remediatingAction(),
        Optional.of(ca),
        CAIResponseBlame.BLAME_CLIENT
      );
    }

    return new CAIResponseError(
      CAUUIDs.nullUUID(),
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
  public Optional<CAUserID> userId()
  {
    return this.subscriber.getUserId();
  }

  @Override
  public HBResultType<Path, CAIResponseError> fileDownload(
    final CAFileID fileID,
    final Path file,
    final Path fileTmp,
    final long size,
    final String hashAlgorithm,
    final String hashValue,
    final Consumer<CAClientTransferStatistics> statistics)
    throws InterruptedException
  {
    Objects.requireNonNull(fileID, "fileID");

    final var handler = (CAHandlerType) this.currentHandler();
    return handler.onExecuteFileDownload(
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
  public HBResultType<CAFileID, CAIResponseError> fileUpload(
    final CAFileID fileID,
    final Path file,
    final String contentType,
    final String description,
    final Consumer<CAClientTransferStatistics> statistics)
    throws InterruptedException
  {
    final var handler = (CAHandlerType) this.currentHandler();
    return handler.onExecuteFileUpload(
      fileID,
      file,
      contentType,
      description,
      statistics
    );
  }

  @Override
  public HBResultType<CAClientUnit, CAIResponseError> garbage()
    throws InterruptedException
  {
    final var handler = (CAHandlerType) this.currentHandler();
    return handler.onExecuteGarbage();
  }

  @Override
  public HBResultType<CAClientUnit, CAIResponseError> invalid()
    throws InterruptedException
  {
    final var handler = (CAHandlerType) this.currentHandler();
    return handler.onExecuteInvalid();
  }

  @Override
  public String toString()
  {
    return "[CAClientSynchronous 0x%s]"
      .formatted(toUnsignedString(this.hashCode(), 16));
  }
}
