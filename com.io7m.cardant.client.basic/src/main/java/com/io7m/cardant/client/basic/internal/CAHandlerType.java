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
import com.io7m.cardant.client.api.CAClientUnit;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.hibiscus.api.HBResultType;
import com.io7m.hibiscus.basic.HBClientHandlerType;

import java.io.InputStream;

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
   * Retrieve data for the given file.
   *
   * @param fileID The file ID
   *
   * @return The result
   *
   * @throws InterruptedException On interruption
   */

  HBResultType<InputStream, CAIResponseError> onExecuteFileData(CAFileID fileID)
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
