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

import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CAPage;
import com.io7m.cardant.model.CATag;

import java.io.PrintWriter;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.lang.Math.max;

/**
 * Functions to format items.
 */

public final class CAItemFormatting
{
  private CAItemFormatting()
  {

  }

  /**
   * Format an item to the given output.
   *
   * @param w    The output
   * @param item The item
   */

  public static void formatItem(
    final PrintWriter w,
    final CAItem item)
  {
    final var main = new TreeMap<String, String>();
    main.put("Item ID", item.id().displayId());
    main.put("Name", item.name());
    main.put("Description", item.descriptionOrEmpty());
    main.put("Count (Here)", Long.toUnsignedString(item.countHere()));
    main.put("Count (Total)", Long.toUnsignedString(item.countTotal()));

    w.printf("# Item %s%n", item.id().displayId());
    w.printf("#-----------------------------------------%n");
    w.println();
    paddedTable(w, main);

    final var metadata = item.metadata();
    if (!metadata.isEmpty()) {
      w.println();
      w.println("# Metadata");
      w.println("#---------");
      w.println();

      paddedTable(
        w,
        new TreeMap<>(
          metadata.entrySet()
            .stream()
            .map(e -> Map.entry(
              "Metadata: " + e.getKey(),
              e.getValue().value()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        ));
    }

    final var attachments = item.attachments();
    if (!attachments.isEmpty()) {
      w.println();
      w.println("# Attachments");
      w.println("#------------");
      w.println();

      for (final var entry : attachments.entrySet()) {
        w.println(entry.getKey().fileID().displayId());
      }
    }

    final var tags = item.tags();
    if (!tags.isEmpty()) {
      w.printf(
        "Tags: %s%n",
        tags.stream()
          .map(CATag::name)
          .collect(Collectors.joining(", "))
      );
    }

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
   * Format item summaries to the given output.
   *
   * @param w     The output
   * @param items The summaries
   */

  public static void formatItemSummaries(
    final PrintWriter w,
    final CAPage<CAItemSummary> items)
  {
    w.printf(
      "# Search results: Page %d of %d%n",
      Integer.valueOf(items.pageIndex()),
      Integer.valueOf(items.pageCount())
    );
    w.println(
      "#--------------------------------"
    );

    for (final var item : items.items()) {
      w.printf("%s : %s%n", item.id().displayId(), item.name());
    }
  }
}
