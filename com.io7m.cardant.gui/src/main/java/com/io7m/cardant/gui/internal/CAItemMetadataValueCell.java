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

import com.io7m.cardant.model.CAItemMetadata;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.Tooltip;

import java.util.Objects;
import java.util.function.Consumer;

public final class CAItemMetadataValueCell
  extends TableCell<CAItemMetadata, CAItemMetadata>
{
  private final CAMainStrings strings;
  private final Consumer<CAItemMetadata> onWantEdit;
  private final Tooltip tooltip;

  public CAItemMetadataValueCell(
    final CAMainStrings inStrings,
    final Consumer<CAItemMetadata> inOnWantEdit)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.onWantEdit =
      Objects.requireNonNull(inOnWantEdit, "onWantEdit");
    this.tooltip =
      new Tooltip(this.strings.format("items.metadata.tooltip.edit"));
  }

  @Override
  protected void updateItem(
    final CAItemMetadata item,
    final boolean empty)
  {
    super.updateItem(item, empty);

    if (empty || item == null) {
      this.setGraphic(null);
      this.setText(null);
      this.setTooltip(null);
      return;
    }

    final var contextMenu =
      new ContextMenu();
    final var editValue =
      new MenuItem(this.strings.format("items.metadata.editor.modify"));
    editValue.setOnAction(event -> this.onWantEdit.accept(item));

    contextMenu.getItems().add(editValue);
    this.setContextMenu(contextMenu);
    this.setGraphic(null);
    this.setText(item.value());
    this.setTooltip(this.tooltip);
  }
}
