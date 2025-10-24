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

import com.io7m.cardant.protocol.api.CAProtocolException;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIMessageType;
import com.io7m.cardant.protocol.inventory.CAITransaction;

import java.math.BigInteger;
import java.util.ArrayList;

public enum CJ1TransactionX
  implements CJ1SerialBijectionType<CJ1Transaction, CAITransaction>
{
  TRANSACTION;

  @Override
  public CAITransaction toCore(
    final CJ1Transaction m)
    throws CAProtocolException
  {
    final var commands =
      new ArrayList<CAICommandType<?>>(m.commands().size());

    for (final var source : m.commands()) {
      commands.add(
        (CAICommandType<?>) CJ1MessageTypeX.MESSAGE.toCore(source)
      );
    }

    return new CAITransaction(commands);
  }

  @Override
  public CJ1Transaction toCJ1(
    final CAITransaction m)
    throws CAProtocolException
  {
    final var commands =
      new ArrayList<CJ1CommandType>(m.commands().size());

    for (final var source : m.commands()) {
      commands.add(
        (CJ1CommandType) CJ1MessageTypeX.MESSAGE.toCJ1(source)
      );
    }

    return new CJ1Transaction(commands);
  }
}
