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
import com.io7m.cardant.model.CAIncludeDeleted;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemSerial;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationMatchType;
import com.io7m.cardant.model.CAStockOccurrenceKind;
import com.io7m.cardant.model.CAStockRepositSerialAdd;
import com.io7m.cardant.model.CAStockRepositSerialMove;
import com.io7m.cardant.model.CAStockRepositSerialRemove;
import com.io7m.cardant.model.CAStockRepositSetAdd;
import com.io7m.cardant.model.CAStockRepositSetMove;
import com.io7m.cardant.model.CAStockRepositSetRemove;
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

import java.util.Collections;
import java.util.Set;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorItemStillInLocation;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorRemoveIdentifiedItems;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorRemoveTooManyItems;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(projectName = "com.io7m.cardant", disabledIfUnsupported = true)
public final class CADatabaseStockTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CADatabaseStockTest.class);

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
   * Adding items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSetAdd()
    throws Exception
  {
    final var itemId = CAItemID.random();
    this.itemCreate.execute(itemId);

    final var search =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationsAll(),
        new CAComparisonExactType.IsEqualTo<>(itemId),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        0L
      );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(0L, c);
    }

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        empty(),
        "Loc0",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);
    this.stockReposit.execute(
      new CAStockRepositSetAdd(itemId, loc0.id(), 100L)
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(100L, c);
    }

    this.stockReposit.execute(
      new CAStockRepositSetAdd(itemId, loc0.id(), 100L)
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(200L, c);
    }
  }

  /**
   * Removing items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSetRemove()
    throws Exception
  {
    final var itemId = CAItemID.random();
    this.itemCreate.execute(itemId);

    final var search =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationsAll(),
        new CAComparisonExactType.IsEqualTo<>(itemId),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        0L
      );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(0L, c);
    }

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        empty(),
        "Loc0",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);
    this.stockReposit.execute(
      new CAStockRepositSetAdd(itemId, loc0.id(), 100L)
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(100L, c);
    }

    this.stockReposit.execute(
      new CAStockRepositSetRemove(itemId, loc0.id(), 99L)
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(1L, c);
    }

    this.stockReposit.execute(
      new CAStockRepositSetRemove(itemId, loc0.id(), 1L)
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(0L, c);
    }
  }

  /**
   * It's not possible to remove too many items.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSetRemoveTooMany0()
    throws Exception
  {
    final var itemId = CAItemID.random();
    this.itemCreate.execute(itemId);

    final var search =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationsAll(),
        new CAComparisonExactType.IsEqualTo<>(itemId),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        0L
      );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(0L, c);
    }

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        empty(),
        "Loc0",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);
    this.stockReposit.execute(
      new CAStockRepositSetAdd(itemId, loc0.id(), 100L)
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(100L, c);
    }

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.stockReposit.execute(
          new CAStockRepositSetRemove(itemId, loc0.id(), 200L)
        );
      });

    assertEquals(errorRemoveTooManyItems(), ex.errorCode());
  }

  /**
   * It's not possible to remove too many items.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSetRemoveTooMany1()
    throws Exception
  {
    final var itemId = CAItemID.random();
    this.itemCreate.execute(itemId);

    final var search =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationsAll(),
        new CAComparisonExactType.IsEqualTo<>(itemId),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        0L
      );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(0L, c);
    }

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        empty(),
        "Loc0",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);

    this.stockReposit.execute(
      new CAStockRepositSetAdd(itemId, loc0.id(), 100L));
    this.stockReposit.execute(
      new CAStockRepositSetRemove(itemId, loc0.id(), 100L));

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.stockReposit.execute(
          new CAStockRepositSetRemove(itemId, loc0.id(), 1L));
      });

    assertEquals(errorRemoveTooManyItems(), ex.errorCode());
  }

  /**
   * Moving items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSetMove()
    throws Exception
  {
    final var itemId = CAItemID.random();
    this.itemCreate.execute(itemId);

    final var search0 =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationsAll(),
        new CAComparisonExactType.IsEqualTo<>(itemId),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        0L
      );

    {
      final var c = this.stockCount.execute(search0);
      assertEquals(0L, c);
    }

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
        empty(),
        "Loc1",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);
    this.locPut.execute(loc1);

    final var searchLoc0 =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationExact(loc0.id()),
        new CAComparisonExactType.IsEqualTo<>(itemId),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        0L
      );

    final var searchLoc1 =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationExact(loc1.id()),
        new CAComparisonExactType.IsEqualTo<>(itemId),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        0L
      );

    this.stockReposit.execute(
      new CAStockRepositSetAdd(itemId, loc0.id(), 100L));

    {
      final var c = this.stockCount.execute(searchLoc0);
      assertEquals(100L, c);
    }

    {
      final var c = this.stockCount.execute(searchLoc1);
      assertEquals(0L, c);
    }

    this.stockReposit.execute(
      new CAStockRepositSetMove(itemId, loc0.id(), loc1.id(), 50L));

    {
      final var c = this.stockCount.execute(searchLoc0);
      assertEquals(50L, c);
    }

    {
      final var c = this.stockCount.execute(searchLoc1);
      assertEquals(50L, c);
    }
  }

  /**
   * It's not possible to move more items than exist.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSetMoveTooMany()
    throws Exception
  {
    final var itemId = CAItemID.random();
    this.itemCreate.execute(itemId);

    final var search0 =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationsAll(),
        new CAComparisonExactType.IsEqualTo<>(itemId),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        0L
      );

    {
      final var c = this.stockCount.execute(search0);
      assertEquals(0L, c);
    }

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
        empty(),
        "Loc1",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);
    this.locPut.execute(loc1);

    this.stockReposit.execute(
      new CAStockRepositSetAdd(itemId, loc0.id(), 100L)
    );

    {
      final var c = this.stockCount.execute(search0);
      assertEquals(100L, c);
    }

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.stockReposit.execute(
          new CAStockRepositSetMove(
            itemId, loc0.id(), loc1.id(), 101L)
        );
      });

    assertEquals(errorRemoveTooManyItems(), ex.errorCode());
  }

  /**
   * Adding items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSerialAdd()
    throws Exception
  {
    final var itemId = CAItemID.random();
    this.itemCreate.execute(itemId);

    final var search0 =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationsAll(),
        new CAComparisonExactType.IsEqualTo<>(itemId),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        0L
      );

    {
      final var c = this.stockCount.execute(search0);
      assertEquals(0L, c);
    }

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        empty(),
        "Loc0",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);
    this.stockReposit.execute(
      new CAStockRepositSerialAdd(
        itemId, loc0.id(), new CAItemSerial("A")
      )
    );

    this.transaction.commit();

    {
      final var c = this.stockCount.execute(search0);
      assertEquals(1L, c);
    }
  }

  /**
   * Removing items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSerialRemove()
    throws Exception
  {
    final var itemId = CAItemID.random();
    this.itemCreate.execute(itemId);

    final var search0 =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationsAll(),
        new CAComparisonExactType.IsEqualTo<>(itemId),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        0L
      );

    {
      final var c = this.stockCount.execute(search0);
      assertEquals(0L, c);
    }

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        empty(),
        "Loc0",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);

    final var serial = new CAItemSerial("A");
    this.stockReposit.execute(
      new CAStockRepositSerialAdd(itemId, loc0.id(), serial));

    {
      final var c = this.stockCount.execute(search0);
      assertEquals(1L, c);
    }

    this.stockReposit.execute(
      new CAStockRepositSerialRemove(itemId, loc0.id(), serial));

    {
      final var c = this.stockCount.execute(search0);
      assertEquals(0L, c);
    }

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.stockReposit.execute(
          new CAStockRepositSerialRemove(itemId, loc0.id(), serial));
      });

    assertEquals(errorNonexistent(), ex.errorCode());
  }

  /**
   * Removing items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSerialRemoveNotSet()
    throws Exception
  {
    final var itemId = CAItemID.random();
    this.itemCreate.execute(itemId);

    final var search0 =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationsAll(),
        new CAComparisonExactType.IsEqualTo<>(itemId),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        0L
      );

    {
      final var c = this.stockCount.execute(search0);
      assertEquals(0L, c);
    }

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        empty(),
        "Loc0",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);
    this.stockReposit.execute(
      new CAStockRepositSerialAdd(
        itemId, loc0.id(), new CAItemSerial("A"))
    );

    {
      final var c = this.stockCount.execute(search0);
      assertEquals(1L, c);
    }

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.stockReposit.execute(
          new CAStockRepositSetRemove(itemId, loc0.id(), 1L));
      });

    assertEquals(errorRemoveIdentifiedItems(), ex.errorCode());
  }

  /**
   * Moving items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSerialMove()
    throws Exception
  {
    final var itemId = CAItemID.random();
    this.itemCreate.execute(itemId);

    final var search0 =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationsAll(),
        new CAComparisonExactType.IsEqualTo<>(itemId),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        0L
      );

    {
      final var c = this.stockCount.execute(search0);
      assertEquals(0L, c);
    }

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
        empty(),
        "Loc1",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    final var searchLoc0 =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationExact(loc0.id()),
        new CAComparisonExactType.IsEqualTo<>(itemId),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        0L
      );

    final var searchLoc1 =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationExact(loc1.id()),
        new CAComparisonExactType.IsEqualTo<>(itemId),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        0L
      );

    this.locPut.execute(loc0);
    this.locPut.execute(loc1);

    final var serial =
      new CAItemSerial("A");

    this.stockReposit.execute(
      new CAStockRepositSerialAdd(itemId, loc0.id(), serial));

    {
      final var c = this.stockCount.execute(searchLoc0);
      assertEquals(1L, c);
    }

    {
      final var c = this.stockCount.execute(searchLoc1);
      assertEquals(0L, c);
    }

    this.stockReposit.execute(
      new CAStockRepositSerialMove(itemId, loc0.id(), loc1.id(), serial));

    final var item_2 = this.itemGet.execute(itemId).orElseThrow();

    {
      final var c = this.stockCount.execute(searchLoc0);
      assertEquals(0L, c);
    }

    {
      final var c = this.stockCount.execute(searchLoc1);
      assertEquals(1L, c);
    }
  }

  /**
   * Removing items works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemRemoveReferenced0()
    throws Exception
  {
    final var itemId = CAItemID.random();
    this.itemCreate.execute(itemId);

    final var search0 =
      new CAStockSearchParameters(
        new CALocationMatchType.CALocationsAll(),
        new CAComparisonExactType.IsEqualTo<>(itemId),
        CAStockOccurrenceKind.all(),
        CAIncludeDeleted.INCLUDE_ONLY_LIVE,
        0L
      );

    {
      final var c = this.stockCount.execute(search0);
      assertEquals(0L, c);
    }

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        empty(),
        "Loc0",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);
    this.stockReposit.execute(
      new CAStockRepositSerialAdd(
        itemId, loc0.id(), new CAItemSerial("A")
      )
    );

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.delete.execute(Set.of(itemId));
      });
    assertEquals(errorItemStillInLocation(), ex.errorCode());
  }
}
