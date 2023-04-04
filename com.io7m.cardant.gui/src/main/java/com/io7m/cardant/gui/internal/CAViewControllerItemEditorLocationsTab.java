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

import com.io7m.cardant.gui.internal.model.CAItemLocationMutable;
import com.io7m.cardant.gui.internal.views.CAItemLocationTables;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

public final class CAViewControllerItemEditorLocationsTab
  implements Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAViewControllerItemEditorLocationsTab.class);

  private final CAMainEventBusType events;
  private final CAMainStrings strings;
  private final RPServiceDirectoryType services;
  private final CAMainController controller;

  @FXML private TableView<CAItemLocationMutable> locationsTableView;

  public CAViewControllerItemEditorLocationsTab(
    final RPServiceDirectoryType mainServices,
    final Stage stage)
  {
    this.events =
      mainServices.requireService(CAMainEventBusType.class);
    this.strings =
      mainServices.requireService(CAMainStrings.class);
    this.controller =
      mainServices.requireService(CAMainController.class);

    this.services = mainServices;
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    CAItemLocationTables.configure(
      this.strings,
      this.locationsTableView
    );

    this.controller.itemSelected()
      .addListener((observable, oldValue, newValue) -> {
        this.bindMetadataTable();
      });
  }

  private void bindMetadataTable()
  {
    CAItemLocationTables.bind(
      this.controller.itemLocationsSelected(),
      this.locationsTableView
    );
  }
}
