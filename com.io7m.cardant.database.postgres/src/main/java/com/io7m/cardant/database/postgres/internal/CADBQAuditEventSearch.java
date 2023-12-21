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

import com.io7m.cardant.database.api.CADatabaseAuditSearchType;
import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseQueriesAuditType;
import com.io7m.cardant.database.postgres.internal.CADBQueryProviderType.Service;
import com.io7m.cardant.model.CAAuditEvent;
import com.io7m.cardant.model.CAAuditSearchParameters;
import com.io7m.cardant.model.CAPage;
import com.io7m.cardant.model.CAUserID;
import com.io7m.jqpage.core.JQField;
import com.io7m.jqpage.core.JQKeysetRandomAccessPageDefinition;
import com.io7m.jqpage.core.JQKeysetRandomAccessPagination;
import com.io7m.jqpage.core.JQKeysetRandomAccessPaginationParameters;
import com.io7m.jqpage.core.JQOrder;
import io.opentelemetry.api.trace.Span;
import org.jooq.DSLContext;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.True;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.postgres.extensions.bindings.HstoreBinding;
import org.jooq.postgres.extensions.types.Hstore;

import java.util.List;

import static com.io7m.cardant.database.postgres.internal.CADBComparisons.createExactMatchQuery;
import static com.io7m.cardant.database.postgres.internal.CADatabaseExceptions.handleDatabaseException;
import static com.io7m.cardant.database.postgres.internal.Tables.AUDIT;
import static io.opentelemetry.semconv.trace.attributes.SemanticAttributes.DB_STATEMENT;

/**
 * Retrieve audit events.
 */

public final class CADBQAuditEventSearch
  extends CADBQAbstract<CAAuditSearchParameters, CADatabaseAuditSearchType>
  implements CADatabaseQueriesAuditType.EventSearchType
{
  private static final Service<CAAuditSearchParameters, CADatabaseAuditSearchType, EventSearchType> SERVICE =
    new Service<>(EventSearchType.class, CADBQAuditEventSearch::new);

  private static final DataType<Hstore> AU_DATA_TYPE =
    SQLDataType.OTHER.asConvertedDataType(new HstoreBinding());

  static final Field<Hstore> AU_DATA =
    DSL.field("DATA", AU_DATA_TYPE);

  private static final True TRUE_CONDITION =
    DSL.trueCondition();

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQAuditEventSearch(
    final CADatabaseTransaction transaction)
  {
    super(transaction);
  }

  @Override
  protected CADatabaseAuditSearchType onExecute(
    final DSLContext context,
    final CAAuditSearchParameters parameters)
  {
    final var conditionUser =
      parameters.owner()
        .map(CAUserID::id)
        .map(AUDIT.USER_ID::eq)
        .orElse(TRUE_CONDITION);

    final var conditionType =
      createExactMatchQuery(parameters.type(), AUDIT.TYPE);

    final var conditionTime =
      AUDIT.TIME.ge(parameters.timeRange().lower())
        .and(AUDIT.TIME.le(parameters.timeRange().upper()));

    final var conditions =
      DSL.and(conditionUser, conditionType, conditionTime);

    final var pageParameters =
      JQKeysetRandomAccessPaginationParameters.forTable(AUDIT)
        .addSortField(new JQField(AUDIT.TIME, JQOrder.ASCENDING))
        .addWhereCondition(conditions)
        .setPageSize(parameters.pageSize())
        .setStatementListener(statement -> {
          Span.current().setAttribute(DB_STATEMENT, statement.toString());
        }).build();

    final var pages =
      JQKeysetRandomAccessPagination.createPageDefinitions(
        context, pageParameters);

    return new CAAuditSearch(pages);
  }

  /**
   * @return A query provider
   */

  public static CADBQueryProviderType provider()
  {
    return () -> SERVICE;
  }

  private static final class CAAuditSearch
    extends CAAbstractSearch<CAAuditEvent>
    implements CADatabaseAuditSearchType
  {
    CAAuditSearch(
      final List<JQKeysetRandomAccessPageDefinition> inPages)
    {
      super(inPages);
    }

    @Override
    protected CAPage<CAAuditEvent> page(
      final CADatabaseTransaction transaction,
      final JQKeysetRandomAccessPageDefinition page)
      throws CADatabaseException
    {
      final var context =
        transaction.createContext();
      final var querySpan =
        transaction.createQuerySpan(
          "CAAuditSearch.page");

      try {
        final var query =
          page.queryFields(context, List.of(
            AUDIT.ID,
            AUDIT.TIME,
            AUDIT.TYPE,
            AUDIT.USER_ID,
            AU_DATA
          ));

        querySpan.setAttribute(DB_STATEMENT, query.toString());

        final var items =
          query.fetch().map(record -> {
            final var userId =
              new CAUserID(record.get(AUDIT.USER_ID));

            return new CAAuditEvent(
              record.get(AUDIT.ID).longValue(),
              record.get(AUDIT.TIME),
              userId,
              record.get(AUDIT.TYPE),
              record.get(AU_DATA).data()
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
}
