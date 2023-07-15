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

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.AttachmentRemoveType.Parameters;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.protocol.inventory.CAICommandLocationAttachmentRemove;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdLocationAttachmentRemove;
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

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSecurityPolicyDenied;
import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_LOCATIONS;
import static com.io7m.cardant.security.CASecurityPolicy.ROLE_INVENTORY_LOCATIONS_WRITER;
import static com.io7m.cardant.security.CASecurityPolicy.WRITE;
import static com.io7m.medrina.api.MRuleConclusion.ALLOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @see CAICmdLocationAttachmentRemove
 */

public final class CAICmdLocationAttachmentRemoveTest
  extends CACmdAbstractContract
{
  private static final CALocationID LOCATION_ID =
    CALocationID.random();
  private static final CAFileID FILE_ID =
    CAFileID.random();

  /**
   * Updating a location requires the permission to WRITE to INVENTORY_LOCATIONS.
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
      new CAICmdLocationAttachmentRemove();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandLocationAttachmentRemove(LOCATION_ID, FILE_ID, "x")
        );
      });

    /* Assert. */

    assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
  }

  /**
   * Updating metadata works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testUpdates()
    throws Exception
  {
    /* Arrange. */

    final var locationGet =
      mock(CADatabaseQueriesLocationsType.GetType.class);
    final var locationAttachRemove =
      mock(CADatabaseQueriesLocationsType.AttachmentRemoveType.class);
    final var transaction =
      this.transaction();

    when(transaction.queries(CADatabaseQueriesLocationsType.GetType.class))
      .thenReturn(locationGet);
    when(transaction.queries(CADatabaseQueriesLocationsType.AttachmentRemoveType.class))
      .thenReturn(locationAttachRemove);

    when(locationGet.execute(any()))
      .thenReturn(Optional.of(new CALocation(
        LOCATION_ID,
        Optional.empty(),
        "Location",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
      )));

    CASecurity.setPolicy(new MPolicy(List.of(
      new MRule(
        MRuleName.of("rule0"),
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

    final var handler = new CAICmdLocationAttachmentRemove();
    handler.execute(
      context,
      new CAICommandLocationAttachmentRemove(LOCATION_ID, FILE_ID, "x")
    );

    /* Assert. */

    verify(transaction)
      .queries(CADatabaseQueriesLocationsType.GetType.class);
    verify(transaction)
      .queries(CADatabaseQueriesLocationsType.AttachmentRemoveType.class);
    verify(locationAttachRemove)
      .execute(new Parameters(LOCATION_ID, FILE_ID, "x"));
    verify(locationGet)
      .execute(LOCATION_ID);

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(locationAttachRemove);
    verifyNoMoreInteractions(locationGet);
  }

  /**
   * Updating a nonexistent location fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testPutNonexistent1()
    throws Exception
  {
    /* Arrange. */

    final var locationGet =
      mock(CADatabaseQueriesLocationsType.GetType.class);
    final var locationAttachRemove =
      mock(CADatabaseQueriesLocationsType.AttachmentRemoveType.class);
    final var transaction =
      this.transaction();

    when(transaction.queries(CADatabaseQueriesLocationsType.GetType.class))
      .thenReturn(locationGet);
    when(transaction.queries(CADatabaseQueriesLocationsType.AttachmentRemoveType.class))
      .thenReturn(locationAttachRemove);

    doThrow(new CADatabaseException(
      "X",
      errorNonexistent(),
      Map.of(),
      Optional.empty()))
      .when(locationAttachRemove)
      .execute(any());

    CASecurity.setPolicy(new MPolicy(List.of(
      new MRule(
        MRuleName.of("rule0"),
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

    final var handler = new CAICmdLocationAttachmentRemove();

    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandLocationAttachmentRemove(LOCATION_ID, FILE_ID, "x")
        );
      });

    /* Assert. */

    assertEquals(errorNonexistent(), ex.errorCode());
  }

  /**
   * Updating a nonexistent location fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testPutNonexistent2()
    throws Exception
  {
    /* Arrange. */

    final var locationGet =
      mock(CADatabaseQueriesLocationsType.GetType.class);
    final var locationAttachRemove =
      mock(CADatabaseQueriesLocationsType.AttachmentRemoveType.class);
    final var transaction =
      this.transaction();

    when(transaction.queries(CADatabaseQueriesLocationsType.GetType.class))
      .thenReturn(locationGet);
    when(transaction.queries(CADatabaseQueriesLocationsType.AttachmentRemoveType.class))
      .thenReturn(locationAttachRemove);

    when(locationGet.execute(any()))
      .thenReturn(Optional.empty());

    CASecurity.setPolicy(new MPolicy(List.of(
      new MRule(
        MRuleName.of("rule0"),
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

    final var handler = new CAICmdLocationAttachmentRemove();

    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandLocationAttachmentRemove(LOCATION_ID, FILE_ID, "x")
        );
      });

    /* Assert. */

    assertEquals(errorNonexistent(), ex.errorCode());
  }
}
