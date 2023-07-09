/*
 * Copyright © 2023 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.protocol.inventory;

import com.io7m.cardant.model.CAItemID;
import com.io7m.lanark.core.RDottedName;

import java.util.Objects;
import java.util.Set;

/**
 * Assign one or more types to an item.
 *
 * @param item The item
 * @param types The names of types that will be added
 */

public record CAICommandItemTypesAssign(
  CAItemID item,
  Set<RDottedName> types)
  implements CAICommandType<CAIResponseItemTypesAssign>
{
  /**
   * Assign one or more types to an item.
   *
   * @param item The item
   * @param types The names of types that will be added
   */

  public CAICommandItemTypesAssign
  {
    Objects.requireNonNull(item, "item");
    Objects.requireNonNull(types, "types");
  }

  @Override
  public Class<CAIResponseItemTypesAssign> responseClass()
  {
    return CAIResponseItemTypesAssign.class;
  }
}
