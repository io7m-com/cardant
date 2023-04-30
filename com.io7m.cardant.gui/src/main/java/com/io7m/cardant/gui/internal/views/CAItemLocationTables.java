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
import com.io7m.cardant.gui.internal.model.CAItemLocationMutable;
import com.io7m.cardant.gui.internal.model.CATableMap;
import com.io7m.cardant.model.CALocationID;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN;

public final class CAItemLocationTables
{
  private CAItemLocationTables()
  {

  }

  public static void configure(
    final CAMainStrings strings,
    final TableView<CAItemLocationMutable> tableView)
  {
    tableView.setPlaceholder(
      new Label(strings.format("items.metadata.noValues")));

    final var tableColumns =
      tableView.getColumns();
    final var tableLocationColumn =
      (TableColumn<CAItemLocationMutable, String>) tableColumns.get(0);
    final var tableCountColumn =
      (TableColumn<CAItemLocationMutable, Long>) tableColumns.get(1);

    tableLocationColumn.setSortable(true);
    tableLocationColumn.setReorderable(false);
    tableLocationColumn.setComparator(String::compareToIgnoreCase);
    tableLocationColumn.setCellFactory(
      column -> new CAItemLocationTableLocationCell());
    tableLocationColumn.setCellValueFactory(
      param -> param.getValue().locationName());

    tableCountColumn.setSortable(true);
    tableCountColumn.setReorderable(false);
    tableCountColumn.setComparator(Long::compareUnsigned);
    tableCountColumn.setCellFactory(
      column -> new CAItemLocationTableCountCell());
    tableCountColumn.setCellValueFactory(
      param -> param.getValue().count().asObject()
    );

    tableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
  }

  public static void bind(
    final CATableMap<CALocationID, CAItemLocationMutable> tableData,
    final TableView<CAItemLocationMutable> tableView)
  {
    final var readable = tableData.readable();
    tableView.setItems(tableData.readable());
    readable.comparatorProperty().bind(tableView.comparatorProperty());
  }
}
