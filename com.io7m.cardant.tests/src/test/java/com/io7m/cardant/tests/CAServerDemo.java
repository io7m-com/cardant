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

package com.io7m.cardant.tests;

import com.io7m.anethum.common.ParseException;
import com.io7m.cardant.database.api.CADatabaseEventType;
import com.io7m.cardant.server.api.CAServerConfiguration;
import com.io7m.cardant.server.api.CAServerConfigurationParserFactoryType;
import com.io7m.cardant.server.api.CAServerFactoryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.ServiceLoader;
import java.util.concurrent.Flow;

public final class CAServerDemo
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAServerDemo.class);

  private CAServerDemo()
  {

  }

  public static void main(final String[] args)
    throws IOException, ParseException
  {
    final var configurationFile =
      Paths.get(args[0]);

    final var servers =
      ServiceLoader.load(CAServerFactoryType.class)
        .findFirst()
        .orElseThrow();

    final var configurations =
      ServiceLoader.load(CAServerConfigurationParserFactoryType.class)
        .findFirst()
        .orElseThrow();

    final CAServerConfiguration configuration =
      configurations.parseFileWithContext(
        FileSystems.getDefault(), configurationFile);

    try (var server = servers.createServer(configuration)) {
      server.database()
        .events()
        .subscribe(new Flow.Subscriber<CADatabaseEventType>()
        {
          private Flow.Subscription subscription;

          @Override
          public void onSubscribe(
            final Flow.Subscription subscription)
          {
            this.subscription = subscription;
            this.subscription.request(1L);
          }

          @Override
          public void onNext(
            final CADatabaseEventType item)
          {
            LOG.debug("event: {}", item);
            this.subscription.request(1L);
          }

          @Override
          public void onError(
            final Throwable throwable)
          {

          }

          @Override
          public void onComplete()
          {

          }
        });

      while (true) {
        try {
          Thread.sleep(1_000L);
        } catch (final InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }
  }
}
