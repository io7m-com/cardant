/*
 * Copyright © 2021 Mark Raynsford <code@io7m.com> https://www.io7m.com
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

package com.io7m.cardant.server.internal.rest.v1;

import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.protocol.inventory.api.CAMessageParserFactoryType;
import com.io7m.cardant.protocol.inventory.api.CAMessageSerializerFactoryType;
import com.io7m.cardant.server.internal.CAServerMessages;
import com.io7m.cardant.server.internal.rest.CAServerEventType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.SubmissionPublisher;

/**
 * A servlet that executes within a database transaction.
 */

public abstract class CA1AuthenticatedTransactionalServlet
  extends CA1AuthenticatedServlet
{
  private final CADatabaseType database;
  private final CAMessageParserFactoryType parsers;

  protected CA1AuthenticatedTransactionalServlet(
    final SubmissionPublisher<CAServerEventType> inEvents,
    final CAMessageParserFactoryType inParsers,
    final CAMessageSerializerFactoryType inSerializers,
    final CAServerMessages inMessages,
    final CADatabaseType inDatabase)
  {
    super(inEvents, inSerializers, inMessages);

    this.parsers =
      Objects.requireNonNull(inParsers, "parsers");
    this.database =
      Objects.requireNonNull(inDatabase, "database");
  }

  /**
   * @return The underlying database
   */

  public final CADatabaseType database()
  {
    return this.database;
  }

  /**
   * @return An inventory message parser factory
   */

  public final CAMessageParserFactoryType parsers()
  {
    return this.parsers;
  }

  protected abstract void serviceTransactional(
    CADatabaseTransactionType transaction,
    HttpServletRequest request,
    HttpServletResponse response,
    HttpSession session)
    throws Exception;

  @Override
  protected final void serviceAuthenticated(
    final HttpServletRequest request,
    final HttpServletResponse servletResponse,
    final HttpSession session)
    throws Exception
  {
    Objects.requireNonNull(request, "request");
    Objects.requireNonNull(servletResponse, "response");
    Objects.requireNonNull(session, "session");

    try (var connection = this.database.openConnection()) {
      try (var transaction = connection.beginTransaction()) {
        this.serviceTransactional(
          transaction,
          request,
          servletResponse,
          session);
      }
    } catch (final Exception e) {
      throw new IOException(e);
    }
  }
}
