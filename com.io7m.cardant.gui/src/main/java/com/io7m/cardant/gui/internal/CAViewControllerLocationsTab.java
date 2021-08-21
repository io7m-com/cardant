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

import com.io7m.cardant.client.api.CAClientType;
import com.io7m.cardant.model.CAInventoryElementType;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.services.api.CAServiceDirectoryType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public final class CAViewControllerLocationsTab implements Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAViewControllerLocationsTab.class);

  private final CAMainEventBusType events;

  @FXML
  private TableView<CALocation> locationTableView;

  private volatile CAClientType clientNow;

  public CAViewControllerLocationsTab(
    final CAServiceDirectoryType mainServices,
    final Stage stage)
  {
    this.events =
      mainServices.requireService(CAMainEventBusType.class);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.events.subscribe(new CAPerpetualSubscriber<>(this::onMainEvent));
  }

  private void onClientDisconnected()
  {
    LOG.debug("onClientDisconnected");
    this.clientNow = null;
  }

  private void onClientConnected(
    final CAClientType client)
  {
    LOG.debug("onClientConnected");
    this.clientNow = client;
  }

  private void onDataReceived(
    final CAInventoryElementType data)
  {

  }

  private void onMainEvent(
    final CAMainEventType item)
  {
    if (item instanceof CAMainEventClientConnection clientEvent) {
      final var client = clientEvent.client();
      if (client.isConnected()) {
        this.onClientConnected(client);
      } else {
        this.onClientDisconnected();
      }
    }

    if (item instanceof CAMainEventClientData clientData) {
      Platform.runLater(() -> {
        this.onDataReceived(clientData.data());
      });
    }
  }
}
