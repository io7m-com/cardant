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
import com.io7m.cardant.model.CAItemLocationMatchType;
import com.io7m.cardant.model.CAItemLocationMatchType.CAItemLocationExact;
import com.io7m.cardant.model.CAItemLocationMatchType.CAItemLocationWithDescendants;
import com.io7m.cardant.model.CAItemLocationMatchType.CAItemLocationsAll;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.strings.CAStringConstantType;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.SExpressionType.SAtomType;
import com.io7m.jsx.SExpressionType.SList;
import com.io7m.jsx.SExpressionType.SSymbol;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_LOCATION_MATCH;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_LOCATION_MATCH_ANYNAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_LOCATION_MATCH_ANYNAME_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_LOCATION_MATCH_DESC;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_LOCATION_MATCH_DESC_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_LOCATION_MATCH_EXACT;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_LOCATION_MATCH_EXACT_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_LOCATION_MATCH_NAME;
import static com.io7m.jlexing.core.LexicalPositions.zero;
import static java.util.Map.entry;

/**
 * Expression parsers for location match expressions.
 */

public final class CAItemLocationMatchExpressions extends CAExpressions
{
  private static final Map<CAStringConstantType, CAStringConstantType> SYNTAX =
    Map.ofEntries(
      entry(
        SYNTAX_LOCATION_MATCH_NAME,
        SYNTAX_LOCATION_MATCH),
      entry(
        SYNTAX_LOCATION_MATCH_EXACT_NAME,
        SYNTAX_LOCATION_MATCH_EXACT),
      entry(
        SYNTAX_LOCATION_MATCH_DESC_NAME,
        SYNTAX_LOCATION_MATCH_DESC),
      entry(
        SYNTAX_LOCATION_MATCH_ANYNAME_NAME,
        SYNTAX_LOCATION_MATCH_ANYNAME)
    );

  /**
   * Expression parsers for location match expressions.
   *
   * @param inStrings The string resources
   */

  public CAItemLocationMatchExpressions(
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
   * Parse a match expression for locations.
   *
   * @param text The input location
   *
   * @return A match expression
   *
   * @throws CAException On errors
   */

  public CAItemLocationMatchType locationMatch(
    final String text)
    throws CAException
  {
    return this.locationMatchExpr(CAExpressions.parse(text));
  }

  private CAItemLocationMatchType locationMatchExpr(
    final SExpressionType expression)
    throws CAException
  {
    if (expression instanceof final SAtomType atom) {
      return switch (atom.text().toUpperCase(Locale.ROOT)) {
        case "ANY-LOCATION" -> {
          yield new CAItemLocationsAll();
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
        case "WITH-LOCATION-EXACT" -> {
          yield this.locationMatchExact(list);
        }
        case "WITH-LOCATION-OR-DESCENDANTS" -> {
          yield this.locationMatchWithDescendants(list);
        }
        default -> throw this.createParseError(head);
      };
    }

    throw this.createParseError(expression);
  }

  private CAItemLocationMatchType locationMatchExact(
    final SList list)
    throws CAException
  {
    if (list.size() == 2) {
      return new CAItemLocationExact(
        this.location(list.get(1))
      );
    }
    throw this.createParseError(list);
  }

  private CAItemLocationMatchType locationMatchWithDescendants(
    final SList list)
    throws CAException
  {
    if (list.size() == 2) {
      return new CAItemLocationWithDescendants(
        this.location(list.get(1))
      );
    }
    throw this.createParseError(list);
  }

  private CALocationID location(
    final SExpressionType expr)
    throws CAException
  {
    if (expr instanceof final SAtomType atom) {
      try {
        return new CALocationID(UUID.fromString(atom.text()));
      } catch (final Exception e) {
        throw this.createParseError(expr, e);
      }
    }

    throw this.createParseError(expr);
  }

  /**
   * Serialize a match expression for locations.
   *
   * @param match The input match
   *
   * @return A match expression
   *
   */

  public SExpressionType locationMatchSerialize(
    final CAItemLocationMatchType match)
  {
    if (match instanceof CAItemLocationsAll) {
      return new SSymbol(zero(), "any-location");
    }

    if (match instanceof final CAItemLocationExact w) {
      return new SList(
        zero(),
        true,
        List.of(
          new SSymbol(zero(), "with-location-exact"),
          new SSymbol(zero(), w.location().displayId())
        )
      );
    }

    if (match instanceof final CAItemLocationWithDescendants w) {
      return new SList(
        zero(),
        true,
        List.of(
          new SSymbol(zero(), "with-location-or-descendants"),
          new SSymbol(zero(), w.location().displayId())
        )
      );
    }

    throw new IllegalStateException();
  }

  /**
   * Serialize a match expression for locations.
   *
   * @param match The input match
   *
   * @return A match expression
   *
   * @throws CAException On errors
   */

  public String locationMatchSerializeToString(
    final CAItemLocationMatchType match)
    throws CAException
  {
    return CAExpressions.serialize(this.locationMatchSerialize(match));
  }
}
