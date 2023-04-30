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

import com.io7m.cardant.client.preferences.api.CAPreferencesServiceType;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.UUID;

public final class CAViewControllerLocationCreate implements Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAViewControllerLocationCreate.class);

  private final Stage stage;
  private final RPServiceDirectoryType mainServices;
  private final CAPreferencesServiceType preferences;
  private final CAMainStrings strings;
  private Optional<CALocation> result;
  private Optional<CALocation> parent;

  @FXML private Label idBad;
  @FXML private TextField idField;
  @FXML private TextField nameField;
  @FXML private TextField parentNameField;
  @FXML private TextField parentIdField;
  @FXML private TextArea descriptionArea;
  @FXML private Button createButton;

  public CAViewControllerLocationCreate(
    final RPServiceDirectoryType inMainServices,
    final Stage inStage)
  {
    this.stage =
      Objects.requireNonNull(inStage, "stage");
    this.mainServices =
      Objects.requireNonNull(inMainServices, "mainServices");
    this.preferences =
      this.mainServices.requireService(CAPreferencesServiceType.class);
    this.strings =
      this.mainServices.requireService(CAMainStrings.class);
    this.result =
      Optional.empty();
    this.parent =
      Optional.empty();
  }

  public void setParent(
    final CALocation parent)
  {
    this.parent = Optional.of(Objects.requireNonNull(parent, "parent"));
    this.parentIdField.setText(parent.id().id().toString());
    this.parentNameField.setText(parent.name());
  }

  @FXML
  private void onGenerateIDSelected()
  {
    this.idField.setText(UUID.randomUUID().toString());
    this.validate();
  }

  @FXML
  private void onFieldChanged()
  {
    this.validate();
  }

  @FXML
  private void onCancelSelected()
  {
    this.stage.close();
  }

  @FXML
  private void onCreateSelected()
  {
    this.result = this.validate();
    this.stage.close();
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.createButton.setDisable(true);
    this.idBad.setVisible(false);
  }

  private Optional<CALocation> validate()
  {
    boolean ok = true;

    try {
      UUID.fromString(this.idField.getCharacters().toString());
      this.idBad.setVisible(false);
    } catch (final IllegalArgumentException e) {
      ok = false;
      this.idBad.setVisible(true);
    }

    this.createButton.setDisable(!ok);
    if (ok) {
      final var locationId =
        CALocationID.of(this.idField.getCharacters().toString());

      final var meta =
        new CAItemMetadata(
          "Description",
          this.descriptionArea.getText()
        );

      final var metadata = new TreeMap<String, CAItemMetadata>();
      metadata.put(meta.name(), meta);

      return Optional.of(
        new CALocation(
          locationId,
          this.parent.map(CALocation::id),
          this.nameField.getCharacters().toString(),
          this.descriptionArea.getText()
        )
      );
    }
    return Optional.empty();
  }

  public Optional<CALocation> result()
  {
    return this.result;
  }
}
