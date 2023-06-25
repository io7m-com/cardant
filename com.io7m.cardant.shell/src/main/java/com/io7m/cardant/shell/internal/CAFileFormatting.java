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

import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAFileType.CAFileWithoutData;
import com.io7m.cardant.model.CAPage;

import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.Math.max;

/**
 * Functions to format items.
 */

public final class CAFileFormatting
{
  private CAFileFormatting()
  {

  }

  /**
   * Format an item to the given output.
   *
   * @param w    The output
   * @param file The item
   */

  public static void formatItem(
    final PrintWriter w,
    final CAFileType file)
  {
    final var main = new TreeMap<String, String>();
    main.put("File ID", file.id().displayId());
    main.put("Description", file.description());

    w.printf("# File %s%n", file.id().displayId());
    w.printf("#-----------------------------------------%n");
    w.println();
    paddedTable(w, main);

    w.println();
  }

  private static void paddedTable(
    final PrintWriter w,
    final Map<String, String> map)
  {
    var keyLength = Integer.MIN_VALUE;
    for (final var entry : map.entrySet()) {
      keyLength = max(entry.getKey().length(), keyLength);
    }
    keyLength += 1;

    for (final var entry : map.entrySet()) {
      final var key = entry.getKey();
      final var val = entry.getValue();
      final var pad = " ".repeat(keyLength - key.length());
      w.printf("%s%s: %s%n", key, pad, val);
    }
  }

  /**
   * Format file summaries to the given output.
   *
   * @param w     The output
   * @param files The summaries
   */

  public static void formatFiles(
    final PrintWriter w,
    final CAPage<CAFileWithoutData> files)
  {
    w.printf(
      "# Search results: Page %d of %d%n",
      Integer.valueOf(files.pageIndex()),
      Integer.valueOf(files.pageCount())
    );
    w.println(
      "#--------------------------------"
    );

    for (final var file : files.items()) {
      w.printf("%s : %s%n", file.id().displayId(), file.description());
    }
  }
}
