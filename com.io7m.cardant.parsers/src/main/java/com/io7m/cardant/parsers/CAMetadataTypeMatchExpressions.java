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
import com.io7m.cardant.model.CANameMatchExact;
import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.cardant.model.comparisons.CAComparisonExactType.Anything;
import com.io7m.cardant.model.comparisons.CAComparisonExactType.IsEqualTo;
import com.io7m.cardant.model.comparisons.CAComparisonExactType.IsNotEqualTo;
import com.io7m.cardant.strings.CAStringConstantType;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.SExpressionType.SAtomType;
import com.io7m.jsx.SExpressionType.SList;
import com.io7m.jsx.SExpressionType.SListType;
import com.io7m.jsx.SExpressionType.SQuotedString;
import com.io7m.jsx.SExpressionType.SSymbol;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_ANYTYPE;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_ANYTYPE_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_WITH_TYPE_EQUAL_TO;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_WITH_TYPE_EQUAL_TO_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_WITH_TYPE_NOT_EQUAL_TO;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_WITH_TYPE_NOT_EQUAL_TO_NAME;
import static com.io7m.jlexing.core.LexicalPositions.zero;
import static java.util.Map.entry;

/**
 * Expression parsers for name match expressions.
 */

public final class CAMetadataTypeMatchExpressions extends CAExpressions
{
  private static final Map<CAStringConstantType, CAStringConstantType> SYNTAX =
    Map.ofEntries(
      entry(
        SYNTAX_TYPE_MATCH_ANYTYPE_NAME,
        SYNTAX_TYPE_MATCH_ANYTYPE),
      entry(
        SYNTAX_TYPE_MATCH_WITH_TYPE_EQUAL_TO_NAME,
        SYNTAX_TYPE_MATCH_WITH_TYPE_EQUAL_TO),
      entry(
        SYNTAX_TYPE_MATCH_WITH_TYPE_NOT_EQUAL_TO_NAME,
        SYNTAX_TYPE_MATCH_WITH_TYPE_NOT_EQUAL_TO)
    );

  /**
   * Expression parsers for name match expressions.
   *
   * @param inStrings The string resources
   */

  public CAMetadataTypeMatchExpressions(
    final CAStrings inStrings)
  {
    super(inStrings);
  }

  @Override
  protected Map<CAStringConstantType, CAStringConstantType> syntax()
  {
    return SYNTAX;
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

  public CANameMatchExact metadataTypeMatch(
    final String text)
    throws CAException
  {
    return this.metadataTypeMatch(CAExpressions.parse(text));
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

  public CANameMatchExact metadataTypeMatch(
    final SExpressionType e)
    throws CAException
  {
    return new CANameMatchExact(this.nameMatchExpr(e));
  }

  private CAComparisonExactType<String> nameMatchExpr(
    final SExpressionType e)
    throws CAException
  {
    return switch (e) {
      case final SAtomType a
        when "WITH-ANY-TYPE".equals(a.text().toUpperCase(Locale.ROOT)) -> {
        yield new Anything<>();
      }
      case final SListType xs
        when xs.size() == 2
          && xs.get(0) instanceof final SAtomType head
          && xs.get(1) instanceof final SAtomType value -> {
        yield switch (head.text().toUpperCase(Locale.ROOT)) {
          case "WITH-TYPE-EQUAL-TO" -> {
            yield new IsEqualTo<>(value.text());
          }
          case "WITH-TYPE-NOT-EQUAL-TO" -> {
            yield new IsNotEqualTo<>(value.text());
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

  public String metadataTypeMatchSerializeToString(
    final CANameMatchExact value)
    throws CAException
  {
    return CAExpressions.serialize(
      this.metadataTypeMatchSerialize(value.expression())
    );
  }

  /**
   * Serialize a match expression.
   *
   * @param expression The expression
   *
   * @return The serialized expression
   */

  public SExpressionType metadataTypeMatchSerialize(
    final CAComparisonExactType<String> expression)
  {
    return switch (expression) {
      case final Anything<String> ignored -> {
        yield new SSymbol(zero(), "with-any-type");
      }

      case final IsEqualTo<String> e -> {
        yield new SList(
          zero(),
          true,
          List.of(
            new SSymbol(zero(), "with-type-equal-to"),
            new SQuotedString(zero(), e.value())
          )
        );
      }

      case final IsNotEqualTo<String> e -> {
        yield new SList(
          zero(),
          true,
          List.of(
            new SSymbol(zero(), "with-type-not-equal-to"),
            new SQuotedString(zero(), e.value())
          )
        );
      }
    };
  }
}
