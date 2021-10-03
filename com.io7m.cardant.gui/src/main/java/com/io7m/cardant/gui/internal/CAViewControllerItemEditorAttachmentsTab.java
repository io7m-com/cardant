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
import com.io7m.cardant.client.api.CAClientType;
import com.io7m.cardant.client.preferences.api.CAPreferencesServiceType;
import com.io7m.cardant.client.transfer.api.CATransferServiceType;
import com.io7m.cardant.gui.internal.model.CAItemAttachmentMutable;
import com.io7m.cardant.gui.internal.views.CAItemAttachmentMutableCellFactory;
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemAttachmentID;
import com.io7m.cardant.services.api.CAServiceDirectoryType;
import com.io7m.jwheatsheaf.api.JWFileChooserAction;
import com.io7m.jwheatsheaf.api.JWFileChooserConfiguration;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Objects;
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
  private final CAMainController controller;
  private final CAFileDialogs fileDialogs;
  private final CAPreferencesServiceType preferences;
  private final Stage stage;
  private final CATransferServiceType transfers;
  private volatile CAClientType clientNow;

  @FXML
  private ListView<CAItemAttachmentMutable> attachmentListView;

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
    this.services =
      Objects.requireNonNull(mainServices, "mainServices");
    this.stage =
      Objects.requireNonNull(stage, "stage");

    this.events =
      mainServices.requireService(CAMainEventBusType.class);
    this.strings =
      mainServices.requireService(CAMainStrings.class);
    this.controller =
      mainServices.requireService(CAMainController.class);
    this.fileDialogs =
      mainServices.requireService(CAFileDialogs.class);
    this.preferences =
      mainServices.requireService(CAPreferencesServiceType.class);
    this.transfers =
      mainServices.requireService(CATransferServiceType.class);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.controller.connectedClient()
      .addListener((observable, oldValue, newValue) -> {
        this.onClientConnectionChanged(newValue);
      });

    this.controller.itemAttachmentSelected()
      .addListener((observable, oldValue, newValue) -> {
        this.onItemAttachmentSelectionChanged(newValue);
      });

    this.controller.itemSelected()
      .addListener((observable, oldValue, newValue) -> {
        this.bindAttachmentTable();
      });

    this.attachmentListView.setCellFactory(
      new CAItemAttachmentMutableCellFactory(this.strings));
    this.attachmentListView.setFixedCellSize(240.0);
    this.attachmentListView.getSelectionModel()
      .setSelectionMode(SelectionMode.SINGLE);

    this.attachmentListView.getSelectionModel()
      .selectedItemProperty()
      .addListener((observable, oldValue, newValue) -> {
        this.onAttachmentSelectionChanged();
      });

    this.bindAttachmentTable();
  }

  private void bindAttachmentTable()
  {
    this.attachmentListView.setItems(
      this.controller.itemAttachments()
        .readable()
    );
  }

  private void onItemAttachmentSelectionChanged(
    final Optional<CAItemAttachmentMutable> newValue)
  {
    if (newValue.isPresent()) {
      this.itemAttachmentRemove.setDisable(false);
      this.itemAttachmentDownload.setDisable(false);
    } else {
      this.itemAttachmentRemove.setDisable(true);
      this.itemAttachmentDownload.setDisable(true);
    }
  }

  private void onAttachmentSelectionChanged()
  {
    this.controller.itemAttachmentSelect(
      Optional.ofNullable(
        this.attachmentListView.getSelectionModel()
          .getSelectedItem())
    );
  }

  @FXML
  private void onSearchFieldChanged()
  {
    this.controller.itemAttachmentSetSearch(
      this.searchField.getText()
        .trim()
        .toUpperCase(Locale.ROOT)
    );
  }

  @FXML
  private void onItemAttachmentDownloadSelected()
    throws IOException
  {
    final var fileChooserConfiguration =
      JWFileChooserConfiguration.builder()
        .setAction(JWFileChooserAction.CREATE)
        .setCssStylesheet(CACSS.mainStylesheet())
        .addFileFilters(this.fileDialogs.filterForImages())
        .setFileSelectionMode(path -> Boolean.valueOf(Files.isRegularFile(path)))
        .setRecentFiles(this.preferences.preferences().recentFiles())
        .setConfirmFileSelection(true)
        .build();

    final var choosers =
      this.fileDialogs.choosers();
    final var chooser =
      choosers.create(this.stage, fileChooserConfiguration);
    final var files =
      chooser.showAndWait();

    if (files.isEmpty()) {
      return;
    }

    final var file =
      this.preferences.addRecentFile(files.get(0));

    final var itemAttachment =
      this.controller.itemAttachmentSelected()
        .get()
        .get();

    final var title =
      this.strings.format("transfer.attachment", itemAttachment.id().id());

    this.clientNow.itemAttachmentData(itemAttachment.id())
      .thenComposeAsync(inputStream -> {
        return this.transfers.transferTo(
          inputStream,
          title,
          itemAttachment.size(),
          itemAttachment.hashAlgorithm(),
          itemAttachment.hashValue(),
          file
        );
      });
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
        final var item =
          this.controller.itemSelected()
            .get()
            .get();

        this.clientNow.itemAttachmentDelete(
          item.id(),
          this.attachmentListView.getSelectionModel()
            .getSelectedItem()
            .id()
        );
      }
    }
  }

  private void onClientConnectionChanged(
    final Optional<CAClientHostileType> newValue)
  {
    if (newValue.isPresent()) {
      this.clientNow = newValue.get();
    } else {
      this.clientNow = null;
    }
  }
}
