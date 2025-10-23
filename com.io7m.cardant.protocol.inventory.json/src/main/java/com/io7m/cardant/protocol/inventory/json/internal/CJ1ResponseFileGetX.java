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

import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.inventory.CAIResponseFileGet;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1FileTypeX.FILE;

public enum CJ1ResponseFileGetX
  implements CJ1SerialBijectionType<CJ1ResponseFileGet, CAIResponseFileGet>
{
  RESPONSE_FILE_GET;

  @Override
  public CAIResponseFileGet toCore(
    final CJ1ResponseFileGet m)
    throws CAProtocolException
  {
    return new CAIResponseFileGet(
      m.requestId(),
      (CAFileType.CAFileWithoutData) FILE.toCore(m.file())
    );
  }

  @Override
  public CJ1ResponseFileGet toCJ1(
    final CAIResponseFileGet m)
    throws CAProtocolException
  {
    return new CJ1ResponseFileGet(
      m.requestId(),
      (CJ1FileWithoutData) FILE.toCJ1(m.data())
    );
  }
}
