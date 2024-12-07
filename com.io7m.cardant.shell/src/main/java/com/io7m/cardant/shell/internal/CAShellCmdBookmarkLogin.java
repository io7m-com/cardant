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
import com.io7m.cardant.client.preferences.api.CAPreferenceServerBookmark;
import com.io7m.cardant.client.preferences.api.CAPreferenceServerUsernamePassword;
import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.cardant.protocol.inventory.CAIResponseLogin;
import com.io7m.idstore.model.IdName;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed01;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.repetoir.core.RPServiceDirectoryType;
import org.jline.builtins.Completers;
import org.jline.reader.Completer;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * "bookmark-login"
 */

public final class CAShellCmdBookmarkLogin
  extends CAShellCmdAbstract
{
  private static final QParameterNamed01<String> NAME =
    new QParameterNamed01<>(
      "--name",
      List.of(),
      new QConstant("The name of the bookmark."),
      Optional.empty(),
      String.class
    );

  /**
   * Construct a command.
   *
   * @param inServices The shell context
   */

  public CAShellCmdBookmarkLogin(
    final RPServiceDirectoryType inServices)
  {
    super(
      inServices,
      new QCommandMetadata(
        "bookmark-login",
        new QConstant("Log in using a bookmark."),
        Optional.empty()
      ));
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return List.of(NAME);
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    final var bookmarkNameOpt =
      context.parameterValue(NAME);

    final CAPreferenceServerBookmark bookmark;
    if (bookmarkNameOpt.isPresent()) {
      final var bookmarkName = bookmarkNameOpt.get();
      bookmark = this.preferences().preferences()
        .serverBookmarks()
        .stream()
        .filter(b -> Objects.equals(b.name(), bookmarkName))
        .findFirst()
        .orElseThrow(CAShellCmdBookmarkLogin::noSuchBookmark);
    } else {
      bookmark = this.preferences().preferences()
        .serverBookmarks()
        .stream()
        .findFirst()
        .orElseThrow(CAShellCmdBookmarkLogin::noSuchBookmark);
    }

    final var credentials =
      new CAClientConnectionParameters(
        bookmark.host(),
        bookmark.port(),
        bookmark.isHTTPs(),
        userNameOf(bookmark),
        passwordOf(bookmark),
        Map.of(),
        bookmark.loginTimeout(),
        bookmark.commandTimeout()
      );

    this.setCommandTimeoutRecent(bookmark.commandTimeout());
    this.setLoginTimeoutRecent(bookmark.loginTimeout());

    final var response =
      (CAIResponseLogin)
        this.client()
          .connectOrThrow(credentials);

    this.loginTracker().setUserId(response.userId());
    return SUCCESS;
  }

  private static CAException noSuchBookmark()
  {
    return new CAException(
      "No such bookmark.",
      CAStandardErrorCodes.errorNonexistent(),
      Map.of(),
      Optional.empty()
    );
  }

  private static IdName userNameOf(
    final CAPreferenceServerBookmark bookmark)
  {
    switch (bookmark.credentials()) {
      case final CAPreferenceServerUsernamePassword c -> {
        return new IdName(c.username());
      }
    }
  }

  private static String passwordOf(
    final CAPreferenceServerBookmark bookmark)
  {
    switch (bookmark.credentials()) {
      case final CAPreferenceServerUsernamePassword c -> {
        return c.password();
      }
    }
  }

  @Override
  public Completer completer()
  {
    return new Completers.OptionCompleter(List.of(), 1);
  }
}
