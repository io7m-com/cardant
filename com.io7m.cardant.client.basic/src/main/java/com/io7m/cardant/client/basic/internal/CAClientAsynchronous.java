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

import com.io7m.cardant.client.api.CAClientAsynchronousType;
import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientCredentials;
import com.io7m.cardant.client.api.CAClientEventType;
import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.hibiscus.basic.HBClientAsynchronousAbstract;

import java.net.http.HttpClient;
import java.util.Optional;

import static java.lang.Integer.toUnsignedString;

/**
 * The asynchronous client.
 */

public final class CAClientAsynchronous
  extends HBClientAsynchronousAbstract<
  CAClientException,
  CAICommandType<?>,
  CAIResponseType,
  CAIResponseType,
  CAIResponseError,
  CAClientEventType,
  CAClientCredentials>
  implements CAClientAsynchronousType
{
  private final CAClientUserIdSubscriber subscriber;

  /**
   * The asynchronous client.
   *
   * @param inConfiguration The configuration
   * @param inHttpClient    The HTTP client
   * @param inStrings       The string resources
   */

  public CAClientAsynchronous(
    final CAClientConfiguration inConfiguration,
    final CAStrings inStrings,
    final HttpClient inHttpClient)
  {
    super(
      new CAClientSynchronous(inConfiguration, inStrings, inHttpClient),
      "com.io7m.cardant.client.basic"
    );

    this.subscriber =
      new CAClientUserIdSubscriber();
    this.state()
      .subscribe(this.subscriber);
  }

  @Override
  public Optional<CAUserID> userId()
  {
    return this.subscriber.getUserId();
  }

  @Override
  public String toString()
  {
    return "[CAClientAsynchronous 0x%s]"
      .formatted(toUnsignedString(this.hashCode(), 16));
  }
}
