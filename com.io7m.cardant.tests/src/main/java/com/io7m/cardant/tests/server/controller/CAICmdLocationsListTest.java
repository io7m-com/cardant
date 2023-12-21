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
import com.io7m.cardant.protocol.inventory.CAICommandLocationList;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdLocationsList;
import com.io7m.medrina.api.MMatchActionType.MMatchActionWithName;
import com.io7m.medrina.api.MMatchObjectType.MMatchObjectWithType;
import com.io7m.medrina.api.MMatchSubjectType.MMatchSubjectWithRolesAny;
import com.io7m.medrina.api.MPolicy;
import com.io7m.medrina.api.MRule;
import com.io7m.medrina.api.MRuleName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSecurityPolicyDenied;
import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_LOCATIONS;
import static com.io7m.cardant.security.CASecurityPolicy.READ;
import static com.io7m.cardant.security.CASecurityPolicy.ROLE_INVENTORY_LOCATIONS_READER;
import static com.io7m.medrina.api.MRuleConclusion.ALLOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @see CAICmdLocationsList
 */

public final class CAICmdLocationsListTest
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
   * Reading locations requires the permission to READ to INVENTORY_LOCATIONS.
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
      new CAICmdLocationsList();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandLocationList());
      });

    /* Assert. */

    assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
  }

  /**
   * Reading locations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testList()
    throws Exception
  {
    /* Arrange. */

    final var locPut =
      mock(CADatabaseQueriesLocationsType.PutType.class);
    final var locList =
      mock(CADatabaseQueriesLocationsType.ListType.class);
    final var transaction =
      this.transaction();

    when(transaction.queries(CADatabaseQueriesLocationsType.PutType.class))
      .thenReturn(locPut);
    when(transaction.queries(CADatabaseQueriesLocationsType.ListType.class))
      .thenReturn(locList);

    when(locList.execute(UNIT))
      .thenReturn(new TreeMap<>(
        Map.ofEntries(
          Map.entry(LOCATION_0.id(), LOCATION_0.summary()),
          Map.entry(LOCATION_1.id(), LOCATION_1.summary()),
          Map.entry(LOCATION_2.id(), LOCATION_2.summary())
        )
      ));

    CASecurity.setPolicy(new MPolicy(List.of(
      new MRule(
        MRuleName.of("rule0"),
        "",
        ALLOW,
        new MMatchSubjectWithRolesAny(Set.of(ROLE_INVENTORY_LOCATIONS_READER)),
        new MMatchObjectWithType(INVENTORY_LOCATIONS.type()),
        new MMatchActionWithName(READ)
      )
    )));

    this.setRoles(ROLE_INVENTORY_LOCATIONS_READER);

    final var context =
      this.createContext();

    /* Act. */

    final var handler = new CAICmdLocationsList();
    handler.execute(
      context,
      new CAICommandLocationList());

    /* Assert. */

    verify(transaction)
      .queries(CADatabaseQueriesLocationsType.ListType.class);
    verify(locList)
      .execute(UNIT);

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(locList);
  }
}
