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

import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.protocol.inventory.CAICommandLocationPut;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdLocationPut;
import com.io7m.medrina.api.MMatchActionType.MMatchActionWithName;
import com.io7m.medrina.api.MMatchObjectType.MMatchObjectWithType;
import com.io7m.medrina.api.MMatchSubjectType.MMatchSubjectWithRolesAny;
import com.io7m.medrina.api.MPolicy;
import com.io7m.medrina.api.MRule;
import com.io7m.medrina.api.MRuleName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSecurityPolicyDenied;
import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_LOCATIONS;
import static com.io7m.cardant.security.CASecurityPolicy.ROLE_INVENTORY_LOCATIONS_WRITER;
import static com.io7m.cardant.security.CASecurityPolicy.WRITE;
import static com.io7m.medrina.api.MRuleConclusion.ALLOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @see CAICmdLocationPut
 */

public final class CAICmdLocationPutTest
  extends CACmdAbstractContract
{
  private static final CALocation LOCATION_0 =
    new CALocation(
      CALocationID.random(),
      Optional.empty(),
      "A",
      Collections.emptySortedMap(),
      Collections.emptySortedMap(),
      Collections.emptySortedSet()
    );
  private static final CALocation LOCATION_1 =
    new CALocation(
      CALocationID.random(),
      Optional.of(LOCATION_0.id()),
      "B",
      Collections.emptySortedMap(),
      Collections.emptySortedMap(),
      Collections.emptySortedSet()
    );
  private static final CALocation LOCATION_2 =
    new CALocation(
      CALocationID.random(),
      Optional.of(LOCATION_1.id()),
      "C",
      Collections.emptySortedMap(),
      Collections.emptySortedMap(),
      Collections.emptySortedSet()
    );

  /**
   * Updating locations requires the permission to WRITE to INVENTORY_LOCATIONS.
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
      new CAICmdLocationPut();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandLocationPut(LOCATION_0));
      });

    /* Assert. */

    assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
  }

  /**
   * Updating locations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testUpdates()
    throws Exception
  {
    /* Arrange. */

    final var locPut =
      mock(CADatabaseQueriesLocationsType.PutType.class);
    final var transaction =
      this.transaction();

    when(transaction.queries(CADatabaseQueriesLocationsType.PutType.class))
      .thenReturn(locPut);

    CASecurity.setPolicy(new MPolicy(List.of(
      new MRule(
        MRuleName.of("x"),
        "",
        ALLOW,
        new MMatchSubjectWithRolesAny(Set.of(ROLE_INVENTORY_LOCATIONS_WRITER)),
        new MMatchObjectWithType(INVENTORY_LOCATIONS.type()),
        new MMatchActionWithName(WRITE)
      )
    )));

    this.setRoles(ROLE_INVENTORY_LOCATIONS_WRITER);

    final var context =
      this.createContext();

    /* Act. */

    final var handler = new CAICmdLocationPut();
    handler.execute(
      context,
      new CAICommandLocationPut(
        LOCATION_0
      ));

    /* Assert. */

    verify(transaction)
      .queries(CADatabaseQueriesLocationsType.PutType.class);
    verify(locPut)
      .execute(LOCATION_0);

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(locPut);
  }
}
