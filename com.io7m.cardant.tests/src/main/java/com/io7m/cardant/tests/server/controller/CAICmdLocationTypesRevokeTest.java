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
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationGetType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationTypesRevokeType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageGetTextType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageSatisfyingType;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.protocol.inventory.CAICommandLocationTypesRevoke;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdLocationTypesRevoke;
import com.io7m.lanark.core.RDottedName;
import com.io7m.medrina.api.MMatchActionType.MMatchActionWithName;
import com.io7m.medrina.api.MMatchObjectType.MMatchObjectWithType;
import com.io7m.medrina.api.MMatchSubjectType.MMatchSubjectWithRolesAny;
import com.io7m.medrina.api.MPolicy;
import com.io7m.medrina.api.MRule;
import com.io7m.medrina.api.MRuleName;
import com.io7m.verona.core.Version;
import org.junit.jupiter.api.Test;
import org.mockito.internal.verification.Times;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

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
 * @see CAICmdLocationTypesRevoke
 */

public final class CAICmdLocationTypesRevokeTest
  extends CACmdAbstractContract
{
  private static final CATypePackageIdentifier P =
    new CATypePackageIdentifier(
      new RDottedName("com.io7m"),
      Version.of(1, 0, 0)
    );

  private static final String P_TEXT = """
    <?xml version="1.0" encoding="UTF-8" ?>
    <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
      <p:PackageInfo Name="com.io7m"
                     Version="1.0.0"
                     Description="An example."/>
      <p:TypeScalarText Name="s" Description="A text type." Pattern=".*"/>
      <p:TypeRecord Name="t0" Description="A record type.">
        <p:Field Name="q" Description="A Q field." Type="s"/>
      </p:TypeRecord>
    </p:Package>
    """;

  private static final CALocationID LOCATION_ID = CALocationID.random();

  /**
   * Editing a location requires the permission to WRITE to INVENTORY_LOCATIONS.
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
      new CAICmdLocationTypesRevoke();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandLocationTypesRevoke(
            LOCATION_ID,
            Set.of(CATypeRecordIdentifier.of("com.io7m:t")))
        );
      });

    /* Assert. */

    assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
  }

  /**
   * Revoking a type from a location works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRevoke()
    throws Exception
  {
    /* Arrange. */

    final var locationGet =
      mock(LocationGetType.class);
    final var locationTypeRevoke =
      mock(LocationTypesRevokeType.class);
    final var typePackageSatisfying =
      mock(TypePackageSatisfyingType.class);
    final var typePackageGetText =
      mock(TypePackageGetTextType.class);

    final var transaction =
      this.transaction();

    when(transaction.queries(LocationGetType.class))
      .thenReturn(locationGet);
    when(transaction.queries(LocationTypesRevokeType.class))
      .thenReturn(locationTypeRevoke);
    when(transaction.queries(TypePackageSatisfyingType.class))
      .thenReturn(typePackageSatisfying);
    when(transaction.queries(TypePackageGetTextType.class))
      .thenReturn(typePackageGetText);

    when(locationGet.execute(any()))
      .thenReturn(Optional.of(new CALocation(
        LOCATION_ID,
        Optional.empty(),
        "Location",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        new TreeSet<>()
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

    when(typePackageSatisfying.execute(any()))
      .thenReturn(Optional.of(P));
    when(typePackageGetText.execute(any()))
      .thenReturn(Optional.of(P_TEXT));

    /* Act. */

    final var handler = new CAICmdLocationTypesRevoke();
    handler.execute(
      context,
      new CAICommandLocationTypesRevoke(
        LOCATION_ID,
        Set.of(CATypeRecordIdentifier.of("com.io7m:t0"))
      )
    );

    /* Assert. */

    verify(transaction)
      .queries(LocationGetType.class);
    verify(transaction)
      .queries(LocationTypesRevokeType.class);
    verify(locationGet, new Times(2))
      .execute(LOCATION_ID);

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(locationGet);
  }

  /**
   * Revoking a type to a nonexistent location fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRevokeNonexistent()
    throws Exception
  {
    /* Arrange. */

    final var locationTypeRevoke =
      mock(LocationTypesRevokeType.class);
    final var locationGet =
      mock(LocationGetType.class);
    final var transaction =
      this.transaction();

    when(transaction.queries(LocationTypesRevokeType.class))
      .thenReturn(locationTypeRevoke);
    when(transaction.queries(LocationGetType.class))
      .thenReturn(locationGet);

    when(locationGet.execute(any()))
      .thenReturn(Optional.of(new CALocation(
        LOCATION_ID,
        Optional.empty(),
        "Location",
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        new TreeSet<>()
      )));

    doThrow(
      new CADatabaseException(
        "Nonexistent.",
        errorNonexistent(),
        Map.of(),
        Optional.empty())
    ).when(locationTypeRevoke)
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

    final var handler = new CAICmdLocationTypesRevoke();

    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandLocationTypesRevoke(
            LOCATION_ID,
            Set.of(CATypeRecordIdentifier.of("com.io7m:t"))
          )
        );
      });

    /* Assert. */

    assertEquals(errorNonexistent(), ex.errorCode());
  }
}
