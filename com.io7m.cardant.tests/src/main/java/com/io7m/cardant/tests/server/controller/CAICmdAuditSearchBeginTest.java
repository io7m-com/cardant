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

import com.io7m.cardant.database.api.CADatabaseAuditSearchType;
import com.io7m.cardant.database.api.CADatabaseQueriesAuditType;
import com.io7m.cardant.model.CAAuditEvent;
import com.io7m.cardant.model.CAAuditSearchParameters;
import com.io7m.cardant.model.CAPage;
import com.io7m.cardant.model.CATimeRange;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.cardant.protocol.inventory.CAICommandAuditSearchBegin;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdAuditSearchBegin;
import com.io7m.medrina.api.MMatchActionType.MMatchActionWithName;
import com.io7m.medrina.api.MMatchObjectType.MMatchObjectWithType;
import com.io7m.medrina.api.MMatchSubjectType.MMatchSubjectWithRolesAny;
import com.io7m.medrina.api.MPolicy;
import com.io7m.medrina.api.MRule;
import com.io7m.medrina.api.MRuleName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSecurityPolicyDenied;
import static com.io7m.cardant.security.CASecurityPolicy.AUDIT;
import static com.io7m.cardant.security.CASecurityPolicy.READ;
import static com.io7m.cardant.security.CASecurityPolicy.ROLE_AUDIT_READER;
import static com.io7m.medrina.api.MRuleConclusion.ALLOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @see CAICmdAuditSearchBegin
 */

public final class CAICmdAuditSearchBeginTest
  extends CACmdAbstractContract
{
  private static final CAAuditSearchParameters PARAMETERS =
    new CAAuditSearchParameters(
      Optional.empty(),
      new CAComparisonExactType.Anything<>(),
      new CATimeRange(
        OffsetDateTime.now().minusDays(365L),
        OffsetDateTime.now().plusDays(356L)
      ),
      100L
    );

  /**
   * Searching for events requires the permission to READ to AUDIT.
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
      new CAICmdAuditSearchBegin();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandAuditSearchBegin(PARAMETERS));
      });

    /* Assert. */

    assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
  }

  /**
   * Searching for audit events works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testSearch()
    throws Exception
  {
    /* Arrange. */

    final var events =
      mock(CADatabaseQueriesAuditType.EventSearchType.class);
    final var fileSearch =
      mock(CADatabaseAuditSearchType.class);

    final var transaction =
      this.transaction();

    final var pageMain =
      new CAPage<>(
        List.of(
          new CAAuditEvent(
            0L,
            OffsetDateTime.now().withNano(0).plusSeconds(1L),
            CAUserID.random(),
            "T",
            Map.of()
          ),
          new CAAuditEvent(
            1L,
            OffsetDateTime.now().withNano(0).plusSeconds(2L),
            CAUserID.random(),
            "U",
            Map.of()
          ),
          new CAAuditEvent(
            2L,
            OffsetDateTime.now().withNano(0).plusSeconds(3L),
            CAUserID.random(),
            "V",
            Map.of()
          )
        ),
        1,
        1,
        0L
      );

    when(transaction.queries(CADatabaseQueriesAuditType.EventSearchType.class))
      .thenReturn(events);
    when(events.execute(any()))
      .thenReturn(fileSearch);
    when(fileSearch.pageCurrent(any()))
      .thenReturn(pageMain);

    CASecurity.setPolicy(new MPolicy(List.of(
      new MRule(
        MRuleName.of("rule0"),
        "",
        ALLOW,
        new MMatchSubjectWithRolesAny(Set.of(ROLE_AUDIT_READER)),
        new MMatchObjectWithType(AUDIT.type()),
        new MMatchActionWithName(READ)
      )
    )));

    this.setRoles(ROLE_AUDIT_READER);

    final var context =
      this.createContext();

    /* Act. */

    final var handler = new CAICmdAuditSearchBegin();

    handler.execute(context, new CAICommandAuditSearchBegin(PARAMETERS));

    /* Assert. */

    verify(transaction)
      .queries(CADatabaseQueriesAuditType.EventSearchType.class);
    verify(events)
      .execute(PARAMETERS);
    verify(fileSearch)
      .pageCurrent(transaction);

    verifyNoMoreInteractions(fileSearch);
    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(events);
  }
}
