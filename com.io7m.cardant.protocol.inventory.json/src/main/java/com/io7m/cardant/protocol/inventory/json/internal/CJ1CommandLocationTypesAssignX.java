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

import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.protocol.inventory.CAICommandLocationTypesAssign;

import java.util.stream.Collectors;

import static com.io7m.cardant.protocol.inventory.json.internal.CJ1TypeRecordIdentifierX.TYPE_RECORD_IDENTIFIER;

public enum CJ1CommandLocationTypesAssignX
  implements CJ1SerialBijectionType<CJ1CommandLocationTypesAssign, CAICommandLocationTypesAssign>
{
  LOCATION_TYPES_ASSIGN;

  @Override
  public CAICommandLocationTypesAssign toCore(
    final CJ1CommandLocationTypesAssign m)
  {
    return new CAICommandLocationTypesAssign(
      CALocationID.of(m.location()),
      m.types()
        .stream()
        .map(TYPE_RECORD_IDENTIFIER::toCore)
        .collect(Collectors.toSet())
    );
  }

  @Override
  public CJ1CommandLocationTypesAssign toCJ1(
    final CAICommandLocationTypesAssign m)
  {
    return new CJ1CommandLocationTypesAssign(
      m.location().id(),
      m.types()
        .stream()
        .map(TYPE_RECORD_IDENTIFIER::toCJ1)
        .collect(Collectors.toSet())
    );
  }
}
