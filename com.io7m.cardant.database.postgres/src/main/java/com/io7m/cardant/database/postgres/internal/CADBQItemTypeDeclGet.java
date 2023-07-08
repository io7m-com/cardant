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
import com.io7m.cardant.database.api.CADatabaseQueriesItemTypesType.TypeDeclarationGetType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CATypeDeclaration;
import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.model.CATypeScalar;
import com.io7m.lanark.core.RDottedName;
import org.jooq.DSLContext;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_SCALAR_TYPES;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_DECLARATIONS;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_FIELDS;

/**
 * Retrieve a type declaration.
 */

public final class CADBQItemTypeDeclGet
  extends CADBQAbstract<RDottedName, Optional<CATypeDeclaration>>
  implements TypeDeclarationGetType
{
  private static final Service<RDottedName, Optional<CATypeDeclaration>, TypeDeclarationGetType> SERVICE =
    new Service<>(TypeDeclarationGetType.class, CADBQItemTypeDeclGet::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemTypeDeclGet(
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
  protected Optional<CATypeDeclaration> onExecute(
    final DSLContext context,
    final RDottedName name)
    throws CADatabaseException
  {
    final var typeName = name.value();

    final var fieldsWithTypes =
      METADATA_TYPE_FIELDS.join(METADATA_SCALAR_TYPES)
        .on(METADATA_TYPE_FIELDS.FIELD_SCALAR_TYPE.eq(METADATA_SCALAR_TYPES.ID));

    final var query =
      context.select(
          METADATA_TYPE_DECLARATIONS.NAME,
          METADATA_TYPE_DECLARATIONS.DESCRIPTION,
          METADATA_TYPE_FIELDS.FIELD_NAME,
          METADATA_TYPE_FIELDS.FIELD_DESCRIPTION,
          METADATA_TYPE_FIELDS.FIELD_REQUIRED,
          METADATA_SCALAR_TYPES.NAME,
          METADATA_SCALAR_TYPES.DESCRIPTION,
          METADATA_SCALAR_TYPES.PATTERN)
        .from(METADATA_TYPE_DECLARATIONS)
        .leftJoin(fieldsWithTypes)
        .on(METADATA_TYPE_FIELDS.FIELD_DECLARATION.eq(METADATA_TYPE_DECLARATIONS.ID))
        .where(METADATA_TYPE_DECLARATIONS.NAME.eq(typeName));

    final var records =
      query.fetch();

    if (records.isEmpty()) {
      return Optional.empty();
    }

    final Map<RDottedName, CATypeField> fields =
      new HashMap<>(8);

    String description = "";
    for (final var record : records) {
      description = record.get(METADATA_TYPE_DECLARATIONS.DESCRIPTION);

      final var scalarTypeNameText =
        record.get(METADATA_SCALAR_TYPES.NAME);

      if (scalarTypeNameText != null) {
        final var scalarTypeName =
          new RDottedName(scalarTypeNameText);
        final var scalarType =
          new CATypeScalar(
            scalarTypeName,
            record.get(METADATA_SCALAR_TYPES.DESCRIPTION),
            record.get(METADATA_SCALAR_TYPES.PATTERN)
          );

        final var field =
          new CATypeField(
            new RDottedName(record.get(METADATA_TYPE_FIELDS.FIELD_NAME)),
            record.get(METADATA_TYPE_FIELDS.FIELD_DESCRIPTION),
            scalarType,
            record.get(METADATA_TYPE_FIELDS.FIELD_REQUIRED).booleanValue()
          );

        fields.put(field.name(), field);
      }
    }

    return Optional.of(
      new CATypeDeclaration(
        name,
        description,
        fields
      )
    );
  }
}
