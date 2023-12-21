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


package com.io7m.cardant.tests.shell;

import com.io7m.cardant.model.CAItemLocationMatchType;
import com.io7m.cardant.model.CAMetadataElementMatchType;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CANameMatchType;
import com.io7m.cardant.model.CATypeMatchType;
import com.io7m.cardant.shell.CAShellValueConverters;
import com.io7m.cardant.strings.CAStrings;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CAConverterTests
{
  @Property
  public void testTypeMatch(
    final @ForAll CATypeMatchType match)
    throws Exception
  {
    final var d =
      CAShellValueConverters.create(CAStrings.create(Locale.ROOT));
    final var c =
      d.converterFor(CATypeMatchType.class)
        .orElseThrow();

    assertEquals(
      match,
      c.convertFromString(c.convertToString(match))
    );
    final var ex = c.exampleValue();
    assertEquals(
      ex,
      c.convertFromString(c.convertToString(ex))
    );
  }

  @Property
  public void testNameMatch(
    final @ForAll CANameMatchType match)
    throws Exception
  {
    final var d =
      CAShellValueConverters.create(CAStrings.create(Locale.ROOT));
    final var c =
      d.converterFor(CANameMatchType.class)
        .orElseThrow();

    assertEquals(
      match,
      c.convertFromString(c.convertToString(match))
    );
    final var ex = c.exampleValue();
    assertEquals(
      ex,
      c.convertFromString(c.convertToString(ex))
    );
  }

  @Property
  public void testMetadataMatch(
    final @ForAll CAMetadataElementMatchType match)
    throws Exception
  {
    final var d =
      CAShellValueConverters.create(CAStrings.create(Locale.ROOT));
    final var c =
      d.converterFor(CAMetadataElementMatchType.class)
        .orElseThrow();

    assertEquals(
      match,
      c.convertFromString(c.convertToString(match))
    );
    final var ex = c.exampleValue();
    assertEquals(
      ex,
      c.convertFromString(c.convertToString(ex))
    );
  }

  @Property
  public void testMetadata(
    final @ForAll CAMetadataType match)
    throws Exception
  {
    final var d =
      CAShellValueConverters.create(CAStrings.create(Locale.ROOT));
    final var c =
      d.converterFor(CAMetadataType.class)
        .orElseThrow();

    assertEquals(
      match,
      c.convertFromString(c.convertToString(match))
    );
    final var ex = c.exampleValue();
    assertEquals(
      ex,
      c.convertFromString(c.convertToString(ex))
    );
  }

  @Property
  public void testItemLocation(
    final @ForAll CAItemLocationMatchType match)
    throws Exception
  {
    final var d =
      CAShellValueConverters.create(CAStrings.create(Locale.ROOT));
    final var c =
      d.converterFor(CAItemLocationMatchType.class)
        .orElseThrow();

    assertEquals(
      match,
      c.convertFromString(c.convertToString(match))
    );
    final var ex = c.exampleValue();
    assertEquals(
      ex,
      c.convertFromString(c.convertToString(ex))
    );
  }
}
