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

import com.io7m.cardant.services.api.CAServiceDirectoryType;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public final class CAViewControllerError implements Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAViewControllerError.class);

  private final Stage stage;
  private final CAServiceDirectoryType mainServices;
  private final CAMainStrings strings;
  @FXML
  private TableColumn<ErrorAttribute, String> errorNameColumn;
  @FXML
  private TableColumn<ErrorAttribute, String> errorValueColumn;
  @FXML
  private TableView<ErrorAttribute> errorTableView;
  @FXML
  private Label errorMessage;
  @FXML
  private TextArea errorDetails;
  @FXML
  private Pane errorContainerPane;
  @FXML
  private Button dismiss;

  public CAViewControllerError(
    final CAServiceDirectoryType inMainServices,
    final Stage inStage)
  {
    this.stage =
      Objects.requireNonNull(inStage, "stage");
    this.mainServices =
      Objects.requireNonNull(inMainServices, "mainServices");
    this.strings =
      this.mainServices.requireService(CAMainStrings.class);
  }

  @FXML
  private void onDismissSelected()
  {
    this.stage.close();
  }

  @Override
  public void initialize(
    final URL location,
    final ResourceBundle resources)
  {
    this.errorNameColumn.setCellValueFactory(
      param -> new ReadOnlyObjectWrapper<>(param.getValue().name()));
    this.errorValueColumn.setCellValueFactory(
      param -> new ReadOnlyObjectWrapper<>(param.getValue().value()));
  }

  public void setEvent(
    final CAMainEventType item)
  {
    this.errorMessage.setText(item.message());

    if (item instanceof CAMainEventErrorWithAttributesType failed) {
      final var errorAttributes = failed.attributes();

      if (!errorAttributes.isEmpty()) {
        this.errorTableView.setItems(
          FXCollections.observableList(
            errorAttributes
              .entrySet()
              .stream()
              .map(e -> new ErrorAttribute(e.getKey(), e.getValue()))
              .sorted((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(
                o1.name(),
                o2.name()))
              .collect(Collectors.toList())
          )
        );
      } else {
        this.errorContainerPane.getChildren()
          .remove(this.errorTableView);
      }

      final var errorDetails = failed.details();
      this.errorDetails.setText("");
      this.errorDetails.appendText(failed.message());
      this.errorDetails.appendText("\n");
      this.errorDetails.appendText("\n");

      for (final var detail : errorDetails) {
        this.errorDetails.appendText(detail);
        this.errorDetails.appendText("\n");
      }
    } else {
      this.errorDetails.setText(item.message());
    }
  }

  private record ErrorAttribute(String name, String value)
  {
  }
}
