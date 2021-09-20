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
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public final class CALocationTree
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CALocationTree.class);

  private static final CALocationID ROOT_ID =
    CALocationID.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));

  private final HashMap<CALocationID, TreeItem<CALocationItemDefined>> locations;
  private final TreeItem<CALocationItemDefined> root;

  public CALocationTree()
  {
    this.locations = new HashMap<>();

    this.root =
      new TreeItem<>(
        new CALocationItemDefined(
          ROOT_ID,
          new SimpleStringProperty(""),
          new SimpleStringProperty("")
        ));
  }

  public TreeItem<CALocationItemDefined> root()
  {
    return this.root;
  }

  public void put(
    final CALocation location)
  {
    Objects.requireNonNull(location, "location");

    if (Objects.equals(location.id(), ROOT_ID)) {
      return;
    }

    final var existing =
      this.locations.get(location.id());

    if (existing == null) {
      final var item = new TreeItem<>(CALocationItemDefined.of(location));
      this.setParent(location, item);
      this.locations.put(location.id(), item);
      return;
    }

    this.setParent(location, existing);
    existing.getValue().updateFrom(location);

    this.iterate(item -> {
      LOG.debug("item: {}", item);
    });
  }

  public void iterate(
    final Consumer<TreeItem<CALocationItemDefined>> processor)
  {
    this.iterateInner(this.root, processor);
  }

  private void iterateInner(
    final TreeItem<CALocationItemDefined> root,
    final Consumer<TreeItem<CALocationItemDefined>> processor)
  {
    processor.accept(root);
    for (final var child : root.getChildren()) {
      this.iterateInner(child, processor);
    }
  }

  private void setParent(
    final CALocation location,
    final TreeItem<CALocationItemDefined> item)
  {
    final var existingParent = item.getParent();
    if (existingParent != null) {
      existingParent.getChildren().remove(item);
    }

    final var targetParentId =
      location.parent().flatMap(x -> Optional.ofNullable(this.locations.get(x)));
    final var targetParent =
      targetParentId.orElse(this.root);

    targetParent.getChildren().add(item);
  }

  public void remove(
    final CALocationID id)
  {
    Objects.requireNonNull(id, "id");

    if (Objects.equals(id, ROOT_ID)) {
      return;
    }

    final var existing = this.locations.get(id);
    if (existing != null) {
      final var parent = existing.getParent();
      parent.getChildren().remove(existing);
    }

    this.locations.remove(id);
  }
}
