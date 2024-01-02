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


package com.io7m.cardant.tests.type_packages;

import com.io7m.cardant.model.type_package.CATypePackage;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.model.type_package.CATypePackageTypeRemovalBehavior;
import com.io7m.cardant.model.type_package.CATypePackageUpgrade;
import com.io7m.cardant.model.type_package.CATypePackageVersionBehavior;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.type_packages.checker.api.CATypePackageCheckerFailure;
import com.io7m.cardant.type_packages.checker.api.CATypePackageCheckerSuccess;
import com.io7m.cardant.type_packages.checkers.CATypePackageCheckers;
import com.io7m.cardant.type_packages.parsers.CATypePackageParsers;
import com.io7m.cardant.type_packages.resolver.api.CATypePackageResolverType;
import com.io7m.cardant.type_packages.upgrades.CATypePackageUpgradePlanner;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpFail;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpPackageInstall;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpPackageSetVersion;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpType;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpTypeRecordCreate;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpTypeRecordFieldRemove;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpTypeRecordFieldUpdate;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpTypeRecordRemove;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpTypeScalarRemove;
import com.io7m.cardant.type_packages.upgrades.api.CATypePackageUOpTypeScalarUpdate;
import com.io7m.lanark.core.RDottedName;
import com.io7m.verona.core.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;

public final class CATypePackageUpgradePlannerTest
{
  private static final CATypePackageIdentifier P =
    new CATypePackageIdentifier(
      new RDottedName("com.io7m"),
      Version.of(1, 0, 0)
    );

  private static final Logger LOG =
    LoggerFactory.getLogger(CATypePackageCheckersTest.class);

  private CATypePackageResolverType resolver;
  private CAStrings strings;
  private CATypePackageParsers parsers;
  private CATypePackageCheckers checkers;

  @BeforeEach
  public void setup()
  {
    this.parsers =
      new CATypePackageParsers();
    this.checkers =
      new CATypePackageCheckers();
    this.resolver =
      Mockito.mock(CATypePackageResolverType.class);
    this.strings =
      CAStrings.create(Locale.ROOT);
  }

  private CATypePackage compile(
    final String text)
    throws Exception
  {
    final var decl =
      this.parsers.parse(
        URI.create("urn:in"),
        new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))
      );

    final var result =
      this.checkers.createChecker(this.strings, this.resolver, decl)
        .execute();

    return switch (result) {
      case final CATypePackageCheckerFailure f -> {
        throw new IllegalStateException();
      }
      case final CATypePackageCheckerSuccess s -> {
        yield s.typePackage();
      }
    };
  }

  /**
   * Upgrades for non-installed packages are installations.
   *
   * @throws Exception On errors
   */

  @Test
  public void testUninstalledInstall()
    throws Exception
  {
    final var packUpgrade =
      this.compile("""
                     <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
                       <p:PackageInfo Name="com.io7m.p" Version="1.1.0" Description="An example."/>
                     </p:Package>""");

    final var planner =
      CATypePackageUpgradePlanner.create(
        this.strings,
        this.resolver,
        new CATypePackageUpgrade(
          CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED,
          CATypePackageVersionBehavior.VERSION_DISALLOW_DOWNGRADES,
          packUpgrade
        )
      );

    Mockito.when(this.resolver.findTypePackageId(any(), any()))
      .thenReturn(Optional.empty());

    final var operations = planner.plan();
    dumpOperations(operations);

    assertEquals(
      new CATypePackageUOpPackageInstall(packUpgrade),
      operations.get(0)
    );
    assertEquals(1, operations.size());
  }

  /**
   * No-op upgrades are no-ops.
   *
   * @throws Exception On errors
   */

  @Test
  public void testEmptyPackageNoop0()
    throws Exception
  {
    final var packInstalled =
      this.compile("""
                     <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
                       <p:PackageInfo Name="com.io7m.p" Version="1.0.0" Description="An example."/>
                     </p:Package>""");

    final var packUpgrade =
      this.compile("""
                     <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
                       <p:PackageInfo Name="com.io7m.p" Version="1.1.0" Description="An example."/>
                     </p:Package>""");

    final var planner =
      CATypePackageUpgradePlanner.create(
        this.strings,
        this.resolver,
        new CATypePackageUpgrade(
          CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED,
          CATypePackageVersionBehavior.VERSION_DISALLOW_DOWNGRADES,
          packUpgrade
        )
      );

    Mockito.when(this.resolver.findTypePackageId(any(), any()))
      .thenReturn(Optional.of(packInstalled.identifier()));
    Mockito.when(this.resolver.findTypePackage(packInstalled.identifier()))
      .thenReturn(Optional.of(packInstalled));

    final var operations = planner.plan();
    dumpOperations(operations);

    assertEquals(
      new CATypePackageUOpPackageSetVersion(packUpgrade.identifier()),
      operations.get(0)
    );
    assertEquals(1, operations.size());
  }

  /**
   * No-op downgrades are no-ops.
   *
   * @throws Exception On errors
   */

  @Test
  public void testEmptyPackageNoop1()
    throws Exception
  {
    final var packInstalled =
      this.compile("""
                     <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
                       <p:PackageInfo Name="com.io7m.p" Version="1.1.0" Description="An example."/>
                     </p:Package>""");

    final var packUpgrade =
      this.compile("""
                     <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
                       <p:PackageInfo Name="com.io7m.p" Version="1.0.0" Description="An example."/>
                     </p:Package>""");

    final var planner =
      CATypePackageUpgradePlanner.create(
        this.strings,
        this.resolver,
        new CATypePackageUpgrade(
          CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED,
          CATypePackageVersionBehavior.VERSION_ALLOW_DOWNGRADES,
          packUpgrade
        )
      );

    Mockito.when(this.resolver.findTypePackageId(any(), any()))
      .thenReturn(Optional.of(packInstalled.identifier()));
    Mockito.when(this.resolver.findTypePackage(packInstalled.identifier()))
      .thenReturn(Optional.of(packInstalled));

    final var operations = planner.plan();
    dumpOperations(operations);

    assertEquals(
      new CATypePackageUOpPackageSetVersion(packUpgrade.identifier()),
      operations.get(0)
    );
    assertEquals(1, operations.size());
  }

  /**
   * No-op downgrades are fail fast if requested.
   *
   * @throws Exception On errors
   */

  @Test
  public void testEmptyPackageNoopDowngradeDisallowed()
    throws Exception
  {
    final var packInstalled =
      this.compile("""
                     <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
                       <p:PackageInfo Name="com.io7m.p" Version="1.1.0" Description="An example."/>
                     </p:Package>""");

    final var packUpgrade =
      this.compile("""
                     <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
                       <p:PackageInfo Name="com.io7m.p" Version="1.0.0" Description="An example."/>
                     </p:Package>""");

    final var planner =
      CATypePackageUpgradePlanner.create(
        this.strings,
        this.resolver,
        new CATypePackageUpgrade(
          CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED,
          CATypePackageVersionBehavior.VERSION_DISALLOW_DOWNGRADES,
          packUpgrade
        )
      );

    Mockito.when(this.resolver.findTypePackageId(any(), any()))
      .thenReturn(Optional.of(packInstalled.identifier()));
    Mockito.when(this.resolver.findTypePackage(packInstalled.identifier()))
      .thenReturn(Optional.of(packInstalled));

    final var operations = planner.plan();
    dumpOperations(operations);

    assertInstanceOf(
      CATypePackageUOpFail.class,
      operations.get(0)
    );
    assertEquals(1, operations.size());
  }

  /**
   * Scalar types can be removed.
   *
   * @throws Exception On errors
   */

  @Test
  public void testScalarTypeRemovals0()
    throws Exception
  {
    final var packInstalled =
      this.compile("""
                     <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
                       <p:PackageInfo Name="com.io7m.p" Version="1.0.0" Description="An example."/>
                       <p:TypeScalarMonetary Name="t0" Description="A monetary type." RangeLower="0" RangeUpper="1000000.0"/>
                     </p:Package>""");

    final var packUpgrade =
      this.compile("""
                     <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
                       <p:PackageInfo Name="com.io7m.p" Version="1.1.0" Description="An example."/>
                     </p:Package>""");

    final var planner =
      CATypePackageUpgradePlanner.create(
        this.strings,
        this.resolver,
        new CATypePackageUpgrade(
          CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED,
          CATypePackageVersionBehavior.VERSION_DISALLOW_DOWNGRADES,
          packUpgrade
        )
      );

    Mockito.when(this.resolver.findTypePackageId(any(), any()))
      .thenReturn(Optional.of(packInstalled.identifier()));
    Mockito.when(this.resolver.findTypePackage(packInstalled.identifier()))
      .thenReturn(Optional.of(packInstalled));

    final var operations = planner.plan();
    dumpOperations(operations);

    assertEquals(
      new RDottedName("com.io7m.p.t0"),
      assertInstanceOf(CATypePackageUOpTypeScalarRemove.class, operations.get(0))
        .removal()
        .typeScalar()
        .name()
    );
    assertEquals(
      new CATypePackageUOpPackageSetVersion(packUpgrade.identifier()),
      operations.get(1)
    );
    assertEquals(2, operations.size());
  }

  /**
   * Scalar types can be created.
   *
   * @throws Exception On errors
   */

  @Test
  public void testScalarTypeCreation0()
    throws Exception
  {
    final var packInstalled =
      this.compile("""
                     <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
                       <p:PackageInfo Name="com.io7m.p" Version="1.0.0" Description="An example."/>
                     </p:Package>""");

    final var packUpgrade =
      this.compile("""
                     <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
                       <p:PackageInfo Name="com.io7m.p" Version="1.1.0" Description="An example."/>
                       <p:TypeScalarMonetary Name="t0" Description="A monetary type." RangeLower="0" RangeUpper="1000000.0"/>
                     </p:Package>""");

    final var planner =
      CATypePackageUpgradePlanner.create(
        this.strings,
        this.resolver,
        new CATypePackageUpgrade(
          CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED,
          CATypePackageVersionBehavior.VERSION_DISALLOW_DOWNGRADES,
          packUpgrade
        )
      );

    Mockito.when(this.resolver.findTypePackageId(any(), any()))
      .thenReturn(Optional.of(packInstalled.identifier()));
    Mockito.when(this.resolver.findTypePackage(packInstalled.identifier()))
      .thenReturn(Optional.of(packInstalled));

    final var operations = planner.plan();
    dumpOperations(operations);

    assertEquals(
      new RDottedName("com.io7m.p.t0"),
      assertInstanceOf(CATypePackageUOpTypeScalarUpdate.class, operations.get(0))
        .typeScalar()
        .name()
    );
    assertEquals(
      new CATypePackageUOpPackageSetVersion(packUpgrade.identifier()),
      operations.get(1)
    );
    assertEquals(2, operations.size());
  }

  /**
   * Record types can be created.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRecordTypeCreation0()
    throws Exception
  {
    final var packInstalled =
      this.compile("""
                     <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
                       <p:PackageInfo Name="com.io7m.p" Version="1.0.0" Description="An example."/>
                       <p:TypeScalarMonetary Name="t0" Description="A monetary type." RangeLower="0" RangeUpper="1000000.0"/>
                     </p:Package>""");

    final var packUpgrade =
      this.compile("""
                     <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
                       <p:PackageInfo Name="com.io7m.p" Version="1.1.0" Description="An example."/>
                       <p:TypeScalarMonetary Name="t0" Description="A monetary type." RangeLower="0" RangeUpper="1000000.0"/>
                       <p:TypeRecord Name="t1" Description="A record type.">
                         <p:Field Name="f0" Description="A Q field." Type="t0"/>
                       </p:TypeRecord>
                     </p:Package>""");

    final var planner =
      CATypePackageUpgradePlanner.create(
        this.strings,
        this.resolver,
        new CATypePackageUpgrade(
          CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED,
          CATypePackageVersionBehavior.VERSION_DISALLOW_DOWNGRADES,
          packUpgrade
        )
      );

    Mockito.when(this.resolver.findTypePackageId(any(), any()))
      .thenReturn(Optional.of(packInstalled.identifier()));
    Mockito.when(this.resolver.findTypePackage(packInstalled.identifier()))
      .thenReturn(Optional.of(packInstalled));

    final var operations = planner.plan();
    dumpOperations(operations);

    assertEquals(
      new RDottedName("com.io7m.p.t0"),
      assertInstanceOf(CATypePackageUOpTypeScalarUpdate.class, operations.get(0))
        .typeScalar()
        .name()
    );
    assertEquals(
      new RDottedName("com.io7m.p.t1"),
      assertInstanceOf(CATypePackageUOpTypeRecordCreate.class, operations.get(1))
        .typeRecord()
        .name()
    );
    assertEquals(
      new CATypePackageUOpPackageSetVersion(packUpgrade.identifier()),
      operations.get(2)
    );
    assertEquals(3, operations.size());
  }

  /**
   * Record types can be updated.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRecordFieldCreation0()
    throws Exception
  {
    final var packInstalled =
      this.compile("""
                     <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
                       <p:PackageInfo Name="com.io7m.p" Version="1.1.0" Description="An example."/>
                       <p:TypeScalarMonetary Name="t0" Description="A monetary type." RangeLower="0" RangeUpper="1000000.0"/>
                       <p:TypeRecord Name="t1" Description="A record type.">
                         <p:Field Name="f0" Description="A Q field." Type="t0"/>
                       </p:TypeRecord>
                     </p:Package>""");

    final var packUpgrade =
      this.compile("""
                     <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
                       <p:PackageInfo Name="com.io7m.p" Version="1.1.0" Description="An example."/>
                       <p:TypeScalarMonetary Name="t0" Description="A monetary type." RangeLower="0" RangeUpper="1000000.0"/>
                       <p:TypeRecord Name="t1" Description="A record type.">
                         <p:Field Name="f0" Description="A Q field." Type="t0"/>
                         <p:Field Name="f1" Description="A Q field." Type="t0"/>
                       </p:TypeRecord>
                     </p:Package>""");

    final var planner =
      CATypePackageUpgradePlanner.create(
        this.strings,
        this.resolver,
        new CATypePackageUpgrade(
          CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED,
          CATypePackageVersionBehavior.VERSION_DISALLOW_DOWNGRADES,
          packUpgrade
        )
      );

    Mockito.when(this.resolver.findTypePackageId(any(), any()))
      .thenReturn(Optional.of(packInstalled.identifier()));
    Mockito.when(this.resolver.findTypePackage(packInstalled.identifier()))
      .thenReturn(Optional.of(packInstalled));

    final var operations = planner.plan();
    dumpOperations(operations);

    assertEquals(
      new RDottedName("com.io7m.p.t0"),
      assertInstanceOf(CATypePackageUOpTypeScalarUpdate.class, operations.get(0))
        .typeScalar()
        .name()
    );
    assertEquals(
      new RDottedName("com.io7m.p.t1.f0"),
      assertInstanceOf(CATypePackageUOpTypeRecordFieldUpdate.class, operations.get(1))
        .update()
        .typeField()
        .name()
    );
    assertEquals(
      new RDottedName("com.io7m.p.t1.f1"),
      assertInstanceOf(CATypePackageUOpTypeRecordFieldUpdate.class, operations.get(2))
        .update()
        .typeField()
        .name()
    );
    assertEquals(
      new CATypePackageUOpPackageSetVersion(packUpgrade.identifier()),
      operations.get(3)
    );
    assertEquals(4, operations.size());
  }

  /**
   * Record types can be updated.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRecordFieldRemoval0()
    throws Exception
  {
    final var packInstalled =
      this.compile("""
                     <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
                       <p:PackageInfo Name="com.io7m.p" Version="1.1.0" Description="An example."/>
                       <p:TypeScalarMonetary Name="t0" Description="A monetary type." RangeLower="0" RangeUpper="1000000.0"/>
                       <p:TypeRecord Name="t1" Description="A record type.">
                         <p:Field Name="f0" Description="A Q field." Type="t0"/>
                         <p:Field Name="f1" Description="A Q field." Type="t0"/>
                       </p:TypeRecord>
                     </p:Package>""");

    final var packUpgrade =
      this.compile("""
                     <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
                       <p:PackageInfo Name="com.io7m.p" Version="1.1.0" Description="An example."/>
                       <p:TypeScalarMonetary Name="t0" Description="A monetary type." RangeLower="0" RangeUpper="1000000.0"/>
                       <p:TypeRecord Name="t1" Description="A record type.">
                         <p:Field Name="f0" Description="A Q field." Type="t0"/>
                       </p:TypeRecord>
                     </p:Package>""");

    final var planner =
      CATypePackageUpgradePlanner.create(
        this.strings,
        this.resolver,
        new CATypePackageUpgrade(
          CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED,
          CATypePackageVersionBehavior.VERSION_DISALLOW_DOWNGRADES,
          packUpgrade
        )
      );

    Mockito.when(this.resolver.findTypePackageId(any(), any()))
      .thenReturn(Optional.of(packInstalled.identifier()));
    Mockito.when(this.resolver.findTypePackage(packInstalled.identifier()))
      .thenReturn(Optional.of(packInstalled));

    final var operations = planner.plan();
    dumpOperations(operations);

    assertEquals(
      new RDottedName("com.io7m.p.t0"),
      assertInstanceOf(CATypePackageUOpTypeScalarUpdate.class, operations.get(0))
        .typeScalar()
        .name()
    );
    assertEquals(
      new RDottedName("com.io7m.p.t1.f1"),
      assertInstanceOf(CATypePackageUOpTypeRecordFieldRemove.class, operations.get(1))
        .field()
        .name()
    );
    assertEquals(
      new RDottedName("com.io7m.p.t1.f0"),
      assertInstanceOf(CATypePackageUOpTypeRecordFieldUpdate.class, operations.get(2))
        .update()
        .typeField()
        .name()
    );
    assertEquals(
      new CATypePackageUOpPackageSetVersion(packUpgrade.identifier()),
      operations.get(3)
    );
    assertEquals(4, operations.size());
  }

  /**
   * Record types can be removed.
   *
   * @throws Exception On errors
   */

  @Test
  public void testRecordRemoval0()
    throws Exception
  {
    final var packInstalled =
      this.compile("""
                     <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
                       <p:PackageInfo Name="com.io7m.p" Version="1.1.0" Description="An example."/>
                       <p:TypeScalarMonetary Name="t0" Description="A monetary type." RangeLower="0" RangeUpper="1000000.0"/>
                       <p:TypeRecord Name="t1" Description="A record type.">
                         <p:Field Name="f0" Description="A Q field." Type="t0"/>
                       </p:TypeRecord>
                     </p:Package>""");

    final var packUpgrade =
      this.compile("""
                     <p:Package xmlns:p="com.io7m.cardant:type_packages:1">
                       <p:PackageInfo Name="com.io7m.p" Version="1.1.0" Description="An example."/>
                       <p:TypeScalarMonetary Name="t0" Description="A monetary type." RangeLower="0" RangeUpper="1000000.0"/>
                     </p:Package>""");

    final var planner =
      CATypePackageUpgradePlanner.create(
        this.strings,
        this.resolver,
        new CATypePackageUpgrade(
          CATypePackageTypeRemovalBehavior.TYPE_REMOVAL_FAIL_IF_TYPES_REFERENCED,
          CATypePackageVersionBehavior.VERSION_DISALLOW_DOWNGRADES,
          packUpgrade
        )
      );

    Mockito.when(this.resolver.findTypePackageId(any(), any()))
      .thenReturn(Optional.of(packInstalled.identifier()));
    Mockito.when(this.resolver.findTypePackage(packInstalled.identifier()))
      .thenReturn(Optional.of(packInstalled));

    final var operations = planner.plan();
    dumpOperations(operations);

    assertEquals(
      new RDottedName("com.io7m.p.t0"),
      assertInstanceOf(CATypePackageUOpTypeScalarUpdate.class, operations.get(0))
        .typeScalar()
        .name()
    );
    assertEquals(
      new RDottedName("com.io7m.p.t1"),
      assertInstanceOf(CATypePackageUOpTypeRecordRemove.class, operations.get(1))
        .removal()
        .typeRecord()
        .name()
    );
    assertEquals(
      new CATypePackageUOpPackageSetVersion(packUpgrade.identifier()),
      operations.get(2)
    );
    assertEquals(3, operations.size());
  }

  private static void dumpOperations(
    final Iterable<CATypePackageUOpType> operations)
  {
    operations.forEach(x -> LOG.debug("{}", x));
  }
}
