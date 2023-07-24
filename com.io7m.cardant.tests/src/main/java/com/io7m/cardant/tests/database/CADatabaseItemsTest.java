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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.MetadataPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.MetadataRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.SetNameType.Parameters;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.model.CAAttachment;
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType.CAFileWithData;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemRepositAdd;
import com.io7m.cardant.model.CAItemRepositMove;
import com.io7m.cardant.model.CAItemRepositRemove;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CAMoney;
import com.io7m.cardant.tests.containers.CATestContainers;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.test_extension.ErvillaCloseAfterAll;
import com.io7m.ervilla.test_extension.ErvillaConfiguration;
import com.io7m.ervilla.test_extension.ErvillaExtension;
import com.io7m.lanark.core.RDottedName;
import com.io7m.zelador.test_extension.CloseableResourcesType;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorDuplicate;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorRemoveTooManyItems;
import static java.time.ZoneOffset.UTC;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(disabledIfUnsupported = true)
public final class CADatabaseItemsTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CADatabaseItemsTest.class);

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
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
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
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
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
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
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
      new CAMetadataType.Text(
        new RDottedName("x.y.a0"),
        "abc"
      );
    final var meta1 =
      new CAMetadataType.Integral(
        new RDottedName("x.y.a1"),
        230L
      );
    final var meta2 =
      new CAMetadataType.Real(
        new RDottedName("x.y.a2"),
        45.0
      );
    final var meta3 =
      new CAMetadataType.Time(
        new RDottedName("x.y.a3"),
        OffsetDateTime.of(2000, 1, 1, 13, 30, 23, 0, UTC)
      );
    final var meta4 =
      new CAMetadataType.Monetary(
        new RDottedName("x.y.a4"),
        CAMoney.money("200.0000000000000000"),
        CurrencyUnit.EUR
      );

    this.metaAdd.execute(
      new MetadataPutType.Parameters(
        id0,
        Set.of(meta0, meta1, meta2, meta3, meta4)
      )
    );

    {
      final var i = this.itemGet.execute(id0).orElseThrow();
      assertEquals(meta0, i.metadata().get(meta0.name()));
      assertEquals(meta1, i.metadata().get(meta1.name()));
      assertEquals(meta2, i.metadata().get(meta2.name()));
      assertEquals(meta3, i.metadata().get(meta3.name()));
      assertEquals(meta4, i.metadata().get(meta4.name()));
    }

    this.metaRemove.execute(
      new MetadataRemoveType.Parameters(id0, Set.of(meta1.name()))
    );

    {
      final var i = this.itemGet.execute(id0).orElseThrow();
      assertEquals(meta0, i.metadata().get(meta0.name()));
      assertEquals(meta2, i.metadata().get(meta2.name()));
      assertEquals(meta3, i.metadata().get(meta3.name()));
      assertEquals(meta4, i.metadata().get(meta4.name()));
    }

    this.metaRemove.execute(
      new MetadataRemoveType.Parameters(
        id0,
        Set.of(
          meta0.name(),
          meta1.name(),
          meta2.name(),
          meta3.name(),
          meta4.name()))
    );

    {
      final var i = this.itemGet.execute(id0).orElseThrow();
      assertEquals(Map.of(), i.metadata());
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

    final var file =
      new CAFileWithData(
        CAFileID.random(),
        "Description",
        "text/plain",
        "SHA-256",
        "ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb",
        new CAByteArray("a".getBytes(StandardCharsets.UTF_8))
      );
    fileAdd.execute(file);

    itemAttachmentAdd.execute(
      new AttachmentAddType.Parameters(id0, file.id(), "misc"));

    {
      final var a =
        Set.copyOf(
          this.itemGet.execute(id0)
            .orElseThrow()
            .attachments()
            .values()
        );

      assertEquals(
        Set.of(new CAAttachment(file.withoutData(), "misc")),
        a
      );
    }

    itemAttachmentRemove.execute(
      new AttachmentRemoveType.Parameters(id0, file.id(), "misc"));

    {
      final var a =
        Set.copyOf(
          this.itemGet.execute(id0)
            .orElseThrow()
            .attachments()
            .values()
        );

      assertEquals(
        Set.of(),
        a
      );
    }
  }
}
