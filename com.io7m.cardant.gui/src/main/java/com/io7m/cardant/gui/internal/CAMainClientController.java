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

import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientEventDataChanged;
import com.io7m.cardant.client.api.CAClientEventDataReceived;
import com.io7m.cardant.client.api.CAClientEventStatusChanged;
import com.io7m.cardant.client.api.CAClientEventType;
import com.io7m.cardant.client.api.CAClientFactoryType;
import com.io7m.cardant.client.api.CAClientType;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.services.api.CAServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Flow;

public final class CAMainClientController implements CAServiceType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAMainClientController.class);

  private final CAMainEventBusType eventBus;
  private final CAMainStrings strings;
  private final CAClientFactoryType clients;
  private volatile CAClientType client;

  public CAMainClientController(
    final CAMainStrings inStrings,
    final CAClientFactoryType inClients,
    final CAMainEventBusType inEventBus)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "inStrings");
    this.clients =
      Objects.requireNonNull(inClients, "inClients");
    this.eventBus =
      Objects.requireNonNull(inEventBus, "eventBus");
  }

  public CAClientType connect(
    final CAClientConfiguration configuration)
  {
    Objects.requireNonNull(configuration, "configuration");

    this.client = this.clients.open(configuration);
    this.client.events()
      .subscribe(
        new ClientEventSubscriber(
          this.client,
          this.eventBus,
          this.strings
        )
      );

    this.client.itemsList();
    return this.client;
  }

  public void disconnect()
  {
    try {
      this.client.close();
      this.client = null;
    } catch (final IOException e) {
      LOG.debug("error closing client: ", e);
    }
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CAMainClientController 0x%08x]",
      Integer.valueOf(this.hashCode())
    );
  }

  private static final class ClientEventSubscriber
    implements Flow.Subscriber<CAClientEventType>
  {
    private final CAMainEventBusType eventBus;
    private final CAMainStrings strings;
    private final CAClientType client;
    private Flow.Subscription subscription;

    ClientEventSubscriber(
      final CAClientType inClient,
      final CAMainEventBusType inEventBus,
      final CAMainStrings inStrings)
    {
      this.client =
        Objects.requireNonNull(inClient, "inClient");
      this.eventBus =
        Objects.requireNonNull(inEventBus, "eventBus");
      this.strings =
        Objects.requireNonNull(inStrings, "strings");
    }

    @Override
    public void onSubscribe(
      final Flow.Subscription newSubscription)
    {
      this.subscription = newSubscription;
      this.subscription.request(1L);
    }

    @Override
    public void onNext(
      final CAClientEventType item)
    {
      if (item instanceof CAClientEventStatusChanged status) {
        this.onClientEventStatusChanged(status);
      }

      if (item instanceof CAClientEventDataReceived data) {
        this.onClientEventDataReceived(data);
      }

      if (item instanceof CAClientEventDataChanged data) {
        this.onClientEventDataChanged(data);
      }

      this.subscription.request(1L);
    }

    private void onClientEventStatusChanged(
      final CAClientEventStatusChanged status)
    {
      this.eventBus.submit(
        new CAMainEventClientStatus(
          status,
          switch (status) {
            case CLIENT_NEGOTIATING_PROTOCOLS -> {
              yield this.strings.format("status.connecting");
            }
            case CLIENT_NEGOTIATING_PROTOCOLS_FAILED -> {
              yield this.strings.format("status.connectionFailed");
            }
            case CLIENT_CONNECTED -> {
              yield this.strings.format("status.connected");
            }
            case CLIENT_DISCONNECTED -> {
              yield this.strings.format("status.disconnected");
            }
            case CLIENT_SENDING_REQUEST -> {
              yield this.strings.format("status.sendingRequest");
            }
            case CLIENT_RECEIVING_DATA -> {
              yield this.strings.format("status.receivingData");
            }
          }
        ));

      switch (status) {
        case CLIENT_NEGOTIATING_PROTOCOLS,
          CLIENT_NEGOTIATING_PROTOCOLS_FAILED,
          CLIENT_SENDING_REQUEST,
          CLIENT_RECEIVING_DATA -> {
        }
        case CLIENT_CONNECTED, CLIENT_DISCONNECTED -> {
          final var message =
            this.client.isConnected()
              ? this.strings.format("status.connected")
              : this.strings.format("status.disconnected");

          this.eventBus.submit(
            new CAMainEventClientConnection(this.client, message)
          );
        }
      }
    }

    private void onClientEventDataReceived(
      final CAClientEventDataReceived data)
    {
      this.eventBus.submit(new CAMainEventClientData(data.element()));
    }

    private void onClientEventDataChanged(
      final CAClientEventDataChanged data)
    {
      for (final var update : data.updated()) {
        if (update instanceof CAItemID id) {
          this.client.itemGet(id);
        }
      }

      final var removed = data.removed();
      if (!removed.isEmpty()) {
        this.eventBus.submit(new CAMainEventClientDataRemoved(removed));
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
}
