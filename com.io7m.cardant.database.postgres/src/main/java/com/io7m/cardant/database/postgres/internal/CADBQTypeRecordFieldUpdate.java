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
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeRecordFieldUpdateType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CATypeRecordFieldUpdate;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_RECORDS;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_RECORD_FIELDS;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_SCALAR;
import static com.io7m.cardant.strings.CAStringConstants.FIELD_NAME;
import static com.io7m.cardant.strings.CAStringConstants.FIELD_TYPE;
import static com.io7m.cardant.strings.CAStringConstants.TYPE;

/**
 * Remove a record field.
 */

public final class CADBQTypeRecordFieldUpdate
  extends CADBQAbstract<CATypeRecordFieldUpdate, CADatabaseUnit>
  implements TypeRecordFieldUpdateType
{
  private static final Service<
    CATypeRecordFieldUpdate,
    CADatabaseUnit,
    TypeRecordFieldUpdateType> SERVICE =
    new Service<>(
      TypeRecordFieldUpdateType.class,
      CADBQTypeRecordFieldUpdate::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQTypeRecordFieldUpdate(
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
  protected CADatabaseUnit onExecute(
    final DSLContext context,
    final CATypeRecordFieldUpdate update)
    throws CADatabaseException
  {
    final var typeField =
      update.typeField();
    final var fieldName =
      typeField.name().value();
    final var scalarType =
      typeField.type();

    final var typeName = update.typeName().value();
    this.setAttribute(TYPE, typeName);
    this.setAttribute(FIELD_NAME, fieldName);
    this.setAttribute(FIELD_TYPE, typeField.type().name().value());

    final var recordTypeId =
      context.select(METADATA_TYPES_RECORDS.MTR_ID)
        .from(METADATA_TYPES_RECORDS)
        .where(METADATA_TYPES_RECORDS.MTR_NAME.eq(typeName));

    final var scalarTypeId =
      context.select(METADATA_TYPES_SCALAR.MTS_ID)
        .from(METADATA_TYPES_SCALAR)
        .where(METADATA_TYPES_SCALAR.MTS_NAME.eq(scalarType.name().value()));

    context.insertInto(METADATA_TYPES_RECORD_FIELDS)
      .set(METADATA_TYPES_RECORD_FIELDS.MTRF_NAME, fieldName)
      .set(METADATA_TYPES_RECORD_FIELDS.MTRF_DECLARATION, recordTypeId)
      .set(
        METADATA_TYPES_RECORD_FIELDS.MTRF_DESCRIPTION,
        typeField.description())
      .set(METADATA_TYPES_RECORD_FIELDS.MTRF_REQUIRED, typeField.isRequired())
      .set(METADATA_TYPES_RECORD_FIELDS.MTRF_SCALAR_TYPE, scalarTypeId)
      .onConflictOnConstraint(DSL.name(
        "metadata_types_record_fields_primary_key"))
      .doUpdate()
      .set(METADATA_TYPES_RECORD_FIELDS.MTRF_NAME, fieldName)
      .set(METADATA_TYPES_RECORD_FIELDS.MTRF_DECLARATION, recordTypeId)
      .set(
        METADATA_TYPES_RECORD_FIELDS.MTRF_DESCRIPTION,
        typeField.description())
      .set(METADATA_TYPES_RECORD_FIELDS.MTRF_REQUIRED, typeField.isRequired())
      .set(METADATA_TYPES_RECORD_FIELDS.MTRF_SCALAR_TYPE, scalarTypeId)
      .execute();

    return CADatabaseUnit.UNIT;
  }
}
