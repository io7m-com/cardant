/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.tests;

import com.io7m.anethum.common.ParseException;
import com.io7m.cardant.security.api.CSAttributeName;
import com.io7m.cardant.security.api.CSAttributeValue;
import com.io7m.cardant.security.api.CSLabel;
import com.io7m.cardant.security.api.CSLabelParsers;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class CSLabelTest
{
  private static DynamicTest failParseOf(final String text)
  {
    return DynamicTest.dynamicTest("testFail_" + text, () -> {
      assertThrows(ParseException.class, () -> {
        new CSLabelParsers().parseFromString(text);
      });
    });
  }

  @Provide
  public Arbitrary<CSAttributeName> names()
  {
    return Arbitraries.strings()
      .ofMinLength(1)
      .ofMaxLength(256)
      .withChars("abcd1234_-.")
      .map(CSAttributeName::new);
  }

  @Provide
  public Arbitrary<CSAttributeValue> values()
  {
    return Arbitraries.strings()
      .ofMinLength(1)
      .ofMaxLength(256)
      .withChars("abcd1234_-.")
      .map(CSAttributeValue::new);
  }

  @Provide
  public Arbitrary<Map<CSAttributeName, CSAttributeValue>> maps()
  {
    return Arbitraries.maps(this.names(), this.values());
  }

  @Property
  public void testMapContains(
    @ForAll("maps") final Map<CSAttributeName, CSAttributeValue> attributes)
  {
    final var label = new CSLabel(new TreeMap<>(attributes));

    assertAll(
      attributes.entrySet()
        .stream()
        .map(e -> {
          return (Executable) () -> assertEquals(
            e.getValue(),
            label.attributes().get(e.getKey())
          );
        }).collect(Collectors.toList())
    );
  }

  @Test
  public void testParse0()
    throws ParseException
  {
    final var serialized = "a=b;";

    final var expected = new CSLabel(
      new TreeMap<>(Map.ofEntries(
        Map.entry(
          new CSAttributeName("a"),
          new CSAttributeValue("b"))
      ))
    );

    assertEquals(expected, new CSLabelParsers().parseFromString(serialized));
    assertEquals(
      expected,
      new CSLabelParsers().parseFromString(expected.serialized()));
  }

  @Test
  public void testParse1()
    throws ParseException
  {
    final var serialized = "a=b;c=d;";

    final var expected = new CSLabel(
      new TreeMap<>(Map.ofEntries(
        Map.entry(
          new CSAttributeName("a"),
          new CSAttributeValue("b")),
        Map.entry(
          new CSAttributeName("c"),
          new CSAttributeValue("d"))
      ))
    );

    assertEquals(expected, new CSLabelParsers().parseFromString(serialized));
    assertEquals(
      expected,
      new CSLabelParsers().parseFromString(expected.serialized()));
  }

  @Test
  public void testParse2()
    throws ParseException
  {
    final var serialized = "a_1-.=b_0-.;";

    final var expected = new CSLabel(
      new TreeMap<>(Map.ofEntries(
        Map.entry(
          new CSAttributeName("a_1-."),
          new CSAttributeValue("b_0-."))
      ))
    );

    assertEquals(expected, new CSLabelParsers().parseFromString(serialized));
    assertEquals(
      expected,
      new CSLabelParsers().parseFromString(expected.serialized()));
  }

  @Test
  public void testParseEmpty()
    throws ParseException
  {
    final var serialized = "";

    assertEquals(
      new CSLabel(new TreeMap<>()),
      new CSLabelParsers().parseFromString(serialized));
  }

  @TestFactory
  public Stream<DynamicTest> testParseBad()
  {
    return Stream.of(
      "=",
      "a=",
      "a=b",
      "a=",
      "a=;",
      "a=;",
      "aZ=b;",
      "a=bZ;"
    ).map(CSLabelTest::failParseOf);
  }
}
