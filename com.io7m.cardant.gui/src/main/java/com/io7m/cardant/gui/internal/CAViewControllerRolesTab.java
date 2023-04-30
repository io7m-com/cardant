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

import com.io7m.cardant.client.api.CAClientAsynchronousType;
import com.io7m.cardant.client.api.CAClientCredentials;
import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.protocol.inventory.CAICommandRolesAssign;
import com.io7m.cardant.protocol.inventory.CAICommandRolesGet;
import com.io7m.cardant.protocol.inventory.CAICommandRolesRevoke;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.protocol.inventory.CAIResponseRolesGet;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.hibiscus.api.HBStateType;
import com.io7m.hibiscus.api.HBStateType.HBStateConnected;
import com.io7m.hibiscus.api.HBStateType.HBStateExecutingLoginSucceeded;
import com.io7m.medrina.api.MRoleName;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.UUID;

public final class CAViewControllerRolesTab implements Initializable
{
  private final CAMainClientService clientService;
  private final ObservableList<MRoleName> ownerRoles;
  private final ObservableList<MRoleName> targetRoles;
  private final SimpleObjectProperty<UUID> targetUser;

  @FXML private ListView<MRoleName> rolesForOwner;
  @FXML private ListView<MRoleName> rolesForTarget;
  @FXML private TextField uuidForOwner;
  @FXML private TextField uuidForTarget;
  @FXML private Button rolesRefreshOwner;
  @FXML private Button rolesRefreshTarget;
  @FXML private Button rolesRemove;
  @FXML private Button rolesAdd;

  public CAViewControllerRolesTab(
    final RPServiceDirectoryType inMainServices,
    final Stage stage)
  {
    this.clientService =
      inMainServices.requireService(CAMainClientService.class);

    this.targetUser =
      new SimpleObjectProperty<>();

    this.ownerRoles =
      FXCollections.observableArrayList();
    this.targetRoles =
      FXCollections.observableArrayList();
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.clientService.client()
      .state()
      .subscribe(new CAPerpetualSubscriber<>(this::onClientStateChanged));

    this.rolesForTarget.setCellFactory(new RoleNameCellCallback());
    this.rolesForTarget.getSelectionModel()
      .setSelectionMode(SelectionMode.MULTIPLE);

    this.rolesForOwner.setCellFactory(new RoleNameCellCallback());
    this.rolesForOwner.getSelectionModel()
      .setSelectionMode(SelectionMode.MULTIPLE);

    this.rolesForOwner.setItems(this.ownerRoles);
    this.rolesForTarget.setItems(this.targetRoles);

    this.rolesRemove.setDisable(true);
    this.rolesAdd.setDisable(true);

    /*
     * The target refresh button should be disabled when the target user
     * field isn't filled in.
     */

    this.rolesRefreshTarget.setDisable(true);
    this.targetUser.addListener((observable, oldValue, newValue) -> {
      this.rolesRefreshTarget.setDisable(newValue == null);
    });

    /*
     * When the target user field is filled in, refresh automatically.
     */

    this.targetUser.addListener((observable, oldValue, newValue) -> {
      if (newValue != null) {
        this.onRefresh();
      }
    });

    /*
     * Allow selecting multiple roles, and enable/disable the buttons
     * when roles are selected.
     */

    this.rolesForTarget.getSelectionModel()
      .getSelectedItems()
      .addListener((ListChangeListener<MRoleName>) c -> {
        final var list = c.getList();
        this.rolesRemove.setDisable(
          list.isEmpty() || this.targetUser.get() == null);
      });

    this.rolesForOwner.getSelectionModel()
      .getSelectedItems()
      .addListener((ListChangeListener<MRoleName>) c -> {
        final var list = c.getList();
        this.rolesAdd.setDisable(
          list.isEmpty() || this.targetUser.get() == null);
      });
  }

  private void onClientStateChanged(
    final HBStateType<
      CAICommandType<?>, CAIResponseType, CAIResponseError, CAClientCredentials> s)
  {
    if (s instanceof HBStateConnected) {
      Platform.runLater(() -> {
        this.uuidForOwner.setText(
          this.clientService.client()
            .userId()
            .map(UUID::toString)
            .orElse("")
        );
      });
    }

    if (s instanceof HBStateExecutingLoginSucceeded) {
      this.refreshOwner(this.clientService.client());
    }
  }

  @FXML
  private void onRefresh()
  {
    final var client =
      this.clientService.client();

    this.refreshOwner(client);
    this.refreshTarget(client);
  }

  /*
   * If there's a target user, refresh them.
   */

  private void refreshTarget(
    final CAClientAsynchronousType client)
  {
    final var targetID = this.targetUser.get();
    if (targetID != null) {
      client.executeAsyncOrElseThrow(
        new CAICommandRolesGet(targetID),
        CAClientException::ofError
      ).thenAcceptAsync(this::acceptRolesGet);
    }
  }

  /*
   * If there's an owner user, refresh them.
   */

  private void refreshOwner(
    final CAClientAsynchronousType client)
  {
    client.userId()
      .ifPresent(uuid -> {
        client.executeAsyncOrElseThrow(
            new CAICommandRolesGet(uuid),
            CAClientException::ofError)
          .thenAcceptAsync(r -> {
            if (r instanceof final CAIResponseRolesGet get) {
              Platform.runLater(() -> {
                this.onOwnerRolesUpdated(get.roles());
              });
            }
          });
      });
  }

  private void onTargetRolesUpdated(
    final Set<MRoleName> roles)
  {
    this.targetRoles.setAll(
      roles.stream()
        .sorted()
        .toList()
    );
  }

  private void onOwnerRolesUpdated(
    final Set<MRoleName> roles)
  {
    this.ownerRoles.setAll(
      roles.stream()
        .sorted()
        .toList()
    );
  }

  @FXML
  private void onTargetChanged()
  {
    try {
      this.targetUser.set(UUID.fromString(this.uuidForTarget.getText()));
    } catch (final IllegalArgumentException e) {
      this.targetUser.set(null);
    }
  }

  @FXML
  private void onRolesRemove()
  {
    final var targetID = this.targetUser.get();
    if (targetID != null) {
      final var roles =
        this.rolesForTarget.getSelectionModel()
          .getSelectedItems();
      final var rolesToRevoke =
        Set.copyOf(roles.stream().toList());

      final var client =
        this.clientService.client();

      client.executeAsyncOrElseThrow(
        new CAICommandRolesRevoke(targetID, rolesToRevoke),
        CAClientException::ofError
      ).thenCompose(r -> {
        return client.executeAsyncOrElseThrow(
          new CAICommandRolesGet(targetID),
          CAClientException::ofError
        );
      }).thenAcceptAsync(this::acceptRolesGet);
    }
  }

  @FXML
  private void onRolesAdd()
  {
    final var targetID = this.targetUser.get();
    if (targetID != null) {
      final var roles =
        this.rolesForOwner.getSelectionModel()
          .getSelectedItems();
      final var rolesToAdd =
        Set.copyOf(roles.stream().toList());

      final var client =
        this.clientService.client();

      client.executeAsyncOrElseThrow(
        new CAICommandRolesAssign(targetID, rolesToAdd),
        CAClientException::ofError
      ).thenCompose(r -> {
        return client.executeAsyncOrElseThrow(
          new CAICommandRolesGet(targetID),
          CAClientException::ofError
        );
      }).thenAcceptAsync(this::acceptRolesGet);
    }
  }

  private void acceptRolesGet(
    final CAIResponseType r)
  {
    if (r instanceof final CAIResponseRolesGet get) {
      Platform.runLater(() -> {
        this.onTargetRolesUpdated(get.roles());
      });
    }
  }

  private static final class RoleNameCellCallback
    implements Callback<ListView<MRoleName>, ListCell<MRoleName>>
  {
    @Override
    public ListCell<MRoleName> call(
      final ListView<MRoleName> param)
    {
      return new ListCell<>()
      {
        @Override
        protected void updateItem(
          final MRoleName t,
          final boolean x)
        {
          super.updateItem(t, x);
          if (t != null) {
            this.setText(t.value());
          }
        }
      };
    }
  }
}
