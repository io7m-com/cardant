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

import com.io7m.cardant.database.api.CADatabaseEventType;
import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseOpenEvent;
import com.io7m.cardant.database.api.CADatabaseParameters;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.derby.CADatabasesDerby;
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType.CAFileWithData;
import com.io7m.cardant.model.CAIdType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemAttachmentKey;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemMetadata;
import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CAItemRepositMove;
import com.io7m.cardant.model.CAItemRepositRemove;
import com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationExact;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAModelDatabaseEventUpdated;
import com.io7m.cardant.model.CAModelDatabaseQueriesType;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.model.CAUserID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_CYCLIC;
import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_DUPLICATE;
import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_NONEXISTENT;
import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_PARAMETERS_INVALID;
import static com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationsAll;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CADerbyDatabaseTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CADerbyDatabaseTest.class);

  private Path directory;
  private ArrayList<CADatabaseOpenEvent> events;
  private ArrayList<CAModelDatabaseEventUpdated> updates;
  private Path databaseDirectory;
  private HashSet<CAIdType> expectedUpdates;
  private HashSet<CAIdType> expectedRemoves;
  private int expectedChangeCount;

  private static <T> SortedSet<T> setOf(
    final T... items)
  {
    return new TreeSet<>(List.of(items));
  }

  @BeforeEach
  public void setup()
    throws IOException
  {
    this.directory =
      CATestDirectories.createTempDirectory();
    this.databaseDirectory =
      this.directory.resolve("database");

    this.events = new ArrayList<>();
    this.updates = new ArrayList<>();
    this.expectedUpdates = new HashSet<CAIdType>();
    this.expectedRemoves = new HashSet<CAIdType>();
  }

  @AfterEach
  public void tearDown()
    throws IOException
  {
    LOG.debug("checking expected changes");
    assertEquals(this.expectedChangeCount, this.updates.size());
    if (this.expectedChangeCount > 0) {
      assertEquals(this.expectedUpdates, this.updates.get(0).updated());
      assertEquals(this.expectedRemoves, this.updates.get(0).removed());
    }

    LOG.debug("deleting {}", this.directory);
    CATestDirectories.deleteDirectory(this.directory);
  }

  private void logEvent(
    final CADatabaseOpenEvent event)
  {
    LOG.debug("event: {}", event);
    this.events.add(event);
  }

  private void withDatabase(
    final WithDatabaseType withDatabase)
    throws Exception
  {
    final var databases = new CADatabasesDerby(Locale.getDefault());

    final var parameters =
      new CADatabaseParameters(
        this.databaseDirectory.toAbsolutePath().toString(),
        true
      );

    final var subscriber =
      new Flow.Subscriber<CADatabaseEventType>()
      {
        private Flow.Subscription sub;

        @Override
        public void onSubscribe(
          final Flow.Subscription subscription)
        {
          this.sub = subscription;
          subscription.request(1L);
        }

        @Override
        public void onNext(
          final CADatabaseEventType item)
        {
          if (item instanceof CAModelDatabaseEventUpdated updated) {
            CADerbyDatabaseTest.this.updates.add(updated);
          }
          this.sub.request(1L);
        }

        @Override
        public void onError(
          final Throwable throwable)
        {

        }

        @Override
        public void onComplete()
        {

        }
      };

    try (var database = databases.open(parameters, this::logEvent)) {
      database.events().subscribe(subscriber);

      try (var connection = database.openConnection()) {
        try (var transaction = connection.beginTransaction()) {
          withDatabase.call(
            transaction,
            transaction.queries(CAModelDatabaseQueriesType.class)
          );
        }
      }
    }
  }

  @Test
  public void testTagPutGet0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var tag0 =
        new CATag(
          CATagID.random(),
          "TAG0");

      queries.tagPut(tag0);

      {
        final var retrieved = queries.tagGet(tag0.id()).orElseThrow();
        assertEquals(tag0, retrieved);
      }

      final var tag0Renamed = new CATag(tag0.id(), "TAG0_CHANGED");
      queries.tagPut(tag0Renamed);

      {
        final var retrieved = queries.tagGet(tag0.id()).orElseThrow();
        assertEquals(tag0Renamed, retrieved);
      }

      this.expectedChangeCount = 1;
      this.expectedUpdates.add(tag0.id());
      transaction.commit();
    });
  }

  @Test
  public void testTagPutDuplicate0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var tag0 =
        new CATag(
          CATagID.random(),
          "TAG0");

      final var tag1 =
        new CATag(
          CATagID.random(),
          "TAG0");

      queries.tagPut(tag0);

      final var ex =
        assertThrows(
          CADatabaseException.class,
          () -> queries.tagPut(tag1)
        );
      assertTrue(ex.attributes().containsValue(tag0.name()));
    });
  }

  @Test
  public void testTagList()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var tagsPut = new TreeSet<CATag>();
      tagsPut.addAll(List.of(
        new CATag(
          CATagID.random(),
          "TAG0"),
        new CATag(
          CATagID.random(),
          "TAG1"),
        new CATag(
          CATagID.random(),
          "TAG2"),
        new CATag(
          CATagID.random(),
          "TAG3"),
        new CATag(
          CATagID.random(),
          "TAG4")
      ));

      for (final var tag : tagsPut) {
        queries.tagPut(tag);
      }

      final var tagsGet = queries.tagList();
      assertEquals(tagsPut, tagsGet);

      this.expectedChangeCount = 1;
      tagsPut.forEach(tag -> this.expectedUpdates.add(tag.id()));
      transaction.commit();
    });
  }

  @Test
  public void testItemSetGet0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var id = CAItemID.random();

      final var item0 =
        new CAItem(
          id,
          "",
          0L,
          0L,
          new TreeMap<>(),
          new TreeMap<>(),
          Collections.emptySortedSet());

      queries.itemCreate(id);

      {
        final var retrieved = queries.itemGet(id).orElseThrow();
        assertEquals(item0, retrieved);
      }

      final var item0p0 =
        new CAItem(
          id,
          "ITEM0",
          0L,
          0L,
          new TreeMap<>(),
          new TreeMap<>(),
          Collections.emptySortedSet());

      queries.itemNameSet(id, "ITEM0");

      {
        final var retrieved = queries.itemGet(id).orElseThrow();
        assertEquals(item0p0, retrieved);
      }

      this.expectedChangeCount = 1;
      this.expectedUpdates.add(item0.id());
      transaction.commit();
    });
  }

  @Test
  public void testItemCreateDeleted()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var id = CAItemID.random();

      queries.itemCreate(id);
      queries.itemsDeleteMarkOnly(Set.of(id));

      final var ex = assertThrows(CADatabaseException.class, () -> {
        queries.itemCreate(id);
      });
      assertEquals(ERROR_DUPLICATE, ex.errorCode());
      assertTrue(ex.getMessage().contains("previously deleted"));
    });
  }

  @Test
  public void testItemCreateListDeleted()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var id = CAItemID.random();

      queries.itemCreate(id);
      assertEquals(
        Set.of(id),
        queries.itemList(new CAListLocationsAll())
          .stream()
          .map(CAItem::id)
          .collect(Collectors.toSet()));
      assertEquals(Set.of(), queries.itemListDeleted());

      queries.itemsDeleteMarkOnly(Set.of(id));
      assertEquals(Set.of(), queries.itemList(new CAListLocationsAll()));
      assertEquals(Set.of(id), queries.itemListDeleted());
    });
  }

  @Test
  public void testItemSetNameNonexistent0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var id = CAItemID.random();

      final var ex = assertThrows(CADatabaseException.class, () -> {
        queries.itemNameSet(id, "HELLO");
      });
      assertEquals(ERROR_NONEXISTENT, ex.errorCode());
    });
  }

  @Test
  public void testItemCreateDuplicate0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var id = CAItemID.random();
      queries.itemCreate(id);

      final var ex = assertThrows(CADatabaseException.class, () -> {
        queries.itemCreate(id);
      });
      assertEquals(ERROR_DUPLICATE, ex.errorCode());
    });
  }

  @Test
  public void testItemListAll()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var itemsPut = new HashSet<CAItemID>();
      itemsPut.add(CAItemID.random());
      itemsPut.add(CAItemID.random());
      itemsPut.add(CAItemID.random());
      itemsPut.add(CAItemID.random());
      itemsPut.add(CAItemID.random());

      for (final var item : itemsPut) {
        queries.itemCreate(item);
      }

      final var itemsGet =
        queries.itemList(new CAListLocationsAll());
      assertEquals(
        itemsPut,
        itemsGet.stream()
          .map(CAItem::id)
          .collect(Collectors.toSet())
      );
    });
  }

  @Test
  public void testItemListLocationExact()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var location0 =
        new CALocation(
          CALocationID.random(),
          Optional.empty(),
          "Location 0",
          "D"
        );

      queries.locationPut(location0);

      final var itemsPut = new ArrayList<CAItemID>();
      itemsPut.add(CAItemID.random());
      itemsPut.add(CAItemID.random());
      itemsPut.add(CAItemID.random());
      itemsPut.add(CAItemID.random());
      itemsPut.add(CAItemID.random());

      for (final var item : itemsPut) {
        queries.itemCreate(item);
      }

      queries.itemReposit(
        new CAItemRepositAdd(itemsPut.get(0), location0.id(), 100L));
      queries.itemReposit(
        new CAItemRepositAdd(itemsPut.get(2), location0.id(), 100L));

      final var itemsGet =
        queries.itemList(new CAListLocationExact(location0.id()));

      final var itemsExpected = new HashSet<>();
      itemsExpected.add(itemsPut.get(0));
      itemsExpected.add(itemsPut.get(2));

      assertEquals(
        itemsExpected,
        itemsGet.stream()
          .map(CAItem::id)
          .collect(Collectors.toSet())
      );
    });
  }

  @Test
  public void testItemTags()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 = CAItemID.random();
      final var item1 = CAItemID.random();

      final var tag0 =
        new CATag(
          CATagID.random(),
          "TAG0");

      final var tag1 =
        new CATag(
          CATagID.random(),
          "TAG1");

      final var tag2 =
        new CATag(
          CATagID.random(),
          "TAG2");

      queries.itemCreate(item0);
      queries.itemCreate(item1);
      queries.tagPut(tag0);
      queries.tagPut(tag1);
      queries.tagPut(tag2);

      queries.itemTagAdd(item0, tag0);
      queries.itemTagAdd(item1, tag1);
      queries.itemTagAdd(item1, tag2);

      assertEquals(setOf(tag0), queries.itemTagList(item0));
      assertEquals(setOf(tag1, tag2), queries.itemTagList(item1));

      queries.itemTagRemove(item0, tag0);
      assertEquals(setOf(), queries.itemTagList(item0));
      assertEquals(setOf(tag1, tag2), queries.itemTagList(item1));

      queries.itemTagRemove(item1, tag2);
      assertEquals(setOf(), queries.itemTagList(item0));
      assertEquals(setOf(tag1), queries.itemTagList(item1));

      queries.itemTagRemove(item1, tag1);
      assertEquals(setOf(), queries.itemTagList(item0));
      assertEquals(setOf(), queries.itemTagList(item1));

      this.expectedChangeCount = 1;
      this.expectedUpdates.add(tag0.id());
      this.expectedUpdates.add(tag1.id());
      this.expectedUpdates.add(tag2.id());
      this.expectedUpdates.add(item0);
      this.expectedUpdates.add(item1);
      transaction.commit();
    });
  }

  @Test
  public void testItemTagsIntegrity0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 = CAItemID.random();

      final var tag0 =
        new CATag(
          CATagID.random(),
          "TAG0");

      final var with = new TreeSet<CATag>();
      with.add(tag0);

      assertEquals(Collections.emptySortedSet(), queries.tagList());
      queries.tagPut(tag0);
      assertEquals(with, queries.tagList());

      queries.itemCreate(item0);
      queries.itemTagAdd(item0, tag0);
      assertEquals(with, queries.itemTagList(item0));

      queries.tagDelete(tag0);
      assertEquals(Collections.emptySortedSet(), queries.itemTagList(item0));

      this.expectedChangeCount = 1;
      this.expectedUpdates.add(item0);
      this.expectedUpdates.add(tag0.id());
      this.expectedRemoves.add(tag0.id());
      transaction.commit();
    });
  }

  @Test
  public void testItemIntegrity0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 = CAItemID.random();

      final var tag0 =
        new CATag(
          CATagID.random(),
          "TAG0");

      assertEquals(setOf(), queries.tagList());
      queries.tagPut(tag0);
      assertEquals(setOf(tag0), queries.tagList());

      queries.itemCreate(item0);
      queries.itemTagAdd(item0, tag0);
      assertEquals(setOf(tag0), queries.itemTagList(item0));

      queries.itemsDelete(Set.of(item0));

      this.expectedChangeCount = 1;
      this.expectedUpdates.add(item0);
      this.expectedUpdates.add(tag0.id());
      this.expectedRemoves.add(item0);
      transaction.commit();
    });
  }

  @Test
  public void testItemTagNonexistent0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 = CAItemID.random();

      final var tag0 =
        new CATag(
          CATagID.random(),
          "TAG0");

      queries.itemCreate(item0);

      final var ex =
        assertThrows(
          CADatabaseException.class,
          () -> queries.itemTagAdd(item0, tag0)
        );
      assertTrue(ex.attributes().containsValue(tag0.displayId()));
      assertEquals(ERROR_NONEXISTENT, ex.errorCode());
    });
  }

  @Test
  public void testItemTagNonexistent1()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 = CAItemID.random();

      final var tag0 =
        new CATag(
          CATagID.random(),
          "TAG0");

      queries.tagPut(tag0);

      final var ex =
        assertThrows(
          CADatabaseException.class,
          () -> queries.itemTagAdd(item0, tag0)
        );
      assertTrue(ex.attributes().containsValue(item0.displayId()));
      assertEquals(ERROR_NONEXISTENT, ex.errorCode());
    });
  }

  @Test
  public void testItemMetadata0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 = CAItemID.random();

      queries.itemCreate(item0);

      final var meta0 =
        new CAItemMetadata("Type", "Vegetable");
      final var meta1 =
        new CAItemMetadata("Colour", "Red");

      queries.itemMetadataPut(item0, meta0);
      queries.itemMetadataPut(item0, meta1);

      {
        final var metas = queries.itemMetadata(item0);
        assertEquals(2, metas.size());
        assertEquals(meta0, metas.get("Type"));
        assertEquals(meta1, metas.get("Colour"));
      }

      queries.itemMetadataRemove(item0, meta0.name());

      {
        final var metas = queries.itemMetadata(item0);
        assertEquals(1, metas.size());
        assertEquals(meta1, metas.get("Colour"));
      }

      final var meta1p =
        new CAItemMetadata(meta1.name(), "Blue");
      queries.itemMetadataPut(item0, meta1p);

      {
        final var metas = queries.itemMetadata(item0);
        assertEquals(1, metas.size());
        assertEquals(meta1p, metas.get("Colour"));
      }

      this.expectedChangeCount = 1;
      this.expectedUpdates.add(item0);
      transaction.commit();
    });
  }

  @Test
  public void testItemMetadataIntegrity0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 = CAItemID.random();

      queries.itemCreate(item0);

      final var meta0 = new CAItemMetadata("Type", "Vegetable");
      queries.itemMetadataPut(item0, meta0);
      queries.itemsDelete(Set.of(item0));

      this.expectedChangeCount = 1;
      this.expectedUpdates.add(item0);
      this.expectedRemoves.add(item0);
      transaction.commit();
    });
  }

  @Test
  public void testUserNonexistent()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      assertEquals(
        Optional.empty(),
        queries.userGet(CAUserID.random())
      );
    });
  }

  @Test
  public void testUserNone()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      assertEquals(
        Map.of(),
        queries.userList()
      );
    });
  }

  @Test
  public void testUserPutGet()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var user = new CAUser(
        CAUserID.random(),
        "user",
        "password",
        "salt",
        "algo"
      );

      queries.userPut(user);
      assertEquals(Optional.of(user), queries.userGet(user.id()));
      assertEquals(Optional.of(user), queries.userGetByName("user"));
      assertEquals(Optional.empty(), queries.userGetByName("user1"));

      final var userAlt = new CAUser(
        user.id(),
        "user1",
        "password1",
        "salt1",
        "algo1"
      );

      queries.userPut(userAlt);
      assertEquals(Optional.of(userAlt), queries.userGet(user.id()));
      assertEquals(Optional.of(userAlt), queries.userGetByName("user1"));
      assertEquals(
        List.of(userAlt),
        List.copyOf(queries.userList().values())
      );

      this.expectedChangeCount = 1;
      this.expectedUpdates.add(user.id());
      transaction.commit();
    });
  }

  @Test
  public void testOpenTwice()
    throws Exception
  {
    final var tag =
      new CATag(
        CATagID.random(),
        "TAG");

    this.withDatabase((transaction, queries) -> {
      queries.tagPut(tag);
      transaction.commit();
    });
    this.withDatabase((transaction, queries) -> {
      final var rtag = queries.tagGet(tag.id()).orElseThrow();
      assertEquals(tag, rtag);
    });

    this.expectedChangeCount = 1;
    this.expectedUpdates.add(tag.id());
  }

  @Test
  public void testLocationNonexistent()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      assertEquals(
        Optional.empty(),
        queries.locationGet(CALocationID.random())
      );
    });
  }

  @Test
  public void testLocationNone()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      assertEquals(
        Map.of(),
        queries.userList()
      );
    });
  }

  @Test
  public void testLocationPutGet()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var location = new CALocation(
        CALocationID.random(),
        Optional.empty(),
        "location",
        "description"
      );

      queries.locationPut(location);
      assertEquals(Optional.of(location), queries.locationGet(location.id()));

      final var locationAlt = new CALocation(
        location.id(),
        Optional.empty(),
        "location1",
        "description1"
      );

      queries.locationPut(locationAlt);
      assertEquals(
        Optional.of(locationAlt),
        queries.locationGet(location.id()));
      assertEquals(
        List.of(locationAlt),
        List.copyOf(queries.locationList().values())
      );

      this.expectedChangeCount = 1;
      this.expectedUpdates.add(location.id());
      transaction.commit();
    });
  }

  @Test
  public void testLocationPutCyclic0()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {

      final var location0 =
        new CALocation(
          CALocationID.random(),
          Optional.empty(),
          "location",
          "description"
        );

      final var location1 =
        new CALocation(
          CALocationID.random(),
          Optional.empty(),
          "location",
          "description"
        );

      queries.locationPut(location0);
      queries.locationPut(location1);

      final var location1p =
        new CALocation(
          location1.id(),
          Optional.of(location0.id()),
          location1.name(),
          location1.description()
        );

      queries.locationPut(location1p);

      final var location0p =
        new CALocation(
          location0.id(),
          Optional.of(location1.id()),
          location0.name(),
          location0.description()
        );

      final var ex =
        assertThrows(CADatabaseException.class, () -> {
          queries.locationPut(location0p);
        });
      assertEquals(ERROR_CYCLIC, ex.errorCode());
    });
  }

  @Test
  public void testItemRepositAddRemove()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 =
        new CAItem(
          CAItemID.random(),
          "",
          0L,
          0L,
          new TreeMap<>(),
          new TreeMap<>(),
          Collections.emptySortedSet()
        );

      queries.itemCreate(item0.id());

      final var location =
        new CALocation(
          CALocationID.random(),
          Optional.empty(),
          "location",
          "location description"
        );

      queries.locationPut(location);
      queries.itemReposit(
        new CAItemRepositAdd(
          item0.id(),
          location.id(),
          100L)
      );

      {
        final var itemLocations =
          queries.itemLocations(item0.id())
            .itemLocations();
        assertEquals(1, itemLocations.size());
        final var byId = itemLocations.get(location.id());
        assertEquals(1, byId.size());
        final var itemLocation = byId.values().iterator().next();
        assertEquals(100L, itemLocation.count());
        assertEquals(item0.id(), itemLocation.item());
        assertEquals(location.id(), itemLocation.location());
      }

      {
        final var itemUpdated =
          queries.itemGet(item0.id()).orElseThrow();
        assertEquals(100L, itemUpdated.countTotal());
      }

      queries.itemReposit(
        new CAItemRepositAdd(
          item0.id(),
          location.id(),
          100L)
      );

      {
        final var itemLocations =
          queries.itemLocations(item0.id())
            .itemLocations();
        assertEquals(1, itemLocations.size());
        final var byId = itemLocations.get(location.id());
        assertEquals(1, byId.size());
        final var itemLocation = byId.values().iterator().next();
        assertEquals(200L, itemLocation.count());
        assertEquals(item0.id(), itemLocation.item());
        assertEquals(location.id(), itemLocation.location());
      }

      {
        final var itemUpdated =
          queries.itemGet(item0.id()).orElseThrow();
        assertEquals(200L, itemUpdated.countTotal());
      }

      queries.itemReposit(
        new CAItemRepositRemove(
          item0.id(),
          location.id(),
          100L)
      );

      {
        final var itemLocations =
          queries.itemLocations(item0.id())
            .itemLocations();
        assertEquals(1, itemLocations.size());
        final var byId = itemLocations.get(location.id());
        assertEquals(1, byId.size());
        final var itemLocation = byId.values().iterator().next();
        assertEquals(100L, itemLocation.count());
        assertEquals(item0.id(), itemLocation.item());
        assertEquals(location.id(), itemLocation.location());
      }

      {
        final var itemUpdated =
          queries.itemGet(item0.id()).orElseThrow();
        assertEquals(100L, itemUpdated.countTotal());
      }

      queries.itemReposit(
        new CAItemRepositRemove(
          item0.id(),
          location.id(),
          100L)
      );

      {
        final var itemLocations =
          queries.itemLocations(item0.id())
            .itemLocations();
        assertEquals(0, itemLocations.size());
      }

      {
        final var itemUpdated =
          queries.itemGet(item0.id()).orElseThrow();
        assertEquals(0L, itemUpdated.countTotal());
      }

      this.expectedChangeCount = 1;
      this.expectedUpdates.add(item0.id());
      this.expectedUpdates.add(location.id());
      transaction.commit();
    });
  }

  @Test
  public void testItemRepositAddRemoveTooMany()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 =
        new CAItem(
          CAItemID.random(),
          "",
          0L,
          0L,
          new TreeMap<>(),
          new TreeMap<>(),
          Collections.emptySortedSet()
        );

      queries.itemCreate(item0.id());

      final var location =
        new CALocation(
          CALocationID.random(),
          Optional.empty(),
          "location",
          "location description"
        );

      queries.locationPut(location);
      queries.itemReposit(
        new CAItemRepositAdd(
          item0.id(),
          location.id(),
          100L)
      );

      final var ex =
        assertThrows(CADatabaseException.class, () -> {
          queries.itemReposit(
            new CAItemRepositRemove(
              item0.id(),
              location.id(),
              101L)
          );
        });
      assertEquals(ERROR_PARAMETERS_INVALID, ex.errorCode());
    });
  }

  @Test
  public void testItemRepositNoItem()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var location =
        new CALocation(
          CALocationID.random(),
          Optional.empty(),
          "location",
          "location description"
        );

      queries.locationPut(location);

      final var ex =
        assertThrows(CADatabaseException.class, () -> {
          queries.itemReposit(
            new CAItemRepositAdd(
              CAItemID.random(),
              location.id(),
              100L)
          );
        });

      assertEquals(ERROR_NONEXISTENT, ex.errorCode());
    });
  }

  @Test
  public void testItemRepositNoLocation()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 =
        new CAItem(
          CAItemID.random(),
          "",
          0L,
          0L,
          new TreeMap<>(),
          new TreeMap<>(),
          Collections.emptySortedSet()
        );

      queries.itemCreate(item0.id());

      final var ex =
        assertThrows(CADatabaseException.class, () -> {
          queries.itemReposit(
            new CAItemRepositAdd(
              item0.id(),
              CALocationID.random(),
              100L)
          );
        });

      assertEquals(ERROR_NONEXISTENT, ex.errorCode());
    });
  }

  @Test
  public void testItemRepositMove()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var item0 =
        new CAItem(
          CAItemID.random(),
          "",
          0L,
          0L,
          new TreeMap<>(),
          new TreeMap<>(),
          Collections.emptySortedSet()
        );

      queries.itemCreate(item0.id());

      final var location0 =
        new CALocation(
          CALocationID.random(),
          Optional.empty(),
          "location0",
          "location description0"
        );
      final var location1 =
        new CALocation(
          CALocationID.random(),
          Optional.empty(),
          "location1",
          "location description1"
        );

      queries.locationPut(location0);
      queries.locationPut(location1);

      {
        final var itemLocations =
          queries.itemLocations(item0.id())
            .itemLocations();
        assertEquals(0, itemLocations.size());
      }

      queries.itemReposit(
        new CAItemRepositAdd(
          item0.id(),
          location0.id(),
          100L)
      );

      {
        final var itemLocations =
          queries.itemLocations(item0.id())
            .itemLocations();
        assertEquals(1, itemLocations.size());
        final var byId = itemLocations.get(location0.id());
        assertEquals(1, byId.size());
        final var itemLocation = byId.values().iterator().next();
        assertEquals(100L, itemLocation.count());
        assertEquals(item0.id(), itemLocation.item());
        assertEquals(location0.id(), itemLocation.location());
      }

      queries.itemReposit(
        new CAItemRepositMove(
          item0.id(),
          location0.id(),
          location1.id(),
          50L
        )
      );

      {
        final var itemLocations =
          queries.itemLocations(item0.id())
            .itemLocations();
        assertEquals(2, itemLocations.size());

        {
          final var byId = itemLocations.get(location0.id());

          {
            final var il = byId.get(item0.id());
            assertEquals(50L, il.count());
            assertEquals(item0.id(), il.item());
            assertEquals(location0.id(), il.location());
          }
        }

        {
          final var byId = itemLocations.get(location1.id());

          {
            final var il = byId.get(item0.id());
            assertEquals(50L, il.count());
            assertEquals(item0.id(), il.item());
            assertEquals(location1.id(), il.location());
          }
        }
      }

      {
        final var itemUpdated =
          queries.itemGet(item0.id()).orElseThrow();
        assertEquals(100L, itemUpdated.countTotal());
      }

      this.expectedChangeCount = 1;
      this.expectedUpdates.add(item0.id());
      this.expectedUpdates.add(location0.id());
      this.expectedUpdates.add(location1.id());
      transaction.commit();
    });
  }

  @Test
  public void testFilePutRemove()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var file =
        new CAFileWithData(
          CAFileID.random(),
          "File description",
          "text/plain",
          5L,
          "SHA-256",
          "3733cd977ff8eb18b987357e22ced99f46097f31ecb239e878ae63760e83e4d5",
          new CAByteArray("HELLO".getBytes(UTF_8))
        );

      queries.filePut(file);
      queries.fileRemove(file.id());

      this.expectedChangeCount = 1;
      this.expectedUpdates.add(file.id());
      this.expectedRemoves.add(file.id());
      transaction.commit();
    });
  }

  @Test
  public void testFileGet()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var file =
        new CAFileWithData(
          CAFileID.random(),
          "File description",
          "text/plain",
          5L,
          "SHA-256",
          "3733cd977ff8eb18b987357e22ced99f46097f31ecb239e878ae63760e83e4d5",
          new CAByteArray("HELLO".getBytes(UTF_8))
        );

      queries.filePut(file);

      final var fileWith =
        queries.fileGet(file.id(), true)
          .orElseThrow();
      assertEquals(file, fileWith);

      final var fileWithout =
        queries.fileGet(file.id(), false)
          .orElseThrow();
      assertEquals(fileWithout, file.withoutData());
      assertEquals(fileWithout, fileWithout.withoutData());

      queries.fileRemove(file.id());

      assertEquals(Optional.empty(), queries.fileGet(file.id(), false));
      assertEquals(Optional.empty(), queries.fileGet(file.id(), true));

      this.expectedChangeCount = 1;
      this.expectedUpdates.add(file.id());
      this.expectedRemoves.add(file.id());
      transaction.commit();
    });
  }

  @Test
  public void testFilePutUpdate()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var file =
        new CAFileWithData(
          CAFileID.random(),
          "File description",
          "text/plain",
          5L,
          "SHA-256",
          "3733cd977ff8eb18b987357e22ced99f46097f31ecb239e878ae63760e83e4d5",
          new CAByteArray("HELLO".getBytes(UTF_8))
        );

      queries.filePut(file);

      final var fileWith =
        queries.fileGet(file.id(), true)
          .orElseThrow();
      assertEquals(file, fileWith);

      final var fileUpdated =
        new CAFileWithData(
          file.id(),
          "File description 2",
          "text/plain 2",
          6L,
          "SHA-256",
          "a2f6017f1fab81333a4288f68557b74495a27337c7d37b3eba46c866aa885098",
          new CAByteArray("HELLO!".getBytes(UTF_8))
        );

      queries.filePut(fileUpdated);

      final var fileUpdatedWith =
        queries.fileGet(file.id(), true)
          .orElseThrow();
      assertEquals(fileUpdated, fileUpdatedWith);

      this.expectedChangeCount = 1;
      this.expectedUpdates.add(file.id());
      transaction.commit();
    });
  }

  @Test
  public void testItemAttachmentAddRemove()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var file =
        new CAFileWithData(
          CAFileID.random(),
          "File description",
          "text/plain",
          5L,
          "SHA-256",
          "3733cd977ff8eb18b987357e22ced99f46097f31ecb239e878ae63760e83e4d5",
          new CAByteArray("HELLO".getBytes(UTF_8))
        );

      queries.filePut(file);

      final var id = CAItemID.random();

      queries.itemCreate(id);
      queries.itemAttachmentAdd(id, file.id(), "description");

      final var item0Get =
        queries.itemGet(id)
          .orElseThrow();

      final var attachment =
        item0Get.attachments()
          .get(new CAItemAttachmentKey(file.id(), "description"));

      assertEquals(file.withoutData(), attachment.file());
      assertEquals("description", attachment.relation());

      queries.itemAttachmentRemove(id, file.id(), "description");

      this.expectedChangeCount = 1;
      this.expectedUpdates.add(file.id());
      this.expectedUpdates.add(id);
      transaction.commit();
    });
  }

  @Test
  public void testFilePutRemoveAttachmentIntegrity()
    throws Exception
  {
    this.withDatabase((transaction, queries) -> {
      final var file =
        new CAFileWithData(
          CAFileID.random(),
          "File description",
          "text/plain",
          5L,
          "SHA-256",
          "3733cd977ff8eb18b987357e22ced99f46097f31ecb239e878ae63760e83e4d5",
          new CAByteArray("HELLO".getBytes(UTF_8))
        );

      queries.filePut(file);

      final var id = CAItemID.random();

      queries.itemCreate(id);
      queries.itemAttachmentAdd(id, file.id(), "description");
      queries.fileRemove(file.id());

      this.expectedChangeCount = 1;
      this.expectedUpdates.add(file.id());
      this.expectedUpdates.add(id);
      this.expectedRemoves.add(file.id());
      transaction.commit();
    });
  }

  interface WithDatabaseType
  {
    void call(
      CADatabaseTransactionType transaction,
      CAModelDatabaseQueriesType queries)
      throws Exception;
  }
}
