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
import com.io7m.cardant.database.api.CADatabaseQueriesFilesType;
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileType;
import org.jooq.exception.DataAccessException;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.cardant.database.postgres.internal.CADatabaseExceptions.handleDatabaseException;
import static com.io7m.cardant.database.postgres.internal.tables.Files.FILES;
import static java.lang.Integer.toUnsignedLong;

/**
 * File related queries.
 */

public final class CADatabaseQueriesFiles
  extends CABaseQueries
  implements CADatabaseQueriesFilesType
{
  CADatabaseQueriesFiles(
    final CADatabaseTransaction inTransaction)
  {
    super(inTransaction);
  }

  @Override
  public void filePut(
    final CAFileType file)
    throws CADatabaseException
  {
    Objects.requireNonNull(file, "file");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesFiles.filePut");

    try {
      final var id = file.id().id();
      var fileRec = context.fetchOne(FILES, FILES.ID.eq(id));
      if (fileRec == null) {
        fileRec = context.newRecord(FILES);
        fileRec.set(FILES.ID, id);
      }

      fileRec.set(FILES.DESCRIPTION, file.description());
      fileRec.set(FILES.MEDIA_TYPE, file.mediaType());
      fileRec.set(FILES.HASH_ALGORITHM, file.hashAlgorithm());
      fileRec.set(FILES.HASH_VALUE, file.hashValue());
      if (file instanceof final CAFileType.CAFileWithData withData) {
        final var bytes =
          withData.data().data();
        final var size =
          toUnsignedLong(bytes.length);

        fileRec.set(FILES.DATA, bytes);
        fileRec.set(FILES.DATA_USED, size);
      }

      fileRec.store();
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(
        transaction,
        e,
        Map.entry("File", file.id().displayId())
      );
    } finally {
      querySpan.end();
    }
  }

  @Override
  public Optional<CAFileType> fileGet(
    final CAFileID file,
    final boolean withData)
    throws CADatabaseException
  {
    Objects.requireNonNull(file, "file");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesFiles.fileGet");

    try {
      final var id = file.id();
      final var fileRec = context.fetchOne(FILES, FILES.ID.eq(id));
      if (fileRec == null) {
        return Optional.empty();
      }

      final var size =
        fileRec.getDataUsed()
          .longValue();

      if (withData) {
        return Optional.of(new CAFileType.CAFileWithData(
          file,
          fileRec.getDescription(),
          fileRec.getMediaType(),
          size,
          fileRec.getHashAlgorithm(),
          fileRec.getHashValue(),
          new CAByteArray(fileRec.getData())
        ));
      }

      return Optional.of(new CAFileType.CAFileWithoutData(
        file,
        fileRec.getDescription(),
        fileRec.getMediaType(),
        size,
        fileRec.getHashAlgorithm(),
        fileRec.getHashValue()
      ));
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(
        transaction,
        e,
        Map.entry("File", file.displayId())
      );
    } finally {
      querySpan.end();
    }
  }

  @Override
  public void fileRemove(
    final CAFileID file)
    throws CADatabaseException
  {
    Objects.requireNonNull(file, "file");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesFiles.fileRemove");

    try {
      final var id = file.id();
      context.deleteFrom(FILES)
        .where(FILES.ID.eq(id))
        .execute();
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(
        transaction,
        e,
        Map.entry("File", file.displayId())
      );
    } finally {
      querySpan.end();
    }
  }
}
