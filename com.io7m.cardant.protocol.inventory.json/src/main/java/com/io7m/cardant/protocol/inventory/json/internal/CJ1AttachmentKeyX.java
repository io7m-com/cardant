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

import com.io7m.cardant.model.CAAttachmentKey;
import com.io7m.cardant.model.CAFileID;

public enum CJ1AttachmentKeyX
  implements CJ1SerialBijectionType<CJ1AttachmentKey, CAAttachmentKey>
{
  ATTACHMENT_KEY;

  @Override
  public CAAttachmentKey toCore(
    final CJ1AttachmentKey m)
  {
    return new CAAttachmentKey(
      new CAFileID(m.fileID()),
      m.relation()
    );
  }

  @Override
  public CJ1AttachmentKey toCJ1(
    final CAAttachmentKey m)
  {
    return new CJ1AttachmentKey(
      m.fileID().id(),
      m.relation()
    );
  }
}
