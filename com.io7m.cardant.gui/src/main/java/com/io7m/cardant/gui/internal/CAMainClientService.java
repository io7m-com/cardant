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


package com.io7m.cardant.gui.internal;

import com.io7m.cardant.client.api.CAClientAsynchronousType;
import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientCredentials;
import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.client.basic.CAClients;
import com.io7m.cardant.gui.internal.CAStatusEventType.CAStatusEventError;
import com.io7m.cardant.gui.internal.CAStatusEventType.CAStatusEventInProgress;
import com.io7m.cardant.gui.internal.CAStatusEventType.CAStatusEventOK;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseLogin;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.hibiscus.api.HBStateType;
import com.io7m.hibiscus.api.HBStateType.HBStateDisconnected;
import com.io7m.hibiscus.api.HBStateType.HBStateExecutingCommand;
import com.io7m.hibiscus.api.HBStateType.HBStateExecutingCommandFailed;
import com.io7m.hibiscus.api.HBStateType.HBStateExecutingCommandSucceeded;
import com.io7m.hibiscus.api.HBStateType.HBStateExecutingLogin;
import com.io7m.hibiscus.api.HBStateType.HBStateExecutingLoginFailed;
import com.io7m.hibiscus.api.HBStateType.HBStateExecutingLoginSucceeded;
import com.io7m.hibiscus.api.HBStateType.HBStatePollingEvents;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import com.io7m.repetoir.core.RPServiceType;
import javafx.beans.property.SimpleObjectProperty;

import java.time.Clock;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public final class CAMainClientService
  implements RPServiceType, AutoCloseable
{
  private final CAClientAsynchronousType client;
  private final CAPerpetualSubscriber<HBStateType<CAICommandType<?>, CAIResponseType, CAIResponseError, CAClientCredentials>> subscriber;
  private final SimpleObjectProperty<UUID> userId;

  private CAMainClientService(
    final CAClientAsynchronousType inClient,
    final CAPerpetualSubscriber<HBStateType<CAICommandType<?>, CAIResponseType, CAIResponseError, CAClientCredentials>> inSubscriber,
    final SimpleObjectProperty<UUID> inUserId)
  {
    this.client =
      Objects.requireNonNull(inClient, "client");
    this.subscriber =
      Objects.requireNonNull(inSubscriber, "subscriber");
    this.userId =
      Objects.requireNonNull(inUserId, "userId");
  }

  public static CAMainClientService create(
    final RPServiceDirectoryType services,
    final Locale locale)
    throws CAClientException
  {
    Objects.requireNonNull(services, "services");
    Objects.requireNonNull(locale, "locale");

    final var statusService =
      services.requireService(CAStatusServiceType.class);
    final var strings =
      services.requireService(CAMainStrings.class);

    final var configuration =
      new CAClientConfiguration(locale, Clock.systemUTC());
    final var clientFactory =
      new CAClients();
    final var newClient =
      clientFactory.openAsynchronousClient(configuration);
    final var userId =
      new SimpleObjectProperty<UUID>();

    final var stateSubscriber =
      new CAPerpetualSubscriber<HBStateType<CAICommandType<?>, CAIResponseType, CAIResponseError, CAClientCredentials>>(s -> {
        executeState(statusService, strings, userId, s);
      });
    newClient.state().subscribe(stateSubscriber);
    return new CAMainClientService(newClient, stateSubscriber, userId);
  }

  private static void executeState(
    final CAStatusServiceType statusService,
    final CAMainStrings strings,
    final SimpleObjectProperty<UUID> user,
    final HBStateType<CAICommandType<?>, CAIResponseType, CAIResponseError, CAClientCredentials> s)
  {
    /*
     * Login.
     */

    if (s instanceof HBStateExecutingLogin) {
      user.set(null);
      statusService.publish(new CAStatusEventInProgress());
      return;
    }

    if (s instanceof final HBStateExecutingLoginFailed<
      ?, CAIResponseType, CAIResponseError, ?> c) {
      user.set(null);

      final var response = c.response();
      statusService.publish(new CAStatusEventError(
        response.errorCode(),
        response.message(),
        response.attributes(),
        response.remediatingAction(),
        response.exception()
      ));
      return;
    }

    if (s instanceof final HBStateExecutingLoginSucceeded<
      ?, CAIResponseType, CAIResponseError, ?> c) {

      final var login = (CAIResponseLogin) c.response();
      user.set(login.userId());

      statusService.publish(new CAStatusEventOK(
        strings.format("client.connected")
      ));
      return;
    }

    /*
     * Command.
     */

    if (s instanceof HBStateExecutingCommand) {
      statusService.publish(new CAStatusEventInProgress());
      return;
    }

    if (s instanceof final HBStateExecutingCommandFailed<
          ?, CAIResponseType, CAIResponseError, ?> c) {
      final var response = c.response();
      statusService.publish(new CAStatusEventError(
        response.errorCode(),
        response.message(),
        response.attributes(),
        response.remediatingAction(),
        response.exception()
      ));
      return;
    }

    if (s instanceof final HBStateExecutingCommandSucceeded<
          ?, CAIResponseType, CAIResponseError, ?> c) {
      final var response = c.response();
      statusService.publish(new CAStatusEventOK(
        strings.format(
          "client.executedCommand",
          c.command().getClass().getSimpleName()
        )
      ));
      return;
    }

    /*
     * Polling.
     */

    if (s instanceof HBStatePollingEvents) {
      statusService.publish(new CAStatusEventInProgress());
      return;
    }

    if (s instanceof HBStateDisconnected) {
      user.set(null);
      statusService.publish(new CAStatusEventOK(
        strings.format("client.disconnected")
      ));
    }
  }

  @Override
  public String description()
  {
    return "Main client service.";
  }

  @Override
  public void close()
    throws Exception
  {
    this.client.close();
    this.subscriber.close();
  }

  public CAClientAsynchronousType client()
  {
    return this.client;
  }
}
