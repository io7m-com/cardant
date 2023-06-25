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

import com.io7m.cardant.client.api.CAClientCredentials;
import com.io7m.cardant.client.api.CAClientEventType;
import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.client.api.CAClientTransferStatistics;
import com.io7m.cardant.client.api.CAClientUnit;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.hibiscus.api.HBResultType;
import com.io7m.hibiscus.basic.HBClientHandlerType;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * A versioned protocol handler.
 */

public interface CAHandlerType
  extends HBClientHandlerType<
  CAClientException,
  CAICommandType<?>,
  CAIResponseType,
  CAIResponseType,
  CAIResponseError,
  CAClientEventType,
  CAClientCredentials>
{
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

  HBResultType<Path, CAIResponseError> onExecuteFileDownload(
    CAFileID fileID,
    Path file,
    Path fileTmp,
    long size,
    String hashAlgorithm,
    String hashValue,
    Consumer<CAClientTransferStatistics> statistics)
    throws InterruptedException;

  /**
   * Upload the data associated with the given file.
   *
   * @param fileID      The file ID
   * @param file        The input file
   * @param contentType The content type
   * @param statistics  A receiver of transfer statistics
   *
   * @return The result
   *
   * @throws InterruptedException On interruption
   */

  HBResultType<CAFileID, CAIResponseError> onExecuteFileUpload(
    CAFileID fileID,
    Path file,
    String contentType,
    Consumer<CAClientTransferStatistics> statistics)
    throws InterruptedException;

  /**
   * Send garbage to the server.
   *
   * @return The result
   *
   * @throws InterruptedException On interruption
   */

  HBResultType<CAClientUnit, CAIResponseError> onExecuteGarbage()
    throws InterruptedException;

  /**
   * Send a well-formed but invalid command to the server.
   *
   * @return The result
   *
   * @throws InterruptedException On interruption
   */

  HBResultType<CAClientUnit, CAIResponseError> onExecuteInvalid()
    throws InterruptedException;
}
