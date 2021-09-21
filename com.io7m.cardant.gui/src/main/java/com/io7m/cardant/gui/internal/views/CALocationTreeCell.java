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

import com.io7m.cardant.gui.internal.CAMainStrings;
import com.io7m.cardant.gui.internal.model.CALocationItemAll;
import com.io7m.cardant.gui.internal.model.CALocationItemDefined;
import com.io7m.cardant.gui.internal.model.CALocationItemRoot;
import com.io7m.cardant.gui.internal.model.CALocationItemType;
import javafx.scene.control.TreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public final class CALocationTreeCell
  extends TreeCell<CALocationItemType>
{
  private final CAMainStrings strings;
  private final Image imageLocation;
  private final Image imageAll;

  public CALocationTreeCell(
    final CAMainStrings inStrings,
    final Image inImageLocation,
    final Image inImageAll)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "strings");
    this.imageLocation =
      Objects.requireNonNull(inImageLocation, "imageLocation");
    this.imageAll =
      Objects.requireNonNull(inImageAll, "imageAll");
  }

  @Override
  protected void updateItem(
    final CALocationItemType item,
    final boolean empty)
  {
    super.updateItem(item, empty);

    if (empty || item == null) {
      this.setGraphic(null);
      this.setText(null);
    } else {
      if (item instanceof CALocationItemAll all) {
        this.setGraphic(new ImageView(this.imageAll));
        this.setText(this.strings.format("locations.anywhere"));
      } else if (item instanceof CALocationItemDefined defined) {
        this.setGraphic(new ImageView(this.imageLocation));
        this.setText(defined.name().getValue());
      } else if (item instanceof CALocationItemRoot root) {
        this.setGraphic(null);
        this.setText(null);
      }
    }
  }
}
