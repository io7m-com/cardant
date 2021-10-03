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

import com.io7m.cardant.client.transfer.api.CATransferServiceType;
import com.io7m.cardant.client.transfer.api.CATransferStatusType;
import com.io7m.cardant.gui.internal.model.CATableMap;
import com.io7m.cardant.gui.internal.model.CATransferMutable;
import com.io7m.cardant.gui.internal.views.CATransferItemCellFactory;
import com.io7m.cardant.services.api.CAServiceDirectoryType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Comparator;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.UUID;

public final class CAViewControllerTransfersTab implements Initializable
{
  private final CATransferServiceType transferService;
  private final CAMainStrings strings;
  private final CATableMap<UUID, CATransferMutable> transfersMap;
  private final CAPerpetualSubscriber<CATransferStatusType> transferSubscriber;
  private final CAServiceDirectoryType mainServices;
  private final CAClockService clock;

  @FXML private ListView<CATransferMutable> transfers;

  public CAViewControllerTransfersTab(
    final CAServiceDirectoryType inMainServices,
    final Stage stage)
  {
    this.mainServices =
      Objects.requireNonNull(inMainServices, "mainServices");

    this.transferService =
      inMainServices.requireService(CATransferServiceType.class);
    this.strings =
      inMainServices.requireService(CAMainStrings.class);
    this.clock =
      inMainServices.requireService(CAClockService.class);

    this.transfersMap =
      new CATableMap<>(FXCollections.observableHashMap());
    this.transferSubscriber =
      new CAPerpetualSubscriber<>(this::onTransferStatus);
  }

  private void onTransferStatus(
    final CATransferStatusType status)
  {
    Platform.runLater(() -> {
      final var map = this.transfersMap.writable();
      final var existing = map.get(status.id());
      if (existing == null) {
        map.put(
          status.id(),
          CATransferMutable.of(this.strings, this.clock.now(), status)
        );
      } else {
        existing.updateFrom(status);
      }
    });
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.transferService.status()
      .subscribe(this.transferSubscriber);
    this.transfers.setCellFactory(
      new CATransferItemCellFactory(this.mainServices));
    this.transfers.setItems(
      this.transfersMap.readable());
    this.transfers.setFixedCellSize(128.0);

    this.transfersMap.readable()
      .setComparator(
        Comparator.comparing(CATransferMutable::started).reversed());
  }
}
