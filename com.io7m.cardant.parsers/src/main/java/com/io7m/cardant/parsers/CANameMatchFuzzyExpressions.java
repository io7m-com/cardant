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
import com.io7m.cardant.model.CANameMatchFuzzy;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType.Anything;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType.IsEqualTo;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType.IsNotEqualTo;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType.IsNotSimilarTo;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType.IsSimilarTo;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.SExpressionType.SAtomType;
import com.io7m.jsx.SExpressionType.SList;
import com.io7m.jsx.SExpressionType.SListType;
import com.io7m.jsx.SExpressionType.SQuotedString;
import com.io7m.jsx.SExpressionType.SSymbol;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_ANYNAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_ANYNAME_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_EXAMPLE_0;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_EXAMPLE_1;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_EXAMPLE_2;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_EXAMPLE_3;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_EQUAL_TO;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_EQUAL_TO_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_NOT_EQUAL_TO;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_NOT_EQUAL_TO_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_NOT_SIMILAR_TO;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_NOT_SIMILAR_TO_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_SIMILAR_TO;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_SIMILAR_TO_NAME;
import static com.io7m.jlexing.core.LexicalPositions.zero;

/**
 * Expression parsers for name match expressions.
 */

public final class CANameMatchFuzzyExpressions extends CAExpressions
{

  /**
   * Expression parsers for name match expressions.
   *
   * @param inStrings The string resources
   */

  public CANameMatchFuzzyExpressions(
    final CAStrings inStrings)
  {
    super(inStrings);
  }

  /**
   * Parse a match expression for names.
   *
   * @param text The input text
   *
   * @return A match expression
   *
   * @throws CAException On errors
   */

  public CANameMatchFuzzy nameMatch(
    final String text)
    throws CAException
  {
    return this.nameMatch(CAExpressions.parse(text));
  }

  /**
   * Parse a match expression for names.
   *
   * @param e The input expression
   *
   * @return A match expression
   *
   * @throws CAException On errors
   */

  public CANameMatchFuzzy nameMatch(
    final SExpressionType e)
    throws CAException
  {
    return new CANameMatchFuzzy(this.nameMatchExpr(e));
  }

  private CAComparisonFuzzyType<String> nameMatchExpr(
    final SExpressionType e)
    throws CAException
  {
    return switch (e) {
      case final SAtomType a
        when "WITH-ANY-NAME".equals(a.text().toUpperCase(Locale.ROOT)) -> {
        yield new Anything<>();
      }
      case final SListType xs
        when xs.size() == 2
          && xs.get(0) instanceof final SAtomType head
          && xs.get(1) instanceof final SAtomType value -> {
        yield switch (head.text().toUpperCase(Locale.ROOT)) {
          case "WITH-NAME-EQUAL-TO" -> {
            yield new IsEqualTo<>(value.text());
          }
          case "WITH-NAME-NOT-EQUAL-TO" -> {
            yield new IsNotEqualTo<>(value.text());
          }
          case "WITH-NAME-SIMILAR-TO" -> {
            yield new IsSimilarTo<>(value.text());
          }
          case "WITH-NAME-NOT-SIMILAR-TO" -> {
            yield new IsNotSimilarTo<>(value.text());
          }
          default -> {
            throw this.createParseError(e);
          }
        };
      }
      default -> {
        throw this.createParseError(e);
      }
    };
  }

  /**
   * Serialize a match expression to a string.
   *
   * @param value The expression
   *
   * @return The serialized text
   *
   * @throws CAException On errors
   */

  public String nameMatchSerializeToString(
    final CANameMatchFuzzy value)
    throws CAException
  {
    return CAExpressions.serialize(this.nameMatchSerialize(value.expression()));
  }

  /**
   * Serialize a match expression.
   *
   * @param expression The expression
   *
   * @return The serialized expression
   */

  public SExpressionType nameMatchSerialize(
    final CAComparisonFuzzyType<String> expression)
  {
    return switch (expression) {
      case final Anything<String> ignored -> {
        yield new SSymbol(zero(), "with-any-name");
      }

      case final IsEqualTo<String> e -> {
        yield new SList(
          zero(),
          true,
          List.of(
            new SSymbol(zero(), "with-name-equal-to"),
            new SQuotedString(zero(), e.value())
          )
        );
      }

      case final IsNotEqualTo<String> e -> {
        yield new SList(
          zero(),
          true,
          List.of(
            new SSymbol(zero(), "with-name-not-equal-to"),
            new SQuotedString(zero(), e.value())
          )
        );
      }

      case final IsNotSimilarTo<String> e -> {
        yield new SList(
          zero(),
          true,
          List.of(
            new SSymbol(zero(), "with-name-not-similar-to"),
            new SQuotedString(zero(), e.value())
          )
        );
      }

      case final IsSimilarTo<String> e -> {
        yield new SList(
          zero(),
          true,
          List.of(
            new SSymbol(zero(), "with-name-similar-to"),
            new SQuotedString(zero(), e.value())
          )
        );
      }
    };
  }

  @Override
  public SortedSet<CASyntaxRuleType> syntaxRules()
  {
    final var results = new TreeSet<CASyntaxRuleType>();

    results.add(
      this.ruleBranch(
        SYNTAX_NAME_MATCH_NAME,
        List.of(
          SYNTAX_NAME_MATCH_ANYNAME_NAME,
          SYNTAX_NAME_MATCH_WITH_NAME_EQUAL_TO_NAME,
          SYNTAX_NAME_MATCH_WITH_NAME_NOT_EQUAL_TO_NAME,
          SYNTAX_NAME_MATCH_WITH_NAME_SIMILAR_TO_NAME,
          SYNTAX_NAME_MATCH_WITH_NAME_NOT_SIMILAR_TO_NAME
        ),
        List.of(
          SYNTAX_NAME_MATCH_EXAMPLE_0,
          SYNTAX_NAME_MATCH_EXAMPLE_1,
          SYNTAX_NAME_MATCH_EXAMPLE_2,
          SYNTAX_NAME_MATCH_EXAMPLE_3
        )
      )
    );

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_NAME_MATCH_ANYNAME_NAME,
        SYNTAX_NAME_MATCH_ANYNAME,
        List.of(SYNTAX_NAME_MATCH_ANYNAME)
      )
    );

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_NAME_MATCH_WITH_NAME_EQUAL_TO_NAME,
        SYNTAX_NAME_MATCH_WITH_NAME_EQUAL_TO,
        List.of(SYNTAX_NAME_MATCH_EXAMPLE_0)
      )
    );

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_NAME_MATCH_WITH_NAME_NOT_EQUAL_TO_NAME,
        SYNTAX_NAME_MATCH_WITH_NAME_NOT_EQUAL_TO,
        List.of(SYNTAX_NAME_MATCH_EXAMPLE_1)
      )
    );

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_NAME_MATCH_WITH_NAME_SIMILAR_TO_NAME,
        SYNTAX_NAME_MATCH_WITH_NAME_SIMILAR_TO,
        List.of(SYNTAX_NAME_MATCH_EXAMPLE_2)
      )
    );

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_NAME_MATCH_WITH_NAME_NOT_SIMILAR_TO_NAME,
        SYNTAX_NAME_MATCH_WITH_NAME_NOT_SIMILAR_TO,
        List.of(SYNTAX_NAME_MATCH_EXAMPLE_3)
      )
    );

    return Collections.unmodifiableSortedSet(results);
  }
}
