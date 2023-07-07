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


package com.io7m.cardant.shell.internal.formatting;

import com.io7m.cardant.client.preferences.api.CAPreferenceServerBookmark;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CAPage;
import com.io7m.cardant.model.CATag;
import com.io7m.medrina.api.MRoleName;
import org.apache.commons.io.FileUtils;
import org.jline.terminal.Terminal;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.lang.Math.max;

/**
 * A raw formatter.
 */

public final class CAFormatterRaw implements CAFormatterType
{
  private final Terminal terminal;

  /**
   * A raw formatter.
   *
   * @param inTerminal The terminal
   */

  public CAFormatterRaw(
    final Terminal inTerminal)
  {
    this.terminal =
      Objects.requireNonNull(inTerminal, "terminal");
  }

  @Override
  public void formatFile(
    final CAFileType file)
  {
    final PrintWriter w = this.terminal.writer();
    final var main = new TreeMap<String, String>();
    main.put("File ID", file.id().displayId());
    main.put("Description", file.description());
    main.put("Content Type", file.mediaType());
    main.put("Hash Algorithm", file.hashAlgorithm());
    main.put("Hash Value", file.hashValue());
    main.put("Size", formatSize(file));

    w.printf("# File %s%n", file.id().displayId());
    w.printf("#-----------------------------------------%n");
    w.println();
    paddedTable(w, main);

    w.println();
  }

  @Override
  public void formatFilesPage(
    final CAPage<CAFileType.CAFileWithoutData> files)
  {
    final PrintWriter w = this.terminal.writer();
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

  @Override
  public void formatItem(
    final CAItem item)
  {
    final PrintWriter w = this.terminal.writer();
    final var main = new TreeMap<String, String>();
    main.put("Item ID", item.id().displayId());
    main.put("Name", item.name());
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
      w.println("# ID ");

      for (final var entry : attachments.entrySet()) {
        final var itemKey = entry.getKey();
        final var itemValue = entry.getValue();
        w.printf(
          "%s %s %s %s%n",
          itemKey.fileID().displayId(),
          itemValue.relation(),
          itemValue.file().mediaType(),
          itemValue.file().description()
        );
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

  @Override
  public void formatItemsPage(
    final CAPage<CAItemSummary> items)
  {
    final PrintWriter w = this.terminal.writer();
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

  @Override
  public void formatBookmarks(
    final List<CAPreferenceServerBookmark> bookmarks)
  {
    final PrintWriter w = this.terminal.writer();

    for (final var bookmark : bookmarks) {
      w.printf(
        "%-32s %s:%s%n",
        bookmark.name(),
        bookmark.host(),
        Integer.valueOf(bookmark.port())
      );
    }
  }

  @Override
  public void formatRoles(
    final Set<MRoleName> roles)
  {
    if (roles.isEmpty()) {
      return;
    }

    final var roleSorted = new ArrayList<>(roles);
    Collections.sort(roleSorted);

    final PrintWriter w = this.terminal.writer();
    for (final var role : roles) {
      w.printf("Role: %s%n", role.value().value());
    }
  }

  static String formatSize(
    final CAFileType file)
  {
    return String.format(
      "%s (%s octets)",
      FileUtils.byteCountToDisplaySize(file.size()),
      Long.toUnsignedString(file.size())
    );
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

  @Override
  public String toString()
  {
    return "[%s]".formatted(this.getClass().getSimpleName());
  }
}
