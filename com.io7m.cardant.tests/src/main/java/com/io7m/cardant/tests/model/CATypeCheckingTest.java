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


package com.io7m.cardant.tests.model;

import com.io7m.cardant.model.CAMetadataType;
import com.io7m.cardant.model.CAMoney;
import com.io7m.cardant.model.CATypeChecking;
import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.model.CATypeRecord;
import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.lanark.core.RDottedName;
import com.io7m.verona.core.Version;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeCheckFieldInvalid;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeCheckFieldRequiredMissing;
import static org.joda.money.CurrencyUnit.EUR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class CATypeCheckingTest
{
  private static final RDottedName NAME_X =
    new RDottedName("com.io7m.x");
  private static final RDottedName NAME_EX =
    new RDottedName("com.io7m.ex");
  private static final RDottedName NAME_T =
    new RDottedName("com.io7m.t");
  private static final CATypePackageIdentifier P =
    new CATypePackageIdentifier(
      new RDottedName("com.io7m"),
      Version.of(1, 0, 0)
    );

  /**
   * Missing but required fields are caught.
   */

  @Test
  public void testMissingRequiredField()
  {
    final var type =
      new CATypeRecord(
        P,
        NAME_EX,
        "An example type.",
        Map.ofEntries(
          Map.entry(
            NAME_X,
            new CATypeField(
              NAME_X,
              "A x.",
              new CATypeScalarType.Text(
                P,
                NAME_T,
                "A t.",
                "^.*$"
              ),
              true
            )
          )
        )
      );

    final var checker =
      CATypeChecking.create(
        CAStrings.create(Locale.ROOT),
        Set.of(type),
        Set.of()
      );

    final var errors = checker.execute();
    errors.stream()
      .filter(e -> Objects.equals(
        e.errorCode(), errorTypeCheckFieldRequiredMissing()))
      .findFirst()
      .orElseThrow();
  }

  /**
   * Fields with unparseable patterns fail type checking.
   */

  @Test
  public void testFieldWithUnparseablePattern()
  {
    assertThrows(PatternSyntaxException.class, () -> {
      new CATypeRecord(
        P,
        NAME_EX,
        "An example type.",
        Map.ofEntries(
          Map.entry(
            NAME_X,
            new CATypeField(
              NAME_X,
              "A x.",
              new CATypeScalarType.Text(
                P,
                NAME_T,
                "A t.",
                "^\\x"
              ),
              true
            )
          )
        )
      );
    });
  }

  /**
   * Fields with values that do not match the type patterns are caught.
   */

  @Test
  public void testFieldInvalid()
  {
    final var type =
      new CATypeRecord(
        P,
        NAME_EX,
        "An example type.",
        Map.ofEntries(
          Map.entry(
            NAME_X,
            new CATypeField(
              NAME_X,
              "A x.",
              new CATypeScalarType.Text(
                P,
                NAME_T,
                "A t.",
                "[0-9]"
              ),
              true
            )
          )
        )
      );

    final var checker =
      CATypeChecking.create(
        CAStrings.create(Locale.ROOT),
        Set.of(type),
        Set.of(
          new CAMetadataType.Text(NAME_X, "z")
        )
      );

    executeAndAssumeFailure(checker);
  }

  /**
   * Fields with the right type succeed type checking.
   */

  @Test
  public void testTypeCheckSuccess0()
  {
    final var type =
      new CATypeRecord(
        P,
        NAME_EX,
        "An example type.",
        Map.ofEntries(
          Map.entry(
            NAME_X,
            new CATypeField(
              NAME_X,
              "A x.",
              new CATypeScalarType.Integral(
                P,
                NAME_T,
                "A t.",
                0L,
                200L
              ),
              true
            )
          )
        )
      );

    final var checker =
      CATypeChecking.create(
        CAStrings.create(Locale.ROOT),
        Set.of(type),
        Set.of(
          new CAMetadataType.Integral(NAME_X, 100L)
        )
      );

    executeAndAssumeSuccess(checker);
  }

  /**
   * Fields with the right type succeed type checking.
   */

  @Test
  public void testTypeCheckSuccess1()
  {
    final var type =
      new CATypeRecord(
        P,
        NAME_EX,
        "An example type.",
        Map.ofEntries(
          Map.entry(
            NAME_X,
            new CATypeField(
              NAME_X,
              "A x.",
              new CATypeScalarType.Real(
                P,
                NAME_T,
                "A t.",
                0.0,
                200.0
              ),
              true
            )
          )
        )
      );

    final var checker =
      CATypeChecking.create(
        CAStrings.create(Locale.ROOT),
        Set.of(type),
        Set.of(
          new CAMetadataType.Real(NAME_X, 100.0)
        )
      );

    executeAndAssumeSuccess(checker);
  }

  /**
   * Fields with the right type succeed type checking.
   */

  @Test
  public void testTypeCheckSuccess2()
  {
    final var type =
      new CATypeRecord(
        P,
        NAME_EX,
        "An example type.",
        Map.ofEntries(
          Map.entry(
            NAME_X,
            new CATypeField(
              NAME_X,
              "A x.",
              new CATypeScalarType.Monetary(
                P,
                NAME_T,
                "A t.",
                CAMoney.money("0.0"),
                CAMoney.money("200.0")
              ),
              true
            )
          )
        )
      );

    final var checker =
      CATypeChecking.create(
        CAStrings.create(Locale.ROOT),
        Set.of(type),
        Set.of(
          new CAMetadataType.Monetary(NAME_X, CAMoney.money("100.0"), EUR)
        )
      );

    executeAndAssumeSuccess(checker);
  }

  /**
   * Fields with the right type succeed type checking.
   */

  @Test
  public void testTypeCheckSuccess3()
  {
    final var type =
      new CATypeRecord(
        P,
        NAME_EX,
        "An example type.",
        Map.ofEntries(
          Map.entry(
            NAME_X,
            new CATypeField(
              NAME_X,
              "A x.",
              new CATypeScalarType.Time(
                P,
                NAME_T,
                "A t.",
                OffsetDateTime.parse("2001-01-01T00:00:00+00:00"),
                OffsetDateTime.parse("2010-01-01T00:00:00+00:00")
              ),
              true
            )
          )
        )
      );

    final var checker =
      CATypeChecking.create(
        CAStrings.create(Locale.ROOT),
        Set.of(type),
        Set.of(
          new CAMetadataType.Time(
            NAME_X,
            OffsetDateTime.parse(
              "2005-01-01T00:00:00+00:00"))
        )
      );

    executeAndAssumeSuccess(checker);
  }

  /**
   * Fields with the right type succeed type checking.
   */

  @Test
  public void testTypeCheckSuccess4()
  {
    final var type =
      new CATypeRecord(
        P,
        NAME_EX,
        "An example type.",
        Map.ofEntries(
          Map.entry(
            NAME_X,
            new CATypeField(
              NAME_X,
              "A x.",
              new CATypeScalarType.Text(
                P,
                NAME_T,
                "A t.",
                "[a-z]+"
              ),
              true
            )
          )
        )
      );

    final var checker =
      CATypeChecking.create(
        CAStrings.create(Locale.ROOT),
        Set.of(type),
        Set.of(
          new CAMetadataType.Text(NAME_X, "abcdefghijklmnopqrstuvwxyz")
        )
      );

    executeAndAssumeSuccess(checker);
  }

  /**
   * Fields with the values that fail constraint checking fail type checking.
   */

  @Test
  public void testTypeCheckFails0()
  {
    final var type =
      new CATypeRecord(
        P,
        NAME_EX,
        "An example type.",
        Map.ofEntries(
          Map.entry(
            NAME_X,
            new CATypeField(
              NAME_X,
              "A x.",
              new CATypeScalarType.Integral(
                P,
                NAME_T,
                "A t.",
                0L,
                200L
              ),
              true
            )
          )
        )
      );

    final var checker =
      CATypeChecking.create(
        CAStrings.create(Locale.ROOT),
        Set.of(type),
        Set.of(
          new CAMetadataType.Integral(NAME_X, 300L)
        )
      );

    executeAndAssumeFailure(checker);
  }

  /**
   * Fields with the values that fail constraint checking fail type checking.
   */

  @Test
  public void testTypeCheckFails1()
  {
    final var type =
      new CATypeRecord(
        P,
        NAME_EX,
        "An example type.",
        Map.ofEntries(
          Map.entry(
            NAME_X,
            new CATypeField(
              NAME_X,
              "A x.",
              new CATypeScalarType.Real(
                P,
                NAME_T,
                "A t.",
                0.0,
                200.0
              ),
              true
            )
          )
        )
      );

    final var checker =
      CATypeChecking.create(
        CAStrings.create(Locale.ROOT),
        Set.of(type),
        Set.of(
          new CAMetadataType.Real(NAME_X, 300.0)
        )
      );

    executeAndAssumeFailure(checker);
  }

  /**
   * Fields with the values that fail constraint checking fail type checking.
   */

  @Test
  public void testTypeCheckFails2()
  {
    final var type =
      new CATypeRecord(
        P,
        NAME_EX,
        "An example type.",
        Map.ofEntries(
          Map.entry(
            NAME_X,
            new CATypeField(
              NAME_X,
              "A x.",
              new CATypeScalarType.Monetary(
                P,
                NAME_T,
                "A t.",
                CAMoney.money("0.0"),
                CAMoney.money("200.0")
              ),
              true
            )
          )
        )
      );

    final var checker =
      CATypeChecking.create(
        CAStrings.create(Locale.ROOT),
        Set.of(type),
        Set.of(
          new CAMetadataType.Monetary(NAME_X, CAMoney.money("300.0"), EUR)
        )
      );

    executeAndAssumeFailure(checker);
  }

  /**
   * Fields with the values that fail constraint checking fail type checking.
   */

  @Test
  public void testTypeCheckFails3()
  {
    final var type =
      new CATypeRecord(
        P,
        NAME_EX,
        "An example type.",
        Map.ofEntries(
          Map.entry(
            NAME_X,
            new CATypeField(
              NAME_X,
              "A x.",
              new CATypeScalarType.Time(
                P,
                NAME_T,
                "A t.",
                OffsetDateTime.parse("2001-01-01T00:00:00+00:00"),
                OffsetDateTime.parse("2010-01-01T00:00:00+00:00")
              ),
              true
            )
          )
        )
      );

    final var checker =
      CATypeChecking.create(
        CAStrings.create(Locale.ROOT),
        Set.of(type),
        Set.of(
          new CAMetadataType.Time(
            NAME_X,
            OffsetDateTime.parse(
              "2015-01-01T00:00:00+00:00"))
        )
      );

    executeAndAssumeFailure(checker);
  }

  /**
   * Fields with the values that fail constraint checking fail type checking.
   */

  @Test
  public void testTypeCheckFails4()
  {
    final var type =
      new CATypeRecord(
        P,
        NAME_EX,
        "An example type.",
        Map.ofEntries(
          Map.entry(
            NAME_X,
            new CATypeField(
              NAME_X,
              "A x.",
              new CATypeScalarType.Text(
                P,
                NAME_T,
                "A t.",
                "[0-9]+"
              ),
              true
            )
          )
        )
      );

    final var checker =
      CATypeChecking.create(
        CAStrings.create(Locale.ROOT),
        Set.of(type),
        Set.of(
          new CAMetadataType.Text(NAME_X, "abcdefghijklmnopqrstuvwxyz")
        )
      );

    executeAndAssumeFailure(checker);
  }

  private static void executeAndAssumeFailure(
    final CATypeChecking checker)
  {
    final var errors =
      checker.execute();

    errors.stream()
      .filter(e -> Objects.equals(e.errorCode(), errorTypeCheckFieldInvalid()))
      .findFirst()
      .orElseThrow();
  }

  private static void executeAndAssumeSuccess(
    final CATypeChecking checker)
  {
    final var errors = checker.execute();
    assertEquals(List.of(), errors);
  }
}
