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

package com.io7m.cardant.gui;

import com.io7m.cardant.gui.internal.CAMainServices;
import com.io7m.cardant.gui.internal.CAMainStrings;
import com.io7m.cardant.gui.internal.CAViewControllerMain;
import com.io7m.cardant.gui.internal.CAViewControllers;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CAMainApplication extends Application
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAMainApplication.class);

  public CAMainApplication()
  {

  }

  @Override
  public void start(final Stage stage)
    throws Exception
  {
    LOG.debug("starting application");

    final var mainServices =
      CAMainServices.create();
    final var strings =
      mainServices.requireService(CAMainStrings.class);
    final var mainXML =
      CAViewControllerMain.class.getResource("mainWindow.fxml");
    final var loader =
      new FXMLLoader(mainXML, strings.resources());

    loader.setControllerFactory(
      clazz -> CAViewControllers.createController(clazz, stage, mainServices)
    );

    final AnchorPane pane = loader.load();
    final var controller = (CAViewControllerMain) loader.getController();

    stage.setTitle(strings.format("programTitle"));
    stage.setMinWidth(1024.0);
    stage.setMinHeight(768.0);
    stage.setScene(new Scene(pane));
    stage.show();
  }
}
