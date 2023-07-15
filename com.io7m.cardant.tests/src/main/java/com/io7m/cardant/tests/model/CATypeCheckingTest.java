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

import com.io7m.cardant.model.CAMetadata;
import com.io7m.cardant.model.CATypeChecking;
import com.io7m.cardant.model.CATypeDeclaration;
import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.model.CATypeScalar;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.lanark.core.RDottedName;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeCheckFieldInvalid;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorTypeCheckFieldRequiredMissing;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class CATypeCheckingTest
{
  /**
   * Missing but required fields are caught.
   */

  @Test
  public void testMissingRequiredField()
  {
    final var type =
      new CATypeDeclaration(
        new RDottedName("com.io7m.ex"),
        "An example type.",
        Map.ofEntries(
          Map.entry(
            new RDottedName("com.io7m.x"),
            new CATypeField(
              new RDottedName("com.io7m.x"),
              "A x.",
              new CATypeScalar(
                new RDottedName("com.io7m.t"),
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

    final var errors =
      checker.execute();
    final var error =
      errors.stream()
        .filter(e -> Objects.equals(e.errorCode(), errorTypeCheckFieldRequiredMissing()))
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
      new CATypeDeclaration(
        new RDottedName("com.io7m.ex"),
        "An example type.",
        Map.ofEntries(
          Map.entry(
            new RDottedName("com.io7m.x"),
            new CATypeField(
              new RDottedName("com.io7m.x"),
              "A x.",
              new CATypeScalar(
                new RDottedName("com.io7m.t"),
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
      new CATypeDeclaration(
        new RDottedName("com.io7m.ex"),
        "An example type.",
        Map.ofEntries(
          Map.entry(
            new RDottedName("com.io7m.x"),
            new CATypeField(
              new RDottedName("com.io7m.x"),
              "A x.",
              new CATypeScalar(
                new RDottedName("com.io7m.t"),
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
          new CAMetadata(new RDottedName("com.io7m.x"), "z")
        )
      );

    final var errors =
      checker.execute();
    final var error =
      errors.stream()
        .filter(e -> Objects.equals(e.errorCode(), errorTypeCheckFieldInvalid()))
        .findFirst()
        .orElseThrow();
  }
}
