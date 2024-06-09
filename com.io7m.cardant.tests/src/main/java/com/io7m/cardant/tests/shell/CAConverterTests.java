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

import com.io7m.cardant.model.CADescriptionMatch;
import com.io7m.cardant.model.CAItemIDMatch;
import com.io7m.cardant.model.CAItemSerialMatch;
import com.io7m.cardant.model.CALocationMatchType;
import com.io7m.cardant.model.CAMediaTypeMatch;
import com.io7m.cardant.model.CAMetadataElementMatchType;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CANameMatchFuzzy;
import com.io7m.cardant.model.CATypeMatch;
import com.io7m.cardant.shell.CAShellValueConverters;
import com.io7m.cardant.shell.internal.converters.CADescriptionMatchConverter;
import com.io7m.cardant.shell.internal.converters.CAFileIdConverter;
import com.io7m.cardant.shell.internal.converters.CAItemIDMatchConverter;
import com.io7m.cardant.shell.internal.converters.CAItemIdConverter;
import com.io7m.cardant.shell.internal.converters.CAItemSerialMatchConverter;
import com.io7m.cardant.shell.internal.converters.CALocationIdConverter;
import com.io7m.cardant.shell.internal.converters.CALocationMatchConverter;
import com.io7m.cardant.shell.internal.converters.CAMediaTypeMatchConverter;
import com.io7m.cardant.shell.internal.converters.CAMetadataConverter;
import com.io7m.cardant.shell.internal.converters.CAMetadataMatchConverter;
import com.io7m.cardant.shell.internal.converters.CAMonetaryRangeConverter;
import com.io7m.cardant.shell.internal.converters.CANameMatchConverter;
import com.io7m.cardant.shell.internal.converters.CAPatternConverter;
import com.io7m.cardant.shell.internal.converters.CARDottedNameConverter;
import com.io7m.cardant.shell.internal.converters.CARangeInclusiveDConverter;
import com.io7m.cardant.shell.internal.converters.CARangeInclusiveLConverter;
import com.io7m.cardant.shell.internal.converters.CARoleNameConverter;
import com.io7m.cardant.shell.internal.converters.CATimeRangeConverter;
import com.io7m.cardant.shell.internal.converters.CATypeMatchConverter;
import com.io7m.cardant.shell.internal.converters.CATypePackageUninstallBehaviorConverter;
import com.io7m.cardant.shell.internal.converters.CATypeRecordFieldIdentifierConverter;
import com.io7m.cardant.shell.internal.converters.CATypeRecordIdentifierConverter;
import com.io7m.cardant.shell.internal.converters.CAUserIdConverter;
import com.io7m.cardant.shell.internal.converters.CAVersionConverter;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.quarrel.core.QException;
import com.io7m.quarrel.core.QValueConverterType;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class CAConverterTests
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CAConverterTests.class);

  private static final CAStrings STRINGS =
    CAStrings.create(Locale.ROOT);

  private static <M> void extracted(
    final Class<M> clazz,
    final M match)
    throws QException
  {
    final var d =
      CAShellValueConverters.create(CAStrings.create(Locale.ROOT));
    final var c =
      d.converterFor(clazz)
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

    c.convertToString(c.exampleValue());
  }

  @Property
  public void testTypeMatch(
    final @ForAll CATypeMatch match)
    throws Exception
  {
    extracted(CATypeMatch.class, match);
  }

  @Property
  public void testNameMatch(
    final @ForAll CANameMatchFuzzy match)
    throws Exception
  {
    extracted(CANameMatchFuzzy.class, match);
  }

  @Property
  public void testDescriptionMatch(
    final @ForAll CADescriptionMatch match)
    throws Exception
  {
    extracted(CADescriptionMatch.class, match);
  }

  @Property
  public void testMediaTypeMatch(
    final @ForAll CAMediaTypeMatch match)
    throws Exception
  {
    extracted(CAMediaTypeMatch.class, match);
  }

  @Property
  public void testMetadataMatch(
    final @ForAll CAMetadataElementMatchType match)
    throws Exception
  {
    extracted(CAMetadataElementMatchType.class, match);
  }

  @Property
  public void testMetadata(
    final @ForAll CAMetadataType match)
    throws Exception
  {
    extracted(CAMetadataType.class, match);
  }

  @Property
  public void testItemLocation(
    final @ForAll CALocationMatchType match)
    throws Exception
  {
    extracted(CALocationMatchType.class, match);
  }

  @Property
  public void testItemID(
    final @ForAll CAItemIDMatch match)
    throws Exception
  {
    extracted(CAItemIDMatch.class, match);
  }

  @Property
  public void testItemSerial(
    final @ForAll CAItemSerialMatch match)
    throws Exception
  {
    extracted(CAItemSerialMatch.class, match);
  }

  @TestFactory
  public Stream<DynamicTest> testBruteForce()
  {
    return Stream.of(
      CADescriptionMatchConverter.class,
      CAFileIdConverter.class,
      CAItemIdConverter.class,
      CAItemIDMatchConverter.class,
      CAItemSerialMatchConverter.class,
      CALocationIdConverter.class,
      CALocationMatchConverter.class,
      CAMediaTypeMatchConverter.class,
      CAMetadataConverter.class,
      CAMetadataMatchConverter.class,
      CAMonetaryRangeConverter.class,
      CANameMatchConverter.class,
      CAPatternConverter.class,
      CARangeInclusiveDConverter.class,
      CARangeInclusiveLConverter.class,
      CARDottedNameConverter.class,
      CARoleNameConverter.class,
      CATimeRangeConverter.class,
      CATypeMatchConverter.class,
      CATypePackageUninstallBehaviorConverter.class,
      CATypeRecordFieldIdentifierConverter.class,
      CATypeRecordIdentifierConverter.class,
      CAUserIdConverter.class,
      CAVersionConverter.class
    ).map(this::bruteForceOf);
  }

  @SuppressWarnings("unchecked")
  private DynamicTest bruteForceOf(
    final Class<? extends QValueConverterType<?>> c)
  {
    return DynamicTest.dynamicTest(
      "testBruteForce_%s".formatted(c.getSimpleName()),
      () -> {
        QValueConverterType<Object> o = null;

        try {
          o = (QValueConverterType<Object>)
            c.getConstructor().newInstance();
        } catch (final NoSuchMethodException e) {
          o = null;
        }

        if (o == null) {
          try {
            o = (QValueConverterType<Object>)
              c.getConstructor(CAStrings.class).newInstance(STRINGS);
          } catch (final NoSuchMethodException e) {
            o = null;
          }
        }

        final var v =
          o.exampleValue();
        final var s =
          o.convertToString(v);
        final var x =
          o.convertFromString(s);

        switch (x) {
          case final Pattern p -> {
            assertEquals(v.toString(), p.toString());
          }
          default -> {
            assertEquals(v, x);
          }
        }

        assertNotNull(o.convertedClass());
        assertNotEquals("", o.syntax());

        final QValueConverterType<Object> finalO = o;

        final var rng =
          SecureRandom.getInstanceStrong();
        final var data =
          new byte[64];
        rng.nextBytes(data);

        final var corruptedText =
          new String(data, StandardCharsets.UTF_8);

        final var ex =
          assertThrows(
            Exception.class,
            () -> finalO.convertFromString(corruptedText)
          );

        LOG.debug("Exception: ", ex);
      });
  }
}
