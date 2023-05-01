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


package com.io7m.cardant.tests.server.controller;

import com.io7m.cardant.database.api.CADatabaseQueriesUsersType;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.protocol.inventory.CAICommandRolesRevoke;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdRolesRevoke;
import com.io7m.idstore.model.IdName;
import com.io7m.medrina.api.MSubject;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorOperationNotPermitted;
import static com.io7m.cardant.security.CASecurityPolicy.ROLE_INVENTORY_ADMIN;
import static com.io7m.cardant.security.CASecurityPolicy.ROLE_INVENTORY_TAGS_WRITER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @see CAICmdRolesRevoke
 */

public final class CAICmdRolesRevokeTest
  extends CACmdAbstractContract
{
  /**
   * It's not possible to take away a role you don't have.
   *
   * @throws Exception On errors
   */

  @Test
  public void testNotAllowed0()
    throws Exception
  {
    /* Arrange. */

    final var users =
      mock(CADatabaseQueriesUsersType.class);
    final var transaction =
      this.transaction();

    when(transaction.queries(CADatabaseQueriesUsersType.class))
      .thenReturn(users);

    final var context =
      this.createContext();

    /* Act. */

    final var handler =
      new CAICmdRolesRevoke();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandRolesRevoke(
            UUID.randomUUID(),
            Set.of(ROLE_INVENTORY_TAGS_WRITER))
        );
      });

    /* Assert. */

    assertEquals(errorOperationNotPermitted(), ex.errorCode());
  }

  /**
   * Trying to update a nonexistent user fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testNonexistent()
    throws Exception
  {
    /* Arrange. */

    final var users =
      mock(CADatabaseQueriesUsersType.class);
    final var transaction =
      this.transaction();

    when(transaction.queries(CADatabaseQueriesUsersType.class))
      .thenReturn(users);

    this.setRoles(ROLE_INVENTORY_TAGS_WRITER);

    final var context =
      this.createContext();

    /* Act. */

    final var handler = new CAICmdRolesRevoke();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandRolesRevoke(
            UUID.randomUUID(),
            Set.of(ROLE_INVENTORY_TAGS_WRITER))
        );
      });

    /* Assert. */

    assertEquals(errorNonexistent(), ex.errorCode());
  }

  /**
   * Taking away a held role works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRoleGiveaway()
    throws Exception
  {
    /* Arrange. */

    final var users =
      mock(CADatabaseQueriesUsersType.class);
    final var transaction =
      this.transaction();

    final var targetUser =
      UUID.randomUUID();

    when(transaction.queries(CADatabaseQueriesUsersType.class))
      .thenReturn(users);
    when(users.userGet(targetUser))
      .thenReturn(Optional.of(
        new CAUser(
          targetUser,
          new IdName("x"),
          new MSubject(Set.of())
        )
      ));

    this.setRoles(ROLE_INVENTORY_TAGS_WRITER);

    final var context =
      this.createContext();

    /* Act. */

    final var handler = new CAICmdRolesRevoke();
    handler.execute(
      context,
      new CAICommandRolesRevoke(targetUser, Set.of(ROLE_INVENTORY_TAGS_WRITER))
    );

    /* Assert. */

    verify(transaction)
      .queries(CADatabaseQueriesUsersType.class);

    verify(users)
      .userGet(targetUser);
    verify(users)
      .userPut(
        new CAUser(targetUser, new IdName("x"), new MSubject(Set.of()))
      );

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(users);
  }

  /**
   * Taking away any role works for an admin.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRoleGiveawayAdmin()
    throws Exception
  {
    /* Arrange. */

    final var users =
      mock(CADatabaseQueriesUsersType.class);
    final var transaction =
      this.transaction();

    final var targetUser =
      UUID.randomUUID();

    when(transaction.queries(CADatabaseQueriesUsersType.class))
      .thenReturn(users);
    when(users.userGet(targetUser))
      .thenReturn(Optional.of(
        new CAUser(
          targetUser,
          new IdName("x"),
          new MSubject(Set.of())
        )
      ));

    this.setRoles(ROLE_INVENTORY_ADMIN);

    final var context =
      this.createContext();

    /* Act. */

    final var handler = new CAICmdRolesRevoke();
    handler.execute(
      context,
      new CAICommandRolesRevoke(targetUser, Set.of(ROLE_INVENTORY_TAGS_WRITER))
    );

    /* Assert. */

    verify(transaction)
      .queries(CADatabaseQueriesUsersType.class);

    verify(users)
      .userGet(targetUser);
    verify(users)
      .userPut(
        new CAUser(targetUser, new IdName("x"), new MSubject(Set.of()))
      );

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(users);
  }
}
