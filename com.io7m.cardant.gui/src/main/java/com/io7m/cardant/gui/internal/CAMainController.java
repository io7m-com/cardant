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

import com.io7m.cardant.client.api.CAClientEventDataReceived;
import com.io7m.cardant.client.api.CAClientEventDataUpdated;
import com.io7m.cardant.client.api.CAClientEventType;
import com.io7m.cardant.gui.internal.model.CAItemAndLocation;
import com.io7m.cardant.gui.internal.model.CAItemAttachmentMutable;
import com.io7m.cardant.gui.internal.model.CAItemLocationMutable;
import com.io7m.cardant.gui.internal.model.CAItemMetadataMutable;
import com.io7m.cardant.gui.internal.model.CAItemMutable;
import com.io7m.cardant.gui.internal.model.CALocationItemAll;
import com.io7m.cardant.gui.internal.model.CALocationItemDefined;
import com.io7m.cardant.gui.internal.model.CALocationItemType;
import com.io7m.cardant.gui.internal.model.CALocationTree;
import com.io7m.cardant.gui.internal.model.CATableMap;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachmentKey;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemLocations;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocations;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import com.io7m.repetoir.core.RPServiceType;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.Predicate;

import static com.io7m.cardant.client.api.CAClientUnit.UNIT;

public final class CAMainController implements RPServiceType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAMainController.class);

  private final CATableMap<CAItemID, CAItemMutable> itemList;
  private final SimpleObjectProperty<Optional<CAItemAttachmentMutable>> itemAttachmentSelected;
  private final SimpleObjectProperty<Optional<CAItemMetadataMutable>> itemMetadataSelected;
  private final SimpleObjectProperty<Optional<CAItemMutable>> itemSelected;
  private final CALocationTree locationTree;
  private final SimpleObjectProperty<Optional<CALocationItemType>> locationTreeSelected;
  private final ObservableMap<CAItemAndLocation, Long> itemLocations;
  private final ObservableMap<CAItemAndLocation, Long> itemLocationsRead;
  private CATableMap<CALocationID, CAItemLocationMutable> itemLocationsSelected;
  private volatile CATableMap<CAItemAttachmentKey, CAItemAttachmentMutable> itemAttachmentList;
  private volatile CATableMap<String, CAItemMetadataMutable> itemMetadataList;
  private volatile Predicate<CAItemAttachmentMutable> itemAttachmentPredicate;
  private volatile Predicate<CAItemMetadataMutable> itemMetadataPredicate;

  public CAMainController(
    final RPServiceDirectoryType services)
  {
    this.locationTree =
      new CALocationTree();
    this.itemList =
      new CATableMap<>(FXCollections.observableHashMap());
    this.itemAttachmentList =
      new CATableMap<>(FXCollections.observableHashMap());
    this.itemMetadataList =
      new CATableMap<>(FXCollections.observableHashMap());

    this.itemLocations =
      FXCollections.observableHashMap();
    this.itemLocationsRead =
      FXCollections.unmodifiableObservableMap(this.itemLocations);
    this.itemLocationsSelected =
      new CATableMap<>(FXCollections.observableHashMap());

    this.locationTreeSelected =
      new SimpleObjectProperty<>(Optional.empty());
    this.itemSelected =
      new SimpleObjectProperty<>(Optional.empty());
    this.itemAttachmentSelected =
      new SimpleObjectProperty<>(Optional.empty());
    this.itemMetadataSelected =
      new SimpleObjectProperty<>(Optional.empty());

    this.itemMetadataPredicate = (ignored) -> true;
    this.itemAttachmentPredicate = (ignored) -> true;
  }

  public ObservableMap<CAItemAndLocation, Long> itemLocations()
  {
    return this.itemLocationsRead;
  }

  public CATableMap<CAItemAttachmentKey, CAItemAttachmentMutable> itemAttachments()
  {
    return this.itemAttachmentList;
  }

  public CATableMap<CAItemID, CAItemMutable> items()
  {
    return this.itemList;
  }

  public CATableMap<String, CAItemMetadataMutable> itemMetadata()
  {
    return this.itemMetadataList;
  }

  public void itemMetadataSetSearch(
    final String search)
  {
    Objects.requireNonNull(search, "search");

    if (search.isEmpty()) {
      this.itemMetadataPredicate = (ignored) -> true;
    } else {
      this.itemMetadataPredicate = (itemMetadata) -> itemMetadata.matches(search);
    }

    this.itemMetadataList.setPredicate(this.itemMetadataPredicate);
  }

  public void itemAttachmentSetSearch(
    final String search)
  {
    Objects.requireNonNull(search, "search");

    if (search.isEmpty()) {
      this.itemAttachmentPredicate =
        (ignored) -> true;
    } else {
      this.itemAttachmentPredicate =
        (itemAttachment) -> itemAttachment.matches(search);
    }

    this.itemAttachmentList.setPredicate(this.itemAttachmentPredicate);
  }

  public void itemSetSearch(
    final String search)
  {
    Objects.requireNonNull(search, "search");

    if (search.isEmpty()) {
      this.itemList.setPredicate(item -> true);
    } else {
      this.itemList.setPredicate(item -> item.matches(search));
    }
  }

  public ObservableObjectValue<Optional<CAItemMetadataMutable>> itemMetadataSelected()
  {
    return this.itemMetadataSelected;
  }

  public ObservableObjectValue<Optional<CAItemMutable>> itemSelected()
  {
    return this.itemSelected;
  }

  public void itemSelect(
    final Optional<CAItemMutable> itemSelection)
  {
    Objects.requireNonNull(itemSelection, "itemSelection");

    if (itemSelection.isPresent()) {
      final var newValue = itemSelection.get();
      this.itemMetadataList =
        new CATableMap<>(newValue.metadata());
      this.itemAttachmentList =
        new CATableMap<>(newValue.attachments());
    } else {
      this.itemMetadataList =
        new CATableMap<>(FXCollections.observableHashMap());
      this.itemAttachmentList =
        new CATableMap<>(FXCollections.observableHashMap());
    }

    this.itemLocationsSelected =
      new CATableMap<>(FXCollections.observableHashMap());

    this.itemMetadataList.setPredicate(this.itemMetadataPredicate);
    this.itemAttachmentList.setPredicate(this.itemAttachmentPredicate);
    this.itemSelected.set(itemSelection);

    itemSelection.map(item -> {
      // XXX
      LOG.debug("ASK FOR ITEMS FOR LOCATION");
      return UNIT;
    });
  }

  public void itemAttachmentSelect(
    final Optional<CAItemAttachmentMutable> attachmentSelection)
  {
    this.itemAttachmentSelected.set(attachmentSelection);
  }

  public void itemMetadataSelect(
    final Optional<CAItemMetadataMutable> metadataSelection)
  {
    this.itemMetadataSelected.set(metadataSelection);
  }

  public ObservableObjectValue<Optional<CAItemAttachmentMutable>> itemAttachmentSelected()
  {
    return this.itemAttachmentSelected;
  }

  private void onClientEvent(
    final CAClientEventType item)
  {
    Platform.runLater(() -> {
      if (item instanceof final CAClientEventDataReceived data) {
        this.onClientEventDataReceived(data);
      } else if (item instanceof final CAClientEventDataUpdated data) {
        this.onClientEventDataUpdated(data);
      } else {
        throw new IllegalStateException("Unrecognized event: " + item);
      }
    });
  }

  private void onClientEventDataReceived(
    final CAClientEventDataReceived data)
  {
    final var element = data.data();
    if (element instanceof final CAItems items) {
      for (final var item : items.items()) {
        this.onClientEventDataReceivedItem(item);
      }
      return;
    }

    if (element instanceof final CAItem item) {
      this.onClientEventDataReceivedItem(item);
      return;
    }

    if (element instanceof final CALocations locations) {
      this.onClientEventDataReceivedLocations(locations);
      return;
    }

    if (element instanceof final CALocation location) {
      this.onClientEventDataReceivedLocation(location);
      return;
    }

    if (element instanceof final CAItemLocations itemLocations) {
      this.onClientEventDataReceivedItemLocations(itemLocations);
      return;
    }

    if (element instanceof CAItemID) {
      return;
    }

    if (element instanceof CAFileID) {
      return;
    }

    if (element instanceof CAFileType) {
      return;
    }

    throw new IllegalStateException("Unexpected data: " + element);
  }

  private void onClientEventDataReceivedItemLocations(
    final CAItemLocations inItemLocations)
  {
    final var itemCurrentlySelected =
      this.itemSelected.get();
    final var itemCurrentlySelectedId =
      itemCurrentlySelected.map(CAItemMutable::id);
    final var locationsSelectedMap =
      this.itemLocationsSelected.writable();

    locationsSelectedMap.clear();

    final var byLocation =
      inItemLocations.itemLocations();
    for (final var locationEntry : byLocation.entrySet()) {
      final var byItem =
        locationEntry.getValue();

      for (final var itemEntry : byItem.entrySet()) {
        final var itemId =
          itemEntry.getKey();
        final var locationId =
          locationEntry.getKey();

        this.itemLocations.put(
          new CAItemAndLocation(itemId, locationId),
          Long.valueOf(itemEntry.getValue().count())
        );

        if (itemCurrentlySelectedId.equals(Optional.of(itemId))) {
          final var locationName =
            Optional.ofNullable(this.locationTree.locations().get(locationId))
              .map(CALocationItemDefined::nameText)
              .orElse("");

          final var locationMutable =
            CAItemLocationMutable.of(locationName, itemEntry.getValue());
          locationsSelectedMap.put(locationId, locationMutable);
        }
      }
    }
  }

  private void onClientEventDataReceivedLocations(
    final CALocations locations)
  {
    this.locationTree.putAll(locations.locations().values());
  }

  private void onClientEventDataReceivedLocation(
    final CALocation location)
  {
    this.locationTree.put(location);
  }

  private void onClientEventDataReceivedItem(
    final CAItem item)
  {
    final var itemMap = this.itemList.writable();
    final var existing = itemMap.get(item.id());
    if (existing == null) {
      itemMap.put(item.id(), CAItemMutable.ofItem(item));
    } else {
      existing.updateFrom(item);
    }
  }

  private void onClientEventDataUpdated(
    final CAClientEventDataUpdated data)
  {
    for (final var update : data.updated()) {
      if (update instanceof final CAItemID id) {
        // XXX
        LOG.debug("ASK FOR ITEM");
      } else if (update instanceof final CALocationID id) {
        // clientNow.locationGet(id);
      } else if (update instanceof final CAFileID id) {
        // OK...
      } else {
        throw new IllegalStateException("Unexpected ID: " + update);
      }
    }

    final var removed = data.removed();
    for (final var removedId : removed) {
      if (removedId instanceof final CAItemID id) {
        this.onItemRemoved(id);
      } else if (removedId instanceof final CALocationID id) {
        this.onLocationRemoved(id);
      } else {
        throw new IllegalStateException("Unexpected ID: " + removedId);
      }
    }
  }

  private void onLocationRemoved(
    final CALocationID id)
  {
    this.locationTree.remove(id);

    final var toRemove = new ArrayList<CAItemAndLocation>();
    for (final var itemLocation : this.itemLocations.entrySet()) {
      final var key = itemLocation.getKey();
      if (Objects.equals(key.locationID(), id)) {
        toRemove.add(key);
      }
    }

    for (final var remove : toRemove) {
      this.itemLocations.remove(remove);
    }
  }

  private void onItemRemoved(
    final CAItemID id)
  {
    this.itemList.writable()
      .remove(id);

    final var toRemove = new ArrayList<CAItemAndLocation>();
    for (final var itemLocation : this.itemLocations.entrySet()) {
      final var key = itemLocation.getKey();
      if (Objects.equals(key.itemId(), id)) {
        toRemove.add(key);
      }
    }

    for (final var remove : toRemove) {
      this.itemLocations.remove(remove);
    }
  }

  @Override
  public String toString()
  {
    return String.format(
      "[CAMainController 0x%08x]",
      Integer.valueOf(this.hashCode())
    );
  }

  public CALocationTree locationTree()
  {
    return this.locationTree;
  }

  public ObservableObjectValue<Optional<CALocationItemType>> locationTreeSelected()
  {
    return this.locationTreeSelected;
  }

  public void locationTreeSelect(
    final Optional<CALocationItemType> locationSelection)
  {
    Objects.requireNonNull(locationSelection, "locationSelection");

    this.locationTreeSelected.set(locationSelection);

    if (locationSelection.isPresent()) {
      final var locationItem = locationSelection.get();
      if (locationItem instanceof final CALocationItemAll all) {
        this.listItemsForAllLocations();
      } else if (locationItem instanceof final CALocationItemDefined defined) {
        this.listItemsForDefinedLocation(defined);
      }
    } else {
      this.listItemsForAllLocations();
    }

    this.itemList.writable().clear();
  }

  private void listItemsForDefinedLocation(
    final CALocationItemDefined defined)
  {
    // XXX
    LOG.debug("GET ITEMS FOR LOCATION");
  }

  private void listItemsForAllLocations()
  {
    // XXX
    LOG.debug("GET ITEMS FOR ALL LOCATIONS");
  }

  public OptionalLong itemLocationCouldRemoveItems(
    final CAItemID id,
    final CALocationID location,
    final long toRemove)
  {
    final var bigExisting =
      new BigInteger(Long.toUnsignedString(
        this.itemLocationCount(id, location)));
    final var bigToRemove =
      new BigInteger(Long.toUnsignedString(toRemove));
    final var resulting =
      bigExisting.subtract(bigToRemove);

    if (resulting.compareTo(BigInteger.ZERO) >= 0) {
      return OptionalLong.of(resulting.longValue());
    }
    return OptionalLong.empty();
  }

  public long itemLocationCount(
    final CAItemID id,
    final CALocationID location)
  {
    return this.itemLocations.getOrDefault(
      new CAItemAndLocation(id, location), Long.valueOf(0L)).longValue();
  }

  public CATableMap<CALocationID, CAItemLocationMutable> itemLocationsSelected()
  {
    return this.itemLocationsSelected;
  }

  @Override
  public String description()
  {
    return "Main controller";
  }
}
