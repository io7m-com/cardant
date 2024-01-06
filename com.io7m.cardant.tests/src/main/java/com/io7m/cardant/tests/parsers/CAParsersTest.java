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


package com.io7m.cardant.tests.parsers;

import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CAMoney;
import com.io7m.cardant.parsers.CAConstraintExpressions;
import com.io7m.cardant.parsers.CADescriptionMatchExpressions;
import com.io7m.cardant.parsers.CAItemLocationMatchExpressions;
import com.io7m.cardant.parsers.CAItemSerialMatchExpressions;
import com.io7m.cardant.parsers.CAMediaTypeMatchExpressions;
import com.io7m.cardant.parsers.CAMetadataExpressions;
import com.io7m.cardant.parsers.CAMetadataMatchExpressions;
import com.io7m.cardant.parsers.CANameMatchExpressions;
import com.io7m.cardant.parsers.CATypeMatchExpressions;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.lanark.core.RDottedName;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.time.OffsetDateTime;
import java.util.Locale;
import java.util.stream.Stream;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorParse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public final class CAParsersTest
{
  private CAStrings strings;

  @BeforeEach
  public void setup()
  {
    this.strings = CAStrings.create(Locale.ROOT);
  }

  @Test
  public void testRangeMonetary()
    throws CAException
  {
    final var r =
      new CAConstraintExpressions(this.strings)
        .monetaryRange("[24.0 500.0]");

    assertEquals(CAMoney.money("24.0"), r.lower());
    assertEquals(CAMoney.money("500.0"), r.upper());
  }

  @Test
  public void testRangeMonetaryError0()
  {
    final var ex =
      assertThrows(CAException.class, () -> {
      new CAConstraintExpressions(this.strings)
        .monetaryRange("[24.0]");
    });
    assertEquals(errorParse(), ex.errorCode());
  }

  @Test
  public void testRangeMonetaryError1()
  {
    final var ex =
      assertThrows(CAException.class, () -> {
        new CAConstraintExpressions(this.strings)
          .monetaryRange("[x y]");
      });
    assertEquals(errorParse(), ex.errorCode());
  }

  @Test
  public void testRangeIntegral()
    throws CAException
  {
    final var r =
      new CAConstraintExpressions(this.strings)
        .integerRange("[24 500]");

    assertEquals(24L, r.lower());
    assertEquals(500L, r.upper());
  }

  @Test
  public void testRangeIntegralError0()
  {
    final var ex =
      assertThrows(CAException.class, () -> {
        new CAConstraintExpressions(this.strings)
          .integerRange("[24 ]");
      });
    assertEquals(errorParse(), ex.errorCode());
  }

  @Test
  public void testRangeIntegralError1()
  {
    final var ex =
      assertThrows(CAException.class, () -> {
        new CAConstraintExpressions(this.strings)
          .integerRange("[x y]");
      });
    assertEquals(errorParse(), ex.errorCode());
  }

  @Test
  public void testRangeReal()
    throws CAException
  {
    final var r =
      new CAConstraintExpressions(this.strings)
        .realRange("[24.3 500.5]");

    assertEquals(24.3, r.lower());
    assertEquals(500.5, r.upper());
  }

  @Test
  public void testRangeRealError0()
  {
    final var ex =
      assertThrows(CAException.class, () -> {
        new CAConstraintExpressions(this.strings)
          .realRange("[24.0 ]");
      });
    assertEquals(errorParse(), ex.errorCode());
  }

  @Test
  public void testRangeRealError1()
  {
    final var ex =
      assertThrows(CAException.class, () -> {
        new CAConstraintExpressions(this.strings)
          .realRange("[x y]");
      });
    assertEquals(errorParse(), ex.errorCode());
  }

  @Test
  public void testRangeTime()
    throws CAException
  {
    final var r =
      new CAConstraintExpressions(this.strings)
        .timeRange("[2023-07-23T12:24:35+00:00 2023-07-23T12:24:54+00:00]");

    assertEquals("2023-07-23T12:24:35Z", r.lower().toString());
    assertEquals("2023-07-23T12:24:54Z", r.upper().toString());
  }

  @Test
  public void testRangeTimeError0()
  {
    final var ex =
      assertThrows(CAException.class, () -> {
        new CAConstraintExpressions(this.strings)
          .timeRange("[2023-07-23T12:24:35+00:00 ]");
      });
    assertEquals(errorParse(), ex.errorCode());
  }

  @Test
  public void testRangeTimeError1()
  {
    final var ex =
      assertThrows(CAException.class, () -> {
        new CAConstraintExpressions(this.strings)
          .timeRange("[x y]");
      });
    assertEquals(errorParse(), ex.errorCode());
  }

  @Test
  public void testMetaInteger0()
    throws CAException
  {
    final var r =
      new CAMetadataExpressions(this.strings)
        .metadataParse("[integer x 23]");

    assertEquals(
      new CAMetadataType.Integral(new RDottedName("x"), 23L),
      r
    );
  }

  @TestFactory
  public Stream<DynamicTest> testMetaErrors()
  {
    return Stream.of(
      "[real]",
      "[real x y]",
      "[real x y z]",
      "[real x []]",
      "[real [] 23]",
      "[integer]",
      "[integer x y]",
      "[integer x y z]",
      "[integer x []]",
      "[integer [] 23]",
      "[text]",
      "[text x]",
      "[text x y z]",
      "[text x []]",
      "[text [] y]",
      "[money]",
      "[money x]",
      "[money x y z]",
      "[money x y []]",
      "[money x 23 []]",
      "[money x y z w]",
      "[money x [] z]",
      "[money x 23 z]",
      "[money x 23 24]",
      "[time]",
      "[time x]",
      "[time x y z]",
      "[time x []]",
      "[time x y]",
      "[time [] y]",
      "[what x y]"
    ).map(text -> {
      return dynamicTest("testMetaErrors_%s".formatted(text), () -> {
        final var ex =
          assertThrows(CAException.class, () -> {
            new CAMetadataExpressions(this.strings)
              .metadataParse(text);
          });
        assertEquals(errorParse(), ex.errorCode());
      });
    });
  }

  @TestFactory
  public Stream<DynamicTest> testMetaParseIdentity()
  {
    return Stream.of(
      "[real x 23.0]",
      "[time x 2023-07-23T14:26:46+00:00]",
      "[money x 23.0 EUR]",
      "[integer x 23]",
      "[text x 2023-07-23T14:26:46+00:00]"
    ).map(text -> {
      return dynamicTest("testMetaParseIdentity_%s".formatted(text), () -> {
        final var p0 =
          new CAMetadataExpressions(this.strings)
            .metadataParse(text);
        final var s =
          new CAMetadataExpressions(this.strings)
            .metadataSerializeToString(p0);
        final var p1 =
          new CAMetadataExpressions(this.strings)
            .metadataParse(s);

        assertEquals(p0, p1);
      });
    });
  }

  @Test
  public void testMetaIntegral0()
    throws CAException
  {
    final var r =
      new CAMetadataExpressions(this.strings)
        .metadataParse("[integer x 23]");

    assertEquals(
      new CAMetadataType.Integral(new RDottedName("x"), 23),
      r
    );
  }

  @Test
  public void testMetaReal0()
    throws CAException
  {
    final var r =
      new CAMetadataExpressions(this.strings)
        .metadataParse("[real x 23.0]");

    assertEquals(
      new CAMetadataType.Real(new RDottedName("x"), 23.0),
      r
    );
  }

  @Test
  public void testMetaTime0()
    throws CAException
  {
    final var r =
      new CAMetadataExpressions(this.strings)
        .metadataParse("[time x 2023-07-23T14:26:46+00:00]");

    assertEquals(
      new CAMetadataType.Time(
        new RDottedName("x"),
        OffsetDateTime.parse("2023-07-23T14:26:46+00:00")
      ),
      r
    );
  }

  @Test
  public void testMetaText0()
    throws CAException
  {
    final var r =
      new CAMetadataExpressions(this.strings)
        .metadataParse("[text x 2023-07-23T14:26:46+00:00]");

    assertEquals(
      new CAMetadataType.Text(
        new RDottedName("x"),
        "2023-07-23T14:26:46+00:00"
      ),
      r
    );
  }

  @Test
  public void testMetaMoney0()
    throws CAException
  {
    final var r =
      new CAMetadataExpressions(this.strings)
        .metadataParse("[money x 23.0 EUR]");

    assertEquals(
      new CAMetadataType.Monetary(
        new RDottedName("x"),
        CAMoney.money("23.0"),
        CurrencyUnit.EUR
      ),
      r
    );
  }

  @TestFactory
  public Stream<DynamicTest> testMetaMatchParseIdentity()
  {
    return Stream.of(
      "anything",
      "[and anything anything]",
      "[or anything anything]",
      "[match with-any-name any-value]",
      "[match [with-name-equal-to x] any-value]",
      "[match [with-name-similar-to x] any-value]",
      "[match with-any-name [within-range-integral 0 100]]",
      "[match with-any-name [within-range-real 0 100]]",
      "[match with-any-name [within-range-time 2023-07-23T14:26:46+00:00 2023-07-23T14:26:46+00:00]]",
      "[match with-any-name [within-range-monetary 0 100]]",
      "[match with-any-name [with-currency EUR]]",
      "[match with-any-name [with-text-exact x]]",
      "[match with-any-name [with-text-search y]]"
    ).map(text -> {
      return dynamicTest("testMetaMatchParseIdentity_%s".formatted(text), () -> {
        final var p0 =
          new CAMetadataMatchExpressions(this.strings)
            .metadataMatch(text);
        final var s =
          new CAMetadataMatchExpressions(this.strings)
            .metadataMatchSerializeToString(p0);
        final var p1 =
          new CAMetadataMatchExpressions(this.strings)
            .metadataMatch(s);

        assertEquals(p0, p1);
      });
    });
  }

  @TestFactory
  public Stream<DynamicTest> testMetaMatchErrors()
  {
    return Stream.of(
      "23",
      "[]",
      "[match [x y z] any-value]",
      "[match any-name [with-text-exact []]]",
      "[match any-name [with-text-exact x y z]]",
      "[match any-name [with-text-exact]]",
      "[match any-name [with-text-search []]]",
      "[match any-name [with-text-search x y z]]",
      "[match any-name [with-text-search]]",
      "[match any-name [with-currency WONKADOLLARS]]",
      "[match any-name [within-range-integral 1 2 x]]",
      "[match any-name [within-range-integral 1 x]]",
      "[match any-name [within-range-integral []]]",
      "[match any-name [within-range-monetary 1 2 x]]",
      "[match any-name [within-range-monetary 1 x]]",
      "[match any-name [within-range-monetary []]]",
      "[match any-name [within-range-real 1 2 x]]",
      "[match any-name [within-range-real 1 x]]",
      "[match any-name [within-range-real []]]",
      "[match any-name [within-range-time 2023-07-23T14:26:46+00:00 2023-07-23T14:26:46+00:00 x]]",
      "[match any-name [within-range-time 2023-07-23T14:26:46+00:00 x]]",
      "[match any-name [within-range-time []]]",
      "[match any-name [x y z]]",
      "[match x y z]",
      "[match y z]",
      "[unrecognized x y]"
    ).map(text -> {
      return dynamicTest("testMetaMatchErrors_%s".formatted(text), () -> {
        final var ex =
          assertThrows(CAException.class, () -> {
            new CAMetadataMatchExpressions(this.strings)
              .metadataMatch(text);
          });
        assertEquals(errorParse(), ex.errorCode());
      });
    });
  }

  @TestFactory
  public Stream<DynamicTest> testTypeMatchErrors()
  {
    return Stream.of(
      "23",
      "[]",
      "[all-of 3]",
      "[all-of []]",
      "[any-of 3]",
      "[any-of []]",
      "[unrecognized x y]"
    ).map(text -> {
      return dynamicTest("testTypeMatchErrors_%s".formatted(text), () -> {
        final var ex =
          assertThrows(CAException.class, () -> {
            new CATypeMatchExpressions(this.strings)
              .typeMatch(text);
          });
        assertEquals(errorParse(), ex.errorCode());
      });
    });
  }

  @TestFactory
  public Stream<DynamicTest> testTypeMatchParseIdentity()
  {
    return Stream.of(
      "with-any-type",
      "[with-types-equal-to x y z]",
      "[with-types-not-equal-to x y z]",
      "[with-types-overlapping x y z]",
      "[with-types-subset-of x y z]",
      "[with-types-superset-of x y z]"
    ).map(text -> {
      return dynamicTest("testTypeMatchParseIdentity_%s".formatted(text), () -> {
        final var p0 =
          new CATypeMatchExpressions(this.strings)
            .typeMatch(text);
        final var s =
          new CATypeMatchExpressions(this.strings)
            .typeMatchSerializeToString(p0);
        final var p1 =
          new CATypeMatchExpressions(this.strings)
            .typeMatch(s);

        assertEquals(p0, p1);
      });
    });
  }

  @TestFactory
  public Stream<DynamicTest> testNameMatchErrors()
  {
    return Stream.of(
      "23",
      "[]",
      "[with-name-exact]",
      "[with-name-search]"
    ).map(text -> {
      return dynamicTest("testNameMatchErrors_%s".formatted(text), () -> {
        final var ex =
          assertThrows(CAException.class, () -> {
            new CANameMatchExpressions(this.strings)
              .nameMatch(text);
          });
        assertEquals(errorParse(), ex.errorCode());
      });
    });
  }

  @TestFactory
  public Stream<DynamicTest> testNameMatchParseIdentity()
  {
    return Stream.of(
      "with-any-name",
      "[with-name-equal-to x]",
      "[with-name-similar-to y]",
      "[with-name-not-equal-to x]",
      "[with-name-not-similar-to y]"
    ).map(text -> {
      return dynamicTest("testNameMatchParseIdentity_%s".formatted(text), () -> {
        final var p0 =
          new CANameMatchExpressions(this.strings)
            .nameMatch(text);
        final var s =
          new CANameMatchExpressions(this.strings)
            .nameMatchSerializeToString(p0);
        final var p1 =
          new CANameMatchExpressions(this.strings)
            .nameMatch(s);

        assertEquals(p0, p1);
      });
    });
  }

  @TestFactory
  public Stream<DynamicTest> testLocationMatchErrors()
  {
    return Stream.of(
      "23",
      "[]",
      "[with-location-exact x]",
      "[with-location-or-descendants t]",
      "[with-location-exact []]",
      "[with-location-or-descendants []]",
      "[with-location-exact x y]",
      "[with-location-or-descendants t u]",
      "[with-what d1ee2c6e-d032-4a5e-8b1b-97a876a12412]"
    ).map(text -> {
      return dynamicTest("testLocationMatchErrors_%s".formatted(text), () -> {
        final var ex =
          assertThrows(CAException.class, () -> {
            new CAItemLocationMatchExpressions(this.strings)
              .locationMatch(text);
          });
        assertEquals(errorParse(), ex.errorCode());
      });
    });
  }

  @TestFactory
  public Stream<DynamicTest> testLocationMatchParseIdentity()
  {
    return Stream.of(
      "any-location",
      "[with-location-exact d1ee2c6e-d032-4a5e-8b1b-97a876a12412]",
      "[with-location-or-descendants 195d8568-370b-4911-83bf-ebaa51d88d43]"
    ).map(text -> {
      return dynamicTest("testLocationMatchParseIdentity_%s".formatted(text), () -> {
        final var p0 =
          new CAItemLocationMatchExpressions(this.strings)
            .locationMatch(text);
        final var s =
          new CAItemLocationMatchExpressions(this.strings)
            .locationMatchSerializeToString(p0);
        final var p1 =
          new CAItemLocationMatchExpressions(this.strings)
            .locationMatch(s);

        assertEquals(p0, p1);
      });
    });
  }

  @TestFactory
  public Stream<DynamicTest> testSerialMatchErrors()
  {
    return Stream.of(
      "23",
      "[]",
      "[with-something x]",
      "[with-serial-equal-to x y]",
      "[with-serial-equal-to [2]]",
      "[with-serial-equal-to]",
      "[with-serial-not-equal-to]"
    ).map(text -> {
      return dynamicTest("testSerialMatchErrors_%s".formatted(text), () -> {
        final var ex =
          assertThrows(CAException.class, () -> {
            new CAItemSerialMatchExpressions(this.strings)
              .serialMatch(text);
          });
        assertEquals(errorParse(), ex.errorCode());
      });
    });
  }

  @TestFactory
  public Stream<DynamicTest> testSerialMatchParseIdentity()
  {
    return Stream.of(
      "with-any-serial",
      "[with-serial-equal-to x]",
      "[with-serial-not-equal-to x]"
    ).map(text -> {
      return dynamicTest("testSerialMatchParseIdentity_%s".formatted(text), () -> {
        final var p0 =
          new CAItemSerialMatchExpressions(this.strings)
            .serialMatch(text);
        final var s =
          new CAItemSerialMatchExpressions(this.strings)
            .serialMatchSerializeToString(p0);
        final var p1 =
          new CAItemSerialMatchExpressions(this.strings)
            .serialMatch(s);

        assertEquals(p0, p1);
      });
    });
  }

  @TestFactory
  public Stream<DynamicTest> testMediatypeMatchErrors()
  {
    return Stream.of(
      "23",
      "[]",
      "[with-something x]",
      "[with-mediatype-equal-to x y]",
      "[with-mediatype-equal-to [2]]",
      "[with-mediatype-equal-to]",
      "[with-mediatype-not-equal-to]",
      "[with-mediatype-similar-to x y]",
      "[with-mediatype-similar-to [2]]",
      "[with-mediatype-similar-to]",
      "[with-mediatype-not-similar-to]"
    ).map(text -> {
      return dynamicTest("testMediatypeMatchErrors_%s".formatted(text), () -> {
        final var ex =
          assertThrows(CAException.class, () -> {
            new CAMediaTypeMatchExpressions(this.strings)
              .mediatypeMatch(text);
          });
        assertEquals(errorParse(), ex.errorCode());
      });
    });
  }

  @TestFactory
  public Stream<DynamicTest> testMediatypeMatchParseIdentity()
  {
    return Stream.of(
      "with-any-mediatype",
      "[with-mediatype-equal-to x]",
      "[with-mediatype-not-equal-to x]",
      "[with-mediatype-similar-to x]",
      "[with-mediatype-not-similar-to x]"
    ).map(text -> {
      return dynamicTest("testMediatypeMatchParseIdentity_%s".formatted(text), () -> {
        final var p0 =
          new CAMediaTypeMatchExpressions(this.strings)
            .mediatypeMatch(text);
        final var s =
          new CAMediaTypeMatchExpressions(this.strings)
            .mediatypeMatchSerializeToString(p0);
        final var p1 =
          new CAMediaTypeMatchExpressions(this.strings)
            .mediatypeMatch(s);

        assertEquals(p0, p1);
      });
    });
  }

  @TestFactory
  public Stream<DynamicTest> testDescriptionMatchErrors()
  {
    return Stream.of(
      "23",
      "[]",
      "[with-something x]",
      "[with-description-equal-to x y]",
      "[with-description-equal-to [2]]",
      "[with-description-equal-to]",
      "[with-description-not-equal-to]",
      "[with-description-similar-to x y]",
      "[with-description-similar-to [2]]",
      "[with-description-similar-to]",
      "[with-description-not-similar-to]"
    ).map(text -> {
      return dynamicTest("testDescriptionMatchErrors_%s".formatted(text), () -> {
        final var ex =
          assertThrows(CAException.class, () -> {
            new CADescriptionMatchExpressions(this.strings)
              .descriptionMatch(text);
          });
        assertEquals(errorParse(), ex.errorCode());
      });
    });
  }

  @TestFactory
  public Stream<DynamicTest> testDescriptionMatchParseIdentity()
  {
    return Stream.of(
      "with-any-description",
      "[with-description-equal-to x]",
      "[with-description-not-equal-to x]",
      "[with-description-similar-to x]",
      "[with-description-not-similar-to x]"
    ).map(text -> {
      return dynamicTest("testDescriptionMatchParseIdentity_%s".formatted(text), () -> {
        final var p0 =
          new CADescriptionMatchExpressions(this.strings)
            .descriptionMatch(text);
        final var s =
          new CADescriptionMatchExpressions(this.strings)
            .descriptionMatchSerializeToString(p0);
        final var p1 =
          new CADescriptionMatchExpressions(this.strings)
            .descriptionMatch(s);

        assertEquals(p0, p1);
      });
    });
  }
}
