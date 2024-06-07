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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemMetadataRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemMetadataRemoveType.Parameters;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageGetTextType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageSatisfyingType;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAMetadataType.Text;
import com.io7m.cardant.model.CATypeRecordFieldIdentifier;
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataRemove;
import com.io7m.cardant.security.CASecurity;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.inventory.CAICmdItemMetadataRemove;
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
 * @see CAICmdItemMetadataRemove
 */

public final class CAICmdItemMetadataRemoveTest
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

    final var handler =
      new CAICmdItemMetadataRemove();
    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandItemMetadataRemove(
            ITEM_ID,
            Set.of(CATypeRecordFieldIdentifier.of("com.io7m:x.y"))
          )
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

    final var itemGet =
      mock(ItemGetType.class);
    final var itemMetaRemove =
      mock(ItemMetadataRemoveType.class);

    final var transaction =
      this.transaction();

    when(transaction.queries(ItemGetType.class))
      .thenReturn(itemGet);
    when(transaction.queries(ItemMetadataRemoveType.class))
      .thenReturn(itemMetaRemove);

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

    final var handler = new CAICmdItemMetadataRemove();
    handler.execute(
      context,
      new CAICommandItemMetadataRemove(
        ITEM_ID,
        Set.of(
          CATypeRecordFieldIdentifier.of("com.io7m:a.x"),
          CATypeRecordFieldIdentifier.of("com.io7m:a.y"),
          CATypeRecordFieldIdentifier.of("com.io7m:a.z")
        )
      ));

    /* Assert. */

    verify(transaction)
      .queries(ItemGetType.class);
    verify(transaction)
      .queries(ItemMetadataRemoveType.class);
    verify(itemMetaRemove)
      .execute(
        new Parameters(
          ITEM_ID,
          Set.of(
            CATypeRecordFieldIdentifier.of("com.io7m:a.x"),
            CATypeRecordFieldIdentifier.of("com.io7m:a.y"),
            CATypeRecordFieldIdentifier.of("com.io7m:a.z")
          )

        )
      );
    verify(itemGet, new Times(2))
      .execute(ITEM_ID);

    verifyNoMoreInteractions(transaction);
    verifyNoMoreInteractions(itemGet);
    verifyNoMoreInteractions(itemMetaRemove);
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
    final var itemMetaRemove =
      mock(ItemMetadataRemoveType.class);

    when(itemGet.execute(any()))
      .thenReturn(
        Optional.of(
          new CAItem(
            CAItemID.random(),
            "Item",
            0L,
            0L,
            new TreeMap<>(),
            Collections.emptySortedMap(),
            new TreeSet<>()
          )
        )
      );

    doThrow(
      new CADatabaseException(
        "Nonexistent.",
        errorNonexistent(),
        Map.of(),
        Optional.empty())
    ).when(itemMetaRemove)
      .execute(any());

    final var transaction =
      this.transaction();

    when(transaction.queries(ItemGetType.class))
      .thenReturn(itemGet);
    when(transaction.queries(ItemMetadataRemoveType.class))
      .thenReturn(itemMetaRemove);

    doThrow(new CADatabaseException(
      "X",
      errorNonexistent(),
      Map.of(),
      Optional.empty()))
      .when(itemMetaRemove)
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

    final var handler = new CAICmdItemMetadataRemove();

    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandItemMetadataRemove(
            ITEM_ID,
            Set.of(
              CATypeRecordFieldIdentifier.of("com.io7m:a.x"),
              CATypeRecordFieldIdentifier.of("com.io7m:a.y"),
              CATypeRecordFieldIdentifier.of("com.io7m:a.z")
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
    final var itemMetaRemove =
      mock(ItemMetadataRemoveType.class);

    doThrow(
      new CADatabaseException(
        "Nonexistent.",
        errorNonexistent(),
        Map.of(),
        Optional.empty())
    ).when(itemMetaRemove)
      .execute(any());

    final var transaction =
      this.transaction();

    when(transaction.queries(ItemGetType.class))
      .thenReturn(itemGet);
    when(transaction.queries(ItemMetadataRemoveType.class))
      .thenReturn(itemMetaRemove);

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

    final var handler = new CAICmdItemMetadataRemove();

    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandItemMetadataRemove(
            ITEM_ID,
            Set.of(
              CATypeRecordFieldIdentifier.of("com.io7m:a.x"),
              CATypeRecordFieldIdentifier.of("com.io7m:a.y"),
              CATypeRecordFieldIdentifier.of("com.io7m:a.z")
            )
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

    final var itemGet =
      mock(ItemGetType.class);
    final var itemMetaRemove =
      mock(ItemMetadataRemoveType.class);
    final var typePackageSatisfying =
      mock(TypePackageSatisfyingType.class);
    final var typePackageGetText =
      mock(TypePackageGetTextType.class);

    final var transaction =
      this.transaction();

    when(transaction.queries(ItemGetType.class))
      .thenReturn(itemGet);
    when(transaction.queries(ItemMetadataRemoveType.class))
      .thenReturn(itemMetaRemove);
    when(transaction.queries(TypePackageSatisfyingType.class))
      .thenReturn(typePackageSatisfying);
    when(transaction.queries(TypePackageGetTextType.class))
      .thenReturn(typePackageGetText);

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
      new Text(
        CATypeRecordFieldIdentifier.of("com.io7m:a.x"),
        "x"
      );

    when(typePackageSatisfying.execute(any()))
      .thenReturn(Optional.of(P));
    when(typePackageGetText.execute(any()))
      .thenReturn(Optional.of(P_TEXT));

    when(itemGet.execute(any()))
      .thenReturn(
        Optional.of(
          new CAItem(
            CAItemID.random(),
            "Item",
            0L,
            0L,
            new TreeMap<>(Map.of(meta0.name(), meta0)),
            Collections.emptySortedMap(),
            new TreeSet<>(
              List.of(
                new CATypeRecordIdentifier(
                  new RDottedName("com.io7m"),
                  new RDottedName("t0")
                )
              )
            )
          )
        )
      );

    /* Act. */

    final var handler = new CAICmdItemMetadataRemove();

    final var ex =
      assertThrows(CACommandExecutionFailure.class, () -> {
        handler.execute(
          context,
          new CAICommandItemMetadataRemove(
            ITEM_ID,
            Set.of(meta0.name())
          ));
      });

    /* Assert. */

    assertEquals(errorTypeCheckFailed(), ex.errorCode());

    verify(transaction)
      .queries(ItemGetType.class);
    verify(transaction)
      .queries(TypePackageGetTextType.class);
    verify(transaction)
      .queries(TypePackageSatisfyingType.class);
    verify(transaction)
      .queries(ItemMetadataRemoveType.class);

    verify(itemGet, new Times(2))
      .execute(ITEM_ID);
    verify(itemMetaRemove)
      .execute(new ItemMetadataRemoveType.Parameters(ITEM_ID, Set.of(meta0.name())));
    verify(typePackageSatisfying)
      .execute(any());
    verify(typePackageGetText)
      .execute(P);

    verifyNoMoreInteractions(itemGet);
    verifyNoMoreInteractions(itemMetaRemove);
    verifyNoMoreInteractions(typePackageSatisfying);
    verifyNoMoreInteractions(typePackageGetText);
    verifyNoMoreInteractions(transaction);
  }
}
