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

package com.io7m.cardant.server.main.internal;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.io7m.cardant.server.api.CAServerConfigurations;
import com.io7m.cardant.server.api.CAServerFactoryType;
import com.io7m.cardant.server.service.configuration.CAConfigurationFiles;
import com.io7m.claypot.core.CLPAbstractCommand;
import com.io7m.claypot.core.CLPCommandContextType;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.nio.file.Path;
import java.time.Clock;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.UUID;

import static com.io7m.claypot.core.CLPCommandType.Status.SUCCESS;

/**
 * The "initialize" command.
 */

@Parameters(commandDescription = "Initialize the database.")
public final class CACmdInitialize extends CLPAbstractCommand
{
  @Parameter(
    names = "--configuration",
    description = "The configuration file",
    required = true
  )
  private Path configurationFile;

  @Parameter(
    names = "--admin",
    description = "The ID of the user that will be the administrator",
    required = true
  )
  private String adminId;

  /**
   * Construct a command.
   *
   * @param inContext The command context
   */

  public CACmdInitialize(
    final CLPCommandContextType inContext)
  {
    super(inContext);
  }

  @Override
  protected Status executeActual()
    throws Exception
  {
    System.setProperty("org.jooq.no-tips", "true");
    System.setProperty("org.jooq.no-logo", "true");

    SLF4JBridgeHandler.removeHandlersForRootLogger();
    SLF4JBridgeHandler.install();

    final var adminUUID =
      UUID.fromString(this.adminId);

    final var configFile =
      new CAConfigurationFiles()
        .parse(this.configurationFile);

    final var configuration =
      CAServerConfigurations.ofFile(
        Locale.getDefault(),
        Clock.systemUTC(),
        configFile
      );

    final var servers =
      ServiceLoader.load(CAServerFactoryType.class)
        .findFirst()
        .orElseThrow(CACmdInitialize::noService);

    try (var server = servers.createServer(configuration)) {
      server.setup(adminUUID);
    }

    return SUCCESS;
  }

  private static IllegalStateException noService()
  {
    return new IllegalStateException(
      "No services available of %s".formatted(CAServerFactoryType.class)
    );
  }

  @Override
  public String name()
  {
    return "initialize";
  }
}
