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
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeRecordGetType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.model.CATypeRecord;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.lanark.core.RDottedName;
import org.jooq.DSLContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.io7m.cardant.database.postgres.internal.CADBQTypeScalarGet.packageIdentifier;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_RECORDS;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_RECORD_FIELDS;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_SCALAR;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_PACKAGES;

/**
 * Retrieve a type declaration.
 */

public final class CADBQTypeRecordGet
  extends CADBQAbstract<RDottedName, Optional<CATypeRecord>>
  implements TypeRecordGetType
{
  private static final Service<RDottedName, Optional<CATypeRecord>, TypeRecordGetType> SERVICE =
    new Service<>(TypeRecordGetType.class, CADBQTypeRecordGet::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQTypeRecordGet(
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
  protected Optional<CATypeRecord> onExecute(
    final DSLContext context,
    final RDottedName name)
    throws CADatabaseException
  {
    final var typeName = name.value();

    final var query =
      context.select(
          METADATA_TYPES_RECORDS.MTR_NAME,
          METADATA_TYPES_RECORDS.MTR_DESCRIPTION,
          METADATA_TYPES_RECORD_FIELDS.MTRF_NAME,
          METADATA_TYPES_RECORD_FIELDS.MTRF_DESCRIPTION,
          METADATA_TYPES_RECORD_FIELDS.MTRF_REQUIRED,
          METADATA_TYPES_SCALAR.MTS_NAME,
          METADATA_TYPES_SCALAR.MTS_DESCRIPTION,
          METADATA_TYPES_SCALAR.MTS_BASE_TYPE,
          METADATA_TYPES_SCALAR.MTS_INTEGRAL_LOWER,
          METADATA_TYPES_SCALAR.MTS_INTEGRAL_UPPER,
          METADATA_TYPES_SCALAR.MTS_REAL_LOWER,
          METADATA_TYPES_SCALAR.MTS_REAL_UPPER,
          METADATA_TYPES_SCALAR.MTS_MONEY_LOWER,
          METADATA_TYPES_SCALAR.MTS_MONEY_UPPER,
          METADATA_TYPES_SCALAR.MTS_TEXT_PATTERN,
          METADATA_TYPES_SCALAR.MTS_TIME_LOWER,
          METADATA_TYPES_SCALAR.MTS_TIME_UPPER,
          METADATA_TYPE_PACKAGES.MTP_NAME,
          METADATA_TYPE_PACKAGES.MTP_DESCRIPTION,
          METADATA_TYPE_PACKAGES.MTP_VERSION_MAJOR,
          METADATA_TYPE_PACKAGES.MTP_VERSION_MINOR,
          METADATA_TYPE_PACKAGES.MTP_VERSION_PATCH,
          METADATA_TYPE_PACKAGES.MTP_VERSION_QUALIFIER
        ).from(METADATA_TYPES_RECORDS)
        .leftOuterJoin(METADATA_TYPES_RECORD_FIELDS)
        .on(METADATA_TYPES_RECORDS.MTR_ID.eq(METADATA_TYPES_RECORD_FIELDS.MTRF_DECLARATION))
        .leftOuterJoin(METADATA_TYPES_SCALAR)
        .on(METADATA_TYPES_SCALAR.MTS_ID.eq(METADATA_TYPES_RECORD_FIELDS.MTRF_SCALAR_TYPE))
        .leftOuterJoin(METADATA_TYPE_PACKAGES)
        .on(METADATA_TYPE_PACKAGES.MTP_ID.eq(METADATA_TYPES_RECORDS.MTR_PACKAGE))
        .where(METADATA_TYPES_RECORDS.MTR_NAME.eq(typeName));

    final var records =
      query.fetch();

    if (records.isEmpty()) {
      return Optional.empty();
    }

    final Map<RDottedName, CATypeField> fields =
      new HashMap<>(8);

    String description = "";
    CATypePackageIdentifier identifier = null;

    for (final var record : records) {
      description =
        record.get(METADATA_TYPES_RECORDS.MTR_DESCRIPTION);
      identifier =
        packageIdentifier(record);

      final var scalarTypeNameText =
        record.get(METADATA_TYPES_SCALAR.MTS_NAME);

      if (scalarTypeNameText != null) {
        final var scalarType =
          CADBQTypeScalarGet.mapRecord(record);

        final var field =
          new CATypeField(
            new RDottedName(record.get(METADATA_TYPES_RECORD_FIELDS.MTRF_NAME)),
            record.get(METADATA_TYPES_RECORD_FIELDS.MTRF_DESCRIPTION),
            scalarType,
            record.get(METADATA_TYPES_RECORD_FIELDS.MTRF_REQUIRED).booleanValue()
          );

        fields.put(field.name(), field);
      }
    }

    return Optional.of(
      new CATypeRecord(
        identifier,
        name,
        description,
        fields
      )
    );
  }
}
