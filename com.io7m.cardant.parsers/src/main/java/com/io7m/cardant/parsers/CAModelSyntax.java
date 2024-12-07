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


package com.io7m.cardant.parsers;

import com.io7m.cardant.strings.CAStrings;

import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_DOTTED_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_DOTTED_NAME_EXAMPLE;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_DOTTED_NAME_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_PACKAGE;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_PACKAGE_EXAMPLE;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_PACKAGE_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_RECORD_FIELD_IDENTIFIER;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_RECORD_FIELD_IDENTIFIER_EXAMPLE;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_RECORD_FIELD_IDENTIFIER_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_RECORD_IDENTIFIER;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_RECORD_IDENTIFIER_EXAMPLE;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_RECORD_IDENTIFIER_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_UNQUALIFIED_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_UNQUALIFIED_NAME_EXAMPLE;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_UNQUALIFIED_NAME_NAME;

/**
 * Model type syntax.
 */

public final class CAModelSyntax
  extends CAExpressions
{
  CAModelSyntax(
    final CAStrings inStrings)
  {
    super(inStrings);
  }

  @Override
  public SortedSet<CASyntaxRuleType> syntaxRules()
  {
    final var results = new TreeSet<CASyntaxRuleType>();

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_DOTTED_NAME_NAME,
        SYNTAX_DOTTED_NAME,
        List.of(SYNTAX_DOTTED_NAME_EXAMPLE)
      )
    );

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_UNQUALIFIED_NAME_NAME,
        SYNTAX_UNQUALIFIED_NAME,
        List.of(SYNTAX_UNQUALIFIED_NAME_EXAMPLE)
      )
    );

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_TYPE_RECORD_IDENTIFIER_NAME,
        SYNTAX_TYPE_RECORD_IDENTIFIER,
        List.of(SYNTAX_TYPE_RECORD_IDENTIFIER_EXAMPLE)
      )
    );

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_TYPE_RECORD_FIELD_IDENTIFIER_NAME,
        SYNTAX_TYPE_RECORD_FIELD_IDENTIFIER,
        List.of(SYNTAX_TYPE_RECORD_FIELD_IDENTIFIER_EXAMPLE)
      )
    );

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_PACKAGE_NAME,
        SYNTAX_PACKAGE,
        List.of(SYNTAX_PACKAGE_EXAMPLE)
      )
    );

    return Collections.unmodifiableSortedSet(results);
  }
}
