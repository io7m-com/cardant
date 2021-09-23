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

import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.control.TreeItem;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class CALocationTree
{
  private final HashMap<CALocationID, CALocationItemDefined> locations;
  private final TreeItem<CALocationItemType> root;
  private final HashSet<CALocationID> expanded;
  private final IntegerProperty edits;
  private final Map<CALocationID, CALocationItemDefined> locationsRead;
  private final Set<CALocationID> expandedRead;

  public CALocationTree()
  {
    this.locations =
      new HashMap<>();
    this.root =
      new TreeItem<>(CALocationItemRoot.get());
    this.expanded =
      new HashSet<>();

    this.root.setExpanded(true);

    this.edits =
      new SimpleIntegerProperty();
    this.locationsRead =
      Collections.unmodifiableMap(this.locations);
    this.expandedRead =
      Collections.unmodifiableSet(this.expanded);
  }

  private static TreeItem<CALocationItemType> soften(
    final TreeItem<CALocationItemDefined> item)
  {
    return (TreeItem<CALocationItemType>) (Object) item;
  }

  public Set<CALocationID> expanded()
  {
    return this.expandedRead;
  }

  public Map<CALocationID, CALocationItemDefined> locations()
  {
    return this.locationsRead;
  }

  public ObservableIntegerValue editsProperty()
  {
    return this.edits;
  }

  public void put(
    final CALocation location)
  {
    Objects.requireNonNull(location, "location");

    this.locations.put(location.id(), CALocationItemDefined.of(location));
    this.expanded.add(location.id());
    this.rebuild();
  }

  public void putAll(
    final Collection<CALocation> locations)
  {
    Objects.requireNonNull(locations, "locations");

    for (final var location : locations) {
      this.locations.put(location.id(), CALocationItemDefined.of(location));
      this.expanded.add(location.id());
    }
    this.rebuild();
  }

  public void remove(
    final CALocationID id)
  {
    Objects.requireNonNull(id, "location");

    this.locations.remove(id);
    this.expanded.remove(id);
    this.rebuild();
  }

  public TreeItem<CALocationItemType> root()
  {
    return this.root;
  }

  private void rebuild()
  {
    try {
      final var treeItems =
        new HashMap<CALocationID, TreeItem<CALocationItemDefined>>(
          this.locations.size());

      for (final var location : this.locations.values()) {
        final var item = new TreeItem<>(location);
        treeItems.put(location.id(), item);
        item.setExpanded(this.expanded.contains(location.id()));
        item.expandedProperty()
          .addListener(new WeakChangeListener<>((observable, oldValue, newValue) -> {
            this.expandedChanged(location.id(), newValue);
          }));
      }

      final var roots = new HashSet<CALocationID>();
      for (final var location : treeItems.values()) {
        final var parentOpt =
          location.getValue()
            .parent()
            .flatMap(i -> Optional.ofNullable(treeItems.get(i)));

        parentOpt.ifPresentOrElse(
          parent -> {
            parent.getChildren().add(location);
          },
          () -> {
            roots.add(location.getValue().id());
          });
      }

      final var newEverywhere =
        new TreeItem<CALocationItemType>(CALocationItemAll.get());
      newEverywhere.setExpanded(true);

      final var everywhereChildren =
        newEverywhere.getChildren();
      everywhereChildren.addAll(
        roots.stream()
          .map(treeItems::get)
          .map(CALocationTree::soften)
          .collect(Collectors.toList())
      );

      final var rootChildren =
        this.root.getChildren();
      rootChildren.clear();
      rootChildren.add(newEverywhere);
    } finally {
      this.edits.set(this.edits.get() + 1);
    }
  }

  private void expandedChanged(
    final CALocationID id,
    final Boolean newValue)
  {
    if (newValue.booleanValue()) {
      this.expanded.add(id);
    } else {
      this.expanded.remove(id);
    }
  }
}
