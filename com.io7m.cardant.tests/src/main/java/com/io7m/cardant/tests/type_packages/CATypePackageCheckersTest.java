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
import com.io7m.cardant.model.type_package.CATypePackage;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.cardant.tests.CATestDirectories;
import com.io7m.cardant.type_packages.CATypePackageCheckerFailure;
import com.io7m.cardant.type_packages.CATypePackageCheckerResultType;
import com.io7m.cardant.type_packages.CATypePackageCheckerSuccess;
import com.io7m.cardant.type_packages.CATypePackageCheckers;
import com.io7m.cardant.type_packages.CATypePackageParsers;
import com.io7m.cardant.type_packages.CATypePackageResolverType;
import com.io7m.cardant.type_packages.CATypePackageSerializers;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public final class CATypePackageCheckersTest
{
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

    Mockito.when(this.resolver.findTypePackage(
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

    dumpResult(r);

    final CATypePackageCheckerSuccess success =
      assertInstanceOf(CATypePackageCheckerSuccess.class, r);

    this.roundTrip(directory, success.typePackage());
  }

  private static void dumpResult(
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
