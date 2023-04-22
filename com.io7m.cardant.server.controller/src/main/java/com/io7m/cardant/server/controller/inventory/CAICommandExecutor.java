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

import com.io7m.cardant.protocol.inventory.CAICommandFilePut;
import com.io7m.cardant.protocol.inventory.CAICommandFileRemove;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAICommandItemAttachmentRemove;
import com.io7m.cardant.protocol.inventory.CAICommandItemCreate;
import com.io7m.cardant.protocol.inventory.CAICommandItemGet;
import com.io7m.cardant.protocol.inventory.CAICommandItemList;
import com.io7m.cardant.protocol.inventory.CAICommandItemLocationsList;
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataPut;
import com.io7m.cardant.protocol.inventory.CAICommandItemMetadataRemove;
import com.io7m.cardant.protocol.inventory.CAICommandItemReposit;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandItemSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAICommandItemUpdate;
import com.io7m.cardant.protocol.inventory.CAICommandItemsRemove;
import com.io7m.cardant.protocol.inventory.CAICommandLocationGet;
import com.io7m.cardant.protocol.inventory.CAICommandLocationList;
import com.io7m.cardant.protocol.inventory.CAICommandLocationPut;
import com.io7m.cardant.protocol.inventory.CAICommandTagList;
import com.io7m.cardant.protocol.inventory.CAICommandTagsDelete;
import com.io7m.cardant.protocol.inventory.CAICommandTagsPut;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAIResponseType;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutionFailure;
import com.io7m.cardant.server.controller.command_exec.CACommandExecutorType;

import java.io.IOException;

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
    throws CACommandExecutionFailure, IOException, InterruptedException
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
    throws CACommandExecutionFailure, IOException, InterruptedException
  {
    if (command instanceof CAICommandFilePut m) {
      return new CAICmdFilePut().execute(context, m);
    }
    if (command instanceof CAICommandFileRemove m) {
      return new CAICmdFileRemove().execute(context, m);
    }
    if (command instanceof CAICommandItemAttachmentAdd m) {
      return new CAICmdItemAttachmentAdd().execute(context, m);
    }
    if (command instanceof CAICommandItemAttachmentRemove m) {
      return new CAICmdItemAttachmentRemove().execute(context, m);
    }
    if (command instanceof CAICommandItemCreate m) {
      return new CAICmdItemCreate().execute(context, m);
    }
    if (command instanceof CAICommandItemGet m) {
      return new CAICmdItemGet().execute(context, m);
    }
    if (command instanceof CAICommandItemList m) {
      return new CAICmdItemsList().execute(context, m);
    }
    if (command instanceof CAICommandItemLocationsList m) {
      return new CAICmdItemLocationsList().execute(context, m);
    }
    if (command instanceof CAICommandItemMetadataPut m) {
      return new CAICmdItemMetadataPut().execute(context, m);
    }
    if (command instanceof CAICommandItemMetadataRemove m) {
      return new CAICmdItemMetadataRemove().execute(context, m);
    }
    if (command instanceof CAICommandItemReposit m) {
      return new CAICmdItemReposit().execute(context, m);
    }
    if (command instanceof CAICommandItemsRemove m) {
      return new CAICmdItemsRemove().execute(context, m);
    }
    if (command instanceof CAICommandItemUpdate m) {
      return new CAICmdItemUpdate().execute(context, m);
    }
    if (command instanceof CAICommandLocationGet m) {
      throw new IllegalStateException();
    }
    if (command instanceof CAICommandLocationList m) {
      return new CAICmdLocationsList().execute(context, m);
    }
    if (command instanceof CAICommandLocationPut m) {
      return new CAICmdLocationPut().execute(context, m);
    }
    if (command instanceof CAICommandTagList m) {
      return new CAICmdTagsList().execute(context, m);
    }
    if (command instanceof CAICommandTagsDelete m) {
      return new CAICmdTagsDelete().execute(context, m);
    }
    if (command instanceof CAICommandTagsPut m) {
      return new CAICmdTagsPut().execute(context, m);
    }
    if (command instanceof CAICommandItemSearchBegin m) {
      return new CAICmdItemSearchBegin().execute(context, m);
    }
    if (command instanceof CAICommandItemSearchNext m) {
      return new CAICmdItemSearchNext().execute(context, m);
    }
    if (command instanceof CAICommandItemSearchPrevious m) {
      return new CAICmdItemSearchPrevious().execute(context, m);
    }

    throw new IllegalStateException();
  }
}
