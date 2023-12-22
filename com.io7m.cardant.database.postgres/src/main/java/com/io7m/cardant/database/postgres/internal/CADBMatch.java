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

import com.io7m.cardant.database.postgres.internal.CADBMatch.QuerySetType.QuerySetCondition;
import com.io7m.cardant.database.postgres.internal.CADBMatch.QuerySetType.QuerySetIntersection;
import com.io7m.cardant.database.postgres.internal.CADBMatch.QuerySetType.QuerySetUnion;
import com.io7m.cardant.database.postgres.internal.enums.MetadataScalarBaseTypeT;
import com.io7m.cardant.model.CAItemLocationMatchType;
import com.io7m.cardant.model.CAItemLocationMatchType.CAItemLocationExact;
import com.io7m.cardant.model.CAItemLocationMatchType.CAItemLocationWithDescendants;
import com.io7m.cardant.model.CAItemLocationMatchType.CAItemLocationsAll;
import com.io7m.cardant.model.CAMetadataElementMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.IntegralMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.MonetaryMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.RealMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.TextMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.TimeMatchType;
import org.jooq.Condition;
import org.jooq.EnumType;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static com.io7m.cardant.database.postgres.internal.enums.MetadataScalarBaseTypeT.SCALAR_INTEGRAL;
import static com.io7m.cardant.database.postgres.internal.enums.MetadataScalarBaseTypeT.SCALAR_MONEY;
import static com.io7m.cardant.database.postgres.internal.enums.MetadataScalarBaseTypeT.SCALAR_REAL;
import static com.io7m.cardant.database.postgres.internal.enums.MetadataScalarBaseTypeT.SCALAR_TEXT;
import static com.io7m.cardant.database.postgres.internal.enums.MetadataScalarBaseTypeT.SCALAR_TIME;

/**
 * Translate match expressions to SQL queries.
 */

public final class CADBMatch
{
  private CADBMatch()
  {

  }

  /**
   * A set of queries.
   */

  sealed interface QuerySetType
  {
    /**
     * A simple query handled with a WHERE clause.
     *
     * @param condition The condition
     */

    record QuerySetCondition(
      Condition condition)
      implements QuerySetType
    {

    }

    /**
     * A pair of queries that are combined with a set union.
     *
     * @param q0 The left query
     * @param q1 The right query
     */

    record QuerySetUnion(
      QuerySetType q0,
      QuerySetType q1)
      implements QuerySetType
    {

    }

    /**
     * A pair of queries that are combined with a set intersection.
     *
     * @param q0 The left query
     * @param q1 The right query
     */

    record QuerySetIntersection(
      QuerySetType q0,
      QuerySetType q1)
      implements QuerySetType
    {

    }
  }

  record LocationFields(
    Field<UUID[]> locationId)
  {

  }

  static Condition ofLocationMatch(
    final LocationFields fields,
    final CAItemLocationMatchType match)
  {
    if (match instanceof CAItemLocationsAll) {
      return DSL.trueCondition();
    }

    if (match instanceof final CAItemLocationExact exact) {
      return fields.locationId.eq(DSL.array(exact.location().id()));
    }

    if (match instanceof final CAItemLocationWithDescendants descendants) {
      return DSL.condition(
        "? && (select array(select location_descendants(?)))",
        fields.locationId,
        descendants.location().id()
      );
    }

    throw new IllegalStateException(
      "Unrecognized match type: %s".formatted(match)
    );
  }

  record TypeFields(
    Field<String[]> types)
  {

  }

  record MetaFields(
    NameFields nameFields,
    Field<EnumType> metaValueType,
    Field<Long> metaValueInteger,
    Field<Double> metaValueReal,
    Field<OffsetDateTime> metaValueTime,
    Field<String> metaValueMoneyCurrency,
    Field<BigDecimal> metaValueMoney,
    Field<String> metaValueText,
    Field<?> metaValueTextSearch)
  {

  }

  record NameFields(
    Field<String> name,
    Field<?> nameSearch)
  {

  }

  static Condition ofMetaValueMatch(
    final MetaFields fields,
    final CAMetadataValueMatchType match)
  {
    if (match instanceof CAMetadataValueMatchType.AnyValue) {
      return DSL.trueCondition();
    }

    if (match instanceof final IntegralMatchType intM) {
      return ofMetaValueMatchIntegral(fields, intM);
    }

    if (match instanceof final MonetaryMatchType moneyM) {
      return ofMetaValueMatchMonetary(fields, moneyM);
    }

    if (match instanceof final RealMatchType realM) {
      return ofMetaValueMatchReal(fields, realM);
    }

    if (match instanceof final TextMatchType textM) {
      return ofMetaValueMatchText(fields, textM);
    }

    if (match instanceof final TimeMatchType timeM) {
      return ofMetaValueMatchTime(fields, timeM);
    }

    throw new IllegalStateException(
      "Unrecognized match type: %s".formatted(match)
    );
  }

  static QuerySetType ofMetaElementMatch(
    final MetaFields fields,
    final CAMetadataElementMatchType match)
  {
    if (match instanceof final CAMetadataElementMatchType.And and) {
      return new QuerySetIntersection(
        ofMetaElementMatch(fields, and.e0()),
        ofMetaElementMatch(fields, and.e1())
      );
    }

    if (match instanceof final CAMetadataElementMatchType.Or or) {
      return new QuerySetUnion(
        ofMetaElementMatch(fields, or.e0()),
        ofMetaElementMatch(fields, or.e1())
      );
    }

    if (match instanceof final CAMetadataElementMatchType.Specific specific) {
      return new QuerySetCondition(DSL.and(
        CADBComparisons.createFuzzyMatchQuery(
          specific.name(),
          fields.nameFields.name,
          fields.nameFields.nameSearch.getName()
        ),
        ofMetaValueMatch(fields, specific.value())
      ));
    }

    throw new IllegalStateException(
      "Unrecognized match type: %s".formatted(match)
    );
  }

  private static Condition ofMetaValueMatchTime(
    final MetaFields fields,
    final TimeMatchType match)
  {
    final var isType =
      createTypeCondition(fields, SCALAR_TIME);

    if (match instanceof final TimeMatchType.WithinRange withinRange) {
      return DSL.and(
        isType,
        DSL.and(
          fields.metaValueTime.ge(withinRange.lower()),
          fields.metaValueTime.le(withinRange.upper())
        )
      );
    }

    throw new IllegalStateException(
      "Unrecognized match type: %s".formatted(match)
    );
  }

  private static Condition ofMetaValueMatchText(
    final MetaFields fields,
    final TextMatchType match)
  {
    final var isType =
      createTypeCondition(fields, SCALAR_TEXT);

    if (match instanceof final TextMatchType.ExactTextValue exact) {
      return DSL.and(
        isType,
        DSL.and(
          fields.metaValueText.eq(exact.text())
        )
      );
    }

    if (match instanceof final TextMatchType.Search search) {
      return DSL.and(
        isType,
        DSL.condition(
          "? @@ websearch_to_tsquery(?)",
          fields.metaValueTextSearch,
          DSL.inline(search.query())
        )
      );
    }

    throw new IllegalStateException(
      "Unrecognized match type: %s".formatted(match)
    );
  }

  private static Condition createTypeCondition(
    final MetaFields fields,
    final MetadataScalarBaseTypeT t)
  {
    return DSL.condition(
      "? = CAST (? AS metadata_scalar_base_type_t)",
      fields.metaValueType,
      t.getLiteral()
    );
  }

  private static Condition ofMetaValueMatchReal(
    final MetaFields fields,
    final RealMatchType match)
  {
    final var isType =
      createTypeCondition(fields, SCALAR_REAL);

    if (match instanceof final RealMatchType.WithinRange withinRange) {
      return DSL.and(
        isType,
        DSL.and(
          fields.metaValueReal.ge(Double.valueOf(withinRange.lower())),
          fields.metaValueReal.le(Double.valueOf(withinRange.upper()))
        )
      );
    }

    throw new IllegalStateException(
      "Unrecognized match type: %s".formatted(match)
    );
  }

  private static Condition ofMetaValueMatchMonetary(
    final MetaFields fields,
    final MonetaryMatchType match)
  {
    final var isType =
      createTypeCondition(fields, SCALAR_MONEY);

    if (match instanceof final MonetaryMatchType.WithCurrency withCurrency) {
      return DSL.and(
        isType,
        fields.metaValueMoneyCurrency.eq(withCurrency.currency().getCode())
      );
    }

    if (match instanceof final MonetaryMatchType.WithinRange withinRange) {
      return DSL.and(
        isType,
        DSL.and(
          fields.metaValueMoney.ge(withinRange.lower()),
          fields.metaValueMoney.le(withinRange.upper())
        )
      );
    }

    throw new IllegalStateException(
      "Unrecognized match type: %s".formatted(match)
    );
  }

  private static Condition ofMetaValueMatchIntegral(
    final MetaFields fields,
    final IntegralMatchType match)
  {
    final var isType =
      createTypeCondition(fields, SCALAR_INTEGRAL);

    if (match instanceof final IntegralMatchType.WithinRange withinRange) {
      return DSL.and(
        isType,
        DSL.and(
          fields.metaValueInteger.ge(Long.valueOf(withinRange.lower())),
          fields.metaValueInteger.le(Long.valueOf(withinRange.upper()))
        )
      );
    }

    throw new IllegalStateException(
      "Unrecognized match type: %s".formatted(match)
    );
  }
}
