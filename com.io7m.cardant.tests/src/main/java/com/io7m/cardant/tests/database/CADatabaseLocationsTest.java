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
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationAttachmentAddType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationAttachmentRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationAttachmentRemoveType.Parameters;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationGetType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationListType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationMetadataPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationMetadataRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesUsersType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CAAttachment;
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType.CAFileWithData;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CALocationSummary;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.tests.containers.CATestContainers;
import com.io7m.cardant.tests.containers.CATestContainers.CADatabaseFixture;
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

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;
import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(projectName = "com.io7m.cardant", disabledIfUnsupported = true)
public final class CADatabaseLocationsTest
{
  private static CADatabaseFixture DATABASE_FIXTURE;
  private CADatabaseConnectionType connection;
  private CADatabaseTransactionType transaction;
  private CADatabaseType database;
  private LocationGetType locationGet;
  private LocationPutType locationPut;
  private LocationListType locationList;
  private LocationMetadataPutType metaPut;
  private LocationMetadataRemoveType metaRemove;

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

    this.locationPut =
      this.transaction.queries(LocationPutType.class);
    this.locationList =
      this.transaction.queries(LocationListType.class);
    this.locationGet =
      this.transaction.queries(LocationGetType.class);

    this.metaPut =
      this.transaction.queries(LocationMetadataPutType.class);
    this.metaRemove =
      this.transaction.queries(LocationMetadataRemoveType.class);
  }

  /**
   * Creating locations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testLocationCreate()
    throws Exception
  {
    final var loc0 =
      new CALocation(
        CALocationID.random(),
        Optional.empty(),
        "Loc0",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );
    final var loc1 =
      new CALocation(
        CALocationID.random(),
        Optional.empty(),
        "Loc1",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );
    final var loc2 =
      new CALocation(
        CALocationID.random(),
        Optional.empty(),
        "Loc2",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locationPut.execute(loc0);
    this.locationPut.execute(loc1);
    this.locationPut.execute(loc2);

    this.locationPut.execute(loc0);
    this.locationPut.execute(loc1);
    this.locationPut.execute(loc2);

    final var r = new TreeMap<CALocationID, CALocationSummary>();
    r.put(loc0.id(), loc0.summary());
    r.put(loc1.id(), loc1.summary());
    r.put(loc2.id(), loc2.summary());

    assertEquals(r, this.locationList.execute(UNIT));

    assertEquals(loc0, this.locationGet.execute(loc0.id()).orElseThrow());
    assertEquals(loc1, this.locationGet.execute(loc1.id()).orElseThrow());
    assertEquals(loc2, this.locationGet.execute(loc2.id()).orElseThrow());
  }

  /**
   * Removing a location parent works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testLocationRemoveParent()
    throws Exception
  {
    final var put =
      this.transaction.queries(LocationPutType.class);
    final var list =
      this.transaction.queries(LocationListType.class);

    final var loc0 =
      new CALocation(
        CALocationID.random(),
        Optional.empty(),
        "Loc0",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );
    final var loc1with =
      new CALocation(
        CALocationID.random(),
        Optional.of(loc0.id()),
        "Loc1",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );
    final var loc1without =
      new CALocation(
        loc1with.id(),
        Optional.empty(),
        "Loc1",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    put.execute(loc0);
    put.execute(loc1with);
    put.execute(loc1without);

    final var r = new TreeMap<CALocationID, CALocationSummary>();
    r.put(loc0.id(), loc0.summary());
    r.put(loc1without.id(), loc1without.summary());

    assertEquals(r, list.execute(UNIT));
  }

  /**
   * Creating a location cycle fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testLocationCyclic0()
    throws Exception
  {


    final var loc0 =
      new CALocation(
        CALocationID.random(),
        Optional.empty(),
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
    final var loc0cyc =
      new CALocation(
        loc0.id(),
        Optional.of(loc2.id()),
        "Loc0",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      );

    this.locationPut.execute(loc0);
    this.locationPut.execute(loc1);
    this.locationPut.execute(loc2);

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.locationPut.execute(loc0cyc);
      });
    assertEquals(CAStandardErrorCodes.errorCyclic(), ex.errorCode());
  }

  /**
   * Location metadata adjustments work.
   *
   * @throws Exception On errors
   */

  @Test
  public void testLocationMetadata()
    throws Exception
  {
    final var id0 =
      CALocationID.random();

    this.locationPut.execute(
      new CALocation(
        id0,
        Optional.empty(),
        "Loc0",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      )
    );

    final var meta0 =
      new CAMetadataType.Text(new RDottedName("x.y.a0"), "abc");
    final var meta1 =
      new CAMetadataType.Text(new RDottedName("x.y.a1"), "def");
    final var meta2 =
      new CAMetadataType.Text(new RDottedName("x.y.a2"), "ghi");

    this.metaPut.execute(
      new LocationMetadataPutType.Parameters(
        id0,
        Set.of(meta0,
               meta1,
               meta2))
    );

    {
      final var i = this.locationGet.execute(id0).orElseThrow();
      assertEquals(meta0, i.metadata().get(meta0.name()));
      assertEquals(meta1, i.metadata().get(meta1.name()));
      assertEquals(meta2, i.metadata().get(meta2.name()));
    }

    this.metaRemove.execute(
      new LocationMetadataRemoveType.Parameters(
        id0,
        Set.of(meta1.name()))
    );

    {
      final var i = this.locationGet.execute(id0).orElseThrow();
      assertEquals(meta0, i.metadata().get(meta0.name()));
      assertEquals(meta2, i.metadata().get(meta2.name()));
    }

    this.metaRemove.execute(
      new LocationMetadataRemoveType.Parameters(
        id0,
        Set.of(
          meta0.name(),
          meta1.name(),
          meta2.name()))
    );

    {
      final var i = this.locationGet.execute(id0).orElseThrow();
      assertEquals(Map.of(), i.metadata());
    }
  }

  /**
   * Location attachment adjustments work.
   *
   * @throws Exception On errors
   */

  @Test
  public void testLocationAttachments()
    throws Exception
  {
    final var id0 =
      CALocationID.random();

    this.locationPut.execute(
      new CALocation(
        id0,
        Optional.empty(),
        "Loc0",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      )
    );

    final var fileAdd =
      this.transaction.queries(
        CADatabaseQueriesFilesType.PutType.class);
    final var locationAttachmentAdd =
      this.transaction.queries(
        LocationAttachmentAddType.class);
    final var locationAttachmentRemove =
      this.transaction.queries(
        LocationAttachmentRemoveType.class);

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

    locationAttachmentAdd.execute(
      new LocationAttachmentAddType.Parameters(
        id0,
        file.id(),
        "misc"));

    {
      final var a =
        Set.copyOf(
          this.locationGet.execute(id0)
            .orElseThrow()
            .attachments()
            .values()
        );

      assertEquals(
        Set.of(new CAAttachment(file.withoutData(), "misc")),
        a
      );
    }

    locationAttachmentRemove.execute(
      new Parameters(
        id0,
        file.id(),
        "misc"));

    {
      final var a =
        Set.copyOf(
          this.locationGet.execute(id0)
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
