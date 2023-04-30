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

import com.io7m.cardant.database.api.CADatabaseQueriesFilesType;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.protocol.inventory.CAICommandFileRemove;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdFileRemove;
import com.io7m.medrina.api.MMatchActionType.MMatchActionWithName;
import com.io7m.medrina.api.MMatchObjectType.MMatchObjectWithType;
import com.io7m.medrina.api.MMatchSubjectType.MMatchSubjectWithRolesAny;
import com.io7m.medrina.api.MPolicy;
import com.io7m.medrina.api.MRule;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSecurityPolicyDenied;
import static com.io7m.cardant.security.CASecurityPolicy.DELETE;
import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_FILES;
import static com.io7m.cardant.security.CASecurityPolicy.ROLE_INVENTORY_FILES_WRITER;
import static com.io7m.medrina.api.MRuleConclusion.ALLOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @see CAICmdFileRemove
 */

public final class CAICmdFileRemoveTest
  extends CACmdAbstractContract
{
  private static final CAFileType FILE =
    new CAFileType.CAFileWithoutData(
      CAFileID.random(),
      "Any",
      "text/plain",
      100L,
      "SHA-256",
      "5891b5b522d5df086d0ff0b110fbd9d21bb4fc7163af34d08286a2e846f6be03"
    );

  /**
   * Deleting a file requires the permission to WRITE to INVENTORY_FILES.
   *
   * @throws Exception On errors
   */

  @Test
  public void testNotAllowed0()
    throws Exception
  {
    /* Arrange. */

    final var context =
      this.createContext();

    /* Act. */

    final var handler =
      new CAICmdFileRemove();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandFileRemove(FILE.id()));
      });

    /* Assert. */

    assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
  }

  /**
   * Deleting a file works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testCreates()
    throws Exception
  {
    /* Arrange. */

    final var files =
      mock(CADatabaseQueriesFilesType.class);
    final var transaction =
      this.transaction();

    when(transaction.queries(CADatabaseQueriesFilesType.class))
      .thenReturn(files);

    CASecurity.setPolicy(new MPolicy(List.of(
      new MRule(
        ALLOW,
        new MMatchSubjectWithRolesAny(Set.of(ROLE_INVENTORY_FILES_WRITER)),
        new MMatchObjectWithType(INVENTORY_FILES.type()),
        new MMatchActionWithName(DELETE)
      )
    )));

    this.setRoles(ROLE_INVENTORY_FILES_WRITER);

    final var context =
      this.createContext();

    /* Act. */

    final var handler = new CAICmdFileRemove();
    handler.execute(context, new CAICommandFileRemove(FILE.id()));

    /* Assert. */

    verify(transaction)
      .queries(CADatabaseQueriesFilesType.class);

    verify(files)
      .fileRemove(FILE.id());

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(files);
  }
}
