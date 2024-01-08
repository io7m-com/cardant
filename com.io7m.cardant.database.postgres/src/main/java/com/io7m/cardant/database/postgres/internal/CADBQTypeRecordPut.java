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
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeRecordPutType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.model.CATypeRecord;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventAdd.auditEvent;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_RECORDS;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_RECORD_FIELDS;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_SCALAR;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_PACKAGES;
import static com.io7m.cardant.strings.CAStringConstants.ERROR_NONEXISTENT;
import static com.io7m.cardant.strings.CAStringConstants.PACKAGE;
import static com.io7m.cardant.strings.CAStringConstants.PACKAGE_VERSION;
import static com.io7m.cardant.strings.CAStringConstants.TYPE;

/**
 * Create or update a type declaration.
 */

public final class CADBQTypeRecordPut
  extends CADBQAbstract<CATypeRecord, CADatabaseUnit>
  implements TypeRecordPutType
{
  private static final Service<CATypeRecord, CADatabaseUnit, TypeRecordPutType> SERVICE =
    new Service<>(TypeRecordPutType.class, CADBQTypeRecordPut::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQTypeRecordPut(
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
    final CATypeRecord declaration)
    throws CADatabaseException
  {
    final var transaction =
      this.transaction();

    final var packageIdentifier =
      declaration.packageIdentifier();

    this.setAttribute(PACKAGE, packageIdentifier.name().value());
    this.setAttribute(PACKAGE_VERSION, packageIdentifier.version().toString());
    this.setAttribute(TYPE, declaration.name().value());

    final var match =
      CADBTypePackages.packageMatch(packageIdentifier);
    final var id =
      context.select(METADATA_TYPE_PACKAGES.MTP_ID)
        .from(METADATA_TYPE_PACKAGES)
        .where(match)
        .fetchOptional(METADATA_TYPE_PACKAGES.MTP_ID)
        .orElseThrow(this::errorNonexistent)
        .intValue();

    final var batches =
      putTypeRecord(transaction, context, id, declaration);

    context.batch(batches).execute();
    return CADatabaseUnit.UNIT;
  }

  private CADatabaseException errorNonexistent()
  {
    return new CADatabaseException(
      this.local(ERROR_NONEXISTENT),
      CAStandardErrorCodes.errorNonexistent(),
      this.attributes(),
      Optional.empty()
    );
  }

  static ArrayList<Query> putTypeRecord(
    final CADatabaseTransaction transaction,
    final DSLContext context,
    final int packageId,
    final CATypeRecord declaration)
  {
    final var typeName =
      declaration.name().value();
    final var description =
      declaration.description();

    final var existingTypeId =
      context.select(METADATA_TYPES_RECORDS.MTR_ID)
        .from(METADATA_TYPES_RECORDS)
        .where(METADATA_TYPES_RECORDS.MTR_NAME.eq(typeName))
        .fetchOptional(METADATA_TYPES_RECORDS.MTR_ID);

    final int typeId;
    if (existingTypeId.isPresent()) {
      final var typeIdB = existingTypeId.get();
      typeId = typeIdB.intValue();
      context.update(METADATA_TYPES_RECORDS)
        .set(METADATA_TYPES_RECORDS.MTR_NAME, typeName)
        .set(METADATA_TYPES_RECORDS.MTR_PACKAGE, packageId)
        .set(METADATA_TYPES_RECORDS.MTR_DESCRIPTION, description)
        .where(METADATA_TYPES_RECORDS.MTR_ID.eq(typeIdB))
        .execute();
    } else {
      typeId = context.insertInto(METADATA_TYPES_RECORDS)
        .set(METADATA_TYPES_RECORDS.MTR_NAME, typeName)
        .set(METADATA_TYPES_RECORDS.MTR_PACKAGE, packageId)
        .set(METADATA_TYPES_RECORDS.MTR_DESCRIPTION, description)
        .returning(METADATA_TYPES_RECORDS.MTR_ID)
        .fetchOne(METADATA_TYPES_RECORDS.MTR_ID)
        .intValue();
    }

    final var batches =
      new ArrayList<Query>(declaration.fields().size());

    /*
     * Delete the existing fields associated with this type.
     */

    final var fieldsJoin =
      METADATA_TYPES_RECORD_FIELDS.join(METADATA_TYPES_RECORDS)
        .on(METADATA_TYPES_RECORD_FIELDS.MTRF_DECLARATION
              .eq(METADATA_TYPES_RECORDS.MTR_ID))
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
          .set(
            METADATA_TYPES_RECORD_FIELDS.MTRF_DESCRIPTION,
            field.description())
          .set(METADATA_TYPES_RECORD_FIELDS.MTRF_REQUIRED, field.isRequired())
          .set(METADATA_TYPES_RECORD_FIELDS.MTRF_SCALAR_TYPE, findTypeId)
          .onConflictOnConstraint(DSL.name(
            "metadata_types_record_fields_unique"))
          .doUpdate()
          .set(METADATA_TYPES_RECORD_FIELDS.MTRF_NAME, field.name().value())
          .set(METADATA_TYPES_RECORD_FIELDS.MTRF_DECLARATION, typeId)
          .set(
            METADATA_TYPES_RECORD_FIELDS.MTRF_DESCRIPTION,
            field.description())
          .set(METADATA_TYPES_RECORD_FIELDS.MTRF_REQUIRED, field.isRequired())
          .set(METADATA_TYPES_RECORD_FIELDS.MTRF_SCALAR_TYPE, findTypeId)
      );
    }

    batches.add(
      auditEvent(
        context,
        OffsetDateTime.now(transaction.clock()),
        transaction.userId(),
        "TYPE_RECORD_UPDATED",
        Map.entry("Type", typeName)
      )
    );
    return batches;
  }
}
