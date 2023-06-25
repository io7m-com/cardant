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
import com.io7m.cardant.client.basic.CAClients;
import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.shell.internal.CAShell;
import com.io7m.cardant.shell.internal.CAShellCmdFileGet;
import com.io7m.cardant.shell.internal.CAShellCmdFilePut;
import com.io7m.cardant.shell.internal.CAShellCmdFileSearchBegin;
import com.io7m.cardant.shell.internal.CAShellCmdFileSearchNext;
import com.io7m.cardant.shell.internal.CAShellCmdFileSearchPrevious;
import com.io7m.cardant.shell.internal.CAShellCmdHelp;
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
import com.io7m.cardant.shell.internal.CAShellCmdLocationGet;
import com.io7m.cardant.shell.internal.CAShellCmdLocationList;
import com.io7m.cardant.shell.internal.CAShellCmdLocationPut;
import com.io7m.cardant.shell.internal.CAShellCmdLogin;
import com.io7m.cardant.shell.internal.CAShellCmdLogout;
import com.io7m.cardant.shell.internal.CAShellCmdSet;
import com.io7m.cardant.shell.internal.CAShellCmdType;
import com.io7m.cardant.shell.internal.CAShellCmdVersion;
import com.io7m.cardant.shell.internal.CAShellOptions;
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
import java.util.concurrent.atomic.AtomicBoolean;
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

    final var options =
      new CAShellOptions(
        new AtomicBoolean(false)
      );

    final List<CAShellCmdType> commands =
      List.of(
        new CAShellCmdFileGet(client),
        new CAShellCmdFilePut(client),
        new CAShellCmdFileSearchBegin(client),
        new CAShellCmdFileSearchNext(client),
        new CAShellCmdFileSearchPrevious(client),
        new CAShellCmdHelp(),
        new CAShellCmdItemCreate(client),
        new CAShellCmdItemGet(client),
        new CAShellCmdItemMetadataPut(client),
        new CAShellCmdItemMetadataRemove(client),
        new CAShellCmdItemRepositAdd(client),
        new CAShellCmdItemRepositMove(client),
        new CAShellCmdItemRepositRemove(client),
        new CAShellCmdItemSearchBegin(client),
        new CAShellCmdItemSearchNext(client),
        new CAShellCmdItemSearchPrevious(client),
        new CAShellCmdLocationGet(client),
        new CAShellCmdLocationList(client),
        new CAShellCmdLocationPut(client),
        new CAShellCmdLogin(client),
        new CAShellCmdLogout(client),
        new CAShellCmdSet(options),
        new CAShellCmdVersion()
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
      client,
      options,
      terminal,
      writer,
      commandsNamed,
      reader
    );
  }
}
