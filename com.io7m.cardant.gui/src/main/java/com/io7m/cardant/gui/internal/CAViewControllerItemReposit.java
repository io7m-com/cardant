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

import com.io7m.cardant.client.api.CAClientHostileType;
import com.io7m.cardant.gui.CALongSpinnerValueFactory;
import com.io7m.cardant.gui.internal.model.CAItemMutable;
import com.io7m.cardant.gui.internal.model.CALocationItemType;
import com.io7m.cardant.gui.internal.model.CALocationTreeFiltered;
import com.io7m.cardant.gui.internal.views.CAItemRepositSelection;
import com.io7m.cardant.gui.internal.views.CAItemRepositSelectionStringConverter;
import com.io7m.cardant.gui.internal.views.CALocationTreeCellFactory;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.services.api.CAServiceDirectoryType;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public final class CAViewControllerItemReposit implements Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAViewControllerItemReposit.class);

  private final CAMainController controller;
  private final CAMainEventBusType events;
  private final CAMainStrings strings;
  private final CAServiceDirectoryType services;
  private final Stage stage;
  private final CALocationTreeFiltered locationTreeAdd;
  private final CALocationTreeFiltered locationTreeRemove;
  private final CALocationTreeFiltered locationTreeMoveFrom;
  private final CALocationTreeFiltered locationTreeMoveTo;
  private CAPerpetualSubscriber<CAMainEventType> subscriber;
  private CAClientHostileType clientNow;

  @FXML
  private TextField itemIdField;
  @FXML
  private TextField itemNameField;

  @FXML
  private Spinner<Long> itemRepositCount;
  @FXML
  private Label itemRepositCountBad;
  private CALongSpinnerValueFactory itemRepositCountFactory;

  @FXML
  private ChoiceBox<CAItemRepositSelection> itemRepositType;
  @FXML
  private Pane itemRepositAdd;
  @FXML
  private TreeView<CALocationItemType> itemRepositAddLocation;
  @FXML
  private TextField itemRepositAddLocationSearch;

  @FXML
  private Pane itemRepositRemove;
  @FXML
  private TreeView<CALocationItemType> itemRepositRemoveLocation;
  @FXML
  private TextField itemRepositRemoveLocationSearch;

  @FXML
  private Pane itemRepositMove;
  @FXML
  private TreeView<CALocationItemType> itemRepositMoveLocationFrom;
  @FXML
  private TreeView<CALocationItemType> itemRepositMoveLocationTo;
  @FXML
  private TextField itemRepositMoveLocationFromSearch;
  @FXML
  private TextField itemRepositMoveLocationToSearch;

  public CAViewControllerItemReposit(
    final CAServiceDirectoryType mainServices,
    final Stage inStage)
  {
    this.services =
      Objects.requireNonNull(mainServices, "mainServices");
    this.stage =
      Objects.requireNonNull(inStage, "stage");

    this.events =
      mainServices.requireService(CAMainEventBusType.class);
    this.strings =
      mainServices.requireService(CAMainStrings.class);
    this.controller =
      mainServices.requireService(CAMainController.class);

    this.locationTreeAdd =
      new CALocationTreeFiltered(this.controller.locationTree());
    this.locationTreeRemove =
      new CALocationTreeFiltered(this.controller.locationTree());
    this.locationTreeMoveFrom =
      new CALocationTreeFiltered(this.controller.locationTree());
    this.locationTreeMoveTo =
      new CALocationTreeFiltered(this.controller.locationTree());
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.itemRepositType.setItems(
      FXCollections.observableArrayList(CAItemRepositSelection.values())
    );
    this.itemRepositType.converterProperty()
      .set(new CAItemRepositSelectionStringConverter(this.strings));
    this.itemRepositType.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onItemRepositTypeChanged(newValue);
      });
    this.itemRepositType.getSelectionModel()
      .selectFirst();

    this.itemRepositCountFactory = new CALongSpinnerValueFactory();
    this.itemRepositCount.setValueFactory(this.itemRepositCountFactory);
    this.itemRepositCountBad.visibleProperty()
      .bind(this.itemRepositCountFactory.invalidProperty());

    final var locationTreeCellFactory =
      new CALocationTreeCellFactory(this.strings);

    this.itemRepositAddLocation.setRoot(this.locationTreeAdd.root());
    this.itemRepositAddLocation.setShowRoot(false);
    this.itemRepositAddLocation.setCellFactory(locationTreeCellFactory);

    this.itemRepositRemoveLocation.setRoot(this.locationTreeRemove.root());
    this.itemRepositRemoveLocation.setShowRoot(false);
    this.itemRepositRemoveLocation.setCellFactory(locationTreeCellFactory);

    this.itemRepositMoveLocationFrom.setRoot(this.locationTreeMoveFrom.root());
    this.itemRepositMoveLocationFrom.setShowRoot(false);
    this.itemRepositMoveLocationFrom.setCellFactory(locationTreeCellFactory);

    this.itemRepositMoveLocationTo.setRoot(this.locationTreeMoveTo.root());
    this.itemRepositMoveLocationTo.setShowRoot(false);
    this.itemRepositMoveLocationTo.setCellFactory(locationTreeCellFactory);
  }

  public void setItem(
    final CAItemMutable item)
  {
    Objects.requireNonNull(item, "item");

    this.itemIdField.setText(
      item.id().id().toString());
    this.itemNameField.textProperty()
      .bind(item.name());
  }

  @FXML
  private void onItemRepositAddLocationSearchChanged()
  {
    this.locationTreeAdd.setFilterChecked(
      this.itemRepositAddLocationSearch.getText()
    );
  }

  @FXML
  private void onItemRepositRemoveLocationSearchChanged()
  {
    this.locationTreeRemove.setFilterChecked(
      this.itemRepositRemoveLocationSearch.getText()
    );
  }

  @FXML
  private void onItemRepositMoveLocationFromSearchChanged()
  {
    this.locationTreeMoveFrom.setFilterChecked(
      this.itemRepositMoveLocationFromSearch.getText()
    );
  }

  @FXML
  private void onItemRepositMoveLocationToSearchChanged()
  {
    this.locationTreeMoveTo.setFilterChecked(
      this.itemRepositMoveLocationToSearch.getText()
    );
  }

  @FXML
  private void onCancelSelected()
  {
    this.stage.close();
  }

  @FXML
  private void onStoreSelected()
  {
    this.stage.close();
  }

  private void onItemRepositTypeChanged(
    final CAItemRepositSelection newValue)
  {
    switch (newValue) {
      case ITEM_REPOSIT_MOVE -> {
        this.itemRepositAdd.setVisible(false);
        this.itemRepositRemove.setVisible(false);
        this.itemRepositMove.setVisible(true);
      }
      case ITEM_REPOSIT_ADD -> {
        this.itemRepositAdd.setVisible(true);
        this.itemRepositRemove.setVisible(false);
        this.itemRepositMove.setVisible(false);
      }
      case ITEM_REPOSIT_REMOVE -> {
        this.itemRepositAdd.setVisible(false);
        this.itemRepositRemove.setVisible(true);
        this.itemRepositMove.setVisible(false);
      }
    }
  }
}
