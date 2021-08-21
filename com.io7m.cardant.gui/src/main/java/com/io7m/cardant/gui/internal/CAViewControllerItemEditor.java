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
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.services.api.CAServiceDirectoryType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public final class CAViewControllerItemEditor implements Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAViewControllerItemEditor.class);

  private final CAMainEventBusType events;
  private final CAMainStrings strings;
  private final CAServiceDirectoryType services;

  @FXML
  private TabPane itemEditorContainer;
  @FXML
  private AnchorPane itemEditorPlaceholder;

  private Optional<CAItem> itemCurrent;
  private volatile CAClientType clientNow;
  private CAPerpetualSubscriber<CAMainEventType> subscriber;

  public CAViewControllerItemEditor(
    final CAServiceDirectoryType mainServices,
    final Stage stage)
  {
    this.events =
      mainServices.requireService(CAMainEventBusType.class);
    this.strings =
      mainServices.requireService(CAMainStrings.class);

    this.services = mainServices;
    this.itemCurrent = Optional.empty();
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.itemEditorContainer.setVisible(false);
    this.itemEditorPlaceholder.setVisible(true);

    this.subscriber = new CAPerpetualSubscriber<>(this::onMainEvent);
    this.events.subscribe(this.subscriber);
  }

  private void onClientDisconnected()
  {
    this.clientNow = null;
  }

  private void onClientConnected(
    final CAClientType client)
  {
    this.clientNow = client;
  }

  private void onDataReceived(
    final CAInventoryElementType data)
  {
    if (data instanceof CAItem item) {
      final var itemIdIncoming =
        Optional.of(item.id());
      final var itemIdCurrent =
        this.itemCurrent.map(CAItem::id);

      if (itemIdIncoming.equals(itemIdCurrent)) {
        this.onItemSelected(Optional.of(item));
      }
    }
  }

  private void onItemSelected(
    final Optional<CAItem> itemOpt)
  {
    this.itemCurrent = itemOpt;

    if (itemOpt.isEmpty()) {
      this.itemEditorContainer.setVisible(false);
      this.itemEditorPlaceholder.setVisible(true);
      return;
    }

    this.itemEditorContainer.setVisible(true);
    this.itemEditorPlaceholder.setVisible(false);
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

    if (item instanceof CAMainEventItemSelected selected) {
      Platform.runLater(() -> {
        this.onItemSelected(selected.item());
      });
    }

    if (item instanceof CAMainEventClientData clientData) {
      Platform.runLater(() -> {
        this.onDataReceived(clientData.data());
      });
    }
  }
}
