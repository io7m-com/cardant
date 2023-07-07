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
import com.io7m.cardant.database.api.CADatabaseQueriesItemTypesType.TypeDeclarationGetMultipleType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CATypeDeclaration;
import com.io7m.cardant.model.CATypeField;
import com.io7m.cardant.model.CATypeScalar;
import com.io7m.lanark.core.RDottedName;
import org.jooq.DSLContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_SCALAR_TYPES;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_DECLARATIONS;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_FIELDS;

/**
 * Retrieve multiple type declarations.
 */

public final class CADBQItemTypeDeclGetMultiple
  extends CADBQAbstract<Set<RDottedName>, List<CATypeDeclaration>>
  implements TypeDeclarationGetMultipleType
{
  private static final Service<Set<RDottedName>, List<CATypeDeclaration>, TypeDeclarationGetMultipleType> SERVICE =
    new Service<>(TypeDeclarationGetMultipleType.class, CADBQItemTypeDeclGetMultiple::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQItemTypeDeclGetMultiple(
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
  protected List<CATypeDeclaration> onExecute(
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
          METADATA_TYPE_DECLARATIONS.NAME,
          METADATA_TYPE_DECLARATIONS.DESCRIPTION,
          METADATA_TYPE_FIELDS.FIELD_NAME,
          METADATA_TYPE_FIELDS.FIELD_DESCRIPTION,
          METADATA_TYPE_FIELDS.FIELD_REQUIRED,
          METADATA_SCALAR_TYPES.NAME,
          METADATA_SCALAR_TYPES.DESCRIPTION,
          METADATA_SCALAR_TYPES.PATTERN)
        .from(METADATA_TYPE_DECLARATIONS, METADATA_TYPE_FIELDS)
        .join(METADATA_SCALAR_TYPES)
        .on(METADATA_SCALAR_TYPES.ID.eq(METADATA_TYPE_FIELDS.FIELD_TYPE))
        .where(METADATA_TYPE_DECLARATIONS.NAME.in(nameSet))
        .orderBy(METADATA_TYPE_DECLARATIONS.NAME.asc());

    final var records =
      query.fetch();

    final Map<RDottedName, Map<RDottedName, CATypeField>> fieldsByTypeName =
      new HashMap<>(names.size());
    final Map<RDottedName, String> descriptionsByTypeName =
      new HashMap<>(names.size());

    for (final var record : records) {
      final var typeName =
        new RDottedName(record.get(METADATA_TYPE_DECLARATIONS.NAME));
      final var description =
        record.get(METADATA_TYPE_DECLARATIONS.DESCRIPTION);

      descriptionsByTypeName.put(typeName, description);

      final var scalarTypeName =
        new RDottedName(record.get(METADATA_SCALAR_TYPES.NAME));
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

      var fields = fieldsByTypeName.get(typeName);
      if (fields == null) {
        fields = new HashMap<>(8);
      }
      fields.put(field.name(), field);
      fieldsByTypeName.put(typeName, fields);
    }

    final var results = new ArrayList<CATypeDeclaration>(names.size());
    for (final var typeName : names) {
      final var description =
        descriptionsByTypeName.get(typeName);
      final var fields =
        fieldsByTypeName.get(typeName);
      results.add(new CATypeDeclaration(typeName, description, fields));
    }

    results.sort(Comparator.comparing(CATypeDeclaration::name));
    return List.copyOf(results);
  }
}
