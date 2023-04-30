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

import com.io7m.cardant.client.api.CAClientCredentials;
import com.io7m.cardant.client.preferences.api.CAPreferencesServiceType;
import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.cardant.gui.internal.CAStatusEventType.CAStatusEventBooting;
import com.io7m.cardant.gui.internal.CAStatusEventType.CAStatusEventError;
import com.io7m.cardant.gui.internal.CAStatusEventType.CAStatusEventInProgress;
import com.io7m.cardant.gui.internal.CAStatusEventType.CAStatusEventOK;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.hibiscus.api.HBStateType;
import com.io7m.hibiscus.api.HBStateType.HBStateConnected;
import com.io7m.hibiscus.api.HBStateType.HBStateDisconnected;
import com.io7m.hibiscus.api.HBStateType.HBStateExecutingLogin;
import com.io7m.hibiscus.api.HBStateType.HBStateExecutingLoginFailed;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import com.io7m.seltzer.api.SStructuredErrorType;
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
import javafx.scene.control.Tooltip;
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
  private final CAStatusServiceType statusService;
  private final CAIconsType icons;
  private final CAPreferencesServiceType preferences;
  private final CAMainClientService clientService;
  private final CAPerpetualSubscriber<CAStatusEventType> statusSubscriber;

  @FXML private TabPane mainTabs;
  @FXML private MenuItem fileConnect;
  @FXML private Label statusText;
  @FXML private ImageView statusIcon;
  @FXML private ProgressIndicator statusProgress;
  @FXML private Tab itemsTab;
  @FXML private Tab locationsTab;
  @FXML private Tab transfersTab;
  @FXML private Tab debuggingTab;
  private Tooltip errorTooltip;

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
    this.statusService =
      mainServices.requireService(CAStatusServiceType.class);
    this.icons =
      mainServices.requireService(CAIconsType.class);
    this.clientService =
      mainServices.requireService(CAMainClientService.class);

    this.statusSubscriber =
      new CAPerpetualSubscriber<>(this::onStatusEvent);
    this.statusService.statusEvents()
      .subscribe(this.statusSubscriber);
  }

  private void onStatusEvent(
    final CAStatusEventType event)
  {
    if (event instanceof final CAStatusEventBooting booting) {
      this.onStatusEventBooting(booting);
      return;
    }
    if (event instanceof final CAStatusEventError error) {
      this.onStatusEventError(error);
      return;
    }
    if (event instanceof final CAStatusEventOK ok) {
      this.onStatusEventOK(ok);
      return;
    }
    if (event instanceof final CAStatusEventInProgress inProgress) {
      this.onStatusEventInProgress(inProgress);
      return;
    }
  }

  private void onStatusEventInProgress(
    final CAStatusEventInProgress inProgress)
  {
    Platform.runLater(() -> {
      Tooltip.uninstall(this.statusIcon, this.errorTooltip);
      this.statusProgress.setVisible(true);
      this.statusIcon.setImage(this.icons.inProgress());
      this.statusIcon.setOnMouseReleased(null);
      this.statusText.setText("");
    });
  }

  private void onStatusEventBooting(
    final CAStatusEventBooting booting)
  {
    Platform.runLater(() -> {
      Tooltip.uninstall(this.statusIcon, this.errorTooltip);
      this.statusProgress.setVisible(true);
      this.statusIcon.setImage(this.icons.inProgress());
      this.statusIcon.setOnMouseReleased(null);
      this.statusText.setText("");
    });
  }

  private void onStatusEventOK(
    final CAStatusEventOK ok)
  {
    Platform.runLater(() -> {
      Tooltip.uninstall(this.statusIcon, this.errorTooltip);
      this.statusProgress.setVisible(false);
      this.statusIcon.setImage(this.icons.ok());
      this.statusIcon.setOnMouseReleased(null);
      this.statusText.setText(ok.message());
    });
  }

  private void onStatusEventError(
    final CAStatusEventError error)
  {
    Platform.runLater(() -> {
      Tooltip.install(this.statusIcon, this.errorTooltip);
      this.statusProgress.setVisible(false);
      this.statusIcon.setImage(this.icons.error());
      this.statusIcon.setOnMouseReleased(e -> this.onClickedErrorIcon(error));
      this.statusText.setText(error.message());
      this.onClickedErrorIcon(error);
    });
  }

  private void onRequestFileDisconnect()
    throws IOException
  {
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

    this.clientService.client()
      .disconnectAsync();
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
      this.clientService.client().loginAsync(configurationOpt.get());
      this.setMenuToDisconnect();
    }
  }

  @FXML
  private void onAbout()
  {
    try {
      final var stage = new Stage();

      final var connectXML =
        CAViewControllerMain.class.getResource("about.fxml");

      final var resources =
        this.strings.resources();
      final var loader =
        new FXMLLoader(connectXML, resources);

      loader.setControllerFactory(
        clazz -> CAViewControllers.createController(clazz, stage, this.services)
      );

      final Pane pane =
        loader.load();

      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setMinWidth(320.0);
      stage.setMinHeight(240.0);
      stage.setScene(new Scene(pane));
      stage.setTitle(this.strings.format("about.title"));
      stage.showAndWait();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
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
    this.errorTooltip =
      new Tooltip(this.strings.format("status.clickForDetails"));

    this.mainTabs.setVisible(false);

    switch (this.preferences.preferences().debuggingEnabled()) {
      case DEBUGGING_ENABLED -> {

      }
      case DEBUGGING_DISABLED -> {
        this.mainTabs.getTabs().remove(this.debuggingTab);
      }
    }

    this.setMenuToConnect();

    this.clientService.client()
      .state()
      .subscribe(new CAPerpetualSubscriber<>(this::onClientState));

    this.statusProgress.setVisible(false);
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

  private void onClientNotConnectedButStillTrying()
  {
    this.setMenuToDisconnect();
    this.mainTabs.setVisible(false);
  }

  private void onClientConnected()
  {
    this.setMenuToDisconnect();
    this.mainTabs.setVisible(true);
  }

  private void onClientNotConnected()
  {
    this.setMenuToConnect();
    this.mainTabs.setVisible(false);
  }

  private void onClientState(
    final HBStateType<CAICommandType<?>, CAIResponseType, CAIResponseError, CAClientCredentials> state)
  {
    if (state instanceof HBStateExecutingLogin) {
      Platform.runLater(this::onClientNotConnectedButStillTrying);
      return;
    }
    if (state instanceof HBStateConnected) {
      Platform.runLater(this::onClientConnected);
      return;
    }
    if (state instanceof HBStateDisconnected
      || state instanceof HBStateExecutingLoginFailed) {
      Platform.runLater(this::onClientNotConnected);
      return;
    }
  }

  private void onClickedErrorIcon(
    final SStructuredErrorType<CAErrorCode> item)
  {
    try {
      final var stage = new Stage();

      final var connectXML =
        CAViewControllerMain.class.getResource("error.fxml");

      final var resources =
        this.strings.resources();
      final var loader =
        new FXMLLoader(connectXML, resources);

      loader.setControllerFactory(
        clazz -> CAViewControllers.createController(clazz, stage, this.services)
      );

      final Pane pane =
        loader.load();
      final CAViewControllerError controller =
        loader.getController();

      controller.setError(item);

      stage.initModality(Modality.APPLICATION_MODAL);
      stage.setMinWidth(320.0);
      stage.setMinHeight(240.0);
      stage.setScene(new Scene(pane));
      stage.setTitle(this.strings.format("error.title"));
      stage.showAndWait();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
