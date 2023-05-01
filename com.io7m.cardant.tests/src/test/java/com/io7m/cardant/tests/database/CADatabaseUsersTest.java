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

import com.io7m.cardant.database.api.CADatabaseQueriesUsersType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.model.CAUser;
import com.io7m.idstore.model.IdName;
import com.io7m.medrina.api.MRoleName;
import com.io7m.medrina.api.MSubject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers(disabledWithoutDocker = true)
@ExtendWith(CADatabaseExtension.class)
public final class CADatabaseUsersTest
{
  /**
   * Creating users works.
   *
   * @param transaction The transaction
   *
   * @throws Exception On errors
   */

  @Test
  public void testUserCreate0(
    final CADatabaseTransactionType transaction)
    throws Exception
  {
    final var q =
      transaction.queries(CADatabaseQueriesUsersType.class);

    final var user =
      new CAUser(
        UUID.randomUUID(),
        new IdName("x"),
        new MSubject(Set.of())
      );

    transaction.setUserId(user.userId());
    q.userPut(user);
    assertEquals(user, q.userGet(user.userId()).orElseThrow());
  }

  /**
   * Creating users works.
   *
   * @param transaction The transaction
   *
   * @throws Exception On errors
   */

  @Test
  public void testUserCreate1(
    final CADatabaseTransactionType transaction)
    throws Exception
  {
    final var q =
      transaction.queries(CADatabaseQueriesUsersType.class);

    final var user =
      new CAUser(
        UUID.randomUUID(),
        new IdName("x"),
        new MSubject(Set.of(
          new MRoleName("role0"),
          new MRoleName("role1"),
          new MRoleName("role2")
        ))
      );

    transaction.setUserId(user.userId());
    q.userPut(user);
    assertEquals(user, q.userGet(user.userId()).orElseThrow());
  }


  /**
   * Nonexistent users are nonexistent.
   *
   * @param transaction The transaction
   *
   * @throws Exception On errors
   */

  @Test
  public void testUserGet0(
    final CADatabaseTransactionType transaction)
    throws Exception
  {
    final var q =
      transaction.queries(CADatabaseQueriesUsersType.class);

    assertEquals(Optional.empty(), q.userGet(UUID.randomUUID()));
  }
}
