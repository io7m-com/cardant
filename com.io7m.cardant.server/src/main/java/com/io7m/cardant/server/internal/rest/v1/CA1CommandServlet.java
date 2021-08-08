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
import com.io7m.cardant.database.api.CADatabaseTransactionType;
import com.io7m.cardant.database.api.CADatabaseType;
import com.io7m.cardant.model.CAByteArray;
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.model.CAModelCADatabaseQueriesType;
import com.io7m.cardant.model.CATags;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageParserFactoryType;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageSerializerFactoryType;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemAttachmentPut;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemCreate;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemList;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandItemUpdate;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandTagList;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandTagsDelete;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1CommandTagsPut;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryCommandType;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryMessageType;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryResponseType;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1InventoryTransaction;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1ResponseError;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1ResponseErrorDetail;
import com.io7m.cardant.protocol.inventory.v1.messages.CA1ResponseOK;
import com.io7m.cardant.server.api.CAServerConfigurationLimits;
import com.io7m.cardant.server.internal.CAServerMessages;
import com.io7m.cardant.server.internal.rest.CAServerCommandExecuted;
import com.io7m.cardant.server.internal.rest.CAServerCommandFailed;
import com.io7m.cardant.server.internal.rest.CAServerEventType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.SubmissionPublisher;

import static com.io7m.cardant.database.api.CADatabaseErrorCode.ERROR_PARAMETERS_INVALID;

/**
 * A command servlet.
 */

public final class CA1CommandServlet
  extends CA1AuthenticatedTransactionalServlet
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CA1CommandServlet.class);

  private final SubmissionPublisher<CAServerEventType> events;
  private final CAServerConfigurationLimits limits;
  private CADatabaseTransactionType transaction;
  private HttpServletResponse response;
  private CAModelCADatabaseQueriesType queries;

  /**
   * Construct a command servlet.
   *
   * @param inEvents      The event publisher
   * @param inParsers     The parsers
   * @param inSerializers The serializers
   * @param inDatabase    The database
   * @param inMessages    The server string resources
   * @param inLimits      The server limits
   */

  public CA1CommandServlet(
    final SubmissionPublisher<CAServerEventType> inEvents,
    final CA1InventoryMessageParserFactoryType inParsers,
    final CA1InventoryMessageSerializerFactoryType inSerializers,
    final CAServerConfigurationLimits inLimits,
    final CAServerMessages inMessages,
    final CADatabaseType inDatabase)
  {
    super(inEvents, inParsers, inSerializers, inMessages, inDatabase);
    this.events = Objects.requireNonNull(inEvents, "inEvents");
    this.limits = Objects.requireNonNull(inLimits, "limits");
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
    this.transaction = dbTransaction;
    this.response = httpResponse;

    final CA1InventoryMessageType message;
    try {
      this.queries =
        dbTransaction.queries(CAModelCADatabaseQueriesType.class);
      message =
        this.parsers().parse(this.clientURI(), request.getInputStream());
    } catch (final CADatabaseException | ParseException | IOException e) {
      this.sendError(500, e.getMessage());
      return;
    }

    final var msgResponse =
      this.executeMessage(message);

    try {
      if (msgResponse instanceof CA1ResponseOK) {
        this.transaction.commit();
        this.response.setStatus(200);
        this.serializers()
          .serialize(
            this.clientURI(),
            this.response.getOutputStream(),
            msgResponse
          );
        return;
      }

      if (msgResponse instanceof CA1ResponseError error) {
        this.transaction.rollback();
        this.response.setStatus(error.status());
        this.serializers()
          .serialize(
            this.clientURI(),
            this.response.getOutputStream(),
            msgResponse
          );
      }
    } catch (final CADatabaseException | SerializeException | IOException e) {
      this.sendError(500, e.getMessage());
    }
  }

  private CA1InventoryResponseType executeMessage(
    final CA1InventoryMessageType message)
  {
    if (message instanceof CA1InventoryTransaction received) {
      return this.executeTransaction(received);
    }
    if (message instanceof CA1InventoryCommandType received) {
      return this.executeCommandAccounted(received);
    }
    if (message instanceof CA1InventoryResponseType received) {
      return this.executeResponse(received);
    }

    throw new IllegalStateException(
      "Unrecognized message: %s".formatted(message));
  }

  private CA1InventoryResponseType executeResponse(
    final CA1InventoryResponseType received)
  {
    return new CA1ResponseError(
      400,
      this.messages().format("errorUnexpectedInput"),
      List.of()
    );
  }

  private CA1InventoryResponseType executeTransaction(
    final CA1InventoryTransaction msgTransaction)
  {
    CA1InventoryResponseType mostRecent = new CA1ResponseOK(Optional.empty());
    boolean failed = false;

    final var details = new ArrayList<CA1ResponseErrorDetail>();
    final var commands = msgTransaction.commands();
    final var messages = this.messages();
    for (int index = 0; index < commands.size(); ++index) {
      final var command = commands.get(index);
      mostRecent = this.executeCommandAccounted(command);
      if (mostRecent instanceof CA1ResponseError error) {
        failed = true;
        final var indexedMessage =
          messages
            .format(
              "indexedMessage",
              Integer.valueOf(index),
              command.getClass().getSimpleName(),
              error.message()
            );
        details.add(new CA1ResponseErrorDetail(indexedMessage));
      }
    }

    if (failed) {
      return new CA1ResponseError(
        500,
        messages.format("errorTransaction"),
        details
      );
    }
    return mostRecent;
  }

  private CA1InventoryResponseType executeCommandAccounted(
    final CA1InventoryCommandType command)
  {
    final var result = this.executeCommand(command);
    if (result instanceof CA1ResponseOK) {
      this.commandExecuted();
      return result;
    }
    if (result instanceof CA1ResponseError) {
      this.commandFailed();
      return result;
    }
    throw new IllegalStateException();
  }

  private void commandFailed()
  {
    this.events.submit(new CAServerCommandFailed());
  }

  private void commandExecuted()
  {
    this.events.submit(new CAServerCommandExecuted());
  }

  private CA1InventoryResponseType executeCommand(
    final CA1InventoryCommandType command)
  {
    if (command instanceof CA1CommandTagList) {
      return this.executeCommandTagList();
    }
    if (command instanceof CA1CommandTagsPut tags) {
      return this.executeCommandTagsPut(tags);
    }
    if (command instanceof CA1CommandTagsDelete tags) {
      return this.executeCommandTagsDelete(tags);
    }
    if (command instanceof CA1CommandItemCreate itemCreate) {
      return this.executeCommandItemCreate(itemCreate);
    }
    if (command instanceof CA1CommandItemUpdate itemUpdate) {
      return this.executeCommandItemUpdate(itemUpdate);
    }
    if (command instanceof CA1CommandItemList) {
      return this.executeCommandItemList();
    }
    if (command instanceof CA1CommandItemAttachmentPut itemAttachmentPut) {
      return this.executeCommandItemAttachmentPut(itemAttachmentPut);
    }

    throw new IllegalStateException();
  }

  private CA1InventoryResponseType executeCommandItemAttachmentPut(
    final CA1CommandItemAttachmentPut itemAttachmentPut)
  {
    try {
      final var attachment =
        itemAttachmentPut.attachment();
      final var sizeLimitOpt =
        this.limits.itemAttachmentMaximumSizeOctets();

      if (sizeLimitOpt.isPresent()) {
        final var sizeLimit =
          sizeLimitOpt.getAsLong();

        final var data =
          attachment.data()
            .orElseThrow(() -> new CADatabaseException(
              ERROR_PARAMETERS_INVALID,
              this.messages().format("errorAttachmentMissingData"))
            );

        if (exceedsSizeLimit(sizeLimit, data)) {
          throw new CADatabaseException(
            ERROR_PARAMETERS_INVALID,
            this.messages().format("errorAttachmentTooLarge")
          );
        }
      }

      this.queries.itemAttachmentPut(attachment);
      return new CA1ResponseOK(Optional.empty());
    } catch (final CADatabaseException e) {
      return switch (e.errorCode()) {
        case ERROR_NONEXISTENT, ERROR_PARAMETERS_INVALID -> new CA1ResponseError(
          400,
          e.getMessage(),
          List.of());
        default -> new CA1ResponseError(500, e.getMessage(), List.of());
      };
    }
  }

  private static boolean exceedsSizeLimit(
    final long sizeLimit,
    final CAByteArray data)
  {
    final var sizeReceived =
      Integer.toUnsignedLong(data.data().length);

    return Long.compareUnsigned(sizeReceived, sizeLimit) > 0;
  }

  private CA1InventoryResponseType executeCommandTagsDelete(
    final CA1CommandTagsDelete tags)
  {
    try {
      for (final var tag : tags.tags().tags()) {
        this.queries.tagDelete(tag);
      }
      return new CA1ResponseOK(Optional.empty());
    } catch (final CADatabaseException e) {
      return new CA1ResponseError(500, e.getMessage(), List.of());
    }
  }

  private CA1InventoryResponseType executeCommandItemList()
  {
    try {
      final var ids =
        this.queries.itemList();
      final var items =
        new HashSet<CAItem>();

      for (final var id : ids) {
        final var itemOpt =
          this.queries.itemGet(id);
        itemOpt.ifPresent(items::add);
      }
      return new CA1ResponseOK(Optional.of(new CAItems(items)));
    } catch (final CADatabaseException e) {
      return new CA1ResponseError(500, e.getMessage(), List.of());
    }
  }

  private CA1InventoryResponseType executeCommandItemCreate(
    final CA1CommandItemCreate itemCreate)
  {
    try {
      final var itemId = itemCreate.id();
      this.queries.itemCreate(itemId);
      this.queries.itemNameSet(itemId, itemCreate.name());
      this.queries.itemCountSet(itemId, itemCreate.count());
      return new CA1ResponseOK(Optional.empty());
    } catch (final CADatabaseException e) {
      return switch (e.errorCode()) {
        case ERROR_DUPLICATE, ERROR_PARAMETERS_INVALID -> new CA1ResponseError(
          400,
          e.getMessage(),
          List.of());
        default -> new CA1ResponseError(500, e.getMessage(), List.of());
      };
    }
  }

  private CA1InventoryResponseType executeCommandItemUpdate(
    final CA1CommandItemUpdate itemUpdate)
  {
    try {
      final var itemId = itemUpdate.id();
      this.queries.itemNameSet(itemId, itemUpdate.name());
      this.queries.itemCountSet(itemId, itemUpdate.count());
      return new CA1ResponseOK(Optional.empty());
    } catch (final CADatabaseException e) {
      return switch (e.errorCode()) {
        case ERROR_NONEXISTENT, ERROR_PARAMETERS_INVALID -> new CA1ResponseError(
          400,
          e.getMessage(),
          List.of());
        default -> new CA1ResponseError(500, e.getMessage(), List.of());
      };
    }
  }

  private CA1InventoryResponseType executeCommandTagsPut(
    final CA1CommandTagsPut tags)
  {
    try {
      for (final var tag : tags.tags().tags()) {
        this.queries.tagPut(tag);
      }
      return new CA1ResponseOK(Optional.empty());
    } catch (final CADatabaseException e) {
      return new CA1ResponseError(500, e.getMessage(), List.of());
    }
  }

  private CA1InventoryResponseType executeCommandTagList()
  {
    try {
      return new CA1ResponseOK(Optional.of(new CATags(this.queries.tagList())));
    } catch (final CADatabaseException e) {
      return new CA1ResponseError(500, e.getMessage(), List.of());
    }
  }
}
