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
import com.io7m.cardant.model.CALocationPath;
import com.io7m.cardant.model.CALocationSummary;
import com.io7m.cardant.model.CAStockInstanceID;
import com.io7m.cardant.model.CAStockOccurrenceKind;
import com.io7m.cardant.model.CAStockOccurrenceSerial;
import com.io7m.cardant.model.CAStockOccurrenceSet;
import com.io7m.cardant.model.CAStockOccurrenceType;
import com.io7m.cardant.model.CAStockRepositSerialIntroduce;
import com.io7m.cardant.model.CAStockRepositSetIntroduce;
import com.io7m.cardant.model.CAStockSearchParameters;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.cardant.tests.containers.CAClockFixture;
import com.io7m.cardant.tests.containers.CADatabaseFixture;
import com.io7m.cardant.tests.containers.CAFixtures;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.test_extension.ErvillaCloseAfterSuite;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;
import static java.time.ZoneOffset.UTC;
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
      CALocationPath.singleton("Loc0"),
      now(),
      now(),
      Collections.emptySortedMap(),
      Collections.emptySortedMap(),
      Collections.emptySortedSet()
    );

  private static final CALocation L1 =
    new CALocation(
      CALocationID.random(),
      Optional.of(L0.id()),
      CALocationPath.singleton("Loc1"),
      now(),
      now(),
      Collections.emptySortedMap(),
      Collections.emptySortedMap(),
      Collections.emptySortedSet()
    );

  private static final CALocation L2 =
    new CALocation(
      CALocationID.random(),
      Optional.of(L1.id()),
      CALocationPath.singleton("Loc2"),
      now(),
      now(),
      Collections.emptySortedMap(),
      Collections.emptySortedMap(),
      Collections.emptySortedSet()
    );

  private static final long PAGE_SIZE =
    1000L;

  private static final List<CAStockInstanceID> STOCK_IDS =
    List.of(
      CAStockInstanceID.random(),
      CAStockInstanceID.random(),
      CAStockInstanceID.random(),
      CAStockInstanceID.random(),
      CAStockInstanceID.random(),
      CAStockInstanceID.random(),
      CAStockInstanceID.random(),
      CAStockInstanceID.random(),
      CAStockInstanceID.random(),
      CAStockInstanceID.random()
    );

  private static final RDottedName TYPE0 =
    new RDottedName("t");

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

    this.setAdd(STOCK_IDS.get(0), L0, item0, 20L);
    this.setAdd(STOCK_IDS.get(1), L0, item1, 5L);
    this.setAdd(STOCK_IDS.get(2), L0, item2, 30L);

    {
      final List<CAStockOccurrenceType> occ =
        this.executeSearch(search);

      assertEquals(
        new CAStockOccurrenceSet(
          STOCK_IDS.get(0),
          L0.summary(),
          new CAItemSummary(
            item0,
            "",
            now(),
            now()
          ),
          20L
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
  public void testStockSearchExactItemNot()
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

    this.setAdd(STOCK_IDS.get(0), L0, item0, 20L);
    this.setAdd(STOCK_IDS.get(1), L0, item1, 5L);
    this.setAdd(STOCK_IDS.get(2), L0, item2, 30L);

    {
      final List<CAStockOccurrenceType> occ =
        this.executeSearch(search);

      assertEquals(
        new CAStockOccurrenceSet(
          STOCK_IDS.get(2),
          L0.summary(),
          new CAItemSummary(
            item2,
            "",
            now(),
            now()
          ),
          30L
        ),
        occ.get(0)
      );
      assertEquals(
        new CAStockOccurrenceSet(
          STOCK_IDS.get(1),
          L0.summary(),
          new CAItemSummary(
            item1,
            "",
            now(),
            now()
          ),
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
  public void testStockSearchSetOnly()
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
        Set.of(CAStockOccurrenceKind.SET),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        PAGE_SIZE
      );

    this.locPut.execute(L0);

    this.setAdd(STOCK_IDS.get(0), L0, item0, 20L);
    this.serialAdd(STOCK_IDS.get(1), L0, item0, "A");

    {
      final List<CAStockOccurrenceType> occ =
        this.executeSearch(search);

      assertEquals(
        new CAStockOccurrenceSet(
          STOCK_IDS.get(0),
          L0.summary(),
          new CAItemSummary(
            item0,
            "",
            now(),
            now()
          ),
          20L
        ),
        occ.get(0)
      );
      assertEquals(1, occ.size());
    }
  }

  private static OffsetDateTime now()
  {
    return OffsetDateTime.now(CAClockFixture.get());
  }

  /**
   * Searching for items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockSearchSerialOnly()
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
        Set.of(CAStockOccurrenceKind.SERIAL),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        PAGE_SIZE
      );

    this.locPut.execute(L0);

    this.setAdd(STOCK_IDS.get(0), L0, item0, 20L);
    this.serialAdd(STOCK_IDS.get(1), L0, item0, "A");

    {
      final List<CAStockOccurrenceType> occ =
        this.executeSearch(search);

      assertEquals(
        new CAStockOccurrenceSerial(
          STOCK_IDS.get(1),
          L0.summary(),
          new CAItemSummary(
            item0,
            "",
            now(),
            now()
          ),
          List.of(new CAItemSerial(TYPE0, "A"))
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
  public void testStockSearchLocationExact()
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
        new CALocationMatchType.CALocationExact(L1.id()),
        new CAComparisonExactType.Anything<>(),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        PAGE_SIZE
      );

    this.locPut.execute(L0);
    this.locPut.execute(L1);
    this.locPut.execute(L2);

    this.setAdd(STOCK_IDS.get(0), L0, item0, 20L);
    this.setAdd(STOCK_IDS.get(1), L1, item1, 5L);
    this.setAdd(STOCK_IDS.get(2), L2, item2, 30L);

    {
      final List<CAStockOccurrenceType> occ =
        this.executeSearch(search);

      assertEquals(
        new CAStockOccurrenceSet(
          STOCK_IDS.get(1),
          new CALocationSummary(
            L1.id(),
            Optional.of(L0.id()),
            CALocationPath.ofArray(new String[]{
              "Loc0",
              "Loc1"
            }),
            now(),
            now()
          ),
          new CAItemSummary(
            item1,
            "",
            now(),
            now()
          ),
          5
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
        new CALocationMatchType.CALocationWithDescendants(L1.id()),
        new CAComparisonExactType.Anything<>(),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        PAGE_SIZE
      );

    this.locPut.execute(L0);
    this.locPut.execute(L1);
    this.locPut.execute(L2);

    this.setAdd(STOCK_IDS.get(0), L0, item0, 20L);
    this.setAdd(STOCK_IDS.get(1), L1, item1, 5L);
    this.setAdd(STOCK_IDS.get(2), L2, item2, 30L);

    {
      final List<CAStockOccurrenceType> occ =
        this.executeSearch(search);

      assertEquals(
        new CAStockOccurrenceSet(
          STOCK_IDS.get(2),
          new CALocationSummary(
            L2.id(),
            Optional.of(L1.id()),
            CALocationPath.ofArray(new String[]{
              "Loc0",
              "Loc1",
              "Loc2"
            }),
            now(),
            now()
          ),
          new CAItemSummary(
            item2,
            "",
            now(),
            now()
          ),
          30L
        ),
        occ.get(0)
      );

      assertEquals(
        new CAStockOccurrenceSet(
          STOCK_IDS.get(1),
          new CALocationSummary(
            L1.id(),
            Optional.of(L0.id()),
            CALocationPath.ofArray(new String[]{
              "Loc0",
              "Loc1"
            }),
            now(),
            now()
          ),
          new CAItemSummary(
            item1,
            "",
            now(),
            now()
          ),
          5
        ),
        occ.get(1)
      );

      assertEquals(2, occ.size());
    }
  }

  private CADatabaseUnit serialAdd(
    final CAStockInstanceID stock,
    final CALocation location,
    final CAItemID item,
    final String serial)
    throws CADatabaseException
  {
    return this.stockReposit.execute(
      new CAStockRepositSerialIntroduce(
        stock,
        item,
        location.id(),
        new CAItemSerial(TYPE0, serial)
      )
    );
  }

  private void setAdd(
    final CAStockInstanceID stock,
    final CALocation location,
    final CAItemID item,
    final long count)
    throws CADatabaseException
  {
    this.stockReposit.execute(
      new CAStockRepositSetIntroduce(
        stock,
        item,
        location.id(),
        count
      )
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
