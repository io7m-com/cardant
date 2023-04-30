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

package com.io7m.cardant.client.basic;

import com.io7m.cardant.client.api.CAClientAsynchronousType;
import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientFactoryType;
import com.io7m.cardant.client.api.CAClientSynchronousType;
import com.io7m.cardant.client.basic.internal.CAClientAsynchronous;
import com.io7m.cardant.client.basic.internal.CAClientSynchronous;
import com.io7m.cardant.client.basic.internal.CAStrings;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.CookieManager;
import java.net.http.HttpClient;

/**
 * The default client factory.
 */

public final class CAClients
  implements CAClientFactoryType
{
  /**
   * The default client factory.
   */

  public CAClients()
  {

  }

  @Override
  public String toString()
  {
    return String.format(
      "[CAClients 0x%08x]",
      Integer.valueOf(this.hashCode())
    );
  }

  @Override
  public String description()
  {
    return "Inventory client service.";
  }

  @Override
  public CAClientAsynchronousType openAsynchronousClient(
    final CAClientConfiguration configuration)
  {
    final var cookieJar =
      new CookieManager();
    final var locale =
      configuration.locale();

    final CAStrings strings;
    try {
      strings = new CAStrings(locale);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }

    final var httpClient =
      HttpClient.newBuilder()
        .cookieHandler(cookieJar)
        .build();

    return new CAClientAsynchronous(configuration, strings, httpClient);
  }

  @Override
  public CAClientSynchronousType openSynchronousClient(
    final CAClientConfiguration configuration)
  {
    final var cookieJar =
      new CookieManager();
    final var locale =
      configuration.locale();

    final CAStrings strings;
    try {
      strings = new CAStrings(locale);
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }

    final var httpClient =
      HttpClient.newBuilder()
        .cookieHandler(cookieJar)
        .build();

    return new CAClientSynchronous(configuration, strings, httpClient);
  }
}
