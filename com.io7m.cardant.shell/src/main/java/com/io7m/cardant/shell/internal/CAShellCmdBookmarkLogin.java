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

import com.io7m.cardant.client.api.CAClientCredentials;
import com.io7m.cardant.client.api.CAClientException;
import com.io7m.cardant.client.preferences.api.CAPreferenceServerBookmark;
import com.io7m.cardant.client.preferences.api.CAPreferenceServerUsernamePassword;
import com.io7m.cardant.error_codes.CAException;
import com.io7m.cardant.error_codes.CAStandardErrorCodes;
import com.io7m.idstore.model.IdName;
import com.io7m.junreachable.UnreachableCodeException;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QParameterNamed01;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
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
   * @param inContext The shell context
   */

  public CAShellCmdBookmarkLogin(
    final CAShellContextType inContext)
  {
    super(
      inContext,
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
      new CAClientCredentials(
        bookmark.host(),
        bookmark.port(),
        bookmark.isHTTPs(),
        userNameOf(bookmark),
        passwordOf(bookmark),
        Map.of()
      );

    this.client().loginOrElseThrow(credentials, CAClientException::ofError);
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
    if (bookmark.credentials() instanceof final CAPreferenceServerUsernamePassword c) {
      return new IdName(c.username());
    }
    throw new UnreachableCodeException();
  }

  private static String passwordOf(
    final CAPreferenceServerBookmark bookmark)
  {
    if (bookmark.credentials() instanceof final CAPreferenceServerUsernamePassword c) {
      return c.password();
    }
    throw new UnreachableCodeException();
  }

  @Override
  public Completer completer()
  {
    return new Completers.OptionCompleter(List.of(), 1);
  }
}
