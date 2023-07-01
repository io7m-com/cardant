/*
 * Copyright © 2023 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.tests.database;

import com.io7m.cardant.database.api.CADatabaseConnectionType;
import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.model.CAItemColumn;
import com.io7m.cardant.model.CAItemColumnOrdering;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CAItemRepositMove;
import com.io7m.cardant.model.CAItemRepositRemove;
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationExact;
import com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationWithDescendants;
import com.io7m.cardant.model.CAListLocationBehaviourType.CAListLocationsAll;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.tests.containers.CATestContainers;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.test_extension.ErvillaCloseAfterAll;
import com.io7m.ervilla.test_extension.ErvillaConfiguration;
import com.io7m.ervilla.test_extension.ErvillaExtension;
import com.io7m.zelador.test_extension.CloseableResourcesType;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorDuplicate;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorRemoveTooManyItems;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(disabledIfUnsupported = true)
public final class CADatabaseItemsTest
{
  private static CATestContainers.CADatabaseFixture DATABASE_FIXTURE;
  private CADatabaseConnectionType connection;
  private CADatabaseTransactionType transaction;
  private CADatabaseType database;

  @BeforeAll
  public static void setupOnce(
    final @ErvillaCloseAfterAll EContainerSupervisorType containers)
    throws Exception
  {
    DATABASE_FIXTURE =
      CATestContainers.createDatabase(containers, 15432);
  }

  @BeforeEach
  public void setup(
    final CloseableResourcesType closeables)
    throws Exception
  {
    DATABASE_FIXTURE.reset();

    this.database =
      closeables.addPerTestResource(DATABASE_FIXTURE.createDatabase());
    this.connection =
      closeables.addPerTestResource(this.database.openConnection(CARDANT));
    this.transaction =
      closeables.addPerTestResource(this.connection.openTransaction());
  }

  /**
   * Creating items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemCreate()
    throws Exception
  {
    final var q =
      this.transaction.queries(CADatabaseQueriesItemsType.class);

    final var id0 =
      CAItemID.random();

    q.itemCreate(id0);

    final var ex0 =
      assertThrows(CADatabaseException.class, () -> q.itemCreate(id0));
    assertEquals(errorDuplicate(), ex0.errorCode());
  }

  /**
   * Setting item names works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemSetName()
    throws Exception
  {
    final var q =
      this.transaction.queries(CADatabaseQueriesItemsType.class);

    final var id0 =
      CAItemID.random();

    q.itemCreate(id0);
    q.itemNameSet(id0, "Item 0");
    assertEquals("Item 0", q.itemGet(id0).orElseThrow().name());

    final var ex0 =
      assertThrows(CADatabaseException.class, () -> {
        q.itemNameSet(CAItemID.random(), "x");
      });
    assertEquals(errorNonexistent(), ex0.errorCode());
  }

  /**
   * Deleting items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemDelete()
    throws Exception
  {
    final var q =
      this.transaction.queries(CADatabaseQueriesItemsType.class);

    final var id0 =
      CAItemID.random();

    q.itemCreate(id0);
    assertEquals(id0, q.itemGet(id0).orElseThrow().id());

    q.itemsDelete(List.of(id0));
    assertEquals(empty(), q.itemGet(id0));
  }

  /**
   * Marking items as deleted works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemDeleteMarkOnly()
    throws Exception
  {
    final var q =
      this.transaction.queries(CADatabaseQueriesItemsType.class);

    final var id0 =
      CAItemID.random();

    q.itemCreate(id0);
    assertEquals(id0, q.itemGet(id0).orElseThrow().id());

    q.itemsDeleteMarkOnly(List.of(id0));
    assertEquals(empty(), q.itemGet(id0));
  }

  /**
   * Listing items by descendants works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemSearchLocationDescendants()
    throws Exception
  {
    final var qi =
      this.transaction.queries(CADatabaseQueriesItemsType.class);
    final var ql =
      this.transaction.queries(CADatabaseQueriesLocationsType.class);

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        empty(),
        "Loc0",
        "Location 0"
      );
    final var loc1 =
      new CALocation(
        CALocationID.random(),
        Optional.of(loc0.id()),
        "Loc1",
        "Location 1"
      );
    final var loc2 =
      new CALocation(
        CALocationID.random(),
        Optional.of(loc1.id()),
        "Loc2",
        "Location 2"
      );

    final var items = new ArrayList<CAItemID>();
    for (int index = 0; index < 100; ++index) {
      final var itemId = CAItemID.random();
      qi.itemCreate(itemId);
      items.add(itemId);
    }

    ql.locationPut(loc0);
    ql.locationPut(loc1);
    ql.locationPut(loc2);

    /*
     * Sort items into locations.
     */

    final var itemsByLocation =
      new HashMap<CALocationID, HashSet<CAItemID>>();

    for (int index = 0; index < items.size(); ++index) {
      final var item = items.get(index);
      final CALocationID locationID;
      if (index > 60) {
        locationID = loc2.id();
      } else if (index > 30) {
        locationID = loc1.id();
      } else {
        locationID = loc0.id();
      }
      var m = itemsByLocation.get(locationID);
      if (m == null) {
        m = new HashSet<>();
      }
      m.add(item);
      itemsByLocation.put(locationID, m);
    }

    for (final var entry : itemsByLocation.entrySet()) {
      final var locationId = entry.getKey();
      final var locationItems = entry.getValue();
      for (final var item : locationItems) {
        qi.itemReposit(new CAItemRepositAdd(item, locationId, 1L));
      }
    }

    this.transaction.commit();

    /*
     * Searching for location 0 will return everything.
     */

    {
      final var search =
        qi.itemSearch(new CAItemSearchParameters(
          new CAListLocationWithDescendants(loc0.id()),
          empty(),
          new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
          100
        ));

      final var page = search.pageCurrent(qi);
      assertEquals(100, page.items().size());

      final var received =
        page.items()
          .stream()
          .collect(Collectors.toMap(CAItemSummary::id, Function.identity()));

      {
        final var locationItems =
          itemsByLocation.get(loc0.id());
        for (final var id : locationItems) {
          assertTrue(received.containsKey(id));
        }
      }

      {
        final var locationItems =
          itemsByLocation.get(loc1.id());
        for (final var id : locationItems) {
          assertTrue(received.containsKey(id));
        }
      }

      {
        final var locationItems =
          itemsByLocation.get(loc2.id());
        for (final var id : locationItems) {
          assertTrue(received.containsKey(id));
        }
      }
    }

    /*
     * Searching for location 1 will return 39 + 60 items.
     */

    {
      final var search =
        qi.itemSearch(new CAItemSearchParameters(
          new CAListLocationWithDescendants(loc1.id()),
          empty(),
          new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
          100
        ));

      final var page = search.pageCurrent(qi);
      assertEquals(69, page.items().size());

      final var received =
        page.items()
          .stream()
          .collect(Collectors.toMap(CAItemSummary::id, Function.identity()));

      {
        final var locationItems =
          itemsByLocation.get(loc0.id());
        for (final var id : locationItems) {
          assertFalse(received.containsKey(id));
        }
      }

      {
        final var locationItems =
          itemsByLocation.get(loc1.id());
        for (final var id : locationItems) {
          assertTrue(received.containsKey(id));
        }
      }

      {
        final var locationItems =
          itemsByLocation.get(loc2.id());
        for (final var id : locationItems) {
          assertTrue(received.containsKey(id));
        }
      }
    }

    /*
     * Searching for location 2 will return 39 items.
     */

    {
      final var search =
        qi.itemSearch(new CAItemSearchParameters(
          new CAListLocationWithDescendants(loc2.id()),
          empty(),
          new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
          100
        ));

      final var page = search.pageCurrent(qi);
      assertEquals(39, page.items().size());

      final var received =
        page.items()
          .stream()
          .collect(Collectors.toMap(CAItemSummary::id, Function.identity()));

      {
        final var locationItems =
          itemsByLocation.get(loc0.id());
        for (final var id : locationItems) {
          assertFalse(received.containsKey(id));
        }
      }

      {
        final var locationItems =
          itemsByLocation.get(loc1.id());
        for (final var id : locationItems) {
          assertFalse(received.containsKey(id));
        }
      }

      {
        final var locationItems =
          itemsByLocation.get(loc2.id());
        for (final var id : locationItems) {
          assertTrue(received.containsKey(id));
        }
      }
    }
  }

  /**
   * Listing items by exact location works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemSearchLocationExact()
    throws Exception
  {
    final var qi =
      this.transaction.queries(CADatabaseQueriesItemsType.class);
    final var ql =
      this.transaction.queries(CADatabaseQueriesLocationsType.class);

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        empty(),
        "Loc0",
        "Location 0"
      );
    final var loc1 =
      new CALocation(
        CALocationID.random(),
        Optional.of(loc0.id()),
        "Loc1",
        "Location 1"
      );
    final var loc2 =
      new CALocation(
        CALocationID.random(),
        Optional.of(loc1.id()),
        "Loc2",
        "Location 2"
      );

    final var items = new ArrayList<CAItemID>();
    for (int index = 0; index < 100; ++index) {
      final var itemId = CAItemID.random();
      qi.itemCreate(itemId);
      items.add(itemId);
    }

    ql.locationPut(loc0);
    ql.locationPut(loc1);
    ql.locationPut(loc2);

    /*
     * Sort items into locations.
     */

    final var itemsByLocation =
      new HashMap<CALocationID, HashSet<CAItemID>>();

    for (int index = 0; index < items.size(); ++index) {
      final var item = items.get(index);
      final CALocationID locationID;
      if (index > 60) {
        locationID = loc2.id();
      } else if (index > 30) {
        locationID = loc1.id();
      } else {
        locationID = loc0.id();
      }
      var m = itemsByLocation.get(locationID);
      if (m == null) {
        m = new HashSet<>();
      }
      m.add(item);
      itemsByLocation.put(locationID, m);
    }

    for (final var entry : itemsByLocation.entrySet()) {
      final var locationId = entry.getKey();
      final var locationItems = entry.getValue();
      for (final var item : locationItems) {
        qi.itemReposit(new CAItemRepositAdd(item, locationId, 1L));
      }
    }

    this.transaction.commit();

    /*
     * Searching for location 0 will return 31 items.
     */

    {
      final var search =
        qi.itemSearch(new CAItemSearchParameters(
          new CAListLocationExact(loc0.id()),
          empty(),
          new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
          100
        ));

      final var page = search.pageCurrent(qi);
      assertEquals(31, page.items().size());

      final var received =
        page.items()
          .stream()
          .collect(Collectors.toMap(CAItemSummary::id, Function.identity()));

      {
        final var locationItems =
          itemsByLocation.get(loc0.id());
        for (final var id : locationItems) {
          assertTrue(received.containsKey(id));
        }
      }

      {
        final var locationItems =
          itemsByLocation.get(loc1.id());
        for (final var id : locationItems) {
          assertFalse(received.containsKey(id));
        }
      }

      {
        final var locationItems =
          itemsByLocation.get(loc2.id());
        for (final var id : locationItems) {
          assertFalse(received.containsKey(id));
        }
      }
    }

    /*
     * Searching for location 1 will return 30 items.
     */

    {
      final var search =
        qi.itemSearch(new CAItemSearchParameters(
          new CAListLocationExact(loc1.id()),
          empty(),
          new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
          100
        ));

      final var page = search.pageCurrent(qi);
      assertEquals(30, page.items().size());

      final var received =
        page.items()
          .stream()
          .collect(Collectors.toMap(CAItemSummary::id, Function.identity()));

      {
        final var locationItems =
          itemsByLocation.get(loc0.id());
        for (final var id : locationItems) {
          assertFalse(received.containsKey(id));
        }
      }

      {
        final var locationItems =
          itemsByLocation.get(loc1.id());
        for (final var id : locationItems) {
          assertTrue(received.containsKey(id));
        }
      }

      {
        final var locationItems =
          itemsByLocation.get(loc2.id());
        for (final var id : locationItems) {
          assertFalse(received.containsKey(id));
        }
      }
    }

    /*
     * Searching for location 2 will return 39 items.
     */

    {
      final var search =
        qi.itemSearch(new CAItemSearchParameters(
          new CAListLocationExact(loc2.id()),
          empty(),
          new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
          100
        ));

      final var page = search.pageCurrent(qi);
      assertEquals(39, page.items().size());

      final var received =
        page.items()
          .stream()
          .collect(Collectors.toMap(CAItemSummary::id, Function.identity()));

      {
        final var locationItems =
          itemsByLocation.get(loc0.id());
        for (final var id : locationItems) {
          assertFalse(received.containsKey(id));
        }
      }

      {
        final var locationItems =
          itemsByLocation.get(loc1.id());
        for (final var id : locationItems) {
          assertFalse(received.containsKey(id));
        }
      }

      {
        final var locationItems =
          itemsByLocation.get(loc2.id());
        for (final var id : locationItems) {
          assertTrue(received.containsKey(id));
        }
      }
    }
  }

  /**
   * Listing items by all locations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemSearchLocationAll()
    throws Exception
  {
    final var qi =
      this.transaction.queries(CADatabaseQueriesItemsType.class);
    final var ql =
      this.transaction.queries(CADatabaseQueriesLocationsType.class);

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        empty(),
        "Loc0",
        "Location 0"
      );
    final var loc1 =
      new CALocation(
        CALocationID.random(),
        Optional.of(loc0.id()),
        "Loc1",
        "Location 1"
      );
    final var loc2 =
      new CALocation(
        CALocationID.random(),
        Optional.of(loc1.id()),
        "Loc2",
        "Location 2"
      );

    final var items = new ArrayList<CAItemID>();
    for (int index = 0; index < 100; ++index) {
      final var itemId = CAItemID.random();
      qi.itemCreate(itemId);
      items.add(itemId);
    }

    ql.locationPut(loc0);
    ql.locationPut(loc1);
    ql.locationPut(loc2);

    /*
     * Sort items into locations.
     */

    final var itemsByLocation =
      new HashMap<CALocationID, HashSet<CAItemID>>();

    for (int index = 0; index < items.size(); ++index) {
      final var item = items.get(index);
      final CALocationID locationID;
      if (index > 60) {
        locationID = loc2.id();
      } else if (index > 30) {
        locationID = loc1.id();
      } else {
        locationID = loc0.id();
      }
      var m = itemsByLocation.get(locationID);
      if (m == null) {
        m = new HashSet<>();
      }
      m.add(item);
      itemsByLocation.put(locationID, m);
    }

    for (final var entry : itemsByLocation.entrySet()) {
      final var locationId = entry.getKey();
      final var locationItems = entry.getValue();
      for (final var item : locationItems) {
        qi.itemReposit(new CAItemRepositAdd(item, locationId, 1L));
      }
    }

    this.transaction.commit();

    /*
     * Searching for location 0 will return 31 items.
     */

    {
      final var search =
        qi.itemSearch(new CAItemSearchParameters(
          new CAListLocationsAll(),
          empty(),
          new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
          100
        ));

      final var page = search.pageCurrent(qi);
      assertEquals(100, page.items().size());

      final var received =
        page.items()
          .stream()
          .collect(Collectors.toMap(CAItemSummary::id, Function.identity()));

      {
        final var locationItems =
          itemsByLocation.get(loc0.id());
        for (final var id : locationItems) {
          assertTrue(received.containsKey(id));
        }
      }

      {
        final var locationItems =
          itemsByLocation.get(loc1.id());
        for (final var id : locationItems) {
          assertTrue(received.containsKey(id));
        }
      }

      {
        final var locationItems =
          itemsByLocation.get(loc2.id());
        for (final var id : locationItems) {
          assertTrue(received.containsKey(id));
        }
      }
    }
  }

  /**
   * Listing items by all locations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemSearchByName()
    throws Exception
  {
    final var qi =
      this.transaction.queries(CADatabaseQueriesItemsType.class);

    final var items = new ArrayList<CAItemID>();
    for (int index = 0; index < 100; ++index) {
      final var itemId = CAItemID.random();
      qi.itemCreate(itemId);
      qi.itemNameSet(itemId, "Item %d".formatted(index));
      items.add(itemId);
    }

    {
      final var search =
        qi.itemSearch(new CAItemSearchParameters(
          new CAListLocationsAll(),
          Optional.of("Item 1"),
          new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
          100
        ));

      final var page = search.pageCurrent(qi);
      assertEquals(11, page.items().size());

      final var received =
        page.items()
          .stream()
          .collect(Collectors.toMap(CAItemSummary::id, Function.identity()));

      for (final var itemSummary : received.values()) {
        assertTrue(itemSummary.name().contains("Item 1"));
      }
    }
  }

  /**
   * Adding items work.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemRepositAdd()
    throws Exception
  {
    final var qi =
      this.transaction.queries(CADatabaseQueriesItemsType.class);
    final var ql =
      this.transaction.queries(CADatabaseQueriesLocationsType.class);

    final var itemId = CAItemID.random();
    qi.itemCreate(itemId);

    final var item_0 = qi.itemGet(itemId).orElseThrow();
    assertEquals(0L, item_0.countTotal());
    assertEquals(0L, item_0.countHere());

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        empty(),
        "Loc0",
        "Location 0"
      );

    ql.locationPut(loc0);
    qi.itemReposit(new CAItemRepositAdd(itemId, loc0.id(), 100L));

    final var item_1 = qi.itemGet(itemId).orElseThrow();
    assertEquals(100L, item_1.countTotal());
    assertEquals(0L, item_1.countHere());

    qi.itemReposit(new CAItemRepositAdd(itemId, loc0.id(), 100L));

    final var item_2 = qi.itemGet(itemId).orElseThrow();
    assertEquals(200L, item_2.countTotal());
    assertEquals(0L, item_2.countHere());
  }

  /**
   * Removing items work.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemRepositRemove()
    throws Exception
  {
    final var qi =
      this.transaction.queries(CADatabaseQueriesItemsType.class);
    final var ql =
      this.transaction.queries(CADatabaseQueriesLocationsType.class);

    final var itemId = CAItemID.random();
    qi.itemCreate(itemId);

    final var item_0 = qi.itemGet(itemId).orElseThrow();
    assertEquals(0L, item_0.countTotal());
    assertEquals(0L, item_0.countHere());

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        empty(),
        "Loc0",
        "Location 0"
      );

    ql.locationPut(loc0);
    qi.itemReposit(new CAItemRepositAdd(itemId, loc0.id(), 100L));

    final var item_1 = qi.itemGet(itemId).orElseThrow();
    assertEquals(100L, item_1.countTotal());
    assertEquals(0L, item_1.countHere());

    qi.itemReposit(new CAItemRepositRemove(itemId, loc0.id(), 99L));

    final var item_2 = qi.itemGet(itemId).orElseThrow();
    assertEquals(1L, item_2.countTotal());
    assertEquals(0L, item_2.countHere());

    qi.itemReposit(new CAItemRepositRemove(itemId, loc0.id(), 1L));

    final var item_3 = qi.itemGet(itemId).orElseThrow();
    assertEquals(0L, item_3.countTotal());
    assertEquals(0L, item_3.countHere());
  }

  /**
   * It's not possible to remove too many items.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemRepositRemoveTooMany()
    throws Exception
  {
    final var qi =
      this.transaction.queries(CADatabaseQueriesItemsType.class);
    final var ql =
      this.transaction.queries(CADatabaseQueriesLocationsType.class);

    final var itemId = CAItemID.random();
    qi.itemCreate(itemId);

    final var item_0 = qi.itemGet(itemId).orElseThrow();
    assertEquals(0L, item_0.countTotal());
    assertEquals(0L, item_0.countHere());

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        empty(),
        "Loc0",
        "Location 0"
      );

    ql.locationPut(loc0);
    qi.itemReposit(new CAItemRepositAdd(itemId, loc0.id(), 100L));

    final var item_1 = qi.itemGet(itemId).orElseThrow();
    assertEquals(100L, item_1.countTotal());
    assertEquals(0L, item_1.countHere());

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        qi.itemReposit(new CAItemRepositRemove(itemId, loc0.id(), 200L));
      });

    assertEquals(errorRemoveTooManyItems(), ex.errorCode());
  }

  /**
   * Moving items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemRepositMove()
    throws Exception
  {
    final var qi =
      this.transaction.queries(CADatabaseQueriesItemsType.class);
    final var ql =
      this.transaction.queries(CADatabaseQueriesLocationsType.class);

    final var itemId = CAItemID.random();
    qi.itemCreate(itemId);

    final var item_0 = qi.itemGet(itemId).orElseThrow();
    assertEquals(0L, item_0.countTotal());
    assertEquals(0L, item_0.countHere());

    final var loc0 =
      new CALocation(
        CALocationID.random(), empty(), "Loc0", "Location 0"
      );
    final var loc1 =
      new CALocation(
        CALocationID.random(), empty(), "Loc1", "Location 1"
      );

    ql.locationPut(loc0);
    ql.locationPut(loc1);
    qi.itemReposit(new CAItemRepositAdd(itemId, loc0.id(), 100L));
    qi.itemReposit(new CAItemRepositMove(itemId, loc0.id(), loc1.id(), 50L));

    final var item_1 = qi.itemGet(itemId).orElseThrow();
    assertEquals(100L, item_1.countTotal());
    assertEquals(0L, item_1.countHere());
  }

  /**
   * It's not possible to move more items than exist.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemRepositMoveTooMany()
    throws Exception
  {
    final var qi =
      this.transaction.queries(CADatabaseQueriesItemsType.class);
    final var ql =
      this.transaction.queries(CADatabaseQueriesLocationsType.class);

    final var itemId = CAItemID.random();
    qi.itemCreate(itemId);

    final var item_0 = qi.itemGet(itemId).orElseThrow();
    assertEquals(0L, item_0.countTotal());
    assertEquals(0L, item_0.countHere());

    final var loc0 =
      new CALocation(
        CALocationID.random(), empty(), "Loc0", "Location 0"
      );
    final var loc1 =
      new CALocation(
        CALocationID.random(), empty(), "Loc1", "Location 1"
      );

    ql.locationPut(loc0);
    ql.locationPut(loc1);
    qi.itemReposit(new CAItemRepositAdd(itemId, loc0.id(), 100L));

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        qi.itemReposit(new CAItemRepositMove(
          itemId,
          loc0.id(),
          loc1.id(),
          101L));
      });

    assertEquals(errorRemoveTooManyItems(), ex.errorCode());
  }
}
