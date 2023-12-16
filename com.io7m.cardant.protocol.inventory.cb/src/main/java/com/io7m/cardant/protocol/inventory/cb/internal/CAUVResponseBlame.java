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


package com.io7m.cardant.protocol.inventory.cb.internal;

import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.CAIResponseBlame;
import com.io7m.cardant.protocol.inventory.cb.CAI1ResponseBlame;

/**
 * A validator.
 */

public enum CAUVResponseBlame
  implements CAProtocolMessageValidatorType<CAIResponseBlame, CAI1ResponseBlame>
{
  /**
   * A validator.
   */

  RESPONSE_BLAME;

  @Override
  public CAI1ResponseBlame convertToWire(
    final CAIResponseBlame blame)
  {
    return switch (blame) {
      case BLAME_SERVER -> {
        yield new CAI1ResponseBlame.BlameServer();
      }
      case BLAME_CLIENT -> {
        yield new CAI1ResponseBlame.BlameClient();
      }
    };
  }

  @Override
  public CAIResponseBlame convertFromWire(
    final CAI1ResponseBlame blame)
  {
    return switch (blame) {
      case final CAI1ResponseBlame.BlameClient blameClient ->
        CAIResponseBlame.BLAME_CLIENT;
      case final CAI1ResponseBlame.BlameServer blameServer ->
        CAIResponseBlame.BLAME_SERVER;
    };
  }
}
