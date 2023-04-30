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

import com.io7m.cardant.gui.internal.model.CAItemMutable;
import com.io7m.cardant.gui.internal.model.CALocationItemDefined;
import com.io7m.cardant.gui.internal.model.CALocationItemType;
import com.io7m.cardant.gui.internal.model.CALocationTreeFiltered;
import com.io7m.cardant.gui.internal.views.CAItemRepositSelection;
import com.io7m.cardant.gui.internal.views.CAItemRepositSelectionStringConverter;
import com.io7m.cardant.gui.internal.views.CALocationTreeCellFactory;
import com.io7m.cardant.gui.internal.views.CALongSpinnerValueFactory;
import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CAItemRepositMove;
import com.io7m.cardant.model.CAItemRepositRemove;
import com.io7m.cardant.model.CAItemRepositType;
import com.io7m.cardant.model.CALocationID;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public final class CAViewControllerItemReposit implements Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAViewControllerItemReposit.class);

  private final CAMainController controller;
  private final CAMainStrings strings;
  private final Stage stage;
  private final CAIconsType icons;
  private final CAItemMutable item;
  private final CALongSpinnerValueFactory itemRepositCountFactory;
  private CALocationTreeFiltered locationTreeAdd;
  private CALocationTreeFiltered locationTreeRemove;
  private CALocationTreeFiltered locationTreeMoveFrom;
  private CALocationTreeFiltered locationTreeMoveTo;
  private Optional<CAItemRepositType> result;

  @FXML private TextField itemIdField;
  @FXML private TextField itemNameField;
  @FXML private Button store;
  @FXML private Spinner<Long> itemRepositCount;
  @FXML private Label itemRepositCountBad;
  @FXML private ChoiceBox<CAItemRepositSelection> itemRepositType;
  @FXML private Pane itemRepositAdd;
  @FXML private TreeView<CALocationItemType> itemRepositAddLocation;
  @FXML private TextField itemRepositAddLocationSearch;
  @FXML private Pane itemRepositRemove;
  @FXML private TreeView<CALocationItemType> itemRepositRemoveLocation;
  @FXML private TextField itemRepositRemoveLocationSearch;
  @FXML private Pane itemRepositMove;
  @FXML private TreeView<CALocationItemType> itemRepositMoveLocationFrom;
  @FXML private TreeView<CALocationItemType> itemRepositMoveLocationTo;
  @FXML private TextField itemRepositMoveLocationFromSearch;
  @FXML private TextField itemRepositMoveLocationToSearch;
  @FXML private ImageView infoIcon;
  @FXML private Label infoText;

  public CAViewControllerItemReposit(
    final RPServiceDirectoryType mainServices,
    final CAItemMutable item,
    final Stage inStage)
  {
    Objects.requireNonNull(mainServices, "mainServices");

    this.item =
      Objects.requireNonNull(item, "item");
    this.stage =
      Objects.requireNonNull(inStage, "stage");

    this.strings =
      mainServices.requireService(CAMainStrings.class);
    this.controller =
      mainServices.requireService(CAMainController.class);
    this.icons =
      mainServices.requireService(CAIconsType.class);

    this.result =
      Optional.empty();
    this.itemRepositCountFactory =
      new CALongSpinnerValueFactory();
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.itemIdField.setText(
      this.item.id().displayId());
    this.itemNameField.textProperty()
      .bind(this.item.name());

    this.locationTreeAdd =
      CALocationTreeFiltered.filterWithItemCounts(
        this.controller.itemLocations(),
        this.item.id(),
        this.controller.locationTree()
      );
    this.locationTreeRemove =
      CALocationTreeFiltered.filterWithItemCounts(
        this.controller.itemLocations(),
        this.item.id(),
        this.controller.locationTree()
      );
    this.locationTreeMoveFrom =
      CALocationTreeFiltered.filterWithItemCounts(
        this.controller.itemLocations(),
        this.item.id(),
        this.controller.locationTree()
      );
    this.locationTreeMoveTo =
      CALocationTreeFiltered.filterWithItemCounts(
        this.controller.itemLocations(),
        this.item.id(),
        this.controller.locationTree()
      );

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

    this.itemRepositCount.setValueFactory(
      this.itemRepositCountFactory);
    this.itemRepositCount.valueProperty()
      .addListener((observable, oldValue, newValue) -> this.validate());

    this.itemRepositCountBad.visibleProperty()
      .bind(this.itemRepositCountFactory.invalidProperty());

    final var locationTreeCellFactory =
      new CALocationTreeCellFactory(this.strings);

    this.itemRepositAddLocation.setRoot(this.locationTreeAdd.root());
    this.itemRepositAddLocation.setShowRoot(false);
    this.itemRepositAddLocation.setCellFactory(locationTreeCellFactory);
    this.itemRepositAddLocation.getSelectionModel()
      .selectedItemProperty()
      .addListener(observable -> this.validate());

    this.itemRepositRemoveLocation.setRoot(this.locationTreeRemove.root());
    this.itemRepositRemoveLocation.setShowRoot(false);
    this.itemRepositRemoveLocation.setCellFactory(locationTreeCellFactory);
    this.itemRepositRemoveLocation.getSelectionModel()
      .selectedItemProperty()
      .addListener(observable -> this.validate());

    this.itemRepositMoveLocationFrom.setRoot(this.locationTreeMoveFrom.root());
    this.itemRepositMoveLocationFrom.setShowRoot(false);
    this.itemRepositMoveLocationFrom.setCellFactory(locationTreeCellFactory);
    this.itemRepositMoveLocationFrom.getSelectionModel()
      .selectedItemProperty()
      .addListener(observable -> this.validate());

    this.itemRepositMoveLocationTo.setRoot(this.locationTreeMoveTo.root());
    this.itemRepositMoveLocationTo.setShowRoot(false);
    this.itemRepositMoveLocationTo.setCellFactory(locationTreeCellFactory);
    this.itemRepositMoveLocationTo.getSelectionModel()
      .selectedItemProperty()
      .addListener(observable -> this.validate());

    this.itemRepositCountFactory.invalidProperty()
      .addListener(observable -> this.validate());

    this.infoIcon.setVisible(false);
    this.infoText.setVisible(false);
    this.infoText.setText("");
    this.store.setDisable(true);
  }

  private void validate()
  {
    try {
      switch (this.itemRepositType.getValue()) {
        case ITEM_REPOSIT_ADD -> this.validateAdd();
        case ITEM_REPOSIT_MOVE -> this.validateMove();
        case ITEM_REPOSIT_REMOVE -> this.validateRemove();
      }
      this.store.setDisable(false);
    } catch (final ValidationException e) {
      this.infoIcon.setImage(this.icons.error());
      this.infoIcon.setVisible(true);
      this.infoText.setText(e.getMessage());
      this.infoText.setVisible(true);
      this.store.setDisable(true);
    }
  }

  private void validateRemove()
    throws ValidationException
  {
    final var selection =
      this.itemRepositRemoveLocation.getSelectionModel()
        .getSelectedItem();

    final var targetLocation =
      this.validateIsValidSelection(selection);

    final var resultStorage =
      this.validateRemoveCountIsValid(selection.getValue().id());
    final var toRemove =
      this.itemRepositCount.getValue();
    final var toRemoveText =
      Long.toUnsignedString(toRemove.longValue());
    final var itemNameText =
      this.item.name().getValueSafe();
    final var locationNameText =
      targetLocation.undecoratedNameText();
    final var resultStorageText =
      Long.toUnsignedString(resultStorage);
    final var resultTotal =
      this.item.countTotal().get() - toRemove.longValue();
    final var resultTotalText =
      Long.toUnsignedString(resultTotal);

    this.infoIcon.setVisible(true);
    this.infoIcon.setImage(this.icons.info());
    this.infoText.setVisible(true);
    this.infoText.setText(
      this.strings.format(
        "item.reposit.remove.explain",
        toRemoveText,
        itemNameText,
        locationNameText,
        resultStorageText,
        resultTotalText
      )
    );
  }

  private long validateRemoveCountIsValid(
    final CALocationID locationID)
    throws ValidationException
  {
    this.validateCountIsValid();

    final var toRemove =
      this.itemRepositCountFactory.getValue();

    final var resultCount =
      this.controller.itemLocationCouldRemoveItems(
        this.item.id(),
        locationID,
        toRemove.longValue()
      );

    if (resultCount.isEmpty()) {
      throw new ValidationException(
        this.strings.format("item.reposit.validation.countTooMany"));
    }

    return resultCount.getAsLong();
  }

  private void validateMove()
    throws ValidationException
  {
    final var moveFrom =
      this.itemRepositMoveLocationFrom.getSelectionModel()
        .getSelectedItem();
    final var moveTo =
      this.itemRepositMoveLocationTo.getSelectionModel()
        .getSelectedItem();

    final var moveFromLocation =
      this.validateIsValidSelection(moveFrom);
    final var moveToLocation =
      this.validateIsValidSelection(moveTo);

    if (Objects.equals(moveFromLocation.id(), moveToLocation.id())) {
      throw new ValidationException(
        this.strings.format("item.reposit.validation.moveSameLocations"));
    }

    this.validateCountIsValid();

    final var toMove =
      this.itemRepositCountFactory.getValue();

    this.validateMoveCountIsValid(toMove.longValue(), moveFromLocation);

    final var toMoveText =
      Long.toUnsignedString(toMove.longValue());
    final var itemNameText =
      this.item.name().getValueSafe();
    final var locationFromNameText =
      moveFromLocation.undecoratedNameText();
    final var locationToNameText =
      moveToLocation.undecoratedNameText();
    final var locationFromCount =
      this.controller.itemLocationCount(this.item.id(), moveFromLocation.id())
      - toMove.longValue();
    final var locationToCount =
      this.controller.itemLocationCount(this.item.id(), moveToLocation.id())
      + toMove.longValue();
    final var locationFromCountText =
      Long.toUnsignedString(locationFromCount);
    final var locationToCountText =
      Long.toUnsignedString(locationToCount);
    final var totalText =
      Long.toUnsignedString(this.item.countTotal().get());

    this.infoIcon.setVisible(true);
    this.infoIcon.setImage(this.icons.info());
    this.infoText.setVisible(true);
    this.infoText.setText(
      this.strings.format(
        "item.reposit.move.explain",
        toMoveText,
        itemNameText,
        locationFromNameText,
        locationToNameText,
        locationFromCountText,
        locationToCountText,
        totalText
      )
    );
  }

  private long validateMoveCountIsValid(
    final long count,
    final CALocationItemDefined moveFromLocation)
    throws ValidationException
  {
    final var resultCount =
      this.controller.itemLocationCouldRemoveItems(
        this.item.id(),
        moveFromLocation.id(),
        count
      );

    if (resultCount.isEmpty()) {
      throw new ValidationException(
        this.strings.format("item.reposit.validation.countTooMany"));
    }

    return resultCount.getAsLong();
  }

  private void validateAdd()
    throws ValidationException
  {
    final var selection =
      this.itemRepositAddLocation.getSelectionModel()
        .getSelectedItem();

    final var targetLocation =
      this.validateIsValidSelection(selection);

    this.validateCountIsValid();

    final var toAdd =
      this.itemRepositCount.getValue().longValue();
    final var toAddText =
      Long.toUnsignedString(toAdd);
    final var itemNameText =
      this.item.name().getValueSafe();
    final var locationNameText =
      targetLocation.undecoratedNameText();
    final var resultStorage =
      this.controller.itemLocationCount(
        this.item.id(), targetLocation.id()) + toAdd;
    final var resultStorageText =
      Long.toUnsignedString(resultStorage);
    final var resultTotal =
      this.item.countTotal().get() + toAdd;
    final var resultTotalText =
      Long.toUnsignedString(resultTotal);

    this.infoIcon.setVisible(true);
    this.infoIcon.setImage(this.icons.info());
    this.infoText.setVisible(true);
    this.infoText.setText(
      this.strings.format(
        "item.reposit.add.explain",
        toAddText,
        itemNameText,
        locationNameText,
        resultStorageText,
        resultTotalText
      )
    );
  }

  private void validateCountIsValid()
    throws ValidationException
  {
    final var invalidNow =
      this.itemRepositCountFactory.invalidProperty()
        .getValue()
        .booleanValue();

    if (invalidNow) {
      throw new ValidationException(
        this.strings.format("item.reposit.validation.count"));
    }
  }

  private CALocationItemDefined validateIsValidSelection(
    final TreeItem<CALocationItemType> selection)
    throws ValidationException
  {
    if (selection == null) {
      throw new ValidationException(
        this.strings.format("item.reposit.validation.locationNotSelected")
      );
    }
    if (selection.getValue() instanceof CALocationItemDefined defined) {
      return defined;
    }

    throw new ValidationException(
      this.strings.format("item.reposit.validation.locationNotEverywhere")
    );
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
    switch (this.itemRepositType.getValue()) {
      case ITEM_REPOSIT_ADD -> {
        this.result = this.storeAdd();
      }
      case ITEM_REPOSIT_MOVE -> {
        this.result = this.storeMove();
      }
      case ITEM_REPOSIT_REMOVE -> {
        this.result = this.storeRemove();
      }
    }

    this.stage.close();
  }

  private Optional<CAItemRepositType> storeRemove()
  {
    return Optional.of(
      new CAItemRepositRemove(
        this.item.id(),
        this.itemRepositRemoveLocation.getSelectionModel()
          .getSelectedItem()
          .getValue()
          .id(),
        this.itemRepositCount.getValue()
          .longValue()
      )
    );
  }

  private Optional<CAItemRepositType> storeMove()
  {
    return Optional.of(
      new CAItemRepositMove(
        this.item.id(),
        this.itemRepositMoveLocationFrom.getSelectionModel()
          .getSelectedItem()
          .getValue()
          .id(),
        this.itemRepositMoveLocationTo.getSelectionModel()
          .getSelectedItem()
          .getValue()
          .id(),
        this.itemRepositCount.getValue()
          .longValue()
      )
    );
  }

  private Optional<CAItemRepositType> storeAdd()
  {
    return Optional.of(
      new CAItemRepositAdd(
        this.item.id(),
        this.itemRepositAddLocation.getSelectionModel()
          .getSelectedItem()
          .getValue()
          .id(),
        this.itemRepositCount.getValue()
          .longValue()
      )
    );
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

    this.validate();
  }

  public Optional<CAItemRepositType> result()
  {
    return this.result;
  }

  private static final class ValidationException extends Exception
  {

    private ValidationException(
      final String message)
    {
      super(Objects.requireNonNull(message, "message"));
    }
  }
}
