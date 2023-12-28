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

import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeDeclarationGetMultipleType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.model.CATypeRecord;
import com.io7m.lanark.core.RDottedName;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_RECORDS;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_RECORD_FIELDS;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_SCALAR;

/**
 * Retrieve multiple type declarations.
 */

public final class CADBQTypeDeclGetMultiple
  extends CADBQAbstract<Set<RDottedName>, List<CATypeRecord>>
  implements TypeDeclarationGetMultipleType
{
  private static final Service<Set<RDottedName>, List<CATypeRecord>, TypeDeclarationGetMultipleType> SERVICE =
    new Service<>(TypeDeclarationGetMultipleType.class, CADBQTypeDeclGetMultiple::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQTypeDeclGetMultiple(
    final CADatabaseTransaction transaction)
  {
    super(transaction);
  }

  /**
   * @return A query provider
   */

  public static CADBQueryProviderType provider()
  {
    return () -> SERVICE;
  }

  @Override
  protected List<CATypeRecord> onExecute(
    final DSLContext context,
    final Set<RDottedName> names)
    throws CADatabaseException
  {
    final var nameSet =
      names.stream()
        .map(RDottedName::value)
        .collect(Collectors.toUnmodifiableSet());

    final var query =
      context.select(
          METADATA_TYPES_RECORDS.MTR_NAME,
          METADATA_TYPES_RECORDS.MTR_DESCRIPTION,
          METADATA_TYPES_RECORD_FIELDS.MTRF_NAME,
          METADATA_TYPES_RECORD_FIELDS.MTRF_DESCRIPTION,
          METADATA_TYPES_RECORD_FIELDS.MTRF_REQUIRED,
          METADATA_TYPES_SCALAR.MTS_DESCRIPTION,
          METADATA_TYPES_SCALAR.MTS_NAME,
          METADATA_TYPES_SCALAR.MTS_BASE_TYPE,
          METADATA_TYPES_SCALAR.MTS_INTEGRAL_LOWER,
          METADATA_TYPES_SCALAR.MTS_INTEGRAL_UPPER,
          METADATA_TYPES_SCALAR.MTS_MONEY_LOWER,
          METADATA_TYPES_SCALAR.MTS_MONEY_UPPER,
          METADATA_TYPES_SCALAR.MTS_REAL_LOWER,
          METADATA_TYPES_SCALAR.MTS_REAL_UPPER,
          METADATA_TYPES_SCALAR.MTS_TEXT_PATTERN,
          METADATA_TYPES_SCALAR.MTS_TIME_LOWER,
          METADATA_TYPES_SCALAR.MTS_TIME_UPPER)
        .from(METADATA_TYPES_RECORDS, METADATA_TYPES_RECORD_FIELDS)
        .join(METADATA_TYPES_SCALAR)
        .on(METADATA_TYPES_SCALAR.MTS_ID.eq(METADATA_TYPES_RECORD_FIELDS.MTRF_SCALAR_TYPE))
        .where(METADATA_TYPES_RECORDS.MTR_NAME.in(nameSet))
        .orderBy(METADATA_TYPES_RECORDS.MTR_NAME.asc());

    final var records =
      query.fetch();

    final Map<RDottedName, Map<RDottedName, CATypeField>> fieldsByTypeName =
      new HashMap<>(names.size());
    final Map<RDottedName, String> descriptionsByTypeName =
      new HashMap<>(names.size());

    for (final var record : records) {
      final var typeName =
        new RDottedName(record.get(METADATA_TYPES_RECORDS.MTR_NAME));
      final var description =
        record.get(METADATA_TYPES_RECORDS.MTR_DESCRIPTION);

      descriptionsByTypeName.put(typeName, description);

      final var scalarType =
        CADBQTypeScalarGet.mapRecord(record);

      final var field =
        new CATypeField(
          new RDottedName(record.get(METADATA_TYPES_RECORD_FIELDS.MTRF_NAME)),
          record.get(METADATA_TYPES_RECORD_FIELDS.MTRF_DESCRIPTION),
          scalarType,
          record.get(METADATA_TYPES_RECORD_FIELDS.MTRF_REQUIRED).booleanValue()
        );

      var fields = fieldsByTypeName.get(typeName);
      if (fields == null) {
        fields = new HashMap<>(8);
      }
      fields.put(field.name(), field);
      fieldsByTypeName.put(typeName, fields);
    }

    final var results = new ArrayList<CATypeRecord>(names.size());
    for (final var typeName : names) {
      final var description =
        descriptionsByTypeName.get(typeName);
      final var fields =
        fieldsByTypeName.get(typeName);
      results.add(new CATypeRecord(typeName, description, fields));
    }

    results.sort(Comparator.comparing(CATypeRecord::name));
    return List.copyOf(results);
  }
}
