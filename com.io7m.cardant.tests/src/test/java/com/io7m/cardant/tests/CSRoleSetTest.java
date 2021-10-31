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
import com.io7m.cardant.security.api.CSRoleName;
import com.io7m.cardant.security.api.CSRoleSet;
import com.io7m.cardant.security.api.CSRoleSetParsers;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class CSRoleSetTest
{
  private static DynamicTest failParseOf(final String text)
  {
    return DynamicTest.dynamicTest("testFail_" + text, () -> {
      assertThrows(ParseException.class, () -> {
        new CSRoleSetParsers().parseFromString(text);
      });
    });
  }

  @Test
  public void testParse0()
    throws ParseException
  {
    final var serialized = "a;b;";

    final var expected = new CSRoleSet(
      new TreeSet<>(Set.of(
        new CSRoleName("a"),
        new CSRoleName("b")
      )));

    assertEquals(expected, new CSRoleSetParsers().parseFromString(serialized));
    assertEquals(
      expected,
      new CSRoleSetParsers().parseFromString(expected.serialized()));
  }

  @Test
  public void testParse1()
    throws ParseException
  {
    final var serialized = "a;b;c;d;";

    final var expected = new CSRoleSet(
      new TreeSet<>(Set.of(
        new CSRoleName("a"),
        new CSRoleName("b"),
        new CSRoleName("c"),
        new CSRoleName("d")
      )));

    assertEquals(expected, new CSRoleSetParsers().parseFromString(serialized));
    assertEquals(
      expected,
      new CSRoleSetParsers().parseFromString(expected.serialized()));
  }

  @Test
  public void testParse2()
    throws ParseException
  {
    final var serialized = "a_1-.;b_0-.;";

    final var expected = new CSRoleSet(
      new TreeSet<>(Set.of(
        new CSRoleName("a_1-."),
        new CSRoleName("b_0-."))
      )
    );

    assertEquals(expected, new CSRoleSetParsers().parseFromString(serialized));
    assertEquals(
      expected,
      new CSRoleSetParsers().parseFromString(expected.serialized()));
  }

  @Test
  public void testParseEmpty()
    throws ParseException
  {
    final var serialized = "";

    assertEquals(
      new CSRoleSet(new TreeSet<>()),
      new CSRoleSetParsers().parseFromString(serialized));
  }

  @TestFactory
  public Stream<DynamicTest> testParseBad()
  {
    return Stream.of(
      "a",
      "a;b",
      "Z;",
      "aZ;b;",
      "a;bZ;"
    ).map(CSRoleSetTest::failParseOf);
  }
}
