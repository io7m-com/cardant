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

import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CAPage;
import com.io7m.tabla.core.TColumnWidthConstraint;
import com.io7m.tabla.core.TColumnWidthConstraintMaximumAtMost;
import com.io7m.tabla.core.TColumnWidthConstraintMinimumAny;
import com.io7m.tabla.core.TException;
import com.io7m.tabla.core.TTableRendererType;
import com.io7m.tabla.core.TTableType;
import com.io7m.tabla.core.Tabla;
import org.jline.terminal.Terminal;

import java.util.Objects;

import static com.io7m.cardant.shell.internal.formatting.CAFormatterRaw.formatSize;
import static com.io7m.tabla.core.TColumnWidthConstraint.atLeastContent;
import static com.io7m.tabla.core.TColumnWidthConstraint.atLeastContentOrHeader;
import static com.io7m.tabla.core.TTableWidthConstraintType.tableWidthExact;

/**
 * A pretty formatter.
 */

public final class CAFormatterPretty implements CAFormatterType
{
  private static final TColumnWidthConstraint UUID_CONSTRAINT =
    TColumnWidthConstraint.exactWidth(36);
  private static final TColumnWidthConstraint ITEM_ATTRIBUTE_CONSTRAINT =
    new TColumnWidthConstraint(
      TColumnWidthConstraintMinimumAny.any(),
      TColumnWidthConstraintMaximumAtMost.atMost(13)
    );
  private static final TColumnWidthConstraint ITEM_METADATA_CONSTRAINT =
    new TColumnWidthConstraint(
      TColumnWidthConstraintMinimumAny.any(),
      TColumnWidthConstraintMaximumAtMost.atMost(6)
    );
  private static final TColumnWidthConstraint FILE_ATTRIBUTE_CONSTRAINT =
    new TColumnWidthConstraint(
      TColumnWidthConstraintMinimumAny.any(),
      TColumnWidthConstraintMaximumAtMost.atMost(16)
    );

  private final Terminal terminal;
  private final TTableRendererType tableRenderer;

  /**
   * A pretty formatter.
   *
   * @param inTerminal The terminal
   */

  public CAFormatterPretty(
    final Terminal inTerminal)
  {
    this.terminal =
      Objects.requireNonNull(inTerminal, "terminal");
    this.tableRenderer =
      Tabla.framedUnicodeRenderer();
  }

  @Override
  public void formatFile(
    final CAFileType file)
    throws TException
  {
    final int width = this.getWidth();
    this.formatFileAttributes(file, width);
  }

  private int getWidth()
  {
    var width = Math.max(0, this.terminal.getWidth() - 8);
    if (width == 0) {
      width = 100;
    }
    return width;
  }

  private void formatFileAttributes(
    final CAFileType file,
    final int width)
    throws TException
  {
    final var tableBuilder =
      Tabla.builder()
        .setWidthConstraint(tableWidthExact(width))
        .declareColumn("Attribute", FILE_ATTRIBUTE_CONSTRAINT)
        .declareColumn("Value", atLeastContent());

    tableBuilder.addRow()
      .addCell("File ID")
      .addCell(file.id().displayId());

    tableBuilder.addRow()
      .addCell("Description")
      .addCell(file.description());

    tableBuilder.addRow()
      .addCell("Content Type")
      .addCell(file.mediaType());

    tableBuilder.addRow()
      .addCell("Hash Algorithm")
      .addCell(file.hashAlgorithm());

    tableBuilder.addRow()
      .addCell("Hash Value")
      .addCell(file.hashValue());

    tableBuilder.addRow()
      .addCell("Size")
      .addCell(formatSize(file));

    this.renderTable(tableBuilder.build());
  }

  private void renderTable(
    final TTableType table)
  {
    final var lines =
      this.tableRenderer.renderLines(table);

    final var writer = this.terminal.writer();
    for (final var line : lines) {
      writer.println(line);
    }
  }

  @Override
  public void formatFilesPage(
    final CAPage<CAFileType.CAFileWithoutData> files)
    throws TException
  {
    final var width = this.getWidth();

    this.terminal.writer()
      .printf(
        "Search results: Page %d of %d%n",
        Integer.valueOf(files.pageIndex()),
        Integer.valueOf(files.pageCount())
      );

    final var tableBuilder =
      Tabla.builder()
        .setWidthConstraint(tableWidthExact(width))
        .declareColumn("File ID", UUID_CONSTRAINT)
        .declareColumn("Description", atLeastContent());

    for (final var file : files.items()) {
      tableBuilder.addRow()
        .addCell(file.id().displayId())
        .addCell(file.description());
    }

    this.renderTable(tableBuilder.build());
  }

  @Override
  public void formatItem(
    final CAItem item)
    throws TException
  {
    final var width = this.getWidth();
    this.formatItemAttributes(item, width);
    this.formatItemMetadata(item, width);
    this.formatItemAttachments(item, width);
  }

  private void formatItemMetadata(
    final CAItem item,
    final int width)
    throws TException
  {
    final var metadata =
      item.metadata();

    if (!metadata.isEmpty()) {
      final var writer = this.terminal.writer();
      writer.println(" Metadata");

      final var tableBuilder =
        Tabla.builder()
          .setWidthConstraint(tableWidthExact(width))
          .declareColumn("Name", atLeastContentOrHeader())
          .declareColumn("Value", atLeastContentOrHeader());

      for (final var entry : metadata.entrySet()) {
        tableBuilder.addRow()
          .addCell(entry.getKey())
          .addCell(entry.getValue().value());
      }

      this.renderTable(tableBuilder.build());
    }
  }

  private void formatItemAttachments(
    final CAItem item,
    final int width)
    throws TException
  {
    final var attachments =
      item.attachments();

    if (!attachments.isEmpty()) {
      final var writer = this.terminal.writer();
      writer.println(" Attachments");

      final var tableBuilder =
        Tabla.builder()
          .setWidthConstraint(tableWidthExact(width))
          .declareColumn("File ID", UUID_CONSTRAINT)
          .declareColumn("Relation", atLeastContentOrHeader());

      for (final var entry : attachments.entrySet()) {
        tableBuilder.addRow()
          .addCell(entry.getValue().file().id().displayId())
          .addCell(entry.getValue().relation());
      }

      this.renderTable(tableBuilder.build());
    }
  }

  private void formatItemAttributes(
    final CAItem item,
    final int width)
    throws TException
  {
    final var tableBuilder =
      Tabla.builder()
        .setWidthConstraint(tableWidthExact(width))
        .declareColumn("Attribute", ITEM_ATTRIBUTE_CONSTRAINT)
        .declareColumn("Value", atLeastContent());

    tableBuilder.addRow()
      .addCell("Item ID")
      .addCell(item.id().displayId());

    tableBuilder.addRow()
      .addCell("Name")
      .addCell(item.name());

    tableBuilder.addRow()
      .addCell("Description")
      .addCell(item.descriptionOrEmpty());

    tableBuilder.addRow()
      .addCell("Count (Total)")
      .addCell(Long.toUnsignedString(item.countTotal()));

    tableBuilder.addRow()
      .addCell("Count (Here)")
      .addCell(Long.toUnsignedString(item.countHere()));

    this.renderTable(tableBuilder.build());
  }

  @Override
  public void formatItemsPage(
    final CAPage<CAItemSummary> items)
    throws TException
  {
    final var width = this.getWidth();

    this.terminal.writer()
      .printf(
        "Search results: Page %d of %d%n",
        Integer.valueOf(items.pageIndex()),
        Integer.valueOf(items.pageCount())
      );

    final var tableBuilder =
      Tabla.builder()
        .setWidthConstraint(tableWidthExact(width))
        .declareColumn("Item ID", UUID_CONSTRAINT)
        .declareColumn("Description", atLeastContentOrHeader());

    for (final var item : items.items()) {
      tableBuilder.addRow()
        .addCell(item.id().displayId())
        .addCell(item.name());
    }

    this.renderTable(tableBuilder.build());
  }

  @Override
  public String toString()
  {
    return "[%s]".formatted(this.getClass().getSimpleName());
  }
}
