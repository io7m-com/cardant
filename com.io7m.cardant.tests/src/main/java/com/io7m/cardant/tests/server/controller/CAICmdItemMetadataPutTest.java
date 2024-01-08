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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemGetType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemMetadataPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemMetadataPutType.Parameters;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeRecordGetType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.model.CATypeRecord;
import com.io7m.cardant.model.CATypeScalarType.Integral;
import com.io7m.cardant.model.CATypeScalarType.Text;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataPut;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdItemMetadataPut;
import com.io7m.lanark.core.RDottedName;
import com.io7m.medrina.api.MMatchActionType.MMatchActionWithName;
import com.io7m.medrina.api.MMatchObjectType.MMatchObjectWithType;
import com.io7m.medrina.api.MMatchSubjectType.MMatchSubjectWithRolesAny;
import com.io7m.medrina.api.MPolicy;
import com.io7m.medrina.api.MRule;
import com.io7m.medrina.api.MRuleName;
import com.io7m.verona.core.Version;
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
 * @see CAICmdItemMetadataPut
 */

public final class CAICmdItemMetadataPutTest
  extends CACmdAbstractContract
{
  private static final CATypePackageIdentifier P =
    new CATypePackageIdentifier(
      new RDottedName("com.io7m"),
      Version.of(1, 0, 0)
    );

  private static final CAItemID ITEM_ID = CAItemID.random();

  /**
   * Updating an item requires the permission to WRITE to INVENTORY_ITEMS.
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

    final var name0 =
      new RDottedName("com.io7m.name0");

    final var handler =
      new CAICmdItemMetadataPut();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandItemMetadataPut(
            ITEM_ID,
            Set.of(new CAMetadataType.Text(name0, "y"))));
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

    final var itemGet =
      mock(ItemGetType.class);
    final var itemMetaPut =
      mock(ItemMetadataPutType.class);

    final var transaction =
      this.transaction();

    when(transaction.queries(ItemGetType.class))
      .thenReturn(itemGet);
    when(transaction.queries(ItemMetadataPutType.class))
      .thenReturn(itemMetaPut);

    when(itemGet.execute(any()))
      .thenReturn(Optional.of(new CAItem(
        ITEM_ID,
        "Item",
        0L,
        0L,
        Collections.emptySortedMap(),
        Collections.emptySortedMap(),
        Collections.emptySortedSet()
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

    final var name0 =
      new RDottedName("com.io7m.name0");
    final var name1 =
      new RDottedName("com.io7m.name1");
    final var name2 =
      new RDottedName("com.io7m.name2");

    final var handler = new CAICmdItemMetadataPut();
    handler.execute(
      context,
      new CAICommandItemMetadataPut(
        ITEM_ID,
        Set.of(
          new CAMetadataType.Text(name0, "x"),
          new CAMetadataType.Text(name1, "y"),
          new CAMetadataType.Text(name2, "z")
        )
      ));

    /* Assert. */

    verify(transaction)
      .queries(ItemGetType.class);
    verify(transaction)
      .queries(ItemMetadataPutType.class);
    verify(itemMetaPut)
      .execute(new Parameters(
        ITEM_ID,
        Set.of(
          new CAMetadataType.Text(name0, "x"),
          new CAMetadataType.Text(name1, "y"),
          new CAMetadataType.Text(name2, "z")))
      );
    verify(itemGet)
      .execute(ITEM_ID);

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(itemGet);
  }

  /**
   * Updating a nonexistent item fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testPutNonexistent1()
    throws Exception
  {
    /* Arrange. */

    final var itemGet =
      mock(ItemGetType.class);
    final var itemMetaPut =
      mock(ItemMetadataPutType.class);

    doThrow(
      new CADatabaseException(
        "Nonexistent.",
        errorNonexistent(),
        Map.of(),
        Optional.empty())
    ).when(itemMetaPut)
      .execute(any());

    final var transaction =
      this.transaction();

    when(transaction.queries(ItemGetType.class))
      .thenReturn(itemGet);
    when(transaction.queries(ItemMetadataPutType.class))
      .thenReturn(itemMetaPut);

    when(itemGet.execute(any()))
      .thenReturn(Optional.of(
        new CAItem(
          ITEM_ID,
          "x",
          0L,
          0L,
          Collections.emptySortedMap(),
          Collections.emptySortedMap(),
          Collections.emptySortedSet())
      ));

    doThrow(new CADatabaseException(
      "X",
      errorNonexistent(),
      Map.of(),
      Optional.empty()))
      .when(itemMetaPut)
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

    final var handler = new CAICmdItemMetadataPut();

    final var name0 =
      new RDottedName("com.io7m.name0");
    final var name1 =
      new RDottedName("com.io7m.name1");
    final var name2 =
      new RDottedName("com.io7m.name2");

    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandItemMetadataPut(
            ITEM_ID,
            Set.of(
              new CAMetadataType.Text(name0, "x"),
              new CAMetadataType.Text(name1, "y"),
              new CAMetadataType.Text(name2, "z")
            )
          ));
      });

    /* Assert. */

    assertEquals(errorNonexistent(), ex.errorCode());
  }

  /**
   * Updating a nonexistent item fails.
   *
   * @throws Exception On errors
   */

  @Test
  public void testPutNonexistent2()
    throws Exception
  {
    /* Arrange. */

    final var itemGet =
      mock(ItemGetType.class);
    final var itemMetaPut =
      mock(ItemMetadataPutType.class);
    final var typeGet =
      mock(TypeRecordGetType.class);

    doThrow(
      new CADatabaseException(
        "Nonexistent.",
        errorNonexistent(),
        Map.of(),
        Optional.empty())
    ).when(itemMetaPut)
      .execute(any());

    final var transaction =
      this.transaction();

    when(transaction.queries(ItemGetType.class))
      .thenReturn(itemGet);
    when(transaction.queries(ItemMetadataPutType.class))
      .thenReturn(itemMetaPut);
    when(transaction.queries(TypeRecordGetType.class))
      .thenReturn(typeGet);

    when(itemGet.execute(any()))
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

    final var handler = new CAICmdItemMetadataPut();

    final var name0 =
      new RDottedName("com.io7m.name0");
    final var name1 =
      new RDottedName("com.io7m.name1");
    final var name2 =
      new RDottedName("com.io7m.name2");

    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandItemMetadataPut(
            ITEM_ID,
            Set.of(
              new CAMetadataType.Text(name0, "x"),
              new CAMetadataType.Text(name1, "y"),
              new CAMetadataType.Text(name2, "z")
            )
          ));
      });

    /* Assert. */

    assertEquals(errorNonexistent(), ex.errorCode());

    verify(transaction)
      .queries(ItemGetType.class);
    verify(transaction)
      .queries(ItemMetadataPutType.class);

    verify(itemMetaPut)
      .execute(
        new Parameters(
          ITEM_ID,
          Set.of(
            new CAMetadataType.Text(name0, "x"),
            new CAMetadataType.Text(name1, "y"),
            new CAMetadataType.Text(name2, "z")
          )
        )
      );

    verifyNoMoreInteractions(itemGet);
    verifyNoMoreInteractions(itemMetaPut);
    verifyNoMoreInteractions(transaction);
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

    final var itemGet =
      mock(ItemGetType.class);
    final var itemMetaPut =
      mock(ItemMetadataPutType.class);
    final var typeGet =
      mock(TypeRecordGetType.class);

    final var transaction =
      this.transaction();

    when(transaction.queries(ItemGetType.class))
      .thenReturn(itemGet);
    when(transaction.queries(ItemMetadataPutType.class))
      .thenReturn(itemMetaPut);
    when(transaction.queries(TypeRecordGetType.class))
      .thenReturn(typeGet);

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

    final var meta0 =
      new CAMetadataType.Text(new RDottedName("a"), "x");

    when(itemGet.execute(any()))
      .thenReturn(Optional.of(
        new CAItem(
          CAItemID.random(),
          "Item",
          0L,
          0L,
          new TreeMap<>(Map.of(meta0.name(), meta0)),
          Collections.emptySortedMap(),
          new TreeSet<>(
            Set.of(new RDottedName("t"))
          )
        )
      ));

    final var type =
      new CATypeRecord(
        P,
        new RDottedName("t"),
        "T",
        Map.ofEntries(
          Map.entry(
            new RDottedName("a"),
            new CATypeField(
              new RDottedName("a"),
              "Field A",
              new Integral(
                P,
                new RDottedName("ts0"),
                "Number",
                23L, 1000L
              ),
              true
            )
          ),
          Map.entry(
            new RDottedName("b"),
            new CATypeField(
              new RDottedName("b"),
              "Field B",
              new Text(
                P,
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
      .thenReturn(Optional.of(type));

    /* Act. */

    final var handler = new CAICmdItemMetadataPut();

    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandItemMetadataPut(
            ITEM_ID,
            Set.of(meta0)
          ));
      });

    /* Assert. */

    assertEquals(errorTypeCheckFailed(), ex.errorCode());

    verify(transaction)
      .queries(ItemGetType.class);
    verify(transaction)
      .queries(ItemMetadataPutType.class);
    verify(transaction)
      .queries(TypeRecordGetType.class);

    verify(itemGet)
      .execute(ITEM_ID);
    verify(itemMetaPut)
      .execute(new Parameters(ITEM_ID, Set.of(meta0)));

    verifyNoMoreInteractions(itemGet);
    verifyNoMoreInteractions(itemMetaPut);
    verifyNoMoreInteractions(transaction);
  }
}
