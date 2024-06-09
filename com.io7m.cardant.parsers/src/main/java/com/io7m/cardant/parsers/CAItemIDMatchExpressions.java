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
import com.io7m.cardant.model.CAItemID;
import com.io7m.cardant.model.CAItemIDMatch;
import com.io7m.cardant.model.comparisons.CAComparisonExactType;
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
import java.util.UUID;

import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_ITEM_MATCH_ANYITEM;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_ITEM_MATCH_ANYITEM_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_ITEM_MATCH_EXAMPLE_0;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_ITEM_MATCH_EXAMPLE_1;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_ITEM_MATCH_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_ITEM_MATCH_WITH_ITEM_EQUAL_TO;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_ITEM_MATCH_WITH_ITEM_EQUAL_TO_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_ITEM_MATCH_WITH_ITEM_NOT_EQUAL_TO;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_ITEM_MATCH_WITH_ITEM_NOT_EQUAL_TO_NAME;
import static com.io7m.jlexing.core.LexicalPositions.zero;

/**
 * Expression parsers for name match expressions.
 */

public final class CAItemIDMatchExpressions extends CAExpressions
{
  /**
   * Expression parsers for name match expressions.
   *
   * @param inStrings The string resources
   */

  public CAItemIDMatchExpressions(
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

  public CAItemIDMatch idMatch(
    final String text)
    throws CAException
  {
    return this.idMatch(CAExpressions.parse(text));
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

  public CAItemIDMatch idMatch(
    final SExpressionType e)
    throws CAException
  {
    return new CAItemIDMatch(this.idMatchExpr(e));
  }

  private CAComparisonExactType<CAItemID> idMatchExpr(
    final SExpressionType e)
    throws CAException
  {
    return switch (e) {
      case final SAtomType a
        when "WITH-ANY-ITEM".equals(a.text().toUpperCase(Locale.ROOT)) -> {
        yield new CAComparisonExactType.Anything<>();
      }
      case final SListType xs
        when xs.size() == 2
          && xs.get(0) instanceof final SAtomType head
          && xs.get(1) instanceof final SAtomType value -> {
        yield switch (head.text().toUpperCase(Locale.ROOT)) {
          case "WITH-ITEM-EQUAL-TO" -> {
            yield new CAComparisonExactType.IsEqualTo<>(
              new CAItemID(UUID.fromString(value.text()))
            );
          }
          case "WITH-ITEM-NOT-EQUAL-TO" -> {
            yield new CAComparisonExactType.IsNotEqualTo<>(
              new CAItemID(UUID.fromString(value.text()))
            );
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

  public String idMatchSerializeToString(
    final CAItemIDMatch value)
    throws CAException
  {
    return CAExpressions.serialize(
      this.serialMatchSerialize(value.expression())
    );
  }

  /**
   * Serialize a match expression.
   *
   * @param expression The expression
   *
   * @return The serialized expression
   */

  public SExpressionType serialMatchSerialize(
    final CAComparisonExactType<CAItemID> expression)
  {
    return switch (expression) {
      case final CAComparisonExactType.Anything<CAItemID> ignored -> {
        yield new SSymbol(zero(), "with-any-item");
      }
      case final CAComparisonExactType.IsEqualTo<CAItemID> e -> {
        yield new SList(
          zero(),
          true,
          List.of(
            new SSymbol(zero(), "with-item-equal-to"),
            new SQuotedString(zero(), e.value().id().toString())
          )
        );
      }
      case final CAComparisonExactType.IsNotEqualTo<CAItemID> e -> {
        yield new SList(
          zero(),
          true,
          List.of(
            new SSymbol(zero(), "with-item-not-equal-to"),
            new SQuotedString(zero(), e.value().id().toString())
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
        SYNTAX_ITEM_MATCH_NAME,
        List.of(
          SYNTAX_ITEM_MATCH_ANYITEM_NAME,
          SYNTAX_ITEM_MATCH_WITH_ITEM_EQUAL_TO_NAME,
          SYNTAX_ITEM_MATCH_WITH_ITEM_NOT_EQUAL_TO_NAME
        ),
        List.of(
          SYNTAX_ITEM_MATCH_ANYITEM
        )
      )
    );

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_ITEM_MATCH_ANYITEM_NAME,
        SYNTAX_ITEM_MATCH_ANYITEM,
        List.of(SYNTAX_ITEM_MATCH_ANYITEM)
      )
    );

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_ITEM_MATCH_WITH_ITEM_EQUAL_TO_NAME,
        SYNTAX_ITEM_MATCH_WITH_ITEM_EQUAL_TO,
        List.of(SYNTAX_ITEM_MATCH_EXAMPLE_0)
      )
    );

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_ITEM_MATCH_WITH_ITEM_NOT_EQUAL_TO_NAME,
        SYNTAX_ITEM_MATCH_WITH_ITEM_NOT_EQUAL_TO,
        List.of(SYNTAX_ITEM_MATCH_EXAMPLE_1)
      )
    );

    return Collections.unmodifiableSortedSet(results);
  }
}
