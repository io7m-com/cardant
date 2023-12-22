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
import com.io7m.cardant.model.CATypeMatch;
import com.io7m.cardant.model.comparisons.CAComparisonSetType;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.Anything;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.IsEqualTo;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.IsNotEqualTo;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.IsOverlapping;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.IsSubsetOf;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.IsSupersetOf;
import com.io7m.cardant.strings.CAStringConstantType;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.SExpressionType.SAtomType;
import com.io7m.jsx.SExpressionType.SList;
import com.io7m.jsx.SExpressionType.SListType;
import com.io7m.jsx.SExpressionType.SSymbol;
import com.io7m.lanark.core.RDottedName;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_ANYNAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_ANYNAME_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_EQUAL_TO;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_EQUAL_TO_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_NOT_EQUAL_TO;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_NOT_EQUAL_TO_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_NOT_SIMILAR_TO;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_NOT_SIMILAR_TO_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_SIMILAR_TO;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_NAME_MATCH_WITH_NAME_SIMILAR_TO_NAME;
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
        SYNTAX_NAME_MATCH_ANYNAME_NAME,
        SYNTAX_NAME_MATCH_ANYNAME),
      entry(
        SYNTAX_NAME_MATCH_WITH_NAME_EQUAL_TO_NAME,
        SYNTAX_NAME_MATCH_WITH_NAME_EQUAL_TO),
      entry(
        SYNTAX_NAME_MATCH_WITH_NAME_NOT_EQUAL_TO_NAME,
        SYNTAX_NAME_MATCH_WITH_NAME_NOT_EQUAL_TO),
      entry(
        SYNTAX_NAME_MATCH_WITH_NAME_SIMILAR_TO_NAME,
        SYNTAX_NAME_MATCH_WITH_NAME_SIMILAR_TO),
      entry(
        SYNTAX_NAME_MATCH_WITH_NAME_NOT_SIMILAR_TO_NAME,
        SYNTAX_NAME_MATCH_WITH_NAME_NOT_SIMILAR_TO)
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

  public CATypeMatch typeMatch(
    final String text)
    throws CAException
  {
    return this.typeMatch(CAExpressions.parse(text));
  }

  /**
   * Parse a match expression for types.
   *
   * @param e The input expression
   *
   * @return A match expression
   *
   * @throws CAException On errors
   */

  public CATypeMatch typeMatch(
    final SExpressionType e)
    throws CAException
  {
    return new CATypeMatch(this.typeMatchExpr(e));
  }

  private CAComparisonSetType<RDottedName> typeMatchExpr(
    final SExpressionType e)
    throws CAException
  {
    return switch (e) {
      case final SAtomType a
        when "WITH-ANY-TYPE".equals(a.text().toUpperCase(Locale.ROOT)) -> {
        yield new Anything<>();
      }
      case final SListType xs
        when xs.size() >= 1
          && xs.get(0) instanceof final SAtomType head -> {
        yield switch (head.text().toUpperCase(Locale.ROOT)) {
          case "WITH-TYPES-EQUAL-TO" -> {
            yield new IsEqualTo<>(this.dottedNamesAfterHead(xs));
          }
          case "WITH-TYPES-NOT-EQUAL-TO" -> {
            yield new IsNotEqualTo<>(this.dottedNamesAfterHead(xs));
          }
          case "WITH-TYPES-SUBSET-OF" -> {
            yield new IsSubsetOf<>(this.dottedNamesAfterHead(xs));
          }
          case "WITH-TYPES-SUPERSET-OF" -> {
            yield new IsSupersetOf<>(this.dottedNamesAfterHead(xs));
          }
          case "WITH-TYPES-OVERLAPPING" -> {
            yield new IsOverlapping<>(this.dottedNamesAfterHead(xs));
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

  private Set<RDottedName> dottedNamesAfterHead(
    final SListType xs)
    throws CAException
  {
    if (xs.size() <= 1) {
      return Set.of();
    }

    final var results = new HashSet<RDottedName>();
    for (var index = 1; index < xs.size(); ++index) {
      switch (xs.get(index)) {
        case final SAtomType a -> {
          results.add(new RDottedName(a.text()));
        }
        case final SListType ys -> {
          throw this.createParseError(ys);
        }
      }
    }
    return Set.copyOf(results);
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

  public String typeMatchSerializeToString(
    final CATypeMatch value)
    throws CAException
  {
    return CAExpressions.serialize(this.typeMatchSerialize(value.expression()));
  }

  /**
   * Serialize a match expression.
   *
   * @param expression The expression
   *
   * @return The serialized expression
   */

  public SExpressionType typeMatchSerialize(
    final CAComparisonSetType<RDottedName> expression)
  {
    return switch (expression) {
      case final Anything<RDottedName> e -> {
        yield new SSymbol(zero(), "with-any-type");
      }
      case final IsEqualTo<RDottedName> e -> {
        yield new SList(
          zero(),
          true,
          Stream.concat(
            Stream.of(
              new SSymbol(zero(), "with-types-equal-to")
            ),
            e.value().stream().map(CATypeMatchExpressions::nameToSymbol)
          ).toList()
        );
      }
      case final IsNotEqualTo<RDottedName> e -> {
        yield new SList(
          zero(),
          true,
          Stream.concat(
            Stream.of(
              new SSymbol(zero(), "with-types-not-equal-to")
            ),
            e.value().stream().map(CATypeMatchExpressions::nameToSymbol)
          ).toList()
        );
      }
      case final IsOverlapping<RDottedName> e -> {
        yield new SList(
          zero(),
          true,
          Stream.concat(
            Stream.of(
              new SSymbol(zero(), "with-types-overlapping")
            ),
            e.value().stream().map(CATypeMatchExpressions::nameToSymbol)
          ).toList()
        );
      }
      case final IsSubsetOf<RDottedName> e -> {
        yield new SList(
          zero(),
          true,
          Stream.concat(
            Stream.of(
              new SSymbol(zero(), "with-types-subset-of")
            ),
            e.value().stream().map(CATypeMatchExpressions::nameToSymbol)
          ).toList()
        );
      }
      case final IsSupersetOf<RDottedName> e -> {
        yield new SList(
          zero(),
          true,
          Stream.concat(
            Stream.of(
              new SSymbol(zero(), "with-types-superset-of")
            ),
            e.value().stream().map(CATypeMatchExpressions::nameToSymbol)
          ).toList()
        );
      }
    };
  }

  private static SExpressionType nameToSymbol(
    final RDottedName n)
  {
    return new SSymbol(zero(), n.value());
  }
}
