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

package com.io7m.cardant.gui.internal.views;

import com.io7m.cardant.client.transfer.api.CATransferDownloadFailed;
import com.io7m.cardant.client.transfer.api.CATransferServiceType;
import com.io7m.cardant.gui.internal.CAIconsType;
import com.io7m.cardant.gui.internal.CAMainEventLocalError;
import com.io7m.cardant.gui.internal.CAMainStrings;
import com.io7m.cardant.gui.internal.CAObservables;
import com.io7m.cardant.gui.internal.CAViewControllerError;
import com.io7m.cardant.gui.internal.CAViewControllerMain;
import com.io7m.cardant.gui.internal.CAViewControllers;
import com.io7m.cardant.gui.internal.model.CATransferMutable;
import com.io7m.cardant.gui.internal.model.CATransferStatus;
import com.io7m.cardant.services.api.CAServiceDirectoryType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Objects;

public final class CATransferItemCellController
{
  private final CAMainStrings strings;
  private final CAIconsType icons;
  private final CATransferServiceType transfers;
  private final CAServiceDirectoryType services;
  private CATransferMutable item;

  @FXML private Pane container;
  @FXML private ImageView image;
  @FXML private Label name;
  @FXML private Label dataReceived;
  @FXML private Label rateReceiving;
  @FXML private Label timeRemaining;
  @FXML private Label statusMessage;
  @FXML private ProgressBar progress;
  @FXML private Button cancelButton;
  @FXML private Button errorDetailsButton;


  public CATransferItemCellController(
    final CAServiceDirectoryType mainServices)
  {
    this.services =
      Objects.requireNonNull(mainServices, "mainServices");
    this.icons =
      mainServices.requireService(CAIconsType.class);
    this.strings =
      mainServices.requireService(CAMainStrings.class);
    this.transfers =
      mainServices.requireService(CATransferServiceType.class);
  }

  public void setTransfer(
    final CATransferMutable item)
  {
    this.item = Objects.requireNonNull(item, "item");

    this.cancelButton.setDisable(false);
    this.errorDetailsButton.setDisable(false);
    this.name.setText(item.title());

    this.dataReceived.textProperty()
      .bind(item.receivedTextProperty());
    this.dataReceived.visibleProperty()
      .bind(CAObservables.transform(
        item.statusProperty(),
        CATransferItemCellController::downloadingTextVisibilityForStatus
      ));

    this.rateReceiving.textProperty()
      .bind(item.receivingTextProperty());
    this.rateReceiving.visibleProperty()
      .bind(CAObservables.transform(
        item.statusProperty(),
        CATransferItemCellController::downloadingTextVisibilityForStatus
      ));

    this.timeRemaining.textProperty()
      .bind(item.timeRemainingTextProperty());
    this.timeRemaining.visibleProperty()
      .bind(CAObservables.transform(
        item.statusProperty(),
        CATransferItemCellController::downloadingTextVisibilityForStatus
      ));

    this.statusMessage.textProperty()
      .bind(item.statusTextProperty());
    this.progress.progressProperty()
      .bind(item.progressProperty());

    this.image.imageProperty()
      .bind(CAObservables.transform(
        item.statusProperty(),
        this::iconForStatus));

    this.cancelButton.visibleProperty()
      .bind(CAObservables.transform(
        item.statusProperty(),
        CATransferItemCellController::cancelVisibilityForStatus
      ));

    this.errorDetailsButton.visibleProperty()
      .bind(CAObservables.transform(
        item.statusProperty(),
        CATransferItemCellController::errorDetailsVisibilityForStatus
      ));
  }

  private static Boolean errorDetailsVisibilityForStatus(
    final CATransferStatus status)
  {
    return switch (status) {
      case STATUS_WAITING, STATUS_DOWNLOADING, STATUS_CANCELLED, STATUS_COMPLETED -> Boolean.FALSE;
      case STATUS_FAILED -> Boolean.TRUE;
    };
  }

  private static Boolean cancelVisibilityForStatus(
    final CATransferStatus status)
  {
    return switch (status) {
      case STATUS_WAITING, STATUS_DOWNLOADING -> Boolean.TRUE;
      case STATUS_CANCELLED, STATUS_COMPLETED, STATUS_FAILED -> Boolean.FALSE;
    };
  }

  private static Boolean downloadingTextVisibilityForStatus(
    final CATransferStatus status)
  {
    return switch (status) {
      case STATUS_DOWNLOADING -> Boolean.TRUE;
      case STATUS_CANCELLED, STATUS_COMPLETED, STATUS_FAILED, STATUS_WAITING -> Boolean.FALSE;
    };
  }

  private Image iconForStatus(
    final CATransferStatus status)
  {
    return switch (status) {
      case STATUS_WAITING, STATUS_DOWNLOADING -> this.icons.downloadInProgress();
      case STATUS_CANCELLED -> this.icons.info();
      case STATUS_COMPLETED -> this.icons.downloadOk();
      case STATUS_FAILED -> this.icons.downloadError();
    };
  }

  @FXML
  private void onErrorDetailsSelected()
  {
    final var mostRecentStatus =
      this.item.transferStatusProperty()
        .getValue();

    if (mostRecentStatus instanceof CATransferDownloadFailed failed) {
      try {
        final var stage = new Stage();

        final var connectXML =
          CAViewControllerMain.class.getResource("error.fxml");

        final var resources = this.strings.resources();
        final var loader = new FXMLLoader(connectXML, resources);

        loader.setControllerFactory(
          clazz -> CAViewControllers.createController(
            clazz,
            stage,
            this.services)
        );

        final Pane pane =
          loader.load();
        final CAViewControllerError controller =
          loader.getController();

        controller.setEvent(new CAMainEventLocalError(
          failed.errorMessage(),
          0,
          failed.errorAttributes(),
          List.of()
        ));

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(new Scene(pane));
        stage.setTitle(this.strings.format("error.title"));
        stage.showAndWait();
      } catch (final IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }

  @FXML
  private void onCancelSelected()
  {
    this.cancelButton.setDisable(true);
    this.transfers.cancel(this.item.id());
  }
}
