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

import com.io7m.cardant.client.api.CAClientConnectionParameters;
import com.io7m.cardant.protocol.inventory.CAIResponseLogin;
import com.io7m.idstore.model.IdName;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed1;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QParameterPositional;
import com.io7m.quarrel.core.QParameterType;
import com.io7m.quarrel.core.QParametersPositionalType;
import com.io7m.quarrel.core.QParametersPositionalTyped;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import org.jline.reader.Completer;
import org.jline.reader.impl.completer.StringsCompleter;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * The shell login command.
 */

public final class CAShellCmdLogin extends CAShellCmdAbstract
{
  private static final QParameterPositional<URI> SERVER =
    new QParameterPositional<>(
      "server",
      new QConstant("The server URI."),
      URI.class
    );

  private static final QParameterPositional<String> USERNAME =
    new QParameterPositional<>(
      "username",
      new QConstant("The username."),
      String.class
    );

  private static final QParameterPositional<String> PASSWORD =
    new QParameterPositional<>(
      "password",
      new QConstant("The password."),
      String.class
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
   * @param inServices The context
   */
  public CAShellCmdLogin(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "login",
        new QConstant("Log in."),
        Optional.empty()
      ));
  }

  @Override
  public Completer completer()
  {
    return new StringsCompleter(
      this.onListNamedParameters()
        .stream()
        .map(QParameterType::name)
        .toList()
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(
      LOGIN_TIMEOUT,
      COMMAND_TIMEOUT
    );
  }

  @Override
  public QParametersPositionalType onListPositionalParameters()
  {
    return new QParametersPositionalTyped(List.of(
      SERVER,
      USERNAME,
      PASSWORD
    ));
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var server =
      context.parameterValue(SERVER);
    final var userName =
      context.parameterValue(USERNAME);
    final var password =
      context.parameterValue(PASSWORD);

    final var loginTimeout =
      context.parameterValue(LOGIN_TIMEOUT);
    final var commandTimeout =
      context.parameterValue(COMMAND_TIMEOUT);

    this.setLoginTimeoutRecent(loginTimeout);
    this.setCommandTimeoutRecent(commandTimeout);

    final var credentials =
      new CAClientConnectionParameters(
        server.getHost(),
        server.getPort(),
        Objects.equals(server.getScheme(), "https"),
        new IdName(userName),
        password,
        Map.of(),
        this.loginTimeout(),
        this.commandTimeout()
      );

    final var response =
      (CAIResponseLogin)
        this.client()
          .connectOrThrow(credentials);

    this.loginTracker().setUserId(response.userId());
    return SUCCESS;
  }
}
