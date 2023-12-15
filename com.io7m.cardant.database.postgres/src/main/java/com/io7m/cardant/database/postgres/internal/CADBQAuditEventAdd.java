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


import com.io7m.cardant.database.api.CADatabaseQueriesAuditType;
import com.io7m.cardant.database.api.CADatabaseUnit;
import com.io7m.cardant.model.CAAuditEvent;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.postgres.extensions.types.Hstore;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import static com.io7m.cardant.database.postgres.internal.CADBQAuditEventSearch.AU_DATA;
import static com.io7m.cardant.database.postgres.internal.Tables.AUDIT;

/**
 * Add audit events.
 */

public final class CADBQAuditEventAdd
  extends CADBQAbstract<CAAuditEvent, CADatabaseUnit>
  implements CADatabaseQueriesAuditType.EventAddType
{
  private static final CADBQueryProviderType.Service<CAAuditEvent, CADatabaseUnit, EventAddType> SERVICE =
    new CADBQueryProviderType.Service<>(EventAddType.class, CADBQAuditEventAdd::new);

  /**
   * Construct a query.
   *
   * @param transaction The transaction
   */

  public CADBQAuditEventAdd(
    final CADatabaseTransaction transaction)
  {
    super(transaction);
  }

  private static Query auditEvent(
    final DSLContext context,
    final CAAuditEvent parameters)
  {
    return context.insertInto(AUDIT)
        .set(AUDIT.TYPE, parameters.type())
        .set(AUDIT.TIME, parameters.time())
        .set(AUDIT.USER_ID, parameters.owner())
        .set(AU_DATA, Hstore.valueOf(parameters.data()));
  }

  @SafeVarargs
  static Query auditEvent(
    final DSLContext context,
    final OffsetDateTime time,
    final UUID user,
    final String type,
    final Map.Entry<String, String>... entries)
  {
    return auditEvent(
      context,
      new CAAuditEvent(0L, time, user, type, Map.ofEntries(entries))
    );
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
    final CAAuditEvent parameters)
  {
    auditEvent(context, parameters).execute();
    return CADatabaseUnit.UNIT;
  }
}
