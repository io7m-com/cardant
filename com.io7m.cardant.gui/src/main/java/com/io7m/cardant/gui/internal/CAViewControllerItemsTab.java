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

import com.io7m.cardant.client.api.CAClientCommandError;
import com.io7m.cardant.client.api.CAClientHostileType;
import com.io7m.cardant.client.api.CAClientType;
import com.io7m.cardant.gui.internal.model.CAItemMutable;
import com.io7m.cardant.gui.internal.model.CALocationItemType;
import com.io7m.cardant.gui.internal.model.CALocationTreeFiltered;
import com.io7m.cardant.gui.internal.views.CAItemMutableTables;
import com.io7m.cardant.gui.internal.views.CALocationTreeCellFactory;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.services.api.CAServiceDirectoryType;
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
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
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
import java.util.stream.Collectors;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.NO;
import static javafx.scene.control.ButtonType.YES;

public final class CAViewControllerItemsTab implements Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAViewControllerItemsTab.class);

  private final CAMainEventBusType events;
  private final CAMainStrings strings;
  private final CAServiceDirectoryType services;
  private final CAMainController controller;
  private final CALocationTreeFiltered locationFilteredView;

  @FXML
  private SplitPane splitPane;
  @FXML
  private TableView<CAItemMutable> itemTableView;
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
  private Button itemReposit;
  @FXML
  private ImageView itemDeleteImage;
  @FXML
  private TreeView<CALocationItemType> locationTreeView;
  @FXML
  private TextField locationSearchField;

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
    this.controller =
      mainServices.requireService(CAMainController.class);

    this.services = mainServices;

    this.locationFilteredView =
      CALocationTreeFiltered.filter(this.controller.locationTree());
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    CAItemMutableTables.configure(
      this.strings,
      this.itemTableView
    );

    CAItemMutableTables.bind(
      this.itemTableView,
      this.controller.items()
    );

    this.locationSearchField.textProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.locationFilteredView.setFilterChecked(newValue);
      });

    this.locationTreeView.setRoot(this.locationFilteredView.root());
    this.locationTreeView.setShowRoot(false);
    this.locationTreeView.setCellFactory(
      new CALocationTreeCellFactory(this.strings));
    this.locationTreeView.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onLocationSelectionChanged();
      });

    this.subscriber = new CAPerpetualSubscriber<>(this::onMainEvent);
    this.events.subscribe(this.subscriber);

    this.itemTableView.getSelectionModel()
      .selectedIndexProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onItemTableSelectionChanged();
      });

    this.controller.itemSelected()
      .addListener((observable, oldValue, newValue) -> {
        this.onItemSelectionChanged(newValue);
      });

    this.controller.connectedClient()
      .addListener((observable, oldValue, newValue) -> {
        this.onClientConnectionChanged(newValue);
      });

    this.onItemTableSelectionChanged();
  }

  private void onMainEvent(
    final CAMainEventType event)
  {

  }

  private void onClientConnectionChanged(
    final Optional<CAClientHostileType> clientOpt)
  {
    if (clientOpt.isPresent()) {
      this.clientNow = clientOpt.get();
      this.splitPane.setDividerPositions(0.25);
    } else {
      this.clientNow = null;
    }
  }

  private void onItemSelectionChanged(
    final Optional<CAItemMutable> itemSelection)
  {
    if (itemSelection.isPresent()) {
      this.itemDelete.setDisable(false);
      this.itemReposit.setDisable(false);
    } else {
      this.itemDelete.setDisable(true);
      this.itemReposit.setDisable(true);
    }
  }

  private void onLocationSelectionChanged()
  {
    final var selectionModel =
      this.locationTreeView.getSelectionModel();

    this.controller.locationTreeSelect(
      Optional.ofNullable(selectionModel.getSelectedItem())
        .map(TreeItem::getValue)
    );
  }

  private void onItemTableSelectionChanged()
  {
    final var selectionModel =
      this.itemTableView.getSelectionModel();

    this.controller.itemSelect(
      Optional.ofNullable(selectionModel.getSelectedItem())
    );
  }

  @FXML
  private void onSearchFieldChanged()
  {
    this.controller.itemSetSearch(
      this.searchField.getText()
        .trim()
        .toUpperCase(Locale.ROOT)
    );
  }

  @FXML
  private void onRepositItemsSelected()
    throws IOException
  {
    final var stage = new Stage();

    final var connectXML =
      CAViewControllerMain.class.getResource("itemReposit.fxml");

    final var resources = this.strings.resources();
    final var loader = new FXMLLoader(connectXML, resources);

    loader.setControllerFactory(
      clazz -> {
        if (Objects.equals(clazz, CAViewControllerItemReposit.class)) {
          return new CAViewControllerItemReposit(
            this.services,
            this.controller.itemSelected().get().get(),
            stage
          );
        }
        return CAViewControllers.createController(clazz, stage, this.services);
      }
    );

    final AnchorPane pane =
      loader.load();
    final CAViewControllerItemReposit controller =
      loader.getController();

    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setScene(new Scene(pane));
    stage.setTitle(this.strings.format("items.reposit"));
    stage.showAndWait();

    controller.result()
      .ifPresent(reposit -> {
        this.clientNow.itemReposit(reposit)
          .thenAccept(result -> {
            if (result instanceof CAClientCommandError) {
              return;
            }
            this.clientNow.itemLocationsList(reposit.item());
          });
      });
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
    itemOpt.ifPresent(item -> {
      this.clientNow.itemCreate(item.id(), item.name());
    });
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
            .map(CAItemMutable::id)
            .collect(Collectors.toSet())
        );
      }
    }
  }
}
