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

import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachmentKey;
import com.io7m.cardant.model.CAItemID;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

import java.util.Objects;
import java.util.Optional;

public record CAItemMutable(
  CAItemID id,
  StringProperty name,
  LongProperty countTotal,
  LongProperty countHere,
  StringExpression description,
  ObservableMap<String, CAItemMetadataMutable> metadata,
  ObservableMap<CAItemAttachmentKey, CAItemAttachmentMutable> attachments)
  implements CAMutableModelElementType
{
  public CAItemMutable
  {
    Objects.requireNonNull(id, "id");
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(countTotal, "countTotal");
    Objects.requireNonNull(countHere, "countHere");
    Objects.requireNonNull(metadata, "metadata");
    Objects.requireNonNull(attachments, "attachments");
  }

  public static CAItemMutable ofItem(
    final CAItem item)
  {
    final ObservableMap<String, CAItemMetadataMutable> metadataMap =
      FXCollections.observableHashMap();

    for (final var entry : item.metadata().entrySet()) {
      metadataMap.put(
        entry.getKey(),
        CAItemMetadataMutable.ofMetadata(entry.getValue())
      );
    }

    final var descriptionMetaBinding =
      Bindings.valueAt(metadataMap, "Description");
    final var descriptionStringBinding =
      Bindings.createStringBinding(() -> {
        final var meta = descriptionMetaBinding.get();
        if (meta == null) {
          return "";
        }
        return meta.value().get();
      }, descriptionMetaBinding);

    final ObservableMap<CAItemAttachmentKey, CAItemAttachmentMutable> attachmentSet =
      FXCollections.observableHashMap();

    for (final var entry : item.attachments().entrySet()) {
      attachmentSet.put(
        entry.getKey(),
        CAItemAttachmentMutable.ofItemAttachment(entry.getValue())
      );
    }

    return new CAItemMutable(
      item.id(),
      new SimpleStringProperty(item.name()),
      new SimpleLongProperty(item.countTotal()),
      new SimpleLongProperty(item.countHere()),
      descriptionStringBinding,
      metadataMap,
      attachmentSet
    );
  }

  public CAItemMutable updateFrom(
    final CAItem item)
  {
    this.countTotal.set(item.countTotal());
    this.countHere.set(item.countHere());
    this.name.set(item.name());

    CAMerges.merge(
      this.metadata,
      item.metadata(),
      CAItemMetadataMutable::ofMetadata,
      CAItemMetadataMutable::updateFrom
    );

    CAMerges.merge(
      this.attachments,
      item.attachments(),
      CAItemAttachmentMutable::ofItemAttachment,
      CAItemAttachmentMutable::updateFrom
    );

    return this;
  }

  public boolean matches(
    final String search)
  {
    return CAStringSearch.containsIgnoreCase(this.name, search)
      || CAStringSearch.containsIgnoreCase(this.id.id(), search)
      || CAStringSearch.containsIgnoreCase(this.description, search);
  }

  public Optional<CAItemAttachmentMutable> imageAttachment()
  {
    return this.attachments.values()
      .stream()
      .filter(a -> Objects.equals(a.relation(), "image"))
      .findFirst();
  }
}
