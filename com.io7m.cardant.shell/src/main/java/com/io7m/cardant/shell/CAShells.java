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
import com.io7m.cardant.client.api.CAClientType;
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
import com.io7m.cardant.shell.internal.CAShellCmdItemDelete;
import com.io7m.cardant.shell.internal.CAShellCmdItemGet;
import com.io7m.cardant.shell.internal.CAShellCmdItemMetadataPut;
import com.io7m.cardant.shell.internal.CAShellCmdItemMetadataRemove;
import com.io7m.cardant.shell.internal.CAShellCmdItemSearchBegin;
import com.io7m.cardant.shell.internal.CAShellCmdItemSearchNext;
import com.io7m.cardant.shell.internal.CAShellCmdItemSearchPrevious;
import com.io7m.cardant.shell.internal.CAShellCmdItemSetName;
import com.io7m.cardant.shell.internal.CAShellCmdItemTypesAssign;
import com.io7m.cardant.shell.internal.CAShellCmdItemTypesRevoke;
import com.io7m.cardant.shell.internal.CAShellCmdLocationAttachmentAdd;
import com.io7m.cardant.shell.internal.CAShellCmdLocationAttachmentRemove;
import com.io7m.cardant.shell.internal.CAShellCmdLocationDelete;
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
import com.io7m.cardant.shell.internal.CAShellCmdStockRepositRemove;
import com.io7m.cardant.shell.internal.CAShellCmdStockRepositSerialIntroduce;
import com.io7m.cardant.shell.internal.CAShellCmdStockRepositSerialMove;
import com.io7m.cardant.shell.internal.CAShellCmdStockRepositSerialNumberAdd;
import com.io7m.cardant.shell.internal.CAShellCmdStockRepositSerialNumberRemove;
import com.io7m.cardant.shell.internal.CAShellCmdStockRepositSetAdd;
import com.io7m.cardant.shell.internal.CAShellCmdStockRepositSetIntroduce;
import com.io7m.cardant.shell.internal.CAShellCmdStockRepositSetMove;
import com.io7m.cardant.shell.internal.CAShellCmdStockRepositSetRemove;
import com.io7m.cardant.shell.internal.CAShellCmdStockSearchBegin;
import com.io7m.cardant.shell.internal.CAShellCmdStockSearchNext;
import com.io7m.cardant.shell.internal.CAShellCmdStockSearchPrevious;
import com.io7m.cardant.shell.internal.CAShellCmdSyntaxList;
import com.io7m.cardant.shell.internal.CAShellCmdSyntaxShow;
import com.io7m.cardant.shell.internal.CAShellCmdType;
import com.io7m.cardant.shell.internal.CAShellCmdTypePackageGetText;
import com.io7m.cardant.shell.internal.CAShellCmdTypePackageInstall;
import com.io7m.cardant.shell.internal.CAShellCmdTypePackageSchema;
import com.io7m.cardant.shell.internal.CAShellCmdTypePackageSearchBegin;
import com.io7m.cardant.shell.internal.CAShellCmdTypePackageSearchNext;
import com.io7m.cardant.shell.internal.CAShellCmdTypePackageSearchPrevious;
import com.io7m.cardant.shell.internal.CAShellCmdTypePackageUninstall;
import com.io7m.cardant.shell.internal.CAShellCmdTypePackageUpgrade;
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
        .create(
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
    final var strings = CAStrings.create(configuration.locale());
    services.register(
      CAStrings.class,
      strings
    );
    services.register(
      CAClientType.class,
      client
    );
    services.register(
      CAShellOptions.class,
      new CAShellOptions(terminal)
    );
    services.register(
      CAShellLoginTracker.class,
      new CAShellLoginTracker()
    );
    services.register(
      CAPreferencesServiceType.class,
      configuration.preferences()
    );
    services.register(
      CAShellTerminalHolder.class,
      new CAShellTerminalHolder(terminal)
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
        new CAShellCmdItemDelete(services),
        new CAShellCmdItemGet(services),
        new CAShellCmdItemMetadataPut(services),
        new CAShellCmdItemMetadataRemove(services),
        new CAShellCmdItemSearchBegin(services),
        new CAShellCmdItemSearchNext(services),
        new CAShellCmdItemSearchPrevious(services),
        new CAShellCmdItemSetName(services),
        new CAShellCmdItemTypesAssign(services),
        new CAShellCmdItemTypesRevoke(services),
        new CAShellCmdLocationAttachmentAdd(services),
        new CAShellCmdLocationAttachmentRemove(services),
        new CAShellCmdLocationDelete(services),
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
        new CAShellCmdStockRepositRemove(services),
        new CAShellCmdStockRepositSerialIntroduce(services),
        new CAShellCmdStockRepositSerialMove(services),
        new CAShellCmdStockRepositSerialNumberAdd(services),
        new CAShellCmdStockRepositSerialNumberRemove(services),
        new CAShellCmdStockRepositSetAdd(services),
        new CAShellCmdStockRepositSetIntroduce(services),
        new CAShellCmdStockRepositSetMove(services),
        new CAShellCmdStockRepositSetRemove(services),
        new CAShellCmdStockSearchBegin(services),
        new CAShellCmdStockSearchNext(services),
        new CAShellCmdStockSearchPrevious(services),
        new CAShellCmdSyntaxList(services),
        new CAShellCmdSyntaxShow(services),
        new CAShellCmdTypePackageGetText(services),
        new CAShellCmdTypePackageInstall(services),
        new CAShellCmdTypePackageSchema(services),
        new CAShellCmdTypePackageSearchBegin(services),
        new CAShellCmdTypePackageSearchNext(services),
        new CAShellCmdTypePackageSearchPrevious(services),
        new CAShellCmdTypePackageUninstall(services),
        new CAShellCmdTypePackageUpgrade(services),
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
