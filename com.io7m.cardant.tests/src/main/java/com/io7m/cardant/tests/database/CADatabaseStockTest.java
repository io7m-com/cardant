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
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAIncludeDeleted;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemSerial;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationMatchType;
import com.io7m.cardant.model.CALocationPath;
import com.io7m.cardant.model.CAStockInstanceID;
import com.io7m.cardant.model.CAStockOccurrenceKind;
import com.io7m.cardant.model.CAStockRepositRemove;
import com.io7m.cardant.model.CAStockRepositSerialIntroduce;
import com.io7m.cardant.model.CAStockRepositSerialMove;
import com.io7m.cardant.model.CAStockRepositSerialNumberAdd;
import com.io7m.cardant.model.CAStockRepositSerialNumberRemove;
import com.io7m.cardant.model.CAStockRepositSetAdd;
import com.io7m.cardant.model.CAStockRepositSetIntroduce;
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
import java.util.Collections;
import java.util.Set;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorItemStillInLocation;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorRemoveTooManyItems;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorStockIsNotSerial;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorStockIsNotSet;
import static java.time.ZoneOffset.UTC;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(projectName = "com.io7m.cardant", disabledIfUnsupported = true)
public final class CADatabaseStockTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CADatabaseStockTest.class);

  private static final RDottedName TYPE0 =
    new RDottedName("com.io7m.ex");

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
   * Introducing stock works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSetIntroduce()
    throws Exception
  {
    final var instance0 =
      CAStockInstanceID.random();
    final var instance1 =
      CAStockInstanceID.random();

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
        CALocationPath.singleton("Loc0"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);
    this.stockReposit.execute(
      new CAStockRepositSetIntroduce(
        instance0,
        itemId,
        loc0.id(),
        100L
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(100L, c);
    }

    this.stockReposit.execute(
      new CAStockRepositSetIntroduce(
        instance1,
        itemId,
        loc0.id(),
        100L
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(200L, c);
    }
  }

  /**
   * Stock can only be introduced once.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSetIntroduceNoDouble()
    throws Exception
  {
    final var instance0 =
      CAStockInstanceID.random();

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
        CALocationPath.singleton("Loc0"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);
    this.stockReposit.execute(
      new CAStockRepositSetIntroduce(
        instance0,
        itemId,
        loc0.id(),
        100L
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(100L, c);
    }

    this.transaction.commit();

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.stockReposit.execute(
          new CAStockRepositSetIntroduce(
            instance0,
            itemId,
            loc0.id(),
            100L
          )
        );
      });

    assertEquals(CAStandardErrorCodes.errorDuplicate(), ex.errorCode());

    {
      final var c = this.stockCount.execute(search);
      assertEquals(100L, c);
    }
  }

  /**
   * Adding stock works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSetAdd()
    throws Exception
  {
    final var instance0 =
      CAStockInstanceID.random();

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
        CALocationPath.singleton("Loc0"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);
    this.stockReposit.execute(
      new CAStockRepositSetIntroduce(
        instance0,
        itemId,
        loc0.id(),
        100L
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(100L, c);
    }

    this.stockReposit.execute(
      new CAStockRepositSetAdd(
        instance0,
        100L
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(200L, c);
    }
  }

  /**
   * Removing stock works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSetAddRemove()
    throws Exception
  {
    final var instance0 =
      CAStockInstanceID.random();

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
        CALocationPath.singleton("Loc0"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);

    this.stockReposit.execute(
      new CAStockRepositSetIntroduce(
        instance0,
        itemId,
        loc0.id(),
        100L
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(100L, c);
    }

    this.stockReposit.execute(
      new CAStockRepositSetRemove(
        instance0,
        50L
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(50L, c);
    }

    this.transaction.commit();

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.stockReposit.execute(
          new CAStockRepositSetRemove(
            instance0,
            51L
          )
        );
      });
    assertEquals(errorRemoveTooManyItems(), ex.errorCode());

    {
      final var c = this.stockCount.execute(search);
      assertEquals(50L, c);
    }

    this.stockReposit.execute(
      new CAStockRepositSetRemove(
        instance0,
        50L
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(0L, c);
    }
  }

  /**
   * Adding stock does not work if it has not been introduced.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSetAddNonexistent()
    throws Exception
  {
    final var instance0 =
      CAStockInstanceID.random();

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
        CALocationPath.singleton("Loc0"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.stockReposit.execute(
          new CAStockRepositSetAdd(
            instance0,
            100L
          )
        );
      });
    assertEquals(errorNonexistent(), ex.errorCode());
  }

  /**
   * Removing stock does not work if it has not been introduced.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSetRemoveNonexistent()
    throws Exception
  {
    final var instance0 =
      CAStockInstanceID.random();

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
        CALocationPath.singleton("Loc0"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.stockReposit.execute(
          new CAStockRepositSetRemove(
            instance0,
            100L
          )
        );
      });
    assertEquals(errorNonexistent(), ex.errorCode());
  }

  /**
   * Moving stock works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSetMove()
    throws Exception
  {
    final var instance0 =
      CAStockInstanceID.random();
    final var instance1 =
      CAStockInstanceID.random();

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
        CALocationPath.singleton("Loc0"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    final var loc1 =
      new CALocation(
        CALocationID.random(),
        empty(),
        CALocationPath.singleton("Loc1"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);
    this.locPut.execute(loc1);

    this.stockReposit.execute(
      new CAStockRepositSetIntroduce(
        instance0,
        itemId,
        loc0.id(),
        100L
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(100L, c);
    }

    this.stockReposit.execute(
      new CAStockRepositSetMove(
        instance0,
        instance1,
        loc1.id(),
        50L
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(100L, c);
    }

    this.stockReposit.execute(
      new CAStockRepositSetMove(
        instance0,
        instance1,
        loc1.id(),
        50L
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(100L, c);
    }
  }

  /**
   * Moving stock fails if the source does not exist.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSetMoveSourceNonexistent()
    throws Exception
  {
    final var instance0 =
      CAStockInstanceID.random();
    final var instance1 =
      CAStockInstanceID.random();

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
        CALocationPath.singleton("Loc0"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    final var loc1 =
      new CALocation(
        CALocationID.random(),
        empty(),
        CALocationPath.singleton("Loc1"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);
    this.locPut.execute(loc1);

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.stockReposit.execute(
          new CAStockRepositSetMove(
            instance0,
            instance1,
            loc1.id(),
            50L
          )
        );
      });
    assertEquals(errorNonexistent(), ex.errorCode());
  }

  /**
   * Moving stock fails if the target is a serial instance.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSetMoveTargetSerial()
    throws Exception
  {
    final var instance0 =
      CAStockInstanceID.random();
    final var instance1 =
      CAStockInstanceID.random();

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
        CALocationPath.singleton("Loc0"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    final var loc1 =
      new CALocation(
        CALocationID.random(),
        empty(),
        CALocationPath.singleton("Loc1"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);
    this.locPut.execute(loc1);

    this.stockReposit.execute(
      new CAStockRepositSetIntroduce(
        instance0,
        itemId,
        loc0.id(),
        100L
      )
    );

    this.stockReposit.execute(
      new CAStockRepositSerialIntroduce(
        instance1,
        itemId,
        loc1.id(),
        new CAItemSerial(TYPE0, "A")
      )
    );

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.stockReposit.execute(
          new CAStockRepositSetMove(
            instance0,
            instance1,
            loc1.id(),
            50L
          )
        );
      });
    assertEquals(errorStockIsNotSet(), ex.errorCode());
  }

  /**
   * Moving stock fails if the source is a serial instance.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSetMoveSourceSerial()
    throws Exception
  {
    final var instance0 =
      CAStockInstanceID.random();
    final var instance1 =
      CAStockInstanceID.random();

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
        CALocationPath.singleton("Loc0"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    final var loc1 =
      new CALocation(
        CALocationID.random(),
        empty(),
        CALocationPath.singleton("Loc1"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);
    this.locPut.execute(loc1);

    this.stockReposit.execute(
      new CAStockRepositSerialIntroduce(
        instance0,
        itemId,
        loc0.id(),
        new CAItemSerial(TYPE0, "A")
      )
    );

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.stockReposit.execute(
          new CAStockRepositSetMove(
            instance0,
            instance1,
            loc1.id(),
            50L
          )
        );
      });
    assertEquals(errorStockIsNotSet(), ex.errorCode());
  }

  /**
   * An item cannot be deleted while stock of it still exists.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemRemoveReferenced0()
    throws Exception
  {
    final var instance0 =
      CAStockInstanceID.random();

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
        CALocationPath.singleton("Loc0"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);
    this.stockReposit.execute(
      new CAStockRepositSetIntroduce(
        instance0,
        itemId,
        loc0.id(),
        1000L
      )
    );

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.delete.execute(Set.of(itemId));
      });
    assertEquals(errorItemStillInLocation(), ex.errorCode());
  }

  /**
   * Introducing stock works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSerialIntroduce()
    throws Exception
  {
    final var instance0 =
      CAStockInstanceID.random();

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
        CALocationPath.singleton("Loc0"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);

    this.stockReposit.execute(
      new CAStockRepositSerialIntroduce(
        instance0,
        itemId,
        loc0.id(),
        new CAItemSerial(TYPE0, "A")
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(1L, c);
    }
  }

  /**
   * Adding serial numbers works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSerialNumberAdd()
    throws Exception
  {
    final var instance0 =
      CAStockInstanceID.random();

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
        CALocationPath.singleton("Loc0"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);

    this.stockReposit.execute(
      new CAStockRepositSerialIntroduce(
        instance0,
        itemId,
        loc0.id(),
        new CAItemSerial(TYPE0, "A")
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(1L, c);
    }

    this.stockReposit.execute(
      new CAStockRepositSerialNumberAdd(
        instance0,
        new CAItemSerial(TYPE0, "B")
      )
    );

    this.stockReposit.execute(
      new CAStockRepositSerialNumberAdd(
        instance0,
        new CAItemSerial(TYPE0, "C")
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(1L, c);
    }
  }

  /**
   * Removing serial numbers works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSerialNumberAddRemove()
    throws Exception
  {
    final var instance0 =
      CAStockInstanceID.random();

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
        CALocationPath.singleton("Loc0"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);

    this.stockReposit.execute(
      new CAStockRepositSerialIntroduce(
        instance0,
        itemId,
        loc0.id(),
        new CAItemSerial(TYPE0, "A")
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(1L, c);
    }

    this.stockReposit.execute(
      new CAStockRepositSerialNumberAdd(
        instance0,
        new CAItemSerial(TYPE0, "B")
      )
    );

    this.stockReposit.execute(
      new CAStockRepositSerialNumberAdd(
        instance0,
        new CAItemSerial(TYPE0, "C")
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(1L, c);
    }

    this.stockReposit.execute(
      new CAStockRepositSerialNumberRemove(
        instance0,
        new CAItemSerial(TYPE0, "C")
      )
    );

    this.stockReposit.execute(
      new CAStockRepositSerialNumberRemove(
        instance0,
        new CAItemSerial(TYPE0, "B")
      )
    );

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.stockReposit.execute(
          new CAStockRepositSerialNumberRemove(
            instance0,
            new CAItemSerial(TYPE0, "B")
          )
        );
      });
    assertEquals(errorNonexistent(), ex.errorCode());
  }

  /**
   * Removing instances works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositRemove0()
    throws Exception
  {
    final var instance0 =
      CAStockInstanceID.random();

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
        CALocationPath.singleton("Loc0"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);

    this.stockReposit.execute(
      new CAStockRepositSerialIntroduce(
        instance0,
        itemId,
        loc0.id(),
        new CAItemSerial(TYPE0, "A")
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(1L, c);
    }

    this.stockReposit.execute(
      new CAStockRepositRemove(
        instance0
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(0L, c);
    }

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.stockReposit.execute(new CAStockRepositRemove(instance0));
      });
    assertEquals(errorNonexistent(), ex.errorCode());
  }

  /**
   * Removing instances works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositRemove1()
    throws Exception
  {
    final var instance0 =
      CAStockInstanceID.random();

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
        CALocationPath.singleton("Loc0"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);

    this.stockReposit.execute(
      new CAStockRepositSetIntroduce(
        instance0,
        itemId,
        loc0.id(),
        1000L
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(1000L, c);
    }

    this.stockReposit.execute(
      new CAStockRepositRemove(
        instance0
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(0L, c);
    }

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.stockReposit.execute(new CAStockRepositRemove(instance0));
      });
    assertEquals(errorNonexistent(), ex.errorCode());
  }

  /**
   * Moving stock works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSerialMove()
    throws Exception
  {
    final var instance0 =
      CAStockInstanceID.random();

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
        CALocationPath.singleton("Loc0"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    final var loc1 =
      new CALocation(
        CALocationID.random(),
        empty(),
        CALocationPath.singleton("Loc1"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);
    this.locPut.execute(loc1);

    this.stockReposit.execute(
      new CAStockRepositSerialIntroduce(
        instance0,
        itemId,
        loc0.id(),
        new CAItemSerial(TYPE0, "A")
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(1L, c);
    }

    this.stockReposit.execute(
      new CAStockRepositSerialMove(
        instance0,
        loc1.id()
      )
    );

    {
      final var c = this.stockCount.execute(search);
      assertEquals(1L, c);
    }
  }

  /**
   * Moving stock fails if the source does not exist.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSerialMoveSourceNonexistent()
    throws Exception
  {
    final var instance0 =
      CAStockInstanceID.random();

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
        CALocationPath.singleton("Loc0"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    final var loc1 =
      new CALocation(
        CALocationID.random(),
        empty(),
        CALocationPath.singleton("Loc1"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);
    this.locPut.execute(loc1);

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.stockReposit.execute(
          new CAStockRepositSerialMove(
            instance0,
            loc1.id()
          )
        );
      });
    assertEquals(errorNonexistent(), ex.errorCode());
  }

  /**
   * Moving stock fails if the source is a set instance.
   *
   * @throws Exception On errors
   */

  @Test
  public void testStockRepositSerialMoveSourceSet()
    throws Exception
  {
    final var instance0 =
      CAStockInstanceID.random();

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
        CALocationPath.singleton("Loc0"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    final var loc1 =
      new CALocation(
        CALocationID.random(),
        empty(),
        CALocationPath.singleton("Loc1"),
        OffsetDateTime.now(UTC),
        OffsetDateTime.now(UTC),
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locPut.execute(loc0);
    this.locPut.execute(loc1);

    this.stockReposit.execute(
      new CAStockRepositSetIntroduce(
        instance0,
        itemId,
        loc0.id(),
        1L
      )
    );

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.stockReposit.execute(
          new CAStockRepositSerialMove(
            instance0,
            loc1.id()
          )
        );
      });
    assertEquals(errorStockIsNotSerial(), ex.errorCode());
  }
}
