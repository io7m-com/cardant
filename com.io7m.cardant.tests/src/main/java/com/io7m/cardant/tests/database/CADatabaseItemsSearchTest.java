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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemDeleteMarkOnlyType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemDeleteType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemGetType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemMetadataPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemSearchType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemSetNameType;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemSetNameType.Parameters;
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType.ItemTypesAssignType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationPutType;
import com.io7m.cardant.database.api.CADatabaseQueriesTypePackagesType.TypePackageInstallType;
import com.io7m.cardant.database.api.CADatabaseQueriesUsersType.PutType;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.model.CAItemColumn;
import com.io7m.cardant.model.CAItemColumnOrdering;
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemSearchParameters;
import com.io7m.cardant.model.CAItemSummary;
import com.io7m.cardant.model.CAMetadataElementMatchType;
import com.io7m.cardant.model.CAMetadataElementMatchType.And;
import com.io7m.cardant.model.CAMetadataElementMatchType.Specific;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CAMetadataType.Text;
import com.io7m.cardant.model.CAMetadataValueMatchType.TextMatchType.ExactTextValue;
import com.io7m.cardant.model.CATypeRecord;
import com.io7m.cardant.model.CATypeRecordFieldIdentifier;
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.cardant.model.CAUser;
import com.io7m.cardant.model.CAUserID;
import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType.IsSimilarTo;
import com.io7m.cardant.model.comparisons.CAComparisonSetType;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.Anything;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.IsOverlapping;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.tests.CATestDirectories;
import com.io7m.cardant.tests.containers.CADatabaseFixture;
import com.io7m.cardant.tests.containers.CAFixtures;
import com.io7m.cardant.type_packages.checker.api.CATypePackageCheckerSuccess;
import com.io7m.cardant.type_packages.checkers.CATypePackageCheckers;
import com.io7m.cardant.type_packages.parsers.CATypePackageParsers;
import com.io7m.cardant.type_packages.resolver.api.CATypePackageResolverType;
import com.io7m.ervilla.api.EContainerSupervisorType;
import com.io7m.ervilla.test_extension.ErvillaCloseAfterSuite;
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
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.io7m.cardant.database.api.CADatabaseRole.CARDANT;
import static com.io7m.cardant.model.CAMetadataValueMatchType.AnyValue.ANY_VALUE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({ErvillaExtension.class, ZeladorExtension.class})
@ErvillaConfiguration(projectName = "com.io7m.cardant", disabledIfUnsupported = true)
public final class CADatabaseItemsSearchTest
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
      <p:TypeRecord Name="t0" Description="A record type."/>
      <p:TypeRecord Name="t1" Description="A record type."/>
    </p:Package>
    """;

  private static final Logger LOG =
    LoggerFactory.getLogger(CADatabaseItemsSearchTest.class);

  private static CADatabaseFixture DATABASE_FIXTURE;
  private CADatabaseConnectionType connection;
  private CADatabaseTransactionType transaction;
  private CADatabaseType database;
  private ItemCreateType itemCreate;
  private ItemSetNameType setName;
  private ItemGetType get;
  private ItemDeleteMarkOnlyType deleteMark;
  private LocationPutType locPut;
  private ItemSearchType searchQuery;
  private ItemDeleteType delete;
  private ItemGetType itemGet;
  private ItemMetadataPutType metaAdd;
  private ItemTypesAssignType itemTypeAssign;

  @BeforeAll
  public static void setupOnce(
    final @ErvillaCloseAfterSuite EContainerSupervisorType containers)
    throws Exception
  {
    DATABASE_FIXTURE =
      CAFixtures.database(CAFixtures.pod(containers));
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

    this.itemCreate =
      this.transaction.queries(ItemCreateType.class);
    this.itemGet =
      this.transaction.queries(ItemGetType.class);
    this.itemTypeAssign =
      this.transaction.queries(ItemTypesAssignType.class);
    this.setName =
      this.transaction.queries(ItemSetNameType.class);
    this.get =
      this.transaction.queries(ItemGetType.class);
    this.deleteMark =
      this.transaction.queries(ItemDeleteMarkOnlyType.class);
    this.delete =
      this.transaction.queries(ItemDeleteType.class);
    this.locPut =
      this.transaction.queries(LocationPutType.class);
    this.searchQuery =
      this.transaction.queries(ItemSearchType.class);
    this.metaAdd =
      this.transaction.queries(ItemMetadataPutType.class);

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

    transaction.queries(TypePackageInstallType.class).execute(packageV);
    transaction.commit();
  }

  /**
   * Searching for items by full text search works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemSearchByNameSimilarTo(
    final @TempDir Path directory)
    throws Exception
  {
    final var itemIDs =
      this.populateItems(directory);

    final var search =
      this.searchQuery.execute(new CAItemSearchParameters(
        new IsSimilarTo<>("join"),
        new CAComparisonFuzzyType.Anything<>(),
        new Anything<>(),
        CAMetadataElementMatchType.ANYTHING,
        new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
        100
      ));

    final var page = search.pageCurrent(this.transaction);
    assertEquals(3, page.items().size());

    final var received =
      page.items()
        .stream()
        .collect(Collectors.toMap(CAItemSummary::id, Function.identity()));

    for (final var itemSummary : received.values()) {
      assertTrue(itemSummary.name().contains("join"));
    }
  }

  /**
   * Searching for items by exact match works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemSearchByNameExact(
    final @TempDir Path directory)
    throws Exception
  {
    final var itemIDs =
      this.populateItems(directory);

    final var itemName =
      "Item method strip president character variety green committee repair couple desire";

    final var search =
      this.searchQuery.execute(new CAItemSearchParameters(
        new CAComparisonFuzzyType.IsEqualTo<>(itemName),
        new CAComparisonFuzzyType.Anything<>(),
        new Anything<>(),
        CAMetadataElementMatchType.ANYTHING,
        new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
        100
      ));

    final var page = search.pageCurrent(this.transaction);
    assertEquals(1, page.items().size());

    final var received =
      page.items()
        .stream()
        .collect(Collectors.toMap(CAItemSummary::id, Function.identity()));

    for (final var itemSummary : received.values()) {
      assertEquals(
        itemName,
        itemSummary.name()
      );
    }
  }

  /**
   * Searching for items by metadata works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemSearchByMetadata(
    final @TempDir Path directory)
    throws Exception
  {
    final var itemIDs =
      this.populateItems(directory);

    /*
     * Search for items that have the given metadata keys with
     * any values.
     */

    final var name0 = new RDottedName("com.io7m");
    final var name1 = "x";
    final var name2 = "i";

    final var search =
      this.searchQuery.execute(new CAItemSearchParameters(
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonFuzzyType.Anything<>(),
        new Anything<>(),
        new And(
          new Specific(
            new CAComparisonExactType.IsEqualTo<>(name0),
            new CAComparisonExactType.IsEqualTo<>(name1),
            new CAComparisonExactType.IsEqualTo<>(name2),
            ANY_VALUE
          ),
          new Specific(
            new CAComparisonExactType.Anything<>(),
            new CAComparisonExactType.Anything<>(),
            new CAComparisonExactType.IsEqualTo<>(name2),
            ANY_VALUE
          )
        ),
        new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
        1000L
      ));

    /*
     * XXX: This test tends to be slightly nondeterministic.
     */

    final var page = search.pageCurrent(this.transaction);
    assertTrue(page.items().size() >= 125);
    assertTrue(page.items().size() <= 127);

    for (final var summary : page.items()) {
      final var item =
        this.itemGet.execute(summary.id()).orElseThrow();

      final var name =
        new CATypeRecordFieldIdentifier(
          new CATypeRecordIdentifier(name0, new RDottedName(name1)),
          new RDottedName(name2)
        );

      assertTrue(item.metadata().containsKey(name));
    }
  }

  /**
   * Searching for items by metadata works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemSearchByMetadataValue(
    final @TempDir Path directory)
    throws Exception
  {
    final var itemIDs =
      this.populateItems(directory);

    /*
     * Search for items that have the given metadata keys with
     * specific values.
     */

    final var search =
      this.searchQuery.execute(new CAItemSearchParameters(
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonFuzzyType.Anything<>(),
        new Anything<>(),
        new Specific(
          new CAComparisonExactType.Anything<>(),
          new CAComparisonExactType.Anything<>(),
          new CAComparisonExactType.Anything<>(),
          new ExactTextValue("explanation")),
        new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
        100
      ));

    final var page = search.pageCurrent(this.transaction);
    assertEquals(2, page.items().size());

    for (final var summary : page.items()) {
      final var item =
        this.itemGet.execute(summary.id()).orElseThrow();

      assertTrue(
        item.metadata()
          .values()
          .stream()
          .anyMatch(x -> {
            if (x instanceof final Text q) {
              return Objects.equals(q.value(), "explanation");
            }
            return false;
          })
      );
    }
  }

  /**
   * Searching for items by types works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemSearchByTypesAny(
    final @TempDir Path directory)
    throws Exception
  {
    final var itemIDs =
      this.populateItems(directory);

    /*
     * Search for items that have any of the given types.
     */

    final var search =
      this.searchQuery.execute(new CAItemSearchParameters(
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonFuzzyType.Anything<>(),
        new IsOverlapping<>(
          Set.of(
            CATypeRecordIdentifier.of("com.io7m:t0"),
            CATypeRecordIdentifier.of("com.io7m:t1")
          )
        ),
        CAMetadataElementMatchType.ANYTHING,
        new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
        1000
      ));

    final var page = search.pageCurrent(this.transaction);
    assertEquals(171, page.items().size());

    for (final var summary : page.items()) {
      final var item =
        this.itemGet.execute(summary.id()).orElseThrow();

      final var t0present =
        item.types().contains(CATypeRecordIdentifier.of("com.io7m:t0"));
      final var t1present =
        item.types().contains(CATypeRecordIdentifier.of("com.io7m:t1"));

      assertTrue(t0present || t1present);
    }
  }

  /**
   * Searching for items by types works.
   *
   * @throws Exception On errors
   */

  @Test
  public void testItemSearchByTypesAll(
    final @TempDir Path directory)
    throws Exception
  {
    final var itemIDs =
      this.populateItems(directory);

    /*
     * Search for items that have all the given types.
     */

    final var search =
      this.searchQuery.execute(new CAItemSearchParameters(
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonFuzzyType.Anything<>(),
        new CAComparisonSetType.IsEqualTo<>(
          Set.of(
            CATypeRecordIdentifier.of("com.io7m:t0"),
            CATypeRecordIdentifier.of("com.io7m:t1")
          )
        ),
        CAMetadataElementMatchType.ANYTHING,
        new CAItemColumnOrdering(CAItemColumn.BY_ID, true),
        100
      ));

    final var page = search.pageCurrent(this.transaction);
    assertEquals(11, page.items().size());

    for (final var summary : page.items()) {
      final var item =
        this.itemGet.execute(summary.id()).orElseThrow();

      final var t0present =
        item.types().contains(CATypeRecordIdentifier.of("com.io7m:t0"));
      final var t1present =
        item.types().contains(CATypeRecordIdentifier.of("com.io7m:t1"));

      assertTrue(t0present && t1present);
    }
  }

  private List<CAItemID> populateItems(
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

    final var items = new ArrayList<CAItemID>();
    final var rng = new Random(1000L);

    final var type0 =
      new CATypeRecord(
        CATypeRecordIdentifier.of("com.io7m:t0"),
        "A type.",
        Map.of()
      );
    final var type1 =
      new CATypeRecord(
        CATypeRecordIdentifier.of("com.io7m:t1"),
        "A type.",
        Map.of()
      );

    for (int index = 0; index < 500; ++index) {
      final var thisName = new ArrayList<String>();
      thisName.add("Item");
      for (int count = 0; count < 10; ++count) {
        thisName.add(nouns.get(rng.nextInt(nouns.size())));
      }
      final var itemName = String.join(" ", thisName);
      LOG.debug("[{}] {}", Integer.valueOf(index), itemName);

      final var itemId = CAItemID.random();
      this.itemCreate.execute(itemId);
      this.setName.execute(new Parameters(itemId, itemName));

      /*
       * Create some metadata.
       */

      final var meta = new HashSet<CAMetadataType>();
      for (int metaIndex = 1; metaIndex < 10; ++metaIndex) {
        final var metaValue =
          thisName.get(metaIndex);
        final var metaName =
          metaValue.substring(0, 1);
        meta.add(
          new Text(
            CATypeRecordFieldIdentifier.of("com.io7m:x." + metaName),
            metaValue
          )
        );

        /*
         * Assign some types.
         */

        if (metaValue.toUpperCase().startsWith("O")) {
          this.itemTypeAssign.execute(new ItemTypesAssignType.Parameters(
            itemId,
            Set.of(type0.name())
          ));
        }

        if (metaValue.toUpperCase().startsWith("I")) {
          this.itemTypeAssign.execute(new ItemTypesAssignType.Parameters(
            itemId,
            Set.of(type1.name())
          ));
        }
      }

      this.metaAdd.execute(new ItemMetadataPutType.Parameters(itemId, meta));
      items.add(itemId);
    }

    this.transaction.commit();
    return List.copyOf(items);
  }

}
