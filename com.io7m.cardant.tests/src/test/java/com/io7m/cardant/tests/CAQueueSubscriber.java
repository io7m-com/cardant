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

import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Flow;
import java.util.function.Predicate;

public final class CAQueueSubscriber<T> implements Flow.Subscriber<T>
{
  private final Predicate<T> predicate;
  private final BlockingQueue<T> queue;
  private Flow.Subscription subscription;

  public CAQueueSubscriber(
    final BlockingQueue<T> inQueue,
    final Predicate<T> inPredicate)
  {
    this.predicate =
      Objects.requireNonNull(inPredicate, "predicate");
    this.queue =
      Objects.requireNonNull(inQueue, "inQueue");
  }

  @Override
  public void onSubscribe(
    final Flow.Subscription newSubscription)
  {
    this.subscription =
      Objects.requireNonNull(newSubscription, "newSubscription");

    newSubscription.request(1L);
  }

  @Override
  public void onNext(final T item)
  {
    if (this.predicate.test(item)) {
      this.queue.add(item);
    }

    if (this.queue.remainingCapacity() > 0) {
      this.subscription.request(1L);
    } else {
      this.subscription.cancel();
    }
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
}
