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
import com.io7m.cardant.database.api.CADatabaseQueryType;
import com.io7m.cardant.strings.CAStringConstantType;
import com.io7m.cardant.strings.CAStrings;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static com.io7m.cardant.database.postgres.internal.CADatabaseExceptions.handleDatabaseException;

abstract class CADBQAbstract<P, R>
  implements CADatabaseQueryType<P, R>
{
  private final CADatabaseTransaction transaction;
  private final TreeMap<String, String> attributes;

  protected CADBQAbstract(
    final CADatabaseTransaction inTransaction)
  {
    this.transaction =
      Objects.requireNonNull(inTransaction, "transaction");
    this.attributes =
      new TreeMap<String, String>();
  }

  protected final Span transactionSpan()
  {
    return this.transaction.span();
  }

  protected final CADatabaseTransaction transaction()
  {
    return this.transaction;
  }

  private CAStrings messages()
  {
    return this.transaction.connection()
      .database()
      .messages();
  }

  protected final String local(
    final CAStringConstantType constant)
  {
    return this.messages().format(constant);
  }

  protected final void setAttribute(
    final CAStringConstantType name,
    final String value)
  {
    this.attributes.put(this.local(name), value);
  }

  @Override
  public final R execute(
    final P parameters)
    throws CADatabaseException
  {
    Objects.requireNonNull(parameters, "parameters");

    final var currentTransaction =
      this.transaction();
    final var context =
      currentTransaction.createContext();
    final var querySpan =
      currentTransaction.createQuerySpan(this.getClass().getSimpleName());

    try (var ignored = querySpan.makeCurrent()) {
      return this.onExecute(context, parameters);
    } catch (final DataAccessException e) {
      querySpan.recordException(e);
      querySpan.setStatus(StatusCode.ERROR);
      throw handleDatabaseException(currentTransaction, e, this.attributes);
    } finally {
      querySpan.end();
    }
  }

  protected abstract R onExecute(
    DSLContext context,
    P parameters)
    throws CADatabaseException;

  protected final Map<String, String> attributes()
  {
    return this.attributes;
  }
}
