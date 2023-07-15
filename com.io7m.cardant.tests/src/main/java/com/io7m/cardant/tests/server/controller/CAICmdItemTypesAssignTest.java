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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.GetType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.TypesAssignType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeDeclarationGetMultipleType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CATypeDeclaration;
import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.model.CATypeScalar;
import com.io7m.cardant.protocol.inventory.CAICommandItemTypesAssign;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdItemTypesAssign;
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
import java.util.TreeSet;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSecurityPolicyDenied;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeCheckFailed;
import static com.io7m.cardant.security.CASecurityPolicy.INVENTORY_ITEMS;
import static com.io7m.cardant.security.CASecurityPolicy.ROLE_INVENTORY_ITEMS_WRITER;
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
 * @see CAICmdItemTypesAssign
 */

public final class CAICmdItemTypesAssignTest
  extends CACmdAbstractContract
{
  private static final CAItemID ITEM_ID = CAItemID.random();

  /**
   * Editing an item requires the permission to WRITE to INVENTORY_ITEMS.
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
      new CAICmdItemTypesAssign();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandItemTypesAssign(
            ITEM_ID,
            Set.of(new RDottedName("t")))
        );
      });

    /* Assert. */

    assertEquals(errorSecurityPolicyDenied(), ex.errorCode());
  }

  /**
   * Assigning a type to an item works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testAssign()
    throws Exception
  {
    /* Arrange. */

    final var itemTypeAssign =
      mock(TypesAssignType.class);
    final var typeGet =
      mock(TypeDeclarationGetMultipleType.class);
    final var itemGet =
      mock(GetType.class);
    final var transaction =
      this.transaction();

    when(transaction.queries(TypesAssignType.class))
      .thenReturn(itemTypeAssign);
    when(transaction.queries(GetType.class))
      .thenReturn(itemGet);
    when(transaction.queries(TypeDeclarationGetMultipleType.class))
      .thenReturn(typeGet);

    when(typeGet.execute(any()))
      .thenReturn(List.of(
        new CATypeDeclaration(
          new RDottedName("t"),
          "A type",
          Map.of()
        )
      ));

    when(itemGet.execute(any()))
      .thenReturn(Optional.of(new CAItem(
        ITEM_ID,
        "Item",
        0L,
        0L,
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        new TreeSet<>(Set.of(new RDottedName("t")))
      )));

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

    final var handler = new CAICmdItemTypesAssign();
    handler.execute(
      context,
      new CAICommandItemTypesAssign(
        ITEM_ID,
        Set.of(new RDottedName("t"))));

    /* Assert. */

    verify(transaction)
      .queries(GetType.class);
    verify(transaction)
      .queries(TypeDeclarationGetMultipleType.class);
    verify(transaction)
      .queries(TypesAssignType.class);
    verify(itemGet)
      .execute(ITEM_ID);

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(itemGet);
  }

  /**
   * Assigning a type fails if type checking fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testAssignCheckFailed()
    throws Exception
  {
    /* Arrange. */

    final var itemTypeAssign =
      mock(TypesAssignType.class);
    final var typeGet =
      mock(TypeDeclarationGetMultipleType.class);
    final var itemGet =
      mock(GetType.class);
    final var transaction =
      this.transaction();

    when(transaction.queries(TypesAssignType.class))
      .thenReturn(itemTypeAssign);
    when(transaction.queries(GetType.class))
      .thenReturn(itemGet);
    when(transaction.queries(TypeDeclarationGetMultipleType.class))
      .thenReturn(typeGet);

    when(typeGet.execute(any()))
      .thenReturn(List.of(
        new CATypeDeclaration(
          new RDottedName("t"),
          "A type",
          Map.of(
            new RDottedName("a"),
            new CATypeField(
              new RDottedName("a"),
              "A field",
              new CATypeScalar(new RDottedName("z"), "x", ".*"),
              true
            )
          )
        )
      ));

    when(itemGet.execute(any()))
      .thenReturn(Optional.of(new CAItem(
        ITEM_ID,
        "Item",
        0L,
        0L,
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        new TreeSet<>(Set.of(new RDottedName("t")))
      )));

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

    final var handler = new CAICmdItemTypesAssign();

    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandItemTypesAssign(
            ITEM_ID,
            Set.of(new RDottedName("t")))
        );
      });

    /* Assert. */

    assertEquals(errorTypeCheckFailed(), ex.errorCode());

    verify(transaction)
      .queries(GetType.class);
    verify(transaction)
      .queries(TypeDeclarationGetMultipleType.class);
    verify(transaction)
      .queries(TypesAssignType.class);
    verify(itemGet)
      .execute(ITEM_ID);

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(itemGet);
  }

  /**
   * Assigning a type to a nonexistent item fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testAssignNonexistent()
    throws Exception
  {
    /* Arrange. */

    final var itemTypeAssign =
      mock(TypesAssignType.class);
    final var itemGet =
      mock(GetType.class);
    final var transaction =
      this.transaction();

    when(transaction.queries(TypesAssignType.class))
      .thenReturn(itemTypeAssign);
    when(transaction.queries(GetType.class))
      .thenReturn(itemGet);

    doThrow(
      new CADatabaseException(
        "Nonexistent.",
        errorNonexistent(),
        Map.of(),
        Optional.empty())
    ).when(itemTypeAssign)
      .execute(any());

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

    final var handler = new CAICmdItemTypesAssign();

    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandItemTypesAssign(
            ITEM_ID,
            Set.of(new RDottedName("t")))
        );
      });

    /* Assert. */

    assertEquals(errorNonexistent(), ex.errorCode());
  }
}
