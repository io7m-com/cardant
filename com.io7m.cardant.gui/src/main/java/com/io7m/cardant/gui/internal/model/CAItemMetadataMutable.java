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

package com.io7m.cardant.gui.internal.model;

import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.jaffirm.core.Preconditions;
import javafx.beans.property.SimpleStringProperty;

import java.util.Objects;

public record CAItemMetadataMutable(
  String name,
  CAItemID itemId,
  SimpleStringProperty value)
{
  public CAItemMetadataMutable
  {
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(itemId, "itemId");
    Objects.requireNonNull(value, "value");
  }

  public static CAItemMetadataMutable ofMetadata(
    final CAItemMetadata metadata)
  {
    return new CAItemMetadataMutable(
      metadata.name(),
      metadata.itemId(),
      new SimpleStringProperty(metadata.value())
    );
  }

  public CAItemMetadata toImmutable()
  {
    return new CAItemMetadata(
      this.itemId,
      this.name,
      this.value.getValue()
    );
  }

  public void updateFrom(
    final CAItemMetadata value)
  {
    Preconditions.checkPreconditionV(
      Objects.equals(this.itemId, value.itemId()),
      "Metadata item %s must match this item %s",
      value.itemId().id(),
      this.itemId.id()
    );
    Preconditions.checkPreconditionV(
      Objects.equals(this.itemId, value.itemId()),
      "Metadata name %s must match this name %s",
      value.name(),
      this.name
    );

    this.value.set(value.value());
  }

  public boolean matches(
    final String search)
  {
    return CAStringSearch.containsIgnoreCase(this.name, search)
      || CAStringSearch.containsIgnoreCase(this.value, search);
  }
}
