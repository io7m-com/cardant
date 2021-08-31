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

package com.io7m.cardant.gui.internal.views;

import com.io7m.cardant.gui.internal.CAMainStrings;
import com.io7m.cardant.gui.internal.model.CAItemMutable;
import com.io7m.cardant.gui.internal.model.CATableMap;
import com.io7m.cardant.model.CAItemID;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

public final class CAItemMutableTables
{
  private CAItemMutableTables()
  {

  }

  public static void configure(
    final CAMainStrings strings,
    final TableView<CAItemMutable> tableView)
  {
    tableView.setPlaceholder(new Label(strings.format("items.noItems")));

    final var tableColumns =
      tableView.getColumns();
    final var tableNameColumn =
      (TableColumn<CAItemMutable, String>) tableColumns.get(0);
    final var tableDescriptionColumn =
      (TableColumn<CAItemMutable, String>) tableColumns.get(1);
    final var tableCountColumn =
      (TableColumn<CAItemMutable, Long>) tableColumns.get(2);

    tableNameColumn.setSortable(true);
    tableNameColumn.setReorderable(false);
    tableNameColumn.setComparator(String::compareToIgnoreCase);
    tableNameColumn.setCellFactory(
      column -> new CAItemMutableTableNameCell());
    tableNameColumn.setCellValueFactory(
      param -> param.getValue().name());

    tableDescriptionColumn.setSortable(true);
    tableDescriptionColumn.setReorderable(false);
    tableDescriptionColumn.setComparator(String::compareToIgnoreCase);
    tableDescriptionColumn.setCellFactory(
      column -> new CAItemMutableTableDescriptionCell());
    tableDescriptionColumn.setCellValueFactory(
      param -> param.getValue().description()
    );

    tableCountColumn.setSortable(true);
    tableCountColumn.setReorderable(false);
    tableCountColumn.setComparator(Long::compareUnsigned);
    tableCountColumn.setCellFactory(
      column -> new CAItemMutableTableCountCell());
    tableCountColumn.setCellValueFactory(
      param -> param.getValue().count().asObject()
    );

    tableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
    tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
  }

  public static void bind(
    final TableView<CAItemMutable> tableView,
    final CATableMap<CAItemID, CAItemMutable> items)
  {
    final var readable = items.readable();
    tableView.setItems(readable);
    readable.comparatorProperty().bind(tableView.comparatorProperty());
  }
}
