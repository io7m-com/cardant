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

import com.io7m.cardant.error_codes.CAErrorCode;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import com.io7m.seltzer.api.SStructuredErrorType;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public final class CAViewControllerError implements Initializable
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAViewControllerError.class);

  private final Stage stage;
  private final RPServiceDirectoryType mainServices;
  private final CAMainStrings strings;

  @FXML private TableColumn<Map.Entry<String, String>, String> errorNameColumn;
  @FXML private TableColumn<Map.Entry<String, String>, String> errorValueColumn;
  @FXML private TableView<Map.Entry<String, String>> errorTableView;
  @FXML private Label errorMessage;
  @FXML private TextArea errorDetails;
  @FXML private Pane errorContainerPane;
  @FXML private Button dismiss;

  public CAViewControllerError(
    final RPServiceDirectoryType inMainServices,
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
      param -> new ReadOnlyObjectWrapper<>(param.getValue().getKey()));
    this.errorValueColumn.setCellValueFactory(
      param -> new ReadOnlyObjectWrapper<>(param.getValue().getValue()));
  }

  public void setError(
    final SStructuredErrorType<CAErrorCode> error)
  {
    this.errorMessage.setText(error.message());

    final var exceptionOpt = error.exception();
    if (exceptionOpt.isPresent()) {
      try (var bytes = new ByteArrayOutputStream()) {
        try (var outputS = new PrintStream(bytes, false, UTF_8)) {
          exceptionOpt.get().printStackTrace(outputS);
          outputS.flush();
        }
        this.errorDetails.setText(bytes.toString(UTF_8));
      } catch (final IOException e) {
        // Can't actually happen
      }
    } else {
      this.errorContainerPane.getChildren()
        .remove(this.errorDetails);
    }

    final var errorAttributes = error.attributes();
    if (!errorAttributes.isEmpty()) {
      this.errorTableView.setItems(
        FXCollections.observableList(
          errorAttributes
            .entrySet()
            .stream()
            .sorted(CAViewControllerError::compareEntries)
            .collect(Collectors.toList())
        )
      );
    } else {
      this.errorContainerPane.getChildren()
        .remove(this.errorTableView);
    }
  }

  private static int compareEntries(
    final Map.Entry<String, String> o1,
    final Map.Entry<String, String> o2)
  {
    return String.CASE_INSENSITIVE_ORDER.compare(o1.getKey(), o2.getKey());
  }
}
