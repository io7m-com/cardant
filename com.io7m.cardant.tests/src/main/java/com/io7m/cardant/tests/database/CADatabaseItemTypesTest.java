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

package com.io7m.cardant.tests.database;

import com.io7m.cardant.database.api.CADatabaseConnectionType;
import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseQueriesItemTypesType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.TypesAssignType.Parameters;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.TypesRevokeType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CATypeDeclaration;
import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.model.CATypeScalar;
import com.io7m.cardant.tests.containers.CATestContainers;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.test_extension.ErvillaCloseAfterAll;
import com.io7m.ervilla.test_extension.ErvillaConfiguration;
import com.io7m.ervilla.test_extension.ErvillaExtension;
import com.io7m.lanark.core.RDottedName;
import com.io7m.zelador.test_extension.CloseableResourcesType;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeFieldTypeNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeReferenced;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeScalarReferenced;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(disabledIfUnsupported = true)
public final class CADatabaseItemTypesTest
{
  private static CATestContainers.CADatabaseFixture DATABASE_FIXTURE;
  private CADatabaseConnectionType connection;
  private CADatabaseTransactionType transaction;
  private CADatabaseType database;
  private CADatabaseQueriesItemTypesType.TypeScalarPutType tsPut;
  private CADatabaseQueriesItemTypesType.TypeDeclarationPutType tdPut;
  private CADatabaseQueriesItemTypesType.TypeDeclarationsSearchType tdSearch;
  private CADatabaseQueriesItemTypesType.TypeDeclarationGetType tdGet;
  private CADatabaseQueriesItemTypesType.TypeDeclarationRemoveType tdRemove;
  private CADatabaseQueriesItemTypesType.TypeDeclarationsReferencingScalarType tdRefSearch;
  private CADatabaseQueriesItemTypesType.TypeDeclarationGetMultipleType tdGetMulti;
  private CADatabaseQueriesItemTypesType.TypeScalarRemoveType tsRemove;
  private CADatabaseQueriesItemTypesType.TypeScalarSearchType tsSearch;
  private CADatabaseQueriesItemTypesType.TypeScalarGetType tsGet;
  private CADatabaseQueriesItemsType.CreateType iCreate;
  private CADatabaseQueriesItemsType.TypesAssignType tAssign;
  private TypesRevokeType tRevoke;

  @BeforeAll
  public static void setupOnce(
    final @ErvillaCloseAfterAll EContainerSupervisorType containers)
    throws Exception
  {
    DATABASE_FIXTURE =
      CATestContainers.createDatabase(containers, 15432);
  }

  @BeforeEach
  public void setup(
    final CloseableResourcesType closeables)
    throws Exception
  {
    DATABASE_FIXTURE.reset();

    this.database =
      closeables.addPerTestResource(DATABASE_FIXTURE.createDatabase());
    this.connection =
      closeables.addPerTestResource(this.database.openConnection(CARDANT));
    this.transaction =
      closeables.addPerTestResource(this.connection.openTransaction());

    this.iCreate =
      this.transaction.queries(CADatabaseQueriesItemsType.CreateType.class);

    this.tsPut =
      this.transaction.queries(CADatabaseQueriesItemTypesType.TypeScalarPutType.class);
    this.tsGet =
      this.transaction.queries(CADatabaseQueriesItemTypesType.TypeScalarGetType.class);
    this.tsRemove =
      this.transaction.queries(CADatabaseQueriesItemTypesType.TypeScalarRemoveType.class);
    this.tsSearch =
      this.transaction.queries(CADatabaseQueriesItemTypesType.TypeScalarSearchType.class);

    this.tdPut =
      this.transaction.queries(CADatabaseQueriesItemTypesType.TypeDeclarationPutType.class);
    this.tdGet =
      this.transaction.queries(CADatabaseQueriesItemTypesType.TypeDeclarationGetType.class);
    this.tdGetMulti =
      this.transaction.queries(CADatabaseQueriesItemTypesType.TypeDeclarationGetMultipleType.class);
    this.tdRemove =
      this.transaction.queries(CADatabaseQueriesItemTypesType.TypeDeclarationRemoveType.class);
    this.tdSearch =
      this.transaction.queries(CADatabaseQueriesItemTypesType.TypeDeclarationsSearchType.class);
    this.tdRefSearch =
      this.transaction.queries(CADatabaseQueriesItemTypesType.TypeDeclarationsReferencingScalarType.class);

    this.tAssign =
      this.transaction.queries(CADatabaseQueriesItemsType.TypesAssignType.class);
    this.tRevoke =
      this.transaction.queries(TypesRevokeType.class);
  }

  /**
   * Creating scalar type declarations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeScalarPut0()
    throws Exception
  {
    final var voltage =
      new CATypeScalar(
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        "^0|([1-9][0-9]+)$"
      );

    this.tsPut.execute(voltage);
    this.tsRemove.execute(voltage.name());
    this.tsPut.execute(voltage);
    this.tsRemove.execute(voltage.name());
  }

  /**
   * Creating and retrieving type declarations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeDeclarationPut()
    throws Exception
  {
    final var voltage =
      new CATypeScalar(
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        "^0|([1-9][0-9]+)$"
      );

    final var size =
      new CATypeScalar(
        new RDottedName("com.io7m.battery.size"),
        "A specification of battery size.",
        "^.*$"
      );

    this.tsPut.execute(voltage);
    this.tsPut.execute(size);

    final var voltageField =
      new CATypeField(
        new RDottedName("com.io7m.battery.voltage"),
        "A measurement of battery voltage.",
        voltage,
        true
      );

    final var sizeField =
      new CATypeField(
        new RDottedName("com.io7m.battery.size"),
        "A battery size (such as AA, AAA, etc).",
        size,
        true
      );

    final var typeDeclaration =
      new CATypeDeclaration(
        new RDottedName("com.io7m.battery"),
        "A battery.",
        Map.ofEntries(
          Map.entry(voltageField.name(), voltageField),
          Map.entry(sizeField.name(), sizeField)
        )
      );

    this.tdPut.execute(typeDeclaration);
    this.tdPut.execute(typeDeclaration);

    this.transaction.commit();

    {
      final var search =
        this.tdSearch.execute("battery");
      final var page =
        search.pageCurrent(this.transaction);
      assertEquals(typeDeclaration.name(), page.items().get(0).name());
    }

    {
      final var search =
        this.tdRefSearch.execute(voltage.name());
      final var page =
        search.pageCurrent(this.transaction);
      assertEquals(typeDeclaration.name(), page.items().get(0).name());
    }

    final var received =
      this.tdGet.execute(typeDeclaration.name())
        .orElseThrow();

    assertEquals(typeDeclaration, received);

    this.tdRemove.execute(typeDeclaration.name());
    assertEquals(empty(), this.tdGet.execute(typeDeclaration.name()));
  }

  /**
   * Multiple type declarations can be fetched.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeDeclarationMultiGet()
    throws Exception
  {
    final var voltage =
      new CATypeScalar(
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        "^0|([1-9][0-9]+)$"
      );

    final var size =
      new CATypeScalar(
        new RDottedName("com.io7m.battery.size"),
        "A specification of battery size.",
        "^.*$"
      );

    this.tsPut.execute(voltage);
    this.tsPut.execute(size);

    final var voltageField =
      new CATypeField(
        new RDottedName("com.io7m.battery.voltage"),
        "A measurement of battery voltage.",
        voltage,
        true
      );

    final var sizeField =
      new CATypeField(
        new RDottedName("com.io7m.battery.size"),
        "A battery size (such as AA, AAA, etc).",
        size,
        true
      );

    final var typeDeclaration0 =
      new CATypeDeclaration(
        new RDottedName("com.io7m.battery1"),
        "A battery 1.",
        Map.ofEntries(
          Map.entry(voltageField.name(), voltageField),
          Map.entry(sizeField.name(), sizeField)
        )
      );

    final var typeDeclaration1 =
      new CATypeDeclaration(
        new RDottedName("com.io7m.battery2"),
        "A battery 2.",
        Map.ofEntries(
          Map.entry(voltageField.name(), voltageField),
          Map.entry(sizeField.name(), sizeField)
        )
      );

    this.tdPut.execute(typeDeclaration0);
    this.tdPut.execute(typeDeclaration1);

    this.transaction.commit();

    final var received =
      this.tdGetMulti.execute(
        Set.of(typeDeclaration0.name(), typeDeclaration1.name())
      );

    {
      final var r = received.get(0);
      assertEquals(r.name(), typeDeclaration0.name());
      assertEquals(r.description(), typeDeclaration0.description());

      for (final var f : typeDeclaration0.fields().values()) {
        final var rf = r.fields().get(f.name());
        assertEquals(f, rf);
      }
    }

    {
      final var r = received.get(1);
      assertEquals(r.name(), typeDeclaration1.name());
      assertEquals(r.description(), typeDeclaration1.description());

      for (final var f : typeDeclaration1.fields().values()) {
        final var rf = r.fields().get(f.name());
        assertEquals(f, rf);
      }
    }
  }

  /**
   * A scalar type cannot be removed when types still reference it.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeDeclarationRemoveScalar()
    throws Exception
  {
    final var voltage =
      new CATypeScalar(
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        "^0|([1-9][0-9]+)$"
      );

    this.tsPut.execute(voltage);

    final var voltageField =
      new CATypeField(
        new RDottedName("com.io7m.voltage"),
        "A measurement of battery voltage.",
        voltage,
        true
      );

    final var typeDeclaration =
      new CATypeDeclaration(
        new RDottedName("com.io7m.battery"),
        "A battery.",
        Map.ofEntries(
          Map.entry(voltage.name(), voltageField)
        )
      );

    this.tdPut.execute(typeDeclaration);

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.tsRemove.execute(voltage.name());
      });
    assertEquals(errorTypeScalarReferenced(), ex.errorCode());
  }

  /**
   * A type declaration cannot refer to a nonexistent scalar type.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeDeclarationPutScalarNonexistent()
    throws Exception
  {
    final var voltage =
      new CATypeScalar(
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        "^0|([1-9][0-9]+)$"
      );

    final var voltageField =
      new CATypeField(
        new RDottedName("com.io7m.battery.voltage"),
        "A measurement of battery voltage.",
        voltage,
        true
      );

    final var typeDeclaration =
      new CATypeDeclaration(
        new RDottedName("com.io7m.battery"),
        "A battery.",
        Map.ofEntries(
          Map.entry(voltage.name(), voltageField)
        )
      );

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.tdPut.execute(typeDeclaration);
      });
    assertEquals(errorTypeFieldTypeNonexistent(), ex.errorCode());
  }

  /**
   * A scalar type can be searched.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeDeclarationSearchScalar()
    throws Exception
  {
    final var voltage =
      new CATypeScalar(
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        "^0|([1-9][0-9]+)$"
      );

    this.tsPut.execute(voltage);

    final var page =
      this.tsSearch.execute("measurement")
        .pageCurrent(this.transaction);

    assertEquals(List.of(voltage), page.items());
  }

  /**
   * A scalar type can be retrieved.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeDeclarationGetScalar()
    throws Exception
  {
    final var voltage =
      new CATypeScalar(
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        "^0|([1-9][0-9]+)$"
      );

    this.tsPut.execute(voltage);
    assertEquals(Optional.of(voltage), this.tsGet.execute(voltage.name()));
    this.tsRemove.execute(voltage.name());
    assertEquals(Optional.empty(), this.tsGet.execute(voltage.name()));
  }

  /**
   * Retrieving fieldless type declarations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeDeclarationFieldless()
    throws Exception
  {
    final var typeDeclaration =
      new CATypeDeclaration(
        new RDottedName("com.io7m.battery"),
        "A battery.",
        Map.of()
      );

    this.tdPut.execute(typeDeclaration);

    this.transaction.commit();

    final var received =
      this.tdGet.execute(typeDeclaration.name())
        .orElseThrow();

    assertEquals(typeDeclaration, received);

    this.tdRemove.execute(typeDeclaration.name());
    assertEquals(empty(), this.tdGet.execute(typeDeclaration.name()));
  }

  /**
   * Removing fields works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeDeclarationPutRemovesField()
    throws Exception
  {
    final var voltage =
      new CATypeScalar(
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        "^0|([1-9][0-9]+)$"
      );

    final var size =
      new CATypeScalar(
        new RDottedName("com.io7m.battery.size"),
        "A specification of battery size.",
        "^.*$"
      );

    this.tsPut.execute(voltage);
    this.tsPut.execute(size);

    final var voltageField =
      new CATypeField(
        new RDottedName("com.io7m.battery.voltage"),
        "A measurement of battery voltage.",
        voltage,
        true
      );

    final var sizeField =
      new CATypeField(
        new RDottedName("com.io7m.battery.size"),
        "A battery size (such as AA, AAA, etc).",
        size,
        true
      );

    final var typeDeclaration =
      new CATypeDeclaration(
        new RDottedName("com.io7m.battery"),
        "A battery.",
        Map.ofEntries(
          Map.entry(voltageField.name(), voltageField),
          Map.entry(sizeField.name(), sizeField)
        )
      );

    this.tdPut.execute(typeDeclaration);
    this.transaction.commit();

    final var typeDeclarationAfter =
      new CATypeDeclaration(
        new RDottedName("com.io7m.battery"),
        "A battery.",
        Map.ofEntries(
          Map.entry(sizeField.name(), sizeField)
        )
      );

    this.tdPut.execute(typeDeclarationAfter);
    this.transaction.commit();

    final var received =
      this.tdGet.execute(typeDeclaration.name())
        .orElseThrow();

    assertEquals(typeDeclarationAfter, received);
  }

  /**
   * Removing type declarations doesn't affect other type declarations.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeDeclarationRemoveSafe()
    throws Exception
  {
    final var voltage =
      new CATypeScalar(
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        "^0|([1-9][0-9]+)$"
      );

    this.tsPut.execute(voltage);

    final var voltageField =
      new CATypeField(
        new RDottedName("com.io7m.battery.voltage"),
        "A measurement of battery voltage.",
        voltage,
        true
      );

    final var typeDeclaration0 =
      new CATypeDeclaration(
        new RDottedName("com.io7m.battery0"),
        "A battery.",
        Map.ofEntries(Map.entry(voltageField.name(), voltageField))
      );

    final var typeDeclaration1 =
      new CATypeDeclaration(
        new RDottedName("com.io7m.battery1"),
        "A battery.",
        Map.ofEntries(Map.entry(voltageField.name(), voltageField))
      );

    final var typeDeclaration2 =
      new CATypeDeclaration(
        new RDottedName("com.io7m.battery2"),
        "A battery.",
        Map.ofEntries(Map.entry(voltageField.name(), voltageField))
      );

    this.tdPut.execute(typeDeclaration0);
    this.tdPut.execute(typeDeclaration1);
    this.tdPut.execute(typeDeclaration2);
    this.transaction.commit();


    this.tdRemove.execute(typeDeclaration1.name());
    this.transaction.commit();

    this.tdGet.execute(typeDeclaration0.name())
      .orElseThrow();

    assertEquals(
      Optional.empty(),
      this.tdGet.execute(typeDeclaration1.name()));

    this.tdGet.execute(typeDeclaration2.name())
      .orElseThrow();
  }

  /**
   * Assigning type declarations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeAssign()
    throws Exception
  {
    final var voltage =
      new CATypeScalar(
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        "^0|([1-9][0-9]+)$"
      );

    this.tsPut.execute(voltage);

    final var voltageField =
      new CATypeField(
        new RDottedName("com.io7m.battery.voltage"),
        "A measurement of battery voltage.",
        voltage,
        true
      );

    final var typeDeclaration0 =
      new CATypeDeclaration(
        new RDottedName("com.io7m.battery0"),
        "A battery.",
        Map.ofEntries(Map.entry(voltageField.name(), voltageField))
      );

    this.tdPut.execute(typeDeclaration0);
    this.transaction.commit();

    /*
     * An item with the type assigned stops the type from being removed.
     */

    final var item = CAItemID.random();
    this.iCreate.execute(item);
    this.tAssign.execute(new Parameters(item, Set.of(typeDeclaration0.name())));

    {
      final var ex =
        assertThrows(CADatabaseException.class, () -> {
          this.tdRemove.execute(typeDeclaration0.name());
        });
      assertEquals(ex.errorCode(), errorTypeReferenced());
    }

    /*
     * Revoking the type from the item allows the type to be removed.
     */

    this.tRevoke.execute(
      new TypesRevokeType.Parameters(item, Set.of(typeDeclaration0.name()))
    );

    this.tdRemove.execute(typeDeclaration0.name());
  }
}
