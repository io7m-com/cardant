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
import com.io7m.cardant.protocol.api.CAProtocolUncheckedException;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.protocol.inventory.CAITransactionResponse;

public enum CJ1TransactionResponseX
  implements CJ1SerialBijectionType<CJ1TransactionResponse, CAITransactionResponse>
{
  TRANSACTION_RESPONSE;

  @Override
  public CAITransactionResponse toCore(
    final CJ1TransactionResponse m)
    throws CAProtocolException
  {
    try {
      return new CAITransactionResponse(
        m.requestId(),
        m.responses()
          .stream()
          .map(x -> {
            try {
              return CJ1MessageTypeX.MESSAGE.toCore(x);
            } catch (final CAProtocolException e) {
              throw new CAProtocolUncheckedException(e);
            }
          })
          .map(CAIResponseType.class::cast)
          .toList()
      );
    } catch (final CAProtocolUncheckedException e) {
      throw e.getCause();
    }
  }

  @Override
  public CJ1TransactionResponse toCJ1(
    final CAITransactionResponse m)
    throws CAProtocolException
  {
    try {
      return new CJ1TransactionResponse(
        m.requestId(),
        m.responses()
          .stream()
          .map(x -> {
            try {
              return CJ1MessageTypeX.MESSAGE.toCJ1(x);
            } catch (final CAProtocolException e) {
              throw new CAProtocolUncheckedException(e);
            }
          })
          .map(CJ1ResponseType.class::cast)
          .toList()
      );
    } catch (final CAProtocolUncheckedException e) {
      throw e.getCause();
    }
  }
}
