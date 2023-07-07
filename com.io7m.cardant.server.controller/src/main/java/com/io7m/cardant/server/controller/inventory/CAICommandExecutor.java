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

package com.io7m.cardant.server.controller.inventory;

import com.io7m.cardant.protocol.inventory.CAICommandFileGet;
import com.io7m.cardant.protocol.inventory.CAICommandFilePut;
import com.io7m.cardant.protocol.inventory.CAICommandFileRemove;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandFileSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentRemove;
import com.io7m.cardant.protocol.inventory.CAICommandItemCreate;
import com.io7m.cardant.protocol.inventory.CAICommandItemGet;
import com.io7m.cardant.protocol.inventory.CAICommandItemLocationsList;
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataPut;
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataRemove;
import com.io7m.cardant.protocol.inventory.CAICommandItemReposit;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAICommandItemSetName;
import com.io7m.cardant.protocol.inventory.CAICommandItemsRemove;
import com.io7m.cardant.protocol.inventory.CAICommandLocationGet;
import com.io7m.cardant.protocol.inventory.CAICommandLocationList;
import com.io7m.cardant.protocol.inventory.CAICommandLocationPut;
import com.io7m.cardant.protocol.inventory.CAICommandRolesAssign;
import com.io7m.cardant.protocol.inventory.CAICommandRolesGet;
import com.io7m.cardant.protocol.inventory.CAICommandRolesRevoke;
import com.io7m.cardant.protocol.inventory.CAICommandTagList;
import com.io7m.cardant.protocol.inventory.CAICommandTagsDelete;
import com.io7m.cardant.protocol.inventory.CAICommandTagsPut;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarGet;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarPut;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarRemove;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandTypeScalarSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutorType;

/**
 * A command executor for public commands.
 */

public final class CAICommandExecutor
  implements CACommandExecutorType<
    CAICommandContext,
    CAICommandType<? extends CAIResponseType>,
    CAIResponseType>
{
  /**
   * A command executor for public commands.
   */

  public CAICommandExecutor()
  {

  }

  @Override
  public CAIResponseType execute(
    final CAICommandContext context,
    final CAICommandType<? extends CAIResponseType> command)
    throws CACommandExecutionFailure
  {
    final var span =
      context.tracer()
        .spanBuilder(command.getClass().getSimpleName())
        .startSpan();

    try (var ignored = span.makeCurrent()) {
      return executeCommand(context, command);
    } catch (final Throwable e) {
      span.recordException(e);
      throw e;
    } finally {
      span.end();
    }
  }

  private static CAIResponseType executeCommand(
    final CAICommandContext context,
    final CAICommandType<? extends CAIResponseType> command)
    throws CACommandExecutionFailure
  {
    if (command instanceof final CAICommandFilePut m) {
      return new CAICmdFilePut().execute(context, m);
    }
    if (command instanceof final CAICommandFileRemove m) {
      return new CAICmdFileRemove().execute(context, m);
    }
    if (command instanceof final CAICommandItemAttachmentAdd m) {
      return new CAICmdItemAttachmentAdd().execute(context, m);
    }
    if (command instanceof final CAICommandItemAttachmentRemove m) {
      return new CAICmdItemAttachmentRemove().execute(context, m);
    }
    if (command instanceof final CAICommandItemCreate m) {
      return new CAICmdItemCreate().execute(context, m);
    }
    if (command instanceof final CAICommandItemGet m) {
      return new CAICmdItemGet().execute(context, m);
    }
    if (command instanceof final CAICommandItemLocationsList m) {
      return new CAICmdItemLocationsList().execute(context, m);
    }
    if (command instanceof final CAICommandItemMetadataPut m) {
      return new CAICmdItemMetadataPut().execute(context, m);
    }
    if (command instanceof final CAICommandItemMetadataRemove m) {
      return new CAICmdItemMetadataRemove().execute(context, m);
    }
    if (command instanceof final CAICommandItemReposit m) {
      return new CAICmdItemReposit().execute(context, m);
    }
    if (command instanceof final CAICommandItemsRemove m) {
      return new CAICmdItemsRemove().execute(context, m);
    }
    if (command instanceof final CAICommandItemSetName m) {
      return new CAICmdItemSetName().execute(context, m);
    }
    if (command instanceof final CAICommandLocationGet m) {
      return new CAICmdLocationGet().execute(context, m);
    }
    if (command instanceof final CAICommandLocationList m) {
      return new CAICmdLocationsList().execute(context, m);
    }
    if (command instanceof final CAICommandLocationPut m) {
      return new CAICmdLocationPut().execute(context, m);
    }
    if (command instanceof final CAICommandTagList m) {
      return new CAICmdTagsList().execute(context, m);
    }
    if (command instanceof final CAICommandTagsDelete m) {
      return new CAICmdTagsDelete().execute(context, m);
    }
    if (command instanceof final CAICommandTagsPut m) {
      return new CAICmdTagsPut().execute(context, m);
    }
    if (command instanceof final CAICommandItemSearchBegin m) {
      return new CAICmdItemSearchBegin().execute(context, m);
    }
    if (command instanceof final CAICommandItemSearchNext m) {
      return new CAICmdItemSearchNext().execute(context, m);
    }
    if (command instanceof final CAICommandItemSearchPrevious m) {
      return new CAICmdItemSearchPrevious().execute(context, m);
    }
    if (command instanceof final CAICommandRolesAssign m) {
      return new CAICmdRolesAssign().execute(context, m);
    }
    if (command instanceof final CAICommandRolesRevoke m) {
      return new CAICmdRolesRevoke().execute(context, m);
    }
    if (command instanceof final CAICommandRolesGet m) {
      return new CAICmdRolesGet().execute(context, m);
    }
    if (command instanceof final CAICommandFileSearchBegin m) {
      return new CAICmdFileSearchBegin().execute(context, m);
    }
    if (command instanceof final CAICommandFileSearchNext m) {
      return new CAICmdFileSearchNext().execute(context, m);
    }
    if (command instanceof final CAICommandFileSearchPrevious m) {
      return new CAICmdFileSearchPrevious().execute(context, m);
    }
    if (command instanceof final CAICommandFileGet m) {
      return new CAICmdFileGet().execute(context, m);
    }
    if (command instanceof final CAICommandTypeScalarSearchNext m) {
      return new CAICmdTypeScalarSearchNext().execute(context, m);
    }
    if (command instanceof final CAICommandTypeScalarSearchPrevious m) {
      return new CAICmdTypeScalarSearchPrevious().execute(context, m);
    }
    if (command instanceof final CAICommandTypeScalarSearchBegin m) {
      return new CAICmdTypeScalarSearchBegin().execute(context, m);
    }
    if (command instanceof final CAICommandTypeScalarPut m) {
      return new CAICmdTypeScalarPut().execute(context, m);
    }
    if (command instanceof final CAICommandTypeScalarGet m) {
      return new CAICmdTypeScalarGet().execute(context, m);
    }
    if (command instanceof final CAICommandTypeScalarRemove m) {
      return new CAICmdTypeScalarRemove().execute(context, m);
    }

    throw new IllegalStateException();
  }
}
