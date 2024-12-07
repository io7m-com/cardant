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
import com.io7m.cardant.model.CAAuditEvent;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemSerial;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CAPage;
import com.io7m.cardant.model.CAStockOccurrenceSerial;
import com.io7m.cardant.model.CAStockOccurrenceSet;
import com.io7m.cardant.model.CAStockOccurrenceType;
import com.io7m.cardant.model.CATypeRecord;
import com.io7m.cardant.model.CATypeRecordSummary;
import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.cardant.model.type_package.CATypePackageSummary;
import com.io7m.medrina.api.MRoleName;
import org.apache.commons.io.FileUtils;
import org.jline.terminal.Terminal;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
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

    w.printf("# Item %s%n", item.id().displayId());
    w.printf("#-----------------------------------------%n");
    w.println();
    paddedTable(w, main);

    final var metadata = item.metadata();
    if (!metadata.isEmpty()) {
      w.println();
      w.println("# metadata");
      w.println("#---------");
      w.println();

      paddedTable(
        w,
        new TreeMap<>(
          metadata.entrySet()
            .stream()
            .map(e -> Map.entry(
              "metadata: " + e.getKey(),
              e.getValue().valueString()))
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

  @Override
  public void formatTypesScalar(
    final Set<CATypeScalarType> types)
  {
    if (types.isEmpty()) {
      return;
    }

    final var typesSorted = new ArrayList<>(types);
    Collections.sort(typesSorted, Comparator.comparing(CATypeScalarType::name));

    final PrintWriter w = this.terminal.writer();
    for (final var type : typesSorted) {
      w.printf("%s: %s%n", type.name().toString(), type.description());
    }
  }

  @Override
  public void formatTypesScalarPage(
    final CAPage<CATypeScalarType> types)
  {
    final PrintWriter w = this.terminal.writer();
    w.printf(
      "# Search results: Page %d of %d%n",
      Integer.valueOf(types.pageIndex()),
      Integer.valueOf(types.pageCount())
    );
    w.println(
      "#--------------------------------"
    );

    for (final var item : types.items()) {
      w.printf("%s : %s%n", item.name().toString(), item.description());
    }
  }

  @Override
  public void formatTypeDeclaration(
    final CATypeRecord type)
  {
    final PrintWriter w = this.terminal.writer();
    final var main = new TreeMap<String, String>();
    main.put("Name", type.name().toString());
    main.put("Description", type.description());

    w.printf("# Type %s%n", type.name());
    w.printf("#-----------------------------------------%n");
    w.println();
    paddedTable(w, main);

    final var fields = type.fields();
    if (!fields.isEmpty()) {
      w.println();
      w.println("# Fields");
      w.println("#---------");
      w.println();

      paddedTable(
        w,
        new TreeMap<>(
          fields.entrySet()
            .stream()
            .map(e -> Map.entry(e.getKey(), e.getValue().name().toString()))
            .collect(Collectors.toMap(
              e -> e.getKey().toString(),
              Map.Entry::getValue))
        ));
    }

    w.println();
  }

  @Override
  public void formatTypeDeclarationPage(
    final CAPage<CATypeRecordSummary> types)
  {
    final PrintWriter w = this.terminal.writer();
    w.printf(
      "# Search results: Page %d of %d%n",
      Integer.valueOf(types.pageIndex()),
      Integer.valueOf(types.pageCount())
    );
    w.println(
      "#--------------------------------"
    );

    for (final var item : types.items()) {
      w.printf("%s : %s%n", item.name().toString(), item.description());
    }
  }

  @Override
  public void formatLocation(
    final CALocation location)
  {
    final PrintWriter w = this.terminal.writer();
    final var main = new TreeMap<String, String>();
    main.put("Location ID", location.id().displayId());
    main.put("Path", location.path().toString());
    main.put("Name", location.name().value());

    w.printf("# Item %s%n", location.id().displayId());
    w.printf("#-----------------------------------------%n");
    w.println();
    paddedTable(w, main);

    final var metadata = location.metadata();
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
              e.getValue().valueString()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        ));
    }

    final var attachments = location.attachments();
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

    w.println();
  }

  @Override
  public void formatAuditPage(
    final CAPage<CAAuditEvent> page)
  {
    final PrintWriter w = this.terminal.writer();
    w.printf(
      "# Search results: Page %d of %d%n",
      Integer.valueOf(page.pageIndex()),
      Integer.valueOf(page.pageCount())
    );
    w.println(
      "#--------------------------------"
    );

    final var events = page.items();
    for (final var event : events) {
      w.printf(
        "%s %s %s %s %s%n",
        Long.toUnsignedString(event.id()),
        event.owner().id(),
        event.type(),
        event.time(),
        event.data()
      );
    }
  }

  @Override
  public void print(
    final String text)
  {
    final PrintWriter w = this.terminal.writer();
    w.print(text);
  }

  @Override
  public void printLine(
    final String text)
  {
    final PrintWriter w = this.terminal.writer();
    w.println(text);
  }

  @Override
  public void formatTypePackagePage(
    final CAPage<CATypePackageSummary> page)
  {
    final PrintWriter w = this.terminal.writer();
    w.printf(
      "# Search results: Page %d of %d%n",
      Integer.valueOf(page.pageIndex()),
      Integer.valueOf(page.pageCount())
    );
    w.println(
      "#--------------------------------"
    );

    final var items = page.items();
    for (final var item : items) {
      w.printf(
        "%s %s %s%n",
        item.identifier().name(),
        item.identifier().version(),
        item.description()
      );
    }
  }

  @Override
  public void formatStringSet(
    final SortedSet<String> set)
  {
    final PrintWriter w = this.terminal.writer();
    for (final var s : set) {
      w.printf("%s%n", s);
    }
  }

  @Override
  public void formatStockPage(
    final CAPage<CAStockOccurrenceType> page)
    throws Exception
  {
    final PrintWriter w = this.terminal.writer();
    w.printf(
      "# Search results: Page %d of %d%n",
      Integer.valueOf(page.pageIndex()),
      Integer.valueOf(page.pageCount())
    );
    w.println(
      "#--------------------------------"
    );

    final var items = page.items();
    for (final var item : items) {
      formatStockOccurrence(item, w);
    }
  }

  @Override
  public void formatStock(final CAStockOccurrenceType item)
    throws Exception
  {
    formatStockOccurrence(item, this.terminal.writer());
  }

  private static void formatStockOccurrence(
    final CAStockOccurrenceType item,
    final PrintWriter w)
  {
    switch (item) {
      case final CAStockOccurrenceSerial serial -> {
        w.printf(
          "%s %s \"%s\" (Serials %s)%n",
          serial.location().id(),
          serial.item().id(),
          serial.item().name(),
          serial.serials()
            .stream()
            .map(CAItemSerial::toString)
            .collect(Collectors.joining(", "))
        );
      }
      case final CAStockOccurrenceSet set -> {
        w.printf(
          "%s %s \"%s\" (Count %s)%n",
          set.location().id(),
          set.item().id(),
          set.item().name(),
          Long.toUnsignedString(set.count())
        );
      }
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
