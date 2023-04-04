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

import com.io7m.cardant.client.api.CAClientType;
import com.io7m.cardant.client.preferences.api.CAPreferencesServiceType;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import static javafx.scene.control.Alert.AlertType.CONFIRMATION;
import static javafx.scene.control.ButtonType.NO;
import static javafx.scene.control.ButtonType.YES;

public final class CAViewControllerMain implements Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAViewControllerMain.class);

  private final CAMainStrings strings;
  private final RPServiceDirectoryType services;
  private final CAMainEventBusType events;
  private final CAIconsType icons;
  private final CAMainController clientController;
  private final CAPreferencesServiceType preferences;

  @FXML private TabPane mainTabs;
  @FXML private MenuItem fileConnect;
  @FXML private Label statusText;
  @FXML private ImageView statusIcon;
  @FXML private ProgressIndicator statusProgress;
  @FXML private Tab itemsTab;
  @FXML private Tab locationsTab;
  @FXML private Tab transfersTab;
  @FXML private Tab debuggingTab;

  private CAClientType client;

  public CAViewControllerMain(
    final RPServiceDirectoryType mainServices,
    final Stage stage)
  {
    this.services =
      Objects.requireNonNull(mainServices, "mainServices");
    this.preferences =
      mainServices.requireService(CAPreferencesServiceType.class);
    this.strings =
      mainServices.requireService(CAMainStrings.class);
    this.events =
      mainServices.requireService(CAMainEventBusType.class);
    this.icons =
      mainServices.requireService(CAIconsType.class);
    this.clientController =
      mainServices.requireService(CAMainController.class);
  }

  private void onRequestFileDisconnect()
    throws IOException
  {
    final var currentClient = this.client;
    if (currentClient != null) {
      final var confirmText =
        this.strings.format("disconnect.confirm");
      final var alert =
        new Alert(CONFIRMATION, confirmText, NO, YES);

      final var alertResult = alert.showAndWait();
      if (alertResult.isEmpty()) {
        return;
      }
      if (!Objects.equals(alertResult.get(), YES)) {
        return;
      }

      this.clientController.disconnect();
    }
  }

  private void onRequestFileConnect()
    throws IOException
  {
    final var stage = new Stage();

    final var connectXML =
      CAViewControllerMain.class.getResource("connect.fxml");

    final var resources = this.strings.resources();
    final var loader = new FXMLLoader(connectXML, resources);

    loader.setControllerFactory(
      clazz -> CAViewControllers.createController(clazz, stage, this.services)
    );

    final AnchorPane pane = loader.load();
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setScene(new Scene(pane));
    stage.setTitle(this.strings.format("connect.title"));
    stage.showAndWait();

    final CAViewControllerConnect connectView = loader.getController();
    final var configurationOpt = connectView.result();
    if (configurationOpt.isPresent()) {
      this.client = this.clientController.connect(configurationOpt.get());
      this.setMenuToDisconnect();
    }
  }

  @FXML
  private void onFileQuit()
  {
    Platform.exit();
    System.exit(0);
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.mainTabs.setVisible(false);

    switch (this.preferences.preferences().debuggingEnabled()) {
      case DEBUGGING_ENABLED -> {

      }
      case DEBUGGING_DISABLED -> {
        this.mainTabs.getTabs().remove(this.debuggingTab);
      }
    }

    this.setMenuToConnect();
    this.events.subscribe(new CAPerpetualSubscriber<>(this::onMainEvent));
    this.events.submit(new CAMainEventBoot());
  }

  private void setMenuToDisconnect()
  {
    this.fileConnect.setText(this.strings.format("menuFileDisconnect"));
    this.fileConnect.setOnAction(actionEvent -> {
      try {
        this.onRequestFileDisconnect();
      } catch (final IOException e) {
        LOG.error("i/o error: ", e);
      }
    });
  }

  private void setMenuToConnect()
  {
    this.fileConnect.setText(this.strings.format("menuFileConnect"));
    this.fileConnect.setOnAction(actionEvent -> {
      try {
        this.onRequestFileConnect();
      } catch (final IOException e) {
        LOG.error("i/o error: ", e);
      }
    });
  }

  private void onNotConnectedButStillTrying()
  {
    this.setMenuToDisconnect();
    this.mainTabs.setVisible(false);
  }

  private void onConnected()
  {
    this.setMenuToDisconnect();
    this.mainTabs.setVisible(true);
  }

  private void onNotConnected()
  {
    this.setMenuToConnect();
    this.mainTabs.setVisible(false);
  }

  private void onMainEvent(
    final CAMainEventType item)
  {
    Platform.runLater(() -> {
      this.statusIcon.setImage(null);
      this.statusIcon.setImage(
        switch (item.classification()) {
          case STATUS_IN_PROGRESS -> null;
          case STATUS_INFO -> this.icons.info();
          case STATUS_OK -> this.icons.ok();
          case STATUS_ERROR -> this.icons.error();
          case STATUS_BOOT -> this.icons.cardant();
          case STATUS_UI -> null;
          case STATUS_DATA_RECEIVED -> null;
        }
      );

      switch (item.classification()) {
        case STATUS_IN_PROGRESS -> {
          this.statusProgress.setVisible(true);
          this.statusIcon.setOnMouseReleased(event -> {

          });
        }
        case STATUS_INFO, STATUS_OK, STATUS_BOOT, STATUS_DATA_RECEIVED, STATUS_UI -> {
          this.statusProgress.setVisible(false);
          this.statusIcon.setOnMouseReleased(event -> {

          });
        }
        case STATUS_ERROR -> {
          this.statusProgress.setVisible(false);
          this.statusIcon.setOnMouseReleased(event -> {
            this.onClickedErrorIcon(item);
          });
        }
      }

      this.statusText.setText(item.message());
    });

    if (item instanceof CAMainEventClientStatus clientStatus) {
      switch (clientStatus.status()) {
        case CLIENT_NEGOTIATING_PROTOCOLS, CLIENT_NEGOTIATING_PROTOCOLS_FAILED -> {
          Platform.runLater(this::onNotConnectedButStillTrying);
        }
        case CLIENT_CONNECTED -> {
          Platform.runLater(this::onConnected);
        }
        case CLIENT_DISCONNECTED -> {
          Platform.runLater(this::onNotConnected);
        }
        case CLIENT_SENDING_REQUEST, CLIENT_RECEIVING_DATA -> {

        }
      }
    }
  }

  private void onClickedErrorIcon(
    final CAMainEventType item)
  {
    try {
      final var stage = new Stage();

      final var connectXML =
        CAViewControllerMain.class.getResource("error.fxml");

      final var resources = this.strings.resources();
      final var loader = new FXMLLoader(connectXML, resources);

      loader.setControllerFactory(
        clazz -> CAViewControllers.createController(clazz, stage, this.services)
      );

      final Pane pane =
        loader.load();
      final CAViewControllerError controller =
        loader.getController();

      controller.setEvent(item);

      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setScene(new Scene(pane));
      stage.setTitle(this.strings.format("error.title"));
      stage.showAndWait();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
