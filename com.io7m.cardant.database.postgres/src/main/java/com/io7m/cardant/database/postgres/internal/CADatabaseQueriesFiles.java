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
import com.io7m.cardant.database.api.CADatabaseFileSearchType;
import com.io7m.cardant.database.api.CADatabaseQueriesFilesType;
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAFileColumnOrdering;
import com.io7m.cardant.model.CAFileID;
import com.io7m.cardant.model.CAFileSearchParameters;
import com.io7m.cardant.model.CAFileType;
import com.io7m.cardant.model.CAFileType.CAFileWithoutData;
import com.io7m.cardant.model.CAPage;
import com.io7m.jqpage.core.JQField;
import com.io7m.jqpage.core.JQKeysetRandomAccessPageDefinition;
import com.io7m.jqpage.core.JQKeysetRandomAccessPagination;
import com.io7m.jqpage.core.JQOrder;
import org.jooq.Condition;
import org.jooq.Table;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.cardant.database.postgres.internal.CADatabaseExceptions.handleDatabaseException;
import static com.io7m.cardant.database.postgres.internal.tables.Files.FILES;
import static com.io7m.cardant.strings.CAStringConstants.FILE;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DB_STATEMENT;
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
        Map.entry(this.local(FILE), file.id().displayId())
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

      return Optional.of(new CAFileWithoutData(
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
        Map.entry(this.local(FILE), file.displayId())
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
        Map.entry(this.local(FILE), file.displayId())
      );
    } finally {
      querySpan.end();
    }
  }

  @Override
  public CADatabaseFileSearchType fileSearch(
    final CAFileSearchParameters parameters)
    throws CADatabaseException
  {
    Objects.requireNonNull(parameters, "parameters");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesFiles.fileSearch");

    try {
      final Table<?> tableSource =
        FILES;

      /*
       * Search queries might be present.
       */

      final Condition descriptionCondition;
      final var descriptionQuery = parameters.description();
      if (descriptionQuery.isPresent()) {
        descriptionCondition =
          DSL.condition(
            "files.description_search @@ websearch_to_tsquery(?)",
            DSL.inline(descriptionQuery.get())
          );
      } else {
        descriptionCondition = DSL.trueCondition();
      }

      final Condition mediaCondition;
      final var mediaQuery = parameters.mediaType();
      if (mediaQuery.isPresent()) {
        final var searchText = "%%%s%%".formatted(mediaQuery.get());
        mediaCondition =
          DSL.condition(FILES.MEDIA_TYPE.likeIgnoreCase(searchText));
      } else {
        mediaCondition = DSL.trueCondition();
      }

      final Condition sizeCondition;
      final var sizeQuery = parameters.sizeRange();
      if (sizeQuery.isPresent()) {
        final var range = sizeQuery.get();
        final var sizeLowerCondition =
          DSL.condition(FILES.DATA_USED.ge(Long.valueOf(range.sizeMinimum())));
        final var sizeUpperCondition =
          DSL.condition(FILES.DATA_USED.le(Long.valueOf(range.sizeMaximum())));
        sizeCondition = DSL.and(sizeLowerCondition, sizeUpperCondition);
      } else {
        sizeCondition = DSL.trueCondition();
      }

      final var allConditions =
        DSL.and(descriptionCondition, mediaCondition, sizeCondition);

      final var orderField =
        orderingToJQField(parameters.ordering());

      final var pages =
        JQKeysetRandomAccessPagination.createPageDefinitions(
          context,
          tableSource,
          List.of(orderField),
          List.of(allConditions),
          List.of(),
          toUnsignedLong(parameters.limit()),
          statement -> {
            querySpan.setAttribute(DB_STATEMENT, statement.toString());
          }
        );

      return new FileSearch(pages);
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(transaction, e);
    } finally {
      querySpan.end();
    }
  }

  private static final class FileSearch
    extends CAAbstractSearch<CADatabaseQueriesFiles, CADatabaseQueriesFilesType, CAFileWithoutData>
    implements CADatabaseFileSearchType
  {
    FileSearch(
      final List<JQKeysetRandomAccessPageDefinition> inPages)
    {
      super(inPages);
    }

    @Override
    protected CAPage<CAFileWithoutData> page(
      final CADatabaseQueriesFiles queries,
      final JQKeysetRandomAccessPageDefinition page)
      throws CADatabaseException
    {
      final var transaction =
        queries.transaction();
      final var context =
        transaction.createContext();

      final var querySpan =
        transaction.createQuerySpan(
          "CADatabaseQueriesFiles.fileSearch.page");

      try {
        final var query =
          page.queryFields(context, List.of(
            FILES.DATA_USED,
            FILES.DESCRIPTION,
            FILES.HASH_ALGORITHM,
            FILES.HASH_VALUE,
            FILES.ID,
            FILES.MEDIA_TYPE
          ));

        querySpan.setAttribute(DB_STATEMENT, query.toString());

        final var items =
          query.fetch().map(record -> {
            return new CAFileWithoutData(
              new CAFileID(record.get(FILES.ID)),
              record.get(FILES.DESCRIPTION),
              record.get(FILES.MEDIA_TYPE),
              record.<Long>get(FILES.DATA_USED).longValue(),
              record.get(FILES.HASH_ALGORITHM),
              record.get(FILES.HASH_VALUE)
            );
          });

        return new CAPage<>(
          items,
          (int) page.index(),
          this.pageCount(),
          page.firstOffset()
        );
      } catch (final DataAccessException e) {
        querySpan.recordException(e);
        throw handleDatabaseException(transaction, e);
      } finally {
        querySpan.end();
      }
    }
  }

  private static JQField orderingToJQField(
    final CAFileColumnOrdering ordering)
  {
    final var field =
      switch (ordering.column()) {
        case BY_ID -> FILES.ID;
        case BY_DESCRIPTION -> FILES.DESCRIPTION;
      };

    return new JQField(
      field,
      ordering.ascending() ? JQOrder.ASCENDING : JQOrder.DESCENDING
    );
  }
}
