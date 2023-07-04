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
import com.io7m.cardant.database.api.CADatabaseQueriesItemsType;
import com.io7m.cardant.database.api.CADatabaseQueriesLocationsType;
import com.io7m.cardant.database.api.CADatabaseQueriesMaintenanceType;
import com.io7m.cardant.database.api.CADatabaseQueriesTagsType;
import com.io7m.cardant.database.api.CADatabaseQueriesType;
import com.io7m.cardant.database.api.CADatabaseQueriesUsersType;
import com.io7m.cardant.database.api.CADatabaseRole;
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.SQLException;
import java.time.Clock;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSql;
import static com.io7m.cardant.error_codes.CAStandardErrorCodes.errorSqlUnsupportedQueryClass;
import static io.opentelemetry.api.trace.SpanKind.INTERNAL;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DB_SYSTEM;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DbSystemValues.POSTGRESQL;
import static org.jooq.SQLDialect.POSTGRES;

final class CADatabaseTransaction
  implements CADatabaseTransactionType
{
  private final CADatabaseConnection connection;
  private final Span transactionSpan;
  private UUID userId;

  CADatabaseTransaction(
    final CADatabaseConnection inConnection,
    final Span inTransactionScope)
  {
    this.connection =
      Objects.requireNonNull(inConnection, "connection");
    this.transactionSpan =
      Objects.requireNonNull(inTransactionScope, "inMetricsScope");
  }

  /**
   * @return The transaction span for metrics
   */

  public Span span()
  {
    return this.transactionSpan;
  }

  /**
   * Create a new query span for measuring query times.
   *
   * @param name The query name
   *
   * @return The query span
   */

  public Span createQuerySpan(
    final String name)
  {
    return this.tracer()
      .spanBuilder(name)
      .setParent(Context.current().with(this.transactionSpan))
      .setAttribute(DB_SYSTEM, POSTGRESQL)
      .setSpanKind(INTERNAL)
      .startSpan();
  }

  CADatabaseConnection connection()
  {
    return this.connection;
  }

  void setRole(
    final CADatabaseRole role)
    throws SQLException
  {
    switch (role) {
      case CARDANT -> {
        // Transactions already start in this role.
      }
      case CARDANT_READ_ONLY -> {
        try (var st =
               this.connection.connection()
                 .prepareStatement("set role cardant_read_only")) {
          st.execute();
        }
      }
      case NONE -> {
        try (var st =
               this.connection.connection()
                 .prepareStatement("set role cardant_none")) {
          st.execute();
        }
      }
    }
  }

  @Override
  public <T extends CADatabaseQueriesType> T queries(
    final Class<T> qClass)
    throws CADatabaseException
  {
    if (Objects.equals(qClass, CADatabaseQueriesUsersType.class)) {
      return qClass.cast(new CADatabaseQueriesUsers(this));
    }
    if (Objects.equals(qClass, CADatabaseQueriesTagsType.class)) {
      return qClass.cast(new CADatabaseQueriesTags(this));
    }
    if (Objects.equals(qClass, CADatabaseQueriesFilesType.class)) {
      return qClass.cast(new CADatabaseQueriesFiles(this));
    }
    if (Objects.equals(qClass, CADatabaseQueriesLocationsType.class)) {
      return qClass.cast(new CADatabaseQueriesLocations(this));
    }
    if (Objects.equals(qClass, CADatabaseQueriesItemsType.class)) {
      return qClass.cast(new CADatabaseQueriesItems(this));
    }
    if (Objects.equals(qClass, CADatabaseQueriesMaintenanceType.class)) {
      return qClass.cast(new CADatabaseQueriesMaintenance(this));
    }

    throw new CADatabaseException(
      "Unsupported query type: %s".formatted(qClass),
      errorSqlUnsupportedQueryClass(),
      Map.of(),
      Optional.empty()
    );
  }

  public DSLContext createContext()
  {
    final var sqlConnection =
      this.connection.connection();
    final var settings =
      this.connection.database().settings();
    return DSL.using(sqlConnection, POSTGRES, settings);
  }

  public Clock clock()
  {
    return this.connection.database().clock();
  }

  @Override
  public void rollback()
    throws CADatabaseException
  {
    try {
      this.connection.connection().rollback();
      this.connection.database()
        .counterTransactionRollbacks()
        .add(1L);
    } catch (final SQLException e) {
      throw new CADatabaseException(
        e.getMessage(),
        e,
        errorSql(),
        Collections.emptySortedMap(),
        Optional.empty()
      );
    }
  }

  @Override
  public void commit()
    throws CADatabaseException
  {
    try {
      this.connection.connection().commit();
      this.connection.database()
        .counterTransactionCommits()
        .add(1L);
    } catch (final SQLException e) {
      throw new CADatabaseException(
        e.getMessage(),
        e,
        errorSql(),
        Collections.emptySortedMap(),
        Optional.empty()
      );
    }
  }

  @Override
  public void close()
    throws CADatabaseException
  {
    try {
      this.rollback();
    } catch (final Exception e) {
      this.transactionSpan.recordException(e);
      throw e;
    } finally {
      this.transactionSpan.end();
    }
  }

  /**
   * @return The metrics tracer
   */

  Tracer tracer()
  {
    return this.connection.database().tracer();
  }

  @Override
  public void setUserId(
    final UUID newUserId)
  {
    this.userId = Objects.requireNonNull(newUserId, "userId");
  }

  @Override
  public UUID userId()
  {
    if (this.userId == null) {
      throw new IllegalStateException("No user ID has been set.");
    }
    return this.userId;
  }

  @Override
  public String toString()
  {
    return "[CADatabaseTransaction 0x%s]"
      .formatted(Long.toUnsignedString(this.hashCode(), 16));
  }
}
