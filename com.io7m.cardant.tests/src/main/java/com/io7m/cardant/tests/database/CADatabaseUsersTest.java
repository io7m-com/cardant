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
import com.io7m.cardant.database.api.CADatabaseQueriesUsersType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.tests.containers.CATestContainers;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.test_extension.ErvillaCloseAfterAll;
import com.io7m.ervilla.test_extension.ErvillaConfiguration;
import com.io7m.ervilla.test_extension.ErvillaExtension;
import com.io7m.idstore.model.IdName;
import com.io7m.medrina.api.MRoleName;
import com.io7m.medrina.api.MSubject;
import com.io7m.zelador.test_extension.CloseableResourcesType;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(disabledIfUnsupported = true)
public final class CADatabaseUsersTest
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
   * Creating users works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testUserCreate0()
    throws Exception
  {
    final var get =
      this.transaction.queries(CADatabaseQueriesUsersType.GetType.class);
    final var put =
      this.transaction.queries(CADatabaseQueriesUsersType.PutType.class);

    final var user =
      new CAUser(
        UUID.randomUUID(),
        new IdName("x"),
        new MSubject(Set.of())
      );

    this.transaction.setUserId(user.userId());
    put.execute(user);
    assertEquals(user, get.execute(user.userId()).orElseThrow());
  }

  /**
   * Creating users works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testUserCreate1()
    throws Exception
  {
    final var get =
      this.transaction.queries(CADatabaseQueriesUsersType.GetType.class);
    final var put =
      this.transaction.queries(CADatabaseQueriesUsersType.PutType.class);

    final var user =
      new CAUser(
        UUID.randomUUID(),
        new IdName("x"),
        new MSubject(Set.of(
          MRoleName.of("role0"),
          MRoleName.of("role1"),
          MRoleName.of("role2")
        ))
      );

    this.transaction.setUserId(user.userId());
    put.execute(user);
    assertEquals(user, get.execute(user.userId()).orElseThrow());
  }


  /**
   * Nonexistent users are nonexistent.
   *
   * @throws Exception On errors
   */

  @Test
  public void testUserGet0()
    throws Exception
  {
    final var get =
      this.transaction.queries(CADatabaseQueriesUsersType.GetType.class);

    assertEquals(Optional.empty(), get.execute(UUID.randomUUID()));
  }
}
