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
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.MetadataRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.MetadataRemoveType.Parameters;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeDeclarationGetMultipleType;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.model.CATypeRecord;
import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.cardant.protocol.inventory.CAICommandLocationMetadataRemove;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdLocationMetadataRemove;
import com.io7m.lanark.core.RDottedName;
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
import java.util.TreeSet;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSecurityPolicyDenied;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeCheckFailed;
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
 * @see CAICmdLocationMetadataRemove
 */

public final class CAICmdLocationMetadataRemoveTest
  extends CACmdAbstractContract
{
  private static final CALocationID LOCATION_ID = CALocationID.random();

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
      new CAICmdLocationMetadataRemove();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandLocationMetadataRemove(
            LOCATION_ID,
            Set.of(new RDottedName("x"))));
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
    final var locationMetaRemove =
      mock(MetadataRemoveType.class);
    final var typeGet =
      mock(TypeDeclarationGetMultipleType.class);

    final var transaction =
      this.transaction();

    when(transaction.queries(CADatabaseQueriesLocationsType.GetType.class))
      .thenReturn(locationGet);
    when(transaction.queries(MetadataRemoveType.class))
      .thenReturn(locationMetaRemove);
    when(transaction.queries(TypeDeclarationGetMultipleType.class))
      .thenReturn(typeGet);

    when(typeGet.execute(any()))
      .thenReturn(List.of());

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

    final var handler = new CAICmdLocationMetadataRemove();
    handler.execute(
      context,
      new CAICommandLocationMetadataRemove(
        LOCATION_ID,
        Set.of(new RDottedName("a"), new RDottedName("b"), new RDottedName("c"))
      ));

    /* Assert. */

    verify(transaction)
      .queries(CADatabaseQueriesLocationsType.GetType.class);
    verify(transaction)
      .queries(MetadataRemoveType.class);
    verify(transaction)
      .queries(TypeDeclarationGetMultipleType.class);
    verify(transaction)
      .setUserId(context.session().userId());
    verify(locationMetaRemove)
      .execute(
        new Parameters(
          LOCATION_ID,
          Set.of(
            new RDottedName("a"),
            new RDottedName("b"),
            new RDottedName("c")
          )

        )
      );
    verify(locationGet)
      .execute(LOCATION_ID);

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(locationGet);
    verifyNoMoreInteractions(locationMetaRemove);
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
    final var locationMetaRemove =
      mock(MetadataRemoveType.class);

    doThrow(
      new CADatabaseException(
        "Nonexistent.",
        errorNonexistent(),
        Map.of(),
        Optional.empty())
    ).when(locationMetaRemove)
      .execute(any());

    final var transaction =
      this.transaction();

    when(transaction.queries(CADatabaseQueriesLocationsType.GetType.class))
      .thenReturn(locationGet);
    when(transaction.queries(MetadataRemoveType.class))
      .thenReturn(locationMetaRemove);

    doThrow(new CADatabaseException(
      "X",
      errorNonexistent(),
      Map.of(),
      Optional.empty()))
      .when(locationMetaRemove)
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

    final var handler = new CAICmdLocationMetadataRemove();

    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandLocationMetadataRemove(
            LOCATION_ID,
            Set.of(new RDottedName("a"), new RDottedName("b"), new RDottedName("c"))
          ));
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
    final var locationMetaRemove =
      mock(MetadataRemoveType.class);

    doThrow(
      new CADatabaseException(
        "Nonexistent.",
        errorNonexistent(),
        Map.of(),
        Optional.empty())
    ).when(locationMetaRemove)
      .execute(any());

    final var transaction =
      this.transaction();

    when(transaction.queries(CADatabaseQueriesLocationsType.GetType.class))
      .thenReturn(locationGet);
    when(transaction.queries(MetadataRemoveType.class))
      .thenReturn(locationMetaRemove);

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

    final var handler = new CAICmdLocationMetadataRemove();

    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandLocationMetadataRemove(
            LOCATION_ID,
            Set.of(new RDottedName("a"), new RDottedName("b"), new RDottedName("c"))
          ));
      });

    /* Assert. */

    assertEquals(errorNonexistent(), ex.errorCode());
  }

  /**
   * Updating fails if type checking fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeChecking0()
    throws Exception
  {
    /* Arrange. */

    final var locationGet =
      mock(CADatabaseQueriesLocationsType.GetType.class);
    final var locationMetaRemove =
      mock(MetadataRemoveType.class);
    final var typeGet =
      mock(TypeDeclarationGetMultipleType.class);

    final var transaction =
      this.transaction();

    when(transaction.queries(CADatabaseQueriesLocationsType.GetType.class))
      .thenReturn(locationGet);
    when(transaction.queries(MetadataRemoveType.class))
      .thenReturn(locationMetaRemove);
    when(transaction.queries(TypeDeclarationGetMultipleType.class))
      .thenReturn(typeGet);

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

    final var meta0 =
      new CAMetadataType.Text(new RDottedName("a"), "x");

    when(locationGet.execute(any()))
      .thenReturn(Optional.of(
        new CALocation(
          CALocationID.random(),
          Optional.empty(),
          "Location",
          new TreeMap<>(Map.of(meta0.name(), meta0)),
          Collections.emptySortedMap(),
          new TreeSet<>(
            Set.of(new RDottedName("t"))
          )
        )
      ));

    final var type =
      new CATypeRecord(
        new RDottedName("t"),
        "T",
        Map.ofEntries(
          Map.entry(
            new RDottedName("a"),
            new CATypeField(
              new RDottedName("a"),
              "Field A",
              new CATypeScalarType.Integral(
                new RDottedName("ts0"),
                "Number",
                23L,
                1000L
              ),
              true
            )
          ),
          Map.entry(
            new RDottedName("b"),
            new CATypeField(
              new RDottedName("b"),
              "Field B",
              new CATypeScalarType.Text(
                new RDottedName("ts0"),
                "Anything",
                ".*"
              ),
              true
            )
          )
        )
      );

    when(typeGet.execute(any()))
      .thenReturn(List.of(type));

    /* Act. */

    final var handler = new CAICmdLocationMetadataRemove();

    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandLocationMetadataRemove(
            LOCATION_ID,
            Set.of(meta0.name())
          ));
      });

    /* Assert. */

    assertEquals(errorTypeCheckFailed(), ex.errorCode());

    verify(transaction)
      .queries(CADatabaseQueriesLocationsType.GetType.class);
    verify(transaction)
      .queries(MetadataRemoveType.class);
    verify(transaction)
      .queries(TypeDeclarationGetMultipleType.class);
    verify(transaction)
      .setUserId(context.session().userId());

    verify(locationGet)
      .execute(LOCATION_ID);
    verify(locationMetaRemove)
      .execute(new Parameters(LOCATION_ID, Set.of(meta0.name())));

    verifyNoMoreInteractions(locationGet);
    verifyNoMoreInteractions(locationMetaRemove);
    verifyNoMoreInteractions(transaction);
  }
}
