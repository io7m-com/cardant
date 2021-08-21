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
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

public final class CAAttachmentCell extends ListCell<CAItemAttachment>
{
  private final AnchorPane root;
  private final CAAttachmentCellController controller;

  public CAAttachmentCell(
    final CAMainStrings strings)
  {
    Objects.requireNonNull(strings, "strings");

    try {
      final FXMLLoader loader =
        new FXMLLoader(
          CAAttachmentCell.class.getResource("itemAttachment.fxml"));
      loader.setResources(strings.resources());

      this.root = loader.load();
      this.controller = loader.getController();
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  protected void updateItem(
    final CAItemAttachment item,
    final boolean empty)
  {
    super.updateItem(item, empty);
    if (empty || item == null) {
      this.setGraphic(null);
    } else {
      this.controller.setItemAttachment(item);
      this.setGraphic(this.root);
    }
  }
}
