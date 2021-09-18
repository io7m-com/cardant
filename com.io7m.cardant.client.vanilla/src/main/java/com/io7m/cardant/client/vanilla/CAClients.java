/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.client.vanilla;

import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientFactoryType;
import com.io7m.cardant.client.api.CAClientHostileType;
import com.io7m.cardant.client.api.CAClientType;
import com.io7m.cardant.client.vanilla.internal.CAClient;
import com.io7m.cardant.protocol.inventory.api.CAMessageServices;
import com.io7m.cardant.protocol.inventory.api.CAMessageServicesType;
import com.io7m.cardant.protocol.versioning.CAVersioningMessageParserFactoryType;
import com.io7m.cardant.protocol.versioning.CAVersioningMessageParsers;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executors;

/**
 * The default client factory.
 */

public final class CAClients implements CAClientFactoryType
{
  private final CAClientStrings strings;
  private final CAVersioningMessageParserFactoryType versioningParsers;
  private final CAMessageServicesType messages;

  /**
   * Construct a client factory.
   *
   * @param inStrings           The strings
   * @param inVersioningParsers The versioning parsers
   * @param inMessages          The message services
   */

  public CAClients(
    final CAClientStrings inStrings,
    final CAVersioningMessageParserFactoryType inVersioningParsers,
    final CAMessageServicesType inMessages)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.versioningParsers =
      Objects.requireNonNull(inVersioningParsers, "versioningParsers");
    this.messages =
      Objects.requireNonNull(inMessages, "inMessages");
  }

  /**
   * Construct a client factory.
   *
   * @throws IOException On I/O errors
   */

  public CAClients()
    throws IOException
  {
    this(
      new CAClientStrings(Locale.getDefault()),
      new CAVersioningMessageParsers(),
      new CAMessageServices()
    );
  }

  @Override
  public CAClientType open(
    final CAClientConfiguration configuration)
  {
    return this.openInternal(configuration, false);
  }

  @Override
  public CAClientHostileType openHostile(
    final CAClientConfiguration configuration)
  {
    return this.openInternal(configuration, true);
  }

  private CAClientHostileType openInternal(
    final CAClientConfiguration configuration,
    final boolean hostile)
  {
    Objects.requireNonNull(configuration, "configuration");

    final var pollExecutor =
      Executors.newSingleThreadExecutor(runnable -> {
        final var thread = new Thread(runnable);
        thread.setName(
          "com.io7m.cardant.client.vanilla.poll[%d]"
            .formatted(Long.valueOf(thread.getId()))
        );
        return thread;
      });

    final var commandExecutor =
      Executors.newSingleThreadExecutor(runnable -> {
        final var thread = new Thread(runnable);
        thread.setName(
          "com.io7m.cardant.client.vanilla.command[%d]"
            .formatted(Long.valueOf(thread.getId()))
        );
        return thread;
      });

    final var httpExecutor =
      Executors.newCachedThreadPool(runnable -> {
        final var thread = new Thread(runnable);
        thread.setName(
          "com.io7m.cardant.client.vanilla.http[%d]"
            .formatted(Long.valueOf(thread.getId()))
        );
        return thread;
      });

    final var client =
      new CAClient(
        pollExecutor,
        httpExecutor,
        commandExecutor,
        configuration,
        this.strings,
        this.versioningParsers,
        this.messages,
        hostile
      );

    pollExecutor.execute(client::executeEventPolling);
    commandExecutor.execute(client::executeCommandProcessing);
    return client;
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CAClients 0x%08x]",
      Integer.valueOf(this.hashCode()));
  }
}
