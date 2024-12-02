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

import com.io7m.cardant.database.api.CADatabaseLanguage;
import com.io7m.cardant.database.postgres.internal.CADBMatch.QuerySetType.QuerySetCondition;
import com.io7m.cardant.database.postgres.internal.CADBMatch.QuerySetType.QuerySetIntersection;
import com.io7m.cardant.database.postgres.internal.CADBMatch.QuerySetType.QuerySetUnion;
import com.io7m.cardant.database.postgres.internal.enums.MetadataScalarBaseTypeT;
import com.io7m.cardant.model.CAItemSerial;
import com.io7m.cardant.model.CALocationMatchType;
import com.io7m.cardant.model.CALocationMatchType.CALocationExact;
import com.io7m.cardant.model.CALocationMatchType.CALocationWithDescendants;
import com.io7m.cardant.model.CALocationMatchType.CALocationsAll;
import com.io7m.cardant.model.CAMetadataElementMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.IntegralMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.MonetaryMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.RealMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.TextMatchType;
import com.io7m.cardant.model.CAMetadataValueMatchType.TimeMatchType;
import com.io7m.cardant.model.comparisons.CAComparisonExactType;
import com.io7m.lanark.core.RDottedName;
import org.jooq.Condition;
import org.jooq.EnumType;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.TableField;
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

  static Condition ofSerialMatch(
    final Field<String[]> fields,
    final CAComparisonExactType<CAItemSerial> match)
  {
    return switch (match) {
      case final CAComparisonExactType.Anything<CAItemSerial> e -> {
        yield DSL.trueCondition();
      }
      case final CAComparisonExactType.IsEqualTo<CAItemSerial> e -> {
        yield DSL.condition("? && ARRAY[?]", fields, e.value().value());
      }
      case final CAComparisonExactType.IsNotEqualTo<CAItemSerial> e -> {
        yield DSL.condition("NOT (? && ARRAY[?])", fields, e.value().value());
      }
    };
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
    final CALocationMatchType match)
  {
    return switch (match) {
      case final CALocationsAll e -> {
        yield DSL.trueCondition();
      }
      case final CALocationExact exact -> {
        yield DSL.condition(
          "ARRAY[?] && ?",
          exact.location().id(),
          fields.locationId
        );
      }
      case final CALocationWithDescendants descendants -> {
        yield DSL.condition(
          "? && (SELECT ARRAY(SELECT location_descendants(?)))",
          fields.locationId,
          descendants.location().id()
        );
      }
    };
  }

  record MetaFields(
    TableField<Record, String> packageNameField,
    TableField<Record, String> typeNameField,
    TableField<Record, String> fieldNameField,
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

  static Condition ofMetaValueMatch(
    final CADatabaseLanguage language,
    final MetaFields fields,
    final CAMetadataValueMatchType match)
  {
    return switch (match) {
      case final CAMetadataValueMatchType.AnyValue e -> {
        yield DSL.trueCondition();
      }
      case final IntegralMatchType intM -> {
        yield ofMetaValueMatchIntegral(fields, intM);
      }
      case final MonetaryMatchType moneyM -> {
        yield ofMetaValueMatchMonetary(fields, moneyM);
      }
      case final RealMatchType realM -> {
        yield ofMetaValueMatchReal(fields, realM);
      }
      case final TextMatchType textM -> {
        yield ofMetaValueMatchText(language, fields, textM);
      }
      case final TimeMatchType timeM -> {
        yield ofMetaValueMatchTime(fields, timeM);
      }
    };
  }

  static QuerySetType ofMetaElementMatch(
    final CADatabaseLanguage language,
    final MetaFields fields,
    final CAMetadataElementMatchType match)
  {
    return switch (match) {
      case final CAMetadataElementMatchType.And and -> {
        yield new QuerySetIntersection(
          ofMetaElementMatch(language, fields, and.e0()),
          ofMetaElementMatch(language, fields, and.e1())
        );
      }
      case final CAMetadataElementMatchType.Or or -> {
        yield new QuerySetUnion(
          ofMetaElementMatch(language, fields, or.e0()),
          ofMetaElementMatch(language, fields, or.e1())
        );
      }
      case final CAMetadataElementMatchType.Specific specific -> {
        yield new QuerySetCondition(DSL.and(
          CADBComparisons.createExactMatchQuery(
            specific.packageName().map(RDottedName::value),
            fields.packageNameField
          ),
          CADBComparisons.createExactMatchQuery(
            specific.typeName(),
            fields.typeNameField
          ),
          CADBComparisons.createExactMatchQuery(
            specific.fieldName(),
            fields.fieldNameField
          ),
          ofMetaValueMatch(language, fields, specific.value())
        ));
      }
    };
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
    final CADatabaseLanguage language,
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
          "? @@ websearch_to_tsquery('%s', ?)".formatted(language),
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
