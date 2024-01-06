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

import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.LocationGetType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAAttachment;
import com.io7m.cardant.model.CAAttachmentKey;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAMetadataType;
import com.io7m.lanark.core.RDottedName;
import org.joda.money.CurrencyUnit;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;

import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.io7m.cardant.database.postgres.internal.Tables.FILES;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATIONS;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATION_ATTACHMENTS;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATION_METADATA;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATION_TYPES;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPES_RECORDS;

/**
 * Retrieve a location.
 */

public final class CADBQLocationGet
  extends CADBQAbstract<CALocationID, Optional<CALocation>>
  implements LocationGetType
{
  private static final Service<CALocationID, Optional<CALocation>, LocationGetType> SERVICE =
    new Service<>(LocationGetType.class, CADBQLocationGet::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQLocationGet(
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
  protected Optional<CALocation> onExecute(
    final DSLContext context,
    final CALocationID id)
  {
    final var query =
      context.select(
          LOCATIONS.LOCATION_ID,
          LOCATIONS.LOCATION_PARENT,
          LOCATIONS.LOCATION_NAME,
          DSL.arrayAgg(METADATA_TYPES_RECORDS.MTR_NAME),
          LOCATION_METADATA.LOCATION_META_NAME,
          LOCATION_METADATA.LOCATION_META_VALUE_INTEGRAL,
          LOCATION_METADATA.LOCATION_META_VALUE_MONEY,
          LOCATION_METADATA.LOCATION_META_VALUE_MONEY_CURRENCY,
          LOCATION_METADATA.LOCATION_META_VALUE_REAL,
          LOCATION_METADATA.LOCATION_META_VALUE_TEXT,
          LOCATION_METADATA.LOCATION_META_VALUE_TIME,
          LOCATION_METADATA.LOCATION_META_VALUE_TYPE,
          LOCATION_ATTACHMENTS.LA_RELATION,
          FILES.FILE_ID,
          FILES.FILE_DATA_USED,
          FILES.FILE_DESCRIPTION,
          FILES.FILE_MEDIA_TYPE,
          FILES.FILE_HASH_ALGORITHM,
          FILES.FILE_HASH_VALUE
        ).from(LOCATIONS)
        .leftJoin(LOCATION_TYPES)
        .on(LOCATION_TYPES.LT_LOCATION.eq(LOCATIONS.LOCATION_ID))
        .leftJoin(METADATA_TYPES_RECORDS)
        .on(METADATA_TYPES_RECORDS.MTR_ID.eq(LOCATION_TYPES.LT_TYPE))
        .leftJoin(LOCATION_METADATA)
        .on(LOCATION_METADATA.LOCATION_META_LOCATION.eq(LOCATIONS.LOCATION_ID))
        .leftJoin(LOCATION_ATTACHMENTS)
        .on(LOCATION_ATTACHMENTS.LA_LOCATION_ID.eq(LOCATIONS.LOCATION_ID))
        .leftJoin(FILES)
        .on(FILES.FILE_ID.eq(LOCATION_ATTACHMENTS.LA_FILE_ID))
        .where(LOCATIONS.LOCATION_ID.eq(id.id()))
        .groupBy(
          LOCATIONS.LOCATION_ID,
          LOCATIONS.LOCATION_PARENT,
          LOCATIONS.LOCATION_NAME,
          LOCATION_METADATA.LOCATION_META_NAME,
          LOCATION_METADATA.LOCATION_META_VALUE_INTEGRAL,
          LOCATION_METADATA.LOCATION_META_VALUE_MONEY,
          LOCATION_METADATA.LOCATION_META_VALUE_MONEY_CURRENCY,
          LOCATION_METADATA.LOCATION_META_VALUE_REAL,
          LOCATION_METADATA.LOCATION_META_VALUE_TEXT,
          LOCATION_METADATA.LOCATION_META_VALUE_TIME,
          LOCATION_METADATA.LOCATION_META_VALUE_TYPE,
          LOCATION_ATTACHMENTS.LA_RELATION,
          FILES.FILE_ID,
          FILES.FILE_DATA_USED,
          FILES.FILE_DESCRIPTION,
          FILES.FILE_MEDIA_TYPE,
          FILES.FILE_HASH_ALGORITHM,
          FILES.FILE_HASH_VALUE
        );

    final var results = query.fetch();
    if (results.isEmpty()) {
      return Optional.empty();
    }

    final var meta =
      new TreeMap<RDottedName, CAMetadataType>();
    final var types =
      new TreeSet<RDottedName>();
    final var attachments =
      new TreeMap<CAAttachmentKey, CAAttachment>();

    CALocationID locationId = null;
    Optional<CALocationID> parent = Optional.empty();
    String name = null;

    for (final var rec : results) {
      locationId =
        new CALocationID(rec.get(LOCATIONS.LOCATION_ID));
      parent =
        Optional.ofNullable(rec.get(LOCATIONS.LOCATION_PARENT))
          .map(CALocationID::new);
      name =
        rec.get(LOCATIONS.LOCATION_NAME);

      Optional.ofNullable(rec.get(LOCATION_METADATA.LOCATION_META_NAME))
        .ifPresent(s -> {
          final var metaName = new RDottedName(s);
          meta.put(metaName, mapLocationMetadataRecord(metaName, rec));
        });

      Optional.ofNullable(rec.get(LOCATION_ATTACHMENTS.LA_RELATION))
        .ifPresent(f -> {
          final var attachment = new CAAttachment(
            new CAFileType.CAFileWithoutData(
              new CAFileID(rec.get(FILES.FILE_ID)),
              rec.get(FILES.FILE_DESCRIPTION),
              rec.get(FILES.FILE_MEDIA_TYPE),
              rec.<Long>get(FILES.FILE_DATA_USED).longValue(),
              rec.get(FILES.FILE_HASH_ALGORITHM),
              rec.get(FILES.FILE_HASH_VALUE)
            ),
            rec.get(LOCATION_ATTACHMENTS.LA_RELATION)
          );
          attachments.put(attachment.key(), attachment);
        });
    }

    return Optional.of(
      new CALocation(
        locationId,
        parent,
        name,
        meta,
        attachments,
        types
      )
    );
  }

  static CAMetadataType mapLocationMetadataRecord(
    final RDottedName name,
    final Record rec)
  {
    final var typeCode =
      rec.get(LOCATION_METADATA.LOCATION_META_VALUE_TYPE);

    return switch (typeCode) {
      case SCALAR_INTEGRAL -> {
        yield new CAMetadataType.Integral(
          name,
          rec.get(LOCATION_METADATA.LOCATION_META_VALUE_INTEGRAL).longValue()
        );
      }
      case SCALAR_REAL -> {
        yield new CAMetadataType.Real(
          name,
          rec.get(LOCATION_METADATA.LOCATION_META_VALUE_REAL).doubleValue()
        );
      }
      case SCALAR_MONEY -> {
        yield new CAMetadataType.Monetary(
          name,
          rec.get(LOCATION_METADATA.LOCATION_META_VALUE_MONEY),
          CurrencyUnit.of(rec.get(LOCATION_METADATA.LOCATION_META_VALUE_MONEY_CURRENCY))
        );
      }
      case SCALAR_TEXT -> {
        yield new CAMetadataType.Text(
          name,
          rec.get(LOCATION_METADATA.LOCATION_META_VALUE_TEXT)
        );
      }
      case SCALAR_TIME -> {
        yield new CAMetadataType.Time(
          name,
          rec.get(LOCATION_METADATA.LOCATION_META_VALUE_TIME)
        );
      }
    };
  }
}
