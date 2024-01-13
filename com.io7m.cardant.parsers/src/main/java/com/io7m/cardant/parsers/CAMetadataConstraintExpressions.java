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

import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.model.CAMonetaryRange;
import com.io7m.cardant.model.CAMoney;
import com.io7m.cardant.model.CATimeRange;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.jranges.RangeInclusiveD;
import com.io7m.jranges.RangeInclusiveL;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.SExpressionType.SAtomType;
import com.io7m.jsx.SExpressionType.SList;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_INTEGER_RANGE;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_INTEGER_RANGE_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_MONEY_RANGE;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_MONEY_RANGE_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_RANGE_EXAMPLE_0;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_RANGE_EXAMPLE_1;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_RANGE_EXAMPLE_2;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_RANGE_EXAMPLE_3;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_RANGE_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_REAL_RANGE;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_REAL_RANGE_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TIME_RANGE;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TIME_RANGE_NAME;

/**
 * Expression parsers for type constraints.
 */

public final class CAMetadataConstraintExpressions extends CAExpressions
{
  /**
   * Expression parsers for type constraints.
   *
   * @param inStrings The string resources
   */

  public CAMetadataConstraintExpressions(
    final CAStrings inStrings)
  {
    super(inStrings);
  }

  /**
   * Parse a range.
   *
   * @param text The text
   *
   * @return A range
   *
   * @throws CAException On errors
   */

  public CATimeRange timeRange(
    final String text)
    throws CAException
  {
    return this.timeRangeExpr(CAExpressions.parse(text));
  }

  private CATimeRange timeRangeExpr(
    final SExpressionType expression)
    throws CAException
  {
    if (expression instanceof final SList list
      && list.size() == 2
      && list.get(0) instanceof final SAtomType atomLower
      && list.get(1) instanceof final SAtomType atomUpper) {

      try {
        return new CATimeRange(
          OffsetDateTime.parse(atomLower.text()),
          OffsetDateTime.parse(atomUpper.text())
        );
      } catch (final Exception e) {
        throw this.createParseError(expression, e);
      }
    }

    throw this.createParseError(expression);
  }

  /**
   * Parse a range.
   *
   * @param text The text
   *
   * @return A range
   *
   * @throws CAException On errors
   */

  public RangeInclusiveL integerRange(
    final String text)
    throws CAException
  {
    return this.integerRangeExpr(CAExpressions.parse(text));
  }

  private RangeInclusiveL integerRangeExpr(
    final SExpressionType expression)
    throws CAException
  {
    if (expression instanceof final SList list
      && list.size() == 2
      && list.get(0) instanceof final SAtomType atomLower
      && list.get(1) instanceof final SAtomType atomUpper) {

      try {
        return RangeInclusiveL.of(
          Long.parseLong(atomLower.text()),
          Long.parseLong(atomUpper.text())
        );
      } catch (final Exception e) {
        throw this.createParseError(expression, e);
      }
    }

    throw this.createParseError(expression);
  }

  /**
   * Parse a range.
   *
   * @param text The text
   *
   * @return A range
   *
   * @throws CAException On errors
   */

  public RangeInclusiveD realRange(
    final String text)
    throws CAException
  {
    return this.realRangeExpr(CAExpressions.parse(text));
  }

  private RangeInclusiveD realRangeExpr(
    final SExpressionType expression)
    throws CAException
  {
    if (expression instanceof final SList list
      && list.size() == 2
      && list.get(0) instanceof final SAtomType atomLower
      && list.get(1) instanceof final SAtomType atomUpper) {

      try {
        return RangeInclusiveD.of(
          Double.parseDouble(atomLower.text()),
          Double.parseDouble(atomUpper.text())
        );
      } catch (final Exception e) {
        throw this.createParseError(expression, e);
      }
    }

    throw this.createParseError(expression);
  }

  /**
   * Parse a range.
   *
   * @param text The text
   *
   * @return A range
   *
   * @throws CAException On errors
   */

  public CAMonetaryRange monetaryRange(
    final String text)
    throws CAException
  {
    return this.monetaryRangeExpr(CAExpressions.parse(text));
  }

  private CAMonetaryRange monetaryRangeExpr(
    final SExpressionType expression)
    throws CAException
  {
    if (expression instanceof final SList list
      && list.size() == 2
      && list.get(0) instanceof final SAtomType atomLower
      && list.get(1) instanceof final SAtomType atomUpper) {

      try {
        return new CAMonetaryRange(
          CAMoney.money(atomLower.text()),
          CAMoney.money(atomUpper.text())
        );
      } catch (final Exception e) {
        throw this.createParseError(expression, e);
      }
    }

    throw this.createParseError(expression);
  }

  @Override
  public SortedSet<CASyntaxRuleType> syntaxRules()
  {
    final var results = new TreeSet<CASyntaxRuleType>();

    results.add(
      this.ruleBranch(
        SYNTAX_RANGE_NAME,
        List.of(
          SYNTAX_INTEGER_RANGE_NAME,
          SYNTAX_MONEY_RANGE_NAME,
          SYNTAX_REAL_RANGE_NAME,
          SYNTAX_TIME_RANGE_NAME
        ),
        List.of(
          SYNTAX_RANGE_EXAMPLE_0,
          SYNTAX_RANGE_EXAMPLE_1,
          SYNTAX_RANGE_EXAMPLE_2,
          SYNTAX_RANGE_EXAMPLE_3
        )
      )
    );

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_INTEGER_RANGE_NAME,
        SYNTAX_INTEGER_RANGE,
        List.of(SYNTAX_RANGE_EXAMPLE_0)
      )
    );
    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_MONEY_RANGE_NAME,
        SYNTAX_MONEY_RANGE,
        List.of(SYNTAX_RANGE_EXAMPLE_1)
      )
    );
    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_REAL_RANGE_NAME,
        SYNTAX_REAL_RANGE,
        List.of(SYNTAX_RANGE_EXAMPLE_2)
      )
    );
    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_TIME_RANGE_NAME,
        SYNTAX_TIME_RANGE,
        List.of(SYNTAX_RANGE_EXAMPLE_3)
      )
    );

    return Collections.unmodifiableSortedSet(results);
  }
}
