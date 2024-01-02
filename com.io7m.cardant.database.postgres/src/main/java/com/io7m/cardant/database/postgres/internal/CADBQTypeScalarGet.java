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
import com.io7m.cardant.database.api.CADatabaseQueriesTypesType.TypeScalarGetType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CATypeScalarType;
import com.io7m.cardant.model.type_package.CATypePackageIdentifier;
import com.io7m.lanark.core.RDottedName;
import com.io7m.verona.core.Version;
import com.io7m.verona.core.VersionQualifier;
import org.jooq.DSLContext;

import java.util.Optional;

import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_SCALAR;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_PACKAGES;
import static com.io7m.cardant.strings.CAStringConstants.TYPE;

/**
 * Retrieve a scalar type declaration.
 */

public final class CADBQTypeScalarGet
  extends CADBQAbstract<RDottedName, Optional<CATypeScalarType>>
  implements TypeScalarGetType
{
  private static final Service<RDottedName, Optional<CATypeScalarType>, TypeScalarGetType> SERVICE =
    new Service<>(TypeScalarGetType.class, CADBQTypeScalarGet::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQTypeScalarGet(
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
  protected Optional<CATypeScalarType> onExecute(
    final DSLContext context,
    final RDottedName name)
    throws CADatabaseException
  {
    this.setAttribute(TYPE, name.value());

    return context.select(
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
        METADATA_TYPES_SCALAR.MTS_TIME_UPPER,
        METADATA_TYPE_PACKAGES.MTP_NAME,
        METADATA_TYPE_PACKAGES.MTP_VERSION_MAJOR,
        METADATA_TYPE_PACKAGES.MTP_VERSION_MINOR,
        METADATA_TYPE_PACKAGES.MTP_VERSION_PATCH,
        METADATA_TYPE_PACKAGES.MTP_VERSION_QUALIFIER
      )
      .from(
        METADATA_TYPES_SCALAR.join(METADATA_TYPE_PACKAGES)
          .on(METADATA_TYPES_SCALAR.MTS_PACKAGE.eq(METADATA_TYPE_PACKAGES.MTP_ID)))
      .where(METADATA_TYPES_SCALAR.MTS_NAME.eq(name.value()))
      .fetchOptional()
      .map(CADBQTypeScalarGet::mapRecord);
  }

  static CATypeScalarType mapRecord(
    final org.jooq.Record record)
  {
    return switch (record.get(METADATA_TYPES_SCALAR.MTS_BASE_TYPE)) {
      case SCALAR_INTEGRAL -> {
        yield new CATypeScalarType.Integral(
          packageIdentifier(record),
          new RDottedName(record.get(METADATA_TYPES_SCALAR.MTS_NAME)),
          record.get(METADATA_TYPES_SCALAR.MTS_DESCRIPTION),
          record.<Long>get(METADATA_TYPES_SCALAR.MTS_INTEGRAL_LOWER).longValue(),
          record.<Long>get(METADATA_TYPES_SCALAR.MTS_INTEGRAL_UPPER).longValue()
        );
      }
      case SCALAR_MONEY -> {
        yield new CATypeScalarType.Monetary(
          packageIdentifier(record),
          new RDottedName(record.get(METADATA_TYPES_SCALAR.MTS_NAME)),
          record.get(METADATA_TYPES_SCALAR.MTS_DESCRIPTION),
          record.get(METADATA_TYPES_SCALAR.MTS_MONEY_LOWER),
          record.get(METADATA_TYPES_SCALAR.MTS_MONEY_UPPER)
        );
      }
      case SCALAR_REAL -> {
        yield new CATypeScalarType.Real(
          packageIdentifier(record),
          new RDottedName(record.get(METADATA_TYPES_SCALAR.MTS_NAME)),
          record.get(METADATA_TYPES_SCALAR.MTS_DESCRIPTION),
          record.<Double>get(METADATA_TYPES_SCALAR.MTS_REAL_LOWER).doubleValue(),
          record.<Double>get(METADATA_TYPES_SCALAR.MTS_REAL_UPPER).doubleValue()
        );
      }
      case SCALAR_TEXT -> {
        yield new CATypeScalarType.Text(
          packageIdentifier(record),
          new RDottedName(record.get(METADATA_TYPES_SCALAR.MTS_NAME)),
          record.get(METADATA_TYPES_SCALAR.MTS_DESCRIPTION),
          record.get(METADATA_TYPES_SCALAR.MTS_TEXT_PATTERN)
        );
      }
      case SCALAR_TIME -> {
        yield new CATypeScalarType.Time(
          packageIdentifier(record),
          new RDottedName(record.get(METADATA_TYPES_SCALAR.MTS_NAME)),
          record.get(METADATA_TYPES_SCALAR.MTS_DESCRIPTION),
          record.get(METADATA_TYPES_SCALAR.MTS_TIME_LOWER),
          record.get(METADATA_TYPES_SCALAR.MTS_TIME_UPPER)
        );
      }
      default -> {
        throw new IllegalStateException(
          "Unexpected value: %d"
            .formatted(record.get(METADATA_TYPES_SCALAR.MTS_BASE_TYPE))
        );
      }
    };
  }

  static CATypePackageIdentifier packageIdentifier(
    final org.jooq.Record record)
  {
    return new CATypePackageIdentifier(
      new RDottedName(record.get(METADATA_TYPE_PACKAGES.MTP_NAME)),
      new Version(
        record.<Integer>get(METADATA_TYPE_PACKAGES.MTP_VERSION_MAJOR)
          .intValue(),
        record.<Integer>get(METADATA_TYPE_PACKAGES.MTP_VERSION_MINOR)
          .intValue(),
        record.<Integer>get(METADATA_TYPE_PACKAGES.MTP_VERSION_PATCH)
          .intValue(),
        Optional.ofNullable(
          record.get(METADATA_TYPE_PACKAGES.MTP_VERSION_QUALIFIER)
        ).map(VersionQualifier::new)
      )
    );
  }
}
