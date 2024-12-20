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

import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.client.api.CAClientFactoryType;
import com.io7m.cardant.client.api.CAClientType;
import com.io7m.cardant.client.basic.internal.CAClient;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.strings.CAStrings;

import java.net.CookieManager;
import java.net.http.HttpClient;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

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

  private static CAStrings openStrings(
    final Locale locale)
    throws CAClientException
  {
    try {
      return CAStrings.create(locale);
    } catch (final Exception e) {
      throw new CAClientException(
        e.getMessage(),
        e,
        CAStandardErrorCodes.errorIo(),
        Map.of(),
        Optional.empty(),
        Optional.empty()
      );
    }
  }

  @Override
  public CAClientType create(
    final CAClientConfiguration configuration)
    throws CAClientException
  {
    final var locale =
      configuration.locale();
    final var strings =
      openStrings(locale);

    final Supplier<HttpClient> clients = () -> {
      return HttpClient.newBuilder()
        .cookieHandler(new CookieManager())
        .executor(Executors.newVirtualThreadPerTaskExecutor())
        .build();
    };

    return new CAClient(configuration, strings, clients);
  }
}
