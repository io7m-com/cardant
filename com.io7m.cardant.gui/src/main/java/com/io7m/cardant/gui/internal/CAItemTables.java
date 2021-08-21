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

import com.io7m.cardant.model.CAItem;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Comparator;

import static javafx.scene.control.TableView.CONSTRAINED_RESIZE_POLICY;

public final class CAItemTables
{
  private CAItemTables()
  {

  }

  public static void configure(
    final CAMainStrings strings,
    final CAItemList itemList,
    final TableView<CAItem> tableView)
  {
    tableView.setPlaceholder(new Label(strings.format("items.noItems")));

    final var tableColumns =
      tableView.getColumns();
    final var tableNameColumn =
      (TableColumn<CAItem, CAItem>) tableColumns.get(0);
    final var tableDescriptionColumn =
      (TableColumn<CAItem, CAItem>) tableColumns.get(1);
    final var tableCountColumn =
      (TableColumn<CAItem, CAItem>) tableColumns.get(2);

    tableNameColumn.setSortable(true);
    tableNameColumn.setReorderable(false);
    tableNameColumn.setComparator(Comparator.comparing(CAItem::name));
    tableNameColumn.setCellFactory(column -> new CAItemTableNameCell());

    tableDescriptionColumn.setSortable(true);
    tableDescriptionColumn.setReorderable(false);
    tableDescriptionColumn.setComparator(Comparator.comparing(CAItem::descriptionOrEmpty));
    tableDescriptionColumn.setCellFactory(column -> new CAItemTableDescriptionCell());

    tableCountColumn.setSortable(true);
    tableCountColumn.setReorderable(false);
    tableCountColumn.setComparator(Comparator.comparingLong(CAItem::count));
    tableCountColumn.setCellFactory(column -> new CAItemTableCountCell());

    tableNameColumn.setCellValueFactory(
      param -> new ReadOnlyObjectWrapper<>(param.getValue()));
    tableDescriptionColumn.setCellValueFactory(
      param -> new ReadOnlyObjectWrapper<>(param.getValue()));
    tableCountColumn.setCellValueFactory(
      param -> new ReadOnlyObjectWrapper<>(param.getValue()));

    tableView.setItems(itemList.items());
    itemList.comparator().bind(tableView.comparatorProperty());

    tableView.setColumnResizePolicy(CONSTRAINED_RESIZE_POLICY);
    tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
  }
}
