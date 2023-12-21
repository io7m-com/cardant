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


package com.io7m.cardant.main.internal;

import com.io7m.cardant.client.preferences.vanilla.CAPreferencesService;
import com.io7m.cardant.server.api.CAServerFactoryType;
import com.io7m.cardant.shell.CAShellConfiguration;
import com.io7m.cardant.shell.CAShellFactoryType;
import com.io7m.jade.api.ApplicationDirectories;
import com.io7m.jade.api.ApplicationDirectoryConfiguration;
import com.io7m.quarrel.core.QCommandContextType;
import com.io7m.quarrel.core.QCommandMetadata;
import com.io7m.quarrel.core.QCommandStatus;
import com.io7m.quarrel.core.QCommandType;
import com.io7m.quarrel.core.QParameterNamedType;
import com.io7m.quarrel.core.QStringType.QConstant;
import com.io7m.quarrel.ext.logback.QLogback;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.time.Clock;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ServiceLoader;

import static com.io7m.quarrel.core.QCommandStatus.SUCCESS;

/**
 * The "shell" command.
 */

public final class CMCmdShell implements QCommandType
{
  private final QCommandMetadata metadata;

  /**
   * Construct a command.
   */

  public CMCmdShell()
  {
    this.metadata = new QCommandMetadata(
      "shell",
      new QConstant("Start the shell."),
      Optional.empty()
    );
  }

  private static IllegalStateException noService()
  {
    return new IllegalStateException(
      "No services available of %s".formatted(CAServerFactoryType.class)
    );
  }

  @Override
  public List<QParameterNamedType<?>> onListNamedParameters()
  {
    return QLogback.parameters();
  }

  @Override
  public QCommandStatus onExecute(
    final QCommandContextType context)
    throws Exception
  {
    System.setProperty("org.jooq.no-tips", "true");
    System.setProperty("org.jooq.no-logo", "true");

    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();

    QLogback.configure(context);

    final var directoryConfiguration =
      ApplicationDirectoryConfiguration.builder()
        .setApplicationName("com.io7m.cardant")
        .setPortablePropertyName("com.io7m.cardant.portable")
        .setOverridePropertyName("com.io7m.cardant.override")
        .build();

    final var directories =
      ApplicationDirectories.get(directoryConfiguration);

    final var preferences =
      CAPreferencesService.openOrDefault(
        directories.configurationDirectory()
          .resolve("preferences.xml")
      );

    final var shells =
      ServiceLoader.load(CAShellFactoryType.class)
        .findFirst()
        .orElseThrow(CMCmdShell::noService);

    final var configuration =
      new CAShellConfiguration(
        preferences,
        Locale.getDefault(),
        Clock.systemUTC(),
        Optional.empty()
      );

    try (var shell = shells.create(configuration)) {
      shell.run();
    }

    return SUCCESS;
  }

  @Override
  public QCommandMetadata metadata()
  {
    return this.metadata;
  }
}
