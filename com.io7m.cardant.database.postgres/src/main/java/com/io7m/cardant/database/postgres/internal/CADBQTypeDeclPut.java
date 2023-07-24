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
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeDeclarationPutType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CATypeDeclaration;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.impl.DSL;

import java.util.ArrayList;

import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_RECORDS;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_RECORD_FIELDS;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_SCALAR;
import static com.io7m.cardant.strings.CAStringConstants.TYPE;

/**
 * Create or update a type declaration.
 */

public final class CADBQTypeDeclPut
  extends CADBQAbstract<CATypeDeclaration, CADatabaseUnit>
  implements TypeDeclarationPutType
{
  private static final Service<CATypeDeclaration, CADatabaseUnit, TypeDeclarationPutType> SERVICE =
    new Service<>(TypeDeclarationPutType.class, CADBQTypeDeclPut::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQTypeDeclPut(
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
    final CATypeDeclaration declaration)
    throws CADatabaseException
  {
    final var typeName =
      declaration.name().value();
    final var description =
      declaration.description();

    this.setAttribute(TYPE, typeName);

    final var typeId =
      context.insertInto(METADATA_TYPES_RECORDS)
        .set(METADATA_TYPES_RECORDS.MTR_NAME, typeName)
        .set(METADATA_TYPES_RECORDS.MTR_DESCRIPTION, description)
        .onConflict(METADATA_TYPES_RECORDS.MTR_NAME)
        .doUpdate()
        .set(METADATA_TYPES_RECORDS.MTR_NAME, typeName)
        .set(METADATA_TYPES_RECORDS.MTR_DESCRIPTION, description)
        .returning(METADATA_TYPES_RECORDS.MTR_ID)
        .execute();

    final var batches =
      new ArrayList<Query>(declaration.fields().size());

    /*
     * Delete the existing fields associated with this type.
     */

    final var fieldsJoin =
      METADATA_TYPES_RECORD_FIELDS.join(METADATA_TYPES_RECORDS)
          .on(METADATA_TYPES_RECORD_FIELDS.MTRF_DECLARATION.eq(METADATA_TYPES_RECORDS.MTR_ID))
            .where(METADATA_TYPES_RECORDS.MTR_NAME.eq(typeName));

    final var deleteQuery =
      DSL.deleteFrom(METADATA_TYPES_RECORD_FIELDS)
        .whereExists(
          DSL.select(
            METADATA_TYPES_RECORD_FIELDS.MTRF_DECLARATION,
            METADATA_TYPES_RECORD_FIELDS.MTRF_NAME
          ).from(fieldsJoin));

    batches.add(deleteQuery);

    /*
     * Add new fields.
     */

    for (final var entry : declaration.fields().entrySet()) {
      final var field =
        entry.getValue();
      final var scalarType =
        field.type();

      final var findTypeId =
        DSL.select(METADATA_TYPES_SCALAR.MTS_ID)
          .from(METADATA_TYPES_SCALAR)
          .where(METADATA_TYPES_SCALAR.MTS_NAME.eq(scalarType.name().value()));

      batches.add(
        DSL.insertInto(METADATA_TYPES_RECORD_FIELDS)
          .set(METADATA_TYPES_RECORD_FIELDS.MTRF_NAME, field.name().value())
          .set(METADATA_TYPES_RECORD_FIELDS.MTRF_DECLARATION, typeId)
          .set(METADATA_TYPES_RECORD_FIELDS.MTRF_DESCRIPTION, field.description())
          .set(METADATA_TYPES_RECORD_FIELDS.MTRF_REQUIRED, field.isRequired())
          .set(METADATA_TYPES_RECORD_FIELDS.MTRF_SCALAR_TYPE, findTypeId)
          .onConflictOnConstraint(DSL.name("metadata_types_record_fields_primary_key"))
          .doUpdate()
          .set(METADATA_TYPES_RECORD_FIELDS.MTRF_NAME, field.name().value())
          .set(METADATA_TYPES_RECORD_FIELDS.MTRF_DECLARATION, typeId)
          .set(METADATA_TYPES_RECORD_FIELDS.MTRF_DESCRIPTION, field.description())
          .set(METADATA_TYPES_RECORD_FIELDS.MTRF_REQUIRED, field.isRequired())
          .set(METADATA_TYPES_RECORD_FIELDS.MTRF_SCALAR_TYPE, findTypeId)
      );
    }

    context.batch(batches).execute();
    return CADatabaseUnit.UNIT;
  }
}
