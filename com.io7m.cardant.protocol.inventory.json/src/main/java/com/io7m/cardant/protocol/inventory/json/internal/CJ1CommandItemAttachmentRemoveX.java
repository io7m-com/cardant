/*
 * Copyright © 2025 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.protocol.inventory.json.internal;

import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentRemove;

public enum CJ1CommandItemAttachmentRemoveX
  implements CJ1SerialBijectionType<CJ1CommandItemAttachmentRemove, CAICommandItemAttachmentRemove>
{
  ITEM_ATTACHMENT_REMOVE;

  @Override
  public CAICommandItemAttachmentRemove toCore(
    final CJ1CommandItemAttachmentRemove m)
  {
    return new CAICommandItemAttachmentRemove(
      CAItemID.of(m.item()),
      CAFileID.of(m.file()),
      m.relation()
    );
  }

  @Override
  public CJ1CommandItemAttachmentRemove toCJ1(
    final CAICommandItemAttachmentRemove m)
  {
    return new CJ1CommandItemAttachmentRemove(
      m.item().id(),
      m.file().id(),
      m.relation()
    );
  }
}
