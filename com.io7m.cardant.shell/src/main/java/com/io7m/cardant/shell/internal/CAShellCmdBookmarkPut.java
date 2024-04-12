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


package com.io7m.cardant.shell.internal;

import com.io7m.cardant.client.preferences.api.CAPreferenceServerBookmark;
import com.io7m.cardant.client.preferences.api.CAPreferenceServerUsernamePassword;
import com.io7m.cardant.client.preferences.api.CAPreferences;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import org.jline.builtins.Completers;
import org.jline.reader.Completer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;
import static java.lang.Boolean.FALSE;

/**
 * "bookmark-put"
 */

public final class CAShellCmdBookmarkPut
  extends CAShellCmdAbstract
{
  private static final QParameterNamed1<String> NAME =
    new QParameterNamed1<>(
      "--name",
      List.of(),
      new QConstant("The name of the bookmark."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed1<String> HOSTNAME =
    new QParameterNamed1<>(
      "--hostname",
      List.of(),
      new QConstant("The hostname of the server."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed1<Integer> PORT =
    new QParameterNamed1<>(
      "--port",
      List.of(),
      new QConstant("The port used for the server."),
      Optional.of(Integer.valueOf(30000)),
      Integer.class
    );

  private static final QParameterNamed1<String> USERNAME =
    new QParameterNamed1<>(
      "--user",
      List.of(),
      new QConstant("The username for the server."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed1<String> PASSWORD =
    new QParameterNamed1<>(
      "--password",
      List.of(),
      new QConstant("The password for the server."),
      Optional.empty(),
      String.class
    );

  private static final QParameterNamed1<Boolean> TLS =
    new QParameterNamed1<>(
      "--tls",
      List.of(),
      new QConstant("Whether to use TLS to connect to the server."),
      Optional.of(FALSE),
      Boolean.class
    );

  private static final QParameterNamed1<Duration> LOGIN_TIMEOUT =
    new QParameterNamed1<>(
      "--login-timeout",
      List.of(),
      new QConstant("The server login timeout duration."),
      Optional.of(Duration.ofSeconds(30L)),
      Duration.class
    );

  private static final QParameterNamed1<Duration> COMMAND_TIMEOUT =
    new QParameterNamed1<>(
      "--command-timeout",
      List.of(),
      new QConstant("The server command timeout duration."),
      Optional.of(Duration.ofSeconds(30L)),
      Duration.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The shell context
   */

  public CAShellCmdBookmarkPut(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "bookmark-put",
        new QConstant("Create or update a server bookmark."),
        Optional.empty()
      ));
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(
      COMMAND_TIMEOUT,
      HOSTNAME,
      LOGIN_TIMEOUT,
      NAME,
      PASSWORD,
      PORT,
      TLS,
      USERNAME
    );
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var name =
      context.parameterValue(NAME);
    final var hostname =
      context.parameterValue(HOSTNAME);
    final var port =
      context.parameterValue(PORT)
        .intValue();
    final var username =
      context.parameterValue(USERNAME);
    final var password =
      context.parameterValue(PASSWORD);
    final var tls =
      context.parameterValue(TLS)
        .booleanValue();
    final var loginTimeout =
      context.parameterValue(LOGIN_TIMEOUT);
    final var commandTimeout =
      context.parameterValue(COMMAND_TIMEOUT);

    final var bookmarksMutable =
      new ArrayList<>(this.preferences().preferences().serverBookmarks());

    bookmarksMutable.removeIf(b -> Objects.equals(b.name(), name));
    bookmarksMutable.add(
      new CAPreferenceServerBookmark(
        name,
        hostname,
        port,
        tls,
        loginTimeout,
        commandTimeout,
        new CAPreferenceServerUsernamePassword(username, password)
      )
    );

    this.preferences().update(oldPreferences -> {
      return new CAPreferences(
        oldPreferences.debuggingEnabled(),
        List.copyOf(bookmarksMutable),
        oldPreferences.recentFiles()
      );
    });
    return SUCCESS;
  }

  @Override
  public Completer completer()
  {
    return new Completers.OptionCompleter(List.of(), 1);
  }
}
