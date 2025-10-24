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

import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.protocol.inventory.CAICommandItemCreate;

public enum CJ1CommandItemCreateX
  implements CJ1SerialBijectionType<CJ1CommandItemCreate, CAICommandItemCreate>
{
  ITEM_CREATE;

  @Override
  public CAICommandItemCreate toCore(
    final CJ1CommandItemCreate m)
  {
    return new CAICommandItemCreate(
      CAItemID.of(m.id()),
      m.name()
    );
  }

  @Override
  public CJ1CommandItemCreate toCJ1(
    final CAICommandItemCreate m)
  {
    return new CJ1CommandItemCreate(
      m.id().id(),
      m.name()
    );
  }
}
