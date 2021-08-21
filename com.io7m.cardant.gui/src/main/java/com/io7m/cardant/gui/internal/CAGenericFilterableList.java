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

import com.io7m.jaffirm.core.Invariants;
import com.io7m.jaffirm.core.Postconditions;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public abstract class CAGenericFilterableList<T, I>
{
  private final ObservableList<T> items;
  private final FilteredList<T> filtered;
  private final SortedList<T> sorted;
  private final HashMap<I, Integer> itemIndices;
  private volatile String search;

  /**
   * Construct a generic filterable list.
   */

  public CAGenericFilterableList()
  {
    this.items =
      FXCollections.observableArrayList();
    this.itemIndices =
      new HashMap<>();

    this.search =
      "";
    this.filtered =
      this.items.filtered(this::checkSearch);
    this.sorted =
      new SortedList<>(this.filtered);
  }

  protected abstract I identifierFor(T item);

  protected abstract boolean isItemVisibleAccordingToSearch(
    T item,
    String searchQuery);

  private boolean checkSearch(final T item)
  {
    if (this.search.isEmpty()) {
      return true;
    }

    return this.isItemVisibleAccordingToSearch(item, this.search);
  }

  protected final boolean searchCompare(
    final Object object,
    final String searchQuery)
  {
    return object.toString()
      .toUpperCase(Locale.ROOT)
      .contains(searchQuery);
  }

  /**
   * Set the items for the item list.
   *
   * @param newItems The new items
   */

  public final void setItems(
    final Iterable<T> newItems)
  {
    final var newIndices =
      new HashMap<I, Integer>();
    final var newList =
      new ArrayList<T>();

    for (final var item : newItems) {
      final var index = newList.size();
      newIndices.put(this.identifierFor(item), Integer.valueOf(index));
      newList.add(item);
    }

    this.itemIndices.clear();
    this.itemIndices.putAll(newIndices);
    this.items.setAll(newList);

    Postconditions.checkPostcondition(
      this.itemIndices.size() == this.items.size(),
      "Item indices map size must match item list size"
    );
  }

  /**
   * Set the search filter.
   *
   * @param searchText The filter
   */

  public final void setSearch(
    final String searchText)
  {
    this.search =
      Objects.requireNonNull(searchText, "searchText")
        .toUpperCase(Locale.ROOT);

    this.items.setAll(List.copyOf(this.items));
  }

  /**
   * @return The observable list of items
   */

  public final ObservableList<T> items()
  {
    return this.sorted;
  }

  /**
   * @return The comparator used for sorting
   */

  public final ObjectProperty<Comparator<? super T>> comparator()
  {
    return this.sorted.comparatorProperty();
  }

  public final int updateItem(
    final T newItem)
  {
    Objects.requireNonNull(newItem, "item");

    final var newId =
      this.identifierFor(newItem);

    final var index = this.itemIndices.get(newId);
    if (index == null) {
      final var newIndex = Integer.valueOf(this.items.size());
      this.itemIndices.put(newId, newIndex);
      this.items.add(newItem);
      return newIndex.intValue();
    }

    final var indexUnboxed = index.intValue();
    final var existing = this.items.get(indexUnboxed);
    final var existingId = this.identifierFor(existing);

    Invariants.checkInvariant(
      Objects.equals(existingId, newId),
      "Item IDs must match"
    );
    this.items.set(indexUnboxed, newItem);
    return indexUnboxed;
  }

  public final void removeItem(
    final I removedId)
  {
    Objects.requireNonNull(removedId, "item");

    final var index = this.itemIndices.get(removedId);
    if (index != null) {
      final var existing =
        this.items.get(index.intValue());
      final var existingId =
        this.identifierFor(existing);

      Invariants.checkInvariant(
        Objects.equals(existingId, removedId),
        "Item IDs must match"
      );

      final var copy = new ArrayList<>(this.items);
      copy.remove(index.intValue());
      this.setItems(copy);
    }
  }
}
