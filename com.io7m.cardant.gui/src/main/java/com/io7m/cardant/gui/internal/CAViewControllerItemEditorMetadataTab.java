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
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.services.api.CAServiceDirectoryType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.NO;
import static javafx.scene.control.ButtonType.YES;

public final class CAViewControllerItemEditorMetadataTab implements
  Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAViewControllerItemEditorMetadataTab.class);

  private final CAMainEventBusType events;
  private final CAMainStrings strings;
  private final CAServiceDirectoryType services;
  private final CAItemMetadataList itemMetadataList;

  @FXML
  private TableView<CAItemMetadata> metadataTableView;
  @FXML
  private Button itemMetadataAdd;
  @FXML
  private Button itemMetadataRemove;
  @FXML
  private TextField searchField;

  private Optional<CAItem> itemCurrent;
  private volatile CAClientType clientNow;
  private CAPerpetualSubscriber<CAMainEventType> subscriber;

  public CAViewControllerItemEditorMetadataTab(
    final CAServiceDirectoryType mainServices,
    final Stage stage)
  {
    this.events =
      mainServices.requireService(CAMainEventBusType.class);
    this.strings =
      mainServices.requireService(CAMainStrings.class);

    this.services = mainServices;
    this.itemMetadataList = new CAItemMetadataList();
    this.itemCurrent = Optional.empty();
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    CAItemMetadataTables.configure(
      this.strings,
      this.itemMetadataList,
      this.metadataTableView,
      this::onWantEditMetadataValue
    );

    this.metadataTableView.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onMetadataSelectionChanged();
      });

    this.subscriber = new CAPerpetualSubscriber<>(this::onMainEvent);
    this.events.subscribe(this.subscriber);
  }

  private void onWantEditMetadataValue(
    final CAItemMetadata itemMetadata)
  {
    try {
      final var stage = new Stage();

      final var connectXML =
        CAViewControllerMain.class.getResource("itemMetadataEditor.fxml");

      final var resources = this.strings.resources();
      final var loader = new FXMLLoader(connectXML, resources);

      loader.setControllerFactory(
        clazz -> CAViewControllers.createController(clazz, stage, this.services)
      );

      final AnchorPane pane = loader.load();
      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setScene(new Scene(pane));

      final CAViewControllerItemMetadataEditor create = loader.getController();
      create.setItem(this.itemCurrent.get());
      create.setEditingMetadata(itemMetadata);
      stage.showAndWait();

      final var itemMetadataOpt = create.result();
      itemMetadataOpt.ifPresent(
        metadata -> this.clientNow.itemMetadataUpdate(metadata));
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void onMetadataSelectionChanged()
  {
    final var selected =
      this.metadataTableView.getSelectionModel()
        .getSelectedItem();

    this.itemMetadataRemove.setDisable(selected == null);
  }

  @FXML
  private void onItemMetadataAddSelected()
    throws IOException
  {
    final var stage = new Stage();

    final var connectXML =
      CAViewControllerMain.class.getResource("itemMetadataEditor.fxml");

    final var resources = this.strings.resources();
    final var loader = new FXMLLoader(connectXML, resources);

    loader.setControllerFactory(
      clazz -> CAViewControllers.createController(clazz, stage, this.services)
    );

    final AnchorPane pane = loader.load();
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setScene(new Scene(pane));

    final CAViewControllerItemMetadataEditor create = loader.getController();
    create.setItem(this.itemCurrent.get());
    stage.showAndWait();

    final var itemMetadataOpt = create.result();
    itemMetadataOpt.ifPresent(
      itemMetadata -> this.clientNow.itemMetadataUpdate(itemMetadata));
  }

  @FXML
  private void onItemMetadataRemoveSelected()
  {
    final var alert =
      new Alert(
        CONFIRMATION,
        this.strings.format("items.metadata.deleteConfirm"),
        NO,
        YES
      );

    final var resultOpt = alert.showAndWait();
    if (resultOpt.isPresent()) {
      final var selected = resultOpt.get();
      if (selected.equals(YES)) {
        this.clientNow.itemMetadataDelete(
          this.itemCurrent.get().id(),
          this.metadataTableView.getSelectionModel().getSelectedItems()
        );
      }
    }
  }

  @FXML
  private void onSearchFieldChanged()
  {
    this.itemMetadataList.setSearch(
      this.searchField.getText().toUpperCase(Locale.ROOT)
    );
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
    this.itemMetadataList.setItems(item.metadata().values());
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
