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
import com.io7m.cardant.model.CATypeRecordIdentifier;
import com.io7m.cardant.model.comparisons.CAComparisonSetType;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.Anything;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.IsEqualTo;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.IsNotEqualTo;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.IsOverlapping;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.IsSubsetOf;
import com.io7m.cardant.model.comparisons.CAComparisonSetType.IsSupersetOf;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.jsx.SExpressionType;
import com.io7m.jsx.SExpressionType.SAtomType;
import com.io7m.jsx.SExpressionType.SList;
import com.io7m.jsx.SExpressionType.SListType;
import com.io7m.jsx.SExpressionType.SSymbol;
import com.io7m.lanark.core.RDottedName;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Stream;

import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_ANYTYPE;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_ANYTYPE_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_EQUAL_TO;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_EQUAL_TO_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_EXAMPLE_0;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_EXAMPLE_1;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_EXAMPLE_2;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_EXAMPLE_3;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_EXAMPLE_4;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_NOT_EQUAL_TO;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_NOT_EQUAL_TO_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_OVERLAPPING_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_OVERLAPPING_TO;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_SUBSETOF_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_SUBSETOF_TO;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_SUPERSETOF_NAME;
import static com.io7m.cardant.strings.CAStringConstants.SYNTAX_TYPE_MATCH_SUPERSETOF_TO;
import static com.io7m.jlexing.core.LexicalPositions.zero;

/**
 * Expression parsers for type match expressions.
 */

public final class CATypeMatchExpressions extends CAExpressions
{
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

  private CAComparisonSetType<CATypeRecordIdentifier> typeMatchExpr(
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
            yield new IsEqualTo<>(this.identifiersAfterHead(xs));
          }
          case "WITH-TYPES-NOT-EQUAL-TO" -> {
            yield new IsNotEqualTo<>(this.identifiersAfterHead(xs));
          }
          case "WITH-TYPES-SUBSET-OF" -> {
            yield new IsSubsetOf<>(this.identifiersAfterHead(xs));
          }
          case "WITH-TYPES-SUPERSET-OF" -> {
            yield new IsSupersetOf<>(this.identifiersAfterHead(xs));
          }
          case "WITH-TYPES-OVERLAPPING" -> {
            yield new IsOverlapping<>(this.identifiersAfterHead(xs));
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

  private Set<CATypeRecordIdentifier> identifiersAfterHead(
    final SListType xs)
    throws CAException
  {
    if (xs.size() <= 1) {
      return Set.of();
    }

    final var results = new HashSet<CATypeRecordIdentifier>();
    for (var index = 1; index < xs.size(); ++index) {
      switch (xs.get(index)) {
        case final SAtomType a -> {
          results.add(this.identifier(a, a.text()));
        }
        case final SListType ys -> {
          throw this.createParseError(ys);
        }
      }
    }
    return Set.copyOf(results);
  }

  private CATypeRecordIdentifier identifier(
    final SExpressionType e,
    final String text)
    throws CAException
  {
    final var segments = List.of(text.split(":"));
    if (segments.size() == 2) {
      return new CATypeRecordIdentifier(
        new RDottedName(segments.get(0)),
        new RDottedName(segments.get(1))
      );
    }
    throw this.createParseError(e);
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
    final CAComparisonSetType<CATypeRecordIdentifier> expression)
  {
    return switch (expression) {
      case final Anything<CATypeRecordIdentifier> e -> {
        yield new SSymbol(zero(), "with-any-type");
      }
      case final IsEqualTo<CATypeRecordIdentifier> e -> {
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
      case final IsNotEqualTo<CATypeRecordIdentifier> e -> {
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
      case final IsOverlapping<CATypeRecordIdentifier> e -> {
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
      case final IsSubsetOf<CATypeRecordIdentifier> e -> {
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
      case final IsSupersetOf<CATypeRecordIdentifier> e -> {
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
    final CATypeRecordIdentifier n)
  {
    return new SSymbol(zero(), n.toString());
  }

  @Override
  public SortedSet<CASyntaxRuleType> syntaxRules()
  {
    final var results = new TreeSet<CASyntaxRuleType>();

    results.add(
      this.ruleBranch(
        SYNTAX_TYPE_MATCH_NAME,
        List.of(
          SYNTAX_TYPE_MATCH_ANYTYPE_NAME,
          SYNTAX_TYPE_MATCH_EQUAL_TO_NAME,
          SYNTAX_TYPE_MATCH_NOT_EQUAL_TO_NAME,
          SYNTAX_TYPE_MATCH_SUPERSETOF_NAME,
          SYNTAX_TYPE_MATCH_SUBSETOF_NAME,
          SYNTAX_TYPE_MATCH_OVERLAPPING_NAME
        ),
        List.of(
          SYNTAX_TYPE_MATCH_EXAMPLE_0,
          SYNTAX_TYPE_MATCH_EXAMPLE_1,
          SYNTAX_TYPE_MATCH_EXAMPLE_2,
          SYNTAX_TYPE_MATCH_EXAMPLE_3,
          SYNTAX_TYPE_MATCH_EXAMPLE_4
        )
      )
    );

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_TYPE_MATCH_ANYTYPE_NAME,
        SYNTAX_TYPE_MATCH_ANYTYPE,
        List.of(SYNTAX_TYPE_MATCH_ANYTYPE)
      )
    );

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_TYPE_MATCH_EQUAL_TO_NAME,
        SYNTAX_TYPE_MATCH_EQUAL_TO,
        List.of(SYNTAX_TYPE_MATCH_EXAMPLE_0)
      )
    );

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_TYPE_MATCH_NOT_EQUAL_TO_NAME,
        SYNTAX_TYPE_MATCH_NOT_EQUAL_TO,
        List.of(SYNTAX_TYPE_MATCH_EXAMPLE_1)
      )
    );

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_TYPE_MATCH_SUPERSETOF_NAME,
        SYNTAX_TYPE_MATCH_SUPERSETOF_TO,
        List.of(SYNTAX_TYPE_MATCH_EXAMPLE_4)
      )
    );

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_TYPE_MATCH_SUBSETOF_NAME,
        SYNTAX_TYPE_MATCH_SUBSETOF_TO,
        List.of(SYNTAX_TYPE_MATCH_EXAMPLE_3)
      )
    );

    results.add(
      this.ruleLeafWithExamples(
        SYNTAX_TYPE_MATCH_OVERLAPPING_NAME,
        SYNTAX_TYPE_MATCH_OVERLAPPING_TO,
        List.of(SYNTAX_TYPE_MATCH_EXAMPLE_2)
      )
    );

    return Collections.unmodifiableSortedSet(results);
  }
}
