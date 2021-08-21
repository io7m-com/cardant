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
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.services.api.CAServiceDirectoryType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.NO;
import static javafx.scene.control.ButtonType.YES;

public final class CAViewControllerItemEditorAttachmentsTab
  implements Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAViewControllerItemEditorAttachmentsTab.class);

  private final CAMainEventBusType events;
  private final CAMainStrings strings;
  private final CAServiceDirectoryType services;
  private final CAItemAttachmentList attachmentList;
  private Optional<CAItem> itemCurrent;
  private volatile CAClientType clientNow;
  private CAPerpetualSubscriber<CAMainEventType> subscriber;

  @FXML
  private ListView<CAItemAttachment> attachmentListView;

  @FXML
  private TextField searchField;

  @FXML
  private Button itemAttachmentDownload;

  @FXML
  private Button itemAttachmentAdd;

  @FXML
  private Button itemAttachmentRemove;

  public CAViewControllerItemEditorAttachmentsTab(
    final CAServiceDirectoryType mainServices,
    final Stage stage)
  {
    this.events =
      mainServices.requireService(CAMainEventBusType.class);
    this.strings =
      mainServices.requireService(CAMainStrings.class);

    this.services = mainServices;
    this.itemCurrent = Optional.empty();
    this.attachmentList = new CAItemAttachmentList();
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.attachmentListView.setCellFactory(
      new CAAttachmentCellFactory(this.strings));
    this.attachmentListView.setFixedCellSize(240.0);
    this.attachmentListView.setItems(
      this.attachmentList.items());
    this.attachmentListView.getSelectionModel()
      .setSelectionMode(SelectionMode.SINGLE);

    this.attachmentListView.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onAttachmentSelectionChanged();
      });

    this.subscriber = new CAPerpetualSubscriber<>(this::onMainEvent);
    this.events.subscribe(this.subscriber);
  }

  private void onAttachmentSelectionChanged()
  {
    final var selected =
      this.attachmentListView.getSelectionModel()
        .getSelectedItem();

    if (selected == null) {
      this.itemAttachmentRemove.setDisable(true);
      this.itemAttachmentDownload.setDisable(true);
      return;
    }

    this.itemAttachmentRemove.setDisable(false);
    this.itemAttachmentDownload.setDisable(false);
  }

  @FXML
  private void onSearchFieldChanged()
  {
    this.attachmentList.setSearch(this.searchField.getText().trim());
  }

  @FXML
  private void onItemAttachmentDownloadSelected()
  {

  }

  @FXML
  private void onItemAttachmentAddSelected()
  {

  }

  @FXML
  private void onItemAttachmentRemoveSelected()
  {
    final var alert =
      new Alert(
        CONFIRMATION,
        this.strings.format("items.attachment.deleteConfirm"),
        NO,
        YES
      );

    final var resultOpt = alert.showAndWait();
    if (resultOpt.isPresent()) {
      final var selected = resultOpt.get();
      if (selected.equals(YES)) {
        this.clientNow.itemAttachmentDelete(
          this.attachmentListView.getSelectionModel()
            .getSelectedItem()
            .id()
        );
      }
    }
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
      return;
    }

    final var item = itemOpt.get();
    this.attachmentList.setItems(item.attachments().values());
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
