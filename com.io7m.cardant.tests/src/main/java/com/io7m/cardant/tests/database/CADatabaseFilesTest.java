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
import com.io7m.cardant.database.api.CADatabaseQueriesFilesType;
import com.io7m.cardant.database.api.CADatabaseQueriesFilesType.GetType.Parameters;
import com.io7m.cardant.database.api.CADatabaseQueriesUsersType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAFileColumn;
import com.io7m.cardant.model.CAFileColumnOrdering;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileSearchParameters;
import com.io7m.cardant.model.CAFileType.CAFileWithData;
import com.io7m.cardant.model.CASizeRange;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(projectName = "com.io7m.cardant", disabledIfUnsupported = true)
public final class CADatabaseFilesTest
{
  private static CADatabaseFixture DATABASE_FIXTURE;
  private CADatabaseConnectionType connection;
  private CADatabaseTransactionType transaction;
  private CADatabaseType database;

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
  }

  /**
   * Creating files works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileCreate()
    throws Exception
  {
    final var put =
      this.transaction.queries(CADatabaseQueriesFilesType.PutType.class);
    final var get =
      this.transaction.queries(CADatabaseQueriesFilesType.GetType.class);
    
    final var file = createFile(0);

    put.execute(file);

    final var fileWithout =
      file.withoutData();

    final var resultWithout =
      get.execute(new Parameters(file.id(), false))
        .orElseThrow();

    assertEquals(fileWithout, resultWithout);

    final var resultWith =
      get.execute(new Parameters(file.id(), true))
        .orElseThrow();

    assertEquals(file, resultWith);
  }

  /**
   * Deleting files works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileDelete()
    throws Exception
  {
    final var put =
      this.transaction.queries(CADatabaseQueriesFilesType.PutType.class);
    final var get =
      this.transaction.queries(CADatabaseQueriesFilesType.GetType.class);
    final var remove =
      this.transaction.queries(CADatabaseQueriesFilesType.RemoveType.class);

    final var file = createFile(0);

    put.execute(file);
    remove.execute(file.id());

    assertEquals(
      Optional.empty(),
      get.execute(new Parameters(file.id(), false))
    );
    assertEquals(
      Optional.empty(),
      get.execute(new Parameters(file.id(), true))
    );
  }

  /**
   * Searching for files works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileSearch0()
    throws Exception
  {
    final var put =
      this.transaction.queries(CADatabaseQueriesFilesType.PutType.class);
    final var search =
      this.transaction.queries(CADatabaseQueriesFilesType.SearchType.class);

    final var file0 = createFile(0);
    final var file1 = createFile(1);
    final var file2 = createFile(2);

    put.execute(file0);
    put.execute(file1);
    put.execute(file2);

    this.transaction.commit();

    final var s =
      search.execute(new CAFileSearchParameters(
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonFuzzyType.Anything<>(),
        new CASizeRange(0L, Long.MAX_VALUE),
        new CAFileColumnOrdering(CAFileColumn.BY_DESCRIPTION, true),
        100
      ));

    {
      final var p = s.pageCurrent(this.transaction);
      assertEquals("File 0", p.items().get(0).description());
      assertEquals("File 1", p.items().get(1).description());
      assertEquals("File 2", p.items().get(2).description());
      assertEquals(3, p.items().size());
    }
  }

  /**
   * Searching for files works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileSearch1()
    throws Exception
  {
    final var put =
      this.transaction.queries(CADatabaseQueriesFilesType.PutType.class);
    final var search =
      this.transaction.queries(CADatabaseQueriesFilesType.SearchType.class);

    final var file0 = createFile(0);
    final var file1 = createFile(1);
    final var file2 = createFile(2);

    put.execute(file0);
    put.execute(file1);
    put.execute(file2);

    this.transaction.commit();

    final var s =
      search.execute(new CAFileSearchParameters(
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonFuzzyType.Anything<>(),
        new CASizeRange(0L, Long.MAX_VALUE),
        new CAFileColumnOrdering(CAFileColumn.BY_DESCRIPTION, false),
        100
      ));

    {
      final var p = s.pageCurrent(this.transaction);
      assertEquals("File 2", p.items().get(0).description());
      assertEquals("File 1", p.items().get(1).description());
      assertEquals("File 0", p.items().get(2).description());
      assertEquals(3, p.items().size());
    }
  }

  /**
   * Searching for files works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileSearch2()
    throws Exception
  {
    final var put =
      this.transaction.queries(CADatabaseQueriesFilesType.PutType.class);
    final var search =
      this.transaction.queries(CADatabaseQueriesFilesType.SearchType.class);

    final var file0 = createFile(0);
    final var file1 = createFile(1);
    final var file2 = createFile(2);

    put.execute(file0);
    put.execute(file1);
    put.execute(file2);

    this.transaction.commit();

    final var s =
      search.execute(new CAFileSearchParameters(
        new CAComparisonFuzzyType.IsEqualTo<>("File 1"),
        new CAComparisonFuzzyType.Anything<>(),
        new CASizeRange(0L, Long.MAX_VALUE),
        new CAFileColumnOrdering(CAFileColumn.BY_DESCRIPTION, true),
        100
      ));

    {
      final var p = s.pageCurrent(this.transaction);
      assertEquals("File 1", p.items().get(0).description());
      assertEquals(1, p.items().size());
    }
  }

  /**
   * Searching for files works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileSearch3()
    throws Exception
  {
    final var put =
      this.transaction.queries(CADatabaseQueriesFilesType.PutType.class);
    final var search =
      this.transaction.queries(CADatabaseQueriesFilesType.SearchType.class);

    final var file0 = createFile(0);
    final var file1 = createFile(1);
    final var file2 = createFile(2);

    put.execute(file0);
    put.execute(file1);
    put.execute(file2);

    this.transaction.commit();

    final var s =
      search.execute(new CAFileSearchParameters(
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonFuzzyType.IsEqualTo<>("text/plain+1"),
        new CASizeRange(0L, Long.MAX_VALUE),
        new CAFileColumnOrdering(CAFileColumn.BY_DESCRIPTION, true),
        100
      ));

    {
      final var p = s.pageCurrent(this.transaction);
      assertEquals("File 1", p.items().get(0).description());
      assertEquals(1, p.items().size());
    }
  }

  /**
   * Searching for files works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileSearch4()
    throws Exception
  {
    final var put =
      this.transaction.queries(CADatabaseQueriesFilesType.PutType.class);
    final var search =
      this.transaction.queries(CADatabaseQueriesFilesType.SearchType.class);

    final var file0 = createFile(0);
    final var file1 = createFile(1);
    final var file2 = createFile(2);

    put.execute(file0);
    put.execute(file1);
    put.execute(file2);

    this.transaction.commit();

    final var s =
      search.execute(new CAFileSearchParameters(
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonFuzzyType.Anything<>(),
        new CASizeRange(0L, 3L),
        new CAFileColumnOrdering(CAFileColumn.BY_DESCRIPTION, true),
        100
      ));

    {
      final var p = s.pageCurrent(this.transaction);
      assertEquals(0, p.items().size());
    }
  }

  /**
   * Searching for files works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileSearch5()
    throws Exception
  {
    final var put =
      this.transaction.queries(CADatabaseQueriesFilesType.PutType.class);
    final var search =
      this.transaction.queries(CADatabaseQueriesFilesType.SearchType.class);

    final var file0 = createFile(0);
    final var file1 = createFile(1);
    final var file2 = createFile(2);

    put.execute(file0);
    put.execute(file1);
    put.execute(file2);

    this.transaction.commit();

    final var s =
      search.execute(new CAFileSearchParameters(
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonFuzzyType.IsEqualTo<>("text/plain+1"),
        new CASizeRange(0L, Long.MAX_VALUE),
        new CAFileColumnOrdering(CAFileColumn.BY_DESCRIPTION, true),
        100
      ));

    {
      final var p = s.pageCurrent(this.transaction);
      assertEquals("File 1", p.items().get(0).description());
      assertEquals(1, p.items().size());
    }
  }

  /**
   * Searching for files works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testFileSearch6()
    throws Exception
  {
    final var put =
      this.transaction.queries(CADatabaseQueriesFilesType.PutType.class);
    final var search =
      this.transaction.queries(CADatabaseQueriesFilesType.SearchType.class);

    final var file0 = createFile(0);
    final var file1 = createFile(1);
    final var file2 = createFile(2);

    put.execute(file0);
    put.execute(file1);
    put.execute(file2);

    this.transaction.commit();

    final var s =
      search.execute(new CAFileSearchParameters(
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonFuzzyType.Anything<>(),
        new CASizeRange(9L, Long.MAX_VALUE),
        new CAFileColumnOrdering(CAFileColumn.BY_DESCRIPTION, true),
        100
      ));

    {
      final var p = s.pageCurrent(this.transaction);
      assertEquals("File 2", p.items().get(0).description());
      assertEquals(1, p.items().size());
    }
  }

  private static CAFileWithData createFile(
    final int index)
    throws NoSuchAlgorithmException
  {
    final var digest =
      MessageDigest.getInstance("SHA-256");
    final var content =
      "HELLO! %s".formatted("X".repeat(index));
    final var contentBytes =
      content.getBytes(UTF_8);
    final var hash =
      digest.digest(contentBytes);
    final var hashS =
      HexFormat.of().formatHex(hash);

    return new CAFileWithData(
      CAFileID.random(),
      "File %d".formatted(Integer.valueOf(index)),
      "text/plain+%d".formatted(Integer.valueOf(index)),
      "SHA-256",
      hashS,
      new CAByteArray(contentBytes)
    );
  }
}
