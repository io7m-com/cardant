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
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.services.api.CAServiceDirectoryType;
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
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.UUID;

public final class CAViewControllerCreateItem implements Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAViewControllerCreateItem.class);

  private final Stage stage;
  private final CAServiceDirectoryType mainServices;
  private final CAPreferencesServiceType preferences;
  private final CAMainStrings strings;

  @FXML
  private Label idBad;

  @FXML
  private TextField idField;

  @FXML
  private TextField nameField;

  @FXML
  private TextArea descriptionArea;

  @FXML
  private Button createButton;
  private Optional<CAItem> result;

  public CAViewControllerCreateItem(
    final CAServiceDirectoryType inMainServices,
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

  private Optional<CAItem> validate()
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
      final var itemId =
        CAItemID.of(this.idField.getCharacters().toString());

      final var meta =
        new CAItemMetadata(
          itemId,
          "Description",
          this.descriptionArea.getText()
        );

      final var metadata = new TreeMap<String, CAItemMetadata>();
      metadata.put(meta.name(), meta);

      return Optional.of(
        new CAItem(
          itemId,
          this.nameField.getCharacters().toString(),
          0L,
          metadata,
          Collections.emptySortedMap(),
          Collections.emptySortedSet()
        )
      );
    }
    return Optional.empty();
  }

  public Optional<CAItem> result()
  {
    return this.result;
  }
}
