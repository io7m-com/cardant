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

import com.io7m.cardant.model.CAItemAttachment;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public final class CAAttachmentCellController
{
  @FXML
  private AnchorPane container;

  @FXML
  private TextField idField;

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

  public CAAttachmentCellController()
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
    final CAItemAttachment attachment)
  {
    this.idField.setText(attachment.id().id().toString());
    this.descriptionField.setText(attachment.description());
    this.mediaTypeField.setText(attachment.mediaType());
    this.relationField.setText(attachment.relation());
    this.sizeField.setText(formatSize(attachment.size()));
    this.hashAlgorithmField.setText(attachment.hashAlgorithm());
    this.hashValueField.setText(attachment.hashValue());
  }
}
