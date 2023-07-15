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

import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType.GetType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAAttachment;
import com.io7m.cardant.model.CAAttachmentKey;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CALocation;
import com.io7m.cardant.model.CALocationID;
import com.io7m.cardant.model.CAMetadata;
import com.io7m.lanark.core.RDottedName;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.io7m.cardant.database.postgres.internal.Tables.FILES;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATIONS;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATION_ATTACHMENTS;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATION_METADATA;
import static com.io7m.cardant.database.postgres.internal.Tables.LOCATION_TYPES;
import static com.io7m.cardant.database.postgres.internal.Tables.METADATA_TYPE_DECLARATIONS;

/**
 * Retrieve a location.
 */

public final class CADBQLocationGet
  extends CADBQAbstract<CALocationID, Optional<CALocation>>
  implements GetType
{
  private static final Service<CALocationID, Optional<CALocation>, GetType> SERVICE =
    new Service<>(GetType.class, CADBQLocationGet::new);

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
          DSL.arrayAgg(METADATA_TYPE_DECLARATIONS.NAME),
          LOCATION_METADATA.METADATA_NAME,
          LOCATION_METADATA.METADATA_VALUE,
          LOCATION_ATTACHMENTS.RELATION,
          FILES.ID,
          FILES.DATA_USED,
          FILES.DESCRIPTION,
          FILES.MEDIA_TYPE,
          FILES.HASH_ALGORITHM,
          FILES.HASH_VALUE
        ).from(LOCATIONS)
        .leftJoin(LOCATION_TYPES)
        .on(LOCATION_TYPES.LOCATION.eq(LOCATIONS.LOCATION_ID))
        .leftJoin(METADATA_TYPE_DECLARATIONS)
        .on(METADATA_TYPE_DECLARATIONS.ID.eq(LOCATION_TYPES.TYPE_DECLARATION))
        .leftJoin(LOCATION_METADATA)
        .on(LOCATION_METADATA.METADATA_LOCATION_ID.eq(LOCATIONS.LOCATION_ID))
        .leftJoin(LOCATION_ATTACHMENTS)
        .on(LOCATION_ATTACHMENTS.LOCATION_ID.eq(LOCATIONS.LOCATION_ID))
        .leftJoin(FILES)
        .on(FILES.ID.eq(LOCATION_ATTACHMENTS.FILE_ID))
        .where(LOCATIONS.LOCATION_ID.eq(id.id()))
        .groupBy(
          LOCATIONS.LOCATION_ID,
          LOCATIONS.LOCATION_PARENT,
          LOCATIONS.LOCATION_NAME,
          LOCATION_METADATA.METADATA_NAME,
          LOCATION_METADATA.METADATA_VALUE,
          LOCATION_ATTACHMENTS.RELATION,
          FILES.ID,
          FILES.DATA_USED,
          FILES.DESCRIPTION,
          FILES.MEDIA_TYPE,
          FILES.HASH_ALGORITHM,
          FILES.HASH_VALUE
        );

    final var results = query.fetch();
    if (results.isEmpty()) {
      return Optional.empty();
    }

    final var meta =
      new TreeMap<RDottedName, CAMetadata>();
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

      Optional.ofNullable(rec.get(LOCATION_METADATA.METADATA_NAME))
        .ifPresent(s -> {
          final var metaName =
            new RDottedName(s);
          final var metaValue =
            rec.get(LOCATION_METADATA.METADATA_VALUE);
          meta.put(metaName, new CAMetadata(metaName, metaValue));
        });

      Optional.ofNullable(rec.get(LOCATION_ATTACHMENTS.RELATION))
        .ifPresent(f -> {
          final var attachment = new CAAttachment(
            new CAFileType.CAFileWithoutData(
              new CAFileID(rec.get(FILES.ID)),
              rec.get(FILES.DESCRIPTION),
              rec.get(FILES.MEDIA_TYPE),
              rec.<Long>get(FILES.DATA_USED).longValue(),
              rec.get(FILES.HASH_ALGORITHM),
              rec.get(FILES.HASH_VALUE)
            ),
            rec.get(LOCATION_ATTACHMENTS.RELATION)
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
}
