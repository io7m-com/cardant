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

import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.cardant.model.type_package.CANameUnqualified;
import com.io7m.cardant.model.type_package.CATypeFieldDeclaration;
import com.io7m.cardant.model.type_package.CATypePackage;
import com.io7m.cardant.model.type_package.CATypePackageDeclaration;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.model.type_package.CATypePackageImport;
import com.io7m.cardant.model.type_package.CATypeRecordDeclaration;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.tests.CATestDirectories;
import com.io7m.cardant.type_packages.checker.api.CATypePackageCheckerFailure;
import com.io7m.cardant.type_packages.checker.api.CATypePackageCheckerResultType;
import com.io7m.cardant.type_packages.checker.api.CATypePackageCheckerSuccess;
import com.io7m.cardant.type_packages.checkers.CATypePackageCheckers;
import com.io7m.cardant.type_packages.parsers.CATypePackageParsers;
import com.io7m.cardant.type_packages.parsers.CATypePackageSerializers;
import com.io7m.cardant.type_packages.resolver.api.CATypePackageResolverType;
import com.io7m.lanark.core.RDottedName;
import com.io7m.verona.core.Version;
import com.io7m.verona.core.VersionRange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public final class CATypePackageCheckersTest
{
  private static final CATypePackageIdentifier P =
    new CATypePackageIdentifier(
      new RDottedName("com.io7m"),
      Version.of(1, 0, 0)
    );

  private static final Logger LOG =
    LoggerFactory.getLogger(CATypePackageCheckersTest.class);

  private CATypePackageParsers parsers;
  private CATypePackageCheckers checkers;
  private CATypePackageResolverType resolver;
  private CAStrings strings;
  private CATypePackageSerializers serializers;

  @BeforeEach
  public void setup()
  {
    this.parsers =
      new CATypePackageParsers();
    this.serializers =
      new CATypePackageSerializers();
    this.checkers =
      new CATypePackageCheckers();
    this.resolver =
      Mockito.mock(CATypePackageResolverType.class);
    this.strings =
      CAStrings.create(Locale.ROOT);
  }

  @Test
  public void testImportMissing()
  {
    final var packDecl =
      new CATypePackageDeclaration(
        new CATypePackageIdentifier(
          new RDottedName("com.io7m.other"),
          Version.of(1, 0, 0)
        ),
        "T",
        Set.of(
          new CATypePackageImport(
            new RDottedName("com.io7m.x"),
            new VersionRange(
              Version.of(1, 0, 0),
              true,
              Version.of(2, 0, 0),
              false
            )
          )
        ),
        Map.of(),
        Map.of()
      );

    final var r =
      this.checkers.createChecker(this.strings, this.resolver, packDecl)
        .execute();

    dumpCheckerResult(r);

    final var f =
      assertInstanceOf(CATypePackageCheckerFailure.class, r);

    {
      final var e = f.errors().get(0);
      assertEquals("error-import-unsatisfied", e.errorCode());
    }
  }

  @Test
  public void testTypeScalarNameInvalid()
  {
    final var packDecl =
      new CATypePackageDeclaration(
        new CATypePackageIdentifier(
          new RDottedName("a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a"),
          Version.of(1, 0, 0)
        ),
        "T",
        Set.of(),
        Map.of(
          new CANameUnqualified("x"),
          new CATypeScalarType.Integral(
            P,
            new RDottedName("x"),
            "X",
            0L,
            100L
          )
        ),
        Map.of()
      );

    final var r =
      this.checkers.createChecker(this.strings, this.resolver, packDecl)
        .execute();

    dumpCheckerResult(r);

    final var f =
      assertInstanceOf(CATypePackageCheckerFailure.class, r);

    {
      final var e = f.errors().get(0);
      assertEquals("error-name-qualified-invalid", e.errorCode());
    }
  }

  @Test
  public void testTypeRecordNameInvalid()
  {
    final var packDecl =
      new CATypePackageDeclaration(
        new CATypePackageIdentifier(
          new RDottedName("a.a.a.a.a.a.a.a.a.a.a.a.a.a.a.a"),
          Version.of(1, 0, 0)
        ),
        "T",
        Set.of(),
        Map.of(),
        Map.of(
          new CANameUnqualified("x"),
          new CATypeRecordDeclaration(
            new CANameUnqualified("x"),
            "X",
            Map.of()
          )
        )
      );

    final var r =
      this.checkers.createChecker(this.strings, this.resolver, packDecl)
        .execute();

    dumpCheckerResult(r);

    final var f =
      assertInstanceOf(CATypePackageCheckerFailure.class, r);

    {
      final var e = f.errors().get(0);
      assertEquals("error-name-qualified-invalid", e.errorCode());
    }
  }

  @Test
  public void testTypeRecordFieldTypeMissing()
  {
    final var packDecl =
      new CATypePackageDeclaration(
        new CATypePackageIdentifier(
          new RDottedName("a"),
          Version.of(1, 0, 0)
        ),
        "T",
        Set.of(),
        Map.of(),
        Map.of(
          new CANameUnqualified("x"),
          new CATypeRecordDeclaration(
            new CANameUnqualified("x"),
            "X",
            Map.of(
              new CANameUnqualified("f"),
              new CATypeFieldDeclaration(
                new CANameUnqualified("f"),
                "F",
                new CANameUnqualified("t"),
                true
              )
            )
          )
        )
      );

    final var r =
      this.checkers.createChecker(this.strings, this.resolver, packDecl)
        .execute();

    dumpCheckerResult(r);

    final var f =
      assertInstanceOf(CATypePackageCheckerFailure.class, r);

    {
      final var e = f.errors().get(0);
      assertEquals("error-type-record-field-type-nonexistent", e.errorCode());
    }
  }

  @Test
  public void testTypeRecordFieldTypeNameInvalid()
  {
    final var packDecl =
      new CATypePackageDeclaration(
        new CATypePackageIdentifier(
          new RDottedName("a"),
          Version.of(1, 0, 0)
        ),
        "T",
        Set.of(),
        Map.of(
          new CANameUnqualified("x"),
          new CATypeScalarType.Integral(
            P,
            new RDottedName("x"),
            "X",
            0L,
            100L
          )
        ),
        Map.of(
          new CANameUnqualified("x"),
          new CATypeRecordDeclaration(
            new CANameUnqualified("x"),
            "X",
            Map.of(
              new CANameUnqualified("cdfdf4253410dc9627b7ab4687e50f4036eb8a5f125210ca3fbeda989e536afe6c"),
              new CATypeFieldDeclaration(
                new CANameUnqualified("cdfdf4253410dc9627b7ab4687e50f4036eb8a5f125210ca3fbeda989e536afe6c"),
                "F",
                new CANameUnqualified("x"),
                true
              )
            )
          )
        )
      );

    final var r =
      this.checkers.createChecker(this.strings, this.resolver, packDecl)
        .execute();

    dumpCheckerResult(r);

    final var f =
      assertInstanceOf(CATypePackageCheckerFailure.class, r);

    {
      final var e = f.errors().get(0);
      assertEquals("error-name-qualified-invalid", e.errorCode());
    }
  }

  @Test
  public void testFile(
    final @TempDir Path directory)
    throws Exception
  {
    final var file =
      CATestDirectories.resourceOf(
        CATypePackageCheckersTest.class,
        directory,
        "tpack0.xml"
      );

    final var packDecl =
      this.parsers.parseFile(file);

    Mockito.when(this.resolver.findTypePackageId(
      new RDottedName("com.io7m.other"),
      new VersionRange(
        Version.of(1, 0, 0),
        true,
        Version.of(2, 0, 0),
        false
      )
    )).thenReturn(
      Optional.of(
        new CATypePackageIdentifier(
          new RDottedName("com.io7m.other"),
          Version.of(1, 0, 0)
        )
      )
    );

    Mockito.when(this.resolver.findTypeScalar(
      new RDottedName("com.io7m.exa.t")
    )).thenReturn(
      Optional.of(
        new CATypeScalarType.Integral(
          P,
          new RDottedName("com.io7m.exa.t"),
          "A type",
          0L,
          100L
        )
      )
    );

    final var r =
      this.checkers.createChecker(this.strings, this.resolver, packDecl)
        .execute();

    dumpCheckerResult(r);

    final CATypePackageCheckerSuccess success =
      assertInstanceOf(CATypePackageCheckerSuccess.class, r);

    this.roundTrip(directory, success.typePackage());
  }

  private static void dumpCheckerResult(
    final CATypePackageCheckerResultType r)
  {
    switch (r) {
      case final CATypePackageCheckerFailure f -> {
        f.errors().forEach(e -> {
          LOG.debug("{}", e);
        });
      }
      case final CATypePackageCheckerSuccess s -> {

      }
    }
  }

  private void roundTrip(
    final Path directory,
    final CATypePackage pack)
    throws Exception
  {
    final var f0 =
      directory.resolve(UUID.randomUUID() + ".xml");

    this.serializers.serializeFile(f0, pack);

    final var packDecl =
      this.parsers.parseFile(f0);
    final CATypePackageCheckerSuccess success =
      (CATypePackageCheckerSuccess)
        this.checkers.createChecker(this.strings, this.resolver, packDecl)
          .execute();

    assertEquals(pack, success.typePackage());
  }
}
