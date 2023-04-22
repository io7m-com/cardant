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
import com.io7m.cardant.database.api.CADatabaseQueriesTagsType;
import com.io7m.cardant.model.CATag;
import com.io7m.cardant.model.CATagID;
import org.jooq.exception.DataAccessException;

import java.util.Objects;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.io7m.cardant.database.postgres.internal.CADatabaseExceptions.handleDatabaseException;
import static com.io7m.cardant.database.postgres.internal.tables.Tags.TAGS;

/**
 * Tag related queries.
 */

public final class CADatabaseQueriesTags
  extends CABaseQueries
  implements CADatabaseQueriesTagsType
{
  CADatabaseQueriesTags(
    final CADatabaseTransaction inTransaction)
  {
    super(inTransaction);
  }

  @Override
  public Optional<CATag> tagGet(
    final CATagID id)
    throws CADatabaseException
  {
    Objects.requireNonNull(id, "id");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesTags.tagGet");

    try {
      return context.select(TAGS.TAG_NAME)
        .from(TAGS)
        .where(TAGS.TAG_ID.eq(id.id()))
        .fetchOptional(r -> new CATag(id, r.component1()));
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(transaction, e);
    } finally {
      querySpan.end();
    }
  }

  @Override
  public void tagPut(
    final CATag tag)
    throws CADatabaseException
  {
    Objects.requireNonNull(tag, "tag");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesTags.tagPut");

    try {
      final var t_id = tag.id().id();
      final var t_name = tag.name();
      context.insertInto(TAGS)
        .columns(TAGS.TAG_ID, TAGS.TAG_NAME)
        .values(t_id, t_name)
        .onDuplicateKeyUpdate()
        .set(TAGS.TAG_ID, t_id)
        .set(TAGS.TAG_NAME, t_name)
        .execute();
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(transaction, e);
    } finally {
      querySpan.end();
    }
  }

  @Override
  public void tagDelete(
    final CATag tag)
    throws CADatabaseException
  {
    Objects.requireNonNull(tag, "tag");

    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesTags.tagDelete");

    try {
      final var t_id = tag.id().id();
      context.deleteFrom(TAGS)
        .where(TAGS.TAG_ID.eq(t_id))
        .execute();
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(transaction, e);
    } finally {
      querySpan.end();
    }
  }

  @Override
  public SortedSet<CATag> tagList()
    throws CADatabaseException
  {
    final var transaction =
      this.transaction();
    final var context =
      transaction.createContext();
    final var querySpan =
      transaction.createQuerySpan(
        "CADatabaseQueriesTags.tagList");

    try {
      return
        new TreeSet<>(
          context.select(TAGS.TAG_ID, TAGS.TAG_NAME)
            .from(TAGS)
            .stream()
            .map(r -> new CATag(new CATagID(r.component1()), r.component2()))
            .collect(Collectors.toUnmodifiableSet())
        );
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      throw handleDatabaseException(transaction, e);
    } finally {
      querySpan.end();
    }
  }
}
