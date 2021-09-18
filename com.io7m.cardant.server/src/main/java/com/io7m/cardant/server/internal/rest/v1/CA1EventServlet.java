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

package com.io7m.cardant.server.internal.rest.v1;

import com.io7m.cardant.database.api.CADatabaseEventType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.model.CAModelDatabaseEventUpdated;
import com.io7m.cardant.protocol.inventory.api.CAEventType;
import com.io7m.cardant.protocol.inventory.api.CAEventType.CAEventUpdated;
import com.io7m.cardant.protocol.inventory.api.CAMessageSerializerFactoryType;
import com.io7m.cardant.server.internal.CAServerMessages;
import com.io7m.cardant.server.internal.rest.CAMediaTypes;
import com.io7m.cardant.server.internal.rest.CAServerEventType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;

/**
 * An event servlet.
 */

public final class CA1EventServlet
  extends CA1AuthenticatedServlet
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CA1EventServlet.class);

  private final CADatabaseType database;

  /**
   * Construct an event servlet.
   *
   * @param inEvents      The event publisher
   * @param inSerializers The serializers
   * @param inDatabase    The database
   * @param inMessages    The server string resources
   */

  public CA1EventServlet(
    final SubmissionPublisher<CAServerEventType> inEvents,
    final CAMessageSerializerFactoryType inSerializers,
    final CAServerMessages inMessages,
    final CADatabaseType inDatabase)
  {
    super(inEvents, inSerializers, inMessages);
    this.database = Objects.requireNonNull(inDatabase, "inDatabase");
  }

  @Override
  protected Logger logger()
  {
    return LOG;
  }

  @Override
  protected void serviceAuthenticated(
    final HttpServletRequest request,
    final HttpServletResponse servletResponse,
    final HttpSession session)
    throws Exception
  {
    final var time =
      this.retrieveTimeoutParameter(request);

    final var queue =
      new ArrayBlockingQueue<CAEventType>(1);

    this.database.events().subscribe(
      new Flow.Subscriber<>()
      {
        private Flow.Subscription savedSubscription;

        @Override
        public void onSubscribe(
          final Flow.Subscription subscription)
        {
          this.savedSubscription = subscription;
          this.savedSubscription.request(1L);
        }

        @Override
        public void onNext(
          final CADatabaseEventType item)
        {
          if (item instanceof CAModelDatabaseEventUpdated updated) {
            queue.add(new CAEventUpdated(
              updated.updated(),
              updated.removed()
            ));
            this.savedSubscription.cancel();
            return;
          }

          this.savedSubscription.request(1L);
        }

        @Override
        public void onError(
          final Throwable throwable)
        {
          this.savedSubscription.cancel();
        }

        @Override
        public void onComplete()
        {
          this.savedSubscription.cancel();
        }
      });

    final var event =
      queue.poll(time, TimeUnit.MILLISECONDS);

    if (event == null) {
      servletResponse.setStatus(204);
      servletResponse.setContentLength(0);
      return;
    }

    servletResponse.setStatus(200);
    servletResponse.setContentType(CAMediaTypes.applicationCardantXML());

    final var outputStream =
      CloseShieldOutputStream.wrap(servletResponse.getOutputStream());

    this.serializers().serialize(this.clientURI(), outputStream, event);
    outputStream.flush();
  }

  private long retrieveTimeoutParameter(
    final HttpServletRequest request)
  {
    final var timeout = request.getParameter("timeout");
    if (timeout == null) {
      return 30_000L;
    }

    final var time = Long.parseUnsignedLong(timeout);
    if (Long.compareUnsigned(time, 30_000L) > 0) {
      return 30_000L;
    }

    return time;
  }
}
