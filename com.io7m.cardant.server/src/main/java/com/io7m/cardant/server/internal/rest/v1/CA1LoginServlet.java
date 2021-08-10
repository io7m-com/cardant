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

import com.io7m.anethum.common.ParseException;
import com.io7m.anethum.common.SerializeException;
import com.io7m.cardant.database.api.CADatabaseException;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.model.CAModelCADatabaseQueriesType;
import com.io7m.cardant.model.CAUsers;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageParserFactoryType;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageSerializerFactoryType;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandLoginUsernamePassword;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryMessageType;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1ResponseError;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1ResponseOK;
import com.io7m.cardant.server.internal.rest.CAServerEventType;
import com.io7m.cardant.server.internal.rest.CAServerLoginFailed;
import com.io7m.cardant.server.internal.rest.CAServerLoginSucceeded;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.SubmissionPublisher;

import static com.io7m.cardant.server.internal.rest.CAMediaTypes.applicationCardantXML;

/**
 * A servlet that performs authentication.
 */

public final class CA1LoginServlet extends HttpServlet
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CA1LoginServlet.class);

  private final CA1InventoryMessageSerializerFactoryType serializers;
  private final CADatabaseType database;
  private final SubmissionPublisher<CAServerEventType> events;
  private final CA1InventoryMessageParserFactoryType parsers;

  /**
   * Construct a servlet.
   *
   * @param inEvents      An event sink
   * @param inParsers     A provider of inventory message parsers
   * @param inSerializers A provider of inventory message serializers
   * @param inDatabase    A database
   */

  public CA1LoginServlet(
    final SubmissionPublisher<CAServerEventType> inEvents,
    final CA1InventoryMessageParserFactoryType inParsers,
    final CA1InventoryMessageSerializerFactoryType inSerializers,
    final CADatabaseType inDatabase)
  {
    this.events =
      Objects.requireNonNull(inEvents, "inEvents");
    this.parsers =
      Objects.requireNonNull(inParsers, "parsers");
    this.serializers =
      Objects.requireNonNull(inSerializers, "serializers");
    this.database =
      Objects.requireNonNull(inDatabase, "database");
  }

  private static URI uriOf(
    final HttpServletRequest request)
  {
    return URI.create(
      String.format(
        "tcp://%s:%d/",
        request.getRemoteAddr(),
        Integer.valueOf(request.getRemotePort()))
    );
  }

  private static String clientOf(
    final HttpServletRequest request)
  {
    return new StringBuilder(64)
      .append('[')
      .append(request.getRemoteAddr())
      .append(':')
      .append(request.getRemotePort())
      .append(']')
      .toString();
  }

  private void sendMessage(
    final HttpServletResponse response,
    final CA1InventoryMessageType message)
    throws IOException, SerializeException
  {
    response.setContentType(applicationCardantXML());
    try (var output = response.getOutputStream()) {
      this.serializers.serialize(URI.create("urn:output"), output, message);
      output.flush();
    }
  }


  @Override
  protected void service(
    final HttpServletRequest request,
    final HttpServletResponse response)
    throws IOException
  {
    MDC.put("client", clientOf(request));

    try {
      final CA1InventoryMessageType loginRequest;

      try {
        loginRequest =
          this.parsers.parse(uriOf(request), request.getInputStream());
      } catch (final IOException | ParseException e) {
        response.setStatus(401);
        this.sendMessage(
          response,
          new CA1ResponseError(401, "Login failed", List.of()));
        this.events.submit(new CAServerLoginFailed());
        return;
      }

      if (loginRequest instanceof CA1CommandLoginUsernamePassword creds) {
        if (this.checkLogin(creds)) {
          LOG.info("user '{}' logged in", creds.user());
          final var session = request.getSession();
          session.setAttribute("userName", creds.user());
          response.setStatus(200);
          this.sendMessage(response, new CA1ResponseOK(Optional.empty()));
          this.events.submit(new CAServerLoginSucceeded());
          return;
        }
      }

      LOG.info("authentication failed");
      response.setStatus(401);
      this.sendMessage(
        response,
        new CA1ResponseError(401, "Login failed", List.of()));
      this.events.submit(new CAServerLoginFailed());
    } catch (final IOException e) {
      LOG.error("i/o: ", e);
      throw e;
    } catch (final CADatabaseException e) {
      LOG.error("database: ", e);
      throw new IOException(e);
    } catch (final GeneralSecurityException e) {
      LOG.error("security: ", e);
      throw new IOException(e);
    } catch (final SerializeException e) {
      LOG.error("serialization: ", e);
      throw new IOException(e);
    } finally {
      MDC.remove("client");
    }
  }

  private boolean checkLogin(
    final CA1CommandLoginUsernamePassword creds)
    throws CADatabaseException, GeneralSecurityException, IOException
  {
    try (var connection = this.database.openConnection()) {
      try (var transaction = connection.beginTransaction()) {
        final var queries =
          transaction.queries(CAModelCADatabaseQueriesType.class);

        final var userName = creds.user();
        final var userOpt = queries.userGetByName(userName);
        if (userOpt.isPresent()) {
          return CAUsers.checkUserPassword(userOpt.get(), creds.password());
        }

        LOG.info("no such user '{}'", userName);
        return false;
      }
    }
  }
}
