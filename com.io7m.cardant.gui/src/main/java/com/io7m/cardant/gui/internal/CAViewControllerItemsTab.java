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
import com.io7m.cardant.model.CAIdType;
import com.io7m.cardant.model.CAInventoryElementType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.services.api.CAServiceDirectoryType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.NO;
import static javafx.scene.control.ButtonType.YES;

public final class CAViewControllerItemsTab implements Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAViewControllerItemsTab.class);

  private final CAMainEventBusType events;
  private final CAItemList itemList;
  private final CAMainStrings strings;
  private final CAServiceDirectoryType services;

  @FXML
  private SplitPane splitPane;
  @FXML
  private TableView<CAItem> itemTableView;
  @FXML
  private TableColumn<CAItem, String> itemNameColumn;
  @FXML
  private TableColumn<CAItem, String> itemDescriptionColumn;
  @FXML
  private TableColumn<CAItem, Long> itemCountColumn;
  @FXML
  private TextField searchField;
  @FXML
  private Button itemCreate;
  @FXML
  private ImageView itemCreateImage;
  @FXML
  private Button itemCreateFile;
  @FXML
  private ImageView itemCreateFileImage;
  @FXML
  private Button itemDelete;
  @FXML
  private ImageView itemDeleteImage;

  private volatile CAClientType clientNow;
  private CAPerpetualSubscriber<CAMainEventType> subscriber;

  public CAViewControllerItemsTab(
    final CAServiceDirectoryType mainServices,
    final Stage stage)
  {
    this.events =
      mainServices.requireService(CAMainEventBusType.class);
    this.strings =
      mainServices.requireService(CAMainStrings.class);

    this.services = mainServices;
    this.itemList = new CAItemList();
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    CAItemTables.configure(
      this.strings,
      this.itemList,
      this.itemTableView
    );

    this.subscriber = new CAPerpetualSubscriber<>(this::onMainEvent);
    this.events.subscribe(this.subscriber);

    this.itemTableView.getSelectionModel()
      .selectedIndexProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onTableSelectionChanged();
      });

    this.onTableSelectionChanged();
  }

  private void onTableSelectionChanged()
  {
    final var selectionModel =
      this.itemTableView.getSelectionModel();

    final var selectedItem = selectionModel.getSelectedItem();
    if (selectedItem == null) {
      this.itemDelete.setDisable(true);
      this.itemDeleteImage.setOpacity(0.5);
      this.events.submit(new CAMainEventItemSelected(Optional.empty()));
      return;
    }

    this.itemDelete.setDisable(false);
    this.itemDeleteImage.setOpacity(1.0);
    this.events.submit(new CAMainEventItemSelected(Optional.of(selectedItem)));
  }

  @FXML
  private void onSearchFieldChanged()
  {
    this.itemList.setSearch(
      this.searchField.getText().toUpperCase(Locale.ROOT)
    );
  }

  @FXML
  private void onCreateItemSelected()
    throws IOException
  {
    final var stage = new Stage();

    final var connectXML =
      CAViewControllerMain.class.getResource("createItem.fxml");

    final var resources = this.strings.resources();
    final var loader = new FXMLLoader(connectXML, resources);

    loader.setControllerFactory(
      clazz -> CAViewControllers.createController(clazz, stage, this.services)
    );

    final AnchorPane pane = loader.load();
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setScene(new Scene(pane));
    stage.setTitle(this.strings.format("items.create"));
    stage.showAndWait();

    final CAViewControllerCreateItem create = loader.getController();
    final var itemOpt = create.result();
    itemOpt.ifPresent(item -> this.clientNow.itemCreate(item));
  }

  @FXML
  private void onCreateItemsFromFileSelected()
  {

  }

  @FXML
  private void onDeleteItemsSelected()
  {
    final var alert =
      new Alert(
        CONFIRMATION,
        this.strings.format("items.deleteConfirm"),
        NO,
        YES
      );

    final var resultOpt = alert.showAndWait();
    if (resultOpt.isPresent()) {
      final var selected = resultOpt.get();
      if (selected.equals(YES)) {
        this.clientNow.itemsDelete(
          this.itemTableView.getSelectionModel()
            .getSelectedItems()
            .stream()
            .map(CAItem::id)
            .collect(Collectors.toList())
        );
      }
    }
  }

  private void onClientDisconnected()
  {
    LOG.debug("onClientDisconnected");
    this.clientNow = null;
    this.itemList.items().clear();
  }

  private void onClientConnected(
    final CAClientType client)
  {
    LOG.debug("onClientConnected");
    this.clientNow = client;

    Platform.runLater(() -> {
      this.splitPane.setDividerPositions(0.125);
    });
  }

  private void onDataRemoved(
    final Set<CAIdType> ids)
  {
    for (final var id : ids) {
      if (id instanceof CAItemID itemId) {
        this.itemList.removeItem(itemId);
      }
    }
  }

  private void onDataReceived(
    final CAInventoryElementType data)
  {
    LOG.debug("received: {}", data.getClass().getSimpleName());

    if (data instanceof CAItems items) {
      for (final var item : items.items()) {
        this.onDataReceived(item);
      }
      return;
    }

    /*
     * It's necessary to explicitly store and restore the selected item
     * in the table view, because the underlying observable array list
     * expresses updates in terms of a removal followed by an addition.
     */

    if (data instanceof CAItem item) {
      final var selectionModel =
        this.itemTableView.getSelectionModel();
      final var selectedItem =
        selectionModel.getSelectedItem();
      final var newIndex =
        this.itemList.updateItem(item);

      if (selectedItem != null) {
        if (Objects.equals(selectedItem.id(), item.id())) {
          selectionModel.select(newIndex);
        }
      }
    }
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

    if (item instanceof CAMainEventClientDataRemoved removed) {
      Platform.runLater(() -> {
        this.onDataRemoved(removed.ids());
      });
    }
  }
}
