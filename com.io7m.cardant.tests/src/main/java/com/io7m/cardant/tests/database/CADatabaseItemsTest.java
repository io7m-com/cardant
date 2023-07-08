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
import com.io7m.cardant.database.api.CADatabaseQueriesFilesType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.AttachmentAddType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.AttachmentRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.AttachmentsGetType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.MetadataGetType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.MetadataPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.MetadataRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.SetNameType.Parameters;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.TagAddType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.TagListType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.TagRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType;
import com.io7m.cardant.database.api.CADatabaseQueriesTagsType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType.CAFileWithData;
import com.io7m.cardant.model.CAItemAttachment;
import com.io7m.cardant.model.CAItemColumn;
import com.io7m.cardant.model.CAItemColumnOrdering;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemMetadata;
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
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;
import com.io7m.cardant.tests.containers.CATestContainers;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.test_extension.ErvillaCloseAfterAll;
import com.io7m.ervilla.test_extension.ErvillaConfiguration;
import com.io7m.ervilla.test_extension.ErvillaExtension;
import com.io7m.lanark.core.RDottedName;
import com.io7m.zelador.test_extension.CloseableResourcesType;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
  private CADatabaseQueriesItemsType.CreateType itemCreate;
  private CADatabaseQueriesItemsType.SetNameType setName;
  private CADatabaseQueriesItemsType.GetType get;
  private CADatabaseQueriesItemsType.DeleteMarkOnlyType deleteMark;
  private CADatabaseQueriesLocationsType.PutType locPut;
  private CADatabaseQueriesItemsType.RepositType repositQuery;
  private CADatabaseQueriesItemsType.SearchType searchQuery;
  private CADatabaseQueriesItemsType.DeleteType delete;
  private CADatabaseQueriesItemsType.GetType itemGet;
  private MetadataPutType metaAdd;
  private MetadataRemoveType metaRemove;
  private MetadataGetType metaGet;

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

    this.itemCreate =
      this.transaction.queries(CADatabaseQueriesItemsType.CreateType.class);
    this.itemGet =
      this.transaction.queries(CADatabaseQueriesItemsType.GetType.class);
    this.setName =
      this.transaction.queries(CADatabaseQueriesItemsType.SetNameType.class);
    this.get =
      this.transaction.queries(CADatabaseQueriesItemsType.GetType.class);
    this.deleteMark =
      this.transaction.queries(CADatabaseQueriesItemsType.DeleteMarkOnlyType.class);
    this.delete =
      this.transaction.queries(CADatabaseQueriesItemsType.DeleteType.class);
    this.locPut =
      this.transaction.queries(CADatabaseQueriesLocationsType.PutType.class);
    this.repositQuery =
      this.transaction.queries(CADatabaseQueriesItemsType.RepositType.class);
    this.searchQuery =
      this.transaction.queries(CADatabaseQueriesItemsType.SearchType.class);
    this.metaAdd =
      this.transaction.queries(MetadataPutType.class);
    this.metaRemove =
      this.transaction.queries(MetadataRemoveType.class);
    this.metaGet =
      this.transaction.queries(MetadataGetType.class);
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
    final var id0 =
      CAItemID.random();

    this.itemCreate.execute(id0);

    final var ex0 =
      assertThrows(
        CADatabaseException.class,
        () -> this.itemCreate.execute(id0));
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
    final var id0 =
      CAItemID.random();

    this.itemCreate.execute(id0);
    this.setName.execute(new Parameters(id0, "Item 0"));
    assertEquals("Item 0", this.get.execute(id0).orElseThrow().name());

    final var ex0 =
      assertThrows(CADatabaseException.class, () -> {
        this.setName.execute(new Parameters(CAItemID.random(), "x"));
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
    final var id0 =
      CAItemID.random();

    this.itemCreate.execute(id0);
    assertEquals(id0, this.get.execute(id0).orElseThrow().id());

    this.delete.execute(List.of(id0));
    assertEquals(empty(), this.get.execute(id0));
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
    final var id0 =
      CAItemID.random();

    this.itemCreate.execute(id0);
    assertEquals(id0, this.get.execute(id0).orElseThrow().id());

    this.deleteMark.execute(List.of(id0));
    assertEquals(empty(), this.get.execute(id0));
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
      this.itemCreate.execute(itemId);
      items.add(itemId);
    }

    this.locPut.execute(loc0);
    this.locPut.execute(loc1);
    this.locPut.execute(loc2);

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
        this.repositQuery.execute(new CAItemRepositAdd(item, locationId, 1L));
      }
    }

    this.transaction.commit();

    /*
     * Searching for location 0 will return everything.
     */

    {
      final var search =
        this.searchQuery.execute(new CAItemSearchParameters(
          new CAListLocationWithDescendants(loc0.id()),
          empty(),
          new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
          100
        ));

      final var page = search.pageCurrent(this.transaction);
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
        this.searchQuery.execute(new CAItemSearchParameters(
          new CAListLocationWithDescendants(loc1.id()),
          empty(),
          new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
          100
        ));

      final var page = search.pageCurrent(this.transaction);
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
        this.searchQuery.execute(new CAItemSearchParameters(
          new CAListLocationWithDescendants(loc2.id()),
          empty(),
          new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
          100
        ));

      final var page = search.pageCurrent(this.transaction);
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
      this.itemCreate.execute(itemId);
      items.add(itemId);
    }

    this.locPut.execute(loc0);
    this.locPut.execute(loc1);
    this.locPut.execute(loc2);

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
        this.repositQuery.execute(new CAItemRepositAdd(item, locationId, 1L));
      }
    }

    this.transaction.commit();

    /*
     * Searching for location 0 will return 31 items.
     */

    {
      final var search =
        this.searchQuery.execute(new CAItemSearchParameters(
          new CAListLocationExact(loc0.id()),
          empty(),
          new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
          100
        ));

      final var page = search.pageCurrent(this.transaction);
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
        this.searchQuery.execute(new CAItemSearchParameters(
          new CAListLocationExact(loc1.id()),
          empty(),
          new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
          100
        ));

      final var page = search.pageCurrent(this.transaction);
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
        this.searchQuery.execute(new CAItemSearchParameters(
          new CAListLocationExact(loc2.id()),
          empty(),
          new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
          100
        ));

      final var page = search.pageCurrent(this.transaction);
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
      this.itemCreate.execute(itemId);
      items.add(itemId);
    }

    this.locPut.execute(loc0);
    this.locPut.execute(loc1);
    this.locPut.execute(loc2);

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
        this.repositQuery.execute(new CAItemRepositAdd(item, locationId, 1L));
      }
    }

    this.transaction.commit();

    /*
     * Searching for location 0 will return 31 items.
     */

    {
      final var search =
        this.searchQuery.execute(new CAItemSearchParameters(
          new CAListLocationsAll(),
          empty(),
          new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
          100
        ));

      final var page = search.pageCurrent(this.transaction);
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
    final var items = new ArrayList<CAItemID>();
    for (int index = 0; index < 100; ++index) {
      final var itemId = CAItemID.random();
      this.itemCreate.execute(itemId);
      this.setName.execute(new Parameters(itemId, "Item %d".formatted(index)));
      items.add(itemId);
    }

    {
      final var search =
        this.searchQuery.execute(new CAItemSearchParameters(
          new CAListLocationsAll(),
          Optional.of("Item 1"),
          new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
          100
        ));

      final var page = search.pageCurrent(this.transaction);
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
    final var itemId = CAItemID.random();
    this.itemCreate.execute(itemId);

    final var item_0 = this.itemGet.execute(itemId).orElseThrow();
    assertEquals(0L, item_0.countTotal());
    assertEquals(0L, item_0.countHere());

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        empty(),
        "Loc0",
        "Location 0"
      );

    this.locPut.execute(loc0);
    this.repositQuery.execute(new CAItemRepositAdd(itemId, loc0.id(), 100L));

    final var item_1 = this.itemGet.execute(itemId).orElseThrow();
    assertEquals(100L, item_1.countTotal());
    assertEquals(0L, item_1.countHere());

    this.repositQuery.execute(new CAItemRepositAdd(itemId, loc0.id(), 100L));

    final var item_2 = this.itemGet.execute(itemId).orElseThrow();
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
    final var itemId = CAItemID.random();
    this.itemCreate.execute(itemId);

    final var item_0 = this.itemGet.execute(itemId).orElseThrow();
    assertEquals(0L, item_0.countTotal());
    assertEquals(0L, item_0.countHere());

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        empty(),
        "Loc0",
        "Location 0"
      );

    this.locPut.execute(loc0);
    this.repositQuery.execute(new CAItemRepositAdd(itemId, loc0.id(), 100L));

    final var item_1 = this.itemGet.execute(itemId).orElseThrow();
    assertEquals(100L, item_1.countTotal());
    assertEquals(0L, item_1.countHere());

    this.repositQuery.execute(new CAItemRepositRemove(itemId, loc0.id(), 99L));

    final var item_2 = this.itemGet.execute(itemId).orElseThrow();
    assertEquals(1L, item_2.countTotal());
    assertEquals(0L, item_2.countHere());

    this.repositQuery.execute(new CAItemRepositRemove(itemId, loc0.id(), 1L));

    final var item_3 = this.itemGet.execute(itemId).orElseThrow();
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
    final var itemId = CAItemID.random();
    this.itemCreate.execute(itemId);

    final var item_0 = this.itemGet.execute(itemId).orElseThrow();
    assertEquals(0L, item_0.countTotal());
    assertEquals(0L, item_0.countHere());

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        empty(),
        "Loc0",
        "Location 0"
      );

    this.locPut.execute(loc0);
    this.repositQuery.execute(new CAItemRepositAdd(itemId, loc0.id(), 100L));

    final var item_1 = this.itemGet.execute(itemId).orElseThrow();
    assertEquals(100L, item_1.countTotal());
    assertEquals(0L, item_1.countHere());

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.repositQuery.execute(new CAItemRepositRemove(
          itemId,
          loc0.id(),
          200L));
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
    final var itemId = CAItemID.random();
    this.itemCreate.execute(itemId);

    final var item_0 = this.itemGet.execute(itemId).orElseThrow();
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

    this.locPut.execute(loc0);
    this.locPut.execute(loc1);
    this.repositQuery.execute(new CAItemRepositAdd(itemId, loc0.id(), 100L));
    this.repositQuery.execute(new CAItemRepositMove(
      itemId,
      loc0.id(),
      loc1.id(),
      50L));

    final var item_1 = this.itemGet.execute(itemId).orElseThrow();
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
    final var itemId = CAItemID.random();
    this.itemCreate.execute(itemId);

    final var item_0 = this.itemGet.execute(itemId).orElseThrow();
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

    this.locPut.execute(loc0);
    this.locPut.execute(loc1);
    this.repositQuery.execute(new CAItemRepositAdd(itemId, loc0.id(), 100L));

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.repositQuery.execute(new CAItemRepositMove(
          itemId,
          loc0.id(),
          loc1.id(),
          101L));
      });

    assertEquals(errorRemoveTooManyItems(), ex.errorCode());
  }

  /**
   * Item metadata adjustments work.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemMetadata()
    throws Exception
  {
    final var id0 =
      CAItemID.random();

    this.itemCreate.execute(id0);

    final var meta0 =
      new CAItemMetadata(new RDottedName("x.y.a0"), "abc");
    final var meta1 =
      new CAItemMetadata(new RDottedName("x.y.a1"), "def");
    final var meta2 =
      new CAItemMetadata(new RDottedName("x.y.a2"), "ghi");

    this.metaAdd.execute(
      new MetadataPutType.Parameters(id0, Set.of(meta0, meta1, meta2))
    );

    {
      final var i = this.itemGet.execute(id0).orElseThrow();
      assertEquals(meta0, i.metadata().get(meta0.name()));
      assertEquals(meta1, i.metadata().get(meta1.name()));
      assertEquals(meta2, i.metadata().get(meta2.name()));
    }

    {
      final var m = this.metaGet.execute(id0);
      assertEquals(meta0, m.get(meta0.name()));
      assertEquals(meta1, m.get(meta1.name()));
      assertEquals(meta2, m.get(meta2.name()));
    }

    this.metaRemove.execute(
      new MetadataRemoveType.Parameters(id0, Set.of(meta1.name()))
    );

    {
      final var i = this.itemGet.execute(id0).orElseThrow();
      assertEquals(meta0, i.metadata().get(meta0.name()));
      assertEquals(meta2, i.metadata().get(meta2.name()));
    }

    {
      final var m = this.metaGet.execute(id0);
      assertEquals(meta0, m.get(meta0.name()));
      assertEquals(meta2, m.get(meta2.name()));
    }

    this.metaRemove.execute(
      new MetadataRemoveType.Parameters(id0, Set.of(meta0.name(), meta1.name(), meta2.name()))
    );

    {
      final var i = this.itemGet.execute(id0).orElseThrow();
      assertEquals(Map.of(), i.metadata());
    }

    {
      final var m = this.metaGet.execute(id0);
      assertEquals(Map.of(), m);
    }
  }

  /**
   * Item tag adjustments work.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemTags()
    throws Exception
  {
    final var id0 =
      CAItemID.random();

    this.itemCreate.execute(id0);

    final var tagPut =
      this.transaction.queries(CADatabaseQueriesTagsType.PutType.class);
    final var itemTagPut =
      this.transaction.queries(TagAddType.class);
    final var itemTagRemove =
      this.transaction.queries(TagRemoveType.class);
    final var itemTagList =
      this.transaction.queries(TagListType.class);

    final var tag0 =
      new CATag(CATagID.random(), "TAG0");
    final var tag1 =
      new CATag(CATagID.random(), "TAG1");
    final var tag2 =
      new CATag(CATagID.random(), "TAG2");

    tagPut.execute(tag0);
    tagPut.execute(tag1);
    tagPut.execute(tag2);

    itemTagPut.execute(new TagAddType.Parameters(id0, tag0));
    itemTagPut.execute(new TagAddType.Parameters(id0, tag1));
    itemTagPut.execute(new TagAddType.Parameters(id0, tag2));

    this.transaction.commit();

    {
      final var i = this.itemGet.execute(id0).orElseThrow();
      assertTrue(i.tags().contains(tag0));
      assertTrue(i.tags().contains(tag1));
      assertTrue(i.tags().contains(tag2));
    }

    {
      final var t = itemTagList.execute(id0);
      assertTrue(t.contains(tag0));
      assertTrue(t.contains(tag1));
      assertTrue(t.contains(tag2));
    }

    itemTagRemove.execute(new TagRemoveType.Parameters(id0, tag1));

    {
      final var i = this.itemGet.execute(id0).orElseThrow();
      assertTrue(i.tags().contains(tag0));
      assertFalse(i.tags().contains(tag1));
      assertTrue(i.tags().contains(tag2));
    }

    {
      final var t = itemTagList.execute(id0);
      assertTrue(t.contains(tag0));
      assertFalse(t.contains(tag1));
      assertTrue(t.contains(tag2));
    }
  }

  /**
   * Item attachment adjustments work.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemAttachments()
    throws Exception
  {
    final var id0 =
      CAItemID.random();

    this.itemCreate.execute(id0);

    final var fileAdd =
      this.transaction.queries(
        CADatabaseQueriesFilesType.PutType.class);
    final var itemAttachmentAdd =
      this.transaction.queries(
        AttachmentAddType.class);
    final var itemAttachmentRemove =
      this.transaction.queries(
        AttachmentRemoveType.class);
    final var itemAttachmentList =
      this.transaction.queries(
        AttachmentsGetType.class);

    final var file =
      new CAFileWithData(
        CAFileID.random(),
        "Description",
        "text/plain",
        1L,
        "SHA-256",
        "ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb",
        new CAByteArray("a".getBytes(StandardCharsets.UTF_8))
      );
    fileAdd.execute(file);

    itemAttachmentAdd.execute(
      new AttachmentAddType.Parameters(id0, file.id(), "misc"));

    {
      final var a =
        itemAttachmentList.execute(
          new AttachmentsGetType.Parameters(id0, false));

      assertEquals(
        Set.of(new CAItemAttachment(file.withoutData(), "misc")),
        a
      );
    }

    itemAttachmentRemove.execute(
      new AttachmentRemoveType.Parameters(id0, file.id(), "misc"));

    {
      final var a =
        itemAttachmentList.execute(
          new AttachmentsGetType.Parameters(id0, false));

      assertEquals(
        Set.of(),
        a
      );
    }
  }
}
