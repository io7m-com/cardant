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
import com.io7m.cardant.gui.internal.model.CALocationItemType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public final class CALocationItemCellController
{
  @FXML
  private ImageView locationImage;

  @FXML
  private ImageView locationAllImage;

  @FXML
  private Label locationName;

  public CALocationItemCellController()
  {

  }

  public void setLocationItem(
    final CAMainStrings strings,
    final CALocationItemType item)
  {
    if (item instanceof CALocationItemAll all) {
      this.locationImage.setVisible(false);
      this.locationAllImage.setVisible(true);
      this.locationName.setText(strings.format("locations.anywhere"));
    } else if (item instanceof CALocationItemDefined defined) {
      this.locationImage.setVisible(true);
      this.locationAllImage.setVisible(false);
      this.locationName.textProperty()
        .bind(defined.name());
    } else {
      throw new IllegalStateException("Unexpected location: " + item);
    }
  }
}
