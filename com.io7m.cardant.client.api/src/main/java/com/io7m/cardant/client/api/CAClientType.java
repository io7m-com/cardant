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

package com.io7m.cardant.client.api;

import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.hibiscus.api.HBClientType;
import com.io7m.hibiscus.api.HBConnectionError;
import com.io7m.hibiscus.api.HBConnectionFailed;
import com.io7m.hibiscus.api.HBConnectionParametersType;
import com.io7m.hibiscus.api.HBConnectionSucceeded;
import com.io7m.hibiscus.api.HBMessageType;
import com.io7m.repetoir.core.RPServiceType;

import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

/**
 * The type of client instances.
 */

public interface CAClientType
  extends HBClientType<
  CAIMessageType,
  CAClientConnectionParameters,
  CAClientException>,
  RPServiceType
{
  @Override
  default String description()
  {
    return "A synchronous inventory client.";
  }

  /**
   * @return The current logged-in user
   */

  Optional<CAUserID> userId();

  /**
   * Download the data associated with the given file. The file will be
   * downloaded to a temporary file and then, if everything succeeds and the
   * hash value matches, the temporary file will atomically replace the
   * output file.
   *
   * @param fileID        The file ID
   * @param file          The output file
   * @param fileTmp       The temporary output file
   * @param size          The expected size
   * @param hashAlgorithm The hash algorithm
   * @param hashValue     The expected hash value
   * @param statistics    A receiver of transfer statistics
   *
   * @throws CAClientException    On errors
   * @throws InterruptedException On interruption
   */

  void fileDownload(
    CAFileID fileID,
    Path file,
    Path fileTmp,
    long size,
    String hashAlgorithm,
    String hashValue,
    Consumer<CAClientTransferStatistics> statistics)
    throws InterruptedException, CAClientException;

  /**
   * Upload the data associated with the given file.
   *
   * @param fileID      The file ID
   * @param file        The input file
   * @param contentType The content type
   * @param description The file description
   * @param statistics  A receiver of transfer statistics
   *
   * @throws CAClientException    On errors
   * @throws InterruptedException On interruption
   */

  void fileUpload(
    CAFileID fileID,
    Path file,
    String contentType,
    String description,
    Consumer<CAClientTransferStatistics> statistics)
    throws InterruptedException, CAClientException;

  /**
   * Call {@link #connect(HBConnectionParametersType)} but throw an exception
   * if the result is an {@link CAIResponseError}.
   *
   * @param parameters The connection parameters
   *
   * @return The success message
   *
   * @throws CAClientException    On errors
   * @throws InterruptedException On interruption
   */

  default CAIMessageType connectOrThrow(
    final CAClientConnectionParameters parameters)
    throws CAClientException, InterruptedException
  {
    final var r =
      this.connect(parameters);

    return switch (r) {
      case final HBConnectionError<
        CAIMessageType, CAClientConnectionParameters, ?, CAClientException>
        error -> {
        throw CAClientException.ofException(error.exception());
      }
      case final HBConnectionFailed<
        CAIMessageType, CAClientConnectionParameters, ?, CAClientException>
        failed -> {
        if (failed.message() instanceof final CAIResponseError error) {
          throw CAClientException.ofError(error);
        }
        throw new IllegalStateException();
      }
      case final HBConnectionSucceeded<
        CAIMessageType, CAClientConnectionParameters, ?, CAClientException>
        succeeded -> {
        yield succeeded.message();
      }
    };
  }

  /**
   * Call {@link #sendAndWait(HBMessageType, Duration)} but throw an exception
   * if the result is an {@link CAIResponseError}.
   *
   * @param message The message
   * @param timeout The timeout
   * @param <R>     The type of results
   *
   * @return The result
   *
   * @throws CAClientException    On errors
   * @throws InterruptedException On interruption
   * @throws TimeoutException     On timeouts
   */

  default <R extends CAIResponseType> R sendAndWaitOrThrow(
    final CAICommandType<R> message,
    final Duration timeout)
    throws CAClientException, InterruptedException, TimeoutException
  {
    final var r =
      this.sendAndWait(message, timeout);

    return switch (r) {
      case final CAIResponseError error -> {
        throw CAClientException.ofError(error);
      }
      default -> (R) r;
    };
  }

  /**
   * Execute all the given commands in order, in a single transaction.
   *
   * @param commands The commands
   *
   * @return The responses
   */

  List<CAIResponseType> transaction(
    List<CAICommandType<?>> commands)
    throws CAClientException, InterruptedException;
}
