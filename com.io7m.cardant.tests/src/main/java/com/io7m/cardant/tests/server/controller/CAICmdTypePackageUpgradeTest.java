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

import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageGetTextType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageInstallType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageSatisfyingType;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageUpgrade;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdTypePackageUpgrade;
import com.io7m.lanark.core.RDottedName;
import com.io7m.medrina.api.MMatchActionType.MMatchActionWithName;
import com.io7m.medrina.api.MMatchObjectType.MMatchObjectWithType;
import com.io7m.medrina.api.MMatchSubjectType.MMatchSubjectWithRolesAny;
import com.io7m.medrina.api.MPolicy;
import com.io7m.medrina.api.MRule;
import com.io7m.medrina.api.MRuleName;
import com.io7m.verona.core.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.verification.Times;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorParse;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSecurityPolicyDenied;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeCheckFailed;
import static com.io7m.cardant.model.type_package.CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED;
import static com.io7m.cardant.model.type_package.CATypePackageVersionBehavior.VERSION_DISALLOW_DOWNGRADES;
import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_ITEMS;
import static com.io7m.cardant.security.CASecurityPolicy.ROLE_INVENTORY_ITEMS_WRITER;
import static com.io7m.cardant.security.CASecurityPolicy.WRITE;
import static com.io7m.medrina.api.MRuleConclusion.ALLOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @see CAICmdTypePackageUpgrade
 */

public final class CAICmdTypePackageUpgradeTest
  extends CACmdAbstractContract
{
  private String text;
  private String textUncheckable;
  private String textZero;

  @BeforeEach
  public void setup()
    throws Exception
  {
    this.textZero = """
      <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
       <p:PackageInfo Name="com.io7m.p" Version="1.0.0" Description="An example."/>
       <p:TypeScalarMonetary Name="t0" Description="A monetary type." RangeLower="0" RangeUpper="1000000.0"/>
       <p:TypeRecord Name="t1" Description="A record type.">
         <p:Field Name="f0" Description="A Q field." Type="t0"/>
       </p:TypeRecord>
      </p:Package>""";

    this.text = """
      <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
       <p:PackageInfo Name="com.io7m.p" Version="1.1.0" Description="An example."/>
       <p:TypeScalarMonetary Name="t0" Description="A monetary type." RangeLower="0" RangeUpper="1000000.0"/>
       <p:TypeRecord Name="t1" Description="A record type.">
         <p:Field Name="f0" Description="A Q field." Type="t0"/>
       </p:TypeRecord>
      </p:Package>""";

    this.textUncheckable = """
      <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
       <p:PackageInfo Name="com.io7m.p" Version="1.1.0" Description="An example."/>
       <p:TypeRecord Name="t1" Description="A record type.">
         <p:Field Name="f0" Description="A Q field." Type="t0"/>
       </p:TypeRecord>
      </p:Package>""";
  }

  /**
   * Upgrading a type package requires the permission to WRITE to INVENTORY_ITEMS.
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
      new CAICmdTypePackageUpgrade();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandTypePackageUpgrade(
            TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED,
            VERSION_DISALLOW_DOWNGRADES,
            this.text
          ));
      });

    /* Assert. */

    assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
  }

  /**
   * Upgrading a type package works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testUpgrade()
    throws Exception
  {
    /* Arrange. */

    final var transaction =
      this.transaction();

    final var satisfy =
      mock(TypePackageSatisfyingType.class);
    final var textGet =
      mock(TypePackageGetTextType.class);
    final var typePackageInstall =
      mock(TypePackageInstallType.class);

    when(transaction.queries(TypePackageSatisfyingType.class))
      .thenReturn(satisfy);
    when(transaction.queries(TypePackageGetTextType.class))
      .thenReturn(textGet);
    when(transaction.queries(TypePackageInstallType.class))
      .thenReturn(typePackageInstall);

    final var typePackageIdentifier =
      new CATypePackageIdentifier(
        new RDottedName("com.io7m.p"),
        Version.of(1, 0, 0)
      );

    when(satisfy.execute(any()))
      .thenReturn(Optional.of(typePackageIdentifier));

    when(textGet.execute(any()))
      .thenReturn(Optional.of(this.textZero));

    CASecurity.setPolicy(new MPolicy(List.of(
      new MRule(
        MRuleName.of("rule0"),
        "",
        ALLOW,
        new MMatchSubjectWithRolesAny(Set.of(ROLE_INVENTORY_ITEMS_WRITER)),
        new MMatchObjectWithType(INVENTORY_ITEMS.type()),
        new MMatchActionWithName(WRITE)
      )
    )));

    this.setRoles(ROLE_INVENTORY_ITEMS_WRITER);

    final var context =
      this.createContext();

    /* Act. */

    final var handler = new CAICmdTypePackageUpgrade();
    handler.execute(
      context,
      new CAICommandTypePackageUpgrade(
        TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED,
        VERSION_DISALLOW_DOWNGRADES,
        this.text
      ));

    /* Assert. */

    verify(transaction)
      .queries(TypePackageInstallType.class);
    verify(typePackageInstall, new Times(1))
      .execute(any());

    verifyNoMoreInteractions(satisfy);
    verifyNoMoreInteractions(textGet);
    verifyNoMoreInteractions(transaction);
  }

  /**
   * Upgrading a type package fails on parse errors.
   *
   * @throws Exception On errors
   */

  @Test
  public void testUpgradeUnparseable()
    throws Exception
  {
    /* Arrange. */

    CASecurity.setPolicy(new MPolicy(List.of(
      new MRule(
        MRuleName.of("rule0"),
        "",
        ALLOW,
        new MMatchSubjectWithRolesAny(Set.of(ROLE_INVENTORY_ITEMS_WRITER)),
        new MMatchObjectWithType(INVENTORY_ITEMS.type()),
        new MMatchActionWithName(WRITE)
      )
    )));

    this.setRoles(ROLE_INVENTORY_ITEMS_WRITER);

    final var context =
      this.createContext();

    /* Act. */

    final var handler =
      new CAICmdTypePackageUpgrade();
    final var r =
      handler.execute(
        context,
        new CAICommandTypePackageUpgrade(
          TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED,
          VERSION_DISALLOW_DOWNGRADES,
          "What is this?"
        )
      );
    final var error = assertInstanceOf(CAIResponseError.class, r);
    assertEquals(errorParse(), error.errorCode());

    /* Assert. */

  }

  /**
   * Upgrading a type package fails on checker errors.
   *
   * @throws Exception On errors
   */

  @Test
  public void testUpgradeUncheckable()
    throws Exception
  {
    /* Arrange. */

    CASecurity.setPolicy(new MPolicy(List.of(
      new MRule(
        MRuleName.of("rule0"),
        "",
        ALLOW,
        new MMatchSubjectWithRolesAny(Set.of(ROLE_INVENTORY_ITEMS_WRITER)),
        new MMatchObjectWithType(INVENTORY_ITEMS.type()),
        new MMatchActionWithName(WRITE)
      )
    )));

    this.setRoles(ROLE_INVENTORY_ITEMS_WRITER);

    final var context =
      this.createContext();

    /* Act. */

    final var handler =
      new CAICmdTypePackageUpgrade();
    final var r =
      handler.execute(
        context,
        new CAICommandTypePackageUpgrade(
          TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED,
          VERSION_DISALLOW_DOWNGRADES,
          this.textUncheckable
        )
      );
    final var error = assertInstanceOf(CAIResponseError.class, r);
    assertEquals(errorTypeCheckFailed(), error.errorCode());

    /* Assert. */

  }
}
