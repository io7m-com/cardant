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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemCreateType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemTypesAssignType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemTypesAssignType.Parameters;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemTypesRevokeType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageInstallType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeRecordFieldRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeRecordFieldUpdateType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeRecordGetType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeRecordPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeRecordRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeRecordsReferencingScalarType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeRecordsSearchType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeScalarGetType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeScalarPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeScalarRemoveType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeScalarSearchType;
import com.io7m.cardant.database.api.CADatabaseQueriesUsersType.PutType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAMoney;
import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.model.CATypeRecord;
import com.io7m.cardant.model.CATypeRecordFieldUpdate;
import com.io7m.cardant.model.CATypeRecordRemoval;
import com.io7m.cardant.model.CATypeRecordSearchParameters;
import com.io7m.cardant.model.CATypeScalarRemoval;
import com.io7m.cardant.model.CATypeScalarSearchParameters;
import com.io7m.cardant.model.CATypeScalarType.Integral;
import com.io7m.cardant.model.CATypeScalarType.Monetary;
import com.io7m.cardant.model.CATypeScalarType.Real;
import com.io7m.cardant.model.CATypeScalarType.Text;
import com.io7m.cardant.model.CATypeScalarType.Time;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType.Anything;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType.IsEqualTo;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType.IsSimilarTo;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.tests.containers.CATestContainers;
import com.io7m.cardant.tests.containers.CATestContainers.CADatabaseFixture;
import com.io7m.cardant.type_packages.checker.api.CATypePackageCheckerSuccess;
import com.io7m.cardant.type_packages.checkers.CATypePackageCheckers;
import com.io7m.cardant.type_packages.parsers.CATypePackageParsers;
import com.io7m.cardant.type_packages.resolver.api.CATypePackageResolverType;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.test_extension.ErvillaCloseAfterClass;
import com.io7m.ervilla.test_extension.ErvillaConfiguration;
import com.io7m.ervilla.test_extension.ErvillaExtension;
import com.io7m.idstore.model.IdName;
import com.io7m.lanark.core.RDottedName;
import com.io7m.medrina.api.MSubject;
import com.io7m.verona.core.Version;
import com.io7m.zelador.test_extension.CloseableResourcesType;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeFieldTypeNonexistent;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeReferenced;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeScalarReferenced;
import static com.io7m.cardant.model.type_package.CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(projectName = "com.io7m.cardant", disabledIfUnsupported = true)
public final class CADatabaseTypesTest
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
    </p:Package>
    """;

  private static CADatabaseFixture DATABASE_FIXTURE;
  private CADatabaseConnectionType connection;
  private CADatabaseTransactionType transaction;
  private CADatabaseType database;
  private TypeScalarPutType tsPut;
  private TypeRecordPutType tdPut;
  private TypeRecordsSearchType tdSearch;
  private TypeRecordGetType tdGet;
  private TypeRecordRemoveType tdRemove;
  private TypeRecordsReferencingScalarType tdRefSearch;
  private TypeScalarRemoveType tsRemove;
  private TypeScalarSearchType tsSearch;
  private TypeScalarGetType tsGet;
  private ItemCreateType iCreate;
  private ItemTypesAssignType tAssign;
  private ItemTypesRevokeType tRevoke;
  private TypeRecordFieldRemoveType trfRemove;
  private TypeRecordFieldUpdateType trfUpdate;

  @BeforeAll
  public static void setupOnce(
    final @ErvillaCloseAfterClass EContainerSupervisorType containers)
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

    final var userId = CAUserID.random();
    this.transaction.queries(PutType.class)
      .execute(new CAUser(userId, new IdName("x"), new MSubject(Set.of())));
    this.transaction.commit();
    this.transaction.setUserId(userId);

    this.iCreate =
      this.transaction.queries(ItemCreateType.class);

    this.tsPut =
      this.transaction.queries(TypeScalarPutType.class);
    this.tsGet =
      this.transaction.queries(TypeScalarGetType.class);
    this.tsRemove =
      this.transaction.queries(TypeScalarRemoveType.class);
    this.tsSearch =
      this.transaction.queries(TypeScalarSearchType.class);

    this.tdPut =
      this.transaction.queries(TypeRecordPutType.class);
    this.tdGet =
      this.transaction.queries(TypeRecordGetType.class);
    this.tdRemove =
      this.transaction.queries(TypeRecordRemoveType.class);
    this.tdSearch =
      this.transaction.queries(TypeRecordsSearchType.class);
    this.tdRefSearch =
      this.transaction.queries(TypeRecordsReferencingScalarType.class);

    this.trfRemove =
      this.transaction.queries(TypeRecordFieldRemoveType.class);
    this.trfUpdate =
      this.transaction.queries(TypeRecordFieldUpdateType.class);

    this.tAssign =
      this.transaction.queries(ItemTypesAssignType.class);
    this.tRevoke =
      this.transaction.queries(ItemTypesRevokeType.class);

    installTestTypePackage(this.transaction);
  }

  static void installTestTypePackage(
    final CADatabaseTransactionType transaction)
    throws Exception
  {
    final var parsers =
      new CATypePackageParsers();
    final var checkers =
      new CATypePackageCheckers();

    final var packageDeclaration =
      parsers.parse(
        URI.create("urn:in"),
        new ByteArrayInputStream(P_TEXT.getBytes(StandardCharsets.UTF_8))
      );

    final var packageV =
      ((CATypePackageCheckerSuccess)
        checkers.createChecker(
          CAStrings.create(Locale.ROOT),
          Mockito.mock(CATypePackageResolverType.class),
          packageDeclaration
        ).execute())
        .typePackage();

    transaction.queries(TypePackageInstallType.class)
      .execute(packageV);
    transaction.commit();
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
      new Real(
        P,
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        -1000.0,
        1000.0
      );

    this.tsPut.execute(voltage);
    this.tsRemove.execute(
      new CATypeScalarRemoval(voltage, TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED));
    this.tsPut.execute(voltage);
    this.tsRemove.execute(
      new CATypeScalarRemoval(voltage, TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED));
  }

  /**
   * Creating scalar type declarations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeScalarPut1()
    throws Exception
  {
    final var voltage =
      new Integral(
        P,
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        -1000L,
        1000L
      );

    this.tsPut.execute(voltage);
    this.tsRemove.execute(
      new CATypeScalarRemoval(voltage, TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED));
    this.tsPut.execute(voltage);
    this.tsRemove.execute(
      new CATypeScalarRemoval(voltage, TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED));
  }

  /**
   * Creating scalar type declarations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeScalarPut2()
    throws Exception
  {
    final var voltage =
      new Monetary(
        P,
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        CAMoney.money("-1000.0"),
        CAMoney.money("1000.0")
      );

    this.tsPut.execute(voltage);
    this.tsRemove.execute(
      new CATypeScalarRemoval(voltage, TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED));
    this.tsPut.execute(voltage);
    this.tsRemove.execute(
      new CATypeScalarRemoval(voltage, TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED));
  }

  /**
   * Creating scalar type declarations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeScalarPut3()
    throws Exception
  {
    final var voltage =
      new Time(
        P,
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        OffsetDateTime.parse("2000-01-01T00:00:00+00:00"),
        OffsetDateTime.parse("2010-01-01T00:00:00+00:00")
      );

    this.tsPut.execute(voltage);
    this.tsRemove.execute(
      new CATypeScalarRemoval(voltage, TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED));
    this.tsPut.execute(voltage);
    this.tsRemove.execute(
      new CATypeScalarRemoval(voltage, TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED));
  }

  /**
   * Creating scalar type declarations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeScalarPut4()
    throws Exception
  {
    final var voltage =
      new Text(
        P,
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        ".*"
      );

    this.tsPut.execute(voltage);
    this.tsRemove.execute(
      new CATypeScalarRemoval(voltage, TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED));
    this.tsPut.execute(voltage);
    this.tsRemove.execute(
      new CATypeScalarRemoval(voltage, TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED));
  }

  /**
   * Creating and retrieving type declarations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeRecordPut()
    throws Exception
  {
    final var voltage =
      new Real(
        P,
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        -1000.0,
        1000.0
      );

    final var size =
      new Text(
        P,
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
      new CATypeRecord(
        P,
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
        this.tdSearch.execute(
          new CATypeRecordSearchParameters(
            new IsEqualTo<>("com.io7m.battery"),
            new Anything<>(),
            100L
          )
        );
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

    this.tdRemove.execute(
      new CATypeRecordRemoval(
        typeDeclaration,
        TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED
      )
    );
    assertEquals(empty(), this.tdGet.execute(typeDeclaration.name()));
  }

  /**
   * A scalar type cannot be removed when types still reference it.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeRecordRemoveScalar()
    throws Exception
  {
    final var voltage =
      new Real(
        P,
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        -1000.0,
        1000.0
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
      new CATypeRecord(
        P,
        new RDottedName("com.io7m.battery"),
        "A battery.",
        Map.ofEntries(
          Map.entry(voltage.name(), voltageField)
        )
      );

    this.tdPut.execute(typeDeclaration);

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.tsRemove.execute(
          new CATypeScalarRemoval(
            voltage,
            TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED)
        );
      });
    assertEquals(errorTypeScalarReferenced(), ex.errorCode());
  }

  /**
   * A type declaration cannot refer to a nonexistent scalar type.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeRecordPutScalarNonexistent()
    throws Exception
  {
    final var voltage =
      new Real(
        P,
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        -1000.0,
        1000.0
      );

    final var voltageField =
      new CATypeField(
        new RDottedName("com.io7m.battery.voltage"),
        "A measurement of battery voltage.",
        voltage,
        true
      );

    final var typeDeclaration =
      new CATypeRecord(
        P,
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
  public void testTypeRecordSearchScalar()
    throws Exception
  {
    final var voltage =
      new Real(
        P,
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        -1000.0,
        1000.0
      );

    this.tsPut.execute(voltage);

    final var page =
      this.tsSearch.execute(
        new CATypeScalarSearchParameters(
          new Anything<>(),
          new IsSimilarTo<>("measurement"),
          100L
        )).pageCurrent(this.transaction);

    assertEquals(List.of(voltage), page.items());
  }

  /**
   * A scalar type can be retrieved.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeRecordGetScalar()
    throws Exception
  {
    final var voltage =
      new Real(
        P,
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        -1000.0,
        1000.0
      );

    this.tsPut.execute(voltage);
    assertEquals(Optional.of(voltage), this.tsGet.execute(voltage.name()));
    this.tsRemove.execute(
      new CATypeScalarRemoval(voltage, TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED));
    assertEquals(empty(), this.tsGet.execute(voltage.name()));
  }

  /**
   * Retrieving fieldless type declarations works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeRecordFieldless()
    throws Exception
  {
    final var typeDeclaration =
      new CATypeRecord(
        P,
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

    this.tdRemove.execute(
      new CATypeRecordRemoval(
        typeDeclaration,
        TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED
      )
    );
    assertEquals(empty(), this.tdGet.execute(typeDeclaration.name()));
  }

  /**
   * Removing fields works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeRecordPutRemovesField()
    throws Exception
  {
    final var voltage =
      new Real(
        P,
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        -1000.0,
        1000.0
      );

    final var size =
      new Text(
        P,
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
      new CATypeRecord(
        P,
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
      new CATypeRecord(
        P,
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
  public void testTypeRecordRemoveSafe()
    throws Exception
  {
    final var voltage =
      new Real(
        P,
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        -1000.0,
        1000.0
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
      new CATypeRecord(
        P,
        new RDottedName("com.io7m.battery0"),
        "A battery.",
        Map.ofEntries(Map.entry(voltageField.name(), voltageField))
      );

    final var typeDeclaration1 =
      new CATypeRecord(
        P,
        new RDottedName("com.io7m.battery1"),
        "A battery.",
        Map.ofEntries(Map.entry(voltageField.name(), voltageField))
      );

    final var typeDeclaration2 =
      new CATypeRecord(
        P,
        new RDottedName("com.io7m.battery2"),
        "A battery.",
        Map.ofEntries(Map.entry(voltageField.name(), voltageField))
      );

    this.tdPut.execute(typeDeclaration0);
    this.tdPut.execute(typeDeclaration1);
    this.tdPut.execute(typeDeclaration2);
    this.transaction.commit();

    this.tdRemove.execute(
      new CATypeRecordRemoval(
        typeDeclaration1,
        TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED
      )
    );

    this.transaction.commit();

    this.tdGet.execute(typeDeclaration0.name())
      .orElseThrow();

    assertEquals(
      empty(),
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
      new Real(
        P,
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        -1000.0,
        1000.0
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
      new CATypeRecord(
        P,
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
          this.tdRemove.execute(
            new CATypeRecordRemoval(
              typeDeclaration0,
              TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED
            )
          );
        });
      assertEquals(ex.errorCode(), errorTypeReferenced());
    }

    /*
     * Revoking the type from the item allows the type to be removed.
     */

    this.tRevoke.execute(
      new ItemTypesRevokeType.Parameters(item, Set.of(typeDeclaration0.name()))
    );

    this.tdRemove.execute(
      new CATypeRecordRemoval(
        typeDeclaration0,
        TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED
      )
    );
  }

  /**
   * Removing a record field works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeRecordFieldRemoval()
    throws Exception
  {
    final var voltage =
      new Real(
        P,
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        -1000.0,
        1000.0
      );

    final var size =
      new Text(
        P,
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
      new CATypeRecord(
        P,
        new RDottedName("com.io7m.battery"),
        "A battery.",
        Map.ofEntries(
          Map.entry(voltageField.name(), voltageField),
          Map.entry(sizeField.name(), sizeField)
        )
      );

    this.tdPut.execute(typeDeclaration);
    this.transaction.commit();
    this.trfRemove.execute(voltageField.name());

    {
      final var tr =
        this.tdGet.execute(new RDottedName("com.io7m.battery"))
          .orElseThrow();

      final var trFields = tr.fields();
      assertEquals(1, trFields.size());
      assertEquals(sizeField, trFields.get(sizeField.name()));
      assertNull(trFields.get(voltageField.name()));
    }

    this.trfUpdate.execute(
      new CATypeRecordFieldUpdate(typeDeclaration.name(), voltageField)
    );

    {
      final var tr =
        this.tdGet.execute(new RDottedName("com.io7m.battery"))
          .orElseThrow();

      final var trFields = tr.fields();
      assertEquals(2, trFields.size());
      assertEquals(sizeField, trFields.get(sizeField.name()));
      assertEquals(voltageField, trFields.get(voltageField.name()));
    }

    this.trfUpdate.execute(
      new CATypeRecordFieldUpdate(typeDeclaration.name(), voltageField)
    );

    {
      final var tr =
        this.tdGet.execute(new RDottedName("com.io7m.battery"))
          .orElseThrow();

      final var trFields = tr.fields();
      assertEquals(2, trFields.size());
      assertEquals(sizeField, trFields.get(sizeField.name()));
      assertEquals(voltageField, trFields.get(voltageField.name()));
    }
  }

  /**
   * A field cannot be updated for a nonexistent record type.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypeRecordFieldUpdateNonexistent()
    throws Exception
  {
    final var voltage =
      new Real(
        P,
        new RDottedName("com.io7m.voltage"),
        "A measurement of voltage.",
        -1000.0,
        1000.0
      );

    this.tsPut.execute(voltage);

    final var voltageField =
      new CATypeField(
        new RDottedName("com.io7m.battery.voltage"),
        "A measurement of battery voltage.",
        voltage,
        true
      );

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.trfUpdate.execute(new CATypeRecordFieldUpdate(
          new RDottedName("nonexistent"),
          voltageField
        ));
      });

    assertEquals(errorNonexistent(), ex.errorCode());
  }
}
