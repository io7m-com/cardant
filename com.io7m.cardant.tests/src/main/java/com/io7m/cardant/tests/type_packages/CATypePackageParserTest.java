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

import com.io7m.anethum.api.ParsingException;
import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.cardant.model.type_package.CANameUnqualified;
import com.io7m.cardant.model.type_package.CATypeRecordDeclaration;
import com.io7m.cardant.tests.CATestDirectories;
import com.io7m.cardant.type_packages.CATypePackageParsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class CATypePackageParserTest
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CATypePackageParserTest.class);

  private CATypePackageParsers parsers;

  @BeforeEach
  public void setup()
  {
    this.parsers = new CATypePackageParsers();
  }

  @Test
  public void testFile(
    final @TempDir Path directory)
    throws Exception
  {
    final var file =
      CATestDirectories.resourceOf(
        CATypePackageParserTest.class,
        directory,
        "tpack0.xml"
      );

    final var pack =
      this.parsers.parseFile(file);
    final var scalars =
      pack.scalarTypes();
    final var records =
      pack.recordTypes();

    {
      final var t =
        assertInstanceOf(
          CATypeScalarType.Monetary.class,
          scalars.get(new CANameUnqualified("t0"))
        );

      assertEquals("t0", t.name().value());
      assertEquals("A monetary type.", t.description());
      assertEquals(new BigDecimal("0"), t.rangeLower());
      assertEquals(new BigDecimal("1000000.0"), t.rangeUpper());
    }

    {
      final var t =
        assertInstanceOf(
          CATypeScalarType.Integral.class,
          scalars.get(new CANameUnqualified("t1"))
        );

      assertEquals("t1", t.name().value());
      assertEquals("An integral type.", t.description());
      assertEquals(0L, t.rangeLower());
      assertEquals(1000L, t.rangeUpper());
    }

    {
      final var t =
        assertInstanceOf(
          CATypeScalarType.Real.class,
          scalars.get(new CANameUnqualified("t2"))
        );

      assertEquals("t2", t.name().value());
      assertEquals("A real type.", t.description());
      assertEquals(0.0, t.rangeLower());
      assertEquals(100.0, t.rangeUpper());
    }

    {
      final var t =
        assertInstanceOf(
          CATypeScalarType.Text.class,
          scalars.get(new CANameUnqualified("t3"))
        );

      assertEquals("t3", t.name().value());
      assertEquals("A text type.", t.description());
      assertEquals(".*", t.pattern());
    }

    {
      final var t =
        assertInstanceOf(
          CATypeScalarType.Time.class,
          scalars.get(new CANameUnqualified("t4"))
        );

      assertEquals("t4", t.name().value());
      assertEquals("A time type.", t.description());
      assertEquals("2000-01-01T00:00Z", t.rangeLower().toString());
      assertEquals("9999-01-01T00:00Z", t.rangeUpper().toString());
    }

    {
      final var t =
        assertInstanceOf(
          CATypeRecordDeclaration.class,
          records.get(new CANameUnqualified("t5"))
        );

      assertEquals("t5", t.name().value());
      assertEquals("A record type.", t.description());

      final var fs = t.fields();

      {
        final var f = fs.get(new CANameUnqualified("q"));
        assertEquals("q", f.name().value());
        assertEquals("A Q field.", f.description());
        assertEquals("t0", f.type().toString());
        assertTrue(f.isRequired());
      }

      {
        final var f = fs.get(new CANameUnqualified("w"));
        assertEquals("w", f.name().value());
        assertEquals("A W field.", f.description());
        assertEquals("com.io7m.exa.t", f.type().toString());
        assertTrue(f.isRequired());
      }

      {
        final var f = fs.get(new CANameUnqualified("s"));
        assertEquals("s", f.name().value());
        assertEquals("An S field.", f.description());
        assertEquals("t0", f.type().toString());
        assertFalse(f.isRequired());
      }
    }
  }

  @Test
  public void testFileErrors0(
    final @TempDir Path directory)
    throws Exception
  {
    final var file =
      CATestDirectories.resourceOf(
        CATypePackageParserTest.class,
        directory,
        "tpack-error0.xml"
      );

    final var ex =
      assertThrows(ParsingException.class, () -> {
        this.parsers.parseFile(file);
      });

    ex.statusValues().forEach(e -> {
      LOG.debug("{}", e);
    });

    assertEquals(17, ex.statusValues().size());
  }
}
