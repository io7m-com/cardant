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

import com.io7m.cardant.database.api.CADatabaseQueriesTagsType;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;
import com.io7m.cardant.model.CATags;
import com.io7m.cardant.protocol.inventory.CAICommandTagsDelete;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdTagsDelete;
import com.io7m.medrina.api.MMatchActionType.MMatchActionWithName;
import com.io7m.medrina.api.MMatchObjectType.MMatchObjectWithType;
import com.io7m.medrina.api.MMatchSubjectType.MMatchSubjectWithRolesAny;
import com.io7m.medrina.api.MPolicy;
import com.io7m.medrina.api.MRule;
import com.io7m.medrina.api.MRuleName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSecurityPolicyDenied;
import static com.io7m.cardant.security.CASecurityPolicy.DELETE;
import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_TAGS;
import static com.io7m.cardant.security.CASecurityPolicy.ROLE_INVENTORY_TAGS_WRITER;
import static com.io7m.medrina.api.MRuleConclusion.ALLOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @see CAICmdTagsDelete
 */

public final class CAICmdTagsDeleteTest
  extends CACmdAbstractContract
{
  private static final CATag TAG_0 = new CATag(CATagID.random(), "x");
  private static final CATag TAG_1 = new CATag(CATagID.random(), "y");
  private static final CATag TAG_2 = new CATag(CATagID.random(), "z");

  /**
   * Updating tags requires the permission to DELETE to INVENTORY_TAGS.
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
      new CAICmdTagsDelete();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandTagsDelete(
            new CATags(new TreeSet<>(
              Set.of(TAG_0)
            ))
          ));
      });

    /* Assert. */

    assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
  }

  /**
   * Deleting tags works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testUpdates()
    throws Exception
  {
    /* Arrange. */

    final var tags =
      mock(CADatabaseQueriesTagsType.class);
    final var transaction =
      this.transaction();

    when(transaction.queries(CADatabaseQueriesTagsType.class))
      .thenReturn(tags);

    CASecurity.setPolicy(new MPolicy(List.of(
      new MRule(
        MRuleName.of("rule0"),
        "",
        ALLOW,
        new MMatchSubjectWithRolesAny(Set.of(ROLE_INVENTORY_TAGS_WRITER)),
        new MMatchObjectWithType(INVENTORY_TAGS.type()),
        new MMatchActionWithName(DELETE)
      )
    )));

    this.setRoles(ROLE_INVENTORY_TAGS_WRITER);

    final var context =
      this.createContext();

    /* Act. */

    final var handler = new CAICmdTagsDelete();
    handler.execute(
      context,
      new CAICommandTagsDelete(
        new CATags(new TreeSet<>(Set.of(TAG_0, TAG_1, TAG_2)))
      ));

    /* Assert. */

    verify(transaction)
      .queries(CADatabaseQueriesTagsType.class);
    verify(tags)
      .tagDelete(TAG_0);
    verify(tags)
      .tagDelete(TAG_1);
    verify(tags)
      .tagDelete(TAG_2);

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(tags);
  }
}