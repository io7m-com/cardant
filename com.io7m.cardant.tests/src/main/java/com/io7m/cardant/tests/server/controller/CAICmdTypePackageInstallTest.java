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

import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageInstallType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageSatisfyingType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeRecordGetType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeScalarGetType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageInstall;
import com.io7m.cardant.protocol.inventory.CAIResponseError;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdTypePackageInstall;
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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorParse;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSecurityPolicyDenied;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeCheckFailed;
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
 * @see CAICmdTypePackageInstall
 */

public final class CAICmdTypePackageInstallTest
  extends CACmdAbstractContract
{
  private String text;
  private String textUncheckable;

  @BeforeEach
  public void setup()
    throws Exception
  {
    this.text =
      new String(
        CAICmdTypePackageInstallTest.class.getResourceAsStream(
            "/com/io7m/cardant/tests/tpack1.xml")
          .readAllBytes(),
        StandardCharsets.UTF_8
      );

    this.textUncheckable =
      new String(
        CAICmdTypePackageInstallTest.class.getResourceAsStream(
            "/com/io7m/cardant/tests/tpack0.xml")
          .readAllBytes(),
        StandardCharsets.UTF_8
      );
  }

  /**
   * Installing a type package requires the permission to WRITE to INVENTORY_ITEMS.
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
      new CAICmdTypePackageInstall();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandTypePackageInstall(this.text));
      });

    /* Assert. */

    assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
  }

  /**
   * Installing a type package works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testInstall()
    throws Exception
  {
    /* Arrange. */

    final var install =
      mock(TypePackageInstallType.class);
    final var satisfy =
      mock(TypePackageSatisfyingType.class);

    final var transaction =
      this.transaction();

    when(transaction.queries(TypePackageSatisfyingType.class))
      .thenReturn(satisfy);
    when(transaction.queries(TypePackageInstallType.class))
      .thenReturn(install);
    when(install.execute(any()))
      .thenReturn(CADatabaseUnit.UNIT);

    when(satisfy.execute(any()))
      .thenReturn(Optional.of(
        new CATypePackageIdentifier(
          new RDottedName("x.y.z"),
          Version.of(1,2,3)
        )
      ));

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

    final var handler = new CAICmdTypePackageInstall();
    handler.execute(
      context,
      new CAICommandTypePackageInstall(this.text));

    /* Assert. */

    verify(transaction)
      .queries(TypePackageInstallType.class);
    verify(transaction)
      .queries(TypePackageSatisfyingType.class);
    verify(install)
      .execute(any());
    verify(satisfy)
      .execute(any());

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(install);
    verifyNoMoreInteractions(satisfy);
  }

  /**
   * Installing a type package fails on parse errors.
   *
   * @throws Exception On errors
   */

  @Test
  public void testInstallUnparseable()
    throws Exception
  {
    /* Arrange. */

    final var install =
      mock(TypePackageInstallType.class);
    final var satisfy =
      mock(TypePackageSatisfyingType.class);

    final var transaction =
      this.transaction();

    when(transaction.queries(TypePackageSatisfyingType.class))
      .thenReturn(satisfy);
    when(transaction.queries(TypePackageInstallType.class))
      .thenReturn(install);

    when(satisfy.execute(any()))
      .thenReturn(Optional.of(
        new CATypePackageIdentifier(
          new RDottedName("x.y.z"),
          Version.of(1,2,3)
        )
      ));

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
      new CAICmdTypePackageInstall();
    final var r =
      handler.execute(
        context,
        new CAICommandTypePackageInstall("What is this?")
      );
    final var error = assertInstanceOf(CAIResponseError.class, r);
    assertEquals(errorParse(), error.errorCode());

    /* Assert. */

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(install);
    verifyNoMoreInteractions(satisfy);
  }

  /**
   * Installing a type package fails on checker errors.
   *
   * @throws Exception On errors
   */

  @Test
  public void testInstallUncheckable()
    throws Exception
  {
    /* Arrange. */

    final var install =
      mock(TypePackageInstallType.class);
    final var satisfy =
      mock(TypePackageSatisfyingType.class);
    final var typeScalar =
      mock(TypeScalarGetType.class);
    final var typeRecord =
      mock(TypeRecordGetType.class);

    final var transaction =
      this.transaction();

    when(transaction.queries(TypePackageSatisfyingType.class))
      .thenReturn(satisfy);
    when(transaction.queries(TypePackageInstallType.class))
      .thenReturn(install);
    when(transaction.queries(TypeScalarGetType.class))
      .thenReturn(typeScalar);
    when(transaction.queries(TypeRecordGetType.class))
      .thenReturn(typeRecord);

    when(satisfy.execute(any()))
      .thenReturn(Optional.of(
        new CATypePackageIdentifier(
          new RDottedName("x.y.z"),
          Version.of(1,2,3)
        )
      ));

    when(typeScalar.execute(any()))
      .thenReturn(Optional.empty());

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
      new CAICmdTypePackageInstall();
    final var r =
      handler.execute(
        context,
        new CAICommandTypePackageInstall(this.textUncheckable)
      );
    final var error = assertInstanceOf(CAIResponseError.class, r);
    assertEquals(errorTypeCheckFailed(), error.errorCode());

    /* Assert. */

    verify(transaction)
      .queries(TypePackageSatisfyingType.class);
    verify(transaction)
      .queries(TypeScalarGetType.class);
    verify(satisfy)
      .execute(any());

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(install);
    verifyNoMoreInteractions(satisfy);
  }
}
