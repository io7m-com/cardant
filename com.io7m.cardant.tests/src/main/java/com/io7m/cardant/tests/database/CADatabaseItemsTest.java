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
import com.io7m.cardant.database.api.CADatabaseQueriesFilesType.PutType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemAttachmentAddType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemAttachmentRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemCreateType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemDeleteMarkOnlyType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemDeleteType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemGetType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemMetadataPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemMetadataRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemSearchType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemSetNameType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemSetNameType.Parameters;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesStockType.StockRepositType;
import com.io7m.cardant.database.api.CADatabaseQueriesUsersType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.model.CAAttachment;
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType.CAFileWithData;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAMetadataType.Integral;
import com.io7m.cardant.model.CAMetadataType.Monetary;
import com.io7m.cardant.model.CAMetadataType.Real;
import com.io7m.cardant.model.CAMetadataType.Text;
import com.io7m.cardant.model.CAMetadataType.Time;
import com.io7m.cardant.model.CAMoney;
import com.io7m.cardant.model.CATypeRecordFieldIdentifier;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.model.CAUserID;
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
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorDuplicate;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static java.time.ZoneOffset.UTC;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(projectName = "com.io7m.cardant", disabledIfUnsupported = true)
public final class CADatabaseItemsTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CADatabaseItemsTest.class);

  private static CADatabaseFixture DATABASE_FIXTURE;
  private CADatabaseConnectionType connection;
  private CADatabaseTransactionType transaction;
  private CADatabaseType database;
  private ItemCreateType itemCreate;
  private ItemSetNameType setName;
  private ItemGetType get;
  private ItemDeleteMarkOnlyType deleteMark;
  private LocationPutType locPut;
  private StockRepositType repositQuery;
  private ItemSearchType searchQuery;
  private ItemDeleteType delete;
  private ItemGetType itemGet;
  private ItemMetadataPutType metaAdd;
  private ItemMetadataRemoveType metaRemove;

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
    this.setName =
      this.transaction.queries(ItemSetNameType.class);
    this.get =
      this.transaction.queries(ItemGetType.class);
    this.deleteMark =
      this.transaction.queries(ItemDeleteMarkOnlyType.class);
    this.delete =
      this.transaction.queries(ItemDeleteType.class);
    this.locPut =
      this.transaction.queries(LocationPutType.class);
    this.repositQuery =
      this.transaction.queries(StockRepositType.class);
    this.searchQuery =
      this.transaction.queries(ItemSearchType.class);
    this.metaAdd =
      this.transaction.queries(ItemMetadataPutType.class);
    this.metaRemove =
      this.transaction.queries(ItemMetadataRemoveType.class);
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

    this.deleteMark.execute(
      new ItemDeleteMarkOnlyType.Parameters(List.of(id0), true)
    );
    assertEquals(empty(), this.get.execute(id0));
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
      new Text(
        CATypeRecordFieldIdentifier.of("x.y:a0.s"),
        "abc"
      );
    final var meta1 =
      new Integral(
        CATypeRecordFieldIdentifier.of("x.y:a1.t"),
        230L
      );
    final var meta2 =
      new Real(
        CATypeRecordFieldIdentifier.of("x.y:a2.u"),
        45.0
      );
    final var meta3 =
      new Time(
        CATypeRecordFieldIdentifier.of("x.y:a3.w"),
        OffsetDateTime.of(2000, 1, 1, 13, 30, 23, 0, UTC)
      );
    final var meta4 =
      new Monetary(
        CATypeRecordFieldIdentifier.of("x.y:a4.p"),
        CAMoney.money("200.0000000000000000"),
        CurrencyUnit.EUR
      );

    this.metaAdd.execute(
      new ItemMetadataPutType.Parameters(
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
      new ItemMetadataRemoveType.Parameters(id0, Set.of(meta1.name()))
    );

    {
      final var i = this.itemGet.execute(id0).orElseThrow();
      assertEquals(meta0, i.metadata().get(meta0.name()));
      assertEquals(meta2, i.metadata().get(meta2.name()));
      assertEquals(meta3, i.metadata().get(meta3.name()));
      assertEquals(meta4, i.metadata().get(meta4.name()));
    }

    this.metaRemove.execute(
      new ItemMetadataRemoveType.Parameters(
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
        PutType.class);
    final var itemAttachmentAdd =
      this.transaction.queries(
        ItemAttachmentAddType.class);
    final var itemAttachmentRemove =
      this.transaction.queries(
        ItemAttachmentRemoveType.class);

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
      new ItemAttachmentAddType.Parameters(id0, file.id(), "misc"));

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
      new ItemAttachmentRemoveType.Parameters(id0, file.id(), "misc"));

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
