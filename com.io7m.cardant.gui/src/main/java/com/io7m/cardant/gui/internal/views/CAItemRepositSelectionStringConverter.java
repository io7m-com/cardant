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
import javafx.util.StringConverter;

import java.util.Objects;

import static com.io7m.cardant.gui.internal.views.CAItemRepositSelection.ITEM_REPOSIT_ADD;
import static com.io7m.cardant.gui.internal.views.CAItemRepositSelection.ITEM_REPOSIT_MOVE;
import static com.io7m.cardant.gui.internal.views.CAItemRepositSelection.ITEM_REPOSIT_REMOVE;

public final class CAItemRepositSelectionStringConverter
  extends StringConverter<CAItemRepositSelection>
{
  private final String add;
  private final String move;
  private final String remove;

  public CAItemRepositSelectionStringConverter(
    final CAMainStrings strings)
  {
    Objects.requireNonNull(strings, "strings");

    this.add =
      strings.format("item.reposit.add");
    this.move =
      strings.format("item.reposit.move");
    this.remove =
      strings.format("item.reposit.remove");
  }

  @Override
  public String toString(
    final CAItemRepositSelection object)
  {
    return switch (object) {
      case ITEM_REPOSIT_ADD -> this.add;
      case ITEM_REPOSIT_MOVE -> this.move;
      case ITEM_REPOSIT_REMOVE -> this.remove;
    };
  }

  @Override
  public CAItemRepositSelection fromString(final String string)
  {
    if (Objects.equals(string, this.add)) {
      return ITEM_REPOSIT_ADD;
    }
    if (Objects.equals(string, this.move)) {
      return ITEM_REPOSIT_MOVE;
    }
    if (Objects.equals(string, this.remove)) {
      return ITEM_REPOSIT_REMOVE;
    }
    return ITEM_REPOSIT_ADD;
  }
}
