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

package com.io7m.cardant.shell;

import com.io7m.cardant.client.api.CAClientConfiguration;
import com.io7m.cardant.client.api.CAClientSynchronousType;
import com.io7m.cardant.client.basic.CAClients;
import com.io7m.cardant.client.preferences.api.CAPreferencesServiceType;
import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.shell.internal.CAShell;
import com.io7m.cardant.shell.internal.CAShellCmdAuditSearchBegin;
import com.io7m.cardant.shell.internal.CAShellCmdAuditSearchNext;
import com.io7m.cardant.shell.internal.CAShellCmdAuditSearchPrevious;
import com.io7m.cardant.shell.internal.CAShellCmdBookmarkList;
import com.io7m.cardant.shell.internal.CAShellCmdBookmarkLogin;
import com.io7m.cardant.shell.internal.CAShellCmdBookmarkPut;
import com.io7m.cardant.shell.internal.CAShellCmdBookmarkRemove;
import com.io7m.cardant.shell.internal.CAShellCmdFileGet;
import com.io7m.cardant.shell.internal.CAShellCmdFilePut;
import com.io7m.cardant.shell.internal.CAShellCmdFileSearchBegin;
import com.io7m.cardant.shell.internal.CAShellCmdFileSearchNext;
import com.io7m.cardant.shell.internal.CAShellCmdFileSearchPrevious;
import com.io7m.cardant.shell.internal.CAShellCmdFilesRecent;
import com.io7m.cardant.shell.internal.CAShellCmdHelp;
import com.io7m.cardant.shell.internal.CAShellCmdItemAttachmentAdd;
import com.io7m.cardant.shell.internal.CAShellCmdItemAttachmentRemove;
import com.io7m.cardant.shell.internal.CAShellCmdItemCreate;
import com.io7m.cardant.shell.internal.CAShellCmdItemGet;
import com.io7m.cardant.shell.internal.CAShellCmdItemMetadataPut;
import com.io7m.cardant.shell.internal.CAShellCmdItemMetadataRemove;
import com.io7m.cardant.shell.internal.CAShellCmdItemRepositAdd;
import com.io7m.cardant.shell.internal.CAShellCmdItemRepositMove;
import com.io7m.cardant.shell.internal.CAShellCmdItemRepositRemove;
import com.io7m.cardant.shell.internal.CAShellCmdItemSearchBegin;
import com.io7m.cardant.shell.internal.CAShellCmdItemSearchNext;
import com.io7m.cardant.shell.internal.CAShellCmdItemSearchPrevious;
import com.io7m.cardant.shell.internal.CAShellCmdLocationAttachmentAdd;
import com.io7m.cardant.shell.internal.CAShellCmdLocationAttachmentRemove;
import com.io7m.cardant.shell.internal.CAShellCmdLocationGet;
import com.io7m.cardant.shell.internal.CAShellCmdLocationList;
import com.io7m.cardant.shell.internal.CAShellCmdLocationPut;
import com.io7m.cardant.shell.internal.CAShellCmdLogin;
import com.io7m.cardant.shell.internal.CAShellCmdLogout;
import com.io7m.cardant.shell.internal.CAShellCmdRolesAssign;
import com.io7m.cardant.shell.internal.CAShellCmdRolesGet;
import com.io7m.cardant.shell.internal.CAShellCmdRolesRevoke;
import com.io7m.cardant.shell.internal.CAShellCmdSelf;
import com.io7m.cardant.shell.internal.CAShellCmdSet;
import com.io7m.cardant.shell.internal.CAShellCmdType;
import com.io7m.cardant.shell.internal.CAShellCmdTypeDeclarationCreate;
import com.io7m.cardant.shell.internal.CAShellCmdTypeDeclarationFieldPut;
import com.io7m.cardant.shell.internal.CAShellCmdTypeDeclarationFieldRemove;
import com.io7m.cardant.shell.internal.CAShellCmdTypeDeclarationGet;
import com.io7m.cardant.shell.internal.CAShellCmdTypeDeclarationSearchBegin;
import com.io7m.cardant.shell.internal.CAShellCmdTypeDeclarationSearchNext;
import com.io7m.cardant.shell.internal.CAShellCmdTypeDeclarationSearchPrevious;
import com.io7m.cardant.shell.internal.CAShellCmdTypeScalarGet;
import com.io7m.cardant.shell.internal.CAShellCmdTypeScalarPut;
import com.io7m.cardant.shell.internal.CAShellCmdTypeScalarRemove;
import com.io7m.cardant.shell.internal.CAShellCmdTypeScalarSearchBegin;
import com.io7m.cardant.shell.internal.CAShellCmdTypeScalarSearchNext;
import com.io7m.cardant.shell.internal.CAShellCmdTypeScalarSearchPrevious;
import com.io7m.cardant.shell.internal.CAShellCmdVersion;
import com.io7m.cardant.shell.internal.CAShellLoginTracker;
import com.io7m.cardant.shell.internal.CAShellOptions;
import com.io7m.cardant.shell.internal.CAShellTerminalHolder;
import com.io7m.cardant.strings.CAStrings;
import com.io7m.repetoir.core.RPServiceDirectory;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The basic shell.
 */

public final class CAShells implements CAShellFactoryType
{
  /**
   * The basic shell.
   */

  public CAShells()
  {

  }

  @Override
  public CAShellType create(
    final CAShellConfiguration configuration)
    throws CAException
  {
    final var client =
      new CAClients()
        .openSynchronousClient(
          new CAClientConfiguration(
            configuration.locale(),
            configuration.clock()
          )
        );
    final var terminal =
      configuration.terminal()
        .orElseGet(() -> {
          try {
            return TerminalBuilder.terminal();
          } catch (final IOException e) {
            throw new UncheckedIOException(e);
          }
        });
    final var writer =
      terminal.writer();

    final var services = new RPServiceDirectory();
    services.register(CAClientSynchronousType.class, client);
    services.register(CAShellOptions.class, new CAShellOptions(terminal));
    services.register(CAShellLoginTracker.class, new CAShellLoginTracker());
    services.register(
      CAPreferencesServiceType.class,
      configuration.preferences()
    );
    services.register(
      CAShellTerminalHolder.class,
      new CAShellTerminalHolder(terminal)
    );
    services.register(
      CAStrings.class,
      CAStrings.create(configuration.locale())
    );

    final List<CAShellCmdType> commands =
      List.of(
        new CAShellCmdAuditSearchBegin(services),
        new CAShellCmdAuditSearchNext(services),
        new CAShellCmdAuditSearchPrevious(services),
        new CAShellCmdBookmarkList(services),
        new CAShellCmdBookmarkLogin(services),
        new CAShellCmdBookmarkPut(services),
        new CAShellCmdBookmarkRemove(services),
        new CAShellCmdFileGet(services),
        new CAShellCmdFilePut(services),
        new CAShellCmdFileSearchBegin(services),
        new CAShellCmdFileSearchNext(services),
        new CAShellCmdFileSearchPrevious(services),
        new CAShellCmdFilesRecent(services),
        new CAShellCmdHelp(services),
        new CAShellCmdItemAttachmentAdd(services),
        new CAShellCmdItemAttachmentRemove(services),
        new CAShellCmdItemCreate(services),
        new CAShellCmdItemGet(services),
        new CAShellCmdItemMetadataPut(services),
        new CAShellCmdItemMetadataRemove(services),
        new CAShellCmdItemRepositAdd(services),
        new CAShellCmdItemRepositMove(services),
        new CAShellCmdItemRepositRemove(services),
        new CAShellCmdItemSearchBegin(services),
        new CAShellCmdItemSearchNext(services),
        new CAShellCmdItemSearchPrevious(services),
        new CAShellCmdLocationAttachmentAdd(services),
        new CAShellCmdLocationAttachmentRemove(services),
        new CAShellCmdLocationGet(services),
        new CAShellCmdLocationList(services),
        new CAShellCmdLocationPut(services),
        new CAShellCmdLogin(services),
        new CAShellCmdLogout(services),
        new CAShellCmdRolesAssign(services),
        new CAShellCmdRolesGet(services),
        new CAShellCmdRolesRevoke(services),
        new CAShellCmdSelf(services),
        new CAShellCmdSet(services),
        new CAShellCmdTypeDeclarationCreate(services),
        new CAShellCmdTypeDeclarationFieldPut(services),
        new CAShellCmdTypeDeclarationFieldRemove(services),
        new CAShellCmdTypeDeclarationGet(services),
        new CAShellCmdTypeDeclarationSearchBegin(services),
        new CAShellCmdTypeDeclarationSearchNext(services),
        new CAShellCmdTypeDeclarationSearchPrevious(services),
        new CAShellCmdTypeScalarGet(services),
        new CAShellCmdTypeScalarPut(services),
        new CAShellCmdTypeScalarRemove(services),
        new CAShellCmdTypeScalarSearchBegin(services),
        new CAShellCmdTypeScalarSearchNext(services),
        new CAShellCmdTypeScalarSearchPrevious(services),
        new CAShellCmdVersion(services)
      );

    final var commandsNamed =
      commands.stream()
        .collect(Collectors.toMap(
          e -> e.metadata().name(),
          Function.identity())
        );

    final var history =
      new DefaultHistory();
    final var parser =
      new DefaultParser();

    final var completer =
      new AggregateCompleter(
        commands.stream()
          .map(c -> {
            return new ArgumentCompleter(
              new StringsCompleter(c.metadata().name()),
              c.completer()
            );
          })
          .collect(Collectors.toList())
      );

    final var reader =
      LineReaderBuilder.builder()
        .appName("cardant")
        .terminal(terminal)
        .completer(completer)
        .parser(parser)
        .history(history)
        .build();

    return new CAShell(
      services,
      terminal,
      writer,
      commandsNamed,
      reader
    );
  }
}
