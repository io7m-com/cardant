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

import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientEventCommandFailed;
import com.io7m.cardant.client.api.CAClientEventDataChanged;
import com.io7m.cardant.client.api.CAClientEventDataReceived;
import com.io7m.cardant.client.api.CAClientEventStatusChanged;
import com.io7m.cardant.client.api.CAClientEventType;
import com.io7m.cardant.client.api.CAClientFactoryType;
import com.io7m.cardant.client.api.CAClientHostileType;
import com.io7m.cardant.gui.internal.model.CAItemAttachmentMutable;
import com.io7m.cardant.gui.internal.model.CAItemMetadataMutable;
import com.io7m.cardant.gui.internal.model.CAItemMutable;
import com.io7m.cardant.gui.internal.model.CALocationItemAll;
import com.io7m.cardant.gui.internal.model.CALocationItemDefined;
import com.io7m.cardant.gui.internal.model.CALocationItemType;
import com.io7m.cardant.gui.internal.model.CALocationTree;
import com.io7m.cardant.gui.internal.model.CATableMap;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachmentID;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationsAll;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocations;
import com.io7m.cardant.services.api.CAServiceType;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import static com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationExact;

public final class CAMainController implements CAServiceType
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAMainController.class);

  private final CAClientFactoryType clients;
  private final CAMainEventBusType eventBus;
  private final CAMainStrings strings;
  private final CATableMap<CAItemID, CAItemMutable> itemList;
  private final SimpleObjectProperty<Optional<CAClientHostileType>> client;
  private final SimpleObjectProperty<Optional<CAItemAttachmentMutable>> itemAttachmentSelected;
  private final SimpleObjectProperty<Optional<CAItemMetadataMutable>> itemMetadataSelected;
  private final SimpleObjectProperty<Optional<CAItemMutable>> itemSelected;
  private final CALocationTree locationTree;
  private final SimpleObjectProperty<Optional<CALocationItemType>> locationTreeSelected;
  private final SimpleStringProperty locationSearchProperty;
  private volatile CAPerpetualSubscriber<CAClientEventType> clientSubscriber;
  private volatile CATableMap<CAItemAttachmentID, CAItemAttachmentMutable> itemAttachmentList;
  private volatile CATableMap<String, CAItemMetadataMutable> itemMetadataList;
  private volatile Predicate<CAItemAttachmentMutable> itemAttachmentPredicate;
  private volatile Predicate<CAItemMetadataMutable> itemMetadataPredicate;

  public CAMainController(
    final CAMainStrings inStrings,
    final CAClientFactoryType inClients,
    final CAMainEventBusType inEventBus)
  {
    this.strings =
      Objects.requireNonNull(inStrings, "inStrings");
    this.clients =
      Objects.requireNonNull(inClients, "inClients");
    this.eventBus =
      Objects.requireNonNull(inEventBus, "eventBus");

    this.locationTree =
      new CALocationTree();
    this.itemList =
      new CATableMap<>(FXCollections.observableHashMap());
    this.itemAttachmentList =
      new CATableMap<>(FXCollections.observableHashMap());
    this.itemMetadataList =
      new CATableMap<>(FXCollections.observableHashMap());

    this.locationSearchProperty =
      new SimpleStringProperty();
    this.locationTreeSelected =
      new SimpleObjectProperty<>(Optional.empty());
    this.itemSelected =
      new SimpleObjectProperty<>(Optional.empty());
    this.itemAttachmentSelected =
      new SimpleObjectProperty<>(Optional.empty());
    this.itemMetadataSelected =
      new SimpleObjectProperty<>(Optional.empty());
    this.client =
      new SimpleObjectProperty<>(Optional.empty());

    this.itemMetadataPredicate = (ignored) -> true;
    this.itemAttachmentPredicate = (ignored) -> true;

    this.locationSearchProperty.addListener((observable, oldValue, newValue) -> {
      this.locationSearchChanged(newValue);
    });
  }

  private void locationSearchChanged(
    final String newValue)
  {
    final var text =
      newValue.trim()
        .toUpperCase(Locale.ROOT);

    if (text.isEmpty()) {
      this.locationTree().setFilter(Optional.empty());
    } else {
      this.locationTree().setFilter(Optional.of(text));
    }
  }

  public CATableMap<CAItemAttachmentID, CAItemAttachmentMutable> itemAttachments()
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

  public SimpleStringProperty locationSearchProperty()
  {
    return this.locationSearchProperty;
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

    this.itemMetadataList.setPredicate(this.itemMetadataPredicate);
    this.itemAttachmentList.setPredicate(this.itemAttachmentPredicate);
    this.itemSelected.set(itemSelection);
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

  public ObservableObjectValue<Optional<CAClientHostileType>> connectedClient()
  {
    return this.client;
  }

  public ObservableObjectValue<Optional<CAItemAttachmentMutable>> itemAttachmentSelected()
  {
    return this.itemAttachmentSelected;
  }

  public CAClientHostileType connect(
    final CAClientConfiguration configuration)
  {
    Objects.requireNonNull(configuration, "configuration");

    final var newClient =
      this.clients.openHostile(configuration);
    this.client.set(Optional.of(newClient));
    this.clientSubscriber =
      new CAPerpetualSubscriber<>(this::onClientEvent);

    newClient.events().subscribe(this.clientSubscriber);
    newClient.locationList();
    return newClient;
  }

  private void onClientEvent(
    final CAClientEventType item)
  {
    Platform.runLater(() -> {
      if (item instanceof CAClientEventStatusChanged status) {
        this.onClientEventStatusChanged(status);
      } else if (item instanceof CAClientEventDataReceived data) {
        this.onClientEventDataReceived(data);
      } else if (item instanceof CAClientEventDataChanged data) {
        this.onClientEventDataChanged(data);
      } else if (item instanceof CAClientEventCommandFailed<?> data) {
        this.onClientEventCommandFailed(data);
      } else {
        throw new IllegalStateException("Unrecognized event: " + item);
      }
    });
  }

  private void onClientEventCommandFailed(
    final CAClientEventCommandFailed<?> data)
  {
    final var message =
      this.strings.format(
        "status.commandFailed",
        data.command(),
        data.result().message()
      );

    this.eventBus.submit(new CAMainEventCommandFailed(message));
  }

  private void onClientEventStatusChanged(
    final CAClientEventStatusChanged status)
  {
    this.eventBus.submit(
      new CAMainEventClientStatus(
        status,
        switch (status) {
          case CLIENT_NEGOTIATING_PROTOCOLS -> {
            yield this.strings.format("status.connecting");
          }
          case CLIENT_NEGOTIATING_PROTOCOLS_FAILED -> {
            yield this.strings.format("status.connectionFailed");
          }
          case CLIENT_CONNECTED -> {
            yield this.strings.format("status.connected");
          }
          case CLIENT_DISCONNECTED -> {
            yield this.strings.format("status.disconnected");
          }
          case CLIENT_SENDING_REQUEST -> {
            yield this.strings.format("status.sendingRequest");
          }
          case CLIENT_RECEIVING_DATA -> {
            yield this.strings.format("status.receivingData");
          }
        }
      ));

    switch (status) {
      case CLIENT_NEGOTIATING_PROTOCOLS,
        CLIENT_NEGOTIATING_PROTOCOLS_FAILED,
        CLIENT_SENDING_REQUEST,
        CLIENT_RECEIVING_DATA -> {
      }
      case CLIENT_CONNECTED, CLIENT_DISCONNECTED -> {
        final var clientOpt = this.client.get();
        if (clientOpt.isEmpty()) {
          return;
        }

        final var clientNow =
          clientOpt.get();
        final var message =
          clientNow.isConnected()
            ? this.strings.format("status.connected")
            : this.strings.format("status.disconnected");

        this.eventBus.submit(
          new CAMainEventClientConnection(clientNow, message)
        );
      }
    }
  }

  private void onClientEventDataReceived(
    final CAClientEventDataReceived data)
  {
    final var element = data.element();
    if (element instanceof CAItems items) {
      for (final var item : items.items()) {
        this.onClientEventDataReceivedItem(item);
      }
      return;
    }

    if (element instanceof CAItem item) {
      this.onClientEventDataReceivedItem(item);
      return;
    }

    if (element instanceof CALocations locations) {
      this.onClientEventDataReceivedLocations(locations);
      return;
    }

    if (element instanceof CALocation location) {
      this.onClientEventDataReceivedLocation(location);
      return;
    }

    throw new IllegalStateException("Unexpected data: " + element);
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

  private void onClientEventDataChanged(
    final CAClientEventDataChanged data)
  {
    final var clientOpt = this.client.get();
    if (clientOpt.isEmpty()) {
      return;
    }

    final var clientNow = clientOpt.get();
    for (final var update : data.updated()) {
      if (update instanceof CAItemID id) {
        clientNow.itemGet(id);
      } else if (update instanceof CALocationID id) {
        // clientNow.locationGet(id);
      } else {
        throw new IllegalStateException("Unexpected ID: " + update);
      }
    }

    final var removed = data.removed();
    for (final var removedId : removed) {
      if (removedId instanceof CAItemID id) {
        this.onItemRemoved(id);
      } else if (removedId instanceof CALocationID id) {
        this.onLocationRemoved(id);
      } else if (removedId instanceof CAItemAttachmentID id) {
        // Nothing to do
      } else {
        throw new IllegalStateException("Unexpected ID: " + removedId);
      }
    }
  }

  private void onLocationRemoved(
    final CALocationID id)
  {
    this.locationTree.remove(id);
  }

  private void onItemRemoved(
    final CAItemID id)
  {
    this.itemList.writable()
      .remove(id);
  }

  public void disconnect()
  {
    try {
      final var clientOpt = this.client.get();
      if (clientOpt.isEmpty()) {
        return;
      }

      final var clientNow = clientOpt.get();
      this.client.set(Optional.empty());
      clientNow.close();
    } catch (final IOException e) {
      LOG.debug("error closing client: ", e);
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
      if (locationItem instanceof CALocationItemAll all) {
        this.listItemsForAllLocations();
      } else if (locationItem instanceof CALocationItemDefined defined) {
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
    final var clientNow =
      this.client.get().orElseThrow(IllegalStateException::new);

    clientNow.itemsList(new CAListLocationExact(defined.id()));
  }

  private void listItemsForAllLocations()
  {
    final var clientNow =
      this.client.get().orElseThrow(IllegalStateException::new);

    clientNow.itemsList(new CAListLocationsAll());
  }
}
