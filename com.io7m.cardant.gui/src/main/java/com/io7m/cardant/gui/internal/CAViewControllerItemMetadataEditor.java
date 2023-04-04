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

import com.io7m.cardant.gui.internal.model.CAItemMetadataMutable;
import com.io7m.cardant.gui.internal.model.CAItemMutable;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public final class CAViewControllerItemMetadataEditor implements Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAViewControllerItemMetadataEditor.class);

  private final CAMainEventBusType events;
  private final CAMainStrings strings;
  private final RPServiceDirectoryType mainServices;
  private final Stage stage;

  @FXML private Button cancelButton;
  @FXML private Button createButton;
  @FXML private Label nameBad;
  @FXML private TextField nameField;
  @FXML private TextField valueField;
  @FXML private Tooltip nameBadTooltip;

  private CAItemMutable currentItem;
  private CAPerpetualSubscriber<CAMainEventType> subscriber;
  private Optional<CAItemMetadataMutable> result;
  private boolean editingExisting;

  public CAViewControllerItemMetadataEditor(
    final RPServiceDirectoryType inMainServices,
    final Stage inStage)
  {
    this.stage =
      Objects.requireNonNull(inStage, "stage");
    this.mainServices =
      Objects.requireNonNull(inMainServices, "mainServices");
    this.strings =
      this.mainServices.requireService(CAMainStrings.class);
    this.events =
      this.mainServices.requireService(CAMainEventBusType.class);
    this.editingExisting =
      false;

    this.result =
      Optional.empty();
  }

  @FXML
  private void onCreateSelected()
  {
    this.validate();
    this.subscriber.close();
    this.stage.close();
  }

  @FXML
  private void onFieldChanged()
  {
    this.validate();
  }

  @FXML
  private void onCancelSelected()
  {
    this.result = Optional.empty();
    this.subscriber.close();
    this.stage.close();
  }

  @Override
  public void initialize(
    final URL url,
    final ResourceBundle resourceBundle)
  {
    this.stage.setTitle(
      this.strings.format("items.metadata.editor.title.create"));

    this.nameBad.setVisible(false);
    this.createButton.setDisable(true);

    this.subscriber = new CAPerpetualSubscriber<>(this::onMainEvent);
    this.events.subscribe(this.subscriber);
  }

  private void validate()
  {
    boolean ok = true;

    final var tryName =
      this.nameField.getCharacters()
        .toString()
        .trim();

    if (tryName.isEmpty()) {
      this.nameBad.setVisible(true);
      this.nameBadTooltip.setText(
        this.strings.format("items.tooltip.metadata.nameNotValid"));
      ok = false;
    }

    /*
     * If we're creating a new value, then check to see if there's already
     * a metadata value with this name.
     */

    if (!this.editingExisting) {
      final var exists =
        this.currentItem.metadata()
          .containsKey(tryName);

      if (exists) {
        this.nameBad.setVisible(true);
        this.nameBadTooltip.setText(
          this.strings.format("items.tooltip.metadata.nameAlreadyUsed"));
        ok = false;
      }
    }

    if (ok) {
      this.nameBad.setVisible(false);
      this.createButton.setDisable(false);
      this.result = Optional.of(
        new CAItemMetadataMutable(
          tryName,
          new SimpleStringProperty(this.valueField.getText())
        )
      );
    } else {
      this.createButton.setDisable(true);
      this.result = Optional.empty();
    }
  }

  public Optional<CAItemMetadataMutable> result()
  {
    return this.result;
  }

  public void setEditingMetadata(
    final CAItemMetadataMutable itemMetadata)
  {
    this.editingExisting = true;
    this.stage.setTitle(
      this.strings.format("items.metadata.editor.title.modify"));

    this.nameField.setText(itemMetadata.name());
    this.nameField.setDisable(true);
    this.valueField.setText(itemMetadata.value().getValue());

    this.createButton.setText(
      this.strings.format("items.metadata.modify"));
    this.createButton.setDisable(false);
  }

  private void onMainEvent(
    final CAMainEventType item)
  {

  }

  public void setItem(
    final CAItemMutable item)
  {
    this.currentItem = Objects.requireNonNull(item, "item");
  }
}
