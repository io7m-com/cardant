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

import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;
import com.io7m.cardant.model.CAValidityException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

public final class CATagTest
{
  @TestFactory
  public Stream<DynamicTest> testTagsValid()
  {
    return Stream.of(
      "a"
    ).map(this::validTag);
  }

  @TestFactory
  public Stream<DynamicTest> testTagsInvalid()
  {
    return Stream.of(
      ""
    ).map(this::invalidTag);
  }

  public DynamicTest validTag(
    final String text)
  {
    return DynamicTest.dynamicTest("testValid_%s".formatted(text), () -> {
      new CATag(CATagID.random(), text);
    });
  }

  public DynamicTest invalidTag(
    final String text)
  {
    return DynamicTest.dynamicTest("testInvalid_%s".formatted(text), () -> {
      try {
        new CATag(CATagID.random(), text);
        Assertions.fail();
      } catch (final CAValidityException e) {
        // OK
      }
    });
  }
}
