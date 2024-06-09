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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemCreateType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemDeleteMarkOnlyType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemDeleteType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemGetType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesStockType.StockCountType;
import com.io7m.cardant.database.api.CADatabaseQueriesStockType.StockRepositType;
import com.io7m.cardant.database.api.CADatabaseQueriesStockType.StockSearchType;
import com.io7m.cardant.database.api.CADatabaseQueriesUsersType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.model.CAIncludeDeleted;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemSerial;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationMatchType;
import com.io7m.cardant.model.CAStockOccurrenceKind;
import com.io7m.cardant.model.CAStockOccurrenceSerial;
import com.io7m.cardant.model.CAStockOccurrenceSet;
import com.io7m.cardant.model.CAStockOccurrenceType;
import com.io7m.cardant.model.CAStockRepositSerialAdd;
import com.io7m.cardant.model.CAStockRepositSetAdd;
import com.io7m.cardant.model.CAStockSearchParameters;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.cardant.tests.containers.CADatabaseFixture;
import com.io7m.cardant.tests.containers.CAFixtures;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.test_extension.ErvillaCloseAfterSuite;
import com.io7m.ervilla.test_extension.ErvillaConfiguration;
import com.io7m.ervilla.test_extension.ErvillaExtension;
import com.io7m.idstore.model.IdName;
import com.io7m.medrina.api.MSubject;
import com.io7m.zelador.test_extension.CloseableResourcesType;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;
import static com.io7m.cardant.model.CAStockOccurrenceKind.SERIAL;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(projectName = "com.io7m.cardant", disabledIfUnsupported = true)
public final class CADatabaseStockSearchTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CADatabaseStockSearchTest.class);

  private static final CALocation L0 =
    new CALocation(
      CALocationID.random(),
      empty(),
      "Loc0",
      Collections.emptySortedMap(),
      Collections.emptySortedMap(),
      Collections.emptySortedSet()
    );

  private static final CALocation L1 =
    new CALocation(
      CALocationID.random(),
      Optional.of(L0.id()),
      "Loc1",
      Collections.emptySortedMap(),
      Collections.emptySortedMap(),
      Collections.emptySortedSet()
    );

  private static final CALocation L2 =
    new CALocation(
      CALocationID.random(),
      Optional.of(L1.id()),
      "Loc2",
      Collections.emptySortedMap(),
      Collections.emptySortedMap(),
      Collections.emptySortedSet()
    );

  private static final long PAGE_SIZE =
    1000L;

  private static CADatabaseFixture DATABASE_FIXTURE;
  private CADatabaseConnectionType connection;
  private CADatabaseTransactionType transaction;
  private CADatabaseType database;
  private ItemCreateType itemCreate;
  private ItemDeleteMarkOnlyType deleteMark;
  private LocationPutType locPut;
  private StockRepositType stockReposit;
  private StockSearchType stockSearch;
  private StockCountType stockCount;
  private ItemDeleteType delete;
  private ItemGetType itemGet;

  @BeforeAll
  public static void setupOnce(
    final @ErvillaCloseAfterSuite EContainerSupervisorType containers)
    throws Exception
  {
    DATABASE_FIXTURE =
      CAFixtures.database(CAFixtures.pod(containers));
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
      this.transaction.queries(ItemCreateType.class);
    this.itemGet =
      this.transaction.queries(ItemGetType.class);
    this.deleteMark =
      this.transaction.queries(ItemDeleteMarkOnlyType.class);
    this.delete =
      this.transaction.queries(ItemDeleteType.class);
    this.locPut =
      this.transaction.queries(LocationPutType.class);
    this.stockReposit =
      this.transaction.queries(StockRepositType.class);
    this.stockSearch =
      this.transaction.queries(StockSearchType.class);
    this.stockCount =
      this.transaction.queries(StockCountType.class);
  }

  /**
   * Searching for items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockSearchExactItem()
    throws Exception
  {
    final var item0 = CAItemID.random();
    final var item1 = CAItemID.random();
    final var item2 = CAItemID.random();

    this.itemCreate.execute(item0);
    this.itemCreate.execute(item1);
    this.itemCreate.execute(item2);

    final var search =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationsAll(),
        new CAComparisonExactType.IsEqualTo<>(item0),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        PAGE_SIZE
      );

    this.locPut.execute(L0);

    this.setAdd(L0, item0);
    this.setAdd(L0, item0);
    this.setAdd(L0, item0);
    this.setAdd(L0, item1);
    this.setAdd(L0, item1);
    this.setAdd(L0, item2);

    {
      final List<CAStockOccurrenceType> occ =
        this.executeSearch(search);

      assertEquals(
        new CAStockOccurrenceSet(
          L0.summary(),
          new CAItemSummary(item0, ""),
          15L
        ),
        occ.get(0)
      );
      assertEquals(1, occ.size());
    }
  }

  /**
   * Searching for items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockSearchExactNotItem()
    throws Exception
  {
    final var item0 = CAItemID.random();
    final var item1 = CAItemID.random();
    final var item2 = CAItemID.random();

    this.itemCreate.execute(item0);
    this.itemCreate.execute(item1);
    this.itemCreate.execute(item2);

    final var search =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationsAll(),
        new CAComparisonExactType.IsNotEqualTo<>(item0),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        PAGE_SIZE
      );

    this.locPut.execute(L0);

    this.setAdd(L0, item0);
    this.setAdd(L0, item0);
    this.setAdd(L0, item0);
    this.setAdd(L0, item1);
    this.setAdd(L0, item1);
    this.setAdd(L0, item2);

    {
      final List<CAStockOccurrenceType> occ =
        this.executeSearch(search);

      assertEquals(
        new CAStockOccurrenceSet(
          L0.summary(),
          new CAItemSummary(item1, ""),
          10L
        ),
        occ.get(0)
      );
      assertEquals(
        new CAStockOccurrenceSet(
          L0.summary(),
          new CAItemSummary(item2, ""),
          5L
        ),
        occ.get(1)
      );
      assertEquals(2, occ.size());
    }
  }

  /**
   * Searching for items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockSearchAnyItem()
    throws Exception
  {
    final var item0 = CAItemID.random();
    final var item1 = CAItemID.random();
    final var item2 = CAItemID.random();

    this.itemCreate.execute(item0);
    this.itemCreate.execute(item1);
    this.itemCreate.execute(item2);

    final var search =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationsAll(),
        new CAComparisonExactType.Anything<>(),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        PAGE_SIZE
      );

    this.locPut.execute(L0);

    this.setAdd(L0, item0);
    this.setAdd(L0, item0);
    this.setAdd(L0, item0);
    this.setAdd(L0, item1);
    this.setAdd(L0, item1);
    this.setAdd(L0, item2);

    {
      final List<CAStockOccurrenceType> occ =
        this.executeSearch(search);

      assertEquals(
        new CAStockOccurrenceSet(
          L0.summary(),
          new CAItemSummary(item0, ""),
          15L
        ),
        occ.get(0)
      );
      assertEquals(
        new CAStockOccurrenceSet(
          L0.summary(),
          new CAItemSummary(item1, ""),
          10L
        ),
        occ.get(1)
      );
      assertEquals(
        new CAStockOccurrenceSet(
          L0.summary(),
          new CAItemSummary(item2, ""),
          5L
        ),
        occ.get(2)
      );
      assertEquals(3, occ.size());
    }
  }

  /**
   * Searching for items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockSearchSerial()
    throws Exception
  {
    final var item0 = CAItemID.random();
    final var item1 = CAItemID.random();
    final var item2 = CAItemID.random();

    this.itemCreate.execute(item0);
    this.itemCreate.execute(item1);
    this.itemCreate.execute(item2);

    final var search =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationsAll(),
        new CAComparisonExactType.Anything<>(),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        PAGE_SIZE
      );

    this.locPut.execute(L0);

    this.setAdd(L0, item0);
    this.setAdd(L0, item0);
    this.setAdd(L0, item0);
    this.serialAdd(L0, item0, "A");
    this.setAdd(L0, item1);
    this.setAdd(L0, item1);
    this.serialAdd(L0, item1, "B");
    this.setAdd(L0, item2);

    this.transaction.commit();

    {
      final List<CAStockOccurrenceType> occ =
        this.executeSearch(search);

      assertEquals(
        new CAStockOccurrenceSet(
          L0.summary(),
          new CAItemSummary(item0, ""),
          15L
        ),
        occ.get(0)
      );
      assertEquals(
        new CAStockOccurrenceSet(
          L0.summary(),
          new CAItemSummary(item1, ""),
          10L
        ),
        occ.get(1)
      );
      assertEquals(
        new CAStockOccurrenceSet(
          L0.summary(),
          new CAItemSummary(item2, ""),
          5L
        ),
        occ.get(2)
      );
      assertEquals(
        new CAStockOccurrenceSerial(
          L0.summary(),
          new CAItemSummary(item0, ""),
          new CAItemSerial("A")
        ),
        occ.get(3)
      );
      assertEquals(
        new CAStockOccurrenceSerial(
          L0.summary(),
          new CAItemSummary(item1, ""),
          new CAItemSerial("B")
        ),
        occ.get(4)
      );
      assertEquals(5, occ.size());
    }
  }

  /**
   * Searching for items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockSearchSerialExact()
    throws Exception
  {
    final var item0 = CAItemID.random();
    final var item1 = CAItemID.random();
    final var item2 = CAItemID.random();

    this.itemCreate.execute(item0);
    this.itemCreate.execute(item1);
    this.itemCreate.execute(item2);

    final var search =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationsAll(),
        new CAComparisonExactType.Anything<>(),
        Set.of(SERIAL),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        PAGE_SIZE
      );

    this.locPut.execute(L0);

    this.setAdd(L0, item0);
    this.setAdd(L0, item0);
    this.setAdd(L0, item0);
    this.serialAdd(L0, item0, "A");
    this.setAdd(L0, item1);
    this.setAdd(L0, item1);
    this.serialAdd(L0, item1, "B");
    this.setAdd(L0, item2);

    this.transaction.commit();

    {
      final List<CAStockOccurrenceType> occ =
        this.executeSearch(search);

      assertEquals(
        new CAStockOccurrenceSerial(
          L0.summary(),
          new CAItemSummary(item0, ""),
          new CAItemSerial("A")
        ),
        occ.get(0)
      );
      assertEquals(
        new CAStockOccurrenceSerial(
          L0.summary(),
          new CAItemSummary(item1, ""),
          new CAItemSerial("B")
        ),
        occ.get(1)
      );
      assertEquals(2, occ.size());
    }
  }

  /**
   * Searching for items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockSearchLocationDescendants()
    throws Exception
  {
    final var item0 = CAItemID.random();
    final var item1 = CAItemID.random();
    final var item2 = CAItemID.random();

    this.itemCreate.execute(item0);
    this.itemCreate.execute(item1);
    this.itemCreate.execute(item2);

    final var search =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationWithDescendants(L0.id()),
        new CAComparisonExactType.Anything<>(),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        PAGE_SIZE
      );

    this.locPut.execute(L0);
    this.locPut.execute(L1);
    this.locPut.execute(L2);

    this.setAdd(L2, item0);
    this.setAdd(L2, item0);
    this.setAdd(L2, item0);

    this.transaction.commit();

    {
      final List<CAStockOccurrenceType> occ =
        this.executeSearch(search);

      assertEquals(
        new CAStockOccurrenceSet(
          L2.summary(),
          new CAItemSummary(item0, ""),
          15L
        ),
        occ.get(0)
      );
      assertEquals(1, occ.size());
    }
  }

  private CADatabaseUnit serialAdd(
    final CALocation location,
    final CAItemID item,
    final String serial)
    throws CADatabaseException
  {
    return this.stockReposit.execute(
      new CAStockRepositSerialAdd(item, location.id(), new CAItemSerial(serial))
    );
  }

  private void setAdd(
    final CALocation location,
    final CAItemID item)
    throws CADatabaseException
  {
    this.stockReposit.execute(
      new CAStockRepositSetAdd(item, location.id(), 5L)
    );
  }

  private List<CAStockOccurrenceType> executeSearch(
    final CAStockSearchParameters search)
    throws CADatabaseException
  {
    final var dbSearch =
      this.stockSearch.execute(search);

    var page = dbSearch.pageCurrent(this.transaction);
    final var results = new ArrayList<CAStockOccurrenceType>();
    while (true) {
      results.addAll(page.items());
      if (page.pageIndex() == page.pageCount()) {
        break;
      }
      page = dbSearch.pageNext(this.transaction);
    }
    return results;
  }
}
