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
import com.io7m.cardant.protocol.inventory.CAICommandRolesGet;
import com.io7m.cardant.protocol.inventory.CAIResponseRolesGet;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdRolesGet;
import com.io7m.idstore.model.IdName;
import com.io7m.medrina.api.MSubject;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.security.CASecurityPolicy.ROLE_INVENTORY_ITEMS_WRITER;
import static com.io7m.cardant.security.CASecurityPolicy.ROLE_INVENTORY_TAGS_READER;
import static com.io7m.cardant.security.CASecurityPolicy.ROLE_INVENTORY_TAGS_WRITER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @see CAICmdRolesGet
 */

public final class CAICmdRolesGetTest
  extends CACmdAbstractContract
{
  /**
   * Trying to examine a nonexistent user fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testNonexistent()
    throws Exception
  {
    /* Arrange. */

    final var userGet =
      mock(CADatabaseQueriesUsersType.GetType.class);
    final var transaction =
      this.transaction();

    when(transaction.queries(CADatabaseQueriesUsersType.GetType.class))
      .thenReturn(userGet);

    when(userGet.execute(any()))
      .thenReturn(Optional.empty());

    final var context =
      this.createContext();

    /* Act. */

    final var handler = new CAICmdRolesGet();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandRolesGet(UUID.randomUUID())
        );
      });

    /* Assert. */

    assertEquals(errorNonexistent(), ex.errorCode());
  }

  /**
   * Examining a user that exists works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRoleGiveawayAdmin()
    throws Exception
  {
    /* Arrange. */

    final var userGet =
      mock(CADatabaseQueriesUsersType.GetType.class);
    final var transaction =
      this.transaction();

    final var targetUser =
      UUID.randomUUID();

    when(transaction.queries(CADatabaseQueriesUsersType.GetType.class))
      .thenReturn(userGet);

    when(userGet.execute(targetUser))
      .thenReturn(Optional.of(
        new CAUser(
          targetUser,
          new IdName("x"),
          new MSubject(Set.of(
            ROLE_INVENTORY_TAGS_WRITER,
            ROLE_INVENTORY_ITEMS_WRITER,
            ROLE_INVENTORY_TAGS_READER
          ))
        )
      ));

    final var context =
      this.createContext();

    /* Act. */

    final var handler =
      new CAICmdRolesGet();
    final var result =
      handler.execute(context, new CAICommandRolesGet(targetUser));
    final var get =
      assertInstanceOf(CAIResponseRolesGet.class, result);

    /* Assert. */

    assertEquals(
      Set.of(
        ROLE_INVENTORY_TAGS_WRITER,
        ROLE_INVENTORY_ITEMS_WRITER,
        ROLE_INVENTORY_TAGS_READER
      ),
      get.roles()
    );

    verify(transaction)
      .queries(CADatabaseQueriesUsersType.GetType.class);
    verify(userGet)
      .execute(targetUser);

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(userGet);
  }
}
