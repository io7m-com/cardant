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
import com.io7m.cardant.model.CAItem;
import com.io7m.cardant.model.CAItems;
import com.io7m.cardant.model.CAModelCADatabaseQueriesType;
import com.io7m.cardant.model.CATags;
import com.io7m.cardant.protocol.inventory.v1.CA1CommandItemCreate;
import com.io7m.cardant.protocol.inventory.v1.CA1CommandItemList;
import com.io7m.cardant.protocol.inventory.v1.CA1CommandItemUpdate;
import com.io7m.cardant.protocol.inventory.v1.CA1CommandTagList;
import com.io7m.cardant.protocol.inventory.v1.CA1CommandTagsDelete;
import com.io7m.cardant.protocol.inventory.v1.CA1CommandTagsPut;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryCommandType;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageParserFactoryType;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageSerializerFactoryType;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryMessageType;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryResponseType;
import com.io7m.cardant.protocol.inventory.v1.CA1InventoryTransaction;
import com.io7m.cardant.protocol.inventory.v1.CA1ResponseError;
import com.io7m.cardant.protocol.inventory.v1.CA1ResponseErrorDetail;
import com.io7m.cardant.protocol.inventory.v1.CA1ResponseOK;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * Construct a command servlet.
 */

public final class CA1CommandServlet
  extends CA1AuthenticatedTransactionalServlet
{
  private static final Logger LOG =
    LoggerFactory.getLogger(CA1CommandServlet.class);

  private CADatabaseTransactionType transaction;
  private HttpServletResponse response;
  private CAModelCADatabaseQueriesType queries;

  /**
   * Construct a command servlet.
   * @param inParsers The parsers
   * @param inSerializers The serializers
   * @param inDatabase The database
   */

  public CA1CommandServlet(
    final CA1InventoryMessageParserFactoryType inParsers,
    final CA1InventoryMessageSerializerFactoryType inSerializers,
    final CADatabaseType inDatabase)
  {
    super(inParsers, inSerializers, inDatabase);
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
    } catch (final CADatabaseException e) {
      this.sendError(500, e.getMessage());
    } catch (final SerializeException | IOException e) {
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
      return this.executeCommand(received);
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
      "Expected a transaction or command.",
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
    for (int index = 0; index < commands.size(); ++index) {
      final var command = commands.get(index);
      mostRecent = this.executeCommand(command);
      if (mostRecent instanceof CA1ResponseError error) {
        failed = true;
        final var indexedMessage =
          String.format(
            "Command [%d] %s: %s",
            Integer.valueOf(index),
            command.getClass().getSimpleName(),
            error.message()
          );
        details.add(new CA1ResponseErrorDetail(indexedMessage));
      }
    }

    if (failed) {
      return new CA1ResponseError(500, "At least one command failed", details);
    }
    return mostRecent;
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

    throw new IllegalStateException();
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
