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
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.hibiscus.api.HBClientSynchronousType;
import com.io7m.hibiscus.api.HBResultType;
import com.io7m.repetoir.core.RPServiceType;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * The type of client instances.
 */

public interface CAClientSynchronousType extends HBClientSynchronousType<
  CAClientException,
  CAICommandType<?>,
  CAIResponseType,
  CAIResponseType,
  CAIResponseError,
  CAClientEventType,
  CAClientCredentials>,
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
   * @return The result
   *
   * @throws InterruptedException On interruption
   */

  HBResultType<Path, CAIResponseError> fileDownload(
    CAFileID fileID,
    Path file,
    Path fileTmp,
    long size,
    String hashAlgorithm,
    String hashValue,
    Consumer<CAClientTransferStatistics> statistics)
    throws InterruptedException;

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
   * @return The result
   *
   * @throws CAClientException    On errors
   * @throws InterruptedException On interruption
   */

  default Path fileDownloadOrThrow(
    final CAFileID fileID,
    final Path file,
    final Path fileTmp,
    final long size,
    final String hashAlgorithm,
    final String hashValue,
    final Consumer<CAClientTransferStatistics> statistics)
    throws InterruptedException, CAClientException
  {
    return this.fileDownload(
        fileID,
        file,
        fileTmp,
        size,
        hashAlgorithm,
        hashValue,
        statistics)
      .orElseThrow(CAClientException::ofError);
  }

  /**
   * Upload the data associated with the given file.
   *
   * @param fileID      The file ID
   * @param file        The input file
   * @param contentType The content type
   * @param description The file description
   * @param statistics  A receiver of transfer statistics
   *
   * @return The result
   *
   * @throws InterruptedException On interruption
   */

  HBResultType<CAFileID, CAIResponseError> fileUpload(
    CAFileID fileID,
    Path file,
    String contentType,
    String description,
    Consumer<CAClientTransferStatistics> statistics)
    throws InterruptedException;

  /**
   * Upload the data associated with the given file.
   *
   * @param fileID      The file ID
   * @param file        The input file
   * @param contentType The content type
   * @param description The file description
   * @param statistics  A receiver of transfer statistics
   *
   * @return The result
   *
   * @throws CAClientException    On errors
   * @throws InterruptedException On interruption
   */

  default CAFileID fileUploadOrThrow(
    final CAFileID fileID,
    final Path file,
    final String contentType,
    final String description,
    final Consumer<CAClientTransferStatistics> statistics)
    throws InterruptedException, CAClientException
  {
    return this.fileUpload(fileID, file, contentType, description, statistics)
      .orElseThrow(CAClientException::ofError);
  }

  /**
   * Send random garbage to the server.
   *
   * @return The result
   *
   * @throws InterruptedException On interruption
   */

  HBResultType<CAClientUnit, CAIResponseError> garbage()
    throws InterruptedException;

  /**
   * Send random garbage to the server.
   *
   * @throws CAClientException    On errors
   * @throws InterruptedException On interruption
   */

  default void garbageOrElseThrow()
    throws CAClientException, InterruptedException
  {
    this.garbage().orElseThrow(CAClientException::ofError);
  }

  /**
   * Send random garbage to the server.
   *
   * @return The result
   *
   * @throws InterruptedException On interruption
   */

  HBResultType<CAClientUnit, CAIResponseError> invalid()
    throws InterruptedException;

  /**
   * Send random garbage to the server.
   *
   * @throws CAClientException    On errors
   * @throws InterruptedException On interruption
   */

  default void invalidOrElseThrow()
    throws CAClientException, InterruptedException
  {
    this.invalid().orElseThrow(CAClientException::ofError);
  }

  /**
   * Log in synchronously, or throw an exception based on the failure response.
   *
   * @param credentials The credentials
   *
   * @return The result
   *
   * @throws CAClientException    On errors
   * @throws InterruptedException On interruption
   */

  default CAIResponseType loginOrElseThrow(
    final CAClientCredentials credentials)
    throws CAClientException, InterruptedException
  {
    return this.loginOrElseThrow(
      credentials,
      CAClientException::ofError
    );
  }

  /**
   * Execute the given command synchronously, or throw an exception based on
   * the failure response.
   *
   * @param command The command
   * @param <R>     The precise type of returned value
   *
   * @return The result
   *
   * @throws CAClientException    If command execution fails
   * @throws InterruptedException On interruption
   */

  default <R extends CAIResponseType> R executeOrElseThrow(
    final CAICommandType<R> command)
    throws CAClientException, InterruptedException
  {
    return (R) this.executeOrElseThrow(
      command,
      CAClientException::ofError
    );
  }
}
