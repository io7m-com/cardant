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

package com.io7m.cardant.gui.internal;

import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

public final class CAMainEventBus implements CAMainEventBusType
{
  private final SubmissionPublisher<CAMainEventType> events;

  public CAMainEventBus()
  {
    this.events = new SubmissionPublisher<>();
  }

  @Override
  public void subscribe(
    final Flow.Subscriber<? super CAMainEventType> subscriber)
  {
    this.events.subscribe(subscriber);
  }

  @Override
  public void submit(
    final CAMainEventType event)
  {
    this.events.submit(Objects.requireNonNull(event, "event"));
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CAMainEventBus 0x%08x]",
      Integer.valueOf(this.hashCode()));
  }
}
