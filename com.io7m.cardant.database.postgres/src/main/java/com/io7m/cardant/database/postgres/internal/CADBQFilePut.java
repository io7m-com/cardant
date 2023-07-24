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
import static java.lang.Integer.toUnsignedLong;

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

    final var id = file.id().id();
    var fileRec = context.fetchOne(FILES, FILES.FILE_ID.eq(id));
    if (fileRec == null) {
      fileRec = context.newRecord(FILES);
      fileRec.set(FILES.FILE_ID, id);
    }

    fileRec.set(FILES.FILE_DESCRIPTION, file.description());
    fileRec.set(FILES.FILE_MEDIA_TYPE, file.mediaType());
    fileRec.set(FILES.FILE_HASH_ALGORITHM, file.hashAlgorithm());
    fileRec.set(FILES.FILE_HASH_VALUE, file.hashValue());
    if (file instanceof final CAFileType.CAFileWithData withData) {
      final var bytes =
        withData.data().data();
      final var size =
        toUnsignedLong(bytes.length);

      fileRec.set(FILES.FILE_DATA, bytes);
      fileRec.set(FILES.FILE_DATA_USED, size);
    }

    fileRec.store();
    return UNIT;
  }

}
