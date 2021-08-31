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

package com.io7m.cardant.tests;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CABindingsDemo
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CABindingsDemo.class);

  private CABindingsDemo()
  {

  }

  private static record ItemMetadata(
    String name,
    SimpleStringProperty description)
  {

  }

  private static record Item(
    String name,
    ObservableMap<String, ItemMetadata> metadata)
  {

  }

  public static void main(
    final String[] args)
  {
    final ObservableMap<String, ItemMetadata> metadata =
      FXCollections.observableHashMap();

    final var metaBinding =
      Bindings.valueAt(metadata, "b");

    metaBinding.addListener((observable, oldValue, newValue) -> {
      LOG.debug("metaBinding: ({}) -> ({})", oldValue, newValue);
    });

    final var bBinding =
      Bindings.createStringBinding(() -> {
        final var meta = metaBinding.get();
        if (meta == null) {
          return "";
        } else {
          return meta.description.get();
        }
      }, metaBinding);

    bBinding.addListener((observable, oldValue, newValue) -> {
      LOG.debug("bBinding: ({}) -> ({})", oldValue, newValue);
    });

    bBinding.get();

    metadata.put(
      "a", new ItemMetadata("a", new SimpleStringProperty("z")));
    metadata.put(
      "b", new ItemMetadata("b", new SimpleStringProperty("y")));
    metadata.put(
      "c", new ItemMetadata("c", new SimpleStringProperty("x")));

    metadata.remove("b");

    final var item =
      new Item("item", metadata);
  }
}
