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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.MetadataPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.MetadataRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.SetNameType.Parameters;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.TypesAssignType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeDeclarationPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesUsersType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.model.CAItemColumn;
import com.io7m.cardant.model.CAItemColumnOrdering;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemLocationMatchType.CAItemLocationExact;
import com.io7m.cardant.model.CAItemLocationMatchType.CAItemLocationWithDescendants;
import com.io7m.cardant.model.CAItemLocationMatchType.CAItemLocationsAll;
import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAMetadataElementMatchType;
import com.io7m.cardant.model.CAMetadataElementMatchType.Specific;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CAMetadataValueMatchType.TextMatchType.ExactTextValue;
import com.io7m.cardant.model.CATypeDeclaration;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType;
import com.io7m.cardant.model.comparisons.CAComparisonSetType;
import com.io7m.cardant.tests.CATestDirectories;
import com.io7m.cardant.tests.containers.CATestContainers;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.test_extension.ErvillaCloseAfterClass;
import com.io7m.ervilla.test_extension.ErvillaConfiguration;
import com.io7m.ervilla.test_extension.ErvillaExtension;
import com.io7m.idstore.model.IdName;
import com.io7m.lanark.core.RDottedName;
import com.io7m.medrina.api.MSubject;
import com.io7m.zelador.test_extension.CloseableResourcesType;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;
import static com.io7m.cardant.model.CAMetadataValueMatchType.AnyValue.ANY_VALUE;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(projectName = "com.io7m.cardant", disabledIfUnsupported = true)
public final class CADatabaseItemsSearchTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CADatabaseItemsSearchTest.class);

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
  private TypeDeclarationPutType typePut;
  private TypesAssignType itemTypeAssign;

  @BeforeAll
  public static void setupOnce(
    final @ErvillaCloseAfterClass EContainerSupervisorType containers)
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

    final var userId = CAUserID.random();
    this.transaction.queries(CADatabaseQueriesUsersType.PutType.class)
      .execute(new CAUser(userId, new IdName("x"), new MSubject(Set.of())));
    this.transaction.commit();
    this.transaction.setUserId(userId);

    this.itemCreate =
      this.transaction.queries(CADatabaseQueriesItemsType.CreateType.class);
    this.itemGet =
      this.transaction.queries(CADatabaseQueriesItemsType.GetType.class);
    this.itemTypeAssign =
      this.transaction.queries(TypesAssignType.class);
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
    this.typePut =
      this.transaction.queries(TypeDeclarationPutType.class);
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
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );
    final var loc1 =
      new CALocation(
        CALocationID.random(),
        Optional.of(loc0.id()),
        "Loc1",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );
    final var loc2 =
      new CALocation(
        CALocationID.random(),
        Optional.of(loc1.id()),
        "Loc2",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
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
          new CAItemLocationWithDescendants(loc0.id()),
          new CAComparisonFuzzyType.Anything<>(),
          new CAComparisonFuzzyType.Anything<>(),
          new CAComparisonSetType.Anything<>(),
          CAMetadataElementMatchType.ANYTHING,
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
          new CAItemLocationWithDescendants(loc1.id()),
          new CAComparisonFuzzyType.Anything<>(),
          new CAComparisonFuzzyType.Anything<>(),
          new CAComparisonSetType.Anything<>(),
          CAMetadataElementMatchType.ANYTHING,
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
          new CAItemLocationWithDescendants(loc2.id()),
          new CAComparisonFuzzyType.Anything<>(),
          new CAComparisonFuzzyType.Anything<>(),
          new CAComparisonSetType.Anything<>(),
          CAMetadataElementMatchType.ANYTHING,
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
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );
    final var loc1 =
      new CALocation(
        CALocationID.random(),
        Optional.of(loc0.id()),
        "Loc1",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );
    final var loc2 =
      new CALocation(
        CALocationID.random(),
        Optional.of(loc1.id()),
        "Loc2",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
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
          new CAItemLocationExact(loc0.id()),
          new CAComparisonFuzzyType.Anything<>(),
          new CAComparisonFuzzyType.Anything<>(),
          new CAComparisonSetType.Anything<>(),
          CAMetadataElementMatchType.ANYTHING,
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
          new CAItemLocationExact(loc1.id()),
          new CAComparisonFuzzyType.Anything<>(),
          new CAComparisonFuzzyType.Anything<>(),
          new CAComparisonSetType.Anything<>(),
          CAMetadataElementMatchType.ANYTHING,
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
          new CAItemLocationExact(loc2.id()),
          new CAComparisonFuzzyType.Anything<>(),
          new CAComparisonFuzzyType.Anything<>(),
          new CAComparisonSetType.Anything<>(),
          CAMetadataElementMatchType.ANYTHING,
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
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );
    final var loc1 =
      new CALocation(
        CALocationID.random(),
        Optional.of(loc0.id()),
        "Loc1",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );
    final var loc2 =
      new CALocation(
        CALocationID.random(),
        Optional.of(loc1.id()),
        "Loc2",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
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
          new CAItemLocationsAll(),
          new CAComparisonFuzzyType.Anything<>(),
          new CAComparisonFuzzyType.Anything<>(),
          new CAComparisonSetType.Anything<>(),
          CAMetadataElementMatchType.ANYTHING,
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
   * Searching for items by full text search works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemSearchByNameSimilarTo(
    final @TempDir Path directory)
    throws Exception
  {
    final var itemIDs =
      this.populateItems(directory);

    final var search =
      this.searchQuery.execute(new CAItemSearchParameters(
        new CAItemLocationsAll(),
        new CAComparisonFuzzyType.IsSimilarTo<>("join"),
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonSetType.Anything<>(),
        CAMetadataElementMatchType.ANYTHING,
        new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
        100
      ));

    final var page = search.pageCurrent(this.transaction);
    assertEquals(3, page.items().size());

    final var received =
      page.items()
        .stream()
        .collect(Collectors.toMap(CAItemSummary::id, Function.identity()));

    for (final var itemSummary : received.values()) {
      assertTrue(itemSummary.name().contains("join"));
    }
  }

  /**
   * Searching for items by exact match works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemSearchByNameExact(
    final @TempDir Path directory)
    throws Exception
  {
    final var itemIDs =
      this.populateItems(directory);

    final var itemName =
      "Item method strip president character variety green committee repair couple desire";

    final var search =
      this.searchQuery.execute(new CAItemSearchParameters(
        new CAItemLocationsAll(),
        new CAComparisonFuzzyType.IsEqualTo<>(itemName),
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonSetType.Anything<>(),
        CAMetadataElementMatchType.ANYTHING,
        new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
        100
      ));

    final var page = search.pageCurrent(this.transaction);
    assertEquals(1, page.items().size());

    final var received =
      page.items()
        .stream()
        .collect(Collectors.toMap(CAItemSummary::id, Function.identity()));

    for (final var itemSummary : received.values()) {
      assertEquals(
        itemName,
        itemSummary.name()
      );
    }
  }

  /**
   * Searching for items by metadata works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemSearchByMetadata(
    final @TempDir Path directory)
    throws Exception
  {
    final var itemIDs =
      this.populateItems(directory);

    /*
     * Search for items that have the given metadata keys with
     * any values.
     */

    final var name0 = new RDottedName("e");
    final var name1 = new RDottedName("a");
    final var name2 = new RDottedName("i");

    final var search =
      this.searchQuery.execute(new CAItemSearchParameters(
        new CAItemLocationsAll(),
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonSetType.Anything<>(),
        new CAMetadataElementMatchType.And(
          new CAMetadataElementMatchType.And(
            new Specific(new CAComparisonFuzzyType.IsEqualTo<>(name0.value()), ANY_VALUE),
            new Specific(new CAComparisonFuzzyType.IsEqualTo<>(name1.value()), ANY_VALUE)
          ),
          new Specific(new CAComparisonFuzzyType.IsEqualTo<>(name2.value()), ANY_VALUE)
        ),
        new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
        100
      ));

    final var page = search.pageCurrent(this.transaction);
    assertEquals(11, page.items().size());

    for (final var summary : page.items()) {
      final var item =
        this.itemGet.execute(summary.id()).orElseThrow();

      assertTrue(item.metadata().containsKey(name0));
      assertTrue(item.metadata().containsKey(name1));
      assertTrue(item.metadata().containsKey(name2));
    }
  }

  /**
   * Searching for items by metadata works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemSearchByMetadataValue(
    final @TempDir Path directory)
    throws Exception
  {
    final var itemIDs =
      this.populateItems(directory);

    /*
     * Search for items that have the given metadata keys with
     * specific values.
     */

    final var name0 = new RDottedName("e");

    final var search =
      this.searchQuery.execute(new CAItemSearchParameters(
        new CAItemLocationsAll(),
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonSetType.Anything<>(),
        new Specific(new CAComparisonFuzzyType.IsEqualTo<>(name0.value()), new ExactTextValue("explanation")),
        new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
        100
      ));

    final var page = search.pageCurrent(this.transaction);
    assertEquals(2, page.items().size());

    for (final var summary : page.items()) {
      final var item =
        this.itemGet.execute(summary.id()).orElseThrow();

      assertTrue(item.metadata().containsKey(name0));
      assertEquals("explanation", item.metadata().get(name0).valueString());
    }
  }

  /**
   * Searching for items by types works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemSearchByTypesAny(
    final @TempDir Path directory)
    throws Exception
  {
    final var itemIDs =
      this.populateItems(directory);

    /*
     * Search for items that have any of the given types.
     */

    final var search =
      this.searchQuery.execute(new CAItemSearchParameters(
        new CAItemLocationsAll(),
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonSetType.IsOverlapping<>(
          Set.of(new RDottedName("t0"), new RDottedName("t1"))
        ),
        CAMetadataElementMatchType.ANYTHING,
        new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
        1000
      ));

    final var page = search.pageCurrent(this.transaction);
    assertEquals(171, page.items().size());

    for (final var summary : page.items()) {
      final var item =
        this.itemGet.execute(summary.id()).orElseThrow();

      final var t0present =
        item.types().contains(new RDottedName("t0"));
      final var t1present =
        item.types().contains(new RDottedName("t1"));

      assertTrue(t0present || t1present);
    }
  }

  /**
   * Searching for items by types works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemSearchByTypesAll(
    final @TempDir Path directory)
    throws Exception
  {
    final var itemIDs =
      this.populateItems(directory);

    /*
     * Search for items that have all the given types.
     */

    final var search =
      this.searchQuery.execute(new CAItemSearchParameters(
        new CAItemLocationsAll(),
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonSetType.IsEqualTo<>(
          Set.of(new RDottedName("t0"), new RDottedName("t1"))
        ),
        CAMetadataElementMatchType.ANYTHING,
        new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
        100
      ));

    final var page = search.pageCurrent(this.transaction);
    assertEquals(11, page.items().size());

    for (final var summary : page.items()) {
      final var item =
        this.itemGet.execute(summary.id()).orElseThrow();

      final var t0present =
        item.types().contains(new RDottedName("t0"));
      final var t1present =
        item.types().contains(new RDottedName("t1"));

      assertTrue(t0present && t1present);
    }
  }

  private List<CAItemID> populateItems(
    final Path directory)
    throws IOException, CADatabaseException
  {
    final var nouns =
      Files.lines(
        CATestDirectories.resourceOf(
          CADatabaseItemsSearchTest.class,
          directory,
          "nouns.txt"
        )).toList();

    final var items = new ArrayList<CAItemID>();
    final var rng = new Random(1000L);

    final var type0 =
      new CATypeDeclaration(
        new RDottedName("t0"), "A type.", Map.of());
    final var type1 =
      new CATypeDeclaration(
        new RDottedName("t1"), "A type.", Map.of());

    this.typePut.execute(type0);
    this.typePut.execute(type1);

    for (int index = 0; index < 500; ++index) {
      final var thisName = new ArrayList<String>();
      thisName.add("Item");
      for (int count = 0; count < 10; ++count) {
        thisName.add(nouns.get(rng.nextInt(nouns.size())));
      }
      final var itemName = String.join(" ", thisName);
      LOG.debug("[{}] {}", Integer.valueOf(index), itemName);

      final var itemId = CAItemID.random();
      this.itemCreate.execute(itemId);
      this.setName.execute(new Parameters(itemId, itemName));

      /*
       * Create some metadata.
       */

      final var meta = new HashSet<CAMetadataType>();
      for (int metaIndex = 1; metaIndex < 10; ++metaIndex) {
        final var metaValue =
          thisName.get(metaIndex);
        final var metaName =
          new RDottedName(metaValue.substring(0, 1));
        meta.add(new CAMetadataType.Text(metaName, metaValue));

        /*
         * Assign some types.
         */

        if (metaValue.toUpperCase().startsWith("O")) {
          this.itemTypeAssign.execute(new TypesAssignType.Parameters(
            itemId,
            Set.of(type0.name())
          ));
        }

        if (metaValue.toUpperCase().startsWith("I")) {
          this.itemTypeAssign.execute(new TypesAssignType.Parameters(
            itemId,
            Set.of(type1.name())
          ));
        }
      }

      this.metaAdd.execute(new MetadataPutType.Parameters(itemId, meta));
      items.add(itemId);
    }

    this.transaction.commit();
    return List.copyOf(items);
  }
}
