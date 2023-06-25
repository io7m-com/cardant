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

import com.io7m.cardant.database.api.CADatabaseFileSearchType;
import com.io7m.cardant.database.api.CADatabaseQueriesFilesType;
import com.io7m.cardant.model.CAFileColumn;
import com.io7m.cardant.model.CAFileColumnOrdering;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileSearchParameters;
import com.io7m.cardant.model.CAFileType.CAFileWithoutData;
import com.io7m.cardant.model.CAPage;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchBegin;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdFileSearchBegin;
import com.io7m.medrina.api.MMatchActionType.MMatchActionWithName;
import com.io7m.medrina.api.MMatchObjectType.MMatchObjectWithType;
import com.io7m.medrina.api.MMatchSubjectType.MMatchSubjectWithRolesAny;
import com.io7m.medrina.api.MPolicy;
import com.io7m.medrina.api.MRule;
import com.io7m.medrina.api.MRuleName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSecurityPolicyDenied;
import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_FILES;
import static com.io7m.cardant.security.CASecurityPolicy.READ;
import static com.io7m.cardant.security.CASecurityPolicy.ROLE_INVENTORY_FILES_READER;
import static com.io7m.medrina.api.MRuleConclusion.ALLOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @see CAICmdFileSearchBegin
 */

public final class CAICmdFileSearchBeginTest
  extends CACmdAbstractContract
{
  private static final CAFileID FILE_ID = CAFileID.random();

  private static final CAFileSearchParameters PARAMETERS =
    new CAFileSearchParameters(
      Optional.empty(),
      Optional.empty(),
      Optional.empty(),
      new CAFileColumnOrdering(CAFileColumn.BY_ID, true),
      100
    );

  /**
   * Searching for files requires the permission to READ to INVENTORY_FILES.
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
      new CAICmdFileSearchBegin();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandFileSearchBegin(PARAMETERS));
      });

    /* Assert. */

    assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
  }

  /**
   * Searching for files works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testSearch()
    throws Exception
  {
    /* Arrange. */

    final var files =
      mock(CADatabaseQueriesFilesType.class);
    final var fileSearch =
      mock(CADatabaseFileSearchType.class);

    final var transaction =
      this.transaction();

    final var page =
      new CAPage<>(
        List.of(new CAFileWithoutData(
          FILE_ID,
          "File",
          "text/plain",
          100L,
          "SHA-256",
          "181f9dbf4f9d7f567de5cff42d00f66b3242fb46cb6f7737229abf0298149226")),
        1,
        1,
        0L
      );

    when(transaction.queries(CADatabaseQueriesFilesType.class))
      .thenReturn(files);
    when(files.fileSearch(any()))
      .thenReturn(fileSearch);
    when(fileSearch.pageCurrent(any()))
      .thenReturn(page);

    CASecurity.setPolicy(new MPolicy(List.of(
      new MRule(
        MRuleName.of("rule0"),
        "",
        ALLOW,
        new MMatchSubjectWithRolesAny(Set.of(ROLE_INVENTORY_FILES_READER)),
        new MMatchObjectWithType(INVENTORY_FILES.type()),
        new MMatchActionWithName(READ)
      )
    )));

    this.setRoles(ROLE_INVENTORY_FILES_READER);

    final var context =
      this.createContext();

    /* Act. */

    final var handler = new CAICmdFileSearchBegin();

    handler.execute(context, new CAICommandFileSearchBegin(PARAMETERS));

    /* Assert. */

    verify(transaction)
      .queries(CADatabaseQueriesFilesType.class);
    verify(files)
      .fileSearch(PARAMETERS);
    verify(fileSearch)
      .pageCurrent(files);

    verifyNoMoreInteractions(fileSearch);
    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(files);
  }
}
