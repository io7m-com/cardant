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

package com.io7m.cardant.gui.internal.model;

import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CALocationID;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.scene.control.TreeItem;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class CALocationTreeFiltered
{
  private final CALocationTree tree;
  private final Optional<ItemFiltering> itemFiltering;
  private final TreeItem<CALocationItemType> root;
  private Optional<String> filter;

  private CALocationTreeFiltered(
    final CALocationTree inTree,
    final Optional<ItemFiltering> inItemFiltering)
  {
    this.tree =
      Objects.requireNonNull(inTree, "tree");
    this.itemFiltering =
      Objects.requireNonNull(inItemFiltering, "inItemFiltering");
    this.root =
      new TreeItem<>(CALocationItemAll.get());

    this.filter = Optional.empty();
    this.root.setExpanded(true);
    this.rebuild();
  }

  public static CALocationTreeFiltered filter(
    final CALocationTree tree)
  {
    final var filtered =
      new CALocationTreeFiltered(tree, Optional.empty());

    filtered.tree.editsProperty()
      .addListener((observable, oldValue, newValue) -> {
        filtered.rebuild();
      });

    return filtered;
  }

  public static CALocationTreeFiltered filterWithItemCounts(
    final ObservableMap<CAItemAndLocation, Long> itemLocations,
    final CAItemID itemID,
    final CALocationTree tree)
  {
    final var filtered =
      new CALocationTreeFiltered(
        tree, Optional.of(new ItemFiltering(itemLocations, itemID)));

    filtered.tree.editsProperty()
      .addListener((observable, oldValue, newValue) -> {
        filtered.rebuild();
      });

    filtered.itemFiltering.ifPresent(parameters -> {
      parameters.itemLocations.addListener((MapChangeListener<? super CAItemAndLocation, ? super Long>) change -> {
        filtered.rebuild();
      });
    });

    return filtered;
  }

  private static SimpleStringProperty produceDecoratedProperty(
    final CALocationItemDefined location,
    final ItemFiltering itemFilter)
  {
    final var count = itemFilter.count(location.id());
    return new SimpleStringProperty(
      new StringBuilder(64)
        .append(location.name().getValueSafe())
        .append(" (")
        .append(Long.toUnsignedString(count.longValue()))
        .append(")")
        .toString()
    );
  }

  private static TreeItem<CALocationItemType> soften(
    final TreeItem<CALocationItemDefined> item)
  {
    return (TreeItem<CALocationItemType>) (Object) item;
  }

  public void setFilter(
    final Optional<String> newFilter)
  {
    this.filter = Objects.requireNonNull(newFilter, "newFilter");
    this.rebuild();
  }

  public TreeItem<CALocationItemType> root()
  {
    return this.root;
  }

  public void setFilterChecked(
    final String searchTerm)
  {
    Objects.requireNonNull(searchTerm, "searchTerm");

    final var text = searchTerm.trim().toUpperCase(Locale.ROOT);
    if (text.isEmpty()) {
      this.setFilter(Optional.empty());
    } else {
      this.setFilter(Optional.of(text));
    }
  }

  private void rebuild()
  {
    if (this.filter.isPresent()) {
      this.rebuildFiltered(this.filter.get());
    } else {
      this.rebuildUnfiltered();
    }
  }

  private TreeItem<CALocationItemType> deepCopyTree(
    final TreeItem<CALocationItemType> item)
  {
    final var copy = new TreeItem<>(item.getValue());
    copy.setExpanded(item.isExpanded());

    final var copyValue = copy.getValue();
    if (copyValue instanceof CALocationItemDefined defined) {
      this.itemFiltering.ifPresent(itemFilter -> {
        copy.setValue(
          new CALocationItemDefined(
            defined.id(),
            defined.parent(),
            produceDecoratedProperty(defined, itemFilter),
            defined.undecoratedName(),
            defined.description()
          )
        );
      });
    }

    for (final var child : item.getChildren()) {
      copy.getChildren().add(this.deepCopyTree(child));
    }
    return copy;
  }

  private void rebuildUnfiltered()
  {
    final var newRoot = this.deepCopyTree(this.tree.root());
    final var rootChildren = this.root.getChildren();
    rootChildren.clear();
    rootChildren.addAll(newRoot.getChildren());
  }

  private void rebuildFiltered(
    final String filterText)
  {
    final var locations =
      this.tree.locations();
    final var expanded =
      this.tree.expanded();
    final var treeItems =
      new HashMap<CALocationID, TreeItem<CALocationItemDefined>>(locations.size());

    for (final var location : locations.values()) {
      if (location.matches(filterText)) {
        final var item = new TreeItem<>(location);
        treeItems.put(location.id(), item);
        item.setExpanded(expanded.contains(location.id()));

        this.itemFiltering.ifPresent(itemFilter -> {
          final var original = item.getValue();
          item.setValue(
            new CALocationItemDefined(
              original.id(),
              original.parent(),
              produceDecoratedProperty(location, itemFilter),
              original.undecoratedName(),
              original.description()
            )
          );
        });
      }
    }

    final var newEverywhere =
      new TreeItem<CALocationItemType>(CALocationItemAll.get());
    newEverywhere.setExpanded(true);

    final var everywhereChildren =
      newEverywhere.getChildren();
    everywhereChildren.addAll(
      treeItems.values()
        .stream()
        .map(CALocationTreeFiltered::soften)
        .collect(Collectors.toList())
    );

    final var rootChildren = this.root.getChildren();
    rootChildren.clear();
    rootChildren.add(newEverywhere);
  }

  private record ItemFiltering(
    ObservableMap<CAItemAndLocation, Long> itemLocations,
    CAItemID itemID)
  {
    public Long count(
      final CALocationID locationID)
    {
      return this.itemLocations.getOrDefault(
        new CAItemAndLocation(this.itemID, locationID),
        Long.valueOf(0L)
      );
    }
  }
}
