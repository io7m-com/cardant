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
import com.io7m.jaffirm.core.Preconditions;

import java.util.Objects;

import static com.io7m.cardant.gui.internal.model.CAStringSearch.containsIgnoreCase;

public record CAItemAttachmentMutable(
  String relation,
  CAFileMutable file)
{
  public static CAItemAttachmentMutable ofItemAttachment(
    final CAItemAttachment itemAttachment)
  {
    return new CAItemAttachmentMutable(
      itemAttachment.relation(),
      CAFileMutable.ofFile(itemAttachment.file())
    );
  }

  public void updateFrom(
    final CAItemAttachment attachment)
  {
    Preconditions.checkPreconditionV(
      Objects.equals(attachment.relation(), this.relation),
      "Relation %s must be %s",
      attachment.relation(),
      this.relation
    );

    this.file.updateFrom(attachment.file());
  }

  public boolean matches(
    final String search)
  {
    return containsIgnoreCase(this.relation, search)
      || this.file.matches(search);
  }
}
