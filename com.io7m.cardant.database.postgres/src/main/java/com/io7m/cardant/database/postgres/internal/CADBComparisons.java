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


package com.io7m.cardant.database.postgres.internal;


import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.cardant.model.comparisons.CAComparisonFuzzyType;
import com.io7m.cardant.model.comparisons.CAComparisonSetType;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.TableField;
import org.jooq.impl.DSL;

/**
 * Functions to perform comparisons over fields/columns.
 */

public final class CADBComparisons
{
  private CADBComparisons()
  {

  }

  /**
   * Create a fuzzy match expression.
   *
   * @param query       The query
   * @param fieldExact  The "exact" field
   * @param fieldSearch The search field
   * @param <T>         The type of compared values
   *
   * @return A fuzzy match condition
   */

  public static <T> Condition createFuzzyMatchQuery(
    final CAComparisonFuzzyType<T> query,
    final Field<T> fieldExact,
    final String fieldSearch)
  {
    return switch (query) {
      case final CAComparisonFuzzyType.Anything<T> e -> {
        yield DSL.trueCondition();
      }
      case final CAComparisonFuzzyType.IsEqualTo<T> isEqualTo -> {
        yield fieldExact.equal(isEqualTo.value());
      }
      case final CAComparisonFuzzyType.IsNotEqualTo<T> isNotEqualTo -> {
        yield fieldExact.notEqual(isNotEqualTo.value());
      }
      case final CAComparisonFuzzyType.IsSimilarTo<T> isSimilarTo -> {
        yield DSL.condition(
          "%s @@ websearch_to_tsquery(?)".formatted(fieldSearch),
          isSimilarTo.value()
        );
      }
      case final CAComparisonFuzzyType.IsNotSimilarTo<T> isNotSimilarTo -> {
        yield DSL.condition(
          "NOT (%s @@ websearch_to_tsquery(?))".formatted(fieldSearch),
          isNotSimilarTo.value()
        );
      }
    };
  }

  /**
   * Create an exact match expression.
   *
   * @param query      The query
   * @param fieldExact The "exact" field
   * @param <T>        The type of compared values
   *
   * @return An exact match condition
   */

  public static <T> Condition createExactMatchQuery(
    final CAComparisonExactType<T> query,
    final TableField<org.jooq.Record, T> fieldExact)
  {
    return switch (query) {
      case final CAComparisonExactType.Anything<T> e -> {
        yield DSL.trueCondition();
      }
      case final CAComparisonExactType.IsEqualTo<T> isEqualTo -> {
        yield fieldExact.equal(isEqualTo.value());
      }
      case final CAComparisonExactType.IsNotEqualTo<T> isNotEqualTo -> {
        yield fieldExact.notEqual(isNotEqualTo.value());
      }
    };
  }

  /**
   * Create a set match expression.
   *
   * @param query The query
   * @param field The array-typed field
   *
   * @return An exact match condition
   */

  public static Condition createSetMatchQueryString(
    final CAComparisonSetType<String> query,
    final TableField<org.jooq.Record, String[]> field)
  {
    switch (query) {
      case CAComparisonSetType.Anything<String> e -> {
        return DSL.trueCondition();
      }
      case final CAComparisonSetType.IsEqualTo<String> isEqualTo -> {
        final var set = isEqualTo.value();
        final var values = new String[set.size()];
        set.toArray(values);

        return DSL.condition(
          "(cast (? as text[]) <@ cast (? as text[])) AND (cast (? as text[]) @> cast (? as text[]))",
          field,
          DSL.array(values),
          field,
          DSL.array(values)
        );
      }
      case final CAComparisonSetType.IsNotEqualTo<String> isNotEqualTo -> {
        final var set = isNotEqualTo.value();
        final var values = new String[set.size()];
        set.toArray(values);

        return DSL.condition(
          "NOT ((cast (? as text[]) <@ cast (? as text[])) AND (cast (? as text[]) @> cast (? as text[])))",
          field,
          DSL.array(values),
          field,
          DSL.array(values)
        );
      }
      case final CAComparisonSetType.IsSubsetOf<String> isSubsetOf -> {
        final var set = isSubsetOf.value();
        final var values = new String[set.size()];
        set.toArray(values);

        return DSL.condition(
          "cast (? as text[]) <@ cast (? as text[])",
          field,
          DSL.array(values)
        );
      }
      case final CAComparisonSetType.IsSupersetOf<String> isSupersetOf -> {
        final var set = isSupersetOf.value();
        final var values = new String[set.size()];
        set.toArray(values);

        return DSL.condition(
          "cast (? as text[]) @> cast (? as text[])",
          field,
          DSL.array(values)
        );
      }
      case final CAComparisonSetType.IsOverlapping<String> isOverlapping -> {
        final var set = isOverlapping.value();
        final var values = new String[set.size()];
        set.toArray(values);

        return DSL.condition(
          "cast (? as text[]) && cast (? as text[])",
          field,
          DSL.array(values)
        );
      }
    }
  }

  /**
   * Create a set match expression.
   *
   * @param query The query
   * @param field The array-typed field
   *
   * @return An exact match condition
   */

  public static Condition createSetMatchQueryLong(
    final CAComparisonSetType<Long> query,
    final TableField<org.jooq.Record, Long[]> field)
  {
    switch (query) {
      case CAComparisonSetType.Anything<Long> e -> {
        return DSL.trueCondition();
      }

      case final CAComparisonSetType.IsEqualTo<Long> isEqualTo -> {
        final var set = isEqualTo.value();
        final var values = new Long[set.size()];
        set.toArray(values);

        return DSL.condition(
          "(? <@ cast (? as bigint[])) AND (? @> cast (? as bigint[]))",
          field,
          DSL.array(values),
          field,
          DSL.array(values)
        );
      }

      case final CAComparisonSetType.IsNotEqualTo<Long> isNotEqualTo -> {
        final var set = isNotEqualTo.value();
        final var values = new Long[set.size()];
        set.toArray(values);

        return DSL.condition(
          "NOT ((? <@ cast (? as bigint[])) AND (? @> cast (? as bigint[])))",
          field,
          DSL.array(values),
          field,
          DSL.array(values)
        );
      }

      case final CAComparisonSetType.IsSubsetOf<Long> isSubsetOf -> {
        final var set = isSubsetOf.value();
        final var values = new Long[set.size()];
        set.toArray(values);

        return DSL.condition(
          "? <@ cast (? as bigint[])",
          field,
          DSL.array(values)
        );
      }

      case final CAComparisonSetType.IsSupersetOf<Long> isSupersetOf -> {
        final var set = isSupersetOf.value();
        final var values = new Long[set.size()];
        set.toArray(values);

        return DSL.condition(
          "? @> cast (? as bigint[])",
          field,
          DSL.array(values)
        );
      }

      case final CAComparisonSetType.IsOverlapping<Long> isOverlapping -> {
        final var set = isOverlapping.value();
        final var values = new Long[set.size()];
        set.toArray(values);

        return DSL.condition(
          "? && cast (? as bigint[])",
          field,
          DSL.array(values)
        );
      }
    }
  }
}
