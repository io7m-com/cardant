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

package com.io7m.cardant.gui.internal.views;

import com.io7m.cardant.gui.internal.CASizeFormatter;
import com.io7m.cardant.gui.internal.model.CAItemAttachmentMutable;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public final class CAItemAttachmentMutableCellController
{
  @FXML
  private AnchorPane container;

  @FXML
  private TextField fileIdField;

  @FXML
  private TextField descriptionField;

  @FXML
  private TextField mediaTypeField;

  @FXML
  private TextField relationField;

  @FXML
  private TextField sizeField;

  @FXML
  private TextField hashAlgorithmField;

  @FXML
  private TextField hashValueField;

  public CAItemAttachmentMutableCellController()
  {

  }

  private static String formatSize(
    final long size)
  {
    return new StringBuilder(128)
      .append(CASizeFormatter.formatSize(size))
      .append(" (")
      .append(Long.toUnsignedString(size))
      .append("B)")
      .toString();
  }

  public void setItemAttachment(
    final CAItemAttachmentMutable attachment)
  {
    final var file = attachment.file();

    this.fileIdField.setText(file.id().displayId());
    this.descriptionField.textProperty()
      .bind(file.description());
    this.mediaTypeField.textProperty()
      .bind(file.mediaType());
    this.relationField.textProperty()
      .set(attachment.relation());

    this.sizeField.setText(formatSize(file.size()));
    this.hashAlgorithmField.setText(file.hashAlgorithm());
    this.hashValueField.setText(file.hashValue());
  }
}
