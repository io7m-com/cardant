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

import com.io7m.cardant.protocol.inventory.CAICommandAuditSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandAuditSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandAuditSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAICommandDebugInvalid;
import com.io7m.cardant.protocol.inventory.CAICommandDebugRandom;
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
import com.io7m.cardant.protocol.inventory.CAICommandItemTypesAssign;
import com.io7m.cardant.protocol.inventory.CAICommandItemTypesRevoke;
import com.io7m.cardant.protocol.inventory.CAICommandItemsRemove;
import com.io7m.cardant.protocol.inventory.CAICommandLocationAttachmentAdd;
import com.io7m.cardant.protocol.inventory.CAICommandLocationAttachmentRemove;
import com.io7m.cardant.protocol.inventory.CAICommandLocationGet;
import com.io7m.cardant.protocol.inventory.CAICommandLocationList;
import com.io7m.cardant.protocol.inventory.CAICommandLocationMetadataPut;
import com.io7m.cardant.protocol.inventory.CAICommandLocationMetadataRemove;
import com.io7m.cardant.protocol.inventory.CAICommandLocationPut;
import com.io7m.cardant.protocol.inventory.CAICommandLocationTypesAssign;
import com.io7m.cardant.protocol.inventory.CAICommandLocationTypesRevoke;
import com.io7m.cardant.protocol.inventory.CAICommandLogin;
import com.io7m.cardant.protocol.inventory.CAICommandRolesAssign;
import com.io7m.cardant.protocol.inventory.CAICommandRolesGet;
import com.io7m.cardant.protocol.inventory.CAICommandRolesRevoke;
import com.io7m.cardant.protocol.inventory.CAICommandType;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationGet;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationPut;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationRemove;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandTypeDeclarationSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageGetText;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageInstall;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageSearchBegin;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageSearchNext;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageSearchPrevious;
import com.io7m.cardant.protocol.inventory.CAICommandTypePackageUninstall;
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
    return switch (command) {
      case final CAICommandFilePut m -> {
        yield new CAICmdFilePut().execute(context, m);
      }
      case final CAICommandFileRemove m -> {
        yield new CAICmdFileRemove().execute(context, m);
      }
      case final CAICommandItemAttachmentAdd m -> {
        yield new CAICmdItemAttachmentAdd().execute(context, m);
      }
      case final CAICommandItemAttachmentRemove m -> {
        yield new CAICmdItemAttachmentRemove().execute(context, m);
      }
      case final CAICommandItemCreate m -> {
        yield new CAICmdItemCreate().execute(context, m);
      }
      case final CAICommandItemGet m -> {
        yield new CAICmdItemGet().execute(context, m);
      }
      case final CAICommandItemLocationsList m -> {
        yield new CAICmdItemLocationsList().execute(context, m);
      }
      case final CAICommandItemMetadataPut m -> {
        yield new CAICmdItemMetadataPut().execute(context, m);
      }
      case final CAICommandItemMetadataRemove m -> {
        yield new CAICmdItemMetadataRemove().execute(context, m);
      }
      case final CAICommandItemReposit m -> {
        yield new CAICmdItemReposit().execute(context, m);
      }
      case final CAICommandItemsRemove m -> {
        yield new CAICmdItemsRemove().execute(context, m);
      }
      case final CAICommandItemSetName m -> {
        yield new CAICmdItemSetName().execute(context, m);
      }
      case final CAICommandLocationGet m -> {
        yield new CAICmdLocationGet().execute(context, m);
      }
      case final CAICommandLocationList m -> {
        yield new CAICmdLocationsList().execute(context, m);
      }
      case final CAICommandLocationPut m -> {
        yield new CAICmdLocationPut().execute(context, m);
      }
      case final CAICommandItemSearchBegin m -> {
        yield new CAICmdItemSearchBegin().execute(context, m);
      }
      case final CAICommandItemSearchNext m -> {
        yield new CAICmdItemSearchNext().execute(context, m);
      }
      case final CAICommandItemSearchPrevious m -> {
        yield new CAICmdItemSearchPrevious().execute(context, m);
      }
      case final CAICommandRolesAssign m -> {
        yield new CAICmdRolesAssign().execute(context, m);
      }
      case final CAICommandRolesRevoke m -> {
        yield new CAICmdRolesRevoke().execute(context, m);
      }
      case final CAICommandRolesGet m -> {
        yield new CAICmdRolesGet().execute(context, m);
      }
      case final CAICommandFileSearchBegin m -> {
        yield new CAICmdFileSearchBegin().execute(context, m);
      }
      case final CAICommandFileSearchNext m -> {
        yield new CAICmdFileSearchNext().execute(context, m);
      }
      case final CAICommandFileSearchPrevious m -> {
        yield new CAICmdFileSearchPrevious().execute(context, m);
      }
      case final CAICommandFileGet m -> {
        yield new CAICmdFileGet().execute(context, m);
      }
      case final CAICommandTypeScalarSearchNext m -> {
        yield new CAICmdTypeScalarSearchNext().execute(context, m);
      }
      case final CAICommandTypeScalarSearchPrevious m -> {
        yield new CAICmdTypeScalarSearchPrevious().execute(context, m);
      }
      case final CAICommandTypeScalarSearchBegin m -> {
        yield new CAICmdTypeScalarSearchBegin().execute(context, m);
      }
      case final CAICommandTypeScalarPut m -> {
        yield new CAICmdTypeScalarPut().execute(context, m);
      }
      case final CAICommandTypeScalarGet m -> {
        yield new CAICmdTypeScalarGet().execute(context, m);
      }
      case final CAICommandTypeScalarRemove m -> {
        yield new CAICmdTypeScalarRemove().execute(context, m);
      }
      case final CAICommandTypeDeclarationSearchNext m -> {
        yield new CAICmdTypeDeclarationSearchNext().execute(context, m);
      }
      case final CAICommandTypeDeclarationSearchPrevious m -> {
        yield new CAICmdTypeDeclarationSearchPrevious().execute(context, m);
      }
      case final CAICommandTypeDeclarationSearchBegin m -> {
        yield new CAICmdTypeDeclarationSearchBegin().execute(context, m);
      }
      case final CAICommandTypeDeclarationPut m -> {
        yield new CAICmdTypeDeclarationPut().execute(context, m);
      }
      case final CAICommandTypeDeclarationGet m -> {
        yield new CAICmdTypeDeclarationGet().execute(context, m);
      }
      case final CAICommandTypeDeclarationRemove m -> {
        yield new CAICmdTypeDeclarationRemove().execute(context, m);
      }
      case final CAICommandItemTypesAssign m -> {
        yield new CAICmdItemTypesAssign().execute(context, m);
      }
      case final CAICommandItemTypesRevoke m -> {
        yield new CAICmdItemTypesRevoke().execute(context, m);
      }
      case final CAICommandLocationTypesAssign m -> {
        yield new CAICmdLocationTypesAssign().execute(context, m);
      }
      case final CAICommandLocationTypesRevoke m -> {
        yield new CAICmdLocationTypesRevoke().execute(context, m);
      }
      case final CAICommandLocationMetadataPut m -> {
        yield new CAICmdLocationMetadataPut().execute(context, m);
      }
      case final CAICommandLocationMetadataRemove m -> {
        yield new CAICmdLocationMetadataRemove().execute(context, m);
      }
      case final CAICommandLocationAttachmentAdd m -> {
        yield new CAICmdLocationAttachmentAdd().execute(context, m);
      }
      case final CAICommandLocationAttachmentRemove m -> {
        yield new CAICmdLocationAttachmentRemove().execute(context, m);
      }
      case final CAICommandAuditSearchBegin m -> {
        yield new CAICmdAuditSearchBegin().execute(context, m);
      }
      case final CAICommandAuditSearchNext m -> {
        yield new CAICmdAuditSearchNext().execute(context, m);
      }
      case final CAICommandAuditSearchPrevious m -> {
        yield new CAICmdAuditSearchPrevious().execute(context, m);
      }
      case final CAICommandDebugInvalid m -> {
        throw new IllegalStateException();
      }
      case final CAICommandDebugRandom m -> {
        throw new IllegalStateException();
      }
      case final CAICommandLogin m -> {
        throw new IllegalStateException();
      }
      case final CAICommandTypePackageSearchBegin m -> {
        throw new IllegalStateException("Unimplemented code.");
      }
      case final CAICommandTypePackageSearchNext m -> {
        throw new IllegalStateException("Unimplemented code.");
      }
      case final CAICommandTypePackageSearchPrevious m -> {
        throw new IllegalStateException("Unimplemented code.");
      }
      case final CAICommandTypePackageGetText m -> {
        throw new IllegalStateException("Unimplemented code.");
      }
      case final CAICommandTypePackageInstall m -> {
        throw new IllegalStateException("Unimplemented code.");
      }
      case final CAICommandTypePackageUninstall m -> {
        throw new IllegalStateException("Unimplemented code.");
      }
    };
  }
}
