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
import com.io7m.cardant.model.CANameMatchType;
import com.io7m.cardant.strings.CAStringConstantType;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.SExpressionType.SAtomType;
import com.io7m.jsx.SExpressionType.SList;
import com.io7m.jsx.SExpressionType.SQuotedString;
import com.io7m.jsx.SExpressionType.SSymbol;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.io7m.cardant.model.CANameMatchType.Any.ANY_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_ANYNAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_ANYNAME_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_EXACT;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_EXACT_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_SEARCH;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_SEARCH_NAME;
import static com.io7m.jlexing.core.LexicalPositions.zero;
import static java.util.Map.entry;

/**
 * Expression parsers for name match expressions.
 */

public final class CANameMatchExpressions extends CAExpressions
{
  private static final Map<CAStringConstantType, CAStringConstantType> SYNTAX =
    Map.ofEntries(
      entry(
        SYNTAX_NAME_MATCH_NAME,
        SYNTAX_NAME_MATCH),
      entry(
        SYNTAX_NAME_MATCH_WITH_NAME_EXACT_NAME,
        SYNTAX_NAME_MATCH_WITH_NAME_EXACT),
      entry(
        SYNTAX_NAME_MATCH_WITH_NAME_SEARCH_NAME,
        SYNTAX_NAME_MATCH_WITH_NAME_SEARCH),
      entry(
        SYNTAX_NAME_MATCH_ANYNAME_NAME,
        SYNTAX_NAME_MATCH_ANYNAME)
    );

  /**
   * Expression parsers for name match expressions.
   *
   * @param inStrings The string resources
   */

  public CANameMatchExpressions(
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

  public CANameMatchType nameMatch(
    final String text)
    throws CAException
  {
    return this.nameMatchExpr(CAExpressions.parse(text));
  }

  private CANameMatchType nameMatchExpr(
    final SExpressionType expression)
    throws CAException
  {
    if (expression instanceof final SAtomType atom) {
      return switch (atom.text().toUpperCase(Locale.ROOT)) {
        case "ANY-NAME" -> {
          yield ANY_NAME;
        }
        default -> {
          throw this.createParseError(expression);
        }
      };
    }

    if (expression instanceof final SList list
      && list.size() >= 2
      && list.get(0) instanceof final SAtomType head) {

      return switch (head.text().toUpperCase(Locale.ROOT)) {
        case "WITH-NAME-EXACT" -> {
          yield this.nameMatchExprExact(list);
        }
        case "WITH-NAME-SEARCH" -> {
          yield this.nameMatchExprSearch(list);
        }
        default -> throw this.createParseError(head);
      };
    }

    throw this.createParseError(expression);
  }

  private CANameMatchType nameMatchExprSearch(
    final SList list)
    throws CAException
  {
    if (list.size() == 2) {
      return new CANameMatchType.Search(
        this.text(list.get(1))
      );
    }
    throw this.createParseError(list);
  }

  private CANameMatchType nameMatchExprExact(
    final SList list)
    throws CAException
  {
    if (list.size() == 2) {
      return new CANameMatchType.Exact(
        this.text(list.get(1))
      );
    }
    throw this.createParseError(list);
  }

  private String text(
    final SExpressionType expr)
    throws CAException
  {
    if (expr instanceof final SAtomType atom) {
      return atom.text();
    }

    throw this.createParseError(expr);
  }

  /**
   * Serialize a match expression for names.
   *
   * @param match The input match
   *
   * @return A match expression
   *
   */

  public SExpressionType nameMatchSerialize(
    final CANameMatchType match)
  {
    if (match == ANY_NAME) {
      return new SSymbol(zero(), "any-name");
    }

    if (match instanceof final CANameMatchType.Exact exact) {
      return new SList(
        zero(),
        true,
        List.of(
          new SSymbol(zero(), "with-name-exact"),
          new SQuotedString(zero(), exact.text())
        )
      );
    }

    if (match instanceof final CANameMatchType.Search search) {
      return new SList(
        zero(),
        true,
        List.of(
          new SSymbol(zero(), "with-name-search"),
          new SQuotedString(zero(), search.query())
        )
      );
    }

    throw new IllegalStateException();
  }

  /**
   * Serialize a match expression for names.
   *
   * @param match The input match
   *
   * @return A match expression
   *
   * @throws CAException On errors
   */

  public String nameMatchSerializeToString(
    final CANameMatchType match)
    throws CAException
  {
    return CAExpressions.serialize(this.nameMatchSerialize(match));
  }
}
