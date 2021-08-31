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

import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemAttachmentID;
import com.io7m.cardant.model.CAItemID;
import javafx.beans.property.SimpleStringProperty;

import static com.io7m.cardant.gui.internal.model.CAStringSearch.containsIgnoreCase;

public record CAItemAttachmentMutable(
  CAItemAttachmentID id,
  CAItemID itemID,
  SimpleStringProperty description,
  SimpleStringProperty mediaType,
  SimpleStringProperty relation,
  long size,
  String hashAlgorithm,
  String hashValue)
{
  public static CAItemAttachmentMutable ofItemAttachment(
    final CAItemAttachment itemAttachment)
  {
    return new CAItemAttachmentMutable(
      itemAttachment.id(),
      itemAttachment.itemId(),
      new SimpleStringProperty(itemAttachment.description()),
      new SimpleStringProperty(itemAttachment.mediaType()),
      new SimpleStringProperty(itemAttachment.relation()),
      itemAttachment.size(),
      itemAttachment.hashAlgorithm(),
      itemAttachment.hashValue()
    );
  }

  public void updateFrom(
    final CAItemAttachment attachment)
  {
    this.description.set(attachment.description());
    this.mediaType.set(attachment.mediaType());
    this.relation.set(attachment.relation());
  }

  public boolean matches(
    final String search)
  {
    return containsIgnoreCase(this.description, search)
      || containsIgnoreCase(this.mediaType, search)
      || containsIgnoreCase(this.relation, search)
      || containsIgnoreCase(this.hashValue, search);
  }
}
