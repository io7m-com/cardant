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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemTypesRevokeType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageInstallType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageSatisfyingType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageSearchType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageUninstallType;
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
import com.io7m.cardant.database.api.CADatabaseTypePackageResolver;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.model.CATypeRecord;
import com.io7m.cardant.model.CATypeScalarType.Integral;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType;
import com.io7m.cardant.model.type_package.CATypePackage;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.model.type_package.CATypePackageSearchParameters;
import com.io7m.cardant.model.type_package.CATypePackageSummary;
import com.io7m.cardant.model.type_package.CATypePackageTypeRemovalBehavior;
import com.io7m.cardant.model.type_package.CATypePackageUninstall;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.tests.CATestDirectories;
import com.io7m.cardant.tests.containers.CATestContainers;
import com.io7m.cardant.tests.containers.CATestContainers.CADatabaseFixture;
import com.io7m.cardant.type_packages.checkers.CATypePackageCheckers;
import com.io7m.cardant.type_packages.compilers.CATypePackageCompilers;
import com.io7m.cardant.type_packages.parsers.CATypePackageParsers;
import com.io7m.cardant.type_packages.parsers.CATypePackageSerializers;
import com.io7m.cardant.type_packages.resolver.api.CATypePackageResolverType;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.test_extension.ErvillaCloseAfterClass;
import com.io7m.ervilla.test_extension.ErvillaConfiguration;
import com.io7m.ervilla.test_extension.ErvillaExtension;
import com.io7m.idstore.model.IdName;
import com.io7m.lanark.core.RDottedName;
import com.io7m.medrina.api.MSubject;
import com.io7m.verona.core.Version;
import com.io7m.verona.core.VersionRange;
import com.io7m.zelador.test_extension.CloseableResourcesType;
import com.io7m.zelador.test_extension.ZeladorExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorDuplicate;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeReferenced;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(projectName = "com.io7m.cardant", disabledIfUnsupported = true)
public final class CADatabaseTypePackagesTest
{
  private static final CATypePackageIdentifier P =
    new CATypePackageIdentifier(
      new RDottedName("x"),
      Version.of(1, 0, 0)
    );

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
  private TypePackageInstallType tpInstall;
  private TypePackageUninstallType tpUninstall;
  private CATypePackageParsers parsers;
  private CATypePackageSerializers serializers;
  private CATypePackageCheckers checkers;
  private CATypePackageResolverType resolver;
  private TypePackageSatisfyingType tpSatisfying;
  private TypePackageSearchType tpSearch;
  private ItemTypesAssignType iTypeAssign;

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

    this.parsers =
      new CATypePackageParsers();
    this.serializers =
      new CATypePackageSerializers();
    this.checkers =
      new CATypePackageCheckers();

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
    this.iTypeAssign =
      this.transaction.queries(ItemTypesAssignType.class);

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

    this.tpInstall =
      this.transaction.queries(TypePackageInstallType.class);
    this.tpUninstall =
      this.transaction.queries(TypePackageUninstallType.class);
    this.tpSatisfying =
      this.transaction.queries(TypePackageSatisfyingType.class);
    this.tpSearch =
      this.transaction.queries(TypePackageSearchType.class);

    this.tAssign =
      this.transaction.queries(ItemTypesAssignType.class);
    this.tRevoke =
      this.transaction.queries(ItemTypesRevokeType.class);

    this.resolver =
      CADatabaseTypePackageResolver.create(
        CATypePackageCompilers.create(
          CAStrings.create(Locale.ROOT),
          new CATypePackageParsers(),
          new CATypePackageCheckers()
        ),
        this.transaction
      );
  }

  /**
   * Installing type packages works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypePackageInstall0()
    throws Exception
  {
    final var t0 =
      new Integral(
        P,
        new RDottedName("x.t0"), "T0", 0L, 100L);

    final var p0 =
      new CATypePackage(
        P,
        "P0",
        Set.of(),
        Map.ofEntries(
          Map.entry(t0.name(), t0)
        ),
        Map.of()
      );

    this.tpInstall.execute(p0);
    this.assertIsInstalled(p0);
    this.transaction.commit();

    {
      final var t0r = this.tsGet.execute(t0.name());
      assertEquals(Optional.of(t0), t0r);
    }

    {
      assertEquals(
        Optional.of(p0.identifier()),
        this.tpSatisfying.execute(
          new TypePackageSatisfyingType.Parameters(
            p0.identifier().name(),
            new VersionRange(
              Version.of(1, 0, 0),
              true,
              Version.of(2, 0, 0),
              false
            )
          )
        )
      );
    }
  }

  /**
   * Installing type packages works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypePackageInstall1()
    throws Exception
  {
    final var t0 =
      new Integral(
        P,
        new RDottedName("x.t0"), "T0", 0L, 100L);

    final var p0 =
      new CATypePackage(
        P,
        "P0",
        Set.of(),
        Map.ofEntries(
          Map.entry(t0.name(), t0)
        ),
        Map.of()
      );

    final var p1 =
      new CATypePackage(
        new CATypePackageIdentifier(
          P.name(),
          Version.of(2, 0, 0)
        ),
        "P0",
        Set.of(),
        Map.ofEntries(
          Map.entry(t0.name(), t0)
        ),
        Map.of()
      );

    this.tpInstall.execute(p0);
    this.assertIsInstalled(p0);

    final var ex =
      assertThrows(CADatabaseException.class, () -> this.tpInstall.execute(p1));
    assertEquals(errorDuplicate(), ex.errorCode());
  }

  /**
   * Installing type packages works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypePackageInstall2()
    throws Exception
  {
    final var t0 =
      new Integral(
        P,
        new RDottedName("x.t0"), "T0", 0L, 100L);

    final var p0 =
      new CATypePackage(
        P,
        "P0",
        Set.of(),
        Map.ofEntries(
          Map.entry(t0.name(), t0)
        ),
        Map.of()
      );

    final var p1 =
      new CATypePackage(
        new CATypePackageIdentifier(
          P.name(),
          Version.of(2, 0, 0)
        ),
        "P0",
        Set.of(),
        Map.ofEntries(
          Map.entry(t0.name(), t0)
        ),
        Map.of()
      );

    this.tpInstall.execute(p0);
    this.assertIsInstalled(p0);
    this.transaction.commit();
    this.tpUninstall.execute(
      new CATypePackageUninstall(
        CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED,
        p0.identifier()
      )
    );
    this.assertIsNotInstalled(p0);
    this.transaction.commit();
    this.tpInstall.execute(p1);
    this.assertIsInstalled(p1);
  }

  /**
   * Type packages cannot be uninstalled if types are still referenced.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypePackageUninstallReferenced0()
    throws Exception
  {
    final var t0 =
      new Integral(
        P,
        new RDottedName("x.t0"), "T0", 0L, 100L);

    final var t1 =
      new CATypeRecord(
        P,
        new RDottedName("x.t1"),
        "T1",
        Map.ofEntries(
          Map.entry(
            new RDottedName("x.t1.f"),
            new CATypeField(
              new RDottedName("x.t1.f"),
              "F0",
              t0,
              true
            )
          )
        )
      );

    final var p0 =
      new CATypePackage(
        P,
        "P0",
        Set.of(),
        Map.ofEntries(Map.entry(t0.name(), t0)),
        Map.ofEntries(Map.entry(t1.name(), t1))
      );

    this.tpInstall.execute(p0);
    this.assertIsInstalled(p0);
    this.transaction.commit();

    final var itemId = CAItemID.random();
    this.iCreate.execute(itemId);
    this.iTypeAssign.execute(
      new ItemTypesAssignType.Parameters(itemId, Set.of(t1.name()))
    );

    final var ex =
      assertThrows(CADatabaseException.class, () -> {
        this.tpUninstall.execute(
          new CATypePackageUninstall(
            CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED,
            p0.identifier()
          )
        );
      });
    assertEquals(errorTypeReferenced(), ex.errorCode());

    this.transaction.rollback();
    this.assertIsInstalled(p0);
  }

  /**
   * Type packages can be uninstalled if types are revoked.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypePackageUninstallReferenced1()
    throws Exception
  {
    final var t0 =
      new Integral(
        P,
        new RDottedName("x.t0"), "T0", 0L, 100L);

    final var t1 =
      new CATypeRecord(
        P,
        new RDottedName("x.t1"),
        "T1",
        Map.ofEntries(
          Map.entry(
            new RDottedName("x.t1.f"),
            new CATypeField(
              new RDottedName("x.t1.f"),
              "F0",
              t0,
              true
            )
          )
        )
      );

    final var p0 =
      new CATypePackage(
        P,
        "P0",
        Set.of(),
        Map.ofEntries(Map.entry(t0.name(), t0)),
        Map.ofEntries(Map.entry(t1.name(), t1))
      );

    this.tpInstall.execute(p0);
    this.assertIsInstalled(p0);

    final var itemId = CAItemID.random();
    this.iCreate.execute(itemId);
    this.iTypeAssign.execute(
      new ItemTypesAssignType.Parameters(itemId, Set.of(t1.name()))
    );
    this.tpUninstall.execute(
      new CATypePackageUninstall(
        CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_REVOKE_TYPES,
        p0.identifier()
      )
    );
    this.assertIsNotInstalled(p0);
  }

  private void assertIsNotInstalled(
    final CATypePackage p)
    throws CADatabaseException
  {
    final var identifier = p.identifier();
    assertEquals(
      Optional.empty(),
      this.tpSatisfying.execute(new TypePackageSatisfyingType.Parameters(
        identifier.name(),
        new VersionRange(
          identifier.version(),
          true,
          identifier.version(),
          true
        )
      ))
    );
  }

  private void assertIsInstalled(
    final CATypePackage p)
    throws CADatabaseException
  {
    final var identifier = p.identifier();
    assertEquals(
      identifier,
      this.tpSatisfying.execute(new TypePackageSatisfyingType.Parameters(
        identifier.name(),
        new VersionRange(
          identifier.version(),
          true,
          identifier.version(),
          true
        )
      )).orElseThrow()
    );
  }

  /**
   * Searching for packages works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testTypePackageSearch0(
    final @TempDir Path directory)
    throws Exception
  {
    final var packages =
      this.populatePackages(directory);

    final var search =
      this.tpSearch.execute(
        new CATypePackageSearchParameters(
          new CAComparisonFuzzyType.IsSimilarTo<>("join"),
          100L
        ));

    final var page = search.pageCurrent(this.transaction);
    assertEquals(3, page.items().size());

    final var received =
      page.items()
        .stream()
        .collect(Collectors.toMap(
          CATypePackageSummary::identifier,
          Function.identity()));

    for (final var itemSummary : received.values()) {
      assertTrue(itemSummary.description().contains("join"));
    }
  }

  private List<CATypePackage> populatePackages(
    final Path directory)
    throws IOException, CADatabaseException
  {
    final var nouns =
      Files.lines(
        CATestDirectories.resourceOf(
          CADatabaseItemsSearchTest.class,
          directory,
          "nouns.txt"
        )).toList();

    final var results =
      new ArrayList<CATypePackage>();
    final var rng =
      new Random(1000L);

    for (int index = 0; index < 500; ++index) {
      final var thisDescription = new ArrayList<String>();
      thisDescription.add("Package");
      for (int count = 0; count < 10; ++count) {
        thisDescription.add(nouns.get(rng.nextInt(nouns.size())));
      }
      final var description =
        String.join(" ", thisDescription);

      final var p =
        new CATypePackage(
          new CATypePackageIdentifier(
            new RDottedName("p%d".formatted(Integer.valueOf(index))),
            Version.of(1, 0, 0)
          ),
          description,
          Set.of(),
          Map.of(),
          Map.of()
        );

      results.add(p);
      this.tpInstall.execute(p);
    }

    this.transaction.commit();
    return List.copyOf(results);
  }
}
