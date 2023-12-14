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


package com.io7m.cardant.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * An inclusive range of monetary values.
 *
 * @param lower The lower bound
 * @param upper The upper bound
 */

public record CAMonetaryRange(
  BigDecimal lower,
  BigDecimal upper)
{
  /**
   * An inclusive range of monetary values.
   *
   * @param lower The lower bound
   * @param upper The upper bound
   */

  public CAMonetaryRange
  {
    Objects.requireNonNull(lower, "lower");
    Objects.requireNonNull(upper, "upper");

    if (lower.compareTo(upper) > 0) {
      throw new CAValidityException(
        "Upper value %s must be <= lower value %s.".formatted(lower, upper)
      );
    }
  }
}
