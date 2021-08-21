/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.gui.internal;

import com.io7m.cardant.model.CAItemMetadata;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Comparator;
import java.util.function.Consumer;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

public final class CAItemMetadataTables
{
  private CAItemMetadataTables()
  {

  }

  public static void configure(
    final CAMainStrings strings,
    final CAItemMetadataList itemList,
    final TableView<CAItemMetadata> tableView,
    final Consumer<CAItemMetadata> onWantEditMetadataValue)
  {
    tableView.setPlaceholder(
      new Label(strings.format("items.metadata.noValues")));

    final var tableColumns =
      tableView.getColumns();
    final var tableNameColumn =
      (TableColumn<CAItemMetadata, CAItemMetadata>) tableColumns.get(0);
    final var tableValueColumn =
      (TableColumn<CAItemMetadata, CAItemMetadata>) tableColumns.get(1);

    tableNameColumn.setSortable(true);
    tableNameColumn.setReorderable(false);
    tableNameColumn.setComparator(Comparator.comparing(CAItemMetadata::name));
    tableNameColumn.setCellFactory(column -> new CAItemMetadataNameCell());

    tableValueColumn.setSortable(true);
    tableValueColumn.setReorderable(false);
    tableValueColumn.setComparator(Comparator.comparing(CAItemMetadata::value));
    tableValueColumn.setCellFactory(
      column -> new CAItemMetadataValueCell(strings, onWantEditMetadataValue));

    tableNameColumn.setCellValueFactory(
      param -> new ReadOnlyObjectWrapper<>(param.getValue()));
    tableValueColumn.setCellValueFactory(
      param -> new ReadOnlyObjectWrapper<>(param.getValue()));

    tableView.setItems(itemList.items());
    itemList.comparator().bind(tableView.comparatorProperty());

    tableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
    tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
  }
}
