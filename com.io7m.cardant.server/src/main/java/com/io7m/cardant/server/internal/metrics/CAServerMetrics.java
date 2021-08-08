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

package com.io7m.cardant.server.internal.metrics;

import com.io7m.cardant.database.api.CADatabaseEventTransactionCommitted;
import com.io7m.cardant.database.api.CADatabaseEventTransactionCreated;
import com.io7m.cardant.database.api.CADatabaseEventTransactionType;
import com.io7m.cardant.database.api.CADatabaseEventType;
import com.io7m.cardant.server.internal.rest.CAServerCommandExecuted;
import com.io7m.cardant.server.internal.rest.CAServerCommandFailed;
import com.io7m.cardant.server.internal.rest.CAServerEventType;
import com.io7m.cardant.server.internal.rest.CAServerLoginFailed;
import com.io7m.cardant.server.internal.rest.CAServerLoginSucceeded;

import java.util.Objects;
import java.util.concurrent.Flow;

/**
 * A server metrics publisher.
 */

public final class CAServerMetrics
{
  private final CAServerDatabaseMetricsSubscriber databaseSubscriber;
  private final CAServerEventMetricsSubscriber commandSubscriber;

  /**
   * Construct a metrics publisher.
   *
   * @param databaseEvents A source of database events
   * @param serverEvents   A source of command events
   * @param metrics        The MX metrics bean
   */

  public CAServerMetrics(
    final Flow.Publisher<CADatabaseEventType> databaseEvents,
    final Flow.Publisher<CAServerEventType> serverEvents,
    final CAServerMetricsBean metrics)
  {
    Objects.requireNonNull(databaseEvents, "databaseEvents");
    Objects.requireNonNull(serverEvents, "commandEvents");
    Objects.requireNonNull(metrics, "metrics");

    this.databaseSubscriber = new CAServerDatabaseMetricsSubscriber(metrics);
    databaseEvents.subscribe(this.databaseSubscriber);
    this.commandSubscriber = new CAServerEventMetricsSubscriber(metrics);
    serverEvents.subscribe(this.commandSubscriber);
  }

  private static final class CAServerDatabaseMetricsSubscriber
    implements Flow.Subscriber<CADatabaseEventType>
  {
    private final CAServerMetricsBean metrics;
    private Flow.Subscription subscription;

    CAServerDatabaseMetricsSubscriber(
      final CAServerMetricsBean inMetrics)
    {
      this.metrics =
        Objects.requireNonNull(inMetrics, "metrics");
    }

    @Override
    public void onSubscribe(
      final Flow.Subscription inSubscription)
    {
      this.subscription =
        Objects.requireNonNull(inSubscription, "subscription");
      this.subscription.request(1L);
    }

    @Override
    public void onNext(final CADatabaseEventType item)
    {
      this.subscription.request(1L);

      if (item instanceof CADatabaseEventTransactionType transaction) {
        if (transaction instanceof CADatabaseEventTransactionCreated) {
          this.metrics.databaseTransactionCreated();
          return;
        }
        if (transaction instanceof CADatabaseEventTransactionCommitted) {
          this.metrics.databaseTransactionCommitted();
          return;
        }
      }
    }

    @Override
    public void onError(final Throwable throwable)
    {

    }

    @Override
    public void onComplete()
    {

    }
  }

  private static final class CAServerEventMetricsSubscriber
    implements Flow.Subscriber<CAServerEventType>
  {
    private final CAServerMetricsBean metrics;
    private Flow.Subscription subscription;

    CAServerEventMetricsSubscriber(
      final CAServerMetricsBean inMetrics)
    {
      this.metrics =
        Objects.requireNonNull(inMetrics, "metrics");
    }

    @Override
    public void onSubscribe(
      final Flow.Subscription inSubscription)
    {
      this.subscription =
        Objects.requireNonNull(inSubscription, "subscription");
      this.subscription.request(1L);
    }

    @Override
    public void onNext(
      final CAServerEventType item)
    {
      this.subscription.request(1L);

      if (item instanceof CAServerCommandExecuted) {
        this.metrics.serverCommandExecuted();
        return;
      }
      if (item instanceof CAServerCommandFailed) {
        this.metrics.serverCommandFailed();
        return;
      }
      if (item instanceof CAServerLoginFailed) {
        this.metrics.serverLoginFailed();
        return;
      }
      if (item instanceof CAServerLoginSucceeded) {
        this.metrics.serverLoginSucceeded();
        return;
      }
    }

    @Override
    public void onError(final Throwable throwable)
    {

    }

    @Override
    public void onComplete()
    {

    }
  }
}
