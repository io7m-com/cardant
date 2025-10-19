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

import com.io7m.cardant.model.CAIncludeDeleted;

public enum CJ1IncludeDeletedX
  implements CJ1SerialBijectionType<CJ1IncludeDeleted, CAIncludeDeleted>
{
  INCLUDE_DELETED;

  @Override
  public CAIncludeDeleted toCore(
    final CJ1IncludeDeleted m)
  {
    return switch (m) {
      case INCLUDE_ONLY_LIVE -> {
        yield CAIncludeDeleted.INCLUDE_ONLY_LIVE;
      }
      case INCLUDE_ONLY_DELETED -> {
        yield CAIncludeDeleted.INCLUDE_ONLY_DELETED;
      }
      case INCLUDE_BOTH_LIVE_AND_DELETED -> {
        yield CAIncludeDeleted.INCLUDE_BOTH_LIVE_AND_DELETED;
      }
    };
  }

  @Override
  public CJ1IncludeDeleted toCJ1(
    final CAIncludeDeleted m)
  {
    return switch (m) {
      case INCLUDE_ONLY_LIVE -> {
        yield CJ1IncludeDeleted.INCLUDE_ONLY_LIVE;
      }
      case INCLUDE_ONLY_DELETED -> {
        yield CJ1IncludeDeleted.INCLUDE_ONLY_DELETED;
      }
      case INCLUDE_BOTH_LIVE_AND_DELETED -> {
        yield CJ1IncludeDeleted.INCLUDE_BOTH_LIVE_AND_DELETED;
      }
    };
  }
}