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
import com.io7m.cardant.database.api.CADatabaseQueriesTagsType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;
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

import java.util.TreeSet;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;
import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(disabledIfUnsupported = true)
public final class CADatabaseTagsTest
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
   * The tag list is empty.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTagListEmpty()
    throws Exception
  {
    final var list =
      this.transaction.queries(CADatabaseQueriesTagsType.ListType.class);
    
    assertEquals(new TreeSet<>(), list.execute(UNIT));
  }

  /**
   * The tag list contains created tags.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTagListCreate()
    throws Exception
  {
    final var list =
      this.transaction.queries(CADatabaseQueriesTagsType.ListType.class);
    final var put =
      this.transaction.queries(CADatabaseQueriesTagsType.PutType.class);
    final var get =
      this.transaction.queries(CADatabaseQueriesTagsType.GetType.class);

    final var ta = new CATag(CATagID.random(), "a");
    put.execute(ta);
    final var tb = new CATag(CATagID.random(), "b");
    put.execute(tb);
    final var tc = new CATag(CATagID.random(), "c");
    put.execute(tc);

    final var expected = new TreeSet<CATag>();
    expected.add(ta);
    expected.add(tb);
    expected.add(tc);

    assertEquals(expected, list.execute(UNIT));
    assertEquals(ta, get.execute(ta.id()).orElseThrow());
    assertEquals(tb, get.execute(tb.id()).orElseThrow());
    assertEquals(tc, get.execute(tc.id()).orElseThrow());
  }

  /**
   * The tag list does not contain deleted tags.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTagListDelete()
    throws Exception
  {
    final var list =
      this.transaction.queries(CADatabaseQueriesTagsType.ListType.class);
    final var put =
      this.transaction.queries(CADatabaseQueriesTagsType.PutType.class);
    final var delete =
      this.transaction.queries(CADatabaseQueriesTagsType.DeleteType.class);

    final var ta = new CATag(CATagID.random(), "a");
    put.execute(ta);
    final var tb = new CATag(CATagID.random(), "b");
    put.execute(tb);
    final var tc = new CATag(CATagID.random(), "c");
    put.execute(tc);

    final var expected = new TreeSet<CATag>();
    expected.add(ta);
    expected.add(tc);

    delete.execute(tb);

    assertEquals(expected, list.execute(UNIT));
  }

  /**
   * Updating tags works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTagUpdate()
    throws Exception
  {
    final var list =
      this.transaction.queries(CADatabaseQueriesTagsType.ListType.class);
    final var put =
      this.transaction.queries(CADatabaseQueriesTagsType.PutType.class);
    final var get =
      this.transaction.queries(CADatabaseQueriesTagsType.GetType.class);

    final var id = CATagID.random();

    final var ta = new CATag(id, "a");
    put.execute(ta);
    final var tb = new CATag(id, "b");
    put.execute(tb);
    final var tc = new CATag(id, "c");
    put.execute(tc);

    final var expected = new TreeSet<CATag>();
    expected.add(tc);

    assertEquals(expected, list.execute(UNIT));
    assertEquals(tc, get.execute(id).orElseThrow());
  }
}
