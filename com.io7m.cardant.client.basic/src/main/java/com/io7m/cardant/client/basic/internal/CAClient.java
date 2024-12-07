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
import com.io7m.cardant.client.api.CAClientConnectionParameters;
import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.client.api.CAClientTransferStatistics;
import com.io7m.cardant.client.api.CAClientType;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.hibiscus.api.HBClientAbstract;

import java.net.http.HttpClient;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The synchronous client.
 */

public final class CAClient
  extends HBClientAbstract<
  CAIMessageType,
  CAClientConnectionParameters,
  CAClientException>
  implements CAClientType
{
  /**
   * The client.
   *
   * @param inConfiguration The configuration
   * @param inHttpClients   The HTTP clients
   * @param inStrings       The string resources
   */

  public CAClient(
    final CAClientConfiguration inConfiguration,
    final CAStrings inStrings,
    final Supplier<HttpClient> inHttpClients)
  {
    super(
      new CAHandlerDisconnected(inConfiguration, inStrings, inHttpClients)
    );
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
    return ((CAHandlerType) this.handler()).userId();
  }

  @Override
  public void fileDownload(
    final CAFileID fileID,
    final Path file,
    final Path fileTmp,
    final long size,
    final String hashAlgorithm,
    final String hashValue,
    final Consumer<CAClientTransferStatistics> statistics)
    throws InterruptedException, CAClientException
  {
    ((CAHandlerType) this.handler())
      .onExecuteFileDownload(
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
  public void fileUpload(
    final CAFileID fileID,
    final Path file,
    final String contentType,
    final String description,
    final Consumer<CAClientTransferStatistics> statistics)
    throws InterruptedException, CAClientException
  {
    ((CAHandlerType) this.handler())
      .onExecuteFileUpload(
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
    return ((CAHandlerType) this.handler())
      .transaction(commands);
  }
}
