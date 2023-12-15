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
import com.io7m.cardant.database.api.CADatabaseQueriesFilesType.PutType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.strings.CAStringConstants;
import org.jooq.DSLContext;

import static com.io7m.cardant.database.api.CADatabaseUnit.UNIT;
import static com.io7m.cardant.database.postgres.internal.tables.Files.FILES;

/**
 * Create or update a file.
 */

public final class CADBQFilePut
  extends CADBQAbstract<CAFileType, CADatabaseUnit>
  implements PutType
{
  private static final Service<CAFileType, CADatabaseUnit, PutType> SERVICE =
    new Service<>(PutType.class, CADBQFilePut::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQFilePut(
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
    final CAFileType file)
    throws CADatabaseException
  {
    this.setAttribute(CAStringConstants.FILE_ID, file.id().displayId());

    var q0 =
      context.insertInto(FILES)
        .set(FILES.FILE_ID, file.id().id())
        .set(FILES.FILE_DESCRIPTION, file.description())
        .set(FILES.FILE_HASH_ALGORITHM, file.hashAlgorithm())
        .set(FILES.FILE_HASH_VALUE, file.hashValue())
        .set(FILES.FILE_MEDIA_TYPE, file.mediaType());

    if (file instanceof final CAFileType.CAFileWithData withData) {
      q0 = q0
        .set(FILES.FILE_DATA, withData.data().data())
        .set(FILES.FILE_DATA_USED, Long.valueOf(withData.size()));
    }

    var q1 = q0.onDuplicateKeyUpdate()
      .set(FILES.FILE_DESCRIPTION, file.description())
      .set(FILES.FILE_HASH_ALGORITHM, file.hashAlgorithm())
      .set(FILES.FILE_HASH_VALUE, file.hashValue())
      .set(FILES.FILE_MEDIA_TYPE, file.mediaType());

    if (file instanceof final CAFileType.CAFileWithData withData) {
      q1 = q1
        .set(FILES.FILE_DATA, withData.data().data())
        .set(FILES.FILE_DATA_USED, Long.valueOf(withData.size()));
    }

    q1.where(FILES.FILE_ID.eq(file.id().id()))
      .execute();

    return UNIT;
  }
}
