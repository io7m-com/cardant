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

import com.io7m.cardant.model.CAIncludeDeleted;
import com.io7m.cardant.protocol.api.CAProtocolMessageValidatorType;
import com.io7m.cardant.protocol.inventory.cb.CAI1IncludeDeleted;
import com.io7m.cardant.protocol.inventory.cb.CAI1IncludeDeleted.IncludeLiveAndDeleted;
import com.io7m.cardant.protocol.inventory.cb.CAI1IncludeDeleted.IncludeOnlyDeleted;
import com.io7m.cardant.protocol.inventory.cb.CAI1IncludeDeleted.IncludeOnlyLive;

/**
 * A validator.
 */

public enum CAUVIncludeDeleted
  implements CAProtocolMessageValidatorType<CAIncludeDeleted, CAI1IncludeDeleted>
{
  /**
   * A validator.
   */

  INCLUDE_DELETED;

  @Override
  public CAI1IncludeDeleted convertToWire(
    final CAIncludeDeleted message)
  {
    return switch (message) {
      case INCLUDE_ONLY_LIVE -> {
        yield new IncludeOnlyLive();
      }
      case INCLUDE_ONLY_DELETED -> {
        yield new IncludeOnlyDeleted();
      }
      case INCLUDE_BOTH_LIVE_AND_DELETED -> {
        yield new IncludeLiveAndDeleted();
      }
    };
  }

  @Override
  public CAIncludeDeleted convertFromWire(
    final CAI1IncludeDeleted message)
  {
    return switch (message) {
      case final IncludeLiveAndDeleted i -> {
        yield CAIncludeDeleted.INCLUDE_BOTH_LIVE_AND_DELETED;
      }
      case final IncludeOnlyDeleted i -> {
        yield CAIncludeDeleted.INCLUDE_ONLY_DELETED;
      }
      case final IncludeOnlyLive i -> {
        yield CAIncludeDeleted.INCLUDE_ONLY_LIVE;
      }
    };
  }
}
