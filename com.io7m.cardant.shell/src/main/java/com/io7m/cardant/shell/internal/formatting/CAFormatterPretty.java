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
import com.io7m.cardant.client.preferences.api.CAPreferenceServerCredentialsType;
import com.io7m.cardant.client.preferences.api.CAPreferenceServerUsernamePassword;
import com.io7m.cardant.model.CAAttachment;
import com.io7m.cardant.model.CAAttachmentKey;
import com.io7m.cardant.model.CAAuditEvent;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAIdType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CAPage;
import com.io7m.cardant.model.CATypeDeclaration;
import com.io7m.cardant.model.CATypeDeclarationSummary;
import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.lanark.core.RDottedName;
import com.io7m.medrina.api.MRoleName;
import com.io7m.tabla.core.TColumnWidthConstraint;
import com.io7m.tabla.core.TColumnWidthConstraintMaximumAtMost;
import com.io7m.tabla.core.TColumnWidthConstraintMinimumAny;
import com.io7m.tabla.core.TException;
import com.io7m.tabla.core.TTableRendererType;
import com.io7m.tabla.core.TTableType;
import com.io7m.tabla.core.TTableWidthConstraintRange;
import com.io7m.tabla.core.Tabla;
import org.jline.terminal.Terminal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;

import static com.io7m.cardant.shell.internal.formatting.CAFormatterRaw.formatSize;
import static com.io7m.tabla.core.TColumnWidthConstraint.atLeastContent;
import static com.io7m.tabla.core.TColumnWidthConstraint.atLeastContentOrHeader;
import static com.io7m.tabla.core.TConstraintHardness.SOFT_CONSTRAINT;
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

  private static String userOf(
    final CAPreferenceServerCredentialsType credentials)
  {
    if (credentials instanceof final CAPreferenceServerUsernamePassword c) {
      return c.username();
    }
    throw new IllegalStateException();
  }

  @Override
  public void formatFile(
    final CAFileType file)
    throws TException
  {
    this.formatFileAttributes(file);
  }

  private int width()
  {
    var width = Math.max(0, this.terminal.getWidth());
    if (width == 0) {
      width = 80;
    }
    return width;
  }

  private int widthFor(
    final int columns)
  {
    final var columnPad = 2;
    final var columnEdge = 1;
    return this.width() - (2 + (columns * (columnEdge + columnPad)));
  }

  private void formatFileAttributes(
    final CAFileType file)
    throws TException
  {
    final var tableBuilder =
      Tabla.builder()
        .setWidthConstraint(this.softTableWidth(2))
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

  private TTableWidthConstraintRange softTableWidth(
    final int columns)
  {
    return tableWidthExact(this.widthFor(columns), SOFT_CONSTRAINT);
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
    this.terminal.writer()
      .printf(
        "Search results: Page %d of %d%n",
        Integer.valueOf(files.pageIndex()),
        Integer.valueOf(files.pageCount())
      );

    final var tableBuilder =
      Tabla.builder()
        .setWidthConstraint(this.softTableWidth(2))
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
    this.formatItemAttributes(item);
    this.formatMetadata(item.metadata());
    this.formatAttachments(item.attachments());
  }

  private void formatMetadata(
    final SortedMap<RDottedName, CAMetadataType> metadata)
    throws TException
  {
    if (!metadata.isEmpty()) {
      final var writer = this.terminal.writer();
      writer.println(" metadata");

      final var tableBuilder =
        Tabla.builder()
          .setWidthConstraint(this.softTableWidth(2))
          .declareColumn("Name", atLeastContentOrHeader())
          .declareColumn("Value", atLeastContentOrHeader());

      for (final var entry : metadata.entrySet()) {
        tableBuilder.addRow()
          .addCell(entry.getKey().value())
          .addCell(entry.getValue().valueString());
      }

      this.renderTable(tableBuilder.build());
    }
  }

  private void formatAttachments(
    final SortedMap<CAAttachmentKey, CAAttachment> attachments)
    throws TException
  {
    if (!attachments.isEmpty()) {
      final var writer = this.terminal.writer();
      writer.println(" Attachments");

      final var tableBuilder =
        Tabla.builder()
          .setWidthConstraint(this.softTableWidth(2))
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
    final CAItem item)
    throws TException
  {
    final var tableBuilder =
      Tabla.builder()
        .setWidthConstraint(this.softTableWidth(2))
        .declareColumn("Attribute", ITEM_ATTRIBUTE_CONSTRAINT)
        .declareColumn("Value", atLeastContent());

    tableBuilder.addRow()
      .addCell("Item ID")
      .addCell(item.id().displayId());

    tableBuilder.addRow()
      .addCell("Name")
      .addCell(item.name());

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
    this.terminal.writer()
      .printf(
        "Search results: Page %d of %d%n",
        Integer.valueOf(items.pageIndex()),
        Integer.valueOf(items.pageCount())
      );

    final var tableBuilder =
      Tabla.builder()
        .setWidthConstraint(this.softTableWidth(2))
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
  public void formatBookmarks(
    final List<CAPreferenceServerBookmark> bookmarks)
    throws Exception
  {
    if (bookmarks.isEmpty()) {
      return;
    }

    final var tableBuilder =
      Tabla.builder()
        .setWidthConstraint(this.softTableWidth(5))
        .declareColumn("Name", atLeastContentOrHeader())
        .declareColumn("Host", atLeastContentOrHeader())
        .declareColumn("Port", atLeastContentOrHeader())
        .declareColumn("TLS", atLeastContentOrHeader())
        .declareColumn("User", atLeastContentOrHeader());

    for (final var bookmark : bookmarks) {
      tableBuilder.addRow()
        .addCell(bookmark.name())
        .addCell(bookmark.host())
        .addCell(Integer.toString(bookmark.port()))
        .addCell(Boolean.toString(bookmark.isHTTPs()))
        .addCell(userOf(bookmark.credentials()));
    }

    this.renderTable(tableBuilder.build());
  }

  @Override
  public void formatRoles(
    final Set<MRoleName> roles)
    throws Exception
  {
    if (roles.isEmpty()) {
      return;
    }

    final var roleSorted = new ArrayList<>(roles);
    Collections.sort(roleSorted);

    final var tableBuilder =
      Tabla.builder()
        .setWidthConstraint(this.softTableWidth(1))
        .declareColumn("Role", atLeastContentOrHeader());

    for (final var role : roleSorted) {
      tableBuilder.addRow().addCell(role.value().value());
    }

    this.renderTable(tableBuilder.build());
  }

  @Override
  public void formatTypesScalar(
    final Set<CATypeScalarType> types)
    throws Exception
  {
    if (types.isEmpty()) {
      return;
    }

    final var typesSorted = new ArrayList<>(types);
    Collections.sort(typesSorted, Comparator.comparing(CATypeScalarType::name));
    this.formatTypesScalarCollection(typesSorted);
  }

  private void formatTypesScalarCollection(
    final Collection<CATypeScalarType> types)
    throws TException
  {
    if (types.isEmpty()) {
      return;
    }

    final var tableBuilder =
      Tabla.builder()
        .setWidthConstraint(this.softTableWidth(4))
        .declareColumn("Type", atLeastContentOrHeader())
        .declareColumn("Description", atLeastContent())
        .declareColumn("Base", atLeastContentOrHeader())
        .declareColumn("Constraint", atLeastContentOrHeader());

    for (final var type : types) {
      tableBuilder.addRow()
        .addCell(type.name().value())
        .addCell(type.description())
        .addCell(type.kind().name())
        .addCell(type.showConstraint());
    }

    this.renderTable(tableBuilder.build());
  }

  @Override
  public void formatTypesScalarPage(
    final CAPage<CATypeScalarType> types)
    throws Exception
  {
    this.terminal.writer()
      .printf(
        "Search results: Page %d of %d%n",
        Integer.valueOf(types.pageIndex()),
        Integer.valueOf(types.pageCount())
      );

    this.formatTypesScalarCollection(types.items());
  }

  @Override
  public void formatTypeDeclaration(
    final CATypeDeclaration type)
    throws Exception
  {
    {
      final var tableBuilder =
        Tabla.builder()
          .setWidthConstraint(this.softTableWidth(2))
          .declareColumn("Type", atLeastContentOrHeader())
          .declareColumn("Description", atLeastContent());

      tableBuilder.addRow()
        .addCell(type.name().value())
        .addCell(type.description());

      this.renderTable(tableBuilder.build());
    }

    if (!type.fields().isEmpty()) {
      this.terminal.writer()
        .println(" Fields");

      final var tableBuilder =
        Tabla.builder()
          .setWidthConstraint(this.softTableWidth(3))
          .declareColumn("Name", atLeastContentOrHeader())
          .declareColumn("Description", atLeastContent())
          .declareColumn("Type", atLeastContentOrHeader());

      final var fieldsSorted = new ArrayList<>(type.fields().values());
      Collections.sort(fieldsSorted, Comparator.comparing(CATypeField::name));

      for (final var field : fieldsSorted) {
        tableBuilder.addRow()
          .addCell(field.name().value())
          .addCell(field.description())
          .addCell(field.type().name().value());
      }

      this.renderTable(tableBuilder.build());
    }
  }

  @Override
  public void formatTypeDeclarationPage(
    final CAPage<CATypeDeclarationSummary> types)
    throws Exception
  {
    this.terminal.writer()
      .printf(
        "Search results: Page %d of %d%n",
        Integer.valueOf(types.pageIndex()),
        Integer.valueOf(types.pageCount())
      );

    if (types.items().isEmpty()) {
      return;
    }

    final var tableBuilder =
      Tabla.builder()
        .setWidthConstraint(this.softTableWidth(2))
        .declareColumn("Type", atLeastContentOrHeader())
        .declareColumn("Description", atLeastContent());

    for (final var type : types.items()) {
      tableBuilder.addRow()
        .addCell(type.name().value())
        .addCell(type.description());
    }

    this.renderTable(tableBuilder.build());
  }

  @Override
  public void formatLocation(
    final CALocation location)
    throws Exception
  {
    this.formatLocationAttributes(location);
    this.formatMetadata(location.metadata());
    this.formatAttachments(location.attachments());
  }

  @Override
  public void formatAuditPage(
    final CAPage<CAAuditEvent> page)
    throws Exception
  {
    this.terminal.writer()
      .printf(
        "Search results: Page %d of %d%n",
        Integer.valueOf(page.pageIndex()),
        Integer.valueOf(page.pageCount())
      );

    if (page.items().isEmpty()) {
      return;
    }

    final var tableBuilder =
      Tabla.builder()
        .setWidthConstraint(this.softTableWidth(2))
        .declareColumn("ID", atLeastContentOrHeader())
        .declareColumn("Owner", atLeastContent())
        .declareColumn("Type", atLeastContent())
        .declareColumn("Time", atLeastContent())
        .declareColumn("Data", atLeastContent());

    for (final var event : page.items()) {
      tableBuilder.addRow()
        .addCell(Long.toUnsignedString(event.id()))
        .addCell(event.owner().toString())
        .addCell(event.type())
        .addCell(event.time().toString())
        .addCell(event.data().toString());
    }

    this.renderTable(tableBuilder.build());
  }

  private void formatLocationAttributes(
    final CALocation location)
    throws TException
  {
    final var tableBuilder =
      Tabla.builder()
        .setWidthConstraint(this.softTableWidth(2))
        .declareColumn("Attribute", ITEM_ATTRIBUTE_CONSTRAINT)
        .declareColumn("Value", atLeastContent());

    tableBuilder.addRow()
      .addCell("Location ID")
      .addCell(location.id().displayId());

    tableBuilder.addRow()
      .addCell("Name")
      .addCell(location.name());

    tableBuilder.addRow()
      .addCell("Parent")
      .addCell(location.parent().map(CAIdType::displayId).orElse("None"));

    this.renderTable(tableBuilder.build());

  }

  @Override
  public String toString()
  {
    return "[%s]".formatted(this.getClass().getSimpleName());
  }
}
