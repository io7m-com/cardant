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
import com.io7m.cardant.database.api.CADatabaseQueriesItemTypesType.TypeDeclarationPutType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CATypeDeclaration;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.impl.DSL;

import java.util.ArrayList;

import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_SCALAR_TYPES;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_DECLARATIONS;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_FIELDS;
import static com.io7m.cardant.strings.CAStringConstants.TYPE;

/**
 * Create or update a type declaration.
 */

public final class CADBQItemTypeDeclPut
  extends CADBQAbstract<CATypeDeclaration, CADatabaseUnit>
  implements TypeDeclarationPutType
{
  private static final Service<CATypeDeclaration, CADatabaseUnit, TypeDeclarationPutType> SERVICE =
    new Service<>(TypeDeclarationPutType.class, CADBQItemTypeDeclPut::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemTypeDeclPut(
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
      context.insertInto(METADATA_TYPE_DECLARATIONS)
        .set(METADATA_TYPE_DECLARATIONS.NAME, typeName)
        .set(METADATA_TYPE_DECLARATIONS.DESCRIPTION, description)
        .onConflict(METADATA_TYPE_DECLARATIONS.NAME)
        .doUpdate()
        .set(METADATA_TYPE_DECLARATIONS.NAME, typeName)
        .set(METADATA_TYPE_DECLARATIONS.DESCRIPTION, description)
        .returning(METADATA_TYPE_DECLARATIONS.ID)
        .execute();

    final var fieldInserts =
      new ArrayList<Query>(declaration.fields().size());

    for (final var entry : declaration.fields().entrySet()) {
      final var field =
        entry.getValue();
      final var scalarType =
        field.type();

      final var findTypeId =
        DSL.select(METADATA_SCALAR_TYPES.ID)
          .from(METADATA_SCALAR_TYPES)
          .where(METADATA_SCALAR_TYPES.NAME.eq(scalarType.name().value()));

      fieldInserts.add(
        DSL.insertInto(METADATA_TYPE_FIELDS)
          .set(METADATA_TYPE_FIELDS.FIELD_NAME, field.name().value())
          .set(METADATA_TYPE_FIELDS.FIELD_DECLARATION, typeId)
          .set(METADATA_TYPE_FIELDS.FIELD_DESCRIPTION, field.description())
          .set(METADATA_TYPE_FIELDS.FIELD_REQUIRED, field.isRequired())
          .set(METADATA_TYPE_FIELDS.FIELD_TYPE, findTypeId)
          .onConflictOnConstraint(DSL.name("metadata_type_fields_pkey"))
          .doUpdate()
          .set(METADATA_TYPE_FIELDS.FIELD_NAME, field.name().value())
          .set(METADATA_TYPE_FIELDS.FIELD_DECLARATION, typeId)
          .set(METADATA_TYPE_FIELDS.FIELD_DESCRIPTION, field.description())
          .set(METADATA_TYPE_FIELDS.FIELD_REQUIRED, field.isRequired())
          .set(METADATA_TYPE_FIELDS.FIELD_TYPE, findTypeId)
      );
    }

    context.batch(fieldInserts).execute();
    return CADatabaseUnit.UNIT;
  }
}
