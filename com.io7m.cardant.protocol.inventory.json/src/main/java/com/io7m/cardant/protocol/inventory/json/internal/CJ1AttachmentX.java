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

import com.io7m.cardant.model.CAAttachment;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1FileTypeX.FILE;

public enum CJ1AttachmentX
  implements CJ1SerialBijectionType<CJ1Attachment, CAAttachment>
{
  ATTACHMENT;

  @Override
  public CAAttachment toCore(
    final CJ1Attachment m)
  {
    return new CAAttachment(
      FILE.toCore(m.file()),
      m.relation()
    );
  }

  @Override
  public CJ1Attachment toCJ1(
    final CAAttachment m)
  {
    return new CJ1Attachment(
      FILE.toCJ1(m.file()),
      m.relation()
    );
  }
}
