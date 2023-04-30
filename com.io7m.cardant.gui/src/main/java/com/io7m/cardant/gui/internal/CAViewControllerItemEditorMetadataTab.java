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

import com.io7m.cardant.gui.internal.model.CAItemMetadataMutable;
import com.io7m.cardant.gui.internal.views.CAItemMetadataTables;
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataPut;
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataRemove;
import com.io7m.repetoir.core.RPServiceDirectoryType;
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
import java.util.Set;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.NO;
import static javafx.scene.control.ButtonType.YES;

public final class CAViewControllerItemEditorMetadataTab
  implements Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAViewControllerItemEditorMetadataTab.class);

  private final CAMainStrings strings;
  private final RPServiceDirectoryType services;
  private final CAMainController controller;
  private final CAMainClientService clientService;

  @FXML private TableView<CAItemMetadataMutable> metadataTableView;
  @FXML private Button itemMetadataAdd;
  @FXML private Button itemMetadataRemove;
  @FXML private TextField searchField;

  public CAViewControllerItemEditorMetadataTab(
    final RPServiceDirectoryType mainServices,
    final Stage stage)
  {
    this.strings =
      mainServices.requireService(CAMainStrings.class);
    this.controller =
      mainServices.requireService(CAMainController.class);
    this.clientService =
      mainServices.requireService(CAMainClientService.class);

    this.services = mainServices;
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    CAItemMetadataTables.configure(
      this.strings,
      this.metadataTableView,
      this::onWantEditMetadataValue
    );

    this.metadataTableView.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onMetadataSelect();
      });

    this.controller.itemSelected()
      .addListener((observable, oldValue, newValue) -> {
        this.bindMetadataTable();
      });

    this.controller.itemMetadataSelected()
      .addListener((observable, oldValue, newValue) -> {
        this.onMetadataSelected(newValue);
      });
  }

  private void bindMetadataTable()
  {
    CAItemMetadataTables.bind(
      this.controller.itemMetadata(),
      this.metadataTableView
    );
  }

  private void onWantEditMetadataValue(
    final CAItemMetadataMutable itemMetadata)
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
      final var item = this.controller.itemSelected().get().get();
      create.setItem(item);
      create.setEditingMetadata(itemMetadata);
      stage.showAndWait();

      final var itemMetadataOpt = create.result();
      itemMetadataOpt.ifPresent(metadata -> {
        this.clientService.client()
          .executeAsync(
            new CAICommandItemMetadataPut(
              item.id(), Set.of(itemMetadata.toImmutable())
            ));
      });
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void onMetadataSelected(
    final Optional<CAItemMetadataMutable> newValue)
  {
    this.itemMetadataRemove.setDisable(newValue.isEmpty());
  }

  private void onMetadataSelect()
  {
    this.controller.itemMetadataSelect(
      Optional.ofNullable(
        this.metadataTableView.getSelectionModel()
          .getSelectedItem()
      ));
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
    final var item = this.controller.itemSelected().get().get();
    create.setItem(item);
    stage.showAndWait();

    final var itemMetadataOpt = create.result();
    itemMetadataOpt.ifPresent(
      itemMetadata -> {
        this.clientService.client()
          .executeAsync(
            new CAICommandItemMetadataPut(
              item.id(), Set.of(itemMetadata.toImmutable())
            ));
      });
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
        final var item =
          this.controller.itemSelected()
            .get()
            .get();

        final var itemMetadata =
          this.controller.itemMetadataSelected()
            .get()
            .orElseThrow()
            .toImmutable();

        this.clientService.client()
          .executeAsync(
            new CAICommandItemMetadataRemove(
              item.id(), Set.of(itemMetadata.name()))
          );
      }
    }
  }

  @FXML
  private void onSearchFieldChanged()
  {
    this.controller.itemMetadataSetSearch(
      this.searchField.getText()
        .trim()
        .toUpperCase(Locale.ROOT)
    );
  }
}
