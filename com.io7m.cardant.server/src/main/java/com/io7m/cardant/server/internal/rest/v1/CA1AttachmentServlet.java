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
import com.io7m.cardant.model.CAItemAttachmentID;
import com.io7m.cardant.model.CAModelCADatabaseQueriesType;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageParserFactoryType;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageSerializerFactoryType;
import com.io7m.cardant.server.internal.CAServerMessages;
import com.io7m.cardant.server.internal.rest.CAServerEventType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.HexFormat;
import java.util.concurrent.SubmissionPublisher;

/**
 * An attachment servlet.
 */

public final class CA1AttachmentServlet
  extends CA1AuthenticatedTransactionalServlet
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CA1AttachmentServlet.class);

  /**
   * Construct an attachment servlet.
   *
   * @param inEvents      The event publisher
   * @param inParsers     The parsers
   * @param inSerializers The serializers
   * @param inDatabase    The database
   * @param inMessages    The server string resources
   */

  public CA1AttachmentServlet(
    final SubmissionPublisher<CAServerEventType> inEvents,
    final CA1InventoryMessageParserFactoryType inParsers,
    final CA1InventoryMessageSerializerFactoryType inSerializers,
    final CAServerMessages inMessages,
    final CADatabaseType inDatabase)
  {
    super(inEvents, inParsers, inSerializers, inMessages, inDatabase);
  }

  @Override
  protected Logger logger()
  {
    return LOG;
  }

  @Override
  protected void serviceTransactional(
    final CADatabaseTransactionType dbTransaction,
    final HttpServletRequest request,
    final HttpServletResponse httpResponse,
    final HttpSession session)
  {
    final var messages = this.messages();

    final var idText = request.getParameter("id");
    if (idText == null) {
      this.sendError(400, messages.format("errorMissingId"));
      return;
    }

    final CAItemAttachmentID id;
    try {
      id = CAItemAttachmentID.of(idText);
    } catch (final Exception e) {
      this.sendError(400, e.getMessage());
      return;
    }

    try {
      final var queries =
        dbTransaction.queries(CAModelCADatabaseQueriesType.class);
      final var attachmentOpt =
        queries.itemAttachmentGet(id, true);

      if (attachmentOpt.isEmpty()) {
        this.sendError(404, messages.format("errorNoSuchAttachment"));
        return;
      }

      final var attachment =
        attachmentOpt.get();
      final var attachmentData =
        attachment.data().orElseThrow();
      final var data =
        attachmentData.data();

      final var digestDecoded =
        HexFormat.of().parseHex(attachment.hashValue());
      final var base64 =
        Base64.getMimeEncoder();

      final var digestBuilder = new StringBuilder(64);
      digestBuilder.append(attachment.hashAlgorithm());
      digestBuilder.append('=');
      digestBuilder.append(base64.encodeToString(digestDecoded));

      httpResponse.setStatus(200);
      httpResponse.setContentLength(data.length);
      httpResponse.setContentType(attachment.mediaType());
      httpResponse.setHeader("Digest", digestBuilder.toString());

      try (var outputStream = httpResponse.getOutputStream()) {
        outputStream.write(data);
        outputStream.flush();
      }
    } catch (final Exception e) {
      this.sendError(500, e.getMessage());
    }
  }
}
