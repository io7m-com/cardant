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
import com.io7m.cardant.model.CATypeMatchType;
import com.io7m.cardant.model.CATypeMatchType.CATypeMatchAllOf;
import com.io7m.cardant.model.CATypeMatchType.CATypeMatchAnyOf;
import com.io7m.cardant.strings.CAStringConstantType;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.SExpressionType.SAtomType;
import com.io7m.jsx.SExpressionType.SList;
import com.io7m.jsx.SExpressionType.SSymbol;
import com.io7m.lanark.core.RDottedName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import static com.io7m.cardant.model.CATypeMatchType.CATypeMatchAny.ANY;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_ALL;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_ALL_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_ANY;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_ANYTYPE;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_ANYTYPE_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_ANY_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_NAME;
import static com.io7m.jlexing.core.LexicalPositions.zero;
import static java.util.Map.entry;

/**
 * Expression parsers for type match expressions.
 */

public final class CATypeMatchExpressions extends CAExpressions
{
  private static final Map<CAStringConstantType, CAStringConstantType> SYNTAX =
    Map.ofEntries(
      entry(
        SYNTAX_TYPE_MATCH_ALL_NAME,
        SYNTAX_TYPE_MATCH_ALL),
      entry(
        SYNTAX_TYPE_MATCH_ANY_NAME,
        SYNTAX_TYPE_MATCH_ANY),
      entry(
        SYNTAX_TYPE_MATCH_ANYTYPE_NAME,
        SYNTAX_TYPE_MATCH_ANYTYPE),
      entry(
        SYNTAX_TYPE_MATCH_NAME,
        SYNTAX_TYPE_MATCH)
    );

  /**
   * Expression parsers for type match expressions.
   *
   * @param inStrings The string resources
   */

  public CATypeMatchExpressions(
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
   * Parse a match expression for types.
   *
   * @param text The input text
   *
   * @return A match expression
   *
   * @throws CAException On errors
   */

  public CATypeMatchType typeMatch(
    final String text)
    throws CAException
  {
    return this.typeMatchExpr(CAExpressions.parse(text));
  }

  private CATypeMatchType typeMatchExpr(
    final SExpressionType expression)
    throws CAException
  {
    if (expression instanceof final SAtomType atom) {
      return switch (atom.text().toUpperCase(Locale.ROOT)) {
        case "ANY-TYPE" -> {
          yield ANY;
        }
        default -> {
          throw this.createParseError(expression);
        }
      };
    }

    if (expression instanceof final SList list
      && list.size() >= 1
      && list.get(0) instanceof final SAtomType head) {

      return switch (head.text().toUpperCase(Locale.ROOT)) {
        case "ALL-OF" -> {
          yield this.typeMatchExprAllOf(list);
        }
        case "ANY-OF" -> {
          yield this.typeMatchExprAnyOf(list);
        }
        default -> throw this.createParseError(head);
      };
    }

    throw this.createParseError(expression);
  }

  private CATypeMatchType typeMatchExprAnyOf(
    final SList list)
    throws CAException
  {
    final var names = new HashSet<RDottedName>();
    for (int index = 1; index < list.size(); ++index) {
      names.add(this.type(list.get(index)));
    }
    return new CATypeMatchAnyOf(names);
  }

  private CATypeMatchType typeMatchExprAllOf(
    final SList list)
    throws CAException
  {
    final var names = new HashSet<RDottedName>();
    for (int index = 1; index < list.size(); ++index) {
      names.add(this.type(list.get(index)));
    }
    return new CATypeMatchAllOf(names);
  }

  private RDottedName type(
    final SExpressionType expr)
    throws CAException
  {
    if (expr instanceof final SAtomType atom) {
      try {
        return new RDottedName(atom.text());
      } catch (final Exception e) {
        throw this.createParseError(expr, e);
      }
    }

    throw this.createParseError(expr);
  }

  /**
   * Serialize a match expression for types.
   *
   * @param match The input match
   *
   * @return A match expression
   *
   */

  public SExpressionType typeMatchSerialize(
    final CATypeMatchType match)
  {
    if (match == ANY) {
      return new SSymbol(zero(), "any-type");
    }

    if (match instanceof final CATypeMatchAllOf allOf) {
      final var types = new ArrayList<>(allOf.types());
      types.sort(RDottedName::compareTo);
      final var result = new ArrayList<SExpressionType>(types.size());
      result.add(new SSymbol(zero(), "all-of"));
      for (final var type : types) {
        result.add(new SSymbol(zero(), type.value()));
      }
      return new SList(zero(), true, result);
    }

    if (match instanceof final CATypeMatchAnyOf anyOf) {
      final var types = new ArrayList<>(anyOf.types());
      types.sort(RDottedName::compareTo);
      final var result = new ArrayList<SExpressionType>(types.size());
      result.add(new SSymbol(zero(), "any-of"));
      for (final var type : types) {
        result.add(new SSymbol(zero(), type.value()));
      }
      return new SList(zero(), true, result);
    }

    throw new IllegalStateException();
  }

  /**
   * Serialize a match expression for types.
   *
   * @param match The input match
   *
   * @return A match expression
   *
   * @throws CAException On errors
   */

  public String typeMatchSerializeToString(
    final CATypeMatchType match)
    throws CAException
  {
    return CAExpressions.serialize(this.typeMatchSerialize(match));
  }
}
