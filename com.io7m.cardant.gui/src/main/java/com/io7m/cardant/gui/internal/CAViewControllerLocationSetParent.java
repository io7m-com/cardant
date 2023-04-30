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

import com.io7m.cardant.gui.internal.model.CALocationItemDefined;
import com.io7m.cardant.gui.internal.model.CALocationItemType;
import com.io7m.cardant.gui.internal.views.CALocationTreeCellFactory;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.protocol.inventory.CAICommandLocationPut;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public final class CAViewControllerLocationSetParent implements Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAViewControllerLocationSetParent.class);

  private final Stage stage;
  private final RPServiceDirectoryType mainServices;
  private final CAMainStrings strings;
  private final CAMainController controller;
  private final CAMainClientService clientService;

  @FXML private TreeView<CALocationItemType> locationTreeView;
  @FXML private Button cancelButton;
  @FXML private Button selectButton;
  @FXML private TreeView<CALocationItemType> locationTree;

  private CALocationItemType target;

  public CAViewControllerLocationSetParent(
    final RPServiceDirectoryType inMainServices,
    final Stage inStage)
  {
    this.stage =
      Objects.requireNonNull(inStage, "stage");
    this.mainServices =
      Objects.requireNonNull(inMainServices, "mainServices");
    this.controller =
      this.mainServices.requireService(CAMainController.class);
    this.strings =
      this.mainServices.requireService(CAMainStrings.class);
    this.clientService =
      this.mainServices.requireService(CAMainClientService.class);
  }

  @FXML
  private void onCancelSelected()
  {
    this.stage.close();
  }

  @FXML
  private void onSelectSelected()
  {
    final var selected =
      this.locationTreeView.getSelectionModel()
        .getSelectedItem()
        .getValue();

    if (this.target instanceof CALocationItemDefined definedTarget) {
      this.clientService.client()
        .executeAsync(
          new CAICommandLocationPut(
            new CALocation(
              definedTarget.id(),
              Optional.of(selected.id()),
              definedTarget.name().getValueSafe(),
              definedTarget.description().getValueSafe()
            )
          )
        );
      this.stage.close();
    }
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.target =
      this.controller.locationTreeSelected()
        .get()
        .orElseThrow(IllegalStateException::new);

    this.locationTreeView.setRoot(
      this.controller.locationTree().root());
    this.locationTreeView.setShowRoot(false);
    this.locationTreeView.setCellFactory(
      new CALocationTreeCellFactory(this.strings));
    this.locationTreeView.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onLocationSelectionChanged();
      });
  }

  private void onLocationSelectionChanged()
  {
    final var selected =
      Optional.ofNullable(
        this.locationTreeView.getSelectionModel()
          .getSelectedItem()
      );

    if (selected.isPresent()) {
      final var selectedId =
        selected.get()
          .getValue()
          .id();

      this.selectButton.setDisable(Objects.equals(
        selectedId,
        this.target.id()));
    } else {
      this.selectButton.setDisable(true);
    }
  }
}
