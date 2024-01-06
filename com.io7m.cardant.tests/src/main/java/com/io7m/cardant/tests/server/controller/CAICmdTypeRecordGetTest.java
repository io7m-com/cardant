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

import com.io7m.cardant.database.api.CADatabaseQueriesTypesType;
import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.model.CATypeRecord;
import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.protocol.inventory.CAICommandTypeRecordGet;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdTypeRecordGet;
import com.io7m.lanark.core.RDottedName;
import com.io7m.medrina.api.MMatchActionType.MMatchActionWithName;
import com.io7m.medrina.api.MMatchObjectType.MMatchObjectWithType;
import com.io7m.medrina.api.MMatchSubjectType.MMatchSubjectWithRolesAny;
import com.io7m.medrina.api.MPolicy;
import com.io7m.medrina.api.MRule;
import com.io7m.medrina.api.MRuleName;
import com.io7m.verona.core.Version;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSecurityPolicyDenied;
import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_ITEMS;
import static com.io7m.cardant.security.CASecurityPolicy.READ;
import static com.io7m.cardant.security.CASecurityPolicy.ROLE_INVENTORY_ITEMS_READER;
import static com.io7m.medrina.api.MRuleConclusion.ALLOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * @see CAICmdTypeRecordGet
 */

public final class CAICmdTypeRecordGetTest
  extends CACmdAbstractContract
{
  private static final CATypePackageIdentifier P =
    new CATypePackageIdentifier(
      new RDottedName("com.io7m"),
      Version.of(1,0,0)
    );

  private static final CATypeScalarType.Integral TYPE_SCALAR =
    new CATypeScalarType.Integral(
      P,
      new RDottedName("a"),
      "b",
      23L,
      1000L
    );

  static final CATypeField TYPE_FIELD_0 =
    new CATypeField(
      new RDottedName("x"),
      "xd",
      TYPE_SCALAR,
      true
    );

  static final CATypeField TYPE_FIELD_1 =
    new CATypeField(
      new RDottedName("y"),
      "yd",
      TYPE_SCALAR,
      true
    );

  static final CATypeField TYPE_FIELD_2 =
    new CATypeField(
      new RDottedName("z"),
      "zd",
      TYPE_SCALAR,
      true
    );

  static final CATypeRecord TYPE_DECLARATION =
    new CATypeRecord(
      P,
      new RDottedName("a.b.c"),
      "a",
      Map.ofEntries(
        Map.entry(TYPE_FIELD_0.name(), TYPE_FIELD_0),
        Map.entry(TYPE_FIELD_1.name(), TYPE_FIELD_1),
        Map.entry(TYPE_FIELD_2.name(), TYPE_FIELD_2)
      )
    );

  /**
   * Retrieving an item requires the permission to READ to INVENTORY_ITEMS.
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
      new CAICmdTypeRecordGet();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandTypeRecordGet(new RDottedName("a.b.c")));
      });

    /* Assert. */

    assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
  }

  /**
   * Retrieving an item works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testGets()
    throws Exception
  {
    /* Arrange. */

    final var itemGet =
      mock(CADatabaseQueriesTypesType.TypeRecordGetType.class);
    final var transaction =
      this.transaction();

    when(transaction.queries(CADatabaseQueriesTypesType.TypeRecordGetType.class))
      .thenReturn(itemGet);
    when(itemGet.execute(any()))
      .thenReturn(Optional.of(TYPE_DECLARATION));

    CASecurity.setPolicy(new MPolicy(List.of(
      new MRule(
        MRuleName.of("rule0"),
        "",
        ALLOW,
        new MMatchSubjectWithRolesAny(Set.of(ROLE_INVENTORY_ITEMS_READER)),
        new MMatchObjectWithType(INVENTORY_ITEMS.type()),
        new MMatchActionWithName(READ)
      )
    )));

    this.setRoles(ROLE_INVENTORY_ITEMS_READER);

    final var context =
      this.createContext();

    /* Act. */

    final var handler = new CAICmdTypeRecordGet();
    handler.execute(
      context,
      new CAICommandTypeRecordGet(new RDottedName("a.b.c")));

    /* Assert. */

    verify(transaction)
      .queries(CADatabaseQueriesTypesType.TypeRecordGetType.class);
    verify(itemGet)
      .execute(new RDottedName("a.b.c"));

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(itemGet);
  }

  /**
   * Getting a nonexistent item fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testGetNonexistent()
    throws Exception
  {
    /* Arrange. */

    final var itemGet =
      mock(CADatabaseQueriesTypesType.TypeRecordGetType.class);
    final var transaction =
      this.transaction();

    when(transaction.queries(CADatabaseQueriesTypesType.TypeRecordGetType.class))
      .thenReturn(itemGet);

    when(itemGet.execute(any()))
      .thenReturn(Optional.empty());

    CASecurity.setPolicy(new MPolicy(List.of(
      new MRule(
        MRuleName.of("rule0"),
        "",
        ALLOW,
        new MMatchSubjectWithRolesAny(Set.of(ROLE_INVENTORY_ITEMS_READER)),
        new MMatchObjectWithType(INVENTORY_ITEMS.type()),
        new MMatchActionWithName(READ)
      )
    )));

    this.setRoles(ROLE_INVENTORY_ITEMS_READER);

    final var context =
      this.createContext();

    /* Act. */

    final var handler = new CAICmdTypeRecordGet();

    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandTypeRecordGet(new RDottedName("a.b.c")));
      });

    /* Assert. */

    assertEquals(errorNonexistent(), ex.errorCode());
  }
}
