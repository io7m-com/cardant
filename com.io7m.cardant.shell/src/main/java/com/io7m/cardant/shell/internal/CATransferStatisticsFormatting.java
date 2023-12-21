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


package com.io7m.cardant.shell.internal;

import com.io7m.cardant.client.api.CAClientTransferStatistics;
import org.apache.commons.io.FileUtils;

/**
 * Functions to format transfer statistics.
 */

public final class CATransferStatisticsFormatting
{
  private CATransferStatisticsFormatting()
  {

  }

  /**
   * Format the given stats.
   *
   * @param stats The stats
   *
   * @return The stats as a string
   */

  public static String format(
    final CAClientTransferStatistics stats)
  {
    final var bar = new StringBuilder(10);

    final var percent = (int) stats.percent();
    for (int index = 0; index < 100; index += 10) {
      if (index >= percent) {
        bar.append('-');
      } else {
        bar.append('*');
      }
    }

    return String.format(
      "# %8.1f%% %s %s/s",
      Double.valueOf(stats.percent()),
      bar,
      FileUtils.byteCountToDisplaySize(Double.valueOf(stats.octetsPerSecond()))
    );
  }
}
